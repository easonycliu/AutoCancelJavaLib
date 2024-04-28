package autocancel.core.policy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

import autocancel.utils.Settings;

public class CancelTrigger {
	private static final Double ABNORMAL_PERFORMANCE_DROP_PORTION =
		Double.valueOf(Settings.getFromJVMOrDefault("abnormal.portion", "0.2"));

	private static final Long ONE_CYCLE_MILLI = 1000L;

	private static final Long CONTINUOUS_ABNORMAL_TO_CANCEL_CYCLE = 2L;

	private static final Long TRIGGER_INTERVAL_IN_CANCEL_CYCLE = 10L;

	private static final Long CONTINUOUS_WORK_WITHOUT_CANCEL_CYCLE = 5L;

	private static final Long PAST_PERFORMANCE_REF_CYCLE = 30L;

	private static final Integer MAX_PAST_CYCLE_PERFORMANCE_REF_NUM = 3;

	private static final Integer AVERAGE_FILTER_SIZE = 2;

	private Boolean cancelStart;

	private AverageFilter averageFilter;

	private PerformanceBuffer performanceBuffer;

	private Long continuousAbnormalCycles;

	private Long prevCancelTimestamp;

	private FixSizePriorityQueue<ThroughputDataPoint> cycleMaxThroughputQueue;

	public CancelTrigger() {
		this.cancelStart = false;
		this.averageFilter = new AverageFilter(CancelTrigger.AVERAGE_FILTER_SIZE);
		this.performanceBuffer = new PerformanceBuffer(CancelTrigger.ONE_CYCLE_MILLI);
		this.continuousAbnormalCycles = 0L;
		this.prevCancelTimestamp =
			System.currentTimeMillis() - CancelTrigger.ONE_CYCLE_MILLI * CancelTrigger.TRIGGER_INTERVAL_IN_CANCEL_CYCLE;
		this.cycleMaxThroughputQueue =
			new FixSizePriorityQueue<ThroughputDataPoint>(CancelTrigger.MAX_PAST_CYCLE_PERFORMANCE_REF_NUM,
				(e1, e2) -> e1.getThroughput().intValue() - e2.getThroughput().intValue());
	}

	public Boolean isAbnormal(Double throughput) {
		Boolean abnormal = false;
		Double normalThroughput =
			this.cycleMaxThroughputQueue.mean((element) -> Double.valueOf(element.getThroughput()));
		if (normalThroughput * (1.0 - CancelTrigger.ABNORMAL_PERFORMANCE_DROP_PORTION) - Double.MIN_VALUE
			> throughput) {
			abnormal = true;
		}
		return System.getProperty("cancel.enable").equals("true") && abnormal;
	}

	public Boolean triggered(long finishedTaskNumber) {
		Boolean need = false;
		long currentTimeMilli = System.currentTimeMillis();
		long lastCyclePerformance = this.performanceBuffer.lastCyclePerformance(currentTimeMilli, finishedTaskNumber);
		if (lastCyclePerformance >= 0) {
			this.cycleMaxThroughputQueue.removeIf((element) -> element.isExpired());
			this.cycleMaxThroughputQueue.enQueue(new ThroughputDataPoint(lastCyclePerformance, currentTimeMilli));
			Double filteredFinishedTaskNumber = this.averageFilter.putAndGet(lastCyclePerformance);
			Boolean abnormal = this.isAbnormal(filteredFinishedTaskNumber);
			if (abnormal && !this.cancelStart) {
				this.continuousAbnormalCycles += 1;
				if (this.continuousAbnormalCycles > CancelTrigger.CONTINUOUS_ABNORMAL_TO_CANCEL_CYCLE) {
					this.cancelStart = true;
				}
			} else {
				this.continuousAbnormalCycles = 0L;
			}

			if (this.cancelStart) {
				need = abnormal
					&& ((currentTimeMilli - this.prevCancelTimestamp)
						> (CancelTrigger.ONE_CYCLE_MILLI * CancelTrigger.TRIGGER_INTERVAL_IN_CANCEL_CYCLE));
			}
			System.out.println(String.format("Finished tasks: %f, Abnormal: %b", filteredFinishedTaskNumber, abnormal));
			CancelLogger.logExperimentInfo(Double.valueOf(lastCyclePerformance), need);
		}

		if (need) {
			this.prevCancelTimestamp = currentTimeMilli;
		}

		this.cancelStart = this.cancelStart
			&& ((currentTimeMilli - this.prevCancelTimestamp) < (CancelTrigger.ONE_CYCLE_MILLI
					* (CancelTrigger.TRIGGER_INTERVAL_IN_CANCEL_CYCLE
						+ CancelTrigger.CONTINUOUS_WORK_WITHOUT_CANCEL_CYCLE)));

		return need;
	}

