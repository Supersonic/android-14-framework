package android.speech;
/* loaded from: classes3.dex */
public interface ModelDownloadListener {
    void onError(int i);

    void onProgress(int i);

    void onScheduled();

    void onSuccess();
}
