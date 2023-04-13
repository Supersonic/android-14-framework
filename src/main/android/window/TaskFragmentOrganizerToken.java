package android.window;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.window.ITaskFragmentOrganizer;
/* loaded from: classes4.dex */
public final class TaskFragmentOrganizerToken implements Parcelable {
    public static final Parcelable.Creator<TaskFragmentOrganizerToken> CREATOR = new Parcelable.Creator<TaskFragmentOrganizerToken>() { // from class: android.window.TaskFragmentOrganizerToken.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentOrganizerToken createFromParcel(Parcel in) {
            ITaskFragmentOrganizer realToken = ITaskFragmentOrganizer.Stub.asInterface(in.readStrongBinder());
            if (realToken == null) {
                return null;
            }
            return new TaskFragmentOrganizerToken(realToken);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentOrganizerToken[] newArray(int size) {
            return new TaskFragmentOrganizerToken[size];
        }
    };
    private final ITaskFragmentOrganizer mRealToken;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskFragmentOrganizerToken(ITaskFragmentOrganizer realToken) {
        this.mRealToken = realToken;
    }

    public IBinder asBinder() {
        return this.mRealToken.asBinder();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongInterface(this.mRealToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        return this.mRealToken.asBinder().hashCode();
    }

    public String toString() {
        return "TaskFragmentOrganizerToken{" + this.mRealToken + "}";
    }

    public boolean equals(Object obj) {
        return (obj instanceof TaskFragmentOrganizerToken) && this.mRealToken.asBinder() == ((TaskFragmentOrganizerToken) obj).asBinder();
    }
}
