package android.net.vcn;

import android.p008os.PersistableBundle;
import android.util.ArraySet;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class VcnWifiUnderlyingNetworkTemplate extends VcnUnderlyingNetworkTemplate {
    private static final String SSIDS_KEY = "mSsids";
    private final Set<String> mSsids;

    private VcnWifiUnderlyingNetworkTemplate(int meteredMatchCriteria, int minEntryUpstreamBandwidthKbps, int minExitUpstreamBandwidthKbps, int minEntryDownstreamBandwidthKbps, int minExitDownstreamBandwidthKbps, Set<String> ssids) {
        super(1, meteredMatchCriteria, minEntryUpstreamBandwidthKbps, minExitUpstreamBandwidthKbps, minEntryDownstreamBandwidthKbps, minExitDownstreamBandwidthKbps);
        this.mSsids = new ArraySet(ssids);
        validate();
    }

    @Override // android.net.vcn.VcnUnderlyingNetworkTemplate
    protected void validate() {
        super.validate();
        validateSsids(this.mSsids);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateSsids(Set<String> ssids) {
        Objects.requireNonNull(ssids, "ssids is null");
        for (String ssid : ssids) {
            Objects.requireNonNull(ssid, "found null value ssid");
        }
    }

    public static VcnWifiUnderlyingNetworkTemplate fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle is null");
        int meteredMatchCriteria = in.getInt("mMeteredMatchCriteria");
        int minEntryUpstreamBandwidthKbps = in.getInt("mMinEntryUpstreamBandwidthKbps", 0);
        int minExitUpstreamBandwidthKbps = in.getInt("mMinExitUpstreamBandwidthKbps", 0);
        int minEntryDownstreamBandwidthKbps = in.getInt("mMinEntryDownstreamBandwidthKbps", 0);
        int minExitDownstreamBandwidthKbps = in.getInt("mMinExitDownstreamBandwidthKbps", 0);
        PersistableBundle ssidsBundle = in.getPersistableBundle(SSIDS_KEY);
        Objects.requireNonNull(ssidsBundle, "ssidsBundle is null");
        Set<String> ssids = new ArraySet<>(PersistableBundleUtils.toList(ssidsBundle, PersistableBundleUtils.STRING_DESERIALIZER));
        return new VcnWifiUnderlyingNetworkTemplate(meteredMatchCriteria, minEntryUpstreamBandwidthKbps, minExitUpstreamBandwidthKbps, minEntryDownstreamBandwidthKbps, minExitDownstreamBandwidthKbps, ssids);
    }

    @Override // android.net.vcn.VcnUnderlyingNetworkTemplate
    public PersistableBundle toPersistableBundle() {
        PersistableBundle result = super.toPersistableBundle();
        PersistableBundle ssidsBundle = PersistableBundleUtils.fromList(new ArrayList(this.mSsids), PersistableBundleUtils.STRING_SERIALIZER);
        result.putPersistableBundle(SSIDS_KEY, ssidsBundle);
        return result;
    }

    @Override // android.net.vcn.VcnUnderlyingNetworkTemplate
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mSsids);
    }

    @Override // android.net.vcn.VcnUnderlyingNetworkTemplate
    public boolean equals(Object other) {
        if (super.equals(other) && (other instanceof VcnWifiUnderlyingNetworkTemplate)) {
            VcnWifiUnderlyingNetworkTemplate rhs = (VcnWifiUnderlyingNetworkTemplate) other;
            return this.mSsids.equals(rhs.mSsids);
        }
        return false;
    }

    @Override // android.net.vcn.VcnUnderlyingNetworkTemplate
    void dumpTransportSpecificFields(IndentingPrintWriter pw) {
        if (!this.mSsids.isEmpty()) {
            pw.println("mSsids: " + this.mSsids);
        }
    }

    public Set<String> getSsids() {
        return Collections.unmodifiableSet(this.mSsids);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mMeteredMatchCriteria = 0;
        private final Set<String> mSsids = new ArraySet();
        private int mMinEntryUpstreamBandwidthKbps = 0;
        private int mMinExitUpstreamBandwidthKbps = 0;
        private int mMinEntryDownstreamBandwidthKbps = 0;
        private int mMinExitDownstreamBandwidthKbps = 0;

        public Builder setMetered(int matchCriteria) {
            VcnUnderlyingNetworkTemplate.validateMatchCriteria(matchCriteria, "setMetered");
            this.mMeteredMatchCriteria = matchCriteria;
            return this;
        }

        public Builder setSsids(Set<String> ssids) {
            VcnWifiUnderlyingNetworkTemplate.validateSsids(ssids);
            this.mSsids.clear();
            this.mSsids.addAll(ssids);
            return this;
        }

        public Builder setMinUpstreamBandwidthKbps(int minEntryUpstreamBandwidthKbps, int minExitUpstreamBandwidthKbps) {
            VcnUnderlyingNetworkTemplate.validateMinBandwidthKbps(minEntryUpstreamBandwidthKbps, minExitUpstreamBandwidthKbps);
            this.mMinEntryUpstreamBandwidthKbps = minEntryUpstreamBandwidthKbps;
            this.mMinExitUpstreamBandwidthKbps = minExitUpstreamBandwidthKbps;
            return this;
        }

        public Builder setMinDownstreamBandwidthKbps(int minEntryDownstreamBandwidthKbps, int minExitDownstreamBandwidthKbps) {
            VcnUnderlyingNetworkTemplate.validateMinBandwidthKbps(minEntryDownstreamBandwidthKbps, minExitDownstreamBandwidthKbps);
            this.mMinEntryDownstreamBandwidthKbps = minEntryDownstreamBandwidthKbps;
            this.mMinExitDownstreamBandwidthKbps = minExitDownstreamBandwidthKbps;
            return this;
        }

        public VcnWifiUnderlyingNetworkTemplate build() {
            return new VcnWifiUnderlyingNetworkTemplate(this.mMeteredMatchCriteria, this.mMinEntryUpstreamBandwidthKbps, this.mMinExitUpstreamBandwidthKbps, this.mMinEntryDownstreamBandwidthKbps, this.mMinExitDownstreamBandwidthKbps, this.mSsids);
        }
    }
}
