package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class DataThrottlingRequest implements Parcelable {
    public static final Parcelable.Creator<DataThrottlingRequest> CREATOR = new Parcelable.Creator<DataThrottlingRequest>() { // from class: android.telephony.DataThrottlingRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataThrottlingRequest createFromParcel(Parcel in) {
            return new DataThrottlingRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataThrottlingRequest[] newArray(int size) {
            return new DataThrottlingRequest[size];
        }
    };
    @SystemApi
    public static final int DATA_THROTTLING_ACTION_HOLD = 3;
    @SystemApi
    public static final int DATA_THROTTLING_ACTION_NO_DATA_THROTTLING = 0;
    @SystemApi
    public static final int DATA_THROTTLING_ACTION_THROTTLE_PRIMARY_CARRIER = 2;
    @SystemApi
    public static final int DATA_THROTTLING_ACTION_THROTTLE_SECONDARY_CARRIER = 1;
    private long mCompletionDurationMillis;
    private int mDataThrottlingAction;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DataThrottlingAction {
    }

    private DataThrottlingRequest(int dataThrottlingAction, long completionDurationMillis) {
        this.mDataThrottlingAction = dataThrottlingAction;
        this.mCompletionDurationMillis = completionDurationMillis;
    }

    private DataThrottlingRequest(Parcel in) {
        this.mDataThrottlingAction = in.readInt();
        this.mCompletionDurationMillis = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDataThrottlingAction);
        dest.writeLong(this.mCompletionDurationMillis);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "[DataThrottlingRequest , DataThrottlingAction=" + this.mDataThrottlingAction + ", completionDurationMillis=" + this.mCompletionDurationMillis + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public int getDataThrottlingAction() {
        return this.mDataThrottlingAction;
    }

    public long getCompletionDurationMillis() {
        return this.mCompletionDurationMillis;
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class Builder {
        private long mCompletionDurationMillis;
        private int mDataThrottlingAction;

        public Builder setDataThrottlingAction(int dataThrottlingAction) {
            this.mDataThrottlingAction = dataThrottlingAction;
            return this;
        }

        public Builder setCompletionDurationMillis(long completionDurationMillis) {
            this.mCompletionDurationMillis = completionDurationMillis;
            return this;
        }

        public DataThrottlingRequest build() {
            long j = this.mCompletionDurationMillis;
            if (j < 0) {
                throw new IllegalArgumentException("completionDurationMillis cannot be a negative number");
            }
            if (this.mDataThrottlingAction == 3 && j != 0) {
                throw new IllegalArgumentException("completionDurationMillis must be 0 for DataThrottlingRequest.DATA_THROTTLING_ACTION_HOLD");
            }
            return new DataThrottlingRequest(this.mDataThrottlingAction, this.mCompletionDurationMillis);
        }
    }
}
