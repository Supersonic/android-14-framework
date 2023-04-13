package android.drm;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.drm.DrmStore;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Looper;
import android.p008os.Message;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
@Deprecated
/* loaded from: classes.dex */
public class DrmManagerClient implements AutoCloseable {
    private static final int ACTION_PROCESS_DRM_INFO = 1002;
    private static final int ACTION_REMOVE_ALL_RIGHTS = 1001;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNKNOWN = -2000;
    public static final int INVALID_SESSION = -1;
    private static final String TAG = "DrmManagerClient";
    private final CloseGuard mCloseGuard;
    private final AtomicBoolean mClosed = new AtomicBoolean();
    private Context mContext;
    private EventHandler mEventHandler;
    HandlerThread mEventThread;
    private InfoHandler mInfoHandler;
    HandlerThread mInfoThread;
    private long mNativeContext;
    private OnErrorListener mOnErrorListener;
    private OnEventListener mOnEventListener;
    private OnInfoListener mOnInfoListener;
    private int mUniqueId;

    /* loaded from: classes.dex */
    public interface OnErrorListener {
        void onError(DrmManagerClient drmManagerClient, DrmErrorEvent drmErrorEvent);
    }

    /* loaded from: classes.dex */
    public interface OnEventListener {
        void onEvent(DrmManagerClient drmManagerClient, DrmEvent drmEvent);
    }

    /* loaded from: classes.dex */
    public interface OnInfoListener {
        void onInfo(DrmManagerClient drmManagerClient, DrmInfoEvent drmInfoEvent);
    }

    private native DrmInfo _acquireDrmInfo(int i, DrmInfoRequest drmInfoRequest);

    private native boolean _canHandle(int i, String str, String str2);

    private native int _checkRightsStatus(int i, String str, int i2);

    private native DrmConvertedStatus _closeConvertSession(int i, int i2);

    private native DrmConvertedStatus _convertData(int i, int i2, byte[] bArr);

    private native DrmSupportInfo[] _getAllSupportInfo(int i);

    private native ContentValues _getConstraints(int i, String str, int i2);

    private native int _getDrmObjectType(int i, String str, String str2);

    private native ContentValues _getMetadata(int i, String str);

    private native String _getOriginalMimeType(int i, String str, FileDescriptor fileDescriptor);

    private native int _initialize();

    private native void _installDrmEngine(int i, String str);

