package com.android.internal.telephony.phonenumbers.prefixmapper;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.phonenumbers.MetadataLoader;
import com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber;
import com.android.internal.telephony.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public class PrefixFileReader {
    private static final Logger logger = Logger.getLogger(PrefixFileReader.class.getName());
    private final String phonePrefixDataDirectory;
    private MappingFileProvider mappingFileProvider = new MappingFileProvider();
    private Map<String, PhonePrefixMap> availablePhonePrefixMaps = new HashMap();
    private final MetadataLoader metadataLoader = DefaultMetadataDependenciesProvider.getInstance().getMetadataLoader();

    public PrefixFileReader(String str) {
        this.phonePrefixDataDirectory = str;
        loadMappingFileProvider();
    }

    private void loadMappingFileProvider() {
        ObjectInputStream objectInputStream;
        MetadataLoader metadataLoader = this.metadataLoader;
        InputStream loadMetadata = metadataLoader.loadMetadata(this.phonePrefixDataDirectory + "config");
        ObjectInputStream objectInputStream2 = null;
        try {
            try {
                objectInputStream = new ObjectInputStream(loadMetadata);
            } catch (IOException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            this.mappingFileProvider.readExternal(objectInputStream);
            close(objectInputStream);
        } catch (IOException e2) {
            e = e2;
            objectInputStream2 = objectInputStream;
            logger.log(Level.WARNING, e.toString());
            close(objectInputStream2);
        } catch (Throwable th2) {
            th = th2;
            objectInputStream2 = objectInputStream;
            close(objectInputStream2);
            throw th;
        }
    }

    private PhonePrefixMap getPhonePrefixDescriptions(int i, String str, String str2, String str3) {
        String fileName = this.mappingFileProvider.getFileName(i, str, str2, str3);
        if (fileName.length() == 0) {
            return null;
        }
        if (!this.availablePhonePrefixMaps.containsKey(fileName)) {
            loadPhonePrefixMapFromFile(fileName);
        }
        return this.availablePhonePrefixMaps.get(fileName);
    }

    private void loadPhonePrefixMapFromFile(String str) {
        ObjectInputStream objectInputStream;
        MetadataLoader metadataLoader = this.metadataLoader;
        InputStream loadMetadata = metadataLoader.loadMetadata(this.phonePrefixDataDirectory + str);
        ObjectInputStream objectInputStream2 = null;
        try {
            try {
                objectInputStream = new ObjectInputStream(loadMetadata);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            PhonePrefixMap phonePrefixMap = new PhonePrefixMap();
            phonePrefixMap.readExternal(objectInputStream);
            this.availablePhonePrefixMaps.put(str, phonePrefixMap);
            close(objectInputStream);
        } catch (IOException e2) {
            e = e2;
            objectInputStream2 = objectInputStream;
            logger.log(Level.WARNING, e.toString());
            close(objectInputStream2);
        } catch (Throwable th2) {
            th = th2;
            objectInputStream2 = objectInputStream;
            close(objectInputStream2);
            throw th;
        }
    }

    private static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.toString());
            }
        }
    }

    public String getDescriptionForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str, String str2, String str3) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        if (countryCode == 1) {
            countryCode = ((int) (phonenumber$PhoneNumber.getNationalNumber() / 10000000)) + 1000;
        }
        PhonePrefixMap phonePrefixDescriptions = getPhonePrefixDescriptions(countryCode, str, str2, str3);
        String lookup = phonePrefixDescriptions != null ? phonePrefixDescriptions.lookup(phonenumber$PhoneNumber) : null;
        if ((lookup == null || lookup.length() == 0) && mayFallBackToEnglish(str)) {
            PhonePrefixMap phonePrefixDescriptions2 = getPhonePrefixDescriptions(countryCode, "en", PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS);
            if (phonePrefixDescriptions2 == null) {
                return PhoneConfigurationManager.SSSS;
            }
            lookup = phonePrefixDescriptions2.lookup(phonenumber$PhoneNumber);
        }
        return lookup != null ? lookup : PhoneConfigurationManager.SSSS;
    }

    private boolean mayFallBackToEnglish(String str) {
        return (str.equals("zh") || str.equals("ja") || str.equals("ko")) ? false : true;
    }
}
