package android.mtp;

import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ApplicationMediaCapabilities;
import android.media.ExifInterface;
import android.media.MediaFormat;
import android.media.ThumbnailUtils;
import android.mtp.MtpStorageManager;
import android.net.Uri;
import android.p008os.BatteryManager;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.p008os.storage.StorageVolume;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.collect.Sets;
import dalvik.system.CloseGuard;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.IntStream;
/* loaded from: classes2.dex */
public class MtpDatabase implements AutoCloseable {
    private static final int[] AUDIO_PROPERTIES;
    private static final int[] DEVICE_PROPERTIES;
    private static final int[] FILE_PROPERTIES;
    private static final int[] IMAGE_PROPERTIES;
    private static final int MAX_THUMB_SIZE = 204800;
    private static final String NO_MEDIA = ".nomedia";
    private static final String PATH_WHERE = "_data=?";
    private static final int[] PLAYBACK_FORMATS;
    private static final String TAG = MtpDatabase.class.getSimpleName();
    private static final int[] VIDEO_PROPERTIES;
    private int mBatteryLevel;
    private BroadcastReceiver mBatteryReceiver;
    private int mBatteryScale;
    private final CloseGuard mCloseGuard;
    private final AtomicBoolean mClosed = new AtomicBoolean();
    private final Context mContext;
    private SharedPreferences mDeviceProperties;
    private int mDeviceType;
    private volatile boolean mHostIsWindows;
    private String mHostType;
    private MtpStorageManager mManager;
    private final ContentProviderClient mMediaProvider;
    private long mNativeContext;
    private final SparseArray<MtpPropertyGroup> mPropertyGroupsByFormat;
    private final SparseArray<MtpPropertyGroup> mPropertyGroupsByProperty;
    private MtpServer mServer;
    private boolean mSkipThumbForHost;
    private final HashMap<String, MtpStorage> mStorageMap;

    private final native void native_finalize();

    private final native void native_setup();

