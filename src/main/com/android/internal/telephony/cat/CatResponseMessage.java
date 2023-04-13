package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class CatResponseMessage {
    CommandDetails mCmdDet;
    ResultCode mResCode = ResultCode.OK;
    int mUsersMenuSelection = 0;
    String mUsersInput = null;
    boolean mUsersYesNoSelection = false;
    boolean mUsersConfirm = false;
    boolean mIncludeAdditionalInfo = false;
    int mAdditionalInfo = 0;
    int mEventValue = -1;
    byte[] mAddedInfo = null;

    public CatResponseMessage(CatCmdMessage catCmdMessage) {
        this.mCmdDet = null;
        this.mCmdDet = catCmdMessage.mCmdDet;
    }

    public void setResultCode(ResultCode resultCode) {
        this.mResCode = resultCode;
    }

    public void setMenuSelection(int i) {
        this.mUsersMenuSelection = i;
    }

    public void setInput(String str) {
        this.mUsersInput = str;
    }

    @UnsupportedAppUsage
    public void setEventDownload(int i, byte[] bArr) {
        this.mEventValue = i;
        this.mAddedInfo = bArr;
    }

    public void setYesNo(boolean z) {
        this.mUsersYesNoSelection = z;
    }

    public void setConfirmation(boolean z) {
        this.mUsersConfirm = z;
    }

    public void setAdditionalInfo(int i) {
        this.mIncludeAdditionalInfo = true;
        this.mAdditionalInfo = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommandDetails getCmdDetails() {
        return this.mCmdDet;
    }
}
