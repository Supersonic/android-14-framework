package android.accounts;

import android.accounts.IAccountManager;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.transition.EpicenterTranslateClipReveal;
import java.util.Set;
/* loaded from: classes.dex */
public class Account implements Parcelable {
    private static final String TAG = "Account";
    private final String accessId;
    private String mSafeName;
    public final String name;
    public final String type;
    private static final Set<Account> sAccessedAccounts = new ArraySet();
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() { // from class: android.accounts.Account.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Account) {
            Account other = (Account) o;
            return this.name.equals(other.name) && this.type.equals(other.type);
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.name.hashCode();
        return (result * 31) + this.type.hashCode();
    }

    public Account(String name, String type) {
        this(name, type, null);
    }

    public Account(Account other, String accessId) {
        this(other.name, other.type, accessId);
    }

    public Account(String name, String type, String accessId) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the name must not be empty: " + name);
        }
        if (TextUtils.isEmpty(type)) {
            throw new IllegalArgumentException("the type must not be empty: " + type);
        }
        this.name = name;
        this.type = type;
        this.accessId = accessId;
    }

    public Account(Parcel in) {
        String readString = in.readString();
        this.name = readString;
        String readString2 = in.readString();
        this.type = readString2;
        if (TextUtils.isEmpty(readString)) {
            throw new BadParcelableException("the name must not be empty: " + readString);
        }
        if (TextUtils.isEmpty(readString2)) {
            throw new BadParcelableException("the type must not be empty: " + readString2);
        }
        String readString3 = in.readString();
        this.accessId = readString3;
        if (readString3 != null) {
            Set<Account> set = sAccessedAccounts;
            synchronized (set) {
                if (set.add(this)) {
                    try {
                        IAccountManager accountManager = IAccountManager.Stub.asInterface(ServiceManager.getService("account"));
                        accountManager.onAccountAccessed(readString3);
                    } catch (RemoteException e) {
                        Log.m109e(TAG, "Error noting account access", e);
                    }
                }
            }
        }
    }

    public String getAccessId() {
        return this.accessId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.accessId);
    }

    public String toString() {
        return "Account {name=" + this.name + ", type=" + this.type + "}";
    }

    public String toSafeString() {
        if (this.mSafeName == null) {
            this.mSafeName = toSafeName(this.name, EpicenterTranslateClipReveal.StateProperty.TARGET_X);
        }
        return "Account {name=" + this.mSafeName + ", type=" + this.type + "}";
    }

    public static String toSafeName(String name, char replacement) {
        StringBuilder builder = new StringBuilder(64);
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                builder.append(replacement);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
