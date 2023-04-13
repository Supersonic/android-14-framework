package android.net.vcn.persistablebundleutils;

import android.net.ipsec.ike.SaProposal;
import android.net.vcn.persistablebundleutils.SaProposalUtilsBase;
import android.p008os.PersistableBundle;
import android.util.Pair;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
abstract class SaProposalUtilsBase {
    static final String DH_GROUP_KEY = "DH_GROUP_KEY";
    static final String ENCRYPT_ALGO_KEY = "ENCRYPT_ALGO_KEY";
    static final String INTEGRITY_ALGO_KEY = "INTEGRITY_ALGO_KEY";

    /* loaded from: classes2.dex */
    static class EncryptionAlgoKeyLenPair {
        private static final String ALGO_KEY = "ALGO_KEY";
        private static final String KEY_LEN_KEY = "KEY_LEN_KEY";
        public final int encryptionAlgo;
        public final int keyLen;

        EncryptionAlgoKeyLenPair(int encryptionAlgo, int keyLen) {
            this.encryptionAlgo = encryptionAlgo;
            this.keyLen = keyLen;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public EncryptionAlgoKeyLenPair(PersistableBundle in) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            this.encryptionAlgo = in.getInt(ALGO_KEY);
            this.keyLen = in.getInt(KEY_LEN_KEY);
        }

        public PersistableBundle toPersistableBundle() {
            PersistableBundle result = new PersistableBundle();
            result.putInt(ALGO_KEY, this.encryptionAlgo);
            result.putInt(KEY_LEN_KEY, this.keyLen);
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PersistableBundle toPersistableBundle(SaProposal proposal) {
        PersistableBundle result = new PersistableBundle();
        List<EncryptionAlgoKeyLenPair> encryptAlgoKeyLenPairs = new ArrayList<>();
        for (Pair<Integer, Integer> pair : proposal.getEncryptionAlgorithms()) {
            encryptAlgoKeyLenPairs.add(new EncryptionAlgoKeyLenPair(pair.first.intValue(), pair.second.intValue()));
        }
        PersistableBundle encryptionBundle = PersistableBundleUtils.fromList(encryptAlgoKeyLenPairs, new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.SaProposalUtilsBase$$ExternalSyntheticLambda0
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ((SaProposalUtilsBase.EncryptionAlgoKeyLenPair) obj).toPersistableBundle();
            }
        });
        result.putPersistableBundle(ENCRYPT_ALGO_KEY, encryptionBundle);
        int[] integrityAlgoIdArray = proposal.getIntegrityAlgorithms().stream().mapToInt(new ToIntFunction() { // from class: android.net.vcn.persistablebundleutils.SaProposalUtilsBase$$ExternalSyntheticLambda1
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray();
        result.putIntArray(INTEGRITY_ALGO_KEY, integrityAlgoIdArray);
        int[] dhGroupArray = proposal.getDhGroups().stream().mapToInt(new ToIntFunction() { // from class: android.net.vcn.persistablebundleutils.SaProposalUtilsBase$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray();
        result.putIntArray(DH_GROUP_KEY, dhGroupArray);
        return result;
    }
}
