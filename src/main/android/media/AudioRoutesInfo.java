package android.media;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
/* loaded from: classes2.dex */
public class AudioRoutesInfo implements Parcelable {
    public static final Parcelable.Creator<AudioRoutesInfo> CREATOR = new Parcelable.Creator<AudioRoutesInfo>() { // from class: android.media.AudioRoutesInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioRoutesInfo createFromParcel(Parcel in) {
            return new AudioRoutesInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioRoutesInfo[] newArray(int size) {
            return new AudioRoutesInfo[size];
        }
    };
    public static final int MAIN_DOCK_SPEAKERS = 4;
    public static final int MAIN_HDMI = 8;
    public static final int MAIN_HEADPHONES = 2;
    public static final int MAIN_HEADSET = 1;
    public static final int MAIN_SPEAKER = 0;
    public static final int MAIN_USB = 16;
    public CharSequence bluetoothName;
    public int mainType;

    public AudioRoutesInfo() {
        this.mainType = 0;
    }

    public AudioRoutesInfo(AudioRoutesInfo o) {
        this.mainType = 0;
        this.bluetoothName = o.bluetoothName;
        this.mainType = o.mainType;
    }

    AudioRoutesInfo(Parcel src) {
        this.mainType = 0;
        this.bluetoothName = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(src);
        this.mainType = src.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return getClass().getSimpleName() + "{ type=" + typeToString(this.mainType) + (TextUtils.isEmpty(this.bluetoothName) ? "" : ", bluetoothName=" + ((Object) this.bluetoothName)) + " }";
    }

    private static String typeToString(int type) {
        return type == 0 ? "SPEAKER" : (type & 1) != 0 ? "HEADSET" : (type & 2) != 0 ? "HEADPHONES" : (type & 4) != 0 ? "DOCK_SPEAKERS" : (type & 8) != 0 ? "HDMI" : (type & 16) != 0 ? "USB" : Integer.toHexString(type);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.bluetoothName, dest, flags);
        dest.writeInt(this.mainType);
    }
}
