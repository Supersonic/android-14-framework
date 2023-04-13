package com.android.server.soundtrigger_middleware;

import android.hardware.audio.common.V2_0.Uuid;
import android.hardware.soundtrigger.V2_0.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHw;
import android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_3.RecognitionConfig;
import android.media.audio.common.AidlConversion;
import android.media.audio.common.AudioConfig;
import android.media.audio.common.AudioConfigBase;
import android.media.audio.common.AudioOffloadInfo;
import android.media.soundtrigger.ConfidenceLevel;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.Phrase;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseRecognitionExtra;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.Properties;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.os.HidlMemory;
import android.os.HidlMemoryUtil;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.util.regex.Matcher;
/* loaded from: classes2.dex */
public class ConversionUtil {
    public static int aidl2hidlModelParameter(int i) {
        return i != 0 ? -1 : 0;
    }

    public static int aidl2hidlRecognitionModes(int i) {
        int i2 = (i & 1) != 0 ? 1 : 0;
        if ((i & 2) != 0) {
            i2 |= 2;
        }
        if ((i & 4) != 0) {
            i2 |= 4;
        }
        return (i & 8) != 0 ? i2 | 8 : i2;
    }

    public static int hidl2aidlAudioCapabilities(int i) {
        int i2 = (i & 1) != 0 ? 1 : 0;
        return (i & 2) != 0 ? i2 | 2 : i2;
    }

    public static int hidl2aidlRecognitionModes(int i) {
        int i2 = (i & 1) != 0 ? 1 : 0;
        if ((i & 2) != 0) {
            i2 |= 2;
        }
        if ((i & 4) != 0) {
            i2 |= 4;
        }
        return (i & 8) != 0 ? i2 | 8 : i2;
    }

    public static Properties hidl2aidlProperties(ISoundTriggerHw.Properties properties) {
        Properties properties2 = new Properties();
        properties2.implementor = properties.implementor;
        properties2.description = properties.description;
        properties2.version = properties.version;
        properties2.uuid = hidl2aidlUuid(properties.uuid);
        properties2.maxSoundModels = properties.maxSoundModels;
        properties2.maxKeyPhrases = properties.maxKeyPhrases;
        properties2.maxUsers = properties.maxUsers;
        properties2.recognitionModes = hidl2aidlRecognitionModes(properties.recognitionModes);
        properties2.captureTransition = properties.captureTransition;
        properties2.maxBufferMs = properties.maxBufferMs;
        properties2.concurrentCapture = properties.concurrentCapture;
        properties2.triggerInEvent = properties.triggerInEvent;
        properties2.powerConsumptionMw = properties.powerConsumptionMw;
        return properties2;
    }

    public static Properties hidl2aidlProperties(android.hardware.soundtrigger.V2_3.Properties properties) {
        Properties hidl2aidlProperties = hidl2aidlProperties(properties.base);
        hidl2aidlProperties.supportedModelArch = properties.supportedModelArch;
        hidl2aidlProperties.audioCapabilities = hidl2aidlAudioCapabilities(properties.audioCapabilities);
        return hidl2aidlProperties;
    }

    public static String hidl2aidlUuid(Uuid uuid) {
        byte[] bArr = uuid.node;
        if (bArr == null || bArr.length != 6) {
            throw new IllegalArgumentException("UUID.node must be of length 6.");
        }
        return String.format("%08x-%04x-%04x-%04x-%02x%02x%02x%02x%02x%02x", Integer.valueOf(uuid.timeLow), Short.valueOf(uuid.timeMid), Short.valueOf(uuid.versionAndTimeHigh), Short.valueOf(uuid.variantAndClockSeqHigh), Byte.valueOf(uuid.node[0]), Byte.valueOf(uuid.node[1]), Byte.valueOf(uuid.node[2]), Byte.valueOf(uuid.node[3]), Byte.valueOf(uuid.node[4]), Byte.valueOf(uuid.node[5]));
    }

