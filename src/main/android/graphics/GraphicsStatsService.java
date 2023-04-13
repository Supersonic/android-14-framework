package android.graphics;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.job.JobInfo;
import android.app.time.LocationTimeZoneManager;
import android.content.Context;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.p008os.Binder;
import android.p008os.Environment;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.system.ErrnoException;
import android.text.format.Time;
import android.util.Log;
import android.view.IGraphicsStats;
import android.view.IGraphicsStatsCallback;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastPrintWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class GraphicsStatsService extends IGraphicsStats.Stub {
    private static final int AID_STATSD = 1066;
    private static final int DELETE_OLD = 2;
    public static final String GRAPHICS_STATS_SERVICE = "graphicsstats";
    private static final int SAVE_BUFFER = 1;
    private static final String TAG = "GraphicsStatsService";
    private ArrayList<ActiveBuffer> mActive;
    private final AlarmManager mAlarmManager;
    private final AppOpsManager mAppOps;
    private final int mAshmemSize;
    private final Context mContext;
    private final Object mFileAccessLock;
    private File mGraphicsStatsDir;
    private final Object mLock;
    private boolean mRotateIsScheduled;
    private Handler mWriteOutHandler;
    private final byte[] mZeroData;

    private static native void nAddToDump(long j, String str);

    private static native void nAddToDump(long j, String str, String str2, long j2, long j3, long j4, byte[] bArr);

    private static native long nCreateDump(int i, boolean z);

    private static native void nFinishDump(long j);

    private static native void nFinishDumpInMemory(long j, long j2, boolean z);

    private static native int nGetAshmemSize();

    private static native void nSaveBuffer(String str, String str2, long j, long j2, long j3, byte[] bArr);

    private static native void nativeDestructor();

    private native void nativeInit();

    public GraphicsStatsService(Context context) {
        int nGetAshmemSize = nGetAshmemSize();
        this.mAshmemSize = nGetAshmemSize;
        this.mZeroData = new byte[nGetAshmemSize];
        this.mLock = new Object();
        this.mActive = new ArrayList<>();
        this.mFileAccessLock = new Object();
        this.mRotateIsScheduled = false;
        this.mContext = context;
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        File systemDataDir = new File(Environment.getDataDirectory(), "system");
        File file = new File(systemDataDir, GRAPHICS_STATS_SERVICE);
        this.mGraphicsStatsDir = file;
        file.mkdirs();
        if (!this.mGraphicsStatsDir.exists()) {
            throw new IllegalStateException("Graphics stats directory does not exist: " + this.mGraphicsStatsDir.getAbsolutePath());
        }
        HandlerThread bgthread = new HandlerThread("GraphicsStats-disk", 10);
        bgthread.start();
        this.mWriteOutHandler = new Handler(bgthread.getLooper(), new Handler.Callback() { // from class: android.graphics.GraphicsStatsService.1
            @Override // android.p008os.Handler.Callback
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        GraphicsStatsService.this.saveBuffer((HistoricalBuffer) msg.obj);
                        return true;
                    case 2:
                        GraphicsStatsService.this.deleteOldBuffers();
                        return true;
                    default:
                        return true;
                }
            }
        });
        nativeInit();
    }

    private void scheduleRotateLocked() {
        if (this.mRotateIsScheduled) {
            return;
        }
        this.mRotateIsScheduled = true;
        Calendar calendar = normalizeDate(System.currentTimeMillis());
        calendar.add(5, 1);
        this.mAlarmManager.setExact(1, calendar.getTimeInMillis(), TAG, new AlarmManager.OnAlarmListener() { // from class: android.graphics.GraphicsStatsService$$ExternalSyntheticLambda0
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                GraphicsStatsService.this.onAlarm();
            }
        }, this.mWriteOutHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAlarm() {
        int i;
        ActiveBuffer[] activeCopy;
        synchronized (this.mLock) {
            this.mRotateIsScheduled = false;
            scheduleRotateLocked();
            activeCopy = (ActiveBuffer[]) this.mActive.toArray(new ActiveBuffer[0]);
        }
        for (ActiveBuffer active : activeCopy) {
            try {
                active.mCallback.onRotateGraphicsStatsBuffer();
            } catch (RemoteException e) {
                Log.m103w(TAG, String.format("Failed to notify '%s' (pid=%d) to rotate buffers", active.mInfo.mPackageName, Integer.valueOf(active.mPid)), e);
            }
        }
        this.mWriteOutHandler.sendEmptyMessageDelayed(2, JobInfo.MIN_BACKOFF_MILLIS);
    }

    @Override // android.view.IGraphicsStats
    public ParcelFileDescriptor requestBufferForProcess(String packageName, IGraphicsStatsCallback token) throws RemoteException {
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                this.mAppOps.checkPackage(uid, packageName);
                PackageInfo info = this.mContext.getPackageManager().getPackageInfoAsUser(packageName, 0, UserHandle.getUserId(uid));
                try {
                    synchronized (this.mLock) {
                        try {
                            ParcelFileDescriptor pfd = requestBufferForProcessLocked(token, uid, pid, packageName, info.getLongVersionCode());
                            return pfd;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RemoteException("Unable to find package: '" + packageName + "'");
            }
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    private void pullGraphicsStats(boolean lastFullDay, long pulledData) throws RemoteException {
        int uid = Binder.getCallingUid();
        if (uid != 1066) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new FastPrintWriter(sw);
            if (!DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, TAG, pw)) {
                pw.flush();
                throw new RemoteException(sw.toString());
            }
        }
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            pullGraphicsStatsImpl(lastFullDay, pulledData);
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    private void pullGraphicsStatsImpl(boolean lastFullDay, long pulledData) {
        long targetDay;
        if (lastFullDay) {
            targetDay = normalizeDate(System.currentTimeMillis() - 86400000).getTimeInMillis();
        } else {
            long targetDay2 = System.currentTimeMillis();
            targetDay = normalizeDate(targetDay2).getTimeInMillis();
        }
        synchronized (this.mLock) {
            try {
                ArrayList<HistoricalBuffer> buffers = new ArrayList<>(this.mActive.size());
                for (int i = 0; i < this.mActive.size(); i++) {
                    try {
                        ActiveBuffer buffer = this.mActive.get(i);
                        if (buffer.mInfo.mStartTime == targetDay) {
                            try {
                                buffers.add(new HistoricalBuffer(buffer));
                            } catch (IOException e) {
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        while (true) {
                            try {
                                break;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        throw th;
                    }
                }
                long dump = nCreateDump(-1, true);
                try {
                    synchronized (this.mFileAccessLock) {
                        try {
                            HashSet<File> skipList = dumpActiveLocked(dump, buffers);
                            buffers.clear();
                            int i2 = 0;
                            String subPath = String.format("%d", Long.valueOf(targetDay));
                            File dateDir = new File(this.mGraphicsStatsDir, subPath);
                            if (dateDir.exists()) {
                                File[] listFiles = dateDir.listFiles();
                                int length = listFiles.length;
                                while (i2 < length) {
                                    File pkg = listFiles[i2];
                                    File[] listFiles2 = pkg.listFiles();
                                    long targetDay3 = targetDay;
                                    try {
                                        int length2 = listFiles2.length;
                                        int i3 = 0;
                                        while (i3 < length2) {
                                            File version = listFiles2[i3];
                                            File[] fileArr = listFiles2;
                                            int i4 = length2;
                                            String subPath2 = subPath;
                                            File data = new File(version, "total");
                                            if (!skipList.contains(data)) {
                                                nAddToDump(dump, data.getAbsolutePath());
                                            }
                                            i3++;
                                            listFiles2 = fileArr;
                                            length2 = i4;
                                            subPath = subPath2;
                                        }
                                        i2++;
                                        targetDay = targetDay3;
                                    } catch (Throwable th3) {
                                        th = th3;
                                        try {
                                            throw th;
                                        } catch (Throwable th4) {
                                            th = th4;
                                            nFinishDumpInMemory(dump, pulledData, lastFullDay);
                                            throw th;
                                        }
                                    }
                                }
                            }
                            nFinishDumpInMemory(dump, pulledData, lastFullDay);
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    }
                } catch (Throwable th6) {
                    th = th6;
                }
            } catch (Throwable th7) {
                th = th7;
            }
        }
    }

    private ParcelFileDescriptor requestBufferForProcessLocked(IGraphicsStatsCallback token, int uid, int pid, String packageName, long versionCode) throws RemoteException {
        ActiveBuffer buffer = fetchActiveBuffersLocked(token, uid, pid, packageName, versionCode);
        scheduleRotateLocked();
        return buffer.getPfd();
    }

    private Calendar normalizeDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        calendar.setTimeInMillis(timestamp);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    private File pathForApp(BufferInfo info) {
        String subPath = String.format("%d/%s/%d/total", Long.valueOf(normalizeDate(info.mStartTime).getTimeInMillis()), info.mPackageName, Long.valueOf(info.mVersionCode));
        return new File(this.mGraphicsStatsDir, subPath);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveBuffer(HistoricalBuffer buffer) {
        if (Trace.isTagEnabled(524288L)) {
            Trace.traceBegin(524288L, "saving graphicsstats for " + buffer.mInfo.mPackageName);
        }
        synchronized (this.mFileAccessLock) {
            File path = pathForApp(buffer.mInfo);
            File parent = path.getParentFile();
            parent.mkdirs();
            if (!parent.exists()) {
                Log.m104w(TAG, "Unable to create path: '" + parent.getAbsolutePath() + "'");
                return;
            }
            nSaveBuffer(path.getAbsolutePath(), buffer.mInfo.mPackageName, buffer.mInfo.mVersionCode, buffer.mInfo.mStartTime, buffer.mInfo.mEndTime, buffer.mData);
            Trace.traceEnd(524288L);
        }
    }

    private void deleteRecursiveLocked(File file) {
        File[] listFiles;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursiveLocked(child);
            }
        }
        if (!file.delete()) {
            Log.m104w(TAG, "Failed to delete '" + file.getAbsolutePath() + "'!");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteOldBuffers() {
        Trace.traceBegin(524288L, "deleting old graphicsstats buffers");
        synchronized (this.mFileAccessLock) {
            File[] files = this.mGraphicsStatsDir.listFiles();
            if (files != null && files.length > 3) {
                long[] sortedDates = new long[files.length];
                for (int i = 0; i < files.length; i++) {
                    try {
                        sortedDates[i] = Long.parseLong(files[i].getName());
                    } catch (NumberFormatException e) {
                    }
                }
                int i2 = sortedDates.length;
                if (i2 <= 3) {
                    return;
                }
                Arrays.sort(sortedDates);
                for (int i3 = 0; i3 < sortedDates.length - 3; i3++) {
                    deleteRecursiveLocked(new File(this.mGraphicsStatsDir, Long.toString(sortedDates[i3])));
                }
                Trace.traceEnd(524288L);
            }
        }
    }

    private void addToSaveQueue(ActiveBuffer buffer) {
        try {
            HistoricalBuffer data = new HistoricalBuffer(buffer);
            Message.obtain(this.mWriteOutHandler, 1, data).sendToTarget();
        } catch (IOException e) {
            Log.m103w(TAG, "Failed to copy graphicsstats from " + buffer.mInfo.mPackageName, e);
        }
        buffer.closeAllBuffers();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processDied(ActiveBuffer buffer) {
        synchronized (this.mLock) {
            this.mActive.remove(buffer);
        }
        addToSaveQueue(buffer);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0040, code lost:
        r0 = new android.graphics.GraphicsStatsService.ActiveBuffer(r15, r16, r17, r18, r19, r20);
        r15.mActive.add(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0056, code lost:
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x005f, code lost:
        throw new android.p008os.RemoteException("Failed to allocate space");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private ActiveBuffer fetchActiveBuffersLocked(IGraphicsStatsCallback token, int uid, int pid, String packageName, long versionCode) throws RemoteException {
        int size = this.mActive.size();
        long today = normalizeDate(System.currentTimeMillis()).getTimeInMillis();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            ActiveBuffer buffer = this.mActive.get(i);
            if (buffer.mPid == pid && buffer.mUid == uid) {
                if (buffer.mInfo.mStartTime < today) {
                    buffer.binderDied();
                } else {
                    return buffer;
                }
            }
            i++;
        }
    }

    private HashSet<File> dumpActiveLocked(long dump, ArrayList<HistoricalBuffer> buffers) {
        HashSet<File> skipFiles = new HashSet<>(buffers.size());
        for (int i = 0; i < buffers.size(); i++) {
            HistoricalBuffer buffer = buffers.get(i);
            File path = pathForApp(buffer.mInfo);
            skipFiles.add(path);
            nAddToDump(dump, path.getAbsolutePath(), buffer.mInfo.mPackageName, buffer.mInfo.mVersionCode, buffer.mInfo.mStartTime, buffer.mInfo.mEndTime, buffer.mData);
        }
        return skipFiles;
    }

    private void dumpHistoricalLocked(long dump, HashSet<File> skipFiles) {
        File[] fileArr;
        File[] listFiles = this.mGraphicsStatsDir.listFiles();
        int length = listFiles.length;
        int i = 0;
        while (i < length) {
            File date = listFiles[i];
            File[] listFiles2 = date.listFiles();
            int length2 = listFiles2.length;
            int i2 = 0;
            while (i2 < length2) {
                File pkg = listFiles2[i2];
                File[] listFiles3 = pkg.listFiles();
                int length3 = listFiles3.length;
                int i3 = 0;
                while (i3 < length3) {
                    File version = listFiles3[i3];
                    File data = new File(version, "total");
                    if (skipFiles.contains(data)) {
                        fileArr = listFiles;
                    } else {
                        fileArr = listFiles;
                        nAddToDump(dump, data.getAbsolutePath());
                    }
                    i3++;
                    listFiles = fileArr;
                }
                i2++;
                listFiles = listFiles;
            }
            i++;
            listFiles = listFiles;
        }
    }

    @Override // android.p008os.Binder
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
        ArrayList<HistoricalBuffer> buffers;
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, TAG, fout)) {
            boolean dumpProto = false;
            int length = args.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String str = args[i];
                if (!LocationTimeZoneManager.DUMP_STATE_OPTION_PROTO.equals(str)) {
                    i++;
                } else {
                    dumpProto = true;
                    break;
                }
            }
            synchronized (this.mLock) {
                buffers = new ArrayList<>(this.mActive.size());
                for (int i2 = 0; i2 < this.mActive.size(); i2++) {
                    try {
                        buffers.add(new HistoricalBuffer(this.mActive.get(i2)));
                    } catch (IOException e) {
                    }
                }
            }
            long dump = nCreateDump(fd.getInt$(), dumpProto);
            try {
                synchronized (this.mFileAccessLock) {
                    HashSet<File> skipList = dumpActiveLocked(dump, buffers);
                    buffers.clear();
                    dumpHistoricalLocked(dump, skipList);
                }
            } finally {
                nFinishDump(dump);
            }
        }
    }

    protected void finalize() throws Throwable {
        nativeDestructor();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class BufferInfo {
        long mEndTime;
        final String mPackageName;
        long mStartTime;
        final long mVersionCode;

        BufferInfo(String packageName, long versionCode, long startTime) {
            this.mPackageName = packageName;
            this.mVersionCode = versionCode;
            this.mStartTime = startTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ActiveBuffer implements IBinder.DeathRecipient {
        final IGraphicsStatsCallback mCallback;
        final BufferInfo mInfo;
        ByteBuffer mMapping;
        final int mPid;
        SharedMemory mProcessBuffer;
        final IBinder mToken;
        final int mUid;

        ActiveBuffer(IGraphicsStatsCallback token, int uid, int pid, String packageName, long versionCode) throws RemoteException, IOException {
            this.mInfo = new BufferInfo(packageName, versionCode, System.currentTimeMillis());
            this.mUid = uid;
            this.mPid = pid;
            this.mCallback = token;
            IBinder asBinder = token.asBinder();
            this.mToken = asBinder;
            asBinder.linkToDeath(this, 0);
            try {
                SharedMemory create = SharedMemory.create("GFXStats-" + pid, GraphicsStatsService.this.mAshmemSize);
                this.mProcessBuffer = create;
                this.mMapping = create.mapReadWrite();
            } catch (ErrnoException ex) {
                ex.rethrowAsIOException();
            }
            this.mMapping.position(0);
            this.mMapping.put(GraphicsStatsService.this.mZeroData, 0, GraphicsStatsService.this.mAshmemSize);
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            this.mToken.unlinkToDeath(this, 0);
            GraphicsStatsService.this.processDied(this);
        }

        void closeAllBuffers() {
            ByteBuffer byteBuffer = this.mMapping;
            if (byteBuffer != null) {
                SharedMemory.unmap(byteBuffer);
                this.mMapping = null;
            }
            SharedMemory sharedMemory = this.mProcessBuffer;
            if (sharedMemory != null) {
                sharedMemory.close();
                this.mProcessBuffer = null;
            }
        }

        ParcelFileDescriptor getPfd() {
            try {
                return this.mProcessBuffer.getFdDup();
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to get PFD from memory file", ex);
            }
        }

        void readBytes(byte[] buffer, int count) throws IOException {
            ByteBuffer byteBuffer = this.mMapping;
            if (byteBuffer == null) {
                throw new IOException("SharedMemory has been deactivated");
            }
            byteBuffer.position(0);
            this.mMapping.get(buffer, 0, count);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class HistoricalBuffer {
        final byte[] mData;
        final BufferInfo mInfo;

        HistoricalBuffer(ActiveBuffer active) throws IOException {
            byte[] bArr = new byte[GraphicsStatsService.this.mAshmemSize];
            this.mData = bArr;
            BufferInfo bufferInfo = active.mInfo;
            this.mInfo = bufferInfo;
            bufferInfo.mEndTime = System.currentTimeMillis();
            active.readBytes(bArr, GraphicsStatsService.this.mAshmemSize);
        }
    }
}
