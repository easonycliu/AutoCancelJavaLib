package autocancel.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class Syscall {

    private interface CStdLib extends Library {
        int syscall(int number, Object... args);
    }

    private static CStdLib library = (CStdLib) Native.loadLibrary("c-2.31", CStdLib.class);

    public static long gettid() {
        long tid = Syscall.library.syscall(186);
        return tid;
    }
}
