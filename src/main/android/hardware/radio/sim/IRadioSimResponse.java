package android.hardware.radio.sim;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSimResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$sim$IRadioSimResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeRequest(int i) throws RemoteException;

    void areUiccApplicationsEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void changeIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void changeIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void enableUiccApplicationsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getAllowedCarriersResponse(RadioResponseInfo radioResponseInfo, CarrierRestrictions carrierRestrictions, int i) throws RemoteException;

    void getCdmaSubscriptionResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void getCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getIccCardStatusResponse(RadioResponseInfo radioResponseInfo, CardStatus cardStatus) throws RemoteException;

    void getImsiForAppResponse(RadioResponseInfo radioResponseInfo, String str) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSimPhonebookCapacityResponse(RadioResponseInfo radioResponseInfo, PhonebookCapacity phonebookCapacity) throws RemoteException;

    void getSimPhonebookRecordsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    @Deprecated
    void iccCloseLogicalChannelResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void iccCloseLogicalChannelWithSessionInfoResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void iccIoForAppResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) throws RemoteException;

    void iccOpenLogicalChannelResponse(RadioResponseInfo radioResponseInfo, int i, byte[] bArr) throws RemoteException;

    void iccTransmitApduBasicChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) throws RemoteException;

    void iccTransmitApduLogicalChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) throws RemoteException;

    void reportStkServiceIsRunningResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void requestIccSimAuthenticationResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) throws RemoteException;

    void sendEnvelopeResponse(RadioResponseInfo radioResponseInfo, String str) throws RemoteException;

    void sendEnvelopeWithStatusResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) throws RemoteException;

    void sendTerminalResponseToSimResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setAllowedCarriersResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void setSimCardPowerResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setUiccSubscriptionResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void supplyIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void supplyIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void supplyIccPuk2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void supplyIccPukForAppResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void supplySimDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i, int i2) throws RemoteException;

    void updateSimPhonebookRecordsResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSimResponse {
        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void areUiccApplicationsEnabledResponse(RadioResponseInfo info, boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void changeIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void changeIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void enableUiccApplicationsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getAllowedCarriersResponse(RadioResponseInfo info, CarrierRestrictions carriers, int multiSimPolicy) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getCdmaSubscriptionResponse(RadioResponseInfo info, String mdn, String hSid, String hNid, String min, String prl) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getCdmaSubscriptionSourceResponse(RadioResponseInfo info, int source) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getFacilityLockForAppResponse(RadioResponseInfo info, int response) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getIccCardStatusResponse(RadioResponseInfo info, CardStatus cardStatus) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getImsiForAppResponse(RadioResponseInfo info, String imsi) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getSimPhonebookCapacityResponse(RadioResponseInfo info, PhonebookCapacity capacity) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void getSimPhonebookRecordsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccCloseLogicalChannelResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccIoForAppResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccOpenLogicalChannelResponse(RadioResponseInfo info, int channelId, byte[] selectResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccTransmitApduBasicChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccTransmitApduLogicalChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void reportStkServiceIsRunningResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void requestIccSimAuthenticationResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void sendEnvelopeResponse(RadioResponseInfo info, String commandResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void sendEnvelopeWithStatusResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void sendTerminalResponseToSimResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setAllowedCarriersResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setCdmaSubscriptionSourceResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setFacilityLockForAppResponse(RadioResponseInfo info, int retry) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setSimCardPowerResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void setUiccSubscriptionResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void supplyIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void supplyIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void supplyIccPuk2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void supplyIccPukForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void supplySimDepersonalizationResponse(RadioResponseInfo info, int persoType, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void updateSimPhonebookRecordsResponse(RadioResponseInfo info, int updatedRecordIndex) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public void iccCloseLogicalChannelWithSessionInfoResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.sim.IRadioSimResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSimResponse {
        static final int TRANSACTION_acknowledgeRequest = 1;
        static final int TRANSACTION_areUiccApplicationsEnabledResponse = 2;
        static final int TRANSACTION_changeIccPin2ForAppResponse = 3;
        static final int TRANSACTION_changeIccPinForAppResponse = 4;
        static final int TRANSACTION_enableUiccApplicationsResponse = 5;
        static final int TRANSACTION_getAllowedCarriersResponse = 6;
        static final int TRANSACTION_getCdmaSubscriptionResponse = 7;
        static final int TRANSACTION_getCdmaSubscriptionSourceResponse = 8;
        static final int TRANSACTION_getFacilityLockForAppResponse = 9;
        static final int TRANSACTION_getIccCardStatusResponse = 10;
        static final int TRANSACTION_getImsiForAppResponse = 11;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSimPhonebookCapacityResponse = 12;
        static final int TRANSACTION_getSimPhonebookRecordsResponse = 13;
        static final int TRANSACTION_iccCloseLogicalChannelResponse = 14;
        static final int TRANSACTION_iccCloseLogicalChannelWithSessionInfoResponse = 36;
        static final int TRANSACTION_iccIoForAppResponse = 15;
        static final int TRANSACTION_iccOpenLogicalChannelResponse = 16;
        static final int TRANSACTION_iccTransmitApduBasicChannelResponse = 17;
        static final int TRANSACTION_iccTransmitApduLogicalChannelResponse = 18;
        static final int TRANSACTION_reportStkServiceIsRunningResponse = 19;
        static final int TRANSACTION_requestIccSimAuthenticationResponse = 20;
        static final int TRANSACTION_sendEnvelopeResponse = 21;
        static final int TRANSACTION_sendEnvelopeWithStatusResponse = 22;
        static final int TRANSACTION_sendTerminalResponseToSimResponse = 23;
        static final int TRANSACTION_setAllowedCarriersResponse = 24;
        static final int TRANSACTION_setCarrierInfoForImsiEncryptionResponse = 25;
        static final int TRANSACTION_setCdmaSubscriptionSourceResponse = 26;
        static final int TRANSACTION_setFacilityLockForAppResponse = 27;
        static final int TRANSACTION_setSimCardPowerResponse = 28;
        static final int TRANSACTION_setUiccSubscriptionResponse = 29;
        static final int TRANSACTION_supplyIccPin2ForAppResponse = 30;
        static final int TRANSACTION_supplyIccPinForAppResponse = 31;
        static final int TRANSACTION_supplyIccPuk2ForAppResponse = 32;
        static final int TRANSACTION_supplyIccPukForAppResponse = 33;
        static final int TRANSACTION_supplySimDepersonalizationResponse = 34;
        static final int TRANSACTION_updateSimPhonebookRecordsResponse = 35;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSimResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSimResponse)) {
                return (IRadioSimResponse) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeRequest(_arg0);
                            break;
                        case 2:
                            RadioResponseInfo _arg02 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            areUiccApplicationsEnabledResponse(_arg02, _arg1);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            changeIccPin2ForAppResponse(_arg03, _arg12);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            changeIccPinForAppResponse(_arg04, _arg13);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            enableUiccApplicationsResponse(_arg05);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CarrierRestrictions _arg14 = (CarrierRestrictions) data.readTypedObject(CarrierRestrictions.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            getAllowedCarriersResponse(_arg06, _arg14, _arg2);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg15 = data.readString();
                            String _arg22 = data.readString();
                            String _arg3 = data.readString();
                            String _arg4 = data.readString();
                            String _arg5 = data.readString();
                            data.enforceNoDataAvail();
                            getCdmaSubscriptionResponse(_arg07, _arg15, _arg22, _arg3, _arg4, _arg5);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaSubscriptionSourceResponse(_arg08, _arg16);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            getFacilityLockForAppResponse(_arg09, _arg17);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CardStatus _arg18 = (CardStatus) data.readTypedObject(CardStatus.CREATOR);
                            data.enforceNoDataAvail();
                            getIccCardStatusResponse(_arg010, _arg18);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg19 = data.readString();
                            data.enforceNoDataAvail();
                            getImsiForAppResponse(_arg011, _arg19);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            PhonebookCapacity _arg110 = (PhonebookCapacity) data.readTypedObject(PhonebookCapacity.CREATOR);
                            data.enforceNoDataAvail();
                            getSimPhonebookCapacityResponse(_arg012, _arg110);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getSimPhonebookRecordsResponse(_arg013);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            iccCloseLogicalChannelResponse(_arg014);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            IccIoResult _arg111 = (IccIoResult) data.readTypedObject(IccIoResult.CREATOR);
                            data.enforceNoDataAvail();
                            iccIoForAppResponse(_arg015, _arg111);
                            break;
                        case 16:
                            RadioResponseInfo _arg016 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg112 = data.readInt();
                            byte[] _arg23 = data.createByteArray();
                            data.enforceNoDataAvail();
                            iccOpenLogicalChannelResponse(_arg016, _arg112, _arg23);
                            break;
                        case 17:
                            RadioResponseInfo _arg017 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            IccIoResult _arg113 = (IccIoResult) data.readTypedObject(IccIoResult.CREATOR);
                            data.enforceNoDataAvail();
                            iccTransmitApduBasicChannelResponse(_arg017, _arg113);
                            break;
                        case 18:
                            RadioResponseInfo _arg018 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            IccIoResult _arg114 = (IccIoResult) data.readTypedObject(IccIoResult.CREATOR);
                            data.enforceNoDataAvail();
                            iccTransmitApduLogicalChannelResponse(_arg018, _arg114);
                            break;
                        case 19:
                            RadioResponseInfo _arg019 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            reportStkServiceIsRunningResponse(_arg019);
                            break;
                        case 20:
                            RadioResponseInfo _arg020 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            IccIoResult _arg115 = (IccIoResult) data.readTypedObject(IccIoResult.CREATOR);
                            data.enforceNoDataAvail();
                            requestIccSimAuthenticationResponse(_arg020, _arg115);
                            break;
                        case 21:
                            RadioResponseInfo _arg021 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg116 = data.readString();
                            data.enforceNoDataAvail();
                            sendEnvelopeResponse(_arg021, _arg116);
                            break;
                        case 22:
                            RadioResponseInfo _arg022 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            IccIoResult _arg117 = (IccIoResult) data.readTypedObject(IccIoResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendEnvelopeWithStatusResponse(_arg022, _arg117);
                            break;
                        case 23:
                            RadioResponseInfo _arg023 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendTerminalResponseToSimResponse(_arg023);
                            break;
                        case 24:
                            RadioResponseInfo _arg024 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setAllowedCarriersResponse(_arg024);
                            break;
                        case 25:
                            RadioResponseInfo _arg025 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCarrierInfoForImsiEncryptionResponse(_arg025);
                            break;
                        case 26:
                            RadioResponseInfo _arg026 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCdmaSubscriptionSourceResponse(_arg026);
                            break;
                        case 27:
                            RadioResponseInfo _arg027 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            setFacilityLockForAppResponse(_arg027, _arg118);
                            break;
                        case 28:
                            RadioResponseInfo _arg028 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSimCardPowerResponse(_arg028);
                            break;
                        case 29:
                            RadioResponseInfo _arg029 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setUiccSubscriptionResponse(_arg029);
                            break;
                        case 30:
                            RadioResponseInfo _arg030 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            supplyIccPin2ForAppResponse(_arg030, _arg119);
                            break;
                        case 31:
                            RadioResponseInfo _arg031 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            supplyIccPinForAppResponse(_arg031, _arg120);
                            break;
                        case 32:
                            RadioResponseInfo _arg032 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            supplyIccPuk2ForAppResponse(_arg032, _arg121);
                            break;
                        case 33:
                            RadioResponseInfo _arg033 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            supplyIccPukForAppResponse(_arg033, _arg122);
                            break;
                        case 34:
                            RadioResponseInfo _arg034 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg123 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            supplySimDepersonalizationResponse(_arg034, _arg123, _arg24);
                            break;
                        case 35:
                            RadioResponseInfo _arg035 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            updateSimPhonebookRecordsResponse(_arg035, _arg124);
                            break;
                        case 36:
                            RadioResponseInfo _arg036 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            iccCloseLogicalChannelWithSessionInfoResponse(_arg036);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSimResponse {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void acknowledgeRequest(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeRequest is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void areUiccApplicationsEnabledResponse(RadioResponseInfo info, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(enabled);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method areUiccApplicationsEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void changeIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method changeIccPin2ForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void changeIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method changeIccPinForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void enableUiccApplicationsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method enableUiccApplicationsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getAllowedCarriersResponse(RadioResponseInfo info, CarrierRestrictions carriers, int multiSimPolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(carriers, 0);
                    _data.writeInt(multiSimPolicy);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAllowedCarriersResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getCdmaSubscriptionResponse(RadioResponseInfo info, String mdn, String hSid, String hNid, String min, String prl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(mdn);
                    _data.writeString(hSid);
                    _data.writeString(hNid);
                    _data.writeString(min);
                    _data.writeString(prl);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaSubscriptionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getCdmaSubscriptionSourceResponse(RadioResponseInfo info, int source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(source);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaSubscriptionSourceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getFacilityLockForAppResponse(RadioResponseInfo info, int response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(response);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getFacilityLockForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getIccCardStatusResponse(RadioResponseInfo info, CardStatus cardStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(cardStatus, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getIccCardStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getImsiForAppResponse(RadioResponseInfo info, String imsi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(imsi);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImsiForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getSimPhonebookCapacityResponse(RadioResponseInfo info, PhonebookCapacity capacity) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(capacity, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimPhonebookCapacityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void getSimPhonebookRecordsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimPhonebookRecordsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccCloseLogicalChannelResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccCloseLogicalChannelResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccIoForAppResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(iccIo, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccIoForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccOpenLogicalChannelResponse(RadioResponseInfo info, int channelId, byte[] selectResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(channelId);
                    _data.writeByteArray(selectResponse);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccOpenLogicalChannelResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccTransmitApduBasicChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(result, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccTransmitApduBasicChannelResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccTransmitApduLogicalChannelResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(result, 0);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccTransmitApduLogicalChannelResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void reportStkServiceIsRunningResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method reportStkServiceIsRunningResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void requestIccSimAuthenticationResponse(RadioResponseInfo info, IccIoResult result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(result, 0);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method requestIccSimAuthenticationResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void sendEnvelopeResponse(RadioResponseInfo info, String commandResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(commandResponse);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendEnvelopeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void sendEnvelopeWithStatusResponse(RadioResponseInfo info, IccIoResult iccIo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(iccIo, 0);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendEnvelopeWithStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void sendTerminalResponseToSimResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendTerminalResponseToSimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setAllowedCarriersResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setAllowedCarriersResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCarrierInfoForImsiEncryptionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setCdmaSubscriptionSourceResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaSubscriptionSourceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setFacilityLockForAppResponse(RadioResponseInfo info, int retry) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(retry);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setFacilityLockForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setSimCardPowerResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSimCardPowerResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void setUiccSubscriptionResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setUiccSubscriptionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void supplyIccPin2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPin2ForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void supplyIccPinForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPinForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void supplyIccPuk2ForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPuk2ForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void supplyIccPukForAppResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPukForAppResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void supplySimDepersonalizationResponse(RadioResponseInfo info, int persoType, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(persoType);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplySimDepersonalizationResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void updateSimPhonebookRecordsResponse(RadioResponseInfo info, int updatedRecordIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(updatedRecordIndex);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method updateSimPhonebookRecordsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public void iccCloseLogicalChannelWithSessionInfoResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccCloseLogicalChannelWithSessionInfoResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.radio.sim.IRadioSimResponse
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
