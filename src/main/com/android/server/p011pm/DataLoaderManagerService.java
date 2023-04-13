package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.DataLoaderParamsParcel;
import android.content.pm.IDataLoader;
import android.content.pm.IDataLoaderManager;
import android.content.pm.IDataLoaderStatusListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.SystemService;
import com.android.server.p011pm.DataLoaderManagerService;
import java.util.List;
/* renamed from: com.android.server.pm.DataLoaderManagerService */
/* loaded from: classes2.dex */
public class DataLoaderManagerService extends SystemService {
    public final DataLoaderManagerBinderService mBinderService;
    public final Context mContext;
    public final Handler mHandler;
    public SparseArray<DataLoaderServiceConnection> mServiceConnections;
    public final HandlerThread mThread;

    public DataLoaderManagerService(Context context) {
        super(context);
        this.mServiceConnections = new SparseArray<>();
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("DataLoaderManager");
        this.mThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        this.mBinderService = new DataLoaderManagerBinderService();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("dataloader_manager", this.mBinderService);
    }

    /* renamed from: com.android.server.pm.DataLoaderManagerService$DataLoaderManagerBinderService */
    /* loaded from: classes2.dex */
    public final class DataLoaderManagerBinderService extends IDataLoaderManager.Stub {
        public DataLoaderManagerBinderService() {
        }

        public boolean bindToDataLoader(final int i, DataLoaderParamsParcel dataLoaderParamsParcel, long j, IDataLoaderStatusListener iDataLoaderStatusListener) {
            synchronized (DataLoaderManagerService.this.mServiceConnections) {
                if (DataLoaderManagerService.this.mServiceConnections.get(i) != null) {
                    return true;
                }
                ComponentName componentName = new ComponentName(dataLoaderParamsParcel.packageName, dataLoaderParamsParcel.className);
                final ComponentName resolveDataLoaderComponentName = resolveDataLoaderComponentName(componentName);
                if (resolveDataLoaderComponentName == null) {
                    Slog.e("DataLoaderManager", "Invalid component: " + componentName + " for ID=" + i);
                    return false;
                }
                final DataLoaderServiceConnection dataLoaderServiceConnection = new DataLoaderServiceConnection(i, iDataLoaderStatusListener);
                final Intent intent = new Intent();
                intent.setComponent(resolveDataLoaderComponentName);
                return DataLoaderManagerService.this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.pm.DataLoaderManagerService$DataLoaderManagerBinderService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DataLoaderManagerService.DataLoaderManagerBinderService.this.lambda$bindToDataLoader$0(intent, dataLoaderServiceConnection, resolveDataLoaderComponentName, i);
                    }
                }, j);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$bindToDataLoader$0(Intent intent, DataLoaderServiceConnection dataLoaderServiceConnection, ComponentName componentName, int i) {
            if (DataLoaderManagerService.this.mContext.bindServiceAsUser(intent, dataLoaderServiceConnection, 1, DataLoaderManagerService.this.mHandler, UserHandle.of(UserHandle.getCallingUserId()))) {
                return;
            }
            Slog.e("DataLoaderManager", "Failed to bind to: " + componentName + " for ID=" + i);
            DataLoaderManagerService.this.mContext.unbindService(dataLoaderServiceConnection);
        }

        public final ComponentName resolveDataLoaderComponentName(ComponentName componentName) {
            PackageManager packageManager = DataLoaderManagerService.this.mContext.getPackageManager();
            if (packageManager == null) {
                Slog.e("DataLoaderManager", "PackageManager is not available.");
                return null;
            }
            Intent intent = new Intent("android.intent.action.LOAD_DATA");
            intent.setComponent(componentName);
            List queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(intent, 0, UserHandle.getCallingUserId());
            if (queryIntentServicesAsUser == null || queryIntentServicesAsUser.isEmpty()) {
                Slog.e("DataLoaderManager", "Failed to find data loader service provider in " + componentName);
                return null;
            } else if (queryIntentServicesAsUser.size() > 0) {
                ServiceInfo serviceInfo = ((ResolveInfo) queryIntentServicesAsUser.get(0)).serviceInfo;
                return new ComponentName(serviceInfo.packageName, serviceInfo.name);
            } else {
                Slog.e("DataLoaderManager", "Didn't find any matching data loader service provider.");
                return null;
            }
        }

