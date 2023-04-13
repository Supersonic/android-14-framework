package com.android.internal.telephony.euicc;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.euicc.EuiccProfileInfo;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.euicc.EuiccNotification;
import android.telephony.euicc.EuiccRulesAuthTable;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.euicc.IEuiccCardController;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.internal.telephony.uicc.euicc.EuiccCard;
import com.android.internal.telephony.uicc.euicc.EuiccCardErrorException;
import com.android.internal.telephony.uicc.euicc.EuiccPort;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public class EuiccCardController extends IEuiccCardController.Stub {
    private static EuiccCardController sInstance;
    private AppOpsManager mAppOps;
    private ComponentInfo mBestComponent;
    private String mCallingPackage;
    private final Context mContext;
    private EuiccController mEuiccController;
    private Handler mEuiccMainThreadHandler;
    private SimSlotStatusChangedBroadcastReceiver mSimSlotStatusChangeReceiver;
    private UiccController mUiccController;

    /* loaded from: classes.dex */
    private class SimSlotStatusChangedBroadcastReceiver extends BroadcastReceiver {
        private SimSlotStatusChangedBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.SIM_SLOT_STATUS_CHANGED".equals(intent.getAction()) && EuiccCardController.this.isEmbeddedCardPresent()) {
                if (EuiccCardController.this.isEmbeddedSlotActivated()) {
                    EuiccCardController.this.mEuiccController.startOtaUpdatingIfNecessary();
                }
                EuiccCardController.this.mContext.unregisterReceiver(EuiccCardController.this.mSimSlotStatusChangeReceiver);
            }
        }
    }

    public static EuiccCardController init(Context context) {
        synchronized (EuiccCardController.class) {
            if (sInstance == null) {
                sInstance = new EuiccCardController(context);
            } else {
                Log.wtf("EuiccCardController", "init() called multiple times! sInstance = " + sInstance);
            }
        }
        return sInstance;
    }

    public static EuiccCardController get() {
        if (sInstance == null) {
            synchronized (EuiccCardController.class) {
                if (sInstance == null) {
                    throw new IllegalStateException("get() called before init()");
                }
            }
        }
        return sInstance;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private EuiccCardController(Context context) {
        this(context, new Handler(), EuiccController.get(), UiccController.getInstance());
        TelephonyFrameworkInitializer.getTelephonyServiceManager().getEuiccCardControllerServiceRegisterer().register(this);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public EuiccCardController(Context context, Handler handler, EuiccController euiccController, UiccController uiccController) {
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        this.mEuiccMainThreadHandler = handler;
        this.mUiccController = uiccController;
        this.mEuiccController = euiccController;
        if (isBootUp(context)) {
            SimSlotStatusChangedBroadcastReceiver simSlotStatusChangedBroadcastReceiver = new SimSlotStatusChangedBroadcastReceiver();
            this.mSimSlotStatusChangeReceiver = simSlotStatusChangedBroadcastReceiver;
            context.registerReceiver(simSlotStatusChangedBroadcastReceiver, new IntentFilter("android.telephony.action.SIM_SLOT_STATUS_CHANGED"));
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static boolean isBootUp(Context context) {
        int i = Settings.Global.getInt(context.getContentResolver(), "boot_count", -1);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int i2 = defaultSharedPreferences.getInt("last_boot_count", -1);
        if (i == -1 || i2 == -1 || i != i2) {
            defaultSharedPreferences.edit().putInt("last_boot_count", i).apply();
            return true;
        }
        return false;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean isEmbeddedSlotActivated() {
        UiccSlot[] uiccSlots = this.mUiccController.getUiccSlots();
        if (uiccSlots == null) {
            return false;
        }
        for (UiccSlot uiccSlot : uiccSlots) {
            if (uiccSlot != null && !uiccSlot.isRemovable() && uiccSlot.isActive()) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean isEmbeddedCardPresent() {
        UiccSlot[] uiccSlots = this.mUiccController.getUiccSlots();
        if (uiccSlots == null) {
            return false;
        }
        for (UiccSlot uiccSlot : uiccSlots) {
            if (uiccSlot != null && !uiccSlot.isRemovable() && uiccSlot.getCardState() != null && uiccSlot.getCardState().isCardPresent()) {
                return true;
            }
        }
        return false;
    }

    private void checkCallingPackage(String str) {
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        this.mCallingPackage = str;
        ComponentInfo findBestComponent = EuiccConnector.findBestComponent(this.mContext.getPackageManager());
        this.mBestComponent = findBestComponent;
        if (findBestComponent == null || !TextUtils.equals(this.mCallingPackage, findBestComponent.packageName)) {
            throw new SecurityException("The calling package can only be LPA.");
        }
    }

    private UiccSlot getUiccSlotForEmbeddedCard(String str) {
        int uiccSlotForCardId = this.mUiccController.getUiccSlotForCardId(str);
        UiccSlot uiccSlot = this.mUiccController.getUiccSlot(uiccSlotForCardId);
        if (uiccSlot == null) {
            loge("UiccSlot is null. slotId : " + uiccSlotForCardId + " cardId : " + str);
            return null;
        } else if (uiccSlot.isEuicc()) {
            return uiccSlot;
        } else {
            loge("UiccSlot is not embedded slot : " + uiccSlotForCardId + " cardId : " + str);
            return null;
        }
    }

    private EuiccCard getEuiccCard(String str) {
        UiccSlot uiccSlotForEmbeddedCard = getUiccSlotForEmbeddedCard(str);
        if (uiccSlotForEmbeddedCard == null) {
            return null;
        }
        UiccCard uiccCard = uiccSlotForEmbeddedCard.getUiccCard();
        if (uiccCard == null) {
            loge("UiccCard is null. cardId : " + str);
            return null;
        }
        return (EuiccCard) uiccCard;
    }

    private EuiccPort getEuiccPortFromIccId(String str, String str2) {
        UiccSlot uiccSlotForEmbeddedCard = getUiccSlotForEmbeddedCard(str);
        if (uiccSlotForEmbeddedCard == null) {
            return null;
        }
        UiccCard uiccCard = uiccSlotForEmbeddedCard.getUiccCard();
        if (uiccCard == null) {
            loge("UiccCard is null. cardId : " + str);
            return null;
        }
        int portIndexFromIccId = uiccSlotForEmbeddedCard.getPortIndexFromIccId(str2);
        UiccPort uiccPort = uiccCard.getUiccPort(portIndexFromIccId);
        if (uiccPort == null) {
            loge("UiccPort is null. cardId : " + str + " portIndex : " + portIndexFromIccId);
            return null;
        }
        return (EuiccPort) uiccPort;
    }

    private EuiccPort getFirstActiveEuiccPort(String str) {
        EuiccCard euiccCard = getEuiccCard(str);
        if (euiccCard == null) {
            return null;
        }
        if (euiccCard.getUiccPortList().length > 0) {
            return (EuiccPort) euiccCard.getUiccPortList()[0];
        }
        loge("No active ports exists. cardId : " + str);
        return null;
    }

    private EuiccPort getEuiccPort(String str, int i) {
        EuiccCard euiccCard = getEuiccCard(str);
        if (euiccCard == null) {
            return null;
        }
        UiccPort uiccPort = euiccCard.getUiccPort(i);
        if (uiccPort == null) {
            loge("UiccPort is null. cardId : " + str + " portIndex : " + i);
            return null;
        }
        return (EuiccPort) uiccPort;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getResultCode(Throwable th) {
        if (th instanceof EuiccCardErrorException) {
            return ((EuiccCardErrorException) th).getErrorCode();
        }
        return -1;
    }

    public void getAllProfiles(String str, String str2, final IGetAllProfilesCallback iGetAllProfilesCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetAllProfilesCallback.onComplete(-2, (EuiccProfileInfo[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("getAllProfiles callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getAllProfiles(new AsyncResultCallback<EuiccProfileInfo[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.1
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccProfileInfo[] euiccProfileInfoArr) {
                    try {
                        iGetAllProfilesCallback.onComplete(0, euiccProfileInfoArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getAllProfiles callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getAllProfiles callback onException: ", th);
                        iGetAllProfilesCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccProfileInfo[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getAllProfiles callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetAllProfilesCallback.onComplete(-3, (EuiccProfileInfo[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getProfile(String str, String str2, String str3, final IGetProfileCallback iGetProfileCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetProfileCallback.onComplete(-2, (EuiccProfileInfo) null);
                    return;
                } catch (RemoteException e) {
                    loge("getProfile callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getProfile(str3, new AsyncResultCallback<EuiccProfileInfo>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.2
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccProfileInfo euiccProfileInfo) {
                    try {
                        iGetProfileCallback.onComplete(0, euiccProfileInfo);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getProfile callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getProfile callback onException: ", th);
                        iGetProfileCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccProfileInfo) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getProfile callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetProfileCallback.onComplete(-3, (EuiccProfileInfo) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getEnabledProfile(String str, String str2, int i, final IGetProfileCallback iGetProfileCallback) {
        UiccSlot[] uiccSlots;
        try {
            checkCallingPackage(str);
            String str3 = null;
            boolean z = false;
            for (UiccSlot uiccSlot : this.mUiccController.getUiccSlots()) {
                if (uiccSlot.getEid().equals(str2) && uiccSlot.isValidPortIndex(i)) {
                    str3 = uiccSlot.getIccId(i);
                    z = true;
                }
            }
            if (!z) {
                try {
                    iGetProfileCallback.onComplete(-2, (EuiccProfileInfo) null);
                } catch (RemoteException e) {
                    loge("getEnabledProfile callback failure due to invalid port slot.", e);
                }
            } else if (TextUtils.isEmpty(str3)) {
                try {
                    iGetProfileCallback.onComplete(-4, (EuiccProfileInfo) null);
                } catch (RemoteException e2) {
                    loge("getEnabledProfile callback failure.", e2);
                }
            } else {
                EuiccPort euiccPort = getEuiccPort(str2, i);
                if (euiccPort == null && (euiccPort = getFirstActiveEuiccPort(str2)) == null) {
                    try {
                        iGetProfileCallback.onComplete(-2, (EuiccProfileInfo) null);
                        return;
                    } catch (RemoteException e3) {
                        loge("getEnabledProfile callback failure.", e3);
                        return;
                    }
                }
                euiccPort.getProfile(str3, new AsyncResultCallback<EuiccProfileInfo>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.3
                    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                    public void onResult(EuiccProfileInfo euiccProfileInfo) {
                        try {
                            iGetProfileCallback.onComplete(0, euiccProfileInfo);
                        } catch (RemoteException e4) {
                            EuiccCardController.loge("getEnabledProfile callback failure.", e4);
                        }
                    }

                    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                    public void onException(Throwable th) {
                        try {
                            EuiccCardController.loge("getEnabledProfile callback onException: ", th);
                            iGetProfileCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccProfileInfo) null);
                        } catch (RemoteException e4) {
                            EuiccCardController.loge("getEnabledProfile callback failure.", e4);
                        }
                    }
                }, this.mEuiccMainThreadHandler);
            }
        } catch (SecurityException unused) {
            try {
                iGetProfileCallback.onComplete(-3, (EuiccProfileInfo) null);
            } catch (RemoteException e4) {
                loge("callback onComplete failure after checkCallingPackage.", e4);
            }
        }
    }

    public void disableProfile(String str, String str2, String str3, boolean z, final IDisableProfileCallback iDisableProfileCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort euiccPortFromIccId = getEuiccPortFromIccId(str2, str3);
            if (euiccPortFromIccId == null) {
                try {
                    iDisableProfileCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("disableProfile callback failure.", e);
                    return;
                }
            }
            euiccPortFromIccId.disableProfile(str3, z, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.4
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r1) {
                    try {
                        iDisableProfileCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("disableProfile callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("disableProfile callback onException: ", th);
                        iDisableProfileCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("disableProfile callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iDisableProfileCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void switchToProfile(String str, String str2, final String str3, int i, final boolean z, final ISwitchToProfileCallback iSwitchToProfileCallback) {
        try {
            checkCallingPackage(str);
            final EuiccPort euiccPort = getEuiccPort(str2, i);
            if (euiccPort == null) {
                try {
                    iSwitchToProfileCallback.onComplete(-2, (EuiccProfileInfo) null);
                    return;
                } catch (RemoteException e) {
                    loge("switchToProfile callback failure for portIndex :" + i, e);
                    return;
                }
            }
            euiccPort.getProfile(str3, new AsyncResultCallback<EuiccProfileInfo>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.5
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(final EuiccProfileInfo euiccProfileInfo) {
                    euiccPort.switchToProfile(str3, z, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.5.1
                        @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                        public void onResult(Void r2) {
                            try {
                                iSwitchToProfileCallback.onComplete(0, euiccProfileInfo);
                            } catch (RemoteException e2) {
                                EuiccCardController.loge("switchToProfile callback failure.", e2);
                            }
                        }

                        @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                        public void onException(Throwable th) {
                            try {
                                EuiccCardController.loge("switchToProfile callback onException: ", th);
                                C01775 c01775 = C01775.this;
                                iSwitchToProfileCallback.onComplete(EuiccCardController.this.getResultCode(th), euiccProfileInfo);
                            } catch (RemoteException e2) {
                                EuiccCardController.loge("switchToProfile callback failure.", e2);
                            }
                        }
                    }, EuiccCardController.this.mEuiccMainThreadHandler);
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getProfile in switchToProfile callback onException: ", th);
                        iSwitchToProfileCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccProfileInfo) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("switchToProfile callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iSwitchToProfileCallback.onComplete(-3, (EuiccProfileInfo) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void setNickname(String str, String str2, String str3, String str4, final ISetNicknameCallback iSetNicknameCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iSetNicknameCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("setNickname callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.setNickname(str3, str4, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.6
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r1) {
                    try {
                        iSetNicknameCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("setNickname callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("setNickname callback onException: ", th);
                        iSetNicknameCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("setNickname callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iSetNicknameCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void deleteProfile(String str, final String str2, String str3, final IDeleteProfileCallback iDeleteProfileCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iDeleteProfileCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("deleteProfile callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.deleteProfile(str3, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.7
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r3) {
                    Log.i("EuiccCardController", "Request subscription info list refresh after delete.");
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        SubscriptionManagerService.getInstance().updateEmbeddedSubscriptions(List.of(Integer.valueOf(EuiccCardController.this.mUiccController.convertToPublicCardId(str2))), null);
                    } else {
                        SubscriptionController.getInstance().requestEmbeddedSubscriptionInfoListRefresh(EuiccCardController.this.mUiccController.convertToPublicCardId(str2));
                    }
                    try {
                        iDeleteProfileCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("deleteProfile callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("deleteProfile callback onException: ", th);
                        iDeleteProfileCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("deleteProfile callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iDeleteProfileCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void resetMemory(String str, final String str2, int i, final IResetMemoryCallback iResetMemoryCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iResetMemoryCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("resetMemory callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.resetMemory(i, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.8
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r3) {
                    Log.i("EuiccCardController", "Request subscription info list refresh after reset memory.");
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        SubscriptionManagerService.getInstance().updateEmbeddedSubscriptions(List.of(Integer.valueOf(EuiccCardController.this.mUiccController.convertToPublicCardId(str2))), null);
                    } else {
                        SubscriptionController.getInstance().requestEmbeddedSubscriptionInfoListRefresh(EuiccCardController.this.mUiccController.convertToPublicCardId(str2));
                    }
                    try {
                        iResetMemoryCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("resetMemory callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("resetMemory callback onException: ", th);
                        iResetMemoryCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("resetMemory callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iResetMemoryCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getDefaultSmdpAddress(String str, String str2, final IGetDefaultSmdpAddressCallback iGetDefaultSmdpAddressCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetDefaultSmdpAddressCallback.onComplete(-2, (String) null);
                    return;
                } catch (RemoteException e) {
                    loge("getDefaultSmdpAddress callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getDefaultSmdpAddress(new AsyncResultCallback<String>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.9
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(String str3) {
                    try {
                        iGetDefaultSmdpAddressCallback.onComplete(0, str3);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getDefaultSmdpAddress callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getDefaultSmdpAddress callback onException: ", th);
                        iGetDefaultSmdpAddressCallback.onComplete(EuiccCardController.this.getResultCode(th), (String) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getDefaultSmdpAddress callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetDefaultSmdpAddressCallback.onComplete(-3, (String) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getSmdsAddress(String str, String str2, final IGetSmdsAddressCallback iGetSmdsAddressCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetSmdsAddressCallback.onComplete(-2, (String) null);
                    return;
                } catch (RemoteException e) {
                    loge("getSmdsAddress callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getSmdsAddress(new AsyncResultCallback<String>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.10
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(String str3) {
                    try {
                        iGetSmdsAddressCallback.onComplete(0, str3);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getSmdsAddress callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getSmdsAddress callback onException: ", th);
                        iGetSmdsAddressCallback.onComplete(EuiccCardController.this.getResultCode(th), (String) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getSmdsAddress callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetSmdsAddressCallback.onComplete(-3, (String) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void setDefaultSmdpAddress(String str, String str2, String str3, final ISetDefaultSmdpAddressCallback iSetDefaultSmdpAddressCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iSetDefaultSmdpAddressCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("setDefaultSmdpAddress callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.setDefaultSmdpAddress(str3, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.11
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r1) {
                    try {
                        iSetDefaultSmdpAddressCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("setDefaultSmdpAddress callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("setDefaultSmdpAddress callback onException: ", th);
                        iSetDefaultSmdpAddressCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("setDefaultSmdpAddress callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iSetDefaultSmdpAddressCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getRulesAuthTable(String str, String str2, final IGetRulesAuthTableCallback iGetRulesAuthTableCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetRulesAuthTableCallback.onComplete(-2, (EuiccRulesAuthTable) null);
                    return;
                } catch (RemoteException e) {
                    loge("getRulesAuthTable callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getRulesAuthTable(new AsyncResultCallback<EuiccRulesAuthTable>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.12
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccRulesAuthTable euiccRulesAuthTable) {
                    try {
                        iGetRulesAuthTableCallback.onComplete(0, euiccRulesAuthTable);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getRulesAuthTable callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getRulesAuthTable callback onException: ", th);
                        iGetRulesAuthTableCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccRulesAuthTable) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getRulesAuthTable callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetRulesAuthTableCallback.onComplete(-3, (EuiccRulesAuthTable) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getEuiccChallenge(String str, String str2, final IGetEuiccChallengeCallback iGetEuiccChallengeCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetEuiccChallengeCallback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("getEuiccChallenge callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getEuiccChallenge(new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.13
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr) {
                    try {
                        iGetEuiccChallengeCallback.onComplete(0, bArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccChallenge callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getEuiccChallenge callback onException: ", th);
                        iGetEuiccChallengeCallback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccChallenge callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetEuiccChallengeCallback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getEuiccInfo1(String str, String str2, final IGetEuiccInfo1Callback iGetEuiccInfo1Callback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetEuiccInfo1Callback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("getEuiccInfo1 callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getEuiccInfo1(new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.14
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr) {
                    try {
                        iGetEuiccInfo1Callback.onComplete(0, bArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccInfo1 callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getEuiccInfo1 callback onException: ", th);
                        iGetEuiccInfo1Callback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccInfo1 callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetEuiccInfo1Callback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void getEuiccInfo2(String str, String str2, final IGetEuiccInfo2Callback iGetEuiccInfo2Callback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iGetEuiccInfo2Callback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("getEuiccInfo2 callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.getEuiccInfo2(new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.15
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr) {
                    try {
                        iGetEuiccInfo2Callback.onComplete(0, bArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccInfo2 callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("getEuiccInfo2 callback onException: ", th);
                        iGetEuiccInfo2Callback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("getEuiccInfo2 callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iGetEuiccInfo2Callback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void authenticateServer(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, final IAuthenticateServerCallback iAuthenticateServerCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iAuthenticateServerCallback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("authenticateServer callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.authenticateServer(str3, bArr, bArr2, bArr3, bArr4, new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.16
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr5) {
                    try {
                        iAuthenticateServerCallback.onComplete(0, bArr5);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("authenticateServer callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("authenticateServer callback onException: ", th);
                        iAuthenticateServerCallback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("authenticateServer callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iAuthenticateServerCallback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void prepareDownload(String str, String str2, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, final IPrepareDownloadCallback iPrepareDownloadCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iPrepareDownloadCallback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("prepareDownload callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.prepareDownload(bArr, bArr2, bArr3, bArr4, new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.17
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr5) {
                    try {
                        iPrepareDownloadCallback.onComplete(0, bArr5);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("prepareDownload callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("prepareDownload callback onException: ", th);
                        iPrepareDownloadCallback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("prepareDownload callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iPrepareDownloadCallback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void loadBoundProfilePackage(String str, final String str2, byte[] bArr, final ILoadBoundProfilePackageCallback iLoadBoundProfilePackageCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iLoadBoundProfilePackageCallback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("loadBoundProfilePackage callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.loadBoundProfilePackage(bArr, new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.18
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr2) {
                    Log.i("EuiccCardController", "Request subscription info list refresh after install.");
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        SubscriptionManagerService.getInstance().updateEmbeddedSubscriptions(List.of(Integer.valueOf(EuiccCardController.this.mUiccController.convertToPublicCardId(str2))), null);
                    } else {
                        SubscriptionController.getInstance().requestEmbeddedSubscriptionInfoListRefresh(EuiccCardController.this.mUiccController.convertToPublicCardId(str2));
                    }
                    try {
                        iLoadBoundProfilePackageCallback.onComplete(0, bArr2);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("loadBoundProfilePackage callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("loadBoundProfilePackage callback onException: ", th);
                        iLoadBoundProfilePackageCallback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("loadBoundProfilePackage callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iLoadBoundProfilePackageCallback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void cancelSession(String str, String str2, byte[] bArr, int i, final ICancelSessionCallback iCancelSessionCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iCancelSessionCallback.onComplete(-2, (byte[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("cancelSession callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.cancelSession(bArr, i, new AsyncResultCallback<byte[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.19
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(byte[] bArr2) {
                    try {
                        iCancelSessionCallback.onComplete(0, bArr2);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("cancelSession callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("cancelSession callback onException: ", th);
                        iCancelSessionCallback.onComplete(EuiccCardController.this.getResultCode(th), (byte[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("cancelSession callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iCancelSessionCallback.onComplete(-3, (byte[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void listNotifications(String str, String str2, int i, final IListNotificationsCallback iListNotificationsCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iListNotificationsCallback.onComplete(-2, (EuiccNotification[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("listNotifications callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.listNotifications(i, new AsyncResultCallback<EuiccNotification[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.20
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccNotification[] euiccNotificationArr) {
                    try {
                        iListNotificationsCallback.onComplete(0, euiccNotificationArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("listNotifications callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("listNotifications callback onException: ", th);
                        iListNotificationsCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccNotification[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("listNotifications callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iListNotificationsCallback.onComplete(-3, (EuiccNotification[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void retrieveNotificationList(String str, String str2, int i, final IRetrieveNotificationListCallback iRetrieveNotificationListCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iRetrieveNotificationListCallback.onComplete(-2, (EuiccNotification[]) null);
                    return;
                } catch (RemoteException e) {
                    loge("retrieveNotificationList callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.retrieveNotificationList(i, new AsyncResultCallback<EuiccNotification[]>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.21
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccNotification[] euiccNotificationArr) {
                    try {
                        iRetrieveNotificationListCallback.onComplete(0, euiccNotificationArr);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("retrieveNotificationList callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("retrieveNotificationList callback onException: ", th);
                        iRetrieveNotificationListCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccNotification[]) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("retrieveNotificationList callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iRetrieveNotificationListCallback.onComplete(-3, (EuiccNotification[]) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void retrieveNotification(String str, String str2, int i, final IRetrieveNotificationCallback iRetrieveNotificationCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iRetrieveNotificationCallback.onComplete(-2, (EuiccNotification) null);
                    return;
                } catch (RemoteException e) {
                    loge("retrieveNotification callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.retrieveNotification(i, new AsyncResultCallback<EuiccNotification>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.22
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(EuiccNotification euiccNotification) {
                    try {
                        iRetrieveNotificationCallback.onComplete(0, euiccNotification);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("retrieveNotification callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("retrieveNotification callback onException: ", th);
                        iRetrieveNotificationCallback.onComplete(EuiccCardController.this.getResultCode(th), (EuiccNotification) null);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("retrieveNotification callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iRetrieveNotificationCallback.onComplete(-3, (EuiccNotification) null);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void removeNotificationFromList(String str, String str2, int i, final IRemoveNotificationFromListCallback iRemoveNotificationFromListCallback) {
        try {
            checkCallingPackage(str);
            EuiccPort firstActiveEuiccPort = getFirstActiveEuiccPort(str2);
            if (firstActiveEuiccPort == null) {
                try {
                    iRemoveNotificationFromListCallback.onComplete(-2);
                    return;
                } catch (RemoteException e) {
                    loge("removeNotificationFromList callback failure.", e);
                    return;
                }
            }
            firstActiveEuiccPort.removeNotificationFromList(i, new AsyncResultCallback<Void>() { // from class: com.android.internal.telephony.euicc.EuiccCardController.23
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(Void r1) {
                    try {
                        iRemoveNotificationFromListCallback.onComplete(0);
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("removeNotificationFromList callback failure.", e2);
                    }
                }

                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onException(Throwable th) {
                    try {
                        EuiccCardController.loge("removeNotificationFromList callback onException: ", th);
                        iRemoveNotificationFromListCallback.onComplete(EuiccCardController.this.getResultCode(th));
                    } catch (RemoteException e2) {
                        EuiccCardController.loge("removeNotificationFromList callback failure.", e2);
                    }
                }
            }, this.mEuiccMainThreadHandler);
        } catch (SecurityException unused) {
            try {
                iRemoveNotificationFromListCallback.onComplete(-3);
            } catch (RemoteException e2) {
                loge("callback onComplete failure after checkCallingPackage.", e2);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "Requires DUMP");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mCallingPackage=" + this.mCallingPackage);
        printWriter.println("mBestComponent=" + this.mBestComponent);
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    private static void loge(String str) {
        Log.e("EuiccCardController", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str, Throwable th) {
        Log.e("EuiccCardController", str, th);
    }
}
