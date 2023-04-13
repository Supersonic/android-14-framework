package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.BarringInfo;
import android.telephony.CallState;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.DataConnectionRealTimeInfo;
import android.telephony.LinkCapacityEstimate;
import android.telephony.PhoneCapability;
import android.telephony.PhysicalChannelConfig;
import android.telephony.PreciseCallState;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
public interface IPhoneStateListener extends IInterface {
    void onActiveDataSubIdChanged(int i) throws RemoteException;

    void onAllowedNetworkTypesChanged(int i, long j) throws RemoteException;

    void onBarringInfoChanged(BarringInfo barringInfo) throws RemoteException;

    void onCallBackModeStarted(int i) throws RemoteException;

    void onCallBackModeStopped(int i, int i2) throws RemoteException;

    void onCallDisconnectCauseChanged(int i, int i2) throws RemoteException;

    void onCallForwardingIndicatorChanged(boolean z) throws RemoteException;

    void onCallStateChanged(int i) throws RemoteException;

    void onCallStatesChanged(List<CallState> list) throws RemoteException;

    void onCarrierNetworkChange(boolean z) throws RemoteException;

    void onCellInfoChanged(List<CellInfo> list) throws RemoteException;

    void onCellLocationChanged(CellIdentity cellIdentity) throws RemoteException;

    void onDataActivationStateChanged(int i) throws RemoteException;

    void onDataActivity(int i) throws RemoteException;

