package android.p008os;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
@SystemApi
/* renamed from: android.os.ServiceSpecificException */
/* loaded from: classes3.dex */
public class ServiceSpecificException extends RuntimeException {
    public final int errorCode;

    public ServiceSpecificException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceSpecificException(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return super.toString() + " (code " + this.errorCode + NavigationBarInflaterView.KEY_CODE_END;
    }
}
