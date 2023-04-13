package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
/* loaded from: classes.dex */
public class IccIoResult {
    @UnsupportedAppUsage
    public byte[] payload;
    @UnsupportedAppUsage
    public int sw1;
    @UnsupportedAppUsage
    public int sw2;

    private String getErrorString() {
        int i = this.sw1;
        if (i == 152) {
            int i2 = this.sw2;
            if (i2 != 2) {
                if (i2 != 4) {
                    if (i2 != 8) {
                        if (i2 != 16) {
                            if (i2 != 64) {
                                if (i2 != 80) {
                                    if (i2 != 98) {
                                        switch (i2) {
                                            case 100:
                                                return "authentication error, security context not supported";
                                            case 101:
                                                return "key freshness failure";
                                            case CallFailCause.RECOVERY_ON_TIMER_EXPIRY /* 102 */:
                                                return "authentication error, no memory space available";
                                            case 103:
                                                return "authentication error, no memory space available in EF_MUK";
                                            default:
                                                return "unknown";
                                        }
                                    }
                                    return "authentication error, application specific";
                                }
                                return "increase cannot be performed, Max value reached";
                            }
                            return "unsuccessful CHV verification, no attempt left/unsuccessful UNBLOCK CHV verification, no attempt left/CHV blocked/UNBLOCK CHV blocked";
                        }
                        return "in contradiction with invalidation status";
                    }
                    return "in contradiction with CHV status";
                }
                return "access condition not fulfilled/unsuccessful CHV verification, at least one attempt left/unsuccessful UNBLOCK CHV verification, at least one attempt left/authentication failed";
            }
            return "no CHV initialized";
        } else if (i == 158 || i == 159) {
            return null;
        } else {
            switch (i) {
                case 97:
                    return this.sw2 + " more response bytes available";
                case 98:
                    int i3 = this.sw2;
                    if (i3 != 0) {
                        switch (i3) {
                            case 129:
                                return "part of returned data may be corrupted";
                            case 130:
                                return "end of file/record reached before reading Le bytes";
                            case 131:
                                return "selected file invalidated";
                            case UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP /* 132 */:
                                return "selected file in termination state";
                            default:
                                switch (i3) {
                                    case CallFailCause.FDN_BLOCKED /* 241 */:
                                        return "more data available";
                                    case 242:
                                        return "more data available and proactive command pending";
                                    case CallFailCause.IMEI_NOT_ACCEPTED /* 243 */:
                                        return "response data available";
                                    default:
                                        return "unknown";
                                }
                        }
                    }
                    return "no information given, state of non volatile memory unchanged";
                case 99:
                    int i4 = this.sw2;
                    if ((i4 >> 4) != 12) {
                        return i4 != 241 ? i4 != 242 ? "unknown" : "more data expected and proactive command pending" : "more data expected";
                    }
                    int i5 = i4 & 15;
                    return "command successful but after using an internal update retry routine " + i5 + " times, or verification failed, " + i5 + " retries remaining";
                case 100:
                    return this.sw2 != 0 ? "unknown" : "no information given, state of non-volatile memory unchanged";
                case 101:
                    int i6 = this.sw2;
                    return i6 != 0 ? i6 != 129 ? "unknown" : "memory problem" : "no information given, state of non-volatile memory changed";
                default:
                    switch (i) {
                        case 103:
                            return this.sw2 != 0 ? "the interpretation of this status word is command dependent" : "incorrect parameter P3";
                        case 104:
                            int i7 = this.sw2;
                            return i7 != 0 ? i7 != 129 ? i7 != 130 ? "unknown" : "secure messaging not supported" : "logical channel not supported" : "no information given";
                        case 105:
                            int i8 = this.sw2;
                            if (i8 != 0) {
                                if (i8 != 137) {
                                    switch (i8) {
                                        case 129:
                                            return "command incompatible with file structure";
                                        case 130:
                                            return "security status not satisfied";
                                        case 131:
                                            return "authentication/PIN method blocked";
                                        case UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP /* 132 */:
                                            return "referenced data invalidated";
                                        case 133:
                                            return "conditions of use not satisfied";
                                        case 134:
                                            return "command not allowed (no EF selected)";
                                        default:
                                            return "unknown";
                                    }
                                }
                                return "command not allowed - secure channel - security not satisfied";
                            }
                            return "no information given";
                        case 106:
                            switch (this.sw2) {
                                case 128:
                                    return "incorrect parameters in the data field";
                                case 129:
                                    return "function not supported";
                                case 130:
                                    return "file not found";
                                case 131:
                                    return "record not found";
                                case UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP /* 132 */:
                                    return "not enough memory space";
                                case 133:
                                default:
                                    return "unknown";
                                case 134:
                                    return "incorrect parameters P1 to P2";
                                case NetworkStackConstants.ICMPV6_NEIGHBOR_SOLICITATION /* 135 */:
                                    return "lc inconsistent with P1 to P2";
                                case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                                    return "referenced data not found";
                            }
                        case 107:
                            return "incorrect parameter P1 or P2";
                        case 108:
                            return "wrong length, retry with " + this.sw2;
                        case 109:
                            return "unknown instruction code given in the command";
                        case 110:
                            return "wrong instruction class given in the command";
                        case 111:
                            return this.sw2 != 0 ? "the interpretation of this status word is command dependent" : "technical problem with no diagnostic given";
                        default:
                            switch (i) {
                                case 144:
                                case 145:
                                    return null;
                                case 146:
                                    int i9 = this.sw2;
                                    return (i9 >> 4) == 0 ? "command successful but after using an internal update retry routine" : i9 != 64 ? "unknown" : "memory problem";
                                case 147:
                                    return this.sw2 != 0 ? "unknown" : "SIM Application Toolkit is busy. Command cannot be executed at present, further normal commands are allowed";
                                case 148:
                                    int i10 = this.sw2;
                                    return i10 != 0 ? i10 != 2 ? i10 != 4 ? i10 != 8 ? "unknown" : "file is inconsistent with the command" : "file ID not found/pattern not found" : "out f range (invalid address)" : "no EF selected";
                                default:
                                    return "unknown";
                            }
                    }
            }
        }
    }

    @UnsupportedAppUsage
    public IccIoResult(int i, int i2, byte[] bArr) {
        this.sw1 = i;
        this.sw2 = i2;
        this.payload = bArr;
    }

    @UnsupportedAppUsage
    public IccIoResult(int i, int i2, String str) {
        this(i, i2, IccUtils.hexStringToBytes(str));
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("IccIoResult sw1:0x");
        sb.append(Integer.toHexString(this.sw1));
        sb.append(" sw2:0x");
        sb.append(Integer.toHexString(this.sw2));
        sb.append(" Payload: ");
        sb.append(TelephonyUtils.IS_DEBUGGABLE ? IccUtils.bytesToHexString(this.payload) : "*******");
        if (success()) {
            str = PhoneConfigurationManager.SSSS;
        } else {
            str = " Error: " + getErrorString();
        }
        sb.append(str);
        return sb.toString();
    }

    @UnsupportedAppUsage
    public boolean success() {
        int i = this.sw1;
        return i == 144 || i == 145 || i == 158 || i == 159;
    }

    public IccException getException() {
        if (success()) {
            return null;
        }
        if (this.sw1 == 148) {
            if (this.sw2 == 8) {
                return new IccFileTypeMismatch();
            }
            return new IccFileNotFound();
        }
        return new IccException("sw1:" + this.sw1 + " sw2:" + this.sw2);
    }
}
