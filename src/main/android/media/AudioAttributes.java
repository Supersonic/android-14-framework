package android.media;

import android.annotation.SystemApi;
import android.audio.policy.configuration.V7_0.AudioUsage;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class AudioAttributes implements Parcelable {
    public static final int ALLOW_CAPTURE_BY_ALL = 1;
    public static final int ALLOW_CAPTURE_BY_NONE = 3;
    public static final int ALLOW_CAPTURE_BY_SYSTEM = 2;
    private static final int ALL_PARCEL_FLAGS = 1;
    private static final int ATTR_PARCEL_IS_NULL_BUNDLE = -1977;
    private static final int ATTR_PARCEL_IS_VALID_BUNDLE = 1980;
    public static final int CONTENT_TYPE_MOVIE = 3;
    public static final int CONTENT_TYPE_MUSIC = 2;
    public static final int CONTENT_TYPE_SONIFICATION = 4;
    public static final int CONTENT_TYPE_SPEECH = 1;
    @SystemApi
    public static final int CONTENT_TYPE_ULTRASOUND = 1997;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final Parcelable.Creator<AudioAttributes> CREATOR;
    private static final int FLAG_ALL = 129023;
    private static final int FLAG_ALL_API_SET = 465;
    private static final int FLAG_ALL_PUBLIC = 273;
    public static final int FLAG_AUDIBILITY_ENFORCED = 1;
    @SystemApi
    public static final int FLAG_BEACON = 8;
    @SystemApi
    public static final int FLAG_BYPASS_INTERRUPTION_POLICY = 64;
    @SystemApi
    public static final int FLAG_BYPASS_MUTE = 128;
    public static final int FLAG_CALL_REDIRECTION = 65536;
    public static final int FLAG_CAPTURE_PRIVATE = 8192;
    public static final int FLAG_CONTENT_SPATIALIZED = 16384;
    public static final int FLAG_DEEP_BUFFER = 512;
    public static final int FLAG_HW_AV_SYNC = 16;
    @SystemApi
    public static final int FLAG_HW_HOTWORD = 32;
    public static final int FLAG_LOW_LATENCY = 256;
    public static final int FLAG_MUTE_HAPTIC = 2048;
    public static final int FLAG_NEVER_SPATIALIZE = 32768;
    public static final int FLAG_NO_MEDIA_PROJECTION = 1024;
    public static final int FLAG_NO_SYSTEM_CAPTURE = 4096;
    public static final int FLAG_SCO = 4;
    public static final int FLAG_SECURE = 2;
    public static final int FLATTEN_TAGS = 1;
    public static final int[] SDK_USAGES;
    public static final int SPATIALIZATION_BEHAVIOR_AUTO = 0;
    public static final int SPATIALIZATION_BEHAVIOR_NEVER = 1;
    public static final int SUPPRESSIBLE_ALARM = 4;
    public static final int SUPPRESSIBLE_CALL = 2;
    public static final int SUPPRESSIBLE_MEDIA = 5;
    public static final int SUPPRESSIBLE_NEVER = 3;
    public static final int SUPPRESSIBLE_NOTIFICATION = 1;
    public static final int SUPPRESSIBLE_SYSTEM = 6;
    public static final SparseIntArray SUPPRESSIBLE_USAGES;
    private static final int SYSTEM_USAGE_OFFSET = 1000;
    private static final String TAG = "AudioAttributes";
    public static final int USAGE_ALARM = 4;
    @SystemApi
    public static final int USAGE_ANNOUNCEMENT = 1003;
    public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
    public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
    public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
    public static final int USAGE_ASSISTANT = 16;
    @SystemApi
    public static final int USAGE_CALL_ASSISTANT = 17;
    @SystemApi
    public static final int USAGE_EMERGENCY = 1000;
    public static final int USAGE_GAME = 14;
    private static final int USAGE_INVALID = -1;
    public static final int USAGE_MEDIA = 1;
    public static final int USAGE_NOTIFICATION = 5;
    @Deprecated
    public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
    @Deprecated
    public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
    @Deprecated
    public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
    public static final int USAGE_NOTIFICATION_EVENT = 10;
    public static final int USAGE_NOTIFICATION_RINGTONE = 6;
    @SystemApi
    public static final int USAGE_SAFETY = 1001;
    public static final int USAGE_UNKNOWN = 0;
    @SystemApi
    public static final int USAGE_VEHICLE_STATUS = 1002;
    public static final int USAGE_VIRTUAL_SOURCE = 15;
    public static final int USAGE_VOICE_COMMUNICATION = 2;
    public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
    private static final Map<String, Integer> sXsdStringToUsage;
    private Bundle mBundle;
    private int mContentType;
    private int mFlags;
    private String mFormattedTags;
    private int mSource;
    private HashSet<String> mTags;
    private int mUsage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AttrInternalContentType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AttributeContentType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AttributeSdkUsage {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AttributeSystemUsage {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AttributeUsage {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface CapturePolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SpatializationBehavior {
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SUPPRESSIBLE_USAGES = sparseIntArray;
        sparseIntArray.put(5, 1);
        sparseIntArray.put(6, 2);
        sparseIntArray.put(7, 2);
        sparseIntArray.put(8, 1);
        sparseIntArray.put(9, 1);
        sparseIntArray.put(10, 1);
        sparseIntArray.put(11, 3);
        sparseIntArray.put(2, 3);
        sparseIntArray.put(3, 3);
        sparseIntArray.put(4, 4);
        sparseIntArray.put(1, 5);
        sparseIntArray.put(12, 5);
        sparseIntArray.put(14, 5);
        sparseIntArray.put(16, 5);
        sparseIntArray.put(17, 3);
        sparseIntArray.put(0, 5);
        sparseIntArray.put(13, 6);
        SDK_USAGES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16};
        CREATOR = new Parcelable.Creator<AudioAttributes>() { // from class: android.media.AudioAttributes.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AudioAttributes createFromParcel(Parcel p) {
                return new AudioAttributes(p);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AudioAttributes[] newArray(int size) {
                return new AudioAttributes[size];
            }
        };
        HashMap hashMap = new HashMap();
        sXsdStringToUsage = hashMap;
        hashMap.put(AudioUsage.AUDIO_USAGE_UNKNOWN.toString(), 0);
        hashMap.put(AudioUsage.AUDIO_USAGE_UNKNOWN.toString(), 0);
        hashMap.put(AudioUsage.AUDIO_USAGE_MEDIA.toString(), 1);
        hashMap.put(AudioUsage.AUDIO_USAGE_VOICE_COMMUNICATION.toString(), 2);
        hashMap.put(AudioUsage.AUDIO_USAGE_VOICE_COMMUNICATION_SIGNALLING.toString(), 3);
        hashMap.put(AudioUsage.AUDIO_USAGE_ALARM.toString(), 4);
        hashMap.put(AudioUsage.AUDIO_USAGE_NOTIFICATION.toString(), 5);
        hashMap.put(AudioUsage.AUDIO_USAGE_NOTIFICATION_TELEPHONY_RINGTONE.toString(), 6);
        hashMap.put(AudioUsage.AUDIO_USAGE_ASSISTANCE_ACCESSIBILITY.toString(), 11);
        hashMap.put(AudioUsage.AUDIO_USAGE_ASSISTANCE_NAVIGATION_GUIDANCE.toString(), 12);
        hashMap.put(AudioUsage.AUDIO_USAGE_ASSISTANCE_SONIFICATION.toString(), 13);
        hashMap.put(AudioUsage.AUDIO_USAGE_GAME.toString(), 14);
        hashMap.put(AudioUsage.AUDIO_USAGE_VIRTUAL_SOURCE.toString(), 15);
        hashMap.put(AudioUsage.AUDIO_USAGE_ASSISTANT.toString(), 16);
        hashMap.put(AudioUsage.AUDIO_USAGE_CALL_ASSISTANT.toString(), 17);
        hashMap.put(AudioUsage.AUDIO_USAGE_EMERGENCY.toString(), 1000);
        hashMap.put(AudioUsage.AUDIO_USAGE_SAFETY.toString(), 1001);
        hashMap.put(AudioUsage.AUDIO_USAGE_VEHICLE_STATUS.toString(), 1002);
        hashMap.put(AudioUsage.AUDIO_USAGE_ANNOUNCEMENT.toString(), 1003);
    }

    public static int[] getSdkUsages() {
        return SDK_USAGES;
    }

    private AudioAttributes() {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mSource = -1;
        this.mFlags = 0;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public int getUsage() {
        if (isSystemUsage(this.mUsage)) {
            return 0;
        }
        return this.mUsage;
    }

    @SystemApi
    public int getSystemUsage() {
        return this.mUsage;
    }

    @SystemApi
    public int getCapturePreset() {
        return this.mSource;
    }

    public int getFlags() {
        return this.mFlags & 273;
    }

    @SystemApi
    public int getAllFlags() {
        return this.mFlags & FLAG_ALL;
    }

    @SystemApi
    public Bundle getBundle() {
        Bundle bundle = this.mBundle;
        if (bundle == null) {
            return bundle;
        }
        return new Bundle(this.mBundle);
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.mTags);
    }

    public boolean areHapticChannelsMuted() {
        return (this.mFlags & 2048) != 0;
    }

    public boolean isContentSpatialized() {
        return (this.mFlags & 16384) != 0;
    }

    public int getSpatializationBehavior() {
        return (this.mFlags & 32768) != 0 ? 1 : 0;
    }

    public int getAllowedCapturePolicy() {
        int i = this.mFlags;
        if ((i & 4096) == 4096) {
            return 3;
        }
        if ((i & 1024) == 1024) {
            return 2;
        }
        return 1;
    }

    public boolean isForCallRedirection() {
        return (this.mFlags & 65536) == 65536;
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private static final int PRIVACY_SENSITIVE_DEFAULT = -1;
        private static final int PRIVACY_SENSITIVE_DISABLED = 0;
        private static final int PRIVACY_SENSITIVE_ENABLED = 1;
        private Bundle mBundle;
        private int mContentType;
        private int mFlags;
        private boolean mIsContentSpatialized;
        private boolean mMuteHapticChannels;
        private int mPrivacySensitive;
        private int mSource;
        private int mSpatializationBehavior;
        private int mSystemUsage;
        private HashSet<String> mTags;
        private int mUsage;

        public Builder() {
            this.mUsage = -1;
            this.mSystemUsage = -1;
            this.mContentType = 0;
            this.mSource = -1;
            this.mFlags = 0;
            this.mMuteHapticChannels = true;
            this.mIsContentSpatialized = false;
            this.mSpatializationBehavior = 0;
            this.mTags = new HashSet<>();
            this.mPrivacySensitive = -1;
        }

        public Builder(AudioAttributes aa) {
            this.mUsage = -1;
            this.mSystemUsage = -1;
            this.mContentType = 0;
            this.mSource = -1;
            this.mFlags = 0;
            this.mMuteHapticChannels = true;
            this.mIsContentSpatialized = false;
            this.mSpatializationBehavior = 0;
            this.mTags = new HashSet<>();
            this.mPrivacySensitive = -1;
            this.mUsage = aa.mUsage;
            this.mContentType = aa.mContentType;
            this.mSource = aa.mSource;
            this.mFlags = aa.getAllFlags();
            this.mTags = (HashSet) aa.mTags.clone();
            this.mMuteHapticChannels = aa.areHapticChannelsMuted();
            this.mIsContentSpatialized = aa.isContentSpatialized();
            this.mSpatializationBehavior = aa.getSpatializationBehavior();
            if ((this.mFlags & 8192) != 0) {
                this.mPrivacySensitive = 1;
            }
        }

        public AudioAttributes build() {
            AudioAttributes aa = new AudioAttributes();
            aa.mContentType = this.mContentType;
            int i = this.mUsage;
            if (i == -1) {
                int i2 = this.mSystemUsage;
                if (i2 == -1) {
                    aa.mUsage = 0;
                } else {
                    aa.mUsage = i2;
                }
            } else if (this.mSystemUsage == -1) {
                aa.mUsage = i;
            } else {
                throw new IllegalArgumentException("Cannot set both usage and system usage on same builder");
            }
            switch (aa.mUsage) {
                case 7:
                case 8:
                case 9:
                    aa.mUsage = 5;
                    break;
            }
            aa.mSource = this.mSource;
            aa.mFlags = this.mFlags;
            if (this.mMuteHapticChannels) {
                aa.mFlags |= 2048;
            }
            if (this.mIsContentSpatialized) {
                aa.mFlags |= 16384;
            }
            if (this.mSpatializationBehavior == 1) {
                aa.mFlags |= 32768;
            }
            int i3 = this.mPrivacySensitive;
            if (i3 == -1) {
                int i4 = this.mSource;
                if (i4 == 7 || i4 == 5) {
                    aa.mFlags |= 8192;
                } else {
                    aa.mFlags &= -8193;
                }
            } else if (i3 == 1) {
                aa.mFlags |= 8192;
            } else {
                aa.mFlags &= -8193;
            }
            aa.mTags = (HashSet) this.mTags.clone();
            aa.mFormattedTags = TextUtils.join(NavigationBarInflaterView.GRAVITY_SEPARATOR, this.mTags);
            if (this.mBundle != null) {
                aa.mBundle = new Bundle(this.mBundle);
            }
            if (this.mSource != 6 && (this.mFlags & 32) == 32) {
                aa.mFlags &= -33;
            }
            return aa;
        }

        public Builder setUsage(int usage) {
            switch (usage) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                    this.mUsage = usage;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid usage " + usage);
            }
        }

        @SystemApi
        public Builder setSystemUsage(int systemUsage) {
            if (AudioAttributes.isSystemUsage(systemUsage)) {
                this.mSystemUsage = systemUsage;
                return this;
            }
            throw new IllegalArgumentException("Invalid system usage " + systemUsage);
        }

        public Builder setContentType(int contentType) {
            switch (contentType) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    this.mContentType = contentType;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid content type " + contentType);
            }
        }

        @SystemApi
        public Builder setInternalContentType(int contentType) {
            switch (contentType) {
                case 1997:
                    this.mContentType = contentType;
                    break;
                default:
                    setContentType(contentType);
                    break;
            }
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags |= flags & 465;
            return this;
        }

        @SystemApi
        public Builder setHotwordModeEnabled(boolean enable) {
            if (enable) {
                this.mFlags |= 32;
            } else {
                this.mFlags &= -33;
            }
            return this;
        }

        public Builder setAllowedCapturePolicy(int capturePolicy) {
            this.mFlags = AudioAttributes.capturePolicyToFlags(capturePolicy, this.mFlags);
            return this;
        }

        public Builder setIsContentSpatialized(boolean isSpatialized) {
            this.mIsContentSpatialized = isSpatialized;
            return this;
        }

        public Builder setSpatializationBehavior(int sb) {
            switch (sb) {
                case 0:
                case 1:
                    this.mSpatializationBehavior = sb;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid spatialization behavior " + sb);
            }
        }

        public Builder replaceFlags(int flags) {
            this.mFlags = AudioAttributes.FLAG_ALL & flags;
            return this;
        }

        @SystemApi
        public Builder addBundle(Bundle bundle) {
            if (bundle == null) {
                throw new IllegalArgumentException("Illegal null bundle");
            }
            Bundle bundle2 = this.mBundle;
            if (bundle2 == null) {
                this.mBundle = new Bundle(bundle);
            } else {
                bundle2.putAll(bundle);
            }
            return this;
        }

        public Builder addTag(String tag) {
            this.mTags.add(tag);
            return this;
        }

        public Builder replaceTags(HashSet<String> tags) {
            this.mTags = (HashSet) tags.clone();
            return this;
        }

        public Builder setLegacyStreamType(int streamType) {
            if (streamType == 10) {
                throw new IllegalArgumentException("STREAM_ACCESSIBILITY is not a legacy stream type that was used for audio playback");
            }
            setInternalLegacyStreamType(streamType);
            return this;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public Builder setInternalLegacyStreamType(int streamType) {
            AudioAttributes attributes;
            this.mContentType = 0;
            this.mUsage = 0;
            if (android.media.audiopolicy.AudioProductStrategy.getAudioProductStrategies().size() > 0 && (attributes = android.media.audiopolicy.AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(streamType)) != null) {
                this.mUsage = attributes.mUsage;
                this.mContentType = attributes.mContentType;
                this.mFlags = attributes.getAllFlags();
                this.mMuteHapticChannels = attributes.areHapticChannelsMuted();
                this.mIsContentSpatialized = attributes.isContentSpatialized();
                this.mSpatializationBehavior = attributes.getSpatializationBehavior();
                this.mTags = attributes.mTags;
                this.mBundle = attributes.mBundle;
                this.mSource = attributes.mSource;
            }
            if (this.mContentType == 0) {
                switch (streamType) {
                    case 0:
                        this.mContentType = 1;
                        break;
                    case 1:
                        this.mContentType = 4;
                        break;
                    case 2:
                        this.mContentType = 4;
                        break;
                    case 3:
                        this.mContentType = 2;
                        break;
                    case 4:
                        this.mContentType = 4;
                        break;
                    case 5:
                        this.mContentType = 4;
                        break;
                    case 6:
                        this.mContentType = 1;
                        this.mFlags |= 4;
                        break;
                    case 7:
                        this.mFlags = 1 | this.mFlags;
                        this.mContentType = 4;
                        break;
                    case 8:
                        this.mContentType = 4;
                        break;
                    case 9:
                        this.mContentType = 4;
                        this.mFlags |= 8;
                        break;
                    case 10:
                        this.mContentType = 1;
                        break;
                    case 11:
                        this.mContentType = 1;
                        break;
                    default:
                        Log.m110e(AudioAttributes.TAG, "Invalid stream type " + streamType + " for AudioAttributes");
                        break;
                }
            }
            if (this.mUsage == 0) {
                this.mUsage = AudioAttributes.usageForStreamType(streamType);
            }
            return this;
        }

        @SystemApi
        public Builder setCapturePreset(int preset) {
            switch (preset) {
                case 0:
                case 1:
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                    this.mSource = preset;
                    break;
                case 2:
                case 3:
                case 4:
                case 8:
                default:
                    Log.m110e(AudioAttributes.TAG, "Invalid capture preset " + preset + " for AudioAttributes");
                    break;
            }
            return this;
        }

        @SystemApi
        public Builder setInternalCapturePreset(int preset) {
            if (preset == 1999 || preset == 8 || preset == 1998 || preset == 3 || preset == 2 || preset == 4 || preset == 1997 || preset == 2000 || preset == -1) {
                this.mSource = preset;
            } else {
                setCapturePreset(preset);
            }
            return this;
        }

        public Builder setHapticChannelsMuted(boolean muted) {
            this.mMuteHapticChannels = muted;
            return this;
        }

        public Builder setPrivacySensitive(boolean privacySensitive) {
            this.mPrivacySensitive = privacySensitive ? 1 : 0;
            return this;
        }

        public Builder setForCallRedirection() {
            this.mFlags |= 65536;
            return this;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUsage);
        dest.writeInt(this.mContentType);
        dest.writeInt(this.mSource);
        dest.writeInt(this.mFlags);
        dest.writeInt(flags & 1);
        if ((flags & 1) == 0) {
            String[] tagsArray = new String[this.mTags.size()];
            this.mTags.toArray(tagsArray);
            dest.writeStringArray(tagsArray);
        } else if ((flags & 1) == 1) {
            dest.writeString(this.mFormattedTags);
        }
        if (this.mBundle == null) {
            dest.writeInt(ATTR_PARCEL_IS_NULL_BUNDLE);
            return;
        }
        dest.writeInt(1980);
        dest.writeBundle(this.mBundle);
    }

    private AudioAttributes(Parcel in) {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mSource = -1;
        this.mFlags = 0;
        this.mUsage = in.readInt();
        this.mContentType = in.readInt();
        this.mSource = in.readInt();
        this.mFlags = in.readInt();
        boolean hasFlattenedTags = (in.readInt() & 1) == 1;
        this.mTags = new HashSet<>();
        if (hasFlattenedTags) {
            String str = new String(in.readString());
            this.mFormattedTags = str;
            this.mTags.add(str);
        } else {
            String[] tagsArray = in.readStringArray();
            for (int i = tagsArray.length - 1; i >= 0; i--) {
                this.mTags.add(tagsArray[i]);
            }
            this.mFormattedTags = TextUtils.join(NavigationBarInflaterView.GRAVITY_SEPARATOR, this.mTags);
        }
        switch (in.readInt()) {
            case ATTR_PARCEL_IS_NULL_BUNDLE /* -1977 */:
                this.mBundle = null;
                return;
            case 1980:
                this.mBundle = new Bundle(in.readBundle());
                return;
            default:
                Log.m110e(TAG, "Illegal value unmarshalling AudioAttributes, can't initialize bundle");
                return;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioAttributes that = (AudioAttributes) o;
        if (this.mContentType == that.mContentType && this.mFlags == that.mFlags && this.mSource == that.mSource && this.mUsage == that.mUsage && this.mFormattedTags.equals(that.mFormattedTags)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mContentType), Integer.valueOf(this.mFlags), Integer.valueOf(this.mSource), Integer.valueOf(this.mUsage), this.mFormattedTags, this.mBundle);
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("AudioAttributes: usage=").append(usageToString()).append(" content=").append(contentTypeToString()).append(this.mSource != -1 ? " source=" + MediaRecorder.toLogFriendlyAudioSource(this.mSource) : "").append(" flags=0x").append(Integer.toHexString(this.mFlags).toUpperCase()).append(" tags=").append(this.mFormattedTags).append(" bundle=");
        Bundle bundle = this.mBundle;
        return new String(append.append(bundle == null ? "null" : bundle.toString()).toString());
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1159641169921L, this.mUsage);
        proto.write(1159641169922L, this.mContentType);
        proto.write(1120986464259L, this.mFlags);
        for (String t : this.mFormattedTags.split(NavigationBarInflaterView.GRAVITY_SEPARATOR)) {
            String t2 = t.trim();
            if (t2 != "") {
                proto.write(2237677961220L, t2);
            }
        }
        proto.end(token);
    }

    public String usageToString() {
        return usageToString(this.mUsage);
    }

    public static String usageToString(int usage) {
        switch (usage) {
            case 0:
                return "USAGE_UNKNOWN";
            case 1:
                return "USAGE_MEDIA";
            case 2:
                return "USAGE_VOICE_COMMUNICATION";
            case 3:
                return "USAGE_VOICE_COMMUNICATION_SIGNALLING";
            case 4:
                return "USAGE_ALARM";
            case 5:
                return "USAGE_NOTIFICATION";
            case 6:
                return "USAGE_NOTIFICATION_RINGTONE";
            case 7:
                return "USAGE_NOTIFICATION_COMMUNICATION_REQUEST";
            case 8:
                return "USAGE_NOTIFICATION_COMMUNICATION_INSTANT";
            case 9:
                return "USAGE_NOTIFICATION_COMMUNICATION_DELAYED";
            case 10:
                return "USAGE_NOTIFICATION_EVENT";
            case 11:
                return "USAGE_ASSISTANCE_ACCESSIBILITY";
            case 12:
                return "USAGE_ASSISTANCE_NAVIGATION_GUIDANCE";
            case 13:
                return "USAGE_ASSISTANCE_SONIFICATION";
            case 14:
                return "USAGE_GAME";
            case 16:
                return "USAGE_ASSISTANT";
            case 17:
                return "USAGE_CALL_ASSISTANT";
            case 1000:
                return "USAGE_EMERGENCY";
            case 1001:
                return "USAGE_SAFETY";
            case 1002:
                return "USAGE_VEHICLE_STATUS";
            case 1003:
                return "USAGE_ANNOUNCEMENT";
            default:
                return "unknown usage " + usage;
        }
    }

    public static String usageToXsdString(int usage) {
        switch (usage) {
            case 0:
                return AudioUsage.AUDIO_USAGE_UNKNOWN.toString();
            case 1:
                return AudioUsage.AUDIO_USAGE_MEDIA.toString();
            case 2:
                return AudioUsage.AUDIO_USAGE_VOICE_COMMUNICATION.toString();
            case 3:
                return AudioUsage.AUDIO_USAGE_VOICE_COMMUNICATION_SIGNALLING.toString();
            case 4:
                return AudioUsage.AUDIO_USAGE_ALARM.toString();
            case 5:
                return AudioUsage.AUDIO_USAGE_NOTIFICATION.toString();
            case 6:
                return AudioUsage.AUDIO_USAGE_NOTIFICATION_TELEPHONY_RINGTONE.toString();
            case 11:
                return AudioUsage.AUDIO_USAGE_ASSISTANCE_ACCESSIBILITY.toString();
            case 12:
                return AudioUsage.AUDIO_USAGE_ASSISTANCE_NAVIGATION_GUIDANCE.toString();
            case 13:
                return AudioUsage.AUDIO_USAGE_ASSISTANCE_SONIFICATION.toString();
            case 14:
                return AudioUsage.AUDIO_USAGE_GAME.toString();
            case 15:
                return AudioUsage.AUDIO_USAGE_VIRTUAL_SOURCE.toString();
            case 16:
                return AudioUsage.AUDIO_USAGE_ASSISTANT.toString();
            case 17:
                return AudioUsage.AUDIO_USAGE_CALL_ASSISTANT.toString();
            case 1000:
                return AudioUsage.AUDIO_USAGE_EMERGENCY.toString();
            case 1001:
                return AudioUsage.AUDIO_USAGE_SAFETY.toString();
            case 1002:
                return AudioUsage.AUDIO_USAGE_VEHICLE_STATUS.toString();
            case 1003:
                return AudioUsage.AUDIO_USAGE_ANNOUNCEMENT.toString();
            default:
                Log.m104w(TAG, "Unknown usage value " + usage);
                return AudioUsage.AUDIO_USAGE_UNKNOWN.toString();
        }
    }

    public static int xsdStringToUsage(String xsdUsage) {
        Map<String, Integer> map = sXsdStringToUsage;
        if (map.containsKey(xsdUsage)) {
            return map.get(xsdUsage).intValue();
        }
        Log.m104w(TAG, "Usage name not found in AudioUsage enum: " + xsdUsage);
        return 0;
    }

    public String contentTypeToString() {
        switch (this.mContentType) {
            case 0:
                return new String("CONTENT_TYPE_UNKNOWN");
            case 1:
                return new String("CONTENT_TYPE_SPEECH");
            case 2:
                return new String("CONTENT_TYPE_MUSIC");
            case 3:
                return new String("CONTENT_TYPE_MOVIE");
            case 4:
                return new String("CONTENT_TYPE_SONIFICATION");
            case 1997:
                return new String("CONTENT_TYPE_ULTRASOUND");
            default:
                return new String("unknown content type " + this.mContentType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int usageForStreamType(int streamType) {
        switch (streamType) {
            case 0:
                return 2;
            case 1:
            case 7:
                return 13;
            case 2:
                return 6;
            case 3:
                return 1;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 2;
            case 8:
                return 3;
            case 9:
            default:
                return 0;
            case 10:
                return 11;
            case 11:
                return 16;
        }
    }

    @SystemApi
    public static boolean isSystemUsage(int usage) {
        return usage == 17 || usage == 1000 || usage == 1001 || usage == 1002 || usage == 1003;
    }

    public int getVolumeControlStream() {
        return toVolumeStreamType(true, this);
    }

    public static int toLegacyStreamType(AudioAttributes aa) {
        return toVolumeStreamType(false, aa);
    }

    private static int toVolumeStreamType(boolean fromGetVolumeControlStream, AudioAttributes aa) {
        if ((aa.getFlags() & 1) == 1) {
            return fromGetVolumeControlStream ? 1 : 7;
        } else if ((aa.getAllFlags() & 4) == 4) {
            return fromGetVolumeControlStream ? 0 : 6;
        } else if ((aa.getAllFlags() & 8) == 8) {
            return fromGetVolumeControlStream ? 3 : 9;
        } else if (android.media.audiopolicy.AudioProductStrategy.getAudioProductStrategies().size() > 0) {
            return android.media.audiopolicy.AudioProductStrategy.getLegacyStreamTypeForStrategyWithAudioAttributes(aa);
        } else {
            switch (aa.getUsage()) {
                case 0:
                case 1000:
                case 1001:
                case 1002:
                case 1003:
                    return 3;
                case 1:
                case 12:
                case 14:
                case 16:
                    return 3;
                case 2:
                case 17:
                    return 0;
                case 3:
                    return fromGetVolumeControlStream ? 0 : 8;
                case 4:
                    return 4;
                case 5:
                case 7:
                case 8:
                case 9:
                case 10:
                    return 5;
                case 6:
                    return 2;
                case 11:
                    return 10;
                case 13:
                    return 1;
                default:
                    if (fromGetVolumeControlStream) {
                        throw new IllegalArgumentException("Unknown usage value " + aa.getUsage() + " in audio attributes");
                    }
                    return 3;
            }
        }
    }

    public static int capturePolicyToFlags(int capturePolicy, int flags) {
        switch (capturePolicy) {
            case 1:
                return flags & (-5121);
            case 2:
                return (flags | 1024) & (-4097);
            case 3:
                return flags | 5120;
            default:
                throw new IllegalArgumentException("Unknown allow playback capture policy");
        }
    }
}
