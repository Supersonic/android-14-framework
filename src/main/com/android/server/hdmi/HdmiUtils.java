package com.android.server.hdmi;

import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.util.HexDump;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.google.android.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class HdmiUtils {
    public static final Map<Integer, List<Integer>> ADDRESS_TO_TYPE = Map.ofEntries(Map.entry(0, Lists.newArrayList(new Integer[]{0})), Map.entry(1, Lists.newArrayList(new Integer[]{1})), Map.entry(2, Lists.newArrayList(new Integer[]{1})), Map.entry(3, Lists.newArrayList(new Integer[]{3})), Map.entry(4, Lists.newArrayList(new Integer[]{4})), Map.entry(5, Lists.newArrayList(new Integer[]{5})), Map.entry(6, Lists.newArrayList(new Integer[]{3})), Map.entry(7, Lists.newArrayList(new Integer[]{3})), Map.entry(8, Lists.newArrayList(new Integer[]{4})), Map.entry(9, Lists.newArrayList(new Integer[]{1})), Map.entry(10, Lists.newArrayList(new Integer[]{3})), Map.entry(11, Lists.newArrayList(new Integer[]{4})), Map.entry(12, Lists.newArrayList(new Integer[]{4, 1, 3, 7})), Map.entry(13, Lists.newArrayList(new Integer[]{4, 1, 3, 7})), Map.entry(14, Lists.newArrayList(new Integer[]{0})), Map.entry(15, Collections.emptyList()));
    public static final String[] DEFAULT_NAMES = {"TV", "Recorder_1", "Recorder_2", "Tuner_1", "Playback_1", "AudioSystem", "Tuner_2", "Tuner_3", "Playback_2", "Recorder_3", "Tuner_4", "Playback_3", "Backup_1", "Backup_2", "Secondary_TV"};

    public static int getLocalPortFromPhysicalAddress(int i, int i2) {
        if (i2 == i) {
            return 0;
        }
        int i3 = 61440;
        int i4 = i2;
        int i5 = 61440;
        while (i4 != 0) {
            i4 = i2 & i5;
            i3 |= i5;
            i5 >>= 4;
        }
        int i6 = i & i3;
        if (((i3 << 4) & i6) != i2) {
            return -1;
        }
        int i7 = i6 & (i5 << 4);
        while (true) {
            int i8 = i7 >> 4;
            if (i8 == 0) {
                return i7;
            }
            i7 = i8;
        }
    }

    public static boolean isValidAddress(int i) {
        return i >= 0 && i <= 14;
    }

    public static int pathRelationship(int i, int i2) {
        int i3 = 0;
        if (i == 65535 || i2 == 65535) {
            return 0;
        }
        while (i3 <= 3) {
            int i4 = 12 - (i3 * 4);
            int i5 = (i >> i4) & 15;
            int i6 = (i2 >> i4) & 15;
            if (i5 != i6) {
                int i7 = i4 - 4;
                int i8 = (i >> i7) & 15;
                int i9 = (i2 >> i7) & 15;
                if (i5 == 0) {
                    return 2;
                }
                if (i6 == 0) {
                    return 3;
                }
                return (i3 == 3 || (i8 == 0 && i9 == 0)) ? 4 : 1;
            }
            i3++;
        }
        return 5;
    }

    public static boolean isEligibleAddressForDevice(int i, int i2) {
        return isValidAddress(i2) && ADDRESS_TO_TYPE.get(Integer.valueOf(i2)).contains(Integer.valueOf(i));
    }

    public static boolean isEligibleAddressForCecVersion(int i, int i2) {
        if (isValidAddress(i2)) {
            return !(i2 == 12 || i2 == 13) || i >= 6;
        }
        return false;
    }

    public static List<Integer> getTypeFromAddress(int i) {
        if (isValidAddress(i)) {
            return ADDRESS_TO_TYPE.get(Integer.valueOf(i));
        }
        return Lists.newArrayList(new Integer[]{-1});
    }

    public static String getDefaultDeviceName(int i) {
        return isValidAddress(i) ? DEFAULT_NAMES[i] : "";
    }

    public static void verifyAddressType(int i, int i2) {
        List<Integer> typeFromAddress = getTypeFromAddress(i);
        if (typeFromAddress.contains(Integer.valueOf(i2))) {
            return;
        }
        throw new IllegalArgumentException("Device type missmatch:[Expected:" + i2 + ", Actual:" + typeFromAddress);
    }

    public static boolean checkCommandSource(HdmiCecMessage hdmiCecMessage, int i, String str) {
        int source = hdmiCecMessage.getSource();
        if (source != i) {
            Slog.w(str, "Invalid source [Expected:" + i + ", Actual:" + source + "]");
            return false;
        }
        return true;
    }

    public static boolean parseCommandParamSystemAudioStatus(HdmiCecMessage hdmiCecMessage) {
        return hdmiCecMessage.getParams()[0] == 1;
    }

    public static boolean isAudioStatusMute(HdmiCecMessage hdmiCecMessage) {
        return (hdmiCecMessage.getParams()[0] & 128) == 128;
    }

    public static int getAudioStatusVolume(HdmiCecMessage hdmiCecMessage) {
        int i = hdmiCecMessage.getParams()[0] & Byte.MAX_VALUE;
        if (i < 0 || 100 < i) {
            return -1;
        }
        return i;
    }

    public static int twoBytesToInt(byte[] bArr) {
        return (bArr[1] & 255) | ((bArr[0] & 255) << 8);
    }

    public static int twoBytesToInt(byte[] bArr, int i) {
        return (bArr[i + 1] & 255) | ((bArr[i] & 255) << 8);
    }

    public static int threeBytesToInt(byte[] bArr) {
        return (bArr[2] & 255) | ((bArr[0] & 255) << 16) | ((bArr[1] & 255) << 8);
    }

    public static <T> List<T> sparseArrayToList(SparseArray<T> sparseArray) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

    public static <T> List<T> mergeToUnmodifiableList(List<T> list, List<T> list2) {
        if (list.isEmpty() && list2.isEmpty()) {
            return Collections.emptyList();
        }
        if (list.isEmpty()) {
            return Collections.unmodifiableList(list2);
        }
        if (list2.isEmpty()) {
            return Collections.unmodifiableList(list);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        arrayList.addAll(list2);
        return Collections.unmodifiableList(arrayList);
    }

    public static boolean isAffectingActiveRoutingPath(int i, int i2) {
        int i3 = 0;
        while (true) {
            if (i3 > 12) {
                break;
            } else if (((i2 >> i3) & 15) != 0) {
                i2 &= 65520 << i3;
                break;
            } else {
                i3 += 4;
            }
        }
        if (i2 == 0) {
            return true;
        }
        return isInActiveRoutingPath(i, i2);
    }

    public static boolean isInActiveRoutingPath(int i, int i2) {
        int pathRelationship = pathRelationship(i2, i);
        return pathRelationship == 2 || pathRelationship == 3 || pathRelationship == 5;
    }

    public static void printWithTrailingColon(IndentingPrintWriter indentingPrintWriter, String str) {
        if (!str.endsWith(XmlUtils.STRING_ARRAY_SEPARATOR)) {
            str = str.concat(XmlUtils.STRING_ARRAY_SEPARATOR);
        }
        indentingPrintWriter.println(str);
    }

    public static <K, V> void dumpMap(IndentingPrintWriter indentingPrintWriter, String str, Map<K, V> map) {
        printWithTrailingColon(indentingPrintWriter, str);
        indentingPrintWriter.increaseIndent();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            indentingPrintWriter.printPair(entry.getKey().toString(), entry.getValue());
            indentingPrintWriter.println();
        }
        indentingPrintWriter.decreaseIndent();
    }

    public static <T> void dumpIterable(IndentingPrintWriter indentingPrintWriter, String str, Iterable<T> iterable) {
        printWithTrailingColon(indentingPrintWriter, str);
        indentingPrintWriter.increaseIndent();
        for (T t : iterable) {
            indentingPrintWriter.println(t);
        }
        indentingPrintWriter.decreaseIndent();
    }

    public static int getAbortFeatureOpcode(HdmiCecMessage hdmiCecMessage) {
        return hdmiCecMessage.getParams()[0] & 255;
    }

    public static int getAbortReason(HdmiCecMessage hdmiCecMessage) {
        return hdmiCecMessage.getParams()[1];
    }

    public static int getEndOfSequence(byte[] bArr, int i) {
        if (i < 0) {
            return -1;
        }
        while (i < bArr.length && ((bArr[i] >> 7) & 1) == 1) {
            i++;
        }
        if (i >= bArr.length) {
            return -1;
        }
        return i;
    }

    /* loaded from: classes.dex */
    public static class ShortAudioDescriptorXmlParser {

        /* renamed from: NS */
        public static final String f1144NS = null;

        public static List<DeviceConfig> parse(InputStream inputStream) throws XmlPullParserException, IOException {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
            resolvePullParser.nextTag();
            return readDevices(resolvePullParser);
        }

        public static void skip(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
            if (typedXmlPullParser.getEventType() != 2) {
                throw new IllegalStateException();
            }
            int i = 1;
            while (i != 0) {
                int next = typedXmlPullParser.next();
                if (next == 2) {
                    i++;
                } else if (next == 3) {
                    i--;
                }
            }
        }

        public static List<DeviceConfig> readDevices(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
            ArrayList arrayList = new ArrayList();
            typedXmlPullParser.require(2, f1144NS, "config");
            while (typedXmlPullParser.next() != 3) {
                if (typedXmlPullParser.getEventType() == 2) {
                    if (typedXmlPullParser.getName().equals("device")) {
                        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "type");
                        DeviceConfig readDeviceConfig = attributeValue != null ? readDeviceConfig(typedXmlPullParser, attributeValue) : null;
                        if (readDeviceConfig != null) {
                            arrayList.add(readDeviceConfig);
                        }
                    } else {
                        skip(typedXmlPullParser);
                    }
                }
            }
            return arrayList;
        }

        public static DeviceConfig readDeviceConfig(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException {
            ArrayList arrayList = new ArrayList();
            typedXmlPullParser.require(2, f1144NS, "device");
            while (typedXmlPullParser.next() != 3) {
                if (typedXmlPullParser.getEventType() == 2) {
                    if (typedXmlPullParser.getName().equals("supportedFormat")) {
                        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "format");
                        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "descriptor");
                        int formatNameToNum = attributeValue == null ? 0 : formatNameToNum(attributeValue);
                        byte[] readSad = readSad(attributeValue2);
                        if (formatNameToNum != 0 && readSad != null) {
                            arrayList.add(new CodecSad(formatNameToNum, readSad));
                        }
                        typedXmlPullParser.nextTag();
                        typedXmlPullParser.require(3, f1144NS, "supportedFormat");
                    } else {
                        skip(typedXmlPullParser);
                    }
                }
            }
            if (arrayList.size() == 0) {
                return null;
            }
            return new DeviceConfig(str, arrayList);
        }

        public static byte[] readSad(String str) {
            if (str == null || str.length() == 0) {
                return null;
            }
            byte[] hexStringToByteArray = HexDump.hexStringToByteArray(str);
            if (hexStringToByteArray.length != 3) {
                Slog.w("HdmiUtils", "SAD byte array length is not 3. Length = " + hexStringToByteArray.length);
                return null;
            }
            return hexStringToByteArray;
        }

        public static int formatNameToNum(String str) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2131742975:
                    if (str.equals("AUDIO_FORMAT_WMAPRO")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1197237630:
                    if (str.equals("AUDIO_FORMAT_ATRAC")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1194465888:
                    if (str.equals("AUDIO_FORMAT_DTSHD")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1186286867:
                    if (str.equals("AUDIO_FORMAT_MPEG1")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1186286866:
                    if (str.equals("AUDIO_FORMAT_MPEG2")) {
                        c = 4;
                        break;
                    }
                    break;
                case -358943216:
                    if (str.equals("AUDIO_FORMAT_ONEBITAUDIO")) {
                        c = 5;
                        break;
                    }
                    break;
                case -282810364:
                    if (str.equals("AUDIO_FORMAT_AAC")) {
                        c = 6;
                        break;
                    }
                    break;
                case -282807375:
                    if (str.equals("AUDIO_FORMAT_DDP")) {
                        c = 7;
                        break;
                    }
                    break;
                case -282806906:
                    if (str.equals("AUDIO_FORMAT_DST")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -282806876:
                    if (str.equals("AUDIO_FORMAT_DTS")) {
                        c = '\t';
                        break;
                    }
                    break;
                case -282798811:
                    if (str.equals("AUDIO_FORMAT_MAX")) {
                        c = '\n';
                        break;
                    }
                    break;
                case -282798383:
                    if (str.equals("AUDIO_FORMAT_MP3")) {
                        c = 11;
                        break;
                    }
                    break;
                case -176844499:
                    if (str.equals("AUDIO_FORMAT_LPCM")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 129424511:
                    if (str.equals("AUDIO_FORMAT_DD")) {
                        c = '\r';
                        break;
                    }
                    break;
                case 2082539401:
                    if (str.equals("AUDIO_FORMAT_TRUEHD")) {
                        c = 14;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return 14;
                case 1:
                    return 8;
                case 2:
                    return 11;
                case 3:
                    return 3;
                case 4:
                    return 5;
                case 5:
                    return 9;
                case 6:
                    return 6;
                case 7:
                    return 10;
                case '\b':
                    return 13;
                case '\t':
                    return 7;
                case '\n':
                    return 15;
                case 11:
                    return 4;
                case '\f':
                    return 1;
                case '\r':
                    return 2;
                case 14:
                    return 12;
                default:
                    return 0;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DeviceConfig {
        public final String name;
        public final List<CodecSad> supportedCodecs;

        public DeviceConfig(String str, List<CodecSad> list) {
            this.name = str;
            this.supportedCodecs = list;
        }

        public boolean equals(Object obj) {
            if (obj instanceof DeviceConfig) {
                DeviceConfig deviceConfig = (DeviceConfig) obj;
                return deviceConfig.name.equals(this.name) && deviceConfig.supportedCodecs.equals(this.supportedCodecs);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.name, Integer.valueOf(this.supportedCodecs.hashCode()));
        }
    }

    /* loaded from: classes.dex */
    public static class CodecSad {
        public final int audioCodec;
        public final byte[] sad;

        public CodecSad(int i, byte[] bArr) {
            this.audioCodec = i;
            this.sad = bArr;
        }

        public boolean equals(Object obj) {
            if (obj instanceof CodecSad) {
                CodecSad codecSad = (CodecSad) obj;
                return codecSad.audioCodec == this.audioCodec && Arrays.equals(codecSad.sad, this.sad);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.audioCodec), Integer.valueOf(Arrays.hashCode(this.sad)));
        }
    }
}
