package android.sysprop;

import android.media.AudioSystem;
import android.p008os.SystemProperties;
import android.provider.Downloads;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
/* loaded from: classes3.dex */
public final class BluetoothProperties {
    private BluetoothProperties() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Boolean tryParseBoolean(String str) {
        char c;
        if (str == null) {
            return null;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        switch (lowerCase.hashCode()) {
            case 48:
                if (lowerCase.equals(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (lowerCase.equals("1")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (lowerCase.equals("true")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (lowerCase.equals("false")) {
                    c = 3;
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
            case 1:
                return Boolean.TRUE;
            case 2:
            case 3:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    private static Integer tryParseInteger(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Integer tryParseUInt(String str) {
        try {
            return Integer.valueOf(Integer.parseUnsignedInt(str));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryParseLong(String str) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryParseULong(String str) {
        try {
            return Long.valueOf(Long.parseUnsignedLong(str));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double tryParseDouble(String str) {
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String tryParseString(String str) {
        if ("".equals(str)) {
            return null;
        }
        return str;
    }

    private static <T extends Enum<T>> T tryParseEnum(Class<T> enumType, String str) {
        try {
            return (T) Enum.valueOf(enumType, str.toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static <T> List<T> tryParseList(Function<String, T> elementParser, String str) {
        if ("".equals(str)) {
            return new ArrayList();
        }
        List<T> ret = new ArrayList<>();
        int p = 0;
        while (true) {
            StringBuilder sb = new StringBuilder();
            while (p < str.length() && str.charAt(p) != ',') {
                if (str.charAt(p) == '\\') {
                    p++;
                }
                if (p == str.length()) {
                    break;
                }
                sb.append(str.charAt(p));
                p++;
            }
            ret.add(elementParser.apply(sb.toString()));
            if (p == str.length()) {
                return ret;
            }
            p++;
        }
    }

    private static <T extends Enum<T>> List<T> tryParseEnumList(Class<T> enumType, String str) {
        String[] split;
        if ("".equals(str)) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (String element : str.split(",")) {
            arrayList.add(tryParseEnum(enumType, element));
        }
        return arrayList;
    }

    private static String escape(String str) {
        return str.replaceAll("([\\\\,])", "\\\\$1");
    }

    private static <T> String formatList(List<T> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T element = it.next();
            joiner.add(element == null ? "" : escape(element.toString()));
        }
        return joiner.toString();
    }

    private static String formatUIntList(List<Integer> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer element = it.next();
            joiner.add(element == null ? "" : escape(Integer.toUnsignedString(element.intValue())));
        }
        return joiner.toString();
    }

    private static String formatULongList(List<Long> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<Long> it = list.iterator();
        while (it.hasNext()) {
            Long element = it.next();
            joiner.add(element == null ? "" : escape(Long.toUnsignedString(element.longValue())));
        }
        return joiner.toString();
    }

    private static <T extends Enum<T>> String formatEnumList(List<T> list, Function<T, String> elementFormatter) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T element = it.next();
            joiner.add(element == null ? "" : elementFormatter.apply(element));
        }
        return joiner.toString();
    }

    /* loaded from: classes3.dex */
    public enum snoop_default_mode_values {
        EMPTY("empty"),
        DISABLED("disabled"),
        FILTERED("filtered"),
        FULL("full");
        
        private final String propValue;

        snoop_default_mode_values(String propValue) {
            this.propValue = propValue;
        }

        public String getPropValue() {
            return this.propValue;
        }
    }

    public static Optional<snoop_default_mode_values> snoop_default_mode() {
        String value = SystemProperties.get("persist.bluetooth.btsnoopdefaultmode");
        return Optional.ofNullable((snoop_default_mode_values) tryParseEnum(snoop_default_mode_values.class, value));
    }

    public static void snoop_default_mode(snoop_default_mode_values value) {
        SystemProperties.set("persist.bluetooth.btsnoopdefaultmode", value == null ? "" : value.getPropValue());
    }

    /* loaded from: classes3.dex */
    public enum snoop_log_mode_values {
        EMPTY("empty"),
        DISABLED("disabled"),
        FILTERED("filtered"),
        FULL("full");
        
        private final String propValue;

        snoop_log_mode_values(String propValue) {
            this.propValue = propValue;
        }

        public String getPropValue() {
            return this.propValue;
        }
    }

    public static Optional<snoop_log_mode_values> snoop_log_mode() {
        String value = SystemProperties.get("persist.bluetooth.btsnooplogmode");
        return Optional.ofNullable((snoop_log_mode_values) tryParseEnum(snoop_log_mode_values.class, value));
    }

    public static void snoop_log_mode(snoop_log_mode_values value) {
        SystemProperties.set("persist.bluetooth.btsnooplogmode", value == null ? "" : value.getPropValue());
    }

    public static Optional<Boolean> snoop_log_filter_snoop_headers_enabled() {
        String value = SystemProperties.get("persist.bluetooth.snooplogfilter.headers.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void snoop_log_filter_snoop_headers_enabled(Boolean value) {
        SystemProperties.set("persist.bluetooth.snooplogfilter.headers.enabled", value == null ? "" : value.toString());
    }

    public static Optional<Boolean> snoop_log_filter_profile_a2dp_enabled() {
        String value = SystemProperties.get("persist.bluetooth.snooplogfilter.profiles.a2dp.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void snoop_log_filter_profile_a2dp_enabled(Boolean value) {
        SystemProperties.set("persist.bluetooth.snooplogfilter.profiles.a2dp.enabled", value == null ? "" : value.toString());
    }

    /* loaded from: classes3.dex */
    public enum snoop_log_filter_profile_map_values {
        EMPTY("empty"),
        DISABLED("disabled"),
        FULLFILTER("fullfilter"),
        HEADER(Downloads.Impl.RequestHeaders.COLUMN_HEADER),
        MAGIC("magic");
        
        private final String propValue;

        snoop_log_filter_profile_map_values(String propValue) {
            this.propValue = propValue;
        }

        public String getPropValue() {
            return this.propValue;
        }
    }

    public static Optional<snoop_log_filter_profile_map_values> snoop_log_filter_profile_map() {
        String value = SystemProperties.get("persist.bluetooth.snooplogfilter.profiles.map");
        return Optional.ofNullable((snoop_log_filter_profile_map_values) tryParseEnum(snoop_log_filter_profile_map_values.class, value));
    }

    public static void snoop_log_filter_profile_map(snoop_log_filter_profile_map_values value) {
        SystemProperties.set("persist.bluetooth.snooplogfilter.profiles.map", value == null ? "" : value.getPropValue());
    }

    /* loaded from: classes3.dex */
    public enum snoop_log_filter_profile_pbap_values {
        EMPTY("empty"),
        DISABLED("disabled"),
        FULLFILTER("fullfilter"),
        HEADER(Downloads.Impl.RequestHeaders.COLUMN_HEADER),
        MAGIC("magic");
        
        private final String propValue;

        snoop_log_filter_profile_pbap_values(String propValue) {
            this.propValue = propValue;
        }

        public String getPropValue() {
            return this.propValue;
        }
    }

    public static Optional<snoop_log_filter_profile_pbap_values> snoop_log_filter_profile_pbap() {
        String value = SystemProperties.get("persist.bluetooth.snooplogfilter.profiles.pbap");
        return Optional.ofNullable((snoop_log_filter_profile_pbap_values) tryParseEnum(snoop_log_filter_profile_pbap_values.class, value));
    }

    public static void snoop_log_filter_profile_pbap(snoop_log_filter_profile_pbap_values value) {
        SystemProperties.set("persist.bluetooth.snooplogfilter.profiles.pbap", value == null ? "" : value.getPropValue());
    }

    public static Optional<Boolean> snoop_log_filter_profile_rfcomm_enabled() {
        String value = SystemProperties.get("persist.bluetooth.snooplogfilter.profiles.rfcomm.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void snoop_log_filter_profile_rfcomm_enabled(Boolean value) {
        SystemProperties.set("persist.bluetooth.snooplogfilter.profiles.rfcomm.enabled", value == null ? "" : value.toString());
    }

    public static Optional<Boolean> factory_reset() {
        String value = SystemProperties.get("persist.bluetooth.factoryreset");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void factory_reset(Boolean value) {
        SystemProperties.set("persist.bluetooth.factoryreset", value == null ? "" : value.toString());
    }

    public static List<String> le_audio_allow_list() {
        String value = SystemProperties.get("persist.bluetooth.leaudio.allow_list");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = BluetoothProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void le_audio_allow_list(List<String> value) {
        SystemProperties.set("persist.bluetooth.leaudio.allow_list", value == null ? "" : formatList(value));
    }

    public static Optional<Boolean> isGapLePrivacyEnabled() {
        String value = SystemProperties.get("bluetooth.core.gap.le.privacy.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Integer> getGapLeConnMinLimit() {
        String value = SystemProperties.get("bluetooth.core.gap.le.conn.min.limit");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Boolean> isGapLeConnOnlyInit1mPhyEnabled() {
        String value = SystemProperties.get("bluetooth.core.gap.le.conn.only_init_1m_phy.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isLeAudioInbandRingtoneSupported() {
        String value = SystemProperties.get("bluetooth.core.le_audio.inband_ringtone.supported");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<String> getDefaultDeviceName() {
        String value = SystemProperties.get("bluetooth.device.default_name");
        return Optional.ofNullable(tryParseString(value));
    }

    public static List<Integer> getClassOfDevice() {
        String value = SystemProperties.get("bluetooth.device.class_of_device");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseUInt;
                tryParseUInt = BluetoothProperties.tryParseUInt((String) obj);
                return tryParseUInt;
            }
        }, value);
    }

    public static Optional<Integer> getDefaultOutputOnlyAudioProfile() {
        String value = SystemProperties.get("bluetooth.device.default_output_only_audio_profile");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> getDefaultDuplexAudioProfile() {
        String value = SystemProperties.get("bluetooth.device.default_duplex_audio_profile");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> getHardwareOperatingVoltageMv() {
        String value = SystemProperties.get("bluetooth.hardware.power.operating_voltage_mv");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> getHardwareIdleCurrentMa() {
        String value = SystemProperties.get("bluetooth.hardware.power.idle_cur_ma");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> getHardwareTxCurrentMa() {
        String value = SystemProperties.get("bluetooth.hardware.power.tx_cur_ma");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> getHardwareRxCurrentMa() {
        String value = SystemProperties.get("bluetooth.hardware.power.rx_cur_ma");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Boolean> isSupportPersistedStateEnabled() {
        String value = SystemProperties.get("bluetooth.framework.support_persisted_state");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isAdapterAddressValidationEnabled() {
        String value = SystemProperties.get("bluetooth.framework.adapter_address_validation");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileA2dpSinkEnabled() {
        String value = SystemProperties.get("bluetooth.profile.a2dp.sink.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileA2dpSourceEnabled() {
        String value = SystemProperties.get("bluetooth.profile.a2dp.source.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileAshaCentralEnabled() {
        String value = SystemProperties.get("bluetooth.profile.asha.central.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileAvrcpControllerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.avrcp.controller.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileAvrcpTargetEnabled() {
        String value = SystemProperties.get("bluetooth.profile.avrcp.target.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileBapBroadcastAssistEnabled() {
        String value = SystemProperties.get("bluetooth.profile.bap.broadcast.assist.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileBapBroadcastSourceEnabled() {
        String value = SystemProperties.get("bluetooth.profile.bap.broadcast.source.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileBapUnicastClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.bap.unicast.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileBasClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.bas.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileBassClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.bass.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileCsipSetCoordinatorEnabled() {
        String value = SystemProperties.get("bluetooth.profile.csip.set_coordinator.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileGattEnabled() {
        String value = SystemProperties.get("bluetooth.profile.gatt.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileHapClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.hap.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileHfpAgEnabled() {
        String value = SystemProperties.get("bluetooth.profile.hfp.ag.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileHfpHfEnabled() {
        String value = SystemProperties.get("bluetooth.profile.hfp.hf.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileHidDeviceEnabled() {
        String value = SystemProperties.get("bluetooth.profile.hid.device.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileHidHostEnabled() {
        String value = SystemProperties.get("bluetooth.profile.hid.host.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileMapClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.map.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileMapServerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.map.server.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileMcpServerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.mcp.server.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileOppEnabled() {
        String value = SystemProperties.get("bluetooth.profile.opp.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfilePanNapEnabled() {
        String value = SystemProperties.get("bluetooth.profile.pan.nap.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfilePanPanuEnabled() {
        String value = SystemProperties.get("bluetooth.profile.pan.panu.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfilePbapClientEnabled() {
        String value = SystemProperties.get("bluetooth.profile.pbap.client.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfilePbapServerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.pbap.server.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileSapServerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.sap.server.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileCcpServerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.ccp.server.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> isProfileVcpControllerEnabled() {
        String value = SystemProperties.get("bluetooth.profile.vcp.controller.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Integer> getLinkSupervisionTimeout() {
        String value = SystemProperties.get("bluetooth.core.acl.link_supervision_timeout");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicPageScanType() {
        String value = SystemProperties.get("bluetooth.core.classic.page_scan_type");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicPageScanInterval() {
        String value = SystemProperties.get("bluetooth.core.classic.page_scan_interval");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicPageScanWindow() {
        String value = SystemProperties.get("bluetooth.core.classic.page_scan_window");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicInquiryScanType() {
        String value = SystemProperties.get("bluetooth.core.classic.inq_scan_type");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicInquiryScanInterval() {
        String value = SystemProperties.get("bluetooth.core.classic.inq_scan_interval");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicInquiryScanWindow() {
        String value = SystemProperties.get("bluetooth.core.classic.inq_scan_window");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getClassicPageTimeout() {
        String value = SystemProperties.get("bluetooth.core.classic.page_timeout");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static List<Integer> getClassicSniffMaxIntervals() {
        String value = SystemProperties.get("bluetooth.core.classic.sniff_max_intervals");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseUInt;
                tryParseUInt = BluetoothProperties.tryParseUInt((String) obj);
                return tryParseUInt;
            }
        }, value);
    }

    public static List<Integer> getClassicSniffMinIntervals() {
        String value = SystemProperties.get("bluetooth.core.classic.sniff_min_intervals");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseUInt;
                tryParseUInt = BluetoothProperties.tryParseUInt((String) obj);
                return tryParseUInt;
            }
        }, value);
    }

    public static List<Integer> getClassicSniffAttempts() {
        String value = SystemProperties.get("bluetooth.core.classic.sniff_attempts");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseUInt;
                tryParseUInt = BluetoothProperties.tryParseUInt((String) obj);
                return tryParseUInt;
            }
        }, value);
    }

    public static List<Integer> getClassicSniffTimeouts() {
        String value = SystemProperties.get("bluetooth.core.classic.sniff_timeouts");
        return tryParseList(new Function() { // from class: android.sysprop.BluetoothProperties$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseUInt;
                tryParseUInt = BluetoothProperties.tryParseUInt((String) obj);
                return tryParseUInt;
            }
        }, value);
    }

    public static Optional<Integer> getLeMinConnectionInterval() {
        String value = SystemProperties.get("bluetooth.core.le.min_connection_interval");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeMaxConnectionInterval() {
        String value = SystemProperties.get("bluetooth.core.le.max_connection_interval");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionLatency() {
        String value = SystemProperties.get("bluetooth.core.le.connection_latency");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionSupervisionTimeout() {
        String value = SystemProperties.get("bluetooth.core.le.connection_supervision_timeout");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeDirectConnectionTimeout() {
        String value = SystemProperties.get("bluetooth.core.le.direct_connection_timeout");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanIntervalFast() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_interval_fast");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanWindowFast() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_window_fast");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanWindow2mFast() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_window_2m_fast");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanWindowCodedFast() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_window_coded_fast");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanIntervalSlow() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_interval_slow");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeConnectionScanWindowSlow() {
        String value = SystemProperties.get("bluetooth.core.le.connection_scan_window_slow");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeInquiryScanInterval() {
        String value = SystemProperties.get("bluetooth.core.le.inquiry_scan_interval");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Integer> getLeInquiryScanWindow() {
        String value = SystemProperties.get("bluetooth.core.le.inquiry_scan_window");
        return Optional.ofNullable(tryParseUInt(value));
    }

    public static Optional<Boolean> getLeVendorCapabilitiesEnabled() {
        String value = SystemProperties.get("bluetooth.core.le.vendor_capabilities.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> getDisableEnchancedConnection() {
        String value = SystemProperties.get("bluetooth.sco.disable_enhanced_connection");
        return Optional.ofNullable(tryParseBoolean(value));
    }
}
