package com.android.server.appop;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.UserPackage;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AppOpsCheckingServiceImpl implements AppOpsCheckingServiceInterface {
    @VisibleForTesting
    static final int CURRENT_VERSION = 3;
    public final Context mContext;
    public boolean mFastWriteScheduled;
    public final AtomicFile mFile;
    public final Handler mHandler;
    public final Object mLock;
    public final SparseArray<int[]> mSwitchedOps;
    public boolean mWriteScheduled;
    public int mVersionAtBoot = -2;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final SparseArray<SparseIntArray> mUidModes = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<ArrayMap<String, SparseIntArray>> mUserPackageModes = new SparseArray<>();
    public final SparseArray<ArraySet<OnOpModeChangedListener>> mOpModeWatchers = new SparseArray<>();
    public final ArrayMap<String, ArraySet<OnOpModeChangedListener>> mPackageModeWatchers = new ArrayMap<>();
    public final Runnable mWriteRunner = new Runnable() { // from class: com.android.server.appop.AppOpsCheckingServiceImpl.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (AppOpsCheckingServiceImpl.this.mLock) {
                AppOpsCheckingServiceImpl appOpsCheckingServiceImpl = AppOpsCheckingServiceImpl.this;
                appOpsCheckingServiceImpl.mWriteScheduled = false;
                appOpsCheckingServiceImpl.mFastWriteScheduled = false;
                new AsyncTask<Void, Void, Void>() { // from class: com.android.server.appop.AppOpsCheckingServiceImpl.1.1
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... voidArr) {
                        AppOpsCheckingServiceImpl.this.writeState();
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }
    };

    public AppOpsCheckingServiceImpl(File file, Object obj, Handler handler, Context context, SparseArray<int[]> sparseArray) {
        this.mFile = new AtomicFile(file);
        this.mLock = obj;
        this.mHandler = handler;
        this.mContext = context;
        this.mSwitchedOps = sparseArray;
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void systemReady() {
        synchronized (this.mLock) {
            upgradeLocked(this.mVersionAtBoot);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseIntArray getNonDefaultUidModes(int i) {
        synchronized (this.mLock) {
            SparseIntArray sparseIntArray = this.mUidModes.get(i, null);
            if (sparseIntArray == null) {
                return new SparseIntArray();
            }
            return sparseIntArray.clone();
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseIntArray getNonDefaultPackageModes(String str, int i) {
        synchronized (this.mLock) {
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i);
            if (arrayMap == null) {
                return new SparseIntArray();
            }
            SparseIntArray sparseIntArray = arrayMap.get(str);
            if (sparseIntArray == null) {
                return new SparseIntArray();
            }
            return sparseIntArray.clone();
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public int getUidMode(int i, int i2) {
        synchronized (this.mLock) {
            SparseIntArray sparseIntArray = this.mUidModes.get(i, null);
            if (sparseIntArray == null) {
                return AppOpsManager.opToDefaultMode(i2);
            }
            return sparseIntArray.get(i2, AppOpsManager.opToDefaultMode(i2));
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public boolean setUidMode(int i, int i2, int i3) {
        int opToDefaultMode = AppOpsManager.opToDefaultMode(i2);
        synchronized (this.mLock) {
            SparseIntArray sparseIntArray = this.mUidModes.get(i, null);
            if (sparseIntArray == null) {
                if (i3 != opToDefaultMode) {
                    SparseIntArray sparseIntArray2 = new SparseIntArray();
                    this.mUidModes.put(i, sparseIntArray2);
                    sparseIntArray2.put(i2, i3);
                    scheduleWriteLocked();
                }
            } else if (sparseIntArray.indexOfKey(i2) >= 0 && sparseIntArray.get(i2) == i3) {
                return false;
            } else {
                if (i3 == opToDefaultMode) {
                    sparseIntArray.delete(i2);
                    if (sparseIntArray.size() <= 0) {
                        this.mUidModes.delete(i);
                    }
                } else {
                    sparseIntArray.put(i2, i3);
                }
                scheduleWriteLocked();
            }
            return true;
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public int getPackageMode(String str, int i, int i2) {
        synchronized (this.mLock) {
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i2, null);
            if (arrayMap == null) {
                return AppOpsManager.opToDefaultMode(i);
            }
            SparseIntArray orDefault = arrayMap.getOrDefault(str, null);
            if (orDefault == null) {
                return AppOpsManager.opToDefaultMode(i);
            }
            return orDefault.get(i, AppOpsManager.opToDefaultMode(i));
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void setPackageMode(String str, int i, int i2, int i3) {
        int opToDefaultMode = AppOpsManager.opToDefaultMode(i);
        synchronized (this.mLock) {
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i3, null);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mUserPackageModes.put(i3, arrayMap);
            }
            SparseIntArray sparseIntArray = arrayMap.get(str);
            if (sparseIntArray == null) {
                if (i2 != opToDefaultMode) {
                    SparseIntArray sparseIntArray2 = new SparseIntArray();
                    arrayMap.put(str, sparseIntArray2);
                    sparseIntArray2.put(i, i2);
                    scheduleWriteLocked();
                }
            } else if (sparseIntArray.indexOfKey(i) >= 0 && sparseIntArray.get(i) == i2) {
            } else {
                if (i2 == opToDefaultMode) {
                    sparseIntArray.delete(i);
                    if (sparseIntArray.size() <= 0) {
                        arrayMap.remove(str);
                    }
                } else {
                    sparseIntArray.put(i, i2);
                }
                scheduleWriteLocked();
            }
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void removeUid(int i) {
        synchronized (this.mLock) {
            if (this.mUidModes.get(i) == null) {
                return;
            }
            this.mUidModes.remove(i);
            scheduleFastWriteLocked();
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public boolean removePackage(String str, int i) {
        synchronized (this.mLock) {
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i, null);
            if (arrayMap == null) {
                return false;
            }
            if (arrayMap.remove(str) != null) {
                scheduleFastWriteLocked();
                return true;
            }
            return false;
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void clearAllModes() {
        synchronized (this.mLock) {
            this.mUidModes.clear();
            this.mUserPackageModes.clear();
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void startWatchingOpModeChanged(OnOpModeChangedListener onOpModeChangedListener, int i) {
        Objects.requireNonNull(onOpModeChangedListener);
        synchronized (this.mLock) {
            ArraySet<OnOpModeChangedListener> arraySet = this.mOpModeWatchers.get(i);
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                this.mOpModeWatchers.put(i, arraySet);
            }
            arraySet.add(onOpModeChangedListener);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void startWatchingPackageModeChanged(OnOpModeChangedListener onOpModeChangedListener, String str) {
        Objects.requireNonNull(onOpModeChangedListener);
        Objects.requireNonNull(str);
        synchronized (this.mLock) {
            ArraySet<OnOpModeChangedListener> arraySet = this.mPackageModeWatchers.get(str);
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                this.mPackageModeWatchers.put(str, arraySet);
            }
            arraySet.add(onOpModeChangedListener);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void removeListener(OnOpModeChangedListener onOpModeChangedListener) {
        Objects.requireNonNull(onOpModeChangedListener);
        synchronized (this.mLock) {
            for (int size = this.mOpModeWatchers.size() - 1; size >= 0; size--) {
                ArraySet<OnOpModeChangedListener> valueAt = this.mOpModeWatchers.valueAt(size);
                valueAt.remove(onOpModeChangedListener);
                if (valueAt.size() <= 0) {
                    this.mOpModeWatchers.removeAt(size);
                }
            }
            for (int size2 = this.mPackageModeWatchers.size() - 1; size2 >= 0; size2--) {
                ArraySet<OnOpModeChangedListener> valueAt2 = this.mPackageModeWatchers.valueAt(size2);
                valueAt2.remove(onOpModeChangedListener);
                if (valueAt2.size() <= 0) {
                    this.mPackageModeWatchers.removeAt(size2);
                }
            }
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public ArraySet<OnOpModeChangedListener> getOpModeChangedListeners(int i) {
        synchronized (this.mLock) {
            ArraySet<OnOpModeChangedListener> arraySet = this.mOpModeWatchers.get(i);
            if (arraySet == null) {
                return new ArraySet<>();
            }
            return new ArraySet<>(arraySet);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public ArraySet<OnOpModeChangedListener> getPackageModeChangedListeners(String str) {
        Objects.requireNonNull(str);
        synchronized (this.mLock) {
            ArraySet<OnOpModeChangedListener> arraySet = this.mPackageModeWatchers.get(str);
            if (arraySet == null) {
                return new ArraySet<>();
            }
            return new ArraySet<>(arraySet);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyWatchersOfChange(int i, int i2) {
        ArraySet<OnOpModeChangedListener> opModeChangedListeners = getOpModeChangedListeners(i);
        if (opModeChangedListeners == null) {
            return;
        }
        for (int i3 = 0; i3 < opModeChangedListeners.size(); i3++) {
            notifyOpChanged(opModeChangedListeners.valueAt(i3), i, i2, null);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyOpChanged(OnOpModeChangedListener onOpModeChangedListener, int i, int i2, String str) {
        int[] iArr;
        Objects.requireNonNull(onOpModeChangedListener);
        if (i2 == -2 || onOpModeChangedListener.getWatchingUid() < 0 || onOpModeChangedListener.getWatchingUid() == i2) {
            if (onOpModeChangedListener.getWatchedOpCode() == -2) {
                iArr = this.mSwitchedOps.get(i);
            } else if (onOpModeChangedListener.getWatchedOpCode() == -1) {
                iArr = new int[]{i};
            } else {
                iArr = new int[]{onOpModeChangedListener.getWatchedOpCode()};
            }
            for (int i3 : iArr) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (!shouldIgnoreCallback(i3, onOpModeChangedListener.getCallingPid(), onOpModeChangedListener.getCallingUid())) {
                        onOpModeChangedListener.onOpModeChanged(i3, i2, str);
                    }
                } catch (RemoteException unused) {
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final boolean shouldIgnoreCallback(int i, int i2, int i3) {
        return AppOpsManager.opRestrictsRead(i) && this.mContext.checkPermission("android.permission.MANAGE_APPOPS", i2, i3) != 0;
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyOpChangedForAllPkgsInUid(int i, int i2, boolean z, OnOpModeChangedListener onOpModeChangedListener) {
        ArrayMap arrayMap;
        String[] packagesForUid = getPackagesForUid(i2);
        synchronized (this.mLock) {
            ArraySet<OnOpModeChangedListener> arraySet = this.mOpModeWatchers.get(i);
            ArrayMap arrayMap2 = null;
            if (arraySet != null) {
                int size = arraySet.size();
                for (int i3 = 0; i3 < size; i3++) {
                    OnOpModeChangedListener valueAt = arraySet.valueAt(i3);
                    if (!z || (valueAt.getFlags() & 1) != 0) {
                        ArraySet arraySet2 = new ArraySet();
                        Collections.addAll(arraySet2, packagesForUid);
                        if (arrayMap2 == null) {
                            arrayMap2 = new ArrayMap();
                        }
                        arrayMap2.put(valueAt, arraySet2);
                    }
                }
            }
            arrayMap = arrayMap2;
            for (String str : packagesForUid) {
                ArraySet<OnOpModeChangedListener> arraySet3 = this.mPackageModeWatchers.get(str);
                if (arraySet3 != null) {
                    if (arrayMap == null) {
                        arrayMap = new ArrayMap();
                    }
                    int size2 = arraySet3.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        OnOpModeChangedListener valueAt2 = arraySet3.valueAt(i4);
                        if (!z || (valueAt2.getFlags() & 1) != 0) {
                            ArraySet arraySet4 = (ArraySet) arrayMap.get(valueAt2);
                            if (arraySet4 == null) {
                                arraySet4 = new ArraySet();
                                arrayMap.put(valueAt2, arraySet4);
                            }
                            arraySet4.add(str);
                        }
                    }
                }
            }
            if (arrayMap != null && onOpModeChangedListener != null) {
                arrayMap.remove(onOpModeChangedListener);
            }
        }
        if (arrayMap == null) {
            return;
        }
        for (int i5 = 0; i5 < arrayMap.size(); i5++) {
            OnOpModeChangedListener onOpModeChangedListener2 = (OnOpModeChangedListener) arrayMap.keyAt(i5);
            ArraySet arraySet5 = (ArraySet) arrayMap.valueAt(i5);
            if (arraySet5 == null) {
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.appop.AppOpsCheckingServiceImpl$$ExternalSyntheticLambda0
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                        ((AppOpsCheckingServiceImpl) obj).notifyOpChanged((OnOpModeChangedListener) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5);
                    }
                }, this, onOpModeChangedListener2, Integer.valueOf(i), Integer.valueOf(i2), (Object) null));
            } else {
                int size3 = arraySet5.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.appop.AppOpsCheckingServiceImpl$$ExternalSyntheticLambda0
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                            ((AppOpsCheckingServiceImpl) obj).notifyOpChanged((OnOpModeChangedListener) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5);
                        }
                    }, this, onOpModeChangedListener2, Integer.valueOf(i), Integer.valueOf(i2), (String) arraySet5.valueAt(i6)));
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0012  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String[] getPackagesForUid(int i) {
        String[] packagesForUid;
        if (AppGlobals.getPackageManager() != null) {
            try {
                packagesForUid = AppGlobals.getPackageManager().getPackagesForUid(i);
            } catch (RemoteException unused) {
            }
            return packagesForUid != null ? EmptyArray.STRING : packagesForUid;
        }
        packagesForUid = null;
        if (packagesForUid != null) {
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseBooleanArray evalForegroundUidOps(int i, SparseBooleanArray sparseBooleanArray) {
        SparseBooleanArray evalForegroundOps;
        synchronized (this.mLock) {
            evalForegroundOps = evalForegroundOps(this.mUidModes.get(i), sparseBooleanArray);
        }
        return evalForegroundOps;
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseBooleanArray evalForegroundPackageOps(String str, SparseBooleanArray sparseBooleanArray, int i) {
        SparseBooleanArray evalForegroundOps;
        synchronized (this.mLock) {
            SparseIntArray sparseIntArray = null;
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i, null);
            if (arrayMap != null) {
                sparseIntArray = arrayMap.get(str);
            }
            evalForegroundOps = evalForegroundOps(sparseIntArray, sparseBooleanArray);
        }
        return evalForegroundOps;
    }

    public final SparseBooleanArray evalForegroundOps(SparseIntArray sparseIntArray, SparseBooleanArray sparseBooleanArray) {
        if (sparseIntArray != null) {
            for (int size = sparseIntArray.size() - 1; size >= 0; size--) {
                if (sparseIntArray.valueAt(size) == 4) {
                    if (sparseBooleanArray == null) {
                        sparseBooleanArray = new SparseBooleanArray();
                    }
                    evalForegroundWatchers(sparseIntArray.keyAt(size), sparseBooleanArray);
                }
            }
        }
        return sparseBooleanArray;
    }

    public final void evalForegroundWatchers(int i, SparseBooleanArray sparseBooleanArray) {
        boolean z = sparseBooleanArray.get(i, false);
        ArraySet<OnOpModeChangedListener> arraySet = this.mOpModeWatchers.get(i);
        if (arraySet != null) {
            for (int size = arraySet.size() - 1; !z && size >= 0; size--) {
                if ((arraySet.valueAt(size).getFlags() & 1) != 0) {
                    z = true;
                }
            }
        }
        sparseBooleanArray.put(i, z);
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0055, code lost:
        r21.println("  Op mode watchers:");
        r11 = true;
     */
    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean dumpListeners(int i, int i2, String str, PrintWriter printWriter) {
        boolean z;
        int i3;
        if (this.mOpModeWatchers.size() > 0) {
            z = false;
            boolean z2 = false;
            for (int i4 = 0; i4 < this.mOpModeWatchers.size(); i4++) {
                if (i < 0 || i == this.mOpModeWatchers.keyAt(i4)) {
                    ArraySet<OnOpModeChangedListener> valueAt = this.mOpModeWatchers.valueAt(i4);
                    boolean z3 = false;
                    while (i3 < valueAt.size()) {
                        OnOpModeChangedListener valueAt2 = valueAt.valueAt(i3);
                        i3 = (str == null || i2 == UserHandle.getAppId(valueAt2.getWatchingUid())) ? 0 : i3 + 1;
                        if (!z3) {
                            printWriter.print("    Op ");
                            printWriter.print(AppOpsManager.opToName(this.mOpModeWatchers.keyAt(i4)));
                            printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                            z3 = true;
                        }
                        printWriter.print("      #");
                        printWriter.print(i3);
                        printWriter.print(": ");
                        printWriter.println(valueAt2.toString());
                        z = true;
                    }
                }
            }
        } else {
            z = false;
        }
        if (this.mPackageModeWatchers.size() > 0 && i < 0) {
            boolean z4 = false;
            for (int i5 = 0; i5 < this.mPackageModeWatchers.size(); i5++) {
                if (str == null || str.equals(this.mPackageModeWatchers.keyAt(i5))) {
                    if (!z4) {
                        printWriter.println("  Package mode watchers:");
                        z4 = true;
                    }
                    printWriter.print("    Pkg ");
                    printWriter.print(this.mPackageModeWatchers.keyAt(i5));
                    printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                    ArraySet<OnOpModeChangedListener> valueAt3 = this.mPackageModeWatchers.valueAt(i5);
                    for (int i6 = 0; i6 < valueAt3.size(); i6++) {
                        printWriter.print("      #");
                        printWriter.print(i6);
                        printWriter.print(": ");
                        printWriter.println(valueAt3.valueAt(i6).toString());
                    }
                    z = true;
                }
            }
        }
        return z;
    }

    public final void scheduleWriteLocked() {
        if (this.mWriteScheduled) {
            return;
        }
        this.mWriteScheduled = true;
        this.mHandler.postDelayed(this.mWriteRunner, 1800000L);
    }

    public final void scheduleFastWriteLocked() {
        if (this.mFastWriteScheduled) {
            return;
        }
        this.mWriteScheduled = true;
        this.mFastWriteScheduled = true;
        this.mHandler.removeCallbacks(this.mWriteRunner);
        this.mHandler.postDelayed(this.mWriteRunner, 10000L);
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void writeState() {
        int size;
        int size2;
        synchronized (this.mFile) {
            try {
                try {
                    FileOutputStream startWrite = this.mFile.startWrite();
                    try {
                        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                        resolveSerializer.startDocument((String) null, Boolean.TRUE);
                        resolveSerializer.startTag((String) null, "app-ops");
                        resolveSerializer.attributeInt((String) null, "v", 3);
                        SparseArray sparseArray = new SparseArray();
                        SparseArray sparseArray2 = new SparseArray();
                        synchronized (this.mLock) {
                            size = this.mUidModes.size();
                            for (int i = 0; i < size; i++) {
                                sparseArray.put(this.mUidModes.keyAt(i), this.mUidModes.valueAt(i).clone());
                            }
                            size2 = this.mUserPackageModes.size();
                            for (int i2 = 0; i2 < size2; i2++) {
                                int keyAt = this.mUserPackageModes.keyAt(i2);
                                ArrayMap<String, SparseIntArray> valueAt = this.mUserPackageModes.valueAt(i2);
                                ArrayMap arrayMap = new ArrayMap();
                                sparseArray2.put(keyAt, arrayMap);
                                int size3 = valueAt.size();
                                for (int i3 = 0; i3 < size3; i3++) {
                                    arrayMap.put(valueAt.keyAt(i3), valueAt.valueAt(i3).clone());
                                }
                            }
                        }
                        for (int i4 = 0; i4 < size; i4++) {
                            int keyAt2 = sparseArray.keyAt(i4);
                            SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.valueAt(i4);
                            resolveSerializer.startTag((String) null, "uid");
                            resolveSerializer.attributeInt((String) null, "n", keyAt2);
                            int size4 = sparseIntArray.size();
                            for (int i5 = 0; i5 < size4; i5++) {
                                int keyAt3 = sparseIntArray.keyAt(i5);
                                int valueAt2 = sparseIntArray.valueAt(i5);
                                resolveSerializer.startTag((String) null, "op");
                                resolveSerializer.attributeInt((String) null, "n", keyAt3);
                                resolveSerializer.attributeInt((String) null, "m", valueAt2);
                                resolveSerializer.endTag((String) null, "op");
                            }
                            resolveSerializer.endTag((String) null, "uid");
                        }
                        for (int i6 = 0; i6 < size2; i6++) {
                            int keyAt4 = sparseArray2.keyAt(i6);
                            ArrayMap arrayMap2 = (ArrayMap) sparseArray2.valueAt(i6);
                            resolveSerializer.startTag((String) null, "user");
                            resolveSerializer.attributeInt((String) null, "n", keyAt4);
                            int size5 = arrayMap2.size();
                            int i7 = 0;
                            while (i7 < size5) {
                                SparseIntArray sparseIntArray2 = (SparseIntArray) arrayMap2.valueAt(i7);
                                resolveSerializer.startTag((String) null, "pkg");
                                resolveSerializer.attribute((String) null, "n", (String) arrayMap2.keyAt(i7));
                                int size6 = sparseIntArray2.size();
                                int i8 = 0;
                                while (i8 < size6) {
                                    int keyAt5 = sparseIntArray2.keyAt(i8);
                                    int valueAt3 = sparseIntArray2.valueAt(i8);
                                    resolveSerializer.startTag((String) null, "op");
                                    resolveSerializer.attributeInt((String) null, "n", keyAt5);
                                    resolveSerializer.attributeInt((String) null, "m", valueAt3);
                                    resolveSerializer.endTag((String) null, "op");
                                    i8++;
                                    size5 = size5;
                                }
                                resolveSerializer.endTag((String) null, "pkg");
                                i7++;
                                size5 = size5;
                            }
                            resolveSerializer.endTag((String) null, "user");
                        }
                        resolveSerializer.endTag((String) null, "app-ops");
                        resolveSerializer.endDocument();
                        this.mFile.finishWrite(startWrite);
                    } catch (IOException e) {
                        Slog.w("LegacyAppOpsServiceInterfaceImpl", "Failed to write state, restoring backup.", e);
                        this.mFile.failWrite(startWrite);
                    }
                } catch (IOException e2) {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Failed to write state: " + e2);
                }
            } finally {
            }
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void readState() {
        int next;
        synchronized (this.mFile) {
            synchronized (this.mLock) {
                try {
                    try {
                        try {
                            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(this.mFile.openRead());
                            while (true) {
                                next = resolvePullParser.next();
                                if (next == 2 || next == 1) {
                                    break;
                                }
                            }
                            if (next != 2) {
                                throw new IllegalStateException("no start tag found");
                            }
                            this.mVersionAtBoot = resolvePullParser.getAttributeInt((String) null, "v", -1);
                            int depth = resolvePullParser.getDepth();
                            while (true) {
                                int next2 = resolvePullParser.next();
                                if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                                    break;
                                } else if (next2 != 3 && next2 != 4) {
                                    String name = resolvePullParser.getName();
                                    if (name.equals("pkg")) {
                                        readPackage(resolvePullParser);
                                    } else if (name.equals("uid")) {
                                        readUidOps(resolvePullParser);
                                    } else if (name.equals("user")) {
                                        readUser(resolvePullParser);
                                    } else {
                                        Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <app-ops>: " + resolvePullParser.getName());
                                        com.android.internal.util.XmlUtils.skipCurrentTag(resolvePullParser);
                                    }
                                }
                            }
                        } catch (XmlPullParserException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e2) {
                        throw new RuntimeException(e2);
                    }
                } catch (FileNotFoundException unused) {
                    Slog.i("LegacyAppOpsServiceInterfaceImpl", "No existing app ops " + this.mFile.getBaseFile() + "; starting empty");
                    this.mVersionAtBoot = -2;
                }
            }
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void shutdown() {
        boolean z;
        synchronized (this) {
            z = false;
            if (this.mWriteScheduled) {
                this.mWriteScheduled = false;
                this.mFastWriteScheduled = false;
                this.mHandler.removeCallbacks(this.mWriteRunner);
                z = true;
            }
        }
        if (z) {
            writeState();
        }
    }

    @GuardedBy({"mLock"})
    public final void readUidOps(TypedXmlPullParser typedXmlPullParser) throws NumberFormatException, XmlPullParserException, IOException {
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "n");
        SparseIntArray sparseIntArray = this.mUidModes.get(attributeInt);
        if (sparseIntArray == null) {
            sparseIntArray = new SparseIntArray();
            this.mUidModes.put(attributeInt, sparseIntArray);
        }
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("op")) {
                    int attributeInt2 = typedXmlPullParser.getAttributeInt((String) null, "n");
                    int attributeInt3 = typedXmlPullParser.getAttributeInt((String) null, "m");
                    if (attributeInt3 != AppOpsManager.opToDefaultMode(attributeInt2)) {
                        sparseIntArray.put(attributeInt2, attributeInt3);
                    }
                } else {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <uid>: " + typedXmlPullParser.getName());
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void readPackage(TypedXmlPullParser typedXmlPullParser) throws NumberFormatException, XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "n");
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("uid")) {
                    readUid(typedXmlPullParser, attributeValue);
                } else {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <pkg>: " + typedXmlPullParser.getName());
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void readUid(TypedXmlPullParser typedXmlPullParser, String str) throws NumberFormatException, XmlPullParserException, IOException {
        int userId = UserHandle.getUserId(typedXmlPullParser.getAttributeInt((String) null, "n"));
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("op")) {
                    readOp(typedXmlPullParser, userId, str);
                } else {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <pkg>: " + typedXmlPullParser.getName());
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void readUser(TypedXmlPullParser typedXmlPullParser) throws NumberFormatException, XmlPullParserException, IOException {
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "n");
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("pkg")) {
                    readPackage(typedXmlPullParser, attributeInt);
                } else {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <user>: " + typedXmlPullParser.getName());
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void readPackage(TypedXmlPullParser typedXmlPullParser, int i) throws NumberFormatException, XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "n");
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("op")) {
                    readOp(typedXmlPullParser, i, attributeValue);
                } else {
                    Slog.w("LegacyAppOpsServiceInterfaceImpl", "Unknown element under <pkg>: " + typedXmlPullParser.getName());
                    com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void readOp(TypedXmlPullParser typedXmlPullParser, int i, String str) throws NumberFormatException, XmlPullParserException {
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "n");
        int opToDefaultMode = AppOpsManager.opToDefaultMode(attributeInt);
        int attributeInt2 = typedXmlPullParser.getAttributeInt((String) null, "m", opToDefaultMode);
        if (attributeInt2 != opToDefaultMode) {
            ArrayMap<String, SparseIntArray> arrayMap = this.mUserPackageModes.get(i);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mUserPackageModes.put(i, arrayMap);
            }
            SparseIntArray sparseIntArray = arrayMap.get(str);
            if (sparseIntArray == null) {
                sparseIntArray = new SparseIntArray();
                arrayMap.put(str, sparseIntArray);
            }
            sparseIntArray.put(attributeInt, attributeInt2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0029, code lost:
        if (r4 != 1) goto L10;
     */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void upgradeLocked(int i) {
        if (i == -2 || i >= 3) {
            return;
        }
        Slog.d("LegacyAppOpsServiceInterfaceImpl", "Upgrading app-ops xml from version " + i + " to 3");
        if (i == -1) {
            upgradeRunAnyInBackgroundLocked();
        }
        upgradeScheduleExactAlarmLocked();
        scheduleFastWriteLocked();
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void upgradeRunAnyInBackgroundLocked() {
        int size = this.mUidModes.size();
        for (int i = 0; i < size; i++) {
            SparseIntArray valueAt = this.mUidModes.valueAt(i);
            int indexOfKey = valueAt.indexOfKey(63);
            if (indexOfKey >= 0) {
                valueAt.put(70, valueAt.valueAt(indexOfKey));
            }
        }
        int size2 = this.mUserPackageModes.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ArrayMap<String, SparseIntArray> valueAt2 = this.mUserPackageModes.valueAt(i2);
            int size3 = valueAt2.size();
            for (int i3 = 0; i3 < size3; i3++) {
                SparseIntArray valueAt3 = valueAt2.valueAt(i3);
                int indexOfKey2 = valueAt3.indexOfKey(63);
                if (indexOfKey2 >= 0) {
                    valueAt3.put(70, valueAt3.valueAt(indexOfKey2));
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void upgradeScheduleExactAlarmLocked() {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        String[] appOpPermissionPackages = ((PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class)).getAppOpPermissionPackages(AppOpsManager.opToPermission(107));
        int[] userIds = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds();
        for (String str : appOpPermissionPackages) {
            for (int i : userIds) {
                int packageUid = packageManagerInternal.getPackageUid(str, 0L, i);
                if (getUidMode(packageUid, 107) == AppOpsManager.opToDefaultMode(107)) {
                    setUidMode(packageUid, 107, 0);
                }
            }
        }
    }

    @VisibleForTesting
    public List<Integer> getUidsWithNonDefaultModes() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mUidModes.size(); i++) {
                if (this.mUidModes.valueAt(i).size() > 0) {
                    arrayList.add(Integer.valueOf(this.mUidModes.keyAt(i)));
                }
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public List<UserPackage> getPackagesWithNonDefaultModes() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mUserPackageModes.size(); i++) {
                ArrayMap<String, SparseIntArray> valueAt = this.mUserPackageModes.valueAt(i);
                for (int i2 = 0; i2 < valueAt.size(); i2++) {
                    if (valueAt.valueAt(i2).size() > 0) {
                        arrayList.add(UserPackage.of(this.mUserPackageModes.keyAt(i), valueAt.keyAt(i2)));
                    }
                }
            }
        }
        return arrayList;
    }
}
