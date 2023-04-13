package android.media.p007tv.tunerresourcemanager;

import android.media.p007tv.tunerresourcemanager.IResourcesReclaimListener;
import android.media.p007tv.tunerresourcemanager.TunerResourceManager;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
/* renamed from: android.media.tv.tunerresourcemanager.TunerResourceManager */
/* loaded from: classes2.dex */
public class TunerResourceManager {
    public static final int INVALID_OWNER_ID = -1;
    public static final int INVALID_RESOURCE_HANDLE = -1;
    public static final int TUNER_RESOURCE_TYPE_CAS_SESSION = 4;
    public static final int TUNER_RESOURCE_TYPE_DEMUX = 1;
    public static final int TUNER_RESOURCE_TYPE_DESCRAMBLER = 2;
    public static final int TUNER_RESOURCE_TYPE_FRONTEND = 0;
    public static final int TUNER_RESOURCE_TYPE_FRONTEND_CICAM = 5;
    public static final int TUNER_RESOURCE_TYPE_LNB = 3;
    public static final int TUNER_RESOURCE_TYPE_MAX = 6;
    private final ITunerResourceManager mService;
    private final int mUserId;
    private static final String TAG = "TunerResourceManager";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    /* renamed from: android.media.tv.tunerresourcemanager.TunerResourceManager$ResourcesReclaimListener */
    /* loaded from: classes2.dex */
    public static abstract class ResourcesReclaimListener {
        public abstract void onReclaimResources();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tunerresourcemanager.TunerResourceManager$TunerResourceType */
    /* loaded from: classes2.dex */
    public @interface TunerResourceType {
    }

    public TunerResourceManager(ITunerResourceManager service, int userId) {
        this.mService = service;
        this.mUserId = userId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.media.tv.tunerresourcemanager.TunerResourceManager$1 */
    /* loaded from: classes2.dex */
    public class BinderC20641 extends IResourcesReclaimListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ ResourcesReclaimListener val$listener;

        BinderC20641(Executor executor, ResourcesReclaimListener resourcesReclaimListener) {
            this.val$executor = executor;
            this.val$listener = resourcesReclaimListener;
        }

        @Override // android.media.p007tv.tunerresourcemanager.IResourcesReclaimListener
        public void onReclaimResources() {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final ResourcesReclaimListener resourcesReclaimListener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.media.tv.tunerresourcemanager.TunerResourceManager$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TunerResourceManager.ResourcesReclaimListener.this.onReclaimResources();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void registerClientProfile(ResourceClientProfile profile, Executor executor, ResourcesReclaimListener listener, int[] clientId) {
        try {
            this.mService.registerClientProfile(profile, new BinderC20641(executor, listener), clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterClientProfile(int clientId) {
        try {
            this.mService.unregisterClientProfile(clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateClientPriority(int clientId, int priority, int niceValue) {
        try {
            boolean result = this.mService.updateClientPriority(clientId, priority, niceValue);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasUnusedFrontend(int frontendType) {
        try {
            boolean result = this.mService.hasUnusedFrontend(frontendType);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isLowestPriority(int clientId, int frontendType) {
        try {
            boolean result = this.mService.isLowestPriority(clientId, frontendType);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void storeResourceMap(int resourceType) {
        try {
            this.mService.storeResourceMap(resourceType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearResourceMap(int resourceType) {
        try {
            this.mService.clearResourceMap(resourceType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void restoreResourceMap(int resourceType) {
        try {
            this.mService.restoreResourceMap(resourceType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setFrontendInfoList(TunerFrontendInfo[] infos) {
        try {
            this.mService.setFrontendInfoList(infos);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setDemuxInfoList(TunerDemuxInfo[] infos) {
        try {
            this.mService.setDemuxInfoList(infos);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateCasInfo(int casSystemId, int maxSessionNum) {
        try {
            this.mService.updateCasInfo(casSystemId, maxSessionNum);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setLnbInfoList(int[] lnbIds) {
        try {
            this.mService.setLnbInfoList(lnbIds);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean acquireLock(int clientId) {
        try {
            return this.mService.acquireLock(clientId, Thread.currentThread().getId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean releaseLock(int clientId) {
        try {
            return this.mService.releaseLock(clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestFrontend(TunerFrontendRequest request, int[] frontendHandle) {
        try {
            boolean result = this.mService.requestFrontend(request, frontendHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setMaxNumberOfFrontends(int frontendType, int maxNum) {
        try {
            boolean result = this.mService.setMaxNumberOfFrontends(frontendType, maxNum);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getMaxNumberOfFrontends(int frontendType) {
        try {
            int result = this.mService.getMaxNumberOfFrontends(frontendType);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void shareFrontend(int selfClientId, int targetClientId) {
        try {
            this.mService.shareFrontend(selfClientId, targetClientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean transferOwner(int resourceType, int currentOwnerId, int newOwnerId) {
        try {
            return this.mService.transferOwner(resourceType, currentOwnerId, newOwnerId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestDemux(TunerDemuxRequest request, int[] demuxHandle) {
        try {
            boolean result = this.mService.requestDemux(request, demuxHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestDescrambler(TunerDescramblerRequest request, int[] descramblerHandle) {
        try {
            boolean result = this.mService.requestDescrambler(request, descramblerHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestCasSession(CasSessionRequest request, int[] casSessionHandle) {
        try {
            boolean result = this.mService.requestCasSession(request, casSessionHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestCiCam(TunerCiCamRequest request, int[] ciCamHandle) {
        try {
            boolean result = this.mService.requestCiCam(request, ciCamHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestLnb(TunerLnbRequest request, int[] lnbHandle) {
        try {
            boolean result = this.mService.requestLnb(request, lnbHandle);
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseFrontend(int frontendHandle, int clientId) {
        try {
            this.mService.releaseFrontend(frontendHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseDemux(int demuxHandle, int clientId) {
        try {
            this.mService.releaseDemux(demuxHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseDescrambler(int descramblerHandle, int clientId) {
        try {
            this.mService.releaseDescrambler(descramblerHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseCasSession(int casSessionHandle, int clientId) {
        try {
            this.mService.releaseCasSession(casSessionHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseCiCam(int ciCamHandle, int clientId) {
        try {
            this.mService.releaseCiCam(ciCamHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void releaseLnb(int lnbHandle, int clientId) {
        try {
            this.mService.releaseLnb(lnbHandle, clientId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isHigherPriority(ResourceClientProfile challengerProfile, ResourceClientProfile holderProfile) {
        try {
            return this.mService.isHigherPriority(challengerProfile, holderProfile);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getClientPriority(int useCase, int pid) {
        try {
            return this.mService.getClientPriority(useCase, pid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getConfigPriority(int useCase, boolean isForeground) {
        try {
            return this.mService.getConfigPriority(useCase, isForeground);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
