package com.android.server.p012tv.tunerresourcemanager;

import android.app.ActivityManager;
import android.content.Context;
import android.media.IResourceManagerService;
import android.media.tv.TvInputManager;
import android.media.tv.tunerresourcemanager.CasSessionRequest;
import android.media.tv.tunerresourcemanager.IResourcesReclaimListener;
import android.media.tv.tunerresourcemanager.ITunerResourceManager;
import android.media.tv.tunerresourcemanager.ResourceClientProfile;
import android.media.tv.tunerresourcemanager.TunerCiCamRequest;
import android.media.tv.tunerresourcemanager.TunerDemuxInfo;
import android.media.tv.tunerresourcemanager.TunerDemuxRequest;
import android.media.tv.tunerresourcemanager.TunerDescramblerRequest;
import android.media.tv.tunerresourcemanager.TunerFrontendInfo;
import android.media.tv.tunerresourcemanager.TunerFrontendRequest;
import android.media.tv.tunerresourcemanager.TunerLnbRequest;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.SystemService;
import com.android.server.p012tv.tunerresourcemanager.CasResource;
import com.android.server.p012tv.tunerresourcemanager.CiCamResource;
import com.android.server.p012tv.tunerresourcemanager.ClientProfile;
import com.android.server.p012tv.tunerresourcemanager.DemuxResource;
import com.android.server.p012tv.tunerresourcemanager.FrontendResource;
import com.android.server.p012tv.tunerresourcemanager.LnbResource;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/* renamed from: com.android.server.tv.tunerresourcemanager.TunerResourceManagerService */
/* loaded from: classes2.dex */
public class TunerResourceManagerService extends SystemService implements IBinder.DeathRecipient {
    public static final boolean DEBUG = Log.isLoggable("TunerResourceManagerService", 3);
    public ActivityManager mActivityManager;
    public Map<Integer, CasResource> mCasResources;
    public Map<Integer, CiCamResource> mCiCamResources;
    public Map<Integer, ClientProfile> mClientProfiles;
    public Map<Integer, DemuxResource> mDemuxResources;
    public SparseIntArray mFrontendExistingNums;
    public SparseIntArray mFrontendExistingNumsBackup;
    public SparseIntArray mFrontendMaxUsableNums;
    public SparseIntArray mFrontendMaxUsableNumsBackup;
    public Map<Integer, FrontendResource> mFrontendResources;
    public Map<Integer, FrontendResource> mFrontendResourcesBackup;
    public SparseIntArray mFrontendUsedNums;
    public SparseIntArray mFrontendUsedNumsBackup;
    @GuardedBy({"mLock"})
    public Map<Integer, ResourcesReclaimListenerRecord> mListeners;
    public Map<Integer, LnbResource> mLnbResources;
    public final Object mLock;
    public final ReentrantLock mLockForTRMSLock;
    public IResourceManagerService mMediaResourceManager;
    public int mNextUnusedClientId;
    public UseCasePriorityHints mPriorityCongfig;
    public int mResourceRequestCount;
    public int mTunerApiLockHolder;
    public long mTunerApiLockHolderThreadId;
    public int mTunerApiLockNestedCount;
    public final Condition mTunerApiLockReleasedCV;
    public TvInputManager mTvInputManager;

    @VisibleForTesting
    public int getResourceIdFromHandle(int i) {
        return i == -1 ? i : (16711680 & i) >> 16;
    }

    public final boolean validateResourceHandle(int i, int i2) {
        return i2 != -1 && (((-16777216) & i2) >> 24) == i;
    }

    public TunerResourceManagerService(Context context) {
        super(context);
        this.mClientProfiles = new HashMap();
        this.mNextUnusedClientId = 0;
        this.mFrontendResources = new HashMap();
        this.mFrontendMaxUsableNums = new SparseIntArray();
        this.mFrontendUsedNums = new SparseIntArray();
        this.mFrontendExistingNums = new SparseIntArray();
        this.mFrontendResourcesBackup = new HashMap();
        this.mFrontendMaxUsableNumsBackup = new SparseIntArray();
        this.mFrontendUsedNumsBackup = new SparseIntArray();
        this.mFrontendExistingNumsBackup = new SparseIntArray();
        this.mDemuxResources = new HashMap();
        this.mLnbResources = new HashMap();
        this.mCasResources = new HashMap();
        this.mCiCamResources = new HashMap();
        this.mListeners = new HashMap();
        this.mPriorityCongfig = new UseCasePriorityHints();
        this.mResourceRequestCount = 0;
        this.mLock = new Object();
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLockForTRMSLock = reentrantLock;
        this.mTunerApiLockReleasedCV = reentrantLock.newCondition();
        this.mTunerApiLockHolder = -1;
        this.mTunerApiLockHolderThreadId = -1L;
        this.mTunerApiLockNestedCount = 0;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        onStart(false);
    }

    @VisibleForTesting
    public void onStart(boolean z) {
        if (!z) {
            publishBinderService("tv_tuner_resource_mgr", new BinderService());
        }
        this.mTvInputManager = (TvInputManager) getContext().getSystemService("tv_input");
        this.mActivityManager = (ActivityManager) getContext().getSystemService("activity");
        this.mPriorityCongfig.parse();
        if (!z && !SystemProperties.getBoolean("ro.tuner.lazyhal", false)) {
            SystemProperties.set("tuner.server.enable", "true");
        }
        if (this.mMediaResourceManager == null) {
            IBinder binderService = getBinderService("media.resource_manager");
            if (binderService == null) {
                Slog.w("TunerResourceManagerService", "Resource Manager Service not available.");
                return;
            }
            try {
                binderService.linkToDeath(this, 0);
                this.mMediaResourceManager = IResourceManagerService.Stub.asInterface(binderService);
            } catch (RemoteException unused) {
                Slog.w("TunerResourceManagerService", "Could not link to death of native resource manager service.");
            }
        }
    }

    /* renamed from: com.android.server.tv.tunerresourcemanager.TunerResourceManagerService$BinderService */
    /* loaded from: classes2.dex */
    public final class BinderService extends ITunerResourceManager.Stub {
        public BinderService() {
        }

        public void registerClientProfile(ResourceClientProfile resourceClientProfile, IResourcesReclaimListener iResourcesReclaimListener, int[] iArr) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("registerClientProfile");
            TunerResourceManagerService.this.enforceTunerAccessPermission("registerClientProfile");
            if (resourceClientProfile == null) {
                throw new RemoteException("ResourceClientProfile can't be null");
            }
            if (iArr == null) {
                throw new RemoteException("clientId can't be null!");
            }
            if (iResourcesReclaimListener == null) {
                throw new RemoteException("IResourcesReclaimListener can't be null!");
            }
            if (!TunerResourceManagerService.this.mPriorityCongfig.isDefinedUseCase(resourceClientProfile.useCase)) {
                throw new RemoteException("Use undefined client use case:" + resourceClientProfile.useCase);
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.registerClientProfileInternal(resourceClientProfile, iResourcesReclaimListener, iArr);
            }
        }

