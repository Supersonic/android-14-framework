package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.telephony.Rlog;
/* loaded from: classes.dex */
public class TelephonyAdminReceiver extends BroadcastReceiver {
    private final Context mContext;
    private boolean mDisallowCellular2gRestriction;
    private final Phone mPhone;
    private UserManager mUserManager = null;

    public TelephonyAdminReceiver(Context context, Phone phone) {
        this.mDisallowCellular2gRestriction = false;
        this.mContext = context;
        this.mPhone = phone;
        if (ensureUserManagerExists()) {
            this.mDisallowCellular2gRestriction = this.mUserManager.hasUserRestriction("no_cellular_2g");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.USER_RESTRICTIONS_CHANGED");
        context.registerReceiver(this, intentFilter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Rlog.d("TelephonyAdminReceiver", "Processing onReceive");
        if (context == null || intent == null) {
            return;
        }
        if (!intent.getAction().equals("android.os.action.USER_RESTRICTIONS_CHANGED")) {
            Rlog.d("TelephonyAdminReceiver", "Ignoring unexpected action: " + intent.getAction());
        } else if (ensureUserManagerExists()) {
            boolean hasUserRestriction = this.mUserManager.hasUserRestriction("no_cellular_2g");
            if (hasUserRestriction != this.mDisallowCellular2gRestriction) {
                Rlog.i("TelephonyAdminReceiver", "Updating allowed network types with new admin 2g restriction. no_cellular_2g: " + hasUserRestriction);
                this.mDisallowCellular2gRestriction = hasUserRestriction;
                this.mPhone.sendSubscriptionSettings(false);
                return;
            }
            Rlog.i("TelephonyAdminReceiver", "Skipping update of allowed network types. Restriction no_cellular_2g unchanged: " + this.mDisallowCellular2gRestriction);
        }
    }

    public boolean isCellular2gDisabled() {
        return this.mDisallowCellular2gRestriction;
    }

    private boolean ensureUserManagerExists() {
        if (this.mUserManager == null) {
            Rlog.d("TelephonyAdminReceiver", "No user manager. Attempting to resolve one.");
            this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        }
        if (this.mUserManager == null) {
            Rlog.e("TelephonyAdminReceiver", "Could not get a user manager instance. All operations will be no-ops until one is resolved");
            return false;
        }
        return true;
    }
}
