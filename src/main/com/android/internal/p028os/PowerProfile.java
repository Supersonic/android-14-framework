package com.android.internal.p028os;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.media.audio.Enums;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.C4057R;
import com.android.internal.p028os.PowerProfileProto;
import com.android.internal.power.ModemPowerProfile;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.BiConsumer;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.internal.os.PowerProfile */
/* loaded from: classes4.dex */
public class PowerProfile {
    private static final String ATTR_NAME = "name";
    private static final String CPU_CLUSTER_POWER_COUNT = "cpu.cluster_power.cluster";
    private static final String CPU_CORE_POWER_PREFIX = "cpu.core_power.cluster";
    private static final String CPU_CORE_SPEED_PREFIX = "cpu.core_speeds.cluster";
    private static final String CPU_PER_CLUSTER_CORE_COUNT = "cpu.clusters.cores";
    private static final String CPU_POWER_BRACKETS_PREFIX = "cpu.power_brackets.cluster";
    private static final int DEFAULT_CPU_POWER_BRACKET_NUMBER = 3;
    @Deprecated
    public static final String POWER_AMBIENT_DISPLAY = "ambient.on";
    public static final String POWER_AUDIO = "audio";
    public static final String POWER_BATTERY_CAPACITY = "battery.capacity";
    @Deprecated
    public static final String POWER_BLUETOOTH_ACTIVE = "bluetooth.active";
    @Deprecated
    public static final String POWER_BLUETOOTH_AT_CMD = "bluetooth.at";
    public static final String POWER_BLUETOOTH_CONTROLLER_IDLE = "bluetooth.controller.idle";
    public static final String POWER_BLUETOOTH_CONTROLLER_OPERATING_VOLTAGE = "bluetooth.controller.voltage";
    public static final String POWER_BLUETOOTH_CONTROLLER_RX = "bluetooth.controller.rx";
    public static final String POWER_BLUETOOTH_CONTROLLER_TX = "bluetooth.controller.tx";
    @Deprecated
    public static final String POWER_BLUETOOTH_ON = "bluetooth.on";
    public static final String POWER_CAMERA = "camera.avg";
    public static final String POWER_CPU_ACTIVE = "cpu.active";
    public static final String POWER_CPU_IDLE = "cpu.idle";
    public static final String POWER_CPU_SUSPEND = "cpu.suspend";
    public static final String POWER_FLASHLIGHT = "camera.flashlight";
    public static final String POWER_GPS_ON = "gps.on";
    public static final String POWER_GPS_OPERATING_VOLTAGE = "gps.voltage";
    public static final String POWER_GPS_SIGNAL_QUALITY_BASED = "gps.signalqualitybased";
    public static final String POWER_GROUP_DISPLAY_AMBIENT = "ambient.on.display";
    public static final String POWER_GROUP_DISPLAY_SCREEN_FULL = "screen.full.display";
    public static final String POWER_GROUP_DISPLAY_SCREEN_ON = "screen.on.display";
    public static final String POWER_MEMORY = "memory.bandwidths";
    public static final String POWER_MODEM_CONTROLLER_IDLE = "modem.controller.idle";
    public static final String POWER_MODEM_CONTROLLER_OPERATING_VOLTAGE = "modem.controller.voltage";
    public static final String POWER_MODEM_CONTROLLER_RX = "modem.controller.rx";
    public static final String POWER_MODEM_CONTROLLER_SLEEP = "modem.controller.sleep";
    public static final String POWER_MODEM_CONTROLLER_TX = "modem.controller.tx";
    public static final String POWER_RADIO_ACTIVE = "radio.active";
    public static final String POWER_RADIO_ON = "radio.on";
    public static final String POWER_RADIO_SCANNING = "radio.scanning";
    @Deprecated
    public static final String POWER_SCREEN_FULL = "screen.full";
    @Deprecated
    public static final String POWER_SCREEN_ON = "screen.on";
    public static final String POWER_VIDEO = "video";
    public static final String POWER_WIFI_ACTIVE = "wifi.active";
    public static final String POWER_WIFI_BATCHED_SCAN = "wifi.batchedscan";
    public static final String POWER_WIFI_CONTROLLER_IDLE = "wifi.controller.idle";
    public static final String POWER_WIFI_CONTROLLER_OPERATING_VOLTAGE = "wifi.controller.voltage";
    public static final String POWER_WIFI_CONTROLLER_RX = "wifi.controller.rx";
    public static final String POWER_WIFI_CONTROLLER_TX = "wifi.controller.tx";
    public static final String POWER_WIFI_CONTROLLER_TX_LEVELS = "wifi.controller.tx_levels";
    public static final String POWER_WIFI_ON = "wifi.on";
    public static final String POWER_WIFI_SCAN = "wifi.scan";
    private static final long SUBSYSTEM_FIELDS_MASK = -1;
    private static final long SUBSYSTEM_MASK = 64424509440L;
    public static final long SUBSYSTEM_MODEM = 4294967296L;
    public static final long SUBSYSTEM_NONE = 0;
    public static final String TAG = "PowerProfile";
    private static final String TAG_ARRAY = "array";
    private static final String TAG_ARRAYITEM = "value";
    private static final String TAG_DEVICE = "device";
    private static final String TAG_ITEM = "item";
    private static final String TAG_MODEM = "modem";
    private CpuClusterKey[] mCpuClusters;
    private int mCpuPowerBracketCount;
    private int mNumDisplays;
    static final HashMap<String, Double> sPowerItemMap = new HashMap<>();
    static final HashMap<String, Double[]> sPowerArrayMap = new HashMap<>();
    static final ModemPowerProfile sModemPowerProfile = new ModemPowerProfile();
    private static final Object sLock = new Object();

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.android.internal.os.PowerProfile$PowerGroup */
    /* loaded from: classes4.dex */
    public @interface PowerGroup {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.android.internal.os.PowerProfile$Subsystem */
    /* loaded from: classes4.dex */
    public @interface Subsystem {
    }

