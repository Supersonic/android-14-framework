package android.security.keymaster;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public class ExportResult implements Parcelable {
    public static final Parcelable.Creator<ExportResult> CREATOR = new Parcelable.Creator<ExportResult>() { // from class: android.security.keymaster.ExportResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExportResult createFromParcel(Parcel in) {
            return new ExportResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExportResult[] newArray(int length) {
            return new ExportResult[length];
        }
    };
    public final byte[] exportData;
    public final int resultCode;

    public ExportResult(int resultCode) {
        this.resultCode = resultCode;
        this.exportData = new byte[0];
    }

    protected ExportResult(Parcel in) {
        this.resultCode = in.readInt();
        this.exportData = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.resultCode);
        out.writeByteArray(this.exportData);
    }
}
