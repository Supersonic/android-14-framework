package android.telecom;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class QueryLocationException extends RuntimeException implements Parcelable {
    public static final Parcelable.Creator<QueryLocationException> CREATOR = new Parcelable.Creator<QueryLocationException>() { // from class: android.telecom.QueryLocationException.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QueryLocationException createFromParcel(Parcel source) {
            return new QueryLocationException(source.readString8(), source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public QueryLocationException[] newArray(int size) {
            return new QueryLocationException[size];
        }
    };
    public static final int ERROR_NOT_ALLOWED_FOR_NON_EMERGENCY_CONNECTIONS = 4;
    public static final int ERROR_NOT_PERMITTED = 3;
    public static final int ERROR_PREVIOUS_REQUEST_EXISTS = 2;
    public static final int ERROR_REQUEST_TIME_OUT = 1;
    public static final int ERROR_SERVICE_UNAVAILABLE = 5;
    public static final int ERROR_UNSPECIFIED = 6;
    public static final String QUERY_LOCATION_ERROR = "QueryLocationErrorKey";
    private int mCode;
    private final String mMessage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface QueryLocationErrorCode {
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

    public QueryLocationException(String message) {
        super(getMessage(message, 6));
        this.mCode = 6;
        this.mMessage = message;
    }

    public QueryLocationException(String message, int code) {
        super(getMessage(message, code));
        this.mCode = 6;
        this.mCode = code;
        this.mMessage = message;
    }

    public QueryLocationException(String message, int code, Throwable cause) {
        super(getMessage(message, code), cause);
        this.mCode = 6;
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
