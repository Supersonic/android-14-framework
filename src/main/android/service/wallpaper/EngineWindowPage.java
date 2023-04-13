package android.service.wallpaper;

import android.app.WallpaperColors;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public class EngineWindowPage {
    private Bitmap mScreenShot;
    private volatile long mLastUpdateTime = 0;
    private Set<RectF> mCallbackAreas = new ArraySet();
    private Map<RectF, WallpaperColors> mRectFColors = new ArrayMap();

    public void addArea(RectF area) {
        this.mCallbackAreas.add(area);
    }

    public void addWallpaperColors(RectF area, WallpaperColors colors) {
        this.mCallbackAreas.add(area);
        this.mRectFColors.put(area, colors);
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = this.mScreenShot;
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        return this.mScreenShot;
    }

    public void removeArea(RectF area) {
        this.mCallbackAreas.remove(area);
        this.mRectFColors.remove(area);
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.mLastUpdateTime = lastUpdateTime;
    }

    public long getLastUpdateTime() {
        return this.mLastUpdateTime;
    }

    public WallpaperColors getColors(RectF rect) {
        return this.mRectFColors.get(rect);
    }

    public void setBitmap(Bitmap screenShot) {
        this.mScreenShot = screenShot;
    }

    public Set<RectF> getAreas() {
        return this.mCallbackAreas;
    }

    public void removeColor(RectF colorArea) {
        this.mRectFColors.remove(colorArea);
    }
}
