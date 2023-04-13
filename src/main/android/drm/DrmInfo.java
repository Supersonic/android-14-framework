package android.drm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
@Deprecated
/* loaded from: classes.dex */
public class DrmInfo {
    private final HashMap<String, Object> mAttributes = new HashMap<>();
    private byte[] mData;
    private final int mInfoType;
    private final String mMimeType;

    public DrmInfo(int infoType, byte[] data, String mimeType) {
        this.mInfoType = infoType;
        this.mMimeType = mimeType;
        this.mData = data;
        if (!isValid()) {
            String msg = "infoType: " + infoType + ",mimeType: " + mimeType + ",data: " + Arrays.toString(data);
            throw new IllegalArgumentException(msg);
        }
    }

    public DrmInfo(int infoType, String path, String mimeType) {
        this.mInfoType = infoType;
        this.mMimeType = mimeType;
        try {
            this.mData = DrmUtils.readBytes(path);
        } catch (IOException e) {
            this.mData = null;
        }
        if (!isValid()) {
            r0 = "infoType: " + infoType + ",mimeType: " + mimeType + ",data: " + Arrays.toString(this.mData);
            throw new IllegalArgumentException();
        }
    }

    public void put(String key, Object value) {
        this.mAttributes.put(key, value);
    }

    public Object get(String key) {
        return this.mAttributes.get(key);
    }

    public Iterator<String> keyIterator() {
        return this.mAttributes.keySet().iterator();
    }

    public Iterator<Object> iterator() {
        return this.mAttributes.values().iterator();
    }

    public byte[] getData() {
        return this.mData;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public int getInfoType() {
        return this.mInfoType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValid() {
        byte[] bArr;
        String str = this.mMimeType;
        return (str == null || str.equals("") || (bArr = this.mData) == null || bArr.length <= 0 || !DrmInfoRequest.isValidType(this.mInfoType)) ? false : true;
    }
}
