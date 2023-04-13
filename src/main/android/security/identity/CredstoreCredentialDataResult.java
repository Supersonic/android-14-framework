package android.security.identity;

import android.security.identity.CredentialDataResult;
import java.util.Collection;
import java.util.LinkedList;
/* loaded from: classes3.dex */
class CredstoreCredentialDataResult extends CredentialDataResult {
    CredstoreEntries mDeviceSignedEntries;
    ResultData mDeviceSignedResult;
    CredstoreEntries mIssuerSignedEntries;
    ResultData mIssuerSignedResult;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CredstoreCredentialDataResult(ResultData deviceSignedResult, ResultData issuerSignedResult) {
        this.mDeviceSignedResult = deviceSignedResult;
        this.mIssuerSignedResult = issuerSignedResult;
        this.mDeviceSignedEntries = new CredstoreEntries(deviceSignedResult);
        this.mIssuerSignedEntries = new CredstoreEntries(issuerSignedResult);
    }

    @Override // android.security.identity.CredentialDataResult
    public byte[] getDeviceNameSpaces() {
        return this.mDeviceSignedResult.getAuthenticatedData();
    }

    @Override // android.security.identity.CredentialDataResult
    public byte[] getDeviceMac() {
        return this.mDeviceSignedResult.getMessageAuthenticationCode();
    }

    @Override // android.security.identity.CredentialDataResult
    public byte[] getDeviceSignature() {
        return this.mDeviceSignedResult.getSignature();
    }

    @Override // android.security.identity.CredentialDataResult
    public byte[] getStaticAuthenticationData() {
        return this.mDeviceSignedResult.getStaticAuthenticationData();
    }

    @Override // android.security.identity.CredentialDataResult
    public CredentialDataResult.Entries getDeviceSignedEntries() {
        return this.mDeviceSignedEntries;
    }

    @Override // android.security.identity.CredentialDataResult
    public CredentialDataResult.Entries getIssuerSignedEntries() {
        return this.mIssuerSignedEntries;
    }

    /* loaded from: classes3.dex */
    static class CredstoreEntries implements CredentialDataResult.Entries {
        ResultData mResultData;

        CredstoreEntries(ResultData resultData) {
            this.mResultData = resultData;
        }

        @Override // android.security.identity.CredentialDataResult.Entries
        public Collection<String> getNamespaces() {
            return this.mResultData.getNamespaces();
        }

        @Override // android.security.identity.CredentialDataResult.Entries
        public Collection<String> getEntryNames(String namespaceName) {
            Collection<String> ret = this.mResultData.getEntryNames(namespaceName);
            if (ret == null) {
                return new LinkedList<>();
            }
            return ret;
        }

        @Override // android.security.identity.CredentialDataResult.Entries
        public Collection<String> getRetrievedEntryNames(String namespaceName) {
            Collection<String> ret = this.mResultData.getRetrievedEntryNames(namespaceName);
            if (ret == null) {
                return new LinkedList<>();
            }
            return ret;
        }

        @Override // android.security.identity.CredentialDataResult.Entries
        public int getStatus(String namespaceName, String name) {
            return this.mResultData.getStatus(namespaceName, name);
        }

        @Override // android.security.identity.CredentialDataResult.Entries
        public byte[] getEntry(String namespaceName, String name) {
            return this.mResultData.getEntry(namespaceName, name);
        }
    }
}
