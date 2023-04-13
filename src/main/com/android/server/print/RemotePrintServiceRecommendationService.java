package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.printservice.recommendation.IRecommendationService;
import android.printservice.recommendation.IRecommendationServiceCallbacks;
import android.printservice.recommendation.RecommendationInfo;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import java.util.List;
/* loaded from: classes2.dex */
public class RemotePrintServiceRecommendationService {
    @GuardedBy({"mLock"})
    public final Connection mConnection;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public boolean mIsBound;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public IRecommendationService mService;

    /* loaded from: classes2.dex */
    public interface RemotePrintServiceRecommendationServiceCallbacks {
        void onPrintServiceRecommendationsUpdated(List<RecommendationInfo> list);
    }

    public final Intent getServiceIntent(UserHandle userHandle) throws Exception {
        List queryIntentServicesAsUser = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.printservice.recommendation.RecommendationService"), 268435588, userHandle.getIdentifier());
        if (queryIntentServicesAsUser.size() != 1) {
            throw new Exception(queryIntentServicesAsUser.size() + " instead of exactly one service found");
        }
        ResolveInfo resolveInfo = (ResolveInfo) queryIntentServicesAsUser.get(0);
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(resolveInfo.serviceInfo.packageName, 0);
        if (applicationInfo == null) {
            throw new Exception("Cannot read appInfo for service");
        }
        if ((applicationInfo.flags & 1) == 0) {
            throw new Exception("Service is not part of the system");
        }
        if (!"android.permission.BIND_PRINT_RECOMMENDATION_SERVICE".equals(resolveInfo.serviceInfo.permission)) {
            throw new Exception("Service " + componentName.flattenToShortString() + " does not require permission android.permission.BIND_PRINT_RECOMMENDATION_SERVICE");
        }
        Intent intent = new Intent();
        intent.setComponent(componentName);
        return intent;
    }

    public RemotePrintServiceRecommendationService(Context context, UserHandle userHandle, RemotePrintServiceRecommendationServiceCallbacks remotePrintServiceRecommendationServiceCallbacks) {
        Object obj = new Object();
        this.mLock = obj;
        this.mContext = context;
        Connection connection = new Connection(remotePrintServiceRecommendationServiceCallbacks);
        this.mConnection = connection;
        try {
            Intent serviceIntent = getServiceIntent(userHandle);
            synchronized (obj) {
                boolean bindServiceAsUser = context.bindServiceAsUser(serviceIntent, connection, 67108865, userHandle);
                this.mIsBound = bindServiceAsUser;
                if (!bindServiceAsUser) {
                    throw new Exception("Failed to bind to service " + serviceIntent);
                }
            }
        } catch (Exception e) {
            Log.e("RemotePrintServiceRecS", "Could not connect to print service recommendation service", e);
        }
    }

    public void close() {
        synchronized (this.mLock) {
            IRecommendationService iRecommendationService = this.mService;
            if (iRecommendationService != null) {
                try {
                    iRecommendationService.registerCallbacks((IRecommendationServiceCallbacks) null);
                } catch (RemoteException e) {
                    Log.e("RemotePrintServiceRecS", "Could not unregister callbacks", e);
                }
                this.mService = null;
            }
            if (this.mIsBound) {
                this.mContext.unbindService(this.mConnection);
                this.mIsBound = false;
            }
        }
    }

    public void finalize() throws Throwable {
        if (this.mIsBound || this.mService != null) {
            Log.w("RemotePrintServiceRecS", "Service still connected on finalize()");
            close();
        }
        super.finalize();
    }

    /* loaded from: classes2.dex */
    public class Connection implements ServiceConnection {
        public final RemotePrintServiceRecommendationServiceCallbacks mCallbacks;

        public Connection(RemotePrintServiceRecommendationServiceCallbacks remotePrintServiceRecommendationServiceCallbacks) {
            this.mCallbacks = remotePrintServiceRecommendationServiceCallbacks;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (RemotePrintServiceRecommendationService.this.mLock) {
                RemotePrintServiceRecommendationService.this.mService = IRecommendationService.Stub.asInterface(iBinder);
                try {
                    RemotePrintServiceRecommendationService.this.mService.registerCallbacks(new IRecommendationServiceCallbacks.Stub() { // from class: com.android.server.print.RemotePrintServiceRecommendationService.Connection.1
                        public void onRecommendationsUpdated(List<RecommendationInfo> list) {
                            synchronized (RemotePrintServiceRecommendationService.this.mLock) {
                                if (RemotePrintServiceRecommendationService.this.mIsBound && RemotePrintServiceRecommendationService.this.mService != null) {
                                    if (list != null) {
                                        Preconditions.checkCollectionElementsNotNull(list, "recommendation");
                                    }
                                    Connection.this.mCallbacks.onPrintServiceRecommendationsUpdated(list);
                                }
                            }
                        }
                    });
                } catch (RemoteException e) {
                    Log.e("RemotePrintServiceRecS", "Could not register callbacks", e);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("RemotePrintServiceRecS", "Unexpected termination of connection");
            synchronized (RemotePrintServiceRecommendationService.this.mLock) {
                RemotePrintServiceRecommendationService.this.mService = null;
            }
        }
    }
}
