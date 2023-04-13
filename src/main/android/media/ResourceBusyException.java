package android.media;
/* loaded from: classes2.dex */
public final class ResourceBusyException extends MediaDrmException {
    public ResourceBusyException(String detailMessage) {
        super(detailMessage);
    }

    public ResourceBusyException(String message, int vendorError, int oemError, int context) {
        super(message, vendorError, oemError, context);
    }
}
