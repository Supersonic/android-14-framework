package android.media;
/* loaded from: classes2.dex */
public class MediaCasStateException extends IllegalStateException {
    private final String mDiagnosticInfo;
    private final int mErrorCode;

    private MediaCasStateException(int err, String msg, String diagnosticInfo) {
        super(msg);
        this.mErrorCode = err;
        this.mDiagnosticInfo = diagnosticInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwExceptionIfNeeded(int err) {
        throwExceptionIfNeeded(err, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwExceptionIfNeeded(int err, String msg) {
        String diagnosticInfo;
        if (err == 0) {
            return;
        }
        if (err == 6) {
            throw new IllegalArgumentException();
        }
        switch (err) {
            case 1:
                diagnosticInfo = "No license";
                break;
            case 2:
                diagnosticInfo = "License expired";
                break;
            case 3:
                diagnosticInfo = "Session not opened";
                break;
            case 4:
                diagnosticInfo = "Unsupported scheme or data format";
                break;
            case 5:
                diagnosticInfo = "Invalid CAS state";
                break;
            case 6:
            case 7:
            case 8:
            case 11:
            default:
                diagnosticInfo = "Unknown CAS state exception";
                break;
            case 9:
                diagnosticInfo = "Insufficient output protection";
                break;
            case 10:
                diagnosticInfo = "Tamper detected";
                break;
            case 12:
                diagnosticInfo = "Not initialized";
                break;
            case 13:
                diagnosticInfo = "Decrypt error";
                break;
            case 14:
                diagnosticInfo = "General CAS error";
                break;
            case 15:
                diagnosticInfo = "Need Activation";
                break;
            case 16:
                diagnosticInfo = "Need Pairing";
                break;
            case 17:
                diagnosticInfo = "No Card";
                break;
            case 18:
                diagnosticInfo = "Card Muted";
                break;
            case 19:
                diagnosticInfo = "Card Invalid";
                break;
            case 20:
                diagnosticInfo = "Blackout";
                break;
            case 21:
                diagnosticInfo = "Rebooting";
                break;
        }
        throw new MediaCasStateException(err, msg, String.format("%s (err=%d)", diagnosticInfo, Integer.valueOf(err)));
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getDiagnosticInfo() {
        return this.mDiagnosticInfo;
    }
}
