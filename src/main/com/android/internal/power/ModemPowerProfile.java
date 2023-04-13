package com.android.internal.power;

import android.content.res.XmlResourceParser;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioSystem;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseDoubleArray;
import com.android.internal.telephony.DctConstants;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public class ModemPowerProfile {
    private static final String ATTR_LEVEL = "level";
    private static final String ATTR_NR_FREQUENCY = "nrFrequency";
    private static final String ATTR_RAT = "rat";
    public static final int MODEM_DRAIN_TYPE_IDLE = 268435456;
    private static final int MODEM_DRAIN_TYPE_MASK = -268435456;
    private static final SparseArray<String> MODEM_DRAIN_TYPE_NAMES;
    public static final int MODEM_DRAIN_TYPE_RX = 536870912;
    public static final int MODEM_DRAIN_TYPE_SLEEP = 0;
    public static final int MODEM_DRAIN_TYPE_TX = 805306368;
    public static final int MODEM_NR_FREQUENCY_RANGE_DEFAULT = 0;
    public static final int MODEM_NR_FREQUENCY_RANGE_HIGH = 196608;
    public static final int MODEM_NR_FREQUENCY_RANGE_LOW = 65536;
    private static final int MODEM_NR_FREQUENCY_RANGE_MASK = 983040;
    public static final int MODEM_NR_FREQUENCY_RANGE_MID = 131072;
    public static final int MODEM_NR_FREQUENCY_RANGE_MMWAVE = 262144;
    private static final SparseArray<String> MODEM_NR_FREQUENCY_RANGE_NAMES;
    public static final int MODEM_RAT_TYPE_DEFAULT = 0;
    public static final int MODEM_RAT_TYPE_LTE = 1048576;
    private static final int MODEM_RAT_TYPE_MASK = 15728640;
    private static final SparseArray<String> MODEM_RAT_TYPE_NAMES;
    public static final int MODEM_RAT_TYPE_NR = 2097152;
    public static final int MODEM_TX_LEVEL_0 = 0;
    public static final int MODEM_TX_LEVEL_1 = 16777216;
    public static final int MODEM_TX_LEVEL_2 = 33554432;
    public static final int MODEM_TX_LEVEL_3 = 50331648;
    public static final int MODEM_TX_LEVEL_4 = 67108864;
    private static final int MODEM_TX_LEVEL_COUNT = 5;
    private static final int[] MODEM_TX_LEVEL_MAP;
    private static final int MODEM_TX_LEVEL_MASK = 251658240;
    private static final SparseArray<String> MODEM_TX_LEVEL_NAMES;
    private static final String TAG = "ModemPowerProfile";
    private static final String TAG_ACTIVE = "active";
    private static final String TAG_IDLE = "idle";
    private static final String TAG_RECEIVE = "receive";
    private static final String TAG_SLEEP = "sleep";
    private static final String TAG_TRANSMIT = "transmit";
    private final SparseDoubleArray mPowerConstants = new SparseDoubleArray();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ModemDrainType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ModemNrFrequencyRange {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ModemRatType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ModemTxLevel {
    }

    static {
        SparseArray<String> sparseArray = new SparseArray<>(4);
        MODEM_DRAIN_TYPE_NAMES = sparseArray;
        sparseArray.put(0, "SLEEP");
        sparseArray.put(268435456, "IDLE");
        sparseArray.put(536870912, "RX");
        sparseArray.put(805306368, "TX");
        SparseArray<String> sparseArray2 = new SparseArray<>(5);
        MODEM_TX_LEVEL_NAMES = sparseArray2;
        sparseArray2.put(0, AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
        sparseArray2.put(16777216, "1");
        sparseArray2.put(33554432, "2");
        sparseArray2.put(50331648, "3");
        sparseArray2.put(67108864, "4");
        MODEM_TX_LEVEL_MAP = new int[]{0, 16777216, 33554432, 50331648, 67108864};
        SparseArray<String> sparseArray3 = new SparseArray<>(3);
        MODEM_RAT_TYPE_NAMES = sparseArray3;
        sparseArray3.put(0, "DEFAULT");
        sparseArray3.put(1048576, DctConstants.RAT_NAME_LTE);
        sparseArray3.put(2097152, "NR");
        SparseArray<String> sparseArray4 = new SparseArray<>(5);
        MODEM_NR_FREQUENCY_RANGE_NAMES = sparseArray4;
        sparseArray4.put(0, "DEFAULT");
        sparseArray4.put(65536, "LOW");
        sparseArray4.put(131072, "MID");
        sparseArray4.put(196608, "HIGH");
        sparseArray4.put(262144, "MMWAVE");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void parseFromXml(XmlResourceParser parser) throws IOException, XmlPullParserException {
        char c;
        int depth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, depth)) {
            String name = parser.getName();
            switch (name.hashCode()) {
                case -1422950650:
                    if (name.equals(TAG_ACTIVE)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 3227604:
                    if (name.equals(TAG_IDLE)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 109522647:
                    if (name.equals(TAG_SLEEP)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    if (parser.next() == 4) {
                        String sleepDrain = parser.getText();
                        setPowerConstant(0, sleepDrain);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    if (parser.next() == 4) {
                        String idleDrain = parser.getText();
                        setPowerConstant(268435456, idleDrain);
                        break;
                    } else {
                        break;
                    }
                case 2:
                    parseActivePowerConstantsFromXml(parser);
                    break;
                default:
                    Slog.m96e(TAG, "Unexpected element parsed: " + name);
                    break;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void parseActivePowerConstantsFromXml(XmlResourceParser parser) throws IOException, XmlPullParserException {
        int nrfType;
        boolean z;
        try {
            int ratType = getTypeFromAttribute(parser, ATTR_RAT, MODEM_RAT_TYPE_NAMES);
            if (ratType == 2097152) {
                nrfType = getTypeFromAttribute(parser, ATTR_NR_FREQUENCY, MODEM_NR_FREQUENCY_RANGE_NAMES);
            } else {
                nrfType = 0;
            }
            int depth = parser.getDepth();
            while (XmlUtils.nextElementWithin(parser, depth)) {
                String name = parser.getName();
                switch (name.hashCode()) {
                    case 1082290915:
                        if (name.equals(TAG_RECEIVE)) {
                            z = false;
                            break;
                        }
                        z = true;
                        break;
                    case 1280889520:
                        if (name.equals(TAG_TRANSMIT)) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    default:
                        z = true;
                        break;
                }
                switch (z) {
                    case false:
                        if (parser.next() == 4) {
                            String rxDrain = parser.getText();
                            int rxKey = 536870912 | ratType | nrfType;
                            setPowerConstant(rxKey, rxDrain);
                            break;
                        } else {
                            break;
                        }
                    case true:
                        int level = XmlUtils.readIntAttribute(parser, "level", -1);
                        if (parser.next() == 4) {
                            String txDrain = parser.getText();
                            if (level < 0 || level >= 5) {
                                Slog.m96e(TAG, "Unexpected tx level: " + level + ". Must be between 0 and 4");
                                break;
                            } else {
                                int modemTxLevel = MODEM_TX_LEVEL_MAP[level];
                                int txKey = 805306368 | modemTxLevel | ratType | nrfType;
                                setPowerConstant(txKey, txDrain);
                                break;
                            }
                        } else {
                            break;
                        }
                        break;
                    default:
                        Slog.m96e(TAG, "Unexpected element parsed: " + name);
                        break;
                }
            }
        } catch (IllegalArgumentException iae) {
            Slog.m95e(TAG, "Failed parse to active modem power constants", iae);
        }
    }

    private static int getTypeFromAttribute(XmlResourceParser parser, String attr, SparseArray<String> names) {
        String value = XmlUtils.readStringAttribute(parser, attr);
        if (value == null) {
            return 0;
        }
        int index = -1;
        int size = names.size();
        for (int i = 0; i < size; i++) {
            if (value.equals(names.valueAt(i))) {
                index = i;
            }
        }
        if (index < 0) {
            String[] stringNames = new String[size];
            for (int i2 = 0; i2 < size; i2++) {
                stringNames[i2] = names.valueAt(i2);
            }
            throw new IllegalArgumentException("Unexpected " + attr + " value : " + value + ". Acceptable values are " + Arrays.toString(stringNames));
        }
        return names.keyAt(index);
    }

    public void setPowerConstant(int key, String value) {
        try {
            this.mPowerConstants.put(key, Double.valueOf(value).doubleValue());
        } catch (Exception e) {
            Slog.m95e(TAG, "Failed to set power constant 0x" + Integer.toHexString(key) + NavigationBarInflaterView.KEY_CODE_START + keyToString(key) + ") to " + value, e);
        }
    }

    public double getAverageBatteryDrainMa(int key) {
        int bestKey = key;
        double value = this.mPowerConstants.get(bestKey, Double.NaN);
        if (Double.isNaN(value)) {
            if ((983040 & bestKey) != 0) {
                bestKey = (bestKey & (-983041)) | 0;
                double value2 = this.mPowerConstants.get(bestKey, Double.NaN);
                if (!Double.isNaN(value2)) {
                    return value2;
                }
            }
            if ((MODEM_RAT_TYPE_MASK & bestKey) != 0) {
                double value3 = this.mPowerConstants.get((bestKey & (-15728641)) | 0, Double.NaN);
                if (!Double.isNaN(value3)) {
                    return value3;
                }
            }
            Slog.m90w(TAG, "getAverageBatteryDrainMaH called with unexpected key: 0x" + Integer.toHexString(key) + ", " + keyToString(key));
            return Double.NaN;
        }
        return value;
    }

    public static String keyToString(int key) {
        StringBuilder sb = new StringBuilder();
        int drainType = (-268435456) & key;
        appendFieldToString(sb, "drain", MODEM_DRAIN_TYPE_NAMES, drainType);
        sb.append(",");
        if (drainType == 805306368) {
            int txLevel = MODEM_TX_LEVEL_MASK & key;
            appendFieldToString(sb, "level", MODEM_TX_LEVEL_NAMES, txLevel);
            sb.append(",");
        }
        int ratType = MODEM_RAT_TYPE_MASK & key;
        appendFieldToString(sb, "RAT", MODEM_RAT_TYPE_NAMES, ratType);
        if (ratType == 2097152) {
            sb.append(",");
            int nrFreq = 983040 & key;
            appendFieldToString(sb, "nrFreq", MODEM_NR_FREQUENCY_RANGE_NAMES, nrFreq);
        }
        return sb.toString();
    }

    private static void appendFieldToString(StringBuilder sb, String fieldName, SparseArray<String> names, int key) {
        sb.append(fieldName);
        sb.append(":");
        String name = names.get(key, null);
        if (name == null) {
            sb.append("UNKNOWN(0x");
            sb.append(Integer.toHexString(key));
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            return;
        }
        sb.append(name);
    }

    public void clear() {
        this.mPowerConstants.clear();
    }

    public void dump(PrintWriter pw) {
        int size = this.mPowerConstants.size();
        for (int i = 0; i < size; i++) {
            pw.print(keyToString(this.mPowerConstants.keyAt(i)));
            pw.print("=");
            pw.println(this.mPowerConstants.valueAt(i));
        }
    }
}