        public IDataLoader getDataLoader(int i) {
            synchronized (DataLoaderManagerService.this.mServiceConnections) {
                DataLoaderServiceConnection dataLoaderServiceConnection = (DataLoaderServiceConnection) DataLoaderManagerService.this.mServiceConnections.get(i, null);
                if (dataLoaderServiceConnection == null) {
                    return null;
                }
                return dataLoaderServiceConnection.getDataLoader();
            }
        }

        public void unbindFromDataLoader(int i) {
            synchronized (DataLoaderManagerService.this.mServiceConnections) {
                DataLoaderServiceConnection dataLoaderServiceConnection = (DataLoaderServiceConnection) DataLoaderManagerService.this.mServiceConnections.get(i, null);
                if (dataLoaderServiceConnection == null) {
                    return;
                }
                dataLoaderServiceConnection.destroy();
            }
        }
    }

    /* renamed from: com.android.server.pm.DataLoaderManagerService$DataLoaderServiceConnection */
    /* loaded from: classes2.dex */
    public class DataLoaderServiceConnection implements ServiceConnection, IBinder.DeathRecipient {
        public IDataLoader mDataLoader = null;
        public final int mId;
        public final IDataLoaderStatusListener mListener;

        public DataLoaderServiceConnection(int i, IDataLoaderStatusListener iDataLoaderStatusListener) {
            this.mId = i;
            this.mListener = iDataLoaderStatusListener;
            callListener(1);
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IDataLoader asInterface = IDataLoader.Stub.asInterface(iBinder);
            this.mDataLoader = asInterface;
            if (asInterface == null) {
                onNullBinding(componentName);
            } else if (!append()) {
                DataLoaderManagerService.this.mContext.unbindService(this);
            } else {
                try {
                    iBinder.linkToDeath(this, 0);
                    callListener(2);
                } catch (RemoteException e) {
                    Slog.e("DataLoaderManager", "Failed to link to DataLoader's death: " + this.mId, e);
                    onBindingDied(componentName);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Slog.i("DataLoaderManager", "DataLoader " + this.mId + " disconnected, but will try to recover");
            unbindAndReportDestroyed();
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            Slog.i("DataLoaderManager", "DataLoader " + this.mId + " died");
            unbindAndReportDestroyed();
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            Slog.i("DataLoaderManager", "DataLoader " + this.mId + " failed to start");
            unbindAndReportDestroyed();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.i("DataLoaderManager", "DataLoader " + this.mId + " died");
            unbindAndReportDestroyed();
        }

        public IDataLoader getDataLoader() {
            return this.mDataLoader;
        }

        public final void unbindAndReportDestroyed() {
            if (unbind()) {
                callListener(0);
            }
        }

        public void destroy() {
            IDataLoader iDataLoader = this.mDataLoader;
            if (iDataLoader != null) {
                try {
                    iDataLoader.destroy(this.mId);
                } catch (RemoteException unused) {
                }
                this.mDataLoader = null;
            }
            unbind();
        }

        public boolean unbind() {
            try {
                DataLoaderManagerService.this.mContext.unbindService(this);
            } catch (Exception unused) {
            }
            return remove();
        }

        public final boolean append() {
            synchronized (DataLoaderManagerService.this.mServiceConnections) {
                DataLoaderServiceConnection dataLoaderServiceConnection = (DataLoaderServiceConnection) DataLoaderManagerService.this.mServiceConnections.get(this.mId);
                if (dataLoaderServiceConnection == this) {
                    return true;
                }
                if (dataLoaderServiceConnection != null) {
                    return false;
                }
                DataLoaderManagerService.this.mServiceConnections.append(this.mId, this);
                return true;
            }
        }

        public final boolean remove() {
            synchronized (DataLoaderManagerService.this.mServiceConnections) {
                if (DataLoaderManagerService.this.mServiceConnections.get(this.mId) == this) {
                    DataLoaderManagerService.this.mServiceConnections.remove(this.mId);
                    return true;
                }
                return false;
            }
        }

        public final void callListener(int i) {
            IDataLoaderStatusListener iDataLoaderStatusListener = this.mListener;
            if (iDataLoaderStatusListener != null) {
                try {
                    iDataLoaderStatusListener.onStatusChanged(this.mId, i);
                } catch (RemoteException unused) {
                }
            }
        }
    }
}
