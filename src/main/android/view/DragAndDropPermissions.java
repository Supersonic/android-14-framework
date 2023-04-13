package android.view;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.view.IDragAndDropPermissions;
/* loaded from: classes4.dex */
public final class DragAndDropPermissions implements Parcelable {
    public static final Parcelable.Creator<DragAndDropPermissions> CREATOR = new Parcelable.Creator<DragAndDropPermissions>() { // from class: android.view.DragAndDropPermissions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DragAndDropPermissions createFromParcel(Parcel source) {
            return new DragAndDropPermissions(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DragAndDropPermissions[] newArray(int size) {
            return new DragAndDropPermissions[size];
        }
    };
    private static final boolean DEBUG = false;
    private static final String TAG = "DragAndDrop";
    private final IDragAndDropPermissions mDragAndDropPermissions;

    public static DragAndDropPermissions obtain(DragEvent dragEvent) {
        if (dragEvent.getDragAndDropPermissions() == null) {
            return null;
        }
        return new DragAndDropPermissions(dragEvent.getDragAndDropPermissions());
    }

    private DragAndDropPermissions(IDragAndDropPermissions dragAndDropPermissions) {
        this.mDragAndDropPermissions = dragAndDropPermissions;
    }

    public boolean take(IBinder activityToken) {
        try {
            this.mDragAndDropPermissions.take(activityToken);
            return true;
        } catch (RemoteException e) {
            Log.m103w(TAG, this + ": take() failed with a RemoteException", e);
            return false;
        }
    }

    public boolean takeTransient() {
        try {
            this.mDragAndDropPermissions.takeTransient();
            return true;
        } catch (RemoteException e) {
            Log.m103w(TAG, this + ": takeTransient() failed with a RemoteException", e);
            return false;
        }
    }

    public void release() {
        try {
            this.mDragAndDropPermissions.release();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeStrongInterface(this.mDragAndDropPermissions);
    }

    private DragAndDropPermissions(Parcel in) {
        this.mDragAndDropPermissions = IDragAndDropPermissions.Stub.asInterface(in.readStrongBinder());
    }
}
