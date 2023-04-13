package com.android.internal.app;

import android.Manifest;
import android.app.AppGlobals;
import android.app.KeyguardManager;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p001pm.IPackageManager;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.SuspendDialogInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.Slog;
import com.android.internal.C4057R;
import com.android.internal.app.AlertController;
import com.android.internal.util.ArrayUtils;
/* loaded from: classes4.dex */
public class SuspendedAppActivity extends AlertActivity implements DialogInterface.OnClickListener {
    public static final String EXTRA_ACTIVITY_OPTIONS = "com.android.internal.app.extra.ACTIVITY_OPTIONS";
    public static final String EXTRA_DIALOG_INFO = "com.android.internal.app.extra.DIALOG_INFO";
    public static final String EXTRA_SUSPENDED_PACKAGE = "com.android.internal.app.extra.SUSPENDED_PACKAGE";
    public static final String EXTRA_SUSPENDING_PACKAGE = "com.android.internal.app.extra.SUSPENDING_PACKAGE";
    public static final String EXTRA_UNSUSPEND_INTENT = "com.android.internal.app.extra.UNSUSPEND_INTENT";
    private static final String PACKAGE_NAME = "com.android.internal.app";
    private static final String TAG = SuspendedAppActivity.class.getSimpleName();
    private Intent mMoreDetailsIntent;
    private int mNeutralButtonAction;
    private IntentSender mOnUnsuspend;
    private Bundle mOptions;
    private PackageManager mPm;
    private SuspendDialogInfo mSuppliedDialogInfo;
    private BroadcastReceiver mSuspendModifiedReceiver = new BroadcastReceiver() { // from class: com.android.internal.app.SuspendedAppActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_PACKAGES_SUSPENSION_CHANGED.equals(intent.getAction())) {
                String[] modified = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                if (ArrayUtils.contains(modified, SuspendedAppActivity.this.mSuspendedPackage) && !SuspendedAppActivity.this.isFinishing()) {
                    Slog.m90w(SuspendedAppActivity.TAG, "Package " + SuspendedAppActivity.this.mSuspendedPackage + " has modified suspension conditions while dialog was visible. Finishing.");
                    SuspendedAppActivity.this.finish();
                }
            }
        }
    };
    private String mSuspendedPackage;
    private Resources mSuspendingAppResources;
    private String mSuspendingPackage;
    private int mUserId;
    private UsageStatsManager mUsm;

    private CharSequence getAppLabel(String packageName) {
        try {
            return this.mPm.getApplicationInfoAsUser(packageName, 0, this.mUserId).loadLabel(this.mPm);
        } catch (PackageManager.NameNotFoundException ne) {
            Slog.m95e(TAG, "Package " + packageName + " not found", ne);
            return packageName;
        }
    }

    private Intent getMoreDetailsActivity() {
        Intent moreDetailsIntent = new Intent(Intent.ACTION_SHOW_SUSPENDED_APP_DETAILS).setPackage(this.mSuspendingPackage);
        ResolveInfo resolvedInfo = this.mPm.resolveActivityAsUser(moreDetailsIntent, 786432, this.mUserId);
        if (resolvedInfo != null && resolvedInfo.activityInfo != null && Manifest.C0000permission.SEND_SHOW_SUSPENDED_APP_DETAILS.equals(resolvedInfo.activityInfo.permission)) {
            moreDetailsIntent.putExtra("android.intent.extra.PACKAGE_NAME", this.mSuspendedPackage).setFlags(335544320);
            return moreDetailsIntent;
        }
        return null;
    }

    private Drawable resolveIcon() {
        Resources resources;
        SuspendDialogInfo suspendDialogInfo = this.mSuppliedDialogInfo;
        int iconId = suspendDialogInfo != null ? suspendDialogInfo.getIconResId() : 0;
        if (iconId != 0 && (resources = this.mSuspendingAppResources) != null) {
            try {
                return resources.getDrawable(iconId, getTheme());
            } catch (Resources.NotFoundException e) {
                Slog.m96e(TAG, "Could not resolve drawable resource id " + iconId);
                return null;
            }
        }
        return null;
    }

    private String resolveTitle() {
        Resources resources;
        SuspendDialogInfo suspendDialogInfo = this.mSuppliedDialogInfo;
        if (suspendDialogInfo != null) {
            int titleId = suspendDialogInfo.getTitleResId();
            String title = this.mSuppliedDialogInfo.getTitle();
            if (titleId != 0 && (resources = this.mSuspendingAppResources) != null) {
                try {
                    return resources.getString(titleId);
                } catch (Resources.NotFoundException e) {
                    Slog.m96e(TAG, "Could not resolve string resource id " + titleId);
                }
            } else if (title != null) {
                return title;
            }
        }
        return getString(C4057R.string.app_suspended_title);
    }

    private String resolveDialogMessage() {
        Resources resources;
        CharSequence suspendedAppLabel = getAppLabel(this.mSuspendedPackage);
        SuspendDialogInfo suspendDialogInfo = this.mSuppliedDialogInfo;
        if (suspendDialogInfo != null) {
            int messageId = suspendDialogInfo.getDialogMessageResId();
            String message = this.mSuppliedDialogInfo.getDialogMessage();
            if (messageId != 0 && (resources = this.mSuspendingAppResources) != null) {
                try {
                    return resources.getString(messageId, suspendedAppLabel);
                } catch (Resources.NotFoundException e) {
                    Slog.m96e(TAG, "Could not resolve string resource id " + messageId);
                }
            } else if (message != null) {
                return String.format(getResources().getConfiguration().getLocales().get(0), message, suspendedAppLabel);
            }
        }
        return getString(C4057R.string.app_suspended_default_message, suspendedAppLabel, getAppLabel(this.mSuspendingPackage));
    }

    private String resolveNeutralButtonText() {
        int defaultButtonTextId;
        Resources resources;
        switch (this.mNeutralButtonAction) {
            case 0:
                if (this.mMoreDetailsIntent != null) {
                    defaultButtonTextId = C4057R.string.app_suspended_more_details;
                    break;
                } else {
                    return null;
                }
            case 1:
                defaultButtonTextId = C4057R.string.app_suspended_unsuspend_message;
                break;
            default:
                Slog.m90w(TAG, "Unknown neutral button action: " + this.mNeutralButtonAction);
                return null;
        }
        SuspendDialogInfo suspendDialogInfo = this.mSuppliedDialogInfo;
        if (suspendDialogInfo != null) {
            int buttonTextId = suspendDialogInfo.getNeutralButtonTextResId();
            String buttonText = this.mSuppliedDialogInfo.getNeutralButtonText();
            if (buttonTextId != 0 && (resources = this.mSuspendingAppResources) != null) {
                try {
                    return resources.getString(buttonTextId);
                } catch (Resources.NotFoundException e) {
                    Slog.m96e(TAG, "Could not resolve string resource id " + buttonTextId);
                }
            } else if (buttonText != null) {
                return buttonText;
            }
        }
        return getString(defaultButtonTextId);
    }

    @Override // com.android.internal.app.AlertActivity, android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mPm = getPackageManager();
        this.mUsm = (UsageStatsManager) getSystemService(UsageStatsManager.class);
        getWindow().setType(2008);
        Intent intent = getIntent();
        this.mOptions = intent.getBundleExtra(EXTRA_ACTIVITY_OPTIONS);
        int intExtra = intent.getIntExtra(Intent.EXTRA_USER_ID, -1);
        this.mUserId = intExtra;
        if (intExtra < 0) {
            Slog.wtf(TAG, "Invalid user: " + this.mUserId);
            finish();
            return;
        }
        this.mSuspendedPackage = intent.getStringExtra(EXTRA_SUSPENDED_PACKAGE);
        this.mSuspendingPackage = intent.getStringExtra(EXTRA_SUSPENDING_PACKAGE);
        this.mSuppliedDialogInfo = (SuspendDialogInfo) intent.getParcelableExtra(EXTRA_DIALOG_INFO, SuspendDialogInfo.class);
        this.mOnUnsuspend = (IntentSender) intent.getParcelableExtra(EXTRA_UNSUSPEND_INTENT, IntentSender.class);
        if (this.mSuppliedDialogInfo != null) {
            try {
                this.mSuspendingAppResources = createContextAsUser(UserHandle.m145of(this.mUserId), 0).getPackageManager().getResourcesForApplication(this.mSuspendingPackage);
            } catch (PackageManager.NameNotFoundException ne) {
                Slog.m95e(TAG, "Could not find resources for " + this.mSuspendingPackage, ne);
            }
        }
        SuspendDialogInfo suspendDialogInfo = this.mSuppliedDialogInfo;
        int neutralButtonAction = suspendDialogInfo != null ? suspendDialogInfo.getNeutralButtonAction() : 0;
        this.mNeutralButtonAction = neutralButtonAction;
        this.mMoreDetailsIntent = neutralButtonAction == 0 ? getMoreDetailsActivity() : null;
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mIcon = resolveIcon();
        ap.mTitle = resolveTitle();
        ap.mMessage = resolveDialogMessage();
        ap.mPositiveButtonText = getString(17039370);
        ap.mNeutralButtonText = resolveNeutralButtonText();
        ap.mNeutralButtonListener = this;
        ap.mPositiveButtonListener = this;
        requestDismissKeyguardIfNeeded(ap.mMessage);
        setupAlert();
        IntentFilter suspendModifiedFilter = new IntentFilter(Intent.ACTION_PACKAGES_SUSPENSION_CHANGED);
        registerReceiverAsUser(this.mSuspendModifiedReceiver, UserHandle.m145of(this.mUserId), suspendModifiedFilter, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mSuspendModifiedReceiver);
    }

    private void requestDismissKeyguardIfNeeded(CharSequence dismissMessage) {
        KeyguardManager km = (KeyguardManager) getSystemService(KeyguardManager.class);
        if (km.isKeyguardLocked()) {
            km.requestDismissKeyguard(this, dismissMessage, new KeyguardManager.KeyguardDismissCallback() { // from class: com.android.internal.app.SuspendedAppActivity.2
                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissError() {
                    Slog.m96e(SuspendedAppActivity.TAG, "Error while dismissing keyguard. Keeping the dialog visible.");
                }

                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissCancelled() {
                    Slog.m90w(SuspendedAppActivity.TAG, "Keyguard dismiss was cancelled. Finishing.");
                    SuspendedAppActivity.this.finish();
                }
            });
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -3:
                switch (this.mNeutralButtonAction) {
                    case 0:
                        Intent intent = this.mMoreDetailsIntent;
                        if (intent != null) {
                            startActivityAsUser(intent, this.mOptions, UserHandle.m145of(this.mUserId));
                            break;
                        } else {
                            Slog.wtf(TAG, "Neutral button should not have existed!");
                            break;
                        }
                    case 1:
                        IPackageManager ipm = AppGlobals.getPackageManager();
                        try {
                            String[] errored = ipm.setPackagesSuspendedAsUser(new String[]{this.mSuspendedPackage}, false, null, null, null, this.mSuspendingPackage, this.mUserId);
                            if (ArrayUtils.contains(errored, this.mSuspendedPackage)) {
                                Slog.m96e(TAG, "Could not unsuspend " + this.mSuspendedPackage);
                                break;
                            } else {
                                Intent reportUnsuspend = new Intent().setAction(Intent.ACTION_PACKAGE_UNSUSPENDED_MANUALLY).putExtra("android.intent.extra.PACKAGE_NAME", this.mSuspendedPackage).setPackage(this.mSuspendingPackage).addFlags(16777216);
                                sendBroadcastAsUser(reportUnsuspend, UserHandle.m145of(this.mUserId));
                                IntentSender intentSender = this.mOnUnsuspend;
                                if (intentSender != null) {
                                    try {
                                        intentSender.sendIntent(this, 0, null, null, null);
                                        break;
                                    } catch (IntentSender.SendIntentException e) {
                                        Slog.m95e(TAG, "Error while starting intent " + this.mOnUnsuspend, e);
                                        break;
                                    }
                                }
                            }
                        } catch (RemoteException re) {
                            Slog.m95e(TAG, "Can't talk to system process", re);
                            break;
                        }
                        break;
                    default:
                        Slog.m96e(TAG, "Unexpected action on neutral button: " + this.mNeutralButtonAction);
                        break;
                }
        }
        this.mUsm.reportUserInteraction(this.mSuspendingPackage, this.mUserId);
        finish();
    }

    public static Intent createSuspendedAppInterceptIntent(String suspendedPackage, String suspendingPackage, SuspendDialogInfo dialogInfo, Bundle options, IntentSender onUnsuspend, int userId) {
        return new Intent().setClassName("android", SuspendedAppActivity.class.getName()).putExtra(EXTRA_SUSPENDED_PACKAGE, suspendedPackage).putExtra(EXTRA_DIALOG_INFO, dialogInfo).putExtra(EXTRA_SUSPENDING_PACKAGE, suspendingPackage).putExtra(EXTRA_UNSUSPEND_INTENT, onUnsuspend).putExtra(EXTRA_ACTIVITY_OPTIONS, options).putExtra(Intent.EXTRA_USER_ID, userId).setFlags(276824064);
    }
}
