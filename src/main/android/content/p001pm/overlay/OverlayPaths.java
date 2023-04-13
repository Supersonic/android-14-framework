package android.content.p001pm.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* renamed from: android.content.pm.overlay.OverlayPaths */
/* loaded from: classes.dex */
public class OverlayPaths {
    private final List<String> mOverlayPaths;
    private final List<String> mResourceDirs;

    /* renamed from: android.content.pm.overlay.OverlayPaths$Builder */
    /* loaded from: classes.dex */
    public static class Builder {
        final OverlayPaths mPaths = new OverlayPaths();

        public Builder addNonApkPath(String idmapPath) {
            this.mPaths.mOverlayPaths.add(idmapPath);
            return this;
        }

        public Builder addApkPath(String overlayPath) {
            addUniquePath(this.mPaths.mResourceDirs, overlayPath);
            addUniquePath(this.mPaths.mOverlayPaths, overlayPath);
            return this;
        }

        public Builder addAll(OverlayPaths other) {
            if (other != null) {
                for (String path : other.getResourceDirs()) {
                    addUniquePath(this.mPaths.mResourceDirs, path);
                }
                for (String path2 : other.getOverlayPaths()) {
                    addUniquePath(this.mPaths.mOverlayPaths, path2);
                }
            }
            return this;
        }

        public OverlayPaths build() {
            return this.mPaths;
        }

        private static void addUniquePath(List<String> paths, String path) {
            if (!paths.contains(path)) {
                paths.add(path);
            }
        }
    }

    public boolean isEmpty() {
        return this.mResourceDirs.isEmpty() && this.mOverlayPaths.isEmpty();
    }

    private OverlayPaths() {
        this.mResourceDirs = new ArrayList();
        this.mOverlayPaths = new ArrayList();
    }

    public List<String> getResourceDirs() {
        return this.mResourceDirs;
    }

    public List<String> getOverlayPaths() {
        return this.mOverlayPaths;
    }

    public String toString() {
        return "OverlayPaths { resourceDirs = " + this.mResourceDirs + ", overlayPaths = " + this.mOverlayPaths + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OverlayPaths that = (OverlayPaths) o;
        if (Objects.equals(this.mResourceDirs, that.mResourceDirs) && Objects.equals(this.mOverlayPaths, that.mOverlayPaths)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mResourceDirs);
        return (_hash * 31) + Objects.hashCode(this.mOverlayPaths);
    }

    @Deprecated
    private void __metadata() {
    }
}
