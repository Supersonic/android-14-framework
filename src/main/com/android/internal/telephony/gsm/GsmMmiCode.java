package com.android.internal.telephony.gsm;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.ResultReceiver;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallForwardInfo;
import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.internal.telephony.MmiCode;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.gsm.SsData;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class GsmMmiCode extends Handler implements MmiCode {
    @UnsupportedAppUsage
    static Pattern sPatternSuppService = Pattern.compile("((\\*|#|\\*#|\\*\\*|##)(\\d{2,3})(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*))?)?)?)?#)(.*)");
    private static String[] sTwoDigitNumberPattern;
    String mAction;
    private ResultReceiver mCallbackReceiver;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    Context mContext;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String mDialingNumber;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    IccRecords mIccRecords;
    private boolean mIsCallFwdReg;
    private boolean mIsNetworkInitiatedUSSD;
    private boolean mIsPendingUSSD;
    private boolean mIsSsInfo;
    private boolean mIsUssdRequest;
    CharSequence mMessage;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    GsmCdmaPhone mPhone;
    String mPoundString;
    String mPwd;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mSc;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mSia;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mSib;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mSic;
    MmiCode.State mState;
    UiccCardApplication mUiccApplication;

    private boolean isServiceClassVoiceorNone(int i) {
        return (i & 1) != 0 || i == 0;
    }

    public static boolean isVoiceUnconditionalForwarding(int i, int i2) {
        return (i == 0 || i == 4) && ((i2 & 1) != 0 || i2 == 0);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static GsmMmiCode newFromDialString(String str, GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        return newFromDialString(str, gsmCdmaPhone, uiccCardApplication, null);
    }

    public static GsmMmiCode newFromDialString(String str, GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication, ResultReceiver resultReceiver) {
        if ((gsmCdmaPhone.getServiceState().getVoiceRoaming() && gsmCdmaPhone.supportsConversionOfCdmaCallerIdMmiCodesWhileRoaming()) || (isEmergencyNumber(gsmCdmaPhone, str) && isCarrierSupportCallerIdVerticalServiceCodes(gsmCdmaPhone))) {
            str = convertCdmaMmiCodesTo3gppMmiCodes(str);
        }
        Matcher matcher = sPatternSuppService.matcher(str);
        GsmMmiCode gsmMmiCode = null;
        if (matcher.matches()) {
            GsmMmiCode gsmMmiCode2 = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
            gsmMmiCode2.mPoundString = makeEmptyNull(matcher.group(1));
            gsmMmiCode2.mAction = makeEmptyNull(matcher.group(2));
            gsmMmiCode2.mSc = makeEmptyNull(matcher.group(3));
            gsmMmiCode2.mSia = makeEmptyNull(matcher.group(5));
            gsmMmiCode2.mSib = makeEmptyNull(matcher.group(7));
            gsmMmiCode2.mSic = makeEmptyNull(matcher.group(9));
            gsmMmiCode2.mPwd = makeEmptyNull(matcher.group(11));
            String makeEmptyNull = makeEmptyNull(matcher.group(12));
            gsmMmiCode2.mDialingNumber = makeEmptyNull;
            if (makeEmptyNull != null && makeEmptyNull.endsWith("#") && str.endsWith("#")) {
                gsmMmiCode = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
                gsmMmiCode.mPoundString = str;
            } else if (!gsmMmiCode2.isFacToDial()) {
                gsmMmiCode = gsmMmiCode2;
            }
        } else if (str.endsWith("#")) {
            gsmMmiCode = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
            gsmMmiCode.mPoundString = str;
        } else if (!isTwoDigitShortCode(gsmCdmaPhone.getContext(), gsmCdmaPhone.getSubId(), str) && isShortCode(str, gsmCdmaPhone)) {
            gsmMmiCode = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
            gsmMmiCode.mDialingNumber = str;
        }
        if (gsmMmiCode != null) {
            gsmMmiCode.mCallbackReceiver = resultReceiver;
        }
        return gsmMmiCode;
    }

    private static String convertCdmaMmiCodesTo3gppMmiCodes(String str) {
        Matcher matcher = MmiCode.sPatternCdmaMmiCodeWhileRoaming.matcher(str);
        if (matcher.matches()) {
            String makeEmptyNull = makeEmptyNull(matcher.group(1));
            String group = matcher.group(2);
            String makeEmptyNull2 = makeEmptyNull(matcher.group(3));
            if (makeEmptyNull.equals("67") && makeEmptyNull2 != null) {
                return "#31#" + group + makeEmptyNull2;
            } else if (!makeEmptyNull.equals("82") || makeEmptyNull2 == null) {
                return str;
            } else {
                return "*31#" + group + makeEmptyNull2;
            }
        }
        return str;
    }

    public static GsmMmiCode newNetworkInitiatedUssd(String str, boolean z, GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        GsmMmiCode gsmMmiCode = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
        gsmMmiCode.mMessage = str;
        gsmMmiCode.mIsUssdRequest = z;
        gsmMmiCode.mIsNetworkInitiatedUSSD = true;
        if (z) {
            gsmMmiCode.mIsPendingUSSD = true;
            gsmMmiCode.mState = MmiCode.State.PENDING;
        } else {
            gsmMmiCode.mState = MmiCode.State.COMPLETE;
        }
        return gsmMmiCode;
    }

    public static GsmMmiCode newFromUssdUserInput(String str, GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        GsmMmiCode gsmMmiCode = new GsmMmiCode(gsmCdmaPhone, uiccCardApplication);
        gsmMmiCode.mMessage = str;
        gsmMmiCode.mState = MmiCode.State.PENDING;
        gsmMmiCode.mIsPendingUSSD = true;
        return gsmMmiCode;
    }

    public void processSsData(AsyncResult asyncResult) {
        Rlog.d("GsmMmiCode", "In processSsData");
        this.mIsSsInfo = true;
        try {
            parseSsData((SsData) asyncResult.result);
        } catch (ClassCastException e) {
            Rlog.e("GsmMmiCode", "Class Cast Exception in parsing SS Data : " + e);
        } catch (NullPointerException e2) {
            Rlog.e("GsmMmiCode", "Null Pointer Exception in parsing SS Data : " + e2);
        }
    }

    void parseSsData(SsData ssData) {
        CommandException fromRilErrno = CommandException.fromRilErrno(ssData.result);
        this.mSc = getScStringFromScType(ssData.serviceType);
        this.mAction = getActionStringFromReqType(ssData.requestType);
        Rlog.d("GsmMmiCode", "parseSsData msc = " + this.mSc + ", action = " + this.mAction + ", ex = " + fromRilErrno);
        int i = C02131.$SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[ssData.requestType.ordinal()];
        if (i == 1 || i == 2 || i == 3 || i == 4) {
            if (ssData.result == 0 && ssData.serviceType.isTypeUnConditional()) {
                SsData.RequestType requestType = ssData.requestType;
                boolean z = (requestType == SsData.RequestType.SS_ACTIVATION || requestType == SsData.RequestType.SS_REGISTRATION) && isServiceClassVoiceorNone(ssData.serviceClass);
                Rlog.d("GsmMmiCode", "setVoiceCallForwardingFlag cffEnabled: " + z);
                this.mPhone.setVoiceCallForwardingFlag(1, z, null);
            }
            onSetComplete(null, new AsyncResult((Object) null, ssData.cfInfo, fromRilErrno));
        } else if (i == 5) {
            if (ssData.serviceType.isTypeClir()) {
                Rlog.d("GsmMmiCode", "CLIR INTERROGATION");
                onGetClirComplete(new AsyncResult((Object) null, ssData.ssInfo, fromRilErrno));
            } else if (ssData.serviceType.isTypeCF()) {
                Rlog.d("GsmMmiCode", "CALL FORWARD INTERROGATION");
                onQueryCfComplete(new AsyncResult((Object) null, ssData.cfInfo, fromRilErrno));
            } else {
                onQueryComplete(new AsyncResult((Object) null, ssData.ssInfo, fromRilErrno));
            }
        } else {
            Rlog.e("GsmMmiCode", "Invaid requestType in SSData : " + ssData.requestType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.gsm.GsmMmiCode$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C02131 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType;
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType;

        static {
            int[] iArr = new int[SsData.ServiceType.values().length];
            $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType = iArr;
            try {
                iArr[SsData.ServiceType.SS_CFU.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CF_BUSY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CF_NO_REPLY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CF_NOT_REACHABLE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CF_ALL.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CF_ALL_CONDITIONAL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CLIP.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_CLIR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_WAIT.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_BAOC.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_BAOIC.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_BAOIC_EXC_HOME.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_BAIC.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_BAIC_ROAMING.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_ALL_BARRING.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_OUTGOING_BARRING.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[SsData.ServiceType.SS_INCOMING_BARRING.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            int[] iArr2 = new int[SsData.RequestType.values().length];
            $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType = iArr2;
            try {
                iArr2[SsData.RequestType.SS_ACTIVATION.ordinal()] = 1;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[SsData.RequestType.SS_DEACTIVATION.ordinal()] = 2;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[SsData.RequestType.SS_REGISTRATION.ordinal()] = 3;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[SsData.RequestType.SS_ERASURE.ordinal()] = 4;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[SsData.RequestType.SS_INTERROGATION.ordinal()] = 5;
            } catch (NoSuchFieldError unused22) {
            }
        }
    }

    private String getScStringFromScType(SsData.ServiceType serviceType) {
        switch (C02131.$SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[serviceType.ordinal()]) {
            case 1:
                return "21";
            case 2:
                return "67";
            case 3:
                return "61";
            case 4:
                return "62";
            case 5:
                return "002";
            case 6:
                return "004";
            case 7:
                return "30";
            case 8:
                return "31";
            case 9:
                return "43";
            case 10:
                return "33";
            case 11:
                return "331";
            case 12:
                return "332";
            case 13:
                return "35";
            case 14:
                return "351";
            case 15:
                return "330";
            case 16:
                return "333";
            case 17:
                return "353";
            default:
                return PhoneConfigurationManager.SSSS;
        }
    }

    private static String getActionStringFromReqType(SsData.RequestType requestType) {
        int i = C02131.$SwitchMap$com$android$internal$telephony$gsm$SsData$RequestType[requestType.ordinal()];
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? PhoneConfigurationManager.SSSS : "*#" : "##" : "**" : "#" : "*";
    }

    @UnsupportedAppUsage
    private static String makeEmptyNull(String str) {
        if (str == null || str.length() != 0) {
            return str;
        }
        return null;
    }

    private static boolean isEmptyOrNull(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    private static int scToCallForwardReason(String str) {
        if (str == null) {
            throw new RuntimeException("invalid call forward sc");
        }
        if (str.equals("002")) {
            return 4;
        }
        if (str.equals("21")) {
            return 0;
        }
        if (str.equals("67")) {
            return 1;
        }
        if (str.equals("62")) {
            return 3;
        }
        if (str.equals("61")) {
            return 2;
        }
        if (str.equals("004")) {
            return 5;
        }
        throw new RuntimeException("invalid call forward sc");
    }

    public static SsData.ServiceType cfReasonToServiceType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                return null;
                            }
                            return SsData.ServiceType.SS_CF_ALL_CONDITIONAL;
                        }
                        return SsData.ServiceType.SS_CF_ALL;
                    }
                    return SsData.ServiceType.SS_CF_NOT_REACHABLE;
                }
                return SsData.ServiceType.SS_CF_NO_REPLY;
            }
            return SsData.ServiceType.SS_CF_BUSY;
        }
        return SsData.ServiceType.SS_CFU;
    }

    public static SsData.RequestType cfActionToRequestType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    if (i != 4) {
                        return null;
                    }
                    return SsData.RequestType.SS_ERASURE;
                }
                return SsData.RequestType.SS_REGISTRATION;
            }
            return SsData.RequestType.SS_ACTIVATION;
        }
        return SsData.RequestType.SS_DEACTIVATION;
    }

    @UnsupportedAppUsage
    private static int siToServiceClass(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int parseInt = Integer.parseInt(str, 10);
        if (parseInt != 16) {
            if (parseInt != 99) {
                switch (parseInt) {
                    case 10:
                        return 13;
                    case 11:
                        return 1;
                    case 12:
                        return 12;
                    case 13:
                        return 4;
                    default:
                        switch (parseInt) {
                            case 19:
                                return 5;
                            case 20:
                                return 48;
                            case 21:
                                return SmsMessage.MAX_USER_DATA_SEPTETS;
                            case 22:
                                return 80;
                            default:
                                switch (parseInt) {
                                    case 24:
                                        return 16;
                                    case 25:
                                        return 32;
                                    case 26:
                                        return 17;
                                    default:
                                        throw new RuntimeException("unsupported MMI service code " + str);
                                }
                        }
                }
            }
            return 64;
        }
        return 8;
    }

    private static int siToTime(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        return Integer.parseInt(str, 10);
    }

    @UnsupportedAppUsage
    static boolean isServiceCodeCallForwarding(String str) {
        return str != null && (str.equals("21") || str.equals("67") || str.equals("61") || str.equals("62") || str.equals("002") || str.equals("004"));
    }

    @UnsupportedAppUsage
    static boolean isServiceCodeCallBarring(String str) {
        String[] stringArray;
        Resources system = Resources.getSystem();
        if (str != null && (stringArray = system.getStringArray(17236007)) != null) {
            for (String str2 : stringArray) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    static String scToBarringFacility(String str) {
        if (str == null) {
            throw new RuntimeException("invalid call barring sc");
        }
        if (str.equals("33")) {
            return CommandsInterface.CB_FACILITY_BAOC;
        }
        if (str.equals("331")) {
            return CommandsInterface.CB_FACILITY_BAOIC;
        }
        if (str.equals("332")) {
            return CommandsInterface.CB_FACILITY_BAOICxH;
        }
        if (str.equals("35")) {
            return CommandsInterface.CB_FACILITY_BAIC;
        }
        if (str.equals("351")) {
            return CommandsInterface.CB_FACILITY_BAICr;
        }
        if (str.equals("330")) {
            return CommandsInterface.CB_FACILITY_BA_ALL;
        }
        if (str.equals("333")) {
            return CommandsInterface.CB_FACILITY_BA_MO;
        }
        if (str.equals("353")) {
            return CommandsInterface.CB_FACILITY_BA_MT;
        }
        throw new RuntimeException("invalid call barring sc");
    }

    public static SsData.ServiceType cbFacilityToServiceType(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_NO_RESPONSE_FROM_BASE_STATION /* 2081 */:
                if (str.equals(CommandsInterface.CB_FACILITY_BA_ALL)) {
                    c = 0;
                    break;
                }
                break;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_REJECTED_BY_BASE_STATION /* 2082 */:
                if (str.equals(CommandsInterface.CB_FACILITY_BA_MT)) {
                    c = 1;
                    break;
                }
                break;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_CDMA_RETRY_ORDER /* 2086 */:
                if (str.equals(CommandsInterface.CB_FACILITY_BA_MO)) {
                    c = 2;
                    break;
                }
                break;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_ACCESS_BLOCK_ALL /* 2088 */:
                if (str.equals(CommandsInterface.CB_FACILITY_BAIC)) {
                    c = 3;
                    break;
                }
                break;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_NO_GPRS_CONTEXT /* 2094 */:
                if (str.equals(CommandsInterface.CB_FACILITY_BAOC)) {
                    c = 4;
                    break;
                }
                break;
            case 2345:
                if (str.equals(CommandsInterface.CB_FACILITY_BAICr)) {
                    c = 5;
                    break;
                }
                break;
            case 2522:
                if (str.equals(CommandsInterface.CB_FACILITY_BAOIC)) {
                    c = 6;
                    break;
                }
                break;
            case 2537:
                if (str.equals(CommandsInterface.CB_FACILITY_BAOICxH)) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return SsData.ServiceType.SS_ALL_BARRING;
            case 1:
                return SsData.ServiceType.SS_INCOMING_BARRING;
            case 2:
                return SsData.ServiceType.SS_OUTGOING_BARRING;
            case 3:
                return SsData.ServiceType.SS_BAIC;
            case 4:
                return SsData.ServiceType.SS_BAOC;
            case 5:
                return SsData.ServiceType.SS_BAIC_ROAMING;
            case 6:
                return SsData.ServiceType.SS_BAOIC;
            case 7:
                return SsData.ServiceType.SS_BAOIC_EXC_HOME;
            default:
                return null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmMmiCode(GsmCdmaPhone gsmCdmaPhone, UiccCardApplication uiccCardApplication) {
        super(gsmCdmaPhone.getHandler().getLooper());
        this.mState = MmiCode.State.PENDING;
        this.mIsSsInfo = false;
        this.mPhone = gsmCdmaPhone;
        this.mContext = gsmCdmaPhone.getContext();
        this.mUiccApplication = uiccCardApplication;
        if (uiccCardApplication != null) {
            this.mIccRecords = uiccCardApplication.getIccRecords();
        }
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
        if (this.mIsPendingUSSD) {
            this.mPhone.mCi.cancelPendingUssd(obtainMessage(7, this));
        } else {
            this.mPhone.onMMIDone(this);
        }
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isCancelable() {
        return this.mIsPendingUSSD;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isNetworkInitiatedUssd() {
        return this.mIsNetworkInitiatedUSSD;
    }

    boolean isShortCode() {
        String str;
        return this.mPoundString == null && (str = this.mDialingNumber) != null && str.length() <= 2;
    }

    @Override // com.android.internal.telephony.MmiCode
    public String getDialString() {
        return this.mPoundString;
    }

    public static boolean isTwoDigitShortCode(Context context, int i, String str) {
        String[] strArr;
        Rlog.d("GsmMmiCode", "isTwoDigitShortCode");
        if (str != null && str.length() <= 2) {
            if (sTwoDigitNumberPattern == null) {
                sTwoDigitNumberPattern = getTwoDigitNumberPattern(context, i);
            }
            for (String str2 : sTwoDigitNumberPattern) {
                Rlog.d("GsmMmiCode", "Two Digit Number Pattern " + str2);
                if (str.equals(str2)) {
                    Rlog.d("GsmMmiCode", "Two Digit Number Pattern -true");
                    return true;
                }
            }
            Rlog.d("GsmMmiCode", "Two Digit Number Pattern -false");
        }
        return false;
    }

    private static String[] getTwoDigitNumberPattern(Context context, int i) {
        String[] strArr;
        PersistableBundle configForSubId;
        Rlog.d("GsmMmiCode", "Get two digit number pattern: subId=" + i);
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            strArr = null;
        } else {
            Rlog.d("GsmMmiCode", "Two Digit Number Pattern from carrir config");
            strArr = configForSubId.getStringArray("mmi_two_digit_number_pattern_string_array");
        }
        return strArr == null ? new String[0] : strArr;
    }

    private static boolean isShortCode(String str, GsmCdmaPhone gsmCdmaPhone) {
        if (str == null || str.length() == 0 || isEmergencyNumber(gsmCdmaPhone, str)) {
            return false;
        }
        return isShortCodeUSSD(str, gsmCdmaPhone);
    }

    private static boolean isShortCodeUSSD(String str, GsmCdmaPhone gsmCdmaPhone) {
        return (str == null || str.length() > 2 || (!gsmCdmaPhone.isInCall() && str.length() == 2 && str.charAt(0) == '1')) ? false : true;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isPinPukCommand() {
        String str = this.mSc;
        return str != null && (str.equals("04") || this.mSc.equals("042") || this.mSc.equals("05") || this.mSc.equals("052"));
    }

    @UnsupportedAppUsage
    public boolean isTemporaryModeCLIR() {
        String str = this.mSc;
        return str != null && str.equals("31") && this.mDialingNumber != null && (isActivate() || isDeactivate());
    }

    @VisibleForTesting
    public static boolean isEmergencyNumber(Phone phone, String str) {
        try {
            return ((TelephonyManager) phone.getContext().getSystemService(TelephonyManager.class)).isEmergencyNumber(str);
        } catch (RuntimeException unused) {
            return false;
        }
    }

    @VisibleForTesting
    public static boolean isCarrierSupportCallerIdVerticalServiceCodes(Phone phone) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService(CarrierConfigManager.class);
        PersistableBundle configForSubId = carrierConfigManager != null ? carrierConfigManager.getConfigForSubId(phone.getSubId()) : null;
        if (configForSubId != null) {
            return configForSubId.getBoolean("carrier_supports_caller_id_vertical_service_codes_bool");
        }
        return false;
    }

    @UnsupportedAppUsage
    public int getCLIRMode() {
        String str = this.mSc;
        if (str == null || !str.equals("31")) {
            return 0;
        }
        if (isActivate()) {
            return 2;
        }
        return isDeactivate() ? 1 : 0;
    }

    public static SsData.RequestType clirModeToRequestType(int i) {
        if (i != 1) {
            if (i != 2) {
                return null;
            }
            return SsData.RequestType.SS_ACTIVATION;
        }
        return SsData.RequestType.SS_DEACTIVATION;
    }

    private boolean isFacToDial() {
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mPhone.getContext().getSystemService("carrier_config")).getConfigForSubId(this.mPhone.getSubId());
        if (configForSubId != null) {
            String[] stringArray = configForSubId.getStringArray("feature_access_codes_string_array");
            if (!ArrayUtils.isEmpty(stringArray)) {
                for (String str : stringArray) {
                    if (str.equals(this.mSc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @UnsupportedAppUsage
    boolean isActivate() {
        String str = this.mAction;
        return str != null && str.equals("*");
    }

    @UnsupportedAppUsage
    boolean isDeactivate() {
        String str = this.mAction;
        return str != null && str.equals("#");
    }

    @UnsupportedAppUsage
    boolean isInterrogate() {
        String str = this.mAction;
        return str != null && str.equals("*#");
    }

    @UnsupportedAppUsage
    boolean isRegister() {
        String str = this.mAction;
        return str != null && str.equals("**");
    }

    @UnsupportedAppUsage
    boolean isErasure() {
        String str = this.mAction;
        return str != null && str.equals("##");
    }

    public boolean isPendingUSSD() {
        return this.mIsPendingUSSD;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isUssdRequest() {
        return this.mIsUssdRequest;
    }

    public boolean isSsInfo() {
        return this.mIsSsInfo;
    }

    /* JADX WARN: Code restructure failed: missing block: B:121:0x0221, code lost:
        if ((r0 & 1) != 1) goto L136;
     */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0121  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0122  */
    @Override // com.android.internal.telephony.MmiCode
    @UnsupportedAppUsage
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void processCode() throws CallStateException {
        UiccCardApplication uiccCardApplication;
        int i;
        int i2;
        try {
            if (isShortCode()) {
                Rlog.d("GsmMmiCode", "processCode: isShortCode");
                sendUssd(this.mDialingNumber);
            } else if (this.mDialingNumber != null) {
                throw new RuntimeException("Invalid or Unsupported MMI Code");
            } else {
                String str = this.mSc;
                if (str != null && str.equals("30")) {
                    Rlog.d("GsmMmiCode", "processCode: is CLIP");
                    if (isInterrogate()) {
                        this.mPhone.mCi.queryCLIP(obtainMessage(5, this));
                        return;
                    }
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
                String str2 = this.mSc;
                int i3 = 1;
                if (str2 != null && str2.equals("31")) {
                    Rlog.d("GsmMmiCode", "processCode: is CLIR");
                    if (isActivate() && !this.mPhone.isClirActivationAndDeactivationPrevented()) {
                        this.mPhone.mCi.setCLIR(1, obtainMessage(1, this));
                    } else if (isDeactivate() && !this.mPhone.isClirActivationAndDeactivationPrevented()) {
                        this.mPhone.mCi.setCLIR(2, obtainMessage(1, this));
                    } else if (isInterrogate()) {
                        this.mPhone.mCi.getCLIR(obtainMessage(2, this));
                    } else {
                        throw new RuntimeException("Invalid or Unsupported MMI Code");
                    }
                } else if (isServiceCodeCallForwarding(this.mSc)) {
                    Rlog.d("GsmMmiCode", "processCode: is CF");
                    String str3 = this.mSia;
                    int siToServiceClass = siToServiceClass(this.mSib);
                    int scToCallForwardReason = scToCallForwardReason(this.mSc);
                    int siToTime = siToTime(this.mSic);
                    if (isInterrogate()) {
                        this.mPhone.mCi.queryCallForwardStatus(scToCallForwardReason, siToServiceClass, str3, obtainMessage(3, this));
                        return;
                    }
                    if (isActivate()) {
                        if (isEmptyOrNull(str3)) {
                            this.mIsCallFwdReg = false;
                            i = 1;
                        } else {
                            this.mIsCallFwdReg = true;
                            i = 3;
                        }
                    } else if (isDeactivate()) {
                        i = 0;
                    } else if (isRegister()) {
                        i = 3;
                    } else if (!isErasure()) {
                        throw new RuntimeException("invalid action");
                    } else {
                        i = 4;
                    }
                    if (i != 1 && i != 3) {
                        i2 = 0;
                        Rlog.d("GsmMmiCode", "processCode: is CF setCallForward");
                        CommandsInterface commandsInterface = this.mPhone.mCi;
                        if (isVoiceUnconditionalForwarding(scToCallForwardReason, siToServiceClass)) {
                            i3 = 0;
                        }
                        commandsInterface.setCallForward(i, scToCallForwardReason, siToServiceClass, str3, siToTime, obtainMessage(6, i3, i2, this));
                    }
                    i2 = 1;
                    Rlog.d("GsmMmiCode", "processCode: is CF setCallForward");
                    CommandsInterface commandsInterface2 = this.mPhone.mCi;
                    if (isVoiceUnconditionalForwarding(scToCallForwardReason, siToServiceClass)) {
                    }
                    commandsInterface2.setCallForward(i, scToCallForwardReason, siToServiceClass, str3, siToTime, obtainMessage(6, i3, i2, this));
                } else if (isServiceCodeCallBarring(this.mSc)) {
                    String str4 = this.mSia;
                    int siToServiceClass2 = siToServiceClass(this.mSib);
                    String scToBarringFacility = scToBarringFacility(this.mSc);
                    if (isInterrogate()) {
                        this.mPhone.mCi.queryFacilityLock(scToBarringFacility, str4, siToServiceClass2, obtainMessage(5, this));
                        return;
                    }
                    if (!isActivate() && !isDeactivate()) {
                        throw new RuntimeException("Invalid or Unsupported MMI Code");
                    }
                    this.mPhone.mCi.setFacilityLock(scToBarringFacility, isActivate(), str4, siToServiceClass2, obtainMessage(1, this));
                } else {
                    String str5 = this.mSc;
                    if (str5 != null && str5.equals("03")) {
                        String str6 = this.mSib;
                        String str7 = this.mSic;
                        if (!isActivate() && !isRegister()) {
                            throw new RuntimeException("Invalid or Unsupported MMI Code");
                        }
                        this.mAction = "**";
                        String str8 = this.mSia;
                        String scToBarringFacility2 = str8 == null ? CommandsInterface.CB_FACILITY_BA_ALL : scToBarringFacility(str8);
                        if (str7.equals(this.mPwd)) {
                            this.mPhone.mCi.changeBarringPassword(scToBarringFacility2, str6, str7, obtainMessage(1, this));
                            return;
                        } else {
                            handlePasswordError(17040932);
                            return;
                        }
                    }
                    String str9 = this.mSc;
                    if (str9 != null && str9.equals("43")) {
                        int siToServiceClass3 = siToServiceClass(this.mSia);
                        if (!isActivate() && !isDeactivate()) {
                            if (isInterrogate()) {
                                if (this.mPhone.getTerminalBasedCallWaitingState(true) != -1) {
                                    this.mPhone.getCallWaiting(obtainMessage(5, this));
                                    return;
                                } else {
                                    this.mPhone.mCi.queryCallWaiting(siToServiceClass3, obtainMessage(5, this));
                                    return;
                                }
                            }
                            throw new RuntimeException("Invalid or Unsupported MMI Code");
                        }
                        if (this.mPhone.getTerminalBasedCallWaitingState(true) != -1) {
                            this.mPhone.setCallWaiting(isActivate(), siToServiceClass3, obtainMessage(1, this));
                            return;
                        }
                        this.mPhone.mCi.setCallWaiting(isActivate(), siToServiceClass3, obtainMessage(1, this));
                    } else if (isPinPukCommand()) {
                        String str10 = this.mSia;
                        String str11 = this.mSib;
                        int length = str11.length();
                        if (isRegister()) {
                            if (!str11.equals(this.mSic)) {
                                handlePasswordError(17040798);
                                return;
                            }
                            if (length >= 4 && length <= 8) {
                                if (this.mSc.equals("04") && (uiccCardApplication = this.mUiccApplication) != null && uiccCardApplication.getState() == IccCardApplicationStatus.AppState.APPSTATE_PUK) {
                                    handlePasswordError(17040827);
                                    return;
                                } else if (this.mUiccApplication != null) {
                                    Rlog.d("GsmMmiCode", "processCode: process mmi service code using UiccApp sc=" + this.mSc);
                                    if (this.mSc.equals("04")) {
                                        this.mUiccApplication.changeIccLockPassword(str10, str11, obtainMessage(1, this));
                                        return;
                                    } else if (this.mSc.equals("042")) {
                                        this.mUiccApplication.changeIccFdnPassword(str10, str11, obtainMessage(1, this));
                                        return;
                                    } else if (this.mSc.equals("05")) {
                                        this.mUiccApplication.supplyPuk(str10, str11, obtainMessage(1, this));
                                        return;
                                    } else if (this.mSc.equals("052")) {
                                        this.mUiccApplication.supplyPuk2(str10, str11, obtainMessage(1, this));
                                        return;
                                    } else {
                                        throw new RuntimeException("uicc unsupported service code=" + this.mSc);
                                    }
                                } else {
                                    throw new RuntimeException("No application mUiccApplicaiton is null");
                                }
                            }
                            handlePasswordError(17040476);
                            return;
                        }
                        throw new RuntimeException("Ivalid register/action=" + this.mAction);
                    } else if (this.mPoundString != null) {
                        if (this.mContext.getResources().getBoolean(17891364)) {
                            if (getIntCarrierConfig("carrier_ussd_method_int") != 3) {
                                sendUssd(this.mPoundString);
                                return;
                            }
                            throw new RuntimeException("The USSD request is not allowed over CS");
                        }
                        sendUssd(this.mPoundString);
                    } else {
                        Rlog.d("GsmMmiCode", "processCode: Invalid or Unsupported MMI Code");
                        throw new RuntimeException("Invalid or Unsupported MMI Code");
                    }
                }
            }
        } catch (RuntimeException e) {
            this.mState = MmiCode.State.FAILED;
            this.mMessage = this.mContext.getText(17040808);
            Rlog.d("GsmMmiCode", "processCode: RuntimeException=" + e);
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

    public void onUssdFinished(String str, boolean z) {
        if (this.mState == MmiCode.State.PENDING) {
            if (TextUtils.isEmpty(str)) {
                Rlog.d("GsmMmiCode", "onUssdFinished: no network provided message; using default.");
                this.mMessage = this.mContext.getText(17040807);
            } else {
                this.mMessage = str;
            }
            this.mIsUssdRequest = z;
            if (!z) {
                this.mState = MmiCode.State.COMPLETE;
            }
            Rlog.d("GsmMmiCode", "onUssdFinished: ussdMessage=" + str);
            this.mPhone.onMMIDone(this);
        }
    }

    public void onUssdFinishedError() {
        if (this.mState == MmiCode.State.PENDING) {
            this.mState = MmiCode.State.FAILED;
            if (TextUtils.isEmpty(this.mMessage)) {
                this.mMessage = this.mContext.getText(17040808);
            }
            Rlog.d("GsmMmiCode", "onUssdFinishedError");
            this.mPhone.onMMIDone(this);
        }
    }

    public void onUssdRelease() {
        if (this.mState == MmiCode.State.PENDING) {
            this.mState = MmiCode.State.COMPLETE;
            this.mMessage = null;
            Rlog.d("GsmMmiCode", "onUssdRelease");
            this.mPhone.onMMIDone(this);
        }
    }

    public void sendUssd(String str) {
        this.mIsPendingUSSD = true;
        this.mPhone.mCi.sendUSSD(str, obtainMessage(4, this));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                onSetComplete(message, (AsyncResult) message.obj);
                return;
            case 2:
                onGetClirComplete((AsyncResult) message.obj);
                return;
            case 3:
                onQueryCfComplete((AsyncResult) message.obj);
                return;
            case 4:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception != null) {
                    this.mState = MmiCode.State.FAILED;
                    this.mMessage = getErrorMessage(asyncResult);
                    this.mPhone.onMMIDone(this);
                    return;
                }
                return;
            case 5:
                onQueryComplete((AsyncResult) message.obj);
                return;
            case 6:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                if (asyncResult2.exception == null && message.arg1 == 1) {
                    this.mPhone.setVoiceCallForwardingFlag(1, message.arg2 == 1, this.mDialingNumber);
                }
                onSetComplete(message, asyncResult2);
                return;
            case 7:
                this.mPhone.onMMIDone(this);
                return;
            default:
                return;
        }
    }

    @VisibleForTesting
    public CharSequence getErrorMessage(AsyncResult asyncResult) {
        Throwable th = asyncResult.exception;
        if (th instanceof CommandException) {
            CommandException.Error commandError = ((CommandException) th).getCommandError();
            if (commandError == CommandException.Error.FDN_CHECK_FAILURE) {
                Rlog.i("GsmMmiCode", "FDN_CHECK_FAILURE");
                return this.mContext.getText(17040811);
            } else if (commandError == CommandException.Error.USSD_MODIFIED_TO_DIAL) {
                Rlog.i("GsmMmiCode", "USSD_MODIFIED_TO_DIAL");
                return this.mContext.getText(17041615);
            } else if (commandError == CommandException.Error.USSD_MODIFIED_TO_SS) {
                Rlog.i("GsmMmiCode", "USSD_MODIFIED_TO_SS");
                return this.mContext.getText(17041617);
            } else if (commandError == CommandException.Error.USSD_MODIFIED_TO_USSD) {
                Rlog.i("GsmMmiCode", "USSD_MODIFIED_TO_USSD");
                return this.mContext.getText(17041618);
            } else if (commandError == CommandException.Error.SS_MODIFIED_TO_DIAL) {
                Rlog.i("GsmMmiCode", "SS_MODIFIED_TO_DIAL");
                return this.mContext.getText(17041611);
            } else if (commandError == CommandException.Error.SS_MODIFIED_TO_USSD) {
                Rlog.i("GsmMmiCode", "SS_MODIFIED_TO_USSD");
                return this.mContext.getText(17041614);
            } else if (commandError == CommandException.Error.SS_MODIFIED_TO_SS) {
                Rlog.i("GsmMmiCode", "SS_MODIFIED_TO_SS");
                return this.mContext.getText(17041613);
            } else if (commandError == CommandException.Error.OEM_ERROR_1) {
                Rlog.i("GsmMmiCode", "OEM_ERROR_1 USSD_MODIFIED_TO_DIAL_VIDEO");
                return this.mContext.getText(17041616);
            } else if (commandError == CommandException.Error.REQUEST_NOT_SUPPORTED || commandError == CommandException.Error.OPERATION_NOT_ALLOWED) {
                Rlog.i("GsmMmiCode", "REQUEST_NOT_SUPPORTED/OPERATION_NOT_ALLOWED");
                return this.mContext.getResources().getText(17040809);
            }
        }
        return this.mContext.getText(17040808);
    }

    @UnsupportedAppUsage
    private CharSequence getScString() {
        String str = this.mSc;
        if (str != null) {
            if (isServiceCodeCallBarring(str)) {
                return this.mContext.getText(17039428);
            }
            if (isServiceCodeCallForwarding(this.mSc)) {
                return this.mContext.getText(17039434);
            }
            if (this.mSc.equals("30")) {
                return this.mContext.getText(17039435);
            }
            if (this.mSc.equals("31")) {
                return this.mContext.getText(17039436);
            }
            if (this.mSc.equals("03")) {
                return this.mContext.getText(17039559);
            }
            if (this.mSc.equals("43")) {
                return this.mContext.getText(17039442);
            }
            return isPinPukCommand() ? this.mContext.getText(17039558) : PhoneConfigurationManager.SSSS;
        }
        return PhoneConfigurationManager.SSSS;
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
                            Rlog.d("GsmMmiCode", "onSetComplete: PUK locked, cancel as lock screen will handle this");
                            this.mState = MmiCode.State.CANCELLED;
                        } else if (i > 0) {
                            Rlog.d("GsmMmiCode", "onSetComplete: attemptsRemaining=" + i);
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
                } else if (commandError == CommandException.Error.FDN_CHECK_FAILURE) {
                    Rlog.i("GsmMmiCode", "FDN_CHECK_FAILURE");
                    sb.append(this.mContext.getText(17040811));
                } else if (commandError == CommandException.Error.MODEM_ERR) {
                    if (isServiceCodeCallForwarding(this.mSc) && this.mPhone.getServiceState().getVoiceRoaming() && !this.mPhone.supports3gppCallForwardingWhileRoaming()) {
                        sb.append(this.mContext.getText(17040810));
                    } else {
                        sb.append(getErrorMessage(asyncResult));
                    }
                } else {
                    sb.append(getErrorMessage(asyncResult));
                }
            } else {
                sb.append(this.mContext.getText(17040808));
            }
        } else if (isActivate()) {
            this.mState = MmiCode.State.COMPLETE;
            if (this.mIsCallFwdReg) {
                sb.append(this.mContext.getText(17041513));
            } else {
                sb.append(this.mContext.getText(17041509));
            }
            if (this.mSc.equals("31")) {
                this.mPhone.saveClirSetting(1);
            }
        } else if (isDeactivate()) {
            this.mState = MmiCode.State.COMPLETE;
            sb.append(this.mContext.getText(17041508));
            if (this.mSc.equals("31")) {
                this.mPhone.saveClirSetting(2);
            }
        } else if (isRegister()) {
            this.mState = MmiCode.State.COMPLETE;
            sb.append(this.mContext.getText(17041513));
        } else if (isErasure()) {
            this.mState = MmiCode.State.COMPLETE;
            sb.append(this.mContext.getText(17041511));
        } else {
            this.mState = MmiCode.State.FAILED;
            sb.append(this.mContext.getText(17040808));
        }
        this.mMessage = sb;
        Rlog.d("GsmMmiCode", "onSetComplete mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private void onGetClirComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        if (asyncResult.exception != null) {
            this.mState = MmiCode.State.FAILED;
            sb.append(getErrorMessage(asyncResult));
        } else {
            int[] iArr = (int[]) asyncResult.result;
            int i = iArr[1];
            if (i == 0) {
                sb.append(this.mContext.getText(17041512));
                this.mState = MmiCode.State.COMPLETE;
            } else if (i == 1) {
                sb.append(this.mContext.getText(17039433));
                this.mState = MmiCode.State.COMPLETE;
            } else if (i == 2) {
                sb.append(this.mContext.getText(17040808));
                this.mState = MmiCode.State.FAILED;
            } else if (i == 3) {
                int i2 = iArr[0];
                if (i2 == 1) {
                    sb.append(this.mContext.getText(17039432));
                } else if (i2 != 2) {
                    sb.append(this.mContext.getText(17039432));
                } else {
                    sb.append(this.mContext.getText(17039431));
                }
                this.mState = MmiCode.State.COMPLETE;
            } else if (i == 4) {
                int i3 = iArr[0];
                if (i3 == 1) {
                    sb.append(this.mContext.getText(17039430));
                } else if (i3 != 2) {
                    sb.append(this.mContext.getText(17039429));
                } else {
                    sb.append(this.mContext.getText(17039429));
                }
                this.mState = MmiCode.State.COMPLETE;
            }
        }
        this.mMessage = sb;
        Rlog.d("GsmMmiCode", "onGetClirComplete: mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private CharSequence serviceClassToCFString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 4) {
                    if (i != 8) {
                        if (i != 16) {
                            if (i != 32) {
                                if (i != 64) {
                                    if (i != 128) {
                                        return null;
                                    }
                                    return this.mContext.getText(17041504);
                                }
                                return this.mContext.getText(17041505);
                            }
                            return this.mContext.getText(17041501);
                        }
                        return this.mContext.getText(17041502);
                    }
                    return this.mContext.getText(17041506);
                }
                return this.mContext.getText(17041503);
            }
            return this.mContext.getText(17041500);
        }
        return this.mContext.getText(17041507);
    }

    private CharSequence makeCFQueryResultMessage(CallForwardInfo callForwardInfo, int i) {
        CharSequence text;
        String[] strArr = {"{0}", "{1}", "{2}"};
        CharSequence[] charSequenceArr = new CharSequence[3];
        boolean z = callForwardInfo.reason == 2;
        int i2 = callForwardInfo.status;
        if (i2 == 1) {
            if (z) {
                text = this.mContext.getText(17039800);
            } else {
                text = this.mContext.getText(17039799);
            }
        } else if (i2 == 0 && isEmptyOrNull(callForwardInfo.number)) {
            text = this.mContext.getText(17039801);
        } else if (z) {
            text = this.mContext.getText(17039803);
        } else {
            text = this.mContext.getText(17039802);
        }
        charSequenceArr[0] = serviceClassToCFString(callForwardInfo.serviceClass & i);
        charSequenceArr[1] = formatLtr(PhoneNumberUtils.stringFromStringAndTOA(callForwardInfo.number, callForwardInfo.toa));
        charSequenceArr[2] = Integer.toString(callForwardInfo.timeSeconds);
        if (callForwardInfo.reason == 0 && (i & callForwardInfo.serviceClass) == 1) {
            this.mPhone.setVoiceCallForwardingFlag(1, callForwardInfo.status == 1, callForwardInfo.number);
        }
        return TextUtils.replace(text, strArr, charSequenceArr);
    }

    private String formatLtr(String str) {
        return str == null ? str : BidiFormatter.getInstance().unicodeWrap(str, TextDirectionHeuristics.LTR, true);
    }

    private void onQueryCfComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        if (asyncResult.exception != null) {
            this.mState = MmiCode.State.FAILED;
            sb.append(getErrorMessage(asyncResult));
        } else {
            CallForwardInfo[] callForwardInfoArr = (CallForwardInfo[]) asyncResult.result;
            if (callForwardInfoArr == null || callForwardInfoArr.length == 0) {
                sb.append(this.mContext.getText(17041508));
                this.mPhone.setVoiceCallForwardingFlag(1, false, null);
            } else {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                for (int i = 1; i <= 128; i <<= 1) {
                    for (CallForwardInfo callForwardInfo : callForwardInfoArr) {
                        if ((callForwardInfo.serviceClass & i) != 0) {
                            spannableStringBuilder.append(makeCFQueryResultMessage(callForwardInfo, i));
                            spannableStringBuilder.append((CharSequence) "\n");
                        }
                    }
                }
                sb.append((CharSequence) spannableStringBuilder);
            }
            this.mState = MmiCode.State.COMPLETE;
        }
        this.mMessage = sb;
        Rlog.d("GsmMmiCode", "onQueryCfComplete: mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private void onQueryComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        if (asyncResult.exception != null) {
            this.mState = MmiCode.State.FAILED;
            sb.append(getErrorMessage(asyncResult));
        } else {
            int[] iArr = (int[]) asyncResult.result;
            if (iArr.length != 0) {
                if (iArr[0] == 0) {
                    sb.append(this.mContext.getText(17041508));
                } else if (this.mSc.equals("43")) {
                    sb.append(createQueryCallWaitingResultMessage(iArr[1]));
                } else if (isServiceCodeCallBarring(this.mSc)) {
                    sb.append(createQueryCallBarringResultMessage(iArr[0]));
                } else if (iArr[0] == 1) {
                    sb.append(this.mContext.getText(17041509));
                } else {
                    sb.append(this.mContext.getText(17040808));
                }
            } else {
                sb.append(this.mContext.getText(17040808));
            }
            this.mState = MmiCode.State.COMPLETE;
        }
        this.mMessage = sb;
        Rlog.d("GsmMmiCode", "onQueryComplete: mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private CharSequence createQueryCallWaitingResultMessage(int i) {
        StringBuilder sb = new StringBuilder(this.mContext.getText(17041510));
        for (int i2 = 1; i2 <= 128; i2 <<= 1) {
            int i3 = i2 & i;
            if (i3 != 0) {
                sb.append("\n");
                sb.append(serviceClassToCFString(i3));
            }
        }
        return sb;
    }

    private CharSequence createQueryCallBarringResultMessage(int i) {
        StringBuilder sb = new StringBuilder(this.mContext.getText(17041510));
        for (int i2 = 1; i2 <= 128; i2 <<= 1) {
            int i3 = i2 & i;
            if (i3 != 0) {
                sb.append("\n");
                sb.append(serviceClassToCFString(i3));
            }
        }
        return sb;
    }

    private int getIntCarrierConfig(String str) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        PersistableBundle configForSubId = carrierConfigManager != null ? carrierConfigManager.getConfigForSubId(this.mPhone.getSubId()) : null;
        if (configForSubId != null) {
            return configForSubId.getInt(str);
        }
        return CarrierConfigManager.getDefaultConfig().getInt(str);
    }

    @Override // com.android.internal.telephony.MmiCode
    public ResultReceiver getUssdCallbackReceiver() {
        return this.mCallbackReceiver;
    }

    public static ArrayList<String> getControlStrings(SsData.RequestType requestType, SsData.ServiceType serviceType) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (requestType != null && serviceType != null) {
            String actionStringFromReqType = getActionStringFromReqType(requestType);
            switch (C02131.$SwitchMap$com$android$internal$telephony$gsm$SsData$ServiceType[serviceType.ordinal()]) {
                case 1:
                    arrayList.add(actionStringFromReqType + "21");
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 2:
                    arrayList.add(actionStringFromReqType + "67");
                    arrayList.add(actionStringFromReqType + "004");
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 3:
                    arrayList.add(actionStringFromReqType + "61");
                    arrayList.add(actionStringFromReqType + "004");
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 4:
                    arrayList.add(actionStringFromReqType + "62");
                    arrayList.add(actionStringFromReqType + "004");
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 5:
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 6:
                    arrayList.add(actionStringFromReqType + "004");
                    arrayList.add(actionStringFromReqType + "002");
                    break;
                case 7:
                    arrayList.add(actionStringFromReqType + "30");
                    break;
                case 8:
                    arrayList.add(actionStringFromReqType + "31");
                    break;
                case 9:
                    arrayList.add(actionStringFromReqType + "43");
                    break;
                case 10:
                    arrayList.add(actionStringFromReqType + "33");
                    arrayList.add(actionStringFromReqType + "333");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 11:
                    arrayList.add(actionStringFromReqType + "331");
                    arrayList.add(actionStringFromReqType + "333");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 12:
                    arrayList.add(actionStringFromReqType + "332");
                    arrayList.add(actionStringFromReqType + "333");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 13:
                    arrayList.add(actionStringFromReqType + "35");
                    arrayList.add(actionStringFromReqType + "353");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 14:
                    arrayList.add(actionStringFromReqType + "351");
                    arrayList.add(actionStringFromReqType + "353");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 15:
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 16:
                    arrayList.add(actionStringFromReqType + "333");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
                case 17:
                    arrayList.add(actionStringFromReqType + "353");
                    arrayList.add(actionStringFromReqType + "330");
                    break;
            }
        }
        return arrayList;
    }

    public static ArrayList<String> getControlStringsForPwd(SsData.RequestType requestType, SsData.ServiceType serviceType) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (requestType != null && serviceType != null) {
            ArrayList<String> controlStrings = getControlStrings(SsData.RequestType.SS_ACTIVATION, serviceType);
            String actionStringFromReqType = getActionStringFromReqType(requestType);
            arrayList = new ArrayList<>();
            Iterator<String> it = controlStrings.iterator();
            while (it.hasNext()) {
                arrayList.add(actionStringFromReqType + "03" + it.next());
            }
        }
        return arrayList;
    }

    @Override // android.os.Handler
    public String toString() {
        StringBuilder sb = new StringBuilder("GsmMmiCode {");
        sb.append("State=" + getState());
        if (this.mAction != null) {
            sb.append(" action=" + this.mAction);
        }
        if (this.mSc != null) {
            sb.append(" sc=" + this.mSc);
        }
        if (this.mSia != null) {
            sb.append(" sia=" + Rlog.pii("GsmMmiCode", this.mSia));
        }
        if (this.mSib != null) {
            sb.append(" sib=" + Rlog.pii("GsmMmiCode", this.mSib));
        }
        if (this.mSic != null) {
            sb.append(" sic=" + Rlog.pii("GsmMmiCode", this.mSic));
        }
        if (this.mPoundString != null) {
            sb.append(" poundString=" + Rlog.pii("GsmMmiCode", this.mPoundString));
        }
        if (this.mDialingNumber != null) {
            sb.append(" dialingNumber=" + Rlog.pii("GsmMmiCode", this.mDialingNumber));
        }
        if (this.mPwd != null) {
            sb.append(" pwd=" + Rlog.pii("GsmMmiCode", this.mPwd));
        }
        if (this.mCallbackReceiver != null) {
            sb.append(" hasReceiver");
        }
        sb.append("}");
        return sb.toString();
    }
}
