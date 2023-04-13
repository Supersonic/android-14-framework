package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class AudioDeviceAttributes implements Parcelable {
    public static final Parcelable.Creator<AudioDeviceAttributes> CREATOR = new Parcelable.Creator<AudioDeviceAttributes>() { // from class: android.media.AudioDeviceAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceAttributes createFromParcel(Parcel p) {
            return new AudioDeviceAttributes(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDeviceAttributes[] newArray(int size) {
            return new AudioDeviceAttributes[size];
        }
    };
    public static final int ROLE_INPUT = 1;
    public static final int ROLE_OUTPUT = 2;
    private final String mAddress;
    private final List<AudioDescriptor> mAudioDescriptors;
    private final List<AudioProfile> mAudioProfiles;
    private final String mName;
    private final int mNativeType;
    private final int mRole;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Role {
    }

    @SystemApi
    public AudioDeviceAttributes(AudioDeviceInfo deviceInfo) {
        Objects.requireNonNull(deviceInfo);
        this.mRole = deviceInfo.isSink() ? 2 : 1;
        this.mType = deviceInfo.getType();
        this.mAddress = deviceInfo.getAddress();
        this.mName = String.valueOf(deviceInfo.getProductName());
        this.mNativeType = deviceInfo.getInternalType();
        this.mAudioProfiles = deviceInfo.getAudioProfiles();
        this.mAudioDescriptors = deviceInfo.getAudioDescriptors();
    }

    @SystemApi
    public AudioDeviceAttributes(int role, int type, String address) {
        this(role, type, address, "", new ArrayList(), new ArrayList());
    }

    @SystemApi
    public AudioDeviceAttributes(int role, int type, String address, String name, List<AudioProfile> profiles, List<AudioDescriptor> descriptors) {
        Objects.requireNonNull(address);
        if (role != 2 && role != 1) {
            throw new IllegalArgumentException("Invalid role " + role);
        }
        if (role == 2) {
            AudioDeviceInfo.enforceValidAudioDeviceTypeOut(type);
            this.mNativeType = AudioDeviceInfo.convertDeviceTypeToInternalDevice(type);
        } else if (role == 1) {
            AudioDeviceInfo.enforceValidAudioDeviceTypeIn(type);
            this.mNativeType = AudioDeviceInfo.convertDeviceTypeToInternalInputDevice(type, address);
        } else {
            this.mNativeType = 0;
        }
        this.mRole = role;
        this.mType = type;
        this.mAddress = address;
        this.mName = name;
        this.mAudioProfiles = profiles;
        this.mAudioDescriptors = descriptors;
    }

    public AudioDeviceAttributes(int nativeType, String address) {
        this(nativeType, address, "");
    }

    public AudioDeviceAttributes(int nativeType, String address, String name) {
        this.mRole = (Integer.MIN_VALUE & nativeType) != 0 ? 1 : 2;
        this.mType = AudioDeviceInfo.convertInternalDeviceToDeviceType(nativeType);
        this.mAddress = address;
        this.mName = name;
        this.mNativeType = nativeType;
        this.mAudioProfiles = new ArrayList();
        this.mAudioDescriptors = new ArrayList();
    }

    @SystemApi
    public int getRole() {
        return this.mRole;
    }

    @SystemApi
    public int getType() {
        return this.mType;
    }

    @SystemApi
    public String getAddress() {
        return this.mAddress;
    }

    @SystemApi
    public String getName() {
        return this.mName;
    }

    public int getInternalType() {
        return this.mNativeType;
    }

    @SystemApi
    public List<AudioProfile> getAudioProfiles() {
        return this.mAudioProfiles;
    }

    @SystemApi
    public List<AudioDescriptor> getAudioDescriptors() {
        return this.mAudioDescriptors;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRole), Integer.valueOf(this.mType), this.mAddress, this.mName, this.mAudioProfiles, this.mAudioDescriptors);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioDeviceAttributes that = (AudioDeviceAttributes) o;
        if (this.mRole == that.mRole && this.mType == that.mType && this.mAddress.equals(that.mAddress) && this.mName.equals(that.mName) && this.mAudioProfiles.equals(that.mAudioProfiles) && this.mAudioDescriptors.equals(that.mAudioDescriptors)) {
            return true;
        }
        return false;
    }

    public boolean equalTypeAddress(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioDeviceAttributes that = (AudioDeviceAttributes) o;
        if (this.mRole == that.mRole && this.mType == that.mType && this.mAddress.equals(that.mAddress)) {
            return true;
        }
        return false;
    }

    public static String roleToString(int role) {
        return role == 2 ? "output" : "input";
    }

    public String toString() {
        return new String("AudioDeviceAttributes: role:" + roleToString(this.mRole) + " type:" + (this.mRole == 2 ? AudioSystem.getOutputDeviceName(this.mNativeType) : AudioSystem.getInputDeviceName(this.mNativeType)) + " addr:" + this.mAddress + " name:" + this.mName + " profiles:" + this.mAudioProfiles.toString() + " descriptors:" + this.mAudioDescriptors.toString());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRole);
        dest.writeInt(this.mType);
        dest.writeString(this.mAddress);
        dest.writeString(this.mName);
        dest.writeInt(this.mNativeType);
        List<AudioProfile> list = this.mAudioProfiles;
        dest.writeParcelableArray((AudioProfile[]) list.toArray(new AudioProfile[list.size()]), flags);
        List<AudioDescriptor> list2 = this.mAudioDescriptors;
        dest.writeParcelableArray((AudioDescriptor[]) list2.toArray(new AudioDescriptor[list2.size()]), flags);
    }

    private AudioDeviceAttributes(Parcel in) {
        this.mRole = in.readInt();
        this.mType = in.readInt();
        this.mAddress = in.readString();
        this.mName = in.readString();
        this.mNativeType = in.readInt();
        AudioProfile[] audioProfilesArray = (AudioProfile[]) in.readParcelableArray(AudioProfile.class.getClassLoader(), AudioProfile.class);
        this.mAudioProfiles = new ArrayList(Arrays.asList(audioProfilesArray));
        AudioDescriptor[] audioDescriptorsArray = (AudioDescriptor[]) in.readParcelableArray(AudioDescriptor.class.getClassLoader(), AudioDescriptor.class);
        this.mAudioDescriptors = new ArrayList(Arrays.asList(audioDescriptorsArray));
    }
}
