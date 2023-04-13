package com.android.server.soundtrigger_middleware;

import android.media.soundtrigger.ConfidenceLevel;
import android.media.soundtrigger.Phrase;
import android.media.soundtrigger.PhraseRecognitionExtra;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.SoundModel;
import java.util.Objects;
/* loaded from: classes2.dex */
public class ValidationUtil {
    public static void validateUuid(String str) {
        Objects.requireNonNull(str);
        if (UuidUtil.PATTERN.matcher(str).matches()) {
            return;
        }
        throw new IllegalArgumentException("Illegal format for UUID: " + str);
    }

    public static void validateGenericModel(SoundModel soundModel) {
        validateModel(soundModel, 1);
    }

    public static void validateModel(SoundModel soundModel, int i) {
        Objects.requireNonNull(soundModel);
        if (soundModel.type != i) {
            throw new IllegalArgumentException("Invalid type");
        }
        validateUuid(soundModel.uuid);
        validateUuid(soundModel.vendorUuid);
        if (soundModel.dataSize > 0) {
            Objects.requireNonNull(soundModel.data);
        }
    }

    public static void validatePhraseModel(PhraseSoundModel phraseSoundModel) {
        Phrase[] phraseArr;
        Objects.requireNonNull(phraseSoundModel);
        validateModel(phraseSoundModel.common, 0);
        Objects.requireNonNull(phraseSoundModel.phrases);
        for (Phrase phrase : phraseSoundModel.phrases) {
            Objects.requireNonNull(phrase);
            if ((phrase.recognitionModes & (-16)) != 0) {
                throw new IllegalArgumentException("Invalid recognitionModes");
            }
            Objects.requireNonNull(phrase.users);
            Objects.requireNonNull(phrase.locale);
            Objects.requireNonNull(phrase.text);
        }
    }

    public static void validateRecognitionConfig(RecognitionConfig recognitionConfig) {
        PhraseRecognitionExtra[] phraseRecognitionExtraArr;
        ConfidenceLevel[] confidenceLevelArr;
        Objects.requireNonNull(recognitionConfig);
        Objects.requireNonNull(recognitionConfig.phraseRecognitionExtras);
        for (PhraseRecognitionExtra phraseRecognitionExtra : recognitionConfig.phraseRecognitionExtras) {
            Objects.requireNonNull(phraseRecognitionExtra);
            if ((phraseRecognitionExtra.recognitionModes & (-16)) != 0) {
                throw new IllegalArgumentException("Invalid recognitionModes");
            }
            int i = phraseRecognitionExtra.confidenceLevel;
            if (i < 0 || i > 100) {
                throw new IllegalArgumentException("Invalid confidenceLevel");
            }
            Objects.requireNonNull(phraseRecognitionExtra.levels);
            for (ConfidenceLevel confidenceLevel : phraseRecognitionExtra.levels) {
                Objects.requireNonNull(confidenceLevel);
                int i2 = confidenceLevel.levelPercent;
                if (i2 < 0 || i2 > 100) {
                    throw new IllegalArgumentException("Invalid confidenceLevel");
                }
            }
        }
        Objects.requireNonNull(recognitionConfig.data);
    }

    public static void validateModelParameter(int i) {
        if (i != 0) {
            throw new IllegalArgumentException("Invalid model parameter");
        }
    }
}
