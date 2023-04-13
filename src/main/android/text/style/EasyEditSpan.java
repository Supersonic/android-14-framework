package android.text.style;

import android.app.PendingIntent;
import android.p008os.Parcel;
import android.text.ParcelableSpan;
/* loaded from: classes3.dex */
public class EasyEditSpan implements ParcelableSpan {
    public static final String EXTRA_TEXT_CHANGED_TYPE = "android.text.style.EXTRA_TEXT_CHANGED_TYPE";
    public static final int TEXT_DELETED = 1;
    public static final int TEXT_MODIFIED = 2;
    private boolean mDeleteEnabled;
    private final PendingIntent mPendingIntent;

    public EasyEditSpan() {
        this.mPendingIntent = null;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(PendingIntent pendingIntent) {
        this.mPendingIntent = pendingIntent;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(Parcel source) {
        this.mPendingIntent = (PendingIntent) source.readParcelable(null, PendingIntent.class);
        this.mDeleteEnabled = source.readByte() == 1;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeParcelable(this.mPendingIntent, 0);
        dest.writeByte(this.mDeleteEnabled ? (byte) 1 : (byte) 0);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 22;
    }

    public boolean isDeleteEnabled() {
        return this.mDeleteEnabled;
    }

    public void setDeleteEnabled(boolean value) {
        this.mDeleteEnabled = value;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }
}
