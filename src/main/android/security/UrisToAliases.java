package android.security;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public final class UrisToAliases implements Parcelable {
    public static final Parcelable.Creator<UrisToAliases> CREATOR = new Parcelable.Creator<UrisToAliases>() { // from class: android.security.UrisToAliases.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UrisToAliases createFromParcel(Parcel in) {
            Map<Uri, String> urisToAliases = new HashMap<>();
            in.readMap(urisToAliases, String.class.getClassLoader());
            return new UrisToAliases(urisToAliases);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UrisToAliases[] newArray(int size) {
            return new UrisToAliases[size];
        }
    };
    private static final String KEY_AUTHENTICATION_POLICY_ALIAS = "policy_alias";
    private static final String KEY_AUTHENTICATION_POLICY_URI = "policy_uri";
    private static final String KEY_AUTHENTICATION_POLICY_URI_TO_ALIAS = "authentication_policy_uri_to_alias";
    private final Map<Uri, String> mUrisToAliases;

    public UrisToAliases() {
        this.mUrisToAliases = new HashMap();
    }

    private UrisToAliases(Map<Uri, String> urisToAliases) {
        this.mUrisToAliases = urisToAliases;
    }

    public Map<Uri, String> getUrisToAliases() {
        return Collections.unmodifiableMap(this.mUrisToAliases);
    }

    public void addUriToAlias(Uri uri, String alias) {
        this.mUrisToAliases.put(uri, alias);
    }

    public static UrisToAliases readFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        Map<Uri, String> urisToAliases = new HashMap<>();
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type != 3 && type != 4 && parser.getName().equals(KEY_AUTHENTICATION_POLICY_URI_TO_ALIAS)) {
                Uri uri = Uri.parse(parser.getAttributeValue(null, KEY_AUTHENTICATION_POLICY_URI));
                String alias = parser.getAttributeValue(null, KEY_AUTHENTICATION_POLICY_ALIAS);
                urisToAliases.put(uri, alias);
            }
        }
        return new UrisToAliases(urisToAliases);
    }

    public void writeToXml(XmlSerializer out) throws IOException {
        for (Map.Entry<Uri, String> urisToAliases : this.mUrisToAliases.entrySet()) {
            out.startTag(null, KEY_AUTHENTICATION_POLICY_URI_TO_ALIAS);
            out.attribute(null, KEY_AUTHENTICATION_POLICY_URI, urisToAliases.getKey().toString());
            out.attribute(null, KEY_AUTHENTICATION_POLICY_ALIAS, urisToAliases.getValue());
            out.endTag(null, KEY_AUTHENTICATION_POLICY_URI_TO_ALIAS);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(this.mUrisToAliases);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UrisToAliases)) {
            return false;
        }
        UrisToAliases other = (UrisToAliases) obj;
        return Objects.equals(this.mUrisToAliases, other.mUrisToAliases);
    }

    public int hashCode() {
        return this.mUrisToAliases.hashCode();
    }
}
