package android.net;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
import java.util.Set;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class ScoredNetwork implements Parcelable {
    public static final String ATTRIBUTES_KEY_BADGING_CURVE = "android.net.attributes.key.BADGING_CURVE";
    public static final String ATTRIBUTES_KEY_HAS_CAPTIVE_PORTAL = "android.net.attributes.key.HAS_CAPTIVE_PORTAL";
    public static final String ATTRIBUTES_KEY_RANKING_SCORE_OFFSET = "android.net.attributes.key.RANKING_SCORE_OFFSET";
    public static final Parcelable.Creator<ScoredNetwork> CREATOR = new Parcelable.Creator<ScoredNetwork>() { // from class: android.net.ScoredNetwork.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScoredNetwork createFromParcel(Parcel in) {
            return new ScoredNetwork(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScoredNetwork[] newArray(int size) {
            return new ScoredNetwork[size];
        }
    };
    public final Bundle attributes;
    public final boolean meteredHint;
    public final NetworkKey networkKey;
    public final RssiCurve rssiCurve;

    public ScoredNetwork(NetworkKey networkKey, RssiCurve rssiCurve) {
        this(networkKey, rssiCurve, false);
    }

    public ScoredNetwork(NetworkKey networkKey, RssiCurve rssiCurve, boolean meteredHint) {
        this(networkKey, rssiCurve, meteredHint, null);
    }

    public ScoredNetwork(NetworkKey networkKey, RssiCurve rssiCurve, boolean meteredHint, Bundle attributes) {
        this.networkKey = networkKey;
        this.rssiCurve = rssiCurve;
        this.meteredHint = meteredHint;
        this.attributes = attributes;
    }

    private ScoredNetwork(Parcel in) {
        this.networkKey = NetworkKey.CREATOR.createFromParcel(in);
        if (in.readByte() == 1) {
            this.rssiCurve = RssiCurve.CREATOR.createFromParcel(in);
        } else {
            this.rssiCurve = null;
        }
        this.meteredHint = in.readByte() == 1;
        this.attributes = in.readBundle();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        this.networkKey.writeToParcel(out, flags);
        if (this.rssiCurve != null) {
            out.writeByte((byte) 1);
            this.rssiCurve.writeToParcel(out, flags);
        } else {
            out.writeByte((byte) 0);
        }
        out.writeByte(this.meteredHint ? (byte) 1 : (byte) 0);
        out.writeBundle(this.attributes);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScoredNetwork that = (ScoredNetwork) o;
        if (Objects.equals(this.networkKey, that.networkKey) && Objects.equals(this.rssiCurve, that.rssiCurve) && Objects.equals(Boolean.valueOf(this.meteredHint), Boolean.valueOf(that.meteredHint)) && bundleEquals(this.attributes, that.attributes)) {
            return true;
        }
        return false;
    }

    private boolean bundleEquals(Bundle bundle1, Bundle bundle2) {
        if (bundle1 == bundle2) {
            return true;
        }
        if (bundle1 == null || bundle2 == null || bundle1.size() != bundle2.size()) {
            return false;
        }
        Set<String> keys = bundle1.keySet();
        for (String key : keys) {
            Object value1 = bundle1.get(key);
            Object value2 = bundle2.get(key);
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.networkKey, this.rssiCurve, Boolean.valueOf(this.meteredHint), this.attributes);
    }

    public String toString() {
        StringBuilder out = new StringBuilder("ScoredNetwork{networkKey=" + this.networkKey + ", rssiCurve=" + this.rssiCurve + ", meteredHint=" + this.meteredHint);
        Bundle bundle = this.attributes;
        if (bundle != null && !bundle.isEmpty()) {
            out.append(", attributes=" + this.attributes);
        }
        out.append('}');
        return out.toString();
    }

    public boolean hasRankingScore() {
        Bundle bundle;
        return this.rssiCurve != null || ((bundle = this.attributes) != null && bundle.containsKey(ATTRIBUTES_KEY_RANKING_SCORE_OFFSET));
    }

    public int calculateRankingScore(int rssi) throws UnsupportedOperationException {
        if (!hasRankingScore()) {
            throw new UnsupportedOperationException("Either rssiCurve or rankingScoreOffset is required to calculate the ranking score");
        }
        Bundle bundle = this.attributes;
        int offset = bundle != null ? 0 + bundle.getInt(ATTRIBUTES_KEY_RANKING_SCORE_OFFSET, 0) : 0;
        RssiCurve rssiCurve = this.rssiCurve;
        int score = rssiCurve != null ? rssiCurve.lookupScore(rssi) << 8 : 0;
        try {
            return Math.addExact(score, offset);
        } catch (ArithmeticException e) {
            return score < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    public int calculateBadge(int rssi) {
        Bundle bundle = this.attributes;
        if (bundle != null && bundle.containsKey(ATTRIBUTES_KEY_BADGING_CURVE)) {
            RssiCurve badgingCurve = (RssiCurve) this.attributes.getParcelable(ATTRIBUTES_KEY_BADGING_CURVE, RssiCurve.class);
            return badgingCurve.lookupScore(rssi);
        }
        return 0;
    }
}
