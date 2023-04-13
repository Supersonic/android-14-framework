package android.telephony.euicc;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class EuiccNotification implements Parcelable {
    public static final int ALL_EVENTS = 15;
    public static final Parcelable.Creator<EuiccNotification> CREATOR = new Parcelable.Creator<EuiccNotification>() { // from class: android.telephony.euicc.EuiccNotification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EuiccNotification createFromParcel(Parcel source) {
            return new EuiccNotification(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EuiccNotification[] newArray(int size) {
            return new EuiccNotification[size];
        }
    };
    public static final int EVENT_DELETE = 8;
    public static final int EVENT_DISABLE = 4;
    public static final int EVENT_ENABLE = 2;
    public static final int EVENT_INSTALL = 1;
    private final byte[] mData;
    private final int mEvent;
    private final int mSeq;
    private final String mTargetAddr;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Event {
    }

    public EuiccNotification(int seq, String targetAddr, int event, byte[] data) {
        this.mSeq = seq;
        this.mTargetAddr = targetAddr;
        this.mEvent = event;
        this.mData = data;
    }

    public int getSeq() {
        return this.mSeq;
    }

    public String getTargetAddr() {
        return this.mTargetAddr;
    }

    public int getEvent() {
        return this.mEvent;
    }

    public byte[] getData() {
        return this.mData;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EuiccNotification that = (EuiccNotification) obj;
        if (this.mSeq == that.mSeq && Objects.equals(this.mTargetAddr, that.mTargetAddr) && this.mEvent == that.mEvent && Arrays.equals(this.mData, that.mData)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (1 * 31) + this.mSeq;
        return (((((result * 31) + Objects.hashCode(this.mTargetAddr)) * 31) + this.mEvent) * 31) + Arrays.hashCode(this.mData);
    }

    public String toString() {
        return "EuiccNotification (seq=" + this.mSeq + ", targetAddr=" + this.mTargetAddr + ", event=" + this.mEvent + ", data=" + (this.mData == null ? "null" : "byte[" + this.mData.length + NavigationBarInflaterView.SIZE_MOD_END) + NavigationBarInflaterView.KEY_CODE_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSeq);
        dest.writeString(this.mTargetAddr);
        dest.writeInt(this.mEvent);
        dest.writeByteArray(this.mData);
    }

    private EuiccNotification(Parcel source) {
        this.mSeq = source.readInt();
        this.mTargetAddr = source.readString();
        this.mEvent = source.readInt();
        this.mData = source.createByteArray();
    }
}