        public void unregisterClientProfile(int i) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("unregisterClientProfile");
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i)) {
                    Slog.e("TunerResourceManagerService", "Unregistering non exists client:" + i);
                    return;
                }
                TunerResourceManagerService.this.unregisterClientProfileInternal(i);
            }
        }

        public boolean updateClientPriority(int i, int i2, int i3) {
            boolean updateClientPriorityInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("updateClientPriority");
            synchronized (TunerResourceManagerService.this.mLock) {
                updateClientPriorityInternal = TunerResourceManagerService.this.updateClientPriorityInternal(i, i2, i3);
            }
            return updateClientPriorityInternal;
        }

        public boolean hasUnusedFrontend(int i) {
            boolean hasUnusedFrontendInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("hasUnusedFrontend");
            synchronized (TunerResourceManagerService.this.mLock) {
                hasUnusedFrontendInternal = TunerResourceManagerService.this.hasUnusedFrontendInternal(i);
            }
            return hasUnusedFrontendInternal;
        }

        public boolean isLowestPriority(int i, int i2) throws RemoteException {
            boolean isLowestPriorityInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("isLowestPriority");
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i)) {
                    throw new RemoteException("isLowestPriority called from unregistered client: " + i);
                }
                isLowestPriorityInternal = TunerResourceManagerService.this.isLowestPriorityInternal(i, i2);
            }
            return isLowestPriorityInternal;
        }

        public void setFrontendInfoList(TunerFrontendInfo[] tunerFrontendInfoArr) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("setFrontendInfoList");
            if (tunerFrontendInfoArr == null) {
                throw new RemoteException("TunerFrontendInfo can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.setFrontendInfoListInternal(tunerFrontendInfoArr);
            }
        }

        public void setDemuxInfoList(TunerDemuxInfo[] tunerDemuxInfoArr) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("setDemuxInfoList");
            if (tunerDemuxInfoArr == null) {
                throw new RemoteException("TunerDemuxInfo can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.setDemuxInfoListInternal(tunerDemuxInfoArr);
            }
        }

        public void updateCasInfo(int i, int i2) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("updateCasInfo");
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.updateCasInfoInternal(i, i2);
            }
        }

        public void setLnbInfoList(int[] iArr) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("setLnbInfoList");
            if (iArr == null) {
                throw new RemoteException("Lnb handle list can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.setLnbInfoListInternal(iArr);
            }
        }

        public boolean requestFrontend(TunerFrontendRequest tunerFrontendRequest, int[] iArr) {
            TunerResourceManagerService.this.enforceTunerAccessPermission("requestFrontend");
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestFrontend");
            if (iArr == null) {
                Slog.e("TunerResourceManagerService", "frontendHandle can't be null");
                return false;
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(tunerFrontendRequest.clientId)) {
                    Slog.e("TunerResourceManagerService", "Request frontend from unregistered client: " + tunerFrontendRequest.clientId);
                    return false;
                } else if (!TunerResourceManagerService.this.getClientProfile(tunerFrontendRequest.clientId).getInUseFrontendHandles().isEmpty()) {
                    Slog.e("TunerResourceManagerService", "Release frontend before requesting another one. Client id: " + tunerFrontendRequest.clientId);
                    return false;
                } else {
                    return TunerResourceManagerService.this.requestFrontendInternal(tunerFrontendRequest, iArr);
                }
            }
        }

        public boolean setMaxNumberOfFrontends(int i, int i2) {
            boolean maxNumberOfFrontendsInternal;
            TunerResourceManagerService.this.enforceTunerAccessPermission("setMaxNumberOfFrontends");
            TunerResourceManagerService.this.enforceTrmAccessPermission("setMaxNumberOfFrontends");
            if (i2 < 0) {
                Slog.w("TunerResourceManagerService", "setMaxNumberOfFrontends failed with maxUsableNum:" + i2 + " frontendType:" + i);
                return false;
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                maxNumberOfFrontendsInternal = TunerResourceManagerService.this.setMaxNumberOfFrontendsInternal(i, i2);
            }
            return maxNumberOfFrontendsInternal;
        }

        public int getMaxNumberOfFrontends(int i) {
            int maxNumberOfFrontendsInternal;
            TunerResourceManagerService.this.enforceTunerAccessPermission("getMaxNumberOfFrontends");
            TunerResourceManagerService.this.enforceTrmAccessPermission("getMaxNumberOfFrontends");
            synchronized (TunerResourceManagerService.this.mLock) {
                maxNumberOfFrontendsInternal = TunerResourceManagerService.this.getMaxNumberOfFrontendsInternal(i);
            }
            return maxNumberOfFrontendsInternal;
        }

        public void shareFrontend(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTunerAccessPermission("shareFrontend");
            TunerResourceManagerService.this.enforceTrmAccessPermission("shareFrontend");
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i)) {
                    throw new RemoteException("Share frontend request from an unregistered client:" + i);
                } else if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Request to share frontend with an unregistered client:" + i2);
                } else if (TunerResourceManagerService.this.getClientProfile(i2).getInUseFrontendHandles().isEmpty()) {
                    throw new RemoteException("Request to share frontend with a client that has no frontend resources. Target client id:" + i2);
                } else {
                    TunerResourceManagerService.this.shareFrontendInternal(i, i2);
                }
            }
        }

        public boolean transferOwner(int i, int i2, int i3) {
            TunerResourceManagerService.this.enforceTunerAccessPermission("transferOwner");
            TunerResourceManagerService.this.enforceTrmAccessPermission("transferOwner");
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    Slog.e("TunerResourceManagerService", "currentOwnerId:" + i2 + " does not exit");
                    return false;
                } else if (!TunerResourceManagerService.this.checkClientExists(i3)) {
                    Slog.e("TunerResourceManagerService", "newOwnerId:" + i3 + " does not exit");
                    return false;
                } else {
                    return TunerResourceManagerService.this.transferOwnerInternal(i, i2, i3);
                }
            }
        }

        public boolean requestDemux(TunerDemuxRequest tunerDemuxRequest, int[] iArr) throws RemoteException {
            boolean requestDemuxInternal;
            TunerResourceManagerService.this.enforceTunerAccessPermission("requestDemux");
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestDemux");
            if (iArr == null) {
                throw new RemoteException("demuxHandle can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(tunerDemuxRequest.clientId)) {
                    throw new RemoteException("Request demux from unregistered client:" + tunerDemuxRequest.clientId);
                }
                requestDemuxInternal = TunerResourceManagerService.this.requestDemuxInternal(tunerDemuxRequest, iArr);
            }
            return requestDemuxInternal;
        }

        public boolean requestDescrambler(TunerDescramblerRequest tunerDescramblerRequest, int[] iArr) throws RemoteException {
            boolean requestDescramblerInternal;
            TunerResourceManagerService.this.enforceDescramblerAccessPermission("requestDescrambler");
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestDescrambler");
            if (iArr == null) {
                throw new RemoteException("descramblerHandle can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(tunerDescramblerRequest.clientId)) {
                    throw new RemoteException("Request descrambler from unregistered client:" + tunerDescramblerRequest.clientId);
                }
                requestDescramblerInternal = TunerResourceManagerService.this.requestDescramblerInternal(tunerDescramblerRequest, iArr);
            }
            return requestDescramblerInternal;
        }

        public boolean requestCasSession(CasSessionRequest casSessionRequest, int[] iArr) throws RemoteException {
            boolean requestCasSessionInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestCasSession");
            if (iArr == null) {
                throw new RemoteException("casSessionHandle can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(casSessionRequest.clientId)) {
                    throw new RemoteException("Request cas from unregistered client:" + casSessionRequest.clientId);
                }
                requestCasSessionInternal = TunerResourceManagerService.this.requestCasSessionInternal(casSessionRequest, iArr);
            }
            return requestCasSessionInternal;
        }

        public boolean requestCiCam(TunerCiCamRequest tunerCiCamRequest, int[] iArr) throws RemoteException {
            boolean requestCiCamInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestCiCam");
            if (iArr == null) {
                throw new RemoteException("ciCamHandle can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(tunerCiCamRequest.clientId)) {
                    throw new RemoteException("Request ciCam from unregistered client:" + tunerCiCamRequest.clientId);
                }
                requestCiCamInternal = TunerResourceManagerService.this.requestCiCamInternal(tunerCiCamRequest, iArr);
            }
            return requestCiCamInternal;
        }

        public boolean requestLnb(TunerLnbRequest tunerLnbRequest, int[] iArr) throws RemoteException {
            boolean requestLnbInternal;
            TunerResourceManagerService.this.enforceTunerAccessPermission("requestLnb");
            TunerResourceManagerService.this.enforceTrmAccessPermission("requestLnb");
            if (iArr == null) {
                throw new RemoteException("lnbHandle can't be null");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(tunerLnbRequest.clientId)) {
                    throw new RemoteException("Request lnb from unregistered client:" + tunerLnbRequest.clientId);
                }
                requestLnbInternal = TunerResourceManagerService.this.requestLnbInternal(tunerLnbRequest, iArr);
            }
            return requestLnbInternal;
        }

        public void releaseFrontend(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTunerAccessPermission("releaseFrontend");
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseFrontend");
            if (!TunerResourceManagerService.this.validateResourceHandle(0, i)) {
                throw new RemoteException("frontendHandle can't be invalid");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Release frontend from unregistered client:" + i2);
                }
                FrontendResource frontendResource = TunerResourceManagerService.this.getFrontendResource(i);
                if (frontendResource == null) {
                    throw new RemoteException("Releasing frontend does not exist.");
                }
                int ownerClientId = frontendResource.getOwnerClientId();
                ClientProfile clientProfile = TunerResourceManagerService.this.getClientProfile(ownerClientId);
                if (ownerClientId != i2 && clientProfile != null && !clientProfile.getShareFeClientIds().contains(Integer.valueOf(i2))) {
                    throw new RemoteException("Client is not the current owner of the releasing fe.");
                }
                TunerResourceManagerService.this.releaseFrontendInternal(frontendResource, i2);
            }
        }

        public void releaseDemux(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTunerAccessPermission("releaseDemux");
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseDemux");
            if (TunerResourceManagerService.DEBUG) {
                Slog.e("TunerResourceManagerService", "releaseDemux(demuxHandle=" + i + ")");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (TunerResourceManagerService.this.mDemuxResources.size() == 0) {
                    return;
                }
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Release demux for unregistered client:" + i2);
                }
                DemuxResource demuxResource = TunerResourceManagerService.this.getDemuxResource(i);
                if (demuxResource == null) {
                    throw new RemoteException("Releasing demux does not exist.");
                }
                if (demuxResource.getOwnerClientId() != i2) {
                    throw new RemoteException("Client is not the current owner of the releasing demux.");
                }
                TunerResourceManagerService.this.releaseDemuxInternal(demuxResource);
            }
        }

        public void releaseDescrambler(int i, int i2) {
            TunerResourceManagerService.this.enforceTunerAccessPermission("releaseDescrambler");
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseDescrambler");
            if (TunerResourceManagerService.DEBUG) {
                Slog.d("TunerResourceManagerService", "releaseDescrambler(descramblerHandle=" + i + ")");
            }
        }

        public void releaseCasSession(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseCasSession");
            if (!TunerResourceManagerService.this.validateResourceHandle(4, i)) {
                throw new RemoteException("casSessionHandle can't be invalid");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Release cas from unregistered client:" + i2);
                }
                CasResource casResource = TunerResourceManagerService.this.getCasResource(TunerResourceManagerService.this.getClientProfile(i2).getInUseCasSystemId());
                if (casResource == null) {
                    throw new RemoteException("Releasing cas does not exist.");
                }
                if (!casResource.getOwnerClientIds().contains(Integer.valueOf(i2))) {
                    throw new RemoteException("Client is not the current owner of the releasing cas.");
                }
                TunerResourceManagerService.this.releaseCasSessionInternal(casResource, i2);
            }
        }

        public void releaseCiCam(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseCiCam");
            if (!TunerResourceManagerService.this.validateResourceHandle(5, i)) {
                throw new RemoteException("ciCamHandle can't be invalid");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Release ciCam from unregistered client:" + i2);
                }
                int inUseCiCamId = TunerResourceManagerService.this.getClientProfile(i2).getInUseCiCamId();
                if (inUseCiCamId != TunerResourceManagerService.this.getResourceIdFromHandle(i)) {
                    throw new RemoteException("The client " + i2 + " is not the owner of the releasing ciCam.");
                }
                CiCamResource ciCamResource = TunerResourceManagerService.this.getCiCamResource(inUseCiCamId);
                if (ciCamResource == null) {
                    throw new RemoteException("Releasing ciCam does not exist.");
                }
                if (!ciCamResource.getOwnerClientIds().contains(Integer.valueOf(i2))) {
                    throw new RemoteException("Client is not the current owner of the releasing ciCam.");
                }
                TunerResourceManagerService.this.releaseCiCamInternal(ciCamResource, i2);
            }
        }

        public void releaseLnb(int i, int i2) throws RemoteException {
            TunerResourceManagerService.this.enforceTunerAccessPermission("releaseLnb");
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseLnb");
            if (!TunerResourceManagerService.this.validateResourceHandle(3, i)) {
                throw new RemoteException("lnbHandle can't be invalid");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                if (!TunerResourceManagerService.this.checkClientExists(i2)) {
                    throw new RemoteException("Release lnb from unregistered client:" + i2);
                }
                LnbResource lnbResource = TunerResourceManagerService.this.getLnbResource(i);
                if (lnbResource == null) {
                    throw new RemoteException("Releasing lnb does not exist.");
                }
                if (lnbResource.getOwnerClientId() != i2) {
                    throw new RemoteException("Client is not the current owner of the releasing lnb.");
                }
                TunerResourceManagerService.this.releaseLnbInternal(lnbResource);
            }
        }

        public boolean isHigherPriority(ResourceClientProfile resourceClientProfile, ResourceClientProfile resourceClientProfile2) throws RemoteException {
            boolean isHigherPriorityInternal;
            TunerResourceManagerService.this.enforceTrmAccessPermission("isHigherPriority");
            if (resourceClientProfile == null || resourceClientProfile2 == null) {
                throw new RemoteException("Client profiles can't be null.");
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                isHigherPriorityInternal = TunerResourceManagerService.this.isHigherPriorityInternal(resourceClientProfile, resourceClientProfile2);
            }
            return isHigherPriorityInternal;
        }

        public void storeResourceMap(int i) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("storeResourceMap");
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.storeResourceMapInternal(i);
            }
        }

        public void clearResourceMap(int i) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("clearResourceMap");
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.clearResourceMapInternal(i);
            }
        }

        public void restoreResourceMap(int i) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("restoreResourceMap");
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService.this.restoreResourceMapInternal(i);
            }
        }

        public boolean acquireLock(int i, long j) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("acquireLock");
            return TunerResourceManagerService.this.acquireLockInternal(i, j, 500L);
        }

        public boolean releaseLock(int i) {
            TunerResourceManagerService.this.enforceTrmAccessPermission("releaseLock");
            return TunerResourceManagerService.this.releaseLockInternal(i, 500L, false, false);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            if (TunerResourceManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
                indentingPrintWriter.println("Permission Denial: can't dump!");
                return;
            }
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService tunerResourceManagerService = TunerResourceManagerService.this;
                tunerResourceManagerService.dumpMap(tunerResourceManagerService.mClientProfiles, "ClientProfiles:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService2 = TunerResourceManagerService.this;
                tunerResourceManagerService2.dumpMap(tunerResourceManagerService2.mFrontendResources, "FrontendResources:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService3 = TunerResourceManagerService.this;
                tunerResourceManagerService3.dumpSIA(tunerResourceManagerService3.mFrontendExistingNums, "FrontendExistingNums:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService4 = TunerResourceManagerService.this;
                tunerResourceManagerService4.dumpSIA(tunerResourceManagerService4.mFrontendUsedNums, "FrontendUsedNums:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService5 = TunerResourceManagerService.this;
                tunerResourceManagerService5.dumpSIA(tunerResourceManagerService5.mFrontendMaxUsableNums, "FrontendMaxUsableNums:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService6 = TunerResourceManagerService.this;
                tunerResourceManagerService6.dumpMap(tunerResourceManagerService6.mFrontendResourcesBackup, "FrontendResourcesBackUp:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService7 = TunerResourceManagerService.this;
                tunerResourceManagerService7.dumpSIA(tunerResourceManagerService7.mFrontendExistingNumsBackup, "FrontendExistingNumsBackup:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService8 = TunerResourceManagerService.this;
                tunerResourceManagerService8.dumpSIA(tunerResourceManagerService8.mFrontendUsedNumsBackup, "FrontendUsedNumsBackup:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService9 = TunerResourceManagerService.this;
                tunerResourceManagerService9.dumpSIA(tunerResourceManagerService9.mFrontendMaxUsableNumsBackup, "FrontendUsedNumsBackup:", ", ", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService10 = TunerResourceManagerService.this;
                tunerResourceManagerService10.dumpMap(tunerResourceManagerService10.mDemuxResources, "DemuxResource:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService11 = TunerResourceManagerService.this;
                tunerResourceManagerService11.dumpMap(tunerResourceManagerService11.mLnbResources, "LnbResource:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService12 = TunerResourceManagerService.this;
                tunerResourceManagerService12.dumpMap(tunerResourceManagerService12.mCasResources, "CasResource:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService13 = TunerResourceManagerService.this;
                tunerResourceManagerService13.dumpMap(tunerResourceManagerService13.mCiCamResources, "CiCamResource:", "\n", indentingPrintWriter);
                TunerResourceManagerService tunerResourceManagerService14 = TunerResourceManagerService.this;
                tunerResourceManagerService14.dumpMap(tunerResourceManagerService14.mListeners, "Listners:", "\n", indentingPrintWriter);
            }
        }

        public int getClientPriority(int i, int i2) throws RemoteException {
            int clientPriority;
            TunerResourceManagerService.this.enforceTrmAccessPermission("getClientPriority");
            synchronized (TunerResourceManagerService.this.mLock) {
                TunerResourceManagerService tunerResourceManagerService = TunerResourceManagerService.this;
                clientPriority = tunerResourceManagerService.getClientPriority(i, tunerResourceManagerService.checkIsForeground(i2));
            }
            return clientPriority;
        }

        public int getConfigPriority(int i, boolean z) throws RemoteException {
            int clientPriority;
            TunerResourceManagerService.this.enforceTrmAccessPermission("getConfigPriority");
            synchronized (TunerResourceManagerService.this.mLock) {
                clientPriority = TunerResourceManagerService.this.getClientPriority(i, z);
            }
            return clientPriority;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        if (DEBUG) {
            Slog.w("TunerResourceManagerService", "Native media resource manager service has died");
        }
        synchronized (this.mLock) {
            this.mMediaResourceManager = null;
        }
    }

    @VisibleForTesting
    public void registerClientProfileInternal(ResourceClientProfile resourceClientProfile, IResourcesReclaimListener iResourcesReclaimListener, int[] iArr) {
        int clientPid;
        IResourceManagerService iResourceManagerService;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "registerClientProfile(clientProfile=" + resourceClientProfile + ")");
        }
        iArr[0] = -1;
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager == null) {
            Slog.e("TunerResourceManagerService", "TvInputManager is null. Can't register client profile.");
            return;
        }
        int i = this.mNextUnusedClientId;
        this.mNextUnusedClientId = i + 1;
        iArr[0] = i;
        String str = resourceClientProfile.tvInputSessionId;
        if (str == null) {
            clientPid = Binder.getCallingPid();
        } else {
            clientPid = tvInputManager.getClientPid(str);
        }
        if (resourceClientProfile.tvInputSessionId != null && (iResourceManagerService = this.mMediaResourceManager) != null) {
            try {
                iResourceManagerService.overridePid(Binder.getCallingPid(), clientPid);
            } catch (RemoteException e) {
                Slog.e("TunerResourceManagerService", "Could not overridePid in resourceManagerSercice, remote exception: " + e);
            }
        }
        ClientProfile build = new ClientProfile.Builder(iArr[0]).tvInputSessionId(resourceClientProfile.tvInputSessionId).useCase(resourceClientProfile.useCase).processId(clientPid).build();
        build.setPriority(getClientPriority(resourceClientProfile.useCase, checkIsForeground(clientPid)));
        addClientProfile(iArr[0], build, iResourcesReclaimListener);
    }

    @VisibleForTesting
    public void unregisterClientProfileInternal(int i) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "unregisterClientProfile(clientId=" + i + ")");
        }
        removeClientProfile(i);
        IResourceManagerService iResourceManagerService = this.mMediaResourceManager;
        if (iResourceManagerService != null) {
            try {
                iResourceManagerService.overridePid(Binder.getCallingPid(), -1);
            } catch (RemoteException e) {
                Slog.e("TunerResourceManagerService", "Could not overridePid in resourceManagerSercice when unregister, remote exception: " + e);
            }
        }
    }

    @VisibleForTesting
    public boolean updateClientPriorityInternal(int i, int i2, int i3) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "updateClientPriority(clientId=" + i + ", priority=" + i2 + ", niceValue=" + i3 + ")");
        }
        ClientProfile clientProfile = getClientProfile(i);
        if (clientProfile == null) {
            Slog.e("TunerResourceManagerService", "Can not find client profile with id " + i + " when trying to update the client priority.");
            return false;
        }
        clientProfile.overwritePriority(i2);
        clientProfile.setNiceValue(i3);
        return true;
    }

    public boolean hasUnusedFrontendInternal(int i) {
        for (FrontendResource frontendResource : getFrontendResources().values()) {
            if (frontendResource.getType() == i && !frontendResource.isInUse()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLowestPriorityInternal(int i, int i2) throws RemoteException {
        ClientProfile clientProfile = getClientProfile(i);
        if (clientProfile == null) {
            return true;
        }
        clientPriorityUpdateOnRequest(clientProfile);
        int priority = clientProfile.getPriority();
        for (FrontendResource frontendResource : getFrontendResources().values()) {
            if (frontendResource.getType() == i2 && frontendResource.isInUse() && priority > updateAndGetOwnerClientPriority(frontendResource.getOwnerClientId())) {
                return false;
            }
        }
        return true;
    }

    public void storeResourceMapInternal(int i) {
        if (i != 0) {
            return;
        }
        replaceFeResourceMap(this.mFrontendResources, this.mFrontendResourcesBackup);
        replaceFeCounts(this.mFrontendExistingNums, this.mFrontendExistingNumsBackup);
        replaceFeCounts(this.mFrontendUsedNums, this.mFrontendUsedNumsBackup);
        replaceFeCounts(this.mFrontendMaxUsableNums, this.mFrontendMaxUsableNumsBackup);
    }

    public void clearResourceMapInternal(int i) {
        if (i != 0) {
            return;
        }
        replaceFeResourceMap(null, this.mFrontendResources);
        replaceFeCounts(null, this.mFrontendExistingNums);
        replaceFeCounts(null, this.mFrontendUsedNums);
        replaceFeCounts(null, this.mFrontendMaxUsableNums);
    }

    public void restoreResourceMapInternal(int i) {
        if (i != 0) {
            return;
        }
        replaceFeResourceMap(this.mFrontendResourcesBackup, this.mFrontendResources);
        replaceFeCounts(this.mFrontendExistingNumsBackup, this.mFrontendExistingNums);
        replaceFeCounts(this.mFrontendUsedNumsBackup, this.mFrontendUsedNums);
        replaceFeCounts(this.mFrontendMaxUsableNumsBackup, this.mFrontendMaxUsableNums);
    }

    @VisibleForTesting
    public void setFrontendInfoListInternal(TunerFrontendInfo[] tunerFrontendInfoArr) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "updateFrontendInfo:");
            for (TunerFrontendInfo tunerFrontendInfo : tunerFrontendInfoArr) {
                Slog.d("TunerResourceManagerService", tunerFrontendInfo.toString());
            }
        }
        HashSet<Integer> hashSet = new HashSet(getFrontendResources().keySet());
        for (int i = 0; i < tunerFrontendInfoArr.length; i++) {
            if (getFrontendResource(tunerFrontendInfoArr[i].handle) != null) {
                if (DEBUG) {
                    Slog.d("TunerResourceManagerService", "Frontend handle=" + tunerFrontendInfoArr[i].handle + "exists.");
                }
                hashSet.remove(Integer.valueOf(tunerFrontendInfoArr[i].handle));
            } else {
                addFrontendResource(new FrontendResource.Builder(tunerFrontendInfoArr[i].handle).type(tunerFrontendInfoArr[i].type).exclusiveGroupId(tunerFrontendInfoArr[i].exclusiveGroupId).build());
            }
        }
        for (Integer num : hashSet) {
            removeFrontendResource(num.intValue());
        }
    }

    @VisibleForTesting
    public void setDemuxInfoListInternal(TunerDemuxInfo[] tunerDemuxInfoArr) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "updateDemuxInfo:");
            for (TunerDemuxInfo tunerDemuxInfo : tunerDemuxInfoArr) {
                Slog.d("TunerResourceManagerService", tunerDemuxInfo.toString());
            }
        }
        HashSet<Integer> hashSet = new HashSet(getDemuxResources().keySet());
        for (int i = 0; i < tunerDemuxInfoArr.length; i++) {
            if (getDemuxResource(tunerDemuxInfoArr[i].handle) != null) {
                if (DEBUG) {
                    Slog.d("TunerResourceManagerService", "Demux handle=" + tunerDemuxInfoArr[i].handle + "exists.");
                }
                hashSet.remove(Integer.valueOf(tunerDemuxInfoArr[i].handle));
            } else {
                addDemuxResource(new DemuxResource.Builder(tunerDemuxInfoArr[i].handle).filterTypes(tunerDemuxInfoArr[i].filterTypes).build());
            }
        }
        for (Integer num : hashSet) {
            removeDemuxResource(num.intValue());
        }
    }

    @VisibleForTesting
    public void setLnbInfoListInternal(int[] iArr) {
        if (DEBUG) {
            for (int i = 0; i < iArr.length; i++) {
                Slog.d("TunerResourceManagerService", "updateLnbInfo(lnbHanle=" + iArr[i] + ")");
            }
        }
        HashSet<Integer> hashSet = new HashSet(getLnbResources().keySet());
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (getLnbResource(iArr[i2]) != null) {
                if (DEBUG) {
                    Slog.d("TunerResourceManagerService", "Lnb handle=" + iArr[i2] + "exists.");
                }
                hashSet.remove(Integer.valueOf(iArr[i2]));
            } else {
                addLnbResource(new LnbResource.Builder(iArr[i2]).build());
            }
        }
        for (Integer num : hashSet) {
            removeLnbResource(num.intValue());
        }
    }

    @VisibleForTesting
    public void updateCasInfoInternal(int i, int i2) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "updateCasInfo(casSystemId=" + i + ", maxSessionNum=" + i2 + ")");
        }
        if (i2 == 0) {
            removeCasResource(i);
            removeCiCamResource(i);
            return;
        }
        CasResource casResource = getCasResource(i);
        CiCamResource ciCamResource = getCiCamResource(i);
        if (casResource != null) {
            if (casResource.getUsedSessionNum() > i2) {
                casResource.getUsedSessionNum();
            }
            casResource.updateMaxSessionNum(i2);
            if (ciCamResource != null) {
                ciCamResource.updateMaxSessionNum(i2);
                return;
            }
            return;
        }
        CasResource build = new CasResource.Builder(i).maxSessionNum(i2).build();
        CiCamResource build2 = new CiCamResource.Builder(i).maxSessionNum(i2).build();
        addCasResource(build);
        addCiCamResource(build2);
    }

    @VisibleForTesting
    public boolean requestFrontendInternal(TunerFrontendRequest tunerFrontendRequest, int[] iArr) {
        int frontendHighestClientPriority;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestFrontend(request=" + tunerFrontendRequest + ")");
        }
        iArr[0] = -1;
        ClientProfile clientProfile = getClientProfile(tunerFrontendRequest.clientId);
        if (clientProfile == null) {
            return false;
        }
        clientPriorityUpdateOnRequest(clientProfile);
        boolean z = tunerFrontendRequest.desiredId != -1;
        Iterator<FrontendResource> it = getFrontendResources().values().iterator();
        int i = 1001;
        int i2 = -1;
        int i3 = -1;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            FrontendResource next = it.next();
            int resourceIdFromHandle = getResourceIdFromHandle(next.getHandle());
            if (next.getType() == tunerFrontendRequest.frontendType && (!z || resourceIdFromHandle == tunerFrontendRequest.desiredId)) {
                if (next.isInUse()) {
                    if (i2 == -1 && i > (frontendHighestClientPriority = getFrontendHighestClientPriority(next.getOwnerClientId()))) {
                        i3 = next.getHandle();
                        i = frontendHighestClientPriority;
                    }
                } else if (isFrontendMaxNumUseReached(tunerFrontendRequest.frontendType)) {
                    continue;
                } else if (next.getExclusiveGroupMemberFeHandles().isEmpty()) {
                    i2 = next.getHandle();
                    break;
                } else if (i2 == -1) {
                    i2 = next.getHandle();
                }
            }
        }
        if (i2 != -1) {
            iArr[0] = i2;
            updateFrontendClientMappingOnNewGrant(i2, tunerFrontendRequest.clientId);
            return true;
        } else if (i3 == -1 || clientProfile.getPriority() <= i || !reclaimResource(getFrontendResource(i3).getOwnerClientId(), 0)) {
            return false;
        } else {
            iArr[0] = i3;
            updateFrontendClientMappingOnNewGrant(i3, tunerFrontendRequest.clientId);
            return true;
        }
    }

    @VisibleForTesting
    public void shareFrontendInternal(int i, int i2) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "shareFrontend from " + i + " with " + i2);
        }
        for (Integer num : getClientProfile(i2).getInUseFrontendHandles()) {
            getClientProfile(i).useFrontend(num.intValue());
        }
        getClientProfile(i2).shareFrontend(i);
    }

    public final boolean transferFeOwner(int i, int i2) {
        ClientProfile clientProfile = getClientProfile(i);
        ClientProfile clientProfile2 = getClientProfile(i2);
        clientProfile2.shareFrontend(i);
        clientProfile.stopSharingFrontend(i2);
        for (Integer num : clientProfile2.getInUseFrontendHandles()) {
            getFrontendResource(num.intValue()).setOwner(i2);
        }
        clientProfile2.setPrimaryFrontend(clientProfile.getPrimaryFrontend());
        clientProfile.setPrimaryFrontend(-1);
        for (Integer num2 : clientProfile.getInUseFrontendHandles()) {
            int intValue = num2.intValue();
            int ownerClientId = getFrontendResource(intValue).getOwnerClientId();
            if (ownerClientId != i2) {
                Slog.e("TunerResourceManagerService", "something is wrong in transferFeOwner:" + intValue + ", " + ownerClientId + ", " + i2);
                return false;
            }
        }
        return true;
    }

    public final boolean transferFeCiCamOwner(int i, int i2) {
        ClientProfile clientProfile = getClientProfile(i);
        ClientProfile clientProfile2 = getClientProfile(i2);
        int inUseCiCamId = clientProfile.getInUseCiCamId();
        clientProfile2.useCiCam(inUseCiCamId);
        getCiCamResource(inUseCiCamId).setOwner(i2);
        clientProfile.releaseCiCam();
        return true;
    }

    public final boolean transferLnbOwner(int i, int i2) {
        ClientProfile clientProfile = getClientProfile(i);
        ClientProfile clientProfile2 = getClientProfile(i2);
        HashSet<Integer> hashSet = new HashSet();
        for (Integer num : clientProfile.getInUseLnbHandles()) {
            clientProfile2.useLnb(num.intValue());
            getLnbResource(num.intValue()).setOwner(i2);
            hashSet.add(num);
        }
        for (Integer num2 : hashSet) {
            clientProfile.releaseLnb(num2.intValue());
        }
        return true;
    }

    @VisibleForTesting
    public boolean transferOwnerInternal(int i, int i2, int i3) {
        if (i != 0) {
            if (i != 3) {
                if (i == 5) {
                    return transferFeCiCamOwner(i2, i3);
                }
                Slog.e("TunerResourceManagerService", "transferOwnerInternal. unsupported resourceType: " + i);
                return false;
            }
            return transferLnbOwner(i2, i3);
        }
        return transferFeOwner(i2, i3);
    }

    @VisibleForTesting
    public boolean requestLnbInternal(TunerLnbRequest tunerLnbRequest, int[] iArr) {
        int i;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestLnb(request=" + tunerLnbRequest + ")");
        }
        iArr[0] = -1;
        ClientProfile clientProfile = getClientProfile(tunerLnbRequest.clientId);
        clientPriorityUpdateOnRequest(clientProfile);
        Iterator<LnbResource> it = getLnbResources().values().iterator();
        int i2 = 1001;
        int i3 = -1;
        while (true) {
            if (!it.hasNext()) {
                i = -1;
                break;
            }
            LnbResource next = it.next();
            if (!next.isInUse()) {
                i = next.getHandle();
                break;
            }
            int updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority(next.getOwnerClientId());
            if (i2 > updateAndGetOwnerClientPriority) {
                i3 = next.getHandle();
                i2 = updateAndGetOwnerClientPriority;
            }
        }
        if (i > -1) {
            iArr[0] = i;
            updateLnbClientMappingOnNewGrant(i, tunerLnbRequest.clientId);
            return true;
        } else if (i3 <= -1 || clientProfile.getPriority() <= i2 || !reclaimResource(getLnbResource(i3).getOwnerClientId(), 3)) {
            return false;
        } else {
            iArr[0] = i3;
            updateLnbClientMappingOnNewGrant(i3, tunerLnbRequest.clientId);
            return true;
        }
    }

    @VisibleForTesting
    public boolean requestCasSessionInternal(CasSessionRequest casSessionRequest, int[] iArr) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestCasSession(request=" + casSessionRequest + ")");
        }
        CasResource casResource = getCasResource(casSessionRequest.casSystemId);
        if (casResource == null) {
            casResource = new CasResource.Builder(casSessionRequest.casSystemId).maxSessionNum(Integer.MAX_VALUE).build();
            addCasResource(casResource);
        }
        iArr[0] = -1;
        ClientProfile clientProfile = getClientProfile(casSessionRequest.clientId);
        clientPriorityUpdateOnRequest(clientProfile);
        if (!casResource.isFullyUsed()) {
            iArr[0] = generateResourceHandle(4, casResource.getSystemId());
            updateCasClientMappingOnNewGrant(casSessionRequest.casSystemId, casSessionRequest.clientId);
            return true;
        }
        int i = 1001;
        int i2 = -1;
        for (Integer num : casResource.getOwnerClientIds()) {
            int intValue = num.intValue();
            int updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority(intValue);
            if (i > updateAndGetOwnerClientPriority) {
                i2 = intValue;
                i = updateAndGetOwnerClientPriority;
            }
        }
        if (i2 <= -1 || clientProfile.getPriority() <= i || !reclaimResource(i2, 4)) {
            return false;
        }
        iArr[0] = generateResourceHandle(4, casResource.getSystemId());
        updateCasClientMappingOnNewGrant(casSessionRequest.casSystemId, casSessionRequest.clientId);
        return true;
    }

    @VisibleForTesting
    public boolean requestCiCamInternal(TunerCiCamRequest tunerCiCamRequest, int[] iArr) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestCiCamInternal(TunerCiCamRequest=" + tunerCiCamRequest + ")");
        }
        CiCamResource ciCamResource = getCiCamResource(tunerCiCamRequest.ciCamId);
        if (ciCamResource == null) {
            ciCamResource = new CiCamResource.Builder(tunerCiCamRequest.ciCamId).maxSessionNum(Integer.MAX_VALUE).build();
            addCiCamResource(ciCamResource);
        }
        iArr[0] = -1;
        ClientProfile clientProfile = getClientProfile(tunerCiCamRequest.clientId);
        clientPriorityUpdateOnRequest(clientProfile);
        if (!ciCamResource.isFullyUsed()) {
            iArr[0] = generateResourceHandle(5, ciCamResource.getCiCamId());
            updateCiCamClientMappingOnNewGrant(tunerCiCamRequest.ciCamId, tunerCiCamRequest.clientId);
            return true;
        }
        int i = 1001;
        int i2 = -1;
        for (Integer num : ciCamResource.getOwnerClientIds()) {
            int intValue = num.intValue();
            int updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority(intValue);
            if (i > updateAndGetOwnerClientPriority) {
                i2 = intValue;
                i = updateAndGetOwnerClientPriority;
            }
        }
        if (i2 <= -1 || clientProfile.getPriority() <= i || !reclaimResource(i2, 5)) {
            return false;
        }
        iArr[0] = generateResourceHandle(5, ciCamResource.getCiCamId());
        updateCiCamClientMappingOnNewGrant(tunerCiCamRequest.ciCamId, tunerCiCamRequest.clientId);
        return true;
    }

    @VisibleForTesting
    public boolean isHigherPriorityInternal(ResourceClientProfile resourceClientProfile, ResourceClientProfile resourceClientProfile2) {
        int clientPid;
        int clientPid2;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "isHigherPriority(challengerProfile=" + resourceClientProfile + ", holderProfile=" + resourceClientProfile + ")");
        }
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager == null) {
            Slog.e("TunerResourceManagerService", "TvInputManager is null. Can't compare the priority.");
            return true;
        }
        String str = resourceClientProfile.tvInputSessionId;
        if (str == null) {
            clientPid = Binder.getCallingPid();
        } else {
            clientPid = tvInputManager.getClientPid(str);
        }
        String str2 = resourceClientProfile2.tvInputSessionId;
        if (str2 == null) {
            clientPid2 = Binder.getCallingPid();
        } else {
            clientPid2 = this.mTvInputManager.getClientPid(str2);
        }
        return getClientPriority(resourceClientProfile.useCase, checkIsForeground(clientPid)) > getClientPriority(resourceClientProfile2.useCase, checkIsForeground(clientPid2));
    }

    @VisibleForTesting
    public void releaseFrontendInternal(FrontendResource frontendResource, int i) {
        ClientProfile clientProfile;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "releaseFrontend(id=" + frontendResource.getHandle() + ", clientId=" + i + " )");
        }
        if (i == frontendResource.getOwnerClientId() && (clientProfile = getClientProfile(frontendResource.getOwnerClientId())) != null) {
            for (Integer num : clientProfile.getShareFeClientIds()) {
                reclaimResource(num.intValue(), 0);
            }
        }
        clearFrontendAndClientMapping(getClientProfile(i));
    }

    @VisibleForTesting
    public void releaseDemuxInternal(DemuxResource demuxResource) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "releaseDemux(DemuxHandle=" + demuxResource.getHandle() + ")");
        }
        updateDemuxClientMappingOnRelease(demuxResource);
    }

    @VisibleForTesting
    public void releaseLnbInternal(LnbResource lnbResource) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "releaseLnb(lnbHandle=" + lnbResource.getHandle() + ")");
        }
        updateLnbClientMappingOnRelease(lnbResource);
    }

    @VisibleForTesting
    public void releaseCasSessionInternal(CasResource casResource, int i) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "releaseCasSession(sessionResourceId=" + casResource.getSystemId() + ")");
        }
        updateCasClientMappingOnRelease(casResource, i);
    }

    @VisibleForTesting
    public void releaseCiCamInternal(CiCamResource ciCamResource, int i) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "releaseCiCamInternal(ciCamId=" + ciCamResource.getCiCamId() + ")");
        }
        updateCiCamClientMappingOnRelease(ciCamResource, i);
    }

    @VisibleForTesting
    public boolean requestDemuxInternal(TunerDemuxRequest tunerDemuxRequest, int[] iArr) {
        int updateAndGetOwnerClientPriority;
        boolean z;
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestDemux(request=" + tunerDemuxRequest + ")");
        }
        if (this.mDemuxResources.size() == 0) {
            iArr[0] = generateResourceHandle(1, 0);
            return true;
        }
        iArr[0] = -1;
        ClientProfile clientProfile = getClientProfile(tunerDemuxRequest.clientId);
        if (clientProfile == null) {
            return false;
        }
        clientPriorityUpdateOnRequest(clientProfile);
        boolean z2 = tunerDemuxRequest.desiredFilterTypes != 0;
        int i = 1001;
        int i2 = 33;
        int i3 = -1;
        int i4 = -1;
        for (DemuxResource demuxResource : getDemuxResources().values()) {
            if (!z2 || demuxResource.hasSufficientCaps(tunerDemuxRequest.desiredFilterTypes)) {
                if (!demuxResource.isInUse()) {
                    int numOfCaps = demuxResource.getNumOfCaps();
                    if (i2 > numOfCaps) {
                        i3 = demuxResource.getHandle();
                        i2 = numOfCaps;
                    }
                } else if (i3 == -1 && i >= (updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority(demuxResource.getOwnerClientId()))) {
                    int numOfCaps2 = demuxResource.getNumOfCaps();
                    if (i > updateAndGetOwnerClientPriority) {
                        i = updateAndGetOwnerClientPriority;
                        z = true;
                    } else {
                        z = false;
                    }
                    if (i2 > numOfCaps2) {
                        z = true;
                        i2 = numOfCaps2;
                    }
                    if (z) {
                        i4 = demuxResource.getHandle();
                    }
                }
            }
        }
        if (i3 != -1) {
            iArr[0] = i3;
            updateDemuxClientMappingOnNewGrant(i3, tunerDemuxRequest.clientId);
            return true;
        } else if (i4 == -1 || clientProfile.getPriority() <= i || !reclaimResource(getDemuxResource(i4).getOwnerClientId(), 1)) {
            return false;
        } else {
            iArr[0] = i4;
            updateDemuxClientMappingOnNewGrant(i4, tunerDemuxRequest.clientId);
            return true;
        }
    }

    @VisibleForTesting
    public void clientPriorityUpdateOnRequest(ClientProfile clientProfile) {
        if (clientProfile.isPriorityOverwritten()) {
            return;
        }
        clientProfile.setPriority(getClientPriority(clientProfile.getUseCase(), checkIsForeground(clientProfile.getProcessId())));
    }

    @VisibleForTesting
    public boolean requestDescramblerInternal(TunerDescramblerRequest tunerDescramblerRequest, int[] iArr) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "requestDescrambler(request=" + tunerDescramblerRequest + ")");
        }
        iArr[0] = generateResourceHandle(2, 0);
        return true;
    }

    public final long getElapsedTime(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis >= j) {
            return uptimeMillis - j;
        }
        long j2 = uptimeMillis + (Long.MAX_VALUE - j);
        if (j2 < 0) {
            return Long.MAX_VALUE;
        }
        return j2;
    }

    public final boolean lockForTunerApiLock(int i, long j, String str) {
        try {
            if (this.mLockForTRMSLock.tryLock(j, TimeUnit.MILLISECONDS)) {
                return true;
            }
            Slog.e("TunerResourceManagerService", "FAILED to lock mLockForTRMSLock in " + str + ", clientId:" + i + ", timeoutMS:" + j + ", mTunerApiLockHolder:" + this.mTunerApiLockHolder);
            return false;
        } catch (InterruptedException e) {
            Slog.e("TunerResourceManagerService", "exception thrown in " + str + XmlUtils.STRING_ARRAY_SEPARATOR + e);
            if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                this.mLockForTRMSLock.unlock();
            }
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x007d A[Catch: all -> 0x0193, InterruptedException -> 0x0195, TryCatch #0 {InterruptedException -> 0x0195, blocks: (B:5:0x001a, B:10:0x0024, B:20:0x0036, B:23:0x0044, B:36:0x00d3, B:38:0x00de, B:40:0x0108, B:41:0x012c, B:43:0x0130, B:44:0x0155, B:46:0x0159, B:47:0x0174, B:24:0x007d, B:29:0x0090), top: B:71:0x001a, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00d3 A[Catch: all -> 0x0193, InterruptedException -> 0x0195, TryCatch #0 {InterruptedException -> 0x0195, blocks: (B:5:0x001a, B:10:0x0024, B:20:0x0036, B:23:0x0044, B:36:0x00d3, B:38:0x00de, B:40:0x0108, B:41:0x012c, B:43:0x0130, B:44:0x0155, B:46:0x0159, B:47:0x0174, B:24:0x007d, B:29:0x0090), top: B:71:0x001a, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0108 A[Catch: all -> 0x0193, InterruptedException -> 0x0195, TryCatch #0 {InterruptedException -> 0x0195, blocks: (B:5:0x001a, B:10:0x0024, B:20:0x0036, B:23:0x0044, B:36:0x00d3, B:38:0x00de, B:40:0x0108, B:41:0x012c, B:43:0x0130, B:44:0x0155, B:46:0x0159, B:47:0x0174, B:24:0x007d, B:29:0x0090), top: B:71:0x001a, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0130 A[Catch: all -> 0x0193, InterruptedException -> 0x0195, TryCatch #0 {InterruptedException -> 0x0195, blocks: (B:5:0x001a, B:10:0x0024, B:20:0x0036, B:23:0x0044, B:36:0x00d3, B:38:0x00de, B:40:0x0108, B:41:0x012c, B:43:0x0130, B:44:0x0155, B:46:0x0159, B:47:0x0174, B:24:0x007d, B:29:0x0090), top: B:71:0x001a, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0159 A[Catch: all -> 0x0193, InterruptedException -> 0x0195, TryCatch #0 {InterruptedException -> 0x0195, blocks: (B:5:0x001a, B:10:0x0024, B:20:0x0036, B:23:0x0044, B:36:0x00d3, B:38:0x00de, B:40:0x0108, B:41:0x012c, B:43:0x0130, B:44:0x0155, B:46:0x0159, B:47:0x0174, B:24:0x007d, B:29:0x0090), top: B:71:0x001a, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x017d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x018d  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0044 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean acquireLockInternal(int i, long j, long j2) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        long elapsedTime;
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            if (lockForTunerApiLock(i, j2, "acquireLockInternal()")) {
                try {
                    int i2 = this.mTunerApiLockHolder;
                    boolean z6 = i2 == -1;
                    if (i == i2) {
                        z = z6;
                        if (j == this.mTunerApiLockHolderThreadId) {
                            z2 = true;
                            z3 = z;
                            while (!z3 && !z2) {
                                elapsedTime = j2 - getElapsedTime(uptimeMillis);
                                if (elapsedTime > 0) {
                                    Slog.e("TunerResourceManagerService", "FAILED:acquireLockInternal(" + i + ", " + j + ", " + j2 + ") - timed out, but will grant the lock to the callee by stealing it from the current holder:" + this.mTunerApiLockHolder + "(" + this.mTunerApiLockHolderThreadId + "), who likely failed to call releaseLock(), to prevent this from becoming an unrecoverable error");
                                    z4 = true;
                                    break;
                                }
                                long j3 = uptimeMillis;
                                this.mTunerApiLockReleasedCV.await(elapsedTime, TimeUnit.MILLISECONDS);
                                z3 = this.mTunerApiLockHolder == -1;
                                if (!z3) {
                                    Slog.w("TunerResourceManagerService", "acquireLockInternal(" + i + ", " + j + ", " + j2 + ") - woken up from cond wait, but " + this.mTunerApiLockHolder + "(" + this.mTunerApiLockHolderThreadId + ") is already holding the lock. Going to wait again if timeout hasn't reached yet");
                                }
                                uptimeMillis = j3;
                            }
                            z4 = false;
                            if (!z3 && !z4) {
                                if (!z2) {
                                    this.mTunerApiLockNestedCount++;
                                    if (DEBUG) {
                                        Slog.d("TunerResourceManagerService", "acquireLockInternal(" + i + ", " + j + ", " + j2 + ") - nested count incremented to " + this.mTunerApiLockNestedCount);
                                    }
                                } else {
                                    Slog.e("TunerResourceManagerService", "acquireLockInternal(" + i + ", " + j + ", " + j2 + ") - should not reach here");
                                }
                                z5 = true;
                                boolean z7 = (!z3 || z2 || z4) ? z5 : false;
                                if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                                    this.mLockForTRMSLock.unlock();
                                }
                                return z7;
                            }
                            if (DEBUG) {
                                Slog.d("TunerResourceManagerService", "SUCCESS:acquireLockInternal(" + i + ", " + j + ", " + j2 + ")");
                            }
                            if (this.mTunerApiLockNestedCount != 0) {
                                Slog.w("TunerResourceManagerService", "Something is wrong as nestedCount(" + this.mTunerApiLockNestedCount + ") is not zero. Will overriding it to 1 anyways");
                            }
                            this.mTunerApiLockHolder = i;
                            this.mTunerApiLockHolderThreadId = j;
                            z5 = true;
                            this.mTunerApiLockNestedCount = 1;
                            if (z3) {
                            }
                            if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                            }
                            return z7;
                        }
                    } else {
                        z = z6;
                    }
                    z2 = false;
                    z3 = z;
                    while (!z3) {
                        elapsedTime = j2 - getElapsedTime(uptimeMillis);
                        if (elapsedTime > 0) {
                        }
                    }
                    z4 = false;
                    if (!z3) {
                        if (!z2) {
                        }
                        z5 = true;
                        if (z3) {
                        }
                        if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                        }
                        return z7;
                    }
                    if (DEBUG) {
                    }
                    if (this.mTunerApiLockNestedCount != 0) {
                    }
                    this.mTunerApiLockHolder = i;
                    this.mTunerApiLockHolderThreadId = j;
                    z5 = true;
                    this.mTunerApiLockNestedCount = 1;
                    if (z3) {
                    }
                    if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                    }
                    return z7;
                } catch (InterruptedException e) {
                    Slog.e("TunerResourceManagerService", "exception thrown in acquireLockInternal(" + i + ", " + j + ", " + j2 + "):" + e);
                    if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                        this.mLockForTRMSLock.unlock();
                        return false;
                    }
                    return false;
                }
            }
            return false;
        } catch (Throwable th) {
            if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                this.mLockForTRMSLock.unlock();
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x009e A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean releaseLockInternal(int i, long j, boolean z, boolean z2) {
        if (lockForTunerApiLock(i, j, "releaseLockInternal()")) {
            try {
                int i2 = this.mTunerApiLockHolder;
                if (i2 != i) {
                    if (i2 == -1) {
                        if (!z2) {
                            Slog.w("TunerResourceManagerService", "releaseLockInternal(" + i + ", " + j + ") - called while there is no current holder");
                        }
                        if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                            this.mLockForTRMSLock.unlock();
                        }
                        return false;
                    }
                    if (!z2) {
                        Slog.e("TunerResourceManagerService", "releaseLockInternal(" + i + ", " + j + ") - called while someone else:" + this.mTunerApiLockHolder + "is the current holder");
                    }
                    if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                        this.mLockForTRMSLock.unlock();
                    }
                    return false;
                }
                int i3 = this.mTunerApiLockNestedCount - 1;
                this.mTunerApiLockNestedCount = i3;
                if (!z && i3 > 0) {
                    if (DEBUG) {
                        Slog.d("TunerResourceManagerService", "releaseLockInternal(" + i + ", " + j + ", " + z + ", " + z2 + ") - NOT signaling because nested count is not zero (" + this.mTunerApiLockNestedCount + ")");
                    }
                    return true;
                }
                if (DEBUG) {
                    Slog.d("TunerResourceManagerService", "SUCCESS:releaseLockInternal(" + i + ", " + j + ", " + z + ", " + z2 + ") - signaling!");
                }
                this.mTunerApiLockHolder = -1;
                this.mTunerApiLockHolderThreadId = -1L;
                this.mTunerApiLockNestedCount = 0;
                this.mTunerApiLockReleasedCV.signal();
                return true;
            } finally {
                if (this.mLockForTRMSLock.isHeldByCurrentThread()) {
                    this.mLockForTRMSLock.unlock();
                }
            }
        }
        return false;
    }

    @VisibleForTesting
    /* renamed from: com.android.server.tv.tunerresourcemanager.TunerResourceManagerService$ResourcesReclaimListenerRecord */
    /* loaded from: classes2.dex */
    public class ResourcesReclaimListenerRecord implements IBinder.DeathRecipient {
        public final int mClientId;
        public final IResourcesReclaimListener mListener;

        public ResourcesReclaimListenerRecord(IResourcesReclaimListener iResourcesReclaimListener, int i) {
            this.mListener = iResourcesReclaimListener;
            this.mClientId = i;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            try {
                synchronized (TunerResourceManagerService.this.mLock) {
                    if (TunerResourceManagerService.this.checkClientExists(this.mClientId)) {
                        TunerResourceManagerService.this.removeClientProfile(this.mClientId);
                    }
                }
            } finally {
                TunerResourceManagerService.this.releaseLockInternal(this.mClientId, 500L, true, true);
            }
        }

        public IResourcesReclaimListener getListener() {
            return this.mListener;
        }
    }

    public final void addResourcesReclaimListener(int i, IResourcesReclaimListener iResourcesReclaimListener) {
        if (iResourcesReclaimListener == null) {
            if (DEBUG) {
                Slog.w("TunerResourceManagerService", "Listener is null when client " + i + " registered!");
                return;
            }
            return;
        }
        ResourcesReclaimListenerRecord resourcesReclaimListenerRecord = new ResourcesReclaimListenerRecord(iResourcesReclaimListener, i);
        try {
            iResourcesReclaimListener.asBinder().linkToDeath(resourcesReclaimListenerRecord, 0);
            this.mListeners.put(Integer.valueOf(i), resourcesReclaimListenerRecord);
        } catch (RemoteException unused) {
            Slog.w("TunerResourceManagerService", "Listener already died.");
        }
    }

    @VisibleForTesting
    public boolean reclaimResource(int i, int i2) {
        Binder.allowBlockingForCurrentThread();
        ClientProfile clientProfile = getClientProfile(i);
        if (clientProfile == null) {
            return true;
        }
        for (Integer num : clientProfile.getShareFeClientIds()) {
            int intValue = num.intValue();
            try {
                this.mListeners.get(Integer.valueOf(intValue)).getListener().onReclaimResources();
                clearAllResourcesAndClientMapping(getClientProfile(intValue));
            } catch (RemoteException e) {
                Slog.e("TunerResourceManagerService", "Failed to reclaim resources on client " + intValue, e);
                return false;
            }
        }
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "Reclaiming resources because higher priority client request resource type " + i2 + ", clientId:" + i);
        }
        try {
            this.mListeners.get(Integer.valueOf(i)).getListener().onReclaimResources();
            clearAllResourcesAndClientMapping(clientProfile);
            return true;
        } catch (RemoteException e2) {
            Slog.e("TunerResourceManagerService", "Failed to reclaim resources on client " + i, e2);
            return false;
        }
    }

    @VisibleForTesting
    public int getClientPriority(int i, boolean z) {
        if (DEBUG) {
            Slog.d("TunerResourceManagerService", "getClientPriority useCase=" + i + ", isForeground=" + z + ")");
        }
        if (z) {
            return this.mPriorityCongfig.getForegroundPriority(i);
        }
        return this.mPriorityCongfig.getBackgroundPriority(i);
    }

    @VisibleForTesting
    public boolean checkIsForeground(int i) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        ActivityManager activityManager = this.mActivityManager;
        if (activityManager == null || (runningAppProcesses = activityManager.getRunningAppProcesses()) == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == i && runningAppProcessInfo.importance == 100) {
                return true;
            }
        }
        return false;
    }

    public final void updateFrontendClientMappingOnNewGrant(int i, int i2) {
        FrontendResource frontendResource = getFrontendResource(i);
        ClientProfile clientProfile = getClientProfile(i2);
        frontendResource.setOwner(i2);
        increFrontendNum(this.mFrontendUsedNums, frontendResource.getType());
        clientProfile.useFrontend(i);
        for (Integer num : frontendResource.getExclusiveGroupMemberFeHandles()) {
            int intValue = num.intValue();
            getFrontendResource(intValue).setOwner(i2);
            clientProfile.useFrontend(intValue);
        }
        clientProfile.setPrimaryFrontend(i);
    }

    public final void updateDemuxClientMappingOnNewGrant(int i, int i2) {
        DemuxResource demuxResource = getDemuxResource(i);
        if (demuxResource != null) {
            ClientProfile clientProfile = getClientProfile(i2);
            demuxResource.setOwner(i2);
            clientProfile.useDemux(i);
        }
    }

    public final void updateDemuxClientMappingOnRelease(DemuxResource demuxResource) {
        ClientProfile clientProfile = getClientProfile(demuxResource.getOwnerClientId());
        demuxResource.removeOwner();
        clientProfile.releaseDemux(demuxResource.getHandle());
    }

    public final void updateLnbClientMappingOnNewGrant(int i, int i2) {
        LnbResource lnbResource = getLnbResource(i);
        ClientProfile clientProfile = getClientProfile(i2);
        lnbResource.setOwner(i2);
        clientProfile.useLnb(i);
    }

    public final void updateLnbClientMappingOnRelease(LnbResource lnbResource) {
        ClientProfile clientProfile = getClientProfile(lnbResource.getOwnerClientId());
        lnbResource.removeOwner();
        clientProfile.releaseLnb(lnbResource.getHandle());
    }

    public final void updateCasClientMappingOnNewGrant(int i, int i2) {
        CasResource casResource = getCasResource(i);
        ClientProfile clientProfile = getClientProfile(i2);
        casResource.setOwner(i2);
        clientProfile.useCas(i);
    }

    public final void updateCiCamClientMappingOnNewGrant(int i, int i2) {
        CiCamResource ciCamResource = getCiCamResource(i);
        ClientProfile clientProfile = getClientProfile(i2);
        ciCamResource.setOwner(i2);
        clientProfile.useCiCam(i);
    }

    public final void updateCasClientMappingOnRelease(CasResource casResource, int i) {
        ClientProfile clientProfile = getClientProfile(i);
        casResource.removeOwner(i);
        clientProfile.releaseCas();
    }

    public final void updateCiCamClientMappingOnRelease(CiCamResource ciCamResource, int i) {
        ClientProfile clientProfile = getClientProfile(i);
        ciCamResource.removeOwner(i);
        clientProfile.releaseCiCam();
    }

    public final int updateAndGetOwnerClientPriority(int i) {
        ClientProfile clientProfile = getClientProfile(i);
        clientPriorityUpdateOnRequest(clientProfile);
        return clientProfile.getPriority();
    }

    public final int getFrontendHighestClientPriority(int i) {
        ClientProfile clientProfile = getClientProfile(i);
        if (clientProfile == null) {
            return 0;
        }
        int updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority(i);
        for (Integer num : clientProfile.getShareFeClientIds()) {
            int updateAndGetOwnerClientPriority2 = updateAndGetOwnerClientPriority(num.intValue());
            if (updateAndGetOwnerClientPriority2 > updateAndGetOwnerClientPriority) {
                updateAndGetOwnerClientPriority = updateAndGetOwnerClientPriority2;
            }
        }
        return updateAndGetOwnerClientPriority;
    }

    @VisibleForTesting
    public FrontendResource getFrontendResource(int i) {
        return this.mFrontendResources.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public Map<Integer, FrontendResource> getFrontendResources() {
        return this.mFrontendResources;
    }

    @VisibleForTesting
    public DemuxResource getDemuxResource(int i) {
        return this.mDemuxResources.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public Map<Integer, DemuxResource> getDemuxResources() {
        return this.mDemuxResources;
    }

    public final boolean setMaxNumberOfFrontendsInternal(int i, int i2) {
        int i3 = this.mFrontendUsedNums.get(i, -1);
        if (i3 == -1 || i3 <= i2) {
            this.mFrontendMaxUsableNums.put(i, i2);
            return true;
        }
        Slog.e("TunerResourceManagerService", "max number of frontend for frontendType: " + i + " cannot be set to a value lower than the current usage count. (requested max num = " + i2 + ", current usage = " + i3);
        return false;
    }

    public final int getMaxNumberOfFrontendsInternal(int i) {
        int i2 = this.mFrontendExistingNums.get(i, -1);
        if (i2 == -1) {
            Log.e("TunerResourceManagerService", "existingNum is -1 for " + i);
            return -1;
        }
        int i3 = this.mFrontendMaxUsableNums.get(i, -1);
        return i3 == -1 ? i2 : i3;
    }

    public final boolean isFrontendMaxNumUseReached(int i) {
        int i2 = this.mFrontendMaxUsableNums.get(i, -1);
        if (i2 == -1) {
            return false;
        }
        int i3 = this.mFrontendUsedNums.get(i, -1);
        if (i3 == -1) {
            i3 = 0;
        }
        return i3 >= i2;
    }

    public final void increFrontendNum(SparseIntArray sparseIntArray, int i) {
        int i2 = sparseIntArray.get(i, -1);
        if (i2 == -1) {
            sparseIntArray.put(i, 1);
        } else {
            sparseIntArray.put(i, i2 + 1);
        }
    }

    public final void decreFrontendNum(SparseIntArray sparseIntArray, int i) {
        int i2 = sparseIntArray.get(i, -1);
        if (i2 != -1) {
            sparseIntArray.put(i, i2 - 1);
        }
    }

    public final void replaceFeResourceMap(Map<Integer, FrontendResource> map, Map<Integer, FrontendResource> map2) {
        if (map2 != null) {
            map2.clear();
            if (map == null || map.size() <= 0) {
                return;
            }
            map2.putAll(map);
        }
    }

    public final void replaceFeCounts(SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2) {
        if (sparseIntArray2 != null) {
            sparseIntArray2.clear();
            if (sparseIntArray != null) {
                for (int i = 0; i < sparseIntArray.size(); i++) {
                    sparseIntArray2.put(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i));
                }
            }
        }
    }

    public final void dumpMap(Map<?, ?> map, String str, String str2, IndentingPrintWriter indentingPrintWriter) {
        if (map != null) {
            indentingPrintWriter.println(str);
            indentingPrintWriter.increaseIndent();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                indentingPrintWriter.print(entry.getKey() + " : " + entry.getValue());
                indentingPrintWriter.print(str2);
            }
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void dumpSIA(SparseIntArray sparseIntArray, String str, String str2, IndentingPrintWriter indentingPrintWriter) {
        if (sparseIntArray != null) {
            indentingPrintWriter.println(str);
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < sparseIntArray.size(); i++) {
                indentingPrintWriter.print(sparseIntArray.keyAt(i) + " : " + sparseIntArray.valueAt(i));
                indentingPrintWriter.print(str2);
            }
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void addFrontendResource(FrontendResource frontendResource) {
        Iterator<FrontendResource> it = getFrontendResources().values().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            FrontendResource next = it.next();
            if (next.getExclusiveGroupId() == frontendResource.getExclusiveGroupId()) {
                frontendResource.addExclusiveGroupMemberFeHandle(next.getHandle());
                frontendResource.addExclusiveGroupMemberFeHandles(next.getExclusiveGroupMemberFeHandles());
                for (Integer num : next.getExclusiveGroupMemberFeHandles()) {
                    getFrontendResource(num.intValue()).addExclusiveGroupMemberFeHandle(frontendResource.getHandle());
                }
                next.addExclusiveGroupMemberFeHandle(frontendResource.getHandle());
            }
        }
        this.mFrontendResources.put(Integer.valueOf(frontendResource.getHandle()), frontendResource);
        increFrontendNum(this.mFrontendExistingNums, frontendResource.getType());
    }

    public final void addDemuxResource(DemuxResource demuxResource) {
        this.mDemuxResources.put(Integer.valueOf(demuxResource.getHandle()), demuxResource);
    }

    public final void removeFrontendResource(int i) {
        FrontendResource frontendResource = getFrontendResource(i);
        if (frontendResource == null) {
            return;
        }
        if (frontendResource.isInUse()) {
            ClientProfile clientProfile = getClientProfile(frontendResource.getOwnerClientId());
            for (Integer num : clientProfile.getShareFeClientIds()) {
                clearFrontendAndClientMapping(getClientProfile(num.intValue()));
            }
            clearFrontendAndClientMapping(clientProfile);
        }
        for (Integer num2 : frontendResource.getExclusiveGroupMemberFeHandles()) {
            getFrontendResource(num2.intValue()).removeExclusiveGroupMemberFeId(frontendResource.getHandle());
        }
        decreFrontendNum(this.mFrontendExistingNums, frontendResource.getType());
        this.mFrontendResources.remove(Integer.valueOf(i));
    }

    public final void removeDemuxResource(int i) {
        DemuxResource demuxResource = getDemuxResource(i);
        if (demuxResource == null) {
            return;
        }
        if (demuxResource.isInUse()) {
            releaseDemuxInternal(demuxResource);
        }
        this.mDemuxResources.remove(Integer.valueOf(i));
    }

    @VisibleForTesting
    public LnbResource getLnbResource(int i) {
        return this.mLnbResources.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public Map<Integer, LnbResource> getLnbResources() {
        return this.mLnbResources;
    }

    public final void addLnbResource(LnbResource lnbResource) {
        this.mLnbResources.put(Integer.valueOf(lnbResource.getHandle()), lnbResource);
    }

    public final void removeLnbResource(int i) {
        LnbResource lnbResource = getLnbResource(i);
        if (lnbResource == null) {
            return;
        }
        if (lnbResource.isInUse()) {
            releaseLnbInternal(lnbResource);
        }
        this.mLnbResources.remove(Integer.valueOf(i));
    }

    @VisibleForTesting
    public CasResource getCasResource(int i) {
        return this.mCasResources.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public CiCamResource getCiCamResource(int i) {
        return this.mCiCamResources.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public Map<Integer, CasResource> getCasResources() {
        return this.mCasResources;
    }

    @VisibleForTesting
    public Map<Integer, CiCamResource> getCiCamResources() {
        return this.mCiCamResources;
    }

    public final void addCasResource(CasResource casResource) {
        this.mCasResources.put(Integer.valueOf(casResource.getSystemId()), casResource);
    }

    public final void addCiCamResource(CiCamResource ciCamResource) {
        this.mCiCamResources.put(Integer.valueOf(ciCamResource.getCiCamId()), ciCamResource);
    }

    public final void removeCasResource(int i) {
        CasResource casResource = getCasResource(i);
        if (casResource == null) {
            return;
        }
        for (Integer num : casResource.getOwnerClientIds()) {
            getClientProfile(num.intValue()).releaseCas();
        }
        this.mCasResources.remove(Integer.valueOf(i));
    }

    public final void removeCiCamResource(int i) {
        CiCamResource ciCamResource = getCiCamResource(i);
        if (ciCamResource == null) {
            return;
        }
        for (Integer num : ciCamResource.getOwnerClientIds()) {
            getClientProfile(num.intValue()).releaseCiCam();
        }
        this.mCiCamResources.remove(Integer.valueOf(i));
    }

    @VisibleForTesting
    public ClientProfile getClientProfile(int i) {
        return this.mClientProfiles.get(Integer.valueOf(i));
    }

    public final void addClientProfile(int i, ClientProfile clientProfile, IResourcesReclaimListener iResourcesReclaimListener) {
        this.mClientProfiles.put(Integer.valueOf(i), clientProfile);
        addResourcesReclaimListener(i, iResourcesReclaimListener);
    }

    public final void removeClientProfile(int i) {
        for (Integer num : getClientProfile(i).getShareFeClientIds()) {
            clearFrontendAndClientMapping(getClientProfile(num.intValue()));
        }
        clearAllResourcesAndClientMapping(getClientProfile(i));
        this.mClientProfiles.remove(Integer.valueOf(i));
        this.mListeners.remove(Integer.valueOf(i));
    }

    public final void clearFrontendAndClientMapping(ClientProfile clientProfile) {
        FrontendResource frontendResource;
        if (clientProfile == null) {
            return;
        }
        for (Integer num : clientProfile.getInUseFrontendHandles()) {
            FrontendResource frontendResource2 = getFrontendResource(num.intValue());
            int ownerClientId = frontendResource2.getOwnerClientId();
            if (ownerClientId == clientProfile.getId()) {
                frontendResource2.removeOwner();
            } else {
                ClientProfile clientProfile2 = getClientProfile(ownerClientId);
                if (clientProfile2 != null) {
                    clientProfile2.stopSharingFrontend(clientProfile.getId());
                }
            }
        }
        int primaryFrontend = clientProfile.getPrimaryFrontend();
        if (primaryFrontend != -1 && (frontendResource = getFrontendResource(primaryFrontend)) != null) {
            decreFrontendNum(this.mFrontendUsedNums, frontendResource.getType());
        }
        clientProfile.releaseFrontend();
    }

    public final void clearAllResourcesAndClientMapping(ClientProfile clientProfile) {
        if (clientProfile == null) {
            return;
        }
        for (Integer num : clientProfile.getInUseLnbHandles()) {
            getLnbResource(num.intValue()).removeOwner();
        }
        if (clientProfile.getInUseCasSystemId() != -1) {
            getCasResource(clientProfile.getInUseCasSystemId()).removeOwner(clientProfile.getId());
        }
        if (clientProfile.getInUseCiCamId() != -1) {
            getCiCamResource(clientProfile.getInUseCiCamId()).removeOwner(clientProfile.getId());
        }
        for (Integer num2 : clientProfile.getInUseDemuxHandles()) {
            getDemuxResource(num2.intValue()).removeOwner();
        }
        clearFrontendAndClientMapping(clientProfile);
        clientProfile.reclaimAllResources();
    }

    @VisibleForTesting
    public boolean checkClientExists(int i) {
        return this.mClientProfiles.keySet().contains(Integer.valueOf(i));
    }

    public final int generateResourceHandle(int i, int i2) {
        int i3 = ((i & 255) << 24) | (i2 << 16);
        int i4 = this.mResourceRequestCount;
        this.mResourceRequestCount = i4 + 1;
        return (65535 & i4) | i3;
    }

    public final void enforceTrmAccessPermission(String str) {
        Context context = getContext();
        context.enforceCallingOrSelfPermission("android.permission.TUNER_RESOURCE_ACCESS", "TunerResourceManagerService: " + str);
    }

    public final void enforceTunerAccessPermission(String str) {
        Context context = getContext();
        context.enforceCallingPermission("android.permission.ACCESS_TV_TUNER", "TunerResourceManagerService: " + str);
    }

    public final void enforceDescramblerAccessPermission(String str) {
        Context context = getContext();
        context.enforceCallingPermission("android.permission.ACCESS_TV_DESCRAMBLER", "TunerResourceManagerService: " + str);
    }
}
