package com.android.server.locksettings.recoverablekeystore.serialization;

import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.KeyDerivationParams;
import android.security.keystore.recovery.WrappedApplicationKey;
import android.util.Base64;
import android.util.Xml;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.util.List;
/* loaded from: classes2.dex */
public class KeyChainSnapshotSerializer {
    public static void serialize(KeyChainSnapshot keyChainSnapshot, OutputStream outputStream) throws IOException, CertificateEncodingException {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
        resolveSerializer.startDocument((String) null, (Boolean) null);
        String str = KeyChainSnapshotSchema.NAMESPACE;
        resolveSerializer.startTag(str, "keyChainSnapshot");
        writeKeyChainSnapshotProperties(resolveSerializer, keyChainSnapshot);
        writeKeyChainProtectionParams(resolveSerializer, keyChainSnapshot.getKeyChainProtectionParams());
        writeApplicationKeys(resolveSerializer, keyChainSnapshot.getWrappedApplicationKeys());
        resolveSerializer.endTag(str, "keyChainSnapshot");
        resolveSerializer.endDocument();
    }

    public static void writeApplicationKeys(TypedXmlSerializer typedXmlSerializer, List<WrappedApplicationKey> list) throws IOException {
        typedXmlSerializer.startTag(KeyChainSnapshotSchema.NAMESPACE, "applicationKeysList");
        for (WrappedApplicationKey wrappedApplicationKey : list) {
            String str = KeyChainSnapshotSchema.NAMESPACE;
            typedXmlSerializer.startTag(str, "applicationKey");
            writeApplicationKeyProperties(typedXmlSerializer, wrappedApplicationKey);
            typedXmlSerializer.endTag(str, "applicationKey");
        }
        typedXmlSerializer.endTag(KeyChainSnapshotSchema.NAMESPACE, "applicationKeysList");
    }

    public static void writeApplicationKeyProperties(TypedXmlSerializer typedXmlSerializer, WrappedApplicationKey wrappedApplicationKey) throws IOException {
        writePropertyTag(typedXmlSerializer, "alias", wrappedApplicationKey.getAlias());
        writePropertyTag(typedXmlSerializer, "keyMaterial", wrappedApplicationKey.getEncryptedKeyMaterial());
        writePropertyTag(typedXmlSerializer, "keyMetadata", wrappedApplicationKey.getMetadata());
    }

    public static void writeKeyChainProtectionParams(TypedXmlSerializer typedXmlSerializer, List<KeyChainProtectionParams> list) throws IOException {
        typedXmlSerializer.startTag(KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParamsList");
        for (KeyChainProtectionParams keyChainProtectionParams : list) {
            String str = KeyChainSnapshotSchema.NAMESPACE;
            typedXmlSerializer.startTag(str, "keyChainProtectionParams");
            writeKeyChainProtectionParamsProperties(typedXmlSerializer, keyChainProtectionParams);
            typedXmlSerializer.endTag(str, "keyChainProtectionParams");
        }
        typedXmlSerializer.endTag(KeyChainSnapshotSchema.NAMESPACE, "keyChainProtectionParamsList");
    }

    public static void writeKeyChainProtectionParamsProperties(TypedXmlSerializer typedXmlSerializer, KeyChainProtectionParams keyChainProtectionParams) throws IOException {
        writePropertyTag(typedXmlSerializer, "userSecretType", keyChainProtectionParams.getUserSecretType());
        writePropertyTag(typedXmlSerializer, "lockScreenUiType", keyChainProtectionParams.getLockScreenUiFormat());
        writeKeyDerivationParams(typedXmlSerializer, keyChainProtectionParams.getKeyDerivationParams());
    }

    public static void writeKeyDerivationParams(TypedXmlSerializer typedXmlSerializer, KeyDerivationParams keyDerivationParams) throws IOException {
        String str = KeyChainSnapshotSchema.NAMESPACE;
        typedXmlSerializer.startTag(str, "keyDerivationParams");
        writeKeyDerivationParamsProperties(typedXmlSerializer, keyDerivationParams);
        typedXmlSerializer.endTag(str, "keyDerivationParams");
    }

    public static void writeKeyDerivationParamsProperties(TypedXmlSerializer typedXmlSerializer, KeyDerivationParams keyDerivationParams) throws IOException {
        writePropertyTag(typedXmlSerializer, "algorithm", keyDerivationParams.getAlgorithm());
        writePropertyTag(typedXmlSerializer, "salt", keyDerivationParams.getSalt());
        writePropertyTag(typedXmlSerializer, "memoryDifficulty", keyDerivationParams.getMemoryDifficulty());
    }

    public static void writeKeyChainSnapshotProperties(TypedXmlSerializer typedXmlSerializer, KeyChainSnapshot keyChainSnapshot) throws IOException, CertificateEncodingException {
        writePropertyTag(typedXmlSerializer, "snapshotVersion", keyChainSnapshot.getSnapshotVersion());
        writePropertyTag(typedXmlSerializer, "maxAttempts", keyChainSnapshot.getMaxAttempts());
        writePropertyTag(typedXmlSerializer, "counterId", keyChainSnapshot.getCounterId());
        writePropertyTag(typedXmlSerializer, "recoveryKeyMaterial", keyChainSnapshot.getEncryptedRecoveryKeyBlob());
        writePropertyTag(typedXmlSerializer, "serverParams", keyChainSnapshot.getServerParams());
        writePropertyTag(typedXmlSerializer, "thmCertPath", keyChainSnapshot.getTrustedHardwareCertPath());
    }

    public static void writePropertyTag(TypedXmlSerializer typedXmlSerializer, String str, long j) throws IOException {
        String str2 = KeyChainSnapshotSchema.NAMESPACE;
        typedXmlSerializer.startTag(str2, str);
        typedXmlSerializer.text(Long.toString(j));
        typedXmlSerializer.endTag(str2, str);
    }

    public static void writePropertyTag(TypedXmlSerializer typedXmlSerializer, String str, String str2) throws IOException {
        String str3 = KeyChainSnapshotSchema.NAMESPACE;
        typedXmlSerializer.startTag(str3, str);
        typedXmlSerializer.text(str2);
        typedXmlSerializer.endTag(str3, str);
    }

    public static void writePropertyTag(TypedXmlSerializer typedXmlSerializer, String str, byte[] bArr) throws IOException {
        if (bArr == null) {
            return;
        }
        String str2 = KeyChainSnapshotSchema.NAMESPACE;
        typedXmlSerializer.startTag(str2, str);
        typedXmlSerializer.text(Base64.encodeToString(bArr, 0));
        typedXmlSerializer.endTag(str2, str);
    }

    public static void writePropertyTag(TypedXmlSerializer typedXmlSerializer, String str, CertPath certPath) throws IOException, CertificateEncodingException {
        writePropertyTag(typedXmlSerializer, str, certPath.getEncoded("PkiPath"));
    }
}
