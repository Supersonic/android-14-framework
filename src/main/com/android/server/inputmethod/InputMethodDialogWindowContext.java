package com.android.server.inputmethod;

import android.app.ActivityThread;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.internal.annotations.VisibleForTesting;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* loaded from: classes.dex */
public final class InputMethodDialogWindowContext {
    public Context mDialogWindowContext;

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public Context get(int i) {
        Context context = this.mDialogWindowContext;
        if (context == null || context.getDisplayId() != i) {
            this.mDialogWindowContext = new ContextThemeWrapper(ActivityThread.currentActivityThread().getSystemUiContext(i).createWindowContext(2012, null), 16974371);
        }
        return this.mDialogWindowContext;
    }
}
