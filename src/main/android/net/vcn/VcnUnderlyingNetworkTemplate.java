package android.net.vcn;

import android.p008os.PersistableBundle;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public abstract class VcnUnderlyingNetworkTemplate {
    static final int DEFAULT_METERED_MATCH_CRITERIA = 0;
    public static final int DEFAULT_MIN_BANDWIDTH_KBPS = 0;
    public static final int MATCH_ANY = 0;
    private static final SparseArray<String> MATCH_CRITERIA_TO_STRING_MAP;
    public static final int MATCH_FORBIDDEN = 2;
    public static final int MATCH_REQUIRED = 1;
    static final String METERED_MATCH_KEY = "mMeteredMatchCriteria";
    static final String MIN_ENTRY_DOWNSTREAM_BANDWIDTH_KBPS_KEY = "mMinEntryDownstreamBandwidthKbps";
    static final String MIN_ENTRY_UPSTREAM_BANDWIDTH_KBPS_KEY = "mMinEntryUpstreamBandwidthKbps";
    static final String MIN_EXIT_DOWNSTREAM_BANDWIDTH_KBPS_KEY = "mMinExitDownstreamBandwidthKbps";
    static final String MIN_EXIT_UPSTREAM_BANDWIDTH_KBPS_KEY = "mMinExitUpstreamBandwidthKbps";
    static final int NETWORK_PRIORITY_TYPE_CELL = 2;
    private static final String NETWORK_PRIORITY_TYPE_KEY = "mNetworkPriorityType";
    static final int NETWORK_PRIORITY_TYPE_WIFI = 1;
    private final int mMeteredMatchCriteria;
    private final int mMinEntryDownstreamBandwidthKbps;
    private final int mMinEntryUpstreamBandwidthKbps;
    private final int mMinExitDownstreamBandwidthKbps;
    private final int mMinExitUpstreamBandwidthKbps;
    private final int mNetworkPriorityType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MatchCriteria {
    }

    abstract void dumpTransportSpecificFields(IndentingPrintWriter indentingPrintWriter);

    static {
        SparseArray<String> sparseArray = new SparseArray<>();
        MATCH_CRITERIA_TO_STRING_MAP = sparseArray;
        sparseArray.put(0, "MATCH_ANY");
        sparseArray.put(1, "MATCH_REQUIRED");
        sparseArray.put(2, "MATCH_FORBIDDEN");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VcnUnderlyingNetworkTemplate(int networkPriorityType, int meteredMatchCriteria, int minEntryUpstreamBandwidthKbps, int minExitUpstreamBandwidthKbps, int minEntryDownstreamBandwidthKbps, int minExitDownstreamBandwidthKbps) {
        this.mNetworkPriorityType = networkPriorityType;
        this.mMeteredMatchCriteria = meteredMatchCriteria;
        this.mMinEntryUpstreamBandwidthKbps = minEntryUpstreamBandwidthKbps;
        this.mMinExitUpstreamBandwidthKbps = minExitUpstreamBandwidthKbps;
        this.mMinEntryDownstreamBandwidthKbps = minEntryDownstreamBandwidthKbps;
        this.mMinExitDownstreamBandwidthKbps = minExitDownstreamBandwidthKbps;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void validateMatchCriteria(int matchCriteria, String matchingCapability) {
        Preconditions.checkArgument(MATCH_CRITERIA_TO_STRING_MAP.contains(matchCriteria), "Invalid matching criteria: " + matchCriteria + " for " + matchingCapability);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void validateMinBandwidthKbps(int minEntryBandwidth, int minExitBandwidth) {
        Preconditions.checkArgument(minEntryBandwidth >= 0, "Invalid minEntryBandwidth, must be >= 0");
        Preconditions.checkArgument(minExitBandwidth >= 0, "Invalid minExitBandwidth, must be >= 0");
        Preconditions.checkArgument(minEntryBandwidth >= minExitBandwidth, "Minimum entry bandwidth must be >= exit bandwidth");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void validate() {
        validateMatchCriteria(this.mMeteredMatchCriteria, METERED_MATCH_KEY);
        validateMinBandwidthKbps(this.mMinEntryUpstreamBandwidthKbps, this.mMinExitUpstreamBandwidthKbps);
        validateMinBandwidthKbps(this.mMinEntryDownstreamBandwidthKbps, this.mMinExitDownstreamBandwidthKbps);
    }

    public static VcnUnderlyingNetworkTemplate fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle is null");
        int networkPriorityType = in.getInt(NETWORK_PRIORITY_TYPE_KEY);
        switch (networkPriorityType) {
            case 1:
                return VcnWifiUnderlyingNetworkTemplate.fromPersistableBundle(in);
            case 2:
                return VcnCellUnderlyingNetworkTemplate.fromPersistableBundle(in);
            default:
                throw new IllegalArgumentException("Invalid networkPriorityType:" + networkPriorityType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PersistableBundle toPersistableBundle() {
        PersistableBundle result = new PersistableBundle();
        result.putInt(NETWORK_PRIORITY_TYPE_KEY, this.mNetworkPriorityType);
        result.putInt(METERED_MATCH_KEY, this.mMeteredMatchCriteria);
        result.putInt(MIN_ENTRY_UPSTREAM_BANDWIDTH_KBPS_KEY, this.mMinEntryUpstreamBandwidthKbps);
        result.putInt(MIN_EXIT_UPSTREAM_BANDWIDTH_KBPS_KEY, this.mMinExitUpstreamBandwidthKbps);
        result.putInt(MIN_ENTRY_DOWNSTREAM_BANDWIDTH_KBPS_KEY, this.mMinEntryDownstreamBandwidthKbps);
        result.putInt(MIN_EXIT_DOWNSTREAM_BANDWIDTH_KBPS_KEY, this.mMinExitDownstreamBandwidthKbps);
        return result;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mNetworkPriorityType), Integer.valueOf(this.mMeteredMatchCriteria), Integer.valueOf(this.mMinEntryUpstreamBandwidthKbps), Integer.valueOf(this.mMinExitUpstreamBandwidthKbps), Integer.valueOf(this.mMinEntryDownstreamBandwidthKbps), Integer.valueOf(this.mMinExitDownstreamBandwidthKbps));
    }

    public boolean equals(Object other) {
        if (other instanceof VcnUnderlyingNetworkTemplate) {
            VcnUnderlyingNetworkTemplate rhs = (VcnUnderlyingNetworkTemplate) other;
            return this.mNetworkPriorityType == rhs.mNetworkPriorityType && this.mMeteredMatchCriteria == rhs.mMeteredMatchCriteria && this.mMinEntryUpstreamBandwidthKbps == rhs.mMinEntryUpstreamBandwidthKbps && this.mMinExitUpstreamBandwidthKbps == rhs.mMinExitUpstreamBandwidthKbps && this.mMinEntryDownstreamBandwidthKbps == rhs.mMinEntryDownstreamBandwidthKbps && this.mMinExitDownstreamBandwidthKbps == rhs.mMinExitDownstreamBandwidthKbps;
        }
        return false;
    }

    static String getNameString(SparseArray<String> toStringMap, int key) {
        return toStringMap.get(key, "Invalid value " + key);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMatchCriteriaString(int matchCriteria) {
        return getNameString(MATCH_CRITERIA_TO_STRING_MAP, matchCriteria);
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println(getClass().getSimpleName() + ":");
        pw.increaseIndent();
        if (this.mMeteredMatchCriteria != 0) {
            pw.println("mMeteredMatchCriteria: " + getMatchCriteriaString(this.mMeteredMatchCriteria));
        }
        if (this.mMinEntryUpstreamBandwidthKbps != 0) {
            pw.println("mMinEntryUpstreamBandwidthKbps: " + this.mMinEntryUpstreamBandwidthKbps);
        }
        if (this.mMinExitUpstreamBandwidthKbps != 0) {
            pw.println("mMinExitUpstreamBandwidthKbps: " + this.mMinExitUpstreamBandwidthKbps);
        }
        if (this.mMinEntryDownstreamBandwidthKbps != 0) {
            pw.println("mMinEntryDownstreamBandwidthKbps: " + this.mMinEntryDownstreamBandwidthKbps);
        }
        if (this.mMinExitDownstreamBandwidthKbps != 0) {
            pw.println("mMinExitDownstreamBandwidthKbps: " + this.mMinExitDownstreamBandwidthKbps);
        }
        dumpTransportSpecificFields(pw);
        pw.decreaseIndent();
    }

    public int getMetered() {
        return this.mMeteredMatchCriteria;
    }

    public int getMinEntryUpstreamBandwidthKbps() {
        return this.mMinEntryUpstreamBandwidthKbps;
    }

    public int getMinExitUpstreamBandwidthKbps() {
        return this.mMinExitUpstreamBandwidthKbps;
    }

    public int getMinEntryDownstreamBandwidthKbps() {
        return this.mMinEntryDownstreamBandwidthKbps;
    }

    public int getMinExitDownstreamBandwidthKbps() {
        return this.mMinExitDownstreamBandwidthKbps;
    }
}
