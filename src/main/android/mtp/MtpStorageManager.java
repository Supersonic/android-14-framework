package android.mtp;

import android.media.MediaFile;
import android.p008os.FileObserver;
import android.p008os.SystemProperties;
import android.p008os.storage.StorageVolume;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class MtpStorageManager {
    private static final int IN_IGNORED = 32768;
    private static final int IN_ISDIR = 1073741824;
    private static final int IN_ONLYDIR = 16777216;
    private static final int IN_Q_OVERFLOW = 16384;
    private static final String TAG = MtpStorageManager.class.getSimpleName();
    public static boolean sDebug = false;
    private MtpNotifier mMtpNotifier;
    private Set<String> mSubdirectories;
    private HashMap<Integer, MtpObject> mObjects = new HashMap<>();
    private HashMap<Integer, MtpObject> mRoots = new HashMap<>();
    private int mNextObjectId = 1;
    private int mNextStorageId = 1;
    private volatile boolean mCheckConsistency = false;
    private Thread mConsistencyThread = new Thread(new Runnable() { // from class: android.mtp.MtpStorageManager$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            MtpStorageManager.this.lambda$new$0();
        }
    });

    /* loaded from: classes2.dex */
    public static abstract class MtpNotifier {
        public abstract void sendObjectAdded(int i);

        public abstract void sendObjectInfoChanged(int i);

        public abstract void sendObjectRemoved(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum MtpObjectState {
        NORMAL,
        FROZEN,
        FROZEN_ADDED,
        FROZEN_REMOVED,
        FROZEN_ONESHOT_ADD,
        FROZEN_ONESHOT_DEL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum MtpOperation {
        NONE,
        ADD,
        RENAME,
        COPY,
        DELETE
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MtpObjectObserver extends FileObserver {
        MtpObject mObject;

        MtpObjectObserver(MtpObject object) {
            super(object.getPath().toString(), 16778184);
            this.mObject = object;
        }

        /* JADX WARN: Code restructure failed: missing block: B:34:0x00d6, code lost:
            android.util.Log.m104w(android.mtp.MtpStorageManager.TAG, "Object was null in event " + r7);
         */
        /* JADX WARN: Code restructure failed: missing block: B:36:0x00f1, code lost:
            return;
         */
        @Override // android.p008os.FileObserver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onEvent(int event, String path) {
            synchronized (MtpStorageManager.this) {
                if ((event & 16384) != 0) {
                    Log.m110e(MtpStorageManager.TAG, "Received Inotify overflow event!");
                }
                MtpObject obj = this.mObject.getChild(path);
                if ((event & 128) == 0 && (event & 256) == 0) {
                    if ((event & 64) == 0 && (event & 512) == 0) {
                        if ((32768 & event) != 0) {
                            if (MtpStorageManager.sDebug) {
                                Log.m108i(MtpStorageManager.TAG, "inotify for " + this.mObject.getPath() + " deleted");
                            }
                            if (this.mObject.mObserver != null) {
                                this.mObject.mObserver.stopWatching();
                            }
                            this.mObject.mObserver = null;
                        } else if ((event & 8) != 0) {
                            if (MtpStorageManager.sDebug) {
                                Log.m108i(MtpStorageManager.TAG, "inotify for " + this.mObject.getPath() + " CLOSE_WRITE: " + path);
                            }
                            MtpStorageManager.this.handleChangedObject(this.mObject, path);
                        } else {
                            Log.m104w(MtpStorageManager.TAG, "Got unrecognized event " + path + " " + event);
                        }
                    }
                    if (MtpStorageManager.sDebug) {
                        Log.m108i(MtpStorageManager.TAG, "Got inotify removed event for " + path + " " + event);
                    }
                    MtpStorageManager.this.handleRemovedObject(obj);
                }
                if (MtpStorageManager.sDebug) {
                    Log.m108i(MtpStorageManager.TAG, "Got inotify added event for " + path + " " + event);
                }
                MtpStorageManager.this.handleAddedObject(this.mObject, path, (1073741824 & event) != 0);
            }
        }

        @Override // android.p008os.FileObserver
        public void finalize() {
        }
    }

    /* loaded from: classes2.dex */
    public static class MtpObject {
        private HashMap<String, MtpObject> mChildren;
        private int mId;
        private boolean mIsDir;
        private String mName;
        private MtpObject mParent;
        private MtpStorage mStorage;
        private FileObserver mObserver = null;
        private boolean mVisited = false;
        private MtpObjectState mState = MtpObjectState.NORMAL;
        private MtpOperation mOp = MtpOperation.NONE;

        MtpObject(String name, int id, MtpStorage storage, MtpObject parent, boolean isDir) {
            this.mId = id;
            this.mName = name;
            this.mStorage = (MtpStorage) Preconditions.checkNotNull(storage);
            this.mParent = parent;
            this.mIsDir = isDir;
            this.mChildren = this.mIsDir ? new HashMap<>() : null;
        }

        public String getName() {
            return this.mName;
        }

        public int getId() {
            return this.mId;
        }

        public boolean isDir() {
            return this.mIsDir;
        }

        public int getFormat() {
            if (this.mIsDir) {
                return 12289;
            }
            return MediaFile.getFormatCode(this.mName, null);
        }

        public int getStorageId() {
            return getRoot().getId();
        }

        public long getModifiedTime() {
            return getPath().toFile().lastModified() / 1000;
        }

        public MtpObject getParent() {
            return this.mParent;
        }

        public MtpObject getRoot() {
            return isRoot() ? this : this.mParent.getRoot();
        }

        public long getSize() {
            if (this.mIsDir) {
                return 0L;
            }
            return maybeApplyTranscodeLengthWorkaround(getPath().toFile().length());
        }

        private long maybeApplyTranscodeLengthWorkaround(long length) {
            if (this.mStorage.isHostWindows() && isTranscodeMtpEnabled() && isFileTranscodeSupported()) {
                return 2 * length;
            }
            return length;
        }

        private boolean isTranscodeMtpEnabled() {
            return SystemProperties.getBoolean("sys.fuse.transcode_mtp", false);
        }

        private boolean isFileTranscodeSupported() {
            Path path = getPath();
            try {
                StructStat stat = Os.stat(path.toString());
                return stat.st_nlink > 1;
            } catch (ErrnoException e) {
                Log.m104w(MtpStorageManager.TAG, "Failed to stat path: " + getPath() + ". Ignoring transcoding.");
                return false;
            }
        }

        public Path getPath() {
            return isRoot() ? Paths.get(this.mName, new String[0]) : this.mParent.getPath().resolve(this.mName);
        }

        public boolean isRoot() {
            return this.mParent == null;
        }

        public String getVolumeName() {
            return this.mStorage.getVolumeName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setName(String name) {
            this.mName = name;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setId(int id) {
            this.mId = id;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isVisited() {
            return this.mVisited;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setParent(MtpObject parent) {
            if (getStorageId() != parent.getStorageId()) {
                this.mStorage = (MtpStorage) Preconditions.checkNotNull(parent.getStorage());
            }
            this.mParent = parent;
        }

        private MtpStorage getStorage() {
            return this.mStorage;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDir(boolean dir) {
            if (dir != this.mIsDir) {
                this.mIsDir = dir;
                this.mChildren = dir ? new HashMap<>() : null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setVisited(boolean visited) {
            this.mVisited = visited;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public MtpObjectState getState() {
            return this.mState;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setState(MtpObjectState state) {
            this.mState = state;
            if (state == MtpObjectState.NORMAL) {
                this.mOp = MtpOperation.NONE;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public MtpOperation getOperation() {
            return this.mOp;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOperation(MtpOperation op) {
            this.mOp = op;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public FileObserver getObserver() {
            return this.mObserver;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setObserver(FileObserver observer) {
            this.mObserver = observer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addChild(MtpObject child) {
            this.mChildren.put(child.getName(), child);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public MtpObject getChild(String name) {
            return this.mChildren.get(name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Collection<MtpObject> getChildren() {
            return this.mChildren.values();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean exists() {
            return getPath().toFile().exists();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public MtpObject copy(boolean recursive) {
            MtpObject copy = new MtpObject(this.mName, this.mId, this.mStorage, this.mParent, this.mIsDir);
            copy.mIsDir = this.mIsDir;
            copy.mVisited = this.mVisited;
            copy.mState = this.mState;
            copy.mChildren = this.mIsDir ? new HashMap<>() : null;
            if (recursive && this.mIsDir) {
                for (MtpObject child : this.mChildren.values()) {
                    MtpObject childCopy = child.copy(true);
                    childCopy.setParent(copy);
                    copy.addChild(childCopy);
                }
            }
            return copy;
        }
    }

    public MtpStorageManager(MtpNotifier notifier, Set<String> subdirectories) {
        this.mMtpNotifier = notifier;
        this.mSubdirectories = subdirectories;
        if (this.mCheckConsistency) {
            this.mConsistencyThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        while (this.mCheckConsistency) {
            try {
                Thread.sleep(15000L);
                if (checkConsistency()) {
                    Log.m106v(TAG, "Cache is consistent");
                } else {
                    Log.m104w(TAG, "Cache is not consistent");
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public synchronized void close() {
        for (MtpObject obj : this.mObjects.values()) {
            if (obj.getObserver() != null) {
                obj.getObserver().stopWatching();
                obj.setObserver(null);
            }
        }
        for (MtpObject obj2 : this.mRoots.values()) {
            if (obj2.getObserver() != null) {
                obj2.getObserver().stopWatching();
                obj2.setObserver(null);
            }
        }
        if (this.mCheckConsistency) {
            this.mCheckConsistency = false;
            this.mConsistencyThread.interrupt();
            try {
                this.mConsistencyThread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    public synchronized void setSubdirectories(Set<String> subDirs) {
        this.mSubdirectories = subDirs;
    }

    public synchronized MtpStorage addMtpStorage(StorageVolume volume, Supplier<Boolean> isHostWindows) {
        MtpStorage storage;
        int storageId = ((getNextStorageId() & 65535) << 16) + 1;
        storage = new MtpStorage(volume, storageId, isHostWindows);
        MtpObject root = new MtpObject(storage.getPath(), storageId, storage, null, true);
        this.mRoots.put(Integer.valueOf(storageId), root);
        return storage;
    }

    public synchronized void removeMtpStorage(MtpStorage storage) {
        removeObjectFromCache(getStorageRoot(storage.getStorageId()), true, true);
    }

    private synchronized boolean isSpecialSubDir(MtpObject obj) {
        boolean z;
        Set<String> set;
        if (obj.getParent().isRoot() && (set = this.mSubdirectories) != null) {
            z = set.contains(obj.getName()) ? false : true;
        }
        return z;
    }

    public synchronized MtpObject getByPath(String path) {
        String[] split;
        MtpObject obj = null;
        for (MtpObject root : this.mRoots.values()) {
            if (path.startsWith(root.getName())) {
                obj = root;
                path = path.substring(root.getName().length());
            }
        }
        for (String name : path.split("/")) {
            if (obj != null && obj.isDir()) {
                if (!"".equals(name)) {
                    if (!obj.isVisited()) {
                        getChildren(obj);
                    }
                    obj = obj.getChild(name);
                }
            }
            return null;
        }
        return obj;
    }

    public synchronized MtpObject getObject(int id) {
        if (id == 0 || id == -1) {
            Log.m104w(TAG, "Can't get root storages with getObject()");
            return null;
        } else if (!this.mObjects.containsKey(Integer.valueOf(id))) {
            Log.m104w(TAG, "Id " + id + " doesn't exist");
            return null;
        } else {
            return this.mObjects.get(Integer.valueOf(id));
        }
    }

    public MtpObject getStorageRoot(int id) {
        if (!this.mRoots.containsKey(Integer.valueOf(id))) {
            Log.m104w(TAG, "StorageId " + id + " doesn't exist");
            return null;
        }
        return this.mRoots.get(Integer.valueOf(id));
    }

    private int getNextObjectId() {
        int ret = this.mNextObjectId;
        this.mNextObjectId = (int) (this.mNextObjectId + 1);
        return ret;
    }

    private int getNextStorageId() {
        int i = this.mNextStorageId;
        this.mNextStorageId = i + 1;
        return i;
    }

    public synchronized List<MtpObject> getObjects(int parent, int format, int storageId) {
        boolean recursive = parent == 0;
        ArrayList<MtpObject> objs = new ArrayList<>();
        boolean ret = true;
        if (parent == -1) {
            parent = 0;
        }
        if (storageId == -1 && parent == 0) {
            for (MtpObject root : this.mRoots.values()) {
                ret &= getObjects(objs, root, format, recursive);
            }
            return ret ? objs : null;
        }
        MtpObject obj = parent == 0 ? getStorageRoot(storageId) : getObject(parent);
        if (obj == null) {
            return null;
        }
        boolean ret2 = getObjects(objs, obj, format, recursive);
        return ret2 ? objs : null;
    }

    private synchronized boolean getObjects(List<MtpObject> toAdd, MtpObject parent, int format, boolean rec) {
        Collection<MtpObject> children = getChildren(parent);
        if (children == null) {
            return false;
        }
        for (MtpObject o : children) {
            if (format == 0 || o.getFormat() == format) {
                toAdd.add(o);
            }
        }
        boolean ret = true;
        if (rec) {
            for (MtpObject o2 : children) {
                if (o2.isDir()) {
                    ret &= getObjects(toAdd, o2, format, true);
                }
            }
        }
        return ret;
    }

    private synchronized Collection<MtpObject> getChildren(MtpObject object) {
        if (object != null) {
            if (object.isDir()) {
                if (!object.isVisited()) {
                    Path dir = object.getPath();
                    if (object.getObserver() != null) {
                        Log.m110e(TAG, "Observer is not null!");
                    }
                    object.setObserver(new MtpObjectObserver(object));
                    object.getObserver().startWatching();
                    try {
                        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
                        try {
                            for (Path file : stream) {
                                addObjectToCache(object, file.getFileName().toString(), file.toFile().isDirectory());
                            }
                            if (stream != null) {
                                stream.close();
                            }
                            object.setVisited(true);
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (IOException | DirectoryIteratorException e) {
                        Log.m110e(TAG, e.toString());
                        object.getObserver().stopWatching();
                        object.setObserver(null);
                        return null;
                    }
                }
                return object.getChildren();
            }
        }
        Log.m104w(TAG, "Can't find children of " + (object == null ? "null" : Integer.valueOf(object.getId())));
        return null;
    }

    private synchronized MtpObject addObjectToCache(MtpObject parent, String newName, boolean isDir) {
        if (parent.isRoot() || getObject(parent.getId()) == parent) {
            if (parent.getChild(newName) != null) {
                return null;
            }
            if (this.mSubdirectories == null || !parent.isRoot() || this.mSubdirectories.contains(newName)) {
                MtpObject obj = new MtpObject(newName, getNextObjectId(), parent.mStorage, parent, isDir);
                this.mObjects.put(Integer.valueOf(obj.getId()), obj);
                parent.addChild(obj);
                return obj;
            }
            return null;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0047 A[Catch: all -> 0x00d9, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0009, B:11:0x0021, B:13:0x0025, B:14:0x0041, B:16:0x0047, B:30:0x0078, B:32:0x007c, B:33:0x0098, B:35:0x009e, B:36:0x00a9, B:39:0x00b1, B:40:0x00be, B:42:0x00c4, B:23:0x0060), top: B:53:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005e  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x009e A[Catch: all -> 0x00d9, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0009, B:11:0x0021, B:13:0x0025, B:14:0x0041, B:16:0x0047, B:30:0x0078, B:32:0x007c, B:33:0x0098, B:35:0x009e, B:36:0x00a9, B:39:0x00b1, B:40:0x00be, B:42:0x00c4, B:23:0x0060), top: B:53:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00c4 A[Catch: all -> 0x00d9, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0009, B:11:0x0021, B:13:0x0025, B:14:0x0041, B:16:0x0047, B:30:0x0078, B:32:0x007c, B:33:0x0098, B:35:0x009e, B:36:0x00a9, B:39:0x00b1, B:40:0x00be, B:42:0x00c4, B:23:0x0060), top: B:53:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private synchronized boolean removeObjectFromCache(MtpObject removed, boolean removeGlobal, boolean recursive) {
        boolean ret;
        if (!removed.isRoot() && !removed.getParent().mChildren.remove(removed.getName(), removed)) {
            ret = false;
            if (!ret && sDebug) {
                Log.m104w(TAG, "Failed to remove from parent " + removed.getPath());
            }
            if (!removed.isRoot()) {
                ret = this.mRoots.remove(Integer.valueOf(removed.getId()), removed) && ret;
            } else if (removeGlobal) {
                ret = this.mObjects.remove(Integer.valueOf(removed.getId()), removed) && ret;
            }
            if (!ret && sDebug) {
                Log.m104w(TAG, "Failed to remove from global cache " + removed.getPath());
            }
            if (removed.getObserver() != null) {
                removed.getObserver().stopWatching();
                removed.setObserver(null);
            }
            if (removed.isDir() && recursive) {
                Collection<MtpObject> children = new ArrayList<>(removed.getChildren());
                for (MtpObject child : children) {
                    ret = removeObjectFromCache(child, removeGlobal, true) && ret;
                }
            }
        }
        ret = true;
        if (!ret) {
            Log.m104w(TAG, "Failed to remove from parent " + removed.getPath());
        }
        if (!removed.isRoot()) {
        }
        if (!ret) {
            Log.m104w(TAG, "Failed to remove from global cache " + removed.getPath());
        }
        if (removed.getObserver() != null) {
        }
        if (removed.isDir()) {
            Collection<MtpObject> children2 = new ArrayList<>(removed.getChildren());
            while (r4.hasNext()) {
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleAddedObject(MtpObject parent, String path, boolean isDir) {
        MtpOperation op = MtpOperation.NONE;
        MtpObject obj = parent.getChild(path);
        if (obj != null) {
            MtpObjectState state = obj.getState();
            op = obj.getOperation();
            if (obj.isDir() != isDir && state != MtpObjectState.FROZEN_REMOVED) {
                Log.m112d(TAG, "Inconsistent directory info! " + obj.getPath());
            }
            obj.setDir(isDir);
            switch (C20681.$SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[state.ordinal()]) {
                case 1:
                case 2:
                    obj.setState(MtpObjectState.FROZEN_ADDED);
                    break;
                case 3:
                    obj.setState(MtpObjectState.NORMAL);
                    break;
                case 4:
                case 5:
                    return;
                default:
                    Log.m104w(TAG, "Unexpected state in add " + path + " " + state);
                    break;
            }
            if (sDebug) {
                Log.m108i(TAG, state + " transitioned to " + obj.getState() + " in op " + op);
            }
        } else {
            obj = addObjectToCache(parent, path, isDir);
            if (obj != null) {
                this.mMtpNotifier.sendObjectAdded(obj.getId());
            } else {
                if (sDebug) {
                    Log.m104w(TAG, "object " + path + " already exists");
                }
                return;
            }
        }
        if (isDir) {
            if (op == MtpOperation.RENAME) {
                return;
            }
            if (op == MtpOperation.COPY && !obj.isVisited()) {
                return;
            }
            if (obj.getObserver() != null) {
                Log.m110e(TAG, "Observer is not null!");
                return;
            }
            obj.setObserver(new MtpObjectObserver(obj));
            obj.getObserver().startWatching();
            obj.setVisited(true);
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(obj.getPath());
                try {
                    for (Path file : stream) {
                        if (sDebug) {
                            Log.m108i(TAG, "Manually handling event for " + file.getFileName().toString());
                        }
                        handleAddedObject(obj, file.getFileName().toString(), file.toFile().isDirectory());
                    }
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException | DirectoryIteratorException e) {
                Log.m110e(TAG, e.toString());
                obj.getObserver().stopWatching();
                obj.setObserver(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.mtp.MtpStorageManager$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class C20681 {
        static final /* synthetic */ int[] $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState;

        static {
            int[] iArr = new int[MtpObjectState.values().length];
            $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState = iArr;
            try {
                iArr[MtpObjectState.FROZEN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[MtpObjectState.FROZEN_REMOVED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[MtpObjectState.FROZEN_ONESHOT_ADD.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[MtpObjectState.NORMAL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[MtpObjectState.FROZEN_ADDED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[MtpObjectState.FROZEN_ONESHOT_DEL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleRemovedObject(MtpObject obj) {
        MtpObjectState state = obj.getState();
        MtpOperation op = obj.getOperation();
        boolean z = true;
        switch (C20681.$SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[state.ordinal()]) {
            case 1:
                obj.setState(MtpObjectState.FROZEN_REMOVED);
                break;
            case 2:
            case 3:
            default:
                Log.m110e(TAG, "Got unexpected object remove for " + obj.getName());
                break;
            case 4:
                if (removeObjectFromCache(obj, true, true)) {
                    this.mMtpNotifier.sendObjectRemoved(obj.getId());
                    break;
                }
                break;
            case 5:
                obj.setState(MtpObjectState.FROZEN_REMOVED);
                break;
            case 6:
                if (op == MtpOperation.RENAME) {
                    z = false;
                }
                removeObjectFromCache(obj, z, false);
                break;
        }
        if (sDebug) {
            Log.m108i(TAG, state + " transitioned to " + obj.getState() + " in op " + op);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void handleChangedObject(MtpObject parent, String path) {
        MtpOperation mtpOperation = MtpOperation.NONE;
        MtpObject obj = parent.getChild(path);
        if (obj != null) {
            if (!obj.isDir() && obj.getSize() > 0) {
                obj.getState();
                obj.getOperation();
                this.mMtpNotifier.sendObjectInfoChanged(obj.getId());
                if (sDebug) {
                    Log.m112d(TAG, "sendObjectInfoChanged: id=" + obj.getId() + ",size=" + obj.getSize());
                }
            }
        } else if (sDebug) {
            Log.m104w(TAG, "object " + path + " null");
        }
    }

    public void flushEvents() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
        }
    }

    public synchronized void dump() {
        for (Integer num : this.mObjects.keySet()) {
            int key = num.intValue();
            MtpObject obj = this.mObjects.get(Integer.valueOf(key));
            Log.m108i(TAG, key + " | " + (obj.getParent() == null ? Integer.valueOf(obj.getParent().getId()) : "null") + " | " + obj.getName() + " | " + (obj.isDir() ? "dir" : "obj") + " | " + (obj.isVisited() ? "v" : "nv") + " | " + obj.getState());
        }
    }

    public synchronized boolean checkConsistency() {
        boolean ret;
        List<MtpObject> objs = new ArrayList<>();
        objs.addAll(this.mRoots.values());
        objs.addAll(this.mObjects.values());
        ret = true;
        for (MtpObject obj : objs) {
            if (!obj.exists()) {
                Log.m104w(TAG, "Object doesn't exist " + obj.getPath() + " " + obj.getId());
                ret = false;
            }
            if (obj.getState() != MtpObjectState.NORMAL) {
                Log.m104w(TAG, "Object " + obj.getPath() + " in state " + obj.getState());
                ret = false;
            }
            if (obj.getOperation() != MtpOperation.NONE) {
                Log.m104w(TAG, "Object " + obj.getPath() + " in operation " + obj.getOperation());
                ret = false;
            }
            if (!obj.isRoot() && this.mObjects.get(Integer.valueOf(obj.getId())) != obj) {
                Log.m104w(TAG, "Object " + obj.getPath() + " is not in map correctly");
                ret = false;
            }
            if (obj.getParent() != null) {
                if (obj.getParent().isRoot() && obj.getParent() != this.mRoots.get(Integer.valueOf(obj.getParent().getId()))) {
                    Log.m104w(TAG, "Root parent is not in root mapping " + obj.getPath());
                    ret = false;
                }
                if (!obj.getParent().isRoot() && obj.getParent() != this.mObjects.get(Integer.valueOf(obj.getParent().getId()))) {
                    Log.m104w(TAG, "Parent is not in object mapping " + obj.getPath());
                    ret = false;
                }
                if (obj.getParent().getChild(obj.getName()) != obj) {
                    Log.m104w(TAG, "Child does not exist in parent " + obj.getPath());
                    ret = false;
                }
            }
            if (obj.isDir()) {
                if (obj.isVisited() == (obj.getObserver() == null)) {
                    Log.m104w(TAG, obj.getPath() + " is " + (obj.isVisited() ? "" : "not ") + " visited but observer is " + obj.getObserver());
                    ret = false;
                }
                if (!obj.isVisited() && obj.getChildren().size() > 0) {
                    Log.m104w(TAG, obj.getPath() + " is not visited but has children");
                    ret = false;
                }
                try {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(obj.getPath());
                    try {
                        Set<String> files = new HashSet<>();
                        for (Path file : stream) {
                            if (obj.isVisited() && obj.getChild(file.getFileName().toString()) == null && (this.mSubdirectories == null || !obj.isRoot() || this.mSubdirectories.contains(file.getFileName().toString()))) {
                                Log.m104w(TAG, "File exists in fs but not in children " + file);
                                ret = false;
                            }
                            files.add(file.toString());
                        }
                        for (MtpObject child : obj.getChildren()) {
                            if (!files.contains(child.getPath().toString())) {
                                Log.m104w(TAG, "File in children doesn't exist in fs " + child.getPath());
                                ret = false;
                            }
                            if (child != this.mObjects.get(Integer.valueOf(child.getId()))) {
                                Log.m104w(TAG, "Child is not in object map " + child.getPath());
                                ret = false;
                            }
                        }
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (Throwable th) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                        break;
                    }
                } catch (IOException | DirectoryIteratorException e) {
                    Log.m104w(TAG, e.toString());
                    ret = false;
                }
            }
        }
        return ret;
    }

    public synchronized int beginSendObject(MtpObject parent, String name, int format) {
        Set<String> set;
        if (sDebug) {
            Log.m106v(TAG, "beginSendObject " + name);
        }
        if (parent.isDir()) {
            if (!parent.isRoot() || (set = this.mSubdirectories) == null || set.contains(name)) {
                getChildren(parent);
                MtpObject obj = addObjectToCache(parent, name, format == 12289);
                if (obj == null) {
                    return -1;
                }
                obj.setState(MtpObjectState.FROZEN);
                obj.setOperation(MtpOperation.ADD);
                return obj.getId();
            }
            return -1;
        }
        return -1;
    }

    public synchronized boolean endSendObject(MtpObject obj, boolean succeeded) {
        if (sDebug) {
            Log.m106v(TAG, "endSendObject " + succeeded);
        }
        return generalEndAddObject(obj, succeeded, true);
    }

    public synchronized boolean beginRenameObject(MtpObject obj, String newName) {
        if (sDebug) {
            Log.m106v(TAG, "beginRenameObject " + obj.getName() + " " + newName);
        }
        if (obj.isRoot()) {
            return false;
        }
        if (isSpecialSubDir(obj)) {
            return false;
        }
        if (obj.getParent().getChild(newName) != null) {
            return false;
        }
        MtpObject oldObj = obj.copy(false);
        obj.setName(newName);
        obj.getParent().addChild(obj);
        oldObj.getParent().addChild(oldObj);
        return generalBeginRenameObject(oldObj, obj);
    }

    public synchronized boolean endRenameObject(MtpObject obj, String oldName, boolean success) {
        MtpObject oldObj;
        if (sDebug) {
            Log.m106v(TAG, "endRenameObject " + success);
        }
        MtpObject parent = obj.getParent();
        oldObj = parent.getChild(oldName);
        if (!success) {
            MtpObjectState oldState = oldObj.getState();
            oldObj.setName(obj.getName());
            oldObj.setState(obj.getState());
            oldObj = obj;
            oldObj.setName(oldName);
            oldObj.setState(oldState);
            obj = oldObj;
            parent.addChild(obj);
            parent.addChild(oldObj);
        }
        return generalEndRenameObject(oldObj, obj, success);
    }

    public synchronized boolean beginRemoveObject(MtpObject obj) {
        boolean z;
        if (sDebug) {
            Log.m106v(TAG, "beginRemoveObject " + obj.getName());
        }
        if (!obj.isRoot() && !isSpecialSubDir(obj)) {
            z = generalBeginRemoveObject(obj, MtpOperation.DELETE);
        }
        return z;
    }

    public synchronized boolean endRemoveObject(MtpObject obj, boolean success) {
        boolean z;
        if (sDebug) {
            Log.m106v(TAG, "endRemoveObject " + success);
        }
        boolean ret = true;
        z = false;
        if (obj.isDir()) {
            Iterator it = new ArrayList(obj.getChildren()).iterator();
            while (it.hasNext()) {
                MtpObject child = (MtpObject) it.next();
                if (child.getOperation() == MtpOperation.DELETE) {
                    ret = endRemoveObject(child, success) && ret;
                }
            }
        }
        if (generalEndRemoveObject(obj, success, true) && ret) {
            z = true;
        }
        return z;
    }

    public synchronized boolean beginMoveObject(MtpObject obj, MtpObject newParent) {
        if (sDebug) {
            Log.m106v(TAG, "beginMoveObject " + newParent.getPath());
        }
        boolean z = false;
        if (obj.isRoot()) {
            return false;
        }
        if (isSpecialSubDir(obj)) {
            return false;
        }
        getChildren(newParent);
        if (newParent.getChild(obj.getName()) != null) {
            return false;
        }
        if (obj.getStorageId() != newParent.getStorageId()) {
            MtpObject newObj = obj.copy(true);
            newObj.setParent(newParent);
            newParent.addChild(newObj);
            if (generalBeginRemoveObject(obj, MtpOperation.RENAME) && generalBeginCopyObject(newObj, false)) {
                z = true;
            }
            return z;
        }
        MtpObject oldObj = obj.copy(false);
        obj.setParent(newParent);
        oldObj.getParent().addChild(oldObj);
        obj.getParent().addChild(obj);
        return generalBeginRenameObject(oldObj, obj);
    }

    public synchronized boolean endMoveObject(MtpObject oldParent, MtpObject newParent, String name, boolean success) {
        if (sDebug) {
            Log.m106v(TAG, "endMoveObject " + success);
        }
        MtpObject oldObj = oldParent.getChild(name);
        MtpObject newObj = newParent.getChild(name);
        boolean z = false;
        if (oldObj != null && newObj != null) {
            if (oldParent.getStorageId() != newObj.getStorageId()) {
                boolean ret = endRemoveObject(oldObj, success);
                if (generalEndCopyObject(newObj, success, true) && ret) {
                    z = true;
                }
                return z;
            }
            if (!success) {
                MtpObjectState oldState = oldObj.getState();
                oldObj.setParent(newObj.getParent());
                oldObj.setState(newObj.getState());
                oldObj = newObj;
                oldObj.setParent(oldParent);
                oldObj.setState(oldState);
                newObj = oldObj;
                newObj.getParent().addChild(newObj);
                oldParent.addChild(oldObj);
            }
            return generalEndRenameObject(oldObj, newObj, success);
        }
        return false;
    }

    public synchronized int beginCopyObject(MtpObject object, MtpObject newParent) {
        Set<String> set;
        if (sDebug) {
            Log.m106v(TAG, "beginCopyObject " + object.getName() + " to " + newParent.getPath());
        }
        String name = object.getName();
        if (newParent.isDir()) {
            if (!newParent.isRoot() || (set = this.mSubdirectories) == null || set.contains(name)) {
                getChildren(newParent);
                if (newParent.getChild(name) != null) {
                    return -1;
                }
                MtpObject newObj = object.copy(object.isDir());
                newParent.addChild(newObj);
                newObj.setParent(newParent);
                if (generalBeginCopyObject(newObj, true)) {
                    return newObj.getId();
                }
                return -1;
            }
            return -1;
        }
        return -1;
    }

    public synchronized boolean endCopyObject(MtpObject object, boolean success) {
        if (sDebug) {
            Log.m106v(TAG, "endCopyObject " + object.getName() + " " + success);
        }
        return generalEndCopyObject(object, success, false);
    }

    private synchronized boolean generalEndAddObject(MtpObject obj, boolean succeeded, boolean removeGlobal) {
        switch (C20681.$SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[obj.getState().ordinal()]) {
            case 1:
                if (succeeded) {
                    obj.setState(MtpObjectState.FROZEN_ONESHOT_ADD);
                    break;
                } else if (!removeObjectFromCache(obj, removeGlobal, false)) {
                    return false;
                }
                break;
            case 2:
                if (!removeObjectFromCache(obj, removeGlobal, false)) {
                    return false;
                }
                if (succeeded) {
                    this.mMtpNotifier.sendObjectRemoved(obj.getId());
                    break;
                }
                break;
            case 3:
            case 4:
            default:
                return false;
            case 5:
                obj.setState(MtpObjectState.NORMAL);
                if (!succeeded) {
                    MtpObject parent = obj.getParent();
                    if (removeObjectFromCache(obj, removeGlobal, false)) {
                        handleAddedObject(parent, obj.getName(), obj.isDir());
                        break;
                    } else {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private synchronized boolean generalEndRemoveObject(MtpObject obj, boolean success, boolean removeGlobal) {
        switch (C20681.$SwitchMap$android$mtp$MtpStorageManager$MtpObjectState[obj.getState().ordinal()]) {
            case 1:
                if (success) {
                    obj.setState(MtpObjectState.FROZEN_ONESHOT_DEL);
                    break;
                } else {
                    obj.setState(MtpObjectState.NORMAL);
                    break;
                }
            case 2:
                if (!removeObjectFromCache(obj, removeGlobal, false)) {
                    return false;
                }
                if (!success) {
                    this.mMtpNotifier.sendObjectRemoved(obj.getId());
                    break;
                }
                break;
            case 3:
            case 4:
            default:
                return false;
            case 5:
                obj.setState(MtpObjectState.NORMAL);
                if (success) {
                    MtpObject parent = obj.getParent();
                    if (removeObjectFromCache(obj, removeGlobal, false)) {
                        handleAddedObject(parent, obj.getName(), obj.isDir());
                        break;
                    } else {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    private synchronized boolean generalBeginRenameObject(MtpObject fromObj, MtpObject toObj) {
        fromObj.setState(MtpObjectState.FROZEN);
        toObj.setState(MtpObjectState.FROZEN);
        fromObj.setOperation(MtpOperation.RENAME);
        toObj.setOperation(MtpOperation.RENAME);
        return true;
    }

    private synchronized boolean generalEndRenameObject(MtpObject fromObj, MtpObject toObj, boolean success) {
        boolean z;
        z = true;
        boolean ret = generalEndRemoveObject(fromObj, success, !success);
        if (!generalEndAddObject(toObj, success, success) || !ret) {
            z = false;
        }
        return z;
    }

    private synchronized boolean generalBeginRemoveObject(MtpObject obj, MtpOperation op) {
        obj.setState(MtpObjectState.FROZEN);
        obj.setOperation(op);
        if (obj.isDir()) {
            for (MtpObject child : obj.getChildren()) {
                generalBeginRemoveObject(child, op);
            }
        }
        return true;
    }

    private synchronized boolean generalBeginCopyObject(MtpObject obj, boolean newId) {
        obj.setState(MtpObjectState.FROZEN);
        obj.setOperation(MtpOperation.COPY);
        if (newId) {
            obj.setId(getNextObjectId());
            this.mObjects.put(Integer.valueOf(obj.getId()), obj);
        }
        if (obj.isDir()) {
            for (MtpObject child : obj.getChildren()) {
                if (!generalBeginCopyObject(child, newId)) {
                    return false;
                }
            }
        }
        return true;
    }

    private synchronized boolean generalEndCopyObject(MtpObject obj, boolean success, boolean addGlobal) {
        boolean ret;
        boolean z;
        if (success && addGlobal) {
            this.mObjects.put(Integer.valueOf(obj.getId()), obj);
        }
        boolean ret2 = true;
        ret = false;
        if (obj.isDir()) {
            Iterator it = new ArrayList(obj.getChildren()).iterator();
            while (it.hasNext()) {
                MtpObject child = (MtpObject) it.next();
                if (child.getOperation() == MtpOperation.COPY) {
                    ret2 = generalEndCopyObject(child, success, addGlobal) && ret2;
                }
            }
        }
        if (!success && addGlobal) {
            z = false;
            if (generalEndAddObject(obj, success, z) && ret2) {
                ret = true;
            }
        }
        z = true;
        if (generalEndAddObject(obj, success, z)) {
            ret = true;
        }
        return ret;
    }
}
