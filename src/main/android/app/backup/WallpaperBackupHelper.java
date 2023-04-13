package android.app.backup;

import android.app.WallpaperManager;
import android.content.Context;
import android.p008os.Environment;
import android.p008os.ParcelFileDescriptor;
import android.util.Slog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public class WallpaperBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final boolean DEBUG = false;
    private static final String STAGE_FILE = new File(Environment.getUserSystemDirectory(0), "wallpaper-tmp").getAbsolutePath();
    private static final String TAG = "WallpaperBackupHelper";
    public static final String WALLPAPER_IMAGE_KEY = "/data/data/com.android.settings/files/wallpaper";
    public static final String WALLPAPER_INFO_KEY = "/data/system/wallpaper_info.xml";
    private final String[] mKeys;
    private final WallpaperManager mWpm;

    @Override // android.app.backup.FileBackupHelperBase, android.app.backup.BackupHelper
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor) {
        super.writeNewStateDescription(parcelFileDescriptor);
    }

    public WallpaperBackupHelper(Context context, String[] keys) {
        super(context);
        this.mContext = context;
        this.mKeys = keys;
        this.mWpm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        if (this.mWpm == null) {
            Slog.m90w(TAG, "restoreEntity(): no wallpaper service");
            return;
        }
        String key = data.getKey();
        if (isKeyInList(key, this.mKeys) && key.equals(WALLPAPER_IMAGE_KEY)) {
            File stage = new File(STAGE_FILE);
            try {
                if (writeFile(stage, data)) {
                    try {
                        FileInputStream in = new FileInputStream(stage);
                        try {
                            this.mWpm.setStream(in);
                            in.close();
                        } catch (Throwable th) {
                            try {
                                in.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (IOException e) {
                        Slog.m96e(TAG, "Unable to set restored wallpaper: " + e.getMessage());
                    }
                } else {
                    Slog.m96e(TAG, "Unable to save restored wallpaper");
                }
            } finally {
                stage.delete();
            }
        }
    }
}
