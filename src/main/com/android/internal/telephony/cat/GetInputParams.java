package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class GetInputParams extends CommandParams {
    Input mInput;

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GetInputParams(CommandDetails commandDetails, Input input) {
        super(commandDetails);
        this.mInput = input;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.telephony.cat.CommandParams
    public boolean setIcon(Bitmap bitmap) {
        Input input;
        if (bitmap == null || (input = this.mInput) == null) {
            return true;
        }
        input.icon = bitmap;
        return true;
    }
}
