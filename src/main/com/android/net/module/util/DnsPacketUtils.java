package com.android.net.module.util;

import android.media.MediaMetrics;
import android.net.InetAddresses;
import android.net.ParseException;
import android.text.TextUtils;
import android.util.Patterns;
import com.android.net.module.util.DnsPacket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.FieldPosition;
/* loaded from: classes5.dex */
public final class DnsPacketUtils {

    /* loaded from: classes5.dex */
    public static class DnsRecordParser {
        private static final int MAXLABELCOUNT = 128;
        private static final int MAXLABELSIZE = 63;
        private static final int MAXNAMESIZE = 255;
        private static final DecimalFormat sByteFormat = new DecimalFormat();
        private static final FieldPosition sPos = new FieldPosition(0);

        static String labelToString(byte[] label) {
            StringBuffer sb = new StringBuffer();
            for (byte b : label) {
                int b2 = Byte.toUnsignedInt(b);
                if (b2 <= 32 || b2 >= 127) {
                    sb.append('\\');
                    sByteFormat.format(b2, sb, sPos);
                } else if (b2 == 34 || b2 == 46 || b2 == 59 || b2 == 92 || b2 == 40 || b2 == 41 || b2 == 64 || b2 == 36) {
                    sb.append('\\');
                    sb.append((char) b2);
                } else {
                    sb.append((char) b2);
                }
            }
            return sb.toString();
        }

        public static byte[] domainNameToLabels(String name) throws IOException, ParseException {
            if (name.length() > 255) {
                throw new ParseException("Domain name exceeds max length: " + name.length());
            }
            if (!isHostName(name)) {
                throw new ParseException("Failed to parse domain name: " + name);
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            String[] labels = name.split("\\.");
            for (String label : labels) {
                if (label.length() > 63) {
                    throw new ParseException("label is too long: " + label);
                }
                buf.write(label.length());
                buf.write(label.getBytes(StandardCharsets.UTF_8));
            }
            buf.write(0);
            return buf.toByteArray();
        }

        public static boolean isHostName(String hostName) {
            return (hostName == null || !Patterns.DOMAIN_NAME.matcher(hostName).matches() || InetAddresses.isNumericAddress(hostName)) ? false : true;
        }

        public static String parseName(ByteBuffer buf, int depth, boolean isNameCompressionSupported) throws BufferUnderflowException, DnsPacket.ParseException {
            if (depth > 128) {
                throw new DnsPacket.ParseException("Failed to parse name, too many labels");
            }
            int len = Byte.toUnsignedInt(buf.get());
            int mask = len & 192;
            if (len == 0) {
                return "";
            }
            if ((mask != 0 && mask != 192) || (!isNameCompressionSupported && mask == 192)) {
                throw new DnsPacket.ParseException("Parse name fail, bad label type: " + mask);
            }
            if (mask == 192) {
                int offset = ((len & (-193)) << 8) + Byte.toUnsignedInt(buf.get());
                int oldPos = buf.position();
                if (offset >= oldPos - 2) {
                    throw new DnsPacket.ParseException("Parse compression name fail, invalid compression");
                }
                buf.position(offset);
                String pointed = parseName(buf, depth + 1, isNameCompressionSupported);
                buf.position(oldPos);
                return pointed;
            }
            byte[] label = new byte[len];
            buf.get(label);
            String head = labelToString(label);
            if (head.length() > 63) {
                throw new DnsPacket.ParseException("Parse name fail, invalid label length");
            }
            String tail = parseName(buf, depth + 1, isNameCompressionSupported);
            return TextUtils.isEmpty(tail) ? head : head + MediaMetrics.SEPARATOR + tail;
        }

        private DnsRecordParser() {
        }
    }

    private DnsPacketUtils() {
    }
}
