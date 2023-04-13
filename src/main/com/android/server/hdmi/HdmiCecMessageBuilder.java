package com.android.server.hdmi;

import com.android.internal.util.FrameworkStatsLog;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
public class HdmiCecMessageBuilder {
    public static byte[] physicalAddressToParam(int i) {
        return new byte[]{(byte) ((i >> 8) & 255), (byte) (i & 255)};
    }

    public static HdmiCecMessage buildFeatureAbortCommand(int i, int i2, int i3, int i4) {
        return HdmiCecMessage.build(i, i2, 0, new byte[]{(byte) (i3 & 255), (byte) (i4 & 255)});
    }

    public static HdmiCecMessage buildGivePhysicalAddress(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 131);
    }

    public static HdmiCecMessage buildGiveOsdNameCommand(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 70);
    }

    public static HdmiCecMessage buildGiveDeviceVendorIdCommand(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__GET_CROSS_PROFILE_PACKAGES);
    }

    public static HdmiCecMessage buildSetMenuLanguageCommand(int i, String str) {
        if (str.length() != 3) {
            return null;
        }
        String lowerCase = str.toLowerCase();
        return HdmiCecMessage.build(i, 15, 50, new byte[]{(byte) (lowerCase.charAt(0) & 255), (byte) (lowerCase.charAt(1) & 255), (byte) (lowerCase.charAt(2) & 255)});
    }

    public static HdmiCecMessage buildSetOsdNameCommand(int i, int i2, String str) {
        try {
            return HdmiCecMessage.build(i, i2, 71, str.substring(0, Math.min(str.length(), 14)).getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException unused) {
            return null;
        }
    }

    public static HdmiCecMessage buildReportPhysicalAddressCommand(int i, int i2, int i3) {
        return HdmiCecMessage.build(i, 15, 132, new byte[]{(byte) ((i2 >> 8) & 255), (byte) (i2 & 255), (byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildDeviceVendorIdCommand(int i, int i2) {
        return HdmiCecMessage.build(i, 15, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED, new byte[]{(byte) ((i2 >> 16) & 255), (byte) ((i2 >> 8) & 255), (byte) (i2 & 255)});
    }

    public static HdmiCecMessage buildCecVersion(int i, int i2, int i3) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f419x663f9746, new byte[]{(byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildRequestArcInitiation(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f411x277c884);
    }

    public static HdmiCecMessage buildInitiateArc(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f392xcd34d435);
    }

    public static HdmiCecMessage buildTerminateArc(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_PARAM);
    }

    public static HdmiCecMessage buildRequestArcTermination(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f410xb766d392);
    }

    public static HdmiCecMessage buildReportArcInitiated(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f390xde8506f2);
    }

    public static HdmiCecMessage buildReportArcTerminated(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_ERROR);
    }

    public static HdmiCecMessage buildRequestShortAudioDescriptor(int i, int i2, int[] iArr) {
        int min = Math.min(iArr.length, 4);
        byte[] bArr = new byte[min];
        for (int i3 = 0; i3 < min; i3++) {
            bArr[i3] = (byte) (iArr[i3] & 255);
        }
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f376xd07885aa, bArr);
    }

    public static HdmiCecMessage buildTextViewOn(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 13);
    }

    public static HdmiCecMessage buildRequestActiveSource(int i) {
        return HdmiCecMessage.build(i, 15, 133);
    }

    public static HdmiCecMessage buildActiveSource(int i, int i2) {
        return HdmiCecMessage.build(i, 15, 130, physicalAddressToParam(i2));
    }

    public static HdmiCecMessage buildInactiveSource(int i, int i2) {
        return HdmiCecMessage.build(i, 0, FrameworkStatsLog.f421x729e24be, physicalAddressToParam(i2));
    }

    public static HdmiCecMessage buildSetStreamPath(int i, int i2) {
        return HdmiCecMessage.build(i, 15, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE, physicalAddressToParam(i2));
    }

    public static HdmiCecMessage buildRoutingChange(int i, int i2, int i3) {
        return HdmiCecMessage.build(i, 15, 128, new byte[]{(byte) ((i2 >> 8) & 255), (byte) (i2 & 255), (byte) ((i3 >> 8) & 255), (byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildRoutingInformation(int i, int i2) {
        return HdmiCecMessage.build(i, 15, 129, physicalAddressToParam(i2));
    }

    public static HdmiCecMessage buildGiveDevicePowerStatus(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 143);
    }

    public static HdmiCecMessage buildReportPowerStatus(int i, int i2, int i3) {
        return HdmiCecMessage.build(i, i2, 144, new byte[]{(byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildReportMenuStatus(int i, int i2, int i3) {
        return HdmiCecMessage.build(i, i2, 142, new byte[]{(byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildSystemAudioModeRequest(int i, int i2, int i3, boolean z) {
        if (z) {
            return HdmiCecMessage.build(i, i2, 112, physicalAddressToParam(i3));
        }
        return HdmiCecMessage.build(i, i2, 112);
    }

    public static HdmiCecMessage buildSetSystemAudioMode(int i, int i2, boolean z) {
        return buildCommandWithBooleanParam(i, i2, 114, z);
    }

    public static HdmiCecMessage buildReportSystemAudioMode(int i, int i2, boolean z) {
        return buildCommandWithBooleanParam(i, i2, 126, z);
    }

    public static HdmiCecMessage buildReportShortAudioDescriptor(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f380x2165d62a, bArr);
    }

    public static HdmiCecMessage buildGiveAudioStatus(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 113);
    }

    public static HdmiCecMessage buildReportAudioStatus(int i, int i2, int i3, boolean z) {
        return HdmiCecMessage.build(i, i2, 122, new byte[]{(byte) ((((byte) i3) & Byte.MAX_VALUE) | ((byte) (z ? 128 : 0)))});
    }

    public static HdmiCecMessage buildUserControlPressed(int i, int i2, int i3) {
        return buildUserControlPressed(i, i2, new byte[]{(byte) (i3 & 255)});
    }

    public static HdmiCecMessage buildUserControlPressed(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, 68, bArr);
    }

    public static HdmiCecMessage buildUserControlReleased(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 69);
    }

    public static HdmiCecMessage buildGiveSystemAudioModeStatus(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 125);
    }

    public static HdmiCecMessage buildStandby(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 54);
    }

    public static HdmiCecMessage buildVendorCommand(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__COMP_TO_ORG_OWNED_PO_MIGRATED, bArr);
    }

    public static HdmiCecMessage buildVendorCommandWithId(int i, int i2, int i3, byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length + 3];
        bArr2[0] = (byte) ((i3 >> 16) & 255);
        bArr2[1] = (byte) ((i3 >> 8) & 255);
        bArr2[2] = (byte) (i3 & 255);
        System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f418x97ec91aa, bArr2);
    }

    public static HdmiCecMessage buildRecordOn(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, 9, bArr);
    }

    public static HdmiCecMessage buildRecordOff(int i, int i2) {
        return HdmiCecMessage.build(i, i2, 11);
    }

    public static HdmiCecMessage buildSetDigitalTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__BIND_CROSS_PROFILE_SERVICE, bArr);
    }

    public static HdmiCecMessage buildSetAnalogueTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, 52, bArr);
    }

    public static HdmiCecMessage buildSetExternalTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f379x24956b9a, bArr);
    }

    public static HdmiCecMessage buildClearDigitalTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PROVISIONING_DPC_SETUP_COMPLETED, bArr);
    }

    public static HdmiCecMessage buildClearAnalogueTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, 51, bArr);
    }

    public static HdmiCecMessage buildClearExternalTimer(int i, int i2, byte[] bArr) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f416x35de4f00, bArr);
    }

    public static HdmiCecMessage buildGiveFeatures(int i, int i2) {
        return HdmiCecMessage.build(i, i2, FrameworkStatsLog.f383xde3a78eb);
    }

    public static HdmiCecMessage buildCommandWithBooleanParam(int i, int i2, int i3, boolean z) {
        return HdmiCecMessage.build(i, i2, i3, new byte[]{z ? (byte) 1 : (byte) 0});
    }
}
