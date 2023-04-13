package com.android.internal.app;

import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.p008os.Bundle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class BlockedAppStreamingActivity extends AlertActivity {
    private static final String BLOCKED_COMPONENT_PLAYSTORE = "com.android.vending";
    private static final String BLOCKED_COMPONENT_SETTINGS = "com.android.settings";
    private static final String EXTRA_BLOCKED_ACTIVITY_INFO = "com.android.internal.app.extra.BLOCKED_ACTIVITY_INFO";
    private static final String EXTRA_STREAMED_DEVICE = "com.android.internal.app.extra.STREAMED_DEVICE";
    private static final String PACKAGE_NAME = "com.android.internal.app";
    private static final String TAG = "BlockedAppStreamingActivity";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.AlertActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        CharSequence appLabel = null;
        ActivityInfo activityInfo = (ActivityInfo) intent.getParcelableExtra(EXTRA_BLOCKED_ACTIVITY_INFO, ActivityInfo.class);
        if (activityInfo != null) {
            appLabel = activityInfo.loadLabel(getPackageManager());
        }
        if (TextUtils.isEmpty(appLabel)) {
            Slog.wtf(TAG, "Invalid activity info: " + activityInfo);
            finish();
            return;
        }
        CharSequence streamedDeviceName = intent.getCharSequenceExtra(EXTRA_STREAMED_DEVICE);
        if (!TextUtils.isEmpty(streamedDeviceName)) {
            if (TextUtils.equals(activityInfo.packageName, getPackageManager().getPermissionControllerPackageName())) {
                this.mAlertParams.mTitle = getString(C4057R.string.app_streaming_blocked_title_for_permission_dialog);
                this.mAlertParams.mMessage = getString(C4057R.string.app_streaming_blocked_message, streamedDeviceName);
            } else if (TextUtils.equals(activityInfo.packageName, BLOCKED_COMPONENT_PLAYSTORE)) {
                this.mAlertParams.mTitle = getString(C4057R.string.app_streaming_blocked_title_for_playstore_dialog);
                this.mAlertParams.mMessage = getString(C4057R.string.app_streaming_blocked_message, streamedDeviceName);
            } else if (TextUtils.equals(activityInfo.packageName, BLOCKED_COMPONENT_SETTINGS)) {
                this.mAlertParams.mTitle = getString(C4057R.string.app_streaming_blocked_title_for_settings_dialog);
                this.mAlertParams.mMessage = getString(C4057R.string.app_streaming_blocked_message_for_settings_dialog, streamedDeviceName);
            } else {
                this.mAlertParams.mMessage = getString(C4057R.string.app_streaming_blocked_message, streamedDeviceName);
            }
        } else {
            this.mAlertParams.mMessage = getString(C4057R.string.app_blocked_message, appLabel);
        }
        this.mAlertParams.mPositiveButtonText = getString(17039370);
        setupAlert();
    }

    public static Intent createIntent(ActivityInfo activityInfo, CharSequence streamedDeviceName) {
        return new Intent().setClassName("android", BlockedAppStreamingActivity.class.getName()).putExtra(EXTRA_BLOCKED_ACTIVITY_INFO, activityInfo).putExtra(EXTRA_STREAMED_DEVICE, streamedDeviceName);
    }
}
