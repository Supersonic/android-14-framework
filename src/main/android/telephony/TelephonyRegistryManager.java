package android.telephony;

import android.compat.Compatibility;
import android.content.Context;
import android.media.AudioPort$$ExternalSyntheticLambda0;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.listeners.ListenerExecutor;
import com.android.internal.telephony.ICarrierConfigChangeListener;
import com.android.internal.telephony.ICarrierPrivilegesCallback;
import com.android.internal.telephony.IOnSubscriptionsChangedListener;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.util.FunctionalUtils;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class TelephonyRegistryManager {
    private static final long LISTEN_CODE_CHANGE = 147600208;
    public static final int SIM_ACTIVATION_TYPE_DATA = 1;
    public static final int SIM_ACTIVATION_TYPE_VOICE = 0;
    private static final String TAG = "TelephonyRegistryManager";
    private static final WeakHashMap<TelephonyManager.CarrierPrivilegesCallback, WeakReference<CarrierPrivilegesCallbackWrapper>> sCarrierPrivilegeCallbacks = new WeakHashMap<>();
    private static ITelephonyRegistry sRegistry;
    private final Context mContext;
    private final ConcurrentHashMap<SubscriptionManager.OnSubscriptionsChangedListener, IOnSubscriptionsChangedListener> mSubscriptionChangedListenerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SubscriptionManager.OnOpportunisticSubscriptionsChangedListener, IOnSubscriptionsChangedListener> mOpportunisticSubscriptionChangedListenerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<CarrierConfigManager.CarrierConfigChangeListener, ICarrierConfigChangeListener> mCarrierConfigChangeListenerMap = new ConcurrentHashMap<>();

    public TelephonyRegistryManager(Context context) {
        this.mContext = context;
        if (sRegistry == null) {
            sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
        }
    }

    public void addOnSubscriptionsChangedListener(SubscriptionManager.OnSubscriptionsChangedListener listener, Executor executor) {
        if (this.mSubscriptionChangedListenerMap.get(listener) != null) {
            Log.m112d(TAG, "addOnSubscriptionsChangedListener listener already present");
            return;
        }
        IOnSubscriptionsChangedListener callback = new BinderC31111(executor, listener);
        this.mSubscriptionChangedListenerMap.put(listener, callback);
        try {
            sRegistry.addOnSubscriptionsChangedListener(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), callback);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.TelephonyRegistryManager$1 */
    /* loaded from: classes3.dex */
    public class BinderC31111 extends IOnSubscriptionsChangedListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ SubscriptionManager.OnSubscriptionsChangedListener val$listener;

        BinderC31111(Executor executor, SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener) {
            this.val$executor = executor;
            this.val$listener = onSubscriptionsChangedListener;
        }

        @Override // com.android.internal.telephony.IOnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.telephony.TelephonyRegistryManager$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SubscriptionManager.OnSubscriptionsChangedListener.this.onSubscriptionsChanged();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void removeOnSubscriptionsChangedListener(SubscriptionManager.OnSubscriptionsChangedListener listener) {
        if (this.mSubscriptionChangedListenerMap.get(listener) == null) {
            return;
        }
        try {
            sRegistry.removeOnSubscriptionsChangedListener(this.mContext.getOpPackageName(), this.mSubscriptionChangedListenerMap.get(listener));
            this.mSubscriptionChangedListenerMap.remove(listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void addOnOpportunisticSubscriptionsChangedListener(SubscriptionManager.OnOpportunisticSubscriptionsChangedListener listener, Executor executor) {
        if (this.mOpportunisticSubscriptionChangedListenerMap.get(listener) != null) {
            Log.m112d(TAG, "addOnOpportunisticSubscriptionsChangedListener listener already present");
            return;
        }
        IOnSubscriptionsChangedListener callback = new BinderC31122(executor, listener);
        this.mOpportunisticSubscriptionChangedListenerMap.put(listener, callback);
        try {
            sRegistry.addOnOpportunisticSubscriptionsChangedListener(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), callback);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.TelephonyRegistryManager$2 */
    /* loaded from: classes3.dex */
    public class BinderC31122 extends IOnSubscriptionsChangedListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ SubscriptionManager.OnOpportunisticSubscriptionsChangedListener val$listener;

        BinderC31122(Executor executor, SubscriptionManager.OnOpportunisticSubscriptionsChangedListener onOpportunisticSubscriptionsChangedListener) {
            this.val$executor = executor;
            this.val$listener = onOpportunisticSubscriptionsChangedListener;
        }

        @Override // com.android.internal.telephony.IOnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            long identity = Binder.clearCallingIdentity();
            try {
                Log.m112d(TelephonyRegistryManager.TAG, "onOpportunisticSubscriptionsChanged callback received.");
                Executor executor = this.val$executor;
                final SubscriptionManager.OnOpportunisticSubscriptionsChangedListener onOpportunisticSubscriptionsChangedListener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.telephony.TelephonyRegistryManager$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SubscriptionManager.OnOpportunisticSubscriptionsChangedListener.this.onOpportunisticSubscriptionsChanged();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void removeOnOpportunisticSubscriptionsChangedListener(SubscriptionManager.OnOpportunisticSubscriptionsChangedListener listener) {
        if (this.mOpportunisticSubscriptionChangedListenerMap.get(listener) == null) {
            return;
        }
        try {
            sRegistry.removeOnSubscriptionsChangedListener(this.mContext.getOpPackageName(), this.mOpportunisticSubscriptionChangedListenerMap.get(listener));
            this.mOpportunisticSubscriptionChangedListenerMap.remove(listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void listenFromListener(int subId, boolean renounceFineLocationAccess, boolean renounceCoarseLocationAccess, String pkg, String featureId, PhoneStateListener listener, int events, boolean notifyNow) {
        int subId2;
        if (listener == null) {
            throw new IllegalStateException("telephony service is null.");
        }
        try {
            int[] eventsList = getEventsFromBitmask(events).stream().mapToInt(new ToIntFunction() { // from class: android.telephony.TelephonyRegistryManager$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int intValue;
                    intValue = ((Integer) obj).intValue();
                    return intValue;
                }
            }).toArray();
            try {
                if (Compatibility.isChangeEnabled((long) LISTEN_CODE_CHANGE)) {
                    listener.mSubId = Integer.valueOf(eventsList.length == 0 ? -1 : subId);
                } else if (listener.mSubId != null) {
                    subId2 = listener.mSubId.intValue();
                    sRegistry.listenWithEventList(renounceFineLocationAccess, renounceCoarseLocationAccess, subId2, pkg, featureId, listener.callback, eventsList, notifyNow);
                    return;
                }
                sRegistry.listenWithEventList(renounceFineLocationAccess, renounceCoarseLocationAccess, subId2, pkg, featureId, listener.callback, eventsList, notifyNow);
                return;
            } catch (RemoteException e) {
                e = e;
                throw e.rethrowFromSystemServer();
            }
            subId2 = subId;
        } catch (RemoteException e2) {
            e = e2;
        }
    }

    private void listenFromCallback(boolean renounceFineLocationAccess, boolean renounceCoarseLocationAccess, int subId, String pkg, String featureId, TelephonyCallback telephonyCallback, int[] events, boolean notifyNow) {
        try {
            try {
                sRegistry.listenWithEventList(renounceFineLocationAccess, renounceCoarseLocationAccess, subId, pkg, featureId, telephonyCallback.callback, events, notifyNow);
            } catch (RemoteException e) {
                e = e;
                throw e.rethrowFromSystemServer();
            }
        } catch (RemoteException e2) {
            e = e2;
        }
    }

    public void notifyCarrierNetworkChange(boolean active) {
        try {
            sRegistry.notifyCarrierNetworkChange(active);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCarrierNetworkChange(int subscriptionId, boolean active) {
        try {
            sRegistry.notifyCarrierNetworkChangeWithSubId(subscriptionId, active);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCallStateChanged(int slotIndex, int subId, int state, String incomingNumber) {
        try {
            sRegistry.notifyCallState(slotIndex, subId, state, incomingNumber);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCallStateChangedForAllSubscriptions(int state, String incomingNumber) {
        try {
            sRegistry.notifyCallStateForAllSubs(state, incomingNumber);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifySubscriptionInfoChanged() {
        try {
            sRegistry.notifySubscriptionInfoChanged();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyOpportunisticSubscriptionInfoChanged() {
        try {
            sRegistry.notifyOpportunisticSubscriptionInfoChanged();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyServiceStateChanged(int slotIndex, int subId, ServiceState state) {
        try {
            sRegistry.notifyServiceStateForPhoneId(slotIndex, subId, state);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifySignalStrengthChanged(int slotIndex, int subId, SignalStrength signalStrength) {
        try {
            sRegistry.notifySignalStrengthForPhoneId(slotIndex, subId, signalStrength);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyMessageWaitingChanged(int slotIndex, int subId, boolean msgWaitingInd) {
        try {
            sRegistry.notifyMessageWaitingChangedForPhoneId(slotIndex, subId, msgWaitingInd);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCallForwardingChanged(int subId, boolean callForwardInd) {
        try {
            sRegistry.notifyCallForwardingChangedForSubscriber(subId, callForwardInd);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDataActivityChanged(int subId, int dataActivityType) {
        try {
            sRegistry.notifyDataActivityForSubscriber(subId, dataActivityType);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDataConnectionForSubscriber(int slotIndex, int subId, PreciseDataConnectionState preciseState) {
        try {
            sRegistry.notifyDataConnectionForSubscriber(slotIndex, subId, preciseState);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCallQualityChanged(int slotIndex, int subId, CallQuality callQuality, int networkType) {
        try {
            sRegistry.notifyCallQualityChanged(callQuality, slotIndex, subId, networkType);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyMediaQualityStatusChanged(int slotIndex, int subId, MediaQualityStatus status) {
        try {
            sRegistry.notifyMediaQualityStatusChanged(slotIndex, subId, status);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyEmergencyNumberList(int slotIndex, int subId) {
        try {
            sRegistry.notifyEmergencyNumberList(slotIndex, subId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyOutgoingEmergencyCall(int phoneId, int subId, EmergencyNumber emergencyNumber) {
        try {
            sRegistry.notifyOutgoingEmergencyCall(phoneId, subId, emergencyNumber);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyOutgoingEmergencySms(int phoneId, int subId, EmergencyNumber emergencyNumber) {
        try {
            sRegistry.notifyOutgoingEmergencySms(phoneId, subId, emergencyNumber);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyRadioPowerStateChanged(int slotIndex, int subId, int radioPowerState) {
        try {
            sRegistry.notifyRadioPowerStateChanged(slotIndex, subId, radioPowerState);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyPhoneCapabilityChanged(PhoneCapability phoneCapability) {
        try {
            sRegistry.notifyPhoneCapabilityChanged(phoneCapability);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDataActivationStateChanged(int slotIndex, int subId, int activationState) {
        try {
            sRegistry.notifySimActivationStateChangedForPhoneId(slotIndex, subId, 1, activationState);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyVoiceActivationStateChanged(int slotIndex, int subId, int activationState) {
        try {
            sRegistry.notifySimActivationStateChangedForPhoneId(slotIndex, subId, 0, activationState);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyUserMobileDataStateChanged(int slotIndex, int subId, boolean state) {
        try {
            sRegistry.notifyUserMobileDataStateChangedForPhoneId(slotIndex, subId, state);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDisplayInfoChanged(int slotIndex, int subscriptionId, TelephonyDisplayInfo telephonyDisplayInfo) {
        try {
            sRegistry.notifyDisplayInfoChanged(slotIndex, subscriptionId, telephonyDisplayInfo);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyImsDisconnectCause(int subId, ImsReasonInfo imsReasonInfo) {
        try {
            sRegistry.notifyImsDisconnectCause(subId, imsReasonInfo);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifySrvccStateChanged(int subId, int state) {
        try {
            sRegistry.notifySrvccStateChanged(subId, state);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyPreciseCallState(int slotIndex, int subId, int[] callStates, String[] imsCallIds, int[] imsServiceTypes, int[] imsCallTypes) {
        try {
            sRegistry.notifyPreciseCallState(slotIndex, subId, callStates, imsCallIds, imsServiceTypes, imsCallTypes);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDisconnectCause(int slotIndex, int subId, int cause, int preciseCause) {
        try {
            sRegistry.notifyDisconnectCause(slotIndex, subId, cause, preciseCause);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCellLocation(int subId, CellIdentity cellLocation) {
        try {
            sRegistry.notifyCellLocationForSubscriber(subId, cellLocation);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCellInfoChanged(int subId, List<CellInfo> cellInfo) {
        try {
            sRegistry.notifyCellInfoForSubscriber(subId, cellInfo);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyActiveDataSubIdChanged(int activeDataSubId) {
        try {
            sRegistry.notifyActiveDataSubIdChanged(activeDataSubId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyRegistrationFailed(int slotIndex, int subId, CellIdentity cellIdentity, String chosenPlmn, int domain, int causeCode, int additionalCauseCode) {
        try {
            sRegistry.notifyRegistrationFailed(slotIndex, subId, cellIdentity, chosenPlmn, domain, causeCode, additionalCauseCode);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyBarringInfoChanged(int slotIndex, int subId, BarringInfo barringInfo) {
        try {
            sRegistry.notifyBarringInfoChanged(slotIndex, subId, barringInfo);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyPhysicalChannelConfigForSubscriber(int slotIndex, int subId, List<PhysicalChannelConfig> configs) {
        try {
            sRegistry.notifyPhysicalChannelConfigForSubscriber(slotIndex, subId, configs);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyDataEnabled(int slotIndex, int subId, boolean enabled, int reason) {
        try {
            sRegistry.notifyDataEnabled(slotIndex, subId, enabled, reason);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyAllowedNetworkTypesChanged(int slotIndex, int subId, int reason, long allowedNetworkType) {
        try {
            sRegistry.notifyAllowedNetworkTypesChanged(slotIndex, subId, reason, allowedNetworkType);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyLinkCapacityEstimateChanged(int slotIndex, int subId, List<LinkCapacityEstimate> linkCapacityEstimateList) {
        try {
            sRegistry.notifyLinkCapacityEstimateChanged(slotIndex, subId, linkCapacityEstimateList);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Set<Integer> getEventsFromCallback(TelephonyCallback telephonyCallback) {
        Set<Integer> eventList = new ArraySet<>();
        if (telephonyCallback instanceof TelephonyCallback.ServiceStateListener) {
            eventList.add(1);
        }
        if (telephonyCallback instanceof TelephonyCallback.MessageWaitingIndicatorListener) {
            eventList.add(3);
        }
        if (telephonyCallback instanceof TelephonyCallback.CallForwardingIndicatorListener) {
            eventList.add(4);
        }
        if (telephonyCallback instanceof TelephonyCallback.CellLocationListener) {
            eventList.add(5);
        }
        if (telephonyCallback instanceof TelephonyCallback.CallStateListener) {
            eventList.add(6);
        }
        if (telephonyCallback instanceof TelephonyCallback.DataConnectionStateListener) {
            eventList.add(7);
        }
        if (telephonyCallback instanceof TelephonyCallback.DataActivityListener) {
            eventList.add(8);
        }
        if (telephonyCallback instanceof TelephonyCallback.SignalStrengthsListener) {
            eventList.add(9);
        }
        if (telephonyCallback instanceof TelephonyCallback.CellInfoListener) {
            eventList.add(11);
        }
        if (telephonyCallback instanceof TelephonyCallback.PreciseCallStateListener) {
            eventList.add(12);
        }
        if (telephonyCallback instanceof TelephonyCallback.CallDisconnectCauseListener) {
            eventList.add(26);
        }
        if (telephonyCallback instanceof TelephonyCallback.ImsCallDisconnectCauseListener) {
            eventList.add(28);
        }
        if (telephonyCallback instanceof TelephonyCallback.PreciseDataConnectionStateListener) {
            eventList.add(13);
        }
        if (telephonyCallback instanceof TelephonyCallback.SrvccStateListener) {
            eventList.add(16);
        }
        if (telephonyCallback instanceof TelephonyCallback.VoiceActivationStateListener) {
            eventList.add(18);
        }
        if (telephonyCallback instanceof TelephonyCallback.DataActivationStateListener) {
            eventList.add(19);
        }
        if (telephonyCallback instanceof TelephonyCallback.UserMobileDataStateListener) {
            eventList.add(20);
        }
        if (telephonyCallback instanceof TelephonyCallback.DisplayInfoListener) {
            eventList.add(21);
        }
        if (telephonyCallback instanceof TelephonyCallback.EmergencyNumberListListener) {
            eventList.add(25);
        }
        if (telephonyCallback instanceof TelephonyCallback.OutgoingEmergencyCallListener) {
            eventList.add(29);
        }
        if (telephonyCallback instanceof TelephonyCallback.OutgoingEmergencySmsListener) {
            eventList.add(30);
        }
        if (telephonyCallback instanceof TelephonyCallback.PhoneCapabilityListener) {
            eventList.add(22);
        }
        if (telephonyCallback instanceof TelephonyCallback.ActiveDataSubscriptionIdListener) {
            eventList.add(23);
        }
        if (telephonyCallback instanceof TelephonyCallback.RadioPowerStateListener) {
            eventList.add(24);
        }
        if (telephonyCallback instanceof TelephonyCallback.CarrierNetworkListener) {
            eventList.add(17);
        }
        if (telephonyCallback instanceof TelephonyCallback.RegistrationFailedListener) {
            eventList.add(31);
        }
        if (telephonyCallback instanceof TelephonyCallback.CallAttributesListener) {
            eventList.add(27);
        }
        if (telephonyCallback instanceof TelephonyCallback.BarringInfoListener) {
            eventList.add(32);
        }
        if (telephonyCallback instanceof TelephonyCallback.PhysicalChannelConfigListener) {
            eventList.add(33);
        }
        if (telephonyCallback instanceof TelephonyCallback.DataEnabledListener) {
            eventList.add(34);
        }
        if (telephonyCallback instanceof TelephonyCallback.AllowedNetworkTypesListener) {
            eventList.add(35);
        }
        if (telephonyCallback instanceof TelephonyCallback.LinkCapacityEstimateChangedListener) {
            eventList.add(37);
        }
        if (telephonyCallback instanceof TelephonyCallback.MediaQualityStatusChangedListener) {
            eventList.add(39);
        }
        if (telephonyCallback instanceof TelephonyCallback.EmergencyCallbackModeListener) {
            eventList.add(40);
        }
        return eventList;
    }

    private Set<Integer> getEventsFromBitmask(int eventMask) {
        Set<Integer> eventList = new ArraySet<>();
        if ((eventMask & 1) != 0) {
            eventList.add(1);
        }
        if ((eventMask & 2) != 0) {
            eventList.add(2);
        }
        if ((eventMask & 4) != 0) {
            eventList.add(3);
        }
        if ((eventMask & 8) != 0) {
            eventList.add(4);
        }
        if ((eventMask & 16) != 0) {
            eventList.add(5);
        }
        if ((eventMask & 32) != 0) {
            eventList.add(36);
        }
        if ((eventMask & 64) != 0) {
            eventList.add(7);
        }
        if ((eventMask & 128) != 0) {
            eventList.add(8);
        }
        if ((eventMask & 256) != 0) {
            eventList.add(9);
        }
        if ((eventMask & 1024) != 0) {
            eventList.add(11);
        }
        if ((eventMask & 2048) != 0) {
            eventList.add(12);
        }
        if ((eventMask & 4096) != 0) {
            eventList.add(13);
        }
        if ((eventMask & 8192) != 0) {
            eventList.add(14);
        }
        if ((32768 & eventMask) != 0) {
            eventList.add(15);
        }
        if ((eventMask & 16384) != 0) {
            eventList.add(16);
        }
        if ((65536 & eventMask) != 0) {
            eventList.add(17);
        }
        if ((131072 & eventMask) != 0) {
            eventList.add(18);
        }
        if ((262144 & eventMask) != 0) {
            eventList.add(19);
        }
        if ((524288 & eventMask) != 0) {
            eventList.add(20);
        }
        if ((1048576 & eventMask) != 0) {
            eventList.add(21);
        }
        if ((2097152 & eventMask) != 0) {
            eventList.add(22);
        }
        if ((4194304 & eventMask) != 0) {
            eventList.add(23);
        }
        if ((8388608 & eventMask) != 0) {
            eventList.add(24);
        }
        if ((16777216 & eventMask) != 0) {
            eventList.add(25);
        }
        if ((33554432 & eventMask) != 0) {
            eventList.add(26);
        }
        if ((67108864 & eventMask) != 0) {
            eventList.add(27);
        }
        if ((134217728 & eventMask) != 0) {
            eventList.add(28);
        }
        if ((268435456 & eventMask) != 0) {
            eventList.add(29);
        }
        if ((536870912 & eventMask) != 0) {
            eventList.add(30);
        }
        if ((1073741824 & eventMask) != 0) {
            eventList.add(31);
        }
        if ((Integer.MIN_VALUE & eventMask) != 0) {
            eventList.add(32);
        }
        return eventList;
    }

    public void registerTelephonyCallback(boolean renounceFineLocationAccess, boolean renounceCoarseLocationAccess, Executor executor, int subId, String pkgName, String attributionTag, TelephonyCallback callback, boolean notifyNow) {
        if (callback == null) {
            throw new IllegalStateException("telephony service is null.");
        }
        callback.init(executor);
        listenFromCallback(renounceFineLocationAccess, renounceCoarseLocationAccess, subId, pkgName, attributionTag, callback, getEventsFromCallback(callback).stream().mapToInt(new ToIntFunction() { // from class: android.telephony.TelephonyRegistryManager$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray(), notifyNow);
    }

    public void unregisterTelephonyCallback(int subId, String pkgName, String attributionTag, TelephonyCallback callback, boolean notifyNow) {
        listenFromCallback(false, false, subId, pkgName, attributionTag, callback, new int[0], notifyNow);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CarrierPrivilegesCallbackWrapper extends ICarrierPrivilegesCallback.Stub implements ListenerExecutor {
        private final WeakReference<TelephonyManager.CarrierPrivilegesCallback> mCallback;
        private final Executor mExecutor;

        CarrierPrivilegesCallbackWrapper(TelephonyManager.CarrierPrivilegesCallback callback, Executor executor) {
            this.mCallback = new WeakReference<>(callback);
            this.mExecutor = executor;
        }

        @Override // com.android.internal.telephony.ICarrierPrivilegesCallback
        public void onCarrierPrivilegesChanged(List<String> privilegedPackageNames, int[] privilegedUids) {
            final Set<String> privilegedPkgNamesSet = Set.copyOf(privilegedPackageNames);
            final Set<Integer> privilegedUidsSet = (Set) Arrays.stream(privilegedUids).boxed().collect(Collectors.toSet());
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.TelephonyRegistryManager$CarrierPrivilegesCallbackWrapper$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    TelephonyRegistryManager.CarrierPrivilegesCallbackWrapper.this.lambda$onCarrierPrivilegesChanged$1(privilegedPkgNamesSet, privilegedUidsSet);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCarrierPrivilegesChanged$1(final Set privilegedPkgNamesSet, final Set privilegedUidsSet) throws Exception {
            Executor executor = this.mExecutor;
            WeakReference<TelephonyManager.CarrierPrivilegesCallback> weakReference = this.mCallback;
            Objects.requireNonNull(weakReference);
            executeSafely(executor, new C3115x8726eb0(weakReference), new ListenerExecutor.ListenerOperation() { // from class: android.telephony.TelephonyRegistryManager$CarrierPrivilegesCallbackWrapper$$ExternalSyntheticLambda3
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public final void operate(Object obj) {
                    ((TelephonyManager.CarrierPrivilegesCallback) obj).onCarrierPrivilegesChanged(privilegedPkgNamesSet, privilegedUidsSet);
                }
            });
        }

        @Override // com.android.internal.telephony.ICarrierPrivilegesCallback
        public void onCarrierServiceChanged(final String packageName, final int uid) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.TelephonyRegistryManager$CarrierPrivilegesCallbackWrapper$$ExternalSyntheticLambda4
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    TelephonyRegistryManager.CarrierPrivilegesCallbackWrapper.this.lambda$onCarrierServiceChanged$3(packageName, uid);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCarrierServiceChanged$3(final String packageName, final int uid) throws Exception {
            Executor executor = this.mExecutor;
            WeakReference<TelephonyManager.CarrierPrivilegesCallback> weakReference = this.mCallback;
            Objects.requireNonNull(weakReference);
            executeSafely(executor, new C3115x8726eb0(weakReference), new ListenerExecutor.ListenerOperation() { // from class: android.telephony.TelephonyRegistryManager$CarrierPrivilegesCallbackWrapper$$ExternalSyntheticLambda2
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public final void operate(Object obj) {
                    ((TelephonyManager.CarrierPrivilegesCallback) obj).onCarrierServiceChanged(packageName, uid);
                }
            });
        }
    }

    public void addCarrierPrivilegesCallback(int logicalSlotIndex, Executor executor, TelephonyManager.CarrierPrivilegesCallback callback) {
        if (callback == null || executor == null) {
            throw new IllegalArgumentException("callback and executor must be non-null");
        }
        WeakHashMap<TelephonyManager.CarrierPrivilegesCallback, WeakReference<CarrierPrivilegesCallbackWrapper>> weakHashMap = sCarrierPrivilegeCallbacks;
        synchronized (weakHashMap) {
            WeakReference<CarrierPrivilegesCallbackWrapper> existing = weakHashMap.get(callback);
            if (existing != null && existing.get() != null) {
                Log.m112d(TAG, "addCarrierPrivilegesCallback: callback already registered");
                return;
            }
            CarrierPrivilegesCallbackWrapper wrapper = new CarrierPrivilegesCallbackWrapper(callback, executor);
            weakHashMap.put(callback, new WeakReference<>(wrapper));
            try {
                sRegistry.addCarrierPrivilegesCallback(logicalSlotIndex, wrapper, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void removeCarrierPrivilegesCallback(TelephonyManager.CarrierPrivilegesCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("listener must be non-null");
        }
        WeakHashMap<TelephonyManager.CarrierPrivilegesCallback, WeakReference<CarrierPrivilegesCallbackWrapper>> weakHashMap = sCarrierPrivilegeCallbacks;
        synchronized (weakHashMap) {
            WeakReference<CarrierPrivilegesCallbackWrapper> ref = weakHashMap.remove(callback);
            if (ref == null) {
                return;
            }
            CarrierPrivilegesCallbackWrapper wrapper = ref.get();
            if (wrapper == null) {
                return;
            }
            try {
                sRegistry.removeCarrierPrivilegesCallback(wrapper, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void notifyCarrierPrivilegesChanged(int logicalSlotIndex, Set<String> privilegedPackageNames, Set<Integer> privilegedUids) {
        if (privilegedPackageNames == null || privilegedUids == null) {
            throw new IllegalArgumentException("privilegedPackageNames and privilegedUids must be non-null");
        }
        try {
            List<String> pkgList = List.copyOf(privilegedPackageNames);
            int[] uids = privilegedUids.stream().mapToInt(new AudioPort$$ExternalSyntheticLambda0()).toArray();
            sRegistry.notifyCarrierPrivilegesChanged(logicalSlotIndex, pkgList, uids);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyCarrierServiceChanged(int logicalSlotIndex, String packageName, int uid) {
        try {
            sRegistry.notifyCarrierServiceChanged(logicalSlotIndex, packageName, uid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addCarrierConfigChangedListener(Executor executor, CarrierConfigManager.CarrierConfigChangeListener listener) {
        Objects.requireNonNull(executor, "Executor should be non-null.");
        Objects.requireNonNull(listener, "Listener should be non-null.");
        if (this.mCarrierConfigChangeListenerMap.get(listener) != null) {
            Log.m110e(TAG, "registerCarrierConfigChangeListener: listener already present");
            return;
        }
        ICarrierConfigChangeListener callback = new BinderC31133(executor, listener);
        try {
            sRegistry.addCarrierConfigChangeListener(callback, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            this.mCarrierConfigChangeListenerMap.put(listener, callback);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.TelephonyRegistryManager$3 */
    /* loaded from: classes3.dex */
    public class BinderC31133 extends ICarrierConfigChangeListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ CarrierConfigManager.CarrierConfigChangeListener val$listener;

        BinderC31133(Executor executor, CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener) {
            this.val$executor = executor;
            this.val$listener = carrierConfigChangeListener;
        }

        @Override // com.android.internal.telephony.ICarrierConfigChangeListener
        public void onCarrierConfigChanged(final int slotIndex, final int subId, final int carrierId, final int specificCarrierId) {
            Log.m112d(TelephonyRegistryManager.TAG, "onCarrierConfigChanged call in ICarrierConfigChangeListener callback");
            long identify = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.telephony.TelephonyRegistryManager$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CarrierConfigManager.CarrierConfigChangeListener.this.onCarrierConfigChanged(slotIndex, subId, carrierId, specificCarrierId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identify);
            }
        }
    }

    public void removeCarrierConfigChangedListener(CarrierConfigManager.CarrierConfigChangeListener listener) {
        Objects.requireNonNull(listener, "Listener should be non-null.");
        if (this.mCarrierConfigChangeListenerMap.get(listener) == null) {
            Log.m110e(TAG, "removeCarrierConfigChangedListener: listener was not present");
            return;
        }
        try {
            sRegistry.removeCarrierConfigChangeListener(this.mCarrierConfigChangeListenerMap.get(listener), this.mContext.getOpPackageName());
            this.mCarrierConfigChangeListenerMap.remove(listener);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void notifyCarrierConfigChanged(int slotIndex, int subId, int carrierId, int specificCarrierId) {
        if (!SubscriptionManager.isValidPhoneId(slotIndex)) {
            Log.m110e(TAG, "notifyCarrierConfigChanged, ignored: invalid slotIndex " + slotIndex);
            return;
        }
        try {
            sRegistry.notifyCarrierConfigChanged(slotIndex, subId, carrierId, specificCarrierId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public void notifyCallBackModeStarted(int phoneId, int subId, int type) {
        try {
            Log.m112d(TAG, "notifyCallBackModeStarted:type=" + type);
            sRegistry.notifyCallbackModeStarted(phoneId, subId, type);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void notifyCallbackModeStopped(int phoneId, int subId, int type, int reason) {
        try {
            Log.m112d(TAG, "notifyCallbackModeStopped:type=" + type + ", reason=" + reason);
            sRegistry.notifyCallbackModeStopped(phoneId, subId, type, reason);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
}
