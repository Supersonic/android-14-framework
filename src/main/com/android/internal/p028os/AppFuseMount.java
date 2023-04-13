package com.android.internal.p028os;

import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
/* renamed from: com.android.internal.os.AppFuseMount */
/* loaded from: classes4.dex */
public class AppFuseMount implements Parcelable {
    public static final Parcelable.Creator<AppFuseMount> CREATOR = new Parcelable.Creator<AppFuseMount>() { // from class: com.android.internal.os.AppFuseMount.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppFuseMount createFromParcel(Parcel in) {
            return new AppFuseMount(in.readInt(), (ParcelFileDescriptor) in.readParcelable(null, ParcelFileDescriptor.class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppFuseMount[] newArray(int size) {
            return new AppFuseMount[size];
        }
    };

    /* renamed from: fd */
    public final ParcelFileDescriptor f902fd;
    public final int mountPointId;

    public AppFuseMount(int mountPointId, ParcelFileDescriptor fd) {
        Preconditions.checkNotNull(fd);
        this.mountPointId = mountPointId;
        this.f902fd = fd;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mountPointId);
        dest.writeParcelable(this.f902fd, flags);
    }
}
