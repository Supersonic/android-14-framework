package android.app.admin;

import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaMetrics;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.p008os.Bundle;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
/* loaded from: classes.dex */
final class ProvisioningIntentHelper {
    private static final Map<String, Class> EXTRAS_TO_CLASS_MAP = createExtrasToClassMap();
    private static final String TAG = "ProvisioningIntentHelper";

    private ProvisioningIntentHelper() {
    }

    public static Intent createProvisioningIntentFromNfcIntent(Intent nfcIntent) {
        Objects.requireNonNull(nfcIntent);
        if (!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())) {
            Log.m110e(TAG, "Wrong Nfc action: " + nfcIntent.getAction());
            return null;
        }
        NdefRecord firstRecord = getFirstNdefRecord(nfcIntent);
        if (firstRecord != null) {
            return createProvisioningIntentFromNdefRecord(firstRecord);
        }
        return null;
    }

    private static Intent createProvisioningIntentFromNdefRecord(NdefRecord firstRecord) {
        Objects.requireNonNull(firstRecord);
        Properties properties = loadPropertiesFromPayload(firstRecord.getPayload());
        if (properties == null) {
            Log.m110e(TAG, "Failed to load NdefRecord properties.");
            return null;
        }
        Bundle bundle = createBundleFromProperties(properties);
        if (!containsRequiredProvisioningExtras(bundle)) {
            Log.m110e(TAG, "Bundle does not contain the required provisioning extras.");
            return null;
        }
        return createProvisioningIntentFromBundle(bundle);
    }

    private static Properties loadPropertiesFromPayload(byte[] payload) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(new String(payload, StandardCharsets.UTF_8)));
            return properties;
        } catch (IOException e) {
            Log.m110e(TAG, "NFC Intent properties loading failed.");
            return null;
        }
    }

    private static Bundle createBundleFromProperties(Properties properties) {
        Enumeration propertyNames = properties.propertyNames();
        Bundle bundle = new Bundle();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            addPropertyToBundle(propertyName, properties, bundle);
        }
        return bundle;
    }

    private static void addPropertyToBundle(String propertyName, Properties properties, Bundle bundle) {
        Map<String, Class> map = EXTRAS_TO_CLASS_MAP;
        if (map.get(propertyName) == ComponentName.class) {
            ComponentName componentName = ComponentName.unflattenFromString(properties.getProperty(propertyName));
            bundle.putParcelable(propertyName, componentName);
        } else if (map.get(propertyName) == PersistableBundle.class) {
            try {
                bundle.putParcelable(propertyName, deserializeExtrasBundle(properties, propertyName));
            } catch (IOException e) {
                Log.m109e(TAG, "Failed to parse " + propertyName + MediaMetrics.SEPARATOR, e);
            }
        } else if (map.get(propertyName) == Boolean.class) {
            bundle.putBoolean(propertyName, Boolean.parseBoolean(properties.getProperty(propertyName)));
        } else if (map.get(propertyName) == Long.class) {
            bundle.putLong(propertyName, Long.parseLong(properties.getProperty(propertyName)));
        } else if (map.get(propertyName) == Integer.class) {
            bundle.putInt(propertyName, Integer.parseInt(properties.getProperty(propertyName)));
        } else {
            bundle.putString(propertyName, properties.getProperty(propertyName));
        }
    }

    private static PersistableBundle deserializeExtrasBundle(Properties properties, String extraName) throws IOException {
        String serializedExtras = properties.getProperty(extraName);
        if (serializedExtras == null) {
            return null;
        }
        Properties bundleProperties = new Properties();
        bundleProperties.load(new StringReader(serializedExtras));
        PersistableBundle extrasBundle = new PersistableBundle(bundleProperties.size());
        Set<String> propertyNames = bundleProperties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            extrasBundle.putString(propertyName, bundleProperties.getProperty(propertyName));
        }
        return extrasBundle;
    }

    private static Intent createProvisioningIntentFromBundle(Bundle bundle) {
        Objects.requireNonNull(bundle);
        Intent provisioningIntent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE);
        provisioningIntent.putExtras(bundle);
        provisioningIntent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_TRIGGER, 5);
        return provisioningIntent;
    }

    private static boolean containsRequiredProvisioningExtras(Bundle bundle) {
        return bundle.containsKey(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME) || bundle.containsKey(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME);
    }

    private static NdefRecord getFirstNdefRecord(Intent nfcIntent) {
        Parcelable[] ndefMessages = nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (ndefMessages == null) {
            Log.m108i(TAG, "No EXTRA_NDEF_MESSAGES from nfcIntent");
            return null;
        }
        for (Parcelable rawMsg : ndefMessages) {
            NdefMessage msg = (NdefMessage) rawMsg;
            NdefRecord[] records = msg.getRecords();
            if (records.length > 0) {
                NdefRecord record = records[0];
                String mimeType = new String(record.getType(), StandardCharsets.UTF_8);
                if (DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC.equals(mimeType)) {
                    return record;
                }
            }
        }
        Log.m108i(TAG, "No compatible records found on nfcIntent");
        return null;
    }

    private static Map<String, Class> createExtrasToClassMap() {
        Map<String, Class> map = new HashMap<>();
        for (String extra : getBooleanExtras()) {
            map.put(extra, Boolean.class);
        }
        for (String extra2 : getLongExtras()) {
            map.put(extra2, Long.class);
        }
        for (String extra3 : getIntExtras()) {
            map.put(extra3, Integer.class);
        }
        for (String extra4 : getComponentNameExtras()) {
            map.put(extra4, ComponentName.class);
        }
        for (String extra5 : getPersistableBundleExtras()) {
            map.put(extra5, PersistableBundle.class);
        }
        return map;
    }

    private static Set<String> getPersistableBundleExtras() {
        return Set.of(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE, DevicePolicyManager.EXTRA_PROVISIONING_ROLE_HOLDER_EXTRAS_BUNDLE);
    }

    private static Set<String> getComponentNameExtras() {
        return Set.of(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME);
    }

    private static Set<String> getIntExtras() {
        return Set.of(DevicePolicyManager.EXTRA_PROVISIONING_WIFI_PROXY_PORT, DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE, DevicePolicyManager.EXTRA_PROVISIONING_SUPPORTED_MODES);
    }

    private static Set<String> getLongExtras() {
        return Set.of(DevicePolicyManager.EXTRA_PROVISIONING_LOCAL_TIME);
    }

    private static Set<String> getBooleanExtras() {
        return Set.of((Object[]) new String[]{DevicePolicyManager.EXTRA_PROVISIONING_ALLOW_OFFLINE, DevicePolicyManager.EXTRA_PROVISIONING_SHOULD_LAUNCH_RESULT_INTENT, DevicePolicyManager.EXTRA_PROVISIONING_KEEP_ACCOUNT_ON_MIGRATION, DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, DevicePolicyManager.EXTRA_PROVISIONING_WIFI_HIDDEN, DevicePolicyManager.EXTRA_PROVISIONING_SENSORS_PERMISSION_GRANT_OPT_OUT, DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION, DevicePolicyManager.EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS, DevicePolicyManager.EXTRA_PROVISIONING_USE_MOBILE_DATA, DevicePolicyManager.EXTRA_PROVISIONING_SKIP_OWNERSHIP_DISCLAIMER, DevicePolicyManager.EXTRA_PROVISIONING_RETURN_BEFORE_POLICY_COMPLIANCE, DevicePolicyManager.EXTRA_PROVISIONING_KEEP_SCREEN_ON});
    }
}
