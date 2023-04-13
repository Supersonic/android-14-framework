package android.net;

import android.Manifest;
import android.annotation.SystemApi;
import android.content.Context;
import android.net.INetworkRecommendationProvider;
import android.p008os.Build;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes2.dex */
public abstract class NetworkRecommendationProvider {
    private static final String TAG = "NetworkRecProvider";
    private static final boolean VERBOSE;
    private final IBinder mService;

    public abstract void onRequestScores(NetworkKey[] networkKeyArr);

    static {
        VERBOSE = Build.IS_DEBUGGABLE && Log.isLoggable(TAG, 2);
    }

    public NetworkRecommendationProvider(Context context, Executor executor) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(executor);
        this.mService = new ServiceWrapper(context, executor);
    }

    public final IBinder getBinder() {
        return this.mService;
    }

    /* loaded from: classes2.dex */
    private final class ServiceWrapper extends INetworkRecommendationProvider.Stub {
        private final Context mContext;
        private final Executor mExecutor;
        private final Handler mHandler = null;

        ServiceWrapper(Context context, Executor executor) {
            this.mContext = context;
            this.mExecutor = executor;
        }

        @Override // android.net.INetworkRecommendationProvider
        public void requestScores(final NetworkKey[] networks) throws RemoteException {
            enforceCallingPermission();
            if (networks != null && networks.length > 0) {
                execute(new Runnable() { // from class: android.net.NetworkRecommendationProvider.ServiceWrapper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NetworkRecommendationProvider.this.onRequestScores(networks);
                    }
                });
            }
        }

        private void execute(Runnable command) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(command);
            } else {
                this.mHandler.post(command);
            }
        }

        private void enforceCallingPermission() {
            Context context = this.mContext;
            if (context != null) {
                context.enforceCallingOrSelfPermission(Manifest.C0000permission.REQUEST_NETWORK_SCORES, "Permission denied.");
            }
        }
    }
}
