package android.security;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public final class AppUriAuthenticationPolicy implements Parcelable {
    public static final Parcelable.Creator<AppUriAuthenticationPolicy> CREATOR = new Parcelable.Creator<AppUriAuthenticationPolicy>() { // from class: android.security.AppUriAuthenticationPolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppUriAuthenticationPolicy createFromParcel(Parcel in) {
            Map<String, UrisToAliases> appToUris = new HashMap<>();
            in.readMap(appToUris, UrisToAliases.class.getClassLoader());
            return new AppUriAuthenticationPolicy(appToUris);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppUriAuthenticationPolicy[] newArray(int size) {
            return new AppUriAuthenticationPolicy[size];
        }
    };
    private static final String KEY_AUTHENTICATION_POLICY_APP = "policy_app";
    private static final String KEY_AUTHENTICATION_POLICY_APP_TO_URIS = "authentication_policy_app_to_uris";
    private final Map<String, UrisToAliases> mAppToUris;

    private AppUriAuthenticationPolicy(Map<String, UrisToAliases> appToUris) {
        Objects.requireNonNull(appToUris);
        this.mAppToUris = appToUris;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private Map<String, UrisToAliases> mPackageNameToUris = new HashMap();

        public Builder addAppAndUriMapping(String appPackageName, Uri uri, String alias) {
            Objects.requireNonNull(appPackageName);
            Objects.requireNonNull(uri);
            Objects.requireNonNull(alias);
            UrisToAliases urisToAliases = this.mPackageNameToUris.getOrDefault(appPackageName, new UrisToAliases());
            urisToAliases.addUriToAlias(uri, alias);
            this.mPackageNameToUris.put(appPackageName, urisToAliases);
            return this;
        }

        public Builder addAppAndUriMapping(String appPackageName, UrisToAliases urisToAliases) {
            Objects.requireNonNull(appPackageName);
            Objects.requireNonNull(urisToAliases);
            this.mPackageNameToUris.put(appPackageName, urisToAliases);
            return this;
        }

        public AppUriAuthenticationPolicy build() {
            return new AppUriAuthenticationPolicy(this.mPackageNameToUris);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(this.mAppToUris);
    }

    public String toString() {
        return "AppUriAuthenticationPolicy{mPackageNameToUris=" + this.mAppToUris + '}';
    }

    public Map<String, Map<Uri, String>> getAppAndUriMappings() {
        Map<String, Map<Uri, String>> appAndUris = new HashMap<>();
        for (Map.Entry<String, UrisToAliases> entry : this.mAppToUris.entrySet()) {
            appAndUris.put(entry.getKey(), entry.getValue().getUrisToAliases());
        }
        return appAndUris;
    }

    public static AppUriAuthenticationPolicy readFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        Builder builder = new Builder();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4 && parser.getName().equals(KEY_AUTHENTICATION_POLICY_APP_TO_URIS)) {
                String app = parser.getAttributeValue(null, KEY_AUTHENTICATION_POLICY_APP);
                UrisToAliases urisToAliases = UrisToAliases.readFromXml(parser);
                builder.addAppAndUriMapping(app, urisToAliases);
            }
        }
        return builder.build();
    }

    public void writeToXml(XmlSerializer out) throws IOException {
        for (Map.Entry<String, UrisToAliases> appsToUris : this.mAppToUris.entrySet()) {
            out.startTag(null, KEY_AUTHENTICATION_POLICY_APP_TO_URIS);
            out.attribute(null, KEY_AUTHENTICATION_POLICY_APP, appsToUris.getKey());
            appsToUris.getValue().writeToXml(out);
            out.endTag(null, KEY_AUTHENTICATION_POLICY_APP_TO_URIS);
        }
    }

    public Set<String> getAliases() {
        Set<String> aliases = new HashSet<>();
        for (UrisToAliases appsToUris : this.mAppToUris.values()) {
            aliases.addAll(appsToUris.getUrisToAliases().values());
        }
        return aliases;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AppUriAuthenticationPolicy)) {
            return false;
        }
        AppUriAuthenticationPolicy other = (AppUriAuthenticationPolicy) obj;
        return Objects.equals(this.mAppToUris, other.mAppToUris);
    }

    public int hashCode() {
        return this.mAppToUris.hashCode();
    }
}