    public PowerProfile(Context context) {
        this(context, false);
    }

    public PowerProfile(Context context, boolean forTest) {
        synchronized (sLock) {
            int xmlId = forTest ? C4057R.xml.power_profile_test : C4057R.xml.power_profile;
            initLocked(context, xmlId);
        }
    }

    public void forceInitForTesting(Context context, int xmlId) {
        synchronized (sLock) {
            sPowerItemMap.clear();
            sPowerArrayMap.clear();
            sModemPowerProfile.clear();
            initLocked(context, xmlId);
        }
    }

    private void initLocked(Context context, int xmlId) {
        if (sPowerItemMap.size() == 0 && sPowerArrayMap.size() == 0) {
            readPowerValuesFromXml(context, xmlId);
        }
        initCpuClusters();
        initDisplays();
        initModem();
    }

    private void readPowerValuesFromXml(Context context, int xmlId) {
        int value;
        Resources resources = context.getResources();
        XmlResourceParser parser = resources.getXml(xmlId);
        boolean parsingArray = false;
        ArrayList<Double> array = new ArrayList<>();
        String arrayName = null;
        try {
            try {
                XmlUtils.beginDocument(parser, "device");
                while (true) {
                    XmlUtils.nextElement(parser);
                    String element = parser.getName();
                    if (element == null) {
                        break;
                    }
                    if (parsingArray && !element.equals("value")) {
                        sPowerArrayMap.put(arrayName, (Double[]) array.toArray(new Double[array.size()]));
                        parsingArray = false;
                    }
                    if (element.equals(TAG_ARRAY)) {
                        parsingArray = true;
                        array.clear();
                        arrayName = parser.getAttributeValue(null, "name");
                    } else {
                        if (!element.equals("item") && !element.equals("value")) {
                            if (element.equals("modem")) {
                                sModemPowerProfile.parseFromXml(parser);
                            }
                        }
                        String name = !parsingArray ? parser.getAttributeValue(null, "name") : null;
                        if (parser.next() == 4) {
                            String power = parser.getText();
                            double value2 = 0.0d;
                            try {
                                value2 = Double.valueOf(power).doubleValue();
                            } catch (NumberFormatException e) {
                            }
                            if (element.equals("item")) {
                                sPowerItemMap.put(name, Double.valueOf(value2));
                            } else if (parsingArray) {
                                array.add(Double.valueOf(value2));
                            }
                        }
                    }
                }
                if (parsingArray) {
                    sPowerArrayMap.put(arrayName, (Double[]) array.toArray(new Double[array.size()]));
                }
                parser.close();
                int[] configResIds = {C4057R.integer.config_bluetooth_idle_cur_ma, C4057R.integer.config_bluetooth_rx_cur_ma, C4057R.integer.config_bluetooth_tx_cur_ma, C4057R.integer.config_bluetooth_operating_voltage_mv};
                String[] configResIdKeys = {POWER_BLUETOOTH_CONTROLLER_IDLE, POWER_BLUETOOTH_CONTROLLER_RX, POWER_BLUETOOTH_CONTROLLER_TX, POWER_BLUETOOTH_CONTROLLER_OPERATING_VOLTAGE};
                for (int i = 0; i < configResIds.length; i++) {
                    String key = configResIdKeys[i];
                    HashMap<String, Double> hashMap = sPowerItemMap;
                    if ((!hashMap.containsKey(key) || hashMap.get(key).doubleValue() <= 0.0d) && (value = resources.getInteger(configResIds[i])) > 0) {
                        hashMap.put(key, Double.valueOf(value));
                    }
                }
            } catch (Throwable th) {
                parser.close();
                throw th;
            }
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        } catch (XmlPullParserException e3) {
            throw new RuntimeException(e3);
        }
    }

