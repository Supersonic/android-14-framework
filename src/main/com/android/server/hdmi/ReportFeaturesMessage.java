package com.android.server.hdmi;

import android.hardware.hdmi.DeviceFeatures;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.location.gnss.hal.GnssNative;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
/* loaded from: classes.dex */
public class ReportFeaturesMessage extends HdmiCecMessage {
    public final int mCecVersion;
    public final DeviceFeatures mDeviceFeatures;

    public ReportFeaturesMessage(int i, int i2, byte[] bArr, int i3, DeviceFeatures deviceFeatures) {
        super(i, i2, FrameworkStatsLog.f382x8c80549a, bArr, 0);
        this.mCecVersion = i3;
        this.mDeviceFeatures = deviceFeatures;
    }

    public static HdmiCecMessage build(int i, int i2, List<Integer> list, int i3, List<Integer> list2, DeviceFeatures deviceFeatures) {
        byte b = (byte) (i2 & 255);
        byte b2 = 0;
        for (Integer num : list) {
            b2 = (byte) (b2 | ((byte) (1 << hdmiDeviceInfoDeviceTypeToShiftValue(num.intValue()))));
        }
        byte b3 = (byte) (((byte) (i3 << 6)) | 0);
        if (i3 == 1) {
            for (Integer num2 : list2) {
                b3 = (byte) (b3 | ((byte) (1 << num2.intValue())));
            }
        } else {
            b3 = (byte) (b3 | ((byte) (list2.get(0).intValue() & GnssNative.GNSS_AIDING_TYPE_ALL)));
        }
        byte[] bArr = {b, b2, b3};
        byte[] operand = deviceFeatures.toOperand();
        byte[] copyOf = Arrays.copyOf(bArr, operand.length + 3);
        System.arraycopy(operand, 0, copyOf, 3, operand.length);
        int validateAddress = validateAddress(i, 15);
        if (validateAddress != 0) {
            return new HdmiCecMessage(i, 15, FrameworkStatsLog.f382x8c80549a, copyOf, validateAddress);
        }
        return new ReportFeaturesMessage(i, 15, copyOf, i2, deviceFeatures);
    }

    public static int hdmiDeviceInfoDeviceTypeToShiftValue(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            if (i == 6) {
                                return 2;
                            }
                            throw new IllegalArgumentException("Unhandled device type: " + i);
                        }
                        return 3;
                    }
                    return 4;
                }
                return 5;
            }
            return 6;
        }
        return 7;
    }

    public static HdmiCecMessage build(final int i, final int i2, final byte[] bArr) {
        Function function = new Function() { // from class: com.android.server.hdmi.ReportFeaturesMessage$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                HdmiCecMessage lambda$build$0;
                lambda$build$0 = ReportFeaturesMessage.lambda$build$0(i, i2, bArr, (Integer) obj);
                return lambda$build$0;
            }
        };
        int validateAddress = validateAddress(i, i2);
        if (validateAddress != 0) {
            return (HdmiCecMessage) function.apply(Integer.valueOf(validateAddress));
        }
        if (bArr.length < 4) {
            return (HdmiCecMessage) function.apply(4);
        }
        int unsignedInt = Byte.toUnsignedInt(bArr[0]);
        int endOfSequence = HdmiUtils.getEndOfSequence(bArr, 2);
        if (endOfSequence == -1) {
            return (HdmiCecMessage) function.apply(4);
        }
        if (HdmiUtils.getEndOfSequence(bArr, endOfSequence + 1) == -1) {
            return (HdmiCecMessage) function.apply(4);
        }
        return new ReportFeaturesMessage(i, i2, bArr, unsignedInt, DeviceFeatures.fromOperand(Arrays.copyOfRange(bArr, HdmiUtils.getEndOfSequence(bArr, 2) + 1, bArr.length)));
    }

    public static /* synthetic */ HdmiCecMessage lambda$build$0(int i, int i2, byte[] bArr, Integer num) {
        return new HdmiCecMessage(i, i2, FrameworkStatsLog.f382x8c80549a, bArr, num.intValue());
    }

    public static int validateAddress(int i, int i2) {
        return HdmiCecMessageValidator.validateAddress(i, i2, 2);
    }

    public int getCecVersion() {
        return this.mCecVersion;
    }

    public DeviceFeatures getDeviceFeatures() {
        return this.mDeviceFeatures;
    }
}
