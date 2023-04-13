package android.app.backup;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public class BackupProgress implements Parcelable {
    public static final Parcelable.Creator<BackupProgress> CREATOR = new Parcelable.Creator<BackupProgress>() { // from class: android.app.backup.BackupProgress.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackupProgress createFromParcel(Parcel in) {
            return new BackupProgress(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackupProgress[] newArray(int size) {
            return new BackupProgress[size];
        }
    };
    public final long bytesExpected;
    public final long bytesTransferred;

    public BackupProgress(long _bytesExpected, long _bytesTransferred) {
        this.bytesExpected = _bytesExpected;
        this.bytesTransferred = _bytesTransferred;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.bytesExpected);
        out.writeLong(this.bytesTransferred);
    }

    private BackupProgress(Parcel in) {
        this.bytesExpected = in.readLong();
        this.bytesTransferred = in.readLong();
    }
}
