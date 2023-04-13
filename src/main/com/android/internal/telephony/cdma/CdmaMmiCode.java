package com.android.internal.telephony.cdma;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.internal.telephony.MmiCode;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.telephony.Rlog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class CdmaMmiCode extends Handler implements MmiCode {
    static Pattern sPatternSuppService = Pattern.compile("((\\*|#|\\*#|\\*\\*|##)(\\d{2,3})(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*))?)?)?)?#)(.*)");
    String mAction;
    Context mContext;
    String mDialingNumber;
    CharSequence mMessage;
    GsmCdmaPhone mPhone;
    String mPoundString;
    String mPwd;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mSc;
    String mSia;
    String mSib;
    String mSic;
    MmiCode.State mState;
    UiccCardApplication mUiccApplication;

    public static String getCallWaitingPrefix(boolean z) {
        return z ? "*74" : "*740";
    }

    @Override // com.android.internal.telephony.MmiCode
    public String getDialString() {
        return null;
    }

    @Override // com.android.internal.telephony.MmiCode
    public ResultReceiver getUssdCallbackReceiver() {
        return null;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isCancelable() {
        return false;
    }

    public static CdmaMmiCode newFromDialString(String str, GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        Matcher matcher = sPatternSuppService.matcher(str);
        if (matcher.matches()) {
            CdmaMmiCode cdmaMmiCode = new CdmaMmiCode(gsmCdmaPhone, uiccCardApplication);
            cdmaMmiCode.mPoundString = makeEmptyNull(matcher.group(1));
            cdmaMmiCode.mAction = makeEmptyNull(matcher.group(2));
            cdmaMmiCode.mSc = makeEmptyNull(matcher.group(3));
            cdmaMmiCode.mSia = makeEmptyNull(matcher.group(5));
            cdmaMmiCode.mSib = makeEmptyNull(matcher.group(7));
            cdmaMmiCode.mSic = makeEmptyNull(matcher.group(9));
            cdmaMmiCode.mPwd = makeEmptyNull(matcher.group(11));
            cdmaMmiCode.mDialingNumber = makeEmptyNull(matcher.group(12));
            return cdmaMmiCode;
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static String makeEmptyNull(String str) {
        if (str == null || str.length() != 0) {
            return str;
        }
        return null;
    }

    CdmaMmiCode(GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        super(gsmCdmaPhone.getHandler().getLooper());
        this.mState = MmiCode.State.PENDING;
        this.mPhone = gsmCdmaPhone;
        this.mContext = gsmCdmaPhone.getContext();
        this.mUiccApplication = uiccCardApplication;
    }

    @Override // com.android.internal.telephony.MmiCode
    public MmiCode.State getState() {
        return this.mState;
    }

    @Override // com.android.internal.telephony.MmiCode
    public CharSequence getMessage() {
        return this.mMessage;
    }

    @Override // com.android.internal.telephony.MmiCode
    public Phone getPhone() {
        return this.mPhone;
    }

    @Override // com.android.internal.telephony.MmiCode
    public void cancel() {
        MmiCode.State state = this.mState;
        if (state == MmiCode.State.COMPLETE || state == MmiCode.State.FAILED) {
            return;
        }
        this.mState = MmiCode.State.CANCELLED;
        this.mPhone.onMMIDone(this);
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isPinPukCommand() {
        String str = this.mSc;
        return str != null && (str.equals("04") || this.mSc.equals("042") || this.mSc.equals("05") || this.mSc.equals("052"));
    }

    boolean isRegister() {
        String str = this.mAction;
        return str != null && str.equals("**");
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isUssdRequest() {
        Rlog.w("CdmaMmiCode", "isUssdRequest is not implemented in CdmaMmiCode");
        return false;
    }

    @Override // com.android.internal.telephony.MmiCode
    public void processCode() {
        UiccCardApplication uiccCardApplication;
        try {
            if (isPinPukCommand()) {
                String str = this.mSia;
                String str2 = this.mSib;
                int length = str2.length();
                if (isRegister()) {
                    if (!str2.equals(this.mSic)) {
                        handlePasswordError(17040798);
                        return;
                    }
                    if (length >= 4 && length <= 8) {
                        if (this.mSc.equals("04") && (uiccCardApplication = this.mUiccApplication) != null && uiccCardApplication.getState() == IccCardApplicationStatus.AppState.APPSTATE_PUK) {
                            handlePasswordError(17040827);
                            return;
                        } else if (this.mUiccApplication != null) {
                            Rlog.d("CdmaMmiCode", "process mmi service code using UiccApp sc=" + this.mSc);
                            if (this.mSc.equals("04")) {
                                this.mUiccApplication.changeIccLockPassword(str, str2, obtainMessage(1, this));
                                return;
                            } else if (this.mSc.equals("042")) {
                                this.mUiccApplication.changeIccFdnPassword(str, str2, obtainMessage(1, this));
                                return;
                            } else if (this.mSc.equals("05")) {
                                this.mUiccApplication.supplyPuk(str, str2, obtainMessage(1, this));
                                return;
                            } else if (this.mSc.equals("052")) {
                                this.mUiccApplication.supplyPuk2(str, str2, obtainMessage(1, this));
                                return;
                            } else {
                                throw new RuntimeException("Unsupported service code=" + this.mSc);
                            }
                        } else {
                            throw new RuntimeException("No application mUiccApplicaiton is null");
                        }
                    }
                    handlePasswordError(17040476);
                    return;
                }
                throw new RuntimeException("Ivalid register/action=" + this.mAction);
            }
        } catch (RuntimeException unused) {
            this.mState = MmiCode.State.FAILED;
            this.mMessage = this.mContext.getText(17040808);
            this.mPhone.onMMIDone(this);
        }
    }

    private void handlePasswordError(int i) {
        this.mState = MmiCode.State.FAILED;
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        sb.append(this.mContext.getText(i));
        this.mMessage = sb;
        this.mPhone.onMMIDone(this);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what == 1) {
            onSetComplete(message, (AsyncResult) message.obj);
        } else {
            Rlog.e("CdmaMmiCode", "Unexpected reply");
        }
    }

    private CharSequence getScString() {
        return (this.mSc == null || !isPinPukCommand()) ? PhoneConfigurationManager.SSSS : this.mContext.getText(17039558);
    }

    private void onSetComplete(Message message, AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        Throwable th = asyncResult.exception;
        if (th != null) {
            this.mState = MmiCode.State.FAILED;
            if (th instanceof CommandException) {
                CommandException.Error commandError = ((CommandException) th).getCommandError();
                if (commandError == CommandException.Error.PASSWORD_INCORRECT) {
                    if (isPinPukCommand()) {
                        if (this.mSc.equals("05") || this.mSc.equals("052")) {
                            sb.append(this.mContext.getText(17039738));
                        } else {
                            sb.append(this.mContext.getText(17039737));
                        }
                        int i = message.arg1;
                        if (i <= 0) {
                            Rlog.d("CdmaMmiCode", "onSetComplete: PUK locked, cancel as lock screen will handle this");
                            this.mState = MmiCode.State.CANCELLED;
                        } else if (i > 0) {
                            Rlog.d("CdmaMmiCode", "onSetComplete: attemptsRemaining=" + i);
                            sb.append(this.mContext.getResources().getQuantityString(18153472, i, Integer.valueOf(i)));
                        }
                    } else {
                        sb.append(this.mContext.getText(17040932));
                    }
                } else if (commandError == CommandException.Error.SIM_PUK2) {
                    sb.append(this.mContext.getText(17039737));
                    sb.append("\n");
                    sb.append(this.mContext.getText(17040828));
                } else if (commandError == CommandException.Error.REQUEST_NOT_SUPPORTED) {
                    if (this.mSc.equals("04")) {
                        sb.append(this.mContext.getText(17040181));
                    }
                } else {
                    sb.append(this.mContext.getText(17040808));
                }
            } else {
                sb.append(this.mContext.getText(17040808));
            }
        } else if (isRegister()) {
            this.mState = MmiCode.State.COMPLETE;
            sb.append(this.mContext.getText(17041513));
        } else {
            this.mState = MmiCode.State.FAILED;
            sb.append(this.mContext.getText(17040808));
        }
        this.mMessage = sb;
        this.mPhone.onMMIDone(this);
    }

    public static String getCallForwardingPrefixAndNumber(int i, int i2, String str) {
        if (i2 != 0) {
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 3) {
                        Rlog.d("CdmaMmiCode", "getCallForwardingPrefix not match any prefix");
                    } else if (i == 3) {
                        return "*68" + str;
                    } else if (i == 0) {
                        return "*680";
                    }
                } else if (i == 3) {
                    return "*92" + str;
                } else if (i == 0) {
                    return "*920";
                }
            } else if (i == 3) {
                return "*90" + str;
            } else if (i == 0) {
                return "*900";
            }
        } else if (i == 3) {
            return "*72" + str;
        } else if (i == 0) {
            return "*720";
        }
        return PhoneConfigurationManager.SSSS;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isNetworkInitiatedUssd() {
        Rlog.w("CdmaMmiCode", "isNetworkInitiated is not implemented in CdmaMmiCode");
        return false;
    }
}
