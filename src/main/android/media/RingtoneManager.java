package android.media;

import android.annotation.SystemApi;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.UserInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.StaleDataException;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.VolumeShaper;
import android.net.Uri;
import android.p008os.Environment;
import android.p008os.FileUtils;
import android.p008os.ParcelFileDescriptor;
import android.p008os.SystemProperties;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.database.SortCursor;
import com.google.android.mms.ContentType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class RingtoneManager {
    public static final String ACTION_RINGTONE_PICKER = "android.intent.action.RINGTONE_PICKER";
    public static final String EXTRA_RINGTONE_AUDIO_ATTRIBUTES_FLAGS = "android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS";
    public static final String EXTRA_RINGTONE_DEFAULT_URI = "android.intent.extra.ringtone.DEFAULT_URI";
    public static final String EXTRA_RINGTONE_EXISTING_URI = "android.intent.extra.ringtone.EXISTING_URI";
    @Deprecated
    public static final String EXTRA_RINGTONE_INCLUDE_DRM = "android.intent.extra.ringtone.INCLUDE_DRM";
    public static final String EXTRA_RINGTONE_PICKED_URI = "android.intent.extra.ringtone.PICKED_URI";
    public static final String EXTRA_RINGTONE_SHOW_DEFAULT = "android.intent.extra.ringtone.SHOW_DEFAULT";
    public static final String EXTRA_RINGTONE_SHOW_SILENT = "android.intent.extra.ringtone.SHOW_SILENT";
    public static final String EXTRA_RINGTONE_TITLE = "android.intent.extra.ringtone.TITLE";
    public static final String EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE";
    public static final int ID_COLUMN_INDEX = 0;
    private static final String[] INTERNAL_COLUMNS = {"_id", "title", "title", "title_key"};
    private static final String[] MEDIA_COLUMNS = {"_id", "title", "title", "title_key"};
    private static final String TAG = "RingtoneManager";
    public static final int TITLE_COLUMN_INDEX = 1;
    public static final int TYPE_ALARM = 4;
    public static final int TYPE_ALL = 7;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_RINGTONE = 1;
    public static final int URI_COLUMN_INDEX = 2;
    private final Activity mActivity;
    private final Context mContext;
    private Cursor mCursor;
    private final List<String> mFilterColumns;
    private boolean mIncludeParentRingtones;
    private Ringtone mPreviousRingtone;
    private boolean mStopPreviousRingtone;
    private int mType;

    public RingtoneManager(Activity activity) {
        this(activity, false);
    }

    public RingtoneManager(Activity activity, boolean includeParentRingtones) {
        this.mType = 1;
        this.mFilterColumns = new ArrayList();
        this.mStopPreviousRingtone = true;
        this.mActivity = activity;
        this.mContext = activity;
        setType(this.mType);
        this.mIncludeParentRingtones = includeParentRingtones;
    }

    public RingtoneManager(Context context) {
        this(context, false);
    }

    public RingtoneManager(Context context, boolean includeParentRingtones) {
        this.mType = 1;
        this.mFilterColumns = new ArrayList();
        this.mStopPreviousRingtone = true;
        this.mActivity = null;
        this.mContext = context;
        setType(this.mType);
        this.mIncludeParentRingtones = includeParentRingtones;
    }

    public void setType(int type) {
        if (this.mCursor != null) {
            throw new IllegalStateException("Setting filter columns should be done before querying for ringtones.");
        }
        this.mType = type;
        setFilterColumnsList(type);
    }

    public int inferStreamType() {
        switch (this.mType) {
            case 2:
                return 5;
            case 3:
            default:
                return 2;
            case 4:
                return 4;
        }
    }

    public void setStopPreviousRingtone(boolean stopPreviousRingtone) {
        this.mStopPreviousRingtone = stopPreviousRingtone;
    }

    public boolean getStopPreviousRingtone() {
        return this.mStopPreviousRingtone;
    }

    public void stopPreviousRingtone() {
        Ringtone ringtone = this.mPreviousRingtone;
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    @Deprecated
    public boolean getIncludeDrm() {
        return false;
    }

    @Deprecated
    public void setIncludeDrm(boolean includeDrm) {
        if (includeDrm) {
            Log.m104w(TAG, "setIncludeDrm no longer supported");
        }
    }

    public Cursor getCursor() {
        Cursor parentRingtonesCursor;
        Cursor cursor = this.mCursor;
        if (cursor != null && cursor.requery()) {
            return this.mCursor;
        }
        ArrayList<Cursor> ringtoneCursors = new ArrayList<>();
        ringtoneCursors.add(getInternalRingtones());
        ringtoneCursors.add(getMediaRingtones());
        if (this.mIncludeParentRingtones && (parentRingtonesCursor = getParentProfileRingtones()) != null) {
            ringtoneCursors.add(parentRingtonesCursor);
        }
        SortCursor sortCursor = new SortCursor((Cursor[]) ringtoneCursors.toArray(new Cursor[ringtoneCursors.size()]), "title_key");
        this.mCursor = sortCursor;
        return sortCursor;
    }

    private Cursor getParentProfileRingtones() {
        Context parentContext;
        UserManager um = UserManager.get(this.mContext);
        UserInfo parentInfo = um.getProfileParent(this.mContext.getUserId());
        if (parentInfo != null && parentInfo.f48id != this.mContext.getUserId() && (parentContext = createPackageContextAsUser(this.mContext, parentInfo.f48id)) != null) {
            Cursor res = getMediaRingtones(parentContext);
            return new ExternalRingtonesCursorWrapper(res, ContentProvider.maybeAddUserId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, parentInfo.f48id));
        }
        return null;
    }

    public Ringtone getRingtone(int position) {
        Ringtone ringtone;
        if (this.mStopPreviousRingtone && (ringtone = this.mPreviousRingtone) != null) {
            ringtone.stop();
        }
        Ringtone ringtone2 = getRingtone(this.mContext, getRingtoneUri(position), inferStreamType(), true);
        this.mPreviousRingtone = ringtone2;
        return ringtone2;
    }

    public Uri getRingtoneUri(int position) {
        try {
            Cursor cursor = this.mCursor;
            if (cursor != null) {
                if (cursor.moveToPosition(position)) {
                    return getUriFromCursor(this.mContext, this.mCursor);
                }
            }
            return null;
        } catch (StaleDataException | IllegalStateException e) {
            Log.m109e(TAG, "Unexpected Exception has been catched.", e);
            return null;
        }
    }

    private static Uri getUriFromCursor(Context context, Cursor cursor) {
        Uri uri = ContentUris.withAppendedId(Uri.parse(cursor.getString(2)), cursor.getLong(0));
        return context.getContentResolver().canonicalizeOrElse(uri);
    }

    public int getRingtonePosition(Uri ringtoneUri) {
        if (ringtoneUri == null) {
            return -1;
        }
        try {
            Cursor cursor = getCursor();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                Uri uriFromCursor = getUriFromCursor(this.mContext, cursor);
                if (ringtoneUri.equals(uriFromCursor)) {
                    return cursor.getPosition();
                }
            }
        } catch (NumberFormatException e) {
            Log.m109e(TAG, "NumberFormatException while getting ringtone position, returning -1", e);
        }
        return -1;
    }

    public static Uri getValidRingtoneUri(Context context) {
        RingtoneManager rm = new RingtoneManager(context);
        Uri uri = getValidRingtoneUriFromCursorAndClose(context, rm.getInternalRingtones());
        if (uri == null) {
            return getValidRingtoneUriFromCursorAndClose(context, rm.getMediaRingtones());
        }
        return uri;
    }

    private static Uri getValidRingtoneUriFromCursorAndClose(Context context, Cursor cursor) {
        if (cursor != null) {
            Uri uri = null;
            if (cursor.moveToFirst()) {
                uri = getUriFromCursor(context, cursor);
            }
            cursor.close();
            return uri;
        }
        return null;
    }

    private Cursor getInternalRingtones() {
        Cursor res = query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, INTERNAL_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key");
        return new ExternalRingtonesCursorWrapper(res, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private Cursor getMediaRingtones() {
        Cursor res = getMediaRingtones(this.mContext);
        return new ExternalRingtonesCursorWrapper(res, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    private Cursor getMediaRingtones(Context context) {
        return query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIA_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key", context);
    }

    private void setFilterColumnsList(int type) {
        List<String> columns = this.mFilterColumns;
        columns.clear();
        if ((type & 1) != 0) {
            columns.add("is_ringtone");
        }
        if ((type & 2) != 0) {
            columns.add("is_notification");
        }
        if ((type & 4) != 0) {
            columns.add("is_alarm");
        }
    }

    private static String constructBooleanTrueWhereClause(List<String> columns) {
        if (columns == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(NavigationBarInflaterView.KEY_CODE_START);
        for (int i = columns.size() - 1; i >= 0; i--) {
            sb.append(columns.get(i)).append("=1 or ");
        }
        int i2 = columns.size();
        if (i2 > 0) {
            sb.setLength(sb.length() - 4);
        }
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(uri, projection, selection, selectionArgs, sortOrder, this.mContext);
    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, Context context) {
        Activity activity = this.mActivity;
        if (activity != null) {
            return activity.managedQuery(uri, projection, selection, selectionArgs, sortOrder);
        }
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static Ringtone getRingtone(Context context, Uri ringtoneUri) {
        return getRingtone(context, ringtoneUri, -1, true);
    }

    public static Ringtone getRingtone(Context context, Uri ringtoneUri, VolumeShaper.Configuration volumeShaperConfig) {
        return getRingtone(context, ringtoneUri, -1, volumeShaperConfig, true);
    }

    public static Ringtone getRingtone(Context context, Uri ringtoneUri, VolumeShaper.Configuration volumeShaperConfig, boolean createLocalMediaPlayer) {
        return getRingtone(context, ringtoneUri, -1, volumeShaperConfig, createLocalMediaPlayer);
    }

    public static Ringtone getRingtone(Context context, Uri ringtoneUri, VolumeShaper.Configuration volumeShaperConfig, AudioAttributes audioAttributes) {
        Ringtone ringtone = getRingtone(context, ringtoneUri, -1, volumeShaperConfig, false);
        if (ringtone != null) {
            ringtone.setAudioAttributesField(audioAttributes);
            if (!ringtone.createLocalMediaPlayer()) {
                Log.m110e(TAG, "Failed to open ringtone " + ringtoneUri);
                return null;
            }
        }
        return ringtone;
    }

    private static Ringtone getRingtone(Context context, Uri ringtoneUri, int streamType, boolean createLocalMediaPlayer) {
        return getRingtone(context, ringtoneUri, streamType, null, createLocalMediaPlayer);
    }

    private static Ringtone getRingtone(Context context, Uri ringtoneUri, int streamType, VolumeShaper.Configuration volumeShaperConfig, boolean createLocalMediaPlayer) {
        try {
            Ringtone r = new Ringtone(context, true);
            if (streamType >= 0) {
                r.setStreamType(streamType);
            }
            r.setVolumeShaperConfig(volumeShaperConfig);
            r.setUri(ringtoneUri, volumeShaperConfig);
            if (createLocalMediaPlayer && !r.createLocalMediaPlayer()) {
                Log.m110e(TAG, "Failed to open ringtone " + ringtoneUri);
                return null;
            }
            return r;
        } catch (Exception ex) {
            Log.m110e(TAG, "Failed to open ringtone " + ringtoneUri + ": " + ex);
            return null;
        }
    }

    public static Uri getActualDefaultRingtoneUri(Context context, int type) {
        String setting = getSettingForType(type);
        if (setting == null) {
            return null;
        }
        String uriString = Settings.System.getStringForUser(context.getContentResolver(), setting, context.getUserId());
        Uri ringtoneUri = uriString != null ? Uri.parse(uriString) : null;
        if (ringtoneUri != null && ContentProvider.getUserIdFromUri(ringtoneUri) == context.getUserId()) {
            return ContentProvider.getUriWithoutUserId(ringtoneUri);
        }
        return ringtoneUri;
    }

    public static void setActualDefaultRingtoneUri(Context context, int type, Uri ringtoneUri) {
        String setting = getSettingForType(type);
        if (setting == null) {
            return;
        }
        ContentResolver resolver = context.getContentResolver();
        if (!isInternalRingtoneUri(ringtoneUri)) {
            ringtoneUri = ContentProvider.maybeAddUserId(ringtoneUri, context.getUserId());
        }
        if (ringtoneUri != null) {
            String mimeType = resolver.getType(ringtoneUri);
            if (mimeType == null) {
                Log.m110e(TAG, "setActualDefaultRingtoneUri for URI:" + ringtoneUri + " ignored: failure to find mimeType (no access from this context?)");
                return;
            } else if (!mimeType.startsWith("audio/") && !mimeType.equals(ContentType.AUDIO_OGG)) {
                Log.m110e(TAG, "setActualDefaultRingtoneUri for URI:" + ringtoneUri + " ignored: associated mimeType:" + mimeType + " is not an audio type");
                return;
            }
        }
        Settings.System.putStringForUser(resolver, setting, ringtoneUri != null ? ringtoneUri.toString() : null, context.getUserId());
        if (ringtoneUri != null) {
            Uri cacheUri = getCacheForType(type, context.getUserId());
            try {
                InputStream in = openRingtone(context, ringtoneUri);
                OutputStream out = resolver.openOutputStream(cacheUri, "wt");
                FileUtils.copy(in, out);
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.m104w(TAG, "Failed to cache ringtone: " + e);
            }
        }
    }

    private static boolean isInternalRingtoneUri(Uri uri) {
        return isRingtoneUriInStorage(uri, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private static boolean isExternalRingtoneUri(Uri uri) {
        return isRingtoneUriInStorage(uri, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }

    private static boolean isRingtoneUriInStorage(Uri ringtone, Uri storage) {
        Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(ringtone);
        if (uriWithoutUserId == null) {
            return false;
        }
        return uriWithoutUserId.toString().startsWith(storage.toString());
    }

    public Uri addCustomExternalRingtone(Uri fileUri, int type) throws FileNotFoundException, IllegalArgumentException, IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("External storage is not mounted. Unable to install ringtones.");
        }
        String mimeType = this.mContext.getContentResolver().getType(fileUri);
        if (mimeType == null || (!mimeType.startsWith("audio/") && !mimeType.equals(ContentType.AUDIO_OGG))) {
            throw new IllegalArgumentException("Ringtone file must have MIME type \"audio/*\". Given file has MIME type \"" + mimeType + "\"");
        }
        String subdirectory = getExternalDirectoryForType(type);
        Context context = this.mContext;
        File outFile = Utils.getUniqueExternalFile(context, subdirectory, FileUtils.buildValidFatFilename(Utils.getFileDisplayNameFromUri(context, fileUri)), mimeType);
        InputStream input = this.mContext.getContentResolver().openInputStream(fileUri);
        try {
            OutputStream output = new FileOutputStream(outFile);
            FileUtils.copy(input, output);
            output.close();
            if (input != null) {
                input.close();
            }
            return MediaStore.scanFile(this.mContext.getContentResolver(), outFile);
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static final String getExternalDirectoryForType(int type) {
        switch (type) {
            case 1:
                return Environment.DIRECTORY_RINGTONES;
            case 2:
                return Environment.DIRECTORY_NOTIFICATIONS;
            case 3:
            default:
                throw new IllegalArgumentException("Unsupported ringtone type: " + type);
            case 4:
                return Environment.DIRECTORY_ALARMS;
        }
    }

    private static InputStream openRingtone(Context context, Uri uri) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        try {
            return resolver.openInputStream(uri);
        } catch (IOException | SecurityException e) {
            Log.m104w(TAG, "Failed to open directly; attempting failover: " + e);
            IRingtonePlayer player = ((AudioManager) context.getSystemService(AudioManager.class)).getRingtonePlayer();
            try {
                return new ParcelFileDescriptor.AutoCloseInputStream(player.openRingtone(uri));
            } catch (Exception e2) {
                throw new IOException(e2);
            }
        }
    }

    private static String getSettingForType(int type) {
        if ((type & 1) != 0) {
            return Settings.System.RINGTONE;
        }
        if ((type & 2) != 0) {
            return Settings.System.NOTIFICATION_SOUND;
        }
        if ((type & 4) != 0) {
            return Settings.System.ALARM_ALERT;
        }
        return null;
    }

    public static Uri getCacheForType(int type) {
        return getCacheForType(type, UserHandle.getCallingUserId());
    }

    public static Uri getCacheForType(int type, int userId) {
        if ((type & 1) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.RINGTONE_CACHE_URI, userId);
        }
        if ((type & 2) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.NOTIFICATION_SOUND_CACHE_URI, userId);
        }
        if ((type & 4) != 0) {
            return ContentProvider.maybeAddUserId(Settings.System.ALARM_ALERT_CACHE_URI, userId);
        }
        return null;
    }

    public static boolean isDefault(Uri ringtoneUri) {
        return getDefaultType(ringtoneUri) != -1;
    }

    public static int getDefaultType(Uri defaultRingtoneUri) {
        Uri defaultRingtoneUri2 = ContentProvider.getUriWithoutUserId(defaultRingtoneUri);
        if (defaultRingtoneUri2 == null) {
            return -1;
        }
        if (defaultRingtoneUri2.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
            return 1;
        }
        if (defaultRingtoneUri2.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
            return 2;
        }
        if (!defaultRingtoneUri2.equals(Settings.System.DEFAULT_ALARM_ALERT_URI)) {
            return -1;
        }
        return 4;
    }

    public static Uri getDefaultUri(int type) {
        if ((type & 1) != 0) {
            return Settings.System.DEFAULT_RINGTONE_URI;
        }
        if ((type & 2) != 0) {
            return Settings.System.DEFAULT_NOTIFICATION_URI;
        }
        if ((type & 4) != 0) {
            return Settings.System.DEFAULT_ALARM_ALERT_URI;
        }
        return null;
    }

    public static AssetFileDescriptor openDefaultRingtoneUri(Context context, Uri uri) throws FileNotFoundException {
        int type = getDefaultType(uri);
        Uri cacheUri = getCacheForType(type, context.getUserId());
        Uri actualUri = getActualDefaultRingtoneUri(context, type);
        ContentResolver resolver = context.getContentResolver();
        AssetFileDescriptor afd = null;
        if (cacheUri != null && (afd = resolver.openAssetFileDescriptor(cacheUri, "r")) != null) {
            return afd;
        }
        if (actualUri != null) {
            AssetFileDescriptor afd2 = resolver.openAssetFileDescriptor(actualUri, "r");
            return afd2;
        }
        return afd;
    }

    public boolean hasHapticChannels(int position) {
        return AudioManager.hasHapticChannels(this.mContext, getRingtoneUri(position));
    }

    public static boolean hasHapticChannels(Uri ringtoneUri) {
        return AudioManager.hasHapticChannels(null, ringtoneUri);
    }

    public static boolean hasHapticChannels(Context context, Uri ringtoneUri) {
        return AudioManager.hasHapticChannels(context, ringtoneUri);
    }

    private static Context createPackageContextAsUser(Context context, int userId) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.m145of(userId));
        } catch (PackageManager.NameNotFoundException e) {
            Log.m109e(TAG, "Unable to create package context", e);
            return null;
        }
    }

    @SystemApi
    public static void ensureDefaultRingtones(Context context) {
        Uri ringtoneUri;
        int[] iArr = {1, 2, 4};
        for (int i = 0; i < 3; i++) {
            int type = iArr[i];
            String setting = getDefaultRingtoneSetting(type);
            if (Settings.System.getInt(context.getContentResolver(), setting, 0) == 0 && (ringtoneUri = computeDefaultRingtoneUri(context, type)) != null) {
                setActualDefaultRingtoneUri(context, type, ringtoneUri);
                Settings.System.putInt(context.getContentResolver(), setting, 1);
            }
        }
    }

    private static Uri computeDefaultRingtoneUri(Context context, int type) {
        String filename = getDefaultRingtoneFilename(type);
        String whichAudio = getQueryStringForType(type);
        String where = "_display_name=? AND " + whichAudio + "=?";
        Uri baseUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(baseUri, new String[]{"_id"}, where, new String[]{filename, "1"}, null);
        try {
            if (cursor.moveToFirst()) {
                Uri ringtoneUri = context.getContentResolver().canonicalizeOrElse(ContentUris.withAppendedId(baseUri, cursor.getLong(0)));
                if (cursor != null) {
                    cursor.close();
                }
                return ringtoneUri;
            } else if (cursor != null) {
                cursor.close();
                return null;
            } else {
                return null;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static String getDefaultRingtoneSetting(int type) {
        switch (type) {
            case 1:
                return "ringtone_set";
            case 2:
                return "notification_sound_set";
            case 3:
            default:
                throw new IllegalArgumentException();
            case 4:
                return "alarm_alert_set";
        }
    }

    private static String getDefaultRingtoneFilename(int type) {
        switch (type) {
            case 1:
                return SystemProperties.get("ro.config.ringtone");
            case 2:
                return SystemProperties.get("ro.config.notification_sound");
            case 3:
            default:
                throw new IllegalArgumentException();
            case 4:
                return SystemProperties.get("ro.config.alarm_alert");
        }
    }

    private static String getQueryStringForType(int type) {
        switch (type) {
            case 1:
                return "is_ringtone";
            case 2:
                return "is_notification";
            case 3:
            default:
                throw new IllegalArgumentException();
            case 4:
                return "is_alarm";
        }
    }
}
