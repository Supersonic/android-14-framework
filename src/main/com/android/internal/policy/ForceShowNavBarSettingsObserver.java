package com.android.internal.policy;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.p008os.Handler;
import android.provider.Settings;
import java.util.Collection;
/* loaded from: classes4.dex */
public class ForceShowNavBarSettingsObserver extends ContentObserver {
    private Context mContext;
    private Runnable mOnChangeRunnable;

    public ForceShowNavBarSettingsObserver(Handler handler, Context context) {
        super(handler);
        this.mContext = context;
    }

    public void setOnChangeRunnable(Runnable r) {
        this.mOnChangeRunnable = r;
    }

    public void register() {
        ContentResolver r = this.mContext.getContentResolver();
        r.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.NAV_BAR_FORCE_VISIBLE), false, this, -1);
    }

    public void unregister() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange, Collection<Uri> uris, int flags, int userId) {
        Runnable runnable;
        if (userId == ActivityManager.getCurrentUser() && (runnable = this.mOnChangeRunnable) != null) {
            runnable.run();
        }
    }

    public boolean isEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), Settings.Secure.NAV_BAR_FORCE_VISIBLE, 0, -2) == 1;
    }
}