	public static class AverageFilter {
		private final int size;

		private long[] buffer;

		private int currentIndex;

		public AverageFilter(int size) {
			this.size = size;
			this.buffer = new long[this.size];
			Arrays.fill(this.buffer, 0);
			this.currentIndex = 0;
		}

		public <T> Double putAndGet(long input) {
			this.buffer[currentIndex] = input;
			currentIndex = (currentIndex + 1) % this.size;
			return Arrays.stream(this.buffer).average().getAsDouble();
		}

		public void clear() {
			Arrays.fill(this.buffer, 0);
		}
	}

	public static class ThroughputDataPoint implements Comparator<ThroughputDataPoint> {
		private final Long throughput;

		private final Long timestamp;

		public ThroughputDataPoint(Long throughput, Long timestamp) {
			this.throughput = throughput;
			this.timestamp = timestamp;
		}

		public Long getThroughput() {
			return this.throughput;
		}

		public Long getTimestamp() {
			return this.timestamp;
		}

		public int compare(ThroughputDataPoint dataPoint1, ThroughputDataPoint dataPoint2) {
			return dataPoint1.getThroughput().compareTo(dataPoint2.getThroughput());
		}

		public Boolean isExpired() {
			return CancelTrigger.PAST_PERFORMANCE_REF_CYCLE.compareTo(
					   (System.currentTimeMillis() - this.timestamp) / CancelTrigger.ONE_CYCLE_MILLI)
				< 0;
		}
	}

	public static class FixSizePriorityQueue<T> {
		private final int size;

		private final Comparator<T> comparator;

		private Queue<T> minQueue;

		public FixSizePriorityQueue(int size, Comparator<T> comparator) {
			this.size = size;
			this.comparator = comparator;
			this.minQueue = new PriorityQueue<>(this.comparator);
		}

		public void enQueue(T e) {
			this.minQueue.add(e);
			if (this.minQueue.size() > this.size) {
				this.minQueue.poll();
			}
		}

		public void removeIf(Predicate<T> filter) {
			this.minQueue.removeIf(filter);
		}

		public void clear() {
			this.minQueue.clear();
		}

		public Double sum(Function<T, Double> mapToDouble) {
			Double sumDouble = 0.0;
			for (T element : this.minQueue) {
				sumDouble += mapToDouble.apply(element);
			}
			return sumDouble;
		}

		public Double mean(Function<T, Double> mapToDouble) {
			Double meanDouble = 0.0;
			int queueSize = this.minQueue.size();
			if (queueSize > 0) {
				Double sumDouble = 0.0;
				for (T element : this.minQueue) {
					sumDouble += mapToDouble.apply(element);
				}
				meanDouble = sumDouble / queueSize;
			}
			return meanDouble;
		}
	}

	public static class PerformanceBuffer {
		private final long outputCycleMilli;

		private long lastCycleTimestamp;

		private long bufferedPerformance;

		public PerformanceBuffer(long outputCycleMilli) {
			this.outputCycleMilli = outputCycleMilli;
			this.lastCycleTimestamp = System.currentTimeMillis();
			this.bufferedPerformance = 0L;
		}

		public long lastCyclePerformance(long timestamp, long performance) {
			long outputPerformance = -1L;
			if (timestamp - this.lastCycleTimestamp > this.outputCycleMilli) {
				outputPerformance = this.bufferedPerformance + performance;
				this.bufferedPerformance = 0;
				this.lastCycleTimestamp = timestamp;
			} else {
				this.bufferedPerformance += performance;
			}
			return outputPerformance;
		}

		public void clear() {
			this.bufferedPerformance = 0L;
		}
	}
}
