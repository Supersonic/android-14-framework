package android.app;

import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public class ResultInfo implements Parcelable {
    public static final Parcelable.Creator<ResultInfo> CREATOR = new Parcelable.Creator<ResultInfo>() { // from class: android.app.ResultInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResultInfo createFromParcel(Parcel in) {
            return new ResultInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResultInfo[] newArray(int size) {
            return new ResultInfo[size];
        }
    };
    public final Intent mData;
    public final int mRequestCode;
    public final int mResultCode;
    public final String mResultWho;

    public ResultInfo(String resultWho, int requestCode, int resultCode, Intent data) {
        this.mResultWho = resultWho;
        this.mRequestCode = requestCode;
        this.mResultCode = resultCode;
        this.mData = data;
    }

    public String toString() {
        return "ResultInfo{who=" + this.mResultWho + ", request=" + this.mRequestCode + ", result=" + this.mResultCode + ", data=" + this.mData + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mResultWho);
        out.writeInt(this.mRequestCode);
        out.writeInt(this.mResultCode);
        if (this.mData != null) {
            out.writeInt(1);
            this.mData.writeToParcel(out, 0);
            return;
        }
        out.writeInt(0);
    }

    public ResultInfo(Parcel in) {
        this.mResultWho = in.readString();
        this.mRequestCode = in.readInt();
        this.mResultCode = in.readInt();
        if (in.readInt() != 0) {
            this.mData = Intent.CREATOR.createFromParcel(in);
        } else {
            this.mData = null;
        }
    }

    public boolean equals(Object obj) {
        boolean intentsEqual;
        if (obj == null || !(obj instanceof ResultInfo)) {
            return false;
        }
        ResultInfo other = (ResultInfo) obj;
        Intent intent = this.mData;
        if (intent == null) {
            intentsEqual = other.mData == null;
        } else {
            intentsEqual = intent.filterEquals(other.mData);
        }
        return intentsEqual && Objects.equals(this.mResultWho, other.mResultWho) && this.mResultCode == other.mResultCode && this.mRequestCode == other.mRequestCode;
    }

    public int hashCode() {
        int result = (((((17 * 31) + this.mRequestCode) * 31) + this.mResultCode) * 31) + Objects.hashCode(this.mResultWho);
        Intent intent = this.mData;
        if (intent != null) {
            return (result * 31) + intent.filterHashCode();
        }
        return result;
    }
}
