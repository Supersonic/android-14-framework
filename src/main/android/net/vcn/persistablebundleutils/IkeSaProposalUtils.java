package android.net.vcn.persistablebundleutils;

import android.net.ipsec.ike.IkeSaProposal;
import android.net.vcn.persistablebundleutils.SaProposalUtilsBase;
import android.p008os.PersistableBundle;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
public final class IkeSaProposalUtils extends SaProposalUtilsBase {
    private static final String PRF_KEY = "PRF_KEY";

    public static PersistableBundle toPersistableBundle(IkeSaProposal proposal) {
        PersistableBundle result = SaProposalUtilsBase.toPersistableBundle(proposal);
        int[] prfArray = proposal.getPseudorandomFunctions().stream().mapToInt(new ToIntFunction() { // from class: android.net.vcn.persistablebundleutils.IkeSaProposalUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray();
        result.putIntArray(PRF_KEY, prfArray);
        return result;
    }

    public static IkeSaProposal fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        IkeSaProposal.Builder builder = new IkeSaProposal.Builder();
        PersistableBundle encryptionBundle = in.getPersistableBundle("ENCRYPT_ALGO_KEY");
        Objects.requireNonNull(encryptionBundle, "Encryption algo bundle was null");
        List<SaProposalUtilsBase.EncryptionAlgoKeyLenPair> encryptList = PersistableBundleUtils.toList(encryptionBundle, new ChildSaProposalUtils$$ExternalSyntheticLambda0());
        for (SaProposalUtilsBase.EncryptionAlgoKeyLenPair t : encryptList) {
            builder.addEncryptionAlgorithm(t.encryptionAlgo, t.keyLen);
        }
        int[] integrityAlgoIdArray = in.getIntArray("INTEGRITY_ALGO_KEY");
        Objects.requireNonNull(integrityAlgoIdArray, "Integrity algo array was null");
        for (int algo : integrityAlgoIdArray) {
            builder.addIntegrityAlgorithm(algo);
        }
        int[] dhGroupArray = in.getIntArray("DH_GROUP_KEY");
        Objects.requireNonNull(dhGroupArray, "DH Group array was null");
        for (int dh : dhGroupArray) {
            builder.addDhGroup(dh);
        }
        int[] prfArray = in.getIntArray(PRF_KEY);
        Objects.requireNonNull(prfArray, "PRF array was null");
        for (int prf : prfArray) {
            builder.addPseudorandomFunction(prf);
        }
        return builder.build();
    }
}
