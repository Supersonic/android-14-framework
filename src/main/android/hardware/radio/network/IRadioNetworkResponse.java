package android.hardware.radio.network;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioNetworkResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$network$IRadioNetworkResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeRequest(int i) throws RemoteException;

    void cancelEmergencyNetworkScanResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void exitEmergencyModeResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getAllowedNetworkTypesBitmapResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getAvailableBandModesResponse(RadioResponseInfo radioResponseInfo, int[] iArr) throws RemoteException;

    void getAvailableNetworksResponse(RadioResponseInfo radioResponseInfo, OperatorInfo[] operatorInfoArr) throws RemoteException;

    void getBarringInfoResponse(RadioResponseInfo radioResponseInfo, CellIdentity cellIdentity, BarringInfo[] barringInfoArr) throws RemoteException;

    void getCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getCellInfoListResponse(RadioResponseInfo radioResponseInfo, CellInfo[] cellInfoArr) throws RemoteException;

    void getDataRegistrationStateResponse(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) throws RemoteException;

    @Deprecated
    void getImsRegistrationStateResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getNetworkSelectionModeResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void getOperatorResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3) throws RemoteException;

    void getSignalStrengthResponse(RadioResponseInfo radioResponseInfo, SignalStrength signalStrength) throws RemoteException;

    void getSystemSelectionChannelsResponse(RadioResponseInfo radioResponseInfo, RadioAccessSpecifier[] radioAccessSpecifierArr) throws RemoteException;

    void getUsageSettingResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getVoiceRadioTechnologyResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getVoiceRegistrationStateResponse(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) throws RemoteException;

    void isN1ModeEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void isNrDualConnectivityEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void isNullCipherAndIntegrityEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void setAllowedNetworkTypesBitmapResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setBandModeResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setBarringPasswordResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCellInfoListRateResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setEmergencyModeResponse(RadioResponseInfo radioResponseInfo, EmergencyRegResult emergencyRegResult) throws RemoteException;

    void setIndicationFilterResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setLocationUpdatesResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setN1ModeEnabledResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setNetworkSelectionModeManualResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setNrDualConnectivityStateResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setNullCipherAndIntegrityEnabledResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSuppServiceNotificationsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSystemSelectionChannelsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setUsageSettingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void startNetworkScanResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void stopNetworkScanResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void supplyNetworkDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void triggerEmergencyNetworkScanResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioNetworkResponse {
        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getAllowedNetworkTypesBitmapResponse(RadioResponseInfo info, int networkTypeBitmap) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getAvailableBandModesResponse(RadioResponseInfo info, int[] bandModes) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getAvailableNetworksResponse(RadioResponseInfo info, OperatorInfo[] networkInfos) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getBarringInfoResponse(RadioResponseInfo info, CellIdentity cellIdentity, BarringInfo[] barringInfos) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getCdmaRoamingPreferenceResponse(RadioResponseInfo info, int type) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getCellInfoListResponse(RadioResponseInfo info, CellInfo[] cellInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getDataRegistrationStateResponse(RadioResponseInfo info, RegStateResult dataRegResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getImsRegistrationStateResponse(RadioResponseInfo info, boolean isRegistered, int ratFamily) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getNetworkSelectionModeResponse(RadioResponseInfo info, boolean manual) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getOperatorResponse(RadioResponseInfo info, String longName, String shortName, String numeric) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getSignalStrengthResponse(RadioResponseInfo info, SignalStrength signalStrength) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getSystemSelectionChannelsResponse(RadioResponseInfo info, RadioAccessSpecifier[] specifiers) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getVoiceRadioTechnologyResponse(RadioResponseInfo info, int rat) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getVoiceRegistrationStateResponse(RadioResponseInfo info, RegStateResult voiceRegResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void isNrDualConnectivityEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setAllowedNetworkTypesBitmapResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setBandModeResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setBarringPasswordResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setCdmaRoamingPreferenceResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setCellInfoListRateResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setIndicationFilterResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setLocationUpdatesResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setNetworkSelectionModeManualResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setNrDualConnectivityStateResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setSuppServiceNotificationsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setSystemSelectionChannelsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void startNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void stopNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void supplyNetworkDepersonalizationResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setUsageSettingResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void getUsageSettingResponse(RadioResponseInfo info, int usageSetting) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setEmergencyModeResponse(RadioResponseInfo info, EmergencyRegResult regState) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void triggerEmergencyNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void exitEmergencyModeResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void cancelEmergencyNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setNullCipherAndIntegrityEnabledResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void isNullCipherAndIntegrityEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void isN1ModeEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public void setN1ModeEnabledResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.network.IRadioNetworkResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioNetworkResponse {
        static final int TRANSACTION_acknowledgeRequest = 1;
        static final int TRANSACTION_cancelEmergencyNetworkScanResponse = 39;
        static final int TRANSACTION_exitEmergencyModeResponse = 38;
        static final int TRANSACTION_getAllowedNetworkTypesBitmapResponse = 2;
        static final int TRANSACTION_getAvailableBandModesResponse = 3;
        static final int TRANSACTION_getAvailableNetworksResponse = 4;
        static final int TRANSACTION_getBarringInfoResponse = 5;
        static final int TRANSACTION_getCdmaRoamingPreferenceResponse = 6;
        static final int TRANSACTION_getCellInfoListResponse = 7;
        static final int TRANSACTION_getDataRegistrationStateResponse = 8;
        static final int TRANSACTION_getImsRegistrationStateResponse = 9;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getNetworkSelectionModeResponse = 10;
        static final int TRANSACTION_getOperatorResponse = 11;
        static final int TRANSACTION_getSignalStrengthResponse = 12;
        static final int TRANSACTION_getSystemSelectionChannelsResponse = 13;
        static final int TRANSACTION_getUsageSettingResponse = 35;
        static final int TRANSACTION_getVoiceRadioTechnologyResponse = 14;
        static final int TRANSACTION_getVoiceRegistrationStateResponse = 15;
        static final int TRANSACTION_isN1ModeEnabledResponse = 42;
        static final int TRANSACTION_isNrDualConnectivityEnabledResponse = 16;
        static final int TRANSACTION_isNullCipherAndIntegrityEnabledResponse = 41;
        static final int TRANSACTION_setAllowedNetworkTypesBitmapResponse = 17;
        static final int TRANSACTION_setBandModeResponse = 18;
        static final int TRANSACTION_setBarringPasswordResponse = 19;
        static final int TRANSACTION_setCdmaRoamingPreferenceResponse = 20;
        static final int TRANSACTION_setCellInfoListRateResponse = 21;
        static final int TRANSACTION_setEmergencyModeResponse = 36;
        static final int TRANSACTION_setIndicationFilterResponse = 22;
        static final int TRANSACTION_setLinkCapacityReportingCriteriaResponse = 23;
        static final int TRANSACTION_setLocationUpdatesResponse = 24;
        static final int TRANSACTION_setN1ModeEnabledResponse = 43;
        static final int TRANSACTION_setNetworkSelectionModeAutomaticResponse = 25;
        static final int TRANSACTION_setNetworkSelectionModeManualResponse = 26;
        static final int TRANSACTION_setNrDualConnectivityStateResponse = 27;
        static final int TRANSACTION_setNullCipherAndIntegrityEnabledResponse = 40;
        static final int TRANSACTION_setSignalStrengthReportingCriteriaResponse = 28;
        static final int TRANSACTION_setSuppServiceNotificationsResponse = 29;
        static final int TRANSACTION_setSystemSelectionChannelsResponse = 30;
        static final int TRANSACTION_setUsageSettingResponse = 34;
        static final int TRANSACTION_startNetworkScanResponse = 31;
        static final int TRANSACTION_stopNetworkScanResponse = 32;
        static final int TRANSACTION_supplyNetworkDepersonalizationResponse = 33;
        static final int TRANSACTION_triggerEmergencyNetworkScanResponse = 37;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioNetworkResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioNetworkResponse)) {
                return (IRadioNetworkResponse) iin;
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
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            getAllowedNetworkTypesBitmapResponse(_arg02, _arg1);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int[] _arg12 = data.createIntArray();
                            data.enforceNoDataAvail();
                            getAvailableBandModesResponse(_arg03, _arg12);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            OperatorInfo[] _arg13 = (OperatorInfo[]) data.createTypedArray(OperatorInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getAvailableNetworksResponse(_arg04, _arg13);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CellIdentity _arg14 = (CellIdentity) data.readTypedObject(CellIdentity.CREATOR);
                            BarringInfo[] _arg2 = (BarringInfo[]) data.createTypedArray(BarringInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getBarringInfoResponse(_arg05, _arg14, _arg2);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaRoamingPreferenceResponse(_arg06, _arg15);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CellInfo[] _arg16 = (CellInfo[]) data.createTypedArray(CellInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getCellInfoListResponse(_arg07, _arg16);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            RegStateResult _arg17 = (RegStateResult) data.readTypedObject(RegStateResult.CREATOR);
                            data.enforceNoDataAvail();
                            getDataRegistrationStateResponse(_arg08, _arg17);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg18 = data.readBoolean();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            getImsRegistrationStateResponse(_arg09, _arg18, _arg22);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg19 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getNetworkSelectionModeResponse(_arg010, _arg19);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg110 = data.readString();
                            String _arg23 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            getOperatorResponse(_arg011, _arg110, _arg23, _arg3);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SignalStrength _arg111 = (SignalStrength) data.readTypedObject(SignalStrength.CREATOR);
                            data.enforceNoDataAvail();
                            getSignalStrengthResponse(_arg012, _arg111);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            RadioAccessSpecifier[] _arg112 = (RadioAccessSpecifier[]) data.createTypedArray(RadioAccessSpecifier.CREATOR);
                            data.enforceNoDataAvail();
                            getSystemSelectionChannelsResponse(_arg013, _arg112);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            getVoiceRadioTechnologyResponse(_arg014, _arg113);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            RegStateResult _arg114 = (RegStateResult) data.readTypedObject(RegStateResult.CREATOR);
                            data.enforceNoDataAvail();
                            getVoiceRegistrationStateResponse(_arg015, _arg114);
                            break;
                        case 16:
                            RadioResponseInfo _arg016 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg115 = data.readBoolean();
                            data.enforceNoDataAvail();
                            isNrDualConnectivityEnabledResponse(_arg016, _arg115);
                            break;
                        case 17:
                            RadioResponseInfo _arg017 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setAllowedNetworkTypesBitmapResponse(_arg017);
                            break;
                        case 18:
                            RadioResponseInfo _arg018 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setBandModeResponse(_arg018);
                            break;
                        case 19:
                            RadioResponseInfo _arg019 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setBarringPasswordResponse(_arg019);
                            break;
                        case 20:
                            RadioResponseInfo _arg020 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCdmaRoamingPreferenceResponse(_arg020);
                            break;
                        case 21:
                            RadioResponseInfo _arg021 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCellInfoListRateResponse(_arg021);
                            break;
                        case 22:
                            RadioResponseInfo _arg022 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setIndicationFilterResponse(_arg022);
                            break;
                        case 23:
                            RadioResponseInfo _arg023 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setLinkCapacityReportingCriteriaResponse(_arg023);
                            break;
                        case 24:
                            RadioResponseInfo _arg024 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setLocationUpdatesResponse(_arg024);
                            break;
                        case 25:
                            RadioResponseInfo _arg025 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setNetworkSelectionModeAutomaticResponse(_arg025);
                            break;
                        case 26:
                            RadioResponseInfo _arg026 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setNetworkSelectionModeManualResponse(_arg026);
                            break;
                        case 27:
                            RadioResponseInfo _arg027 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setNrDualConnectivityStateResponse(_arg027);
                            break;
                        case 28:
                            RadioResponseInfo _arg028 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSignalStrengthReportingCriteriaResponse(_arg028);
                            break;
                        case 29:
                            RadioResponseInfo _arg029 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSuppServiceNotificationsResponse(_arg029);
                            break;
                        case 30:
                            RadioResponseInfo _arg030 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSystemSelectionChannelsResponse(_arg030);
                            break;
                        case 31:
                            RadioResponseInfo _arg031 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            startNetworkScanResponse(_arg031);
                            break;
                        case 32:
                            RadioResponseInfo _arg032 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            stopNetworkScanResponse(_arg032);
                            break;
                        case 33:
                            RadioResponseInfo _arg033 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            supplyNetworkDepersonalizationResponse(_arg033, _arg116);
                            break;
                        case 34:
                            RadioResponseInfo _arg034 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setUsageSettingResponse(_arg034);
                            break;
                        case 35:
                            RadioResponseInfo _arg035 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            getUsageSettingResponse(_arg035, _arg117);
                            break;
                        case 36:
                            RadioResponseInfo _arg036 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            EmergencyRegResult _arg118 = (EmergencyRegResult) data.readTypedObject(EmergencyRegResult.CREATOR);
                            data.enforceNoDataAvail();
                            setEmergencyModeResponse(_arg036, _arg118);
                            break;
                        case 37:
                            RadioResponseInfo _arg037 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            triggerEmergencyNetworkScanResponse(_arg037);
                            break;
                        case 38:
                            RadioResponseInfo _arg038 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            exitEmergencyModeResponse(_arg038);
                            break;
                        case 39:
                            RadioResponseInfo _arg039 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            cancelEmergencyNetworkScanResponse(_arg039);
                            break;
                        case 40:
                            RadioResponseInfo _arg040 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setNullCipherAndIntegrityEnabledResponse(_arg040);
                            break;
                        case 41:
                            RadioResponseInfo _arg041 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg119 = data.readBoolean();
                            data.enforceNoDataAvail();
                            isNullCipherAndIntegrityEnabledResponse(_arg041, _arg119);
                            break;
                        case 42:
                            RadioResponseInfo _arg042 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg120 = data.readBoolean();
                            data.enforceNoDataAvail();
                            isN1ModeEnabledResponse(_arg042, _arg120);
                            break;
                        case 43:
                            RadioResponseInfo _arg043 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setN1ModeEnabledResponse(_arg043);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioNetworkResponse {
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

            @Override // android.hardware.radio.network.IRadioNetworkResponse
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

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getAllowedNetworkTypesBitmapResponse(RadioResponseInfo info, int networkTypeBitmap) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(networkTypeBitmap);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAllowedNetworkTypesBitmapResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getAvailableBandModesResponse(RadioResponseInfo info, int[] bandModes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeIntArray(bandModes);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAvailableBandModesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getAvailableNetworksResponse(RadioResponseInfo info, OperatorInfo[] networkInfos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(networkInfos, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getAvailableNetworksResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getBarringInfoResponse(RadioResponseInfo info, CellIdentity cellIdentity, BarringInfo[] barringInfos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(cellIdentity, 0);
                    _data.writeTypedArray(barringInfos, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getBarringInfoResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getCdmaRoamingPreferenceResponse(RadioResponseInfo info, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaRoamingPreferenceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getCellInfoListResponse(RadioResponseInfo info, CellInfo[] cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(cellInfo, 0);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCellInfoListResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getDataRegistrationStateResponse(RadioResponseInfo info, RegStateResult dataRegResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(dataRegResponse, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDataRegistrationStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getImsRegistrationStateResponse(RadioResponseInfo info, boolean isRegistered, int ratFamily) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(isRegistered);
                    _data.writeInt(ratFamily);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImsRegistrationStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getNetworkSelectionModeResponse(RadioResponseInfo info, boolean manual) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(manual);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getNetworkSelectionModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getOperatorResponse(RadioResponseInfo info, String longName, String shortName, String numeric) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(longName);
                    _data.writeString(shortName);
                    _data.writeString(numeric);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getOperatorResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getSignalStrengthResponse(RadioResponseInfo info, SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(signalStrength, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSignalStrengthResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getSystemSelectionChannelsResponse(RadioResponseInfo info, RadioAccessSpecifier[] specifiers) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(specifiers, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSystemSelectionChannelsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getVoiceRadioTechnologyResponse(RadioResponseInfo info, int rat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(rat);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getVoiceRadioTechnologyResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getVoiceRegistrationStateResponse(RadioResponseInfo info, RegStateResult voiceRegResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(voiceRegResponse, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getVoiceRegistrationStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void isNrDualConnectivityEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(isEnabled);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isNrDualConnectivityEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setAllowedNetworkTypesBitmapResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setAllowedNetworkTypesBitmapResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setBandModeResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setBandModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setBarringPasswordResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setBarringPasswordResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setCdmaRoamingPreferenceResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaRoamingPreferenceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setCellInfoListRateResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCellInfoListRateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setIndicationFilterResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setIndicationFilterResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setLinkCapacityReportingCriteriaResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setLocationUpdatesResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setLocationUpdatesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNetworkSelectionModeAutomaticResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setNetworkSelectionModeManualResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNetworkSelectionModeManualResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setNrDualConnectivityStateResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNrDualConnectivityStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSignalStrengthReportingCriteriaResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setSuppServiceNotificationsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSuppServiceNotificationsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setSystemSelectionChannelsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSystemSelectionChannelsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void startNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startNetworkScanResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void stopNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopNetworkScanResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void supplyNetworkDepersonalizationResponse(RadioResponseInfo info, int remainingRetries) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(remainingRetries);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method supplyNetworkDepersonalizationResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setUsageSettingResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setUsageSettingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void getUsageSettingResponse(RadioResponseInfo info, int usageSetting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(usageSetting);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getUsageSettingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setEmergencyModeResponse(RadioResponseInfo info, EmergencyRegResult regState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(regState, 0);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setEmergencyModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void triggerEmergencyNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(37, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method triggerEmergencyNetworkScanResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void exitEmergencyModeResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(38, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method exitEmergencyModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void cancelEmergencyNetworkScanResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(39, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelEmergencyNetworkScanResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setNullCipherAndIntegrityEnabledResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(40, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNullCipherAndIntegrityEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void isNullCipherAndIntegrityEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(isEnabled);
                    boolean _status = this.mRemote.transact(41, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isNullCipherAndIntegrityEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void isN1ModeEnabledResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(isEnabled);
                    boolean _status = this.mRemote.transact(42, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isN1ModeEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
            public void setN1ModeEnabledResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(43, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setN1ModeEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.network.IRadioNetworkResponse
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

            @Override // android.hardware.radio.network.IRadioNetworkResponse
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
