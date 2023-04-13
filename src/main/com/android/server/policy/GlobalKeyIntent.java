package com.android.server.policy;

import android.content.ComponentName;
import android.content.Intent;
import android.view.KeyEvent;
/* loaded from: classes2.dex */
public final class GlobalKeyIntent {
    public final boolean mBeganFromNonInteractive;
    public final ComponentName mComponentName;
    public final KeyEvent mKeyEvent;

    public GlobalKeyIntent(ComponentName componentName, KeyEvent keyEvent, boolean z) {
        this.mComponentName = componentName;
        this.mKeyEvent = new KeyEvent(keyEvent);
        this.mBeganFromNonInteractive = z;
    }

    public Intent getIntent() {
        return new Intent("android.intent.action.GLOBAL_BUTTON").setComponent(this.mComponentName).setFlags(268435456).putExtra("android.intent.extra.KEY_EVENT", this.mKeyEvent).putExtra("EXTRA_BEGAN_FROM_NON_INTERACTIVE", this.mBeganFromNonInteractive);
    }
}
