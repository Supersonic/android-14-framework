package com.android.internal.telephony.ims;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.ims.aidl.IImsServiceController;
import android.telephony.ims.stub.ImsFeatureConfiguration;
import android.util.Log;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class ImsServiceFeatureQueryManager {
    private final Context mContext;
    private final Listener mListener;
    private final Map<ComponentName, ImsServiceFeatureQuery> mActiveQueries = new HashMap();
    private final Object mLock = new Object();

    /* loaded from: classes.dex */
    public interface Listener {
        void onComplete(ComponentName componentName, Set<ImsFeatureConfiguration.FeatureSlotPair> set);

        void onError(ComponentName componentName);

        void onPermanentError(ComponentName componentName);
    }

    /* loaded from: classes.dex */
    private final class ImsServiceFeatureQuery implements ServiceConnection {
        private final String mIntentFilter;
        private boolean mIsServiceConnectionDead = false;
        private final ComponentName mName;

        ImsServiceFeatureQuery(ComponentName componentName, String str) {
            this.mName = componentName;
            this.mIntentFilter = str;
        }

        public boolean start() {
            Log.d("ImsServiceFeatureQuery", "start: intent filter=" + this.mIntentFilter + ", name=" + this.mName);
            boolean bindService = ImsServiceFeatureQueryManager.this.mContext.bindService(new Intent(this.mIntentFilter).setComponent(this.mName), this, 67108929);
            if (!bindService) {
                cleanup();
            }
            return bindService;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("ImsServiceFeatureQuery", "onServiceConnected for component: " + componentName);
            if (iBinder != null) {
                queryImsFeatures(IImsServiceController.Stub.asInterface(iBinder));
                return;
            }
            Log.w("ImsServiceFeatureQuery", "onServiceConnected: " + componentName + " binder null.");
            cleanup();
            ImsServiceFeatureQueryManager.this.mListener.onPermanentError(componentName);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("ImsServiceFeatureQuery", "onServiceDisconnected for component: " + componentName);
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            this.mIsServiceConnectionDead = true;
            Log.w("ImsServiceFeatureQuery", "onBindingDied: " + componentName);
            cleanup();
            ImsServiceFeatureQueryManager.this.mListener.onError(componentName);
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            Log.w("ImsServiceFeatureQuery", "onNullBinding: " + componentName);
            if (this.mIsServiceConnectionDead) {
                return;
            }
            cleanup();
            ImsServiceFeatureQueryManager.this.mListener.onPermanentError(componentName);
        }

        private void queryImsFeatures(IImsServiceController iImsServiceController) {
            Set<ImsFeatureConfiguration.FeatureSlotPair> serviceFeatures;
            try {
                ImsFeatureConfiguration querySupportedImsFeatures = iImsServiceController.querySupportedImsFeatures();
                if (querySupportedImsFeatures == null) {
                    serviceFeatures = Collections.emptySet();
                } else {
                    serviceFeatures = querySupportedImsFeatures.getServiceFeatures();
                }
                cleanup();
                ImsServiceFeatureQueryManager.this.mListener.onComplete(this.mName, serviceFeatures);
            } catch (Exception e) {
                Log.w("ImsServiceFeatureQuery", "queryImsFeatures - error: " + e);
                cleanup();
                ImsServiceFeatureQueryManager.this.mListener.onError(this.mName);
            }
        }

        private void cleanup() {
            ImsServiceFeatureQueryManager.this.mContext.unbindService(this);
            synchronized (ImsServiceFeatureQueryManager.this.mLock) {
                ImsServiceFeatureQueryManager.this.mActiveQueries.remove(this.mName);
            }
        }
    }

    public ImsServiceFeatureQueryManager(Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public boolean startQuery(ComponentName componentName, String str) {
        synchronized (this.mLock) {
            if (this.mActiveQueries.containsKey(componentName)) {
                return true;
            }
            ImsServiceFeatureQuery imsServiceFeatureQuery = new ImsServiceFeatureQuery(componentName, str);
            this.mActiveQueries.put(componentName, imsServiceFeatureQuery);
            return imsServiceFeatureQuery.start();
        }
    }

    public boolean isQueryInProgress() {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mActiveQueries.isEmpty();
        }
        return z;
    }
}
