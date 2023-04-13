package android.app.admin;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class PreferentialNetworkServiceConfig implements Parcelable {
    private static final String ATTR_VALUE = "value";
    private static final String LOG_TAG = "PreferentialNetworkServiceConfig";
    public static final int PREFERENTIAL_NETWORK_ID_1 = 1;
    public static final int PREFERENTIAL_NETWORK_ID_2 = 2;
    public static final int PREFERENTIAL_NETWORK_ID_3 = 3;
    public static final int PREFERENTIAL_NETWORK_ID_4 = 4;
    public static final int PREFERENTIAL_NETWORK_ID_5 = 5;
    private static final String TAG_ALLOW_FALLBACK_TO_DEFAULT_CONNECTION = "allow_fallback_to_default_connection";
    private static final String TAG_BLOCK_NON_MATCHING_NETWORKS = "block_non_matching_networks";
    private static final String TAG_CONFIG_ENABLED = "preferential_network_service_config_enabled";
    private static final String TAG_EXCLUDED_UIDS = "excluded_uids";
    private static final String TAG_INCLUDED_UIDS = "included_uids";
    private static final String TAG_NETWORK_ID = "preferential_network_service_network_id";
    private static final String TAG_PREFERENTIAL_NETWORK_SERVICE_CONFIG = "preferential_network_service_config";
    private static final String TAG_UID = "uid";
    final boolean mAllowFallbackToDefaultConnection;
    final int[] mExcludedUids;
    final int[] mIncludedUids;
    final boolean mIsEnabled;
    final int mNetworkId;
    final boolean mShouldBlockNonMatchingNetworks;
    public static final PreferentialNetworkServiceConfig DEFAULT = new Builder().build();
    public static final Parcelable.Creator<PreferentialNetworkServiceConfig> CREATOR = new Parcelable.Creator<PreferentialNetworkServiceConfig>() { // from class: android.app.admin.PreferentialNetworkServiceConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PreferentialNetworkServiceConfig[] newArray(int size) {
            return new PreferentialNetworkServiceConfig[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PreferentialNetworkServiceConfig createFromParcel(Parcel in) {
            return new PreferentialNetworkServiceConfig(in);
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PreferentialNetworkPreferenceId {
    }

    private PreferentialNetworkServiceConfig(boolean isEnabled, boolean allowFallbackToDefaultConnection, boolean shouldBlockNonMatchingNetworks, int[] includedUids, int[] excludedUids, int networkId) {
        this.mIsEnabled = isEnabled;
        this.mAllowFallbackToDefaultConnection = allowFallbackToDefaultConnection;
        this.mShouldBlockNonMatchingNetworks = shouldBlockNonMatchingNetworks;
        this.mIncludedUids = includedUids;
        this.mExcludedUids = excludedUids;
        this.mNetworkId = networkId;
    }

    private PreferentialNetworkServiceConfig(Parcel in) {
        this.mIsEnabled = in.readBoolean();
        this.mAllowFallbackToDefaultConnection = in.readBoolean();
        this.mShouldBlockNonMatchingNetworks = in.readBoolean();
        this.mNetworkId = in.readInt();
        this.mIncludedUids = in.createIntArray();
        this.mExcludedUids = in.createIntArray();
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public boolean isFallbackToDefaultConnectionAllowed() {
        return this.mAllowFallbackToDefaultConnection;
    }

    public boolean shouldBlockNonMatchingNetworks() {
        return this.mShouldBlockNonMatchingNetworks;
    }

    public int[] getIncludedUids() {
        return this.mIncludedUids;
    }

    public int[] getExcludedUids() {
        return this.mExcludedUids;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public String toString() {
        return "PreferentialNetworkServiceConfig{mIsEnabled=" + isEnabled() + "mAllowFallbackToDefaultConnection=" + isFallbackToDefaultConnectionAllowed() + "mBlockNonMatchingNetworks=" + shouldBlockNonMatchingNetworks() + "mIncludedUids=" + Arrays.toString(this.mIncludedUids) + "mExcludedUids=" + Arrays.toString(this.mExcludedUids) + "mNetworkId=" + this.mNetworkId + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreferentialNetworkServiceConfig that = (PreferentialNetworkServiceConfig) o;
        if (this.mIsEnabled == that.mIsEnabled && this.mAllowFallbackToDefaultConnection == that.mAllowFallbackToDefaultConnection && this.mShouldBlockNonMatchingNetworks == that.mShouldBlockNonMatchingNetworks && this.mNetworkId == that.mNetworkId && Arrays.equals(this.mIncludedUids, that.mIncludedUids) && Arrays.equals(this.mExcludedUids, that.mExcludedUids)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mIsEnabled), Boolean.valueOf(this.mAllowFallbackToDefaultConnection), Boolean.valueOf(this.mShouldBlockNonMatchingNetworks), Integer.valueOf(Arrays.hashCode(this.mIncludedUids)), Integer.valueOf(Arrays.hashCode(this.mExcludedUids)), Integer.valueOf(this.mNetworkId));
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        boolean mIsEnabled = false;
        int mNetworkId = 0;
        boolean mAllowFallbackToDefaultConnection = true;
        boolean mShouldBlockNonMatchingNetworks = false;
        int[] mIncludedUids = new int[0];
        int[] mExcludedUids = new int[0];

        public Builder setEnabled(boolean isEnabled) {
            this.mIsEnabled = isEnabled;
            return this;
        }

        public Builder setFallbackToDefaultConnectionAllowed(boolean allowFallbackToDefaultConnection) {
            this.mAllowFallbackToDefaultConnection = allowFallbackToDefaultConnection;
            return this;
        }

        public Builder setShouldBlockNonMatchingNetworks(boolean blockNonMatchingNetworks) {
            this.mShouldBlockNonMatchingNetworks = blockNonMatchingNetworks;
            return this;
        }

        public Builder setIncludedUids(int[] uids) {
            Objects.requireNonNull(uids);
            this.mIncludedUids = uids;
            return this;
        }

        public Builder setExcludedUids(int[] uids) {
            Objects.requireNonNull(uids);
            this.mExcludedUids = uids;
            return this;
        }

        public PreferentialNetworkServiceConfig build() {
            if (this.mIncludedUids.length > 0 && this.mExcludedUids.length > 0) {
                throw new IllegalStateException("Both includedUids and excludedUids cannot be nonempty");
            }
            if (this.mShouldBlockNonMatchingNetworks && this.mAllowFallbackToDefaultConnection) {
                throw new IllegalStateException("A config cannot both allow fallback and block non-matching networks");
            }
            return new PreferentialNetworkServiceConfig(this.mIsEnabled, this.mAllowFallbackToDefaultConnection, this.mShouldBlockNonMatchingNetworks, this.mIncludedUids, this.mExcludedUids, this.mNetworkId);
        }

        public Builder setNetworkId(int preferenceId) {
            if (preferenceId < 1 || preferenceId > 5) {
                throw new IllegalArgumentException("Invalid preference identifier");
            }
            this.mNetworkId = preferenceId;
            return this;
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mIsEnabled);
        dest.writeBoolean(this.mAllowFallbackToDefaultConnection);
        dest.writeBoolean(this.mShouldBlockNonMatchingNetworks);
        dest.writeInt(this.mNetworkId);
        dest.writeIntArray(this.mIncludedUids);
        dest.writeIntArray(this.mExcludedUids);
    }

    private void writeAttributeValueToXml(TypedXmlSerializer out, String tag, int value) throws IOException {
        out.startTag(null, tag);
        out.attributeInt(null, "value", value);
        out.endTag(null, tag);
    }

    private void writeAttributeValueToXml(TypedXmlSerializer out, String tag, boolean value) throws IOException {
        out.startTag(null, tag);
        out.attributeBoolean(null, "value", value);
        out.endTag(null, tag);
    }

    private void writeAttributeValuesToXml(TypedXmlSerializer out, String outerTag, String innerTag, Collection<String> values) throws IOException {
        out.startTag(null, outerTag);
        for (String value : values) {
            out.startTag(null, innerTag);
            out.attribute(null, "value", value);
            out.endTag(null, innerTag);
        }
        out.endTag(null, outerTag);
    }

    private static void readAttributeValues(TypedXmlPullParser parser, String tag, Collection<String> result) throws XmlPullParserException, IOException {
        result.clear();
        int outerDepthDAM = parser.getDepth();
        while (true) {
            int typeDAM = parser.next();
            if (typeDAM != 1) {
                if (typeDAM != 3 || parser.getDepth() > outerDepthDAM) {
                    if (typeDAM != 3 && typeDAM != 4) {
                        String tagDAM = parser.getName();
                        if (tag.equals(tagDAM)) {
                            result.add(parser.getAttributeValue(null, "value"));
                        } else {
                            Log.m110e(LOG_TAG, "Expected tag " + tag + " but found " + tagDAM);
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private List<String> intArrayToStringList(int[] array) {
        return (List) Arrays.stream(array).mapToObj(new IntFunction() { // from class: android.app.admin.PreferentialNetworkServiceConfig$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return String.valueOf(i);
            }
        }).collect(Collectors.toList());
    }

    private static int[] readStringListToIntArray(TypedXmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        List<String> stringList = new ArrayList<>();
        readAttributeValues(parser, tag, stringList);
        int[] intArray = stringList.stream().map(new Function() { // from class: android.app.admin.PreferentialNetworkServiceConfig$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer valueOf;
                valueOf = Integer.valueOf(Integer.parseInt((String) obj));
                return valueOf;
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).toArray();
        return intArray;
    }

    public static PreferentialNetworkServiceConfig getPreferentialNetworkServiceConfig(TypedXmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        int outerDepthDAM = parser.getDepth();
        Builder resultBuilder = new Builder();
        while (true) {
            int typeDAM = parser.next();
            if (typeDAM == 1 || (typeDAM == 3 && parser.getDepth() <= outerDepthDAM)) {
                break;
            } else if (typeDAM != 3 && typeDAM != 4) {
                String tagDAM = parser.getName();
                if (TAG_CONFIG_ENABLED.equals(tagDAM)) {
                    resultBuilder.setEnabled(parser.getAttributeBoolean(null, "value", false));
                } else if (TAG_NETWORK_ID.equals(tagDAM)) {
                    int val = parser.getAttributeInt(null, "value", 0);
                    if (val != 0) {
                        resultBuilder.setNetworkId(val);
                    }
                } else if (TAG_ALLOW_FALLBACK_TO_DEFAULT_CONNECTION.equals(tagDAM)) {
                    resultBuilder.setFallbackToDefaultConnectionAllowed(parser.getAttributeBoolean(null, "value", true));
                } else if (TAG_BLOCK_NON_MATCHING_NETWORKS.equals(tagDAM)) {
                    resultBuilder.setShouldBlockNonMatchingNetworks(parser.getAttributeBoolean(null, "value", false));
                } else if (TAG_INCLUDED_UIDS.equals(tagDAM)) {
                    resultBuilder.setIncludedUids(readStringListToIntArray(parser, "uid"));
                } else if (TAG_EXCLUDED_UIDS.equals(tagDAM)) {
                    resultBuilder.setExcludedUids(readStringListToIntArray(parser, "uid"));
                } else {
                    Log.m104w(LOG_TAG, "Unknown tag under " + tag + ": " + tagDAM);
                }
            }
        }
        return resultBuilder.build();
    }

    public void writeToXml(TypedXmlSerializer out) throws IOException {
        out.startTag(null, TAG_PREFERENTIAL_NETWORK_SERVICE_CONFIG);
        writeAttributeValueToXml(out, TAG_CONFIG_ENABLED, isEnabled());
        writeAttributeValueToXml(out, TAG_NETWORK_ID, getNetworkId());
        writeAttributeValueToXml(out, TAG_ALLOW_FALLBACK_TO_DEFAULT_CONNECTION, isFallbackToDefaultConnectionAllowed());
        writeAttributeValueToXml(out, TAG_BLOCK_NON_MATCHING_NETWORKS, shouldBlockNonMatchingNetworks());
        writeAttributeValuesToXml(out, TAG_INCLUDED_UIDS, "uid", intArrayToStringList(getIncludedUids()));
        writeAttributeValuesToXml(out, TAG_EXCLUDED_UIDS, "uid", intArrayToStringList(getExcludedUids()));
        out.endTag(null, TAG_PREFERENTIAL_NETWORK_SERVICE_CONFIG);
    }

    public void dump(IndentingPrintWriter pw) {
        pw.print("networkId=");
        pw.println(this.mNetworkId);
        pw.print("isEnabled=");
        pw.println(this.mIsEnabled);
        pw.print("allowFallbackToDefaultConnection=");
        pw.println(this.mAllowFallbackToDefaultConnection);
        pw.print("blockNonMatchingNetworks=");
        pw.println(this.mShouldBlockNonMatchingNetworks);
        pw.print("includedUids=");
        pw.println(Arrays.toString(this.mIncludedUids));
        pw.print("excludedUids=");
        pw.println(Arrays.toString(this.mExcludedUids));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
