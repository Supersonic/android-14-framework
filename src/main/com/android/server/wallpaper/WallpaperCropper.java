package com.android.server.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.os.FileUtils;
import android.os.SELinux;
import android.util.Slog;
import android.view.DisplayInfo;
import com.android.server.utils.TimingsTraceAndSlog;
import com.android.server.wallpaper.WallpaperDisplayHelper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public class WallpaperCropper {
    public static final String TAG = "WallpaperCropper";
    public final WallpaperDisplayHelper mWallpaperDisplayHelper;

    public WallpaperCropper(WallpaperDisplayHelper wallpaperDisplayHelper) {
        this.mWallpaperDisplayHelper = wallpaperDisplayHelper;
    }

    public void generateCrop(WallpaperData wallpaperData) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog(TAG);
        timingsTraceAndSlog.traceBegin("WPMS.generateCrop");
        generateCropInternal(wallpaperData);
        timingsTraceAndSlog.traceEnd();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(23:5|(1:7)(10:73|(1:75)(1:91)|76|(1:78)(1:90)|79|(1:81)|82|(1:84)|85|(9:89|9|(1:72)(1:15)|(2:17|(1:19))|20|(11:34|35|36|(2:37|(1:39)(1:40))|41|(1:43)|44|(1:46)(1:68)|47|(1:49)(6:51|52|53|54|55|56)|50)(2:23|(1:25))|(1:27)|28|(2:30|31)(1:33)))|8|9|(1:11)|72|(0)|20|(0)|34|35|36|(3:37|(0)(0)|39)|41|(0)|44|(0)(0)|47|(0)(0)|50|(0)|28|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x02f9, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x02fa, code lost:
        r2 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0302, code lost:
        r2 = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:40:0x009e  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0161 A[LOOP:0: B:51:0x015d->B:53:0x0161, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0193 A[Catch: all -> 0x02f9, Exception -> 0x0302, TryCatch #4 {Exception -> 0x0302, all -> 0x02f9, blocks: (B:50:0x0155, B:51:0x015d, B:54:0x0163, B:56:0x0193, B:57:0x01ce, B:61:0x027d, B:63:0x02c4, B:64:0x02cc), top: B:93:0x0155 }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0278  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x027b  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x02c4 A[Catch: all -> 0x02f9, Exception -> 0x0302, TryCatch #4 {Exception -> 0x0302, all -> 0x02f9, blocks: (B:50:0x0155, B:51:0x015d, B:54:0x0163, B:56:0x0193, B:57:0x01ce, B:61:0x027d, B:63:0x02c4, B:64:0x02cc), top: B:93:0x0155 }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x02cc A[Catch: all -> 0x02f9, Exception -> 0x0302, TRY_LEAVE, TryCatch #4 {Exception -> 0x0302, all -> 0x02f9, blocks: (B:50:0x0155, B:51:0x015d, B:54:0x0163, B:56:0x0193, B:57:0x01ce, B:61:0x027d, B:63:0x02c4, B:64:0x02cc), top: B:93:0x0155 }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0314  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0328  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0163 A[EDGE_INSN: B:94:0x0163->B:54:0x0163 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:95:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void generateCropInternal(WallpaperData wallpaperData) {
        boolean z;
        boolean z2;
        boolean z3;
        FileOutputStream fileOutputStream;
        final int i;
        int i2;
        int width;
        Bitmap decodeBitmap;
        WallpaperDisplayHelper.DisplayData displayDataOrCreate = this.mWallpaperDisplayHelper.getDisplayDataOrCreate(0);
        Rect rect = new Rect(wallpaperData.cropHint);
        DisplayInfo displayInfo = this.mWallpaperDisplayHelper.getDisplayInfo(0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(wallpaperData.wallpaperFile.getAbsolutePath(), options);
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            Slog.w(TAG, "Invalid wallpaper data");
        } else {
            if (rect.isEmpty()) {
                rect.top = 0;
                rect.left = 0;
                rect.right = options.outWidth;
                rect.bottom = options.outHeight;
            } else {
                int i3 = rect.right;
                int i4 = options.outWidth;
                int i5 = i3 > i4 ? i4 - i3 : 0;
                int i6 = rect.bottom;
                int i7 = options.outHeight;
                rect.offset(i5, i6 > i7 ? i7 - i6 : 0);
                if (rect.left < 0) {
                    rect.left = 0;
                }
                if (rect.top < 0) {
                    rect.top = 0;
                }
                if (options.outHeight > rect.height() || options.outWidth > rect.width()) {
                    z2 = true;
                    z3 = rect.height() <= displayDataOrCreate.mHeight || rect.height() > GLHelper.getMaxTextureSize() || rect.width() > GLHelper.getMaxTextureSize();
                    if (z3) {
                        int width2 = (int) (rect.width() * (displayDataOrCreate.mHeight / rect.height()));
                        int i8 = displayInfo.logicalWidth;
                        if (width2 < i8) {
                            rect.bottom = (int) (rect.width() * (displayInfo.logicalHeight / i8));
                            z2 = true;
                        }
                    }
                    String str = TAG;
                    Slog.v(str, "crop: w=" + rect.width() + " h=" + rect.height());
                    Slog.v(str, "dims: w=" + displayDataOrCreate.mWidth + " h=" + displayDataOrCreate.mHeight);
                    Slog.v(str, "meas: w=" + options.outWidth + " h=" + options.outHeight);
                    Slog.v(str, "crop?=" + z2 + " scale?=" + z3);
                    if (z2 && !z3) {
                        z = FileUtils.copyFile(wallpaperData.wallpaperFile, wallpaperData.cropFile);
                        if (!z) {
                            wallpaperData.cropFile.delete();
                        }
                    } else {
                        BufferedOutputStream bufferedOutputStream = null;
                        i = 1;
                        while (true) {
                            i2 = i * 2;
                            if (i2 <= rect.height() / displayDataOrCreate.mHeight) {
                                break;
                            }
                            i = i2;
                        }
                        options.inSampleSize = i;
                        options.inJustDecodeBounds = false;
                        final Rect rect2 = new Rect(rect);
                        rect2.scale(1.0f / options.inSampleSize);
                        float height = displayDataOrCreate.mHeight / rect2.height();
                        int height2 = (int) (rect2.height() * height);
                        width = (int) (rect2.width() * height);
                        if (width > GLHelper.getMaxTextureSize()) {
                            int i9 = (int) (displayDataOrCreate.mHeight / height);
                            int i10 = (int) (displayDataOrCreate.mWidth / height);
                            rect2.set(rect);
                            rect2.left += (rect.width() - i10) / 2;
                            int height3 = rect2.top + ((rect.height() - i9) / 2);
                            rect2.top = height3;
                            rect2.right = rect2.left + i10;
                            rect2.bottom = height3 + i9;
                            rect.set(rect2);
                            rect2.scale(1.0f / options.inSampleSize);
                        }
                        int height4 = (int) (rect2.height() * height);
                        int width3 = (int) (rect2.width() * height);
                        String str2 = TAG;
                        Slog.v(str2, "Decode parameters:");
                        Slog.v(str2, "  cropHint=" + rect + ", estimateCrop=" + rect2);
                        Slog.v(str2, "  down sampling=" + options.inSampleSize + ", hRatio=" + height);
                        Slog.v(str2, "  dest=" + width + "x" + height2);
                        Slog.v(str2, "  safe=" + width3 + "x" + height4);
                        StringBuilder sb = new StringBuilder();
                        sb.append("  maxTextureSize=");
                        sb.append(GLHelper.getMaxTextureSize());
                        Slog.v(str2, sb.toString());
                        File file = new File(WallpaperUtils.getWallpaperDir(wallpaperData.userId), !wallpaperData.wallpaperFile.getName().equals("wallpaper_orig") ? "decode_record" : "decode_lock_record");
                        file.createNewFile();
                        Slog.v(str2, "record path =" + file.getPath() + ", record name =" + file.getName());
                        decodeBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(wallpaperData.wallpaperFile), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.android.server.wallpaper.WallpaperCropper$$ExternalSyntheticLambda0
                            @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                            public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                                WallpaperCropper.lambda$generateCropInternal$0(i, rect2, imageDecoder, imageInfo, source);
                            }
                        });
                        file.delete();
                        if (decodeBitmap != null) {
                            Slog.e(str2, "Could not decode new wallpaper");
                            fileOutputStream = null;
                            z = false;
                        } else {
                            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(decodeBitmap, width3, height4, true);
                            fileOutputStream = new FileOutputStream(wallpaperData.cropFile);
                            try {
                                BufferedOutputStream bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream, 32768);
                                try {
                                    createScaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream2);
                                    bufferedOutputStream2.flush();
                                    bufferedOutputStream = bufferedOutputStream2;
                                    z = true;
                                } catch (Exception unused) {
                                    bufferedOutputStream = bufferedOutputStream2;
                                    IoUtils.closeQuietly(bufferedOutputStream);
                                    IoUtils.closeQuietly(fileOutputStream);
                                    z = false;
                                    if (!z) {
                                    }
                                    if (wallpaperData.cropFile.exists()) {
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    bufferedOutputStream = bufferedOutputStream2;
                                    IoUtils.closeQuietly(bufferedOutputStream);
                                    IoUtils.closeQuietly(fileOutputStream);
                                    throw th;
                                }
                            } catch (Exception unused2) {
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        IoUtils.closeQuietly(bufferedOutputStream);
                        IoUtils.closeQuietly(fileOutputStream);
                    }
                    if (!z) {
                        Slog.e(TAG, "Unable to apply new wallpaper");
                        wallpaperData.cropFile.delete();
                    }
                    if (wallpaperData.cropFile.exists()) {
                        return;
                    }
                    SELinux.restorecon(wallpaperData.cropFile.getAbsoluteFile());
                    return;
                }
            }
            z2 = false;
            if (rect.height() <= displayDataOrCreate.mHeight) {
            }
            if (z3) {
            }
            String str3 = TAG;
            Slog.v(str3, "crop: w=" + rect.width() + " h=" + rect.height());
            Slog.v(str3, "dims: w=" + displayDataOrCreate.mWidth + " h=" + displayDataOrCreate.mHeight);
            Slog.v(str3, "meas: w=" + options.outWidth + " h=" + options.outHeight);
            Slog.v(str3, "crop?=" + z2 + " scale?=" + z3);
            if (z2) {
            }
            BufferedOutputStream bufferedOutputStream3 = null;
            i = 1;
            while (true) {
                i2 = i * 2;
                if (i2 <= rect.height() / displayDataOrCreate.mHeight) {
                }
                i = i2;
            }
            options.inSampleSize = i;
            options.inJustDecodeBounds = false;
            final Rect rect22 = new Rect(rect);
            rect22.scale(1.0f / options.inSampleSize);
            float height5 = displayDataOrCreate.mHeight / rect22.height();
            int height22 = (int) (rect22.height() * height5);
            width = (int) (rect22.width() * height5);
            if (width > GLHelper.getMaxTextureSize()) {
            }
            int height42 = (int) (rect22.height() * height5);
            int width32 = (int) (rect22.width() * height5);
            String str22 = TAG;
            Slog.v(str22, "Decode parameters:");
            Slog.v(str22, "  cropHint=" + rect + ", estimateCrop=" + rect22);
            Slog.v(str22, "  down sampling=" + options.inSampleSize + ", hRatio=" + height5);
            Slog.v(str22, "  dest=" + width + "x" + height22);
            Slog.v(str22, "  safe=" + width32 + "x" + height42);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  maxTextureSize=");
            sb2.append(GLHelper.getMaxTextureSize());
            Slog.v(str22, sb2.toString());
            File file2 = new File(WallpaperUtils.getWallpaperDir(wallpaperData.userId), !wallpaperData.wallpaperFile.getName().equals("wallpaper_orig") ? "decode_record" : "decode_lock_record");
            file2.createNewFile();
            Slog.v(str22, "record path =" + file2.getPath() + ", record name =" + file2.getName());
            decodeBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(wallpaperData.wallpaperFile), new ImageDecoder.OnHeaderDecodedListener() { // from class: com.android.server.wallpaper.WallpaperCropper$$ExternalSyntheticLambda0
                @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                    WallpaperCropper.lambda$generateCropInternal$0(i, rect22, imageDecoder, imageInfo, source);
                }
            });
            file2.delete();
            if (decodeBitmap != null) {
            }
            IoUtils.closeQuietly(bufferedOutputStream3);
            IoUtils.closeQuietly(fileOutputStream);
            if (!z) {
            }
            if (wallpaperData.cropFile.exists()) {
            }
        }
        z = false;
        if (!z) {
        }
        if (wallpaperData.cropFile.exists()) {
        }
    }

    public static /* synthetic */ void lambda$generateCropInternal$0(int i, Rect rect, ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setTargetSampleSize(i);
        imageDecoder.setCrop(rect);
    }
}
