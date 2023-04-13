package android.credentials.p002ui;

import android.annotation.NonNull;
import android.app.PendingIntent;
import android.app.slice.Slice;
import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.credentials.ui.Entry */
/* loaded from: classes.dex */
public final class Entry implements Parcelable {
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() { // from class: android.credentials.ui.Entry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
    private Intent mFrameworkExtrasIntent;
    private final String mKey;
    private PendingIntent mPendingIntent;
    private final Slice mSlice;
    private final String mSubkey;

    private Entry(Parcel in) {
        String key = in.readString8();
        String subkey = in.readString8();
        Slice slice = (Slice) in.readTypedObject(Slice.CREATOR);
        this.mKey = key;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) key);
        this.mSubkey = subkey;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) subkey);
        this.mSlice = slice;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) slice);
        this.mPendingIntent = (PendingIntent) in.readTypedObject(PendingIntent.CREATOR);
        this.mFrameworkExtrasIntent = (Intent) in.readTypedObject(Intent.CREATOR);
    }

    public Entry(String key, String subkey, Slice slice) {
        this.mKey = key;
        this.mSubkey = subkey;
        this.mSlice = slice;
    }

    public Entry(String key, String subkey, Slice slice, Intent intent) {
        this(key, subkey, slice);
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

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }

    public Intent getFrameworkExtrasIntent() {
        return this.mFrameworkExtrasIntent;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mKey);
        dest.writeString8(this.mSubkey);
        dest.writeTypedObject(this.mSlice, flags);
        dest.writeTypedObject(this.mPendingIntent, flags);
        dest.writeTypedObject(this.mFrameworkExtrasIntent, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
