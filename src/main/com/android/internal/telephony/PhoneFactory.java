package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.content.Context;
import android.net.LocalServerSocket;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.telephony.AnomalyReporter;
import android.telephony.RadioAccessFamily;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.cdma.CdmaSubscriptionSourceManager;
import com.android.internal.telephony.data.CellularNetworkValidator;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.internal.telephony.data.TelephonyNetworkFactory;
import com.android.internal.telephony.euicc.EuiccCardController;
import com.android.internal.telephony.euicc.EuiccController;
import com.android.internal.telephony.imsphone.ImsPhoneFactory;
import com.android.internal.telephony.metrics.MetricsCollector;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class PhoneFactory {
    private static CellularNetworkValidator sCellularNetworkValidator = null;
    private static CommandsInterface[] sCommandsInterfaces = null;
    @UnsupportedAppUsage
    private static Context sContext = null;
    private static EuiccCardController sEuiccCardController = null;
    private static EuiccController sEuiccController = null;
    private static IntentBroadcaster sIntentBroadcaster = null;
    @UnsupportedAppUsage
    private static boolean sMadeDefaults = false;
    private static MetricsCollector sMetricsCollector;
    private static NotificationChannelController sNotificationChannelController;
    private static Phone sPhone;
    private static PhoneConfigurationManager sPhoneConfigurationManager;
    @UnsupportedAppUsage
    private static PhoneNotifier sPhoneNotifier;
    private static PhoneSwitcher sPhoneSwitcher;
    private static Phone[] sPhones;
    private static ProxyController sProxyController;
    private static RadioInterfaceCapabilityController sRadioHalCapabilities;
    private static SubscriptionInfoUpdater sSubInfoRecordUpdater;
    private static SubscriptionManagerService sSubscriptionManagerService;
    private static TelephonyNetworkFactory[] sTelephonyNetworkFactories;
    private static UiccController sUiccController;
    static final Object sLockProxyPhones = new Object();
    private static final HashMap<String, LocalLog> sLocalLogs = new HashMap<>();
    private static boolean sSubscriptionManagerServiceEnabled = false;

    public static void makeDefaultPhones(Context context) {
        makeDefaultPhone(context);
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x0230  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0040 A[SYNTHETIC] */
    @UnsupportedAppUsage
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void makeDefaultPhone(Context context) {
        boolean z;
        int i;
        boolean z2;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                sContext = context;
                if (!context.getResources().getBoolean(17891874) && !DeviceConfig.getBoolean("telephony", "enable_subscription_manager_service", false)) {
                    z = false;
                    sSubscriptionManagerServiceEnabled = z;
                    TelephonyDevController.create();
                    TelephonyMetrics.getInstance().setContext(context);
                    i = 0;
                    while (true) {
                        i++;
                        try {
                            new LocalServerSocket("com.android.internal.telephony");
                            z2 = false;
                        } catch (IOException unused) {
                            z2 = true;
                        }
                        if (z2) {
                            sMetricsCollector = new MetricsCollector(context);
                            sPhoneNotifier = new DefaultPhoneNotifier(context);
                            int i2 = CdmaSubscriptionSourceManager.getDefault(context);
                            Rlog.i("PhoneFactory", "Cdma Subscription set to " + i2);
                            int activeModemCount = TelephonyManager.getDefault().getActiveModemCount();
                            int[] iArr = new int[activeModemCount];
                            sPhones = new Phone[activeModemCount];
                            sCommandsInterfaces = new RIL[activeModemCount];
                            sTelephonyNetworkFactories = new TelephonyNetworkFactory[activeModemCount];
                            for (int i3 = 0; i3 < activeModemCount; i3++) {
                                iArr[i3] = RILConstants.PREFERRED_NETWORK_MODE;
                                Rlog.i("PhoneFactory", "Network Mode set to " + Integer.toString(iArr[i3]));
                                sCommandsInterfaces[i3] = new RIL(context, RadioAccessFamily.getRafFromNetworkType(iArr[i3]), i2, Integer.valueOf(i3));
                            }
                            if (activeModemCount > 0) {
                                sRadioHalCapabilities = RadioInterfaceCapabilityController.init(RadioConfig.make(context, sCommandsInterfaces[0].getHalVersion(0)), sCommandsInterfaces[0]);
                            } else {
                                sRadioHalCapabilities = RadioInterfaceCapabilityController.init(RadioConfig.make(context, HalVersion.UNKNOWN), null);
                            }
                            sUiccController = UiccController.make(context);
                            if (isSubscriptionManagerServiceEnabled()) {
                                Rlog.i("PhoneFactory", "Creating SubscriptionManagerService");
                                sSubscriptionManagerService = new SubscriptionManagerService(context, Looper.myLooper());
                            } else {
                                Rlog.i("PhoneFactory", "Creating SubscriptionController");
                                TelephonyComponentFactory.getInstance().inject(SubscriptionController.class.getName()).initSubscriptionController(context);
                            }
                            TelephonyComponentFactory.getInstance().inject(MultiSimSettingController.class.getName()).initMultiSimSettingController(context, isSubscriptionManagerServiceEnabled() ? null : SubscriptionController.getInstance());
                            if (context.getPackageManager().hasSystemFeature("android.hardware.telephony.euicc")) {
                                sEuiccController = EuiccController.init(context);
                                sEuiccCardController = EuiccCardController.init(context);
                            }
                            for (int i4 = 0; i4 < activeModemCount; i4++) {
                                sPhones[i4] = createPhone(context, i4);
                            }
                            if (activeModemCount > 0) {
                                sPhone = sPhones[0];
                            }
                            ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(context, true);
                            Rlog.i("PhoneFactory", "defaultSmsApplication: " + (defaultSmsApplication != null ? defaultSmsApplication.getPackageName() : "NONE"));
                            SmsApplication.initSmsPackageMonitor(context);
                            sMadeDefaults = true;
                            if (!isSubscriptionManagerServiceEnabled()) {
                                Rlog.i("PhoneFactory", "Creating SubInfoRecordUpdater ");
                                HandlerThread handlerThread = new HandlerThread("PhoneFactoryHandlerThread");
                                handlerThread.start();
                                sSubInfoRecordUpdater = TelephonyComponentFactory.getInstance().inject(SubscriptionInfoUpdater.class.getName()).makeSubscriptionInfoUpdater(handlerThread.getLooper(), context, SubscriptionController.getInstance());
                            }
                            if (context.getPackageManager().hasSystemFeature("android.hardware.telephony.ims")) {
                                for (int i5 = 0; i5 < activeModemCount; i5++) {
                                    sPhones[i5].createImsPhone();
                                }
                            } else {
                                Rlog.i("PhoneFactory", "IMS is not supported on this device, skipping ImsResolver.");
                            }
                            sPhoneConfigurationManager = PhoneConfigurationManager.init(sContext);
                            sCellularNetworkValidator = CellularNetworkValidator.make(sContext);
                            sPhoneSwitcher = TelephonyComponentFactory.getInstance().inject(PhoneSwitcher.class.getName()).makePhoneSwitcher(sPhoneConfigurationManager.getNumberOfModemsWithSimultaneousDataConnections(), sContext, Looper.myLooper());
                            sProxyController = ProxyController.getInstance(context);
                            sIntentBroadcaster = IntentBroadcaster.getInstance(context);
                            sNotificationChannelController = new NotificationChannelController(context);
                            for (int i6 = 0; i6 < activeModemCount; i6++) {
                                sTelephonyNetworkFactories[i6] = new TelephonyNetworkFactory(Looper.myLooper(), sPhones[i6]);
                            }
                        } else if (i > 3) {
                            throw new RuntimeException("PhoneFactory probably already running");
                        } else {
                            try {
                                Thread.sleep(2000L);
                            } catch (InterruptedException unused2) {
                            }
                        }
                    }
                }
                z = true;
                sSubscriptionManagerServiceEnabled = z;
                TelephonyDevController.create();
                TelephonyMetrics.getInstance().setContext(context);
                i = 0;
                while (true) {
                    i++;
                    new LocalServerSocket("com.android.internal.telephony");
                    z2 = false;
                    if (z2) {
                    }
                }
            }
        }
    }

    public static boolean isSubscriptionManagerServiceEnabled() {
        return sSubscriptionManagerServiceEnabled;
    }

    public static void onMultiSimConfigChanged(Context context, int i) {
        synchronized (sLockProxyPhones) {
            Phone[] phoneArr = sPhones;
            int length = phoneArr.length;
            if (length == i) {
                return;
            }
            if (length > i) {
                return;
            }
            sPhones = (Phone[]) Arrays.copyOf(phoneArr, i);
            sCommandsInterfaces = (CommandsInterface[]) Arrays.copyOf(sCommandsInterfaces, i);
            sTelephonyNetworkFactories = (TelephonyNetworkFactory[]) Arrays.copyOf(sTelephonyNetworkFactories, i);
            int i2 = CdmaSubscriptionSourceManager.getDefault(context);
            while (length < i) {
                sCommandsInterfaces[length] = new RIL(context, RadioAccessFamily.getRafFromNetworkType(RILConstants.PREFERRED_NETWORK_MODE), i2, Integer.valueOf(length));
                sPhones[length] = createPhone(context, length);
                if (context.getPackageManager().hasSystemFeature("android.hardware.telephony.ims")) {
                    sPhones[length].createImsPhone();
                }
                sTelephonyNetworkFactories[length] = new TelephonyNetworkFactory(Looper.myLooper(), sPhones[length]);
                length++;
            }
        }
    }

    private static Phone createPhone(Context context, int i) {
        int phoneType = TelephonyManager.getPhoneType(RILConstants.PREFERRED_NETWORK_MODE);
        Rlog.i("PhoneFactory", "Creating Phone with type = " + phoneType + " phoneId = " + i);
        if (phoneType == 2) {
            phoneType = 6;
        }
        return TelephonyComponentFactory.getInstance().inject(GsmCdmaPhone.class.getName()).makePhone(context, sCommandsInterfaces[i], sPhoneNotifier, i, phoneType, TelephonyComponentFactory.getInstance());
    }

    @UnsupportedAppUsage
    public static Phone getDefaultPhone() {
        Phone phone;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                throw new IllegalStateException("Default phones haven't been made yet!");
            }
            phone = sPhone;
        }
        return phone;
    }

    @UnsupportedAppUsage
    public static Phone getPhone(int i) {
        Phone phone;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                throw new IllegalStateException("Default phones haven't been made yet!");
            }
            if (i == Integer.MAX_VALUE) {
                phone = sPhone;
            } else {
                if (i >= 0) {
                    Phone[] phoneArr = sPhones;
                    if (i < phoneArr.length) {
                        phone = phoneArr[i];
                    }
                }
                phone = null;
            }
        }
        return phone;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static Phone[] getPhones() {
        Phone[] phoneArr;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                throw new IllegalStateException("Default phones haven't been made yet!");
            }
            phoneArr = sPhones;
        }
        return phoneArr;
    }

    public static SubscriptionInfoUpdater getSubscriptionInfoUpdater() {
        return sSubInfoRecordUpdater;
    }

    public static TelephonyNetworkFactory getNetworkFactory(int i) {
        TelephonyNetworkFactory telephonyNetworkFactory;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                throw new IllegalStateException("Default phones haven't been made yet!");
            }
            if (i == Integer.MAX_VALUE) {
                i = sPhone.getSubId();
            }
            TelephonyNetworkFactory[] telephonyNetworkFactoryArr = sTelephonyNetworkFactories;
            telephonyNetworkFactory = (telephonyNetworkFactoryArr == null || i < 0 || i >= telephonyNetworkFactoryArr.length) ? null : telephonyNetworkFactoryArr[i];
        }
        return telephonyNetworkFactory;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static int calculatePreferredNetworkType(int i) {
        if (getPhone(i) == null) {
            Rlog.d("PhoneFactory", "Invalid phoneId return default network mode ");
            return RadioAccessFamily.getRafFromNetworkType(RILConstants.PREFERRED_NETWORK_MODE);
        }
        int allowedNetworkTypes = (int) getPhone(i).getAllowedNetworkTypes(0);
        Rlog.d("PhoneFactory", "calculatePreferredNetworkType: phoneId = " + i + " networkType = " + allowedNetworkTypes);
        return allowedNetworkTypes;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static int getDefaultSubscription() {
        if (isSubscriptionManagerServiceEnabled()) {
            return SubscriptionManagerService.getInstance().getDefaultSubId();
        }
        return SubscriptionController.getInstance().getDefaultSubId();
    }

    public static boolean isSMSPromptEnabled() {
        int i;
        try {
            i = Settings.Global.getInt(sContext.getContentResolver(), "multi_sim_sms_prompt");
        } catch (Settings.SettingNotFoundException unused) {
            Rlog.e("PhoneFactory", "Settings Exception Reading Dual Sim SMS Prompt Values");
            i = 0;
        }
        boolean z = i != 0;
        Rlog.d("PhoneFactory", "SMS Prompt option:" + z);
        return z;
    }

    public static Phone makeImsPhone(PhoneNotifier phoneNotifier, Phone phone) {
        return ImsPhoneFactory.makePhone(sContext, phoneNotifier, phone);
    }

    public static void requestEmbeddedSubscriptionInfoListRefresh(int i, Runnable runnable) {
        sSubInfoRecordUpdater.requestEmbeddedSubscriptionInfoListRefresh(i, runnable);
    }

    public static SmsController getSmsController() {
        SmsController smsController;
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                throw new IllegalStateException("Default phones haven't been made yet!");
            }
            smsController = sProxyController.getSmsController();
        }
        return smsController;
    }

    public static CommandsInterface[] getCommandsInterfaces() {
        CommandsInterface[] commandsInterfaceArr;
        synchronized (sLockProxyPhones) {
            commandsInterfaceArr = sCommandsInterfaces;
        }
        return commandsInterfaceArr;
    }

    public static void addLocalLog(String str, int i) {
        HashMap<String, LocalLog> hashMap = sLocalLogs;
        synchronized (hashMap) {
            if (hashMap.containsKey(str)) {
                throw new IllegalArgumentException("key " + str + " already present");
            }
            hashMap.put(str, new LocalLog(i));
        }
    }

    public static void localLog(String str, String str2) {
        HashMap<String, LocalLog> hashMap = sLocalLogs;
        synchronized (hashMap) {
            if (!hashMap.containsKey(str)) {
                throw new IllegalArgumentException("key " + str + " not found");
            }
            hashMap.get(str).log(str2);
        }
    }

    public static MetricsCollector getMetricsCollector() {
        return sMetricsCollector;
    }

    public static void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("PhoneFactory:");
        indentingPrintWriter.println(" sMadeDefaults=" + sMadeDefaults);
        sPhoneSwitcher.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.println();
        Phone[] phones = getPhones();
        for (int i = 0; i < phones.length; i++) {
            indentingPrintWriter.increaseIndent();
            try {
                phones[i].dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.flush();
                indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
                sTelephonyNetworkFactories[i].dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.flush();
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
            } catch (Exception e) {
                indentingPrintWriter.println("Telephony DebugService: Could not get Phone[" + i + "] e=" + e);
            }
        }
        indentingPrintWriter.println("UiccController:");
        indentingPrintWriter.increaseIndent();
        try {
            sUiccController.dump(fileDescriptor, indentingPrintWriter, strArr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        indentingPrintWriter.flush();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        if (!isSubscriptionManagerServiceEnabled()) {
            indentingPrintWriter.println("SubscriptionController:");
            indentingPrintWriter.increaseIndent();
            try {
                SubscriptionController.getInstance().dump(fileDescriptor, indentingPrintWriter, strArr);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            indentingPrintWriter.flush();
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
            indentingPrintWriter.println("SubInfoRecordUpdater:");
            indentingPrintWriter.increaseIndent();
            try {
                sSubInfoRecordUpdater.dump(fileDescriptor, indentingPrintWriter, strArr);
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        indentingPrintWriter.flush();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        indentingPrintWriter.println("sRadioHalCapabilities:");
        indentingPrintWriter.increaseIndent();
        try {
            sRadioHalCapabilities.dump(fileDescriptor, indentingPrintWriter, strArr);
        } catch (Exception e5) {
            e5.printStackTrace();
        }
        indentingPrintWriter.flush();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        indentingPrintWriter.println("LocalLogs:");
        indentingPrintWriter.increaseIndent();
        HashMap<String, LocalLog> hashMap = sLocalLogs;
        synchronized (hashMap) {
            for (String str : hashMap.keySet()) {
                indentingPrintWriter.println(str);
                indentingPrintWriter.increaseIndent();
                sLocalLogs.get(str).dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.flush();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        indentingPrintWriter.println("SharedPreferences:");
        indentingPrintWriter.increaseIndent();
        try {
            Context context = sContext;
            if (context != null) {
                Map<String, ?> all = PreferenceManager.getDefaultSharedPreferences(context).getAll();
                for (String str2 : all.keySet()) {
                    indentingPrintWriter.println(((Object) str2) + " : " + all.get(str2));
                }
            }
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        indentingPrintWriter.println("DebugEvents:");
        indentingPrintWriter.increaseIndent();
        try {
            AnomalyReporter.dump(fileDescriptor, indentingPrintWriter, strArr);
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        indentingPrintWriter.flush();
        indentingPrintWriter.decreaseIndent();
    }
}
