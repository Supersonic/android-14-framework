package android.service.dreams;

import android.app.Activity;
import android.p008os.Bundle;
import android.service.dreams.DreamService;
import android.text.TextUtils;
/* loaded from: classes3.dex */
public class DreamActivity extends Activity {
    static final String EXTRA_CALLBACK = "binder";
    static final String EXTRA_DREAM_TITLE = "title";
    private DreamService.DreamActivityCallbacks mCallback;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        Object callback = getIntent().getExtras().getBinder("binder");
        if (callback instanceof DreamService.DreamActivityCallbacks) {
            DreamService.DreamActivityCallbacks dreamActivityCallbacks = (DreamService.DreamActivityCallbacks) callback;
            this.mCallback = dreamActivityCallbacks;
            dreamActivityCallbacks.onActivityCreated(this);
            return;
        }
        this.mCallback = null;
        finishAndRemoveTask();
    }

    @Override // android.app.Activity
    public void onDestroy() {
        DreamService.DreamActivityCallbacks dreamActivityCallbacks = this.mCallback;
        if (dreamActivityCallbacks != null) {
            dreamActivityCallbacks.onActivityDestroyed();
        }
        super.onDestroy();
    }
}
