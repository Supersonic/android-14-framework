package com.android.server.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.sysprop.TelephonyProperties;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.widget.AdapterView;
import com.android.internal.app.AlertController;
import com.android.internal.globalactions.Action;
import com.android.internal.globalactions.ActionsAdapter;
import com.android.internal.globalactions.ActionsDialog;
import com.android.internal.globalactions.LongPressAction;
import com.android.internal.globalactions.SinglePressAction;
import com.android.internal.globalactions.ToggleAction;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.policy.WindowManagerPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
/* loaded from: classes2.dex */
public class LegacyGlobalActions implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
    public ActionsAdapter mAdapter;
    public ToggleAction mAirplaneModeOn;
    public final AudioManager mAudioManager;
    public final Context mContext;
    public ActionsDialog mDialog;
    public final EmergencyAffordanceManager mEmergencyAffordanceManager;
    public final boolean mHasTelephony;
    public boolean mHasVibrator;
    public ArrayList<Action> mItems;
    public final Runnable mOnDismiss;
    public final boolean mShowSilentToggle;
    public Action mSilentModeAction;
    public final WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
    public boolean mKeyguardShowing = false;
    public boolean mDeviceProvisioned = false;
    public ToggleAction.State mAirplaneState = ToggleAction.State.Off;
    public boolean mIsWaitingForEcmExit = false;
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.LegacyGlobalActions.9
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) {
                if ("globalactions".equals(intent.getStringExtra("reason"))) {
                    return;
                }
                LegacyGlobalActions.this.mHandler.sendEmptyMessage(0);
            } else if ("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED".equals(action) && !intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false) && LegacyGlobalActions.this.mIsWaitingForEcmExit) {
                LegacyGlobalActions.this.mIsWaitingForEcmExit = false;
                LegacyGlobalActions.this.changeAirplaneModeSystemSetting(true);
            }
        }
    };
    public PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.server.policy.LegacyGlobalActions.10
        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            if (LegacyGlobalActions.this.mHasTelephony) {
                boolean z = serviceState.getState() == 3;
                LegacyGlobalActions.this.mAirplaneState = z ? ToggleAction.State.On : ToggleAction.State.Off;
                LegacyGlobalActions.this.mAirplaneModeOn.updateState(LegacyGlobalActions.this.mAirplaneState);
                LegacyGlobalActions.this.mAdapter.notifyDataSetChanged();
            }
        }
    };
    public BroadcastReceiver mRingerModeReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.LegacyGlobalActions.11
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
                LegacyGlobalActions.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    public ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler()) { // from class: com.android.server.policy.LegacyGlobalActions.12
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            LegacyGlobalActions.this.onAirplaneModeChanged();
        }
    };
    public Handler mHandler = new Handler() { // from class: com.android.server.policy.LegacyGlobalActions.13
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                if (LegacyGlobalActions.this.mDialog != null) {
                    LegacyGlobalActions.this.mDialog.dismiss();
                    LegacyGlobalActions.this.mDialog = null;
                }
            } else if (i == 1) {
                LegacyGlobalActions.this.refreshSilentMode();
                LegacyGlobalActions.this.mAdapter.notifyDataSetChanged();
            } else if (i != 2) {
            } else {
                LegacyGlobalActions.this.handleShow();
            }
        }
    };
    public final IDreamManager mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));

    public LegacyGlobalActions(Context context, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs, Runnable runnable) {
        boolean z = false;
        this.mContext = context;
        this.mWindowManagerFuncs = windowManagerFuncs;
        this.mOnDismiss = runnable;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        context.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter, null, null, 2);
        this.mHasTelephony = context.getPackageManager().hasSystemFeature("android.hardware.telephony");
        ((TelephonyManager) context.getSystemService("phone")).listen(this.mPhoneStateListener, 1);
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator != null && vibrator.hasVibrator()) {
            z = true;
        }
        this.mHasVibrator = z;
        this.mShowSilentToggle = !context.getResources().getBoolean(17891860);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
    }

    public void showDialog(boolean z, boolean z2) {
        this.mKeyguardShowing = z;
        this.mDeviceProvisioned = z2;
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null) {
            actionsDialog.dismiss();
            this.mDialog = null;
            this.mHandler.sendEmptyMessage(2);
            return;
        }
        handleShow();
    }

    public final void awakenIfNecessary() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager != null) {
            try {
                if (iDreamManager.isDreaming()) {
                    this.mDreamManager.awaken();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    public final void handleShow() {
        awakenIfNecessary();
        this.mDialog = createDialog();
        prepareDialog();
        if (this.mAdapter.getCount() == 1 && (this.mAdapter.getItem(0) instanceof SinglePressAction) && !(this.mAdapter.getItem(0) instanceof LongPressAction)) {
            this.mAdapter.getItem(0).onPress();
            return;
        }
        ActionsDialog actionsDialog = this.mDialog;
        if (actionsDialog != null) {
            WindowManager.LayoutParams attributes = actionsDialog.getWindow().getAttributes();
            attributes.setTitle("LegacyGlobalActions");
            this.mDialog.getWindow().setAttributes(attributes);
            this.mDialog.show();
            this.mDialog.getWindow().getDecorView().setSystemUiVisibility(65536);
        }
    }

    public final ActionsDialog createDialog() {
        if (!this.mHasVibrator) {
            this.mSilentModeAction = new SilentModeToggleAction();
        } else {
            this.mSilentModeAction = new SilentModeTriStateAction(this.mContext, this.mAudioManager, this.mHandler);
        }
        this.mAirplaneModeOn = new ToggleAction(17302498, 17302500, 17040395, 17040394, 17040393) { // from class: com.android.server.policy.LegacyGlobalActions.1
            public boolean showBeforeProvisioning() {
                return false;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onToggle(boolean z) {
                if (LegacyGlobalActions.this.mHasTelephony && ((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                    LegacyGlobalActions.this.mIsWaitingForEcmExit = true;
                    Intent intent = new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null);
                    intent.addFlags(268435456);
                    LegacyGlobalActions.this.mContext.startActivity(intent);
                    return;
                }
                LegacyGlobalActions.this.changeAirplaneModeSystemSetting(z);
            }

            public void changeStateFromPress(boolean z) {
                if (LegacyGlobalActions.this.mHasTelephony && !((Boolean) TelephonyProperties.in_ecm_mode().orElse(Boolean.FALSE)).booleanValue()) {
                    ToggleAction.State state = z ? ToggleAction.State.TurningOn : ToggleAction.State.TurningOff;
                    ((ToggleAction) this).mState = state;
                    LegacyGlobalActions.this.mAirplaneState = state;
                }
            }
        };
        onAirplaneModeChanged();
        this.mItems = new ArrayList<>();
        String[] stringArray = this.mContext.getResources().getStringArray(17236075);
        ArraySet arraySet = new ArraySet();
        for (String str : stringArray) {
            if (!arraySet.contains(str)) {
                if ("power".equals(str)) {
                    this.mItems.add(new PowerAction(this.mContext, this.mWindowManagerFuncs));
                } else if ("airplane".equals(str)) {
                    this.mItems.add(this.mAirplaneModeOn);
                } else if ("bugreport".equals(str)) {
                    if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "bugreport_in_power_menu", 0, this.mContext.getUserId()) != 0 && isCurrentUserAdmin()) {
                        this.mItems.add(new BugReportAction());
                    }
                } else if ("silent".equals(str)) {
                    if (this.mShowSilentToggle) {
                        this.mItems.add(this.mSilentModeAction);
                    }
                } else if ("users".equals(str)) {
                    if (SystemProperties.getBoolean("fw.power_user_switcher", false)) {
                        addUsersToMenu(this.mItems);
                    }
                } else if ("settings".equals(str)) {
                    this.mItems.add(getSettingsAction());
                } else if ("lockdown".equals(str)) {
                    this.mItems.add(getLockdownAction());
                } else if ("voiceassist".equals(str)) {
                    this.mItems.add(getVoiceAssistAction());
                } else if ("assist".equals(str)) {
                    this.mItems.add(getAssistAction());
                } else if ("restart".equals(str)) {
                    this.mItems.add(new RestartAction(this.mContext, this.mWindowManagerFuncs));
                } else {
                    Log.e("LegacyGlobalActions", "Invalid global action key " + str);
                }
                arraySet.add(str);
            }
        }
        if (this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
            this.mItems.add(getEmergencyAction());
        }
        this.mAdapter = new ActionsAdapter(this.mContext, this.mItems, new BooleanSupplier() { // from class: com.android.server.policy.LegacyGlobalActions$$ExternalSyntheticLambda0
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$createDialog$0;
                lambda$createDialog$0 = LegacyGlobalActions.this.lambda$createDialog$0();
                return lambda$createDialog$0;
            }
        }, new BooleanSupplier() { // from class: com.android.server.policy.LegacyGlobalActions$$ExternalSyntheticLambda1
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$createDialog$1;
                lambda$createDialog$1 = LegacyGlobalActions.this.lambda$createDialog$1();
                return lambda$createDialog$1;
            }
        });
        AlertController.AlertParams alertParams = new AlertController.AlertParams(this.mContext);
        alertParams.mAdapter = this.mAdapter;
        alertParams.mOnClickListener = this;
        alertParams.mForceInverseBackground = true;
        ActionsDialog actionsDialog = new ActionsDialog(this.mContext, alertParams);
        actionsDialog.setCanceledOnTouchOutside(false);
        actionsDialog.getListView().setItemsCanFocus(true);
        actionsDialog.getListView().setLongClickable(true);
        actionsDialog.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.android.server.policy.LegacyGlobalActions.2
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                LongPressAction item = LegacyGlobalActions.this.mAdapter.getItem(i);
                if (item instanceof LongPressAction) {
                    return item.onLongPress();
                }
                return false;
            }
        });
        actionsDialog.getWindow().setType(2009);
        actionsDialog.getWindow().setFlags(IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
        actionsDialog.setOnDismissListener(this);
        return actionsDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDialog$0() {
        return this.mDeviceProvisioned;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDialog$1() {
        return this.mKeyguardShowing;
    }

    /* loaded from: classes2.dex */
    public class BugReportAction extends SinglePressAction implements LongPressAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public BugReportAction() {
            super(17302502, 17039768);
        }

        public void onPress() {
            if (ActivityManager.isUserAMonkey()) {
                return;
            }
            LegacyGlobalActions.this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.policy.LegacyGlobalActions.BugReportAction.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        MetricsLogger.action(LegacyGlobalActions.this.mContext, 292);
                        ActivityManager.getService().requestInteractiveBugReport();
                    } catch (RemoteException unused) {
                    }
                }
            }, 500L);
        }

        public boolean onLongPress() {
            if (ActivityManager.isUserAMonkey()) {
                return false;
            }
            try {
                MetricsLogger.action(LegacyGlobalActions.this.mContext, 293);
                ActivityManager.getService().requestFullBugReport();
            } catch (RemoteException unused) {
            }
            return false;
        }

        public String getStatus() {
            return LegacyGlobalActions.this.mContext.getString(17039767, Build.VERSION.RELEASE_OR_CODENAME, Build.ID);
        }
    }

    public final Action getSettingsAction() {
        return new SinglePressAction(17302851, 17040387) { // from class: com.android.server.policy.LegacyGlobalActions.3
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.settings.SETTINGS");
                intent.addFlags(335544320);
                LegacyGlobalActions.this.mContext.startActivity(intent);
            }
        };
    }

    public final Action getEmergencyAction() {
        return new SinglePressAction(17302215, 17040379) { // from class: com.android.server.policy.LegacyGlobalActions.4
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                LegacyGlobalActions.this.mEmergencyAffordanceManager.performEmergencyCall();
            }
        };
    }

    public final Action getAssistAction() {
        return new SinglePressAction(17302313, 17040377) { // from class: com.android.server.policy.LegacyGlobalActions.5
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.ASSIST");
                intent.addFlags(335544320);
                LegacyGlobalActions.this.mContext.startActivity(intent);
            }
        };
    }

    public final Action getVoiceAssistAction() {
        return new SinglePressAction(17302897, 17040391) { // from class: com.android.server.policy.LegacyGlobalActions.6
            public boolean showBeforeProvisioning() {
                return true;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                Intent intent = new Intent("android.intent.action.VOICE_ASSIST");
                intent.addFlags(335544320);
                LegacyGlobalActions.this.mContext.startActivity(intent);
            }
        };
    }

    public final Action getLockdownAction() {
        return new SinglePressAction(17301551, 17040381) { // from class: com.android.server.policy.LegacyGlobalActions.7
            public boolean showBeforeProvisioning() {
                return false;
            }

            public boolean showDuringKeyguard() {
                return true;
            }

            public void onPress() {
                new LockPatternUtils(LegacyGlobalActions.this.mContext).requireCredentialEntry(-1);
                try {
                    WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
                } catch (RemoteException e) {
                    Log.e("LegacyGlobalActions", "Error while trying to lock device.", e);
                }
            }
        };
    }

    public final UserInfo getCurrentUser() {
        try {
            return ActivityManager.getService().getCurrentUser();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public final boolean isCurrentUserAdmin() {
        UserInfo currentUser = getCurrentUser();
        return currentUser != null && currentUser.isAdmin();
    }

    public final void addUsersToMenu(ArrayList<Action> arrayList) {
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        if (userManager.isUserSwitcherEnabled()) {
            List<UserInfo> users = userManager.getUsers();
            UserInfo currentUser = getCurrentUser();
            for (final UserInfo userInfo : users) {
                if (userInfo.supportsSwitchToByUser()) {
                    boolean z = true;
                    if (currentUser != null ? currentUser.id != userInfo.id : userInfo.id != 0) {
                        z = false;
                    }
                    String str = userInfo.iconPath;
                    Drawable createFromPath = str != null ? Drawable.createFromPath(str) : null;
                    StringBuilder sb = new StringBuilder();
                    String str2 = userInfo.name;
                    if (str2 == null) {
                        str2 = "Primary";
                    }
                    sb.append(str2);
                    sb.append(z ? " ✔" : "");
                    arrayList.add(new SinglePressAction(17302721, createFromPath, sb.toString()) { // from class: com.android.server.policy.LegacyGlobalActions.8
                        public boolean showBeforeProvisioning() {
                            return false;
                        }

                        public boolean showDuringKeyguard() {
                            return true;
                        }

                        public void onPress() {
                            try {
                                ActivityManager.getService().switchUser(userInfo.id);
                            } catch (RemoteException e) {
                                Log.e("LegacyGlobalActions", "Couldn't switch user " + e);
                            }
                        }
                    });
                }
            }
        }
    }

    public final void prepareDialog() {
        refreshSilentMode();
        this.mAirplaneModeOn.updateState(this.mAirplaneState);
        this.mAdapter.notifyDataSetChanged();
        this.mDialog.getWindow().setType(2009);
        if (this.mShowSilentToggle) {
            this.mContext.registerReceiver(this.mRingerModeReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
        }
    }

    public final void refreshSilentMode() {
        if (this.mHasVibrator) {
            return;
        }
        this.mSilentModeAction.updateState(this.mAudioManager.getRingerMode() != 2 ? ToggleAction.State.On : ToggleAction.State.Off);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        Runnable runnable = this.mOnDismiss;
        if (runnable != null) {
            runnable.run();
        }
        if (this.mShowSilentToggle) {
            try {
                this.mContext.unregisterReceiver(this.mRingerModeReceiver);
            } catch (IllegalArgumentException e) {
                Log.w("LegacyGlobalActions", e);
            }
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (!(this.mAdapter.getItem(i) instanceof SilentModeTriStateAction)) {
            dialogInterface.dismiss();
        }
        this.mAdapter.getItem(i).onPress();
    }

    /* loaded from: classes2.dex */
    public class SilentModeToggleAction extends ToggleAction {
        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public SilentModeToggleAction() {
            super(17302332, 17302331, 17040390, 17040389, 17040388);
        }

        public void onToggle(boolean z) {
            if (z) {
                LegacyGlobalActions.this.mAudioManager.setRingerMode(0);
            } else {
                LegacyGlobalActions.this.mAudioManager.setRingerMode(2);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class SilentModeTriStateAction implements Action, View.OnClickListener {
        public final int[] ITEM_IDS = {16909323, 16909324, 16909325};
        public final AudioManager mAudioManager;
        public final Context mContext;
        public final Handler mHandler;

        public CharSequence getLabelForAccessibility(Context context) {
            return null;
        }

        public final int indexToRingerMode(int i) {
            return i;
        }

        public boolean isEnabled() {
            return true;
        }

        public void onPress() {
        }

        public final int ringerModeToIndex(int i) {
            return i;
        }

        public boolean showBeforeProvisioning() {
            return false;
        }

        public boolean showDuringKeyguard() {
            return true;
        }

        public SilentModeTriStateAction(Context context, AudioManager audioManager, Handler handler) {
            this.mAudioManager = audioManager;
            this.mHandler = handler;
            this.mContext = context;
        }

        public View create(Context context, View view, ViewGroup viewGroup, LayoutInflater layoutInflater) {
            View inflate = layoutInflater.inflate(17367173, viewGroup, false);
            int ringerModeToIndex = ringerModeToIndex(this.mAudioManager.getRingerMode());
            int i = 0;
            while (i < 3) {
                View findViewById = inflate.findViewById(this.ITEM_IDS[i]);
                findViewById.setSelected(ringerModeToIndex == i);
                findViewById.setTag(Integer.valueOf(i));
                findViewById.setOnClickListener(this);
                i++;
            }
            return inflate;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view.getTag() instanceof Integer) {
                this.mAudioManager.setRingerMode(indexToRingerMode(((Integer) view.getTag()).intValue()));
                this.mHandler.sendEmptyMessageDelayed(0, 300L);
            }
        }
    }

    public final void onAirplaneModeChanged() {
        if (this.mHasTelephony) {
            return;
        }
        ToggleAction.State state = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1 ? ToggleAction.State.On : ToggleAction.State.Off;
        this.mAirplaneState = state;
        this.mAirplaneModeOn.updateState(state);
    }

    public final void changeAirplaneModeSystemSetting(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", z ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", z);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        if (this.mHasTelephony) {
            return;
        }
        this.mAirplaneState = z ? ToggleAction.State.On : ToggleAction.State.Off;
    }
}
