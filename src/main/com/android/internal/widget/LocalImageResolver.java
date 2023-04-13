package com.android.internal.widget;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import java.io.IOException;
/* loaded from: classes5.dex */
public class LocalImageResolver {
    static final int DEFAULT_MAX_SAFE_ICON_SIZE_PX = 480;
    public static final int NO_MAX_SIZE = -1;
    private static final String TAG = "LocalImageResolver";

    public static Drawable resolveImage(Uri uri, Context context) throws IOException {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
            return ImageDecoder.decodeDrawable(source, new ImageDecoder.OnHeaderDecodedListener() { // from class: com.android.internal.widget.LocalImageResolver$$ExternalSyntheticLambda0
                @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source2) {
                    LocalImageResolver.onHeaderDecoded(imageDecoder, imageInfo, 480, 480);
                }
            });
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static Drawable resolveImage(Icon icon, Context context) throws IOException {
        return resolveImage(icon, context, 480, 480);
    }

    public static Drawable resolveImage(Icon icon, Context context, int maxWidth, int maxHeight) {
        Drawable result;
        if (icon == null) {
            return null;
        }
        switch (icon.getType()) {
            case 1:
            case 5:
                return resolveBitmapImage(icon, context, maxWidth, maxHeight);
            case 2:
                Resources res = resolveResourcesForIcon(context, icon);
                if (res == null) {
                    return icon.loadDrawable(context);
                }
                Drawable result2 = resolveImage(res, icon.getResId(), maxWidth, maxHeight);
                if (result2 != null) {
                    return tintDrawable(icon, result2);
                }
                break;
            case 4:
            case 6:
                Uri uri = getResolvableUri(icon);
                if (uri != null && (result = resolveImage(uri, context, maxWidth, maxHeight)) != null) {
                    return tintDrawable(icon, result);
                }
                break;
        }
        try {
            return icon.loadDrawable(context);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    public static Drawable resolveImage(Uri uri, Context context, int maxWidth, int maxHeight) {
        ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
        return resolveImage(source, maxWidth, maxHeight);
    }

    public static Drawable resolveImage(int resId, Context context, int maxWidth, int maxHeight) {
        ImageDecoder.Source source = ImageDecoder.createSource(context.getResources(), resId);
        return resolveImage(source, maxWidth, maxHeight);
    }

    private static Drawable resolveImage(Resources res, int resId, int maxWidth, int maxHeight) {
        ImageDecoder.Source source = ImageDecoder.createSource(res, resId);
        return resolveImage(source, maxWidth, maxHeight);
    }

    private static Drawable resolveBitmapImage(Icon icon, Context context, int maxWidth, int maxHeight) {
        if (maxWidth > 0 && maxHeight > 0) {
            Bitmap bitmap = icon.getBitmap();
            if (bitmap == null) {
                return null;
            }
            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                Icon smallerIcon = icon.getType() == 5 ? Icon.createWithAdaptiveBitmap(bitmap) : Icon.createWithBitmap(bitmap);
                smallerIcon.setTintList(icon.getTintList()).setTintBlendMode(icon.getTintBlendMode()).scaleDownIfNecessary(maxWidth, maxHeight);
                return smallerIcon.loadDrawable(context);
            }
        }
        return icon.loadDrawable(context);
    }

    private static Drawable tintDrawable(Icon icon, Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (icon.hasTint()) {
            drawable.mutate();
            drawable.setTintList(icon.getTintList());
            drawable.setTintBlendMode(icon.getTintBlendMode());
        }
        return drawable;
    }

    private static Drawable resolveImage(ImageDecoder.Source source, final int maxWidth, final int maxHeight) {
        try {
            return ImageDecoder.decodeDrawable(source, new ImageDecoder.OnHeaderDecodedListener() { // from class: com.android.internal.widget.LocalImageResolver$$ExternalSyntheticLambda1
                @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source2) {
                    LocalImageResolver.lambda$resolveImage$1(maxWidth, maxHeight, imageDecoder, imageInfo, source2);
                }
            });
        } catch (Resources.NotFoundException | IOException e) {
            Log.m112d(TAG, "Couldn't use ImageDecoder for drawable, falling back to non-resized load.");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$resolveImage$1(int maxWidth, int maxHeight, ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source unused) {
        if (maxWidth <= 0 || maxHeight <= 0) {
            return;
        }
        Size size = info.getSize();
        if (size.getWidth() <= maxWidth && size.getHeight() <= maxHeight) {
            return;
        }
        if (size.getWidth() > size.getHeight()) {
            if (size.getWidth() > maxWidth) {
                int targetHeight = (size.getHeight() * maxWidth) / size.getWidth();
                decoder.setTargetSize(maxWidth, targetHeight);
            }
        } else if (size.getHeight() > maxHeight) {
            int targetWidth = (size.getWidth() * maxHeight) / size.getHeight();
            decoder.setTargetSize(targetWidth, maxHeight);
        }
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        return Math.max(1, k);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onHeaderDecoded(ImageDecoder decoder, ImageDecoder.ImageInfo info, int maxWidth, int maxHeight) {
        double ratio;
        Size size = info.getSize();
        int originalSize = Math.max(size.getHeight(), size.getWidth());
        int maxSize = Math.max(maxWidth, maxHeight);
        if (originalSize > maxSize) {
            ratio = (originalSize * 1.0f) / maxSize;
        } else {
            ratio = 1.0d;
        }
        decoder.setTargetSampleSize(getPowerOfTwoForSampleRatio(ratio));
    }

    private static Uri getResolvableUri(Icon icon) {
        if (icon != null) {
            if (icon.getType() != 4 && icon.getType() != 6) {
                return null;
            }
            return icon.getUri();
        }
        return null;
    }

    public static Resources resolveResourcesForIcon(Context context, Icon icon) {
        if (icon.getType() != 2) {
            return null;
        }
        Resources res = icon.getResources();
        if (res != null) {
            return res;
        }
        String resPackage = icon.getResPackage();
        if (TextUtils.isEmpty(resPackage) || context.getPackageName().equals(resPackage)) {
            return context.getResources();
        }
        if ("android".equals(resPackage)) {
            return Resources.getSystem();
        }
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(resPackage, 9216);
            if (ai != null) {
                return pm.getResourcesForApplication(ai);
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, String.format("Unable to resolve package %s for icon %s", resPackage, icon));
            return null;
        }
    }
}
