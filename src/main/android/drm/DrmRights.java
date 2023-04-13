package android.drm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
@Deprecated
/* loaded from: classes.dex */
public class DrmRights {
    private String mAccountId;
    private byte[] mData;
    private String mMimeType;
    private String mSubscriptionId;

    public DrmRights(String rightsFilePath, String mimeType) {
        File file = new File(rightsFilePath);
        instantiate(file, mimeType);
    }

    public DrmRights(String rightsFilePath, String mimeType, String accountId) {
        this(rightsFilePath, mimeType);
        this.mAccountId = accountId;
    }

    public DrmRights(String rightsFilePath, String mimeType, String accountId, String subscriptionId) {
        this(rightsFilePath, mimeType);
        this.mAccountId = accountId;
        this.mSubscriptionId = subscriptionId;
    }

    public DrmRights(File rightsFile, String mimeType) {
        instantiate(rightsFile, mimeType);
    }

    private void instantiate(File rightsFile, String mimeType) {
        try {
            this.mData = DrmUtils.readBytes(rightsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mMimeType = mimeType;
        if (!isValid()) {
            String msg = "mimeType: " + this.mMimeType + ",data: " + Arrays.toString(this.mData);
            throw new IllegalArgumentException(msg);
        }
    }

    public DrmRights(ProcessedData data, String mimeType) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        this.mData = data.getData();
        this.mAccountId = data.getAccountId();
        this.mSubscriptionId = data.getSubscriptionId();
        this.mMimeType = mimeType;
        if (!isValid()) {
            String msg = "mimeType: " + this.mMimeType + ",data: " + Arrays.toString(this.mData);
            throw new IllegalArgumentException(msg);
        }
    }

    public byte[] getData() {
        return this.mData;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public String getAccountId() {
        return this.mAccountId;
    }

    public String getSubscriptionId() {
        return this.mSubscriptionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValid() {
        byte[] bArr;
        String str = this.mMimeType;
        return (str == null || str.equals("") || (bArr = this.mData) == null || bArr.length <= 0) ? false : true;
    }
}
