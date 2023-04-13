package android.media;

import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class MediaRoute2Info implements Parcelable {
    public static final int CONNECTION_STATE_CONNECTED = 2;
    public static final int CONNECTION_STATE_CONNECTING = 1;
    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final Parcelable.Creator<MediaRoute2Info> CREATOR = new Parcelable.Creator<MediaRoute2Info>() { // from class: android.media.MediaRoute2Info.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRoute2Info createFromParcel(Parcel in) {
            return new MediaRoute2Info(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaRoute2Info[] newArray(int size) {
            return new MediaRoute2Info[size];
        }
    };
    public static final String FEATURE_LIVE_AUDIO = "android.media.route.feature.LIVE_AUDIO";
    public static final String FEATURE_LIVE_VIDEO = "android.media.route.feature.LIVE_VIDEO";
    public static final String FEATURE_LOCAL_PLAYBACK = "android.media.route.feature.LOCAL_PLAYBACK";
    public static final String FEATURE_REMOTE_AUDIO_PLAYBACK = "android.media.route.feature.REMOTE_AUDIO_PLAYBACK";
    public static final String FEATURE_REMOTE_GROUP_PLAYBACK = "android.media.route.feature.REMOTE_GROUP_PLAYBACK";
    public static final String FEATURE_REMOTE_PLAYBACK = "android.media.route.feature.REMOTE_PLAYBACK";
    public static final String FEATURE_REMOTE_VIDEO_PLAYBACK = "android.media.route.feature.REMOTE_VIDEO_PLAYBACK";
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int TYPE_BLE_HEADSET = 26;
    public static final int TYPE_BLUETOOTH_A2DP = 8;
    public static final int TYPE_BUILTIN_SPEAKER = 2;
    public static final int TYPE_DOCK = 13;
    public static final int TYPE_GROUP = 2000;
    public static final int TYPE_HDMI = 9;
    public static final int TYPE_HEARING_AID = 23;
    public static final int TYPE_REMOTE_AUDIO_VIDEO_RECEIVER = 1003;
    public static final int TYPE_REMOTE_SPEAKER = 1002;
    public static final int TYPE_REMOTE_TV = 1001;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_USB_ACCESSORY = 12;
    public static final int TYPE_USB_DEVICE = 11;
    public static final int TYPE_USB_HEADSET = 22;
    public static final int TYPE_WIRED_HEADPHONES = 4;
    public static final int TYPE_WIRED_HEADSET = 3;
    final String mAddress;
    final Set<String> mAllowedPackages;
    final String mClientPackageName;
    final int mConnectionState;
    final Set<String> mDeduplicationIds;
    final CharSequence mDescription;
    final Bundle mExtras;
    final List<String> mFeatures;
    final Uri mIconUri;
    final String mId;
    final boolean mIsSystem;
    final boolean mIsVisibilityRestricted;
    final CharSequence mName;
    final String mPackageName;
    final String mProviderId;
    final int mType;
    final int mVolume;
    final int mVolumeHandling;
    final int mVolumeMax;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ConnectionState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PlaybackVolume {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    MediaRoute2Info(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
        this.mFeatures = builder.mFeatures;
        this.mType = builder.mType;
        this.mIsSystem = builder.mIsSystem;
        this.mIconUri = builder.mIconUri;
        this.mDescription = builder.mDescription;
        this.mConnectionState = builder.mConnectionState;
        this.mClientPackageName = builder.mClientPackageName;
        this.mPackageName = builder.mPackageName;
        this.mVolumeHandling = builder.mVolumeHandling;
        this.mVolumeMax = builder.mVolumeMax;
        this.mVolume = builder.mVolume;
        this.mAddress = builder.mAddress;
        this.mDeduplicationIds = builder.mDeduplicationIds;
        this.mExtras = builder.mExtras;
        this.mProviderId = builder.mProviderId;
        this.mIsVisibilityRestricted = builder.mIsVisibilityRestricted;
        this.mAllowedPackages = builder.mAllowedPackages;
    }

    MediaRoute2Info(Parcel in) {
        String readString = in.readString();
        this.mId = readString;
        Preconditions.checkArgument(!TextUtils.isEmpty(readString));
        this.mName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mFeatures = in.createStringArrayList();
        this.mType = in.readInt();
        this.mIsSystem = in.readBoolean();
        this.mIconUri = (Uri) in.readParcelable(null, Uri.class);
        this.mDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mConnectionState = in.readInt();
        this.mClientPackageName = in.readString();
        this.mPackageName = in.readString();
        this.mVolumeHandling = in.readInt();
        this.mVolumeMax = in.readInt();
        this.mVolume = in.readInt();
        this.mAddress = in.readString();
        this.mDeduplicationIds = Set.of((Object[]) in.readStringArray());
        this.mExtras = in.readBundle();
        this.mProviderId = in.readString();
        this.mIsVisibilityRestricted = in.readBoolean();
        this.mAllowedPackages = Set.of((Object[]) in.createString8Array());
    }

    public String getId() {
        if (!TextUtils.isEmpty(this.mProviderId)) {
            return MediaRouter2Utils.toUniqueId(this.mProviderId, this.mId);
        }
        return this.mId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public List<String> getFeatures() {
        return this.mFeatures;
    }

    public int getType() {
        return this.mType;
    }

    public boolean isSystemRoute() {
        return this.mIsSystem;
    }

    public Uri getIconUri() {
        return this.mIconUri;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public int getConnectionState() {
        return this.mConnectionState;
    }

    public String getClientPackageName() {
        return this.mClientPackageName;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getVolumeHandling() {
        return this.mVolumeHandling;
    }

    public int getVolumeMax() {
        return this.mVolumeMax;
    }

    public int getVolume() {
        return this.mVolume;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public Set<String> getDeduplicationIds() {
        return this.mDeduplicationIds;
    }

    public Bundle getExtras() {
        if (this.mExtras == null) {
            return null;
        }
        return new Bundle(this.mExtras);
    }

    public String getOriginalId() {
        return this.mId;
    }

    public String getProviderId() {
        return this.mProviderId;
    }

    public boolean hasAnyFeatures(Collection<String> features) {
        Objects.requireNonNull(features, "features must not be null");
        for (String feature : features) {
            if (getFeatures().contains(feature)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllFeatures(Collection<String> features) {
        Objects.requireNonNull(features, "features must not be null");
        for (String feature : features) {
            if (!getFeatures().contains(feature)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(getId()) || TextUtils.isEmpty(getName()) || TextUtils.isEmpty(getProviderId())) {
            return false;
        }
        return true;
    }

    public boolean isVisibleTo(String packageName) {
        return !this.mIsVisibilityRestricted || getPackageName().equals(packageName) || this.mAllowedPackages.contains(packageName);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "MediaRoute2Info");
        String indent = prefix + "  ";
        pw.println(indent + "mId=" + this.mId);
        pw.println(indent + "mName=" + ((Object) this.mName));
        pw.println(indent + "mFeatures=" + this.mFeatures);
        pw.println(indent + "mType=" + getDeviceTypeString(this.mType));
        pw.println(indent + "mIsSystem=" + this.mIsSystem);
        pw.println(indent + "mIconUri=" + this.mIconUri);
        pw.println(indent + "mDescription=" + ((Object) this.mDescription));
        pw.println(indent + "mConnectionState=" + this.mConnectionState);
        pw.println(indent + "mClientPackageName=" + this.mClientPackageName);
        pw.println(indent + "mPackageName=" + this.mPackageName);
        dumpVolume(pw, indent);
        pw.println(indent + "mAddress=" + this.mAddress);
        pw.println(indent + "mDeduplicationIds=" + this.mDeduplicationIds);
        pw.println(indent + "mExtras=" + this.mExtras);
        pw.println(indent + "mProviderId=" + this.mProviderId);
        pw.println(indent + "mIsVisibilityRestricted=" + this.mIsVisibilityRestricted);
        pw.println(indent + "mAllowedPackages=" + this.mAllowedPackages);
    }

    private void dumpVolume(PrintWriter pw, String prefix) {
        String volumeHandlingName;
        switch (this.mVolumeHandling) {
            case 0:
                volumeHandlingName = "FIXED";
                break;
            case 1:
                volumeHandlingName = "VARIABLE";
                break;
            default:
                volumeHandlingName = "UNKNOWN";
                break;
        }
        String volume = String.format(Locale.US, "volume(current=%d, max=%d, handling=%s(%d))", Integer.valueOf(this.mVolume), Integer.valueOf(this.mVolumeMax), volumeHandlingName, Integer.valueOf(this.mVolumeHandling));
        pw.println(prefix + volume);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MediaRoute2Info) {
            MediaRoute2Info other = (MediaRoute2Info) obj;
            return Objects.equals(this.mId, other.mId) && Objects.equals(this.mName, other.mName) && Objects.equals(this.mFeatures, other.mFeatures) && this.mType == other.mType && this.mIsSystem == other.mIsSystem && Objects.equals(this.mIconUri, other.mIconUri) && Objects.equals(this.mDescription, other.mDescription) && this.mConnectionState == other.mConnectionState && Objects.equals(this.mClientPackageName, other.mClientPackageName) && Objects.equals(this.mPackageName, other.mPackageName) && this.mVolumeHandling == other.mVolumeHandling && this.mVolumeMax == other.mVolumeMax && this.mVolume == other.mVolume && Objects.equals(this.mAddress, other.mAddress) && Objects.equals(this.mDeduplicationIds, other.mDeduplicationIds) && Objects.equals(this.mProviderId, other.mProviderId) && this.mIsVisibilityRestricted == other.mIsVisibilityRestricted && Objects.equals(this.mAllowedPackages, other.mAllowedPackages);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId, this.mName, this.mFeatures, Integer.valueOf(this.mType), Boolean.valueOf(this.mIsSystem), this.mIconUri, this.mDescription, Integer.valueOf(this.mConnectionState), this.mClientPackageName, this.mPackageName, Integer.valueOf(this.mVolumeHandling), Integer.valueOf(this.mVolumeMax), Integer.valueOf(this.mVolume), this.mAddress, this.mDeduplicationIds, this.mProviderId, Boolean.valueOf(this.mIsVisibilityRestricted), this.mAllowedPackages);
    }

    public String toString() {
        StringBuilder result = new StringBuilder().append("MediaRoute2Info{ ").append("id=").append(getId()).append(", name=").append(getName()).append(", features=").append(getFeatures()).append(", iconUri=").append(getIconUri()).append(", description=").append(getDescription()).append(", connectionState=").append(getConnectionState()).append(", clientPackageName=").append(getClientPackageName()).append(", volumeHandling=").append(getVolumeHandling()).append(", volumeMax=").append(getVolumeMax()).append(", volume=").append(getVolume()).append(", deduplicationIds=").append(String.join(",", getDeduplicationIds())).append(", providerId=").append(getProviderId()).append(", isVisibilityRestricted=").append(this.mIsVisibilityRestricted).append(", allowedPackages=").append(String.join(",", this.mAllowedPackages)).append(" }");
        return result.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        TextUtils.writeToParcel(this.mName, dest, flags);
        dest.writeStringList(this.mFeatures);
        dest.writeInt(this.mType);
        dest.writeBoolean(this.mIsSystem);
        dest.writeParcelable(this.mIconUri, flags);
        TextUtils.writeToParcel(this.mDescription, dest, flags);
        dest.writeInt(this.mConnectionState);
        dest.writeString(this.mClientPackageName);
        dest.writeString(this.mPackageName);
        dest.writeInt(this.mVolumeHandling);
        dest.writeInt(this.mVolumeMax);
        dest.writeInt(this.mVolume);
        dest.writeString(this.mAddress);
        Set<String> set = this.mDeduplicationIds;
        dest.writeStringArray((String[]) set.toArray(new String[set.size()]));
        dest.writeBundle(this.mExtras);
        dest.writeString(this.mProviderId);
        dest.writeBoolean(this.mIsVisibilityRestricted);
        dest.writeString8Array((String[]) this.mAllowedPackages.toArray(new String[0]));
    }

    private static String getDeviceTypeString(int deviceType) {
        switch (deviceType) {
            case 2:
                return "BUILTIN_SPEAKER";
            case 3:
                return "WIRED_HEADSET";
            case 4:
                return "WIRED_HEADPHONES";
            case 8:
                return "BLUETOOTH_A2DP";
            case 9:
                return "HDMI";
            case 11:
                return "USB_DEVICE";
            case 12:
                return "USB_ACCESSORY";
            case 13:
                return "DOCK";
            case 22:
                return "USB_HEADSET";
            case 23:
                return "HEARING_AID";
            case 1001:
                return "REMOTE_TV";
            case 1002:
                return "REMOTE_SPEAKER";
            case 1003:
                return "REMOTE_AUDIO_VIDEO_RECEIVER";
            case 2000:
                return "GROUP";
            default:
                return TextUtils.formatSimple("UNKNOWN(%d)", Integer.valueOf(deviceType));
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        String mAddress;
        Set<String> mAllowedPackages;
        String mClientPackageName;
        int mConnectionState;
        Set<String> mDeduplicationIds;
        CharSequence mDescription;
        Bundle mExtras;
        final List<String> mFeatures;
        Uri mIconUri;
        final String mId;
        boolean mIsSystem;
        boolean mIsVisibilityRestricted;
        final CharSequence mName;
        String mPackageName;
        String mProviderId;
        int mType;
        int mVolume;
        int mVolumeHandling;
        int mVolumeMax;

        public Builder(String id, CharSequence name) {
            this.mType = 0;
            this.mVolumeHandling = 0;
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id must not be empty");
            }
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name must not be empty");
            }
            this.mId = id;
            this.mName = name;
            this.mFeatures = new ArrayList();
            this.mDeduplicationIds = Set.of();
            this.mAllowedPackages = Set.of();
        }

        public Builder(MediaRoute2Info routeInfo) {
            this(routeInfo.mId, routeInfo);
        }

        public Builder(String id, MediaRoute2Info routeInfo) {
            this.mType = 0;
            this.mVolumeHandling = 0;
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id must not be empty");
            }
            Objects.requireNonNull(routeInfo, "routeInfo must not be null");
            this.mId = id;
            this.mName = routeInfo.mName;
            this.mFeatures = new ArrayList(routeInfo.mFeatures);
            this.mType = routeInfo.mType;
            this.mIsSystem = routeInfo.mIsSystem;
            this.mIconUri = routeInfo.mIconUri;
            this.mDescription = routeInfo.mDescription;
            this.mConnectionState = routeInfo.mConnectionState;
            this.mClientPackageName = routeInfo.mClientPackageName;
            this.mPackageName = routeInfo.mPackageName;
            this.mVolumeHandling = routeInfo.mVolumeHandling;
            this.mVolumeMax = routeInfo.mVolumeMax;
            this.mVolume = routeInfo.mVolume;
            this.mAddress = routeInfo.mAddress;
            this.mDeduplicationIds = Set.copyOf(routeInfo.mDeduplicationIds);
            if (routeInfo.mExtras != null) {
                this.mExtras = new Bundle(routeInfo.mExtras);
            }
            this.mProviderId = routeInfo.mProviderId;
            this.mIsVisibilityRestricted = routeInfo.mIsVisibilityRestricted;
            this.mAllowedPackages = routeInfo.mAllowedPackages;
        }

        public Builder addFeature(String feature) {
            if (TextUtils.isEmpty(feature)) {
                throw new IllegalArgumentException("feature must not be null or empty");
            }
            this.mFeatures.add(feature);
            return this;
        }

        public Builder addFeatures(Collection<String> features) {
            Objects.requireNonNull(features, "features must not be null");
            for (String feature : features) {
                addFeature(feature);
            }
            return this;
        }

        public Builder clearFeatures() {
            this.mFeatures.clear();
            return this;
        }

        public Builder setType(int type) {
            this.mType = type;
            return this;
        }

        public Builder setSystemRoute(boolean isSystem) {
            this.mIsSystem = isSystem;
            return this;
        }

        public Builder setIconUri(Uri iconUri) {
            this.mIconUri = iconUri;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            this.mDescription = description;
            return this;
        }

        public Builder setConnectionState(int connectionState) {
            this.mConnectionState = connectionState;
            return this;
        }

        public Builder setClientPackageName(String packageName) {
            this.mClientPackageName = packageName;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.mPackageName = packageName;
            return this;
        }

        public Builder setVolumeHandling(int volumeHandling) {
            this.mVolumeHandling = volumeHandling;
            return this;
        }

        public Builder setVolumeMax(int volumeMax) {
            this.mVolumeMax = volumeMax;
            return this;
        }

        public Builder setVolume(int volume) {
            this.mVolume = volume;
            return this;
        }

        public Builder setAddress(String address) {
            this.mAddress = address;
            return this;
        }

        public Builder setDeduplicationIds(Set<String> id) {
            this.mDeduplicationIds = Set.copyOf(id);
            return this;
        }

        public Builder setExtras(Bundle extras) {
            if (extras == null) {
                this.mExtras = null;
                return this;
            }
            this.mExtras = new Bundle(extras);
            return this;
        }

        public Builder setProviderId(String providerId) {
            if (TextUtils.isEmpty(providerId)) {
                throw new IllegalArgumentException("providerId must not be null or empty");
            }
            this.mProviderId = providerId;
            return this;
        }

        public Builder setVisibilityPublic() {
            this.mIsVisibilityRestricted = false;
            this.mAllowedPackages = Set.of();
            return this;
        }

        public Builder setVisibilityRestricted(Set<String> allowedPackages) {
            this.mIsVisibilityRestricted = true;
            this.mAllowedPackages = Set.copyOf(allowedPackages);
            return this;
        }

        public MediaRoute2Info build() {
            if (this.mFeatures.isEmpty()) {
                throw new IllegalArgumentException("features must not be empty!");
            }
            return new MediaRoute2Info(this);
        }
    }
}
