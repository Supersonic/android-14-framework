package android.media.audiopolicy;

import android.annotation.SystemApi;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioSystem;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public class AudioMix {
    private static final int CALLBACK_FLAGS_ALL = 1;
    public static final int CALLBACK_FLAG_NOTIFY_ACTIVITY = 1;
    public static final int MIX_STATE_DISABLED = -1;
    public static final int MIX_STATE_IDLE = 0;
    public static final int MIX_STATE_MIXING = 1;
    public static final int MIX_TYPE_INVALID = -1;
    public static final int MIX_TYPE_PLAYERS = 0;
    public static final int MIX_TYPE_RECORDERS = 1;
    private static final int PRIVILEDGED_CAPTURE_MAX_BYTES_PER_SAMPLE = 2;
    private static final int PRIVILEDGED_CAPTURE_MAX_CHANNEL_NUMBER = 1;
    private static final int PRIVILEDGED_CAPTURE_MAX_SAMPLE_RATE = 16000;
    public static final int ROUTE_FLAG_LOOP_BACK = 2;
    public static final int ROUTE_FLAG_LOOP_BACK_RENDER = 3;
    public static final int ROUTE_FLAG_RENDER = 1;
    private static final int ROUTE_FLAG_SUPPORTED = 3;
    int mCallbackFlags;
    String mDeviceAddress;
    final int mDeviceSystemType;
    private AudioFormat mFormat;
    int mMixState;
    private int mMixType;
    private int mRouteFlags;
    private AudioMixingRule mRule;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RouteFlags {
    }

    private AudioMix(AudioMixingRule rule, AudioFormat format, int routeFlags, int callbackFlags, int deviceType, String deviceAddress) {
        this.mMixType = -1;
        this.mMixState = -1;
        this.mRule = rule;
        this.mFormat = format;
        this.mRouteFlags = routeFlags;
        this.mMixType = rule.getTargetMixType();
        this.mCallbackFlags = callbackFlags;
        this.mDeviceSystemType = deviceType;
        this.mDeviceAddress = deviceAddress == null ? new String("") : deviceAddress;
    }

    public int getMixState() {
        return this.mMixState;
    }

    public int getRouteFlags() {
        return this.mRouteFlags;
    }

    public AudioFormat getFormat() {
        return this.mFormat;
    }

    public AudioMixingRule getRule() {
        return this.mRule;
    }

    public int getMixType() {
        return this.mMixType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRegistration(String regId) {
        this.mDeviceAddress = regId;
    }

    public String getRegistration() {
        return this.mDeviceAddress;
    }

    public boolean isAffectingUsage(int usage) {
        return this.mRule.isAffectingUsage(usage);
    }

    public boolean containsMatchAttributeRuleForUsage(int usage) {
        return this.mRule.containsMatchAttributeRuleForUsage(usage);
    }

    public boolean isRoutedToDevice(int deviceType, String deviceAddress) {
        return (this.mRouteFlags & 1) == 1 && deviceType == this.mDeviceSystemType && deviceAddress.equals(this.mDeviceAddress);
    }

    public static String canBeUsedForPrivilegedMediaCapture(AudioFormat format) {
        int sampleRate = format.getSampleRate();
        if (sampleRate > PRIVILEDGED_CAPTURE_MAX_SAMPLE_RATE || sampleRate <= 0) {
            return "Privileged audio capture sample rate " + sampleRate + " can not be over " + PRIVILEDGED_CAPTURE_MAX_SAMPLE_RATE + "kHz";
        }
        int channelCount = format.getChannelCount();
        if (channelCount > 1 || channelCount <= 0) {
            return "Privileged audio capture channel count " + channelCount + " can not be over 1";
        }
        int encoding = format.getEncoding();
        if (!AudioFormat.isPublicEncoding(encoding) || !AudioFormat.isEncodingLinearPcm(encoding)) {
            return "Privileged audio capture encoding " + encoding + "is not linear";
        }
        if (AudioFormat.getBytesPerSample(encoding) > 2) {
            return "Privileged audio capture encoding " + encoding + " can not be over 2 bytes per sample";
        }
        return null;
    }

    public boolean isForCallRedirection() {
        return this.mRule.isForCallRedirection();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioMix that = (AudioMix) o;
        if (this.mRouteFlags == that.mRouteFlags && this.mMixType == that.mMixType && Objects.equals(this.mRule, that.mRule) && Objects.equals(this.mFormat, that.mFormat)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRouteFlags), this.mRule, Integer.valueOf(this.mMixType), this.mFormat);
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private int mCallbackFlags;
        private String mDeviceAddress;
        private int mDeviceSystemType;
        private AudioFormat mFormat;
        private int mRouteFlags;
        private AudioMixingRule mRule;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder() {
            this.mRule = null;
            this.mFormat = null;
            this.mRouteFlags = 0;
            this.mCallbackFlags = 0;
            this.mDeviceSystemType = 0;
            this.mDeviceAddress = null;
        }

        public Builder(AudioMixingRule rule) throws IllegalArgumentException {
            this.mRule = null;
            this.mFormat = null;
            this.mRouteFlags = 0;
            this.mCallbackFlags = 0;
            this.mDeviceSystemType = 0;
            this.mDeviceAddress = null;
            if (rule == null) {
                throw new IllegalArgumentException("Illegal null AudioMixingRule argument");
            }
            this.mRule = rule;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setMixingRule(AudioMixingRule rule) throws IllegalArgumentException {
            if (rule == null) {
                throw new IllegalArgumentException("Illegal null AudioMixingRule argument");
            }
            this.mRule = rule;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setCallbackFlags(int flags) throws IllegalArgumentException {
            if (flags != 0 && (flags & 1) == 0) {
                throw new IllegalArgumentException("Illegal callback flags 0x" + Integer.toHexString(flags).toUpperCase());
            }
            this.mCallbackFlags = flags;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setDevice(int deviceType, String address) {
            this.mDeviceSystemType = deviceType;
            this.mDeviceAddress = address;
            return this;
        }

        public Builder setFormat(AudioFormat format) throws IllegalArgumentException {
            if (format == null) {
                throw new IllegalArgumentException("Illegal null AudioFormat argument");
            }
            this.mFormat = format;
            return this;
        }

        public Builder setRouteFlags(int routeFlags) throws IllegalArgumentException {
            if (routeFlags == 0) {
                throw new IllegalArgumentException("Illegal empty route flags");
            }
            if ((routeFlags & 3) == 0) {
                throw new IllegalArgumentException("Invalid route flags 0x" + Integer.toHexString(routeFlags) + "when configuring an AudioMix");
            }
            if ((routeFlags & (-4)) != 0) {
                throw new IllegalArgumentException("Unknown route flags 0x" + Integer.toHexString(routeFlags) + "when configuring an AudioMix");
            }
            this.mRouteFlags = routeFlags;
            return this;
        }

        public Builder setDevice(AudioDeviceInfo device) throws IllegalArgumentException {
            if (device == null) {
                throw new IllegalArgumentException("Illegal null AudioDeviceInfo argument");
            }
            if (!device.isSink()) {
                throw new IllegalArgumentException("Unsupported device type on mix, not a sink");
            }
            this.mDeviceSystemType = AudioDeviceInfo.convertDeviceTypeToInternalDevice(device.getType());
            this.mDeviceAddress = device.getAddress();
            return this;
        }

        public AudioMix build() throws IllegalArgumentException {
            String error;
            if (this.mRule == null) {
                throw new IllegalArgumentException("Illegal null AudioMixingRule");
            }
            if (this.mRouteFlags == 0) {
                this.mRouteFlags = 2;
            }
            AudioFormat audioFormat = this.mFormat;
            if (audioFormat == null) {
                int rate = AudioSystem.getPrimaryOutputSamplingRate();
                if (rate <= 0) {
                    rate = 44100;
                }
                this.mFormat = new AudioFormat.Builder().setSampleRate(rate).build();
            } else if ((audioFormat.getPropertySetMask() & 4) != 0 && this.mFormat.getChannelCount() == 1 && this.mFormat.getChannelMask() == 16) {
                this.mFormat = new AudioFormat.Builder(this.mFormat).setChannelMask(4).build();
            }
            int i = this.mDeviceSystemType;
            if (i != 0 && i != 32768 && i != -2147483392) {
                if ((this.mRouteFlags & 1) == 0) {
                    throw new IllegalArgumentException("Can't have audio device without flag ROUTE_FLAG_RENDER");
                }
                if (this.mRule.getTargetMixType() != 0) {
                    throw new IllegalArgumentException("Unsupported device on non-playback mix");
                }
            } else {
                int i2 = this.mRouteFlags;
                if ((i2 & 3) == 1) {
                    throw new IllegalArgumentException("Can't have flag ROUTE_FLAG_RENDER without an audio device");
                }
                if ((i2 & 2) == 2) {
                    if (this.mRule.getTargetMixType() == 0) {
                        this.mDeviceSystemType = 32768;
                    } else if (this.mRule.getTargetMixType() == 1) {
                        this.mDeviceSystemType = AudioSystem.DEVICE_IN_REMOTE_SUBMIX;
                    } else {
                        throw new IllegalArgumentException("Unknown mixing rule type");
                    }
                }
            }
            if (this.mRule.allowPrivilegedMediaPlaybackCapture() && (error = AudioMix.canBeUsedForPrivilegedMediaCapture(this.mFormat)) != null) {
                throw new IllegalArgumentException(error);
            }
            return new AudioMix(this.mRule, this.mFormat, this.mRouteFlags, this.mCallbackFlags, this.mDeviceSystemType, this.mDeviceAddress);
        }
    }
}
