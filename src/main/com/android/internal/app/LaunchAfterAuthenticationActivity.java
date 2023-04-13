package com.android.internal.app;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.IntentSender;
import android.p008os.Bundle;
import android.util.Slog;
import java.util.Objects;
/* loaded from: classes4.dex */
public class LaunchAfterAuthenticationActivity extends Activity {
    private static final String EXTRA_ON_SUCCESS_INTENT = "com.android.internal.app.extra.ON_SUCCESS_INTENT";
    private static final String TAG = LaunchAfterAuthenticationActivity.class.getSimpleName();

    public static Intent createLaunchAfterAuthenticationIntent(IntentSender onSuccessIntent) {
        return new Intent().setClassName("android", LaunchAfterAuthenticationActivity.class.getName()).putExtra(EXTRA_ON_SUCCESS_INTENT, onSuccessIntent).setFlags(276824064);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        IntentSender onSuccessIntent = (IntentSender) getIntent().getParcelableExtra(EXTRA_ON_SUCCESS_INTENT, IntentSender.class);
        requestDismissKeyguardIfNeeded(onSuccessIntent);
    }

    private void requestDismissKeyguardIfNeeded(final IntentSender onSuccessIntent) {
        KeyguardManager km = (KeyguardManager) Objects.requireNonNull((KeyguardManager) getSystemService(KeyguardManager.class));
        if (km.isKeyguardLocked()) {
            km.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() { // from class: com.android.internal.app.LaunchAfterAuthenticationActivity.1
                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissCancelled() {
                    LaunchAfterAuthenticationActivity.this.finish();
                }

                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissSucceeded() {
                    IntentSender intentSender = onSuccessIntent;
                    if (intentSender != null) {
                        LaunchAfterAuthenticationActivity.this.onUnlocked(intentSender);
                    }
                    LaunchAfterAuthenticationActivity.this.finish();
                }

                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissError() {
                    Slog.m96e(LaunchAfterAuthenticationActivity.TAG, "Error while dismissing keyguard.");
                    LaunchAfterAuthenticationActivity.this.finish();
                }
            });
        } else {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUnlocked(IntentSender targetIntent) {
        try {
            targetIntent.sendIntent(this, 0, null, null, null);
        } catch (IntentSender.SendIntentException e) {
            Slog.m95e(TAG, "Error while sending original intent", e);
        }
    }
}
