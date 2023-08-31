package autocancel.core.policy;

import autocancel.utils.Policy;
import autocancel.utils.Settings;
import autocancel.utils.id.CancellableID;

public class BasePolicy extends Policy {

    private static final Integer ABNORMAL_PERFORMANCE_THRESHOLD = 2;

    private static final Integer MAX_CONTINUOUS_ABNORMAL = 10;

    private Boolean started = false;

    private Integer continuousAbnormalTimes = 0;
    
    public BasePolicy() {
        super();
    }

    @Override
    public Boolean needCancellation() {
        Boolean need = false;
        if (!started) {
            started = (this.infoCenter.getFinishedTaskNumber() != 0);
        }
        else {
            if (this.infoCenter.getFinishedTaskNumber() < BasePolicy.ABNORMAL_PERFORMANCE_THRESHOLD) {
                this.continuousAbnormalTimes += 1;
            }
            else {
                this.continuousAbnormalTimes = 0;
            }

            if (this.continuousAbnormalTimes > BasePolicy.MAX_CONTINUOUS_ABNORMAL) {
                need = true;
            }
        }
        
        return need;
    }

    @Override
    public CancellableID getCancelTarget() {
        return new CancellableID();
    }
}
