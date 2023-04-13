package android.security.net.config;

import android.media.p007tv.interactive.TvInteractiveAppView;
import java.security.cert.X509Certificate;
/* loaded from: classes3.dex */
public final class TrustAnchor {
    public final X509Certificate certificate;
    public final boolean overridesPins;

    public TrustAnchor(X509Certificate certificate, boolean overridesPins) {
        if (certificate == null) {
            throw new NullPointerException(TvInteractiveAppView.BI_INTERACTIVE_APP_KEY_CERTIFICATE);
        }
        this.certificate = certificate;
        this.overridesPins = overridesPins;
    }
}
