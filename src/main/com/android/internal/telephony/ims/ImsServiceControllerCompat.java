package com.android.internal.telephony.ims;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsRcsFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.ISipTransport;
import android.util.Log;
import android.util.SparseArray;
import com.android.ims.ImsFeatureBinderRepository;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.ims.internal.IImsMMTelFeature;
import com.android.ims.internal.IImsServiceController;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.ims.ImsServiceController;
/* loaded from: classes.dex */
public class ImsServiceControllerCompat extends ImsServiceController {
    private final SparseArray<ImsConfigCompatAdapter> mConfigCompatAdapters;
    private final SparseArray<MmTelFeatureCompatAdapter> mMmTelCompatAdapters;
    private final MmTelFeatureCompatFactory mMmTelFeatureFactory;
    private final SparseArray<ImsRegistrationCompatAdapter> mRegCompatAdapters;
    private IImsServiceController mServiceController;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface MmTelFeatureCompatFactory {
        MmTelFeatureCompatAdapter create(Context context, int i, MmTelInterfaceAdapter mmTelInterfaceAdapter);
    }

    private IImsRcsFeature createRcsFeature(int i) {
        return null;
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected final String getServiceInterface() {
        return "android.telephony.ims.compat.ImsService";
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    public ISipTransport getSipTransport(int i) {
        return null;
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected long getStaticServiceCapabilities() {
        return 0L;
    }

    public ImsServiceControllerCompat(Context context, ComponentName componentName, ImsServiceController.ImsServiceControllerCallbacks imsServiceControllerCallbacks, ImsFeatureBinderRepository imsFeatureBinderRepository) {
        super(context, componentName, imsServiceControllerCallbacks, imsFeatureBinderRepository);
        this.mMmTelCompatAdapters = new SparseArray<>();
        this.mConfigCompatAdapters = new SparseArray<>();
        this.mRegCompatAdapters = new SparseArray<>();
        this.mMmTelFeatureFactory = new MmTelFeatureCompatFactory() { // from class: com.android.internal.telephony.ims.ImsServiceControllerCompat$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.ims.ImsServiceControllerCompat.MmTelFeatureCompatFactory
            public final MmTelFeatureCompatAdapter create(Context context2, int i, MmTelInterfaceAdapter mmTelInterfaceAdapter) {
                return new MmTelFeatureCompatAdapter(context2, i, mmTelInterfaceAdapter);
            }
        };
    }

    @VisibleForTesting
    public ImsServiceControllerCompat(Context context, ComponentName componentName, ImsServiceController.ImsServiceControllerCallbacks imsServiceControllerCallbacks, Handler handler, ImsServiceController.RebindRetry rebindRetry, ImsFeatureBinderRepository imsFeatureBinderRepository, MmTelFeatureCompatFactory mmTelFeatureCompatFactory) {
        super(context, componentName, imsServiceControllerCallbacks, handler, rebindRetry, imsFeatureBinderRepository);
        this.mMmTelCompatAdapters = new SparseArray<>();
        this.mConfigCompatAdapters = new SparseArray<>();
        this.mRegCompatAdapters = new SparseArray<>();
        this.mMmTelFeatureFactory = mmTelFeatureCompatFactory;
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    public final void enableIms(int i, int i2) {
        MmTelFeatureCompatAdapter mmTelFeatureCompatAdapter = this.mMmTelCompatAdapters.get(i);
        if (mmTelFeatureCompatAdapter == null) {
            Log.w("ImsSCCompat", "enableIms: adapter null for slot :" + i);
            return;
        }
        try {
            mmTelFeatureCompatAdapter.enableIms();
        } catch (RemoteException e) {
            Log.w("ImsSCCompat", "Couldn't enable IMS: " + e.getMessage());
        }
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    public final void disableIms(int i, int i2) {
        MmTelFeatureCompatAdapter mmTelFeatureCompatAdapter = this.mMmTelCompatAdapters.get(i);
        if (mmTelFeatureCompatAdapter == null) {
            Log.w("ImsSCCompat", "enableIms: adapter null for slot :" + i);
            return;
        }
        try {
            mmTelFeatureCompatAdapter.disableIms();
        } catch (RemoteException e) {
            Log.w("ImsSCCompat", "Couldn't enable IMS: " + e.getMessage());
        }
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    public final IImsRegistration getRegistration(int i, int i2) {
        ImsRegistrationCompatAdapter imsRegistrationCompatAdapter = this.mRegCompatAdapters.get(i);
        if (imsRegistrationCompatAdapter == null) {
            Log.w("ImsSCCompat", "getRegistration: Registration does not exist for slot " + i);
            return null;
        }
        return imsRegistrationCompatAdapter.getBinder();
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    public final IImsConfig getConfig(int i, int i2) {
        ImsConfigCompatAdapter imsConfigCompatAdapter = this.mConfigCompatAdapters.get(i);
        if (imsConfigCompatAdapter == null) {
            Log.w("ImsSCCompat", "getConfig: Config does not exist for slot " + i);
            return null;
        }
        return imsConfigCompatAdapter.getIImsConfig();
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected final void notifyImsServiceReady() {
        Log.d("ImsSCCompat", "notifyImsServiceReady");
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected final IInterface createImsFeature(int i, int i2, int i3, long j) throws RemoteException {
        if (i3 != 1) {
            if (i3 != 2) {
                return null;
            }
            return createRcsFeature(i);
        }
        return createMMTelCompat(i);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected void registerImsFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException {
        this.mServiceController.addFeatureStatusCallback(i, i2, iImsFeatureStatusCallback);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected void unregisterImsFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) {
        try {
            this.mServiceController.removeFeatureStatusCallback(i, i2, iImsFeatureStatusCallback);
        } catch (RemoteException unused) {
            Log.w("ImsSCCompat", "compat - unregisterImsFeatureStatusCallback - couldn't remove " + iImsFeatureStatusCallback);
        }
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected final void removeImsFeature(int i, int i2, boolean z) throws RemoteException {
        if (i2 == 1) {
            MmTelFeatureCompatAdapter mmTelFeatureCompatAdapter = this.mMmTelCompatAdapters.get(i, null);
            if (mmTelFeatureCompatAdapter != null) {
                mmTelFeatureCompatAdapter.onFeatureRemoved();
            }
            this.mMmTelCompatAdapters.remove(i);
            this.mRegCompatAdapters.remove(i);
            this.mConfigCompatAdapters.remove(i);
        }
        IImsServiceController iImsServiceController = this.mServiceController;
        if (iImsServiceController != null) {
            iImsServiceController.removeImsFeature(i, i2);
        }
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected void setServiceController(IBinder iBinder) {
        this.mServiceController = IImsServiceController.Stub.asInterface(iBinder);
    }

    @Override // com.android.internal.telephony.ims.ImsServiceController
    protected boolean isServiceControllerAvailable() {
        return this.mServiceController != null;
    }

    private MmTelInterfaceAdapter getInterface(int i) throws RemoteException {
        IImsMMTelFeature createMMTelFeature = this.mServiceController.createMMTelFeature(i);
        if (createMMTelFeature == null) {
            Log.w("ImsSCCompat", "createMMTelCompat: createMMTelFeature returned null.");
            return null;
        }
        return new MmTelInterfaceAdapter(i, createMMTelFeature.asBinder());
    }

    private IImsMmTelFeature createMMTelCompat(int i) throws RemoteException {
        MmTelFeatureCompatAdapter create = this.mMmTelFeatureFactory.create(this.mContext, i, getInterface(i));
        this.mMmTelCompatAdapters.put(i, create);
        ImsRegistrationCompatAdapter imsRegistrationCompatAdapter = new ImsRegistrationCompatAdapter();
        create.addRegistrationAdapter(imsRegistrationCompatAdapter);
        this.mRegCompatAdapters.put(i, imsRegistrationCompatAdapter);
        this.mConfigCompatAdapters.put(i, new ImsConfigCompatAdapter(create.getOldConfigInterface()));
        return create.getBinder();
    }
}
