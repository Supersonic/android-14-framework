package com.android.server.p014wm;

import android.content.ComponentName;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageList;
import com.android.server.p014wm.LaunchParamsController;
import com.android.server.p014wm.LaunchParamsPersister;
import com.android.server.p014wm.PersisterQueue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.LaunchParamsPersister */
/* loaded from: classes2.dex */
public class LaunchParamsPersister {
    public final SparseArray<ArrayMap<ComponentName, PersistableLaunchParams>> mLaunchParamsMap;
    public PackageList mPackageList;
    public final PersisterQueue mPersisterQueue;
    public final ActivityTaskSupervisor mSupervisor;
    public final IntFunction<File> mUserFolderGetter;
    public final ArrayMap<String, ArraySet<ComponentName>> mWindowLayoutAffinityMap;

    public LaunchParamsPersister(PersisterQueue persisterQueue, ActivityTaskSupervisor activityTaskSupervisor) {
        this(persisterQueue, activityTaskSupervisor, new IntFunction() { // from class: com.android.server.wm.LaunchParamsPersister$$ExternalSyntheticLambda3
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return Environment.getDataSystemCeDirectory(i);
            }
        });
    }

    @VisibleForTesting
    public LaunchParamsPersister(PersisterQueue persisterQueue, ActivityTaskSupervisor activityTaskSupervisor, IntFunction<File> intFunction) {
        this.mLaunchParamsMap = new SparseArray<>();
        this.mWindowLayoutAffinityMap = new ArrayMap<>();
        this.mPersisterQueue = persisterQueue;
        this.mSupervisor = activityTaskSupervisor;
        this.mUserFolderGetter = intFunction;
    }

    public void onSystemReady() {
        this.mPackageList = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageList(new PackageListObserver());
    }

    public void onUnlockUser(int i) {
        loadLaunchParams(i);
    }

    public void onCleanupUser(int i) {
        this.mLaunchParamsMap.remove(i);
    }

    public final void loadLaunchParams(int i) {
        File file;
        FileInputStream fileInputStream;
        ArrayList arrayList = new ArrayList();
        File launchParamFolder = getLaunchParamFolder(i);
        if (!launchParamFolder.isDirectory()) {
            Slog.i("LaunchParamsPersister", "Didn't find launch param folder for user " + i);
            return;
        }
        ArraySet arraySet = new ArraySet(this.mPackageList.getPackageNames());
        File[] listFiles = launchParamFolder.listFiles();
        ArrayMap<ComponentName, PersistableLaunchParams> arrayMap = new ArrayMap<>(listFiles.length);
        this.mLaunchParamsMap.put(i, arrayMap);
        int length = listFiles.length;
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            File file2 = listFiles[i3];
            if (!file2.isFile()) {
                Slog.w("LaunchParamsPersister", file2.getAbsolutePath() + " is not a file.");
            } else if (!file2.getName().endsWith(".xml")) {
                Slog.w("LaunchParamsPersister", "Unexpected params file name: " + file2.getName());
                arrayList.add(file2);
            } else {
                String name = file2.getName();
                int indexOf = name.indexOf(95);
                if (indexOf != -1) {
                    if (name.indexOf(95, indexOf + 1) != -1) {
                        arrayList.add(file2);
                    } else {
                        name = name.replace('_', '-');
                        File file3 = new File(launchParamFolder, name);
                        if (file2.renameTo(file3)) {
                            file2 = file3;
                        } else {
                            arrayList.add(file2);
                        }
                    }
                }
                ComponentName unflattenFromString = ComponentName.unflattenFromString(name.substring(i2, name.length() - 4).replace('-', '/'));
                if (unflattenFromString == null) {
                    Slog.w("LaunchParamsPersister", "Unexpected file name: " + name);
                    arrayList.add(file2);
                } else if (!arraySet.contains(unflattenFromString.getPackageName())) {
                    arrayList.add(file2);
                } else {
                    try {
                        fileInputStream = new FileInputStream(file2);
                    } catch (Exception e) {
                        e = e;
                        file = launchParamFolder;
                    }
                    try {
                        PersistableLaunchParams persistableLaunchParams = new PersistableLaunchParams();
                        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                        while (true) {
                            int next = resolvePullParser.next();
                            if (next == 1 || next == 3) {
                                break;
                            } else if (next == 2) {
                                String name2 = resolvePullParser.getName();
                                if (!"launch_params".equals(name2)) {
                                    StringBuilder sb = new StringBuilder();
                                    file = launchParamFolder;
                                    try {
                                        sb.append("Unexpected tag name: ");
                                        sb.append(name2);
                                        Slog.w("LaunchParamsPersister", sb.toString());
                                    } catch (Throwable th) {
                                        th = th;
                                        Throwable th2 = th;
                                        fileInputStream.close();
                                        throw th2;
                                        break;
                                    }
                                } else {
                                    file = launchParamFolder;
                                    persistableLaunchParams.restore(file2, resolvePullParser);
                                }
                                launchParamFolder = file;
                            }
                        }
                        file = launchParamFolder;
                        arrayMap.put(unflattenFromString, persistableLaunchParams);
                        addComponentNameToLaunchParamAffinityMapIfNotNull(unflattenFromString, persistableLaunchParams.mWindowLayoutAffinity);
                        try {
                            fileInputStream.close();
                        } catch (Exception e2) {
                            e = e2;
                            Slog.w("LaunchParamsPersister", "Failed to restore launch params for " + unflattenFromString, e);
                            arrayList.add(file2);
                            i3++;
                            launchParamFolder = file;
                            i2 = 0;
                        }
                        i3++;
                        launchParamFolder = file;
                        i2 = 0;
                    } catch (Throwable th3) {
                        th = th3;
                        file = launchParamFolder;
                    }
                }
            }
            file = launchParamFolder;
            i3++;
            launchParamFolder = file;
            i2 = 0;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.mPersisterQueue.addItem(new CleanUpComponentQueueItem(arrayList), true);
    }

    public void saveTask(Task task, DisplayContent displayContent) {
        ComponentName componentName = task.realActivity;
        if (componentName == null) {
            return;
        }
        int i = task.mUserId;
        ArrayMap<ComponentName, PersistableLaunchParams> arrayMap = this.mLaunchParamsMap.get(i);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            this.mLaunchParamsMap.put(i, arrayMap);
        }
        PersistableLaunchParams computeIfAbsent = arrayMap.computeIfAbsent(componentName, new Function() { // from class: com.android.server.wm.LaunchParamsPersister$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                LaunchParamsPersister.PersistableLaunchParams lambda$saveTask$0;
                lambda$saveTask$0 = LaunchParamsPersister.this.lambda$saveTask$0((ComponentName) obj);
                return lambda$saveTask$0;
            }
        });
        boolean saveTaskToLaunchParam = saveTaskToLaunchParam(task, displayContent, computeIfAbsent);
        addComponentNameToLaunchParamAffinityMapIfNotNull(componentName, computeIfAbsent.mWindowLayoutAffinity);
        if (saveTaskToLaunchParam) {
            this.mPersisterQueue.updateLastOrAddItem(new LaunchParamsWriteQueueItem(i, componentName, computeIfAbsent), false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ PersistableLaunchParams lambda$saveTask$0(ComponentName componentName) {
        return new PersistableLaunchParams();
    }

    public final boolean saveTaskToLaunchParam(Task task, DisplayContent displayContent, PersistableLaunchParams persistableLaunchParams) {
        Rect rect;
        boolean z;
        DisplayInfo displayInfo = new DisplayInfo();
        displayContent.mDisplay.getDisplayInfo(displayInfo);
        boolean z2 = !Objects.equals(persistableLaunchParams.mDisplayUniqueId, displayInfo.uniqueId);
        persistableLaunchParams.mDisplayUniqueId = displayInfo.uniqueId;
        boolean z3 = (persistableLaunchParams.mWindowingMode != task.getWindowingMode()) | z2;
        persistableLaunchParams.mWindowingMode = task.getWindowingMode();
        if (task.mLastNonFullscreenBounds != null) {
            z = z3 | (!Objects.equals(persistableLaunchParams.mBounds, rect));
            persistableLaunchParams.mBounds.set(task.mLastNonFullscreenBounds);
        } else {
            z = z3 | (!persistableLaunchParams.mBounds.isEmpty());
            persistableLaunchParams.mBounds.setEmpty();
        }
        String str = task.mWindowLayoutAffinity;
        boolean equals = z | Objects.equals(str, persistableLaunchParams.mWindowLayoutAffinity);
        persistableLaunchParams.mWindowLayoutAffinity = str;
        if (equals) {
            persistableLaunchParams.mTimestamp = System.currentTimeMillis();
        }
        return equals;
    }

    public static /* synthetic */ ArraySet lambda$addComponentNameToLaunchParamAffinityMapIfNotNull$1(String str) {
        return new ArraySet();
    }

    public final void addComponentNameToLaunchParamAffinityMapIfNotNull(ComponentName componentName, String str) {
        if (str == null) {
            return;
        }
        this.mWindowLayoutAffinityMap.computeIfAbsent(str, new Function() { // from class: com.android.server.wm.LaunchParamsPersister$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ArraySet lambda$addComponentNameToLaunchParamAffinityMapIfNotNull$1;
                lambda$addComponentNameToLaunchParamAffinityMapIfNotNull$1 = LaunchParamsPersister.lambda$addComponentNameToLaunchParamAffinityMapIfNotNull$1((String) obj);
                return lambda$addComponentNameToLaunchParamAffinityMapIfNotNull$1;
            }
        }).add(componentName);
    }

    public void getLaunchParams(Task task, ActivityRecord activityRecord, LaunchParamsController.LaunchParams launchParams) {
        String str;
        ComponentName componentName = task != null ? task.realActivity : activityRecord.mActivityComponent;
        int i = task != null ? task.mUserId : activityRecord.mUserId;
        if (task != null) {
            str = task.mWindowLayoutAffinity;
        } else {
            ActivityInfo.WindowLayout windowLayout = activityRecord.info.windowLayout;
            str = windowLayout == null ? null : windowLayout.windowLayoutAffinity;
        }
        launchParams.reset();
        ArrayMap<ComponentName, PersistableLaunchParams> arrayMap = this.mLaunchParamsMap.get(i);
        if (arrayMap == null) {
            return;
        }
        PersistableLaunchParams persistableLaunchParams = arrayMap.get(componentName);
        if (str != null && this.mWindowLayoutAffinityMap.get(str) != null) {
            ArraySet<ComponentName> arraySet = this.mWindowLayoutAffinityMap.get(str);
            for (int i2 = 0; i2 < arraySet.size(); i2++) {
                PersistableLaunchParams persistableLaunchParams2 = arrayMap.get(arraySet.valueAt(i2));
                if (persistableLaunchParams2 != null && (persistableLaunchParams == null || persistableLaunchParams2.mTimestamp > persistableLaunchParams.mTimestamp)) {
                    persistableLaunchParams = persistableLaunchParams2;
                }
            }
        }
        if (persistableLaunchParams == null) {
            return;
        }
        DisplayContent displayContent = this.mSupervisor.mRootWindowContainer.getDisplayContent(persistableLaunchParams.mDisplayUniqueId);
        if (displayContent != null) {
            launchParams.mPreferredTaskDisplayArea = displayContent.getDefaultTaskDisplayArea();
        }
        launchParams.mWindowingMode = persistableLaunchParams.mWindowingMode;
        launchParams.mBounds.set(persistableLaunchParams.mBounds);
    }

    public void removeRecordForPackage(final String str) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mLaunchParamsMap.size(); i++) {
            File launchParamFolder = getLaunchParamFolder(this.mLaunchParamsMap.keyAt(i));
            ArrayMap<ComponentName, PersistableLaunchParams> valueAt = this.mLaunchParamsMap.valueAt(i);
            for (int size = valueAt.size() - 1; size >= 0; size--) {
                ComponentName keyAt = valueAt.keyAt(size);
                if (keyAt.getPackageName().equals(str)) {
                    valueAt.removeAt(size);
                    arrayList.add(getParamFile(launchParamFolder, keyAt));
                }
            }
        }
        synchronized (this.mPersisterQueue) {
            this.mPersisterQueue.removeItems(new Predicate() { // from class: com.android.server.wm.LaunchParamsPersister$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removeRecordForPackage$2;
                    lambda$removeRecordForPackage$2 = LaunchParamsPersister.lambda$removeRecordForPackage$2(str, (LaunchParamsPersister.LaunchParamsWriteQueueItem) obj);
                    return lambda$removeRecordForPackage$2;
                }
            }, LaunchParamsWriteQueueItem.class);
            this.mPersisterQueue.addItem(new CleanUpComponentQueueItem(arrayList), true);
        }
    }

    public static /* synthetic */ boolean lambda$removeRecordForPackage$2(String str, LaunchParamsWriteQueueItem launchParamsWriteQueueItem) {
        return launchParamsWriteQueueItem.mComponentName.getPackageName().equals(str);
    }

    public final File getParamFile(File file, ComponentName componentName) {
        String replace = componentName.flattenToShortString().replace('/', '-');
        return new File(file, replace + ".xml");
    }

    public final File getLaunchParamFolder(int i) {
        return new File(this.mUserFolderGetter.apply(i), "launch_params");
    }

    /* renamed from: com.android.server.wm.LaunchParamsPersister$PackageListObserver */
    /* loaded from: classes2.dex */
    public class PackageListObserver implements PackageManagerInternal.PackageListObserver {
        @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
        public void onPackageAdded(String str, int i) {
        }

        public PackageListObserver() {
        }

        @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
        public void onPackageRemoved(String str, int i) {
            synchronized (LaunchParamsPersister.this.mSupervisor.mService.getGlobalLock()) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    LaunchParamsPersister.this.removeRecordForPackage(str);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* renamed from: com.android.server.wm.LaunchParamsPersister$LaunchParamsWriteQueueItem */
    /* loaded from: classes2.dex */
    public class LaunchParamsWriteQueueItem implements PersisterQueue.WriteQueueItem<LaunchParamsWriteQueueItem> {
        public final ComponentName mComponentName;
        public PersistableLaunchParams mLaunchParams;
        public final int mUserId;

        public LaunchParamsWriteQueueItem(int i, ComponentName componentName, PersistableLaunchParams persistableLaunchParams) {
            this.mUserId = i;
            this.mComponentName = componentName;
            this.mLaunchParams = persistableLaunchParams;
        }

        public final byte[] saveParamsToXml() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(byteArrayOutputStream);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "launch_params");
                this.mLaunchParams.saveToXml(resolveSerializer);
                resolveSerializer.endTag((String) null, "launch_params");
                resolveSerializer.endDocument();
                resolveSerializer.flush();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException unused) {
                return null;
            }
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void process() {
            FileOutputStream fileOutputStream;
            byte[] saveParamsToXml = saveParamsToXml();
            File launchParamFolder = LaunchParamsPersister.this.getLaunchParamFolder(this.mUserId);
            if (!launchParamFolder.isDirectory() && !launchParamFolder.mkdirs()) {
                Slog.w("LaunchParamsPersister", "Failed to create folder for " + this.mUserId);
                return;
            }
            AtomicFile atomicFile = new AtomicFile(LaunchParamsPersister.this.getParamFile(launchParamFolder, this.mComponentName));
            try {
                fileOutputStream = atomicFile.startWrite();
            } catch (Exception e) {
                e = e;
                fileOutputStream = null;
            }
            try {
                fileOutputStream.write(saveParamsToXml);
                atomicFile.finishWrite(fileOutputStream);
            } catch (Exception e2) {
                e = e2;
                Slog.e("LaunchParamsPersister", "Failed to write param file for " + this.mComponentName, e);
                if (fileOutputStream != null) {
                    atomicFile.failWrite(fileOutputStream);
                }
            }
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public boolean matches(LaunchParamsWriteQueueItem launchParamsWriteQueueItem) {
            return this.mUserId == launchParamsWriteQueueItem.mUserId && this.mComponentName.equals(launchParamsWriteQueueItem.mComponentName);
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void updateFrom(LaunchParamsWriteQueueItem launchParamsWriteQueueItem) {
            this.mLaunchParams = launchParamsWriteQueueItem.mLaunchParams;
        }
    }

    /* renamed from: com.android.server.wm.LaunchParamsPersister$CleanUpComponentQueueItem */
    /* loaded from: classes2.dex */
    public class CleanUpComponentQueueItem implements PersisterQueue.WriteQueueItem {
        public final List<File> mComponentFiles;

        public CleanUpComponentQueueItem(List<File> list) {
            this.mComponentFiles = list;
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void process() {
            for (File file : this.mComponentFiles) {
                if (!file.delete()) {
                    Slog.w("LaunchParamsPersister", "Failed to delete " + file.getAbsolutePath());
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.LaunchParamsPersister$PersistableLaunchParams */
    /* loaded from: classes2.dex */
    public class PersistableLaunchParams {
        public final Rect mBounds;
        public String mDisplayUniqueId;
        public long mTimestamp;
        public String mWindowLayoutAffinity;
        public int mWindowingMode;

        public PersistableLaunchParams() {
            this.mBounds = new Rect();
        }

        public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
            typedXmlSerializer.attribute((String) null, "display_unique_id", this.mDisplayUniqueId);
            typedXmlSerializer.attributeInt((String) null, "windowing_mode", this.mWindowingMode);
            typedXmlSerializer.attribute((String) null, "bounds", this.mBounds.flattenToString());
            String str = this.mWindowLayoutAffinity;
            if (str != null) {
                typedXmlSerializer.attribute((String) null, "window_layout_affinity", str);
            }
        }

        public void restore(File file, TypedXmlPullParser typedXmlPullParser) {
            for (int i = 0; i < typedXmlPullParser.getAttributeCount(); i++) {
                String attributeValue = typedXmlPullParser.getAttributeValue(i);
                String attributeName = typedXmlPullParser.getAttributeName(i);
                attributeName.hashCode();
                char c = 65535;
                switch (attributeName.hashCode()) {
                    case -1499361012:
                        if (attributeName.equals("display_unique_id")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1383205195:
                        if (attributeName.equals("bounds")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 748872656:
                        if (attributeName.equals("windowing_mode")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1999609934:
                        if (attributeName.equals("window_layout_affinity")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        this.mDisplayUniqueId = attributeValue;
                        break;
                    case 1:
                        Rect unflattenFromString = Rect.unflattenFromString(attributeValue);
                        if (unflattenFromString != null) {
                            this.mBounds.set(unflattenFromString);
                            break;
                        } else {
                            break;
                        }
                    case 2:
                        this.mWindowingMode = Integer.parseInt(attributeValue);
                        break;
                    case 3:
                        this.mWindowLayoutAffinity = attributeValue;
                        break;
                }
            }
            this.mTimestamp = file.lastModified();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("PersistableLaunchParams{");
            sb.append(" windowingMode=" + this.mWindowingMode);
            sb.append(" displayUniqueId=" + this.mDisplayUniqueId);
            sb.append(" bounds=" + this.mBounds);
            if (this.mWindowLayoutAffinity != null) {
                sb.append(" launchParamsAffinity=" + this.mWindowLayoutAffinity);
            }
            sb.append(" timestamp=" + this.mTimestamp);
            sb.append(" }");
            return sb.toString();
        }
    }
}
