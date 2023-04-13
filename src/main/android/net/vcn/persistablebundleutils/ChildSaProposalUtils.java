package android.net.vcn.persistablebundleutils;

import android.net.ipsec.ike.ChildSaProposal;
import android.net.vcn.persistablebundleutils.SaProposalUtilsBase;
import android.p008os.PersistableBundle;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ChildSaProposalUtils extends SaProposalUtilsBase {
    public static PersistableBundle toPersistableBundle(ChildSaProposal proposal) {
        return SaProposalUtilsBase.toPersistableBundle(proposal);
    }

    public static ChildSaProposal fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        ChildSaProposal.Builder builder = new ChildSaProposal.Builder();
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
        return builder.build();
    }
}
