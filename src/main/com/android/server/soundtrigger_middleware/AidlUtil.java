package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseRecognitionExtra;
import android.media.soundtrigger.RecognitionEvent;
/* loaded from: classes2.dex */
public class AidlUtil {
    public static RecognitionEvent newEmptyRecognitionEvent() {
        RecognitionEvent recognitionEvent = new RecognitionEvent();
        recognitionEvent.data = new byte[0];
        return recognitionEvent;
    }

    public static PhraseRecognitionEvent newEmptyPhraseRecognitionEvent() {
        PhraseRecognitionEvent phraseRecognitionEvent = new PhraseRecognitionEvent();
        phraseRecognitionEvent.common = newEmptyRecognitionEvent();
        phraseRecognitionEvent.phraseExtras = new PhraseRecognitionExtra[0];
        return phraseRecognitionEvent;
    }

    public static RecognitionEvent newAbortEvent() {
        RecognitionEvent newEmptyRecognitionEvent = newEmptyRecognitionEvent();
        newEmptyRecognitionEvent.type = 1;
        newEmptyRecognitionEvent.status = 1;
        return newEmptyRecognitionEvent;
    }

    public static PhraseRecognitionEvent newAbortPhraseEvent() {
        PhraseRecognitionEvent newEmptyPhraseRecognitionEvent = newEmptyPhraseRecognitionEvent();
        RecognitionEvent recognitionEvent = newEmptyPhraseRecognitionEvent.common;
        recognitionEvent.type = 0;
        recognitionEvent.status = 1;
        return newEmptyPhraseRecognitionEvent;
    }
}
