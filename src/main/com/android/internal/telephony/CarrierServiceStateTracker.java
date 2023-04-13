package com.android.internal.telephony;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.RadioAccessFamily;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class CarrierServiceStateTracker extends Handler {
    protected static final int CARRIER_EVENT_BASE = 100;
    protected static final int CARRIER_EVENT_DATA_DEREGISTRATION = 104;
    protected static final int CARRIER_EVENT_DATA_REGISTRATION = 103;
    protected static final int CARRIER_EVENT_IMS_CAPABILITIES_CHANGED = 105;
    protected static final int CARRIER_EVENT_VOICE_DEREGISTRATION = 102;
    protected static final int CARRIER_EVENT_VOICE_REGISTRATION = 101;
    @VisibleForTesting
    public static final String EMERGENCY_NOTIFICATION_TAG = "EmergencyNetworkNotification";
    public static final int NOTIFICATION_EMERGENCY_NETWORK = 1001;
    public static final int NOTIFICATION_PREF_NETWORK = 1000;
    @VisibleForTesting
    public static final String PREF_NETWORK_NOTIFICATION_TAG = "PrefNetworkNotification";
    private long mAllowedNetworkType;
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private Phone mPhone;
    private ServiceStateTracker mSST;
    private TelephonyManager mTelephonyManager;
    private final Map<Integer, NotificationType> mNotificationTypeMap = new HashMap();
    private int mPreviousSubId = -1;

    /* loaded from: classes.dex */
    public interface NotificationType {
        int getDelay();

        Notification.Builder getNotificationBuilder();

        int getNotificationId();

        String getNotificationTag();

        int getTypeId();

        boolean sendMessage();

        void setDelay(PersistableBundle persistableBundle);
    }

    private boolean checkSupportedBitmask(long j, long j2) {
        return (j2 & j) == j2;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class AllowedNetworkTypesListener extends TelephonyCallback implements TelephonyCallback.AllowedNetworkTypesListener {
        public AllowedNetworkTypesListener() {
        }

        public void onAllowedNetworkTypesChanged(int i, long j) {
            if (i == 0 && CarrierServiceStateTracker.this.mAllowedNetworkType != j) {
                CarrierServiceStateTracker.this.mAllowedNetworkType = j;
                CarrierServiceStateTracker.this.handleAllowedNetworkTypeChanged();
            }
        }
    }

    public CarrierServiceStateTracker(Phone phone, ServiceStateTracker serviceStateTracker) {
        this.mAllowedNetworkType = -1L;
        this.mPhone = phone;
        this.mSST = serviceStateTracker;
        this.mTelephonyManager = ((TelephonyManager) phone.getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mPhone.getSubId());
        ((CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(this.mPhone.getContext().getMainExecutor(), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.CarrierServiceStateTracker$$ExternalSyntheticLambda0
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                CarrierServiceStateTracker.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        SubscriptionManager.from(this.mPhone.getContext()).addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener(getLooper()) { // from class: com.android.internal.telephony.CarrierServiceStateTracker.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                int subId = CarrierServiceStateTracker.this.mPhone.getSubId();
                if (CarrierServiceStateTracker.this.mPreviousSubId != subId) {
                    CarrierServiceStateTracker.this.mPreviousSubId = subId;
                    CarrierServiceStateTracker carrierServiceStateTracker = CarrierServiceStateTracker.this;
                    carrierServiceStateTracker.mTelephonyManager = carrierServiceStateTracker.mTelephonyManager.createForSubscriptionId(CarrierServiceStateTracker.this.mPhone.getSubId());
                    CarrierServiceStateTracker.this.registerAllowedNetworkTypesListener();
                }
            }
        });
        registerNotificationTypes();
        this.mAllowedNetworkType = RadioAccessFamily.getNetworkTypeFromRaf((int) this.mPhone.getAllowedNetworkTypes(0));
        this.mAllowedNetworkTypesListener = new AllowedNetworkTypesListener();
        registerAllowedNetworkTypesListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        Rlog.d("CSST", "onCarrierConfigChanged: slotIndex=" + i + ", subId=" + i2 + ", carrierId=" + i3);
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{"emergency_notification_delay_int", "network_notification_delay_int"});
        if (carrierConfigSubset.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, NotificationType> entry : this.mNotificationTypeMap.entrySet()) {
            entry.getValue().setDelay(carrierConfigSubset);
        }
        handleConfigChanges();
    }

    @VisibleForTesting
    public AllowedNetworkTypesListener getAllowedNetworkTypesChangedListener() {
        return this.mAllowedNetworkTypesListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerAllowedNetworkTypesListener() {
        TelephonyManager telephonyManager;
        int subId = this.mPhone.getSubId();
        unregisterAllowedNetworkTypesListener();
        if (!SubscriptionManager.isValidSubscriptionId(subId) || (telephonyManager = this.mTelephonyManager) == null) {
            return;
        }
        telephonyManager.registerTelephonyCallback(new HandlerExecutor(this), this.mAllowedNetworkTypesListener);
    }

    private void unregisterAllowedNetworkTypesListener() {
        this.mTelephonyManager.unregisterTelephonyCallback(this.mAllowedNetworkTypesListener);
    }

    @VisibleForTesting
    public Map<Integer, NotificationType> getNotificationTypeMap() {
        return this.mNotificationTypeMap;
    }

    private void registerNotificationTypes() {
        this.mNotificationTypeMap.put(1000, new PrefNetworkNotification(1000));
        this.mNotificationTypeMap.put(1001, new EmergencyNetworkNotification(1001));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i != 1000 && i != 1001) {
            switch (i) {
                case 101:
                case 102:
                case CARRIER_EVENT_DATA_REGISTRATION /* 103 */:
                case CARRIER_EVENT_DATA_DEREGISTRATION /* 104 */:
                    handleConfigChanges();
                    return;
                case CARRIER_EVENT_IMS_CAPABILITIES_CHANGED /* 105 */:
                    handleImsCapabilitiesChanged();
                    return;
                default:
                    return;
            }
        }
        Rlog.d("CSST", "sending notification after delay: " + message.what);
        NotificationType notificationType = this.mNotificationTypeMap.get(Integer.valueOf(message.what));
        if (notificationType != null) {
            sendNotification(notificationType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPhoneStillRegistered() {
        ServiceState serviceState = this.mSST.mSS;
        return serviceState == null || serviceState.getState() == 0 || this.mSST.mSS.getDataRegistrationState() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPhoneRegisteredForWifiCalling() {
        Rlog.d("CSST", "isPhoneRegisteredForWifiCalling: " + this.mPhone.isWifiCallingEnabled());
        return this.mPhone.isWifiCallingEnabled();
    }

    @VisibleForTesting
    public boolean isRadioOffOrAirplaneMode() {
        try {
            return (this.mSST.isRadioOn() && Settings.Global.getInt(this.mPhone.getContext().getContentResolver(), "airplane_mode_on", 0) == 0) ? false : true;
        } catch (Exception unused) {
            Rlog.e("CSST", "Unable to get AIRPLACE_MODE_ON.");
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isGlobalMode() {
        try {
            int calculatePreferredNetworkType = PhoneFactory.calculatePreferredNetworkType(this.mPhone.getPhoneId());
            return isNrSupported() ? calculatePreferredNetworkType == RadioAccessFamily.getRafFromNetworkType(27) : calculatePreferredNetworkType == RadioAccessFamily.getRafFromNetworkType(10);
        } catch (Exception unused) {
            Rlog.e("CSST", "Unable to get PREFERRED_NETWORK_MODE.");
            return true;
        }
    }

    private boolean isNrSupported() {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).createForSubscriptionId(this.mPhone.getSubId());
        boolean isCarrierConfigEnableNr = isCarrierConfigEnableNr();
        boolean checkSupportedBitmask = checkSupportedBitmask(createForSubscriptionId.getSupportedRadioAccessFamily(), 524288L);
        boolean checkSupportedBitmask2 = checkSupportedBitmask(createForSubscriptionId.getAllowedNetworkTypesForReason(2), 524288L);
        Rlog.i("CSST", "isNrSupported:  carrierConfigEnabled: " + isCarrierConfigEnableNr + ", AccessFamilySupported: " + checkSupportedBitmask + ", isNrNetworkTypeAllowed: " + checkSupportedBitmask2);
        return isCarrierConfigEnableNr && checkSupportedBitmask && checkSupportedBitmask2;
    }

    private boolean isCarrierConfigEnableNr() {
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{"carrier_nr_availabilities_int_array"});
        if (carrierConfigSubset.isEmpty()) {
            Rlog.e("CSST", "isCarrierConfigEnableNr: Cannot get config " + this.mPhone.getSubId());
            return false;
        }
        return !ArrayUtils.isEmpty(carrierConfigSubset.getIntArray("carrier_nr_availabilities_int_array"));
    }

    private void handleConfigChanges() {
        for (Map.Entry<Integer, NotificationType> entry : this.mNotificationTypeMap.entrySet()) {
            evaluateSendingMessageOrCancelNotification(entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAllowedNetworkTypeChanged() {
        NotificationType notificationType = this.mNotificationTypeMap.get(1000);
        if (notificationType != null) {
            evaluateSendingMessageOrCancelNotification(notificationType);
        }
    }

    private void handleImsCapabilitiesChanged() {
        NotificationType notificationType = this.mNotificationTypeMap.get(1001);
        if (notificationType != null) {
            evaluateSendingMessageOrCancelNotification(notificationType);
        }
    }

    private void evaluateSendingMessageOrCancelNotification(NotificationType notificationType) {
        if (evaluateSendingMessage(notificationType)) {
            Message obtainMessage = obtainMessage(notificationType.getTypeId(), null);
            Rlog.i("CSST", "starting timer for notifications." + notificationType.getTypeId());
            sendMessageDelayed(obtainMessage, (long) getDelay(notificationType));
            return;
        }
        cancelNotification(notificationType);
        Rlog.i("CSST", "canceling notifications: " + notificationType.getTypeId());
    }

    @VisibleForTesting
    public boolean evaluateSendingMessage(NotificationType notificationType) {
        return notificationType.sendMessage();
    }

    @VisibleForTesting
    public int getDelay(NotificationType notificationType) {
        return notificationType.getDelay();
    }

    @VisibleForTesting
    public Notification.Builder getNotificationBuilder(NotificationType notificationType) {
        return notificationType.getNotificationBuilder();
    }

    @VisibleForTesting
    public NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService("notification");
    }

    @VisibleForTesting
    public void sendNotification(NotificationType notificationType) {
        if (evaluateSendingMessage(notificationType)) {
            Context context = this.mPhone.getContext();
            Notification.Builder notificationBuilder = getNotificationBuilder(notificationType);
            notificationBuilder.setWhen(System.currentTimeMillis()).setShowWhen(true).setAutoCancel(true).setSmallIcon(17301642).setColor(context.getResources().getColor(17170460));
            getNotificationManager(context).notify(notificationType.getNotificationTag(), notificationType.getNotificationId(), notificationBuilder.build());
        }
    }

    public void cancelNotification(NotificationType notificationType) {
        Context context = this.mPhone.getContext();
        removeMessages(notificationType.getTypeId());
        getNotificationManager(context).cancel(notificationType.getNotificationTag(), notificationType.getNotificationId());
    }

    public void dispose() {
        unregisterAllowedNetworkTypesListener();
    }

    /* loaded from: classes.dex */
    public class PrefNetworkNotification implements NotificationType {
        private int mDelay = -1;
        private final int mTypeId;

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public String getNotificationTag() {
            return CarrierServiceStateTracker.PREF_NETWORK_NOTIFICATION_TAG;
        }

        PrefNetworkNotification(int i) {
            this.mTypeId = i;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public void setDelay(PersistableBundle persistableBundle) {
            if (persistableBundle == null) {
                Rlog.e("CSST", "bundle is null");
                return;
            }
            this.mDelay = persistableBundle.getInt("network_notification_delay_int");
            Rlog.i("CSST", "reading time to delay notification pref network: " + this.mDelay);
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getDelay() {
            return this.mDelay;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getTypeId() {
            return this.mTypeId;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getNotificationId() {
            return CarrierServiceStateTracker.this.mPhone.getSubId();
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public boolean sendMessage() {
            Rlog.i("CSST", "PrefNetworkNotification: sendMessage() w/values: ," + CarrierServiceStateTracker.this.isPhoneStillRegistered() + "," + this.mDelay + "," + CarrierServiceStateTracker.this.isGlobalMode() + "," + CarrierServiceStateTracker.this.mSST.isRadioOn());
            return (this.mDelay == -1 || CarrierServiceStateTracker.this.isPhoneStillRegistered() || CarrierServiceStateTracker.this.isGlobalMode() || CarrierServiceStateTracker.this.isRadioOffOrAirplaneMode()) ? false : true;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public Notification.Builder getNotificationBuilder() {
            Context context = CarrierServiceStateTracker.this.mPhone.getContext();
            Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
            intent.putExtra("expandable", true);
            PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 1140850688);
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(context, CarrierServiceStateTracker.this.mPhone.getSubId());
            CharSequence text = resourcesForSubId.getText(17039448);
            CharSequence text2 = resourcesForSubId.getText(17039447);
            return new Notification.Builder(context).setContentTitle(text).setStyle(new Notification.BigTextStyle().bigText(text2)).setContentText(text2).setChannelId(NotificationChannelController.CHANNEL_ID_ALERT).setContentIntent(activity);
        }
    }

    /* loaded from: classes.dex */
    public class EmergencyNetworkNotification implements NotificationType {
        private int mDelay = -1;
        private final int mTypeId;

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public String getNotificationTag() {
            return CarrierServiceStateTracker.EMERGENCY_NOTIFICATION_TAG;
        }

        EmergencyNetworkNotification(int i) {
            this.mTypeId = i;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public void setDelay(PersistableBundle persistableBundle) {
            if (persistableBundle == null) {
                Rlog.e("CSST", "bundle is null");
                return;
            }
            this.mDelay = persistableBundle.getInt("emergency_notification_delay_int");
            Rlog.i("CSST", "reading time to delay notification emergency: " + this.mDelay);
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getDelay() {
            return this.mDelay;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getTypeId() {
            return this.mTypeId;
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public int getNotificationId() {
            return CarrierServiceStateTracker.this.mPhone.getSubId();
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public boolean sendMessage() {
            Rlog.i("CSST", "EmergencyNetworkNotification: sendMessage() w/values: ," + this.mDelay + "," + CarrierServiceStateTracker.this.isPhoneRegisteredForWifiCalling() + "," + CarrierServiceStateTracker.this.mSST.isRadioOn());
            return this.mDelay != -1 && CarrierServiceStateTracker.this.isPhoneRegisteredForWifiCalling();
        }

        @Override // com.android.internal.telephony.CarrierServiceStateTracker.NotificationType
        public Notification.Builder getNotificationBuilder() {
            Context context = CarrierServiceStateTracker.this.mPhone.getContext();
            Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(context, CarrierServiceStateTracker.this.mPhone.getSubId());
            CharSequence text = resourcesForSubId.getText(17039445);
            CharSequence text2 = resourcesForSubId.getText(17039444);
            return new Notification.Builder(context).setContentTitle(text).setStyle(new Notification.BigTextStyle().bigText(text2)).setContentText(text2).setOngoing(true).setChannelId(NotificationChannelController.CHANNEL_ID_WFC);
        }
    }
}
