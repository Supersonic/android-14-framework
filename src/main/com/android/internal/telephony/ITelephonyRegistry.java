package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.BarringInfo;
import android.telephony.CallQuality;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.LinkCapacityEstimate;
import android.telephony.PhoneCapability;
import android.telephony.PhysicalChannelConfig;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import com.android.internal.telephony.ICarrierConfigChangeListener;
import com.android.internal.telephony.ICarrierPrivilegesCallback;
import com.android.internal.telephony.IOnSubscriptionsChangedListener;
import com.android.internal.telephony.IPhoneStateListener;
import java.util.List;
/* loaded from: classes3.dex */
public interface ITelephonyRegistry extends IInterface {
    void addCarrierConfigChangeListener(ICarrierConfigChangeListener iCarrierConfigChangeListener, String str, String str2) throws RemoteException;

    void addCarrierPrivilegesCallback(int i, ICarrierPrivilegesCallback iCarrierPrivilegesCallback, String str, String str2) throws RemoteException;

    void addOnOpportunisticSubscriptionsChangedListener(String str, String str2, IOnSubscriptionsChangedListener iOnSubscriptionsChangedListener) throws RemoteException;

    void addOnSubscriptionsChangedListener(String str, String str2, IOnSubscriptionsChangedListener iOnSubscriptionsChangedListener) throws RemoteException;

    void listenWithEventList(boolean z, boolean z2, int i, String str, String str2, IPhoneStateListener iPhoneStateListener, int[] iArr, boolean z3) throws RemoteException;

    void notifyActiveDataSubIdChanged(int i) throws RemoteException;

    void notifyAllowedNetworkTypesChanged(int i, int i2, int i3, long j) throws RemoteException;

    void notifyBarringInfoChanged(int i, int i2, BarringInfo barringInfo) throws RemoteException;

    void notifyCallForwardingChanged(boolean z) throws RemoteException;

    void notifyCallForwardingChangedForSubscriber(int i, boolean z) throws RemoteException;

    void notifyCallQualityChanged(CallQuality callQuality, int i, int i2, int i3) throws RemoteException;

    void notifyCallState(int i, int i2, int i3, String str) throws RemoteException;

    void notifyCallStateForAllSubs(int i, String str) throws RemoteException;

    void notifyCallbackModeStarted(int i, int i2, int i3) throws RemoteException;

    void notifyCallbackModeStopped(int i, int i2, int i3, int i4) throws RemoteException;

    void notifyCarrierConfigChanged(int i, int i2, int i3, int i4) throws RemoteException;

    void notifyCarrierNetworkChange(boolean z) throws RemoteException;

    void notifyCarrierNetworkChangeWithSubId(int i, boolean z) throws RemoteException;

    void notifyCarrierPrivilegesChanged(int i, List<String> list, int[] iArr) throws RemoteException;

    void notifyCarrierServiceChanged(int i, String str, int i2) throws RemoteException;

    void notifyCellInfo(List<CellInfo> list) throws RemoteException;

    void notifyCellInfoForSubscriber(int i, List<CellInfo> list) throws RemoteException;

    void notifyCellLocationForSubscriber(int i, CellIdentity cellIdentity) throws RemoteException;

    void notifyDataActivity(int i) throws RemoteException;

    void notifyDataActivityForSubscriber(int i, int i2) throws RemoteException;

    void notifyDataConnectionForSubscriber(int i, int i2, PreciseDataConnectionState preciseDataConnectionState) throws RemoteException;

    void notifyDataEnabled(int i, int i2, boolean z, int i3) throws RemoteException;

    void notifyDisconnectCause(int i, int i2, int i3, int i4) throws RemoteException;

