package android.service.trust;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class GrantTrustResult implements Parcelable {
    public static final Parcelable.Creator<GrantTrustResult> CREATOR = new Parcelable.Creator<GrantTrustResult>() { // from class: android.service.trust.GrantTrustResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GrantTrustResult[] newArray(int size) {
            return new GrantTrustResult[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GrantTrustResult createFromParcel(Parcel in) {
            return new GrantTrustResult(in);
        }
    };
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_UNLOCKED_BY_GRANT = 1;
    private int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Status {
    }

    public static String statusToString(int value) {
        switch (value) {
            case 0:
                return "STATUS_UNKNOWN";
            case 1:
                return "STATUS_UNLOCKED_BY_GRANT";
            default:
                return Integer.toHexString(value);
        }
    }

    public GrantTrustResult(int status) {
        this.mStatus = status;
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("status was " + this.mStatus + " but must be one of: STATUS_UNKNOWN(0), STATUS_UNLOCKED_BY_GRANT(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public int getStatus() {
        return this.mStatus;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatus);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    GrantTrustResult(Parcel in) {
        int status = in.readInt();
        this.mStatus = status;
        if (status != 0 && status != 1) {
            throw new IllegalArgumentException("status was " + this.mStatus + " but must be one of: STATUS_UNKNOWN(0), STATUS_UNLOCKED_BY_GRANT(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
