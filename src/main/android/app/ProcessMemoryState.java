package android.app;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class ProcessMemoryState implements Parcelable {
    public static final Parcelable.Creator<ProcessMemoryState> CREATOR = new Parcelable.Creator<ProcessMemoryState>() { // from class: android.app.ProcessMemoryState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProcessMemoryState createFromParcel(Parcel in) {
            return new ProcessMemoryState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProcessMemoryState[] newArray(int size) {
            return new ProcessMemoryState[size];
        }
    };
    public final boolean hasForegroundServices;
    public final int oomScore;
    public final int pid;
    public final String processName;
    public final int uid;

    public ProcessMemoryState(int uid, int pid, String processName, int oomScore, boolean hasForegroundServices) {
        this.uid = uid;
        this.pid = pid;
        this.processName = processName;
        this.oomScore = oomScore;
        this.hasForegroundServices = hasForegroundServices;
    }

    private ProcessMemoryState(Parcel in) {
        this.uid = in.readInt();
        this.pid = in.readInt();
        this.processName = in.readString();
        this.oomScore = in.readInt();
        this.hasForegroundServices = in.readInt() == 1;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.uid);
        parcel.writeInt(this.pid);
        parcel.writeString(this.processName);
        parcel.writeInt(this.oomScore);
        parcel.writeInt(this.hasForegroundServices ? 1 : 0);
    }
}
