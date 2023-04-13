package com.android.server.hdmi;

import android.util.SparseArray;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.location.gnss.hal.GnssNative;
/* loaded from: classes.dex */
public class HdmiCecMessageValidator {
    public static final SparseArray<ValidationInfo> sValidationInfo = new SparseArray<>();

    /* loaded from: classes.dex */
    public interface ParameterValidator {
        int isValid(byte[] bArr);
    }

    public static boolean isValidAnalogueFrequency(int i) {
        int i2 = i & GnssNative.GNSS_AIDING_TYPE_ALL;
        return (i2 == 0 || i2 == 65535) ? false : true;
    }

    public static boolean isValidDisplayControl(int i) {
        int i2 = i & 255;
        return i2 == 0 || i2 == 64 || i2 == 128 || i2 == 192;
    }

    public static boolean isValidType(int i) {
        return i >= 0 && i <= 7 && i != 2;
    }

    public static boolean isValidUiBroadcastType(int i) {
        return i == 0 || i == 1 || i == 16 || i == 32 || i == 48 || i == 64 || i == 80 || i == 96 || i == 112 || i == 128 || i == 144 || i == 145 || i == 160;
    }

    public static boolean isWithinRange(int i, int i2, int i3) {
        int i4 = i & 255;
        return i4 >= i2 && i4 <= i3;
    }

    public static int toErrorCode(boolean z) {
        return z ? 0 : 3;
    }

    public static int validateAddress(int i, int i2, int i3) {
        if (i == 15 && (i3 & 4) == 0) {
            return 1;
        }
        return i2 == 15 ? (i3 & 2) == 0 ? 2 : 0 : (i3 & 1) == 0 ? 2 : 0;
    }

    /* loaded from: classes.dex */
    public static class ValidationInfo {
        public final int addressType;
        public final ParameterValidator parameterValidator;

        public ValidationInfo(ParameterValidator parameterValidator, int i) {
            this.parameterValidator = parameterValidator;
            this.addressType = i;
        }
    }