    private native int _openConvertSession(int i, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public native DrmInfoStatus _processDrmInfo(int i, DrmInfo drmInfo);

    private native void _release(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int _removeAllRights(int i);

    private native int _removeRights(int i, String str);

    private native int _saveRights(int i, DrmRights drmRights, String str, String str2);

    private native void _setListeners(int i, Object obj);

    static {
        System.loadLibrary("drmframework_jni");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            DrmEvent event = null;
            DrmErrorEvent error = null;
            HashMap<String, Object> attributes = new HashMap<>();
            switch (msg.what) {
                case 1001:
                    DrmManagerClient drmManagerClient = DrmManagerClient.this;
                    if (drmManagerClient._removeAllRights(drmManagerClient.mUniqueId) == 0) {
                        event = new DrmEvent(DrmManagerClient.this.mUniqueId, 1001, null);
                        break;
                    } else {
                        error = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, 2007, null);
                        break;
                    }
                case 1002:
                    DrmInfo drmInfo = (DrmInfo) msg.obj;
                    DrmManagerClient drmManagerClient2 = DrmManagerClient.this;
                    DrmInfoStatus status = drmManagerClient2._processDrmInfo(drmManagerClient2.mUniqueId, drmInfo);
                    attributes.put(DrmEvent.DRM_INFO_STATUS_OBJECT, status);
                    attributes.put(DrmEvent.DRM_INFO_OBJECT, drmInfo);
                    if (status != null && 1 == status.statusCode) {
                        event = new DrmEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getEventType(status.infoType), null, attributes);
                        break;
                    } else {
                        int infoType = status != null ? status.infoType : drmInfo.getInfoType();
                        error = new DrmErrorEvent(DrmManagerClient.this.mUniqueId, DrmManagerClient.this.getErrorType(infoType), null, attributes);
                        break;
                    }
                    break;
                default:
                    Log.m110e(DrmManagerClient.TAG, "Unknown message type " + msg.what);
                    return;
            }
            if (DrmManagerClient.this.mOnEventListener != null && event != null) {
                DrmManagerClient.this.mOnEventListener.onEvent(DrmManagerClient.this, event);
            }
            if (DrmManagerClient.this.mOnErrorListener != null && error != null) {
                DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, error);
            }
        }
    }

    public static void notify(Object thisReference, int uniqueId, int infoType, String message) {
        InfoHandler infoHandler;
        DrmManagerClient instance = (DrmManagerClient) ((WeakReference) thisReference).get();
        if (instance != null && (infoHandler = instance.mInfoHandler) != null) {
            Message m = infoHandler.obtainMessage(1, uniqueId, infoType, message);
            instance.mInfoHandler.sendMessage(m);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class InfoHandler extends Handler {
        public static final int INFO_EVENT_TYPE = 1;

        public InfoHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            DrmInfoEvent info = null;
            DrmErrorEvent error = null;
            switch (msg.what) {
                case 1:
                    int uniqueId = msg.arg1;
                    int infoType = msg.arg2;
                    String message = msg.obj.toString();
                    switch (infoType) {
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            info = new DrmInfoEvent(uniqueId, infoType, message);
                            break;
                        case 2:
                            try {
                                DrmUtils.removeFile(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            info = new DrmInfoEvent(uniqueId, infoType, message);
                            break;
                        default:
                            error = new DrmErrorEvent(uniqueId, infoType, message);
                            break;
                    }
                    if (DrmManagerClient.this.mOnInfoListener != null && info != null) {
                        DrmManagerClient.this.mOnInfoListener.onInfo(DrmManagerClient.this, info);
                    }
                    if (DrmManagerClient.this.mOnErrorListener != null && error != null) {
                        DrmManagerClient.this.mOnErrorListener.onError(DrmManagerClient.this, error);
                        return;
                    }
                    return;
                default:
                    Log.m110e(DrmManagerClient.TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    public DrmManagerClient(Context context) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mContext = context;
        createEventThreads();
        this.mUniqueId = _initialize();
        closeGuard.open("release");
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

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mCloseGuard.close();
        if (this.mClosed.compareAndSet(false, true)) {
            if (this.mEventHandler != null) {
                this.mEventThread.quit();
                this.mEventThread = null;
            }
            if (this.mInfoHandler != null) {
                this.mInfoThread.quit();
                this.mInfoThread = null;
            }
            this.mEventHandler = null;
            this.mInfoHandler = null;
            this.mOnEventListener = null;
            this.mOnInfoListener = null;
            this.mOnErrorListener = null;
            _release(this.mUniqueId);
        }
    }

    @Deprecated
    public void release() {
        close();
    }

    public synchronized void setOnInfoListener(OnInfoListener infoListener) {
        this.mOnInfoListener = infoListener;
        if (infoListener != null) {
            createListeners();
        }
    }

    public synchronized void setOnEventListener(OnEventListener eventListener) {
        this.mOnEventListener = eventListener;
        if (eventListener != null) {
            createListeners();
        }
    }

    public synchronized void setOnErrorListener(OnErrorListener errorListener) {
        this.mOnErrorListener = errorListener;
        if (errorListener != null) {
            createListeners();
        }
    }

    public String[] getAvailableDrmEngines() {
        DrmSupportInfo[] supportInfos = _getAllSupportInfo(this.mUniqueId);
        ArrayList<String> descriptions = new ArrayList<>();
        for (DrmSupportInfo drmSupportInfo : supportInfos) {
            descriptions.add(drmSupportInfo.getDescriprition());
        }
        int i = descriptions.size();
        String[] drmEngines = new String[i];
        return (String[]) descriptions.toArray(drmEngines);
    }

    public Collection<DrmSupportInfo> getAvailableDrmSupportInfo() {
        return Arrays.asList(_getAllSupportInfo(this.mUniqueId));
    }

    public ContentValues getConstraints(String path, int action) {
        if (path == null || path.equals("") || !DrmStore.Action.isValid(action)) {
            throw new IllegalArgumentException("Given usage or path is invalid/null");
        }
        return _getConstraints(this.mUniqueId, path, action);
    }

    public ContentValues getMetadata(String path) {
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("Given path is invalid/null");
        }
        return _getMetadata(this.mUniqueId, path);
    }

    public ContentValues getConstraints(Uri uri, int action) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getConstraints(convertUriToPath(uri), action);
    }

    public ContentValues getMetadata(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Uri should be non null");
        }
        return getMetadata(convertUriToPath(uri));
    }

    public int saveRights(DrmRights drmRights, String rightsPath, String contentPath) throws IOException {
        if (drmRights == null || !drmRights.isValid()) {
            throw new IllegalArgumentException("Given drmRights or contentPath is not valid");
        }
        if (rightsPath != null && !rightsPath.equals("")) {
            DrmUtils.writeToFile(rightsPath, drmRights.getData());
        }
        return _saveRights(this.mUniqueId, drmRights, rightsPath, contentPath);
    }

    public void installDrmEngine(String engineFilePath) {
        if (engineFilePath == null || engineFilePath.equals("")) {
            throw new IllegalArgumentException("Given engineFilePath: " + engineFilePath + "is not valid");
        }
        _installDrmEngine(this.mUniqueId, engineFilePath);
    }

    public boolean canHandle(String path, String mimeType) {
        if ((path == null || path.equals("")) && (mimeType == null || mimeType.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _canHandle(this.mUniqueId, path, mimeType);
    }

    public boolean canHandle(Uri uri, String mimeType) {
        if ((uri == null || Uri.EMPTY == uri) && (mimeType == null || mimeType.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        return canHandle(convertUriToPath(uri), mimeType);
    }

    public int processDrmInfo(DrmInfo drmInfo) {
        if (drmInfo == null || !drmInfo.isValid()) {
            throw new IllegalArgumentException("Given drmInfo is invalid/null");
        }
        EventHandler eventHandler = this.mEventHandler;
        if (eventHandler == null) {
            return ERROR_UNKNOWN;
        }
        Message msg = eventHandler.obtainMessage(1002, drmInfo);
        int result = this.mEventHandler.sendMessage(msg) ? 0 : -2000;
        return result;
    }

    public DrmInfo acquireDrmInfo(DrmInfoRequest drmInfoRequest) {
        if (drmInfoRequest == null || !drmInfoRequest.isValid()) {
            throw new IllegalArgumentException("Given drmInfoRequest is invalid/null");
        }
        return _acquireDrmInfo(this.mUniqueId, drmInfoRequest);
    }

    public int acquireRights(DrmInfoRequest drmInfoRequest) {
        DrmInfo drmInfo = acquireDrmInfo(drmInfoRequest);
        if (drmInfo == null) {
            return ERROR_UNKNOWN;
        }
        return processDrmInfo(drmInfo);
    }

    public int getDrmObjectType(String path, String mimeType) {
        if ((path == null || path.equals("")) && (mimeType == null || mimeType.equals(""))) {
            throw new IllegalArgumentException("Path or the mimetype should be non null");
        }
        return _getDrmObjectType(this.mUniqueId, path, mimeType);
    }

    public int getDrmObjectType(Uri uri, String mimeType) {
        if ((uri == null || Uri.EMPTY == uri) && (mimeType == null || mimeType.equals(""))) {
            throw new IllegalArgumentException("Uri or the mimetype should be non null");
        }
        String path = "";
        try {
            path = convertUriToPath(uri);
        } catch (Exception e) {
            Log.m104w(TAG, "Given Uri could not be found in media store");
        }
        return getDrmObjectType(path, mimeType);
    }

    public String getOriginalMimeType(String path) {
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        String mime = null;
        FileInputStream is = null;
        FileDescriptor fd = null;
        try {
            try {
                File file = new File(path);
                if (file.exists()) {
                    is = new FileInputStream(file);
                    fd = is.getFD();
                }
                mime = _getOriginalMimeType(this.mUniqueId, path, fd);
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                if (is != null) {
                    is.close();
                }
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
        }
        return mime;
    }

    public String getOriginalMimeType(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return getOriginalMimeType(convertUriToPath(uri));
    }

    public int checkRightsStatus(String path) {
        return checkRightsStatus(path, 0);
    }

    public int checkRightsStatus(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri));
    }

    public int checkRightsStatus(String path, int action) {
        if (path == null || path.equals("") || !DrmStore.Action.isValid(action)) {
            throw new IllegalArgumentException("Given path or action is not valid");
        }
        return _checkRightsStatus(this.mUniqueId, path, action);
    }

    public int checkRightsStatus(Uri uri, int action) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return checkRightsStatus(convertUriToPath(uri), action);
    }

    public int removeRights(String path) {
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("Given path should be non null");
        }
        return _removeRights(this.mUniqueId, path);
    }

    public int removeRights(Uri uri) {
        if (uri == null || Uri.EMPTY == uri) {
            throw new IllegalArgumentException("Given uri is not valid");
        }
        return removeRights(convertUriToPath(uri));
    }

    public int removeAllRights() {
        EventHandler eventHandler = this.mEventHandler;
        if (eventHandler == null) {
            return ERROR_UNKNOWN;
        }
        Message msg = eventHandler.obtainMessage(1001);
        int result = this.mEventHandler.sendMessage(msg) ? 0 : -2000;
        return result;
    }

    public int openConvertSession(String mimeType) {
        if (mimeType == null || mimeType.equals("")) {
            throw new IllegalArgumentException("Path or the mimeType should be non null");
        }
        return _openConvertSession(this.mUniqueId, mimeType);
    }

    public DrmConvertedStatus convertData(int convertId, byte[] inputData) {
        if (inputData == null || inputData.length <= 0) {
            throw new IllegalArgumentException("Given inputData should be non null");
        }
        return _convertData(this.mUniqueId, convertId, inputData);
    }

    public DrmConvertedStatus closeConvertSession(int convertId) {
        return _closeConvertSession(this.mUniqueId, convertId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getEventType(int infoType) {
        switch (infoType) {
            case 1:
            case 2:
            case 3:
                return 1002;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getErrorType(int infoType) {
        switch (infoType) {
            case 1:
            case 2:
            case 3:
                return 2006;
            default:
                return -1;
        }
    }

    private String convertUriToPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("") || scheme.equals("file")) {
            String path = uri.getPath();
            return path;
        } else if (scheme.equals(IntentFilter.SCHEME_HTTP) || scheme.equals(IntentFilter.SCHEME_HTTPS)) {
            String path2 = uri.toString();
            return path2;
        } else if (scheme.equals("content")) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                try {
                    cursor = this.mContext.getContentResolver().query(uri, projection, null, null, null);
                    if (cursor == null || cursor.getCount() == 0 || !cursor.moveToFirst()) {
                        throw new IllegalArgumentException("Given Uri could not be found in media store");
                    }
                    int pathIndex = cursor.getColumnIndexOrThrow("_data");
                    String path3 = cursor.getString(pathIndex);
                } catch (SQLiteException e) {
                    throw new IllegalArgumentException("Given Uri is not formatted in a way so that it can be found in media store.");
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            throw new IllegalArgumentException("Given Uri scheme is not supported");
        }
    }

    private void createEventThreads() {
        if (this.mEventHandler == null && this.mInfoHandler == null) {
            HandlerThread handlerThread = new HandlerThread("DrmManagerClient.InfoHandler");
            this.mInfoThread = handlerThread;
            handlerThread.start();
            this.mInfoHandler = new InfoHandler(this.mInfoThread.getLooper());
            HandlerThread handlerThread2 = new HandlerThread("DrmManagerClient.EventHandler");
            this.mEventThread = handlerThread2;
            handlerThread2.start();
            this.mEventHandler = new EventHandler(this.mEventThread.getLooper());
        }
    }

    private void createListeners() {
        _setListeners(this.mUniqueId, new WeakReference(this));
    }
}
