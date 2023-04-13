package android.service.tracing;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Messenger;
import android.p008os.ParcelFileDescriptor;
import android.tracing.TraceReportParams;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;
@SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
/* loaded from: classes3.dex */
public class TraceReportService extends Service {
    public static final int MSG_REPORT_TRACE = 1;
    private static final String TAG = "TraceReportService";
    private Messenger mMessenger = null;

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    /* loaded from: classes3.dex */
    public static final class TraceParams {
        private final ParcelFileDescriptor mFd;
        private final UUID mUuid;

        private TraceParams(TraceReportParams params) {
            this.mFd = params.f464fd;
            this.mUuid = new UUID(params.uuidMsb, params.uuidLsb);
        }

        public ParcelFileDescriptor getFd() {
            return this.mFd;
        }

        public UUID getUuid() {
            return this.mUuid;
        }
    }

    public void onReportTrace(TraceParams args) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onMessage(Message msg) {
        if (msg.what == 1) {
            if (!(msg.obj instanceof TraceReportParams)) {
                Log.m110e(TAG, "Received invalid type for report trace message.");
                return false;
            }
            TraceParams params = new TraceParams((TraceReportParams) msg.obj);
            try {
                onReportTrace(params);
                return true;
            } finally {
                try {
                    params.getFd().close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mMessenger == null) {
            this.mMessenger = new Messenger(new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: android.service.tracing.TraceReportService$$ExternalSyntheticLambda0
                @Override // android.p008os.Handler.Callback
                public final boolean handleMessage(Message message) {
                    boolean onMessage;
                    onMessage = TraceReportService.this.onMessage(message);
                    return onMessage;
                }
            }));
        }
        return this.mMessenger.getBinder();
    }
}
