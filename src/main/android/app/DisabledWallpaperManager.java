package android.app;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import java.io.IOException;
import java.io.InputStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class DisabledWallpaperManager extends WallpaperManager {
    private static final boolean DEBUG = false;
    private static final String TAG = DisabledWallpaperManager.class.getSimpleName();
    private static DisabledWallpaperManager sInstance;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DisabledWallpaperManager getInstance() {
        if (sInstance == null) {
            sInstance = new DisabledWallpaperManager();
        }
        return sInstance;
    }

    private DisabledWallpaperManager() {
    }

    @Override // android.app.WallpaperManager
    public boolean isWallpaperSupported() {
        return false;
    }

    @Override // android.app.WallpaperManager
    public boolean isSetWallpaperAllowed() {
        return false;
    }

    private static <T> T unsupported() {
        return null;
    }

    private static boolean unsupportedBoolean() {
        return false;
    }

    private static int unsupportedInt() {
        return -1;
    }

    @Override // android.app.WallpaperManager
    public Drawable getDrawable() {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable getBuiltInDrawable() {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable getBuiltInDrawable(int which) {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment) {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment, int which) {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable peekDrawable() {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable getFastDrawable() {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Drawable peekFastDrawable() {
        return (Drawable) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Bitmap getBitmap() {
        return (Bitmap) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Bitmap getBitmap(boolean hardware) {
        return (Bitmap) unsupported();
    }

    @Override // android.app.WallpaperManager
    public Bitmap getBitmapAsUser(int userId, boolean hardware) {
        return (Bitmap) unsupported();
    }

    @Override // android.app.WallpaperManager
    public ParcelFileDescriptor getWallpaperFile(int which) {
        return (ParcelFileDescriptor) unsupported();
    }

    @Override // android.app.WallpaperManager
    public void addOnColorsChangedListener(WallpaperManager.OnColorsChangedListener listener, Handler handler) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void addOnColorsChangedListener(WallpaperManager.OnColorsChangedListener listener, Handler handler, int userId) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void removeOnColorsChangedListener(WallpaperManager.OnColorsChangedListener callback) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void removeOnColorsChangedListener(WallpaperManager.OnColorsChangedListener callback, int userId) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperColors getWallpaperColors(int which) {
        return (WallpaperColors) unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperColors getWallpaperColors(int which, int userId) {
        return (WallpaperColors) unsupported();
    }

    @Override // android.app.WallpaperManager
    public ParcelFileDescriptor getWallpaperFile(int which, int userId) {
        return (ParcelFileDescriptor) unsupported();
    }

    @Override // android.app.WallpaperManager
    public ParcelFileDescriptor getWallpaperFile(int which, boolean getCropped) {
        return (ParcelFileDescriptor) unsupported();
    }

    @Override // android.app.WallpaperManager
    public void forgetLoadedWallpaper() {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperInfo getWallpaperInfo() {
        return (WallpaperInfo) unsupported();
    }

    @Override // android.app.WallpaperManager
    public ParcelFileDescriptor getWallpaperInfoFile() {
        return (ParcelFileDescriptor) unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperInfo getWallpaperInfoForUser(int userId) {
        return (WallpaperInfo) unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperInfo getWallpaperInfo(int which) {
        return (WallpaperInfo) unsupported();
    }

    @Override // android.app.WallpaperManager
    public WallpaperInfo getWallpaperInfo(int which, int userId) {
        return (WallpaperInfo) unsupported();
    }

    @Override // android.app.WallpaperManager
    public int getWallpaperId(int which) {
        return unsupportedInt();
    }

    @Override // android.app.WallpaperManager
    public int getWallpaperIdForUser(int which, int userId) {
        return unsupportedInt();
    }

    @Override // android.app.WallpaperManager
    public Intent getCropAndSetWallpaperIntent(Uri imageUri) {
        return (Intent) unsupported();
    }

    @Override // android.app.WallpaperManager
    public void setResource(int resid) throws IOException {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public int setResource(int resid, int which) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public void setBitmap(Bitmap bitmap) throws IOException {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup, int which) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup, int which, int userId) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public void setStream(InputStream bitmapData) throws IOException {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public int setStream(InputStream bitmapData, Rect visibleCropHint, boolean allowBackup) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public int setStream(InputStream bitmapData, Rect visibleCropHint, boolean allowBackup, int which) throws IOException {
        unsupported();
        return 0;
    }

    @Override // android.app.WallpaperManager
    public boolean hasResourceWallpaper(int resid) {
        return unsupportedBoolean();
    }

    @Override // android.app.WallpaperManager
    public int getDesiredMinimumWidth() {
        return unsupportedInt();
    }

    @Override // android.app.WallpaperManager
    public int getDesiredMinimumHeight() {
        return unsupportedInt();
    }

    @Override // android.app.WallpaperManager
    public void suggestDesiredDimensions(int minimumWidth, int minimumHeight) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void setDisplayPadding(Rect padding) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void setDisplayOffset(IBinder windowToken, int x, int y) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void clearWallpaper() {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void clearWallpaper(int which, int userId) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public boolean setWallpaperComponent(ComponentName name) {
        return unsupportedBoolean();
    }

    @Override // android.app.WallpaperManager
    public boolean setWallpaperComponent(ComponentName name, int userId) {
        return unsupportedBoolean();
    }

    @Override // android.app.WallpaperManager
    public void setWallpaperOffsets(IBinder windowToken, float xOffset, float yOffset) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void setWallpaperOffsetSteps(float xStep, float yStep) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void sendWallpaperCommand(IBinder windowToken, String action, int x, int y, int z, Bundle extras) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void clearWallpaperOffsets(IBinder windowToken) {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void clear() throws IOException {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public void clear(int which) throws IOException {
        unsupported();
    }

    @Override // android.app.WallpaperManager
    public boolean isWallpaperBackupEligible(int which) {
        return unsupportedBoolean();
    }

    @Override // android.app.WallpaperManager
    public boolean wallpaperSupportsWcg(int which) {
        return unsupportedBoolean();
    }
}
