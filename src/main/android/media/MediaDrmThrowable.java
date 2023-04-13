package android.media;
/* loaded from: classes2.dex */
public interface MediaDrmThrowable {
    default int getVendorError() {
        return 0;
    }

    default int getOemError() {
        return 0;
    }

    default int getErrorContext() {
        return 0;
    }
}
