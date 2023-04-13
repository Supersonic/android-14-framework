package android.app.time;

import android.text.TextUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/* loaded from: classes.dex */
public final class DetectorStatusTypes {
    public static final int DETECTION_ALGORITHM_STATUS_NOT_RUNNING = 2;
    public static final int DETECTION_ALGORITHM_STATUS_NOT_SUPPORTED = 1;
    public static final int DETECTION_ALGORITHM_STATUS_RUNNING = 3;
    public static final int DETECTION_ALGORITHM_STATUS_UNKNOWN = 0;
    public static final int DETECTOR_STATUS_NOT_RUNNING = 2;
    public static final int DETECTOR_STATUS_NOT_SUPPORTED = 1;
    public static final int DETECTOR_STATUS_RUNNING = 3;
    public static final int DETECTOR_STATUS_UNKNOWN = 0;

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DetectionAlgorithmStatus {
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DetectorStatus {
    }

    private DetectorStatusTypes() {
    }

    public static int requireValidDetectorStatus(int detectorStatus) {
        if (detectorStatus < 0 || detectorStatus > 3) {
            throw new IllegalArgumentException("Invalid detector status: " + detectorStatus);
        }
        return detectorStatus;
    }

    public static String detectorStatusToString(int detectorStatus) {
        switch (detectorStatus) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "NOT_SUPPORTED";
            case 2:
                return "NOT_RUNNING";
            case 3:
                return "RUNNING";
            default:
                throw new IllegalArgumentException("Unknown status: " + detectorStatus);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int detectorStatusFromString(String detectorStatusString) {
        char c;
        if (TextUtils.isEmpty(detectorStatusString)) {
            throw new IllegalArgumentException("Empty status: " + detectorStatusString);
        }
        switch (detectorStatusString.hashCode()) {
            case -2026200673:
                if (detectorStatusString.equals("RUNNING")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (detectorStatusString.equals("UNKNOWN")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 854821378:
                if (detectorStatusString.equals("NOT_SUPPORTED")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 2056721427:
                if (detectorStatusString.equals("NOT_RUNNING")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown status: " + detectorStatusString);
        }
    }

    public static int requireValidDetectionAlgorithmStatus(int detectionAlgorithmStatus) {
        if (detectionAlgorithmStatus < 0 || detectionAlgorithmStatus > 3) {
            throw new IllegalArgumentException("Invalid detection algorithm: " + detectionAlgorithmStatus);
        }
        return detectionAlgorithmStatus;
    }

    public static String detectionAlgorithmStatusToString(int detectorAlgorithmStatus) {
        switch (detectorAlgorithmStatus) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "NOT_SUPPORTED";
            case 2:
                return "NOT_RUNNING";
            case 3:
                return "RUNNING";
            default:
                throw new IllegalArgumentException("Unknown status: " + detectorAlgorithmStatus);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int detectionAlgorithmStatusFromString(String detectorAlgorithmStatusString) {
        char c;
        if (TextUtils.isEmpty(detectorAlgorithmStatusString)) {
            throw new IllegalArgumentException("Empty status: " + detectorAlgorithmStatusString);
        }
        switch (detectorAlgorithmStatusString.hashCode()) {
            case -2026200673:
                if (detectorAlgorithmStatusString.equals("RUNNING")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (detectorAlgorithmStatusString.equals("UNKNOWN")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 854821378:
                if (detectorAlgorithmStatusString.equals("NOT_SUPPORTED")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 2056721427:
                if (detectorAlgorithmStatusString.equals("NOT_RUNNING")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown status: " + detectorAlgorithmStatusString);
        }
    }
}
