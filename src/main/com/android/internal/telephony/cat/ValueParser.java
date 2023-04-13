package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.telephony.SmsMessage;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.cat.Duration;
import com.android.internal.telephony.uicc.IccUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public abstract class ValueParser {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static CommandDetails retrieveCommandDetails(ComprehensionTlv comprehensionTlv) throws ResultException {
        CommandDetails commandDetails = new CommandDetails();
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        try {
            commandDetails.compRequired = comprehensionTlv.isComprehensionRequired();
            commandDetails.commandNumber = rawValue[valueIndex] & 255;
            commandDetails.typeOfCommand = rawValue[valueIndex + 1] & 255;
            commandDetails.commandQualifier = rawValue[valueIndex + 2] & 255;
            return commandDetails;
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    static DeviceIdentities retrieveDeviceIdentities(ComprehensionTlv comprehensionTlv) throws ResultException {
        DeviceIdentities deviceIdentities = new DeviceIdentities();
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        try {
            deviceIdentities.sourceId = rawValue[valueIndex] & 255;
            deviceIdentities.destinationId = rawValue[valueIndex + 1] & 255;
            return deviceIdentities;
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Duration retrieveDuration(ComprehensionTlv comprehensionTlv) throws ResultException {
        Duration.TimeUnit timeUnit = Duration.TimeUnit.MINUTE;
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        try {
            return new Duration(rawValue[valueIndex + 1] & 255, Duration.TimeUnit.values()[rawValue[valueIndex] & 255]);
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Item retrieveItem(ComprehensionTlv comprehensionTlv) throws ResultException {
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        int length = comprehensionTlv.getLength();
        if (length != 0) {
            try {
                return new Item(rawValue[valueIndex] & 255, IccUtils.adnStringFieldToString(rawValue, valueIndex + 1, length - 1));
            } catch (IndexOutOfBoundsException unused) {
                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int retrieveItemId(ComprehensionTlv comprehensionTlv) throws ResultException {
        try {
            return comprehensionTlv.getRawValue()[comprehensionTlv.getValueIndex()] & 255;
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IconId retrieveIconId(ComprehensionTlv comprehensionTlv) throws ResultException {
        IconId iconId = new IconId();
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        int i = valueIndex + 1;
        try {
            iconId.selfExplanatory = (rawValue[valueIndex] & 255) == 0;
            iconId.recordNumber = rawValue[i] & 255;
            return iconId;
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ItemsIconId retrieveItemsIconId(ComprehensionTlv comprehensionTlv) throws ResultException {
        CatLog.m4d("ValueParser", "retrieveItemsIconId:");
        ItemsIconId itemsIconId = new ItemsIconId();
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        boolean z = true;
        int length = comprehensionTlv.getLength() - 1;
        itemsIconId.recordNumbers = new int[length];
        int i = valueIndex + 1;
        try {
            int i2 = 0;
            if ((rawValue[valueIndex] & 255) != 0) {
                z = false;
            }
            itemsIconId.selfExplanatory = z;
            while (i2 < length) {
                int i3 = i2 + 1;
                int i4 = i + 1;
                itemsIconId.recordNumbers[i2] = rawValue[i];
                i2 = i3;
                i = i4;
            }
            return itemsIconId;
        } catch (IndexOutOfBoundsException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    static List<TextAttribute> retrieveTextAttribute(ComprehensionTlv comprehensionTlv) throws ResultException {
        ArrayList arrayList = new ArrayList();
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        int length = comprehensionTlv.getLength();
        if (length != 0) {
            int i = length / 4;
            int i2 = 0;
            while (i2 < i) {
                try {
                    int i3 = rawValue[valueIndex] & 255;
                    int i4 = rawValue[valueIndex + 1] & 255;
                    int i5 = rawValue[valueIndex + 2] & 255;
                    int i6 = rawValue[valueIndex + 3] & 255;
                    TextAlignment fromInt = TextAlignment.fromInt(i5 & 3);
                    FontSize fromInt2 = FontSize.fromInt((i5 >> 2) & 3);
                    if (fromInt2 == null) {
                        fromInt2 = FontSize.NORMAL;
                    }
                    arrayList.add(new TextAttribute(i3, i4, fromInt, fromInt2, (i5 & 16) != 0, (i5 & 32) != 0, (i5 & 64) != 0, (i5 & 128) != 0, TextColor.fromInt(i6)));
                    i2++;
                    valueIndex += 4;
                } catch (IndexOutOfBoundsException unused) {
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
                }
            }
            return arrayList;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static String retrieveAlphaId(ComprehensionTlv comprehensionTlv, boolean z) throws ResultException {
        if (comprehensionTlv == null) {
            if (z) {
                return null;
            }
            return "Default Message";
        }
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        int length = comprehensionTlv.getLength();
        if (length != 0) {
            try {
                return IccUtils.adnStringFieldToString(rawValue, valueIndex, length);
            } catch (IndexOutOfBoundsException unused) {
                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
            }
        }
        CatLog.m4d("ValueParser", "Alpha Id length=" + length);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static String retrieveTextString(ComprehensionTlv comprehensionTlv) throws ResultException {
        byte[] rawValue = comprehensionTlv.getRawValue();
        int valueIndex = comprehensionTlv.getValueIndex();
        int length = comprehensionTlv.getLength();
        if (length == 0) {
            return null;
        }
        int i = length - 1;
        try {
            byte b = (byte) (rawValue[valueIndex] & 12);
            if (b == 0) {
                return GsmAlphabet.gsm7BitPackedToString(rawValue, valueIndex + 1, (i * 8) / 7);
            }
            if (b == 4) {
                return GsmAlphabet.gsm8BitUnpackedToString(rawValue, valueIndex + 1, i);
            }
            if (b == 8) {
                return new String(rawValue, valueIndex + 1, i, "UTF-16");
            }
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        } catch (UnsupportedEncodingException unused) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        } catch (IndexOutOfBoundsException unused2) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
        }
    }

    public static SmsMessage retrieveTpduAsSmsMessage(ComprehensionTlv comprehensionTlv) throws ResultException {
        if (comprehensionTlv != null) {
            byte[] rawValue = comprehensionTlv.getRawValue();
            int valueIndex = comprehensionTlv.getValueIndex();
            int length = comprehensionTlv.getLength();
            if (length != 0) {
                try {
                    byte[] copyOfRange = Arrays.copyOfRange(rawValue, valueIndex, length + valueIndex);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(copyOfRange.length + 1);
                    byteArrayOutputStream.write(0);
                    byteArrayOutputStream.write(copyOfRange, 0, copyOfRange.length);
                    return SmsMessage.createFromPdu(byteArrayOutputStream.toByteArray(), "3gpp");
                } catch (IndexOutOfBoundsException unused) {
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD);
                }
            }
            return null;
        }
        return null;
    }
}
