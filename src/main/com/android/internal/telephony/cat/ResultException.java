package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.internal.telephony.PhoneConfigurationManager;
/* loaded from: classes.dex */
public class ResultException extends CatException {
    private int mAdditionalInfo;
    private String mExplanation;
    private ResultCode mResult;

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ResultException(ResultCode resultCode) {
        switch (C00901.$SwitchMap$com$android$internal$telephony$cat$ResultCode[resultCode.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                throw new AssertionError("For result code, " + resultCode + ", additional information must be given!");
            default:
                this.mResult = resultCode;
                this.mAdditionalInfo = -1;
                this.mExplanation = PhoneConfigurationManager.SSSS;
                return;
        }
    }

    /* renamed from: com.android.internal.telephony.cat.ResultException$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00901 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$cat$ResultCode;

        static {
            int[] iArr = new int[ResultCode.values().length];
            $SwitchMap$com$android$internal$telephony$cat$ResultCode = iArr;
            try {
                iArr[ResultCode.TERMINAL_CRNTLY_UNABLE_TO_PROCESS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.NETWORK_CRNTLY_UNABLE_TO_PROCESS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.LAUNCH_BROWSER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.MULTI_CARDS_CMD_ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.USIM_CALL_CONTROL_PERMANENT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.BIP_ERROR.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.FRAMES_ERROR.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$cat$ResultCode[ResultCode.MMS_ERROR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    public ResultException(ResultCode resultCode, String str) {
        this(resultCode);
        this.mExplanation = str;
    }

    public ResultException(ResultCode resultCode, int i) {
        this(resultCode);
        if (i < 0) {
            throw new AssertionError("Additional info must be greater than zero!");
        }
        this.mAdditionalInfo = i;
    }

    public ResultException(ResultCode resultCode, int i, String str) {
        this(resultCode, i);
        this.mExplanation = str;
    }

    public ResultCode result() {
        return this.mResult;
    }

    public boolean hasAdditionalInfo() {
        return this.mAdditionalInfo >= 0;
    }

    public int additionalInfo() {
        return this.mAdditionalInfo;
    }

    public String explanation() {
        return this.mExplanation;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return "result=" + this.mResult + " additionalInfo=" + this.mAdditionalInfo + " explantion=" + this.mExplanation;
    }
}
