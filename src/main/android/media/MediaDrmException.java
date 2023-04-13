package android.media;
/* loaded from: classes2.dex */
public class MediaDrmException extends Exception implements MediaDrmThrowable {
    private final int mErrorContext;
    private final int mOemError;
    private final int mVendorError;

    public MediaDrmException(String detailMessage) {
        this(detailMessage, 0, 0, 0);
    }

    public MediaDrmException(String message, int vendorError, int oemError, int errorContext) {
        super(message);
        this.mVendorError = vendorError;
        this.mOemError = oemError;
        this.mErrorContext = errorContext;
    }

    @Override // android.media.MediaDrmThrowable
    public int getVendorError() {
        return this.mVendorError;
    }

    @Override // android.media.MediaDrmThrowable
    public int getOemError() {
        return this.mOemError;
    }

    @Override // android.media.MediaDrmThrowable
    public int getErrorContext() {
        return this.mErrorContext;
    }
}
