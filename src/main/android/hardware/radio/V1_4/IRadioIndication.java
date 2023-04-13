package android.hardware.radio.V1_4;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback;
import android.hardware.radio.V1_0.CdmaCallWaiting;
import android.hardware.radio.V1_0.CdmaInformationRecords;
import android.hardware.radio.V1_0.CdmaSignalInfoRecord;
import android.hardware.radio.V1_0.CdmaSmsMessage;
import android.hardware.radio.V1_0.HardwareConfig;
import android.hardware.radio.V1_0.LceDataInfo;
import android.hardware.radio.V1_0.PcoDataInfo;
import android.hardware.radio.V1_0.SimRefreshResult;
import android.hardware.radio.V1_0.StkCcUnsolSsResult;
import android.hardware.radio.V1_0.SuppSvcNotification;
import android.hardware.radio.V1_1.KeepaliveStatus;
import android.hardware.radio.V1_2.LinkCapacityEstimate;
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
import com.android.net.module.util.NetworkStackConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IRadioIndication extends android.hardware.radio.V1_3.IRadioIndication {
    public static final String kInterfaceName = "android.hardware.radio@1.4::IRadioIndication";

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    void cellInfoList_1_4(int i, ArrayList<CellInfo> arrayList) throws RemoteException;

    void currentEmergencyNumberList(int i, ArrayList<EmergencyNumber> arrayList) throws RemoteException;

    void currentPhysicalChannelConfigs_1_4(int i, ArrayList<PhysicalChannelConfig> arrayList) throws RemoteException;

    void currentSignalStrength_1_4(int i, SignalStrength signalStrength) throws RemoteException;

    void dataCallListChanged_1_4(int i, ArrayList<SetupDataCallResult> arrayList) throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void networkScanResult_1_4(int i, NetworkScanResult networkScanResult) throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IRadioIndication asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IRadioIndication)) {
            return (IRadioIndication) iface;
        }
        IRadioIndication proxy = new Proxy(binder);
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

    static IRadioIndication castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IRadioIndication getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IRadioIndication getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IRadioIndication getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IRadioIndication getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IRadioIndication {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.radio@1.4::IRadioIndication]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void radioStateChanged(int type, int radioState) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(radioState);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void callStateChanged(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void networkStateChanged(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void newSms(int type, ArrayList<Byte> pdu) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt8Vector(pdu);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void newSmsStatusReport(int type, ArrayList<Byte> pdu) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt8Vector(pdu);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void newSmsOnSim(int type, int recordNumber) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(recordNumber);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void onUssd(int type, int modeType, String msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(modeType);
            _hidl_request.writeString(msg);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void nitzTimeReceived(int type, String nitzTime, long receivedTime) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(nitzTime);
            _hidl_request.writeInt64(receivedTime);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void currentSignalStrength(int type, android.hardware.radio.V1_0.SignalStrength signalStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            signalStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void dataCallListChanged(int type, ArrayList<android.hardware.radio.V1_0.SetupDataCallResult> dcList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            android.hardware.radio.V1_0.SetupDataCallResult.writeVectorToParcel(_hidl_request, dcList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void suppSvcNotify(int type, SuppSvcNotification suppSvc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            suppSvc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void stkSessionEnd(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void stkProactiveCommand(int type, String cmd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(cmd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void stkEventNotify(int type, String cmd) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(cmd);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void stkCallSetup(int type, long timeout) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt64(timeout);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void simSmsStorageFull(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void simRefresh(int type, SimRefreshResult refreshResult) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            refreshResult.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void callRing(int type, boolean isGsm, CdmaSignalInfoRecord record) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeBool(isGsm);
            record.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void simStatusChanged(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaNewSms(int type, CdmaSmsMessage msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            msg.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void newBroadcastSms(int type, ArrayList<Byte> data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt8Vector(data);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaRuimSmsStorageFull(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void restrictedStateChanged(int type, int state) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(state);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void enterEmergencyCallbackMode(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaCallWaiting(int type, CdmaCallWaiting callWaitingRecord) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            callWaitingRecord.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaOtaProvisionStatus(int type, int status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaInfoRec(int type, CdmaInformationRecords records) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            records.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void indicateRingbackTone(int type, boolean start) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeBool(start);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void resendIncallMute(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaSubscriptionSourceChanged(int type, int cdmaSource) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(cdmaSource);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cdmaPrlChanged(int type, int version) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(version);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void exitEmergencyCallbackMode(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void rilConnected(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void voiceRadioTechChanged(int type, int rat) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(rat);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void cellInfoList(int type, ArrayList<android.hardware.radio.V1_0.CellInfo> records) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            android.hardware.radio.V1_0.CellInfo.writeVectorToParcel(_hidl_request, records);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void imsNetworkStateChanged(int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void subscriptionStatusChanged(int type, boolean activate) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeBool(activate);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void srvccStateNotify(int type, int state) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeInt32(state);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void hardwareConfigChanged(int type, ArrayList<HardwareConfig> configs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            HardwareConfig.writeVectorToParcel(_hidl_request, configs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(39, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void radioCapabilityIndication(int type, android.hardware.radio.V1_0.RadioCapability rc) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            rc.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(40, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void onSupplementaryServiceIndication(int type, StkCcUnsolSsResult ss) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            ss.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(41, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void stkCallControlAlphaNotify(int type, String alpha) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(alpha);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(42, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void lceData(int type, LceDataInfo lce) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            lce.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(43, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void pcoData(int type, PcoDataInfo pco) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            pco.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(44, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_0.IRadioIndication
        public void modemReset(int type, String reason) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            _hidl_request.writeString(reason);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(45, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioIndication
        public void carrierInfoForImsiEncryption(int info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(info);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(46, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioIndication
        public void networkScanResult(int type, android.hardware.radio.V1_1.NetworkScanResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(47, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_1.IRadioIndication
        public void keepaliveStatus(int type, KeepaliveStatus status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            status.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(48, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioIndication
        public void networkScanResult_1_2(int type, android.hardware.radio.V1_2.NetworkScanResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(49, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioIndication
        public void cellInfoList_1_2(int type, ArrayList<android.hardware.radio.V1_2.CellInfo> records) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            android.hardware.radio.V1_2.CellInfo.writeVectorToParcel(_hidl_request, records);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(50, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioIndication
        public void currentLinkCapacityEstimate(int type, LinkCapacityEstimate lce) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            lce.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(51, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioIndication
        public void currentPhysicalChannelConfigs(int type, ArrayList<android.hardware.radio.V1_2.PhysicalChannelConfig> configs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            android.hardware.radio.V1_2.PhysicalChannelConfig.writeVectorToParcel(_hidl_request, configs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(52, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_2.IRadioIndication
        public void currentSignalStrength_1_2(int type, android.hardware.radio.V1_2.SignalStrength signalStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            signalStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(53, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void currentEmergencyNumberList(int type, ArrayList<EmergencyNumber> emergencyNumberList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            EmergencyNumber.writeVectorToParcel(_hidl_request, emergencyNumberList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(54, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void cellInfoList_1_4(int type, ArrayList<CellInfo> records) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            CellInfo.writeVectorToParcel(_hidl_request, records);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(55, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void networkScanResult_1_4(int type, NetworkScanResult result) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            result.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(56, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void currentPhysicalChannelConfigs_1_4(int type, ArrayList<PhysicalChannelConfig> configs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            PhysicalChannelConfig.writeVectorToParcel(_hidl_request, configs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(57, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void dataCallListChanged_1_4(int type, ArrayList<SetupDataCallResult> dcList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            SetupDataCallResult.writeVectorToParcel(_hidl_request, dcList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(58, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication
        public void currentSignalStrength_1_4(int type, SignalStrength signalStrength) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IRadioIndication.kInterfaceName);
            _hidl_request.writeInt32(type);
            signalStrength.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(59, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IRadioIndication {
        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IRadioIndication.kInterfaceName, android.hardware.radio.V1_3.IRadioIndication.kInterfaceName, android.hardware.radio.V1_2.IRadioIndication.kInterfaceName, android.hardware.radio.V1_1.IRadioIndication.kInterfaceName, android.hardware.radio.V1_0.IRadioIndication.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IRadioIndication.kInterfaceName;
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{51, -39, -26, -119, 92, -54, -104, -86, 86, 41, 107, MidiConstants.STATUS_CONTROL_CHANGE, 23, NetworkStackConstants.TCPHDR_URG, -47, -117, -118, -51, AcquiredInfo.POWER_PRESS, 77, -28, -106, 11, -21, 113, 46, 99, -83, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, 116, 56, -91}, new byte[]{-23, MidiConstants.STATUS_CHANNEL_PRESSURE, MidiConstants.STATUS_MIDI_TIME_CODE, 26, 82, 113, 95, 90, 41, -40, -98, 45, -114, 46, 33, -37, 30, 22, -92, 49, 116, -81, 107, -99, 81, -90, 45, 112, 92, -38, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, 85}, new byte[]{-51, -89, 82, -82, -85, -86, -68, NetworkStackConstants.TCPHDR_URG, 72, 106, -126, -84, 87, -93, -35, 16, 119, -123, MidiConstants.STATUS_PROGRAM_CHANGE, 6, 9, 74, 52, -101, -59, -30, 36, -24, -86, 34, -95, 124}, new byte[]{-4, -59, -56, -56, -117, -123, -87, -10, 63, -70, 103, -39, -26, 116, -38, 70, 108, 114, -87, -116, -94, -121, MidiConstants.STATUS_SONG_SELECT, 67, -5, 87, 33, MidiConstants.STATUS_CHANNEL_PRESSURE, -104, 113, 63, -122}, new byte[]{-119, -41, -113, -92, -101, 9, -30, MidiConstants.STATUS_SONG_SELECT, 24, 18, -69, 99, -31, -65, -84, 43, MidiConstants.STATUS_SONG_SELECT, 24, -87, 86, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, 115, -58, MidiConstants.STATUS_CONTROL_CHANGE, -19, 105, 4, -50, 24, 55, 125, 84}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.radio.V1_4.IRadioIndication, android.hardware.radio.V1_3.IRadioIndication, android.hardware.radio.V1_2.IRadioIndication, android.hardware.radio.V1_1.IRadioIndication, android.hardware.radio.V1_0.IRadioIndication, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IRadioIndication.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type = _hidl_request.readInt32();
                    int radioState = _hidl_request.readInt32();
                    radioStateChanged(type, radioState);
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type2 = _hidl_request.readInt32();
                    callStateChanged(type2);
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type3 = _hidl_request.readInt32();
                    networkStateChanged(type3);
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type4 = _hidl_request.readInt32();
                    ArrayList<Byte> pdu = _hidl_request.readInt8Vector();
                    newSms(type4, pdu);
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type5 = _hidl_request.readInt32();
                    ArrayList<Byte> pdu2 = _hidl_request.readInt8Vector();
                    newSmsStatusReport(type5, pdu2);
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type6 = _hidl_request.readInt32();
                    int recordNumber = _hidl_request.readInt32();
                    newSmsOnSim(type6, recordNumber);
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type7 = _hidl_request.readInt32();
                    int modeType = _hidl_request.readInt32();
                    onUssd(type7, modeType, _hidl_request.readString());
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type8 = _hidl_request.readInt32();
                    String nitzTime = _hidl_request.readString();
                    long receivedTime = _hidl_request.readInt64();
                    nitzTimeReceived(type8, nitzTime, receivedTime);
                    return;
                case 9:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type9 = _hidl_request.readInt32();
                    android.hardware.radio.V1_0.SignalStrength signalStrength = new android.hardware.radio.V1_0.SignalStrength();
                    signalStrength.readFromParcel(_hidl_request);
                    currentSignalStrength(type9, signalStrength);
                    return;
                case 10:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type10 = _hidl_request.readInt32();
                    ArrayList<android.hardware.radio.V1_0.SetupDataCallResult> dcList = android.hardware.radio.V1_0.SetupDataCallResult.readVectorFromParcel(_hidl_request);
                    dataCallListChanged(type10, dcList);
                    return;
                case 11:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type11 = _hidl_request.readInt32();
                    SuppSvcNotification suppSvc = new SuppSvcNotification();
                    suppSvc.readFromParcel(_hidl_request);
                    suppSvcNotify(type11, suppSvc);
                    return;
                case 12:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type12 = _hidl_request.readInt32();
                    stkSessionEnd(type12);
                    return;
                case 13:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type13 = _hidl_request.readInt32();
                    String cmd = _hidl_request.readString();
                    stkProactiveCommand(type13, cmd);
                    return;
                case 14:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type14 = _hidl_request.readInt32();
                    String cmd2 = _hidl_request.readString();
                    stkEventNotify(type14, cmd2);
                    return;
                case 15:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type15 = _hidl_request.readInt32();
                    long timeout = _hidl_request.readInt64();
                    stkCallSetup(type15, timeout);
                    return;
                case 16:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type16 = _hidl_request.readInt32();
                    simSmsStorageFull(type16);
                    return;
                case 17:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type17 = _hidl_request.readInt32();
                    SimRefreshResult refreshResult = new SimRefreshResult();
                    refreshResult.readFromParcel(_hidl_request);
                    simRefresh(type17, refreshResult);
                    return;
                case 18:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type18 = _hidl_request.readInt32();
                    boolean isGsm = _hidl_request.readBool();
                    CdmaSignalInfoRecord record = new CdmaSignalInfoRecord();
                    record.readFromParcel(_hidl_request);
                    callRing(type18, isGsm, record);
                    return;
                case 19:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type19 = _hidl_request.readInt32();
                    simStatusChanged(type19);
                    return;
                case 20:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type20 = _hidl_request.readInt32();
                    CdmaSmsMessage msg = new CdmaSmsMessage();
                    msg.readFromParcel(_hidl_request);
                    cdmaNewSms(type20, msg);
                    return;
                case 21:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type21 = _hidl_request.readInt32();
                    ArrayList<Byte> data = _hidl_request.readInt8Vector();
                    newBroadcastSms(type21, data);
                    return;
                case 22:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type22 = _hidl_request.readInt32();
                    cdmaRuimSmsStorageFull(type22);
                    return;
                case 23:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type23 = _hidl_request.readInt32();
                    int state = _hidl_request.readInt32();
                    restrictedStateChanged(type23, state);
                    return;
                case 24:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type24 = _hidl_request.readInt32();
                    enterEmergencyCallbackMode(type24);
                    return;
                case 25:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type25 = _hidl_request.readInt32();
                    CdmaCallWaiting callWaitingRecord = new CdmaCallWaiting();
                    callWaitingRecord.readFromParcel(_hidl_request);
                    cdmaCallWaiting(type25, callWaitingRecord);
                    return;
                case 26:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type26 = _hidl_request.readInt32();
                    cdmaOtaProvisionStatus(type26, _hidl_request.readInt32());
                    return;
                case 27:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type27 = _hidl_request.readInt32();
                    CdmaInformationRecords records = new CdmaInformationRecords();
                    records.readFromParcel(_hidl_request);
                    cdmaInfoRec(type27, records);
                    return;
                case 28:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type28 = _hidl_request.readInt32();
                    boolean start = _hidl_request.readBool();
                    indicateRingbackTone(type28, start);
                    return;
                case 29:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type29 = _hidl_request.readInt32();
                    resendIncallMute(type29);
                    return;
                case 30:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type30 = _hidl_request.readInt32();
                    int cdmaSource = _hidl_request.readInt32();
                    cdmaSubscriptionSourceChanged(type30, cdmaSource);
                    return;
                case 31:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type31 = _hidl_request.readInt32();
                    int version = _hidl_request.readInt32();
                    cdmaPrlChanged(type31, version);
                    return;
                case 32:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type32 = _hidl_request.readInt32();
                    exitEmergencyCallbackMode(type32);
                    return;
                case 33:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type33 = _hidl_request.readInt32();
                    rilConnected(type33);
                    return;
                case 34:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type34 = _hidl_request.readInt32();
                    int rat = _hidl_request.readInt32();
                    voiceRadioTechChanged(type34, rat);
                    return;
                case 35:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type35 = _hidl_request.readInt32();
                    cellInfoList(type35, android.hardware.radio.V1_0.CellInfo.readVectorFromParcel(_hidl_request));
                    return;
                case 36:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type36 = _hidl_request.readInt32();
                    imsNetworkStateChanged(type36);
                    return;
                case 37:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type37 = _hidl_request.readInt32();
                    boolean activate = _hidl_request.readBool();
                    subscriptionStatusChanged(type37, activate);
                    return;
                case 38:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type38 = _hidl_request.readInt32();
                    int state2 = _hidl_request.readInt32();
                    srvccStateNotify(type38, state2);
                    return;
                case 39:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type39 = _hidl_request.readInt32();
                    ArrayList<HardwareConfig> configs = HardwareConfig.readVectorFromParcel(_hidl_request);
                    hardwareConfigChanged(type39, configs);
                    return;
                case 40:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type40 = _hidl_request.readInt32();
                    android.hardware.radio.V1_0.RadioCapability rc = new android.hardware.radio.V1_0.RadioCapability();
                    rc.readFromParcel(_hidl_request);
                    radioCapabilityIndication(type40, rc);
                    return;
                case 41:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type41 = _hidl_request.readInt32();
                    StkCcUnsolSsResult ss = new StkCcUnsolSsResult();
                    ss.readFromParcel(_hidl_request);
                    onSupplementaryServiceIndication(type41, ss);
                    return;
                case 42:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type42 = _hidl_request.readInt32();
                    String alpha = _hidl_request.readString();
                    stkCallControlAlphaNotify(type42, alpha);
                    return;
                case 43:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type43 = _hidl_request.readInt32();
                    LceDataInfo lce = new LceDataInfo();
                    lce.readFromParcel(_hidl_request);
                    lceData(type43, lce);
                    return;
                case 44:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type44 = _hidl_request.readInt32();
                    PcoDataInfo pco = new PcoDataInfo();
                    pco.readFromParcel(_hidl_request);
                    pcoData(type44, pco);
                    return;
                case 45:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_0.IRadioIndication.kInterfaceName);
                    int type45 = _hidl_request.readInt32();
                    String reason = _hidl_request.readString();
                    modemReset(type45, reason);
                    return;
                case 46:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
                    int info = _hidl_request.readInt32();
                    carrierInfoForImsiEncryption(info);
                    return;
                case 47:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
                    int type46 = _hidl_request.readInt32();
                    android.hardware.radio.V1_1.NetworkScanResult result = new android.hardware.radio.V1_1.NetworkScanResult();
                    result.readFromParcel(_hidl_request);
                    networkScanResult(type46, result);
                    return;
                case 48:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_1.IRadioIndication.kInterfaceName);
                    int type47 = _hidl_request.readInt32();
                    KeepaliveStatus status = new KeepaliveStatus();
                    status.readFromParcel(_hidl_request);
                    keepaliveStatus(type47, status);
                    return;
                case 49:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
                    int type48 = _hidl_request.readInt32();
                    android.hardware.radio.V1_2.NetworkScanResult result2 = new android.hardware.radio.V1_2.NetworkScanResult();
                    result2.readFromParcel(_hidl_request);
                    networkScanResult_1_2(type48, result2);
                    return;
                case 50:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
                    int type49 = _hidl_request.readInt32();
                    cellInfoList_1_2(type49, android.hardware.radio.V1_2.CellInfo.readVectorFromParcel(_hidl_request));
                    return;
                case 51:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
                    int type50 = _hidl_request.readInt32();
                    LinkCapacityEstimate lce2 = new LinkCapacityEstimate();
                    lce2.readFromParcel(_hidl_request);
                    currentLinkCapacityEstimate(type50, lce2);
                    return;
                case 52:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
                    int type51 = _hidl_request.readInt32();
                    ArrayList<android.hardware.radio.V1_2.PhysicalChannelConfig> configs2 = android.hardware.radio.V1_2.PhysicalChannelConfig.readVectorFromParcel(_hidl_request);
                    currentPhysicalChannelConfigs(type51, configs2);
                    return;
                case 53:
                    _hidl_request.enforceInterface(android.hardware.radio.V1_2.IRadioIndication.kInterfaceName);
                    int type52 = _hidl_request.readInt32();
                    android.hardware.radio.V1_2.SignalStrength signalStrength2 = new android.hardware.radio.V1_2.SignalStrength();
                    signalStrength2.readFromParcel(_hidl_request);
                    currentSignalStrength_1_2(type52, signalStrength2);
                    return;
                case 54:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type53 = _hidl_request.readInt32();
                    ArrayList<EmergencyNumber> emergencyNumberList = EmergencyNumber.readVectorFromParcel(_hidl_request);
                    currentEmergencyNumberList(type53, emergencyNumberList);
                    return;
                case 55:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type54 = _hidl_request.readInt32();
                    cellInfoList_1_4(type54, CellInfo.readVectorFromParcel(_hidl_request));
                    return;
                case 56:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type55 = _hidl_request.readInt32();
                    NetworkScanResult result3 = new NetworkScanResult();
                    result3.readFromParcel(_hidl_request);
                    networkScanResult_1_4(type55, result3);
                    return;
                case 57:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type56 = _hidl_request.readInt32();
                    ArrayList<PhysicalChannelConfig> configs3 = PhysicalChannelConfig.readVectorFromParcel(_hidl_request);
                    currentPhysicalChannelConfigs_1_4(type56, configs3);
                    return;
                case 58:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type57 = _hidl_request.readInt32();
                    ArrayList<SetupDataCallResult> dcList2 = SetupDataCallResult.readVectorFromParcel(_hidl_request);
                    dataCallListChanged_1_4(type57, dcList2);
                    return;
                case 59:
                    _hidl_request.enforceInterface(IRadioIndication.kInterfaceName);
                    int type58 = _hidl_request.readInt32();
                    SignalStrength signalStrength3 = new SignalStrength();
                    signalStrength3.readFromParcel(_hidl_request);
                    currentSignalStrength_1_4(type58, signalStrength3);
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
