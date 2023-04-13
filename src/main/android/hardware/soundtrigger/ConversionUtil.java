package android.hardware.soundtrigger;

import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.media.audio.common.AidlConversion;
import android.media.audio.common.AudioConfig;
import android.media.soundtrigger.ConfidenceLevel;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.Phrase;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseRecognitionExtra;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.p008os.ParcelFileDescriptor;
import android.p008os.SharedMemory;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
/* loaded from: classes2.dex */
class ConversionUtil {
    ConversionUtil() {
    }

    public static SoundTrigger.ModuleProperties aidl2apiModuleDescriptor(SoundTriggerModuleDescriptor aidlDesc) {
        Properties properties = aidlDesc.properties;
        return new SoundTrigger.ModuleProperties(aidlDesc.handle, properties.implementor, properties.description, properties.uuid, properties.version, properties.supportedModelArch, properties.maxSoundModels, properties.maxKeyPhrases, properties.maxUsers, aidl2apiRecognitionModes(properties.recognitionModes), properties.captureTransition, properties.maxBufferMs, properties.concurrentCapture, properties.powerConsumptionMw, properties.triggerInEvent, aidl2apiAudioCapabilities(properties.audioCapabilities));
    }

    public static int aidl2apiRecognitionModes(int aidlModes) {
        int result = 0;
        if ((aidlModes & 1) != 0) {
            result = 0 | 1;
        }
        if ((aidlModes & 2) != 0) {
            result |= 2;
        }
        if ((aidlModes & 4) != 0) {
            result |= 4;
        }
        if ((aidlModes & 8) != 0) {
            return result | 8;
        }
        return result;
    }

    public static int api2aidlRecognitionModes(int apiModes) {
        int result = 0;
        if ((apiModes & 1) != 0) {
            result = 0 | 1;
        }
        if ((apiModes & 2) != 0) {
            result |= 2;
        }
        if ((apiModes & 4) != 0) {
            result |= 4;
        }
        if ((apiModes & 8) != 0) {
            return result | 8;
        }
        return result;
    }

    public static SoundModel api2aidlGenericSoundModel(SoundTrigger.GenericSoundModel apiModel) {
        return api2aidlSoundModel(apiModel);
    }

    public static SoundModel api2aidlSoundModel(SoundTrigger.SoundModel apiModel) {
        SoundModel aidlModel = new SoundModel();
        aidlModel.type = apiModel.getType();
        aidlModel.uuid = api2aidlUuid(apiModel.getUuid());
        aidlModel.vendorUuid = api2aidlUuid(apiModel.getVendorUuid());
        byte[] data = apiModel.getData();
        aidlModel.data = byteArrayToSharedMemory(data, "SoundTrigger SoundModel");
        aidlModel.dataSize = data.length;
        return aidlModel;
    }

    public static String api2aidlUuid(UUID apiUuid) {
        return apiUuid.toString();
    }

    public static PhraseSoundModel api2aidlPhraseSoundModel(SoundTrigger.KeyphraseSoundModel apiModel) {
        PhraseSoundModel aidlModel = new PhraseSoundModel();
        aidlModel.common = api2aidlSoundModel(apiModel);
        aidlModel.phrases = new Phrase[apiModel.getKeyphrases().length];
        for (int i = 0; i < apiModel.getKeyphrases().length; i++) {
            aidlModel.phrases[i] = api2aidlPhrase(apiModel.getKeyphrases()[i]);
        }
        return aidlModel;
    }

    public static Phrase api2aidlPhrase(SoundTrigger.Keyphrase apiPhrase) {
        Phrase aidlPhrase = new Phrase();
        aidlPhrase.f296id = apiPhrase.getId();
        aidlPhrase.recognitionModes = api2aidlRecognitionModes(apiPhrase.getRecognitionModes());
        aidlPhrase.users = Arrays.copyOf(apiPhrase.getUsers(), apiPhrase.getUsers().length);
        aidlPhrase.locale = apiPhrase.getLocale().toLanguageTag();
        aidlPhrase.text = apiPhrase.getText();
        return aidlPhrase;
    }

    public static RecognitionConfig api2aidlRecognitionConfig(SoundTrigger.RecognitionConfig apiConfig) {
        RecognitionConfig aidlConfig = new RecognitionConfig();
        aidlConfig.captureRequested = apiConfig.captureRequested;
        aidlConfig.phraseRecognitionExtras = new PhraseRecognitionExtra[apiConfig.keyphrases.length];
        for (int i = 0; i < apiConfig.keyphrases.length; i++) {
            aidlConfig.phraseRecognitionExtras[i] = api2aidlPhraseRecognitionExtra(apiConfig.keyphrases[i]);
        }
        aidlConfig.data = Arrays.copyOf(apiConfig.data, apiConfig.data.length);
        aidlConfig.audioCapabilities = api2aidlAudioCapabilities(apiConfig.audioCapabilities);
        return aidlConfig;
    }

