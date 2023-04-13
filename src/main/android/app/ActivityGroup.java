package android.app;

import android.content.Intent;
import android.p008os.Bundle;
import java.util.HashMap;
@Deprecated
/* loaded from: classes.dex */
public class ActivityGroup extends Activity {
    static final String PARENT_NON_CONFIG_INSTANCE_KEY = "android:parent_non_config_instance";
    private static final String STATES_KEY = "android:states";
    protected LocalActivityManager mLocalActivityManager;

    public ActivityGroup() {
        this(true);
    }

    public ActivityGroup(boolean singleActivityMode) {
        this.mLocalActivityManager = new LocalActivityManager(this, singleActivityMode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle states = savedInstanceState != null ? savedInstanceState.getBundle(STATES_KEY) : null;
        this.mLocalActivityManager.dispatchCreate(states);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.mLocalActivityManager.dispatchResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle state = this.mLocalActivityManager.saveInstanceState();
        if (state != null) {
            outState.putBundle(STATES_KEY, state);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.mLocalActivityManager.dispatchPause(isFinishing());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        this.mLocalActivityManager.dispatchStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        this.mLocalActivityManager.dispatchDestroy(isFinishing());
    }

    @Override // android.app.Activity
    public HashMap<String, Object> onRetainNonConfigurationChildInstances() {
        return this.mLocalActivityManager.dispatchRetainNonConfigurationInstance();
    }

    public Activity getCurrentActivity() {
        return this.mLocalActivityManager.getCurrentActivity();
    }

    public final LocalActivityManager getLocalActivityManager() {
        return this.mLocalActivityManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.app.Activity
    public void dispatchActivityResult(String who, int requestCode, int resultCode, Intent data, String reason) {
        Activity act;
        if (who != null && (act = this.mLocalActivityManager.getActivity(who)) != null) {
            act.onActivityResult(requestCode, resultCode, data);
        } else {
            super.dispatchActivityResult(who, requestCode, resultCode, data, reason);
        }
    }
}
