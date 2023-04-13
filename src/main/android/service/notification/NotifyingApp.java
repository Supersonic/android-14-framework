package android.service.notification;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.format.DateFormat;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class NotifyingApp implements Parcelable, Comparable<NotifyingApp> {
    public static final Parcelable.Creator<NotifyingApp> CREATOR = new Parcelable.Creator<NotifyingApp>() { // from class: android.service.notification.NotifyingApp.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotifyingApp createFromParcel(Parcel in) {
            return new NotifyingApp(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotifyingApp[] newArray(int size) {
            return new NotifyingApp[size];
        }
    };
    private long mLastNotified;
    private String mPkg;
    private int mUserId;

    public NotifyingApp() {
    }

    protected NotifyingApp(Parcel in) {
        this.mUserId = in.readInt();
        this.mPkg = in.readString();
        this.mLastNotified = in.readLong();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public NotifyingApp setUserId(int mUserId) {
        this.mUserId = mUserId;
        return this;
    }

    public String getPackage() {
        return this.mPkg;
    }

    public NotifyingApp setPackage(String mPkg) {
        this.mPkg = mPkg;
        return this;
    }

    public long getLastNotified() {
        return this.mLastNotified;
    }

    public NotifyingApp setLastNotified(long mLastNotified) {
        this.mLastNotified = mLastNotified;
        return this;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserId);
        dest.writeString(this.mPkg);
        dest.writeLong(this.mLastNotified);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotifyingApp that = (NotifyingApp) o;
        if (getUserId() == that.getUserId() && getLastNotified() == that.getLastNotified() && Objects.equals(this.mPkg, that.mPkg)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(getUserId()), this.mPkg, Long.valueOf(getLastNotified()));
    }

    @Override // java.lang.Comparable
    public int compareTo(NotifyingApp o) {
        if (getLastNotified() == o.getLastNotified()) {
            if (getUserId() == o.getUserId()) {
                return getPackage().compareTo(o.getPackage());
            }
            return Integer.compare(getUserId(), o.getUserId());
        }
        return -Long.compare(getLastNotified(), o.getLastNotified());
    }

    public String toString() {
        return "NotifyingApp{mUserId=" + this.mUserId + ", mPkg='" + this.mPkg + DateFormat.QUOTE + ", mLastNotified=" + this.mLastNotified + '}';
    }
}
