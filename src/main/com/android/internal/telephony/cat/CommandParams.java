package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
import com.android.internal.telephony.cat.AppInterface;
/* loaded from: classes.dex */
public class CommandParams {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    CommandDetails mCmdDet;
    boolean mLoadIconFailed = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setIcon(Bitmap bitmap) {
        return true;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public CommandParams(CommandDetails commandDetails) {
        this.mCmdDet = commandDetails;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AppInterface.CommandType getCommandType() {
        return AppInterface.CommandType.fromInt(this.mCmdDet.typeOfCommand);
    }

    public String toString() {
        return this.mCmdDet.toString();
    }
}
