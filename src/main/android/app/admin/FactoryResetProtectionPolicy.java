package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class FactoryResetProtectionPolicy implements Parcelable {
    private static final String ATTR_VALUE = "value";
    public static final Parcelable.Creator<FactoryResetProtectionPolicy> CREATOR = new Parcelable.Creator<FactoryResetProtectionPolicy>() { // from class: android.app.admin.FactoryResetProtectionPolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FactoryResetProtectionPolicy createFromParcel(Parcel in) {
            List<String> factoryResetProtectionAccounts = new ArrayList<>();
            int accountsCount = in.readInt();
            for (int i = 0; i < accountsCount; i++) {
                factoryResetProtectionAccounts.add(in.readString());
            }
            boolean factoryResetProtectionEnabled = in.readBoolean();
            return new FactoryResetProtectionPolicy(factoryResetProtectionAccounts, factoryResetProtectionEnabled);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FactoryResetProtectionPolicy[] newArray(int size) {
            return new FactoryResetProtectionPolicy[size];
        }
    };
    private static final String KEY_FACTORY_RESET_PROTECTION_ACCOUNT = "factory_reset_protection_account";
    private static final String KEY_FACTORY_RESET_PROTECTION_ENABLED = "factory_reset_protection_enabled";
    private static final String LOG_TAG = "FactoryResetProtectionPolicy";
    private final List<String> mFactoryResetProtectionAccounts;
    private final boolean mFactoryResetProtectionEnabled;

    private FactoryResetProtectionPolicy(List<String> factoryResetProtectionAccounts, boolean factoryResetProtectionEnabled) {
        this.mFactoryResetProtectionAccounts = factoryResetProtectionAccounts;
        this.mFactoryResetProtectionEnabled = factoryResetProtectionEnabled;
    }

    public List<String> getFactoryResetProtectionAccounts() {
        return this.mFactoryResetProtectionAccounts;
    }

    public boolean isFactoryResetProtectionEnabled() {
        return this.mFactoryResetProtectionEnabled;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private List<String> mFactoryResetProtectionAccounts;
        private boolean mFactoryResetProtectionEnabled = true;

        public Builder setFactoryResetProtectionAccounts(List<String> factoryResetProtectionAccounts) {
            this.mFactoryResetProtectionAccounts = new ArrayList(factoryResetProtectionAccounts);
            return this;
        }

        public Builder setFactoryResetProtectionEnabled(boolean factoryResetProtectionEnabled) {
            this.mFactoryResetProtectionEnabled = factoryResetProtectionEnabled;
            return this;
        }

        public FactoryResetProtectionPolicy build() {
            return new FactoryResetProtectionPolicy(this.mFactoryResetProtectionAccounts, this.mFactoryResetProtectionEnabled);
        }
    }

    public String toString() {
        return "FactoryResetProtectionPolicy{mFactoryResetProtectionAccounts=" + this.mFactoryResetProtectionAccounts + ", mFactoryResetProtectionEnabled=" + this.mFactoryResetProtectionEnabled + '}';
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int accountsCount = this.mFactoryResetProtectionAccounts.size();
        dest.writeInt(accountsCount);
        for (String account : this.mFactoryResetProtectionAccounts) {
            dest.writeString(account);
        }
        dest.writeBoolean(this.mFactoryResetProtectionEnabled);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static FactoryResetProtectionPolicy readFromXml(TypedXmlPullParser parser) {
        try {
            boolean factoryResetProtectionEnabled = parser.getAttributeBoolean(null, KEY_FACTORY_RESET_PROTECTION_ENABLED, false);
            List<String> factoryResetProtectionAccounts = new ArrayList<>();
            int outerDepth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                    break;
                } else if (type != 3 && type != 4 && parser.getName().equals(KEY_FACTORY_RESET_PROTECTION_ACCOUNT)) {
                    factoryResetProtectionAccounts.add(parser.getAttributeValue(null, "value"));
                }
            }
            return new FactoryResetProtectionPolicy(factoryResetProtectionAccounts, factoryResetProtectionEnabled);
        } catch (IOException | XmlPullParserException e) {
            Log.m103w(LOG_TAG, "Reading from xml failed", e);
            return null;
        }
    }

    public void writeToXml(TypedXmlSerializer out) throws IOException {
        out.attributeBoolean(null, KEY_FACTORY_RESET_PROTECTION_ENABLED, this.mFactoryResetProtectionEnabled);
        for (String account : this.mFactoryResetProtectionAccounts) {
            out.startTag(null, KEY_FACTORY_RESET_PROTECTION_ACCOUNT);
            out.attribute(null, "value", account);
            out.endTag(null, KEY_FACTORY_RESET_PROTECTION_ACCOUNT);
        }
    }

    public boolean isNotEmpty() {
        return !this.mFactoryResetProtectionAccounts.isEmpty() && this.mFactoryResetProtectionEnabled;
    }

    public void dump(IndentingPrintWriter pw) {
        pw.print("factoryResetProtectionEnabled=");
        pw.println(this.mFactoryResetProtectionEnabled);
        pw.print("factoryResetProtectionAccounts=");
        pw.increaseIndent();
        for (int i = 0; i < this.mFactoryResetProtectionAccounts.size(); i++) {
            pw.println(this.mFactoryResetProtectionAccounts.get(i));
        }
        pw.decreaseIndent();
    }
}
