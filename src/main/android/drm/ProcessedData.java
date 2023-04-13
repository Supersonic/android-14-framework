package android.drm;
@Deprecated
/* loaded from: classes.dex */
public class ProcessedData {
    private String mAccountId;
    private final byte[] mData;
    private String mSubscriptionId;

    ProcessedData(byte[] data, String accountId) {
        this.mAccountId = "_NO_USER";
        this.mSubscriptionId = "";
        this.mData = data;
        this.mAccountId = accountId;
    }

    ProcessedData(byte[] data, String accountId, String subscriptionId) {
        this.mAccountId = "_NO_USER";
        this.mSubscriptionId = "";
        this.mData = data;
        this.mAccountId = accountId;
        this.mSubscriptionId = subscriptionId;
    }

    public byte[] getData() {
        return this.mData;
    }

    public String getAccountId() {
        return this.mAccountId;
    }

    public String getSubscriptionId() {
        return this.mSubscriptionId;
    }
}
