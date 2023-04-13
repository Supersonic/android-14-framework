package android.media;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
/* loaded from: classes2.dex */
public final class AudioHalVersionInfo implements Parcelable, Comparable<AudioHalVersionInfo> {
    public static final AudioHalVersionInfo AIDL_1_0 = new AudioHalVersionInfo(1, 1, 0);
    public static final int AUDIO_HAL_TYPE_AIDL = 1;
    public static final int AUDIO_HAL_TYPE_HIDL = 0;
    public static final Parcelable.Creator<AudioHalVersionInfo> CREATOR;
    public static final AudioHalVersionInfo HIDL_2_0;
    public static final AudioHalVersionInfo HIDL_4_0;
    public static final AudioHalVersionInfo HIDL_5_0;
    public static final AudioHalVersionInfo HIDL_6_0;
    public static final AudioHalVersionInfo HIDL_7_0;
    public static final AudioHalVersionInfo HIDL_7_1;
    private static final String TAG = "AudioHalVersionInfo";
    public static final List<AudioHalVersionInfo> VERSIONS;
    private AudioHalVersion mHalVersion;

    /* loaded from: classes2.dex */
    public @interface AudioHalType {
    }

    static {
        AudioHalVersionInfo audioHalVersionInfo = new AudioHalVersionInfo(0, 7, 1);
        HIDL_7_1 = audioHalVersionInfo;
        AudioHalVersionInfo audioHalVersionInfo2 = new AudioHalVersionInfo(0, 7, 0);
        HIDL_7_0 = audioHalVersionInfo2;
        AudioHalVersionInfo audioHalVersionInfo3 = new AudioHalVersionInfo(0, 6, 0);
        HIDL_6_0 = audioHalVersionInfo3;
        AudioHalVersionInfo audioHalVersionInfo4 = new AudioHalVersionInfo(0, 5, 0);
        HIDL_5_0 = audioHalVersionInfo4;
        AudioHalVersionInfo audioHalVersionInfo5 = new AudioHalVersionInfo(0, 4, 0);
        HIDL_4_0 = audioHalVersionInfo5;
        HIDL_2_0 = new AudioHalVersionInfo(0, 2, 0);
        VERSIONS = List.of(audioHalVersionInfo, audioHalVersionInfo2, audioHalVersionInfo3, audioHalVersionInfo4, audioHalVersionInfo5);
        CREATOR = new Parcelable.Creator<AudioHalVersionInfo>() { // from class: android.media.AudioHalVersionInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AudioHalVersionInfo createFromParcel(Parcel in) {
                return new AudioHalVersionInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AudioHalVersionInfo[] newArray(int size) {
                return new AudioHalVersionInfo[size];
            }
        };
    }

    public int getHalType() {
        return this.mHalVersion.type;
    }

    public int getMajorVersion() {
        return this.mHalVersion.major;
    }

    public int getMinorVersion() {
        return this.mHalVersion.minor;
    }

    private static String typeToString(int type) {
        if (type == 0) {
            return "HIDL";
        }
        if (type == 1) {
            return "AIDL";
        }
        return "INVALID";
    }

    private static String toString(int type, int major, int minor) {
        return typeToString(type) + ":" + Integer.toString(major) + MediaMetrics.SEPARATOR + Integer.toString(minor);
    }

    private AudioHalVersionInfo(int type, int major, int minor) {
        AudioHalVersion audioHalVersion = new AudioHalVersion();
        this.mHalVersion = audioHalVersion;
        audioHalVersion.type = type;
        this.mHalVersion.major = major;
        this.mHalVersion.minor = minor;
    }

    private AudioHalVersionInfo(Parcel in) {
        this.mHalVersion = new AudioHalVersion();
        this.mHalVersion = (AudioHalVersion) in.readTypedObject(AudioHalVersion.CREATOR);
    }

    public String toString() {
        return toString(this.mHalVersion.type, this.mHalVersion.major, this.mHalVersion.minor);
    }

    @Override // java.lang.Comparable
    public int compareTo(AudioHalVersionInfo other) {
        List<AudioHalVersionInfo> list = VERSIONS;
        int indexOther = list.indexOf(other);
        int indexThis = list.indexOf(this);
        if (indexThis < 0 || indexOther < 0) {
            return indexThis - indexOther;
        }
        return indexOther - indexThis;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flag) {
        out.writeTypedObject(this.mHalVersion, flag);
    }
}
