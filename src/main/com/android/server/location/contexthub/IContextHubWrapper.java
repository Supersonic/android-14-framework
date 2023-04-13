package com.android.server.location.contexthub;

import android.hardware.contexthub.ContextHubMessage;
import android.hardware.contexthub.HostEndpointInfo;
import android.hardware.contexthub.IContextHub;
import android.hardware.contexthub.IContextHubCallback;
import android.hardware.contexthub.NanSessionRequest;
import android.hardware.contexthub.NanoappInfo;
import android.hardware.contexthub.V1_0.ContextHub;
import android.hardware.contexthub.V1_0.ContextHubMsg;
import android.hardware.contexthub.V1_0.HubAppInfo;
import android.hardware.contexthub.V1_0.IContexthub;
import android.hardware.contexthub.V1_2.IContexthub;
import android.hardware.contexthub.V1_2.IContexthubCallback;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.NanoAppBinary;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppState;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Pair;
import com.android.server.location.contexthub.IContextHubWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class IContextHubWrapper {

    /* loaded from: classes.dex */
    public interface ICallback {
        void handleContextHubEvent(int i);

        void handleNanoappAbort(long j, int i);

        void handleNanoappInfo(List<NanoAppState> list);

        void handleNanoappMessage(short s, NanoAppMessage nanoAppMessage, List<String> list, List<String> list2);

        void handleServiceRestart();

        void handleTransactionResult(int i, boolean z);
    }

    public abstract int disableNanoapp(int i, long j, int i2) throws RemoteException;

    public abstract int enableNanoapp(int i, long j, int i2) throws RemoteException;

    public abstract Pair<List<ContextHubInfo>, List<String>> getHubs() throws RemoteException;

    public abstract long[] getPreloadedNanoappIds(int i);

    public abstract int loadNanoapp(int i, NanoAppBinary nanoAppBinary, int i2) throws RemoteException;

    public abstract void onAirplaneModeSettingChanged(boolean z);

    public abstract void onBtMainSettingChanged(boolean z);

    public abstract void onBtScanningSettingChanged(boolean z);

    public void onHostEndpointConnected(HostEndpointInfo hostEndpointInfo) {
    }

    public void onHostEndpointDisconnected(short s) {
    }

    public abstract void onLocationSettingChanged(boolean z);

    public abstract void onMicrophoneSettingChanged(boolean z);

    public abstract void onWifiMainSettingChanged(boolean z);

    public abstract void onWifiScanningSettingChanged(boolean z);

    public abstract void onWifiSettingChanged(boolean z);

    public abstract int queryNanoapps(int i) throws RemoteException;

    public abstract void registerCallback(int i, ICallback iCallback) throws RemoteException;

    public abstract void registerExistingCallback(int i) throws RemoteException;

    public abstract int sendMessageToContextHub(short s, int i, NanoAppMessage nanoAppMessage) throws RemoteException;

    public abstract boolean setTestMode(boolean z);

    public abstract boolean supportsAirplaneModeSettingNotifications();

    public abstract boolean supportsBtSettingNotifications();

    public abstract boolean supportsLocationSettingNotifications();

    public abstract boolean supportsMicrophoneSettingNotifications();

    public abstract boolean supportsWifiSettingNotifications();

    public abstract int unloadNanoapp(int i, long j, int i2) throws RemoteException;

    public static IContextHubWrapper getContextHubWrapper() {
        IContextHubWrapper maybeConnectToAidl = maybeConnectToAidl();
        if (maybeConnectToAidl == null) {
            maybeConnectToAidl = maybeConnectTo1_2();
        }
        if (maybeConnectToAidl == null) {
            maybeConnectToAidl = maybeConnectTo1_1();
        }
        return maybeConnectToAidl == null ? maybeConnectTo1_0() : maybeConnectToAidl;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0019  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static IContextHubWrapper maybeConnectTo1_0() {
        IContexthub iContexthub;
        try {
            iContexthub = IContexthub.getService(true);
        } catch (RemoteException e) {
            Log.e("IContextHubWrapper", "RemoteException while attaching to Context Hub HAL proxy", e);
            iContexthub = null;
            if (iContexthub == null) {
            }
        } catch (NoSuchElementException unused) {
            Log.i("IContextHubWrapper", "Context Hub HAL service not found");
            iContexthub = null;
            if (iContexthub == null) {
            }
        }
        if (iContexthub == null) {
            return null;
        }
        return new ContextHubWrapperV1_0(iContexthub);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0019  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static IContextHubWrapper maybeConnectTo1_1() {
        android.hardware.contexthub.V1_1.IContexthub iContexthub;
        try {
            iContexthub = android.hardware.contexthub.V1_1.IContexthub.getService(true);
        } catch (RemoteException e) {
            Log.e("IContextHubWrapper", "RemoteException while attaching to Context Hub HAL proxy", e);
            iContexthub = null;
            if (iContexthub == null) {
            }
        } catch (NoSuchElementException unused) {
            Log.i("IContextHubWrapper", "Context Hub HAL service not found");
            iContexthub = null;
            if (iContexthub == null) {
            }
        }
        if (iContexthub == null) {
            return null;
        }
        return new ContextHubWrapperV1_1(iContexthub);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0019  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static IContextHubWrapper maybeConnectTo1_2() {
        android.hardware.contexthub.V1_2.IContexthub iContexthub;
        try {
            iContexthub = android.hardware.contexthub.V1_2.IContexthub.getService(true);
        } catch (RemoteException e) {
            Log.e("IContextHubWrapper", "RemoteException while attaching to Context Hub HAL proxy", e);
            iContexthub = null;
            if (iContexthub == null) {
            }
        } catch (NoSuchElementException unused) {
            Log.i("IContextHubWrapper", "Context Hub HAL service not found");
            iContexthub = null;
            if (iContexthub == null) {
            }
        }
        if (iContexthub == null) {
            return null;
        }
        return new ContextHubWrapperV1_2(iContexthub);
    }

    public static IContextHub maybeConnectToAidlGetProxy() {
        String str = IContextHub.class.getCanonicalName() + "/default";
        if (ServiceManager.isDeclared(str)) {
            IContextHub asInterface = IContextHub.Stub.asInterface(ServiceManager.waitForService(str));
            if (asInterface == null) {
                Log.e("IContextHubWrapper", "Context Hub AIDL service was declared but was not found");
                return asInterface;
            }
            return asInterface;
        }
        Log.d("IContextHubWrapper", "Context Hub AIDL service is not declared");
        return null;
    }

    public static IContextHubWrapper maybeConnectToAidl() {
        IContextHub maybeConnectToAidlGetProxy = maybeConnectToAidlGetProxy();
        if (maybeConnectToAidlGetProxy == null) {
            return null;
        }
        return new ContextHubWrapperAidl(maybeConnectToAidlGetProxy);
    }

    /* loaded from: classes.dex */
    public static class ContextHubWrapperAidl extends IContextHubWrapper implements IBinder.DeathRecipient {
        public Handler mHandler;
        public IContextHub mHub;
        public final Map<Integer, ContextHubAidlCallback> mAidlCallbackMap = new HashMap();
        public Runnable mHandleServiceRestartCallback = null;
        public HandlerThread mHandlerThread = new HandlerThread("Context Hub AIDL callback", 10);

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsAirplaneModeSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsBtSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsLocationSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsMicrophoneSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsWifiSettingNotifications() {
            return true;
        }

        /* loaded from: classes.dex */
        public class ContextHubAidlCallback extends IContextHubCallback.Stub {
            public final ICallback mCallback;
            public final int mContextHubId;

            public String getInterfaceHash() {
                return "notfrozen";
            }

            public int getInterfaceVersion() {
                return 2;
            }

            public void handleNanSessionRequest(NanSessionRequest nanSessionRequest) {
            }

            public ContextHubAidlCallback(int i, ICallback iCallback) {
                this.mContextHubId = i;
                this.mCallback = iCallback;
            }

            public void handleNanoappInfo(NanoappInfo[] nanoappInfoArr) {
                final List<NanoAppState> createNanoAppStateList = ContextHubServiceUtil.createNanoAppStateList(nanoappInfoArr);
                ContextHubWrapperAidl.this.mHandler.post(new Runnable() { // from class: com.android.server.location.contexthub.IContextHubWrapper$ContextHubWrapperAidl$ContextHubAidlCallback$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        IContextHubWrapper.ContextHubWrapperAidl.ContextHubAidlCallback.this.lambda$handleNanoappInfo$0(createNanoAppStateList);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$handleNanoappInfo$0(List list) {
                this.mCallback.handleNanoappInfo(list);
            }

            public void handleContextHubMessage(final ContextHubMessage contextHubMessage, final String[] strArr) {
                ContextHubWrapperAidl.this.mHandler.post(new Runnable() { // from class: com.android.server.location.contexthub.IContextHubWrapper$ContextHubWrapperAidl$ContextHubAidlCallback$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        IContextHubWrapper.ContextHubWrapperAidl.ContextHubAidlCallback.this.lambda$handleContextHubMessage$1(contextHubMessage, strArr);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$handleContextHubMessage$1(ContextHubMessage contextHubMessage, String[] strArr) {
                this.mCallback.handleNanoappMessage((short) contextHubMessage.hostEndPoint, ContextHubServiceUtil.createNanoAppMessage(contextHubMessage), new ArrayList(Arrays.asList(contextHubMessage.permissions)), new ArrayList(Arrays.asList(strArr)));
            }

            public void handleContextHubAsyncEvent(final int i) {
                ContextHubWrapperAidl.this.mHandler.post(new Runnable() { // from class: com.android.server.location.contexthub.IContextHubWrapper$ContextHubWrapperAidl$ContextHubAidlCallback$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        IContextHubWrapper.ContextHubWrapperAidl.ContextHubAidlCallback.this.lambda$handleContextHubAsyncEvent$2(i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$handleContextHubAsyncEvent$2(int i) {
                this.mCallback.handleContextHubEvent(ContextHubServiceUtil.toContextHubEventFromAidl(i));
            }

            public void handleTransactionResult(final int i, final boolean z) {
                ContextHubWrapperAidl.this.mHandler.post(new Runnable() { // from class: com.android.server.location.contexthub.IContextHubWrapper$ContextHubWrapperAidl$ContextHubAidlCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        IContextHubWrapper.ContextHubWrapperAidl.ContextHubAidlCallback.this.lambda$handleTransactionResult$3(i, z);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$handleTransactionResult$3(int i, boolean z) {
                this.mCallback.handleTransactionResult(i, z);
            }
        }

        public ContextHubWrapperAidl(IContextHub iContextHub) {
            setHub(iContextHub);
            this.mHandlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper());
            linkWrapperToHubDeath();
        }

        public final synchronized IContextHub getHub() {
            return this.mHub;
        }

        public final synchronized void setHub(IContextHub iContextHub) {
            this.mHub = iContextHub;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.i("IContextHubWrapper", "Context Hub AIDL HAL died");
            setHub(IContextHubWrapper.maybeConnectToAidlGetProxy());
            if (getHub() == null) {
                Log.e("IContextHubWrapper", "Could not reconnect to Context Hub AIDL HAL");
                return;
            }
            linkWrapperToHubDeath();
            Runnable runnable = this.mHandleServiceRestartCallback;
            if (runnable != null) {
                runnable.run();
            } else {
                Log.e("IContextHubWrapper", "mHandleServiceRestartCallback is not set");
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public Pair<List<ContextHubInfo>, List<String>> getHubs() throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return new Pair<>(new ArrayList(), new ArrayList());
            }
            HashSet hashSet = new HashSet();
            ArrayList arrayList = new ArrayList();
            for (android.hardware.contexthub.ContextHubInfo contextHubInfo : hub.getContextHubs()) {
                arrayList.add(new ContextHubInfo(contextHubInfo));
                for (String str : contextHubInfo.supportedPermissions) {
                    hashSet.add(str);
                }
            }
            return new Pair<>(arrayList, new ArrayList(hashSet));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onLocationSettingChanged(boolean z) {
            onSettingChanged((byte) 1, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onAirplaneModeSettingChanged(boolean z) {
            onSettingChanged((byte) 4, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onMicrophoneSettingChanged(boolean z) {
            onSettingChanged((byte) 5, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiMainSettingChanged(boolean z) {
            onSettingChanged((byte) 2, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiScanningSettingChanged(boolean z) {
            onSettingChanged((byte) 3, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onBtMainSettingChanged(boolean z) {
            onSettingChanged((byte) 6, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onBtScanningSettingChanged(boolean z) {
            onSettingChanged((byte) 7, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onHostEndpointConnected(HostEndpointInfo hostEndpointInfo) {
            IContextHub hub = getHub();
            if (hub == null) {
                return;
            }
            try {
                hub.onHostEndpointConnected(hostEndpointInfo);
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e("IContextHubWrapper", "Exception in onHostEndpointConnected" + e.getMessage());
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onHostEndpointDisconnected(short s) {
            IContextHub hub = getHub();
            if (hub == null) {
                return;
            }
            try {
                hub.onHostEndpointDisconnected((char) s);
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e("IContextHubWrapper", "Exception in onHostEndpointDisconnected" + e.getMessage());
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int sendMessageToContextHub(short s, int i, NanoAppMessage nanoAppMessage) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.sendMessageToHub(i, ContextHubServiceUtil.createAidlContextHubMessage(s, nanoAppMessage));
                return 0;
            } catch (RemoteException | ServiceSpecificException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int loadNanoapp(int i, NanoAppBinary nanoAppBinary, int i2) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.loadNanoapp(i, ContextHubServiceUtil.createAidlNanoAppBinary(nanoAppBinary), i2);
                return 0;
            } catch (RemoteException | ServiceSpecificException | UnsupportedOperationException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int unloadNanoapp(int i, long j, int i2) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.unloadNanoapp(i, j, i2);
                return 0;
            } catch (RemoteException | ServiceSpecificException | UnsupportedOperationException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int enableNanoapp(int i, long j, int i2) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.enableNanoapp(i, j, i2);
                return 0;
            } catch (RemoteException | ServiceSpecificException | UnsupportedOperationException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int disableNanoapp(int i, long j, int i2) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.disableNanoapp(i, j, i2);
                return 0;
            } catch (RemoteException | ServiceSpecificException | UnsupportedOperationException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int queryNanoapps(int i) throws RemoteException {
            IContextHub hub = getHub();
            if (hub == null) {
                return 2;
            }
            try {
                hub.queryNanoapps(i);
                return 0;
            } catch (RemoteException | ServiceSpecificException | UnsupportedOperationException unused) {
                return 1;
            } catch (IllegalArgumentException unused2) {
                return 2;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public long[] getPreloadedNanoappIds(int i) {
            IContextHub hub = getHub();
            if (hub == null) {
                return null;
            }
            try {
                return hub.getPreloadedNanoappIds(i);
            } catch (RemoteException e) {
                Log.e("IContextHubWrapper", "Exception while getting preloaded nanoapp IDs: " + e.getMessage());
                return null;
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void registerExistingCallback(int i) {
            IContextHub hub = getHub();
            if (hub == null) {
                return;
            }
            ContextHubAidlCallback contextHubAidlCallback = this.mAidlCallbackMap.get(Integer.valueOf(i));
            if (contextHubAidlCallback == null) {
                Log.e("IContextHubWrapper", "Could not find existing callback to register for context hub ID = " + i);
                return;
            }
            try {
                hub.registerCallback(i, contextHubAidlCallback);
            } catch (RemoteException | ServiceSpecificException | IllegalArgumentException e) {
                Log.e("IContextHubWrapper", "Exception while registering callback: " + e.getMessage());
            }
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void registerCallback(int i, final ICallback iCallback) {
            if (getHub() == null) {
                return;
            }
            Objects.requireNonNull(iCallback);
            this.mHandleServiceRestartCallback = new Runnable() { // from class: com.android.server.location.contexthub.IContextHubWrapper$ContextHubWrapperAidl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    IContextHubWrapper.ICallback.this.handleServiceRestart();
                }
            };
            this.mAidlCallbackMap.put(Integer.valueOf(i), new ContextHubAidlCallback(i, iCallback));
            registerExistingCallback(i);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean setTestMode(boolean z) {
            IContextHub hub = getHub();
            if (hub == null) {
                return false;
            }
            try {
                hub.setTestMode(z);
                return true;
            } catch (RemoteException | ServiceSpecificException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Exception while setting test mode (enable: ");
                sb.append(z ? "true" : "false");
                sb.append("): ");
                sb.append(e.getMessage());
                Log.e("IContextHubWrapper", sb.toString());
                return false;
            }
        }

        public final void onSettingChanged(byte b, boolean z) {
            IContextHub hub = getHub();
            if (hub == null) {
                return;
            }
            try {
                hub.onSettingChanged(b, z);
            } catch (RemoteException | ServiceSpecificException e) {
                Log.e("IContextHubWrapper", "Exception while sending setting update: " + e.getMessage());
            }
        }

        public final void linkWrapperToHubDeath() {
            IContextHub hub = getHub();
            if (hub == null) {
                return;
            }
            try {
                hub.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
                Log.e("IContextHubWrapper", "Context Hub AIDL service death receipt could not be linked");
            }
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ContextHubWrapperHidl extends IContextHubWrapper {
        public ICallback mCallback = null;
        public final Map<Integer, ContextHubWrapperHidlCallback> mHidlCallbackMap = new HashMap();
        public IContexthub mHub;

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public long[] getPreloadedNanoappIds(int i) {
            return new long[0];
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onBtMainSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onBtScanningSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiMainSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiScanningSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean setTestMode(boolean z) {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsBtSettingNotifications() {
            return false;
        }

        /* loaded from: classes.dex */
        public class ContextHubWrapperHidlCallback extends IContexthubCallback.Stub {
            public final ICallback mCallback;
            public final int mContextHubId;

            public ContextHubWrapperHidlCallback(int i, ICallback iCallback) {
                this.mContextHubId = i;
                this.mCallback = iCallback;
            }

            public void handleClientMsg(ContextHubMsg contextHubMsg) {
                this.mCallback.handleNanoappMessage(contextHubMsg.hostEndPoint, ContextHubServiceUtil.createNanoAppMessage(contextHubMsg), Collections.emptyList(), Collections.emptyList());
            }

            public void handleTxnResult(int i, int i2) {
                this.mCallback.handleTransactionResult(i, i2 == 0);
            }

            public void handleHubEvent(int i) {
                this.mCallback.handleContextHubEvent(ContextHubServiceUtil.toContextHubEvent(i));
            }

            public void handleAppAbort(long j, int i) {
                this.mCallback.handleNanoappAbort(j, i);
            }

            public void handleAppsInfo(ArrayList<HubAppInfo> arrayList) {
                handleAppsInfo_1_2(ContextHubServiceUtil.toHubAppInfo_1_2(arrayList));
            }

            public void handleClientMsg_1_2(android.hardware.contexthub.V1_2.ContextHubMsg contextHubMsg, ArrayList<String> arrayList) {
                ICallback iCallback = this.mCallback;
                ContextHubMsg contextHubMsg2 = contextHubMsg.msg_1_0;
                iCallback.handleNanoappMessage(contextHubMsg2.hostEndPoint, ContextHubServiceUtil.createNanoAppMessage(contextHubMsg2), contextHubMsg.permissions, arrayList);
            }

            public void handleAppsInfo_1_2(ArrayList<android.hardware.contexthub.V1_2.HubAppInfo> arrayList) {
                this.mCallback.handleNanoappInfo(ContextHubServiceUtil.createNanoAppStateList(arrayList));
            }
        }

        public ContextHubWrapperHidl(IContexthub iContexthub) {
            this.mHub = iContexthub;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int sendMessageToContextHub(short s, int i, NanoAppMessage nanoAppMessage) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.sendMessageToHub(i, ContextHubServiceUtil.createHidlContextHubMessage(s, nanoAppMessage)));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int loadNanoapp(int i, NanoAppBinary nanoAppBinary, int i2) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.loadNanoApp(i, ContextHubServiceUtil.createHidlNanoAppBinary(nanoAppBinary), i2));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int unloadNanoapp(int i, long j, int i2) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.unloadNanoApp(i, j, i2));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int enableNanoapp(int i, long j, int i2) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.enableNanoApp(i, j, i2));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int disableNanoapp(int i, long j, int i2) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.disableNanoApp(i, j, i2));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public int queryNanoapps(int i) throws RemoteException {
            return ContextHubServiceUtil.toTransactionResult(this.mHub.queryApps(i));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void registerCallback(int i, ICallback iCallback) throws RemoteException {
            this.mHidlCallbackMap.put(Integer.valueOf(i), new ContextHubWrapperHidlCallback(i, iCallback));
            this.mHub.registerCallback(i, this.mHidlCallbackMap.get(Integer.valueOf(i)));
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void registerExistingCallback(int i) throws RemoteException {
            ContextHubWrapperHidlCallback contextHubWrapperHidlCallback = this.mHidlCallbackMap.get(Integer.valueOf(i));
            if (contextHubWrapperHidlCallback == null) {
                Log.e("IContextHubWrapper", "Could not find existing callback for context hub with ID = " + i);
                return;
            }
            this.mHub.registerCallback(i, contextHubWrapperHidlCallback);
        }
    }

    /* loaded from: classes.dex */
    public static class ContextHubWrapperV1_0 extends ContextHubWrapperHidl {
        public IContexthub mHub;

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onAirplaneModeSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onLocationSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onMicrophoneSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsAirplaneModeSettingNotifications() {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsLocationSettingNotifications() {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsMicrophoneSettingNotifications() {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsWifiSettingNotifications() {
            return false;
        }

        public ContextHubWrapperV1_0(IContexthub iContexthub) {
            super(iContexthub);
            this.mHub = iContexthub;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public Pair<List<ContextHubInfo>, List<String>> getHubs() throws RemoteException {
            ArrayList arrayList = new ArrayList();
            Iterator it = this.mHub.getHubs().iterator();
            while (it.hasNext()) {
                arrayList.add(new ContextHubInfo((ContextHub) it.next()));
            }
            return new Pair<>(arrayList, new ArrayList());
        }
    }

    /* loaded from: classes.dex */
    public static class ContextHubWrapperV1_1 extends ContextHubWrapperHidl {
        public android.hardware.contexthub.V1_1.IContexthub mHub;

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onAirplaneModeSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onMicrophoneSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiSettingChanged(boolean z) {
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsAirplaneModeSettingNotifications() {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsLocationSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsMicrophoneSettingNotifications() {
            return false;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsWifiSettingNotifications() {
            return false;
        }

        public ContextHubWrapperV1_1(android.hardware.contexthub.V1_1.IContexthub iContexthub) {
            super(iContexthub);
            this.mHub = iContexthub;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public Pair<List<ContextHubInfo>, List<String>> getHubs() throws RemoteException {
            ArrayList arrayList = new ArrayList();
            Iterator it = this.mHub.getHubs().iterator();
            while (it.hasNext()) {
                arrayList.add(new ContextHubInfo((ContextHub) it.next()));
            }
            return new Pair<>(arrayList, new ArrayList());
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onLocationSettingChanged(boolean z) {
            try {
                this.mHub.onSettingChanged((byte) 0, z ? (byte) 1 : (byte) 0);
            } catch (RemoteException e) {
                Log.e("IContextHubWrapper", "Failed to send setting change to Contexthub", e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ContextHubWrapperV1_2 extends ContextHubWrapperHidl implements IContexthub.getHubs_1_2Callback {
        public final android.hardware.contexthub.V1_2.IContexthub mHub;
        public Pair<List<ContextHubInfo>, List<String>> mHubInfo;

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsAirplaneModeSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsLocationSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsMicrophoneSettingNotifications() {
            return true;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public boolean supportsWifiSettingNotifications() {
            return true;
        }

        public ContextHubWrapperV1_2(android.hardware.contexthub.V1_2.IContexthub iContexthub) {
            super(iContexthub);
            this.mHubInfo = new Pair<>(Collections.emptyList(), Collections.emptyList());
            this.mHub = iContexthub;
        }

        public void onValues(ArrayList<ContextHub> arrayList, ArrayList<String> arrayList2) {
            ArrayList arrayList3 = new ArrayList();
            Iterator<ContextHub> it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList3.add(new ContextHubInfo(it.next()));
            }
            this.mHubInfo = new Pair<>(arrayList3, arrayList2);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public Pair<List<ContextHubInfo>, List<String>> getHubs() throws RemoteException {
            this.mHub.getHubs_1_2(this);
            return this.mHubInfo;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onLocationSettingChanged(boolean z) {
            sendSettingChanged((byte) 0, z ? (byte) 1 : (byte) 0);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onWifiSettingChanged(boolean z) {
            sendSettingChanged((byte) 1, z ? (byte) 1 : (byte) 0);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onAirplaneModeSettingChanged(boolean z) {
            sendSettingChanged((byte) 2, z ? (byte) 1 : (byte) 0);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper
        public void onMicrophoneSettingChanged(boolean z) {
            sendSettingChanged((byte) 3, z ? (byte) 1 : (byte) 0);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ContextHubWrapperHidl, com.android.server.location.contexthub.IContextHubWrapper
        public void registerCallback(int i, ICallback iCallback) throws RemoteException {
            this.mHidlCallbackMap.put(Integer.valueOf(i), new ContextHubWrapperHidl.ContextHubWrapperHidlCallback(i, iCallback));
            this.mHub.registerCallback_1_2(i, this.mHidlCallbackMap.get(Integer.valueOf(i)));
        }

        public final void sendSettingChanged(byte b, byte b2) {
            try {
                this.mHub.onSettingChanged_1_2(b, b2);
            } catch (RemoteException e) {
                Log.e("IContextHubWrapper", "Failed to send setting change to Contexthub", e);
            }
        }
    }
}
