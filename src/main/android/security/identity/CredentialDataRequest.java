package android.security.identity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class CredentialDataRequest {
    Map<String, Collection<String>> mDeviceSignedEntriesToRequest = new LinkedHashMap();
    Map<String, Collection<String>> mIssuerSignedEntriesToRequest = new LinkedHashMap();
    boolean mAllowUsingExhaustedKeys = true;
    boolean mAllowUsingExpiredKeys = false;
    boolean mIncrementUseCount = true;
    byte[] mRequestMessage = null;
    byte[] mReaderSignature = null;

    CredentialDataRequest() {
    }

    public Map<String, Collection<String>> getDeviceSignedEntriesToRequest() {
        return this.mDeviceSignedEntriesToRequest;
    }

    public Map<String, Collection<String>> getIssuerSignedEntriesToRequest() {
        return this.mIssuerSignedEntriesToRequest;
    }

    public boolean isAllowUsingExhaustedKeys() {
        return this.mAllowUsingExhaustedKeys;
    }

    public boolean isAllowUsingExpiredKeys() {
        return this.mAllowUsingExpiredKeys;
    }

    public boolean isIncrementUseCount() {
        return this.mIncrementUseCount;
    }

    public byte[] getRequestMessage() {
        return this.mRequestMessage;
    }

    public byte[] getReaderSignature() {
        return this.mReaderSignature;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private CredentialDataRequest mData = new CredentialDataRequest();

        public Builder setDeviceSignedEntriesToRequest(Map<String, Collection<String>> entriesToRequest) {
            this.mData.mDeviceSignedEntriesToRequest = entriesToRequest;
            return this;
        }

        public Builder setIssuerSignedEntriesToRequest(Map<String, Collection<String>> entriesToRequest) {
            this.mData.mIssuerSignedEntriesToRequest = entriesToRequest;
            return this;
        }

        public Builder setAllowUsingExhaustedKeys(boolean allowUsingExhaustedKeys) {
            this.mData.mAllowUsingExhaustedKeys = allowUsingExhaustedKeys;
            return this;
        }

        public Builder setAllowUsingExpiredKeys(boolean allowUsingExpiredKeys) {
            this.mData.mAllowUsingExpiredKeys = allowUsingExpiredKeys;
            return this;
        }

        public Builder setIncrementUseCount(boolean incrementUseCount) {
            this.mData.mIncrementUseCount = incrementUseCount;
            return this;
        }

        public Builder setRequestMessage(byte[] requestMessage) {
            this.mData.mRequestMessage = requestMessage;
            return this;
        }

        public Builder setReaderSignature(byte[] readerSignature) {
            this.mData.mReaderSignature = readerSignature;
            return this;
        }

        public CredentialDataRequest build() {
            return this.mData;
        }
    }
}
