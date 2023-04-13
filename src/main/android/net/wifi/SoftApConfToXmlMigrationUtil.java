package android.net.wifi;

import android.net.MacAddress;
import android.net.wifi.SoftApConfiguration;
import android.p008os.Environment;
import android.util.Log;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes2.dex */
public final class SoftApConfToXmlMigrationUtil {
    private static final int CONFIG_STORE_DATA_VERSION = 3;
    private static final String LEGACY_AP_CONFIG_FILE = "softap.conf";
    private static final String LEGACY_WIFI_STORE_DIRECTORY_NAME = "wifi";
    private static final String TAG = "SoftApConfToXmlMigrationUtil";
    private static final int WIFICONFIG_AP_BAND_2GHZ = 0;
    private static final int WIFICONFIG_AP_BAND_5GHZ = 1;
    private static final int WIFICONFIG_AP_BAND_ANY = -1;
    private static final String XML_TAG_ALLOWED_CLIENT_LIST = "AllowedClientList";
    private static final String XML_TAG_AP_BAND = "ApBand";
    private static final String XML_TAG_AUTO_SHUTDOWN_ENABLED = "AutoShutdownEnabled";
    private static final String XML_TAG_BLOCKED_CLIENT_LIST = "BlockedClientList";
    private static final String XML_TAG_BSSID = "Bssid";
    private static final String XML_TAG_CHANNEL = "Channel";
    private static final String XML_TAG_CLIENT_CONTROL_BY_USER = "ClientControlByUser";
    public static final String XML_TAG_CLIENT_MACADDRESS = "ClientMacAddress";
    private static final String XML_TAG_DOCUMENT_HEADER = "WifiConfigStoreData";
    private static final String XML_TAG_HIDDEN_SSID = "HiddenSSID";
    private static final String XML_TAG_MAX_NUMBER_OF_CLIENTS = "MaxNumberOfClients";
    private static final String XML_TAG_PASSPHRASE = "Passphrase";
    private static final String XML_TAG_SECTION_HEADER_SOFTAP = "SoftAp";
    private static final String XML_TAG_SECURITY_TYPE = "SecurityType";
    private static final String XML_TAG_SHUTDOWN_TIMEOUT_MILLIS = "ShutdownTimeoutMillis";
    private static final String XML_TAG_SSID = "SSID";
    private static final String XML_TAG_VERSION = "Version";

    private static File getLegacyWifiSharedDirectory() {
        return new File(Environment.getDataMiscDirectory(), "wifi");
    }

    public static int convertWifiConfigBandToSoftApConfigBand(int wifiConfigBand) {
        switch (wifiConfigBand) {
            case -1:
                return 3;
            case 0:
                return 1;
            case 1:
                return 2;
            default:
                return 1;
        }
    }

