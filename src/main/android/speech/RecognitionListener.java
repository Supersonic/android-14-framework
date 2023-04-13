package android.speech;

import android.p008os.Bundle;
/* loaded from: classes3.dex */
public interface RecognitionListener {
    void onBeginningOfSpeech();

    void onBufferReceived(byte[] bArr);

    void onEndOfSpeech();

    void onError(int i);

    void onEvent(int i, Bundle bundle);

    void onPartialResults(Bundle bundle);

    void onReadyForSpeech(Bundle bundle);

    void onResults(Bundle bundle);

    void onRmsChanged(float f);

    default void onSegmentResults(Bundle segmentResults) {
    }

    default void onEndOfSegmentedSession() {
    }

    default void onLanguageDetection(Bundle results) {
    }
}
