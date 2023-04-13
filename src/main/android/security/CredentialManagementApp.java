package android.security;

import android.util.Log;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public class CredentialManagementApp {
    private static final String KEY_PACKAGE_NAME = "package_name";
    private static final String TAG = "CredentialManagementApp";
    private AppUriAuthenticationPolicy mAuthenticationPolicy;
    private final String mPackageName;

    public CredentialManagementApp(String packageName, AppUriAuthenticationPolicy authenticationPolicy) {
        Objects.requireNonNull(packageName);
        Objects.requireNonNull(authenticationPolicy);
        this.mPackageName = packageName;
        this.mAuthenticationPolicy = authenticationPolicy;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public AppUriAuthenticationPolicy getAuthenticationPolicy() {
        return this.mAuthenticationPolicy;
    }

    public void setAuthenticationPolicy(AppUriAuthenticationPolicy authenticationPolicy) {
        Objects.requireNonNull(authenticationPolicy);
        this.mAuthenticationPolicy = authenticationPolicy;
    }

    public static CredentialManagementApp readFromXml(XmlPullParser parser) {
        try {
            String packageName = parser.getAttributeValue(null, "package_name");
            AppUriAuthenticationPolicy policy = AppUriAuthenticationPolicy.readFromXml(parser);
            return new CredentialManagementApp(packageName, policy);
        } catch (IOException | XmlPullParserException e) {
            Log.m103w(TAG, "Reading from xml failed", e);
            return null;
        }
    }

    public void writeToXml(XmlSerializer out) throws IOException {
        out.attribute(null, "package_name", this.mPackageName);
        AppUriAuthenticationPolicy appUriAuthenticationPolicy = this.mAuthenticationPolicy;
        if (appUriAuthenticationPolicy != null) {
            appUriAuthenticationPolicy.writeToXml(out);
        }
    }
}
