package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.p008os.RemoteException;
import android.telephony.SmsMessage;
import android.telephony.ims.aidl.IImsSmsListener;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class ImsSmsImplBase {
    public static final int DELIVER_STATUS_ERROR_GENERIC = 2;
    public static final int DELIVER_STATUS_ERROR_NO_MEMORY = 3;
    public static final int DELIVER_STATUS_ERROR_REQUEST_NOT_SUPPORTED = 4;
    public static final int DELIVER_STATUS_OK = 1;
    private static final String LOG_TAG = "SmsImplBase";
    public static final int RESULT_NO_NETWORK_ERROR = -1;
    public static final int SEND_STATUS_ERROR = 2;
    public static final int SEND_STATUS_ERROR_FALLBACK = 4;
    public static final int SEND_STATUS_ERROR_RETRY = 3;
    public static final int SEND_STATUS_OK = 1;
    public static final int STATUS_REPORT_STATUS_ERROR = 2;
    public static final int STATUS_REPORT_STATUS_OK = 1;
    private Executor mExecutor;
    private IImsSmsListener mListener;
    private final Object mLock = new Object();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeliverStatusResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SendStatusResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StatusReportResult {
    }

    public ImsSmsImplBase() {
    }

    public ImsSmsImplBase(Executor executor) {
        this.mExecutor = executor;
    }

    public final void registerSmsListener(IImsSmsListener listener) {
        synchronized (this.mLock) {
            this.mListener = listener;
        }
    }

    public void sendSms(int token, int messageRef, String format, String smsc, boolean isRetry, byte[] pdu) {
        try {
            onSendSmsResult(token, messageRef, 2, 1);
        } catch (RuntimeException e) {
            Log.m110e(LOG_TAG, "Can not send sms: " + e.getMessage());
        }
    }

    public void onMemoryAvailable(int token) {
    }

    public void acknowledgeSms(int token, int messageRef, int result) {
        Log.m110e(LOG_TAG, "acknowledgeSms() not implemented.");
    }

    public void acknowledgeSms(int token, int messageRef, int result, byte[] pdu) {
        Log.m110e(LOG_TAG, "acknowledgeSms() not implemented. acknowledgeSms(int, int, int) called.");
        acknowledgeSms(token, messageRef, result);
    }

    public void acknowledgeSmsReport(int token, int messageRef, int result) {
        Log.m110e(LOG_TAG, "acknowledgeSmsReport() not implemented.");
    }

    public final void onSmsReceived(int token, String format, byte[] pdu) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSmsReceived(token, format, pdu);
        } catch (RemoteException e) {
            Log.m110e(LOG_TAG, "Can not deliver sms: " + e.getMessage());
            SmsMessage message = SmsMessage.createFromPdu(pdu, format);
            if (message != null && message.mWrappedSmsMessage != null) {
                acknowledgeSms(token, message.mWrappedSmsMessage.mMessageRef, 2);
                return;
            }
            Log.m104w(LOG_TAG, "onSmsReceived: Invalid pdu entered.");
            acknowledgeSms(token, 0, 2);
        }
    }

    public final void onSendSmsResultSuccess(int token, int messageRef) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSendSmsResult(token, messageRef, 1, 0, -1);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public final void onSendSmsResult(int token, int messageRef, int status, int reason) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSendSmsResult(token, messageRef, status, reason, -1);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void onSendSmsResultError(int token, int messageRef, int status, int reason, int networkErrorCode) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSendSmsResult(token, messageRef, status, reason, networkErrorCode);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void onMemoryAvailableResult(int token, int result, int networkErrorCode) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onMemoryAvailableResult(token, result, networkErrorCode);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public final void onSmsStatusReportReceived(int token, int messageRef, String format, byte[] pdu) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSmsStatusReportReceived(token, format, pdu);
        } catch (RemoteException e) {
            Log.m110e(LOG_TAG, "Can not process sms status report: " + e.getMessage());
            acknowledgeSmsReport(token, messageRef, 2);
        }
    }

    public final void onSmsStatusReportReceived(int token, String format, byte[] pdu) throws RuntimeException {
        IImsSmsListener listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener == null) {
            throw new RuntimeException("Feature not ready.");
        }
        try {
            listener.onSmsStatusReportReceived(token, format, pdu);
        } catch (RemoteException e) {
            Log.m110e(LOG_TAG, "Can not process sms status report: " + e.getMessage());
            SmsMessage message = SmsMessage.createFromPdu(pdu, format);
            if (message != null && message.mWrappedSmsMessage != null) {
                acknowledgeSmsReport(token, message.mWrappedSmsMessage.mMessageRef, 2);
                return;
            }
            Log.m104w(LOG_TAG, "onSmsStatusReportReceived: Invalid pdu entered.");
            acknowledgeSmsReport(token, 0, 2);
        }
    }

    public String getSmsFormat() {
        return "3gpp";
    }

    public void onReady() {
    }

    public final void setDefaultExecutor(Executor executor) {
        if (this.mExecutor == null) {
            this.mExecutor = executor;
        }
    }

    public Executor getExecutor() {
        Executor executor = this.mExecutor;
        return executor != null ? executor : new PendingIntent$$ExternalSyntheticLambda1();
    }
}
