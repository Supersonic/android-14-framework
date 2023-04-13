package com.android.internal.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.p008os.Build;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import com.android.internal.C4057R;
/* loaded from: classes3.dex */
public class EmergencyAffordanceManager {
    private static final String EMERGENCY_CALL_NUMBER_SETTING = "emergency_affordance_number";
    public static final boolean ENABLED = true;
    private static final String FORCE_EMERGENCY_AFFORDANCE_SETTING = "force_emergency_affordance";
    private final Context mContext;

    public EmergencyAffordanceManager(Context context) {
        this.mContext = context;
    }

    public final void performEmergencyCall() {
        performEmergencyCall(this.mContext);
    }

    private static Uri getPhoneUri(Context context) {
        String override;
        String number = context.getResources().getString(C4057R.string.config_emergency_call_number);
        if (Build.IS_DEBUGGABLE && (override = Settings.Global.getString(context.getContentResolver(), EMERGENCY_CALL_NUMBER_SETTING)) != null) {
            number = override;
        }
        return Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null);
    }

    private static void performEmergencyCall(Context context) {
        Intent intent = new Intent(Intent.ACTION_CALL_EMERGENCY);
        intent.setData(getPhoneUri(context));
        intent.setFlags(268435456);
        context.startActivityAsUser(intent, UserHandle.CURRENT);
    }

    public boolean needsEmergencyAffordance() {
        if (forceShowing()) {
            return true;
        }
        return isEmergencyAffordanceNeeded();
    }

    private boolean isEmergencyAffordanceNeeded() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.EMERGENCY_AFFORDANCE_NEEDED, 0) != 0;
    }

    private boolean forceShowing() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), FORCE_EMERGENCY_AFFORDANCE_SETTING, 0) != 0;
    }
}
