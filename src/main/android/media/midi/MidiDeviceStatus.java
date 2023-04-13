package android.media.midi;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class MidiDeviceStatus implements Parcelable {
    public static final Parcelable.Creator<MidiDeviceStatus> CREATOR = new Parcelable.Creator<MidiDeviceStatus>() { // from class: android.media.midi.MidiDeviceStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MidiDeviceStatus createFromParcel(Parcel in) {
            ClassLoader classLoader = MidiDeviceInfo.class.getClassLoader();
            MidiDeviceInfo deviceInfo = (MidiDeviceInfo) in.readParcelable(classLoader, MidiDeviceInfo.class);
            boolean[] inputPortOpen = in.createBooleanArray();
            int[] outputPortOpenCount = in.createIntArray();
            return new MidiDeviceStatus(deviceInfo, inputPortOpen, outputPortOpenCount);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MidiDeviceStatus[] newArray(int size) {
            return new MidiDeviceStatus[size];
        }
    };
    private static final String TAG = "MidiDeviceStatus";
    private final MidiDeviceInfo mDeviceInfo;
    private final boolean[] mInputPortOpen;
    private final int[] mOutputPortOpenCount;

    public MidiDeviceStatus(MidiDeviceInfo deviceInfo, boolean[] inputPortOpen, int[] outputPortOpenCount) {
        this.mDeviceInfo = deviceInfo;
        boolean[] zArr = new boolean[inputPortOpen.length];
        this.mInputPortOpen = zArr;
        System.arraycopy(inputPortOpen, 0, zArr, 0, inputPortOpen.length);
        int[] iArr = new int[outputPortOpenCount.length];
        this.mOutputPortOpenCount = iArr;
        System.arraycopy(outputPortOpenCount, 0, iArr, 0, outputPortOpenCount.length);
    }

    public MidiDeviceStatus(MidiDeviceInfo deviceInfo) {
        this.mDeviceInfo = deviceInfo;
        this.mInputPortOpen = new boolean[deviceInfo.getInputPortCount()];
        this.mOutputPortOpenCount = new int[deviceInfo.getOutputPortCount()];
    }

    public MidiDeviceInfo getDeviceInfo() {
        return this.mDeviceInfo;
    }

    public boolean isInputPortOpen(int portNumber) {
        return this.mInputPortOpen[portNumber];
    }

    public int getOutputPortOpenCount(int portNumber) {
        return this.mOutputPortOpenCount[portNumber];
    }

    public String toString() {
        int inputPortCount = this.mDeviceInfo.getInputPortCount();
        int outputPortCount = this.mDeviceInfo.getOutputPortCount();
        StringBuilder builder = new StringBuilder("mInputPortOpen=[");
        for (int i = 0; i < inputPortCount; i++) {
            builder.append(this.mInputPortOpen[i]);
            if (i < inputPortCount - 1) {
                builder.append(",");
            }
        }
        builder.append("] mOutputPortOpenCount=[");
        for (int i2 = 0; i2 < outputPortCount; i2++) {
            builder.append(this.mOutputPortOpenCount[i2]);
            if (i2 < outputPortCount - 1) {
                builder.append(",");
            }
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        return builder.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mDeviceInfo, flags);
        parcel.writeBooleanArray(this.mInputPortOpen);
        parcel.writeIntArray(this.mOutputPortOpenCount);
    }
}
