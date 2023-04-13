package android.security.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.KeyProperties;
import android.system.keystore2.Authorization;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
abstract class AndroidKeyStoreECDSASignatureSpi extends AndroidKeyStoreSignatureSpiBase {
    private static final Set<String> ACCEPTED_SIGNING_SCHEMES = Set.of(KeyProperties.KEY_ALGORITHM_EC.toLowerCase(), NamedParameterSpec.ED25519.getName().toLowerCase(), "eddsa");
    private int mGroupSizeBits = -1;
    private final int mKeymasterDigest;

    /* loaded from: classes3.dex */
    public static final class NONE extends AndroidKeyStoreECDSASignatureSpi {
        public NONE() {
            super(0);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "NONEwithECDSA";
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        public KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStoreOperation operation) {
            return new TruncateToFieldSizeMessageStreamer(super.createMainDataStreamer(operation), getGroupSizeBits());
        }

        /* loaded from: classes3.dex */
        private static class TruncateToFieldSizeMessageStreamer implements KeyStoreCryptoOperationStreamer {
            private long mConsumedInputSizeBytes;
            private final KeyStoreCryptoOperationStreamer mDelegate;
            private final int mGroupSizeBits;
            private final ByteArrayOutputStream mInputBuffer;

            private TruncateToFieldSizeMessageStreamer(KeyStoreCryptoOperationStreamer delegate, int groupSizeBits) {
                this.mInputBuffer = new ByteArrayOutputStream();
                this.mDelegate = delegate;
                this.mGroupSizeBits = groupSizeBits;
            }

            @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
            public byte[] update(byte[] input, int inputOffset, int inputLength) throws KeyStoreException {
                if (inputLength > 0) {
                    this.mInputBuffer.write(input, inputOffset, inputLength);
                    this.mConsumedInputSizeBytes += inputLength;
                }
                return EmptyArray.BYTE;
            }

            @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
            public byte[] doFinal(byte[] input, int inputOffset, int inputLength, byte[] signature) throws KeyStoreException {
                if (inputLength > 0) {
                    this.mConsumedInputSizeBytes += inputLength;
                    this.mInputBuffer.write(input, inputOffset, inputLength);
                }
                byte[] bufferedInput = this.mInputBuffer.toByteArray();
                this.mInputBuffer.reset();
                return this.mDelegate.doFinal(bufferedInput, 0, Math.min(bufferedInput.length, (this.mGroupSizeBits + 7) / 8), signature);
            }

            @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
            public long getConsumedInputSizeBytes() {
                return this.mConsumedInputSizeBytes;
            }

            @Override // android.security.keystore2.KeyStoreCryptoOperationStreamer
            public long getProducedOutputSizeBytes() {
                return this.mDelegate.getProducedOutputSizeBytes();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Ed25519 extends AndroidKeyStoreECDSASignatureSpi {
        public Ed25519() {
            super(0);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return NamedParameterSpec.ED25519.getName();
        }
    }

    /* loaded from: classes3.dex */
    public static final class SHA1 extends AndroidKeyStoreECDSASignatureSpi {
        public SHA1() {
            super(2);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "SHA1withECDSA";
        }
    }

    /* loaded from: classes3.dex */
    public static final class SHA224 extends AndroidKeyStoreECDSASignatureSpi {
        public SHA224() {
            super(3);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "SHA224withECDSA";
        }
    }

    /* loaded from: classes3.dex */
    public static final class SHA256 extends AndroidKeyStoreECDSASignatureSpi {
        public SHA256() {
            super(4);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "SHA256withECDSA";
        }
    }

    /* loaded from: classes3.dex */
    public static final class SHA384 extends AndroidKeyStoreECDSASignatureSpi {
        public SHA384() {
            super(5);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "SHA384withECDSA";
        }
    }

    /* loaded from: classes3.dex */
    public static final class SHA512 extends AndroidKeyStoreECDSASignatureSpi {
        public SHA512() {
            super(6);
        }

        @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
        protected String getAlgorithm() {
            return "SHA512withECDSA";
        }
    }

    AndroidKeyStoreECDSASignatureSpi(int keymasterDigest) {
        this.mKeymasterDigest = keymasterDigest;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
    public final void initKey(AndroidKeyStoreKey key) throws InvalidKeyException {
        Set<String> set;
        if (!ACCEPTED_SIGNING_SCHEMES.contains(key.getAlgorithm().toLowerCase())) {
            throw new InvalidKeyException("Unsupported key algorithm: " + key.getAlgorithm() + ". Only" + Arrays.toString(set.stream().toArray()) + " supported");
        }
        long keySizeBits = -1;
        Authorization[] authorizations = key.getAuthorizations();
        int length = authorizations.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Authorization a = authorizations[i];
            if (a.keyParameter.tag == 805306371) {
                keySizeBits = KeyStore2ParameterUtils.getUnsignedInt(a);
                break;
            } else if (a.keyParameter.tag != 268435466) {
                i++;
            } else {
                keySizeBits = KeyProperties.EcCurve.fromKeymasterCurve(a.keyParameter.value.getEcCurve());
                break;
            }
        }
        if (keySizeBits == -1) {
            throw new InvalidKeyException("Size of key not known");
        }
        if (keySizeBits > 2147483647L) {
            throw new InvalidKeyException("Key too large: " + keySizeBits + " bits");
        }
        this.mGroupSizeBits = (int) keySizeBits;
        super.initKey(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
    public final void resetAll() {
        this.mGroupSizeBits = -1;
        super.resetAll();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
    public final void resetWhilePreservingInitState() {
        super.resetWhilePreservingInitState();
    }

    @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
    protected final void addAlgorithmSpecificParametersToBegin(List<KeyParameter> parameters) {
        parameters.add(KeyStore2ParameterUtils.makeEnum(268435458, 3));
        parameters.add(KeyStore2ParameterUtils.makeEnum(536870917, this.mKeymasterDigest));
    }

    @Override // android.security.keystore2.AndroidKeyStoreSignatureSpiBase
    protected final int getAdditionalEntropyAmountForSign() {
        return (this.mGroupSizeBits + 7) / 8;
    }

    protected final int getGroupSizeBits() {
        int i = this.mGroupSizeBits;
        if (i == -1) {
            throw new IllegalStateException("Not initialized");
        }
        return i;
    }
}
