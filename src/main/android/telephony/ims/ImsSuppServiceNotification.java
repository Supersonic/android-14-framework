package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
@SystemApi
/* loaded from: classes3.dex */
public final class ImsSuppServiceNotification implements Parcelable {
    public static final Parcelable.Creator<ImsSuppServiceNotification> CREATOR = new Parcelable.Creator<ImsSuppServiceNotification>() { // from class: android.telephony.ims.ImsSuppServiceNotification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSuppServiceNotification createFromParcel(Parcel in) {
            return new ImsSuppServiceNotification(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsSuppServiceNotification[] newArray(int size) {
            return new ImsSuppServiceNotification[size];
        }
    };
    private static final String TAG = "ImsSuppServiceNotification";
    public final int code;
    public final String[] history;
    public final int index;
    public final int notificationType;
    public final String number;
    public final int type;

    public ImsSuppServiceNotification(int notificationType, int code, int index, int type, String number, String[] history) {
        this.notificationType = notificationType;
        this.code = code;
        this.index = index;
        this.type = type;
        this.number = number;
        this.history = history;
    }

    public ImsSuppServiceNotification(Parcel in) {
        this.notificationType = in.readInt();
        this.code = in.readInt();
        this.index = in.readInt();
        this.type = in.readInt();
        this.number = in.readString();
        this.history = in.createStringArray();
    }

    public String toString() {
        return "{ notificationType=" + this.notificationType + ", code=" + this.code + ", index=" + this.index + ", type=" + this.type + ", number=" + this.number + ", history=" + Arrays.toString(this.history) + " }";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.notificationType);
        out.writeInt(this.code);
        out.writeInt(this.index);
        out.writeInt(this.type);
        out.writeString(this.number);
        out.writeStringArray(this.history);
    }
}
