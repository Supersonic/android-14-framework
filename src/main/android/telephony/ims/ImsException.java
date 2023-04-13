package android.telephony.ims;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class ImsException extends Exception {
    public static final int CODE_ERROR_INVALID_SUBSCRIPTION = 3;
    public static final int CODE_ERROR_SERVICE_UNAVAILABLE = 1;
    public static final int CODE_ERROR_UNSPECIFIED = 0;
    public static final int CODE_ERROR_UNSUPPORTED_OPERATION = 2;
    private int mCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsErrorCode {
    }

    @SystemApi
    public ImsException(String message) {
        super(getMessage(message, 0));
        this.mCode = 0;
    }

    @SystemApi
    public ImsException(String message, int code) {
        super(getMessage(message, code));
        this.mCode = 0;
        this.mCode = code;
    }

    @SystemApi
    public ImsException(String message, int code, Throwable cause) {
        super(getMessage(message, code), cause);
        this.mCode = 0;
        this.mCode = code;
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
