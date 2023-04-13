package android.media.p007tv.tuner;
/* renamed from: android.media.tv.tuner.TunerUtils */
/* loaded from: classes2.dex */
public final class TunerUtils {
    public static int getFilterSubtype(int mainType, int subtype) {
        if (mainType == 1) {
            switch (subtype) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 4;
                case 4:
                    return 5;
                case 6:
                    return 7;
                case 7:
                    return 3;
                case 8:
                    return 6;
                case 9:
                    return 8;
            }
        } else if (mainType == 2) {
            switch (subtype) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 4;
                case 4:
                    return 5;
                case 5:
                    return 7;
                case 6:
                    return 6;
                case 10:
                    return 3;
            }
        } else if (mainType == 4) {
            switch (subtype) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 11:
                    return 2;
                case 12:
                    return 3;
                case 13:
                    return 4;
                case 14:
                    return 5;
            }
        } else if (mainType == 8) {
            switch (subtype) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 14:
                    return 3;
                case 15:
                    return 2;
            }
        } else if (mainType == 16) {
            switch (subtype) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 14:
                    return 3;
                case 16:
                    return 2;
            }
        }
        throw new IllegalArgumentException("Invalid filter types. Main type=" + mainType + ", subtype=" + subtype);
    }

    public static void throwExceptionForResult(int r, String msg) {
        if (msg == null) {
            msg = "";
        }
        switch (r) {
            case 0:
                return;
            case 1:
                throw new IllegalStateException("Invalid state: resource unavailable. " + msg);
            case 2:
                throw new IllegalStateException("Invalid state: not initialized. " + msg);
            case 3:
                throw new IllegalStateException(msg);
            case 4:
                throw new IllegalArgumentException(msg);
            case 5:
                throw new OutOfMemoryError(msg);
            case 6:
                throw new RuntimeException("Unknown error" + msg);
            default:
                throw new RuntimeException("Unexpected result " + r + ".  " + msg);
        }
    }

    public static void checkResourceState(String name, boolean closed) {
        if (closed) {
            throw new IllegalStateException(name + " has been closed");
        }
    }

    public static void checkResourceAccessible(String name, boolean accessible) {
        if (!accessible) {
            throw new IllegalStateException(name + " is inaccessible");
        }
    }

    private TunerUtils() {
    }
}
