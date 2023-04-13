package android.security.net.config;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
/* loaded from: classes3.dex */
public class RootTrustManagerFactorySpi extends TrustManagerFactorySpi {
    private ApplicationConfig mApplicationConfig;
    private NetworkSecurityConfig mConfig;

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public void engineInit(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
        if (!(spec instanceof ApplicationConfigParameters)) {
            throw new InvalidAlgorithmParameterException("Unsupported spec: " + spec + ". Only " + ApplicationConfigParameters.class.getName() + " supported");
        }
        this.mApplicationConfig = ((ApplicationConfigParameters) spec).config;
    }

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public void engineInit(KeyStore ks) throws KeyStoreException {
        if (ks != null) {
            this.mApplicationConfig = new ApplicationConfig(new KeyStoreConfigSource(ks));
        } else {
            this.mApplicationConfig = ApplicationConfig.getDefaultInstance();
        }
    }

    @Override // javax.net.ssl.TrustManagerFactorySpi
    public TrustManager[] engineGetTrustManagers() {
        ApplicationConfig applicationConfig = this.mApplicationConfig;
        if (applicationConfig != null) {
            return new TrustManager[]{applicationConfig.getTrustManager()};
        }
        throw new IllegalStateException("TrustManagerFactory not initialized");
    }

    /* loaded from: classes3.dex */
    public static final class ApplicationConfigParameters implements ManagerFactoryParameters {
        public final ApplicationConfig config;

        public ApplicationConfigParameters(ApplicationConfig config) {
            this.config = config;
        }
    }
}
