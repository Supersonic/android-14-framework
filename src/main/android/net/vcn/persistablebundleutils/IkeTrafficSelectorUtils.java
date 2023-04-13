package android.net.vcn.persistablebundleutils;

import android.net.InetAddresses;
import android.net.ipsec.ike.IkeTrafficSelector;
import android.p008os.PersistableBundle;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class IkeTrafficSelectorUtils {
    private static final String END_ADDRESS_KEY = "END_ADDRESS_KEY";
    private static final String END_PORT_KEY = "END_PORT_KEY";
    private static final String START_ADDRESS_KEY = "START_ADDRESS_KEY";
    private static final String START_PORT_KEY = "START_PORT_KEY";

    public static IkeTrafficSelector fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        int startPort = in.getInt(START_PORT_KEY);
        int endPort = in.getInt(END_PORT_KEY);
        String startingAddress = in.getString(START_ADDRESS_KEY);
        String endingAddress = in.getString(END_ADDRESS_KEY);
        Objects.requireNonNull(startingAddress, "startAddress was null");
        Objects.requireNonNull(startingAddress, "endAddress was null");
        return new IkeTrafficSelector(startPort, endPort, InetAddresses.parseNumericAddress(startingAddress), InetAddresses.parseNumericAddress(endingAddress));
    }

    public static PersistableBundle toPersistableBundle(IkeTrafficSelector ts) {
        PersistableBundle result = new PersistableBundle();
        result.putInt(START_PORT_KEY, ts.startPort);
        result.putInt(END_PORT_KEY, ts.endPort);
        result.putString(START_ADDRESS_KEY, ts.startingAddress.getHostAddress());
        result.putString(END_ADDRESS_KEY, ts.endingAddress.getHostAddress());
        return result;
    }
}
