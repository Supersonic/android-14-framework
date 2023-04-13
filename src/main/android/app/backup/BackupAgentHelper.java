package android.app.backup;

import android.p008os.ParcelFileDescriptor;
import java.io.IOException;
/* loaded from: classes.dex */
public class BackupAgentHelper extends BackupAgent {
    static final String TAG = "BackupAgentHelper";
    BackupHelperDispatcher mDispatcher = new BackupHelperDispatcher();

    @Override // android.app.backup.BackupAgent
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        this.mDispatcher.performBackup(oldState, data, newState);
    }

    @Override // android.app.backup.BackupAgent
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        this.mDispatcher.performRestore(data, appVersionCode, newState);
    }

    public BackupHelperDispatcher getDispatcher() {
        return this.mDispatcher;
    }

    public void addHelper(String keyPrefix, BackupHelper helper) {
        this.mDispatcher.addHelper(keyPrefix, helper);
    }
}
