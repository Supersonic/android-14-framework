package com.android.server.p006am;

import android.content.pm.VersionedPackage;
import android.util.ArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.procstats.ProcessStats;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
/* renamed from: com.android.server.am.PackageList */
/* loaded from: classes.dex */
public final class PackageList {
    public final ArrayMap<String, ProcessStats.ProcessStateHolder> mPkgList = new ArrayMap<>();
    public final ProcessRecord mProcess;

    public PackageList(ProcessRecord processRecord) {
        this.mProcess = processRecord;
    }

    public ProcessStats.ProcessStateHolder put(String str, ProcessStats.ProcessStateHolder processStateHolder) {
        ProcessStats.ProcessStateHolder put;
        synchronized (this) {
            this.mProcess.getWindowProcessController().addPackage(str);
            put = this.mPkgList.put(str, processStateHolder);
        }
        return put;
    }

    public void clear() {
        synchronized (this) {
            this.mPkgList.clear();
            this.mProcess.getWindowProcessController().clearPackageList();
        }
    }

    public int size() {
        int size;
        synchronized (this) {
            size = this.mPkgList.size();
        }
        return size;
    }

    public boolean containsKey(Object obj) {
        boolean containsKey;
        synchronized (this) {
            containsKey = this.mPkgList.containsKey(obj);
        }
        return containsKey;
    }

    public ProcessStats.ProcessStateHolder get(String str) {
        ProcessStats.ProcessStateHolder processStateHolder;
        synchronized (this) {
            processStateHolder = this.mPkgList.get(str);
        }
        return processStateHolder;
    }

    public void forEachPackage(Consumer<String> consumer) {
        synchronized (this) {
            int size = this.mPkgList.size();
            for (int i = 0; i < size; i++) {
                consumer.accept(this.mPkgList.keyAt(i));
            }
        }
    }

    public void forEachPackage(BiConsumer<String, ProcessStats.ProcessStateHolder> biConsumer) {
        synchronized (this) {
            int size = this.mPkgList.size();
            for (int i = 0; i < size; i++) {
                biConsumer.accept(this.mPkgList.keyAt(i), this.mPkgList.valueAt(i));
            }
        }
    }

    public <R> R searchEachPackage(Function<String, R> function) {
        synchronized (this) {
            int size = this.mPkgList.size();
            for (int i = 0; i < size; i++) {
                R apply = function.apply(this.mPkgList.keyAt(i));
                if (apply != null) {
                    return apply;
                }
            }
            return null;
        }
    }

    public void forEachPackageProcessStats(Consumer<ProcessStats.ProcessStateHolder> consumer) {
        synchronized (this) {
            int size = this.mPkgList.size();
            for (int i = 0; i < size; i++) {
                consumer.accept(this.mPkgList.valueAt(i));
            }
        }
    }

    @GuardedBy({"this"})
    public ArrayMap<String, ProcessStats.ProcessStateHolder> getPackageListLocked() {
        return this.mPkgList;
    }

    public String[] getPackageList() {
        synchronized (this) {
            int size = this.mPkgList.size();
            if (size == 0) {
                return null;
            }
            String[] strArr = new String[size];
            for (int i = 0; i < size; i++) {
                strArr[i] = this.mPkgList.keyAt(i);
            }
            return strArr;
        }
    }

    public List<VersionedPackage> getPackageListWithVersionCode() {
        synchronized (this) {
            int size = this.mPkgList.size();
            if (size == 0) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < size; i++) {
                arrayList.add(new VersionedPackage(this.mPkgList.keyAt(i), this.mPkgList.valueAt(i).appVersion));
            }
            return arrayList;
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this) {
            printWriter.print(str);
            printWriter.print("packageList={");
            int size = this.mPkgList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    printWriter.print(", ");
                }
                printWriter.print(this.mPkgList.keyAt(i));
            }
            printWriter.println("}");
        }
    }
}
