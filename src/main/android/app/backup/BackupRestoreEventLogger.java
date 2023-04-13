package android.app.backup;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.Slog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SystemApi
/* loaded from: classes.dex */
public class BackupRestoreEventLogger {
    public static final int DATA_TYPES_ALLOWED = 15;
    private static final String TAG = "BackupRestoreEventLogger";
    private final MessageDigest mHashDigest;
    private final int mOperationType;
    private final Map<String, DataTypeResult> mResults = new HashMap();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackupRestoreDataType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackupRestoreError {
    }

    public BackupRestoreEventLogger(int operationType) {
        this.mOperationType = operationType;
        MessageDigest hashDigest = null;
        try {
            hashDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Slog.m88w("Couldn't create MessageDigest for hash computation", e);
        }
        this.mHashDigest = hashDigest;
    }

    public void logItemsBackedUp(String dataType, int count) {
        logSuccess(0, dataType, count);
    }

    public void logItemsBackupFailed(String dataType, int count, String error) {
        logFailure(0, dataType, count, error);
    }

    public void logBackupMetaData(String dataType, String metaData) {
        logMetaData(0, dataType, metaData);
    }

    public void logItemsRestored(String dataType, int count) {
        logSuccess(1, dataType, count);
    }

    public void logItemsRestoreFailed(String dataType, int count, String error) {
        logFailure(1, dataType, count, error);
    }

    public void logRestoreMetadata(String dataType, String metadata) {
        logMetaData(1, dataType, metadata);
    }

    public List<DataTypeResult> getLoggingResults() {
        return new ArrayList(this.mResults.values());
    }

    public int getOperationType() {
        return this.mOperationType;
    }

    public void clearData() {
        this.mResults.clear();
    }

    private void logSuccess(int operationType, String dataType, int count) {
        DataTypeResult dataTypeResult = getDataTypeResult(operationType, dataType);
        if (dataTypeResult == null) {
            return;
        }
        dataTypeResult.mSuccessCount += count;
        this.mResults.put(dataType, dataTypeResult);
    }

    private void logFailure(int operationType, String dataType, int count, String error) {
        DataTypeResult dataTypeResult = getDataTypeResult(operationType, dataType);
        if (dataTypeResult == null) {
            return;
        }
        dataTypeResult.mFailCount += count;
        if (error != null) {
            dataTypeResult.mErrors.merge(error, Integer.valueOf(count), new BackupRestoreEventLogger$$ExternalSyntheticLambda0());
        }
    }

    private void logMetaData(int operationType, String dataType, String metaData) {
        DataTypeResult dataTypeResult;
        if (this.mHashDigest == null || (dataTypeResult = getDataTypeResult(operationType, dataType)) == null) {
            return;
        }
        dataTypeResult.mMetadataHash = getMetaDataHash(metaData);
    }

    private DataTypeResult getDataTypeResult(int operationType, String dataType) {
        if (operationType != this.mOperationType) {
            Slog.m98d(TAG, "Operation type mismatch: logger created for " + this.mOperationType + ", trying to log for " + operationType);
            return null;
        }
        if (!this.mResults.containsKey(dataType)) {
            if (this.mResults.keySet().size() == 15) {
                Slog.m98d(TAG, "Logger is full, ignoring new data type");
                return null;
            }
            this.mResults.put(dataType, new DataTypeResult(dataType));
        }
        return this.mResults.get(dataType);
    }

    private byte[] getMetaDataHash(String metaData) {
        return this.mHashDigest.digest(metaData.getBytes(StandardCharsets.UTF_8));
    }

    /* loaded from: classes.dex */
    public static final class DataTypeResult implements Parcelable {
        public static final Parcelable.Creator<DataTypeResult> CREATOR = new Parcelable.Creator<DataTypeResult>() { // from class: android.app.backup.BackupRestoreEventLogger.DataTypeResult.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DataTypeResult createFromParcel(Parcel in) {
                String dataType = in.readString();
                int successCount = in.readInt();
                int failCount = in.readInt();
                ArrayMap arrayMap = new ArrayMap();
                Bundle errorsBundle = in.readBundle(getClass().getClassLoader());
                for (String key : errorsBundle.keySet()) {
                    arrayMap.put(key, Integer.valueOf(errorsBundle.getInt(key)));
                }
                byte[] metadataHash = in.createByteArray();
                DataTypeResult result = new DataTypeResult(dataType);
                result.mSuccessCount = successCount;
                result.mFailCount = failCount;
                result.mErrors.putAll(arrayMap);
                result.mMetadataHash = metadataHash;
                return result;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DataTypeResult[] newArray(int size) {
                return new DataTypeResult[size];
            }
        };
        private final String mDataType;
        private final Map<String, Integer> mErrors = new HashMap();
        private int mFailCount;
        private byte[] mMetadataHash;
        private int mSuccessCount;

        public DataTypeResult(String dataType) {
            this.mDataType = dataType;
        }

        public String getDataType() {
            return this.mDataType;
        }

        public int getSuccessCount() {
            return this.mSuccessCount;
        }

        public int getFailCount() {
            return this.mFailCount;
        }

        public Map<String, Integer> getErrors() {
            return this.mErrors;
        }

        public byte[] getMetadataHash() {
            return this.mMetadataHash;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mDataType);
            dest.writeInt(this.mSuccessCount);
            dest.writeInt(this.mFailCount);
            Bundle errorsBundle = new Bundle();
            for (Map.Entry<String, Integer> e : this.mErrors.entrySet()) {
                errorsBundle.putInt(e.getKey(), e.getValue().intValue());
            }
            dest.writeBundle(errorsBundle);
            dest.writeByteArray(this.mMetadataHash);
        }
    }
}
