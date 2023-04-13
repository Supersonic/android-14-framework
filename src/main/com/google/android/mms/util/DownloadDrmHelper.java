package com.google.android.mms.util;

import android.content.Context;
import android.drm.DrmManagerClient;
import android.media.MediaMetrics;
import android.util.Log;
/* loaded from: classes5.dex */
public class DownloadDrmHelper {
    public static final String EXTENSION_DRM_MESSAGE = ".dm";
    public static final String EXTENSION_INTERNAL_FWDL = ".fl";
    public static final String MIMETYPE_DRM_MESSAGE = "application/vnd.oma.drm.message";
    private static final String TAG = "DownloadDrmHelper";

    public static boolean isDrmMimeType(Context context, String mimetype) {
        if (context != null) {
            try {
                DrmManagerClient drmClient = new DrmManagerClient(context);
                if (mimetype == null || mimetype.length() <= 0) {
                    return false;
                }
                boolean result = drmClient.canHandle("", mimetype);
                return result;
            } catch (IllegalArgumentException e) {
                Log.m104w(TAG, "DrmManagerClient instance could not be created, context is Illegal.");
                return false;
            } catch (IllegalStateException e2) {
                Log.m104w(TAG, "DrmManagerClient didn't initialize properly.");
                return false;
            }
        }
        return false;
    }

    public static boolean isDrmConvertNeeded(String mimetype) {
        return "application/vnd.oma.drm.message".equals(mimetype);
    }

    public static String modifyDrmFwLockFileExtension(String filename) {
        if (filename != null) {
            int extensionIndex = filename.lastIndexOf(MediaMetrics.SEPARATOR);
            if (extensionIndex != -1) {
                filename = filename.substring(0, extensionIndex);
            }
            return filename.concat(EXTENSION_INTERNAL_FWDL);
        }
        return filename;
    }

    public static String getOriginalMimeType(Context context, String path, String containingMime) {
        DrmManagerClient drmClient = new DrmManagerClient(context);
        try {
            if (drmClient.canHandle(path, (String) null)) {
                String result = drmClient.getOriginalMimeType(path);
                return result;
            }
            return containingMime;
        } catch (IllegalArgumentException e) {
            Log.m104w(TAG, "Can't get original mime type since path is null or empty string.");
            return containingMime;
        } catch (IllegalStateException e2) {
            Log.m104w(TAG, "DrmManagerClient didn't initialize properly.");
            return containingMime;
        }
    }
}
