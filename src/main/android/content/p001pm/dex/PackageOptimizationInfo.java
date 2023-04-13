package android.content.p001pm.dex;
/* renamed from: android.content.pm.dex.PackageOptimizationInfo */
/* loaded from: classes.dex */
public class PackageOptimizationInfo {
    private final int mCompilationFilter;
    private final int mCompilationReason;

    public PackageOptimizationInfo(int compilerFilter, int compilationReason) {
        this.mCompilationReason = compilationReason;
        this.mCompilationFilter = compilerFilter;
    }

    public int getCompilationReason() {
        return this.mCompilationReason;
    }

    public int getCompilationFilter() {
        return this.mCompilationFilter;
    }

    public static PackageOptimizationInfo createWithNoInfo() {
        return new PackageOptimizationInfo(-1, -1);
    }
}
