package android.credentials.p002ui;

import android.annotation.NonNull;
import android.app.slice.Slice;
import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.credentials.ui.AuthenticationEntry */
/* loaded from: classes.dex */
public final class AuthenticationEntry implements Parcelable {
    public static final Parcelable.Creator<AuthenticationEntry> CREATOR = new Parcelable.Creator<AuthenticationEntry>() { // from class: android.credentials.ui.AuthenticationEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticationEntry createFromParcel(Parcel in) {
            return new AuthenticationEntry(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AuthenticationEntry[] newArray(int size) {
            return new AuthenticationEntry[size];
        }
    };
    public static final int STATUS_LOCKED = 0;
    public static final int STATUS_UNLOCKED_BUT_EMPTY_LESS_RECENT = 1;
    public static final int STATUS_UNLOCKED_BUT_EMPTY_MOST_RECENT = 2;
    private Intent mFrameworkExtrasIntent;
    private final String mKey;
    private final Slice mSlice;
    private final int mStatus;
    private final String mSubkey;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.credentials.ui.AuthenticationEntry$Status */
    /* loaded from: classes.dex */
    public @interface Status {
    }

    private AuthenticationEntry(Parcel in) {
        String readString8 = in.readString8();
        this.mKey = readString8;
        String readString82 = in.readString8();
        this.mSubkey = readString82;
        this.mStatus = in.readInt();
        Slice slice = (Slice) in.readTypedObject(Slice.CREATOR);
        this.mSlice = slice;
        this.mFrameworkExtrasIntent = (Intent) in.readTypedObject(Intent.CREATOR);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) readString8);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) readString82);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) slice);
    }

    public AuthenticationEntry(String key, String subkey, Slice slice, int status) {
        this.mKey = key;
        this.mSubkey = subkey;
        this.mSlice = slice;
        this.mStatus = status;
    }

    public AuthenticationEntry(String key, String subkey, Slice slice, int status, Intent intent) {
        this(key, subkey, slice, status);
        this.mFrameworkExtrasIntent = intent;
    }

    public String getKey() {
        return this.mKey;
    }

    public String getSubkey() {
        return this.mSubkey;
    }

    public Slice getSlice() {
        return this.mSlice;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public Intent getFrameworkExtrasIntent() {
        return this.mFrameworkExtrasIntent;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mKey);
        dest.writeString8(this.mSubkey);
        dest.writeInt(this.mStatus);
        dest.writeTypedObject(this.mSlice, flags);
        dest.writeTypedObject(this.mFrameworkExtrasIntent, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
