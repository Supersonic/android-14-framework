package android.net.metrics;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import java.util.BitSet;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class DefaultNetworkEvent {
    public final long creationTimeMs;
    public long durationMs;
    public int finalScore;
    public int initialScore;
    public boolean ipv4;
    public boolean ipv6;
    public int netId = 0;
    public int previousTransports;
    public int transports;
    public long validatedMs;

    public DefaultNetworkEvent(long timeMs) {
        this.creationTimeMs = timeMs;
    }

    public void updateDuration(long timeMs) {
        this.durationMs = timeMs - this.creationTimeMs;
    }

    public String toString() {
        StringJoiner j = new StringJoiner(", ", "DefaultNetworkEvent(", NavigationBarInflaterView.KEY_CODE_END);
        j.add("netId=" + this.netId);
        j.add("transports=" + BitSet.valueOf(new long[]{this.transports}));
        j.add("ip=" + ipSupport());
        if (this.initialScore > 0) {
            j.add("initial_score=" + this.initialScore);
        }
        if (this.finalScore > 0) {
            j.add("final_score=" + this.finalScore);
        }
        j.add(String.format("duration=%.0fs", Double.valueOf(this.durationMs / 1000.0d)));
        j.add(String.format("validation=%04.1f%%", Double.valueOf((this.validatedMs * 100.0d) / this.durationMs)));
        return j.toString();
    }

    private String ipSupport() {
        boolean z = this.ipv4;
        if (z && this.ipv6) {
            return "IPv4v6";
        }
        if (this.ipv6) {
            return "IPv6";
        }
        if (z) {
            return "IPv4";
        }
        return KeyProperties.DIGEST_NONE;
    }
}
