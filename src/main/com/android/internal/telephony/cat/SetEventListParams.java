package com.android.internal.telephony.cat;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class SetEventListParams extends CommandParams {
    int[] mEventInfo;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetEventListParams(CommandDetails commandDetails, int[] iArr) {
        super(commandDetails);
        this.mEventInfo = iArr;
    }
}
