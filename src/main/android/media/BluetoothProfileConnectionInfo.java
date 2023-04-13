package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public final class BluetoothProfileConnectionInfo implements Parcelable {
    public static final Parcelable.Creator<BluetoothProfileConnectionInfo> CREATOR = new Parcelable.Creator<BluetoothProfileConnectionInfo>() { // from class: android.media.BluetoothProfileConnectionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothProfileConnectionInfo createFromParcel(Parcel source) {
            return new BluetoothProfileConnectionInfo(source.readInt(), source.readBoolean(), source.readInt(), source.readBoolean());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothProfileConnectionInfo[] newArray(int size) {
            return new BluetoothProfileConnectionInfo[size];
        }
    };
    private final boolean mIsLeOutput;
    private final int mProfile;
    private final boolean mSupprNoisy;
    private final int mVolume;

    private BluetoothProfileConnectionInfo(int profile, boolean suppressNoisyIntent, int volume, boolean isLeOutput) {
        this.mProfile = profile;
        this.mSupprNoisy = suppressNoisyIntent;
        this.mVolume = volume;
        this.mIsLeOutput = isLeOutput;
    }

    public BluetoothProfileConnectionInfo(int profile) {
        this(profile, false, -1, false);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mProfile);
        dest.writeBoolean(this.mSupprNoisy);
        dest.writeInt(this.mVolume);
        dest.writeBoolean(this.mIsLeOutput);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static BluetoothProfileConnectionInfo createA2dpInfo(boolean suppressNoisyIntent, int volume) {
        return new BluetoothProfileConnectionInfo(2, suppressNoisyIntent, volume, false);
    }

    public static BluetoothProfileConnectionInfo createA2dpSinkInfo(int volume) {
        return new BluetoothProfileConnectionInfo(11, true, volume, false);
    }

    public static BluetoothProfileConnectionInfo createHearingAidInfo(boolean suppressNoisyIntent) {
        return new BluetoothProfileConnectionInfo(21, suppressNoisyIntent, -1, false);
    }

    public static BluetoothProfileConnectionInfo createLeAudioInfo(boolean suppressNoisyIntent, boolean isLeOutput) {
        return new BluetoothProfileConnectionInfo(22, suppressNoisyIntent, -1, isLeOutput);
    }

    public static BluetoothProfileConnectionInfo createLeAudioOutputInfo(boolean suppressNoisyIntent, int volume) {
        return new BluetoothProfileConnectionInfo(22, suppressNoisyIntent, volume, true);
    }

    public int getProfile() {
        return this.mProfile;
    }

    public boolean isSuppressNoisyIntent() {
        return this.mSupprNoisy;
    }

    public int getVolume() {
        return this.mVolume;
    }

    public boolean isLeOutput() {
        return this.mIsLeOutput;
    }
}
