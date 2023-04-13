package android.media;
/* loaded from: classes2.dex */
public final class DeniedByServerException extends MediaDrmException {
    public DeniedByServerException(String detailMessage) {
        super(detailMessage);
    }

    public DeniedByServerException(String message, int vendorError, int oemError, int context) {
        super(message, vendorError, oemError, context);
    }
}