    static {
        PhysicalAddressValidator physicalAddressValidator = new PhysicalAddressValidator();
        addValidationInfo(130, physicalAddressValidator, 6);
        addValidationInfo(FrameworkStatsLog.f421x729e24be, physicalAddressValidator, 1);
        addValidationInfo(132, new ReportPhysicalAddressValidator(), 6);
        addValidationInfo(128, new RoutingChangeValidator(), 6);
        addValidationInfo(129, physicalAddressValidator, 6);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE, physicalAddressValidator, 2);
        addValidationInfo(112, new SystemAudioModeRequestValidator(), 1);
        FixedLengthValidator fixedLengthValidator = new FixedLengthValidator(0);
        addValidationInfo(255, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f420x89db317, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__CAN_INTERACT_ACROSS_PROFILES_TRUE, fixedLengthValidator, 5);
        addValidationInfo(113, fixedLengthValidator, 1);
        addValidationInfo(143, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES, fixedLengthValidator, 5);
        addValidationInfo(70, fixedLengthValidator, 1);
        addValidationInfo(131, fixedLengthValidator, 5);
        addValidationInfo(125, fixedLengthValidator, 1);
        addValidationInfo(4, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f392xcd34d435, fixedLengthValidator, 1);
        addValidationInfo(11, fixedLengthValidator, 1);
        addValidationInfo(15, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f390xde8506f2, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_ERROR, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f411x277c884, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f410xb766d392, fixedLengthValidator, 1);
        addValidationInfo(133, fixedLengthValidator, 6);
        addValidationInfo(54, fixedLengthValidator, 7);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_PARAM, fixedLengthValidator, 1);
        addValidationInfo(13, fixedLengthValidator, 1);
        addValidationInfo(6, fixedLengthValidator, 1);
        addValidationInfo(5, fixedLengthValidator, 1);
        addValidationInfo(69, fixedLengthValidator, 1);
        addValidationInfo(FrameworkStatsLog.f427xc808b4a, fixedLengthValidator, 3);
        addValidationInfo(9, new VariableLengthValidator(1, 8), 1);
        addValidationInfo(10, new RecordStatusInfoValidator(), 1);
        addValidationInfo(51, new AnalogueTimerValidator(), 1);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PROVISIONING_DPC_SETUP_COMPLETED, new DigitalTimerValidator(), 1);
        addValidationInfo(FrameworkStatsLog.f416x35de4f00, new ExternalTimerValidator(), 1);
        addValidationInfo(52, new AnalogueTimerValidator(), 1);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__BIND_CROSS_PROFILE_SERVICE, new DigitalTimerValidator(), 1);
        addValidationInfo(FrameworkStatsLog.f379x24956b9a, new ExternalTimerValidator(), 1);
        addValidationInfo(103, new AsciiValidator(1, 14), 1);
        addValidationInfo(67, new TimerClearedStatusValidator(), 1);
        addValidationInfo(53, new TimerStatusValidator(), 1);
        FixedLengthValidator fixedLengthValidator2 = new FixedLengthValidator(1);
        addValidationInfo(FrameworkStatsLog.f419x663f9746, fixedLengthValidator2, 1);
        addValidationInfo(50, new AsciiValidator(3), 2);
        OneByteRangeValidator oneByteRangeValidator = new OneByteRangeValidator(1, 3);
        addValidationInfo(66, new OneByteRangeValidator(1, 4), 1);
        addValidationInfo(27, new OneByteRangeValidator(17, 31), 1);
        addValidationInfo(26, oneByteRangeValidator, 1);
        addValidationInfo(65, new PlayModeValidator(), 1);
        addValidationInfo(8, oneByteRangeValidator, 1);
        addValidationInfo(146, new SelectAnalogueServiceValidator(), 1);
        addValidationInfo(147, new SelectDigitalServiceValidator(), 1);
        addValidationInfo(7, new TunerDeviceStatusValidator(), 1);
        VariableLengthValidator variableLengthValidator = new VariableLengthValidator(0, 14);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED, new FixedLengthValidator(3), 2);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__COMP_TO_ORG_OWNED_PO_MIGRATED, new VariableLengthValidator(1, 14), 5);
        addValidationInfo(FrameworkStatsLog.f418x97ec91aa, new VariableLengthValidator(4, 14), 7);
        addValidationInfo(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_CROSS_PROFILE_PACKAGES, variableLengthValidator, 7);
        addValidationInfo(100, new OsdStringValidator(), 1);
        addValidationInfo(71, new AsciiValidator(1, 14), 1);
        addValidationInfo(FrameworkStatsLog.f364xfb4318d7, new OneByteRangeValidator(0, 2), 1);
        addValidationInfo(142, new OneByteRangeValidator(0, 1), 1);
        addValidationInfo(68, new UserControlPressedValidator(), 1);
        addValidationInfo(144, new OneByteRangeValidator(0, 3), 3);
        addValidationInfo(0, new FixedLengthValidator(2), 1);
        addValidationInfo(122, fixedLengthValidator2, 1);
        addValidationInfo(FrameworkStatsLog.f380x2165d62a, new FixedLengthValidator(3), 1);
        addValidationInfo(FrameworkStatsLog.f376xd07885aa, fixedLengthValidator2, 1);
        addValidationInfo(114, new OneByteRangeValidator(0, 1), 3);
        addValidationInfo(126, new OneByteRangeValidator(0, 1), 1);
        addValidationInfo(FrameworkStatsLog.f404xb094ac9f, new OneByteRangeValidator(0, 6), 1);
        addValidationInfo(FrameworkStatsLog.f383xde3a78eb, fixedLengthValidator, 5);
        addValidationInfo(FrameworkStatsLog.f381xaad26533, physicalAddressValidator, 2);
        addValidationInfo(168, new VariableLengthValidator(4, 14), 2);
        addValidationInfo(FrameworkStatsLog.INTEGRITY_RULES_PUSHED, variableLengthValidator, 6);
    }

    public static void addValidationInfo(int i, ParameterValidator parameterValidator, int i2) {
        sValidationInfo.append(i, new ValidationInfo(parameterValidator, i2));
    }

    public static int validate(int i, int i2, int i3, byte[] bArr) {
        ValidationInfo validationInfo = sValidationInfo.get(i3);
        if (validationInfo == null) {
            HdmiLogger.warning("No validation information for the opcode: " + i3, new Object[0]);
            return 0;
        }
        int validateAddress = validateAddress(i, i2, validationInfo.addressType);
        if (validateAddress != 0) {
            return validateAddress;
        }
        int isValid = validationInfo.parameterValidator.isValid(bArr);
        if (isValid != 0) {
            return isValid;
        }
        return 0;
    }

    /* loaded from: classes.dex */
    public static class FixedLengthValidator implements ParameterValidator {
        public final int mLength;

        public FixedLengthValidator(int i) {
            this.mLength = i;
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            return bArr.length < this.mLength ? 4 : 0;
        }
    }

    /* loaded from: classes.dex */
    public static class VariableLengthValidator implements ParameterValidator {
        public final int mMaxLength;
        public final int mMinLength;

        public VariableLengthValidator(int i, int i2) {
            this.mMinLength = i;
            this.mMaxLength = i2;
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            return bArr.length < this.mMinLength ? 4 : 0;
        }
    }

    public static boolean isValidPhysicalAddress(byte[] bArr, int i) {
        int twoBytesToInt = HdmiUtils.twoBytesToInt(bArr, i);
        while (twoBytesToInt != 0) {
            int i2 = 61440 & twoBytesToInt;
            twoBytesToInt = (twoBytesToInt << 4) & GnssNative.GNSS_AIDING_TYPE_ALL;
            if (i2 == 0 && twoBytesToInt != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidAsciiString(byte[] bArr, int i, int i2) {
        while (i < bArr.length && i < i2) {
            if (!isWithinRange(bArr[i], 32, 126)) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static boolean isValidDayOfMonth(int i) {
        return isWithinRange(i, 1, 31);
    }

    public static boolean isValidMonthOfYear(int i) {
        return isWithinRange(i, 1, 12);
    }

    public static boolean isValidHour(int i) {
        return isWithinRange(i, 0, 23);
    }

    public static boolean isValidMinute(int i) {
        return isWithinRange(i, 0, 59);
    }

    public static boolean isValidDurationHours(int i) {
        return isWithinRange(i, 0, 99);
    }

    public static boolean isValidRecordingSequence(int i) {
        int i2 = i & 255;
        return (i2 & 128) == 0 && Integer.bitCount(i2) <= 1;
    }

    public static boolean isValidAnalogueBroadcastType(int i) {
        return isWithinRange(i, 0, 2);
    }

    public static boolean isValidBroadcastSystem(int i) {
        return isWithinRange(i, 0, 31);
    }

    public static boolean isAribDbs(int i) {
        return i == 0 || isWithinRange(i, 8, 10);
    }

    public static boolean isAtscDbs(int i) {
        return i == 1 || isWithinRange(i, 16, 18);
    }

    public static boolean isDvbDbs(int i) {
        return i == 2 || isWithinRange(i, 24, 27);
    }

    public static boolean isValidDigitalBroadcastSystem(int i) {
        return isAribDbs(i) || isAtscDbs(i) || isDvbDbs(i);
    }

    public static boolean isValidChannelIdentifier(byte[] bArr, int i) {
        int i2 = bArr[i] & 252;
        return i2 == 4 ? bArr.length - i >= 3 : i2 == 8 && bArr.length - i >= 4;
    }

    public static boolean isValidDigitalServiceIdentification(byte[] bArr, int i) {
        byte b = bArr[i];
        int i2 = b & 128;
        int i3 = b & Byte.MAX_VALUE;
        int i4 = i + 1;
        if (i2 == 0) {
            return isAribDbs(i3) ? bArr.length - i4 >= 6 : isAtscDbs(i3) ? bArr.length - i4 >= 4 : isDvbDbs(i3) && bArr.length - i4 >= 6;
        } else if (i2 == 128 && isValidDigitalBroadcastSystem(i3)) {
            return isValidChannelIdentifier(bArr, i4);
        }
        return false;
    }

    public static boolean isValidExternalPlug(int i) {
        return isWithinRange(i, 1, 255);
    }

    public static boolean isValidExternalSource(byte[] bArr, int i) {
        byte b = bArr[i];
        int i2 = i + 1;
        if (b == 4) {
            return isValidExternalPlug(bArr[i2]);
        }
        if (b != 5 || bArr.length - i2 < 2) {
            return false;
        }
        return isValidPhysicalAddress(bArr, i2);
    }

    public static boolean isValidProgrammedInfo(int i) {
        return isWithinRange(i, 0, 11);
    }

    public static boolean isValidNotProgrammedErrorInfo(int i) {
        return isWithinRange(i, 0, 14);
    }

    public static boolean isValidTimerStatusData(byte[] bArr, int i) {
        boolean z;
        byte b = bArr[i];
        if ((b & 16) == 16) {
            int i2 = b & 15;
            if (isValidProgrammedInfo(i2)) {
                if (i2 != 9 && i2 != 11) {
                    return true;
                }
                z = true;
            }
            z = false;
        } else {
            int i3 = b & 15;
            if (isValidNotProgrammedErrorInfo(i3)) {
                if (i3 != 14) {
                    return true;
                }
                z = true;
            }
            z = false;
        }
        int i4 = i + 1;
        if (!z || bArr.length - i4 < 2) {
            return false;
        }
        return isValidDurationHours(bArr[i4]) && isValidMinute(bArr[i4 + 1]);
    }

    public static boolean isValidPlayMode(int i) {
        return isWithinRange(i, 5, 7) || isWithinRange(i, 9, 11) || isWithinRange(i, 21, 23) || isWithinRange(i, 25, 27) || isWithinRange(i, 36, 37) || i == 32;
    }

    public static boolean isValidUiSoundPresenationControl(int i) {
        int i2 = i & 255;
        return i2 == 32 || i2 == 48 || i2 == 128 || i2 == 144 || i2 == 160 || isWithinRange(i2, 177, FrameworkStatsLog.f373xa546c0ca) || isWithinRange(i2, FrameworkStatsLog.f390xde8506f2, FrameworkStatsLog.f411x277c884);
    }

    public static boolean isValidTunerDeviceInfo(byte[] bArr) {
        int i = bArr[0] & Byte.MAX_VALUE;
        if (i == 0) {
            if (bArr.length >= 5) {
                return isValidDigitalServiceIdentification(bArr, 1);
            }
            return false;
        } else if (i == 1) {
            return true;
        } else {
            return i == 2 && bArr.length >= 5 && isValidAnalogueBroadcastType(bArr[1]) && isValidAnalogueFrequency(HdmiUtils.twoBytesToInt(bArr, 2)) && isValidBroadcastSystem(bArr[4]);
        }
    }

    /* loaded from: classes.dex */
    public static class PhysicalAddressValidator implements ParameterValidator {
        public PhysicalAddressValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 2) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidPhysicalAddress(bArr, 0));
        }
    }

    /* loaded from: classes.dex */
    public static class SystemAudioModeRequestValidator extends PhysicalAddressValidator {
        public SystemAudioModeRequestValidator() {
            super();
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.PhysicalAddressValidator, com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length == 0) {
                return 0;
            }
            return super.isValid(bArr);
        }
    }

    /* loaded from: classes.dex */
    public static class ReportPhysicalAddressValidator implements ParameterValidator {
        public ReportPhysicalAddressValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 3) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidPhysicalAddress(bArr, 0) && HdmiCecMessageValidator.isValidType(bArr[2])) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class RoutingChangeValidator implements ParameterValidator {
        public RoutingChangeValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 4) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidPhysicalAddress(bArr, 0) && HdmiCecMessageValidator.isValidPhysicalAddress(bArr, 2)) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class RecordStatusInfoValidator implements ParameterValidator {
        public RecordStatusInfoValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            boolean z = true;
            if (bArr.length < 1) {
                return 4;
            }
            if (!HdmiCecMessageValidator.isWithinRange(bArr[0], 1, 7) && !HdmiCecMessageValidator.isWithinRange(bArr[0], 9, 14) && !HdmiCecMessageValidator.isWithinRange(bArr[0], 16, 23) && !HdmiCecMessageValidator.isWithinRange(bArr[0], 26, 27) && bArr[0] != 31) {
                z = false;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class AsciiValidator implements ParameterValidator {
        public final int mMaxLength;
        public final int mMinLength;

        public AsciiValidator(int i) {
            this.mMinLength = i;
            this.mMaxLength = i;
        }

        public AsciiValidator(int i, int i2) {
            this.mMinLength = i;
            this.mMaxLength = i2;
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < this.mMinLength) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidAsciiString(bArr, 0, this.mMaxLength));
        }
    }

    /* loaded from: classes.dex */
    public static class OsdStringValidator implements ParameterValidator {
        public OsdStringValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 2) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidDisplayControl(bArr[0]) && HdmiCecMessageValidator.isValidAsciiString(bArr, 1, 14)) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class OneByteRangeValidator implements ParameterValidator {
        public final int mMaxValue;
        public final int mMinValue;

        public OneByteRangeValidator(int i, int i2) {
            this.mMinValue = i;
            this.mMaxValue = i2;
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 1) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isWithinRange(bArr[0], this.mMinValue, this.mMaxValue));
        }
    }

    /* loaded from: classes.dex */
    public static class AnalogueTimerValidator implements ParameterValidator {
        public AnalogueTimerValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 11) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidDayOfMonth(bArr[0]) && HdmiCecMessageValidator.isValidMonthOfYear(bArr[1]) && HdmiCecMessageValidator.isValidHour(bArr[2]) && HdmiCecMessageValidator.isValidMinute(bArr[3]) && HdmiCecMessageValidator.isValidDurationHours(bArr[4]) && HdmiCecMessageValidator.isValidMinute(bArr[5]) && HdmiCecMessageValidator.isValidRecordingSequence(bArr[6]) && HdmiCecMessageValidator.isValidAnalogueBroadcastType(bArr[7]) && HdmiCecMessageValidator.isValidAnalogueFrequency(HdmiUtils.twoBytesToInt(bArr, 8)) && HdmiCecMessageValidator.isValidBroadcastSystem(bArr[10])) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class DigitalTimerValidator implements ParameterValidator {
        public DigitalTimerValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 11) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidDayOfMonth(bArr[0]) && HdmiCecMessageValidator.isValidMonthOfYear(bArr[1]) && HdmiCecMessageValidator.isValidHour(bArr[2]) && HdmiCecMessageValidator.isValidMinute(bArr[3]) && HdmiCecMessageValidator.isValidDurationHours(bArr[4]) && HdmiCecMessageValidator.isValidMinute(bArr[5]) && HdmiCecMessageValidator.isValidRecordingSequence(bArr[6]) && HdmiCecMessageValidator.isValidDigitalServiceIdentification(bArr, 7)) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class ExternalTimerValidator implements ParameterValidator {
        public ExternalTimerValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 9) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidDayOfMonth(bArr[0]) && HdmiCecMessageValidator.isValidMonthOfYear(bArr[1]) && HdmiCecMessageValidator.isValidHour(bArr[2]) && HdmiCecMessageValidator.isValidMinute(bArr[3]) && HdmiCecMessageValidator.isValidDurationHours(bArr[4]) && HdmiCecMessageValidator.isValidMinute(bArr[5]) && HdmiCecMessageValidator.isValidRecordingSequence(bArr[6]) && HdmiCecMessageValidator.isValidExternalSource(bArr, 7)) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class TimerClearedStatusValidator implements ParameterValidator {
        public TimerClearedStatusValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            boolean z = true;
            if (bArr.length < 1) {
                return 4;
            }
            if (!HdmiCecMessageValidator.isWithinRange(bArr[0], 0, 2) && (bArr[0] & 255) != 128) {
                z = false;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class TimerStatusValidator implements ParameterValidator {
        public TimerStatusValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 1) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidTimerStatusData(bArr, 0));
        }
    }

    /* loaded from: classes.dex */
    public static class PlayModeValidator implements ParameterValidator {
        public PlayModeValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 1) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidPlayMode(bArr[0]));
        }
    }

    /* loaded from: classes.dex */
    public static class SelectAnalogueServiceValidator implements ParameterValidator {
        public SelectAnalogueServiceValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 4) {
                return 4;
            }
            boolean z = false;
            if (HdmiCecMessageValidator.isValidAnalogueBroadcastType(bArr[0]) && HdmiCecMessageValidator.isValidAnalogueFrequency(HdmiUtils.twoBytesToInt(bArr, 1)) && HdmiCecMessageValidator.isValidBroadcastSystem(bArr[3])) {
                z = true;
            }
            return HdmiCecMessageValidator.toErrorCode(z);
        }
    }

    /* loaded from: classes.dex */
    public static class SelectDigitalServiceValidator implements ParameterValidator {
        public SelectDigitalServiceValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 4) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidDigitalServiceIdentification(bArr, 0));
        }
    }

    /* loaded from: classes.dex */
    public static class TunerDeviceStatusValidator implements ParameterValidator {
        public TunerDeviceStatusValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 1) {
                return 4;
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidTunerDeviceInfo(bArr));
        }
    }

    /* loaded from: classes.dex */
    public static class UserControlPressedValidator implements ParameterValidator {
        public UserControlPressedValidator() {
        }

        @Override // com.android.server.hdmi.HdmiCecMessageValidator.ParameterValidator
        public int isValid(byte[] bArr) {
            if (bArr.length < 1) {
                return 4;
            }
            if (bArr.length == 1) {
                return 0;
            }
            byte b = bArr[0];
            if (b != 86) {
                if (b != 87) {
                    if (b != 96) {
                        if (b != 103) {
                            return 0;
                        }
                        if (bArr.length >= 4) {
                            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidChannelIdentifier(bArr, 1));
                        }
                        return 4;
                    }
                    return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidPlayMode(bArr[1]));
                }
                return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidUiSoundPresenationControl(bArr[1]));
            }
            return HdmiCecMessageValidator.toErrorCode(HdmiCecMessageValidator.isValidUiBroadcastType(bArr[1]));
        }
    }
}
