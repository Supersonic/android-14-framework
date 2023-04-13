package android.drm;
@Deprecated
/* loaded from: classes.dex */
public class DrmInfoStatus {
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_OK = 1;
    public final ProcessedData data;
    public final int infoType;
    public final String mimeType;
    public final int statusCode;

    public DrmInfoStatus(int statusCode, int infoType, ProcessedData data, String mimeType) {
        if (!DrmInfoRequest.isValidType(infoType)) {
            throw new IllegalArgumentException("infoType: " + infoType);
        }
        if (!isValidStatusCode(statusCode)) {
            throw new IllegalArgumentException("Unsupported status code: " + statusCode);
        }
        if (mimeType == null || mimeType == "") {
            throw new IllegalArgumentException("mimeType is null or an empty string");
        }
        this.statusCode = statusCode;
        this.infoType = infoType;
        this.data = data;
        this.mimeType = mimeType;
    }

    private boolean isValidStatusCode(int statusCode) {
        return statusCode == 1 || statusCode == 2;
    }
}