    void notifyDisplayInfoChanged(int i, int i2, TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException;

    void notifyEmergencyNumberList(int i, int i2) throws RemoteException;

    void notifyImsDisconnectCause(int i, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void notifyLinkCapacityEstimateChanged(int i, int i2, List<LinkCapacityEstimate> list) throws RemoteException;

    void notifyMediaQualityStatusChanged(int i, int i2, MediaQualityStatus mediaQualityStatus) throws RemoteException;

    void notifyMessageWaitingChangedForPhoneId(int i, int i2, boolean z) throws RemoteException;

    void notifyOemHookRawEventForSubscriber(int i, int i2, byte[] bArr) throws RemoteException;

    void notifyOpportunisticSubscriptionInfoChanged() throws RemoteException;

    void notifyOutgoingEmergencyCall(int i, int i2, EmergencyNumber emergencyNumber) throws RemoteException;

    void notifyOutgoingEmergencySms(int i, int i2, EmergencyNumber emergencyNumber) throws RemoteException;

    void notifyPhoneCapabilityChanged(PhoneCapability phoneCapability) throws RemoteException;

    void notifyPhysicalChannelConfigForSubscriber(int i, int i2, List<PhysicalChannelConfig> list) throws RemoteException;

    void notifyPreciseCallState(int i, int i2, int[] iArr, String[] strArr, int[] iArr2, int[] iArr3) throws RemoteException;

    void notifyRadioPowerStateChanged(int i, int i2, int i3) throws RemoteException;

    void notifyRegistrationFailed(int i, int i2, CellIdentity cellIdentity, String str, int i3, int i4, int i5) throws RemoteException;

    void notifyServiceStateForPhoneId(int i, int i2, ServiceState serviceState) throws RemoteException;

    void notifySignalStrengthForPhoneId(int i, int i2, SignalStrength signalStrength) throws RemoteException;

    void notifySimActivationStateChangedForPhoneId(int i, int i2, int i3, int i4) throws RemoteException;

    void notifySrvccStateChanged(int i, int i2) throws RemoteException;

    void notifySubscriptionInfoChanged() throws RemoteException;

    void notifyUserMobileDataStateChangedForPhoneId(int i, int i2, boolean z) throws RemoteException;

    void removeCarrierConfigChangeListener(ICarrierConfigChangeListener iCarrierConfigChangeListener, String str) throws RemoteException;

    void removeCarrierPrivilegesCallback(ICarrierPrivilegesCallback iCarrierPrivilegesCallback, String str) throws RemoteException;

    void removeOnSubscriptionsChangedListener(String str, IOnSubscriptionsChangedListener iOnSubscriptionsChangedListener) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITelephonyRegistry {
        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void addOnSubscriptionsChangedListener(String pkg, String featureId, IOnSubscriptionsChangedListener callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void addOnOpportunisticSubscriptionsChangedListener(String pkg, String featureId, IOnSubscriptionsChangedListener callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void removeOnSubscriptionsChangedListener(String pkg, IOnSubscriptionsChangedListener callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void listenWithEventList(boolean renounceFineLocationAccess, boolean renounceCoarseLocationAccess, int subId, String pkg, String featureId, IPhoneStateListener callback, int[] events, boolean notifyNow) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallStateForAllSubs(int state, String incomingNumber) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallState(int phoneId, int subId, int state, String incomingNumber) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyServiceStateForPhoneId(int phoneId, int subId, ServiceState state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifySignalStrengthForPhoneId(int phoneId, int subId, SignalStrength signalStrength) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyMessageWaitingChangedForPhoneId(int phoneId, int subId, boolean mwi) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallForwardingChanged(boolean cfi) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallForwardingChangedForSubscriber(int subId, boolean cfi) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDataActivity(int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDataActivityForSubscriber(int subId, int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDataConnectionForSubscriber(int phoneId, int subId, PreciseDataConnectionState preciseState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCellLocationForSubscriber(int subId, CellIdentity cellLocation) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCellInfo(List<CellInfo> cellInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyPreciseCallState(int phoneId, int subId, int[] callStates, String[] imsCallIds, int[] imsCallServiceTypes, int[] imsCallTypes) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDisconnectCause(int phoneId, int subId, int disconnectCause, int preciseDisconnectCause) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCellInfoForSubscriber(int subId, List<CellInfo> cellInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifySrvccStateChanged(int subId, int lteState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifySimActivationStateChangedForPhoneId(int phoneId, int subId, int activationState, int activationType) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyOemHookRawEventForSubscriber(int phoneId, int subId, byte[] rawData) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifySubscriptionInfoChanged() throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyOpportunisticSubscriptionInfoChanged() throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCarrierNetworkChange(boolean active) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCarrierNetworkChangeWithSubId(int subId, boolean active) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyUserMobileDataStateChangedForPhoneId(int phoneId, int subId, boolean state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDisplayInfoChanged(int slotIndex, int subId, TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyPhoneCapabilityChanged(PhoneCapability capability) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyActiveDataSubIdChanged(int activeDataSubId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyRadioPowerStateChanged(int phoneId, int subId, int state) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyEmergencyNumberList(int phoneId, int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyOutgoingEmergencyCall(int phoneId, int subId, EmergencyNumber emergencyNumber) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyOutgoingEmergencySms(int phoneId, int subId, EmergencyNumber emergencyNumber) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallQualityChanged(CallQuality callQuality, int phoneId, int subId, int callNetworkType) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyMediaQualityStatusChanged(int phoneId, int subId, MediaQualityStatus status) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyImsDisconnectCause(int subId, ImsReasonInfo imsReasonInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyRegistrationFailed(int slotIndex, int subId, CellIdentity cellIdentity, String chosenPlmn, int domain, int causeCode, int additionalCauseCode) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyBarringInfoChanged(int slotIndex, int subId, BarringInfo barringInfo) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyPhysicalChannelConfigForSubscriber(int phoneId, int subId, List<PhysicalChannelConfig> configs) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyDataEnabled(int phoneId, int subId, boolean enabled, int reason) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyAllowedNetworkTypesChanged(int phoneId, int subId, int reason, long allowedNetworkType) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyLinkCapacityEstimateChanged(int phoneId, int subId, List<LinkCapacityEstimate> linkCapacityEstimateList) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void addCarrierPrivilegesCallback(int phoneId, ICarrierPrivilegesCallback callback, String pkg, String featureId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void removeCarrierPrivilegesCallback(ICarrierPrivilegesCallback callback, String pkg) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCarrierPrivilegesChanged(int phoneId, List<String> privilegedPackageNames, int[] privilegedUids) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCarrierServiceChanged(int phoneId, String packageName, int uid) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void addCarrierConfigChangeListener(ICarrierConfigChangeListener listener, String pkg, String featureId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void removeCarrierConfigChangeListener(ICarrierConfigChangeListener listener, String pkg) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCarrierConfigChanged(int phoneId, int subId, int carrierId, int specificCarrierId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallbackModeStarted(int phoneId, int subId, int type) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ITelephonyRegistry
        public void notifyCallbackModeStopped(int phoneId, int subId, int type, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITelephonyRegistry {
        public static final String DESCRIPTOR = "com.android.internal.telephony.ITelephonyRegistry";
        static final int TRANSACTION_addCarrierConfigChangeListener = 48;
        static final int TRANSACTION_addCarrierPrivilegesCallback = 44;
        static final int TRANSACTION_addOnOpportunisticSubscriptionsChangedListener = 2;
        static final int TRANSACTION_addOnSubscriptionsChangedListener = 1;
        static final int TRANSACTION_listenWithEventList = 4;
        static final int TRANSACTION_notifyActiveDataSubIdChanged = 30;
        static final int TRANSACTION_notifyAllowedNetworkTypesChanged = 42;
        static final int TRANSACTION_notifyBarringInfoChanged = 39;
        static final int TRANSACTION_notifyCallForwardingChanged = 10;
        static final int TRANSACTION_notifyCallForwardingChangedForSubscriber = 11;
        static final int TRANSACTION_notifyCallQualityChanged = 35;
        static final int TRANSACTION_notifyCallState = 6;
        static final int TRANSACTION_notifyCallStateForAllSubs = 5;
        static final int TRANSACTION_notifyCallbackModeStarted = 51;
        static final int TRANSACTION_notifyCallbackModeStopped = 52;
        static final int TRANSACTION_notifyCarrierConfigChanged = 50;
        static final int TRANSACTION_notifyCarrierNetworkChange = 25;
        static final int TRANSACTION_notifyCarrierNetworkChangeWithSubId = 26;
        static final int TRANSACTION_notifyCarrierPrivilegesChanged = 46;
        static final int TRANSACTION_notifyCarrierServiceChanged = 47;
        static final int TRANSACTION_notifyCellInfo = 16;
        static final int TRANSACTION_notifyCellInfoForSubscriber = 19;
        static final int TRANSACTION_notifyCellLocationForSubscriber = 15;
        static final int TRANSACTION_notifyDataActivity = 12;
        static final int TRANSACTION_notifyDataActivityForSubscriber = 13;
        static final int TRANSACTION_notifyDataConnectionForSubscriber = 14;
        static final int TRANSACTION_notifyDataEnabled = 41;
        static final int TRANSACTION_notifyDisconnectCause = 18;
        static final int TRANSACTION_notifyDisplayInfoChanged = 28;
        static final int TRANSACTION_notifyEmergencyNumberList = 32;
        static final int TRANSACTION_notifyImsDisconnectCause = 37;
        static final int TRANSACTION_notifyLinkCapacityEstimateChanged = 43;
        static final int TRANSACTION_notifyMediaQualityStatusChanged = 36;
        static final int TRANSACTION_notifyMessageWaitingChangedForPhoneId = 9;
        static final int TRANSACTION_notifyOemHookRawEventForSubscriber = 22;
        static final int TRANSACTION_notifyOpportunisticSubscriptionInfoChanged = 24;
        static final int TRANSACTION_notifyOutgoingEmergencyCall = 33;
        static final int TRANSACTION_notifyOutgoingEmergencySms = 34;
        static final int TRANSACTION_notifyPhoneCapabilityChanged = 29;
        static final int TRANSACTION_notifyPhysicalChannelConfigForSubscriber = 40;
        static final int TRANSACTION_notifyPreciseCallState = 17;
        static final int TRANSACTION_notifyRadioPowerStateChanged = 31;
        static final int TRANSACTION_notifyRegistrationFailed = 38;
        static final int TRANSACTION_notifyServiceStateForPhoneId = 7;
        static final int TRANSACTION_notifySignalStrengthForPhoneId = 8;
        static final int TRANSACTION_notifySimActivationStateChangedForPhoneId = 21;
        static final int TRANSACTION_notifySrvccStateChanged = 20;
        static final int TRANSACTION_notifySubscriptionInfoChanged = 23;
        static final int TRANSACTION_notifyUserMobileDataStateChangedForPhoneId = 27;
        static final int TRANSACTION_removeCarrierConfigChangeListener = 49;
        static final int TRANSACTION_removeCarrierPrivilegesCallback = 45;
        static final int TRANSACTION_removeOnSubscriptionsChangedListener = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITelephonyRegistry asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITelephonyRegistry)) {
                return (ITelephonyRegistry) iin;
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
                    return "addOnSubscriptionsChangedListener";
                case 2:
                    return "addOnOpportunisticSubscriptionsChangedListener";
                case 3:
                    return "removeOnSubscriptionsChangedListener";
                case 4:
                    return "listenWithEventList";
                case 5:
                    return "notifyCallStateForAllSubs";
                case 6:
                    return "notifyCallState";
                case 7:
                    return "notifyServiceStateForPhoneId";
                case 8:
                    return "notifySignalStrengthForPhoneId";
                case 9:
                    return "notifyMessageWaitingChangedForPhoneId";
                case 10:
                    return "notifyCallForwardingChanged";
                case 11:
                    return "notifyCallForwardingChangedForSubscriber";
                case 12:
                    return "notifyDataActivity";
                case 13:
                    return "notifyDataActivityForSubscriber";
                case 14:
                    return "notifyDataConnectionForSubscriber";
                case 15:
                    return "notifyCellLocationForSubscriber";
                case 16:
                    return "notifyCellInfo";
                case 17:
                    return "notifyPreciseCallState";
                case 18:
                    return "notifyDisconnectCause";
                case 19:
                    return "notifyCellInfoForSubscriber";
                case 20:
                    return "notifySrvccStateChanged";
                case 21:
                    return "notifySimActivationStateChangedForPhoneId";
                case 22:
                    return "notifyOemHookRawEventForSubscriber";
                case 23:
                    return "notifySubscriptionInfoChanged";
                case 24:
                    return "notifyOpportunisticSubscriptionInfoChanged";
                case 25:
                    return "notifyCarrierNetworkChange";
                case 26:
                    return "notifyCarrierNetworkChangeWithSubId";
                case 27:
                    return "notifyUserMobileDataStateChangedForPhoneId";
                case 28:
                    return "notifyDisplayInfoChanged";
                case 29:
                    return "notifyPhoneCapabilityChanged";
                case 30:
                    return "notifyActiveDataSubIdChanged";
                case 31:
                    return "notifyRadioPowerStateChanged";
                case 32:
                    return "notifyEmergencyNumberList";
                case 33:
                    return "notifyOutgoingEmergencyCall";
                case 34:
                    return "notifyOutgoingEmergencySms";
                case 35:
                    return "notifyCallQualityChanged";
                case 36:
                    return "notifyMediaQualityStatusChanged";
                case 37:
                    return "notifyImsDisconnectCause";
                case 38:
                    return "notifyRegistrationFailed";
                case 39:
                    return "notifyBarringInfoChanged";
                case 40:
                    return "notifyPhysicalChannelConfigForSubscriber";
                case 41:
                    return "notifyDataEnabled";
                case 42:
                    return "notifyAllowedNetworkTypesChanged";
                case 43:
                    return "notifyLinkCapacityEstimateChanged";
                case 44:
                    return "addCarrierPrivilegesCallback";
                case 45:
                    return "removeCarrierPrivilegesCallback";
                case 46:
                    return "notifyCarrierPrivilegesChanged";
                case 47:
                    return "notifyCarrierServiceChanged";
                case 48:
                    return "addCarrierConfigChangeListener";
                case 49:
                    return "removeCarrierConfigChangeListener";
                case 50:
                    return "notifyCarrierConfigChanged";
                case 51:
                    return "notifyCallbackModeStarted";
                case 52:
                    return "notifyCallbackModeStopped";
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
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            IOnSubscriptionsChangedListener _arg2 = IOnSubscriptionsChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnSubscriptionsChangedListener(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg12 = data.readString();
                            IOnSubscriptionsChangedListener _arg22 = IOnSubscriptionsChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnOpportunisticSubscriptionsChangedListener(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            IOnSubscriptionsChangedListener _arg13 = IOnSubscriptionsChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnSubscriptionsChangedListener(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            boolean _arg14 = data.readBoolean();
                            int _arg23 = data.readInt();
                            String _arg3 = data.readString();
                            String _arg4 = data.readString();
                            IPhoneStateListener _arg5 = IPhoneStateListener.Stub.asInterface(data.readStrongBinder());
                            int[] _arg6 = data.createIntArray();
                            boolean _arg7 = data.readBoolean();
                            data.enforceNoDataAvail();
                            listenWithEventList(_arg04, _arg14, _arg23, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            notifyCallStateForAllSubs(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg16 = data.readInt();
                            int _arg24 = data.readInt();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            notifyCallState(_arg06, _arg16, _arg24, _arg32);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg17 = data.readInt();
                            ServiceState _arg25 = (ServiceState) data.readTypedObject(ServiceState.CREATOR);
                            data.enforceNoDataAvail();
                            notifyServiceStateForPhoneId(_arg07, _arg17, _arg25);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg18 = data.readInt();
                            SignalStrength _arg26 = (SignalStrength) data.readTypedObject(SignalStrength.CREATOR);
                            data.enforceNoDataAvail();
                            notifySignalStrengthForPhoneId(_arg08, _arg18, _arg26);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg19 = data.readInt();
                            boolean _arg27 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyMessageWaitingChangedForPhoneId(_arg09, _arg19, _arg27);
                            reply.writeNoException();
                            break;
                        case 10:
                            boolean _arg010 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyCallForwardingChanged(_arg010);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            boolean _arg110 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyCallForwardingChangedForSubscriber(_arg011, _arg110);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyDataActivity(_arg012);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyDataActivityForSubscriber(_arg013, _arg111);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            int _arg112 = data.readInt();
                            PreciseDataConnectionState _arg28 = (PreciseDataConnectionState) data.readTypedObject(PreciseDataConnectionState.CREATOR);
                            data.enforceNoDataAvail();
                            notifyDataConnectionForSubscriber(_arg014, _arg112, _arg28);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            CellIdentity _arg113 = (CellIdentity) data.readTypedObject(CellIdentity.CREATOR);
                            data.enforceNoDataAvail();
                            notifyCellLocationForSubscriber(_arg015, _arg113);
                            reply.writeNoException();
                            break;
                        case 16:
                            List<CellInfo> _arg016 = data.createTypedArrayList(CellInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyCellInfo(_arg016);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            int _arg114 = data.readInt();
                            int[] _arg29 = data.createIntArray();
                            String[] _arg33 = data.createStringArray();
                            int[] _arg42 = data.createIntArray();
                            int[] _arg52 = data.createIntArray();
                            data.enforceNoDataAvail();
                            notifyPreciseCallState(_arg017, _arg114, _arg29, _arg33, _arg42, _arg52);
                            reply.writeNoException();
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            int _arg115 = data.readInt();
                            int _arg210 = data.readInt();
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyDisconnectCause(_arg018, _arg115, _arg210, _arg34);
                            reply.writeNoException();
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            List<CellInfo> _arg116 = data.createTypedArrayList(CellInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyCellInfoForSubscriber(_arg019, _arg116);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            notifySrvccStateChanged(_arg020, _arg117);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            int _arg118 = data.readInt();
                            int _arg211 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            notifySimActivationStateChangedForPhoneId(_arg021, _arg118, _arg211, _arg35);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            int _arg119 = data.readInt();
                            byte[] _arg212 = data.createByteArray();
                            data.enforceNoDataAvail();
                            notifyOemHookRawEventForSubscriber(_arg022, _arg119, _arg212);
                            reply.writeNoException();
                            break;
                        case 23:
                            notifySubscriptionInfoChanged();
                            reply.writeNoException();
                            break;
                        case 24:
                            notifyOpportunisticSubscriptionInfoChanged();
                            reply.writeNoException();
                            break;
                        case 25:
                            boolean _arg023 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyCarrierNetworkChange(_arg023);
                            reply.writeNoException();
                            break;
                        case 26:
                            int _arg024 = data.readInt();
                            boolean _arg120 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyCarrierNetworkChangeWithSubId(_arg024, _arg120);
                            reply.writeNoException();
                            break;
                        case 27:
                            int _arg025 = data.readInt();
                            int _arg121 = data.readInt();
                            boolean _arg213 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyUserMobileDataStateChangedForPhoneId(_arg025, _arg121, _arg213);
                            reply.writeNoException();
                            break;
                        case 28:
                            int _arg026 = data.readInt();
                            int _arg122 = data.readInt();
                            TelephonyDisplayInfo _arg214 = (TelephonyDisplayInfo) data.readTypedObject(TelephonyDisplayInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyDisplayInfoChanged(_arg026, _arg122, _arg214);
                            reply.writeNoException();
                            break;
                        case 29:
                            PhoneCapability _arg027 = (PhoneCapability) data.readTypedObject(PhoneCapability.CREATOR);
                            data.enforceNoDataAvail();
                            notifyPhoneCapabilityChanged(_arg027);
                            reply.writeNoException();
                            break;
                        case 30:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyActiveDataSubIdChanged(_arg028);
                            reply.writeNoException();
                            break;
                        case 31:
                            int _arg029 = data.readInt();
                            int _arg123 = data.readInt();
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRadioPowerStateChanged(_arg029, _arg123, _arg215);
                            reply.writeNoException();
                            break;
                        case 32:
                            int _arg030 = data.readInt();
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyEmergencyNumberList(_arg030, _arg124);
                            reply.writeNoException();
                            break;
                        case 33:
                            int _arg031 = data.readInt();
                            int _arg125 = data.readInt();
                            EmergencyNumber _arg216 = (EmergencyNumber) data.readTypedObject(EmergencyNumber.CREATOR);
                            data.enforceNoDataAvail();
                            notifyOutgoingEmergencyCall(_arg031, _arg125, _arg216);
                            reply.writeNoException();
                            break;
                        case 34:
                            int _arg032 = data.readInt();
                            int _arg126 = data.readInt();
                            EmergencyNumber _arg217 = (EmergencyNumber) data.readTypedObject(EmergencyNumber.CREATOR);
                            data.enforceNoDataAvail();
                            notifyOutgoingEmergencySms(_arg032, _arg126, _arg217);
                            reply.writeNoException();
                            break;
                        case 35:
                            CallQuality _arg033 = (CallQuality) data.readTypedObject(CallQuality.CREATOR);
                            int _arg127 = data.readInt();
                            int _arg218 = data.readInt();
                            int _arg36 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyCallQualityChanged(_arg033, _arg127, _arg218, _arg36);
                            reply.writeNoException();
                            break;
                        case 36:
                            int _arg034 = data.readInt();
                            int _arg128 = data.readInt();
                            MediaQualityStatus _arg219 = (MediaQualityStatus) data.readTypedObject(MediaQualityStatus.CREATOR);
                            data.enforceNoDataAvail();
                            notifyMediaQualityStatusChanged(_arg034, _arg128, _arg219);
                            reply.writeNoException();
                            break;
                        case 37:
                            int _arg035 = data.readInt();
                            ImsReasonInfo _arg129 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyImsDisconnectCause(_arg035, _arg129);
                            reply.writeNoException();
                            break;
                        case 38:
                            int _arg036 = data.readInt();
                            int _arg130 = data.readInt();
                            CellIdentity _arg220 = (CellIdentity) data.readTypedObject(CellIdentity.CREATOR);
                            String _arg37 = data.readString();
                            int _arg43 = data.readInt();
                            int _arg53 = data.readInt();
                            int _arg62 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRegistrationFailed(_arg036, _arg130, _arg220, _arg37, _arg43, _arg53, _arg62);
                            reply.writeNoException();
                            break;
                        case 39:
                            int _arg037 = data.readInt();
                            int _arg131 = data.readInt();
                            BarringInfo _arg221 = (BarringInfo) data.readTypedObject(BarringInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyBarringInfoChanged(_arg037, _arg131, _arg221);
                            reply.writeNoException();
                            break;
                        case 40:
                            int _arg038 = data.readInt();
                            int _arg132 = data.readInt();
                            List<PhysicalChannelConfig> _arg222 = data.createTypedArrayList(PhysicalChannelConfig.CREATOR);
                            data.enforceNoDataAvail();
                            notifyPhysicalChannelConfigForSubscriber(_arg038, _arg132, _arg222);
                            reply.writeNoException();
                            break;
                        case 41:
                            int _arg039 = data.readInt();
                            int _arg133 = data.readInt();
                            boolean _arg223 = data.readBoolean();
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyDataEnabled(_arg039, _arg133, _arg223, _arg38);
                            reply.writeNoException();
                            break;
                        case 42:
                            int _arg040 = data.readInt();
                            int _arg134 = data.readInt();
                            int _arg224 = data.readInt();
                            long _arg39 = data.readLong();
                            data.enforceNoDataAvail();
                            notifyAllowedNetworkTypesChanged(_arg040, _arg134, _arg224, _arg39);
                            reply.writeNoException();
                            break;
                        case 43:
                            int _arg041 = data.readInt();
                            int _arg135 = data.readInt();
                            List<LinkCapacityEstimate> _arg225 = data.createTypedArrayList(LinkCapacityEstimate.CREATOR);
                            data.enforceNoDataAvail();
                            notifyLinkCapacityEstimateChanged(_arg041, _arg135, _arg225);
                            reply.writeNoException();
                            break;
                        case 44:
                            int _arg042 = data.readInt();
                            ICarrierPrivilegesCallback _arg136 = ICarrierPrivilegesCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg226 = data.readString();
                            String _arg310 = data.readString();
                            data.enforceNoDataAvail();
                            addCarrierPrivilegesCallback(_arg042, _arg136, _arg226, _arg310);
                            reply.writeNoException();
                            break;
                        case 45:
                            ICarrierPrivilegesCallback _arg043 = ICarrierPrivilegesCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg137 = data.readString();
                            data.enforceNoDataAvail();
                            removeCarrierPrivilegesCallback(_arg043, _arg137);
                            reply.writeNoException();
                            break;
                        case 46:
                            int _arg044 = data.readInt();
                            List<String> _arg138 = data.createStringArrayList();
                            int[] _arg227 = data.createIntArray();
                            data.enforceNoDataAvail();
                            notifyCarrierPrivilegesChanged(_arg044, _arg138, _arg227);
                            reply.writeNoException();
                            break;
                        case 47:
                            int _arg045 = data.readInt();
                            String _arg139 = data.readString();
                            int _arg228 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyCarrierServiceChanged(_arg045, _arg139, _arg228);
                            reply.writeNoException();
                            break;
                        case 48:
                            ICarrierConfigChangeListener _arg046 = ICarrierConfigChangeListener.Stub.asInterface(data.readStrongBinder());
                            String _arg140 = data.readString();
                            String _arg229 = data.readString();
                            data.enforceNoDataAvail();
                            addCarrierConfigChangeListener(_arg046, _arg140, _arg229);
                            reply.writeNoException();
                            break;
                        case 49:
                            ICarrierConfigChangeListener _arg047 = ICarrierConfigChangeListener.Stub.asInterface(data.readStrongBinder());
                            String _arg141 = data.readString();
                            data.enforceNoDataAvail();
                            removeCarrierConfigChangeListener(_arg047, _arg141);
                            reply.writeNoException();
                            break;
                        case 50:
                            int _arg048 = data.readInt();
                            int _arg142 = data.readInt();
                            int _arg230 = data.readInt();
                            int _arg311 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyCarrierConfigChanged(_arg048, _arg142, _arg230, _arg311);
                            reply.writeNoException();
                            break;
                        case 51:
                            int _arg049 = data.readInt();
                            int _arg143 = data.readInt();
                            int _arg231 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyCallbackModeStarted(_arg049, _arg143, _arg231);
                            reply.writeNoException();
                            break;
                        case 52:
                            int _arg050 = data.readInt();
                            int _arg144 = data.readInt();
                            int _arg232 = data.readInt();
                            int _arg312 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyCallbackModeStopped(_arg050, _arg144, _arg232, _arg312);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ITelephonyRegistry {
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void addOnSubscriptionsChangedListener(String pkg, String featureId, IOnSubscriptionsChangedListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(featureId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void addOnOpportunisticSubscriptionsChangedListener(String pkg, String featureId, IOnSubscriptionsChangedListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeString(featureId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void removeOnSubscriptionsChangedListener(String pkg, IOnSubscriptionsChangedListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void listenWithEventList(boolean renounceFineLocationAccess, boolean renounceCoarseLocationAccess, int subId, String pkg, String featureId, IPhoneStateListener callback, int[] events, boolean notifyNow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(renounceFineLocationAccess);
                    _data.writeBoolean(renounceCoarseLocationAccess);
                    _data.writeInt(subId);
                    _data.writeString(pkg);
                    _data.writeString(featureId);
                    _data.writeStrongInterface(callback);
                    _data.writeIntArray(events);
                    _data.writeBoolean(notifyNow);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallStateForAllSubs(int state, String incomingNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeString(incomingNumber);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallState(int phoneId, int subId, int state, String incomingNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(state);
                    _data.writeString(incomingNumber);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyServiceStateForPhoneId(int phoneId, int subId, ServiceState state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(state, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifySignalStrengthForPhoneId(int phoneId, int subId, SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(signalStrength, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyMessageWaitingChangedForPhoneId(int phoneId, int subId, boolean mwi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeBoolean(mwi);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallForwardingChanged(boolean cfi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(cfi);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallForwardingChangedForSubscriber(int subId, boolean cfi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeBoolean(cfi);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataActivity(int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataActivityForSubscriber(int subId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(state);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataConnectionForSubscriber(int phoneId, int subId, PreciseDataConnectionState preciseState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(preciseState, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCellLocationForSubscriber(int subId, CellIdentity cellLocation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedObject(cellLocation, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCellInfo(List<CellInfo> cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(cellInfo, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyPreciseCallState(int phoneId, int subId, int[] callStates, String[] imsCallIds, int[] imsCallServiceTypes, int[] imsCallTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeIntArray(callStates);
                    _data.writeStringArray(imsCallIds);
                    _data.writeIntArray(imsCallServiceTypes);
                    _data.writeIntArray(imsCallTypes);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDisconnectCause(int phoneId, int subId, int disconnectCause, int preciseDisconnectCause) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(disconnectCause);
                    _data.writeInt(preciseDisconnectCause);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCellInfoForSubscriber(int subId, List<CellInfo> cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedList(cellInfo, 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifySrvccStateChanged(int subId, int lteState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(lteState);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifySimActivationStateChangedForPhoneId(int phoneId, int subId, int activationState, int activationType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(activationState);
                    _data.writeInt(activationType);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyOemHookRawEventForSubscriber(int phoneId, int subId, byte[] rawData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeByteArray(rawData);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifySubscriptionInfoChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyOpportunisticSubscriptionInfoChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCarrierNetworkChange(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCarrierNetworkChangeWithSubId(int subId, boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeBoolean(active);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyUserMobileDataStateChangedForPhoneId(int phoneId, int subId, boolean state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeBoolean(state);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDisplayInfoChanged(int slotIndex, int subId, TelephonyDisplayInfo telephonyDisplayInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeInt(subId);
                    _data.writeTypedObject(telephonyDisplayInfo, 0);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyPhoneCapabilityChanged(PhoneCapability capability) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(capability, 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyActiveDataSubIdChanged(int activeDataSubId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(activeDataSubId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyRadioPowerStateChanged(int phoneId, int subId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(state);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyEmergencyNumberList(int phoneId, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyOutgoingEmergencyCall(int phoneId, int subId, EmergencyNumber emergencyNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(emergencyNumber, 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyOutgoingEmergencySms(int phoneId, int subId, EmergencyNumber emergencyNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(emergencyNumber, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallQualityChanged(CallQuality callQuality, int phoneId, int subId, int callNetworkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(callQuality, 0);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(callNetworkType);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyMediaQualityStatusChanged(int phoneId, int subId, MediaQualityStatus status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedObject(status, 0);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyImsDisconnectCause(int subId, ImsReasonInfo imsReasonInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedObject(imsReasonInfo, 0);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyRegistrationFailed(int slotIndex, int subId, CellIdentity cellIdentity, String chosenPlmn, int domain, int causeCode, int additionalCauseCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeInt(subId);
                    _data.writeTypedObject(cellIdentity, 0);
                    _data.writeString(chosenPlmn);
                    _data.writeInt(domain);
                    _data.writeInt(causeCode);
                    _data.writeInt(additionalCauseCode);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyBarringInfoChanged(int slotIndex, int subId, BarringInfo barringInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeInt(subId);
                    _data.writeTypedObject(barringInfo, 0);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyPhysicalChannelConfigForSubscriber(int phoneId, int subId, List<PhysicalChannelConfig> configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedList(configs, 0);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataEnabled(int phoneId, int subId, boolean enabled, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeBoolean(enabled);
                    _data.writeInt(reason);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyAllowedNetworkTypesChanged(int phoneId, int subId, int reason, long allowedNetworkType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(reason);
                    _data.writeLong(allowedNetworkType);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyLinkCapacityEstimateChanged(int phoneId, int subId, List<LinkCapacityEstimate> linkCapacityEstimateList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeTypedList(linkCapacityEstimateList, 0);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void addCarrierPrivilegesCallback(int phoneId, ICarrierPrivilegesCallback callback, String pkg, String featureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeStrongInterface(callback);
                    _data.writeString(pkg);
                    _data.writeString(featureId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void removeCarrierPrivilegesCallback(ICarrierPrivilegesCallback callback, String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(pkg);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCarrierPrivilegesChanged(int phoneId, List<String> privilegedPackageNames, int[] privilegedUids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeStringList(privilegedPackageNames);
                    _data.writeIntArray(privilegedUids);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCarrierServiceChanged(int phoneId, String packageName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void addCarrierConfigChangeListener(ICarrierConfigChangeListener listener, String pkg, String featureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(pkg);
                    _data.writeString(featureId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void removeCarrierConfigChangeListener(ICarrierConfigChangeListener listener, String pkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeString(pkg);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCarrierConfigChanged(int phoneId, int subId, int carrierId, int specificCarrierId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(carrierId);
                    _data.writeInt(specificCarrierId);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallbackModeStarted(int phoneId, int subId, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(type);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallbackModeStopped(int phoneId, int subId, int type, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeInt(subId);
                    _data.writeInt(type);
                    _data.writeInt(reason);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 51;
        }
    }
}
