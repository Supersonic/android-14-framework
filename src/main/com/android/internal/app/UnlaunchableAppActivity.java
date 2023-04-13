package com.android.internal.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p001pm.ResolveInfo;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.telecom.TelecomManager;
import android.util.Log;
import com.android.internal.C4057R;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class UnlaunchableAppActivity extends Activity implements DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
    private static final String EXTRA_UNLAUNCHABLE_REASON = "unlaunchable_reason";
    private static final String TAG = "UnlaunchableAppActivity";
    private static final int UNLAUNCHABLE_REASON_QUIET_MODE = 1;
    private int mReason;
    private IntentSender mTarget;
    private TelecomManager mTelecomManager;
    private int mUserId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        boolean showEmergencyCallButton;
        AlertDialog.Builder builder;
        String dialogMessage;
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        Intent intent = getIntent();
        this.mTelecomManager = (TelecomManager) getSystemService(TelecomManager.class);
        this.mReason = intent.getIntExtra(EXTRA_UNLAUNCHABLE_REASON, -1);
        this.mUserId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000);
        this.mTarget = (IntentSender) intent.getParcelableExtra(Intent.EXTRA_INTENT, IntentSender.class);
        if (this.mUserId != -10000) {
            if (this.mReason != 1) {
                Log.wtf(TAG, "Invalid unlaunchable type: " + this.mReason);
                finish();
                return;
            }
            String targetPackageName = intent.getStringExtra("android.intent.extra.PACKAGE_NAME");
            if (targetPackageName != null && targetPackageName.equals(this.mTelecomManager.getDefaultDialerPackage(UserHandle.m145of(this.mUserId)))) {
                showEmergencyCallButton = true;
            } else {
                showEmergencyCallButton = false;
            }
            if (showEmergencyCallButton) {
                builder = new AlertDialog.Builder(this, C4057R.C4062style.AlertDialogWithEmergencyButton);
                dialogMessage = getDialogMessage(C4057R.string.work_mode_dialer_off_message);
                builder.setNeutralButton(C4057R.string.work_mode_emergency_call_button, this);
            } else {
                builder = new AlertDialog.Builder(this);
                dialogMessage = getDialogMessage(C4057R.string.work_mode_off_message);
            }
            builder.setTitle(getDialogTitle()).setMessage(dialogMessage).setOnDismissListener(this).setPositiveButton(C4057R.string.work_mode_turn_on, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            AlertDialog dialog = builder.create();
            dialog.create();
            if (showEmergencyCallButton) {
                dialog.getWindow().findViewById(C4057R.C4059id.parentPanel).setPadding(0, 0, 0, 30);
                dialog.getWindow().findViewById(16908315).setOutlineProvider(null);
            }
            getWindow().setHideOverlayWindows(true);
            dialog.getButton(-1).setFilterTouchesWhenObscured(true);
            dialog.show();
            return;
        }
        Log.wtf(TAG, "Invalid user id: " + this.mUserId + ". Stopping.");
        finish();
    }

    private String getDialogTitle() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.UNLAUNCHABLE_APP_WORK_PAUSED_TITLE, new Supplier() { // from class: com.android.internal.app.UnlaunchableAppActivity$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDialogTitle$0;
                lambda$getDialogTitle$0 = UnlaunchableAppActivity.this.lambda$getDialogTitle$0();
                return lambda$getDialogTitle$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDialogTitle$0() {
        return getString(C4057R.string.work_mode_off_title);
    }

    private String getDialogMessage(final int dialogMessageString) {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.UNLAUNCHABLE_APP_WORK_PAUSED_MESSAGE, new Supplier() { // from class: com.android.internal.app.UnlaunchableAppActivity$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDialogMessage$1;
                lambda$getDialogMessage$1 = UnlaunchableAppActivity.this.lambda$getDialogMessage$1(dialogMessageString);
                return lambda$getDialogMessage$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDialogMessage$1(int dialogMessageString) {
        return getString(dialogMessageString);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        finish();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (this.mReason != 1) {
            return;
        }
        if (which == -1) {
            final UserManager userManager = UserManager.get(this);
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.internal.app.UnlaunchableAppActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    UnlaunchableAppActivity.this.lambda$onClick$2(userManager);
                }
            });
        } else if (which == -3) {
            launchEmergencyDialer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$2(UserManager userManager) {
        userManager.requestQuietModeEnabled(false, UserHandle.m145of(this.mUserId), this.mTarget);
    }

    private void launchEmergencyDialer() {
        startActivity(this.mTelecomManager.createLaunchEmergencyDialerIntent(null).setFlags(343932928));
    }

    private static final Intent createBaseIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("android", UnlaunchableAppActivity.class.getName()));
        intent.setFlags(276824064);
        return intent;
    }

    public static Intent createInQuietModeDialogIntent(int userId) {
        Intent intent = createBaseIntent();
        intent.putExtra(EXTRA_UNLAUNCHABLE_REASON, 1);
        intent.putExtra(Intent.EXTRA_USER_HANDLE, userId);
        return intent;
    }

    public static Intent createInQuietModeDialogIntent(int userId, IntentSender target, ResolveInfo resolveInfo) {
        Intent intent = createInQuietModeDialogIntent(userId);
        intent.putExtra(Intent.EXTRA_INTENT, target);
        if (resolveInfo != null) {
            intent.putExtra("android.intent.extra.PACKAGE_NAME", resolveInfo.getComponentInfo().packageName);
        }
        return intent;
    }
}
