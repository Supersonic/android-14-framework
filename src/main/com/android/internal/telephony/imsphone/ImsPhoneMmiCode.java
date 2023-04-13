package com.android.internal.telephony.imsphone;

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
import android.telephony.ims.ImsCallForwardInfo;
import android.telephony.ims.ImsSsData;
import android.telephony.ims.ImsSsInfo;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.android.ims.ImsException;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallForwardInfo;
import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.MmiCode;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.gsm.GsmMmiCode;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class ImsPhoneMmiCode extends Handler implements MmiCode {
    @Deprecated
    public static final String UT_BUNDLE_KEY_CLIR = "queryClir";
    @Deprecated
    public static final String UT_BUNDLE_KEY_SSINFO = "imsSsInfo";
    private static Pattern sPatternSuppService = Pattern.compile("((\\*|#|\\*#|\\*\\*|##)(\\d{2,3})(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*)(\\*([^*#]*))?)?)?)?#)(.*)");
    private String mAction;
    private ResultReceiver mCallbackReceiver;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Context mContext;
    private String mDialingNumber;
    private IccRecords mIccRecords;
    private boolean mIsCallFwdReg;
    private boolean mIsNetworkInitiatedUSSD;
    private boolean mIsPendingUSSD;
    private boolean mIsSsInfo;
    private boolean mIsUssdRequest;
    private CharSequence mMessage;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsPhone mPhone;
    private String mPoundString;
    private String mPwd;
    private String mSc;
    private String mSia;
    private String mSib;
    private String mSic;
    private MmiCode.State mState;

    private String getActionStringFromReqType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return null;
                        }
                        return "##";
                    }
                    return "**";
                }
                return "*#";
            }
            return "#";
        }
        return "*";
    }

    private String getScStringFromScType(int i) {
        switch (i) {
            case 0:
                return "21";
            case 1:
                return "67";
            case 2:
                return "61";
            case 3:
                return "62";
            case 4:
                return "002";
            case 5:
                return "004";
            case 6:
            default:
                return null;
            case 7:
                return "30";
            case 8:
                return "31";
            case 9:
                return "76";
            case 10:
                return "77";
            case 11:
                return "300";
            case 12:
                return "43";
            case 13:
                return "33";
            case 14:
                return "331";
            case 15:
                return "332";
            case 16:
                return "35";
            case 17:
                return "351";
            case 18:
                return "330";
            case 19:
                return "333";
            case 20:
                return "353";
            case 21:
                return "156";
            case 22:
                return "157";
        }
    }

    private boolean isServiceClassVoiceVideoOrNone(int i) {
        return i == 0 || i == 1 || i == 80;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public static ImsPhoneMmiCode newFromDialString(String str, ImsPhone imsPhone) {
        return newFromDialString(str, imsPhone, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ImsPhoneMmiCode newFromDialString(String str, ImsPhone imsPhone, ResultReceiver resultReceiver) {
        if ((imsPhone.getDefaultPhone().getServiceState().getVoiceRoaming() && imsPhone.getDefaultPhone().supportsConversionOfCdmaCallerIdMmiCodesWhileRoaming()) || (isEmergencyNumber(imsPhone, str) && isCarrierSupportCallerIdVerticalServiceCodes(imsPhone))) {
            str = convertCdmaMmiCodesTo3gppMmiCodes(str);
        }
        Matcher matcher = sPatternSuppService.matcher(str);
        if (matcher.matches()) {
            ImsPhoneMmiCode imsPhoneMmiCode = new ImsPhoneMmiCode(imsPhone);
            imsPhoneMmiCode.mPoundString = makeEmptyNull(matcher.group(1));
            imsPhoneMmiCode.mAction = makeEmptyNull(matcher.group(2));
            imsPhoneMmiCode.mSc = makeEmptyNull(matcher.group(3));
            imsPhoneMmiCode.mSia = makeEmptyNull(matcher.group(5));
            imsPhoneMmiCode.mSib = makeEmptyNull(matcher.group(7));
            imsPhoneMmiCode.mSic = makeEmptyNull(matcher.group(9));
            imsPhoneMmiCode.mPwd = makeEmptyNull(matcher.group(11));
            String makeEmptyNull = makeEmptyNull(matcher.group(12));
            imsPhoneMmiCode.mDialingNumber = makeEmptyNull;
            imsPhoneMmiCode.mCallbackReceiver = resultReceiver;
            if (makeEmptyNull != null && makeEmptyNull.endsWith("#") && str.endsWith("#")) {
                ImsPhoneMmiCode imsPhoneMmiCode2 = new ImsPhoneMmiCode(imsPhone);
                imsPhoneMmiCode2.mPoundString = str;
                return imsPhoneMmiCode2;
            }
            return imsPhoneMmiCode;
        } else if (str.endsWith("#")) {
            ImsPhoneMmiCode imsPhoneMmiCode3 = new ImsPhoneMmiCode(imsPhone);
            imsPhoneMmiCode3.mPoundString = str;
            return imsPhoneMmiCode3;
        } else if (!GsmMmiCode.isTwoDigitShortCode(imsPhone.getContext(), imsPhone.getSubId(), str) && isShortCode(str, imsPhone)) {
            ImsPhoneMmiCode imsPhoneMmiCode4 = new ImsPhoneMmiCode(imsPhone);
            imsPhoneMmiCode4.mDialingNumber = str;
            return imsPhoneMmiCode4;
        } else {
            return null;
        }
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

    public static ImsPhoneMmiCode newNetworkInitiatedUssd(String str, boolean z, ImsPhone imsPhone) {
        ImsPhoneMmiCode imsPhoneMmiCode = new ImsPhoneMmiCode(imsPhone);
        imsPhoneMmiCode.mMessage = str;
        imsPhoneMmiCode.mIsUssdRequest = z;
        imsPhoneMmiCode.mIsNetworkInitiatedUSSD = true;
        if (z) {
            imsPhoneMmiCode.mIsPendingUSSD = true;
            imsPhoneMmiCode.mState = MmiCode.State.PENDING;
        } else {
            imsPhoneMmiCode.mState = MmiCode.State.COMPLETE;
        }
        return imsPhoneMmiCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ImsPhoneMmiCode newFromUssdUserInput(String str, ImsPhone imsPhone) {
        ImsPhoneMmiCode imsPhoneMmiCode = new ImsPhoneMmiCode(imsPhone);
        imsPhoneMmiCode.mMessage = str;
        imsPhoneMmiCode.mState = MmiCode.State.PENDING;
        imsPhoneMmiCode.mIsPendingUSSD = true;
        return imsPhoneMmiCode;
    }

    private static String makeEmptyNull(String str) {
        if (str == null || str.length() != 0) {
            return str;
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
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

    static boolean isServiceCodeCallForwarding(String str) {
        return str != null && (str.equals("21") || str.equals("67") || str.equals("61") || str.equals("62") || str.equals("002") || str.equals("004"));
    }

    static boolean isServiceCodeCallBarring(String str) {
        String[] stringArray;
        Resources system = Resources.getSystem();
        if (str != null && (stringArray = system.getStringArray(17236008)) != null) {
            for (String str2 : stringArray) {
                if (str.equals(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isPinPukCommand(String str) {
        return str != null && (str.equals("04") || str.equals("042") || str.equals("05") || str.equals("052"));
    }

    public static boolean isSuppServiceCodes(String str, Phone phone) {
        if (phone != null && phone.getServiceState().getVoiceRoaming() && phone.getDefaultPhone().supportsConversionOfCdmaCallerIdMmiCodesWhileRoaming()) {
            str = convertCdmaMmiCodesTo3gppMmiCodes(str);
        }
        Matcher matcher = sPatternSuppService.matcher(str);
        if (matcher.matches()) {
            String makeEmptyNull = makeEmptyNull(matcher.group(3));
            if (isServiceCodeCallForwarding(makeEmptyNull) || isServiceCodeCallBarring(makeEmptyNull)) {
                return true;
            }
            if (makeEmptyNull == null || !makeEmptyNull.equals("22")) {
                if (makeEmptyNull == null || !makeEmptyNull.equals("30")) {
                    if (makeEmptyNull == null || !makeEmptyNull.equals("31")) {
                        if (makeEmptyNull == null || !makeEmptyNull.equals("76")) {
                            if (makeEmptyNull == null || !makeEmptyNull.equals("77")) {
                                if (makeEmptyNull == null || !makeEmptyNull.equals("300")) {
                                    if (makeEmptyNull == null || !makeEmptyNull.equals("156")) {
                                        if (makeEmptyNull == null || !makeEmptyNull.equals("157")) {
                                            if (makeEmptyNull == null || !makeEmptyNull.equals("03")) {
                                                return (makeEmptyNull != null && makeEmptyNull.equals("43")) || isPinPukCommand(makeEmptyNull);
                                            }
                                            return true;
                                        }
                                        return true;
                                    }
                                    return true;
                                }
                                return true;
                            }
                            return true;
                        }
                        return true;
                    }
                    return true;
                }
                return true;
            }
            return true;
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

    public ImsPhoneMmiCode(ImsPhone imsPhone) {
        super(imsPhone.getHandler().getLooper());
        this.mState = MmiCode.State.PENDING;
        this.mIsSsInfo = false;
        this.mPhone = imsPhone;
        this.mContext = imsPhone.getContext();
        this.mIccRecords = this.mPhone.mDefaultPhone.getIccRecords();
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
            this.mPhone.cancelUSSD(obtainMessage(5, this));
        } else {
            this.mPhone.onMMIDone(this);
        }
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isCancelable() {
        return this.mIsPendingUSSD;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getDialingNumber() {
        return this.mDialingNumber;
    }

    boolean isShortCode() {
        String str;
        return this.mPoundString == null && (str = this.mDialingNumber) != null && str.length() <= 2;
    }

    @Override // com.android.internal.telephony.MmiCode
    public String getDialString() {
        return this.mPoundString;
    }

    private static boolean isShortCode(String str, ImsPhone imsPhone) {
        if (str == null || str.length() == 0 || isEmergencyNumber(imsPhone, str)) {
            return false;
        }
        return isShortCodeUSSD(str, imsPhone);
    }

    private static boolean isShortCodeUSSD(String str, ImsPhone imsPhone) {
        return (str == null || str.length() > 2 || (!imsPhone.isInCall() && str.length() == 2 && str.charAt(0) == '1')) ? false : true;
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isPinPukCommand() {
        return isPinPukCommand(this.mSc);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
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

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
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

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    boolean isActivate() {
        String str = this.mAction;
        return str != null && str.equals("*");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    boolean isDeactivate() {
        String str = this.mAction;
        return str != null && str.equals("#");
    }

    boolean isInterrogate() {
        String str = this.mAction;
        return str != null && str.equals("*#");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    boolean isRegister() {
        String str = this.mAction;
        return str != null && str.equals("**");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
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

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isSupportedOverImsPhone() {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        if (isShortCode()) {
            return true;
        }
        if (isServiceCodeCallForwarding(this.mSc) || isServiceCodeCallBarring(this.mSc) || (((str = this.mSc) != null && str.equals("43")) || (((str2 = this.mSc) != null && str2.equals("31")) || (((str3 = this.mSc) != null && str3.equals("30")) || (((str4 = this.mSc) != null && str4.equals("77")) || (((str5 = this.mSc) != null && str5.equals("76")) || (((str6 = this.mSc) != null && str6.equals("156")) || ((str7 = this.mSc) != null && str7.equals("157"))))))))) {
            try {
                int siToServiceClass = siToServiceClass(this.mSib);
                return siToServiceClass == 0 || siToServiceClass == 1 || siToServiceClass == 80;
            } catch (RuntimeException e) {
                Rlog.d("ImsPhoneMmiCode", "Invalid service class " + e);
            }
        } else if (!isPinPukCommand() && (((str8 = this.mSc) == null || (!str8.equals("03") && !this.mSc.equals("30") && !this.mSc.equals("31"))) && this.mPoundString != null)) {
            return true;
        }
        return false;
    }

    public int callBarAction(String str) {
        if (isActivate()) {
            return 1;
        }
        if (isDeactivate()) {
            return 0;
        }
        if (isRegister()) {
            if (isEmptyOrNull(str)) {
                throw new RuntimeException("invalid action");
            }
            return 3;
        } else if (isErasure()) {
            return 4;
        } else {
            throw new RuntimeException("invalid action");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:188:0x037e, code lost:
        if ((r0 & 1) != 1) goto L234;
     */
    @Override // com.android.internal.telephony.MmiCode
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void processCode() throws CallStateException {
        int i;
        int i2;
        try {
        } catch (RuntimeException e) {
            this.mState = MmiCode.State.FAILED;
            this.mMessage = this.mContext.getText(17040808);
            Rlog.d("ImsPhoneMmiCode", "processCode: RuntimeException = " + e);
            this.mPhone.onMMIDone(this);
        }
        if (isShortCode()) {
            Rlog.d("ImsPhoneMmiCode", "processCode: isShortCode");
            if (isUssdOverImsAllowed()) {
                Rlog.d("ImsPhoneMmiCode", "processCode: Sending short code '" + this.mDialingNumber + "' over IMS pipe.");
                sendUssd(this.mDialingNumber);
                return;
            }
            Rlog.d("ImsPhoneMmiCode", "processCode: Sending short code '" + this.mDialingNumber + "' over CS pipe.");
            throw new CallStateException(Phone.CS_FALLBACK);
        }
        int i3 = 1;
        if (isServiceCodeCallForwarding(this.mSc)) {
            Rlog.d("ImsPhoneMmiCode", "processCode: is CF");
            String str = this.mSia;
            int scToCallForwardReason = scToCallForwardReason(this.mSc);
            int siToServiceClass = siToServiceClass(this.mSib);
            int siToTime = siToTime(this.mSic);
            if (isInterrogate()) {
                this.mPhone.getCallForwardingOption(scToCallForwardReason, siToServiceClass, obtainMessage(1, this));
                return;
            }
            if (isActivate()) {
                if (isEmptyOrNull(str)) {
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
            if (scToCallForwardReason != 0 && scToCallForwardReason != 4) {
                i2 = 0;
                if (i != 1 && i != 3) {
                    i3 = 0;
                }
                Rlog.d("ImsPhoneMmiCode", "processCode: is CF setCallForward");
                this.mPhone.setCallForwardingOption(i, scToCallForwardReason, str, siToServiceClass, siToTime, obtainMessage(4, i2, i3, this));
            }
            i2 = 1;
            if (i != 1) {
                i3 = 0;
            }
            Rlog.d("ImsPhoneMmiCode", "processCode: is CF setCallForward");
            this.mPhone.setCallForwardingOption(i, scToCallForwardReason, str, siToServiceClass, siToTime, obtainMessage(4, i2, i3, this));
        } else if (isServiceCodeCallBarring(this.mSc)) {
            String str2 = this.mSia;
            String scToBarringFacility = scToBarringFacility(this.mSc);
            int siToServiceClass2 = siToServiceClass(this.mSib);
            if (isInterrogate()) {
                this.mPhone.getCallBarring(scToBarringFacility, obtainMessage(7, this), siToServiceClass2);
                return;
            }
            if (!isActivate() && !isDeactivate()) {
                throw new RuntimeException("Invalid or Unsupported MMI Code");
            }
            this.mPhone.setCallBarring(scToBarringFacility, isActivate(), str2, obtainMessage(0, this), siToServiceClass2);
        } else {
            String str3 = this.mSc;
            if (str3 != null && str3.equals("31")) {
                if (isActivate() && !this.mPhone.getDefaultPhone().isClirActivationAndDeactivationPrevented()) {
                    try {
                        this.mPhone.setOutgoingCallerIdDisplay(1, obtainMessage(0, this));
                        return;
                    } catch (Exception unused) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCLIR.");
                        return;
                    }
                } else if (isDeactivate() && !this.mPhone.getDefaultPhone().isClirActivationAndDeactivationPrevented()) {
                    try {
                        this.mPhone.setOutgoingCallerIdDisplay(2, obtainMessage(0, this));
                        return;
                    } catch (Exception unused2) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCLIR.");
                        return;
                    }
                } else if (isInterrogate()) {
                    try {
                        this.mPhone.getOutgoingCallerIdDisplay(obtainMessage(6, this));
                        return;
                    } catch (Exception unused3) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for queryCLIR.");
                        return;
                    }
                } else {
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
            }
            String str4 = this.mSc;
            if (str4 != null && str4.equals("30")) {
                if (isInterrogate()) {
                    try {
                        this.mPhone.queryCLIP(obtainMessage(7, this));
                        return;
                    } catch (Exception unused4) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for queryCLIP.");
                        return;
                    }
                }
                if (!isActivate() && !isDeactivate()) {
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
                try {
                    this.mPhone.mCT.getUtInterface().updateCLIP(isActivate(), obtainMessage(0, this));
                    return;
                } catch (ImsException unused5) {
                    Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCLIP.");
                    return;
                }
            }
            String str5 = this.mSc;
            if (str5 != null && str5.equals("76")) {
                if (isInterrogate()) {
                    try {
                        this.mPhone.mCT.getUtInterface().queryCOLP(obtainMessage(7, this));
                        return;
                    } catch (ImsException unused6) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for queryCOLP.");
                        return;
                    }
                }
                if (!isActivate() && !isDeactivate()) {
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
                try {
                    this.mPhone.mCT.getUtInterface().updateCOLP(isActivate(), obtainMessage(0, this));
                    return;
                } catch (ImsException unused7) {
                    Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCOLP.");
                    return;
                }
            }
            String str6 = this.mSc;
            if (str6 != null && str6.equals("77")) {
                if (isActivate()) {
                    try {
                        this.mPhone.mCT.getUtInterface().updateCOLR(1, obtainMessage(0, this));
                        return;
                    } catch (ImsException unused8) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCOLR.");
                        return;
                    }
                } else if (isDeactivate()) {
                    try {
                        this.mPhone.mCT.getUtInterface().updateCOLR(0, obtainMessage(0, this));
                        return;
                    } catch (ImsException unused9) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for updateCOLR.");
                        return;
                    }
                } else if (isInterrogate()) {
                    try {
                        this.mPhone.mCT.getUtInterface().queryCOLR(obtainMessage(7, this));
                        return;
                    } catch (ImsException unused10) {
                        Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for queryCOLR.");
                        return;
                    }
                } else {
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
            }
            String str7 = this.mSc;
            if (str7 != null && str7.equals("156")) {
                try {
                    if (isInterrogate()) {
                        this.mPhone.mCT.getUtInterface().queryCallBarring(10, obtainMessage(10, this));
                    } else {
                        processIcbMmiCodeForUpdate();
                    }
                    return;
                } catch (ImsException unused11) {
                    Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for ICB.");
                    return;
                }
            }
            String str8 = this.mSc;
            if (str8 != null && str8.equals("157")) {
                try {
                    if (isInterrogate()) {
                        this.mPhone.mCT.getUtInterface().queryCallBarring(6, obtainMessage(10, this));
                        return;
                    }
                    if (!isActivate()) {
                        isDeactivate();
                        i3 = 0;
                    }
                    this.mPhone.mCT.getUtInterface().updateCallBarring(6, i3, obtainMessage(0, this), (String[]) null);
                    return;
                } catch (ImsException unused12) {
                    Rlog.d("ImsPhoneMmiCode", "processCode: Could not get UT handle for ICBa.");
                    return;
                }
            }
            String str9 = this.mSc;
            if (str9 != null && str9.equals("43")) {
                int siToServiceClass3 = siToServiceClass(this.mSia);
                if (!isActivate() && !isDeactivate()) {
                    if (isInterrogate()) {
                        if (this.mPhone.getTerminalBasedCallWaitingState(false) != -1) {
                            this.mPhone.getDefaultPhone().getCallWaiting(obtainMessage(3, this));
                            return;
                        } else {
                            this.mPhone.getCallWaiting(obtainMessage(3, this));
                            return;
                        }
                    }
                    throw new RuntimeException("Invalid or Unsupported MMI Code");
                }
                if (this.mPhone.getTerminalBasedCallWaitingState(false) != -1) {
                    this.mPhone.getDefaultPhone().setCallWaiting(isActivate(), siToServiceClass3, obtainMessage(0, this));
                    return;
                }
                this.mPhone.setCallWaiting(isActivate(), siToServiceClass3, obtainMessage(0, this));
                return;
            } else if (this.mPoundString != null) {
                if (isUssdOverImsAllowed()) {
                    Rlog.i("ImsPhoneMmiCode", "processCode: Sending ussd string '" + Rlog.pii("ImsPhoneMmiCode", this.mPoundString) + "' over IMS pipe.");
                    sendUssd(this.mPoundString);
                    return;
                }
                Rlog.i("ImsPhoneMmiCode", "processCode: Sending ussd string '" + Rlog.pii("ImsPhoneMmiCode", this.mPoundString) + "' over CS pipe.");
                throw new CallStateException(Phone.CS_FALLBACK);
            } else {
                Rlog.d("ImsPhoneMmiCode", "processCode: invalid or unsupported MMI");
                throw new RuntimeException("Invalid or Unsupported MMI Code");
            }
            this.mState = MmiCode.State.FAILED;
            this.mMessage = this.mContext.getText(17040808);
            Rlog.d("ImsPhoneMmiCode", "processCode: RuntimeException = " + e);
            this.mPhone.onMMIDone(this);
        }
    }

    private boolean isUssdOverImsAllowed() {
        if (this.mContext.getResources().getBoolean(17891364)) {
            int intCarrierConfig = getIntCarrierConfig("carrier_ussd_method_int");
            if (intCarrierConfig == 0) {
                if (this.mPhone.getDefaultPhone().getServiceStateTracker().mSS.getState() == 0) {
                    return false;
                }
                Rlog.i("ImsPhoneMmiCode", "isUssdOverImsAllowed: CS is out of service");
                return true;
            }
            if (intCarrierConfig != 1) {
                if (intCarrierConfig != 2) {
                    if (intCarrierConfig != 3) {
                        Rlog.i("ImsPhoneMmiCode", "isUssdOverImsAllowed: Unsupported method");
                    }
                }
                return false;
            }
            return true;
        }
        Rlog.i("ImsPhoneMmiCode", "isUssdOverImsAllowed: USSD over IMS pipe is not supported.");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUssdFinished(String str, boolean z) {
        if (this.mState == MmiCode.State.PENDING) {
            if (TextUtils.isEmpty(str)) {
                this.mMessage = this.mContext.getText(17040807);
                Rlog.v("ImsPhoneMmiCode", "onUssdFinished: no message; using: " + ((Object) this.mMessage));
            } else {
                Rlog.v("ImsPhoneMmiCode", "onUssdFinished: message: " + str);
                this.mMessage = str;
            }
            this.mIsUssdRequest = z;
            if (!z) {
                this.mState = MmiCode.State.COMPLETE;
            }
            this.mPhone.onMMIDone(this);
        }
    }

    public void onUssdFinishedError() {
        if (this.mState == MmiCode.State.PENDING) {
            this.mState = MmiCode.State.FAILED;
            if (TextUtils.isEmpty(this.mMessage)) {
                this.mMessage = this.mContext.getText(17040808);
            }
            Rlog.d("ImsPhoneMmiCode", "onUssdFinishedError: mmi=" + this);
            this.mPhone.onMMIDone(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendUssd(String str) {
        this.mIsPendingUSSD = true;
        this.mPhone.sendUSSD(str, obtainMessage(2, this));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i != 10) {
            switch (i) {
                case 0:
                    onSetComplete(message, (AsyncResult) message.obj);
                    return;
                case 1:
                    onQueryCfComplete((AsyncResult) message.obj);
                    return;
                case 2:
                    AsyncResult asyncResult = (AsyncResult) message.obj;
                    if (asyncResult.exception != null) {
                        this.mState = MmiCode.State.FAILED;
                        this.mMessage = getErrorMessage(asyncResult);
                        this.mPhone.onMMIDone(this);
                        return;
                    }
                    return;
                case 3:
                    onQueryComplete((AsyncResult) message.obj);
                    return;
                case 4:
                    AsyncResult asyncResult2 = (AsyncResult) message.obj;
                    if (asyncResult2.exception == null && message.arg1 == 1) {
                        boolean z = message.arg2 == 1;
                        IccRecords iccRecords = this.mIccRecords;
                        if (iccRecords != null) {
                            this.mPhone.setVoiceCallForwardingFlag(iccRecords, 1, z, this.mDialingNumber);
                        }
                    }
                    onSetComplete(message, asyncResult2);
                    return;
                case 5:
                    this.mPhone.onMMIDone(this);
                    return;
                case 6:
                    onQueryClirComplete((AsyncResult) message.obj);
                    return;
                case 7:
                    onSuppSvcQueryComplete((AsyncResult) message.obj);
                    return;
                default:
                    return;
            }
        }
        onIcbQueryComplete((AsyncResult) message.obj);
    }

    private void processIcbMmiCodeForUpdate() {
        String str = this.mSia;
        try {
            this.mPhone.mCT.getUtInterface().updateCallBarring(10, callBarAction(str), obtainMessage(0, this), str != null ? str.split("\\$") : null);
        } catch (ImsException unused) {
            Rlog.d("ImsPhoneMmiCode", "processIcbMmiCodeForUpdate:Could not get UT handle for updating ICB.");
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CharSequence getErrorMessage(AsyncResult asyncResult) {
        CharSequence mmiErrorMessage = getMmiErrorMessage(asyncResult);
        return mmiErrorMessage != null ? mmiErrorMessage : this.mContext.getText(17040808);
    }

    @VisibleForTesting
    public CharSequence getMmiErrorMessage(AsyncResult asyncResult) {
        ImsException imsException = asyncResult.exception;
        if (imsException instanceof ImsException) {
            int code = imsException.getCode();
            if (code == 241) {
                return this.mContext.getText(17040811);
            }
            switch (code) {
                case 822:
                    return this.mContext.getText(17041611);
                case 823:
                    return this.mContext.getText(17041614);
                case 824:
                    return this.mContext.getText(17041613);
                case 825:
                    return this.mContext.getText(17041612);
                default:
                    return null;
            }
        }
        if (imsException instanceof CommandException) {
            CommandException commandException = (CommandException) imsException;
            if (commandException.getCommandError() == CommandException.Error.FDN_CHECK_FAILURE) {
                return this.mContext.getText(17040811);
            }
            if (commandException.getCommandError() == CommandException.Error.SS_MODIFIED_TO_DIAL) {
                return this.mContext.getText(17041611);
            }
            if (commandException.getCommandError() == CommandException.Error.SS_MODIFIED_TO_USSD) {
                return this.mContext.getText(17041614);
            }
            if (commandException.getCommandError() == CommandException.Error.SS_MODIFIED_TO_SS) {
                return this.mContext.getText(17041613);
            }
            if (commandException.getCommandError() == CommandException.Error.SS_MODIFIED_TO_DIAL_VIDEO) {
                return this.mContext.getText(17041612);
            }
            if (commandException.getCommandError() == CommandException.Error.INTERNAL_ERR) {
                return this.mContext.getText(17040808);
            }
            if (commandException.getCommandError() == CommandException.Error.REQUEST_NOT_SUPPORTED || commandException.getCommandError() == CommandException.Error.OPERATION_NOT_ALLOWED) {
                return this.mContext.getResources().getText(17040809);
            }
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CharSequence getScString() {
        String str = this.mSc;
        if (str != null) {
            if (isServiceCodeCallBarring(str)) {
                return this.mContext.getText(17039428);
            }
            if (isServiceCodeCallForwarding(this.mSc)) {
                return this.mContext.getText(17039434);
            }
            if (this.mSc.equals("03")) {
                return this.mContext.getText(17039559);
            }
            if (this.mSc.equals("43")) {
                return this.mContext.getText(17039442);
            }
            if (this.mSc.equals("30")) {
                return this.mContext.getText(17039435);
            }
            if (this.mSc.equals("31")) {
                return this.mContext.getText(17039436);
            }
            if (this.mSc.equals("76")) {
                return this.mContext.getText(17039440);
            }
            if (this.mSc.equals("77")) {
                return this.mContext.getText(17039441);
            }
            return this.mSc.equals("156") ? "Specific Incoming Call Barring" : this.mSc.equals("157") ? "Anonymous Incoming Call Barring" : PhoneConfigurationManager.SSSS;
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
                CommandException commandException = (CommandException) th;
                if (commandException.getCommandError() == CommandException.Error.PASSWORD_INCORRECT) {
                    sb.append(this.mContext.getText(17040932));
                } else {
                    CharSequence mmiErrorMessage = getMmiErrorMessage(asyncResult);
                    if (mmiErrorMessage != null) {
                        sb.append(mmiErrorMessage);
                    } else if (commandException.getMessage() != null) {
                        sb.append(commandException.getMessage());
                    } else {
                        sb.append(this.mContext.getText(17040808));
                    }
                }
            } else if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            }
        } else {
            Object obj = asyncResult.result;
            if (obj != null && (obj instanceof Integer) && ((Integer) obj).intValue() == 255) {
                this.mState = MmiCode.State.FAILED;
                sb = null;
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
        }
        this.mMessage = sb;
        Rlog.d("ImsPhoneMmiCode", "onSetComplete: mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
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
        charSequenceArr[1] = PhoneNumberUtils.stringFromStringAndTOA(callForwardInfo.number, callForwardInfo.toa);
        charSequenceArr[2] = Integer.toString(callForwardInfo.timeSeconds);
        if (callForwardInfo.reason == 0 && (i & callForwardInfo.serviceClass) == 1) {
            this.mPhone.setVoiceCallForwardingFlag(this.mIccRecords, 1, callForwardInfo.status == 1, callForwardInfo.number);
        }
        return TextUtils.replace(text, strArr, charSequenceArr);
    }

    private void onQueryCfComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        Throwable th = asyncResult.exception;
        if (th != null) {
            this.mState = MmiCode.State.FAILED;
            if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            } else {
                sb.append(getErrorMessage(asyncResult));
            }
        } else {
            Object obj = asyncResult.result;
            if ((obj instanceof CallForwardInfo[]) && ((CallForwardInfo[]) obj)[0].status == 255) {
                this.mState = MmiCode.State.FAILED;
                sb = null;
            } else {
                CallForwardInfo[] callForwardInfoArr = (CallForwardInfo[]) obj;
                if (callForwardInfoArr == null || callForwardInfoArr.length == 0) {
                    sb.append(this.mContext.getText(17041508));
                    this.mPhone.setVoiceCallForwardingFlag(this.mIccRecords, 1, false, null);
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
        }
        this.mMessage = sb;
        Rlog.d("ImsPhoneMmiCode", "onQueryCfComplete: mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private void onSuppSvcQueryComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        this.mState = MmiCode.State.FAILED;
        Throwable th = asyncResult.exception;
        if (th != null) {
            if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            } else {
                sb.append(getErrorMessage(asyncResult));
            }
        } else if (asyncResult.result instanceof ImsSsInfo) {
            Rlog.d("ImsPhoneMmiCode", "onSuppSvcQueryComplete: Received CLIP/COLP/COLR Response.");
            ImsSsInfo imsSsInfo = (ImsSsInfo) asyncResult.result;
            if (imsSsInfo != null) {
                Rlog.d("ImsPhoneMmiCode", "onSuppSvcQueryComplete: ImsSsInfo mStatus = " + imsSsInfo.getStatus());
                if (imsSsInfo.getProvisionStatus() == 0) {
                    sb.append(this.mContext.getText(17041512));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (imsSsInfo.getStatus() == 0) {
                    sb.append(this.mContext.getText(17041508));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (imsSsInfo.getStatus() == 1) {
                    sb.append(this.mContext.getText(17041509));
                    this.mState = MmiCode.State.COMPLETE;
                } else {
                    sb.append(this.mContext.getText(17040808));
                }
            } else {
                sb.append(this.mContext.getText(17040808));
            }
        } else {
            Rlog.d("ImsPhoneMmiCode", "onSuppSvcQueryComplete: Received Call Barring/CSFB CLIP Response.");
            int[] iArr = (int[]) asyncResult.result;
            if (iArr == null || iArr.length == 0) {
                sb.append(this.mContext.getText(17040808));
            } else if (iArr[0] != 0) {
                sb.append(this.mContext.getText(17041509));
                this.mState = MmiCode.State.COMPLETE;
            } else {
                sb.append(this.mContext.getText(17041508));
                this.mState = MmiCode.State.COMPLETE;
            }
        }
        this.mMessage = sb;
        Rlog.d("ImsPhoneMmiCode", "onSuppSvcQueryComplete mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private void onIcbQueryComplete(AsyncResult asyncResult) {
        List asList;
        Rlog.d("ImsPhoneMmiCode", "onIcbQueryComplete mmi=" + this);
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        Throwable th = asyncResult.exception;
        if (th != null) {
            this.mState = MmiCode.State.FAILED;
            if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            } else {
                sb.append(getErrorMessage(asyncResult));
            }
        } else {
            try {
                asList = (List) asyncResult.result;
            } catch (ClassCastException unused) {
                asList = Arrays.asList((ImsSsInfo[]) asyncResult.result);
            }
            if (asList == null || asList.size() == 0) {
                sb.append(this.mContext.getText(17041508));
            } else {
                int size = asList.size();
                for (int i = 0; i < size; i++) {
                    ImsSsInfo imsSsInfo = (ImsSsInfo) asList.get(i);
                    if (imsSsInfo.getIncomingCommunicationBarringNumber() != null) {
                        sb.append("Num: " + imsSsInfo.getIncomingCommunicationBarringNumber() + " status: " + imsSsInfo.getStatus() + "\n");
                    } else if (imsSsInfo.getStatus() == 1) {
                        sb.append(this.mContext.getText(17041509));
                    } else {
                        sb.append(this.mContext.getText(17041508));
                    }
                }
            }
            this.mState = MmiCode.State.COMPLETE;
        }
        this.mMessage = sb;
        this.mPhone.onMMIDone(this);
    }

    private void onQueryClirComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        MmiCode.State state = MmiCode.State.FAILED;
        this.mState = state;
        Throwable th = asyncResult.exception;
        if (th != null) {
            if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            } else {
                sb.append(getErrorMessage(asyncResult));
            }
        } else {
            int[] iArr = (int[]) asyncResult.result;
            Rlog.d("ImsPhoneMmiCode", "onQueryClirComplete: CLIR param n=" + iArr[0] + " m=" + iArr[1]);
            int i = iArr[1];
            if (i == 0) {
                sb.append(this.mContext.getText(17041512));
                this.mState = MmiCode.State.COMPLETE;
            } else if (i == 1) {
                sb.append(this.mContext.getText(17039433));
                this.mState = MmiCode.State.COMPLETE;
            } else if (i == 3) {
                int i2 = iArr[0];
                if (i2 == 0) {
                    sb.append(this.mContext.getText(17039432));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (i2 == 1) {
                    sb.append(this.mContext.getText(17039432));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (i2 == 2) {
                    sb.append(this.mContext.getText(17039431));
                    this.mState = MmiCode.State.COMPLETE;
                } else {
                    sb.append(this.mContext.getText(17040808));
                    this.mState = state;
                }
            } else if (i == 4) {
                int i3 = iArr[0];
                if (i3 == 0) {
                    sb.append(this.mContext.getText(17039429));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (i3 == 1) {
                    sb.append(this.mContext.getText(17039430));
                    this.mState = MmiCode.State.COMPLETE;
                } else if (i3 == 2) {
                    sb.append(this.mContext.getText(17039429));
                    this.mState = MmiCode.State.COMPLETE;
                } else {
                    sb.append(this.mContext.getText(17040808));
                    this.mState = state;
                }
            } else {
                sb.append(this.mContext.getText(17040808));
                this.mState = state;
            }
        }
        this.mMessage = sb;
        Rlog.d("ImsPhoneMmiCode", "onQueryClirComplete mmi=" + this);
        this.mPhone.onMMIDone(this);
    }

    private void onQueryComplete(AsyncResult asyncResult) {
        StringBuilder sb = new StringBuilder(getScString());
        sb.append("\n");
        this.mState = MmiCode.State.FAILED;
        Throwable th = asyncResult.exception;
        if (th != null) {
            if (th instanceof ImsException) {
                sb.append(getImsErrorMessage(asyncResult));
            } else {
                sb.append(getErrorMessage(asyncResult));
            }
        } else {
            Object obj = asyncResult.result;
            if ((obj instanceof int[]) && ((int[]) obj)[0] == 255) {
                sb = null;
            } else {
                int[] iArr = (int[]) obj;
                if (iArr != null && iArr.length != 0) {
                    if (iArr[0] == 0) {
                        sb.append(this.mContext.getText(17041508));
                        this.mState = MmiCode.State.COMPLETE;
                    } else if (this.mSc.equals("43")) {
                        sb.append(createQueryCallWaitingResultMessage(iArr[1]));
                        this.mState = MmiCode.State.COMPLETE;
                    } else if (iArr[0] == 1) {
                        sb.append(this.mContext.getText(17041509));
                        this.mState = MmiCode.State.COMPLETE;
                    } else {
                        sb.append(this.mContext.getText(17040808));
                    }
                } else {
                    sb.append(this.mContext.getText(17040808));
                }
            }
        }
        this.mMessage = sb;
        Rlog.d("ImsPhoneMmiCode", "onQueryComplete mmi=" + this);
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

    private CharSequence getImsErrorMessage(AsyncResult asyncResult) {
        ImsException imsException = asyncResult.exception;
        CharSequence mmiErrorMessage = getMmiErrorMessage(asyncResult);
        if (mmiErrorMessage != null) {
            return mmiErrorMessage;
        }
        if (imsException.getMessage() != null) {
            return imsException.getMessage();
        }
        return getErrorMessage(asyncResult);
    }

    private int getIntCarrierConfig(String str) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
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

    public void processImsSsData(AsyncResult asyncResult) throws ImsException {
        try {
            parseSsData((ImsSsData) asyncResult.result);
        } catch (ClassCastException | NullPointerException unused) {
            throw new ImsException("Exception in parsing SS Data", 0);
        }
    }

    void parseSsData(ImsSsData imsSsData) {
        Throwable imsException = imsSsData.getResult() != 0 ? new ImsException((String) null, imsSsData.getResult()) : null;
        this.mSc = getScStringFromScType(imsSsData.getServiceType());
        this.mAction = getActionStringFromReqType(imsSsData.getRequestType());
        Rlog.d("ImsPhoneMmiCode", "parseSsData msc = " + this.mSc + ", action = " + this.mAction + ", ex = " + imsException);
        int requestType = imsSsData.getRequestType();
        boolean z = false;
        if (requestType != 0 && requestType != 1) {
            if (requestType == 2) {
                if (imsSsData.isTypeClir()) {
                    Rlog.d("ImsPhoneMmiCode", "CLIR INTERROGATION");
                    onQueryClirComplete(new AsyncResult((Object) null, imsSsData.getSuppServiceInfoCompat(), imsException));
                    return;
                } else if (imsSsData.isTypeCF()) {
                    Rlog.d("ImsPhoneMmiCode", "CALL FORWARD INTERROGATION");
                    List callForwardInfo = imsSsData.getCallForwardInfo();
                    onQueryCfComplete(new AsyncResult((Object) null, this.mPhone.handleCfQueryResult(callForwardInfo != null ? (ImsCallForwardInfo[]) callForwardInfo.toArray(new ImsCallForwardInfo[callForwardInfo.size()]) : null), imsException));
                    return;
                } else if (imsSsData.isTypeBarring()) {
                    onSuppSvcQueryComplete(new AsyncResult((Object) null, imsSsData.getSuppServiceInfoCompat(), imsException));
                    return;
                } else if (imsSsData.isTypeColr() || imsSsData.isTypeClip() || imsSsData.isTypeColp()) {
                    onSuppSvcQueryComplete(new AsyncResult((Object) null, imsSsData.getSuppServiceInfo().get(0), imsException));
                    return;
                } else if (imsSsData.isTypeIcb()) {
                    onIcbQueryComplete(new AsyncResult((Object) null, imsSsData.getSuppServiceInfo(), imsException));
                    return;
                } else {
                    onQueryComplete(new AsyncResult((Object) null, imsSsData.getSuppServiceInfoCompat(), imsException));
                    return;
                }
            } else if (requestType != 3 && requestType != 4) {
                Rlog.e("ImsPhoneMmiCode", "Invaid requestType in SSData : " + imsSsData.getRequestType());
                return;
            }
        }
        if (imsSsData.getResult() == 0 && imsSsData.isTypeUnConditional()) {
            if ((imsSsData.getRequestType() == 0 || imsSsData.getRequestType() == 3) && isServiceClassVoiceVideoOrNone(imsSsData.getServiceClass())) {
                z = true;
            }
            Rlog.d("ImsPhoneMmiCode", "setCallForwardingFlag cffEnabled: " + z);
            if (this.mIccRecords != null) {
                Rlog.d("ImsPhoneMmiCode", "setVoiceCallForwardingFlag done from SS Info.");
                this.mPhone.setVoiceCallForwardingFlag(1, z, null);
            } else {
                Rlog.e("ImsPhoneMmiCode", "setCallForwardingFlag aborted. sim records is null.");
            }
        }
        onSetComplete(null, new AsyncResult((Object) null, imsSsData.getCallForwardInfo(), imsException));
    }

    public boolean isSsInfo() {
        return this.mIsSsInfo;
    }

    public void setIsSsInfo(boolean z) {
        this.mIsSsInfo = z;
    }

    @Override // android.os.Handler
    public String toString() {
        StringBuilder sb = new StringBuilder("ImsPhoneMmiCode {");
        sb.append("State=" + getState());
        if (this.mAction != null) {
            sb.append(" action=" + this.mAction);
        }
        if (this.mSc != null) {
            sb.append(" sc=" + this.mSc);
        }
        if (this.mSia != null) {
            sb.append(" sia=" + this.mSia);
        }
        if (this.mSib != null) {
            sb.append(" sib=" + this.mSib);
        }
        if (this.mSic != null) {
            sb.append(" sic=" + this.mSic);
        }
        if (this.mPoundString != null) {
            sb.append(" poundString=" + Rlog.pii("ImsPhoneMmiCode", this.mPoundString));
        }
        if (this.mDialingNumber != null) {
            sb.append(" dialingNumber=" + Rlog.pii("ImsPhoneMmiCode", this.mDialingNumber));
        }
        if (this.mPwd != null) {
            sb.append(" pwd=" + Rlog.pii("ImsPhoneMmiCode", this.mPwd));
        }
        if (this.mCallbackReceiver != null) {
            sb.append(" hasReceiver");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override // com.android.internal.telephony.MmiCode
    public boolean isNetworkInitiatedUssd() {
        return this.mIsNetworkInitiatedUSSD;
    }
}
