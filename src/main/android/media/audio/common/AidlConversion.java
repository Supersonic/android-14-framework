package android.media.audio.common;

import android.media.AudioDescriptor;
import android.media.AudioDeviceAttributes;
import android.media.AudioFormat;
import android.media.AudioSystem;
import android.media.MediaFormat;
import android.media.audiopolicy.AudioMixingRule;
import android.p008os.Parcel;
import com.android.internal.telephony.RILConstants;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class AidlConversion {
    private static native int aidl2legacy_AudioChannelLayout_Parcel_audio_channel_mask_t(Parcel parcel, boolean z);

    public static native int aidl2legacy_AudioEncapsulationMode_audio_encapsulation_mode_t(int i);

    private static native int aidl2legacy_AudioFormatDescription_Parcel_audio_format_t(Parcel parcel);

    public static native int aidl2legacy_AudioStreamType_audio_stream_type_t(int i);

    public static native int aidl2legacy_AudioUsage_audio_usage_t(int i);

    private static native Parcel legacy2aidl_audio_channel_mask_t_AudioChannelLayout_Parcel(int i, boolean z);

    public static native int legacy2aidl_audio_encapsulation_mode_t_AudioEncapsulationMode(int i);

    private static native Parcel legacy2aidl_audio_format_t_AudioFormatDescription_Parcel(int i);

    public static native int legacy2aidl_audio_stream_type_t_AudioStreamType(int i);

    public static native int legacy2aidl_audio_usage_t_AudioUsage(int i);

    public static int aidl2legacy_AudioChannelLayout_audio_channel_mask_t(AudioChannelLayout aidl, boolean isInput) {
        Parcel out = Parcel.obtain();
        aidl.writeToParcel(out, 0);
        out.setDataPosition(0);
        try {
            return aidl2legacy_AudioChannelLayout_Parcel_audio_channel_mask_t(out, isInput);
        } finally {
            out.recycle();
        }
    }

    public static AudioChannelLayout legacy2aidl_audio_channel_mask_t_AudioChannelLayout(int legacy, boolean isInput) {
        Parcel in = legacy2aidl_audio_channel_mask_t_AudioChannelLayout_Parcel(legacy, isInput);
        if (in != null) {
            try {
                return AudioChannelLayout.CREATOR.createFromParcel(in);
            } finally {
                in.recycle();
            }
        }
        throw new IllegalArgumentException("Failed to convert legacy audio " + (isInput ? "input" : "output") + " audio_channel_mask_t " + legacy + " value");
    }

    public static int aidl2legacy_AudioFormatDescription_audio_format_t(AudioFormatDescription aidl) {
        Parcel out = Parcel.obtain();
        aidl.writeToParcel(out, 0);
        out.setDataPosition(0);
        try {
            return aidl2legacy_AudioFormatDescription_Parcel_audio_format_t(out);
        } finally {
            out.recycle();
        }
    }

    public static AudioFormatDescription legacy2aidl_audio_format_t_AudioFormatDescription(int legacy) {
        Parcel in = legacy2aidl_audio_format_t_AudioFormatDescription_Parcel(legacy);
        if (in != null) {
            try {
                return AudioFormatDescription.CREATOR.createFromParcel(in);
            } finally {
                in.recycle();
            }
        }
        throw new IllegalArgumentException("Failed to convert legacy audio_format_t value " + legacy);
    }

    private static int aidl2api_AudioChannelLayoutBit_AudioFormatChannel(int aidlBit, boolean isInput) {
        if (isInput) {
            switch (aidlBit) {
                case 1:
                    return 4;
                case 2:
                    return 8;
                case 4:
                    return 262144;
                case 8:
                    return 1048576;
                case 16:
                    return 65536;
                case 32:
                    return 131072;
                case 256:
                    return 32;
                case 262144:
                    return 2097152;
                case 524288:
                    return 4194304;
                default:
                    return 0;
            }
        }
        switch (aidlBit) {
            case 1:
                return 4;
            case 2:
                return 8;
            case 4:
                return 16;
            case 8:
                return 32;
            case 16:
                return 64;
            case 32:
                return 128;
            case 64:
                return 256;
            case 128:
                return 512;
            case 256:
                return 1024;
            case 512:
                return 2048;
            case 1024:
                return 4096;
            case 2048:
                return 8192;
            case 4096:
                return 16384;
            case 8192:
                return 32768;
            case 16384:
                return 65536;
            case 32768:
                return 131072;
            case 65536:
                return 262144;
            case 131072:
                return 524288;
            case 262144:
                return 1048576;
            case 524288:
                return 2097152;
            case 1048576:
                return 4194304;
            case 2097152:
                return 8388608;
            case 4194304:
                return 16777216;
            case 8388608:
                return 33554432;
            case 16777216:
                return 67108864;
            case 33554432:
                return 134217728;
            case 536870912:
                return 268435456;
            case 1073741824:
                return 536870912;
            default:
                return 0;
        }
    }

    private static int aidl2api_AudioChannelLayoutBitMask_AudioFormatChannelMask(int aidlBitMask, boolean isInput) {
        int apiMask = 0;
        for (int bit = Integer.MIN_VALUE; bit != 0; bit >>>= 1) {
            if ((aidlBitMask & bit) == bit) {
                int apiBit = aidl2api_AudioChannelLayoutBit_AudioFormatChannel(bit, isInput);
                if (apiBit != 0) {
                    apiMask |= apiBit;
                    aidlBitMask &= ~bit;
                    if (aidlBitMask == 0) {
                        return apiMask;
                    }
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static int aidl2api_AudioChannelLayout_AudioFormatChannelMask(AudioChannelLayout aidlMask, boolean isInput) {
        switch (aidlMask.getTag()) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return aidlMask.getIndexMask();
            case 3:
                if (isInput) {
                    switch (aidlMask.getLayoutMask()) {
                        case 1:
                            return 16;
                        case 3:
                            return 12;
                        case 63:
                            return AudioFormat.CHANNEL_IN_5POINT1;
                        case 260:
                            return 48;
                        case AudioChannelLayout.LAYOUT_2POINT0POINT2 /* 786435 */:
                            return AudioFormat.CHANNEL_IN_2POINT0POINT2;
                        case AudioChannelLayout.LAYOUT_3POINT0POINT2 /* 786439 */:
                            return AudioFormat.CHANNEL_IN_3POINT0POINT2;
                        case AudioChannelLayout.LAYOUT_2POINT1POINT2 /* 786443 */:
                            return AudioFormat.CHANNEL_IN_2POINT1POINT2;
                        case AudioChannelLayout.LAYOUT_3POINT1POINT2 /* 786447 */:
                            return AudioFormat.CHANNEL_IN_3POINT1POINT2;
                    }
                }
                switch (aidlMask.getLayoutMask()) {
                    case 1:
                        return 4;
                    case 3:
                        return 12;
                    case 7:
                        return 28;
                    case 11:
                        return 44;
                    case 15:
                        return 60;
                    case 51:
                        return 204;
                    case 55:
                        return 220;
                    case 63:
                        return 252;
                    case 259:
                        return 1036;
                    case 260:
                        return RILConstants.RIL_UNSOL_HARDWARE_CONFIG_CHANGED;
                    case 263:
                        return 1052;
                    case 319:
                        return 1276;
                    case 1539:
                        return AudioFormat.CHANNEL_OUT_QUAD_SIDE;
                    case 1551:
                        return AudioFormat.CHANNEL_OUT_5POINT1_SIDE;
                    case 1599:
                        return AudioFormat.CHANNEL_OUT_7POINT1_SURROUND;
                    case AudioChannelLayout.LAYOUT_5POINT1POINT4 /* 184383 */:
                        return AudioFormat.CHANNEL_OUT_5POINT1POINT4;
                    case AudioChannelLayout.LAYOUT_7POINT1POINT4 /* 185919 */:
                        return AudioFormat.CHANNEL_OUT_7POINT1POINT4;
                    case AudioChannelLayout.LAYOUT_2POINT0POINT2 /* 786435 */:
                        return 3145740;
                    case AudioChannelLayout.LAYOUT_3POINT0POINT2 /* 786439 */:
                        return 3145756;
                    case AudioChannelLayout.LAYOUT_2POINT1POINT2 /* 786443 */:
                        return 3145772;
                    case AudioChannelLayout.LAYOUT_3POINT1POINT2 /* 786447 */:
                        return 3145788;
                    case AudioChannelLayout.LAYOUT_5POINT1POINT2 /* 786495 */:
                        return AudioFormat.CHANNEL_OUT_5POINT1POINT2;
                    case AudioChannelLayout.LAYOUT_7POINT1POINT2 /* 788031 */:
                        return AudioFormat.CHANNEL_OUT_7POINT1POINT2;
                    case AudioChannelLayout.LAYOUT_13POINT_360RA /* 7534087 */:
                        return AudioFormat.CHANNEL_OUT_13POINT_360RA;
                    case 16777215:
                        return AudioFormat.CHANNEL_OUT_22POINT2;
                    case AudioChannelLayout.LAYOUT_9POINT1POINT4 /* 50517567 */:
                        return AudioFormat.CHANNEL_OUT_9POINT1POINT4;
                    case AudioChannelLayout.LAYOUT_9POINT1POINT6 /* 51303999 */:
                        return AudioFormat.CHANNEL_OUT_9POINT1POINT6;
                    case AudioChannelLayout.LAYOUT_MONO_HAPTIC_A /* 1073741825 */:
                        return 536870916;
                    case AudioChannelLayout.LAYOUT_STEREO_HAPTIC_A /* 1073741827 */:
                        return 536870924;
                    case 1610612736:
                        return 805306368;
                    case AudioChannelLayout.LAYOUT_MONO_HAPTIC_AB /* 1610612737 */:
                        return 805306372;
                    case AudioChannelLayout.LAYOUT_STEREO_HAPTIC_AB /* 1610612739 */:
                        return 805306380;
                }
                return aidl2api_AudioChannelLayoutBitMask_AudioFormatChannelMask(aidlMask.getLayoutMask(), isInput);
            case 4:
                if (isInput) {
                    switch (aidlMask.getVoiceMask()) {
                        case 16384:
                            return 16400;
                        case 32768:
                            return AudioMixingRule.RULE_EXCLUDE_AUDIO_SESSION_ID;
                        case AudioChannelLayout.VOICE_CALL_MONO /* 49152 */:
                            return 49168;
                    }
                }
                return 0;
            default:
                return 0;
        }
    }

    public static AudioFormat aidl2api_AudioConfig_AudioFormat(AudioConfig aidl, boolean isInput) {
        return aidl2api_AudioConfigBase_AudioFormat(aidl.base, isInput);
    }

    public static AudioFormat aidl2api_AudioConfigBase_AudioFormat(AudioConfigBase aidl, boolean isInput) {
        AudioFormat.Builder apiBuilder = new AudioFormat.Builder();
        apiBuilder.setSampleRate(aidl.sampleRate);
        if (aidl.channelMask.getTag() != 2) {
            apiBuilder.setChannelMask(aidl2api_AudioChannelLayout_AudioFormatChannelMask(aidl.channelMask, isInput));
        } else {
            apiBuilder.setChannelIndexMask(aidl2api_AudioChannelLayout_AudioFormatChannelMask(aidl.channelMask, isInput));
        }
        apiBuilder.setEncoding(aidl2api_AudioFormat_AudioFormatEncoding(aidl.format));
        return apiBuilder.build();
    }

    public static int aidl2api_AudioFormat_AudioFormatEncoding(AudioFormatDescription aidl) {
        switch (aidl.type) {
            case 0:
                if (aidl.encoding == null || aidl.encoding.isEmpty()) {
                    return 1;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AC3.equals(aidl.encoding)) {
                    return 5;
                }
                if (MediaFormat.MIMETYPE_AUDIO_EAC3.equals(aidl.encoding)) {
                    return 6;
                }
                if (MediaFormat.MIMETYPE_AUDIO_DTS.equals(aidl.encoding)) {
                    return 7;
                }
                if (MediaFormat.MIMETYPE_AUDIO_DTS_HD.equals(aidl.encoding)) {
                    return 8;
                }
                if ("audio/mpeg".equals(aidl.encoding)) {
                    return 9;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AAC_LC.equals(aidl.encoding)) {
                    return 10;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AAC_HE_V1.equals(aidl.encoding)) {
                    return 11;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AAC_HE_V2.equals(aidl.encoding)) {
                    return 12;
                }
                if (MediaFormat.MIMETYPE_AUDIO_IEC61937.equals(aidl.encoding) && aidl.pcm == 1) {
                    return 13;
                }
                if (MediaFormat.MIMETYPE_AUDIO_DOLBY_TRUEHD.equals(aidl.encoding)) {
                    return 14;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AAC_ELD.equals(aidl.encoding)) {
                    return 15;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AAC_XHE.equals(aidl.encoding)) {
                    return 16;
                }
                if (MediaFormat.MIMETYPE_AUDIO_AC4.equals(aidl.encoding)) {
                    return 17;
                }
                if (MediaFormat.MIMETYPE_AUDIO_EAC3_JOC.equals(aidl.encoding)) {
                    return 18;
                }
                if (MediaFormat.MIMETYPE_AUDIO_DOLBY_MAT.equals(aidl.encoding) || aidl.encoding.startsWith("audio/vnd.dolby.mat.")) {
                    return 19;
                }
                if (MediaFormat.MIMETYPE_AUDIO_OPUS.equals(aidl.encoding)) {
                    return 20;
                }
                if (MediaFormat.MIMETYPE_AUDIO_MPEGH_BL_L3.equals(aidl.encoding)) {
                    return 23;
                }
                if (MediaFormat.MIMETYPE_AUDIO_MPEGH_BL_L4.equals(aidl.encoding)) {
                    return 24;
                }
                if (MediaFormat.MIMETYPE_AUDIO_MPEGH_LC_L3.equals(aidl.encoding)) {
                    return 25;
                }
                if (MediaFormat.MIMETYPE_AUDIO_MPEGH_LC_L4.equals(aidl.encoding)) {
                    return 26;
                }
                if (MediaFormat.MIMETYPE_AUDIO_DTS_UHD.equals(aidl.encoding)) {
                    return 27;
                }
                return MediaFormat.MIMETYPE_AUDIO_DRA.equals(aidl.encoding) ? 28 : 0;
            case 1:
                switch (aidl.pcm) {
                    case 0:
                        return 3;
                    case 1:
                        return 2;
                    case 2:
                        return 22;
                    case 3:
                    case 4:
                        return 4;
                    case 5:
                        return 21;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    public static AudioPort api2aidl_AudioDeviceAttributes_AudioPort(AudioDeviceAttributes attributes) {
        AudioPort port = new AudioPort();
        port.name = attributes.getName();
        port.profiles = new AudioProfile[0];
        port.extraAudioDescriptors = (ExtraAudioDescriptor[]) ((List) attributes.getAudioDescriptors().stream().map(new Function() { // from class: android.media.audio.common.AidlConversion$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ExtraAudioDescriptor api2aidl_AudioDescriptor_ExtraAudioDescriptor;
                api2aidl_AudioDescriptor_ExtraAudioDescriptor = AidlConversion.api2aidl_AudioDescriptor_ExtraAudioDescriptor((AudioDescriptor) obj);
                return api2aidl_AudioDescriptor_ExtraAudioDescriptor;
            }
        }).collect(Collectors.toList())).toArray(new IntFunction() { // from class: android.media.audio.common.AidlConversion$$ExternalSyntheticLambda1
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return AidlConversion.lambda$api2aidl_AudioDeviceAttributes_AudioPort$1(i);
            }
        });
        port.flags = new AudioIoFlags();
        port.gains = new AudioGain[0];
        AudioPortDeviceExt deviceExt = new AudioPortDeviceExt();
        deviceExt.device = new AudioDevice();
        deviceExt.encodedFormats = new AudioFormatDescription[0];
        deviceExt.device.type = api2aidl_NativeType_AudioDeviceDescription(attributes.getInternalType());
        deviceExt.device.address = AudioDeviceAddress.m147id(attributes.getAddress());
        port.ext = AudioPortExt.device(deviceExt);
        return port;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ExtraAudioDescriptor[] lambda$api2aidl_AudioDeviceAttributes_AudioPort$1(int x$0) {
        return new ExtraAudioDescriptor[x$0];
    }

    public static ExtraAudioDescriptor api2aidl_AudioDescriptor_ExtraAudioDescriptor(AudioDescriptor descriptor) {
        ExtraAudioDescriptor extraDescriptor = new ExtraAudioDescriptor();
        extraDescriptor.standard = api2aidl_AudioDescriptorStandard_AudioStandard(descriptor.getStandard());
        extraDescriptor.audioDescriptor = descriptor.getDescriptor();
        extraDescriptor.encapsulationType = api2aidl_AudioProfileEncapsulationType_AudioEncapsulationType(descriptor.getEncapsulationType());
        return extraDescriptor;
    }

    public static AudioDescriptor aidl2api_ExtraAudioDescriptor_AudioDescriptor(ExtraAudioDescriptor extraDescriptor) {
        AudioDescriptor descriptor = new AudioDescriptor(aidl2api_AudioStandard_AudioDescriptorStandard(extraDescriptor.standard), aidl2api_AudioEncapsulationType_AudioProfileEncapsulationType(extraDescriptor.encapsulationType), extraDescriptor.audioDescriptor);
        return descriptor;
    }

    public static int api2aidl_AudioDescriptorStandard_AudioStandard(int standard) {
        switch (standard) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 0;
        }
    }

    public static int aidl2api_AudioStandard_AudioDescriptorStandard(int standard) {
        switch (standard) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 0;
        }
    }

    public static int api2aidl_AudioProfileEncapsulationType_AudioEncapsulationType(int type) {
        switch (type) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    public static int aidl2api_AudioEncapsulationType_AudioProfileEncapsulationType(int type) {
        switch (type) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    public static AudioDeviceDescription api2aidl_NativeType_AudioDeviceDescription(int nativeType) {
        AudioDeviceDescription aidl = new AudioDeviceDescription();
        aidl.connection = "";
        switch (nativeType) {
            case -2147483644:
                aidl.type = 9;
                break;
            case -2147483640:
                aidl.type = 7;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_SCO;
                break;
            case -2147483632:
                aidl.type = 7;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case -2147483616:
                aidl.type = 4;
                aidl.connection = "hdmi";
                break;
            case -2147483584:
                aidl.type = 12;
                break;
            case -2147483520:
                aidl.type = 10;
                break;
            case AudioSystem.DEVICE_IN_REMOTE_SUBMIX /* -2147483392 */:
                aidl.type = 11;
                break;
            case -2147483136:
                aidl.type = 14;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case -2147482624:
                aidl.type = 14;
                aidl.connection = "usb";
                break;
            case -2147481600:
                aidl.type = 2;
                aidl.connection = "usb";
                break;
            case -2147479552:
                aidl.type = 4;
                aidl.connection = "usb";
                break;
            case -2147475456:
                aidl.type = 6;
                break;
            case -2147467264:
                aidl.type = 13;
                break;
            case -2147450880:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case -2147418112:
                aidl.type = 4;
                aidl.connection = "spdif";
                break;
            case AudioSystem.DEVICE_IN_BLUETOOTH_A2DP /* -2147352576 */:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_A2DP;
                break;
            case -2147221504:
                aidl.type = 8;
                break;
            case AudioSystem.DEVICE_IN_IP /* -2146959360 */:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_IP_V4;
                break;
            case AudioSystem.DEVICE_IN_BUS /* -2146435072 */:
                aidl.type = 4;
                aidl.connection = "bus";
                break;
            case AudioSystem.DEVICE_IN_PROXY /* -2130706432 */:
                aidl.type = 3;
                break;
            case AudioSystem.DEVICE_IN_USB_HEADSET /* -2113929216 */:
                aidl.type = 7;
                aidl.connection = "usb";
                break;
            case AudioSystem.DEVICE_IN_BLUETOOTH_BLE /* -2080374784 */:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_LE;
                break;
            case -2013265920:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_HDMI_ARC;
                break;
            case -2013265919:
                aidl.type = 4;
                aidl.connection = AudioDeviceDescription.CONNECTION_HDMI_EARC;
                break;
            case -1879048192:
                aidl.type = 5;
                break;
            case -1610612736:
                aidl.type = 7;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_LE;
                break;
            case AudioSystem.DEVICE_IN_DEFAULT /* -1073741824 */:
                aidl.type = 1;
                break;
            case 1:
                aidl.type = 141;
                break;
            case 2:
                aidl.type = 140;
                break;
            case 4:
                aidl.type = 137;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case 8:
                aidl.type = 136;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case 16:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_SCO;
                break;
            case 32:
                aidl.type = 137;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_SCO;
                break;
            case 64:
                aidl.type = 132;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_SCO;
                break;
            case 128:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_A2DP;
                break;
            case 256:
                aidl.type = 136;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_A2DP;
                break;
            case 512:
                aidl.type = 140;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_A2DP;
                break;
            case 1024:
                aidl.type = 133;
                aidl.connection = "hdmi";
                break;
            case 2048:
                aidl.type = 145;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case 4096:
                aidl.type = 145;
                aidl.connection = "usb";
                break;
            case 8192:
                aidl.type = 130;
                aidl.connection = "usb";
                break;
            case 16384:
                aidl.type = 133;
                aidl.connection = "usb";
                break;
            case 32768:
                aidl.type = 143;
                break;
            case 65536:
                aidl.type = 144;
                break;
            case 131072:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_ANALOG;
                break;
            case 262144:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_HDMI_ARC;
                break;
            case 262145:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_HDMI_EARC;
                break;
            case 524288:
                aidl.type = 133;
                aidl.connection = "spdif";
                break;
            case 1048576:
                aidl.type = 135;
                break;
            case 2097152:
                aidl.type = 139;
                break;
            case 4194304:
                aidl.type = 142;
                break;
            case 8388608:
                aidl.type = 133;
                aidl.connection = AudioDeviceDescription.CONNECTION_IP_V4;
                break;
            case 16777216:
                aidl.type = 133;
                aidl.connection = "bus";
                break;
            case 33554432:
                aidl.type = 131;
                break;
            case 67108864:
                aidl.type = 137;
                aidl.connection = "usb";
                break;
            case 134217728:
                aidl.type = 138;
                aidl.connection = AudioDeviceDescription.CONNECTION_WIRELESS;
                break;
            case 268435456:
                aidl.type = 134;
                break;
            case 536870912:
                aidl.type = 137;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_LE;
                break;
            case 536870913:
                aidl.type = 140;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_LE;
                break;
            case 536870914:
                aidl.type = 146;
                aidl.connection = AudioDeviceDescription.CONNECTION_BT_LE;
                break;
            case 1073741824:
                aidl.type = 129;
                break;
            default:
                aidl.type = 0;
                break;
        }
        return aidl;
    }
}
