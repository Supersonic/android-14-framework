package android.provider;

import android.provider.DeviceConfig;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public interface DeviceConfigInterface {
    public static final DeviceConfigInterface REAL = new DeviceConfigInterface() { // from class: android.provider.DeviceConfigInterface.1
        @Override // android.provider.DeviceConfigInterface
        public String getProperty(String namespace, String name) {
            return DeviceConfig.getProperty(namespace, name);
        }

        @Override // android.provider.DeviceConfigInterface
        public DeviceConfig.Properties getProperties(String namespace, String... names) {
            return DeviceConfig.getProperties(namespace, names);
        }

        @Override // android.provider.DeviceConfigInterface
        public boolean setProperty(String namespace, String name, String value, boolean makeDefault) {
            return DeviceConfig.setProperty(namespace, name, value, makeDefault);
        }

        @Override // android.provider.DeviceConfigInterface
        public boolean setProperties(DeviceConfig.Properties properties) throws DeviceConfig.BadConfigException {
            return DeviceConfig.setProperties(properties);
        }

        @Override // android.provider.DeviceConfigInterface
        public boolean deleteProperty(String namespace, String name) {
            return DeviceConfig.deleteProperty(namespace, name);
        }

        @Override // android.provider.DeviceConfigInterface
        public void resetToDefaults(int resetMode, String namespace) {
            DeviceConfig.resetToDefaults(resetMode, namespace);
        }

        @Override // android.provider.DeviceConfigInterface
        public String getString(String namespace, String name, String defaultValue) {
            return DeviceConfig.getString(namespace, name, defaultValue);
        }

        @Override // android.provider.DeviceConfigInterface
        public int getInt(String namespace, String name, int defaultValue) {
            return DeviceConfig.getInt(namespace, name, defaultValue);
        }

        @Override // android.provider.DeviceConfigInterface
        public long getLong(String namespace, String name, long defaultValue) {
            return DeviceConfig.getLong(namespace, name, defaultValue);
        }

        @Override // android.provider.DeviceConfigInterface
        public boolean getBoolean(String namespace, String name, boolean defaultValue) {
            return DeviceConfig.getBoolean(namespace, name, defaultValue);
        }

        @Override // android.provider.DeviceConfigInterface
        public float getFloat(String namespace, String name, float defaultValue) {
            return DeviceConfig.getFloat(namespace, name, defaultValue);
        }

        @Override // android.provider.DeviceConfigInterface
        public void addOnPropertiesChangedListener(String namespace, Executor executor, DeviceConfig.OnPropertiesChangedListener listener) {
            DeviceConfig.addOnPropertiesChangedListener(namespace, executor, listener);
        }

        @Override // android.provider.DeviceConfigInterface
        public void removeOnPropertiesChangedListener(DeviceConfig.OnPropertiesChangedListener listener) {
            DeviceConfig.removeOnPropertiesChangedListener(listener);
        }
    };

    void addOnPropertiesChangedListener(String str, Executor executor, DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener);

    boolean deleteProperty(String str, String str2);

    boolean getBoolean(String str, String str2, boolean z);

    float getFloat(String str, String str2, float f);

    int getInt(String str, String str2, int i);

    long getLong(String str, String str2, long j);

    DeviceConfig.Properties getProperties(String str, String... strArr);

    String getProperty(String str, String str2);

    String getString(String str, String str2, String str3);

    void removeOnPropertiesChangedListener(DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener);

    void resetToDefaults(int i, String str);

    boolean setProperties(DeviceConfig.Properties properties) throws DeviceConfig.BadConfigException;

    boolean setProperty(String str, String str2, String str3, boolean z);
}
