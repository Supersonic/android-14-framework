package android.privacy;
/* loaded from: classes3.dex */
public interface DifferentialPrivacyEncoder {
    byte[] encodeBits(byte[] bArr);

    byte[] encodeBoolean(boolean z);

    byte[] encodeString(String str);

    DifferentialPrivacyConfig getConfig();

    boolean isInsecureEncoderForTest();
}
