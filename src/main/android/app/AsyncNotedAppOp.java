package android.app;

import android.annotation.CurrentTimeMillisLong;
import android.annotation.IntRange;
import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Annotation;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AsyncNotedAppOp implements Parcelable {
    public static final Parcelable.Creator<AsyncNotedAppOp> CREATOR = new Parcelable.Creator<AsyncNotedAppOp>() { // from class: android.app.AsyncNotedAppOp.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AsyncNotedAppOp[] newArray(int size) {
            return new AsyncNotedAppOp[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AsyncNotedAppOp createFromParcel(Parcel in) {
            return new AsyncNotedAppOp(in);
        }
    };
    private final String mAttributionTag;
    private final String mMessage;
    private final int mNotingUid;
    private final int mOpCode;
    private final long mTime;

    public String getOp() {
        return AppOpsManager.opToPublicName(this.mOpCode);
    }

    private void onConstructed() {
        Preconditions.checkArgumentInRange(this.mOpCode, 0, 133, "opCode");
    }

    private String opCodeToString() {
        return getOp();
    }

    public AsyncNotedAppOp(int opCode, int notingUid, String attributionTag, String message, long time) {
        this.mOpCode = opCode;
        AnnotationValidations.validate((Class<IntRange>) IntRange.class, (IntRange) null, opCode, "from", 0L);
        this.mNotingUid = notingUid;
        AnnotationValidations.validate((Class<IntRange>) IntRange.class, (IntRange) null, notingUid, "from", 0L);
        this.mAttributionTag = attributionTag;
        this.mMessage = message;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) message);
        this.mTime = time;
        AnnotationValidations.validate(CurrentTimeMillisLong.class, (Annotation) null, time);
        onConstructed();
    }

    public int getNotingUid() {
        return this.mNotingUid;
    }

    public String getAttributionTag() {
        return this.mAttributionTag;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public long getTime() {
        return this.mTime;
    }

    public String toString() {
        return "AsyncNotedAppOp { opCode = " + opCodeToString() + ", notingUid = " + this.mNotingUid + ", attributionTag = " + this.mAttributionTag + ", message = " + this.mMessage + ", time = " + this.mTime + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsyncNotedAppOp that = (AsyncNotedAppOp) o;
        if (this.mOpCode == that.mOpCode && this.mNotingUid == that.mNotingUid && Objects.equals(this.mAttributionTag, that.mAttributionTag) && Objects.equals(this.mMessage, that.mMessage) && this.mTime == that.mTime) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mOpCode;
        return (((((((_hash * 31) + this.mNotingUid) * 31) + Objects.hashCode(this.mAttributionTag)) * 31) + Objects.hashCode(this.mMessage)) * 31) + Long.hashCode(this.mTime);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mAttributionTag != null ? (byte) (0 | 4) : (byte) 0;
        dest.writeByte(flg);
        dest.writeInt(this.mOpCode);
        dest.writeInt(this.mNotingUid);
        String str = this.mAttributionTag;
        if (str != null) {
            dest.writeString(str);
        }
        dest.writeString(this.mMessage);
        dest.writeLong(this.mTime);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AsyncNotedAppOp(Parcel in) {
        byte flg = in.readByte();
        int opCode = in.readInt();
        int notingUid = in.readInt();
        String attributionTag = (flg & 4) == 0 ? null : in.readString();
        String message = in.readString();
        long time = in.readLong();
        this.mOpCode = opCode;
        AnnotationValidations.validate((Class<IntRange>) IntRange.class, (IntRange) null, opCode, "from", 0L);
        this.mNotingUid = notingUid;
        AnnotationValidations.validate((Class<IntRange>) IntRange.class, (IntRange) null, notingUid, "from", 0L);
        this.mAttributionTag = attributionTag;
        this.mMessage = message;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) message);
        this.mTime = time;
        AnnotationValidations.validate(CurrentTimeMillisLong.class, (Annotation) null, time);
        onConstructed();
    }

    @Deprecated
    private void __metadata() {
    }
}
