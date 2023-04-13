package com.android.server.hdmi;

import android.hardware.p002tv.hdmi.earc.IEArc;
import android.hardware.p002tv.hdmi.earc.IEArcCallback;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.hdmi.HdmiEarcController;
/* loaded from: classes.dex */
public final class HdmiEarcController {
    public Handler mControlHandler;
    public EarcNativeWrapper mEarcNativeWrapperImpl;
    public final HdmiControlService mService;

    /* loaded from: classes.dex */
    public interface EarcNativeWrapper {
        byte[] nativeGetLastReportedAudioCapabilities(int i);

        byte nativeGetState(int i);

        boolean nativeInit();

        boolean nativeIsEarcEnabled();

        void nativeSetCallback(EarcAidlCallback earcAidlCallback);

        void nativeSetEarcEnabled(boolean z);
    }

    /* loaded from: classes.dex */
    public static final class EarcNativeWrapperImpl implements EarcNativeWrapper, IBinder.DeathRecipient {
        public IEArc mEarc;
        public EarcAidlCallback mEarcCallback;

        public EarcNativeWrapperImpl() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mEarc.asBinder().unlinkToDeath(this, 0);
            connectToHal();
            EarcAidlCallback earcAidlCallback = this.mEarcCallback;
            if (earcAidlCallback != null) {
                nativeSetCallback(earcAidlCallback);
            }
        }

        public boolean connectToHal() {
            IEArc asInterface = IEArc.Stub.asInterface(ServiceManager.getService(IEArc.DESCRIPTOR + "/default"));
            this.mEarc = asInterface;
            if (asInterface == null) {
                return false;
            }
            try {
                asInterface.asBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                HdmiLogger.error("Couldn't link callback object: ", e, new Object[0]);
                return true;
            }
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public boolean nativeInit() {
            return connectToHal();
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public void nativeSetEarcEnabled(boolean z) {
            try {
                this.mEarc.setEArcEnabled(z);
            } catch (ServiceSpecificException e) {
                HdmiLogger.error("Could not set eARC enabled to " + z + ". Error: ", Integer.valueOf(e.errorCode));
            } catch (RemoteException e2) {
                HdmiLogger.error("Could not set eARC enabled to " + z + ":. Exception: ", e2, new Object[0]);
            }
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public boolean nativeIsEarcEnabled() {
            try {
                return this.mEarc.isEArcEnabled();
            } catch (RemoteException e) {
                HdmiLogger.error("Could not read if eARC is enabled. Exception: ", e, new Object[0]);
                return false;
            }
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public void nativeSetCallback(EarcAidlCallback earcAidlCallback) {
            this.mEarcCallback = earcAidlCallback;
            try {
                this.mEarc.setCallback(earcAidlCallback);
            } catch (RemoteException e) {
                HdmiLogger.error("Could not set callback. Exception: ", e, new Object[0]);
            }
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public byte nativeGetState(int i) {
            try {
                return this.mEarc.getState(i);
            } catch (RemoteException e) {
                HdmiLogger.error("Could not get eARC state. Exception: ", e, new Object[0]);
                return (byte) -1;
            }
        }

        @Override // com.android.server.hdmi.HdmiEarcController.EarcNativeWrapper
        public byte[] nativeGetLastReportedAudioCapabilities(int i) {
            try {
                return this.mEarc.getLastReportedAudioCapabilities(i);
            } catch (RemoteException e) {
                HdmiLogger.error("Could not read last reported audio capabilities. Exception: ", e, new Object[0]);
                return null;
            }
        }
    }

    public HdmiEarcController(HdmiControlService hdmiControlService, EarcNativeWrapper earcNativeWrapper) {
        this.mService = hdmiControlService;
        this.mEarcNativeWrapperImpl = earcNativeWrapper;
    }

    public static HdmiEarcController create(HdmiControlService hdmiControlService) {
        return createWithNativeWrapper(hdmiControlService, new EarcNativeWrapperImpl());
    }

    public static HdmiEarcController createWithNativeWrapper(HdmiControlService hdmiControlService, EarcNativeWrapper earcNativeWrapper) {
        HdmiEarcController hdmiEarcController = new HdmiEarcController(hdmiControlService, earcNativeWrapper);
        if (hdmiEarcController.init(earcNativeWrapper)) {
            return hdmiEarcController;
        }
        HdmiLogger.warning("Could not connect to eARC AIDL HAL.", new Object[0]);
        return null;
    }

    public final boolean init(EarcNativeWrapper earcNativeWrapper) {
        if (earcNativeWrapper.nativeInit()) {
            this.mControlHandler = new Handler(this.mService.getServiceLooper());
            this.mEarcNativeWrapperImpl.nativeSetCallback(new EarcAidlCallback());
            return true;
        }
        return false;
    }

    public final void assertRunOnServiceThread() {
        if (Looper.myLooper() != this.mControlHandler.getLooper()) {
            throw new IllegalStateException("Should run on service thread.");
        }
    }

    @VisibleForTesting
    public void runOnServiceThread(Runnable runnable) {
        this.mControlHandler.post(new WorkSourceUidPreservingRunnable(runnable));
    }

    public void setEarcEnabled(boolean z) {
        assertRunOnServiceThread();
        this.mEarcNativeWrapperImpl.nativeSetEarcEnabled(z);
    }

    /* loaded from: classes.dex */
    public final class EarcAidlCallback extends IEArcCallback.Stub {
        @Override // android.hardware.p002tv.hdmi.earc.IEArcCallback
        public int getInterfaceVersion() throws RemoteException {
            return 1;
        }

        public EarcAidlCallback() {
        }

        @Override // android.hardware.p002tv.hdmi.earc.IEArcCallback
        public void onStateChange(final byte b, final int i) {
            HdmiEarcController.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiEarcController$EarcAidlCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    HdmiEarcController.EarcAidlCallback.this.lambda$onStateChange$0(b, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStateChange$0(byte b, int i) {
            HdmiEarcController.this.mService.handleEarcStateChange(b, i);
        }

        @Override // android.hardware.p002tv.hdmi.earc.IEArcCallback
        public void onCapabilitiesReported(final byte[] bArr, final int i) {
            HdmiEarcController.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiEarcController$EarcAidlCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HdmiEarcController.EarcAidlCallback.this.lambda$onCapabilitiesReported$1(bArr, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapabilitiesReported$1(byte[] bArr, int i) {
            HdmiEarcController.this.mService.handleEarcCapabilitiesReported(bArr, i);
        }

        @Override // android.hardware.p002tv.hdmi.earc.IEArcCallback
        public synchronized String getInterfaceHash() throws RemoteException {
            return "notfrozen";
        }
    }
}
