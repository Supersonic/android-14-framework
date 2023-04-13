package android.app.backup;

import android.p008os.ParcelFileDescriptor;
import android.util.ArrayMap;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
/* loaded from: classes.dex */
public abstract class BlobBackupHelper implements BackupHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "BlobBackupHelper";
    private final int mCurrentBlobVersion;
    private final String[] mKeys;

    protected abstract void applyRestoredPayload(String str, byte[] bArr);

    protected abstract byte[] getBackupPayload(String str);

    public BlobBackupHelper(int currentBlobVersion, String... keys) {
        this.mCurrentBlobVersion = currentBlobVersion;
        this.mKeys = keys;
    }

    private ArrayMap<String, Long> readOldState(ParcelFileDescriptor oldStateFd) {
        ArrayMap<String, Long> state = new ArrayMap<>();
        FileInputStream fis = new FileInputStream(oldStateFd.getFileDescriptor());
        DataInputStream in = new DataInputStream(fis);
        try {
            int version = in.readInt();
            if (version > this.mCurrentBlobVersion) {
                Log.m104w(TAG, "Prior state from unrecognized version " + version);
            } else {
                int numKeys = in.readInt();
                for (int i = 0; i < numKeys; i++) {
                    String key = in.readUTF();
                    long checksum = in.readLong();
                    state.put(key, Long.valueOf(checksum));
                }
            }
        } catch (EOFException e) {
            state.clear();
        } catch (Exception e2) {
            Log.m110e(TAG, "Error examining prior backup state " + e2.getMessage());
            state.clear();
        }
        return state;
    }

    private void writeBackupState(ArrayMap<String, Long> state, ParcelFileDescriptor stateFile) {
        try {
            FileOutputStream fos = new FileOutputStream(stateFile.getFileDescriptor());
            DataOutputStream out = new DataOutputStream(fos);
            out.writeInt(this.mCurrentBlobVersion);
            int N = state != null ? state.size() : 0;
            out.writeInt(N);
            for (int i = 0; i < N; i++) {
                String key = state.keyAt(i);
                long checksum = state.valueAt(i).longValue();
                out.writeUTF(key);
                out.writeLong(checksum);
            }
        } catch (IOException e) {
            Log.m109e(TAG, "Unable to write updated state", e);
        }
    }

    private byte[] deflate(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            DataOutputStream headerOut = new DataOutputStream(sink);
            headerOut.writeInt(this.mCurrentBlobVersion);
            DeflaterOutputStream out = new DeflaterOutputStream(sink);
            out.write(data);
            out.close();
            byte[] result = sink.toByteArray();
            return result;
        } catch (IOException e) {
            Log.m104w(TAG, "Unable to process payload: " + e.getMessage());
            return null;
        }
    }

    private byte[] inflate(byte[] compressedData) {
        if (compressedData == null) {
            return null;
        }
        try {
            ByteArrayInputStream source = new ByteArrayInputStream(compressedData);
            DataInputStream headerIn = new DataInputStream(source);
            int version = headerIn.readInt();
            if (version > this.mCurrentBlobVersion) {
                Log.m104w(TAG, "Saved payload from unrecognized version " + version);
                return null;
            }
            InflaterInputStream in = new InflaterInputStream(source);
            ByteArrayOutputStream inflated = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (true) {
                int nRead = in.read(buffer);
                if (nRead > 0) {
                    inflated.write(buffer, 0, nRead);
                } else {
                    in.close();
                    inflated.flush();
                    byte[] result = inflated.toByteArray();
                    return result;
                }
            }
        } catch (IOException e) {
            Log.m104w(TAG, "Unable to process restored payload: " + e.getMessage());
            return null;
        }
    }

    private long checksum(byte[] buffer) {
        if (buffer != null) {
            try {
                CRC32 crc = new CRC32();
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                byte[] buf = new byte[4096];
                while (true) {
                    int nRead = bis.read(buf);
                    if (nRead >= 0) {
                        crc.update(buf, 0, nRead);
                    } else {
                        return crc.getValue();
                    }
                }
            } catch (Exception e) {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldStateFd, BackupDataOutput data, ParcelFileDescriptor newStateFd) {
        String[] strArr;
        ArrayMap<String, Long> oldState = readOldState(oldStateFd);
        ArrayMap<String, Long> newState = new ArrayMap<>();
        try {
            try {
                for (String key : this.mKeys) {
                    byte[] payload = deflate(getBackupPayload(key));
                    long checksum = checksum(payload);
                    newState.put(key, Long.valueOf(checksum));
                    Long oldChecksum = oldState.get(key);
                    if (oldChecksum == null || checksum != oldChecksum.longValue()) {
                        if (payload != null) {
                            data.writeEntityHeader(key, payload.length);
                            data.writeEntityData(payload, payload.length);
                        } else {
                            data.writeEntityHeader(key, -1);
                        }
                    }
                }
            } catch (Exception e) {
                Log.m104w(TAG, "Unable to record notification state: " + e.getMessage());
                newState.clear();
            }
        } finally {
            writeBackupState(newState, newStateFd);
        }
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        String key = data.getKey();
        int which = 0;
        while (true) {
            try {
                String[] strArr = this.mKeys;
                if (which >= strArr.length || key.equals(strArr[which])) {
                    break;
                }
                which++;
            } catch (Exception e) {
                Log.m110e(TAG, "Exception restoring entity " + key + " : " + e.getMessage());
                return;
            }
        }
        if (which >= this.mKeys.length) {
            Log.m110e(TAG, "Unrecognized key " + key + ", ignoring");
            return;
        }
        byte[] compressed = new byte[data.size()];
        data.read(compressed);
        byte[] payload = inflate(compressed);
        applyRestoredPayload(key, payload);
    }

    @Override // android.app.backup.BackupHelper
    public void writeNewStateDescription(ParcelFileDescriptor newState) {
        writeBackupState(null, newState);
    }
}
