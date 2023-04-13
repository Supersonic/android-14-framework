package android.telephony.mbms;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.p001pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.p008os.ParcelFileDescriptor;
import android.telephony.MbmsDownloadSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes3.dex */
public class MbmsTempFileProvider extends ContentProvider {
    public static final String TEMP_FILE_ROOT_PREF_FILE_NAME = "MbmsTempFileRootPrefs";
    public static final String TEMP_FILE_ROOT_PREF_NAME = "mbms_temp_file_root";
    private String mAuthority;
    private Context mContext;

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("No querying supported");
    }

    @Override // android.content.ContentProvider, android.content.ContentInterface
    public String getType(Uri uri) {
        return "application/octet-stream";
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No inserting supported");
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No deleting supported");
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No updating supported");
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = getFileForUri(this.mContext, this.mAuthority, uri);
        int fileMode = ParcelFileDescriptor.parseMode(mode);
        return ParcelFileDescriptor.open(file, fileMode);
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grant uri permissions");
        }
        this.mAuthority = info.authority;
        this.mContext = context;
    }

    public static Uri getUriForFile(Context context, String authority, File file) {
        String pathFragment;
        try {
            String filePath = file.getCanonicalPath();
            File tempFileDir = getEmbmsTempFileDir(context);
            if (!MbmsUtils.isContainedIn(tempFileDir, file)) {
                throw new IllegalArgumentException("File " + file + " is not contained in the temp file directory, which is " + tempFileDir);
            }
            try {
                String tempFileDirPath = tempFileDir.getCanonicalPath();
                if (tempFileDirPath.endsWith("/")) {
                    pathFragment = filePath.substring(tempFileDirPath.length());
                } else {
                    pathFragment = filePath.substring(tempFileDirPath.length() + 1);
                }
                String encodedPath = Uri.encode(pathFragment);
                return new Uri.Builder().scheme("content").authority(authority).encodedPath(encodedPath).build();
            } catch (IOException e) {
                throw new RuntimeException("Could not get canonical path for temp file root dir " + tempFileDir);
            }
        } catch (IOException e2) {
            throw new IllegalArgumentException("Could not get canonical path for file " + file);
        }
    }

    public static File getFileForUri(Context context, String authority, Uri uri) throws FileNotFoundException {
        if (!"content".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Uri must have scheme content");
        }
        if (!Objects.equals(authority, uri.getAuthority())) {
            throw new IllegalArgumentException("Uri does not have a matching authority: " + authority + ", " + uri.getAuthority());
        }
        String relPath = Uri.decode(uri.getEncodedPath());
        try {
            File tempFileDir = getEmbmsTempFileDir(context).getCanonicalFile();
            File file = new File(tempFileDir, relPath).getCanonicalFile();
            if (!file.getPath().startsWith(tempFileDir.getPath())) {
                throw new SecurityException("Resolved path jumped beyond configured root");
            }
            return file;
        } catch (IOException e) {
            throw new FileNotFoundException("Could not resolve paths");
        }
    }

    public static File getEmbmsTempFileDir(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TEMP_FILE_ROOT_PREF_FILE_NAME, 0);
        String storedTempFileRoot = prefs.getString(TEMP_FILE_ROOT_PREF_NAME, null);
        try {
            if (storedTempFileRoot != null) {
                return new File(storedTempFileRoot).getCanonicalFile();
            }
            return new File(context.getFilesDir(), MbmsDownloadSession.DEFAULT_TOP_LEVEL_TEMP_DIRECTORY).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to canonicalize temp file root path " + e);
        }
    }
}