    private void initCpuClusters() {
        HashMap<String, Double[]> hashMap = sPowerArrayMap;
        if (hashMap.containsKey(CPU_PER_CLUSTER_CORE_COUNT)) {
            Double[] data = hashMap.get(CPU_PER_CLUSTER_CORE_COUNT);
            this.mCpuClusters = new CpuClusterKey[data.length];
            for (int cluster = 0; cluster < data.length; cluster++) {
                int numCpusInCluster = (int) Math.round(data[cluster].doubleValue());
                this.mCpuClusters[cluster] = new CpuClusterKey(CPU_CORE_SPEED_PREFIX + cluster, CPU_CLUSTER_POWER_COUNT + cluster, CPU_CORE_POWER_PREFIX + cluster, numCpusInCluster);
            }
        } else {
            this.mCpuClusters = new CpuClusterKey[1];
            int numCpus = 1;
            HashMap<String, Double> hashMap2 = sPowerItemMap;
            if (hashMap2.containsKey(CPU_PER_CLUSTER_CORE_COUNT)) {
                numCpus = (int) Math.round(hashMap2.get(CPU_PER_CLUSTER_CORE_COUNT).doubleValue());
            }
            this.mCpuClusters[0] = new CpuClusterKey("cpu.core_speeds.cluster0", "cpu.cluster_power.cluster0", "cpu.core_power.cluster0", numCpus);
        }
        initCpuPowerBrackets(3);
    }

