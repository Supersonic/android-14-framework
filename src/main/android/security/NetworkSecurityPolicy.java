package android.security;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.security.net.config.ApplicationConfig;
import android.security.net.config.ManifestConfigSource;
/* loaded from: classes3.dex */
public class NetworkSecurityPolicy {
    private static final NetworkSecurityPolicy INSTANCE = new NetworkSecurityPolicy();

    private NetworkSecurityPolicy() {
    }

    public static NetworkSecurityPolicy getInstance() {
        return INSTANCE;
    }

    public boolean isCleartextTrafficPermitted() {
        return libcore.net.NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted();
    }

    public boolean isCleartextTrafficPermitted(String hostname) {
        return libcore.net.NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(hostname);
    }

    public void setCleartextTrafficPermitted(boolean permitted) {
        FrameworkNetworkSecurityPolicy policy = new FrameworkNetworkSecurityPolicy(permitted);
        libcore.net.NetworkSecurityPolicy.setInstance(policy);
    }

    public void handleTrustStorageUpdate() {
        ApplicationConfig config = ApplicationConfig.getDefaultInstance();
        if (config != null) {
            config.handleTrustStorageUpdate();
        }
    }

    public static ApplicationConfig getApplicationConfigForPackage(Context context, String packageName) throws PackageManager.NameNotFoundException {
        Context appContext = context.createPackageContext(packageName, 0);
        ManifestConfigSource source = new ManifestConfigSource(appContext);
        return new ApplicationConfig(source);
    }
}
