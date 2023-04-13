package android.telecom;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class CallException extends RuntimeException implements Parcelable {
    public static final int CODE_CALL_CANNOT_BE_SET_TO_ACTIVE = 4;
    public static final int CODE_CALL_IS_NOT_BEING_TRACKED = 3;
    public static final int CODE_CALL_NOT_PERMITTED_AT_PRESENT_TIME = 5;
    public static final int CODE_CANNOT_HOLD_CURRENT_ACTIVE_CALL = 2;
    public static final int CODE_ERROR_UNKNOWN = 1;
    public static final int CODE_OPERATION_TIMED_OUT = 6;
    public static final Parcelable.Creator<CallException> CREATOR = new Parcelable.Creator<CallException>() { // from class: android.telecom.CallException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallException createFromParcel(Parcel source) {
            return new CallException(source.readString8(), source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallException[] newArray(int size) {
            return new CallException[size];
        }
    };
    public static final String TRANSACTION_EXCEPTION_KEY = "TelecomTransactionalExceptionKey";
    private int mCode;
    private final String mMessage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallErrorCode {
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

    public CallException(String message, int code) {
        super(getMessage(message, code));
        this.mCode = 1;
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
