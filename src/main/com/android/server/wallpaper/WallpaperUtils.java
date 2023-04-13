package com.android.server.wallpaper;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class WallpaperUtils {
    public static final String[] sPerUserFiles = {"wallpaper_orig", "wallpaper", "wallpaper_lock_orig", "wallpaper_lock", "wallpaper_info.xml"};
    public static int sWallpaperId;

    public static File getWallpaperDir(int i) {
        return Environment.getUserSystemDirectory(i);
    }

    public static int makeWallpaperIdLocked() {
        int i;
        do {
            i = sWallpaperId + 1;
            sWallpaperId = i;
        } while (i == 0);
        return i;
    }

    public static int getCurrentWallpaperId() {
        return sWallpaperId;
    }

    public static void setCurrentWallpaperId(int i) {
        sWallpaperId = i;
    }

    public static List<File> getWallpaperFiles(int i) {
        File wallpaperDir = getWallpaperDir(i);
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        while (true) {
            String[] strArr = sPerUserFiles;
            if (i2 >= strArr.length) {
                return arrayList;
            }
            arrayList.add(new File(wallpaperDir, strArr[i2]));
            i2++;
        }
    }
}
