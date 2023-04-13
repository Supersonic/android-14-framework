package android.media;
/* loaded from: classes2.dex */
public class MediaCasException extends Exception {
    private MediaCasException(String detailMessage) {
        super(detailMessage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void throwExceptionIfNeeded(int error) throws MediaCasException {
        if (error == 0) {
            return;
        }
        if (error == 7) {
            throw new NotProvisionedException(null);
        }
        if (error == 8) {
            throw new ResourceBusyException(null);
        }
        if (error == 11) {
            throw new DeniedByServerException(null);
        }
        MediaCasStateException.throwExceptionIfNeeded(error);
    }

    /* loaded from: classes2.dex */
    public static final class UnsupportedCasException extends MediaCasException {
        public UnsupportedCasException(String detailMessage) {
            super(detailMessage);
        }
    }

    /* loaded from: classes2.dex */
    public static final class NotProvisionedException extends MediaCasException {
        public NotProvisionedException(String detailMessage) {
            super(detailMessage);
        }
    }

    /* loaded from: classes2.dex */
    public static final class DeniedByServerException extends MediaCasException {
        public DeniedByServerException(String detailMessage) {
            super(detailMessage);
        }
    }

    /* loaded from: classes2.dex */
    public static final class ResourceBusyException extends MediaCasException {
        public ResourceBusyException(String detailMessage) {
            super(detailMessage);
        }
    }

    /* loaded from: classes2.dex */
    public static final class InsufficientResourceException extends MediaCasException {
        public InsufficientResourceException(String detailMessage) {
            super(detailMessage);
        }
    }
}
