package android.content.p001pm.split;

import android.content.p001pm.parsing.PackageLite;
import android.util.IntArray;
import android.util.SparseArray;
import java.lang.Exception;
import java.util.Arrays;
import java.util.BitSet;
import libcore.util.EmptyArray;
/* renamed from: android.content.pm.split.SplitDependencyLoader */
/* loaded from: classes.dex */
public abstract class SplitDependencyLoader<E extends Exception> {
    private final SparseArray<int[]> mDependencies;

    protected abstract void constructSplit(int i, int[] iArr, int i2) throws Exception;

    protected abstract boolean isSplitCached(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public SplitDependencyLoader(SparseArray<int[]> dependencies) {
        this.mDependencies = dependencies;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loadDependenciesForSplit(int splitIdx) throws Exception {
        if (isSplitCached(splitIdx)) {
            return;
        }
        if (splitIdx == 0) {
            int[] configSplitIndices = collectConfigSplitIndices(0);
            constructSplit(0, configSplitIndices, -1);
            return;
        }
        IntArray linearDependencies = new IntArray();
        linearDependencies.add(splitIdx);
        while (true) {
            int[] deps = this.mDependencies.get(splitIdx);
            if (deps != null && deps.length > 0) {
                splitIdx = deps[0];
            } else {
                splitIdx = -1;
            }
            if (splitIdx < 0 || isSplitCached(splitIdx)) {
                break;
            }
            linearDependencies.add(splitIdx);
        }
        int parentIdx = splitIdx;
        for (int i = linearDependencies.size() - 1; i >= 0; i--) {
            int idx = linearDependencies.get(i);
            int[] configSplitIndices2 = collectConfigSplitIndices(idx);
            constructSplit(idx, configSplitIndices2, parentIdx);
            parentIdx = idx;
        }
    }

    private int[] collectConfigSplitIndices(int splitIdx) {
        int[] deps = this.mDependencies.get(splitIdx);
        if (deps == null || deps.length <= 1) {
            return EmptyArray.INT;
        }
        return Arrays.copyOfRange(deps, 1, deps.length);
    }

    /* renamed from: android.content.pm.split.SplitDependencyLoader$IllegalDependencyException */
    /* loaded from: classes.dex */
    public static class IllegalDependencyException extends Exception {
        private IllegalDependencyException(String message) {
            super(message);
        }
    }

    private static int[] append(int[] src, int elem) {
        if (src == null) {
            return new int[]{elem};
        }
        int[] dst = Arrays.copyOf(src, src.length + 1);
        dst[src.length] = elem;
        return dst;
    }

    public static SparseArray<int[]> createDependenciesFromPackage(PackageLite pkg) throws IllegalDependencyException {
        int depIdx;
        int depIdx2;
        SparseArray<int[]> splitDependencies = new SparseArray<>();
        splitDependencies.put(0, new int[]{-1});
        for (int splitIdx = 0; splitIdx < pkg.getSplitNames().length; splitIdx++) {
            if (pkg.getIsFeatureSplits()[splitIdx]) {
                String splitDependency = pkg.getUsesSplitNames()[splitIdx];
                if (splitDependency != null) {
                    int depIdx3 = Arrays.binarySearch(pkg.getSplitNames(), splitDependency);
                    if (depIdx3 < 0) {
                        throw new IllegalDependencyException("Split '" + pkg.getSplitNames()[splitIdx] + "' requires split '" + splitDependency + "', which is missing.");
                    }
                    depIdx2 = depIdx3 + 1;
                } else {
                    depIdx2 = 0;
                }
                splitDependencies.put(splitIdx + 1, new int[]{depIdx2});
            }
        }
        int size = pkg.getSplitNames().length;
        for (int splitIdx2 = 0; splitIdx2 < size; splitIdx2++) {
            if (!pkg.getIsFeatureSplits()[splitIdx2]) {
                String configForSplit = pkg.getConfigForSplit()[splitIdx2];
                if (configForSplit != null) {
                    int depIdx4 = Arrays.binarySearch(pkg.getSplitNames(), configForSplit);
                    if (depIdx4 < 0) {
                        throw new IllegalDependencyException("Split '" + pkg.getSplitNames()[splitIdx2] + "' targets split '" + configForSplit + "', which is missing.");
                    }
                    if (!pkg.getIsFeatureSplits()[depIdx4]) {
                        throw new IllegalDependencyException("Split '" + pkg.getSplitNames()[splitIdx2] + "' declares itself as configuration split for a non-feature split '" + pkg.getSplitNames()[depIdx4] + "'");
                    }
                    depIdx = depIdx4 + 1;
                } else {
                    depIdx = 0;
                }
                splitDependencies.put(depIdx, append(splitDependencies.get(depIdx), splitIdx2 + 1));
            }
        }
        BitSet bitset = new BitSet();
        int size2 = splitDependencies.size();
        for (int i = 0; i < size2; i++) {
            int splitIdx3 = splitDependencies.keyAt(i);
            bitset.clear();
            while (splitIdx3 != -1) {
                if (bitset.get(splitIdx3)) {
                    throw new IllegalDependencyException("Cycle detected in split dependencies.");
                }
                bitset.set(splitIdx3);
                int[] deps = splitDependencies.get(splitIdx3);
                splitIdx3 = deps != null ? deps[0] : -1;
            }
        }
        return splitDependencies;
    }
}
