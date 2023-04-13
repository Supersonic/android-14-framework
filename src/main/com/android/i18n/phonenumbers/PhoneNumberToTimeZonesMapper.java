package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.prefixmapper.PrefixTimeZonesMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public class PhoneNumberToTimeZonesMapper {
    private static final String MAPPING_DATA_DIRECTORY = "/com/android/i18n/phonenumbers/timezones/data/";
    private static final String MAPPING_DATA_FILE_NAME = "map_data";
    private static final String UNKNOWN_TIMEZONE = "Etc/Unknown";
    static final List<String> UNKNOWN_TIME_ZONE_LIST;
    private static final Logger logger;
    private PrefixTimeZonesMap prefixTimeZonesMap;

    static {
        ArrayList arrayList = new ArrayList(1);
        UNKNOWN_TIME_ZONE_LIST = arrayList;
        arrayList.add(UNKNOWN_TIMEZONE);
        logger = Logger.getLogger(PhoneNumberToTimeZonesMapper.class.getName());
    }

    PhoneNumberToTimeZonesMapper(String prefixTimeZonesMapDataDirectory) {
        this.prefixTimeZonesMap = null;
        this.prefixTimeZonesMap = loadPrefixTimeZonesMapFromFile(prefixTimeZonesMapDataDirectory + MAPPING_DATA_FILE_NAME);
    }

    private PhoneNumberToTimeZonesMapper(PrefixTimeZonesMap prefixTimeZonesMap) {
        this.prefixTimeZonesMap = null;
        this.prefixTimeZonesMap = prefixTimeZonesMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PrefixTimeZonesMap loadPrefixTimeZonesMapFromFile(String path) {
        InputStream source = PhoneNumberToTimeZonesMapper.class.getResourceAsStream(path);
        ObjectInputStream in = null;
        PrefixTimeZonesMap map = new PrefixTimeZonesMap();
        try {
            try {
                in = new ObjectInputStream(source);
                map.readExternal(in);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.toString());
            }
            return map;
        } finally {
            close(in);
        }
    }

    private static void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.toString());
            }
        }
    }

    /* loaded from: classes.dex */
    private static class LazyHolder {
        private static final PhoneNumberToTimeZonesMapper INSTANCE;

        private LazyHolder() {
        }

        static {
            PrefixTimeZonesMap map = PhoneNumberToTimeZonesMapper.loadPrefixTimeZonesMapFromFile("/com/android/i18n/phonenumbers/timezones/data/map_data");
            INSTANCE = new PhoneNumberToTimeZonesMapper(map);
        }
    }

    public static synchronized PhoneNumberToTimeZonesMapper getInstance() {
        PhoneNumberToTimeZonesMapper phoneNumberToTimeZonesMapper;
        synchronized (PhoneNumberToTimeZonesMapper.class) {
            phoneNumberToTimeZonesMapper = LazyHolder.INSTANCE;
        }
        return phoneNumberToTimeZonesMapper;
    }

    public List<String> getTimeZonesForGeographicalNumber(Phonenumber.PhoneNumber number) {
        return getTimeZonesForGeocodableNumber(number);
    }

    public List<String> getTimeZonesForNumber(Phonenumber.PhoneNumber number) {
        PhoneNumberUtil.PhoneNumberType numberType = PhoneNumberUtil.getInstance().getNumberType(number);
        if (numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN) {
            return UNKNOWN_TIME_ZONE_LIST;
        }
        if (!PhoneNumberUtil.getInstance().isNumberGeographical(numberType, number.getCountryCode())) {
            return getCountryLevelTimeZonesforNumber(number);
        }
        return getTimeZonesForGeographicalNumber(number);
    }

    public static String getUnknownTimeZone() {
        return UNKNOWN_TIMEZONE;
    }

    private List<String> getTimeZonesForGeocodableNumber(Phonenumber.PhoneNumber number) {
        List<String> timezones = this.prefixTimeZonesMap.lookupTimeZonesForNumber(number);
        return Collections.unmodifiableList(timezones.isEmpty() ? UNKNOWN_TIME_ZONE_LIST : timezones);
    }

    private List<String> getCountryLevelTimeZonesforNumber(Phonenumber.PhoneNumber number) {
        List<String> timezones = this.prefixTimeZonesMap.lookupCountryLevelTimeZonesForNumber(number);
        return Collections.unmodifiableList(timezones.isEmpty() ? UNKNOWN_TIME_ZONE_LIST : timezones);
    }
}