    static {
        System.loadLibrary("media_jni");
        PLAYBACK_FORMATS = new int[]{12288, 12289, 12292, 12293, 12296, 12297, 12299, MtpConstants.FORMAT_EXIF_JPEG, MtpConstants.FORMAT_TIFF_EP, MtpConstants.FORMAT_BMP, MtpConstants.FORMAT_GIF, MtpConstants.FORMAT_JFIF, MtpConstants.FORMAT_PNG, MtpConstants.FORMAT_TIFF, MtpConstants.FORMAT_WMA, MtpConstants.FORMAT_OGG, MtpConstants.FORMAT_AAC, MtpConstants.FORMAT_MP4_CONTAINER, MtpConstants.FORMAT_MP2, MtpConstants.FORMAT_3GP_CONTAINER, MtpConstants.FORMAT_ABSTRACT_AV_PLAYLIST, MtpConstants.FORMAT_WPL_PLAYLIST, MtpConstants.FORMAT_M3U_PLAYLIST, MtpConstants.FORMAT_PLS_PLAYLIST, MtpConstants.FORMAT_XML_DOCUMENT, MtpConstants.FORMAT_FLAC, MtpConstants.FORMAT_DNG, MtpConstants.FORMAT_HEIF};
        FILE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED};
        AUDIO_PROPERTIES = new int[]{MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER, MtpConstants.PROPERTY_AUDIO_WAVE_CODEC, MtpConstants.PROPERTY_BITRATE_TYPE, MtpConstants.PROPERTY_AUDIO_BITRATE, MtpConstants.PROPERTY_NUMBER_OF_CHANNELS, MtpConstants.PROPERTY_SAMPLE_RATE};
        VIDEO_PROPERTIES = new int[]{MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION};
        IMAGE_PROPERTIES = new int[]{MtpConstants.PROPERTY_DESCRIPTION};
        DEVICE_PROPERTIES = new int[]{MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER, MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME, MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE, MtpConstants.DEVICE_PROPERTY_BATTERY_LEVEL, MtpConstants.DEVICE_PROPERTY_PERCEIVED_DEVICE_TYPE, MtpConstants.DEVICE_PROPERTY_SESSION_INITIATOR_VERSION_INFO};
    }

    private int[] getSupportedObjectProperties(int format) {
        switch (format) {
            case 12296:
            case 12297:
            case MtpConstants.FORMAT_WMA /* 47361 */:
            case MtpConstants.FORMAT_OGG /* 47362 */:
            case MtpConstants.FORMAT_AAC /* 47363 */:
                return IntStream.concat(Arrays.stream(FILE_PROPERTIES), Arrays.stream(AUDIO_PROPERTIES)).toArray();
            case 12299:
            case MtpConstants.FORMAT_WMV /* 47489 */:
            case MtpConstants.FORMAT_3GP_CONTAINER /* 47492 */:
                return IntStream.concat(Arrays.stream(FILE_PROPERTIES), Arrays.stream(VIDEO_PROPERTIES)).toArray();
            case MtpConstants.FORMAT_EXIF_JPEG /* 14337 */:
            case MtpConstants.FORMAT_BMP /* 14340 */:
            case MtpConstants.FORMAT_GIF /* 14343 */:
            case MtpConstants.FORMAT_PNG /* 14347 */:
            case MtpConstants.FORMAT_DNG /* 14353 */:
            case MtpConstants.FORMAT_HEIF /* 14354 */:
                return IntStream.concat(Arrays.stream(FILE_PROPERTIES), Arrays.stream(IMAGE_PROPERTIES)).toArray();
            default:
                return FILE_PROPERTIES;
        }
    }

    public static Uri getObjectPropertiesUri(int format, String volumeName) {
        switch (format) {
            case 12296:
            case 12297:
            case MtpConstants.FORMAT_WMA /* 47361 */:
            case MtpConstants.FORMAT_OGG /* 47362 */:
            case MtpConstants.FORMAT_AAC /* 47363 */:
                return MediaStore.Audio.Media.getContentUri(volumeName);
            case 12299:
            case MtpConstants.FORMAT_WMV /* 47489 */:
            case MtpConstants.FORMAT_3GP_CONTAINER /* 47492 */:
                return MediaStore.Video.Media.getContentUri(volumeName);
            case MtpConstants.FORMAT_EXIF_JPEG /* 14337 */:
            case MtpConstants.FORMAT_BMP /* 14340 */:
            case MtpConstants.FORMAT_GIF /* 14343 */:
            case MtpConstants.FORMAT_PNG /* 14347 */:
            case MtpConstants.FORMAT_DNG /* 14353 */:
            case MtpConstants.FORMAT_HEIF /* 14354 */:
                return MediaStore.Images.Media.getContentUri(volumeName);
            default:
                return MediaStore.Files.getContentUri(volumeName);
        }
    }

    private int[] getSupportedDeviceProperties() {
        return DEVICE_PROPERTIES;
    }

    private int[] getSupportedPlaybackFormats() {
        return PLAYBACK_FORMATS;
    }

    private int[] getSupportedCaptureFormats() {
        return null;
    }

    public MtpDatabase(Context context, String[] subDirectories) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mStorageMap = new HashMap<>();
        this.mPropertyGroupsByProperty = new SparseArray<>();
        this.mPropertyGroupsByFormat = new SparseArray<>();
        this.mSkipThumbForHost = false;
        this.mHostIsWindows = false;
        this.mBatteryReceiver = new BroadcastReceiver() { // from class: android.mtp.MtpDatabase.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    MtpDatabase.this.mBatteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                    int newLevel = intent.getIntExtra("level", 0);
                    if (newLevel != MtpDatabase.this.mBatteryLevel) {
                        MtpDatabase.this.mBatteryLevel = newLevel;
                        if (MtpDatabase.this.mServer != null) {
                            MtpDatabase.this.mServer.sendDevicePropertyChanged(MtpConstants.DEVICE_PROPERTY_BATTERY_LEVEL);
                        }
                    }
                }
            }
        };
        native_setup();
        this.mContext = (Context) Objects.requireNonNull(context);
        this.mMediaProvider = context.getContentResolver().acquireContentProviderClient("media");
        this.mManager = new MtpStorageManager(new MtpStorageManager.MtpNotifier() { // from class: android.mtp.MtpDatabase.2
            @Override // android.mtp.MtpStorageManager.MtpNotifier
            public void sendObjectAdded(int id) {
                if (MtpDatabase.this.mServer != null) {
                    MtpDatabase.this.mServer.sendObjectAdded(id);
                }
            }

            @Override // android.mtp.MtpStorageManager.MtpNotifier
            public void sendObjectRemoved(int id) {
                if (MtpDatabase.this.mServer != null) {
                    MtpDatabase.this.mServer.sendObjectRemoved(id);
                }
            }

            @Override // android.mtp.MtpStorageManager.MtpNotifier
            public void sendObjectInfoChanged(int id) {
                if (MtpDatabase.this.mServer != null) {
                    MtpDatabase.this.mServer.sendObjectInfoChanged(id);
                }
            }
        }, subDirectories == null ? null : Sets.newHashSet(subDirectories));
        initDeviceProperties(context);
        this.mDeviceType = SystemProperties.getInt("sys.usb.mtp.device_type", 0);
        closeGuard.open("close");
    }

    public void setServer(MtpServer server) {
        this.mServer = server;
        try {
            this.mContext.unregisterReceiver(this.mBatteryReceiver);
        } catch (IllegalArgumentException e) {
        }
        if (server != null) {
            this.mContext.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mManager.close();
        this.mCloseGuard.close();
        if (this.mClosed.compareAndSet(false, true)) {
            ContentProviderClient contentProviderClient = this.mMediaProvider;
            if (contentProviderClient != null) {
                contentProviderClient.close();
            }
            native_finalize();
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$addStorage$0() {
        return Boolean.valueOf(this.mHostIsWindows);
    }

    public void addStorage(StorageVolume storage) {
        MtpStorage mtpStorage = this.mManager.addMtpStorage(storage, new Supplier() { // from class: android.mtp.MtpDatabase$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                Boolean lambda$addStorage$0;
                lambda$addStorage$0 = MtpDatabase.this.lambda$addStorage$0();
                return lambda$addStorage$0;
            }
        });
        this.mStorageMap.put(storage.getPath(), mtpStorage);
        MtpServer mtpServer = this.mServer;
        if (mtpServer != null) {
            mtpServer.addStorage(mtpStorage);
        }
    }

    public void removeStorage(StorageVolume storage) {
        MtpStorage mtpStorage = this.mStorageMap.get(storage.getPath());
        if (mtpStorage == null) {
            return;
        }
        MtpServer mtpServer = this.mServer;
        if (mtpServer != null) {
            mtpServer.removeStorage(mtpStorage);
        }
        this.mManager.removeMtpStorage(mtpStorage);
        this.mStorageMap.remove(storage.getPath());
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0083, code lost:
        if (r7 != null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0086, code lost:
        r19.deleteDatabase("device-properties");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void initDeviceProperties(Context context) {
        this.mDeviceProperties = context.getSharedPreferences("device-properties", 0);
        File databaseFile = context.getDatabasePath("device-properties");
        if (databaseFile.exists()) {
            SQLiteDatabase db = null;
            Cursor c = null;
            try {
                try {
                    db = context.openOrCreateDatabase("device-properties", 0, null);
                    if (db != null && (c = db.query("properties", new String[]{"_id", "code", "value"}, null, null, null, null, null)) != null) {
                        SharedPreferences.Editor e = this.mDeviceProperties.edit();
                        while (c.moveToNext()) {
                            String name = c.getString(1);
                            String value = c.getString(2);
                            e.putString(name, value);
                        }
                        e.commit();
                    }
                } catch (Exception e2) {
                    Log.m109e(TAG, "failed to migrate device properties", e2);
                    if (c != null) {
                        c.close();
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
        this.mHostType = "";
        this.mSkipThumbForHost = false;
        this.mHostIsWindows = false;
    }

    public int beginSendObject(String path, int format, int parent, int storageId) {
        MtpStorageManager mtpStorageManager = this.mManager;
        MtpStorageManager.MtpObject parentObj = parent == 0 ? mtpStorageManager.getStorageRoot(storageId) : mtpStorageManager.getObject(parent);
        if (parentObj == null) {
            return -1;
        }
        Path objPath = Paths.get(path, new String[0]);
        return this.mManager.beginSendObject(parentObj, objPath.getFileName().toString(), format);
    }

    private void endSendObject(int handle, boolean succeeded) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null || !this.mManager.endSendObject(obj, succeeded)) {
            Log.m110e(TAG, "Failed to successfully end send object");
        } else if (succeeded) {
            updateMediaStore(this.mContext, obj.getPath().toFile());
        }
    }

    private void rescanFile(String path, int handle, int format) {
        MediaStore.scanFile(this.mContext.getContentResolver(), new File(path));
    }

    private int[] getObjectList(int storageID, int format, int parent) {
        List<MtpStorageManager.MtpObject> objs = this.mManager.getObjects(parent, format, storageID);
        if (objs == null) {
            return null;
        }
        int[] ret = new int[objs.size()];
        for (int i = 0; i < objs.size(); i++) {
            ret[i] = objs.get(i).getId();
        }
        return ret;
    }

    public int getNumObjects(int storageID, int format, int parent) {
        List<MtpStorageManager.MtpObject> objs = this.mManager.getObjects(parent, format, storageID);
        if (objs == null) {
            return -1;
        }
        return objs.size();
    }

    private MtpPropertyList getObjectPropertyList(int handle, int format, int property, int groupCode, int depth) {
        MtpPropertyGroup propertyGroup;
        if (property == 0) {
            if (groupCode == 0) {
                return new MtpPropertyList(8198);
            }
            return new MtpPropertyList(MtpConstants.RESPONSE_SPECIFICATION_BY_GROUP_UNSUPPORTED);
        }
        if (depth == -1 && (handle == 0 || handle == -1)) {
            handle = -1;
            depth = 0;
        }
        if (depth != 0 && depth != 1) {
            return new MtpPropertyList(MtpConstants.RESPONSE_SPECIFICATION_BY_DEPTH_UNSUPPORTED);
        }
        List<MtpStorageManager.MtpObject> objs = null;
        MtpStorageManager.MtpObject thisObj = null;
        if (handle == -1) {
            objs = this.mManager.getObjects(0, format, -1);
            if (objs == null) {
                return new MtpPropertyList(8201);
            }
        } else if (handle != 0) {
            MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
            if (obj == null) {
                return new MtpPropertyList(8201);
            }
            if (obj.getFormat() == format || format == 0) {
                thisObj = obj;
            }
        }
        if (handle == 0 || depth == 1) {
            if (handle == 0) {
                handle = -1;
            }
            objs = this.mManager.getObjects(handle, format, -1);
            if (objs == null) {
                return new MtpPropertyList(8201);
            }
        }
        if (objs == null) {
            objs = new ArrayList<>();
        }
        if (thisObj != null) {
            objs.add(thisObj);
        }
        MtpPropertyList ret = new MtpPropertyList(8193);
        for (MtpStorageManager.MtpObject obj2 : objs) {
            if (property == -1) {
                if (format == 0 && handle != 0 && handle != -1) {
                    format = obj2.getFormat();
                }
                propertyGroup = this.mPropertyGroupsByFormat.get(format);
                if (propertyGroup == null) {
                    int[] propertyList = getSupportedObjectProperties(format);
                    propertyGroup = new MtpPropertyGroup(propertyList);
                    this.mPropertyGroupsByFormat.put(format, propertyGroup);
                }
            } else {
                propertyGroup = this.mPropertyGroupsByProperty.get(property);
                if (propertyGroup == null) {
                    int[] propertyList2 = {property};
                    propertyGroup = new MtpPropertyGroup(propertyList2);
                    this.mPropertyGroupsByProperty.put(property, propertyGroup);
                }
            }
            int err = propertyGroup.getPropertyList(this.mMediaProvider, obj2.getVolumeName(), obj2, ret);
            if (err != 8193) {
                return new MtpPropertyList(err);
            }
        }
        return ret;
    }

    private int renameFile(int handle, String newName) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return 8201;
        }
        Path oldPath = obj.getPath();
        if (this.mManager.beginRenameObject(obj, newName)) {
            Path newPath = obj.getPath();
            boolean success = oldPath.toFile().renameTo(newPath.toFile());
            try {
                Os.access(oldPath.toString(), OsConstants.F_OK);
                Os.access(newPath.toString(), OsConstants.F_OK);
            } catch (ErrnoException e) {
            }
            if (!this.mManager.endRenameObject(obj, oldPath.getFileName().toString(), success)) {
                Log.m110e(TAG, "Failed to end rename object");
            }
            if (success) {
                updateMediaStore(this.mContext, oldPath.toFile());
                updateMediaStore(this.mContext, newPath.toFile());
                return 8193;
            }
            return 8194;
        }
        return 8194;
    }

    private int beginMoveObject(int handle, int newParent, int newStorage) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        MtpStorageManager.MtpObject parent = newParent == 0 ? this.mManager.getStorageRoot(newStorage) : this.mManager.getObject(newParent);
        if (obj == null || parent == null) {
            return 8201;
        }
        boolean allowed = this.mManager.beginMoveObject(obj, parent);
        return allowed ? 8193 : 8194;
    }

    private void endMoveObject(int oldParent, int newParent, int oldStorage, int newStorage, int objId, boolean success) {
        MtpStorageManager.MtpObject oldParentObj = oldParent == 0 ? this.mManager.getStorageRoot(oldStorage) : this.mManager.getObject(oldParent);
        MtpStorageManager.MtpObject newParentObj = newParent == 0 ? this.mManager.getStorageRoot(newStorage) : this.mManager.getObject(newParent);
        MtpStorageManager.MtpObject obj = this.mManager.getObject(objId);
        String name = obj.getName();
        if (newParentObj == null || oldParentObj == null || !this.mManager.endMoveObject(oldParentObj, newParentObj, name, success)) {
            Log.m110e(TAG, "Failed to end move object");
            return;
        }
        MtpStorageManager.MtpObject obj2 = this.mManager.getObject(objId);
        if (!success || obj2 == null) {
            return;
        }
        Path path = newParentObj.getPath().resolve(name);
        Path oldPath = oldParentObj.getPath().resolve(name);
        updateMediaStore(this.mContext, oldPath.toFile());
        updateMediaStore(this.mContext, path.toFile());
    }

    private int beginCopyObject(int handle, int newParent, int newStorage) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        MtpStorageManager.MtpObject parent = newParent == 0 ? this.mManager.getStorageRoot(newStorage) : this.mManager.getObject(newParent);
        if (obj == null || parent == null) {
            return 8201;
        }
        return this.mManager.beginCopyObject(obj, parent);
    }

    private void endCopyObject(int handle, boolean success) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null || !this.mManager.endCopyObject(obj, success)) {
            Log.m110e(TAG, "Failed to end copy object");
        } else if (!success) {
        } else {
            updateMediaStore(this.mContext, obj.getPath().toFile());
        }
    }

    private static void updateMediaStore(Context context, File file) {
        ContentResolver resolver = context.getContentResolver();
        if (!file.isDirectory() && file.getName().toLowerCase(Locale.ROOT).endsWith(NO_MEDIA)) {
            MediaStore.scanFile(resolver, file.getParentFile());
        } else {
            MediaStore.scanFile(resolver, file);
        }
    }

    private int setObjectProperty(int handle, int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
                return renameFile(handle, stringValue);
            default:
                return MtpConstants.RESPONSE_OBJECT_PROP_NOT_SUPPORTED;
        }
    }

    private int getDeviceProperty(int property, long[] outIntValue, char[] outStringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_BATTERY_LEVEL /* 20481 */:
                outIntValue[0] = this.mBatteryLevel;
                outIntValue[1] = this.mBatteryScale;
                return 8193;
            case MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE /* 20483 */:
                Display display = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getMaximumSizeDimension();
                int height = display.getMaximumSizeDimension();
                String imageSize = Integer.toString(width) + "x" + Integer.toString(height);
                imageSize.getChars(0, imageSize.length(), outStringValue, 0);
                outStringValue[imageSize.length()] = 0;
                return 8193;
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                String value = this.mDeviceProperties.getString(Integer.toString(property), "");
                int length = value.length();
                if (length > 255) {
                    length = 255;
                }
                value.getChars(0, length, outStringValue, 0);
                outStringValue[length] = 0;
                return 8193;
            case MtpConstants.DEVICE_PROPERTY_SESSION_INITIATOR_VERSION_INFO /* 54278 */:
                String value2 = this.mHostType;
                int length2 = value2.length();
                if (length2 > 255) {
                    length2 = 255;
                }
                value2.getChars(0, length2, outStringValue, 0);
                outStringValue[length2] = 0;
                return 8193;
            case MtpConstants.DEVICE_PROPERTY_PERCEIVED_DEVICE_TYPE /* 54279 */:
                outIntValue[0] = this.mDeviceType;
                return 8193;
            default:
                return 8202;
        }
    }

    private int setDeviceProperty(int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                SharedPreferences.Editor e = this.mDeviceProperties.edit();
                e.putString(Integer.toString(property), stringValue);
                if (e.commit()) {
                    return 8193;
                }
                return 8194;
            case MtpConstants.DEVICE_PROPERTY_SESSION_INITIATOR_VERSION_INFO /* 54278 */:
                this.mHostType = stringValue;
                Log.m112d(TAG, "setDeviceProperty." + Integer.toHexString(property) + "=" + stringValue);
                if (stringValue.startsWith("Android/")) {
                    this.mSkipThumbForHost = true;
                } else if (stringValue.startsWith("Windows/")) {
                    this.mHostIsWindows = true;
                }
                return 8193;
            default:
                return 8202;
        }
    }

    private boolean getObjectInfo(int handle, int[] outStorageFormatParent, char[] outName, long[] outCreatedModified) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return false;
        }
        outStorageFormatParent[0] = obj.getStorageId();
        outStorageFormatParent[1] = obj.getFormat();
        outStorageFormatParent[2] = obj.getParent().isRoot() ? 0 : obj.getParent().getId();
        int nameLen = Integer.min(obj.getName().length(), 255);
        obj.getName().getChars(0, nameLen, outName, 0);
        outName[nameLen] = 0;
        outCreatedModified[0] = obj.getModifiedTime();
        outCreatedModified[1] = obj.getModifiedTime();
        return true;
    }

    private int getObjectFilePath(int handle, char[] outFilePath, long[] outFileLengthFormat) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return 8201;
        }
        String path = obj.getPath().toString();
        int pathLen = Integer.min(path.length(), 4096);
        path.getChars(0, pathLen, outFilePath, 0);
        outFilePath[pathLen] = 0;
        outFileLengthFormat[0] = obj.getSize();
        outFileLengthFormat[1] = obj.getFormat();
        return 8193;
    }

    private int openFilePath(String path, boolean transcode) {
        Uri uri = MediaStore.scanFile(this.mContext.getContentResolver(), new File(path));
        if (uri == null) {
            Log.m108i(TAG, "Failed to obtain URI for openFile with transcode support: " + path);
            return -1;
        }
        try {
            Log.m108i(TAG, "openFile with transcode support: " + path);
            Bundle bundle = new Bundle();
            if (transcode) {
                bundle.putParcelable("android.provider.extra.MEDIA_CAPABILITIES", new ApplicationMediaCapabilities.Builder().addUnsupportedVideoMimeType(MediaFormat.MIMETYPE_VIDEO_HEVC).build());
            } else {
                bundle.putParcelable("android.provider.extra.MEDIA_CAPABILITIES", new ApplicationMediaCapabilities.Builder().addSupportedVideoMimeType(MediaFormat.MIMETYPE_VIDEO_HEVC).build());
            }
            return this.mMediaProvider.openTypedAssetFileDescriptor(uri, "*/*", bundle).getParcelFileDescriptor().detachFd();
        } catch (RemoteException | FileNotFoundException e) {
            Log.m103w(TAG, "Failed to openFile with transcode support: " + path, e);
            return -1;
        }
    }

    private int getObjectFormat(int handle) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return -1;
        }
        return obj.getFormat();
    }

    private byte[] getThumbnailProcess(String path, Bitmap bitmap) {
        try {
            if (bitmap == null) {
                Log.m112d(TAG, "getThumbnailProcess: Fail to generate thumbnail. Probably unsupported or corrupted image");
                return null;
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            if (byteStream.size() > MAX_THUMB_SIZE) {
                Log.m104w(TAG, "getThumbnailProcess: size=" + byteStream.size());
                return null;
            }
            byte[] byteArray = byteStream.toByteArray();
            return byteArray;
        } catch (OutOfMemoryError oomEx) {
            Log.m104w(TAG, "OutOfMemoryError:" + oomEx);
            return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public boolean getThumbnailInfo(int handle, long[] outLongs) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return false;
        }
        String path = obj.getPath().toString();
        switch (obj.getFormat()) {
            case MtpConstants.FORMAT_EXIF_JPEG /* 14337 */:
            case MtpConstants.FORMAT_JFIF /* 14344 */:
            case MtpConstants.FORMAT_HEIF /* 14354 */:
                try {
                    ExifInterface exif = new ExifInterface(path);
                    long[] thumbOffsetAndSize = exif.getThumbnailRange();
                    outLongs[0] = thumbOffsetAndSize != null ? thumbOffsetAndSize[1] : 0L;
                    outLongs[1] = exif.getAttributeInt(ExifInterface.TAG_PIXEL_X_DIMENSION, 0);
                    outLongs[2] = exif.getAttributeInt(ExifInterface.TAG_PIXEL_Y_DIMENSION, 0);
                    if (this.mSkipThumbForHost) {
                        Log.m112d(TAG, "getThumbnailInfo: Skip runtime thumbnail.");
                        return true;
                    } else if (exif.getThumbnailRange() != null) {
                        if (outLongs[0] == 0 || outLongs[1] == 0 || outLongs[2] == 0) {
                            Log.m112d(TAG, "getThumbnailInfo: check thumb info:" + thumbOffsetAndSize[0] + "," + thumbOffsetAndSize[1] + "," + outLongs[1] + "," + outLongs[2]);
                        }
                        return true;
                    }
                } catch (IOException e) {
                    break;
                }
                break;
            case MtpConstants.FORMAT_BMP /* 14340 */:
            case MtpConstants.FORMAT_GIF /* 14343 */:
            case MtpConstants.FORMAT_PNG /* 14347 */:
                break;
            default:
                return false;
        }
        outLongs[0] = 204800;
        outLongs[1] = 320;
        outLongs[2] = 240;
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public byte[] getThumbnailData(int handle) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return null;
        }
        String path = obj.getPath().toString();
        switch (obj.getFormat()) {
            case MtpConstants.FORMAT_EXIF_JPEG /* 14337 */:
            case MtpConstants.FORMAT_JFIF /* 14344 */:
            case MtpConstants.FORMAT_HEIF /* 14354 */:
                try {
                    ExifInterface exif = new ExifInterface(path);
                    if (this.mSkipThumbForHost) {
                        Log.m112d(TAG, "getThumbnailData: Skip runtime thumbnail.");
                        return exif.getThumbnail();
                    } else if (exif.getThumbnailRange() != null) {
                        return exif.getThumbnail();
                    }
                } catch (IOException e) {
                    break;
                }
                break;
            case MtpConstants.FORMAT_BMP /* 14340 */:
            case MtpConstants.FORMAT_GIF /* 14343 */:
            case MtpConstants.FORMAT_PNG /* 14347 */:
                break;
            default:
                return null;
        }
        Bitmap bitmap = ThumbnailUtils.createImageThumbnail(path, 1);
        byte[] byteArray = getThumbnailProcess(path, bitmap);
        return byteArray;
    }

    private int beginDeleteObject(int handle) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return 8201;
        }
        if (!this.mManager.beginRemoveObject(obj)) {
            return 8194;
        }
        return 8193;
    }

    private void endDeleteObject(int handle, boolean success) {
        MtpStorageManager.MtpObject obj = this.mManager.getObject(handle);
        if (obj == null) {
            return;
        }
        if (!this.mManager.endRemoveObject(obj, success)) {
            Log.m110e(TAG, "Failed to end remove object");
        }
        if (success) {
            deleteFromMedia(obj, obj.getPath(), obj.isDir());
        }
    }

    private void deleteFromMedia(MtpStorageManager.MtpObject obj, Path path, boolean isDir) {
        Uri objectsUri = MediaStore.Files.getContentUri(obj.getVolumeName());
        if (isDir) {
            try {
                this.mMediaProvider.delete(objectsUri, "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[]{path + "/%", Integer.toString(path.toString().length() + 1), path.toString() + "/"});
            } catch (Exception e) {
                Log.m112d(TAG, "Failed to delete " + path + " from MediaProvider");
                return;
            }
        }
        String[] whereArgs = {path.toString()};
        if (this.mMediaProvider.delete(objectsUri, PATH_WHERE, whereArgs) == 0) {
            Log.m108i(TAG, "MediaProvider didn't delete " + path);
        }
        updateMediaStore(this.mContext, path.toFile());
    }

    private int[] getObjectReferences(int handle) {
        return null;
    }

    private int setObjectReferences(int handle, int[] references) {
        return 8197;
    }
}
