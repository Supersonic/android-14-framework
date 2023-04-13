package android.app.usage;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.usage.ICacheQuotaService;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteCallback;
import android.util.Log;
import android.util.Pair;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public abstract class CacheQuotaService extends Service {
    public static final String REQUEST_LIST_KEY = "requests";
    public static final String SERVICE_INTERFACE = "android.app.usage.CacheQuotaService";
    private static final String TAG = "CacheQuotaService";
    private Handler mHandler;
    private CacheQuotaServiceWrapper mWrapper;

    public abstract List<CacheQuotaHint> onComputeCacheQuotaHints(List<CacheQuotaHint> list);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWrapper = new CacheQuotaServiceWrapper();
        this.mHandler = new ServiceHandler(getMainLooper());
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    /* loaded from: classes.dex */
    private final class CacheQuotaServiceWrapper extends ICacheQuotaService.Stub {
        private CacheQuotaServiceWrapper() {
        }

        @Override // android.app.usage.ICacheQuotaService
        public void computeCacheQuotaHints(RemoteCallback callback, List<CacheQuotaHint> requests) {
            Pair<RemoteCallback, List<CacheQuotaHint>> pair = Pair.create(callback, requests);
            Message msg = CacheQuotaService.this.mHandler.obtainMessage(1, pair);
            CacheQuotaService.this.mHandler.sendMessage(msg);
        }
    }

    /* loaded from: classes.dex */
    private final class ServiceHandler extends Handler {
        public static final int MSG_SEND_LIST = 1;

        public ServiceHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            int action = msg.what;
            switch (action) {
                case 1:
                    Pair<RemoteCallback, List<CacheQuotaHint>> pair = (Pair) msg.obj;
                    List<CacheQuotaHint> processed = CacheQuotaService.this.onComputeCacheQuotaHints((List) pair.second);
                    Bundle data = new Bundle();
                    data.putParcelableList(CacheQuotaService.REQUEST_LIST_KEY, processed);
                    RemoteCallback callback = (RemoteCallback) pair.first;
                    callback.sendResult(data);
                    return;
                default:
                    Log.m104w(CacheQuotaService.TAG, "Handling unknown message: " + action);
                    return;
            }
        }
    }
}
