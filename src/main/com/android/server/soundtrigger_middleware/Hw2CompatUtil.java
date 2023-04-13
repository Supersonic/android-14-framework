package com.android.server.soundtrigger_middleware;

import android.hardware.soundtrigger.V2_0.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_3.Properties;
import android.hardware.soundtrigger.V2_3.RecognitionConfig;
import android.os.HidlMemoryUtil;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class Hw2CompatUtil {
    public static ISoundTriggerHw.SoundModel convertSoundModel_2_1_to_2_0(ISoundTriggerHw.SoundModel soundModel) {
        ISoundTriggerHw.SoundModel soundModel2 = soundModel.header;
        soundModel2.data = HidlMemoryUtil.hidlMemoryToByteList(soundModel.data);
        return soundModel2;
    }

    public static ISoundTriggerHwCallback.RecognitionEvent convertRecognitionEvent_2_0_to_2_1(ISoundTriggerHwCallback.RecognitionEvent recognitionEvent) {
        ISoundTriggerHwCallback.RecognitionEvent recognitionEvent2 = new ISoundTriggerHwCallback.RecognitionEvent();
        recognitionEvent2.header = recognitionEvent;
        recognitionEvent2.data = HidlMemoryUtil.byteListToHidlMemory(recognitionEvent.data, "SoundTrigger RecognitionEvent");
        recognitionEvent2.header.data = new ArrayList<>();
        return recognitionEvent2;
    }

    public static ISoundTriggerHwCallback.PhraseRecognitionEvent convertPhraseRecognitionEvent_2_0_to_2_1(ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent) {
        ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent2 = new ISoundTriggerHwCallback.PhraseRecognitionEvent();
        phraseRecognitionEvent2.common = convertRecognitionEvent_2_0_to_2_1(phraseRecognitionEvent.common);
        phraseRecognitionEvent2.phraseExtras = phraseRecognitionEvent.phraseExtras;
        return phraseRecognitionEvent2;
    }

    public static ISoundTriggerHw.PhraseSoundModel convertPhraseSoundModel_2_1_to_2_0(ISoundTriggerHw.PhraseSoundModel phraseSoundModel) {
        ISoundTriggerHw.PhraseSoundModel phraseSoundModel2 = new ISoundTriggerHw.PhraseSoundModel();
        phraseSoundModel2.common = convertSoundModel_2_1_to_2_0(phraseSoundModel.common);
        phraseSoundModel2.phrases = phraseSoundModel.phrases;
        return phraseSoundModel2;
    }

    public static ISoundTriggerHw.RecognitionConfig convertRecognitionConfig_2_3_to_2_1(RecognitionConfig recognitionConfig) {
        return recognitionConfig.base;
    }

    public static ISoundTriggerHw.RecognitionConfig convertRecognitionConfig_2_3_to_2_0(RecognitionConfig recognitionConfig) {
        ISoundTriggerHw.RecognitionConfig recognitionConfig2 = recognitionConfig.base;
        ISoundTriggerHw.RecognitionConfig recognitionConfig3 = recognitionConfig2.header;
        recognitionConfig3.data = HidlMemoryUtil.hidlMemoryToByteList(recognitionConfig2.data);
        return recognitionConfig3;
    }

    public static Properties convertProperties_2_0_to_2_3(ISoundTriggerHw.Properties properties) {
        Properties properties2 = new Properties();
        properties2.base = properties;
        return properties2;
    }
}
