package android.net.vcn.persistablebundleutils;

import android.net.ipsec.ike.IkeSessionParams;
import android.net.ipsec.ike.IkeTunnelConnectionParams;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.p008os.PersistableBundle;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TunnelConnectionParamsUtils {
    private static final int EXPECTED_BUNDLE_KEY_CNT = 1;
    private static final String PARAMS_TYPE_IKE = "IKE";

    public static PersistableBundle toPersistableBundle(IkeTunnelConnectionParams params) {
        PersistableBundle result = new PersistableBundle();
        result.putPersistableBundle(PARAMS_TYPE_IKE, IkeTunnelConnectionParamsUtils.serializeIkeParams(params));
        return result;
    }

    public static IkeTunnelConnectionParams fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        if (in.keySet().size() != 1) {
            throw new IllegalArgumentException(String.format("Expect PersistableBundle to have %d element but found: %s", 1, in.keySet()));
        }
        if (in.get(PARAMS_TYPE_IKE) != null) {
            return IkeTunnelConnectionParamsUtils.deserializeIkeParams(in.getPersistableBundle(PARAMS_TYPE_IKE));
        }
        throw new IllegalArgumentException("Invalid Tunnel Connection Params type " + in.keySet().iterator().next());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class IkeTunnelConnectionParamsUtils {
        private static final String CHILD_PARAMS_KEY = "CHILD_PARAMS_KEY";
        private static final String IKE_PARAMS_KEY = "IKE_PARAMS_KEY";

        private IkeTunnelConnectionParamsUtils() {
        }

        public static PersistableBundle serializeIkeParams(IkeTunnelConnectionParams ikeParams) {
            PersistableBundle result = new PersistableBundle();
            result.putPersistableBundle(IKE_PARAMS_KEY, IkeSessionParamsUtils.toPersistableBundle(ikeParams.getIkeSessionParams()));
            result.putPersistableBundle(CHILD_PARAMS_KEY, TunnelModeChildSessionParamsUtils.toPersistableBundle(ikeParams.getTunnelModeChildSessionParams()));
            return result;
        }

        public static IkeTunnelConnectionParams deserializeIkeParams(PersistableBundle in) {
            PersistableBundle ikeBundle = in.getPersistableBundle(IKE_PARAMS_KEY);
            PersistableBundle childBundle = in.getPersistableBundle(CHILD_PARAMS_KEY);
            Objects.requireNonNull(ikeBundle, "IkeSessionParams was null");
            Objects.requireNonNull(ikeBundle, "TunnelModeChildSessionParams was null");
            IkeSessionParams ikeParams = IkeSessionParamsUtils.fromPersistableBundle(ikeBundle);
            TunnelModeChildSessionParams childParams = TunnelModeChildSessionParamsUtils.fromPersistableBundle(childBundle);
            return new IkeTunnelConnectionParams(ikeParams, childParams);
        }
    }
}
