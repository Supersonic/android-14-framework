package com.android.internal.telephony.util;

import android.net.InetAddresses;
import android.net.ParseException;
import android.text.TextUtils;
import android.util.Patterns;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.util.DnsPacket;
import com.android.net.module.annotation.VisibleForTesting;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.FieldPosition;
/* loaded from: classes.dex */
public class DnsPacketUtils$DnsRecordParser {
    private static final DecimalFormat sByteFormat = new DecimalFormat();
    private static final FieldPosition sPos = new FieldPosition(0);

    @VisibleForTesting
    static String labelToString(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            int unsignedInt = Byte.toUnsignedInt(b);
            if (unsignedInt <= 32 || unsignedInt >= 127) {
                stringBuffer.append('\\');
                sByteFormat.format(unsignedInt, stringBuffer, sPos);
            } else if (unsignedInt == 34 || unsignedInt == 46 || unsignedInt == 59 || unsignedInt == 92 || unsignedInt == 40 || unsignedInt == 41 || unsignedInt == 64 || unsignedInt == 36) {
                stringBuffer.append('\\');
                stringBuffer.append((char) unsignedInt);
            } else {
                stringBuffer.append((char) unsignedInt);
            }
        }
        return stringBuffer.toString();
    }

    public static byte[] domainNameToLabels(String str) throws IOException, ParseException {
        String[] split;
        if (str.length() > 255) {
            throw new ParseException("Domain name exceeds max length: " + str.length());
        } else if (!isHostName(str)) {
            throw new ParseException("Failed to parse domain name: " + str);
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (String str2 : str.split("\\.")) {
                if (str2.length() > 63) {
                    throw new ParseException("label is too long: " + str2);
                }
                byteArrayOutputStream.write(str2.length());
                byteArrayOutputStream.write(str2.getBytes(StandardCharsets.UTF_8));
            }
            byteArrayOutputStream.write(0);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static boolean isHostName(String str) {
        return (str == null || !Patterns.DOMAIN_NAME.matcher(str).matches() || InetAddresses.isNumericAddress(str)) ? false : true;
    }

    public static String parseName(ByteBuffer byteBuffer, int i, boolean z) throws BufferUnderflowException, DnsPacket.ParseException {
        if (i > 128) {
            throw new DnsPacket.ParseException("Failed to parse name, too many labels");
        }
        int unsignedInt = Byte.toUnsignedInt(byteBuffer.get());
        int i2 = unsignedInt & DnsPacket.DnsRecord.NAME_COMPRESSION;
        if (unsignedInt == 0) {
            return PhoneConfigurationManager.SSSS;
        }
        if ((i2 != 0 && i2 != 192) || (!z && i2 == 192)) {
            throw new DnsPacket.ParseException("Parse name fail, bad label type: " + i2);
        } else if (i2 == 192) {
            int unsignedInt2 = ((unsignedInt & (-193)) << 8) + Byte.toUnsignedInt(byteBuffer.get());
            int position = byteBuffer.position();
            if (unsignedInt2 >= position - 2) {
                throw new DnsPacket.ParseException("Parse compression name fail, invalid compression");
            }
            byteBuffer.position(unsignedInt2);
            String parseName = parseName(byteBuffer, i + 1, z);
            byteBuffer.position(position);
            return parseName;
        } else {
            byte[] bArr = new byte[unsignedInt];
            byteBuffer.get(bArr);
            String labelToString = labelToString(bArr);
            if (labelToString.length() > 63) {
                throw new DnsPacket.ParseException("Parse name fail, invalid label length");
            }
            String parseName2 = parseName(byteBuffer, i + 1, z);
            if (TextUtils.isEmpty(parseName2)) {
                return labelToString;
            }
            return labelToString + "." + parseName2;
        }
    }

    private DnsPacketUtils$DnsRecordParser() {
    }
}