    public static PhraseRecognitionExtra api2aidlPhraseRecognitionExtra(SoundTrigger.KeyphraseRecognitionExtra apiExtra) {
        PhraseRecognitionExtra aidlExtra = new PhraseRecognitionExtra();
        aidlExtra.f297id = apiExtra.f214id;
        aidlExtra.recognitionModes = api2aidlRecognitionModes(apiExtra.recognitionModes);
        aidlExtra.confidenceLevel = apiExtra.coarseConfidenceLevel;
        aidlExtra.levels = new ConfidenceLevel[apiExtra.confidenceLevels.length];
        for (int i = 0; i < apiExtra.confidenceLevels.length; i++) {
            aidlExtra.levels[i] = api2aidlConfidenceLevel(apiExtra.confidenceLevels[i]);
        }
        return aidlExtra;
    }

    public static SoundTrigger.KeyphraseRecognitionExtra aidl2apiPhraseRecognitionExtra(PhraseRecognitionExtra aidlExtra) {
        SoundTrigger.ConfidenceLevel[] apiLevels = new SoundTrigger.ConfidenceLevel[aidlExtra.levels.length];
        for (int i = 0; i < aidlExtra.levels.length; i++) {
            apiLevels[i] = aidl2apiConfidenceLevel(aidlExtra.levels[i]);
        }
        return new SoundTrigger.KeyphraseRecognitionExtra(aidlExtra.f297id, aidl2apiRecognitionModes(aidlExtra.recognitionModes), aidlExtra.confidenceLevel, apiLevels);
    }

    public static ConfidenceLevel api2aidlConfidenceLevel(SoundTrigger.ConfidenceLevel apiLevel) {
        ConfidenceLevel aidlLevel = new ConfidenceLevel();
        aidlLevel.levelPercent = apiLevel.confidenceLevel;
        aidlLevel.userId = apiLevel.userId;
        return aidlLevel;
    }

    public static SoundTrigger.ConfidenceLevel aidl2apiConfidenceLevel(ConfidenceLevel apiLevel) {
        return new SoundTrigger.ConfidenceLevel(apiLevel.userId, apiLevel.levelPercent);
    }

    public static SoundTrigger.RecognitionEvent aidl2apiRecognitionEvent(int modelHandle, int captureSession, RecognitionEvent aidlEvent) {
        AudioFormat audioFormat = aidl2apiAudioFormatWithDefault(aidlEvent.audioConfig, true);
        return new SoundTrigger.GenericRecognitionEvent(aidlEvent.status, modelHandle, aidlEvent.captureAvailable, captureSession, aidlEvent.captureDelayMs, aidlEvent.capturePreambleMs, aidlEvent.triggerInData, audioFormat, aidlEvent.data, aidlEvent.recognitionStillActive);
    }

    public static SoundTrigger.RecognitionEvent aidl2apiPhraseRecognitionEvent(int modelHandle, int captureSession, PhraseRecognitionEvent aidlEvent) {
        SoundTrigger.KeyphraseRecognitionExtra[] apiExtras = new SoundTrigger.KeyphraseRecognitionExtra[aidlEvent.phraseExtras.length];
        for (int i = 0; i < aidlEvent.phraseExtras.length; i++) {
            apiExtras[i] = aidl2apiPhraseRecognitionExtra(aidlEvent.phraseExtras[i]);
        }
        AudioFormat audioFormat = aidl2apiAudioFormatWithDefault(aidlEvent.common.audioConfig, true);
        return new SoundTrigger.KeyphraseRecognitionEvent(aidlEvent.common.status, modelHandle, aidlEvent.common.captureAvailable, captureSession, aidlEvent.common.captureDelayMs, aidlEvent.common.capturePreambleMs, aidlEvent.common.triggerInData, audioFormat, aidlEvent.common.data, apiExtras);
    }

    public static AudioFormat aidl2apiAudioFormatWithDefault(AudioConfig audioConfig, boolean isInput) {
        if (audioConfig != null) {
            return AidlConversion.aidl2api_AudioConfig_AudioFormat(audioConfig, isInput);
        }
        return new AudioFormat.Builder().build();
    }

    public static int api2aidlModelParameter(int apiParam) {
        switch (apiParam) {
            case 0:
                return 0;
            default:
                return -1;
        }
    }

    public static SoundTrigger.ModelParamRange aidl2apiModelParameterRange(ModelParameterRange aidlRange) {
        if (aidlRange == null) {
            return null;
        }
        return new SoundTrigger.ModelParamRange(aidlRange.minInclusive, aidlRange.maxInclusive);
    }

    public static int aidl2apiAudioCapabilities(int aidlCapabilities) {
        int result = 0;
        if ((aidlCapabilities & 1) != 0) {
            result = 0 | 1;
        }
        if ((aidlCapabilities & 2) != 0) {
            return result | 2;
        }
        return result;
    }

    public static int api2aidlAudioCapabilities(int apiCapabilities) {
        int result = 0;
        if ((apiCapabilities & 1) != 0) {
            result = 0 | 1;
        }
        if ((apiCapabilities & 2) != 0) {
            return result | 2;
        }
        return result;
    }

    private static ParcelFileDescriptor byteArrayToSharedMemory(byte[] data, String name) {
        if (data.length == 0) {
            return null;
        }
        try {
            SharedMemory shmem = SharedMemory.create(name != null ? name : "", data.length);
            ByteBuffer buffer = shmem.mapReadWrite();
            buffer.put(data);
            SharedMemory.unmap(buffer);
            ParcelFileDescriptor fd = shmem.getFdDup();
            shmem.close();
            return fd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
