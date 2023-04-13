package android.telephony;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Message;
import android.p008os.Messenger;
import android.p008os.RemoteException;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
/* loaded from: classes3.dex */
public abstract class VisualVoicemailService extends Service {
    public static final String DATA_PHONE_ACCOUNT_HANDLE = "data_phone_account_handle";
    public static final String DATA_SMS = "data_sms";
    public static final int MSG_ON_CELL_SERVICE_CONNECTED = 1;
    public static final int MSG_ON_SIM_REMOVED = 3;
    public static final int MSG_ON_SMS_RECEIVED = 2;
    public static final int MSG_TASK_ENDED = 4;
    public static final int MSG_TASK_STOPPED = 5;
    public static final String SERVICE_INTERFACE = "android.telephony.VisualVoicemailService";
    private static final String TAG = "VvmService";
    private final Messenger mMessenger = new Messenger(new Handler() { // from class: android.telephony.VisualVoicemailService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            PhoneAccountHandle handle = (PhoneAccountHandle) msg.getData().getParcelable(VisualVoicemailService.DATA_PHONE_ACCOUNT_HANDLE, PhoneAccountHandle.class);
            VisualVoicemailTask task = new VisualVoicemailTask(msg.replyTo, msg.arg1);
            switch (msg.what) {
                case 1:
                    VisualVoicemailService.this.onCellServiceConnected(task, handle);
                    return;
                case 2:
                    VisualVoicemailSms sms = (VisualVoicemailSms) msg.getData().getParcelable(VisualVoicemailService.DATA_SMS, VisualVoicemailSms.class);
                    VisualVoicemailService.this.onSmsReceived(task, sms);
                    return;
                case 3:
                    VisualVoicemailService.this.onSimRemoved(task, handle);
                    return;
                case 4:
                default:
                    super.handleMessage(msg);
                    return;
                case 5:
                    VisualVoicemailService.this.onStopped(task);
                    return;
            }
        }
    });

    public abstract void onCellServiceConnected(VisualVoicemailTask visualVoicemailTask, PhoneAccountHandle phoneAccountHandle);

    public abstract void onSimRemoved(VisualVoicemailTask visualVoicemailTask, PhoneAccountHandle phoneAccountHandle);

    public abstract void onSmsReceived(VisualVoicemailTask visualVoicemailTask, VisualVoicemailSms visualVoicemailSms);

    public abstract void onStopped(VisualVoicemailTask visualVoicemailTask);

    /* loaded from: classes3.dex */
    public static class VisualVoicemailTask {
        private final Messenger mReplyTo;
        private final int mTaskId;

        private VisualVoicemailTask(Messenger replyTo, int taskId) {
            this.mTaskId = taskId;
            this.mReplyTo = replyTo;
        }

        public final void finish() {
            Message message = Message.obtain();
            try {
                message.what = 4;
                message.arg1 = this.mTaskId;
                this.mReplyTo.send(message);
            } catch (RemoteException e) {
                Log.m110e(VisualVoicemailService.TAG, "Cannot send MSG_TASK_ENDED, remote handler no longer exist");
            }
        }

        public boolean equals(Object obj) {
            return (obj instanceof VisualVoicemailTask) && this.mTaskId == ((VisualVoicemailTask) obj).mTaskId;
        }

        public int hashCode() {
            return this.mTaskId;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }

    @SystemApi
    public static final void setSmsFilterSettings(Context context, PhoneAccountHandle phoneAccountHandle, VisualVoicemailSmsFilterSettings settings) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        int subId = getSubId(context, phoneAccountHandle);
        if (settings == null) {
            telephonyManager.disableVisualVoicemailSmsFilter(subId);
        } else {
            telephonyManager.enableVisualVoicemailSmsFilter(subId, settings);
        }
    }

    @SystemApi
    public static final void sendVisualVoicemailSms(Context context, PhoneAccountHandle phoneAccountHandle, String number, short port, String text, PendingIntent sentIntent) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        telephonyManager.sendVisualVoicemailSmsForSubscriber(getSubId(context, phoneAccountHandle), number, port, text, sentIntent);
    }

    private static int getSubId(Context context, PhoneAccountHandle phoneAccountHandle) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(TelecomManager.class);
        return telephonyManager.getSubIdForPhoneAccount(telecomManager.getPhoneAccount(phoneAccountHandle));
    }
}
