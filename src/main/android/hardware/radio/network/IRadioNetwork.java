package android.hardware.radio.network;

import android.hardware.radio.network.IRadioNetworkIndication;
import android.hardware.radio.network.IRadioNetworkResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioNetwork extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$network$IRadioNetwork".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void cancelEmergencyNetworkScan(int i, boolean z) throws RemoteException;

    void exitEmergencyMode(int i) throws RemoteException;

    void getAllowedNetworkTypesBitmap(int i) throws RemoteException;

    void getAvailableBandModes(int i) throws RemoteException;

    void getAvailableNetworks(int i) throws RemoteException;

    void getBarringInfo(int i) throws RemoteException;

    void getCdmaRoamingPreference(int i) throws RemoteException;

    void getCellInfoList(int i) throws RemoteException;

    void getDataRegistrationState(int i) throws RemoteException;

    @Deprecated
    void getImsRegistrationState(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getNetworkSelectionMode(int i) throws RemoteException;

    void getOperator(int i) throws RemoteException;

    void getSignalStrength(int i) throws RemoteException;

    void getSystemSelectionChannels(int i) throws RemoteException;

    void getUsageSetting(int i) throws RemoteException;

    void getVoiceRadioTechnology(int i) throws RemoteException;

    void getVoiceRegistrationState(int i) throws RemoteException;

    void isN1ModeEnabled(int i) throws RemoteException;

    void isNrDualConnectivityEnabled(int i) throws RemoteException;

    void isNullCipherAndIntegrityEnabled(int i) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void setAllowedNetworkTypesBitmap(int i, int i2) throws RemoteException;

    void setBandMode(int i, int i2) throws RemoteException;

    void setBarringPassword(int i, String str, String str2, String str3) throws RemoteException;

    void setCdmaRoamingPreference(int i, int i2) throws RemoteException;

    void setCellInfoListRate(int i, int i2) throws RemoteException;

    void setEmergencyMode(int i, int i2) throws RemoteException;

    void setIndicationFilter(int i, int i2) throws RemoteException;

    void setLinkCapacityReportingCriteria(int i, int i2, int i3, int i4, int[] iArr, int[] iArr2, int i5) throws RemoteException;

    void setLocationUpdates(int i, boolean z) throws RemoteException;

    void setN1ModeEnabled(int i, boolean z) throws RemoteException;

    void setNetworkSelectionModeAutomatic(int i) throws RemoteException;

    void setNetworkSelectionModeManual(int i, String str, int i2) throws RemoteException;

    void setNrDualConnectivityState(int i, byte b) throws RemoteException;

    void setNullCipherAndIntegrityEnabled(int i, boolean z) throws RemoteException;

    void setResponseFunctions(IRadioNetworkResponse iRadioNetworkResponse, IRadioNetworkIndication iRadioNetworkIndication) throws RemoteException;

    void setSignalStrengthReportingCriteria(int i, SignalThresholdInfo[] signalThresholdInfoArr) throws RemoteException;

    void setSuppServiceNotifications(int i, boolean z) throws RemoteException;

    void setSystemSelectionChannels(int i, boolean z, RadioAccessSpecifier[] radioAccessSpecifierArr) throws RemoteException;

    void setUsageSetting(int i, int i2) throws RemoteException;

    void startNetworkScan(int i, NetworkScanRequest networkScanRequest) throws RemoteException;

    void stopNetworkScan(int i) throws RemoteException;

    void supplyNetworkDepersonalization(int i, String str) throws RemoteException;

    void triggerEmergencyNetworkScan(int i, EmergencyNetworkScanTrigger emergencyNetworkScanTrigger) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioNetwork {
        @Override // android.hardware.radio.network.IRadioNetwork
        public void getAllowedNetworkTypesBitmap(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getAvailableBandModes(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getAvailableNetworks(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getBarringInfo(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getCdmaRoamingPreference(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getCellInfoList(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getDataRegistrationState(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getImsRegistrationState(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getNetworkSelectionMode(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getOperator(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getSignalStrength(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getSystemSelectionChannels(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getVoiceRadioTechnology(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getVoiceRegistrationState(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void isNrDualConnectivityEnabled(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setAllowedNetworkTypesBitmap(int serial, int networkTypeBitmap) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setBandMode(int serial, int mode) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setBarringPassword(int serial, String facility, String oldPassword, String newPassword) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setCdmaRoamingPreference(int serial, int type) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setCellInfoListRate(int serial, int rate) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setIndicationFilter(int serial, int indicationFilter) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setLinkCapacityReportingCriteria(int serial, int hysteresisMs, int hysteresisDlKbps, int hysteresisUlKbps, int[] thresholdsDownlinkKbps, int[] thresholdsUplinkKbps, int accessNetwork) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setLocationUpdates(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setNetworkSelectionModeAutomatic(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setNetworkSelectionModeManual(int serial, String operatorNumeric, int ran) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setNrDualConnectivityState(int serial, byte nrDualConnectivityState) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setResponseFunctions(IRadioNetworkResponse radioNetworkResponse, IRadioNetworkIndication radioNetworkIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setSignalStrengthReportingCriteria(int serial, SignalThresholdInfo[] signalThresholdInfos) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setSuppServiceNotifications(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setSystemSelectionChannels(int serial, boolean specifyChannels, RadioAccessSpecifier[] specifiers) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void startNetworkScan(int serial, NetworkScanRequest request) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void stopNetworkScan(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void supplyNetworkDepersonalization(int serial, String netPin) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setUsageSetting(int serial, int usageSetting) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void getUsageSetting(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setEmergencyMode(int serial, int emcModeType) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void triggerEmergencyNetworkScan(int serial, EmergencyNetworkScanTrigger request) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void cancelEmergencyNetworkScan(int serial, boolean resetScan) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void exitEmergencyMode(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setNullCipherAndIntegrityEnabled(int serial, boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void isNullCipherAndIntegrityEnabled(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void isN1ModeEnabled(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public void setN1ModeEnabled(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.network.IRadioNetwork
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioNetwork {
        static final int TRANSACTION_cancelEmergencyNetworkScan = 39;
        static final int TRANSACTION_exitEmergencyMode = 40;
        static final int TRANSACTION_getAllowedNetworkTypesBitmap = 1;
        static final int TRANSACTION_getAvailableBandModes = 2;
        static final int TRANSACTION_getAvailableNetworks = 3;
        static final int TRANSACTION_getBarringInfo = 4;
        static final int TRANSACTION_getCdmaRoamingPreference = 5;
        static final int TRANSACTION_getCellInfoList = 6;
        static final int TRANSACTION_getDataRegistrationState = 7;
        static final int TRANSACTION_getImsRegistrationState = 8;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getNetworkSelectionMode = 9;
        static final int TRANSACTION_getOperator = 10;
        static final int TRANSACTION_getSignalStrength = 11;
        static final int TRANSACTION_getSystemSelectionChannels = 12;
        static final int TRANSACTION_getUsageSetting = 36;
        static final int TRANSACTION_getVoiceRadioTechnology = 13;
        static final int TRANSACTION_getVoiceRegistrationState = 14;
        static final int TRANSACTION_isN1ModeEnabled = 43;
        static final int TRANSACTION_isNrDualConnectivityEnabled = 15;
        static final int TRANSACTION_isNullCipherAndIntegrityEnabled = 42;
        static final int TRANSACTION_responseAcknowledgement = 16;
        static final int TRANSACTION_setAllowedNetworkTypesBitmap = 17;
        static final int TRANSACTION_setBandMode = 18;
        static final int TRANSACTION_setBarringPassword = 19;
        static final int TRANSACTION_setCdmaRoamingPreference = 20;
        static final int TRANSACTION_setCellInfoListRate = 21;
        static final int TRANSACTION_setEmergencyMode = 37;
        static final int TRANSACTION_setIndicationFilter = 22;
        static final int TRANSACTION_setLinkCapacityReportingCriteria = 23;
        static final int TRANSACTION_setLocationUpdates = 24;
        static final int TRANSACTION_setN1ModeEnabled = 44;
        static final int TRANSACTION_setNetworkSelectionModeAutomatic = 25;
        static final int TRANSACTION_setNetworkSelectionModeManual = 26;
        static final int TRANSACTION_setNrDualConnectivityState = 27;
        static final int TRANSACTION_setNullCipherAndIntegrityEnabled = 41;
        static final int TRANSACTION_setResponseFunctions = 28;
        static final int TRANSACTION_setSignalStrengthReportingCriteria = 29;
        static final int TRANSACTION_setSuppServiceNotifications = 30;
        static final int TRANSACTION_setSystemSelectionChannels = 31;
        static final int TRANSACTION_setUsageSetting = 35;
        static final int TRANSACTION_startNetworkScan = 32;
        static final int TRANSACTION_stopNetworkScan = 33;
        static final int TRANSACTION_supplyNetworkDepersonalization = 34;
        static final int TRANSACTION_triggerEmergencyNetworkScan = 38;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioNetwork asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioNetwork)) {
                return (IRadioNetwork) iin;
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
                            getAllowedNetworkTypesBitmap(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            getAvailableBandModes(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            getAvailableNetworks(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            getBarringInfo(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaRoamingPreference(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            getCellInfoList(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            getDataRegistrationState(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            getImsRegistrationState(_arg08);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            getNetworkSelectionMode(_arg09);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            getOperator(_arg010);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            getSignalStrength(_arg011);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            getSystemSelectionChannels(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            getVoiceRadioTechnology(_arg013);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            getVoiceRegistrationState(_arg014);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            isNrDualConnectivityEnabled(_arg015);
                            break;
                        case 16:
                            responseAcknowledgement();
                            break;
                        case 17:
                            int _arg016 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            setAllowedNetworkTypesBitmap(_arg016, _arg1);
                            break;
                        case 18:
                            int _arg017 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            setBandMode(_arg017, _arg12);
                            break;
                        case 19:
                            int _arg018 = data.readInt();
                            String _arg13 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            setBarringPassword(_arg018, _arg13, _arg2, _arg3);
                            break;
                        case 20:
                            int _arg019 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            setCdmaRoamingPreference(_arg019, _arg14);
                            break;
                        case 21:
                            int _arg020 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            setCellInfoListRate(_arg020, _arg15);
                            break;
                        case 22:
                            int _arg021 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            setIndicationFilter(_arg021, _arg16);
                            break;
                        case 23:
                            int _arg022 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            int[] _arg4 = data.createIntArray();
                            int[] _arg5 = data.createIntArray();
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            setLinkCapacityReportingCriteria(_arg022, _arg17, _arg22, _arg32, _arg4, _arg5, _arg6);
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            boolean _arg18 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setLocationUpdates(_arg023, _arg18);
                            break;
                        case 25:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            setNetworkSelectionModeAutomatic(_arg024);
                            break;
                        case 26:
                            int _arg025 = data.readInt();
                            String _arg19 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setNetworkSelectionModeManual(_arg025, _arg19, _arg23);
                            break;
                        case 27:
                            int _arg026 = data.readInt();
                            byte _arg110 = data.readByte();
                            data.enforceNoDataAvail();
                            setNrDualConnectivityState(_arg026, _arg110);
                            break;
                        case 28:
                            IRadioNetworkResponse _arg027 = IRadioNetworkResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioNetworkIndication _arg111 = IRadioNetworkIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg027, _arg111);
                            break;
                        case 29:
                            int _arg028 = data.readInt();
                            SignalThresholdInfo[] _arg112 = (SignalThresholdInfo[]) data.createTypedArray(SignalThresholdInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSignalStrengthReportingCriteria(_arg028, _arg112);
                            break;
                        case 30:
                            int _arg029 = data.readInt();
                            boolean _arg113 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSuppServiceNotifications(_arg029, _arg113);
                            break;
                        case 31:
                            int _arg030 = data.readInt();
                            boolean _arg114 = data.readBoolean();
                            RadioAccessSpecifier[] _arg24 = (RadioAccessSpecifier[]) data.createTypedArray(RadioAccessSpecifier.CREATOR);
                            data.enforceNoDataAvail();
                            setSystemSelectionChannels(_arg030, _arg114, _arg24);
                            break;
                        case 32:
                            int _arg031 = data.readInt();
                            NetworkScanRequest _arg115 = (NetworkScanRequest) data.readTypedObject(NetworkScanRequest.CREATOR);
                            data.enforceNoDataAvail();
                            startNetworkScan(_arg031, _arg115);
                            break;
                        case 33:
                            int _arg032 = data.readInt();
                            data.enforceNoDataAvail();
                            stopNetworkScan(_arg032);
                            break;
                        case 34:
                            int _arg033 = data.readInt();
                            String _arg116 = data.readString();
                            data.enforceNoDataAvail();
                            supplyNetworkDepersonalization(_arg033, _arg116);
                            break;
                        case 35:
                            int _arg034 = data.readInt();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            setUsageSetting(_arg034, _arg117);
                            break;
                        case 36:
                            int _arg035 = data.readInt();
                            data.enforceNoDataAvail();
                            getUsageSetting(_arg035);
                            break;
                        case 37:
                            int _arg036 = data.readInt();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            setEmergencyMode(_arg036, _arg118);
                            break;
                        case 38:
                            int _arg037 = data.readInt();
                            EmergencyNetworkScanTrigger _arg119 = (EmergencyNetworkScanTrigger) data.readTypedObject(EmergencyNetworkScanTrigger.CREATOR);
                            data.enforceNoDataAvail();
                            triggerEmergencyNetworkScan(_arg037, _arg119);
                            break;
                        case 39:
                            int _arg038 = data.readInt();
                            boolean _arg120 = data.readBoolean();
                            data.enforceNoDataAvail();
                            cancelEmergencyNetworkScan(_arg038, _arg120);
                            break;
                        case 40:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            exitEmergencyMode(_arg039);
                            break;
                        case 41:
                            int _arg040 = data.readInt();
                            boolean _arg121 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setNullCipherAndIntegrityEnabled(_arg040, _arg121);
                            break;
                        case 42:
                            int _arg041 = data.readInt();
                            data.enforceNoDataAvail();
                            isNullCipherAndIntegrityEnabled(_arg041);
                            break;
                        case 43:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            isN1ModeEnabled(_arg042);
                            break;
                        case 44:
                            int _arg043 = data.readInt();
                            boolean _arg122 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setN1ModeEnabled(_arg043, _arg122);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioNetwork {
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

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getAllowedNetworkTypesBitmap(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAllowedNetworkTypesBitmap is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getAvailableBandModes(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAvailableBandModes is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getAvailableNetworks(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAvailableNetworks is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getBarringInfo(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getBarringInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getCdmaRoamingPreference(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaRoamingPreference is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getCellInfoList(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCellInfoList is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getDataRegistrationState(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDataRegistrationState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getImsRegistrationState(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImsRegistrationState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getNetworkSelectionMode(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getNetworkSelectionMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getOperator(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getOperator is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getSignalStrength(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSignalStrength is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getSystemSelectionChannels(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSystemSelectionChannels is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getVoiceRadioTechnology(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getVoiceRadioTechnology is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getVoiceRegistrationState(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getVoiceRegistrationState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void isNrDualConnectivityEnabled(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isNrDualConnectivityEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setAllowedNetworkTypesBitmap(int serial, int networkTypeBitmap) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(networkTypeBitmap);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setAllowedNetworkTypesBitmap is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setBandMode(int serial, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setBandMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setBarringPassword(int serial, String facility, String oldPassword, String newPassword) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(facility);
                    _data.writeString(oldPassword);
                    _data.writeString(newPassword);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setBarringPassword is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setCdmaRoamingPreference(int serial, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaRoamingPreference is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setCellInfoListRate(int serial, int rate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(rate);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCellInfoListRate is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setIndicationFilter(int serial, int indicationFilter) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(indicationFilter);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setIndicationFilter is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setLinkCapacityReportingCriteria(int serial, int hysteresisMs, int hysteresisDlKbps, int hysteresisUlKbps, int[] thresholdsDownlinkKbps, int[] thresholdsUplinkKbps, int accessNetwork) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(hysteresisMs);
                    _data.writeInt(hysteresisDlKbps);
                    _data.writeInt(hysteresisUlKbps);
                    _data.writeIntArray(thresholdsDownlinkKbps);
                    _data.writeIntArray(thresholdsUplinkKbps);
                    _data.writeInt(accessNetwork);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setLinkCapacityReportingCriteria is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setLocationUpdates(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setLocationUpdates is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setNetworkSelectionModeAutomatic(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNetworkSelectionModeAutomatic is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setNetworkSelectionModeManual(int serial, String operatorNumeric, int ran) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(operatorNumeric);
                    _data.writeInt(ran);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNetworkSelectionModeManual is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setNrDualConnectivityState(int serial, byte nrDualConnectivityState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeByte(nrDualConnectivityState);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNrDualConnectivityState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setResponseFunctions(IRadioNetworkResponse radioNetworkResponse, IRadioNetworkIndication radioNetworkIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioNetworkResponse);
                    _data.writeStrongInterface(radioNetworkIndication);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setSignalStrengthReportingCriteria(int serial, SignalThresholdInfo[] signalThresholdInfos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(signalThresholdInfos, 0);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSignalStrengthReportingCriteria is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setSuppServiceNotifications(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSuppServiceNotifications is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setSystemSelectionChannels(int serial, boolean specifyChannels, RadioAccessSpecifier[] specifiers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(specifyChannels);
                    _data.writeTypedArray(specifiers, 0);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSystemSelectionChannels is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void startNetworkScan(int serial, NetworkScanRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(request, 0);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startNetworkScan is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void stopNetworkScan(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopNetworkScan is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void supplyNetworkDepersonalization(int serial, String netPin) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(netPin);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyNetworkDepersonalization is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setUsageSetting(int serial, int usageSetting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(usageSetting);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setUsageSetting is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void getUsageSetting(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getUsageSetting is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setEmergencyMode(int serial, int emcModeType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(emcModeType);
                    boolean _status = this.mRemote.transact(37, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setEmergencyMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void triggerEmergencyNetworkScan(int serial, EmergencyNetworkScanTrigger request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(request, 0);
                    boolean _status = this.mRemote.transact(38, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method triggerEmergencyNetworkScan is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void cancelEmergencyNetworkScan(int serial, boolean resetScan) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(resetScan);
                    boolean _status = this.mRemote.transact(39, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelEmergencyNetworkScan is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void exitEmergencyMode(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(40, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method exitEmergencyMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setNullCipherAndIntegrityEnabled(int serial, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enabled);
                    boolean _status = this.mRemote.transact(41, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNullCipherAndIntegrityEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void isNullCipherAndIntegrityEnabled(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(42, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isNullCipherAndIntegrityEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void isN1ModeEnabled(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(43, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isN1ModeEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
            public void setN1ModeEnabled(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(44, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setN1ModeEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetwork
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

            @Override // android.hardware.radio.network.IRadioNetwork
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
