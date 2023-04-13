package android.telephony.ims.compat;

import android.app.Service;
import android.content.Intent;
import android.p008os.IBinder;
import android.telephony.ims.compat.feature.ImsFeature;
import android.telephony.ims.compat.feature.MMTelFeature;
import android.telephony.ims.compat.feature.RcsFeature;
import android.util.Log;
import android.util.SparseArray;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.ims.internal.IImsMMTelFeature;
import com.android.ims.internal.IImsRcsFeature;
import com.android.ims.internal.IImsServiceController;
/* loaded from: classes3.dex */
public class ImsService extends Service {
    private static final String LOG_TAG = "ImsService(Compat)";
    public static final String SERVICE_INTERFACE = "android.telephony.ims.compat.ImsService";
    private final SparseArray<SparseArray<ImsFeature>> mFeaturesBySlot = new SparseArray<>();
    protected final IBinder mImsServiceController = new IImsServiceController.Stub() { // from class: android.telephony.ims.compat.ImsService.1
        @Override // com.android.ims.internal.IImsServiceController
        public IImsMMTelFeature createEmergencyMMTelFeature(int slotId) {
            return ImsService.this.createEmergencyMMTelFeatureInternal(slotId);
        }

        @Override // com.android.ims.internal.IImsServiceController
        public IImsMMTelFeature createMMTelFeature(int slotId) {
            return ImsService.this.createMMTelFeatureInternal(slotId);
        }

        @Override // com.android.ims.internal.IImsServiceController
        public IImsRcsFeature createRcsFeature(int slotId) {
            return ImsService.this.createRcsFeatureInternal(slotId);
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void removeImsFeature(int slotId, int featureType) {
            ImsService.this.removeImsFeature(slotId, featureType);
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void addFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
            ImsService.this.addImsFeatureStatusCallback(slotId, featureType, c);
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void removeFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
            ImsService.this.removeImsFeatureStatusCallback(slotId, featureType, c);
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            Log.m108i(LOG_TAG, "ImsService(Compat) Bound.");
            return this.mImsServiceController;
        }
        return null;
    }

    public SparseArray<ImsFeature> getFeatures(int slotId) {
        return this.mFeaturesBySlot.get(slotId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsMMTelFeature createEmergencyMMTelFeatureInternal(int slotId) {
        MMTelFeature f = onCreateEmergencyMMTelImsFeature(slotId);
        if (f != null) {
            setupFeature(f, slotId, 0);
            return f.getBinder();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsMMTelFeature createMMTelFeatureInternal(int slotId) {
        MMTelFeature f = onCreateMMTelImsFeature(slotId);
        if (f != null) {
            setupFeature(f, slotId, 1);
            return f.getBinder();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsRcsFeature createRcsFeatureInternal(int slotId) {
        RcsFeature f = onCreateRcsFeature(slotId);
        if (f != null) {
            setupFeature(f, slotId, 2);
            return f.getBinder();
        }
        return null;
    }

    private void setupFeature(ImsFeature f, int slotId, int featureType) {
        f.setContext(this);
        f.setSlotId(slotId);
        addImsFeature(slotId, featureType, f);
        f.onFeatureReady();
    }

    private void addImsFeature(int slotId, int featureType, ImsFeature f) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                features = new SparseArray<>();
                this.mFeaturesBySlot.put(slotId, features);
            }
            features.put(featureType, f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addImsFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not add ImsFeatureStatusCallback. No ImsFeatures exist on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f != null) {
                f.addImsFeatureStatusCallback(c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeImsFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeatureStatusCallback. No ImsFeatures exist on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f != null) {
                f.removeImsFeatureStatusCallback(c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeImsFeature(int slotId, int featureType) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeature. No ImsFeatures exist on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeature. No feature with type " + featureType + " exists on slot " + slotId);
                return;
            }
            f.onFeatureRemoved();
            features.remove(featureType);
        }
    }

    public MMTelFeature onCreateEmergencyMMTelImsFeature(int slotId) {
        return null;
    }

    public MMTelFeature onCreateMMTelImsFeature(int slotId) {
        return null;
    }

    public RcsFeature onCreateRcsFeature(int slotId) {
        return null;
    }
}