    public void initCpuPowerBrackets(int defaultCpuPowerBracketNumber) {
        boolean anyBracketsSpecified = false;
        boolean allBracketsSpecified = true;
        for (int cluster = 0; cluster < this.mCpuClusters.length; cluster++) {
            int steps = getNumSpeedStepsInCpuCluster(cluster);
            this.mCpuClusters[cluster].powerBrackets = new int[steps];
            if (sPowerArrayMap.get(CPU_POWER_BRACKETS_PREFIX + cluster) != null) {
                anyBracketsSpecified = true;
            } else {
                allBracketsSpecified = false;
            }
        }
        if (!anyBracketsSpecified || allBracketsSpecified) {
            this.mCpuPowerBracketCount = 0;
            if (allBracketsSpecified) {
                for (int cluster2 = 0; cluster2 < this.mCpuClusters.length; cluster2++) {
                    Double[] data = sPowerArrayMap.get(CPU_POWER_BRACKETS_PREFIX + cluster2);
                    if (data.length != this.mCpuClusters[cluster2].powerBrackets.length) {
                        throw new RuntimeException("Wrong number of items in cpu.power_brackets.cluster" + cluster2);
                    }
                    for (int i = 0; i < data.length; i++) {
                        int bracket = (int) Math.round(data[i].doubleValue());
                        this.mCpuClusters[cluster2].powerBrackets[i] = bracket;
                        if (bracket > this.mCpuPowerBracketCount) {
                            this.mCpuPowerBracketCount = bracket;
                        }
                    }
                }
                int cluster3 = this.mCpuPowerBracketCount;
                this.mCpuPowerBracketCount = cluster3 + 1;
                return;
            }
            double minPower = Double.MAX_VALUE;
            double maxPower = Double.MIN_VALUE;
            int stateCount = 0;
            for (int cluster4 = 0; cluster4 < this.mCpuClusters.length; cluster4++) {
                int steps2 = getNumSpeedStepsInCpuCluster(cluster4);
                for (int step = 0; step < steps2; step++) {
                    double power = getAveragePowerForCpuCore(cluster4, step);
                    if (power < minPower) {
                        minPower = power;
                    }
                    if (power > maxPower) {
                        maxPower = power;
                    }
                }
                stateCount += steps2;
            }
            if (stateCount <= defaultCpuPowerBracketNumber) {
                this.mCpuPowerBracketCount = stateCount;
                int bracket2 = 0;
                for (int cluster5 = 0; cluster5 < this.mCpuClusters.length; cluster5++) {
                    int steps3 = getNumSpeedStepsInCpuCluster(cluster5);
                    int step2 = 0;
                    while (step2 < steps3) {
                        this.mCpuClusters[cluster5].powerBrackets[step2] = bracket2;
                        step2++;
                        bracket2++;
                    }
                }
                return;
            }
            this.mCpuPowerBracketCount = defaultCpuPowerBracketNumber;
            double minLogPower = Math.log(minPower);
            double logBracket = (Math.log(maxPower) - minLogPower) / defaultCpuPowerBracketNumber;
            for (int cluster6 = 0; cluster6 < this.mCpuClusters.length; cluster6++) {
                int steps4 = getNumSpeedStepsInCpuCluster(cluster6);
                int step3 = 0;
                while (step3 < steps4) {
                    boolean anyBracketsSpecified2 = anyBracketsSpecified;
                    boolean allBracketsSpecified2 = allBracketsSpecified;
                    int bracket3 = (int) ((Math.log(getAveragePowerForCpuCore(cluster6, step3)) - minLogPower) / logBracket);
                    if (bracket3 >= defaultCpuPowerBracketNumber) {
                        bracket3 = defaultCpuPowerBracketNumber - 1;
                    }
                    this.mCpuClusters[cluster6].powerBrackets[step3] = bracket3;
                    step3++;
                    allBracketsSpecified = allBracketsSpecified2;
                    anyBracketsSpecified = anyBracketsSpecified2;
                }
            }
            return;
        }
        throw new RuntimeException("Power brackets should be specified for all clusters or no clusters");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.PowerProfile$CpuClusterKey */
    /* loaded from: classes4.dex */
    public static class CpuClusterKey {
        public final String clusterPowerKey;
        public final String corePowerKey;
        public final String freqKey;
        public final int numCpus;
        public int[] powerBrackets;

        private CpuClusterKey(String freqKey, String clusterPowerKey, String corePowerKey, int numCpus) {
            this.freqKey = freqKey;
            this.clusterPowerKey = clusterPowerKey;
            this.corePowerKey = corePowerKey;
            this.numCpus = numCpus;
        }
    }

    public int getNumCpuClusters() {
        return this.mCpuClusters.length;
    }

    public int getNumCoresInCpuCluster(int cluster) {
        if (cluster >= 0) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster >= cpuClusterKeyArr.length) {
                return 0;
            }
            return cpuClusterKeyArr[cluster].numCpus;
        }
        return 0;
    }

