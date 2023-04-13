package android.telecom;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class CallEndpointException extends RuntimeException implements Parcelable {
    public static final String CHANGE_ERROR = "ChangeErrorKey";
    public static final Parcelable.Creator<CallEndpointException> CREATOR = new Parcelable.Creator<CallEndpointException>() { // from class: android.telecom.CallEndpointException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallEndpointException createFromParcel(Parcel source) {
            return new CallEndpointException(source.readString8(), source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallEndpointException[] newArray(int size) {
            return new CallEndpointException[size];
        }
    };
    public static final int ERROR_ANOTHER_REQUEST = 3;
    public static final int ERROR_ENDPOINT_DOES_NOT_EXIST = 1;
    public static final int ERROR_REQUEST_TIME_OUT = 2;
    public static final int ERROR_UNSPECIFIED = 4;
    private int mCode;
    private final String mMessage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallEndpointErrorCode {
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mMessage);
        dest.writeInt(this.mCode);
    }

    public CallEndpointException(String message, int code) {
        super(getMessage(message, code));
        this.mCode = 4;
        this.mCode = code;
        this.mMessage = message;
    }

    public int getCode() {
        return this.mCode;
    }

    private static String getMessage(String message, int code) {
        if (!TextUtils.isEmpty(message)) {
            return message + " (code: " + code + NavigationBarInflaterView.KEY_CODE_END;
        }
        return "code: " + code;
    }
}
