package android.net.ipmemorystore;

import com.android.internal.annotations.VisibleForTesting;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public class SameL3NetworkResponse {
    public static final int NETWORK_DIFFERENT = 2;
    public static final int NETWORK_NEVER_CONNECTED = 3;
    public static final int NETWORK_SAME = 1;
    public final float confidence;
    public final String l2Key1;
    public final String l2Key2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NetworkSameness {
    }

    public final int getNetworkSameness() {
        float f = this.confidence;
        if (f > 1.0d || f < 0.0d) {
            return 3;
        }
        return ((double) f) > 0.5d ? 1 : 2;
    }

    public SameL3NetworkResponse(String str, String str2, float f) {
        this.l2Key1 = str;
        this.l2Key2 = str2;
        this.confidence = f;
    }

    @VisibleForTesting
    public SameL3NetworkResponse(SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable) {
        this(sameL3NetworkResponseParcelable.l2Key1, sameL3NetworkResponseParcelable.l2Key2, sameL3NetworkResponseParcelable.confidence);
    }

    public SameL3NetworkResponseParcelable toParcelable() {
        SameL3NetworkResponseParcelable sameL3NetworkResponseParcelable = new SameL3NetworkResponseParcelable();
        sameL3NetworkResponseParcelable.l2Key1 = this.l2Key1;
        sameL3NetworkResponseParcelable.l2Key2 = this.l2Key2;
        sameL3NetworkResponseParcelable.confidence = this.confidence;
        return sameL3NetworkResponseParcelable;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SameL3NetworkResponse) {
            SameL3NetworkResponse sameL3NetworkResponse = (SameL3NetworkResponse) obj;
            return this.l2Key1.equals(sameL3NetworkResponse.l2Key1) && this.l2Key2.equals(sameL3NetworkResponse.l2Key2) && this.confidence == sameL3NetworkResponse.confidence;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.l2Key1, this.l2Key2, Float.valueOf(this.confidence));
    }

    public String toString() {
        int networkSameness = getNetworkSameness();
        if (networkSameness == 1) {
            return "\"" + this.l2Key1 + "\" same L3 network as \"" + this.l2Key2 + "\"";
        } else if (networkSameness == 2) {
            return "\"" + this.l2Key1 + "\" different L3 network from \"" + this.l2Key2 + "\"";
        } else if (networkSameness == 3) {
            return "\"" + this.l2Key1 + "\" can't be tested against \"" + this.l2Key2 + "\"";
        } else {
            return "Buggy sameness value ? \"" + this.l2Key1 + "\", \"" + this.l2Key2 + "\"";
        }
    }
}