    public int getNumSpeedStepsInCpuCluster(int cluster) {
        if (cluster >= 0) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster >= cpuClusterKeyArr.length) {
                return 0;
            }
            HashMap<String, Double[]> hashMap = sPowerArrayMap;
            if (hashMap.containsKey(cpuClusterKeyArr[cluster].freqKey)) {
                return hashMap.get(this.mCpuClusters[cluster].freqKey).length;
            }
            return 1;
        }
        return 0;
    }

    public double getAveragePowerForCpuCluster(int cluster) {
        if (cluster >= 0) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster < cpuClusterKeyArr.length) {
                return getAveragePower(cpuClusterKeyArr[cluster].clusterPowerKey);
            }
            return 0.0d;
        }
        return 0.0d;
    }

    public double getAveragePowerForCpuCore(int cluster, int step) {
        if (cluster >= 0) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster < cpuClusterKeyArr.length) {
                return getAveragePower(cpuClusterKeyArr[cluster].corePowerKey, step);
            }
            return 0.0d;
        }
        return 0.0d;
    }

    public int getCpuPowerBracketCount() {
        return this.mCpuPowerBracketCount;
    }

    public String getCpuPowerBracketDescription(int powerBracket) {
        StringBuilder sb = new StringBuilder();
        int cluster = 0;
        while (true) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster < cpuClusterKeyArr.length) {
                int[] brackets = cpuClusterKeyArr[cluster].powerBrackets;
                for (int step = 0; step < brackets.length; step++) {
                    if (brackets[step] == powerBracket) {
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        if (this.mCpuClusters.length > 1) {
                            sb.append(cluster).append('/');
                        }
                        Double[] freqs = sPowerArrayMap.get(this.mCpuClusters[cluster].freqKey);
                        if (freqs != null && step < freqs.length) {
                            sb.append(freqs[step].intValue() / 1000);
                        }
                        sb.append('(');
                        sb.append(String.format(Locale.US, "%.1f", Double.valueOf(getAveragePowerForCpuCore(cluster, step))));
                        sb.append(')');
                    }
                }
                cluster++;
            } else {
                return sb.toString();
            }
        }
    }

    public int getPowerBracketForCpuCore(int cluster, int step) {
        if (cluster >= 0) {
            CpuClusterKey[] cpuClusterKeyArr = this.mCpuClusters;
            if (cluster < cpuClusterKeyArr.length && step >= 0 && step < cpuClusterKeyArr[cluster].powerBrackets.length) {
                return this.mCpuClusters[cluster].powerBrackets[step];
            }
            return 0;
        }
        return 0;
    }

    private void initDisplays() {
        this.mNumDisplays = 0;
        while (true) {
            if (Double.isNaN(getAveragePowerForOrdinal(POWER_GROUP_DISPLAY_AMBIENT, this.mNumDisplays, Double.NaN)) && Double.isNaN(getAveragePowerForOrdinal(POWER_GROUP_DISPLAY_SCREEN_ON, this.mNumDisplays, Double.NaN)) && Double.isNaN(getAveragePowerForOrdinal(POWER_GROUP_DISPLAY_SCREEN_FULL, this.mNumDisplays, Double.NaN))) {
                break;
            }
            this.mNumDisplays++;
        }
        HashMap<String, Double> hashMap = sPowerItemMap;
        Double deprecatedAmbientDisplay = hashMap.get(POWER_AMBIENT_DISPLAY);
        boolean legacy = false;
        if (deprecatedAmbientDisplay != null && this.mNumDisplays == 0) {
            String key = getOrdinalPowerType(POWER_GROUP_DISPLAY_AMBIENT, 0);
            Slog.m90w(TAG, "ambient.on is deprecated! Use " + key + " instead.");
            hashMap.put(key, deprecatedAmbientDisplay);
            legacy = true;
        }
        Double deprecatedScreenOn = hashMap.get(POWER_SCREEN_ON);
        if (deprecatedScreenOn != null && this.mNumDisplays == 0) {
            String key2 = getOrdinalPowerType(POWER_GROUP_DISPLAY_SCREEN_ON, 0);
            Slog.m90w(TAG, "screen.on is deprecated! Use " + key2 + " instead.");
            hashMap.put(key2, deprecatedScreenOn);
            legacy = true;
        }
        Double deprecatedScreenFull = hashMap.get(POWER_SCREEN_FULL);
        if (deprecatedScreenFull != null && this.mNumDisplays == 0) {
            String key3 = getOrdinalPowerType(POWER_GROUP_DISPLAY_SCREEN_FULL, 0);
            Slog.m90w(TAG, "screen.full is deprecated! Use " + key3 + " instead.");
            hashMap.put(key3, deprecatedScreenFull);
            legacy = true;
        }
        if (legacy) {
            this.mNumDisplays = 1;
        }
    }

    public int getNumDisplays() {
        return this.mNumDisplays;
    }

    private void initModem() {
        handleDeprecatedModemConstant(0, POWER_MODEM_CONTROLLER_SLEEP, 0);
        handleDeprecatedModemConstant(268435456, POWER_MODEM_CONTROLLER_IDLE, 0);
        handleDeprecatedModemConstant(536870912, POWER_MODEM_CONTROLLER_RX, 0);
        handleDeprecatedModemConstant(805306368, POWER_MODEM_CONTROLLER_TX, 0);
        handleDeprecatedModemConstant(Enums.AUDIO_FORMAT_APTX_R4, POWER_MODEM_CONTROLLER_TX, 1);
        handleDeprecatedModemConstant(Enums.AUDIO_FORMAT_DTS_HD_MA, POWER_MODEM_CONTROLLER_TX, 2);
        handleDeprecatedModemConstant(Enums.AUDIO_FORMAT_DTS_UHD_P2, POWER_MODEM_CONTROLLER_TX, 3);
        handleDeprecatedModemConstant(872415232, POWER_MODEM_CONTROLLER_TX, 4);
    }

    private void handleDeprecatedModemConstant(int key, String deprecatedKey, int level) {
        ModemPowerProfile modemPowerProfile = sModemPowerProfile;
        double drain = modemPowerProfile.getAverageBatteryDrainMa(key);
        if (Double.isNaN(drain)) {
            double deprecatedDrain = getAveragePower(deprecatedKey, level);
            modemPowerProfile.setPowerConstant(key, Double.toString(deprecatedDrain));
        }
    }

    public int getNumElements(String key) {
        if (sPowerItemMap.containsKey(key)) {
            return 1;
        }
        HashMap<String, Double[]> hashMap = sPowerArrayMap;
        if (hashMap.containsKey(key)) {
            return hashMap.get(key).length;
        }
        return 0;
    }

    public double getAveragePowerOrDefault(String type, double defaultValue) {
        HashMap<String, Double> hashMap = sPowerItemMap;
        if (hashMap.containsKey(type)) {
            return hashMap.get(type).doubleValue();
        }
        HashMap<String, Double[]> hashMap2 = sPowerArrayMap;
        if (hashMap2.containsKey(type)) {
            return hashMap2.get(type)[0].doubleValue();
        }
        return defaultValue;
    }

    public double getAveragePower(String type) {
        return getAveragePowerOrDefault(type, 0.0d);
    }

    public double getAverageBatteryDrainOrDefaultMa(long key, double defaultValue) {
        double value;
        long subsystemType = 64424509440L & key;
        int subsystemFields = (int) ((-1) & key);
        if (subsystemType == 4294967296L) {
            value = sModemPowerProfile.getAverageBatteryDrainMa(subsystemFields);
        } else {
            value = Double.NaN;
        }
        return Double.isNaN(value) ? defaultValue : value;
    }

    public double getAverageBatteryDrainMa(long key) {
        return getAverageBatteryDrainOrDefaultMa(key, 0.0d);
    }

    public double getAveragePower(String type, int level) {
        HashMap<String, Double> hashMap = sPowerItemMap;
        if (hashMap.containsKey(type)) {
            return hashMap.get(type).doubleValue();
        }
        HashMap<String, Double[]> hashMap2 = sPowerArrayMap;
        if (hashMap2.containsKey(type)) {
            Double[] values = hashMap2.get(type);
            if (values.length > level && level >= 0) {
                return values[level].doubleValue();
            }
            if (level < 0 || values.length == 0) {
                return 0.0d;
            }
            return values[values.length - 1].doubleValue();
        }
        return 0.0d;
    }

    public double getAveragePowerForOrdinal(String group, int ordinal, double defaultValue) {
        String type = getOrdinalPowerType(group, ordinal);
        return getAveragePowerOrDefault(type, defaultValue);
    }

    public double getAveragePowerForOrdinal(String group, int ordinal) {
        return getAveragePowerForOrdinal(group, ordinal, 0.0d);
    }

    public double getBatteryCapacity() {
        return getAveragePower(POWER_BATTERY_CAPACITY);
    }

    public void dumpDebug(ProtoOutputStream proto) {
        Double[] dArr;
        Double[] dArr2;
        writePowerConstantToProto(proto, POWER_CPU_SUSPEND, 1103806595073L);
        writePowerConstantToProto(proto, POWER_CPU_IDLE, 1103806595074L);
        writePowerConstantToProto(proto, POWER_CPU_ACTIVE, 1103806595075L);
        for (int cluster = 0; cluster < this.mCpuClusters.length; cluster++) {
            long token = proto.start(2246267895848L);
            proto.write(1120986464257L, cluster);
            proto.write(1103806595074L, sPowerItemMap.get(this.mCpuClusters[cluster].clusterPowerKey).doubleValue());
            proto.write(1120986464259L, this.mCpuClusters[cluster].numCpus);
            for (Double speed : sPowerArrayMap.get(this.mCpuClusters[cluster].freqKey)) {
                proto.write(PowerProfileProto.CpuCluster.SPEED, speed.doubleValue());
            }
            for (Double corePower : sPowerArrayMap.get(this.mCpuClusters[cluster].corePowerKey)) {
                proto.write(PowerProfileProto.CpuCluster.CORE_POWER, corePower.doubleValue());
            }
            proto.end(token);
        }
        writePowerConstantToProto(proto, POWER_WIFI_SCAN, 1103806595076L);
        writePowerConstantToProto(proto, POWER_WIFI_ON, 1103806595077L);
        writePowerConstantToProto(proto, POWER_WIFI_ACTIVE, 1103806595078L);
        writePowerConstantToProto(proto, POWER_WIFI_CONTROLLER_IDLE, PowerProfileProto.WIFI_CONTROLLER_IDLE);
        writePowerConstantToProto(proto, POWER_WIFI_CONTROLLER_RX, 1103806595080L);
        writePowerConstantToProto(proto, POWER_WIFI_CONTROLLER_TX, 1103806595081L);
        writePowerConstantArrayToProto(proto, POWER_WIFI_CONTROLLER_TX_LEVELS, PowerProfileProto.WIFI_CONTROLLER_TX_LEVELS);
        writePowerConstantToProto(proto, POWER_WIFI_CONTROLLER_OPERATING_VOLTAGE, PowerProfileProto.WIFI_CONTROLLER_OPERATING_VOLTAGE);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_CONTROLLER_IDLE, PowerProfileProto.BLUETOOTH_CONTROLLER_IDLE);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_CONTROLLER_RX, PowerProfileProto.BLUETOOTH_CONTROLLER_RX);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_CONTROLLER_TX, PowerProfileProto.BLUETOOTH_CONTROLLER_TX);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_CONTROLLER_OPERATING_VOLTAGE, PowerProfileProto.BLUETOOTH_CONTROLLER_OPERATING_VOLTAGE);
        writePowerConstantToProto(proto, POWER_MODEM_CONTROLLER_SLEEP, PowerProfileProto.MODEM_CONTROLLER_SLEEP);
        writePowerConstantToProto(proto, POWER_MODEM_CONTROLLER_IDLE, PowerProfileProto.MODEM_CONTROLLER_IDLE);
        writePowerConstantToProto(proto, POWER_MODEM_CONTROLLER_RX, 1103806595090L);
        writePowerConstantArrayToProto(proto, POWER_MODEM_CONTROLLER_TX, PowerProfileProto.MODEM_CONTROLLER_TX);
        writePowerConstantToProto(proto, POWER_MODEM_CONTROLLER_OPERATING_VOLTAGE, 1103806595092L);
        writePowerConstantToProto(proto, POWER_GPS_ON, 1103806595093L);
        writePowerConstantArrayToProto(proto, POWER_GPS_SIGNAL_QUALITY_BASED, PowerProfileProto.GPS_SIGNAL_QUALITY_BASED);
        writePowerConstantToProto(proto, POWER_GPS_OPERATING_VOLTAGE, 1103806595095L);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_ON, 1103806595096L);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_ACTIVE, 1103806595097L);
        writePowerConstantToProto(proto, POWER_BLUETOOTH_AT_CMD, 1103806595098L);
        writePowerConstantToProto(proto, POWER_AMBIENT_DISPLAY, PowerProfileProto.AMBIENT_DISPLAY);
        writePowerConstantToProto(proto, POWER_SCREEN_ON, PowerProfileProto.SCREEN_ON);
        writePowerConstantToProto(proto, POWER_RADIO_ON, PowerProfileProto.RADIO_ON);
        writePowerConstantToProto(proto, POWER_RADIO_SCANNING, PowerProfileProto.RADIO_SCANNING);
        writePowerConstantToProto(proto, POWER_RADIO_ACTIVE, PowerProfileProto.RADIO_ACTIVE);
        writePowerConstantToProto(proto, POWER_SCREEN_FULL, PowerProfileProto.SCREEN_FULL);
        writePowerConstantToProto(proto, "audio", PowerProfileProto.AUDIO);
        writePowerConstantToProto(proto, "video", PowerProfileProto.VIDEO);
        writePowerConstantToProto(proto, POWER_FLASHLIGHT, 1103806595107L);
        writePowerConstantToProto(proto, POWER_MEMORY, 1103806595108L);
        writePowerConstantToProto(proto, POWER_CAMERA, 1103806595109L);
        writePowerConstantToProto(proto, POWER_WIFI_BATCHED_SCAN, PowerProfileProto.WIFI_BATCHED_SCAN);
        writePowerConstantToProto(proto, POWER_BATTERY_CAPACITY, PowerProfileProto.BATTERY_CAPACITY);
    }

    public void dump(PrintWriter pw) {
        final IndentingPrintWriter ipw = new IndentingPrintWriter(pw);
        sPowerItemMap.forEach(new BiConsumer() { // from class: com.android.internal.os.PowerProfile$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PowerProfile.lambda$dump$0(IndentingPrintWriter.this, (String) obj, (Double) obj2);
            }
        });
        sPowerArrayMap.forEach(new BiConsumer() { // from class: com.android.internal.os.PowerProfile$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PowerProfile.lambda$dump$1(IndentingPrintWriter.this, (String) obj, (Double[]) obj2);
            }
        });
        ipw.println("Modem values:");
        ipw.increaseIndent();
        sModemPowerProfile.dump(ipw);
        ipw.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$dump$0(IndentingPrintWriter ipw, String key, Double value) {
        ipw.print(key, value);
        ipw.println();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$dump$1(IndentingPrintWriter ipw, String key, Double[] value) {
        ipw.print(key, Arrays.toString(value));
        ipw.println();
    }

    private void writePowerConstantToProto(ProtoOutputStream proto, String key, long fieldId) {
        HashMap<String, Double> hashMap = sPowerItemMap;
        if (hashMap.containsKey(key)) {
            proto.write(fieldId, hashMap.get(key).doubleValue());
        }
    }

    private void writePowerConstantArrayToProto(ProtoOutputStream proto, String key, long fieldId) {
        Double[] dArr;
        HashMap<String, Double[]> hashMap = sPowerArrayMap;
        if (hashMap.containsKey(key)) {
            for (Double d : hashMap.get(key)) {
                proto.write(fieldId, d.doubleValue());
            }
        }
    }

    private static String getOrdinalPowerType(String group, int ordinal) {
        return group + ordinal;
    }
}
