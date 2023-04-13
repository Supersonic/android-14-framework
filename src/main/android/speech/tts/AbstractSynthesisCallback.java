package android.speech.tts;
/* loaded from: classes3.dex */
abstract class AbstractSynthesisCallback implements SynthesisCallback {
    protected final boolean mClientIsUsingV2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void stop();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractSynthesisCallback(boolean clientIsUsingV2) {
        this.mClientIsUsingV2 = clientIsUsingV2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int errorCodeOnStop() {
        return this.mClientIsUsingV2 ? -2 : -1;
    }
}
