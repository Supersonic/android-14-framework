package android.service.games;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.p008os.Bundle;
import android.util.Slog;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes3.dex */
public final class GameSessionTrampolineActivity extends Activity {
    static final String FUTURE_KEY = "GameSessionTrampolineActivity.future";
    private static final String HAS_LAUNCHED_INTENT_KEY = "GameSessionTrampolineActivity.hasLaunchedIntent";
    static final String INTENT_KEY = "GameSessionTrampolineActivity.intent";
    static final String OPTIONS_KEY = "GameSessionTrampolineActivity.options";
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "GameSessionTrampoline";
    private boolean mHasLaunchedIntent = false;

    public static Intent createIntent(Intent targetIntent, Bundle options, AndroidFuture<GameSessionActivityResult> resultFuture) {
        Intent trampolineIntent = new Intent();
        trampolineIntent.setComponent(new ComponentName("android", "android.service.games.GameSessionTrampolineActivity"));
        trampolineIntent.putExtra(INTENT_KEY, targetIntent);
        trampolineIntent.putExtra(OPTIONS_KEY, options);
        trampolineIntent.putExtra(FUTURE_KEY, resultFuture);
        return trampolineIntent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mHasLaunchedIntent = savedInstanceState.getBoolean(HAS_LAUNCHED_INTENT_KEY);
        }
        if (this.mHasLaunchedIntent) {
            return;
        }
        this.mHasLaunchedIntent = true;
        try {
            startActivityAsCaller((Intent) getIntent().getParcelableExtra(INTENT_KEY, Intent.class), getIntent().getBundleExtra(OPTIONS_KEY), false, getUserId(), 1);
        } catch (Exception e) {
            Slog.m90w(TAG, "Unable to launch activity from game session");
            AndroidFuture<GameSessionActivityResult> future = (AndroidFuture) getIntent().getParcelableExtra(FUTURE_KEY, AndroidFuture.class);
            future.completeExceptionally(e);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(HAS_LAUNCHED_INTENT_KEY, this.mHasLaunchedIntent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 1) {
            throw new IllegalStateException("Unexpected request code: " + requestCode);
        }
        AndroidFuture<GameSessionActivityResult> future = (AndroidFuture) getIntent().getParcelableExtra(FUTURE_KEY, AndroidFuture.class);
        future.complete(new GameSessionActivityResult(resultCode, data));
        finish();
        overridePendingTransition(0, 0);
    }
}
