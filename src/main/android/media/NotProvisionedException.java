package android.media;
/* loaded from: classes2.dex */
public final class NotProvisionedException extends MediaDrmException {
    public NotProvisionedException(String detailMessage) {
        super(detailMessage);
    }

    public NotProvisionedException(String message, int vendorError, int oemError, int context) {
        super(message, vendorError, oemError, context);
    }
}
