package android.content.p001pm;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.content.pm.KeySet */
/* loaded from: classes.dex */
public class KeySet implements Parcelable {
    public static final Parcelable.Creator<KeySet> CREATOR = new Parcelable.Creator<KeySet>() { // from class: android.content.pm.KeySet.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeySet createFromParcel(Parcel source) {
            return KeySet.readFromParcel(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeySet[] newArray(int size) {
            return new KeySet[size];
        }
    };
    private IBinder token;

    public KeySet(IBinder token) {
        if (token == null) {
            throw new NullPointerException("null value for KeySet IBinder token");
        }
        this.token = token;
    }

    public IBinder getToken() {
        return this.token;
    }

    public boolean equals(Object o) {
        if (o instanceof KeySet) {
            KeySet ks = (KeySet) o;
            return this.token == ks.token;
        }
        return false;
    }

    public int hashCode() {
        return this.token.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static KeySet readFromParcel(Parcel in) {
        IBinder token = in.readStrongBinder();
        return new KeySet(token);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.token);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