    void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo dataConnectionRealTimeInfo) throws RemoteException;

    void onDataConnectionStateChanged(int i, int i2) throws RemoteException;

    void onDataEnabledChanged(boolean z, int i) throws RemoteException;

    void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException;

    void onEmergencyNumberListChanged(Map map) throws RemoteException;

    void onImsCallDisconnectCauseChanged(ImsReasonInfo imsReasonInfo) throws RemoteException;

    void onLegacyCallStateChanged(int i, String str) throws RemoteException;

    void onLinkCapacityEstimateChanged(List<LinkCapacityEstimate> list) throws RemoteException;

    void onMediaQualityStatusChanged(MediaQualityStatus mediaQualityStatus) throws RemoteException;

    void onMessageWaitingIndicatorChanged(boolean z) throws RemoteException;

    void onOemHookRawEvent(byte[] bArr) throws RemoteException;

    void onOutgoingEmergencyCall(EmergencyNumber emergencyNumber, int i) throws RemoteException;

    void onOutgoingEmergencySms(EmergencyNumber emergencyNumber, int i) throws RemoteException;

    void onPhoneCapabilityChanged(PhoneCapability phoneCapability) throws RemoteException;

    void onPhysicalChannelConfigChanged(List<PhysicalChannelConfig> list) throws RemoteException;

    void onPreciseCallStateChanged(PreciseCallState preciseCallState) throws RemoteException;

    void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) throws RemoteException;

    void onRadioPowerStateChanged(int i) throws RemoteException;

    void onRegistrationFailed(CellIdentity cellIdentity, String str, int i, int i2, int i3) throws RemoteException;

    void onServiceStateChanged(ServiceState serviceState) throws RemoteException;

    void onSignalStrengthChanged(int i) throws RemoteException;

    void onSignalStrengthsChanged(SignalStrength signalStrength) throws RemoteException;

    void onSrvccStateChanged(int i) throws RemoteException;

    void onUserMobileDataStateChanged(boolean z) throws RemoteException;

    void onVoiceActivationStateChanged(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPhoneStateListener {
        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthChanged(int asu) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onMessageWaitingIndicatorChanged(boolean mwi) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallForwardingIndicatorChanged(boolean cfi) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellLocationChanged(CellIdentity location) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onLegacyCallStateChanged(int state, String incomingNumber) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallStateChanged(int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataConnectionStateChanged(int state, int networkType) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataActivity(int direction) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellInfoChanged(List<CellInfo> cellInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPreciseCallStateChanged(PreciseCallState callState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo dcRtInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSrvccStateChanged(int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onVoiceActivationStateChanged(int activationState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataActivationStateChanged(int activationState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOemHookRawEvent(byte[] rawData) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCarrierNetworkChange(boolean active) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onUserMobileDataStateChanged(boolean enabled) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPhoneCapabilityChanged(PhoneCapability capability) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onActiveDataSubIdChanged(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onRadioPowerStateChanged(int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallStatesChanged(List<CallState> callStateList) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onEmergencyNumberListChanged(Map emergencyNumberList) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOutgoingEmergencyCall(EmergencyNumber placedEmergencyNumber, int subscriptionId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOutgoingEmergencySms(EmergencyNumber sentEmergencyNumber, int subscriptionId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallDisconnectCauseChanged(int disconnectCause, int preciseDisconnectCause) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onImsCallDisconnectCauseChanged(ImsReasonInfo imsReasonInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onRegistrationFailed(CellIdentity cellIdentity, String chosenPlmn, int domain, int causeCode, int additionalCauseCode) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onBarringInfoChanged(BarringInfo barringInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onPhysicalChannelConfigChanged(List<PhysicalChannelConfig> configs) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataEnabledChanged(boolean enabled, int reason) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onAllowedNetworkTypesChanged(int reason, long allowedNetworkType) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onLinkCapacityEstimateChanged(List<LinkCapacityEstimate> linkCapacityEstimateList) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onMediaQualityStatusChanged(MediaQualityStatus mediaQualityStatus) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallBackModeStarted(int type) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallBackModeStopped(int type, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPhoneStateListener {
        public static final String DESCRIPTOR = "com.android.internal.telephony.IPhoneStateListener";
        static final int TRANSACTION_onActiveDataSubIdChanged = 23;
        static final int TRANSACTION_onAllowedNetworkTypesChanged = 35;
        static final int TRANSACTION_onBarringInfoChanged = 32;
        static final int TRANSACTION_onCallBackModeStarted = 38;
        static final int TRANSACTION_onCallBackModeStopped = 39;
        static final int TRANSACTION_onCallDisconnectCauseChanged = 29;
        static final int TRANSACTION_onCallForwardingIndicatorChanged = 4;
        static final int TRANSACTION_onCallStateChanged = 7;
        static final int TRANSACTION_onCallStatesChanged = 25;
        static final int TRANSACTION_onCarrierNetworkChange = 19;
        static final int TRANSACTION_onCellInfoChanged = 11;
        static final int TRANSACTION_onCellLocationChanged = 5;
        static final int TRANSACTION_onDataActivationStateChanged = 17;
        static final int TRANSACTION_onDataActivity = 9;
        static final int TRANSACTION_onDataConnectionRealTimeInfoChanged = 14;
        static final int TRANSACTION_onDataConnectionStateChanged = 8;
        static final int TRANSACTION_onDataEnabledChanged = 34;
        static final int TRANSACTION_onDisplayInfoChanged = 21;
        static final int TRANSACTION_onEmergencyNumberListChanged = 26;
        static final int TRANSACTION_onImsCallDisconnectCauseChanged = 30;
        static final int TRANSACTION_onLegacyCallStateChanged = 6;
        static final int TRANSACTION_onLinkCapacityEstimateChanged = 36;
        static final int TRANSACTION_onMediaQualityStatusChanged = 37;
        static final int TRANSACTION_onMessageWaitingIndicatorChanged = 3;
        static final int TRANSACTION_onOemHookRawEvent = 18;
        static final int TRANSACTION_onOutgoingEmergencyCall = 27;
        static final int TRANSACTION_onOutgoingEmergencySms = 28;
        static final int TRANSACTION_onPhoneCapabilityChanged = 22;
        static final int TRANSACTION_onPhysicalChannelConfigChanged = 33;
        static final int TRANSACTION_onPreciseCallStateChanged = 12;
        static final int TRANSACTION_onPreciseDataConnectionStateChanged = 13;
        static final int TRANSACTION_onRadioPowerStateChanged = 24;
        static final int TRANSACTION_onRegistrationFailed = 31;
        static final int TRANSACTION_onServiceStateChanged = 1;
        static final int TRANSACTION_onSignalStrengthChanged = 2;
        static final int TRANSACTION_onSignalStrengthsChanged = 10;
        static final int TRANSACTION_onSrvccStateChanged = 15;
        static final int TRANSACTION_onUserMobileDataStateChanged = 20;
        static final int TRANSACTION_onVoiceActivationStateChanged = 16;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPhoneStateListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPhoneStateListener)) {
                return (IPhoneStateListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onServiceStateChanged";
                case 2:
                    return "onSignalStrengthChanged";
                case 3:
                    return "onMessageWaitingIndicatorChanged";
                case 4:
                    return "onCallForwardingIndicatorChanged";
                case 5:
                    return "onCellLocationChanged";
                case 6:
                    return "onLegacyCallStateChanged";
                case 7:
                    return "onCallStateChanged";
                case 8:
                    return "onDataConnectionStateChanged";
                case 9:
                    return "onDataActivity";
                case 10:
                    return "onSignalStrengthsChanged";
                case 11:
                    return "onCellInfoChanged";
                case 12:
                    return "onPreciseCallStateChanged";
                case 13:
                    return "onPreciseDataConnectionStateChanged";
                case 14:
                    return "onDataConnectionRealTimeInfoChanged";
                case 15:
                    return "onSrvccStateChanged";
                case 16:
                    return "onVoiceActivationStateChanged";
                case 17:
                    return "onDataActivationStateChanged";
                case 18:
                    return "onOemHookRawEvent";
                case 19:
                    return "onCarrierNetworkChange";
                case 20:
                    return "onUserMobileDataStateChanged";
                case 21:
                    return "onDisplayInfoChanged";
                case 22:
                    return "onPhoneCapabilityChanged";
                case 23:
                    return "onActiveDataSubIdChanged";
                case 24:
                    return "onRadioPowerStateChanged";
                case 25:
                    return "onCallStatesChanged";
                case 26:
                    return "onEmergencyNumberListChanged";
                case 27:
                    return "onOutgoingEmergencyCall";
                case 28:
                    return "onOutgoingEmergencySms";
                case 29:
                    return "onCallDisconnectCauseChanged";
                case 30:
                    return "onImsCallDisconnectCauseChanged";
                case 31:
                    return "onRegistrationFailed";
                case 32:
                    return "onBarringInfoChanged";
                case 33:
                    return "onPhysicalChannelConfigChanged";
                case 34:
                    return "onDataEnabledChanged";
                case 35:
                    return "onAllowedNetworkTypesChanged";
                case 36:
                    return "onLinkCapacityEstimateChanged";
                case 37:
                    return "onMediaQualityStatusChanged";
                case 38:
                    return "onCallBackModeStarted";
                case 39:
                    return "onCallBackModeStopped";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ServiceState _arg0 = (ServiceState) data.readTypedObject(ServiceState.CREATOR);
                            data.enforceNoDataAvail();
                            onServiceStateChanged(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onSignalStrengthChanged(_arg02);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onMessageWaitingIndicatorChanged(_arg03);
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onCallForwardingIndicatorChanged(_arg04);
                            break;
                        case 5:
                            CellIdentity _arg05 = (CellIdentity) data.readTypedObject(CellIdentity.CREATOR);
                            data.enforceNoDataAvail();
                            onCellLocationChanged(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onLegacyCallStateChanged(_arg06, _arg1);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallStateChanged(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onDataConnectionStateChanged(_arg08, _arg12);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            onDataActivity(_arg09);
                            break;
                        case 10:
                            SignalStrength _arg010 = (SignalStrength) data.readTypedObject(SignalStrength.CREATOR);
                            data.enforceNoDataAvail();
                            onSignalStrengthsChanged(_arg010);
                            break;
                        case 11:
                            List<CellInfo> _arg011 = data.createTypedArrayList(CellInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onCellInfoChanged(_arg011);
                            break;
                        case 12:
                            PreciseCallState _arg012 = (PreciseCallState) data.readTypedObject(PreciseCallState.CREATOR);
                            data.enforceNoDataAvail();
                            onPreciseCallStateChanged(_arg012);
                            break;
                        case 13:
                            PreciseDataConnectionState _arg013 = (PreciseDataConnectionState) data.readTypedObject(PreciseDataConnectionState.CREATOR);
                            data.enforceNoDataAvail();
                            onPreciseDataConnectionStateChanged(_arg013);
                            break;
                        case 14:
                            DataConnectionRealTimeInfo _arg014 = (DataConnectionRealTimeInfo) data.readTypedObject(DataConnectionRealTimeInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDataConnectionRealTimeInfoChanged(_arg014);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            onSrvccStateChanged(_arg015);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            onVoiceActivationStateChanged(_arg016);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            onDataActivationStateChanged(_arg017);
                            break;
                        case 18:
                            byte[] _arg018 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onOemHookRawEvent(_arg018);
                            break;
                        case 19:
                            boolean _arg019 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onCarrierNetworkChange(_arg019);
                            break;
                        case 20:
                            boolean _arg020 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onUserMobileDataStateChanged(_arg020);
                            break;
                        case 21:
                            TelephonyDisplayInfo _arg021 = (TelephonyDisplayInfo) data.readTypedObject(TelephonyDisplayInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayInfoChanged(_arg021);
                            break;
                        case 22:
                            PhoneCapability _arg022 = (PhoneCapability) data.readTypedObject(PhoneCapability.CREATOR);
                            data.enforceNoDataAvail();
                            onPhoneCapabilityChanged(_arg022);
                            break;
                        case 23:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            onActiveDataSubIdChanged(_arg023);
                            break;
                        case 24:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            onRadioPowerStateChanged(_arg024);
                            break;
                        case 25:
                            List<CallState> _arg025 = data.createTypedArrayList(CallState.CREATOR);
                            data.enforceNoDataAvail();
                            onCallStatesChanged(_arg025);
                            break;
                        case 26:
                            ClassLoader cl = getClass().getClassLoader();
                            Map _arg026 = data.readHashMap(cl);
                            data.enforceNoDataAvail();
                            onEmergencyNumberListChanged(_arg026);
                            break;
                        case 27:
                            EmergencyNumber _arg027 = (EmergencyNumber) data.readTypedObject(EmergencyNumber.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onOutgoingEmergencyCall(_arg027, _arg13);
                            break;
                        case 28:
                            EmergencyNumber _arg028 = (EmergencyNumber) data.readTypedObject(EmergencyNumber.CREATOR);
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            onOutgoingEmergencySms(_arg028, _arg14);
                            break;
                        case 29:
                            int _arg029 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallDisconnectCauseChanged(_arg029, _arg15);
                            break;
                        case 30:
                            ImsReasonInfo _arg030 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onImsCallDisconnectCauseChanged(_arg030);
                            break;
                        case 31:
                            CellIdentity _arg031 = (CellIdentity) data.readTypedObject(CellIdentity.CREATOR);
                            String _arg16 = data.readString();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            onRegistrationFailed(_arg031, _arg16, _arg2, _arg3, _arg4);
                            break;
                        case 32:
                            BarringInfo _arg032 = (BarringInfo) data.readTypedObject(BarringInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onBarringInfoChanged(_arg032);
                            break;
                        case 33:
                            List<PhysicalChannelConfig> _arg033 = data.createTypedArrayList(PhysicalChannelConfig.CREATOR);
                            data.enforceNoDataAvail();
                            onPhysicalChannelConfigChanged(_arg033);
                            break;
                        case 34:
                            boolean _arg034 = data.readBoolean();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            onDataEnabledChanged(_arg034, _arg17);
                            break;
                        case 35:
                            int _arg035 = data.readInt();
                            long _arg18 = data.readLong();
                            data.enforceNoDataAvail();
                            onAllowedNetworkTypesChanged(_arg035, _arg18);
                            break;
                        case 36:
                            List<LinkCapacityEstimate> _arg036 = data.createTypedArrayList(LinkCapacityEstimate.CREATOR);
                            data.enforceNoDataAvail();
                            onLinkCapacityEstimateChanged(_arg036);
                            break;
                        case 37:
                            MediaQualityStatus _arg037 = (MediaQualityStatus) data.readTypedObject(MediaQualityStatus.CREATOR);
                            data.enforceNoDataAvail();
                            onMediaQualityStatusChanged(_arg037);
                            break;
                        case 38:
                            int _arg038 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallBackModeStarted(_arg038);
                            break;
                        case 39:
                            int _arg039 = data.readInt();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            onCallBackModeStopped(_arg039, _arg19);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPhoneStateListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onServiceStateChanged(ServiceState serviceState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(serviceState, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onSignalStrengthChanged(int asu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(asu);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onMessageWaitingIndicatorChanged(boolean mwi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(mwi);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallForwardingIndicatorChanged(boolean cfi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(cfi);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCellLocationChanged(CellIdentity location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onLegacyCallStateChanged(int state, String incomingNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeString(incomingNumber);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataConnectionStateChanged(int state, int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(networkType);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataActivity(int direction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onSignalStrengthsChanged(SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(signalStrength, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCellInfoChanged(List<CellInfo> cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(cellInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onPreciseCallStateChanged(PreciseCallState callState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(callState, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState dataConnectionState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(dataConnectionState, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo dcRtInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(dcRtInfo, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onSrvccStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onVoiceActivationStateChanged(int activationState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(activationState);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataActivationStateChanged(int activationState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(activationState);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onOemHookRawEvent(byte[] rawData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(rawData);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCarrierNetworkChange(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onUserMobileDataStateChanged(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(telephonyDisplayInfo, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onPhoneCapabilityChanged(PhoneCapability capability) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(capability, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onActiveDataSubIdChanged(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onRadioPowerStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallStatesChanged(List<CallState> callStateList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(callStateList, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onEmergencyNumberListChanged(Map emergencyNumberList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeMap(emergencyNumberList);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onOutgoingEmergencyCall(EmergencyNumber placedEmergencyNumber, int subscriptionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(placedEmergencyNumber, 0);
                    _data.writeInt(subscriptionId);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onOutgoingEmergencySms(EmergencyNumber sentEmergencyNumber, int subscriptionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sentEmergencyNumber, 0);
                    _data.writeInt(subscriptionId);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallDisconnectCauseChanged(int disconnectCause, int preciseDisconnectCause) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(disconnectCause);
                    _data.writeInt(preciseDisconnectCause);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onImsCallDisconnectCauseChanged(ImsReasonInfo imsReasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(imsReasonInfo, 0);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onRegistrationFailed(CellIdentity cellIdentity, String chosenPlmn, int domain, int causeCode, int additionalCauseCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(cellIdentity, 0);
                    _data.writeString(chosenPlmn);
                    _data.writeInt(domain);
                    _data.writeInt(causeCode);
                    _data.writeInt(additionalCauseCode);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onBarringInfoChanged(BarringInfo barringInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(barringInfo, 0);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onPhysicalChannelConfigChanged(List<PhysicalChannelConfig> configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(configs, 0);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataEnabledChanged(boolean enabled, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(reason);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onAllowedNetworkTypesChanged(int reason, long allowedNetworkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    _data.writeLong(allowedNetworkType);
                    this.mRemote.transact(35, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onLinkCapacityEstimateChanged(List<LinkCapacityEstimate> linkCapacityEstimateList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(linkCapacityEstimateList, 0);
                    this.mRemote.transact(36, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onMediaQualityStatusChanged(MediaQualityStatus mediaQualityStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(mediaQualityStatus, 0);
                    this.mRemote.transact(37, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallBackModeStarted(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    this.mRemote.transact(38, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallBackModeStopped(int type, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(reason);
                    this.mRemote.transact(39, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 38;
        }
    }
}
