package android.app.backup;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
/* loaded from: classes.dex */
public class BackupHelperDispatcher {
    private static final String TAG = "BackupHelperDispatcher";
    TreeMap<String, BackupHelper> mHelpers = new TreeMap<>();

    private static native int allocateHeader_native(Header header, FileDescriptor fileDescriptor);

    private static native int readHeader_native(Header header, FileDescriptor fileDescriptor);

    private static native int skipChunk_native(FileDescriptor fileDescriptor, int i);

    private static native int writeHeader_native(Header header, FileDescriptor fileDescriptor, int i);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Header {
        int chunkSize;
        String keyPrefix;

        private Header() {
        }
    }

    public void addHelper(String keyPrefix, BackupHelper helper) {
        this.mHelpers.put(keyPrefix, helper);
    }

    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        Header header = new Header();
        TreeMap<String, BackupHelper> helpers = (TreeMap) this.mHelpers.clone();
        if (oldState != null) {
            FileDescriptor oldStateFD = oldState.getFileDescriptor();
            while (true) {
                int err = readHeader_native(header, oldStateFD);
                if (err < 0) {
                    break;
                } else if (err == 0) {
                    BackupHelper helper = helpers.get(header.keyPrefix);
                    Log.m112d(TAG, "handling existing helper '" + header.keyPrefix + "' " + helper);
                    if (helper != null) {
                        doOneBackup(oldState, data, newState, header, helper);
                        helpers.remove(header.keyPrefix);
                    } else {
                        skipChunk_native(oldStateFD, header.chunkSize);
                    }
                }
            }
        }
        for (Map.Entry<String, BackupHelper> entry : helpers.entrySet()) {
            header.keyPrefix = entry.getKey();
            Log.m112d(TAG, "handling new helper '" + header.keyPrefix + "'");
            doOneBackup(oldState, data, newState, header, entry.getValue());
        }
    }

    private void doOneBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState, Header header, BackupHelper helper) throws IOException {
        FileDescriptor newStateFD = newState.getFileDescriptor();
        int pos = allocateHeader_native(header, newStateFD);
        if (pos < 0) {
            throw new IOException("allocateHeader_native failed (error " + pos + NavigationBarInflaterView.KEY_CODE_END);
        }
        data.setKeyPrefix(header.keyPrefix);
        helper.performBackup(oldState, data, newState);
        int err = writeHeader_native(header, newStateFD, pos);
        if (err != 0) {
            throw new IOException("writeHeader_native failed (error " + err + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public void performRestore(BackupDataInput input, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        boolean alreadyComplained = false;
        BackupDataInputStream stream = new BackupDataInputStream(input);
        while (input.readNextHeader()) {
            String rawKey = input.getKey();
            int pos = rawKey.indexOf(58);
            if (pos > 0) {
                String prefix = rawKey.substring(0, pos);
                BackupHelper helper = this.mHelpers.get(prefix);
                if (helper != null) {
                    stream.dataSize = input.getDataSize();
                    stream.key = rawKey.substring(pos + 1);
                    helper.restoreEntity(stream);
                } else if (!alreadyComplained) {
                    Log.m104w(TAG, "Couldn't find helper for: '" + rawKey + "'");
                    alreadyComplained = true;
                }
            } else if (!alreadyComplained) {
                Log.m104w(TAG, "Entity with no prefix: '" + rawKey + "'");
                alreadyComplained = true;
            }
            input.skipEntityData();
        }
        for (BackupHelper helper2 : this.mHelpers.values()) {
            helper2.writeNewStateDescription(newState);
        }
    }
}