    private static SoftApConfiguration loadFromLegacyFile(InputStream fis) {
        SoftApConfiguration.Builder configBuilder;
        DataInputStream in;
        int version;
        SoftApConfiguration config = null;
        DataInputStream in2 = null;
        try {
            try {
                try {
                    try {
                        configBuilder = new SoftApConfiguration.Builder();
                        in = new DataInputStream(new BufferedInputStream(fis));
                        version = in.readInt();
                    } catch (IllegalArgumentException ie) {
                        Log.m109e(TAG, "Invalid hotspot configuration ", ie);
                        config = null;
                        if (0 != 0) {
                            in2.close();
                        }
                    }
                } catch (IOException e) {
                    Log.m109e(TAG, "Error reading hotspot configuration ", e);
                    config = null;
                    if (0 != 0) {
                        in2.close();
                    }
                }
            } catch (IOException e2) {
                Log.m109e(TAG, "Error closing hotspot configuration during read", e2);
            }
            if (version >= 1 && version <= 3) {
                configBuilder.setSsid(in.readUTF());
                if (version >= 2) {
                    int band = in.readInt();
                    int channel = in.readInt();
                    if (channel == 0) {
                        configBuilder.setBand(convertWifiConfigBandToSoftApConfigBand(band));
                    } else {
                        configBuilder.setChannel(channel, convertWifiConfigBandToSoftApConfigBand(band));
                    }
                }
                if (version >= 3) {
                    configBuilder.setHiddenSsid(in.readBoolean());
                }
                int authType = in.readInt();
                if (authType == 4) {
                    configBuilder.setPassphrase(in.readUTF(), 1);
                }
                config = configBuilder.build();
                in.close();
                return config;
            }
            Log.m110e(TAG, "Bad version on hotspot configuration file");
            try {
                in.close();
                return null;
            } catch (IOException e3) {
                Log.m109e(TAG, "Error closing hotspot configuration during read", e3);
                return null;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    in2.close();
                } catch (IOException e4) {
                    Log.m109e(TAG, "Error closing hotspot configuration during read", e4);
                }
            }
            throw th;
        }
    }

    private static byte[] convertConfToXml(SoftApConfiguration softApConf) {
        try {
            XmlSerializer out = new FastXmlSerializer();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            out.setOutput(outputStream, StandardCharsets.UTF_8.name());
            out.startDocument(null, true);
            out.startTag(null, XML_TAG_DOCUMENT_HEADER);
            XmlUtils.writeValueXml((Object) 3, XML_TAG_VERSION, out);
            out.startTag(null, XML_TAG_SECTION_HEADER_SOFTAP);
            XmlUtils.writeValueXml(softApConf.getSsid(), XML_TAG_SSID, out);
            if (softApConf.getBssid() != null) {
                XmlUtils.writeValueXml(softApConf.getBssid().toString(), XML_TAG_BSSID, out);
            }
            XmlUtils.writeValueXml(Integer.valueOf(softApConf.getBand()), XML_TAG_AP_BAND, out);
            XmlUtils.writeValueXml(Integer.valueOf(softApConf.getChannel()), XML_TAG_CHANNEL, out);
            XmlUtils.writeValueXml(Boolean.valueOf(softApConf.isHiddenSsid()), XML_TAG_HIDDEN_SSID, out);
            XmlUtils.writeValueXml(Integer.valueOf(softApConf.getSecurityType()), XML_TAG_SECURITY_TYPE, out);
            if (softApConf.getSecurityType() != 0) {
                XmlUtils.writeValueXml(softApConf.getPassphrase(), XML_TAG_PASSPHRASE, out);
            }
            XmlUtils.writeValueXml(Integer.valueOf(softApConf.getMaxNumberOfClients()), XML_TAG_MAX_NUMBER_OF_CLIENTS, out);
            XmlUtils.writeValueXml(Boolean.valueOf(softApConf.isClientControlByUserEnabled()), XML_TAG_CLIENT_CONTROL_BY_USER, out);
            XmlUtils.writeValueXml(Boolean.valueOf(softApConf.isAutoShutdownEnabled()), XML_TAG_AUTO_SHUTDOWN_ENABLED, out);
            XmlUtils.writeValueXml(Long.valueOf(softApConf.getShutdownTimeoutMillis()), XML_TAG_SHUTDOWN_TIMEOUT_MILLIS, out);
            out.startTag(null, XML_TAG_BLOCKED_CLIENT_LIST);
            for (MacAddress mac : softApConf.getBlockedClientList()) {
                XmlUtils.writeValueXml(mac.toString(), XML_TAG_CLIENT_MACADDRESS, out);
            }
            out.endTag(null, XML_TAG_BLOCKED_CLIENT_LIST);
            out.startTag(null, XML_TAG_ALLOWED_CLIENT_LIST);
            for (MacAddress mac2 : softApConf.getAllowedClientList()) {
                XmlUtils.writeValueXml(mac2.toString(), XML_TAG_CLIENT_MACADDRESS, out);
            }
            out.endTag(null, XML_TAG_ALLOWED_CLIENT_LIST);
            out.endTag(null, XML_TAG_SECTION_HEADER_SOFTAP);
            out.endTag(null, XML_TAG_DOCUMENT_HEADER);
            out.endDocument();
            return outputStream.toByteArray();
        } catch (IOException | XmlPullParserException e) {
            Log.m109e(TAG, "Failed to convert softap conf to XML", e);
            return null;
        }
    }

    private SoftApConfToXmlMigrationUtil() {
    }

    public static InputStream convert(InputStream fis) {
        byte[] xmlBytes;
        SoftApConfiguration softApConf = loadFromLegacyFile(fis);
        if (softApConf == null || (xmlBytes = convertConfToXml(softApConf)) == null) {
            return null;
        }
        return new ByteArrayInputStream(xmlBytes);
    }

    public static InputStream convert() {
        File file = new File(getLegacyWifiSharedDirectory(), LEGACY_AP_CONFIG_FILE);
        try {
            FileInputStream fis = new FileInputStream(file);
            return convert(fis);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void remove() {
        File file = new File(getLegacyWifiSharedDirectory(), LEGACY_AP_CONFIG_FILE);
        file.delete();
    }
}
