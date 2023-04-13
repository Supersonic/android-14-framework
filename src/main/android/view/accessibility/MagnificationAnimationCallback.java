package android.view.accessibility;
/* loaded from: classes4.dex */
public interface MagnificationAnimationCallback {
    public static final MagnificationAnimationCallback STUB_ANIMATION_CALLBACK = new MagnificationAnimationCallback() { // from class: android.view.accessibility.MagnificationAnimationCallback$$ExternalSyntheticLambda0
        @Override // android.view.accessibility.MagnificationAnimationCallback
        public final void onResult(boolean z) {
            MagnificationAnimationCallback.lambda$static$0(z);
        }
    };

    void onResult(boolean z);

    static /* synthetic */ void lambda$static$0(boolean success) {
    }
}
