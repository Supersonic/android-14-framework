package com.android.internal.util.dump;

import android.util.ArrayMap;
import android.util.Dumpable;
import android.util.DumpableContainer;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.accessibility.common.ShortcutConstants;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public final class DumpableContainerImpl implements DumpableContainer {
    private static final boolean DEBUG = false;
    private static final String TAG = DumpableContainerImpl.class.getSimpleName();
    private final ArrayMap<String, Dumpable> mDumpables = new ArrayMap<>();

    @Override // android.util.DumpableContainer
    public boolean addDumpable(final Dumpable dumpable) {
        Objects.requireNonNull(dumpable, "dumpable");
        String name = dumpable.getDumpableName();
        Objects.requireNonNull(name, new Supplier() { // from class: com.android.internal.util.dump.DumpableContainerImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                return DumpableContainerImpl.lambda$addDumpable$0(Dumpable.this);
            }
        });
        if (this.mDumpables.containsKey(name)) {
            return false;
        }
        this.mDumpables.put(name, dumpable);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$addDumpable$0(Dumpable dumpable) {
        return "name of" + dumpable;
    }

    @Override // android.util.DumpableContainer
    public boolean removeDumpable(Dumpable dumpable) {
        Dumpable candidate;
        Objects.requireNonNull(dumpable, "dumpable");
        String name = dumpable.getDumpableName();
        if (name == null || (candidate = this.mDumpables.get(name)) == null) {
            return false;
        }
        if (candidate != dumpable) {
            Log.m104w(TAG, "removeDumpable(): passed dumpable (" + dumpable + ") named " + name + ", but internal dumpable with that name is " + candidate);
            return false;
        }
        this.mDumpables.remove(name);
        return true;
    }

    private int dumpNumberDumpables(IndentingPrintWriter writer) {
        int size = this.mDumpables.size();
        if (size == 0) {
            writer.print("No dumpables");
        } else {
            writer.print(size);
            writer.print(" dumpables");
        }
        return size;
    }

    public void listDumpables(String prefix, PrintWriter writer) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(writer, prefix, prefix);
        int size = dumpNumberDumpables(ipw);
        if (size == 0) {
            ipw.println();
            return;
        }
        ipw.print(": ");
        for (int i = 0; i < size; i++) {
            ipw.print(this.mDumpables.keyAt(i));
            if (i < size - 1) {
                ipw.print(' ');
            }
        }
        ipw.println();
    }

    public void dumpAllDumpables(String prefix, PrintWriter writer, String[] args) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(writer, prefix, prefix);
        int size = dumpNumberDumpables(ipw);
        if (size == 0) {
            ipw.println();
            return;
        }
        ipw.println(":");
        for (int i = 0; i < size; i++) {
            String dumpableName = this.mDumpables.keyAt(i);
            ipw.print('#');
            ipw.print(i);
            ipw.print(": ");
            ipw.println(dumpableName);
            Dumpable dumpable = this.mDumpables.valueAt(i);
            indentAndDump(ipw, dumpable, args);
        }
    }

    private void indentAndDump(IndentingPrintWriter writer, Dumpable dumpable, String[] args) {
        writer.increaseIndent();
        try {
            dumpable.dump(writer, args);
        } finally {
            writer.decreaseIndent();
        }
    }

    public void dumpOneDumpable(String prefix, PrintWriter writer, String dumpableName, String[] args) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(writer, prefix, prefix);
        Dumpable dumpable = this.mDumpables.get(dumpableName);
        if (dumpable == null) {
            ipw.print("No ");
            ipw.println(dumpableName);
            return;
        }
        ipw.print(dumpableName);
        ipw.println(ShortcutConstants.SERVICES_SEPARATOR);
        indentAndDump(ipw, dumpable, args);
    }
}
