package android.hardware.radio.sim;

import android.hardware.radio.sim.IRadioSimIndication;
import android.hardware.radio.sim.IRadioSimResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSim extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$sim$IRadioSim".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void areUiccApplicationsEnabled(int i) throws RemoteException;

    void changeIccPin2ForApp(int i, String str, String str2, String str3) throws RemoteException;

    void changeIccPinForApp(int i, String str, String str2, String str3) throws RemoteException;

    void enableUiccApplications(int i, boolean z) throws RemoteException;

    void getAllowedCarriers(int i) throws RemoteException;

    void getCdmaSubscription(int i) throws RemoteException;

    void getCdmaSubscriptionSource(int i) throws RemoteException;

    void getFacilityLockForApp(int i, String str, String str2, int i2, String str3) throws RemoteException;

    void getIccCardStatus(int i) throws RemoteException;

    void getImsiForApp(int i, String str) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSimPhonebookCapacity(int i) throws RemoteException;

    void getSimPhonebookRecords(int i) throws RemoteException;

    @Deprecated
    void iccCloseLogicalChannel(int i, int i2) throws RemoteException;

    void iccCloseLogicalChannelWithSessionInfo(int i, SessionInfo sessionInfo) throws RemoteException;

    void iccIoForApp(int i, IccIo iccIo) throws RemoteException;

    void iccOpenLogicalChannel(int i, String str, int i2) throws RemoteException;

    void iccTransmitApduBasicChannel(int i, SimApdu simApdu) throws RemoteException;

    void iccTransmitApduLogicalChannel(int i, SimApdu simApdu) throws RemoteException;

    void reportStkServiceIsRunning(int i) throws RemoteException;

    void requestIccSimAuthentication(int i, int i2, String str, String str2) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void sendEnvelope(int i, String str) throws RemoteException;

    void sendEnvelopeWithStatus(int i, String str) throws RemoteException;

    void sendTerminalResponseToSim(int i, String str) throws RemoteException;

    void setAllowedCarriers(int i, CarrierRestrictions carrierRestrictions, int i2) throws RemoteException;

    void setCarrierInfoForImsiEncryption(int i, ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException;

    void setCdmaSubscriptionSource(int i, int i2) throws RemoteException;

    void setFacilityLockForApp(int i, String str, boolean z, String str2, int i2, String str3) throws RemoteException;

    void setResponseFunctions(IRadioSimResponse iRadioSimResponse, IRadioSimIndication iRadioSimIndication) throws RemoteException;

    void setSimCardPower(int i, int i2) throws RemoteException;

    void setUiccSubscription(int i, SelectUiccSub selectUiccSub) throws RemoteException;

    void supplyIccPin2ForApp(int i, String str, String str2) throws RemoteException;

    void supplyIccPinForApp(int i, String str, String str2) throws RemoteException;

    void supplyIccPuk2ForApp(int i, String str, String str2, String str3) throws RemoteException;

    void supplyIccPukForApp(int i, String str, String str2, String str3) throws RemoteException;

    void supplySimDepersonalization(int i, int i2, String str) throws RemoteException;

    void updateSimPhonebookRecords(int i, PhonebookRecordInfo phonebookRecordInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSim {
        @Override // android.hardware.radio.sim.IRadioSim
        public void areUiccApplicationsEnabled(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void changeIccPin2ForApp(int serial, String oldPin2, String newPin2, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void changeIccPinForApp(int serial, String oldPin, String newPin, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void enableUiccApplications(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getAllowedCarriers(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getCdmaSubscription(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getCdmaSubscriptionSource(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getFacilityLockForApp(int serial, String facility, String password, int serviceClass, String appId) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getIccCardStatus(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getImsiForApp(int serial, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getSimPhonebookCapacity(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void getSimPhonebookRecords(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccCloseLogicalChannel(int serial, int channelId) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccIoForApp(int serial, IccIo iccIo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccOpenLogicalChannel(int serial, String aid, int p2) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccTransmitApduBasicChannel(int serial, SimApdu message) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccTransmitApduLogicalChannel(int serial, SimApdu message) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void reportStkServiceIsRunning(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void requestIccSimAuthentication(int serial, int authContext, String authData, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void sendEnvelope(int serial, String contents) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void sendEnvelopeWithStatus(int serial, String contents) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void sendTerminalResponseToSim(int serial, String contents) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setAllowedCarriers(int serial, CarrierRestrictions carriers, int multiSimPolicy) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setCarrierInfoForImsiEncryption(int serial, ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setCdmaSubscriptionSource(int serial, int cdmaSub) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setFacilityLockForApp(int serial, String facility, boolean lockState, String password, int serviceClass, String appId) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setResponseFunctions(IRadioSimResponse radioSimResponse, IRadioSimIndication radioSimIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setSimCardPower(int serial, int powerUp) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void setUiccSubscription(int serial, SelectUiccSub uiccSub) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void supplyIccPin2ForApp(int serial, String pin2, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void supplyIccPinForApp(int serial, String pin, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void supplyIccPuk2ForApp(int serial, String puk2, String pin2, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void supplyIccPukForApp(int serial, String puk, String pin, String aid) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void supplySimDepersonalization(int serial, int persoType, String controlKey) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void updateSimPhonebookRecords(int serial, PhonebookRecordInfo recordInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public void iccCloseLogicalChannelWithSessionInfo(int serial, SessionInfo sessionInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.sim.IRadioSim
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSim {
        static final int TRANSACTION_areUiccApplicationsEnabled = 1;
        static final int TRANSACTION_changeIccPin2ForApp = 2;
        static final int TRANSACTION_changeIccPinForApp = 3;
        static final int TRANSACTION_enableUiccApplications = 4;
        static final int TRANSACTION_getAllowedCarriers = 5;
        static final int TRANSACTION_getCdmaSubscription = 6;
        static final int TRANSACTION_getCdmaSubscriptionSource = 7;
        static final int TRANSACTION_getFacilityLockForApp = 8;
        static final int TRANSACTION_getIccCardStatus = 9;
        static final int TRANSACTION_getImsiForApp = 10;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSimPhonebookCapacity = 11;
        static final int TRANSACTION_getSimPhonebookRecords = 12;
        static final int TRANSACTION_iccCloseLogicalChannel = 13;
        static final int TRANSACTION_iccCloseLogicalChannelWithSessionInfo = 37;
        static final int TRANSACTION_iccIoForApp = 14;
        static final int TRANSACTION_iccOpenLogicalChannel = 15;
        static final int TRANSACTION_iccTransmitApduBasicChannel = 16;
        static final int TRANSACTION_iccTransmitApduLogicalChannel = 17;
        static final int TRANSACTION_reportStkServiceIsRunning = 18;
        static final int TRANSACTION_requestIccSimAuthentication = 19;
        static final int TRANSACTION_responseAcknowledgement = 20;
        static final int TRANSACTION_sendEnvelope = 21;
        static final int TRANSACTION_sendEnvelopeWithStatus = 22;
        static final int TRANSACTION_sendTerminalResponseToSim = 23;
        static final int TRANSACTION_setAllowedCarriers = 24;
        static final int TRANSACTION_setCarrierInfoForImsiEncryption = 25;
        static final int TRANSACTION_setCdmaSubscriptionSource = 26;
        static final int TRANSACTION_setFacilityLockForApp = 27;
        static final int TRANSACTION_setResponseFunctions = 28;
        static final int TRANSACTION_setSimCardPower = 29;
        static final int TRANSACTION_setUiccSubscription = 30;
        static final int TRANSACTION_supplyIccPin2ForApp = 31;
        static final int TRANSACTION_supplyIccPinForApp = 32;
        static final int TRANSACTION_supplyIccPuk2ForApp = 33;
        static final int TRANSACTION_supplyIccPukForApp = 34;
        static final int TRANSACTION_supplySimDepersonalization = 35;
        static final int TRANSACTION_updateSimPhonebookRecords = 36;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSim asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSim)) {
                return (IRadioSim) iin;
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
                            areUiccApplicationsEnabled(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg1 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            changeIccPin2ForApp(_arg02, _arg1, _arg2, _arg3);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            changeIccPinForApp(_arg03, _arg12, _arg22, _arg32);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            enableUiccApplications(_arg04, _arg13);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            getAllowedCarriers(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaSubscription(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaSubscriptionSource(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg14 = data.readString();
                            String _arg23 = data.readString();
                            int _arg33 = data.readInt();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            getFacilityLockForApp(_arg08, _arg14, _arg23, _arg33, _arg4);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            getIccCardStatus(_arg09);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            getImsiForApp(_arg010, _arg15);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            getSimPhonebookCapacity(_arg011);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            getSimPhonebookRecords(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            iccCloseLogicalChannel(_arg013, _arg16);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            IccIo _arg17 = (IccIo) data.readTypedObject(IccIo.CREATOR);
                            data.enforceNoDataAvail();
                            iccIoForApp(_arg014, _arg17);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            String _arg18 = data.readString();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            iccOpenLogicalChannel(_arg015, _arg18, _arg24);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            SimApdu _arg19 = (SimApdu) data.readTypedObject(SimApdu.CREATOR);
                            data.enforceNoDataAvail();
                            iccTransmitApduBasicChannel(_arg016, _arg19);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            SimApdu _arg110 = (SimApdu) data.readTypedObject(SimApdu.CREATOR);
                            data.enforceNoDataAvail();
                            iccTransmitApduLogicalChannel(_arg017, _arg110);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            data.enforceNoDataAvail();
                            reportStkServiceIsRunning(_arg018);
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            int _arg111 = data.readInt();
                            String _arg25 = data.readString();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            requestIccSimAuthentication(_arg019, _arg111, _arg25, _arg34);
                            break;
                        case 20:
                            responseAcknowledgement();
                            break;
                        case 21:
                            int _arg020 = data.readInt();
                            String _arg112 = data.readString();
                            data.enforceNoDataAvail();
                            sendEnvelope(_arg020, _arg112);
                            break;
                        case 22:
                            int _arg021 = data.readInt();
                            String _arg113 = data.readString();
                            data.enforceNoDataAvail();
                            sendEnvelopeWithStatus(_arg021, _arg113);
                            break;
                        case 23:
                            int _arg022 = data.readInt();
                            String _arg114 = data.readString();
                            data.enforceNoDataAvail();
                            sendTerminalResponseToSim(_arg022, _arg114);
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            CarrierRestrictions _arg115 = (CarrierRestrictions) data.readTypedObject(CarrierRestrictions.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setAllowedCarriers(_arg023, _arg115, _arg26);
                            break;
                        case 25:
                            int _arg024 = data.readInt();
                            ImsiEncryptionInfo _arg116 = (ImsiEncryptionInfo) data.readTypedObject(ImsiEncryptionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCarrierInfoForImsiEncryption(_arg024, _arg116);
                            break;
                        case 26:
                            int _arg025 = data.readInt();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            setCdmaSubscriptionSource(_arg025, _arg117);
                            break;
                        case 27:
                            int _arg026 = data.readInt();
                            String _arg118 = data.readString();
                            boolean _arg27 = data.readBoolean();
                            String _arg35 = data.readString();
                            int _arg42 = data.readInt();
                            String _arg5 = data.readString();
                            data.enforceNoDataAvail();
                            setFacilityLockForApp(_arg026, _arg118, _arg27, _arg35, _arg42, _arg5);
                            break;
                        case 28:
                            IRadioSimResponse _arg027 = IRadioSimResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioSimIndication _arg119 = IRadioSimIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg027, _arg119);
                            break;
                        case 29:
                            int _arg028 = data.readInt();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            setSimCardPower(_arg028, _arg120);
                            break;
                        case 30:
                            int _arg029 = data.readInt();
                            SelectUiccSub _arg121 = (SelectUiccSub) data.readTypedObject(SelectUiccSub.CREATOR);
                            data.enforceNoDataAvail();
                            setUiccSubscription(_arg029, _arg121);
                            break;
                        case 31:
                            int _arg030 = data.readInt();
                            String _arg122 = data.readString();
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            supplyIccPin2ForApp(_arg030, _arg122, _arg28);
                            break;
                        case 32:
                            int _arg031 = data.readInt();
                            String _arg123 = data.readString();
                            String _arg29 = data.readString();
                            data.enforceNoDataAvail();
                            supplyIccPinForApp(_arg031, _arg123, _arg29);
                            break;
                        case 33:
                            int _arg032 = data.readInt();
                            String _arg124 = data.readString();
                            String _arg210 = data.readString();
                            String _arg36 = data.readString();
                            data.enforceNoDataAvail();
                            supplyIccPuk2ForApp(_arg032, _arg124, _arg210, _arg36);
                            break;
                        case 34:
                            int _arg033 = data.readInt();
                            String _arg125 = data.readString();
                            String _arg211 = data.readString();
                            String _arg37 = data.readString();
                            data.enforceNoDataAvail();
                            supplyIccPukForApp(_arg033, _arg125, _arg211, _arg37);
                            break;
                        case 35:
                            int _arg034 = data.readInt();
                            int _arg126 = data.readInt();
                            String _arg212 = data.readString();
                            data.enforceNoDataAvail();
                            supplySimDepersonalization(_arg034, _arg126, _arg212);
                            break;
                        case 36:
                            int _arg035 = data.readInt();
                            PhonebookRecordInfo _arg127 = (PhonebookRecordInfo) data.readTypedObject(PhonebookRecordInfo.CREATOR);
                            data.enforceNoDataAvail();
                            updateSimPhonebookRecords(_arg035, _arg127);
                            break;
                        case 37:
                            int _arg036 = data.readInt();
                            SessionInfo _arg128 = (SessionInfo) data.readTypedObject(SessionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            iccCloseLogicalChannelWithSessionInfo(_arg036, _arg128);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSim {
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

            @Override // android.hardware.radio.sim.IRadioSim
            public void areUiccApplicationsEnabled(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method areUiccApplicationsEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void changeIccPin2ForApp(int serial, String oldPin2, String newPin2, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(oldPin2);
                    _data.writeString(newPin2);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method changeIccPin2ForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void changeIccPinForApp(int serial, String oldPin, String newPin, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(oldPin);
                    _data.writeString(newPin);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method changeIccPinForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void enableUiccApplications(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method enableUiccApplications is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getAllowedCarriers(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAllowedCarriers is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getCdmaSubscription(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaSubscription is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getCdmaSubscriptionSource(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaSubscriptionSource is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getFacilityLockForApp(int serial, String facility, String password, int serviceClass, String appId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(facility);
                    _data.writeString(password);
                    _data.writeInt(serviceClass);
                    _data.writeString(appId);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getFacilityLockForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getIccCardStatus(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getIccCardStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getImsiForApp(int serial, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImsiForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getSimPhonebookCapacity(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimPhonebookCapacity is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void getSimPhonebookRecords(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimPhonebookRecords is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccCloseLogicalChannel(int serial, int channelId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(channelId);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccCloseLogicalChannel is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccIoForApp(int serial, IccIo iccIo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(iccIo, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccIoForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccOpenLogicalChannel(int serial, String aid, int p2) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(aid);
                    _data.writeInt(p2);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccOpenLogicalChannel is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccTransmitApduBasicChannel(int serial, SimApdu message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccTransmitApduBasicChannel is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccTransmitApduLogicalChannel(int serial, SimApdu message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccTransmitApduLogicalChannel is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void reportStkServiceIsRunning(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method reportStkServiceIsRunning is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void requestIccSimAuthentication(int serial, int authContext, String authData, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(authContext);
                    _data.writeString(authData);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method requestIccSimAuthentication is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void sendEnvelope(int serial, String contents) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(contents);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendEnvelope is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void sendEnvelopeWithStatus(int serial, String contents) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(contents);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendEnvelopeWithStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void sendTerminalResponseToSim(int serial, String contents) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(contents);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendTerminalResponseToSim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setAllowedCarriers(int serial, CarrierRestrictions carriers, int multiSimPolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(carriers, 0);
                    _data.writeInt(multiSimPolicy);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setAllowedCarriers is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setCarrierInfoForImsiEncryption(int serial, ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(imsiEncryptionInfo, 0);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCarrierInfoForImsiEncryption is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setCdmaSubscriptionSource(int serial, int cdmaSub) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(cdmaSub);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaSubscriptionSource is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setFacilityLockForApp(int serial, String facility, boolean lockState, String password, int serviceClass, String appId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(facility);
                    _data.writeBoolean(lockState);
                    _data.writeString(password);
                    _data.writeInt(serviceClass);
                    _data.writeString(appId);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setFacilityLockForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setResponseFunctions(IRadioSimResponse radioSimResponse, IRadioSimIndication radioSimIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioSimResponse);
                    _data.writeStrongInterface(radioSimIndication);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setSimCardPower(int serial, int powerUp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(powerUp);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSimCardPower is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void setUiccSubscription(int serial, SelectUiccSub uiccSub) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(uiccSub, 0);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setUiccSubscription is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void supplyIccPin2ForApp(int serial, String pin2, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(pin2);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPin2ForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void supplyIccPinForApp(int serial, String pin, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(pin);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPinForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void supplyIccPuk2ForApp(int serial, String puk2, String pin2, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(puk2);
                    _data.writeString(pin2);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPuk2ForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void supplyIccPukForApp(int serial, String puk, String pin, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(puk);
                    _data.writeString(pin);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyIccPukForApp is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void supplySimDepersonalization(int serial, int persoType, String controlKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(persoType);
                    _data.writeString(controlKey);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplySimDepersonalization is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void updateSimPhonebookRecords(int serial, PhonebookRecordInfo recordInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(recordInfo, 0);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method updateSimPhonebookRecords is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
            public void iccCloseLogicalChannelWithSessionInfo(int serial, SessionInfo sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(sessionInfo, 0);
                    boolean _status = this.mRemote.transact(37, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method iccCloseLogicalChannelWithSessionInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.sim.IRadioSim
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

            @Override // android.hardware.radio.sim.IRadioSim
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
