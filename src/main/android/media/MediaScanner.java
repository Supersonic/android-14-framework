package android.media;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.p008os.RemoteException;
@Deprecated
/* loaded from: classes2.dex */
public class MediaScanner implements AutoCloseable {
    @Deprecated
    private static final String[] FILES_PRESCAN_PROJECTION = new String[0];
    @Deprecated
    private final Uri mAudioUri;
    @Deprecated
    private final MyMediaScannerClient mClient = new MyMediaScannerClient();
    @Deprecated
    private final Context mContext;
    @Deprecated
    private String mDefaultAlarmAlertFilename;
    @Deprecated
    private String mDefaultNotificationFilename;
    @Deprecated
    private String mDefaultRingtoneFilename;
    @Deprecated
    private final Uri mFilesUri;
    @Deprecated
    private MediaInserter mMediaInserter;
    @Deprecated
    private final String mPackageName;

    /* loaded from: classes2.dex */
    private static class FileEntry {
        @Deprecated
        boolean mLastModifiedChanged;
        @Deprecated
        long mRowId;

        @Deprecated
        FileEntry(long rowId, String path, long lastModified, int format) {
            throw new UnsupportedOperationException();
        }
    }

    @Deprecated
    public MediaScanner(Context c, String volumeName) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    private boolean isDrmEnabled() {
        throw new UnsupportedOperationException();
    }

    /* loaded from: classes2.dex */
    private class MyMediaScannerClient implements MediaScannerClient {
        @Deprecated
        private int mFileType;
        @Deprecated
        private boolean mIsDrm;
        @Deprecated
        private String mMimeType;
        @Deprecated
        private boolean mNoMedia;
        @Deprecated
        private String mPath;

        public MyMediaScannerClient() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public FileEntry beginFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            throw new UnsupportedOperationException();
        }

        @Override // android.media.MediaScannerClient
        @Deprecated
        public void scanFile(String path, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        public Uri doScanFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean scanAlways, boolean noMedia) {
            throw new UnsupportedOperationException();
        }

        @Override // android.media.MediaScannerClient
        @Deprecated
        public void handleStringTag(String name, String value) {
            throw new UnsupportedOperationException();
        }

        @Override // android.media.MediaScannerClient
        @Deprecated
        public void setMimeType(String mimeType) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        private ContentValues toValues() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        private Uri endFile(FileEntry entry, boolean ringtones, boolean notifications, boolean alarms, boolean podcasts, boolean audiobooks, boolean music) throws RemoteException {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        private int getFileTypeFromDrm(String path) {
            throw new UnsupportedOperationException();
        }
    }

    @Deprecated
    private void prescan(String filePath, boolean prescanFiles) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    private void postscan(String[] directories) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Uri scanSingleFile(String path, String mimeType) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public static boolean isNoMediaPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    FileEntry makeEntryFor(String path) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    private void setLocale(String locale) {
        throw new UnsupportedOperationException();
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        throw new UnsupportedOperationException();
    }
}
