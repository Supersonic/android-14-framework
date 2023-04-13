package android.security.net.config;

import android.content.Context;
import android.media.MediaMetrics;
import android.util.Log;
import java.security.Provider;
import java.security.Security;
import libcore.net.NetworkSecurityPolicy;
/* loaded from: classes3.dex */
public final class NetworkSecurityConfigProvider extends Provider {
    private static final String LOG_TAG = "nsconfig";
    private static final String PREFIX = NetworkSecurityConfigProvider.class.getPackage().getName() + MediaMetrics.SEPARATOR;

    public NetworkSecurityConfigProvider() {
        super("AndroidNSSP", 1.0d, "Android Network Security Policy Provider");
        put("TrustManagerFactory.PKIX", PREFIX + "RootTrustManagerFactorySpi");
        put("Alg.Alias.TrustManagerFactory.X509", "PKIX");
    }

    public static void install(Context context) {
        ApplicationConfig config = new ApplicationConfig(new ManifestConfigSource(context));
        ApplicationConfig.setDefaultInstance(config);
        int pos = Security.insertProviderAt(new NetworkSecurityConfigProvider(), 1);
        if (pos != 1) {
            throw new RuntimeException("Failed to install provider as highest priority provider. Provider was installed at position " + pos);
        }
        NetworkSecurityPolicy.setInstance(new ConfigNetworkSecurityPolicy(config));
    }

    public static void handleNewApplication(Context context) {
        ApplicationConfig config = new ApplicationConfig(new ManifestConfigSource(context));
        ApplicationConfig defaultConfig = ApplicationConfig.getDefaultInstance();
        String mProcessName = context.getApplicationInfo().processName;
        if (defaultConfig != null && defaultConfig.isCleartextTrafficPermitted() != config.isCleartextTrafficPermitted()) {
            Log.m104w(LOG_TAG, mProcessName + ": New config does not match the previously set config.");
            if (defaultConfig.hasPerDomainConfigs() || config.hasPerDomainConfigs()) {
                throw new RuntimeException("Found multiple conflicting per-domain rules");
            }
            config = defaultConfig.isCleartextTrafficPermitted() ? defaultConfig : config;
        }
        ApplicationConfig.setDefaultInstance(config);
    }
}
