package android.content;

import android.accounts.Account;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public class PeriodicSync implements Parcelable {
    public static final Parcelable.Creator<PeriodicSync> CREATOR = new Parcelable.Creator<PeriodicSync>() { // from class: android.content.PeriodicSync.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PeriodicSync createFromParcel(Parcel source) {
            return new PeriodicSync(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PeriodicSync[] newArray(int size) {
            return new PeriodicSync[size];
        }
    };
    public final Account account;
    public final String authority;
    public final Bundle extras;
    public final long flexTime;
    public final long period;

    public PeriodicSync(Account account, String authority, Bundle extras, long periodInSeconds) {
        this.account = account;
        this.authority = authority;
        if (extras == null) {
            this.extras = new Bundle();
        } else {
            this.extras = new Bundle(extras);
        }
        this.period = periodInSeconds;
        this.flexTime = 0L;
    }

    public PeriodicSync(PeriodicSync other) {
        this.account = other.account;
        this.authority = other.authority;
        this.extras = new Bundle(other.extras);
        this.period = other.period;
        this.flexTime = other.flexTime;
    }

    public PeriodicSync(Account account, String authority, Bundle extras, long period, long flexTime) {
        this.account = account;
        this.authority = authority;
        this.extras = new Bundle(extras);
        this.period = period;
        this.flexTime = flexTime;
    }

    private PeriodicSync(Parcel in) {
        this.account = (Account) in.readParcelable(null, Account.class);
        this.authority = in.readString();
        this.extras = in.readBundle();
        this.period = in.readLong();
        this.flexTime = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.account, flags);
        dest.writeString(this.authority);
        dest.writeBundle(this.extras);
        dest.writeLong(this.period);
        dest.writeLong(this.flexTime);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PeriodicSync) {
            PeriodicSync other = (PeriodicSync) o;
            return this.account.equals(other.account) && this.authority.equals(other.authority) && this.period == other.period && syncExtrasEquals(this.extras, other.extras);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0022  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean syncExtrasEquals(Bundle b1, Bundle b2) {
        if (b1.size() != b2.size()) {
            return false;
        }
        if (b1.isEmpty()) {
            return true;
        }
        for (String key : b1.keySet()) {
            if (!b2.containsKey(key) || !Objects.equals(b1.get(key), b2.get(key))) {
                return false;
            }
            while (r0.hasNext()) {
            }
        }
        return true;
    }

    public String toString() {
        return "account: " + this.account + ", authority: " + this.authority + ". period: " + this.period + "s , flex: " + this.flexTime;
    }
}