    public static Uuid aidl2hidlUuid(String str) {
        Matcher matcher = UuidUtil.PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal format for UUID: " + str);
        }
        Uuid uuid = new Uuid();
        uuid.timeLow = Integer.parseUnsignedInt(matcher.group(1), 16);
        uuid.timeMid = (short) Integer.parseUnsignedInt(matcher.group(2), 16);
        uuid.versionAndTimeHigh = (short) Integer.parseUnsignedInt(matcher.group(3), 16);
        uuid.variantAndClockSeqHigh = (short) Integer.parseUnsignedInt(matcher.group(4), 16);
        uuid.node = new byte[]{(byte) Integer.parseUnsignedInt(matcher.group(5), 16), (byte) Integer.parseUnsignedInt(matcher.group(6), 16), (byte) Integer.parseUnsignedInt(matcher.group(7), 16), (byte) Integer.parseUnsignedInt(matcher.group(8), 16), (byte) Integer.parseUnsignedInt(matcher.group(9), 16), (byte) Integer.parseUnsignedInt(matcher.group(10), 16)};
        return uuid;
    }

    public static int aidl2hidlSoundModelType(int i) {
        if (i != 0) {
            if (i == 1) {
                return 1;
            }
            throw new IllegalArgumentException("Unknown sound model type: " + i);
        }
        return 0;
    }

    public static int hidl2aidlSoundModelType(int i) {
        if (i != 0) {
            if (i == 1) {
                return 1;
            }
            throw new IllegalArgumentException("Unknown sound model type: " + i);
        }
        return 0;
    }

    public static ISoundTriggerHw.Phrase aidl2hidlPhrase(Phrase phrase) {
        ISoundTriggerHw.Phrase phrase2 = new ISoundTriggerHw.Phrase();
        phrase2.f12id = phrase.id;
        phrase2.recognitionModes = aidl2hidlRecognitionModes(phrase.recognitionModes);
        for (int i : phrase.users) {
            phrase2.users.add(Integer.valueOf(i));
        }
        phrase2.locale = phrase.locale;
        phrase2.text = phrase.text;
        return phrase2;
    }

    public static ISoundTriggerHw.SoundModel aidl2hidlSoundModel(SoundModel soundModel) {
        ISoundTriggerHw.SoundModel soundModel2 = new ISoundTriggerHw.SoundModel();
        soundModel2.header.type = aidl2hidlSoundModelType(soundModel.type);
        soundModel2.header.uuid = aidl2hidlUuid(soundModel.uuid);
        soundModel2.header.vendorUuid = aidl2hidlUuid(soundModel.vendorUuid);
        soundModel2.data = parcelFileDescriptorToHidlMemory(soundModel.data, soundModel.dataSize);
        return soundModel2;
    }

    public static ISoundTriggerHw.PhraseSoundModel aidl2hidlPhraseSoundModel(PhraseSoundModel phraseSoundModel) {
        ISoundTriggerHw.PhraseSoundModel phraseSoundModel2 = new ISoundTriggerHw.PhraseSoundModel();
        phraseSoundModel2.common = aidl2hidlSoundModel(phraseSoundModel.common);
        for (Phrase phrase : phraseSoundModel.phrases) {
            phraseSoundModel2.phrases.add(aidl2hidlPhrase(phrase));
        }
        return phraseSoundModel2;
    }

    public static RecognitionConfig aidl2hidlRecognitionConfig(android.media.soundtrigger.RecognitionConfig recognitionConfig, int i, int i2) {
        RecognitionConfig recognitionConfig2 = new RecognitionConfig();
        ISoundTriggerHw.RecognitionConfig recognitionConfig3 = recognitionConfig2.base.header;
        recognitionConfig3.captureDevice = i;
        recognitionConfig3.captureHandle = i2;
        recognitionConfig3.captureRequested = recognitionConfig.captureRequested;
        for (PhraseRecognitionExtra phraseRecognitionExtra : recognitionConfig.phraseRecognitionExtras) {
            recognitionConfig2.base.header.phrases.add(aidl2hidlPhraseRecognitionExtra(phraseRecognitionExtra));
        }
        recognitionConfig2.base.data = HidlMemoryUtil.byteArrayToHidlMemory(recognitionConfig.data, "SoundTrigger RecognitionConfig");
        recognitionConfig2.audioCapabilities = recognitionConfig.audioCapabilities;
        return recognitionConfig2;
    }

    public static android.hardware.soundtrigger.V2_0.PhraseRecognitionExtra aidl2hidlPhraseRecognitionExtra(PhraseRecognitionExtra phraseRecognitionExtra) {
        android.hardware.soundtrigger.V2_0.PhraseRecognitionExtra phraseRecognitionExtra2 = new android.hardware.soundtrigger.V2_0.PhraseRecognitionExtra();
        phraseRecognitionExtra2.f13id = phraseRecognitionExtra.id;
        phraseRecognitionExtra2.recognitionModes = aidl2hidlRecognitionModes(phraseRecognitionExtra.recognitionModes);
        phraseRecognitionExtra2.confidenceLevel = phraseRecognitionExtra.confidenceLevel;
        phraseRecognitionExtra2.levels.ensureCapacity(phraseRecognitionExtra.levels.length);
        for (ConfidenceLevel confidenceLevel : phraseRecognitionExtra.levels) {
            phraseRecognitionExtra2.levels.add(aidl2hidlConfidenceLevel(confidenceLevel));
        }
        return phraseRecognitionExtra2;
    }

    public static PhraseRecognitionExtra hidl2aidlPhraseRecognitionExtra(android.hardware.soundtrigger.V2_0.PhraseRecognitionExtra phraseRecognitionExtra) {
        PhraseRecognitionExtra phraseRecognitionExtra2 = new PhraseRecognitionExtra();
        phraseRecognitionExtra2.id = phraseRecognitionExtra.f13id;
        phraseRecognitionExtra2.recognitionModes = hidl2aidlRecognitionModes(phraseRecognitionExtra.recognitionModes);
        phraseRecognitionExtra2.confidenceLevel = phraseRecognitionExtra.confidenceLevel;
        phraseRecognitionExtra2.levels = new ConfidenceLevel[phraseRecognitionExtra.levels.size()];
        for (int i = 0; i < phraseRecognitionExtra.levels.size(); i++) {
            phraseRecognitionExtra2.levels[i] = hidl2aidlConfidenceLevel(phraseRecognitionExtra.levels.get(i));
        }
        return phraseRecognitionExtra2;
    }

    public static android.hardware.soundtrigger.V2_0.ConfidenceLevel aidl2hidlConfidenceLevel(ConfidenceLevel confidenceLevel) {
        android.hardware.soundtrigger.V2_0.ConfidenceLevel confidenceLevel2 = new android.hardware.soundtrigger.V2_0.ConfidenceLevel();
        confidenceLevel2.userId = confidenceLevel.userId;
        confidenceLevel2.levelPercent = confidenceLevel.levelPercent;
        return confidenceLevel2;
    }

    public static ConfidenceLevel hidl2aidlConfidenceLevel(android.hardware.soundtrigger.V2_0.ConfidenceLevel confidenceLevel) {
        ConfidenceLevel confidenceLevel2 = new ConfidenceLevel();
        confidenceLevel2.userId = confidenceLevel.userId;
        confidenceLevel2.levelPercent = confidenceLevel.levelPercent;
        return confidenceLevel2;
    }

    public static int hidl2aidlRecognitionStatus(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    if (i == 3) {
                        return 3;
                    }
                    throw new IllegalArgumentException("Unknown recognition status: " + i);
                }
            }
            return i2;
        }
        return 0;
    }

    public static RecognitionEvent hidl2aidlRecognitionEvent(ISoundTriggerHwCallback.RecognitionEvent recognitionEvent) {
        RecognitionEvent recognitionEvent2 = new RecognitionEvent();
        recognitionEvent2.status = hidl2aidlRecognitionStatus(recognitionEvent.status);
        recognitionEvent2.type = hidl2aidlSoundModelType(recognitionEvent.type);
        recognitionEvent2.captureAvailable = recognitionEvent.captureAvailable;
        recognitionEvent2.captureDelayMs = recognitionEvent.captureDelayMs;
        recognitionEvent2.capturePreambleMs = recognitionEvent.capturePreambleMs;
        recognitionEvent2.triggerInData = recognitionEvent.triggerInData;
        recognitionEvent2.audioConfig = hidl2aidlAudioConfig(recognitionEvent.audioConfig, true);
        recognitionEvent2.data = new byte[recognitionEvent.data.size()];
        int i = 0;
        while (true) {
            byte[] bArr = recognitionEvent2.data;
            if (i >= bArr.length) {
                break;
            }
            bArr[i] = recognitionEvent.data.get(i).byteValue();
            i++;
        }
        recognitionEvent2.recognitionStillActive = recognitionEvent2.status == 3;
        return recognitionEvent2;
    }

    public static RecognitionEvent hidl2aidlRecognitionEvent(ISoundTriggerHwCallback.RecognitionEvent recognitionEvent) {
        RecognitionEvent hidl2aidlRecognitionEvent = hidl2aidlRecognitionEvent(recognitionEvent.header);
        hidl2aidlRecognitionEvent.data = HidlMemoryUtil.hidlMemoryToByteArray(recognitionEvent.data);
        return hidl2aidlRecognitionEvent;
    }

    public static PhraseRecognitionEvent hidl2aidlPhraseRecognitionEvent(ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent) {
        PhraseRecognitionEvent phraseRecognitionEvent2 = new PhraseRecognitionEvent();
        phraseRecognitionEvent2.common = hidl2aidlRecognitionEvent(phraseRecognitionEvent.common);
        phraseRecognitionEvent2.phraseExtras = new PhraseRecognitionExtra[phraseRecognitionEvent.phraseExtras.size()];
        for (int i = 0; i < phraseRecognitionEvent.phraseExtras.size(); i++) {
            phraseRecognitionEvent2.phraseExtras[i] = hidl2aidlPhraseRecognitionExtra(phraseRecognitionEvent.phraseExtras.get(i));
        }
        return phraseRecognitionEvent2;
    }

    public static AudioConfig hidl2aidlAudioConfig(android.hardware.audio.common.V2_0.AudioConfig audioConfig, boolean z) {
        AudioConfig audioConfig2 = new AudioConfig();
        audioConfig2.base = hidl2aidlAudioConfigBase(audioConfig.sampleRateHz, audioConfig.channelMask, audioConfig.format, z);
        audioConfig2.offloadInfo = hidl2aidlOffloadInfo(audioConfig.offloadInfo);
        audioConfig2.frameCount = audioConfig.frameCount;
        return audioConfig2;
    }

    public static AudioOffloadInfo hidl2aidlOffloadInfo(android.hardware.audio.common.V2_0.AudioOffloadInfo audioOffloadInfo) {
        AudioOffloadInfo audioOffloadInfo2 = new AudioOffloadInfo();
        audioOffloadInfo2.base = hidl2aidlAudioConfigBase(audioOffloadInfo.sampleRateHz, audioOffloadInfo.channelMask, audioOffloadInfo.format, false);
        audioOffloadInfo2.streamType = AidlConversion.legacy2aidl_audio_stream_type_t_AudioStreamType(audioOffloadInfo.streamType);
        audioOffloadInfo2.bitRatePerSecond = audioOffloadInfo.bitRatePerSecond;
        audioOffloadInfo2.durationUs = audioOffloadInfo.durationMicroseconds;
        audioOffloadInfo2.hasVideo = audioOffloadInfo.hasVideo;
        audioOffloadInfo2.isStreaming = audioOffloadInfo.isStreaming;
        audioOffloadInfo2.bitWidth = audioOffloadInfo.bitWidth;
        audioOffloadInfo2.offloadBufferSize = audioOffloadInfo.bufferSize;
        audioOffloadInfo2.usage = AidlConversion.legacy2aidl_audio_usage_t_AudioUsage(audioOffloadInfo.usage);
        return audioOffloadInfo2;
    }

    public static AudioConfigBase hidl2aidlAudioConfigBase(int i, int i2, int i3, boolean z) {
        AudioConfigBase audioConfigBase = new AudioConfigBase();
        audioConfigBase.sampleRate = i;
        audioConfigBase.channelMask = AidlConversion.legacy2aidl_audio_channel_mask_t_AudioChannelLayout(i2, z);
        audioConfigBase.format = AidlConversion.legacy2aidl_audio_format_t_AudioFormatDescription(i3);
        return audioConfigBase;
    }

    public static ModelParameterRange hidl2aidlModelParameterRange(android.hardware.soundtrigger.V2_3.ModelParameterRange modelParameterRange) {
        if (modelParameterRange == null) {
            return null;
        }
        ModelParameterRange modelParameterRange2 = new ModelParameterRange();
        modelParameterRange2.minInclusive = modelParameterRange.start;
        modelParameterRange2.maxInclusive = modelParameterRange.end;
        return modelParameterRange2;
    }

    public static HidlMemory parcelFileDescriptorToHidlMemory(ParcelFileDescriptor parcelFileDescriptor, int i) {
        if (i > 0) {
            return HidlMemoryUtil.fileDescriptorToHidlMemory(parcelFileDescriptor.getFileDescriptor(), i);
        }
        return HidlMemoryUtil.fileDescriptorToHidlMemory((FileDescriptor) null, 0);
    }
}
