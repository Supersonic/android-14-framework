package android.hardware.radio.V1_5;

import android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback;
import android.hardware.radio.V1_0.ActivityStatsInfo;
import android.hardware.radio.V1_0.Call;
import android.hardware.radio.V1_0.CallForwardInfo;
import android.hardware.radio.V1_0.CarrierRestrictions;
import android.hardware.radio.V1_0.CdmaBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.DataRegStateResult;
import android.hardware.radio.V1_0.GsmBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.HardwareConfig;
import android.hardware.radio.V1_0.IccIoResult;
import android.hardware.radio.V1_0.LastCallFailCauseInfo;
import android.hardware.radio.V1_0.LceDataInfo;
import android.hardware.radio.V1_0.LceStatusInfo;
import android.hardware.radio.V1_0.NeighboringCell;
import android.hardware.radio.V1_0.OperatorInfo;
import android.hardware.radio.V1_0.RadioCapability;
import android.hardware.radio.V1_0.RadioResponseInfo;
import android.hardware.radio.V1_0.SendSmsResult;
import android.hardware.radio.V1_0.SignalStrength;
import android.hardware.radio.V1_0.VoiceRegStateResult;
import android.hardware.radio.V1_1.KeepaliveStatus;
import android.hardware.radio.V1_4.CarrierRestrictionsWithPriority;
import android.internal.hidl.base.V1_0.DebugInfo;
import android.internal.hidl.base.V1_0.IBase;
import android.p008os.HidlSupport;
import android.p008os.HwBinder;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import android.p008os.IHwBinder;
import android.p008os.IHwInterface;
import android.p008os.NativeHandle;
import android.p008os.RemoteException;
import com.android.internal.midi.MidiConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IRadioResponse extends android.hardware.radio.V1_4.IRadioResponse {
    public static final String kInterfaceName = "android.hardware.radio@1.5::IRadioResponse";

    void areUiccApplicationsEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void enableUiccApplicationsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getBarringInfoResponse(RadioResponseInfo radioResponseInfo, CellIdentity cellIdentity, ArrayList<BarringInfo> arrayList) throws RemoteException;

    void getCellInfoListResponse_1_5(RadioResponseInfo radioResponseInfo, ArrayList<CellInfo> arrayList) throws RemoteException;

    void getDataCallListResponse_1_5(RadioResponseInfo radioResponseInfo, ArrayList<SetupDataCallResult> arrayList) throws RemoteException;

    void getDataRegistrationStateResponse_1_5(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getIccCardStatusResponse_1_5(RadioResponseInfo radioResponseInfo, CardStatus cardStatus) throws RemoteException;

    void getVoiceRegistrationStateResponse_1_5(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    void sendCdmaSmsExpectMoreResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void setDataProfileResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    void setIndicationFilterResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setInitialAttachApnResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setLinkCapacityReportingCriteriaResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setNetworkSelectionModeManualResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setRadioPowerResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSignalStrengthReportingCriteriaResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSystemSelectionChannelsResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setupDataCallResponse_1_5(RadioResponseInfo radioResponseInfo, SetupDataCallResult setupDataCallResult) throws RemoteException;

    void startNetworkScanResponse_1_5(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void supplySimDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i, int i2) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IRadioResponse asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IRadioResponse)) {
            return (IRadioResponse) iface;
        }
        IRadioResponse proxy = new Proxy(binder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                String descriptor = it.next();
                if (descriptor.equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    static IRadioResponse castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IRadioResponse getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IRadioResponse getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IRadioResponse getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IRadioResponse getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IRadioResponse {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.radio@1.5::IRadioResponse]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getIccCardStatusResponse(RadioResponseInfo info, android.hardware.radio.V1_0.CardStatus cardStatus) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            cardStatus.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void supplyIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void supplyIccPukForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void supplyIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void supplyIccPuk2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void changeIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void changeIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void supplyNetworkDepersonalizationResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCurrentCallsResponse(RadioResponseInfo info, ArrayList<Call> calls) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            Call.writeVectorToParcel(_hidl_request, calls);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void dialResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getIMSIForAppResponse(RadioResponseInfo info, String imsi) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(imsi);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void hangupConnectionResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void hangupWaitingOrBackgroundResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void hangupForegroundResumeBackgroundResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void conferenceResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void rejectCallResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getLastCallFailCauseResponse(RadioResponseInfo info, LastCallFailCauseInfo failCauseinfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            failCauseinfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getSignalStrengthResponse(RadioResponseInfo info, SignalStrength sigStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sigStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getVoiceRegistrationStateResponse(RadioResponseInfo info, VoiceRegStateResult voiceRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            voiceRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getDataRegistrationStateResponse(RadioResponseInfo info, DataRegStateResult dataRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dataRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getOperatorResponse(RadioResponseInfo info, String longName, String shortName, String numeric) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(longName);
            _hidl_request.writeString(shortName);
            _hidl_request.writeString(numeric);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setRadioPowerResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendDtmfResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendSMSExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setupDataCallResponse(RadioResponseInfo info, android.hardware.radio.V1_0.SetupDataCallResult dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dcResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void iccIOForAppResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            iccIo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendUssdResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void cancelPendingUssdResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getClirResponse(RadioResponseInfo info, int n, int m) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(n);
            _hidl_request.writeInt32(m);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setClirResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCallForwardStatusResponse(RadioResponseInfo info, ArrayList<CallForwardInfo> callForwardInfos) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            CallForwardInfo.writeVectorToParcel(_hidl_request, callForwardInfos);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCallForwardResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCallWaitingResponse(RadioResponseInfo info, boolean enable, int serviceClass) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(enable);
            _hidl_request.writeInt32(serviceClass);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCallWaitingResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void acknowledgeLastIncomingGsmSmsResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void acceptCallResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void deactivateDataCallResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getFacilityLockForAppResponse(RadioResponseInfo info, int response) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(response);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setFacilityLockForAppResponse(RadioResponseInfo info, int retry) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(retry);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setBarringPasswordResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getNetworkSelectionModeResponse(RadioResponseInfo info, boolean manual) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(manual);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setNetworkSelectionModeManualResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getAvailableNetworksResponse(RadioResponseInfo info, ArrayList<OperatorInfo> networkInfos) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            OperatorInfo.writeVectorToParcel(_hidl_request, networkInfos);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void startDtmfResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void stopDtmfResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getBasebandVersionResponse(RadioResponseInfo info, String version) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(version);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void separateConnectionResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setMuteResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getMuteResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getClipResponse(RadioResponseInfo info, int status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getDataCallListResponse(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_0.SetupDataCallResult> dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_0.SetupDataCallResult.writeVectorToParcel(_hidl_request, dcResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setSuppServiceNotificationsResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void writeSmsToSimResponse(RadioResponseInfo info, int index) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(index);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void deleteSmsOnSimResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(57, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setBandModeResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(58, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getAvailableBandModesResponse(RadioResponseInfo info, ArrayList<Integer> bandModes) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32Vector(bandModes);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(59, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendEnvelopeResponse(RadioResponseInfo info, String commandResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(commandResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(60, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendTerminalResponseToSimResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(61, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(62, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void explicitCallTransferResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(63, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setPreferredNetworkTypeResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(64, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getPreferredNetworkTypeResponse(RadioResponseInfo info, int nwType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(nwType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(65, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getNeighboringCidsResponse(RadioResponseInfo info, ArrayList<NeighboringCell> cells) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            NeighboringCell.writeVectorToParcel(_hidl_request, cells);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(66, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setLocationUpdatesResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(67, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCdmaSubscriptionSourceResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(68, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCdmaRoamingPreferenceResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(69, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCdmaRoamingPreferenceResponse(RadioResponseInfo info, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(70, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setTTYModeResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(71, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getTTYModeResponse(RadioResponseInfo info, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(72, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setPreferredVoicePrivacyResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(73, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getPreferredVoicePrivacyResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(74, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendCDMAFeatureCodeResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(75, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendBurstDtmfResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(76, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendCdmaSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(77, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void acknowledgeLastIncomingCdmaSmsResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(78, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getGsmBroadcastConfigResponse(RadioResponseInfo info, ArrayList<GsmBroadcastSmsConfigInfo> configs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            GsmBroadcastSmsConfigInfo.writeVectorToParcel(_hidl_request, configs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(79, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setGsmBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(80, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setGsmBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(81, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCdmaBroadcastConfigResponse(RadioResponseInfo info, ArrayList<CdmaBroadcastSmsConfigInfo> configs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            CdmaBroadcastSmsConfigInfo.writeVectorToParcel(_hidl_request, configs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(82, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCdmaBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(83, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCdmaBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(84, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCDMASubscriptionResponse(RadioResponseInfo info, String mdn, String hSid, String hNid, String min, String prl) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(mdn);
            _hidl_request.writeString(hSid);
            _hidl_request.writeString(hNid);
            _hidl_request.writeString(min);
            _hidl_request.writeString(prl);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(85, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void writeSmsToRuimResponse(RadioResponseInfo info, int index) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(index);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(86, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void deleteSmsOnRuimResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(87, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getDeviceIdentityResponse(RadioResponseInfo info, String imei, String imeisv, String esn, String meid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(imei);
            _hidl_request.writeString(imeisv);
            _hidl_request.writeString(esn);
            _hidl_request.writeString(meid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(88, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void exitEmergencyCallbackModeResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(89, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getSmscAddressResponse(RadioResponseInfo info, String smsc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(smsc);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(90, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setSmscAddressResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(91, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void reportSmsMemoryStatusResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(92, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void reportStkServiceIsRunningResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(93, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCdmaSubscriptionSourceResponse(RadioResponseInfo info, int source) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(source);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(94, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void requestIsimAuthenticationResponse(RadioResponseInfo info, String response) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(response);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(95, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void acknowledgeIncomingGsmSmsWithPduResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(96, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendEnvelopeWithStatusResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            iccIo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(97, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getVoiceRadioTechnologyResponse(RadioResponseInfo info, int rat) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(rat);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(98, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getCellInfoListResponse(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_0.CellInfo> cellInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_0.CellInfo.writeVectorToParcel(_hidl_request, cellInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(99, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setCellInfoListRateResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(100, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setInitialAttachApnResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(101, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getImsRegistrationStateResponse(RadioResponseInfo info, boolean isRegistered, int ratFamily) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(isRegistered);
            _hidl_request.writeInt32(ratFamily);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(102, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendImsSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(103, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void iccTransmitApduBasicChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(104, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void iccOpenLogicalChannelResponse(RadioResponseInfo info, int channelId, ArrayList<Byte> selectResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(channelId);
            _hidl_request.writeInt8Vector(selectResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(105, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void iccCloseLogicalChannelResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(106, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void iccTransmitApduLogicalChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(107, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void nvReadItemResponse(RadioResponseInfo info, String result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeString(result);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(108, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void nvWriteItemResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(109, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void nvWriteCdmaPrlResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(110, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void nvResetConfigResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(111, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setUiccSubscriptionResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(112, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setDataAllowedResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(113, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getHardwareConfigResponse(RadioResponseInfo info, ArrayList<HardwareConfig> config) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HardwareConfig.writeVectorToParcel(_hidl_request, config);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(114, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void requestIccSimAuthenticationResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(115, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setDataProfileResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(116, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void requestShutdownResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(117, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            rc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(118, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            rc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(119, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void startLceServiceResponse(RadioResponseInfo info, LceStatusInfo statusInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            statusInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(120, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void stopLceServiceResponse(RadioResponseInfo info, LceStatusInfo statusInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            statusInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(121, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void pullLceDataResponse(RadioResponseInfo info, LceDataInfo lceInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            lceInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(122, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getModemActivityInfoResponse(RadioResponseInfo info, ActivityStatsInfo activityInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            activityInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(123, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setAllowedCarriersResponse(RadioResponseInfo info, int numAllowed) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(numAllowed);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(124, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void getAllowedCarriersResponse(RadioResponseInfo info, boolean allAllowed, CarrierRestrictions carriers) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(allAllowed);
            carriers.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(125, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void sendDeviceStateResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(126, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setIndicationFilterResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(127, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void setSimCardPowerResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(128, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(129, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(130, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void setSimCardPowerResponse_1_1(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(131, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void startNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(132, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void stopNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(133, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void startKeepaliveResponse(RadioResponseInfo info, KeepaliveStatus status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            status.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(134, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioResponse
        public void stopKeepaliveResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(135, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getCellInfoListResponse_1_2(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_2.CellInfo> cellInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_2.CellInfo.writeVectorToParcel(_hidl_request, cellInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(136, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getIccCardStatusResponse_1_2(RadioResponseInfo info, android.hardware.radio.V1_2.CardStatus cardStatus) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            cardStatus.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(137, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(138, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(139, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getCurrentCallsResponse_1_2(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_2.Call> calls) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_2.Call.writeVectorToParcel(_hidl_request, calls);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(140, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getSignalStrengthResponse_1_2(RadioResponseInfo info, android.hardware.radio.V1_2.SignalStrength signalStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            signalStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(141, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getVoiceRegistrationStateResponse_1_2(RadioResponseInfo info, android.hardware.radio.V1_2.VoiceRegStateResult voiceRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            voiceRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(142, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioResponse
        public void getDataRegistrationStateResponse_1_2(RadioResponseInfo info, android.hardware.radio.V1_2.DataRegStateResult dataRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dataRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(143, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadioResponse
        public void setSystemSelectionChannelsResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(144, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadioResponse
        public void enableModemResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(145, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadioResponse
        public void getModemStackStatusResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(isEnabled);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(146, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void emergencyDialResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(147, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void startNetworkScanResponse_1_4(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(148, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getCellInfoListResponse_1_4(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_4.CellInfo> cellInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_4.CellInfo.writeVectorToParcel(_hidl_request, cellInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(149, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getDataRegistrationStateResponse_1_4(RadioResponseInfo info, android.hardware.radio.V1_4.DataRegStateResult dataRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dataRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(150, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getIccCardStatusResponse_1_4(RadioResponseInfo info, android.hardware.radio.V1_4.CardStatus cardStatus) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            cardStatus.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(151, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getPreferredNetworkTypeBitmapResponse(RadioResponseInfo info, int networkTypeBitmap) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(networkTypeBitmap);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(152, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void setPreferredNetworkTypeBitmapResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(153, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getDataCallListResponse_1_4(RadioResponseInfo info, ArrayList<android.hardware.radio.V1_4.SetupDataCallResult> dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            android.hardware.radio.V1_4.SetupDataCallResult.writeVectorToParcel(_hidl_request, dcResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(154, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void setupDataCallResponse_1_4(RadioResponseInfo info, android.hardware.radio.V1_4.SetupDataCallResult dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dcResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(155, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void setAllowedCarriersResponse_1_4(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(156, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getAllowedCarriersResponse_1_4(RadioResponseInfo info, CarrierRestrictionsWithPriority carriers, int multiSimPolicy) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            carriers.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(multiSimPolicy);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(157, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioResponse
        public void getSignalStrengthResponse_1_4(RadioResponseInfo info, android.hardware.radio.V1_4.SignalStrength signalStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            signalStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(158, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setSignalStrengthReportingCriteriaResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(159, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setLinkCapacityReportingCriteriaResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(160, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void enableUiccApplicationsResponse(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(161, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void areUiccApplicationsEnabledResponse(RadioResponseInfo info, boolean enabled) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeBool(enabled);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(162, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setSystemSelectionChannelsResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(163, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void startNetworkScanResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(164, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setupDataCallResponse_1_5(RadioResponseInfo info, SetupDataCallResult dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dcResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(165, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getDataCallListResponse_1_5(RadioResponseInfo info, ArrayList<SetupDataCallResult> dcResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            SetupDataCallResult.writeVectorToParcel(_hidl_request, dcResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(166, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setInitialAttachApnResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(167, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setDataProfileResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(168, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setRadioPowerResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(169, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setIndicationFilterResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(170, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getBarringInfoResponse(RadioResponseInfo info, CellIdentity cellIdentity, ArrayList<BarringInfo> barringInfos) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            cellIdentity.writeToParcel(_hidl_request);
            BarringInfo.writeVectorToParcel(_hidl_request, barringInfos);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(171, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getVoiceRegistrationStateResponse_1_5(RadioResponseInfo info, RegStateResult voiceRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            voiceRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(172, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getDataRegistrationStateResponse_1_5(RadioResponseInfo info, RegStateResult dataRegResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            dataRegResponse.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(173, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getCellInfoListResponse_1_5(RadioResponseInfo info, ArrayList<CellInfo> cellInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            CellInfo.writeVectorToParcel(_hidl_request, cellInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(174, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void setNetworkSelectionModeManualResponse_1_5(RadioResponseInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(175, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void sendCdmaSmsExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(176, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void supplySimDepersonalizationResponse(RadioResponseInfo info, int persoType, int remainingRetries) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(persoType);
            _hidl_request.writeInt32(remainingRetries);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(177, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse
        public void getIccCardStatusResponse_1_5(RadioResponseInfo info, CardStatus cardStatus) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioResponse.kInterfaceName);
            info.writeToParcel(_hidl_request);
            cardStatus.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(178, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256067662, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<String> _hidl_out_descriptors = _hidl_reply.readStringVector();
                return _hidl_out_descriptors;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            _hidl_request.writeNativeHandle(fd);
            _hidl_request.writeStringVector(options);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256131655, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public String interfaceDescriptor() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256136003, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                String _hidl_out_descriptor = _hidl_reply.readString();
                return _hidl_out_descriptor;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256398152, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<byte[]> _hidl_out_hashchain = new ArrayList<>();
                HwBlob _hidl_blob = _hidl_reply.readBuffer(16L);
                int _hidl_vec_size = _hidl_blob.getInt32(8L);
                HwBlob childBlob = _hidl_reply.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
                _hidl_out_hashchain.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    byte[] _hidl_vec_element = new byte[32];
                    long _hidl_array_offset_1 = _hidl_index_0 * 32;
                    childBlob.copyToInt8Array(_hidl_array_offset_1, _hidl_vec_element, 32);
                    _hidl_out_hashchain.add(_hidl_vec_element);
                }
                return _hidl_out_hashchain;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public void setHALInstrumentation() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256462420, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public void ping() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256921159, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257049926, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugInfo _hidl_out_info = new DebugInfo();
                _hidl_out_info.readFromParcel(_hidl_reply);
                return _hidl_out_info;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public void notifySyspropsChanged() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257120595, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IRadioResponse {
        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IRadioResponse.kInterfaceName, android.hardware.radio.V1_4.IRadioResponse.kInterfaceName, android.hardware.radio.V1_3.IRadioResponse.kInterfaceName, android.hardware.radio.V1_2.IRadioResponse.kInterfaceName, android.hardware.radio.V1_1.IRadioResponse.kInterfaceName, android.hardware.radio.V1_0.IRadioResponse.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IRadioResponse.kInterfaceName;
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-72, 9, 25, 57, 112, -87, 28, -90, 55, -92, MidiConstants.STATUS_CONTROL_CHANGE, 24, 71, 103, 49, 86, 1, -45, 46, 62, MidiConstants.STATUS_SONG_SELECT, -43, -103, 47, -5, -57, -88, -47, 74, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, -16, 21}, new byte[]{12, MidiConstants.STATUS_PROGRAM_CHANGE, -35, -121, -58, 52, -86, -45, 109, 125, MidiConstants.STATUS_SONG_POSITION, 43, 40, 50, -125, -98, -9, -34, -41, 25, 9, -37, -51, -31, 28, -3, -42, -99, MidiConstants.STATUS_PROGRAM_CHANGE, -36, 82, -72}, new byte[]{-11, -5, -28, MidiConstants.STATUS_SONG_POSITION, -118, -98, 52, 107, -29, 96, 99, -20, -92, -26, -56, 100, 17, 74, 26, 111, -74, 72, -124, -37, 3, -3, -40, 37, 121, 26, -39, -72}, new byte[]{-38, -116, 106, -23, -111, -58, -92, -78, -124, -52, 110, 68, 83, 50, MidiConstants.STATUS_PITCH_BEND, 100, -30, -114, -24, MidiConstants.STATUS_POLYPHONIC_AFTERTOUCH, -108, -126, -19, 90, -1, -7, -47, 89, -20, 102, -108, -73}, new byte[]{0, 54, 107, 47, -120, -7, -20, 36, 88, 1, 73, 114, -109, -126, 112, -56, 65, 61, 74, -77, 3, 33, -114, 55, -65, 58, -35, 43, -114, 107, -126, -102}, new byte[]{-68, 60, -116, 35, 48, -123, -4, -93, -121, -99, -57, 75, 73, 11, -98, 91, -63, 6, 50, 88, 71, 13, 59, 76, 18, -9, -89, 75, MidiConstants.STATUS_SONG_POSITION, 21, -53, -67}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.radio.V1_5.IRadioResponse, android.hardware.radio.V1_4.IRadioResponse, android.hardware.radio.V1_3.IRadioResponse, android.hardware.radio.V1_2.IRadioResponse, android.hardware.radio.V1_1.IRadioResponse, android.hardware.radio.V1_0.IRadioResponse, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IRadioResponse.kInterfaceName.equals(descriptor)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String serviceName) throws RemoteException {
            registerService(serviceName);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        @Override // android.p008os.HwBinder
        public void onTransact(int _hidl_code, HwParcel _hidl_request, HwParcel _hidl_reply, int _hidl_flags) throws RemoteException {
            switch (_hidl_code) {
                case 1:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info = new RadioResponseInfo();
                    info.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_0.CardStatus cardStatus = new android.hardware.radio.V1_0.CardStatus();
                    cardStatus.readFromParcel(_hidl_request);
                    getIccCardStatusResponse(info, cardStatus);
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info2 = new RadioResponseInfo();
                    info2.readFromParcel(_hidl_request);
                    int remainingRetries = _hidl_request.readInt32();
                    supplyIccPinForAppResponse(info2, remainingRetries);
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info3 = new RadioResponseInfo();
                    info3.readFromParcel(_hidl_request);
                    int remainingRetries2 = _hidl_request.readInt32();
                    supplyIccPukForAppResponse(info3, remainingRetries2);
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info4 = new RadioResponseInfo();
                    info4.readFromParcel(_hidl_request);
                    int remainingRetries3 = _hidl_request.readInt32();
                    supplyIccPin2ForAppResponse(info4, remainingRetries3);
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info5 = new RadioResponseInfo();
                    info5.readFromParcel(_hidl_request);
                    int remainingRetries4 = _hidl_request.readInt32();
                    supplyIccPuk2ForAppResponse(info5, remainingRetries4);
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info6 = new RadioResponseInfo();
                    info6.readFromParcel(_hidl_request);
                    int remainingRetries5 = _hidl_request.readInt32();
                    changeIccPinForAppResponse(info6, remainingRetries5);
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info7 = new RadioResponseInfo();
                    info7.readFromParcel(_hidl_request);
                    int remainingRetries6 = _hidl_request.readInt32();
                    changeIccPin2ForAppResponse(info7, remainingRetries6);
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info8 = new RadioResponseInfo();
                    info8.readFromParcel(_hidl_request);
                    int remainingRetries7 = _hidl_request.readInt32();
                    supplyNetworkDepersonalizationResponse(info8, remainingRetries7);
                    return;
                case 9:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info9 = new RadioResponseInfo();
                    info9.readFromParcel(_hidl_request);
                    ArrayList<Call> calls = Call.readVectorFromParcel(_hidl_request);
                    getCurrentCallsResponse(info9, calls);
                    return;
                case 10:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info10 = new RadioResponseInfo();
                    info10.readFromParcel(_hidl_request);
                    dialResponse(info10);
                    return;
                case 11:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info11 = new RadioResponseInfo();
                    info11.readFromParcel(_hidl_request);
                    String imsi = _hidl_request.readString();
                    getIMSIForAppResponse(info11, imsi);
                    return;
                case 12:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info12 = new RadioResponseInfo();
                    info12.readFromParcel(_hidl_request);
                    hangupConnectionResponse(info12);
                    return;
                case 13:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info13 = new RadioResponseInfo();
                    info13.readFromParcel(_hidl_request);
                    hangupWaitingOrBackgroundResponse(info13);
                    return;
                case 14:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info14 = new RadioResponseInfo();
                    info14.readFromParcel(_hidl_request);
                    hangupForegroundResumeBackgroundResponse(info14);
                    return;
                case 15:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info15 = new RadioResponseInfo();
                    info15.readFromParcel(_hidl_request);
                    switchWaitingOrHoldingAndActiveResponse(info15);
                    return;
                case 16:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info16 = new RadioResponseInfo();
                    info16.readFromParcel(_hidl_request);
                    conferenceResponse(info16);
                    return;
                case 17:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info17 = new RadioResponseInfo();
                    info17.readFromParcel(_hidl_request);
                    rejectCallResponse(info17);
                    return;
                case 18:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info18 = new RadioResponseInfo();
                    info18.readFromParcel(_hidl_request);
                    LastCallFailCauseInfo failCauseinfo = new LastCallFailCauseInfo();
                    failCauseinfo.readFromParcel(_hidl_request);
                    getLastCallFailCauseResponse(info18, failCauseinfo);
                    return;
                case 19:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info19 = new RadioResponseInfo();
                    info19.readFromParcel(_hidl_request);
                    SignalStrength sigStrength = new SignalStrength();
                    sigStrength.readFromParcel(_hidl_request);
                    getSignalStrengthResponse(info19, sigStrength);
                    return;
                case 20:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info20 = new RadioResponseInfo();
                    info20.readFromParcel(_hidl_request);
                    VoiceRegStateResult voiceRegResponse = new VoiceRegStateResult();
                    voiceRegResponse.readFromParcel(_hidl_request);
                    getVoiceRegistrationStateResponse(info20, voiceRegResponse);
                    return;
                case 21:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info21 = new RadioResponseInfo();
                    info21.readFromParcel(_hidl_request);
                    DataRegStateResult dataRegResponse = new DataRegStateResult();
                    dataRegResponse.readFromParcel(_hidl_request);
                    getDataRegistrationStateResponse(info21, dataRegResponse);
                    return;
                case 22:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info22 = new RadioResponseInfo();
                    info22.readFromParcel(_hidl_request);
                    String longName = _hidl_request.readString();
                    String shortName = _hidl_request.readString();
                    String numeric = _hidl_request.readString();
                    getOperatorResponse(info22, longName, shortName, numeric);
                    return;
                case 23:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info23 = new RadioResponseInfo();
                    info23.readFromParcel(_hidl_request);
                    setRadioPowerResponse(info23);
                    return;
                case 24:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info24 = new RadioResponseInfo();
                    info24.readFromParcel(_hidl_request);
                    sendDtmfResponse(info24);
                    return;
                case 25:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info25 = new RadioResponseInfo();
                    info25.readFromParcel(_hidl_request);
                    SendSmsResult sms = new SendSmsResult();
                    sms.readFromParcel(_hidl_request);
                    sendSmsResponse(info25, sms);
                    return;
                case 26:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info26 = new RadioResponseInfo();
                    info26.readFromParcel(_hidl_request);
                    SendSmsResult sms2 = new SendSmsResult();
                    sms2.readFromParcel(_hidl_request);
                    sendSMSExpectMoreResponse(info26, sms2);
                    return;
                case 27:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info27 = new RadioResponseInfo();
                    info27.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_0.SetupDataCallResult dcResponse = new android.hardware.radio.V1_0.SetupDataCallResult();
                    dcResponse.readFromParcel(_hidl_request);
                    setupDataCallResponse(info27, dcResponse);
                    return;
                case 28:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info28 = new RadioResponseInfo();
                    info28.readFromParcel(_hidl_request);
                    IccIoResult iccIo = new IccIoResult();
                    iccIo.readFromParcel(_hidl_request);
                    iccIOForAppResponse(info28, iccIo);
                    return;
                case 29:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info29 = new RadioResponseInfo();
                    info29.readFromParcel(_hidl_request);
                    sendUssdResponse(info29);
                    return;
                case 30:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info30 = new RadioResponseInfo();
                    info30.readFromParcel(_hidl_request);
                    cancelPendingUssdResponse(info30);
                    return;
                case 31:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info31 = new RadioResponseInfo();
                    info31.readFromParcel(_hidl_request);
                    int n = _hidl_request.readInt32();
                    int m = _hidl_request.readInt32();
                    getClirResponse(info31, n, m);
                    return;
                case 32:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info32 = new RadioResponseInfo();
                    info32.readFromParcel(_hidl_request);
                    setClirResponse(info32);
                    return;
                case 33:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info33 = new RadioResponseInfo();
                    info33.readFromParcel(_hidl_request);
                    ArrayList<CallForwardInfo> callForwardInfos = CallForwardInfo.readVectorFromParcel(_hidl_request);
                    getCallForwardStatusResponse(info33, callForwardInfos);
                    return;
                case 34:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info34 = new RadioResponseInfo();
                    info34.readFromParcel(_hidl_request);
                    setCallForwardResponse(info34);
                    return;
                case 35:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info35 = new RadioResponseInfo();
                    info35.readFromParcel(_hidl_request);
                    boolean enable = _hidl_request.readBool();
                    int serviceClass = _hidl_request.readInt32();
                    getCallWaitingResponse(info35, enable, serviceClass);
                    return;
                case 36:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info36 = new RadioResponseInfo();
                    info36.readFromParcel(_hidl_request);
                    setCallWaitingResponse(info36);
                    return;
                case 37:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info37 = new RadioResponseInfo();
                    info37.readFromParcel(_hidl_request);
                    acknowledgeLastIncomingGsmSmsResponse(info37);
                    return;
                case 38:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info38 = new RadioResponseInfo();
                    info38.readFromParcel(_hidl_request);
                    acceptCallResponse(info38);
                    return;
                case 39:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info39 = new RadioResponseInfo();
                    info39.readFromParcel(_hidl_request);
                    deactivateDataCallResponse(info39);
                    return;
                case 40:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info40 = new RadioResponseInfo();
                    info40.readFromParcel(_hidl_request);
                    int response = _hidl_request.readInt32();
                    getFacilityLockForAppResponse(info40, response);
                    return;
                case 41:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info41 = new RadioResponseInfo();
                    info41.readFromParcel(_hidl_request);
                    int retry = _hidl_request.readInt32();
                    setFacilityLockForAppResponse(info41, retry);
                    return;
                case 42:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info42 = new RadioResponseInfo();
                    info42.readFromParcel(_hidl_request);
                    setBarringPasswordResponse(info42);
                    return;
                case 43:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info43 = new RadioResponseInfo();
                    info43.readFromParcel(_hidl_request);
                    boolean manual = _hidl_request.readBool();
                    getNetworkSelectionModeResponse(info43, manual);
                    return;
                case 44:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info44 = new RadioResponseInfo();
                    info44.readFromParcel(_hidl_request);
                    setNetworkSelectionModeAutomaticResponse(info44);
                    return;
                case 45:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info45 = new RadioResponseInfo();
                    info45.readFromParcel(_hidl_request);
                    setNetworkSelectionModeManualResponse(info45);
                    return;
                case 46:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info46 = new RadioResponseInfo();
                    info46.readFromParcel(_hidl_request);
                    ArrayList<OperatorInfo> networkInfos = OperatorInfo.readVectorFromParcel(_hidl_request);
                    getAvailableNetworksResponse(info46, networkInfos);
                    return;
                case 47:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info47 = new RadioResponseInfo();
                    info47.readFromParcel(_hidl_request);
                    startDtmfResponse(info47);
                    return;
                case 48:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info48 = new RadioResponseInfo();
                    info48.readFromParcel(_hidl_request);
                    stopDtmfResponse(info48);
                    return;
                case 49:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info49 = new RadioResponseInfo();
                    info49.readFromParcel(_hidl_request);
                    String version = _hidl_request.readString();
                    getBasebandVersionResponse(info49, version);
                    return;
                case 50:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info50 = new RadioResponseInfo();
                    info50.readFromParcel(_hidl_request);
                    separateConnectionResponse(info50);
                    return;
                case 51:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info51 = new RadioResponseInfo();
                    info51.readFromParcel(_hidl_request);
                    setMuteResponse(info51);
                    return;
                case 52:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info52 = new RadioResponseInfo();
                    info52.readFromParcel(_hidl_request);
                    boolean enable2 = _hidl_request.readBool();
                    getMuteResponse(info52, enable2);
                    return;
                case 53:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info53 = new RadioResponseInfo();
                    info53.readFromParcel(_hidl_request);
                    getClipResponse(info53, _hidl_request.readInt32());
                    return;
                case 54:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info54 = new RadioResponseInfo();
                    info54.readFromParcel(_hidl_request);
                    getDataCallListResponse(info54, android.hardware.radio.V1_0.SetupDataCallResult.readVectorFromParcel(_hidl_request));
                    return;
                case 55:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info55 = new RadioResponseInfo();
                    info55.readFromParcel(_hidl_request);
                    setSuppServiceNotificationsResponse(info55);
                    return;
                case 56:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info56 = new RadioResponseInfo();
                    info56.readFromParcel(_hidl_request);
                    int index = _hidl_request.readInt32();
                    writeSmsToSimResponse(info56, index);
                    return;
                case 57:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info57 = new RadioResponseInfo();
                    info57.readFromParcel(_hidl_request);
                    deleteSmsOnSimResponse(info57);
                    return;
                case 58:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info58 = new RadioResponseInfo();
                    info58.readFromParcel(_hidl_request);
                    setBandModeResponse(info58);
                    return;
                case 59:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info59 = new RadioResponseInfo();
                    info59.readFromParcel(_hidl_request);
                    ArrayList<Integer> bandModes = _hidl_request.readInt32Vector();
                    getAvailableBandModesResponse(info59, bandModes);
                    return;
                case 60:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info60 = new RadioResponseInfo();
                    info60.readFromParcel(_hidl_request);
                    String commandResponse = _hidl_request.readString();
                    sendEnvelopeResponse(info60, commandResponse);
                    return;
                case 61:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info61 = new RadioResponseInfo();
                    info61.readFromParcel(_hidl_request);
                    sendTerminalResponseToSimResponse(info61);
                    return;
                case 62:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info62 = new RadioResponseInfo();
                    info62.readFromParcel(_hidl_request);
                    handleStkCallSetupRequestFromSimResponse(info62);
                    return;
                case 63:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info63 = new RadioResponseInfo();
                    info63.readFromParcel(_hidl_request);
                    explicitCallTransferResponse(info63);
                    return;
                case 64:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info64 = new RadioResponseInfo();
                    info64.readFromParcel(_hidl_request);
                    setPreferredNetworkTypeResponse(info64);
                    return;
                case 65:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info65 = new RadioResponseInfo();
                    info65.readFromParcel(_hidl_request);
                    int nwType = _hidl_request.readInt32();
                    getPreferredNetworkTypeResponse(info65, nwType);
                    return;
                case 66:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info66 = new RadioResponseInfo();
                    info66.readFromParcel(_hidl_request);
                    ArrayList<NeighboringCell> cells = NeighboringCell.readVectorFromParcel(_hidl_request);
                    getNeighboringCidsResponse(info66, cells);
                    return;
                case 67:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info67 = new RadioResponseInfo();
                    info67.readFromParcel(_hidl_request);
                    setLocationUpdatesResponse(info67);
                    return;
                case 68:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info68 = new RadioResponseInfo();
                    info68.readFromParcel(_hidl_request);
                    setCdmaSubscriptionSourceResponse(info68);
                    return;
                case 69:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info69 = new RadioResponseInfo();
                    info69.readFromParcel(_hidl_request);
                    setCdmaRoamingPreferenceResponse(info69);
                    return;
                case 70:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info70 = new RadioResponseInfo();
                    info70.readFromParcel(_hidl_request);
                    int type = _hidl_request.readInt32();
                    getCdmaRoamingPreferenceResponse(info70, type);
                    return;
                case 71:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info71 = new RadioResponseInfo();
                    info71.readFromParcel(_hidl_request);
                    setTTYModeResponse(info71);
                    return;
                case 72:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info72 = new RadioResponseInfo();
                    info72.readFromParcel(_hidl_request);
                    int mode = _hidl_request.readInt32();
                    getTTYModeResponse(info72, mode);
                    return;
                case 73:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info73 = new RadioResponseInfo();
                    info73.readFromParcel(_hidl_request);
                    setPreferredVoicePrivacyResponse(info73);
                    return;
                case 74:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info74 = new RadioResponseInfo();
                    info74.readFromParcel(_hidl_request);
                    boolean enable3 = _hidl_request.readBool();
                    getPreferredVoicePrivacyResponse(info74, enable3);
                    return;
                case 75:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info75 = new RadioResponseInfo();
                    info75.readFromParcel(_hidl_request);
                    sendCDMAFeatureCodeResponse(info75);
                    return;
                case 76:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info76 = new RadioResponseInfo();
                    info76.readFromParcel(_hidl_request);
                    sendBurstDtmfResponse(info76);
                    return;
                case 77:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info77 = new RadioResponseInfo();
                    info77.readFromParcel(_hidl_request);
                    SendSmsResult sms3 = new SendSmsResult();
                    sms3.readFromParcel(_hidl_request);
                    sendCdmaSmsResponse(info77, sms3);
                    return;
                case 78:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info78 = new RadioResponseInfo();
                    info78.readFromParcel(_hidl_request);
                    acknowledgeLastIncomingCdmaSmsResponse(info78);
                    return;
                case 79:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info79 = new RadioResponseInfo();
                    info79.readFromParcel(_hidl_request);
                    ArrayList<GsmBroadcastSmsConfigInfo> configs = GsmBroadcastSmsConfigInfo.readVectorFromParcel(_hidl_request);
                    getGsmBroadcastConfigResponse(info79, configs);
                    return;
                case 80:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info80 = new RadioResponseInfo();
                    info80.readFromParcel(_hidl_request);
                    setGsmBroadcastConfigResponse(info80);
                    return;
                case 81:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info81 = new RadioResponseInfo();
                    info81.readFromParcel(_hidl_request);
                    setGsmBroadcastActivationResponse(info81);
                    return;
                case 82:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info82 = new RadioResponseInfo();
                    info82.readFromParcel(_hidl_request);
                    ArrayList<CdmaBroadcastSmsConfigInfo> configs2 = CdmaBroadcastSmsConfigInfo.readVectorFromParcel(_hidl_request);
                    getCdmaBroadcastConfigResponse(info82, configs2);
                    return;
                case 83:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info83 = new RadioResponseInfo();
                    info83.readFromParcel(_hidl_request);
                    setCdmaBroadcastConfigResponse(info83);
                    return;
                case 84:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info84 = new RadioResponseInfo();
                    info84.readFromParcel(_hidl_request);
                    setCdmaBroadcastActivationResponse(info84);
                    return;
                case 85:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info85 = new RadioResponseInfo();
                    info85.readFromParcel(_hidl_request);
                    String mdn = _hidl_request.readString();
                    String hSid = _hidl_request.readString();
                    String hNid = _hidl_request.readString();
                    String min = _hidl_request.readString();
                    String prl = _hidl_request.readString();
                    getCDMASubscriptionResponse(info85, mdn, hSid, hNid, min, prl);
                    return;
                case 86:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info86 = new RadioResponseInfo();
                    info86.readFromParcel(_hidl_request);
                    int index2 = _hidl_request.readInt32();
                    writeSmsToRuimResponse(info86, index2);
                    return;
                case 87:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info87 = new RadioResponseInfo();
                    info87.readFromParcel(_hidl_request);
                    deleteSmsOnRuimResponse(info87);
                    return;
                case 88:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info88 = new RadioResponseInfo();
                    info88.readFromParcel(_hidl_request);
                    String imei = _hidl_request.readString();
                    String imeisv = _hidl_request.readString();
                    String esn = _hidl_request.readString();
                    String meid = _hidl_request.readString();
                    getDeviceIdentityResponse(info88, imei, imeisv, esn, meid);
                    return;
                case 89:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info89 = new RadioResponseInfo();
                    info89.readFromParcel(_hidl_request);
                    exitEmergencyCallbackModeResponse(info89);
                    return;
                case 90:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info90 = new RadioResponseInfo();
                    info90.readFromParcel(_hidl_request);
                    String smsc = _hidl_request.readString();
                    getSmscAddressResponse(info90, smsc);
                    return;
                case 91:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info91 = new RadioResponseInfo();
                    info91.readFromParcel(_hidl_request);
                    setSmscAddressResponse(info91);
                    return;
                case 92:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info92 = new RadioResponseInfo();
                    info92.readFromParcel(_hidl_request);
                    reportSmsMemoryStatusResponse(info92);
                    return;
                case 93:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info93 = new RadioResponseInfo();
                    info93.readFromParcel(_hidl_request);
                    reportStkServiceIsRunningResponse(info93);
                    return;
                case 94:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info94 = new RadioResponseInfo();
                    info94.readFromParcel(_hidl_request);
                    int source = _hidl_request.readInt32();
                    getCdmaSubscriptionSourceResponse(info94, source);
                    return;
                case 95:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info95 = new RadioResponseInfo();
                    info95.readFromParcel(_hidl_request);
                    String response2 = _hidl_request.readString();
                    requestIsimAuthenticationResponse(info95, response2);
                    return;
                case 96:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info96 = new RadioResponseInfo();
                    info96.readFromParcel(_hidl_request);
                    acknowledgeIncomingGsmSmsWithPduResponse(info96);
                    return;
                case 97:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info97 = new RadioResponseInfo();
                    info97.readFromParcel(_hidl_request);
                    IccIoResult iccIo2 = new IccIoResult();
                    iccIo2.readFromParcel(_hidl_request);
                    sendEnvelopeWithStatusResponse(info97, iccIo2);
                    return;
                case 98:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info98 = new RadioResponseInfo();
                    info98.readFromParcel(_hidl_request);
                    int rat = _hidl_request.readInt32();
                    getVoiceRadioTechnologyResponse(info98, rat);
                    return;
                case 99:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info99 = new RadioResponseInfo();
                    info99.readFromParcel(_hidl_request);
                    ArrayList<android.hardware.radio.V1_0.CellInfo> cellInfo = android.hardware.radio.V1_0.CellInfo.readVectorFromParcel(_hidl_request);
                    getCellInfoListResponse(info99, cellInfo);
                    return;
                case 100:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info100 = new RadioResponseInfo();
                    info100.readFromParcel(_hidl_request);
                    setCellInfoListRateResponse(info100);
                    return;
                case 101:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info101 = new RadioResponseInfo();
                    info101.readFromParcel(_hidl_request);
                    setInitialAttachApnResponse(info101);
                    return;
                case 102:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info102 = new RadioResponseInfo();
                    info102.readFromParcel(_hidl_request);
                    boolean isRegistered = _hidl_request.readBool();
                    int ratFamily = _hidl_request.readInt32();
                    getImsRegistrationStateResponse(info102, isRegistered, ratFamily);
                    return;
                case 103:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info103 = new RadioResponseInfo();
                    info103.readFromParcel(_hidl_request);
                    SendSmsResult sms4 = new SendSmsResult();
                    sms4.readFromParcel(_hidl_request);
                    sendImsSmsResponse(info103, sms4);
                    return;
                case 104:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info104 = new RadioResponseInfo();
                    info104.readFromParcel(_hidl_request);
                    IccIoResult result = new IccIoResult();
                    result.readFromParcel(_hidl_request);
                    iccTransmitApduBasicChannelResponse(info104, result);
                    return;
                case 105:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info105 = new RadioResponseInfo();
                    info105.readFromParcel(_hidl_request);
                    int channelId = _hidl_request.readInt32();
                    ArrayList<Byte> selectResponse = _hidl_request.readInt8Vector();
                    iccOpenLogicalChannelResponse(info105, channelId, selectResponse);
                    return;
                case 106:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info106 = new RadioResponseInfo();
                    info106.readFromParcel(_hidl_request);
                    iccCloseLogicalChannelResponse(info106);
                    return;
                case 107:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info107 = new RadioResponseInfo();
                    info107.readFromParcel(_hidl_request);
                    IccIoResult result2 = new IccIoResult();
                    result2.readFromParcel(_hidl_request);
                    iccTransmitApduLogicalChannelResponse(info107, result2);
                    return;
                case 108:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info108 = new RadioResponseInfo();
                    info108.readFromParcel(_hidl_request);
                    nvReadItemResponse(info108, _hidl_request.readString());
                    return;
                case 109:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info109 = new RadioResponseInfo();
                    info109.readFromParcel(_hidl_request);
                    nvWriteItemResponse(info109);
                    return;
                case 110:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info110 = new RadioResponseInfo();
                    info110.readFromParcel(_hidl_request);
                    nvWriteCdmaPrlResponse(info110);
                    return;
                case 111:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info111 = new RadioResponseInfo();
                    info111.readFromParcel(_hidl_request);
                    nvResetConfigResponse(info111);
                    return;
                case 112:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info112 = new RadioResponseInfo();
                    info112.readFromParcel(_hidl_request);
                    setUiccSubscriptionResponse(info112);
                    return;
                case 113:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info113 = new RadioResponseInfo();
                    info113.readFromParcel(_hidl_request);
                    setDataAllowedResponse(info113);
                    return;
                case 114:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info114 = new RadioResponseInfo();
                    info114.readFromParcel(_hidl_request);
                    ArrayList<HardwareConfig> config = HardwareConfig.readVectorFromParcel(_hidl_request);
                    getHardwareConfigResponse(info114, config);
                    return;
                case 115:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info115 = new RadioResponseInfo();
                    info115.readFromParcel(_hidl_request);
                    IccIoResult result3 = new IccIoResult();
                    result3.readFromParcel(_hidl_request);
                    requestIccSimAuthenticationResponse(info115, result3);
                    return;
                case 116:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info116 = new RadioResponseInfo();
                    info116.readFromParcel(_hidl_request);
                    setDataProfileResponse(info116);
                    return;
                case 117:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info117 = new RadioResponseInfo();
                    info117.readFromParcel(_hidl_request);
                    requestShutdownResponse(info117);
                    return;
                case 118:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info118 = new RadioResponseInfo();
                    info118.readFromParcel(_hidl_request);
                    RadioCapability rc = new RadioCapability();
                    rc.readFromParcel(_hidl_request);
                    getRadioCapabilityResponse(info118, rc);
                    return;
                case 119:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info119 = new RadioResponseInfo();
                    info119.readFromParcel(_hidl_request);
                    RadioCapability rc2 = new RadioCapability();
                    rc2.readFromParcel(_hidl_request);
                    setRadioCapabilityResponse(info119, rc2);
                    return;
                case 120:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info120 = new RadioResponseInfo();
                    info120.readFromParcel(_hidl_request);
                    LceStatusInfo statusInfo = new LceStatusInfo();
                    statusInfo.readFromParcel(_hidl_request);
                    startLceServiceResponse(info120, statusInfo);
                    return;
                case 121:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info121 = new RadioResponseInfo();
                    info121.readFromParcel(_hidl_request);
                    LceStatusInfo statusInfo2 = new LceStatusInfo();
                    statusInfo2.readFromParcel(_hidl_request);
                    stopLceServiceResponse(info121, statusInfo2);
                    return;
                case 122:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info122 = new RadioResponseInfo();
                    info122.readFromParcel(_hidl_request);
                    LceDataInfo lceInfo = new LceDataInfo();
                    lceInfo.readFromParcel(_hidl_request);
                    pullLceDataResponse(info122, lceInfo);
                    return;
                case 123:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info123 = new RadioResponseInfo();
                    info123.readFromParcel(_hidl_request);
                    ActivityStatsInfo activityInfo = new ActivityStatsInfo();
                    activityInfo.readFromParcel(_hidl_request);
                    getModemActivityInfoResponse(info123, activityInfo);
                    return;
                case 124:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info124 = new RadioResponseInfo();
                    info124.readFromParcel(_hidl_request);
                    int numAllowed = _hidl_request.readInt32();
                    setAllowedCarriersResponse(info124, numAllowed);
                    return;
                case 125:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info125 = new RadioResponseInfo();
                    info125.readFromParcel(_hidl_request);
                    boolean allAllowed = _hidl_request.readBool();
                    CarrierRestrictions carriers = new CarrierRestrictions();
                    carriers.readFromParcel(_hidl_request);
                    getAllowedCarriersResponse(info125, allAllowed, carriers);
                    return;
                case 126:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info126 = new RadioResponseInfo();
                    info126.readFromParcel(_hidl_request);
                    sendDeviceStateResponse(info126);
                    return;
                case 127:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info127 = new RadioResponseInfo();
                    info127.readFromParcel(_hidl_request);
                    setIndicationFilterResponse(info127);
                    return;
                case 128:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info128 = new RadioResponseInfo();
                    info128.readFromParcel(_hidl_request);
                    setSimCardPowerResponse(info128);
                    return;
                case 129:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioResponse.kInterfaceName);
                    int serial = _hidl_request.readInt32();
                    acknowledgeRequest(serial);
                    return;
                case 130:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info129 = new RadioResponseInfo();
                    info129.readFromParcel(_hidl_request);
                    setCarrierInfoForImsiEncryptionResponse(info129);
                    return;
                case 131:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info130 = new RadioResponseInfo();
                    info130.readFromParcel(_hidl_request);
                    setSimCardPowerResponse_1_1(info130);
                    return;
                case 132:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info131 = new RadioResponseInfo();
                    info131.readFromParcel(_hidl_request);
                    startNetworkScanResponse(info131);
                    return;
                case 133:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info132 = new RadioResponseInfo();
                    info132.readFromParcel(_hidl_request);
                    stopNetworkScanResponse(info132);
                    return;
                case 134:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info133 = new RadioResponseInfo();
                    info133.readFromParcel(_hidl_request);
                    KeepaliveStatus status = new KeepaliveStatus();
                    status.readFromParcel(_hidl_request);
                    startKeepaliveResponse(info133, status);
                    return;
                case 135:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info134 = new RadioResponseInfo();
                    info134.readFromParcel(_hidl_request);
                    stopKeepaliveResponse(info134);
                    return;
                case 136:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info135 = new RadioResponseInfo();
                    info135.readFromParcel(_hidl_request);
                    ArrayList<android.hardware.radio.V1_2.CellInfo> cellInfo2 = android.hardware.radio.V1_2.CellInfo.readVectorFromParcel(_hidl_request);
                    getCellInfoListResponse_1_2(info135, cellInfo2);
                    return;
                case 137:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info136 = new RadioResponseInfo();
                    info136.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_2.CardStatus cardStatus2 = new android.hardware.radio.V1_2.CardStatus();
                    cardStatus2.readFromParcel(_hidl_request);
                    getIccCardStatusResponse_1_2(info136, cardStatus2);
                    return;
                case 138:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info137 = new RadioResponseInfo();
                    info137.readFromParcel(_hidl_request);
                    setSignalStrengthReportingCriteriaResponse(info137);
                    return;
                case 139:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info138 = new RadioResponseInfo();
                    info138.readFromParcel(_hidl_request);
                    setLinkCapacityReportingCriteriaResponse(info138);
                    return;
                case 140:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info139 = new RadioResponseInfo();
                    info139.readFromParcel(_hidl_request);
                    ArrayList<android.hardware.radio.V1_2.Call> calls2 = android.hardware.radio.V1_2.Call.readVectorFromParcel(_hidl_request);
                    getCurrentCallsResponse_1_2(info139, calls2);
                    return;
                case 141:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info140 = new RadioResponseInfo();
                    info140.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_2.SignalStrength signalStrength = new android.hardware.radio.V1_2.SignalStrength();
                    signalStrength.readFromParcel(_hidl_request);
                    getSignalStrengthResponse_1_2(info140, signalStrength);
                    return;
                case 142:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info141 = new RadioResponseInfo();
                    info141.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_2.VoiceRegStateResult voiceRegResponse2 = new android.hardware.radio.V1_2.VoiceRegStateResult();
                    voiceRegResponse2.readFromParcel(_hidl_request);
                    getVoiceRegistrationStateResponse_1_2(info141, voiceRegResponse2);
                    return;
                case 143:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info142 = new RadioResponseInfo();
                    info142.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_2.DataRegStateResult dataRegResponse2 = new android.hardware.radio.V1_2.DataRegStateResult();
                    dataRegResponse2.readFromParcel(_hidl_request);
                    getDataRegistrationStateResponse_1_2(info142, dataRegResponse2);
                    return;
                case 144:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info143 = new RadioResponseInfo();
                    info143.readFromParcel(_hidl_request);
                    setSystemSelectionChannelsResponse(info143);
                    return;
                case 145:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info144 = new RadioResponseInfo();
                    info144.readFromParcel(_hidl_request);
                    enableModemResponse(info144);
                    return;
                case 146:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info145 = new RadioResponseInfo();
                    info145.readFromParcel(_hidl_request);
                    boolean isEnabled = _hidl_request.readBool();
                    getModemStackStatusResponse(info145, isEnabled);
                    return;
                case 147:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info146 = new RadioResponseInfo();
                    info146.readFromParcel(_hidl_request);
                    emergencyDialResponse(info146);
                    return;
                case 148:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info147 = new RadioResponseInfo();
                    info147.readFromParcel(_hidl_request);
                    startNetworkScanResponse_1_4(info147);
                    return;
                case 149:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info148 = new RadioResponseInfo();
                    info148.readFromParcel(_hidl_request);
                    ArrayList<android.hardware.radio.V1_4.CellInfo> cellInfo3 = android.hardware.radio.V1_4.CellInfo.readVectorFromParcel(_hidl_request);
                    getCellInfoListResponse_1_4(info148, cellInfo3);
                    return;
                case 150:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info149 = new RadioResponseInfo();
                    info149.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_4.DataRegStateResult dataRegResponse3 = new android.hardware.radio.V1_4.DataRegStateResult();
                    dataRegResponse3.readFromParcel(_hidl_request);
                    getDataRegistrationStateResponse_1_4(info149, dataRegResponse3);
                    return;
                case 151:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info150 = new RadioResponseInfo();
                    info150.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_4.CardStatus cardStatus3 = new android.hardware.radio.V1_4.CardStatus();
                    cardStatus3.readFromParcel(_hidl_request);
                    getIccCardStatusResponse_1_4(info150, cardStatus3);
                    return;
                case 152:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info151 = new RadioResponseInfo();
                    info151.readFromParcel(_hidl_request);
                    int networkTypeBitmap = _hidl_request.readInt32();
                    getPreferredNetworkTypeBitmapResponse(info151, networkTypeBitmap);
                    return;
                case 153:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info152 = new RadioResponseInfo();
                    info152.readFromParcel(_hidl_request);
                    setPreferredNetworkTypeBitmapResponse(info152);
                    return;
                case 154:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info153 = new RadioResponseInfo();
                    info153.readFromParcel(_hidl_request);
                    getDataCallListResponse_1_4(info153, android.hardware.radio.V1_4.SetupDataCallResult.readVectorFromParcel(_hidl_request));
                    return;
                case 155:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info154 = new RadioResponseInfo();
                    info154.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_4.SetupDataCallResult dcResponse2 = new android.hardware.radio.V1_4.SetupDataCallResult();
                    dcResponse2.readFromParcel(_hidl_request);
                    setupDataCallResponse_1_4(info154, dcResponse2);
                    return;
                case 156:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info155 = new RadioResponseInfo();
                    info155.readFromParcel(_hidl_request);
                    setAllowedCarriersResponse_1_4(info155);
                    return;
                case 157:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info156 = new RadioResponseInfo();
                    info156.readFromParcel(_hidl_request);
                    CarrierRestrictionsWithPriority carriers2 = new CarrierRestrictionsWithPriority();
                    carriers2.readFromParcel(_hidl_request);
                    int multiSimPolicy = _hidl_request.readInt32();
                    getAllowedCarriersResponse_1_4(info156, carriers2, multiSimPolicy);
                    return;
                case 158:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadioResponse.kInterfaceName);
                    RadioResponseInfo info157 = new RadioResponseInfo();
                    info157.readFromParcel(_hidl_request);
                    android.hardware.radio.V1_4.SignalStrength signalStrength2 = new android.hardware.radio.V1_4.SignalStrength();
                    signalStrength2.readFromParcel(_hidl_request);
                    getSignalStrengthResponse_1_4(info157, signalStrength2);
                    return;
                case 159:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info158 = new RadioResponseInfo();
                    info158.readFromParcel(_hidl_request);
                    setSignalStrengthReportingCriteriaResponse_1_5(info158);
                    return;
                case 160:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info159 = new RadioResponseInfo();
                    info159.readFromParcel(_hidl_request);
                    setLinkCapacityReportingCriteriaResponse_1_5(info159);
                    return;
                case 161:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info160 = new RadioResponseInfo();
                    info160.readFromParcel(_hidl_request);
                    enableUiccApplicationsResponse(info160);
                    return;
                case 162:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info161 = new RadioResponseInfo();
                    info161.readFromParcel(_hidl_request);
                    boolean enabled = _hidl_request.readBool();
                    areUiccApplicationsEnabledResponse(info161, enabled);
                    return;
                case 163:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info162 = new RadioResponseInfo();
                    info162.readFromParcel(_hidl_request);
                    setSystemSelectionChannelsResponse_1_5(info162);
                    return;
                case 164:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info163 = new RadioResponseInfo();
                    info163.readFromParcel(_hidl_request);
                    startNetworkScanResponse_1_5(info163);
                    return;
                case 165:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info164 = new RadioResponseInfo();
                    info164.readFromParcel(_hidl_request);
                    SetupDataCallResult dcResponse3 = new SetupDataCallResult();
                    dcResponse3.readFromParcel(_hidl_request);
                    setupDataCallResponse_1_5(info164, dcResponse3);
                    return;
                case 166:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info165 = new RadioResponseInfo();
                    info165.readFromParcel(_hidl_request);
                    getDataCallListResponse_1_5(info165, SetupDataCallResult.readVectorFromParcel(_hidl_request));
                    return;
                case 167:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info166 = new RadioResponseInfo();
                    info166.readFromParcel(_hidl_request);
                    setInitialAttachApnResponse_1_5(info166);
                    return;
                case 168:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info167 = new RadioResponseInfo();
                    info167.readFromParcel(_hidl_request);
                    setDataProfileResponse_1_5(info167);
                    return;
                case 169:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info168 = new RadioResponseInfo();
                    info168.readFromParcel(_hidl_request);
                    setRadioPowerResponse_1_5(info168);
                    return;
                case 170:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info169 = new RadioResponseInfo();
                    info169.readFromParcel(_hidl_request);
                    setIndicationFilterResponse_1_5(info169);
                    return;
                case 171:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info170 = new RadioResponseInfo();
                    info170.readFromParcel(_hidl_request);
                    CellIdentity cellIdentity = new CellIdentity();
                    cellIdentity.readFromParcel(_hidl_request);
                    ArrayList<BarringInfo> barringInfos = BarringInfo.readVectorFromParcel(_hidl_request);
                    getBarringInfoResponse(info170, cellIdentity, barringInfos);
                    return;
                case 172:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info171 = new RadioResponseInfo();
                    info171.readFromParcel(_hidl_request);
                    RegStateResult voiceRegResponse3 = new RegStateResult();
                    voiceRegResponse3.readFromParcel(_hidl_request);
                    getVoiceRegistrationStateResponse_1_5(info171, voiceRegResponse3);
                    return;
                case 173:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info172 = new RadioResponseInfo();
                    info172.readFromParcel(_hidl_request);
                    RegStateResult dataRegResponse4 = new RegStateResult();
                    dataRegResponse4.readFromParcel(_hidl_request);
                    getDataRegistrationStateResponse_1_5(info172, dataRegResponse4);
                    return;
                case 174:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info173 = new RadioResponseInfo();
                    info173.readFromParcel(_hidl_request);
                    ArrayList<CellInfo> cellInfo4 = CellInfo.readVectorFromParcel(_hidl_request);
                    getCellInfoListResponse_1_5(info173, cellInfo4);
                    return;
                case 175:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info174 = new RadioResponseInfo();
                    info174.readFromParcel(_hidl_request);
                    setNetworkSelectionModeManualResponse_1_5(info174);
                    return;
                case 176:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info175 = new RadioResponseInfo();
                    info175.readFromParcel(_hidl_request);
                    SendSmsResult sms5 = new SendSmsResult();
                    sms5.readFromParcel(_hidl_request);
                    sendCdmaSmsExpectMoreResponse(info175, sms5);
                    return;
                case 177:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info176 = new RadioResponseInfo();
                    info176.readFromParcel(_hidl_request);
                    int persoType = _hidl_request.readInt32();
                    int remainingRetries8 = _hidl_request.readInt32();
                    supplySimDepersonalizationResponse(info176, persoType, remainingRetries8);
                    return;
                case 178:
                    _hidl_request.enforceInterface(IRadioResponse.kInterfaceName);
                    RadioResponseInfo info177 = new RadioResponseInfo();
                    info177.readFromParcel(_hidl_request);
                    CardStatus cardStatus4 = new CardStatus();
                    cardStatus4.readFromParcel(_hidl_request);
                    getIccCardStatusResponse_1_5(info177, cardStatus4);
                    return;
                case 256067662:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<String> _hidl_out_descriptors = interfaceChain();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStringVector(_hidl_out_descriptors);
                    _hidl_reply.send();
                    return;
                case 256131655:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    NativeHandle fd = _hidl_request.readNativeHandle();
                    ArrayList<String> options = _hidl_request.readStringVector();
                    debug(fd, options);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256136003:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    String _hidl_out_descriptor = interfaceDescriptor();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeString(_hidl_out_descriptor);
                    _hidl_reply.send();
                    return;
                case 256398152:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<byte[]> _hidl_out_hashchain = getHashChain();
                    _hidl_reply.writeStatus(0);
                    HwBlob _hidl_blob = new HwBlob(16);
                    int _hidl_vec_size = _hidl_out_hashchain.size();
                    _hidl_blob.putInt32(8L, _hidl_vec_size);
                    _hidl_blob.putBool(12L, false);
                    HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        long _hidl_array_offset_1 = _hidl_index_0 * 32;
                        byte[] _hidl_array_item_1 = _hidl_out_hashchain.get(_hidl_index_0);
                        if (_hidl_array_item_1 == null || _hidl_array_item_1.length != 32) {
                            throw new IllegalArgumentException("Array element is not of the expected length");
                        }
                        childBlob.putInt8Array(_hidl_array_offset_1, _hidl_array_item_1);
                    }
                    _hidl_blob.putBlob(0L, childBlob);
                    _hidl_reply.writeBuffer(_hidl_blob);
                    _hidl_reply.send();
                    return;
                case 256462420:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    setHALInstrumentation();
                    return;
                case 256660548:
                default:
                    return;
                case 256921159:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ping();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 257049926:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    DebugInfo _hidl_out_info = getDebugInfo();
                    _hidl_reply.writeStatus(0);
                    _hidl_out_info.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 257120595:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    notifySyspropsChanged();
                    return;
            }
        }
    }
}
