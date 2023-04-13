package android.hardware.radio.V1_5;

import android.hardware.radio.V1_0.CallForwardInfo;
import android.hardware.radio.V1_0.CarrierRestrictions;
import android.hardware.radio.V1_0.CdmaBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.CdmaSmsAck;
import android.hardware.radio.V1_0.CdmaSmsMessage;
import android.hardware.radio.V1_0.CdmaSmsWriteArgs;
import android.hardware.radio.V1_0.Dial;
import android.hardware.radio.V1_0.GsmBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.GsmSmsMessage;
import android.hardware.radio.V1_0.IccIo;
import android.hardware.radio.V1_0.ImsSmsMessage;
import android.hardware.radio.V1_0.NvWriteItem;
import android.hardware.radio.V1_0.RadioCapability;
import android.hardware.radio.V1_0.SelectUiccSub;
import android.hardware.radio.V1_0.SimApdu;
import android.hardware.radio.V1_0.SmsWriteArgs;
import android.hardware.radio.V1_1.ImsiEncryptionInfo;
import android.hardware.radio.V1_1.KeepaliveRequest;
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
import com.android.internal.telephony.GsmAlphabet;
import com.android.net.module.util.NetworkStackConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IRadio extends android.hardware.radio.V1_4.IRadio {
    public static final String kInterfaceName = "android.hardware.radio@1.5::IRadio";

    void areUiccApplicationsEnabled(int i) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void enableUiccApplications(int i, boolean z) throws RemoteException;

    void getBarringInfo(int i) throws RemoteException;

    void getDataRegistrationState_1_5(int i) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getVoiceRegistrationState_1_5(int i) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    void sendCdmaSmsExpectMore(int i, CdmaSmsMessage cdmaSmsMessage) throws RemoteException;

    void setDataProfile_1_5(int i, ArrayList<DataProfileInfo> arrayList) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    void setIndicationFilter_1_5(int i, int i2) throws RemoteException;

    void setInitialAttachApn_1_5(int i, DataProfileInfo dataProfileInfo) throws RemoteException;

    void setLinkCapacityReportingCriteria_1_5(int i, int i2, int i3, int i4, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2, int i5) throws RemoteException;

    void setNetworkSelectionModeManual_1_5(int i, String str, int i2) throws RemoteException;

    void setRadioPower_1_5(int i, boolean z, boolean z2, boolean z3) throws RemoteException;

    void setSignalStrengthReportingCriteria_1_5(int i, SignalThresholdInfo signalThresholdInfo, int i2) throws RemoteException;

    void setSystemSelectionChannels_1_5(int i, boolean z, ArrayList<RadioAccessSpecifier> arrayList) throws RemoteException;

    void setupDataCall_1_5(int i, int i2, DataProfileInfo dataProfileInfo, boolean z, int i3, ArrayList<LinkAddress> arrayList, ArrayList<String> arrayList2) throws RemoteException;

    void startNetworkScan_1_5(int i, NetworkScanRequest networkScanRequest) throws RemoteException;

    void supplySimDepersonalization(int i, int i2, String str) throws RemoteException;

    @Override // android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IRadio asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IRadio)) {
            return (IRadio) iface;
        }
        IRadio proxy = new Proxy(binder);
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

    static IRadio castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IRadio getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IRadio getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IRadio getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IRadio getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IRadio {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.radio@1.5::IRadio]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setResponseFunctions(android.hardware.radio.V1_0.IRadioResponse radioResponse, android.hardware.radio.V1_0.IRadioIndication radioIndication) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeStrongBinder(radioResponse == null ? null : radioResponse.asBinder());
            _hidl_request.writeStrongBinder(radioIndication != null ? radioIndication.asBinder() : null);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getIccCardStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void supplyIccPinForApp(int serial, String pin, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(pin);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void supplyIccPukForApp(int serial, String puk, String pin, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(puk);
            _hidl_request.writeString(pin);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void supplyIccPin2ForApp(int serial, String pin2, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(pin2);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void supplyIccPuk2ForApp(int serial, String puk2, String pin2, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(puk2);
            _hidl_request.writeString(pin2);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void changeIccPinForApp(int serial, String oldPin, String newPin, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(oldPin);
            _hidl_request.writeString(newPin);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void changeIccPin2ForApp(int serial, String oldPin2, String newPin2, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(oldPin2);
            _hidl_request.writeString(newPin2);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void supplyNetworkDepersonalization(int serial, String netPin) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(netPin);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCurrentCalls(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void dial(int serial, Dial dialInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dialInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getImsiForApp(int serial, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void hangup(int serial, int gsmIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(gsmIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void hangupWaitingOrBackground(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void hangupForegroundResumeBackground(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void switchWaitingOrHoldingAndActive(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void conference(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void rejectCall(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getLastCallFailCause(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getSignalStrength(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getVoiceRegistrationState(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getDataRegistrationState(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getOperator(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setRadioPower(int serial, boolean on) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(on);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendDtmf(int serial, String s) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(s);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendSms(int serial, GsmSmsMessage message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendSMSExpectMore(int serial, GsmSmsMessage message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setupDataCall(int serial, int radioTechnology, android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo, boolean modemCognitive, boolean roamingAllowed, boolean isRoaming) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(radioTechnology);
            dataProfileInfo.writeToParcel(_hidl_request);
            _hidl_request.writeBool(modemCognitive);
            _hidl_request.writeBool(roamingAllowed);
            _hidl_request.writeBool(isRoaming);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void iccIOForApp(int serial, IccIo iccIo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            iccIo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendUssd(int serial, String ussd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(ussd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void cancelPendingUssd(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getClir(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setClir(int serial, int status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCallForwardStatus(int serial, CallForwardInfo callInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            callInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCallForward(int serial, CallForwardInfo callInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            callInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCallWaiting(int serial, int serviceClass) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(serviceClass);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCallWaiting(int serial, boolean enable, int serviceClass) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            _hidl_request.writeInt32(serviceClass);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void acknowledgeLastIncomingGsmSms(int serial, boolean success, int cause) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(success);
            _hidl_request.writeInt32(cause);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void acceptCall(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void deactivateDataCall(int serial, int cid, boolean reasonRadioShutDown) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(cid);
            _hidl_request.writeBool(reasonRadioShutDown);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getFacilityLockForApp(int serial, String facility, String password, int serviceClass, String appId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(facility);
            _hidl_request.writeString(password);
            _hidl_request.writeInt32(serviceClass);
            _hidl_request.writeString(appId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setFacilityLockForApp(int serial, String facility, boolean lockState, String password, int serviceClass, String appId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(facility);
            _hidl_request.writeBool(lockState);
            _hidl_request.writeString(password);
            _hidl_request.writeInt32(serviceClass);
            _hidl_request.writeString(appId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setBarringPassword(int serial, String facility, String oldPassword, String newPassword) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(facility);
            _hidl_request.writeString(oldPassword);
            _hidl_request.writeString(newPassword);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getNetworkSelectionMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setNetworkSelectionModeAutomatic(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setNetworkSelectionModeManual(int serial, String operatorNumeric) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(operatorNumeric);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getAvailableNetworks(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void startDtmf(int serial, String s) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(s);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void stopDtmf(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getBasebandVersion(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void separateConnection(int serial, int gsmIndex) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(gsmIndex);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setMute(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getMute(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getClip(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getDataCallList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setSuppServiceNotifications(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void writeSmsToSim(int serial, SmsWriteArgs smsWriteArgs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            smsWriteArgs.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(57, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void deleteSmsOnSim(int serial, int index) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(index);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(58, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setBandMode(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(59, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getAvailableBandModes(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(60, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendEnvelope(int serial, String command) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(command);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(61, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendTerminalResponseToSim(int serial, String commandResponse) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(commandResponse);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(62, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void handleStkCallSetupRequestFromSim(int serial, boolean accept) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(accept);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(63, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void explicitCallTransfer(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(64, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setPreferredNetworkType(int serial, int nwType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(nwType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(65, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getPreferredNetworkType(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(66, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getNeighboringCids(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(67, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setLocationUpdates(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(68, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCdmaSubscriptionSource(int serial, int cdmaSub) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(cdmaSub);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(69, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCdmaRoamingPreference(int serial, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(70, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCdmaRoamingPreference(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(71, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setTTYMode(int serial, int mode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(mode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(72, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getTTYMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(73, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setPreferredVoicePrivacy(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(74, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getPreferredVoicePrivacy(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(75, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendCDMAFeatureCode(int serial, String featureCode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(featureCode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(76, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendBurstDtmf(int serial, String dtmf, int on, int off) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(dtmf);
            _hidl_request.writeInt32(on);
            _hidl_request.writeInt32(off);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(77, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendCdmaSms(int serial, CdmaSmsMessage sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(78, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void acknowledgeLastIncomingCdmaSms(int serial, CdmaSmsAck smsAck) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            smsAck.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(79, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getGsmBroadcastConfig(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(80, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setGsmBroadcastConfig(int serial, ArrayList<GsmBroadcastSmsConfigInfo> configInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            GsmBroadcastSmsConfigInfo.writeVectorToParcel(_hidl_request, configInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(81, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setGsmBroadcastActivation(int serial, boolean activate) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(activate);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(82, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCdmaBroadcastConfig(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(83, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCdmaBroadcastConfig(int serial, ArrayList<CdmaBroadcastSmsConfigInfo> configInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            CdmaBroadcastSmsConfigInfo.writeVectorToParcel(_hidl_request, configInfo);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(84, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCdmaBroadcastActivation(int serial, boolean activate) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(activate);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(85, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCDMASubscription(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(86, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void writeSmsToRuim(int serial, CdmaSmsWriteArgs cdmaSms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            cdmaSms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(87, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void deleteSmsOnRuim(int serial, int index) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(index);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(88, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getDeviceIdentity(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(89, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void exitEmergencyCallbackMode(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(90, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getSmscAddress(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(91, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setSmscAddress(int serial, String smsc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(smsc);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(92, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void reportSmsMemoryStatus(int serial, boolean available) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(available);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(93, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void reportStkServiceIsRunning(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(94, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCdmaSubscriptionSource(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(95, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void requestIsimAuthentication(int serial, String challenge) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(challenge);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(96, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void acknowledgeIncomingGsmSmsWithPdu(int serial, boolean success, String ackPdu) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(success);
            _hidl_request.writeString(ackPdu);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(97, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendEnvelopeWithStatus(int serial, String contents) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(contents);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(98, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getVoiceRadioTechnology(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(99, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getCellInfoList(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(100, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setCellInfoListRate(int serial, int rate) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(rate);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(101, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setInitialAttachApn(int serial, android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo, boolean modemCognitive, boolean isRoaming) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dataProfileInfo.writeToParcel(_hidl_request);
            _hidl_request.writeBool(modemCognitive);
            _hidl_request.writeBool(isRoaming);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(102, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getImsRegistrationState(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(103, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendImsSms(int serial, ImsSmsMessage message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(104, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void iccTransmitApduBasicChannel(int serial, SimApdu message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(105, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void iccOpenLogicalChannel(int serial, String aid, int p2) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(aid);
            _hidl_request.writeInt32(p2);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(106, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void iccCloseLogicalChannel(int serial, int channelId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(channelId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(107, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void iccTransmitApduLogicalChannel(int serial, SimApdu message) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            message.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(108, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void nvReadItem(int serial, int itemId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(itemId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(109, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void nvWriteItem(int serial, NvWriteItem item) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            item.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(110, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void nvWriteCdmaPrl(int serial, ArrayList<Byte> prl) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt8Vector(prl);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(111, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void nvResetConfig(int serial, int resetType) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(resetType);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(112, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setUiccSubscription(int serial, SelectUiccSub uiccSub) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            uiccSub.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(113, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setDataAllowed(int serial, boolean allow) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(allow);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(114, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getHardwareConfig(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(115, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void requestIccSimAuthentication(int serial, int authContext, String authData, String aid) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(authContext);
            _hidl_request.writeString(authData);
            _hidl_request.writeString(aid);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(116, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setDataProfile(int serial, ArrayList<android.hardware.radio.V1_0.DataProfileInfo> profiles, boolean isRoaming) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            android.hardware.radio.V1_0.DataProfileInfo.writeVectorToParcel(_hidl_request, profiles);
            _hidl_request.writeBool(isRoaming);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(117, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void requestShutdown(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(118, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getRadioCapability(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(119, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setRadioCapability(int serial, RadioCapability rc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            rc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(120, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void startLceService(int serial, int reportInterval, boolean pullMode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(reportInterval);
            _hidl_request.writeBool(pullMode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(121, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void stopLceService(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(122, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void pullLceData(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(123, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void getModemActivityInfo(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(124, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setAllowedCarriers(int serial, boolean allAllowed, CarrierRestrictions carriers) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
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

        @Override // android.hardware.radio.V1_0.IRadio
        public void getAllowedCarriers(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(126, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void sendDeviceState(int serial, int deviceStateType, boolean state) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(deviceStateType);
            _hidl_request.writeBool(state);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(127, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setIndicationFilter(int serial, int indicationFilter) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(indicationFilter);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(128, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void setSimCardPower(int serial, boolean powerUp) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(powerUp);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(129, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadio
        public void responseAcknowledgement() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadio.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(130, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void setCarrierInfoForImsiEncryption(int serial, ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            imsiEncryptionInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(131, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void setSimCardPower_1_1(int serial, int powerUp) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(powerUp);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(132, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void startNetworkScan(int serial, android.hardware.radio.V1_1.NetworkScanRequest request) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            request.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(133, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void stopNetworkScan(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(134, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void startKeepalive(int serial, KeepaliveRequest keepalive) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            keepalive.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(135, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadio
        public void stopKeepalive(int serial, int sessionHandle) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(sessionHandle);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(136, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void startNetworkScan_1_2(int serial, android.hardware.radio.V1_2.NetworkScanRequest request) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            request.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(137, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void setIndicationFilter_1_2(int serial, int indicationFilter) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(indicationFilter);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(138, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void setSignalStrengthReportingCriteria(int serial, int hysteresisMs, int hysteresisDb, ArrayList<Integer> thresholdsDbm, int accessNetwork) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(hysteresisMs);
            _hidl_request.writeInt32(hysteresisDb);
            _hidl_request.writeInt32Vector(thresholdsDbm);
            _hidl_request.writeInt32(accessNetwork);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(139, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void setLinkCapacityReportingCriteria(int serial, int hysteresisMs, int hysteresisDlKbps, int hysteresisUlKbps, ArrayList<Integer> thresholdsDownlinkKbps, ArrayList<Integer> thresholdsUplinkKbps, int accessNetwork) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(hysteresisMs);
            _hidl_request.writeInt32(hysteresisDlKbps);
            _hidl_request.writeInt32(hysteresisUlKbps);
            _hidl_request.writeInt32Vector(thresholdsDownlinkKbps);
            _hidl_request.writeInt32Vector(thresholdsUplinkKbps);
            _hidl_request.writeInt32(accessNetwork);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(140, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void setupDataCall_1_2(int serial, int accessNetwork, android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo, boolean modemCognitive, boolean roamingAllowed, boolean isRoaming, int reason, ArrayList<String> addresses, ArrayList<String> dnses) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(accessNetwork);
            dataProfileInfo.writeToParcel(_hidl_request);
            _hidl_request.writeBool(modemCognitive);
            _hidl_request.writeBool(roamingAllowed);
            _hidl_request.writeBool(isRoaming);
            _hidl_request.writeInt32(reason);
            _hidl_request.writeStringVector(addresses);
            _hidl_request.writeStringVector(dnses);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(141, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadio
        public void deactivateDataCall_1_2(int serial, int cid, int reason) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(cid);
            _hidl_request.writeInt32(reason);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(142, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadio
        public void setSystemSelectionChannels(int serial, boolean specifyChannels, ArrayList<android.hardware.radio.V1_1.RadioAccessSpecifier> specifiers) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(specifyChannels);
            android.hardware.radio.V1_1.RadioAccessSpecifier.writeVectorToParcel(_hidl_request, specifiers);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(143, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadio
        public void enableModem(int serial, boolean on) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(on);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(144, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_3.IRadio
        public void getModemStackStatus(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_3.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(145, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void setupDataCall_1_4(int serial, int accessNetwork, android.hardware.radio.V1_4.DataProfileInfo dataProfileInfo, boolean roamingAllowed, int reason, ArrayList<String> addresses, ArrayList<String> dnses) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(accessNetwork);
            dataProfileInfo.writeToParcel(_hidl_request);
            _hidl_request.writeBool(roamingAllowed);
            _hidl_request.writeInt32(reason);
            _hidl_request.writeStringVector(addresses);
            _hidl_request.writeStringVector(dnses);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(146, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void setInitialAttachApn_1_4(int serial, android.hardware.radio.V1_4.DataProfileInfo dataProfileInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dataProfileInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(147, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void setDataProfile_1_4(int serial, ArrayList<android.hardware.radio.V1_4.DataProfileInfo> profiles) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            android.hardware.radio.V1_4.DataProfileInfo.writeVectorToParcel(_hidl_request, profiles);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(148, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void emergencyDial(int serial, Dial dialInfo, int categories, ArrayList<String> urns, int routing, boolean hasKnownUserIntentEmergency, boolean isTesting) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dialInfo.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(categories);
            _hidl_request.writeStringVector(urns);
            _hidl_request.writeInt32(routing);
            _hidl_request.writeBool(hasKnownUserIntentEmergency);
            _hidl_request.writeBool(isTesting);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(149, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void startNetworkScan_1_4(int serial, android.hardware.radio.V1_2.NetworkScanRequest request) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            request.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(150, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void getPreferredNetworkTypeBitmap(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(151, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void setPreferredNetworkTypeBitmap(int serial, int networkTypeBitmap) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(networkTypeBitmap);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(152, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void setAllowedCarriers_1_4(int serial, CarrierRestrictionsWithPriority carriers, int multiSimPolicy) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            carriers.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(multiSimPolicy);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(153, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void getAllowedCarriers_1_4(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(154, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadio
        public void getSignalStrength_1_4(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_4.IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(155, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setSignalStrengthReportingCriteria_1_5(int serial, SignalThresholdInfo signalThresholdInfo, int accessNetwork) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            signalThresholdInfo.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(accessNetwork);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(156, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setLinkCapacityReportingCriteria_1_5(int serial, int hysteresisMs, int hysteresisDlKbps, int hysteresisUlKbps, ArrayList<Integer> thresholdsDownlinkKbps, ArrayList<Integer> thresholdsUplinkKbps, int accessNetwork) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(hysteresisMs);
            _hidl_request.writeInt32(hysteresisDlKbps);
            _hidl_request.writeInt32(hysteresisUlKbps);
            _hidl_request.writeInt32Vector(thresholdsDownlinkKbps);
            _hidl_request.writeInt32Vector(thresholdsUplinkKbps);
            _hidl_request.writeInt32(accessNetwork);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(157, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void enableUiccApplications(int serial, boolean enable) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(enable);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(158, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void areUiccApplicationsEnabled(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(159, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setSystemSelectionChannels_1_5(int serial, boolean specifyChannels, ArrayList<RadioAccessSpecifier> specifiers) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(specifyChannels);
            RadioAccessSpecifier.writeVectorToParcel(_hidl_request, specifiers);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(160, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void startNetworkScan_1_5(int serial, NetworkScanRequest request) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            request.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(161, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setupDataCall_1_5(int serial, int accessNetwork, DataProfileInfo dataProfileInfo, boolean roamingAllowed, int reason, ArrayList<LinkAddress> addresses, ArrayList<String> dnses) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(accessNetwork);
            dataProfileInfo.writeToParcel(_hidl_request);
            _hidl_request.writeBool(roamingAllowed);
            _hidl_request.writeInt32(reason);
            LinkAddress.writeVectorToParcel(_hidl_request, addresses);
            _hidl_request.writeStringVector(dnses);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(162, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setInitialAttachApn_1_5(int serial, DataProfileInfo dataProfileInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            dataProfileInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(163, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setDataProfile_1_5(int serial, ArrayList<DataProfileInfo> profiles) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            DataProfileInfo.writeVectorToParcel(_hidl_request, profiles);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(164, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setRadioPower_1_5(int serial, boolean powerOn, boolean forEmergencyCall, boolean preferredForEmergencyCall) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeBool(powerOn);
            _hidl_request.writeBool(forEmergencyCall);
            _hidl_request.writeBool(preferredForEmergencyCall);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(165, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setIndicationFilter_1_5(int serial, int indicationFilter) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(indicationFilter);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(166, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void getBarringInfo(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(167, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void getVoiceRegistrationState_1_5(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(168, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void getDataRegistrationState_1_5(int serial) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(169, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void setNetworkSelectionModeManual_1_5(int serial, String operatorNumeric, int ran) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeString(operatorNumeric);
            _hidl_request.writeInt32(ran);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(170, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void sendCdmaSmsExpectMore(int serial, CdmaSmsMessage sms) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            sms.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(171, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio
        public void supplySimDepersonalization(int serial, int persoType, String controlKey) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadio.kInterfaceName);
            _hidl_request.writeInt32(serial);
            _hidl_request.writeInt32(persoType);
            _hidl_request.writeString(controlKey);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(172, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IRadio {
        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IRadio.kInterfaceName, android.hardware.radio.V1_4.IRadio.kInterfaceName, android.hardware.radio.V1_3.IRadio.kInterfaceName, android.hardware.radio.V1_2.IRadio.kInterfaceName, android.hardware.radio.V1_1.IRadio.kInterfaceName, android.hardware.radio.V1_0.IRadio.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IRadio.kInterfaceName;
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-76, 84, -33, -123, 52, 65, -63, 47, 110, 66, 94, -118, 96, -35, 41, -3, -94, MidiConstants.STATUS_CHANNEL_MASK, 94, 110, 57, -71, 61, 17, 3, -28, -77, 116, -107, -37, 56, -86}, new byte[]{-17, 74, -73, 65, -9, -25, 118, 47, -76, 94, 46, 36, -54, -125, -121, 31, 114, 0, 108, MidiConstants.STATUS_PITCH_BEND, 95, 87, -86, -102, -35, -59, 116, -119, 61, -46, -104, 114}, new byte[]{-95, -58, MidiConstants.STATUS_CONTROL_CHANGE, 118, GsmAlphabet.GSM_EXTENDED_ESCAPE, -53, -119, -42, -65, 21, -95, 86, -7, 48, 107, Byte.MIN_VALUE, MidiConstants.STATUS_NOTE_ON, -77, -87, 22, -95, 95, -22, 22, -119, -76, MidiConstants.STATUS_CONTROL_CHANGE, -63, 115, -114, 56, 47}, new byte[]{43, 90, -2, -10, -114, 62, 47, MidiConstants.STATUS_MIDI_TIME_CODE, -38, -74, 62, 79, 46, -27, 115, 55, -17, 38, 53, -20, -127, 47, 73, 8, 12, -83, -4, -23, 102, -45, 59, 82}, new byte[]{-122, -5, 7, -102, 96, 11, 35, 1, -89, 82, 36, -99, -5, -4, 83, -104, 58, 121, 93, 117, 47, 17, -86, -68, -74, -125, 21, -95, -119, -10, -55, -94}, new byte[]{-49, -86, MidiConstants.STATUS_CONTROL_CHANGE, -28, 92, 93, 123, 53, -107, 3, 45, 100, -99, -94, -98, -41, 18, -23, NetworkStackConstants.TCPHDR_URG, -7, 86, -63, 54, 113, -17, -45, 86, 2, -6, -127, -55, 35}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.radio.V1_5.IRadio, android.hardware.radio.V1_4.IRadio, android.hardware.radio.V1_3.IRadio, android.hardware.radio.V1_2.IRadio, android.hardware.radio.V1_1.IRadio, android.hardware.radio.V1_0.IRadio, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IRadio.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    android.hardware.radio.V1_0.IRadioResponse radioResponse = android.hardware.radio.V1_0.IRadioResponse.asInterface(_hidl_request.readStrongBinder());
                    android.hardware.radio.V1_0.IRadioIndication radioIndication = android.hardware.radio.V1_0.IRadioIndication.asInterface(_hidl_request.readStrongBinder());
                    setResponseFunctions(radioResponse, radioIndication);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial = _hidl_request.readInt32();
                    getIccCardStatus(serial);
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial2 = _hidl_request.readInt32();
                    String pin = _hidl_request.readString();
                    String aid = _hidl_request.readString();
                    supplyIccPinForApp(serial2, pin, aid);
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial3 = _hidl_request.readInt32();
                    String puk = _hidl_request.readString();
                    String pin2 = _hidl_request.readString();
                    String aid2 = _hidl_request.readString();
                    supplyIccPukForApp(serial3, puk, pin2, aid2);
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial4 = _hidl_request.readInt32();
                    String pin22 = _hidl_request.readString();
                    String aid3 = _hidl_request.readString();
                    supplyIccPin2ForApp(serial4, pin22, aid3);
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial5 = _hidl_request.readInt32();
                    String puk2 = _hidl_request.readString();
                    String pin23 = _hidl_request.readString();
                    String aid4 = _hidl_request.readString();
                    supplyIccPuk2ForApp(serial5, puk2, pin23, aid4);
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial6 = _hidl_request.readInt32();
                    String oldPin = _hidl_request.readString();
                    String newPin = _hidl_request.readString();
                    String aid5 = _hidl_request.readString();
                    changeIccPinForApp(serial6, oldPin, newPin, aid5);
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial7 = _hidl_request.readInt32();
                    String oldPin2 = _hidl_request.readString();
                    String newPin2 = _hidl_request.readString();
                    String aid6 = _hidl_request.readString();
                    changeIccPin2ForApp(serial7, oldPin2, newPin2, aid6);
                    return;
                case 9:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial8 = _hidl_request.readInt32();
                    String netPin = _hidl_request.readString();
                    supplyNetworkDepersonalization(serial8, netPin);
                    return;
                case 10:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial9 = _hidl_request.readInt32();
                    getCurrentCalls(serial9);
                    return;
                case 11:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial10 = _hidl_request.readInt32();
                    Dial dialInfo = new Dial();
                    dialInfo.readFromParcel(_hidl_request);
                    dial(serial10, dialInfo);
                    return;
                case 12:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial11 = _hidl_request.readInt32();
                    String aid7 = _hidl_request.readString();
                    getImsiForApp(serial11, aid7);
                    return;
                case 13:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial12 = _hidl_request.readInt32();
                    int gsmIndex = _hidl_request.readInt32();
                    hangup(serial12, gsmIndex);
                    return;
                case 14:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial13 = _hidl_request.readInt32();
                    hangupWaitingOrBackground(serial13);
                    return;
                case 15:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial14 = _hidl_request.readInt32();
                    hangupForegroundResumeBackground(serial14);
                    return;
                case 16:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial15 = _hidl_request.readInt32();
                    switchWaitingOrHoldingAndActive(serial15);
                    return;
                case 17:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial16 = _hidl_request.readInt32();
                    conference(serial16);
                    return;
                case 18:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial17 = _hidl_request.readInt32();
                    rejectCall(serial17);
                    return;
                case 19:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial18 = _hidl_request.readInt32();
                    getLastCallFailCause(serial18);
                    return;
                case 20:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial19 = _hidl_request.readInt32();
                    getSignalStrength(serial19);
                    return;
                case 21:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial20 = _hidl_request.readInt32();
                    getVoiceRegistrationState(serial20);
                    return;
                case 22:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial21 = _hidl_request.readInt32();
                    getDataRegistrationState(serial21);
                    return;
                case 23:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial22 = _hidl_request.readInt32();
                    getOperator(serial22);
                    return;
                case 24:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial23 = _hidl_request.readInt32();
                    boolean on = _hidl_request.readBool();
                    setRadioPower(serial23, on);
                    return;
                case 25:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial24 = _hidl_request.readInt32();
                    String s = _hidl_request.readString();
                    sendDtmf(serial24, s);
                    return;
                case 26:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial25 = _hidl_request.readInt32();
                    GsmSmsMessage message = new GsmSmsMessage();
                    message.readFromParcel(_hidl_request);
                    sendSms(serial25, message);
                    return;
                case 27:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial26 = _hidl_request.readInt32();
                    GsmSmsMessage message2 = new GsmSmsMessage();
                    message2.readFromParcel(_hidl_request);
                    sendSMSExpectMore(serial26, message2);
                    return;
                case 28:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial27 = _hidl_request.readInt32();
                    int radioTechnology = _hidl_request.readInt32();
                    android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo = new android.hardware.radio.V1_0.DataProfileInfo();
                    dataProfileInfo.readFromParcel(_hidl_request);
                    boolean modemCognitive = _hidl_request.readBool();
                    boolean roamingAllowed = _hidl_request.readBool();
                    boolean isRoaming = _hidl_request.readBool();
                    setupDataCall(serial27, radioTechnology, dataProfileInfo, modemCognitive, roamingAllowed, isRoaming);
                    return;
                case 29:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial28 = _hidl_request.readInt32();
                    IccIo iccIo = new IccIo();
                    iccIo.readFromParcel(_hidl_request);
                    iccIOForApp(serial28, iccIo);
                    return;
                case 30:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial29 = _hidl_request.readInt32();
                    String ussd = _hidl_request.readString();
                    sendUssd(serial29, ussd);
                    return;
                case 31:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial30 = _hidl_request.readInt32();
                    cancelPendingUssd(serial30);
                    return;
                case 32:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial31 = _hidl_request.readInt32();
                    getClir(serial31);
                    return;
                case 33:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial32 = _hidl_request.readInt32();
                    int status = _hidl_request.readInt32();
                    setClir(serial32, status);
                    return;
                case 34:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial33 = _hidl_request.readInt32();
                    CallForwardInfo callInfo = new CallForwardInfo();
                    callInfo.readFromParcel(_hidl_request);
                    getCallForwardStatus(serial33, callInfo);
                    return;
                case 35:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial34 = _hidl_request.readInt32();
                    CallForwardInfo callInfo2 = new CallForwardInfo();
                    callInfo2.readFromParcel(_hidl_request);
                    setCallForward(serial34, callInfo2);
                    return;
                case 36:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial35 = _hidl_request.readInt32();
                    int serviceClass = _hidl_request.readInt32();
                    getCallWaiting(serial35, serviceClass);
                    return;
                case 37:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial36 = _hidl_request.readInt32();
                    boolean enable = _hidl_request.readBool();
                    int serviceClass2 = _hidl_request.readInt32();
                    setCallWaiting(serial36, enable, serviceClass2);
                    return;
                case 38:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial37 = _hidl_request.readInt32();
                    boolean success = _hidl_request.readBool();
                    int cause = _hidl_request.readInt32();
                    acknowledgeLastIncomingGsmSms(serial37, success, cause);
                    return;
                case 39:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial38 = _hidl_request.readInt32();
                    acceptCall(serial38);
                    return;
                case 40:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial39 = _hidl_request.readInt32();
                    int cid = _hidl_request.readInt32();
                    boolean reasonRadioShutDown = _hidl_request.readBool();
                    deactivateDataCall(serial39, cid, reasonRadioShutDown);
                    return;
                case 41:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial40 = _hidl_request.readInt32();
                    String facility = _hidl_request.readString();
                    String password = _hidl_request.readString();
                    int serviceClass3 = _hidl_request.readInt32();
                    String appId = _hidl_request.readString();
                    getFacilityLockForApp(serial40, facility, password, serviceClass3, appId);
                    return;
                case 42:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial41 = _hidl_request.readInt32();
                    String facility2 = _hidl_request.readString();
                    boolean lockState = _hidl_request.readBool();
                    String password2 = _hidl_request.readString();
                    int serviceClass4 = _hidl_request.readInt32();
                    String appId2 = _hidl_request.readString();
                    setFacilityLockForApp(serial41, facility2, lockState, password2, serviceClass4, appId2);
                    return;
                case 43:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial42 = _hidl_request.readInt32();
                    String facility3 = _hidl_request.readString();
                    String oldPassword = _hidl_request.readString();
                    String newPassword = _hidl_request.readString();
                    setBarringPassword(serial42, facility3, oldPassword, newPassword);
                    return;
                case 44:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial43 = _hidl_request.readInt32();
                    getNetworkSelectionMode(serial43);
                    return;
                case 45:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial44 = _hidl_request.readInt32();
                    setNetworkSelectionModeAutomatic(serial44);
                    return;
                case 46:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial45 = _hidl_request.readInt32();
                    String operatorNumeric = _hidl_request.readString();
                    setNetworkSelectionModeManual(serial45, operatorNumeric);
                    return;
                case 47:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial46 = _hidl_request.readInt32();
                    getAvailableNetworks(serial46);
                    return;
                case 48:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial47 = _hidl_request.readInt32();
                    String s2 = _hidl_request.readString();
                    startDtmf(serial47, s2);
                    return;
                case 49:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial48 = _hidl_request.readInt32();
                    stopDtmf(serial48);
                    return;
                case 50:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial49 = _hidl_request.readInt32();
                    getBasebandVersion(serial49);
                    return;
                case 51:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial50 = _hidl_request.readInt32();
                    int gsmIndex2 = _hidl_request.readInt32();
                    separateConnection(serial50, gsmIndex2);
                    return;
                case 52:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial51 = _hidl_request.readInt32();
                    boolean enable2 = _hidl_request.readBool();
                    setMute(serial51, enable2);
                    return;
                case 53:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial52 = _hidl_request.readInt32();
                    getMute(serial52);
                    return;
                case 54:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial53 = _hidl_request.readInt32();
                    getClip(serial53);
                    return;
                case 55:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial54 = _hidl_request.readInt32();
                    getDataCallList(serial54);
                    return;
                case 56:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial55 = _hidl_request.readInt32();
                    boolean enable3 = _hidl_request.readBool();
                    setSuppServiceNotifications(serial55, enable3);
                    return;
                case 57:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial56 = _hidl_request.readInt32();
                    SmsWriteArgs smsWriteArgs = new SmsWriteArgs();
                    smsWriteArgs.readFromParcel(_hidl_request);
                    writeSmsToSim(serial56, smsWriteArgs);
                    return;
                case 58:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial57 = _hidl_request.readInt32();
                    int index = _hidl_request.readInt32();
                    deleteSmsOnSim(serial57, index);
                    return;
                case 59:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial58 = _hidl_request.readInt32();
                    int mode = _hidl_request.readInt32();
                    setBandMode(serial58, mode);
                    return;
                case 60:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial59 = _hidl_request.readInt32();
                    getAvailableBandModes(serial59);
                    return;
                case 61:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial60 = _hidl_request.readInt32();
                    String command = _hidl_request.readString();
                    sendEnvelope(serial60, command);
                    return;
                case 62:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial61 = _hidl_request.readInt32();
                    String commandResponse = _hidl_request.readString();
                    sendTerminalResponseToSim(serial61, commandResponse);
                    return;
                case 63:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial62 = _hidl_request.readInt32();
                    boolean accept = _hidl_request.readBool();
                    handleStkCallSetupRequestFromSim(serial62, accept);
                    return;
                case 64:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial63 = _hidl_request.readInt32();
                    explicitCallTransfer(serial63);
                    return;
                case 65:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial64 = _hidl_request.readInt32();
                    int nwType = _hidl_request.readInt32();
                    setPreferredNetworkType(serial64, nwType);
                    return;
                case 66:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial65 = _hidl_request.readInt32();
                    getPreferredNetworkType(serial65);
                    return;
                case 67:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial66 = _hidl_request.readInt32();
                    getNeighboringCids(serial66);
                    return;
                case 68:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial67 = _hidl_request.readInt32();
                    boolean enable4 = _hidl_request.readBool();
                    setLocationUpdates(serial67, enable4);
                    return;
                case 69:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial68 = _hidl_request.readInt32();
                    int cdmaSub = _hidl_request.readInt32();
                    setCdmaSubscriptionSource(serial68, cdmaSub);
                    return;
                case 70:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial69 = _hidl_request.readInt32();
                    int type = _hidl_request.readInt32();
                    setCdmaRoamingPreference(serial69, type);
                    return;
                case 71:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial70 = _hidl_request.readInt32();
                    getCdmaRoamingPreference(serial70);
                    return;
                case 72:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial71 = _hidl_request.readInt32();
                    int mode2 = _hidl_request.readInt32();
                    setTTYMode(serial71, mode2);
                    return;
                case 73:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial72 = _hidl_request.readInt32();
                    getTTYMode(serial72);
                    return;
                case 74:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial73 = _hidl_request.readInt32();
                    boolean enable5 = _hidl_request.readBool();
                    setPreferredVoicePrivacy(serial73, enable5);
                    return;
                case 75:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial74 = _hidl_request.readInt32();
                    getPreferredVoicePrivacy(serial74);
                    return;
                case 76:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial75 = _hidl_request.readInt32();
                    String featureCode = _hidl_request.readString();
                    sendCDMAFeatureCode(serial75, featureCode);
                    return;
                case 77:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial76 = _hidl_request.readInt32();
                    String dtmf = _hidl_request.readString();
                    int on2 = _hidl_request.readInt32();
                    int off = _hidl_request.readInt32();
                    sendBurstDtmf(serial76, dtmf, on2, off);
                    return;
                case 78:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial77 = _hidl_request.readInt32();
                    CdmaSmsMessage sms = new CdmaSmsMessage();
                    sms.readFromParcel(_hidl_request);
                    sendCdmaSms(serial77, sms);
                    return;
                case 79:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial78 = _hidl_request.readInt32();
                    CdmaSmsAck smsAck = new CdmaSmsAck();
                    smsAck.readFromParcel(_hidl_request);
                    acknowledgeLastIncomingCdmaSms(serial78, smsAck);
                    return;
                case 80:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial79 = _hidl_request.readInt32();
                    getGsmBroadcastConfig(serial79);
                    return;
                case 81:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial80 = _hidl_request.readInt32();
                    ArrayList<GsmBroadcastSmsConfigInfo> configInfo = GsmBroadcastSmsConfigInfo.readVectorFromParcel(_hidl_request);
                    setGsmBroadcastConfig(serial80, configInfo);
                    return;
                case 82:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial81 = _hidl_request.readInt32();
                    boolean activate = _hidl_request.readBool();
                    setGsmBroadcastActivation(serial81, activate);
                    return;
                case 83:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial82 = _hidl_request.readInt32();
                    getCdmaBroadcastConfig(serial82);
                    return;
                case 84:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial83 = _hidl_request.readInt32();
                    ArrayList<CdmaBroadcastSmsConfigInfo> configInfo2 = CdmaBroadcastSmsConfigInfo.readVectorFromParcel(_hidl_request);
                    setCdmaBroadcastConfig(serial83, configInfo2);
                    return;
                case 85:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial84 = _hidl_request.readInt32();
                    boolean activate2 = _hidl_request.readBool();
                    setCdmaBroadcastActivation(serial84, activate2);
                    return;
                case 86:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial85 = _hidl_request.readInt32();
                    getCDMASubscription(serial85);
                    return;
                case 87:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial86 = _hidl_request.readInt32();
                    CdmaSmsWriteArgs cdmaSms = new CdmaSmsWriteArgs();
                    cdmaSms.readFromParcel(_hidl_request);
                    writeSmsToRuim(serial86, cdmaSms);
                    return;
                case 88:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial87 = _hidl_request.readInt32();
                    int index2 = _hidl_request.readInt32();
                    deleteSmsOnRuim(serial87, index2);
                    return;
                case 89:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial88 = _hidl_request.readInt32();
                    getDeviceIdentity(serial88);
                    return;
                case 90:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial89 = _hidl_request.readInt32();
                    exitEmergencyCallbackMode(serial89);
                    return;
                case 91:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial90 = _hidl_request.readInt32();
                    getSmscAddress(serial90);
                    return;
                case 92:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial91 = _hidl_request.readInt32();
                    String smsc = _hidl_request.readString();
                    setSmscAddress(serial91, smsc);
                    return;
                case 93:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial92 = _hidl_request.readInt32();
                    boolean available = _hidl_request.readBool();
                    reportSmsMemoryStatus(serial92, available);
                    return;
                case 94:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial93 = _hidl_request.readInt32();
                    reportStkServiceIsRunning(serial93);
                    return;
                case 95:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial94 = _hidl_request.readInt32();
                    getCdmaSubscriptionSource(serial94);
                    return;
                case 96:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial95 = _hidl_request.readInt32();
                    String challenge = _hidl_request.readString();
                    requestIsimAuthentication(serial95, challenge);
                    return;
                case 97:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial96 = _hidl_request.readInt32();
                    boolean success2 = _hidl_request.readBool();
                    String ackPdu = _hidl_request.readString();
                    acknowledgeIncomingGsmSmsWithPdu(serial96, success2, ackPdu);
                    return;
                case 98:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial97 = _hidl_request.readInt32();
                    String contents = _hidl_request.readString();
                    sendEnvelopeWithStatus(serial97, contents);
                    return;
                case 99:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial98 = _hidl_request.readInt32();
                    getVoiceRadioTechnology(serial98);
                    return;
                case 100:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial99 = _hidl_request.readInt32();
                    getCellInfoList(serial99);
                    return;
                case 101:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial100 = _hidl_request.readInt32();
                    int rate = _hidl_request.readInt32();
                    setCellInfoListRate(serial100, rate);
                    return;
                case 102:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial101 = _hidl_request.readInt32();
                    android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo2 = new android.hardware.radio.V1_0.DataProfileInfo();
                    dataProfileInfo2.readFromParcel(_hidl_request);
                    boolean modemCognitive2 = _hidl_request.readBool();
                    boolean isRoaming2 = _hidl_request.readBool();
                    setInitialAttachApn(serial101, dataProfileInfo2, modemCognitive2, isRoaming2);
                    return;
                case 103:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial102 = _hidl_request.readInt32();
                    getImsRegistrationState(serial102);
                    return;
                case 104:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial103 = _hidl_request.readInt32();
                    ImsSmsMessage message3 = new ImsSmsMessage();
                    message3.readFromParcel(_hidl_request);
                    sendImsSms(serial103, message3);
                    return;
                case 105:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial104 = _hidl_request.readInt32();
                    SimApdu message4 = new SimApdu();
                    message4.readFromParcel(_hidl_request);
                    iccTransmitApduBasicChannel(serial104, message4);
                    return;
                case 106:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial105 = _hidl_request.readInt32();
                    String aid8 = _hidl_request.readString();
                    int p2 = _hidl_request.readInt32();
                    iccOpenLogicalChannel(serial105, aid8, p2);
                    return;
                case 107:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial106 = _hidl_request.readInt32();
                    int channelId = _hidl_request.readInt32();
                    iccCloseLogicalChannel(serial106, channelId);
                    return;
                case 108:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial107 = _hidl_request.readInt32();
                    SimApdu message5 = new SimApdu();
                    message5.readFromParcel(_hidl_request);
                    iccTransmitApduLogicalChannel(serial107, message5);
                    return;
                case 109:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial108 = _hidl_request.readInt32();
                    int itemId = _hidl_request.readInt32();
                    nvReadItem(serial108, itemId);
                    return;
                case 110:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial109 = _hidl_request.readInt32();
                    NvWriteItem item = new NvWriteItem();
                    item.readFromParcel(_hidl_request);
                    nvWriteItem(serial109, item);
                    return;
                case 111:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial110 = _hidl_request.readInt32();
                    ArrayList<Byte> prl = _hidl_request.readInt8Vector();
                    nvWriteCdmaPrl(serial110, prl);
                    return;
                case 112:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial111 = _hidl_request.readInt32();
                    int resetType = _hidl_request.readInt32();
                    nvResetConfig(serial111, resetType);
                    return;
                case 113:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial112 = _hidl_request.readInt32();
                    SelectUiccSub uiccSub = new SelectUiccSub();
                    uiccSub.readFromParcel(_hidl_request);
                    setUiccSubscription(serial112, uiccSub);
                    return;
                case 114:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial113 = _hidl_request.readInt32();
                    boolean allow = _hidl_request.readBool();
                    setDataAllowed(serial113, allow);
                    return;
                case 115:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial114 = _hidl_request.readInt32();
                    getHardwareConfig(serial114);
                    return;
                case 116:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial115 = _hidl_request.readInt32();
                    int authContext = _hidl_request.readInt32();
                    String authData = _hidl_request.readString();
                    String aid9 = _hidl_request.readString();
                    requestIccSimAuthentication(serial115, authContext, authData, aid9);
                    return;
                case 117:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial116 = _hidl_request.readInt32();
                    ArrayList<android.hardware.radio.V1_0.DataProfileInfo> profiles = android.hardware.radio.V1_0.DataProfileInfo.readVectorFromParcel(_hidl_request);
                    boolean isRoaming3 = _hidl_request.readBool();
                    setDataProfile(serial116, profiles, isRoaming3);
                    return;
                case 118:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial117 = _hidl_request.readInt32();
                    requestShutdown(serial117);
                    return;
                case 119:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial118 = _hidl_request.readInt32();
                    getRadioCapability(serial118);
                    return;
                case 120:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial119 = _hidl_request.readInt32();
                    RadioCapability rc = new RadioCapability();
                    rc.readFromParcel(_hidl_request);
                    setRadioCapability(serial119, rc);
                    return;
                case 121:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial120 = _hidl_request.readInt32();
                    int reportInterval = _hidl_request.readInt32();
                    boolean pullMode = _hidl_request.readBool();
                    startLceService(serial120, reportInterval, pullMode);
                    return;
                case 122:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial121 = _hidl_request.readInt32();
                    stopLceService(serial121);
                    return;
                case 123:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial122 = _hidl_request.readInt32();
                    pullLceData(serial122);
                    return;
                case 124:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial123 = _hidl_request.readInt32();
                    getModemActivityInfo(serial123);
                    return;
                case 125:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial124 = _hidl_request.readInt32();
                    boolean allAllowed = _hidl_request.readBool();
                    CarrierRestrictions carriers = new CarrierRestrictions();
                    carriers.readFromParcel(_hidl_request);
                    setAllowedCarriers(serial124, allAllowed, carriers);
                    return;
                case 126:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial125 = _hidl_request.readInt32();
                    getAllowedCarriers(serial125);
                    return;
                case 127:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial126 = _hidl_request.readInt32();
                    int deviceStateType = _hidl_request.readInt32();
                    boolean state = _hidl_request.readBool();
                    sendDeviceState(serial126, deviceStateType, state);
                    return;
                case 128:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial127 = _hidl_request.readInt32();
                    int indicationFilter = _hidl_request.readInt32();
                    setIndicationFilter(serial127, indicationFilter);
                    return;
                case 129:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    int serial128 = _hidl_request.readInt32();
                    boolean powerUp = _hidl_request.readBool();
                    setSimCardPower(serial128, powerUp);
                    return;
                case 130:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadio.kInterfaceName);
                    responseAcknowledgement();
                    return;
                case 131:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial129 = _hidl_request.readInt32();
                    ImsiEncryptionInfo imsiEncryptionInfo = new ImsiEncryptionInfo();
                    imsiEncryptionInfo.readFromParcel(_hidl_request);
                    setCarrierInfoForImsiEncryption(serial129, imsiEncryptionInfo);
                    return;
                case 132:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial130 = _hidl_request.readInt32();
                    int powerUp2 = _hidl_request.readInt32();
                    setSimCardPower_1_1(serial130, powerUp2);
                    return;
                case 133:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial131 = _hidl_request.readInt32();
                    android.hardware.radio.V1_1.NetworkScanRequest request = new android.hardware.radio.V1_1.NetworkScanRequest();
                    request.readFromParcel(_hidl_request);
                    startNetworkScan(serial131, request);
                    return;
                case 134:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial132 = _hidl_request.readInt32();
                    stopNetworkScan(serial132);
                    return;
                case 135:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial133 = _hidl_request.readInt32();
                    KeepaliveRequest keepalive = new KeepaliveRequest();
                    keepalive.readFromParcel(_hidl_request);
                    startKeepalive(serial133, keepalive);
                    return;
                case 136:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadio.kInterfaceName);
                    int serial134 = _hidl_request.readInt32();
                    int sessionHandle = _hidl_request.readInt32();
                    stopKeepalive(serial134, sessionHandle);
                    return;
                case 137:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial135 = _hidl_request.readInt32();
                    android.hardware.radio.V1_2.NetworkScanRequest request2 = new android.hardware.radio.V1_2.NetworkScanRequest();
                    request2.readFromParcel(_hidl_request);
                    startNetworkScan_1_2(serial135, request2);
                    return;
                case 138:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial136 = _hidl_request.readInt32();
                    int indicationFilter2 = _hidl_request.readInt32();
                    setIndicationFilter_1_2(serial136, indicationFilter2);
                    return;
                case 139:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial137 = _hidl_request.readInt32();
                    int hysteresisMs = _hidl_request.readInt32();
                    int hysteresisDb = _hidl_request.readInt32();
                    ArrayList<Integer> thresholdsDbm = _hidl_request.readInt32Vector();
                    int accessNetwork = _hidl_request.readInt32();
                    setSignalStrengthReportingCriteria(serial137, hysteresisMs, hysteresisDb, thresholdsDbm, accessNetwork);
                    return;
                case 140:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial138 = _hidl_request.readInt32();
                    int hysteresisMs2 = _hidl_request.readInt32();
                    int hysteresisDlKbps = _hidl_request.readInt32();
                    int hysteresisUlKbps = _hidl_request.readInt32();
                    ArrayList<Integer> thresholdsDownlinkKbps = _hidl_request.readInt32Vector();
                    ArrayList<Integer> thresholdsUplinkKbps = _hidl_request.readInt32Vector();
                    int accessNetwork2 = _hidl_request.readInt32();
                    setLinkCapacityReportingCriteria(serial138, hysteresisMs2, hysteresisDlKbps, hysteresisUlKbps, thresholdsDownlinkKbps, thresholdsUplinkKbps, accessNetwork2);
                    return;
                case 141:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial139 = _hidl_request.readInt32();
                    int accessNetwork3 = _hidl_request.readInt32();
                    android.hardware.radio.V1_0.DataProfileInfo dataProfileInfo3 = new android.hardware.radio.V1_0.DataProfileInfo();
                    dataProfileInfo3.readFromParcel(_hidl_request);
                    boolean modemCognitive3 = _hidl_request.readBool();
                    boolean roamingAllowed2 = _hidl_request.readBool();
                    boolean isRoaming4 = _hidl_request.readBool();
                    int reason = _hidl_request.readInt32();
                    ArrayList<String> addresses = _hidl_request.readStringVector();
                    ArrayList<String> dnses = _hidl_request.readStringVector();
                    setupDataCall_1_2(serial139, accessNetwork3, dataProfileInfo3, modemCognitive3, roamingAllowed2, isRoaming4, reason, addresses, dnses);
                    return;
                case 142:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadio.kInterfaceName);
                    int serial140 = _hidl_request.readInt32();
                    int cid2 = _hidl_request.readInt32();
                    int reason2 = _hidl_request.readInt32();
                    deactivateDataCall_1_2(serial140, cid2, reason2);
                    return;
                case 143:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadio.kInterfaceName);
                    int serial141 = _hidl_request.readInt32();
                    boolean specifyChannels = _hidl_request.readBool();
                    ArrayList<android.hardware.radio.V1_1.RadioAccessSpecifier> specifiers = android.hardware.radio.V1_1.RadioAccessSpecifier.readVectorFromParcel(_hidl_request);
                    setSystemSelectionChannels(serial141, specifyChannels, specifiers);
                    return;
                case 144:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadio.kInterfaceName);
                    int serial142 = _hidl_request.readInt32();
                    boolean on3 = _hidl_request.readBool();
                    enableModem(serial142, on3);
                    return;
                case 145:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_3.IRadio.kInterfaceName);
                    int serial143 = _hidl_request.readInt32();
                    getModemStackStatus(serial143);
                    return;
                case 146:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial144 = _hidl_request.readInt32();
                    int accessNetwork4 = _hidl_request.readInt32();
                    android.hardware.radio.V1_4.DataProfileInfo dataProfileInfo4 = new android.hardware.radio.V1_4.DataProfileInfo();
                    dataProfileInfo4.readFromParcel(_hidl_request);
                    boolean roamingAllowed3 = _hidl_request.readBool();
                    int reason3 = _hidl_request.readInt32();
                    ArrayList<String> addresses2 = _hidl_request.readStringVector();
                    ArrayList<String> dnses2 = _hidl_request.readStringVector();
                    setupDataCall_1_4(serial144, accessNetwork4, dataProfileInfo4, roamingAllowed3, reason3, addresses2, dnses2);
                    return;
                case 147:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial145 = _hidl_request.readInt32();
                    android.hardware.radio.V1_4.DataProfileInfo dataProfileInfo5 = new android.hardware.radio.V1_4.DataProfileInfo();
                    dataProfileInfo5.readFromParcel(_hidl_request);
                    setInitialAttachApn_1_4(serial145, dataProfileInfo5);
                    return;
                case 148:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial146 = _hidl_request.readInt32();
                    ArrayList<android.hardware.radio.V1_4.DataProfileInfo> profiles2 = android.hardware.radio.V1_4.DataProfileInfo.readVectorFromParcel(_hidl_request);
                    setDataProfile_1_4(serial146, profiles2);
                    return;
                case 149:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial147 = _hidl_request.readInt32();
                    Dial dialInfo2 = new Dial();
                    dialInfo2.readFromParcel(_hidl_request);
                    int categories = _hidl_request.readInt32();
                    ArrayList<String> urns = _hidl_request.readStringVector();
                    int routing = _hidl_request.readInt32();
                    boolean hasKnownUserIntentEmergency = _hidl_request.readBool();
                    boolean isTesting = _hidl_request.readBool();
                    emergencyDial(serial147, dialInfo2, categories, urns, routing, hasKnownUserIntentEmergency, isTesting);
                    return;
                case 150:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial148 = _hidl_request.readInt32();
                    android.hardware.radio.V1_2.NetworkScanRequest request3 = new android.hardware.radio.V1_2.NetworkScanRequest();
                    request3.readFromParcel(_hidl_request);
                    startNetworkScan_1_4(serial148, request3);
                    return;
                case 151:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial149 = _hidl_request.readInt32();
                    getPreferredNetworkTypeBitmap(serial149);
                    return;
                case 152:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial150 = _hidl_request.readInt32();
                    int networkTypeBitmap = _hidl_request.readInt32();
                    setPreferredNetworkTypeBitmap(serial150, networkTypeBitmap);
                    return;
                case 153:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial151 = _hidl_request.readInt32();
                    CarrierRestrictionsWithPriority carriers2 = new CarrierRestrictionsWithPriority();
                    carriers2.readFromParcel(_hidl_request);
                    int multiSimPolicy = _hidl_request.readInt32();
                    setAllowedCarriers_1_4(serial151, carriers2, multiSimPolicy);
                    return;
                case 154:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial152 = _hidl_request.readInt32();
                    getAllowedCarriers_1_4(serial152);
                    return;
                case 155:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_4.IRadio.kInterfaceName);
                    int serial153 = _hidl_request.readInt32();
                    getSignalStrength_1_4(serial153);
                    return;
                case 156:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial154 = _hidl_request.readInt32();
                    SignalThresholdInfo signalThresholdInfo = new SignalThresholdInfo();
                    signalThresholdInfo.readFromParcel(_hidl_request);
                    int accessNetwork5 = _hidl_request.readInt32();
                    setSignalStrengthReportingCriteria_1_5(serial154, signalThresholdInfo, accessNetwork5);
                    return;
                case 157:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial155 = _hidl_request.readInt32();
                    int hysteresisMs3 = _hidl_request.readInt32();
                    int hysteresisDlKbps2 = _hidl_request.readInt32();
                    int hysteresisUlKbps2 = _hidl_request.readInt32();
                    ArrayList<Integer> thresholdsDownlinkKbps2 = _hidl_request.readInt32Vector();
                    ArrayList<Integer> thresholdsUplinkKbps2 = _hidl_request.readInt32Vector();
                    int accessNetwork6 = _hidl_request.readInt32();
                    setLinkCapacityReportingCriteria_1_5(serial155, hysteresisMs3, hysteresisDlKbps2, hysteresisUlKbps2, thresholdsDownlinkKbps2, thresholdsUplinkKbps2, accessNetwork6);
                    return;
                case 158:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial156 = _hidl_request.readInt32();
                    boolean enable6 = _hidl_request.readBool();
                    enableUiccApplications(serial156, enable6);
                    return;
                case 159:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial157 = _hidl_request.readInt32();
                    areUiccApplicationsEnabled(serial157);
                    return;
                case 160:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial158 = _hidl_request.readInt32();
                    boolean specifyChannels2 = _hidl_request.readBool();
                    ArrayList<RadioAccessSpecifier> specifiers2 = RadioAccessSpecifier.readVectorFromParcel(_hidl_request);
                    setSystemSelectionChannels_1_5(serial158, specifyChannels2, specifiers2);
                    return;
                case 161:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial159 = _hidl_request.readInt32();
                    NetworkScanRequest request4 = new NetworkScanRequest();
                    request4.readFromParcel(_hidl_request);
                    startNetworkScan_1_5(serial159, request4);
                    return;
                case 162:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial160 = _hidl_request.readInt32();
                    int accessNetwork7 = _hidl_request.readInt32();
                    DataProfileInfo dataProfileInfo6 = new DataProfileInfo();
                    dataProfileInfo6.readFromParcel(_hidl_request);
                    boolean roamingAllowed4 = _hidl_request.readBool();
                    int reason4 = _hidl_request.readInt32();
                    ArrayList<LinkAddress> addresses3 = LinkAddress.readVectorFromParcel(_hidl_request);
                    ArrayList<String> dnses3 = _hidl_request.readStringVector();
                    setupDataCall_1_5(serial160, accessNetwork7, dataProfileInfo6, roamingAllowed4, reason4, addresses3, dnses3);
                    return;
                case 163:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial161 = _hidl_request.readInt32();
                    DataProfileInfo dataProfileInfo7 = new DataProfileInfo();
                    dataProfileInfo7.readFromParcel(_hidl_request);
                    setInitialAttachApn_1_5(serial161, dataProfileInfo7);
                    return;
                case 164:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial162 = _hidl_request.readInt32();
                    ArrayList<DataProfileInfo> profiles3 = DataProfileInfo.readVectorFromParcel(_hidl_request);
                    setDataProfile_1_5(serial162, profiles3);
                    return;
                case 165:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial163 = _hidl_request.readInt32();
                    boolean powerOn = _hidl_request.readBool();
                    boolean forEmergencyCall = _hidl_request.readBool();
                    boolean preferredForEmergencyCall = _hidl_request.readBool();
                    setRadioPower_1_5(serial163, powerOn, forEmergencyCall, preferredForEmergencyCall);
                    return;
                case 166:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial164 = _hidl_request.readInt32();
                    int indicationFilter3 = _hidl_request.readInt32();
                    setIndicationFilter_1_5(serial164, indicationFilter3);
                    return;
                case 167:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial165 = _hidl_request.readInt32();
                    getBarringInfo(serial165);
                    return;
                case 168:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial166 = _hidl_request.readInt32();
                    getVoiceRegistrationState_1_5(serial166);
                    return;
                case 169:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial167 = _hidl_request.readInt32();
                    getDataRegistrationState_1_5(serial167);
                    return;
                case 170:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial168 = _hidl_request.readInt32();
                    String operatorNumeric2 = _hidl_request.readString();
                    int ran = _hidl_request.readInt32();
                    setNetworkSelectionModeManual_1_5(serial168, operatorNumeric2, ran);
                    return;
                case 171:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial169 = _hidl_request.readInt32();
                    CdmaSmsMessage sms2 = new CdmaSmsMessage();
                    sms2.readFromParcel(_hidl_request);
                    sendCdmaSmsExpectMore(serial169, sms2);
                    return;
                case 172:
                    _hidl_request.enforceInterface(IRadio.kInterfaceName);
                    int serial170 = _hidl_request.readInt32();
                    int persoType = _hidl_request.readInt32();
                    String controlKey = _hidl_request.readString();
                    supplySimDepersonalization(serial170, persoType, controlKey);
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
