package com.android.internal.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.p008os.UserHandle;
import android.util.AttributeSet;
import android.util.Pools;
import android.util.TypedValue;
import com.android.internal.C4057R;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;
@Deprecated
/* loaded from: classes4.dex */
public class SimpleIconFactory {
    private static final int AMBIENT_SHADOW_ALPHA = 7;
    private static final float BLUR_FACTOR = 0.03125f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static final int KEY_SHADOW_ALPHA = 10;
    private static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final Pools.SynchronizedPool<SimpleIconFactory> sPool = new Pools.SynchronizedPool<>(Runtime.getRuntime().availableProcessors());
    private static boolean sPoolEnabled = true;
    private final Rect mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private int mBadgeBitmapSize;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private Canvas mCanvas;
    private Context mContext;
    private BlurMaskFilter mDefaultBlurMaskFilter;
    private int mFillResIconDpi;
    private int mIconBitmapSize;
    private final float[] mLeftBorder;
    private final int mMaxSize;
    private final byte[] mPixels;
    private PackageManager mPm;
    private final float[] mRightBorder;
    private final Canvas mScaleCheckCanvas;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;
    private final Rect mOldBounds = new Rect();
    private Paint mBlurPaint = new Paint(3);
    private Paint mDrawPaint = new Paint(3);

    @Deprecated
    public static SimpleIconFactory obtain(Context ctx) {
        SimpleIconFactory instance = sPoolEnabled ? sPool.acquire() : null;
        if (instance == null) {
            ActivityManager am = (ActivityManager) ctx.getSystemService("activity");
            int iconDpi = am == null ? 0 : am.getLauncherLargeIconDensity();
            int iconSize = getIconSizeFromContext(ctx);
            int badgeSize = getBadgeSizeFromContext(ctx);
            SimpleIconFactory instance2 = new SimpleIconFactory(ctx, iconDpi, iconSize, badgeSize);
            instance2.setWrapperBackgroundColor(-1);
            return instance2;
        }
        return instance;
    }

    public static void setPoolEnabled(boolean poolEnabled) {
        sPoolEnabled = poolEnabled;
    }

    private static int getAttrDimFromContext(Context ctx, int attrId, String errorMsg) {
        Resources res = ctx.getResources();
        TypedValue outVal = new TypedValue();
        if (!ctx.getTheme().resolveAttribute(attrId, outVal, true)) {
            throw new IllegalStateException(errorMsg);
        }
        return res.getDimensionPixelSize(outVal.resourceId);
    }

    private static int getIconSizeFromContext(Context ctx) {
        return getAttrDimFromContext(ctx, C4057R.attr.iconfactoryIconSize, "Expected theme to define iconfactoryIconSize.");
    }

    private static int getBadgeSizeFromContext(Context ctx) {
        return getAttrDimFromContext(ctx, C4057R.attr.iconfactoryBadgeSize, "Expected theme to define iconfactoryBadgeSize.");
    }

    @Deprecated
    public void recycle() {
        setWrapperBackgroundColor(-1);
        sPool.release(this);
    }

    @Deprecated
    private SimpleIconFactory(Context context, int fillResIconDpi, int iconBitmapSize, int badgeBitmapSize) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mPm = applicationContext.getPackageManager();
        this.mIconBitmapSize = iconBitmapSize;
        this.mBadgeBitmapSize = badgeBitmapSize;
        this.mFillResIconDpi = fillResIconDpi;
        Canvas canvas = new Canvas();
        this.mCanvas = canvas;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        int i = iconBitmapSize * 2;
        this.mMaxSize = i;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ALPHA_8);
        this.mBitmap = createBitmap;
        this.mScaleCheckCanvas = new Canvas(createBitmap);
        this.mPixels = new byte[i * i];
        this.mLeftBorder = new float[i];
        this.mRightBorder = new float[i];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new Rect();
        this.mAdaptiveIconScale = 0.0f;
        this.mDefaultBlurMaskFilter = new BlurMaskFilter(iconBitmapSize * BLUR_FACTOR, BlurMaskFilter.Blur.NORMAL);
    }

    @Deprecated
    void setWrapperBackgroundColor(int color) {
        this.mWrapperBackgroundColor = Color.alpha(color) < 255 ? -1 : color;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public Bitmap createUserBadgedIconBitmap(Drawable icon, UserHandle user) {
        float[] scale = new float[1];
        if (icon == null) {
            icon = getFullResDefaultActivityIcon(this.mFillResIconDpi);
        }
        Drawable icon2 = normalizeAndWrapToAdaptiveIcon(icon, null, scale);
        Bitmap bitmap = createIconBitmap(icon2, scale[0]);
        if (icon2 instanceof AdaptiveIconDrawable) {
            this.mCanvas.setBitmap(bitmap);
            recreateIcon(Bitmap.createBitmap(bitmap), this.mCanvas);
            this.mCanvas.setBitmap(null);
        }
        if (user != null) {
            BitmapDrawable drawable = new FixedSizeBitmapDrawable(bitmap);
            Drawable badged = this.mPm.getUserBadgedIcon(drawable, user);
            if (badged instanceof BitmapDrawable) {
                Bitmap result = ((BitmapDrawable) badged).getBitmap();
                return result;
            }
            Bitmap result2 = createIconBitmap(badged, 1.0f);
            return result2;
        }
        return bitmap;
    }

    @Deprecated
    public Bitmap createAppBadgedIconBitmap(Drawable icon, Bitmap renderedAppIcon) {
        if (icon == null) {
            icon = getFullResDefaultActivityIcon(this.mFillResIconDpi);
        }
        int w = icon.getIntrinsicWidth();
        int h = icon.getIntrinsicHeight();
        float scale = 1.0f;
        if (h > w && w > 0) {
            scale = h / w;
        } else if (w > h && h > 0) {
            scale = w / h;
        }
        Bitmap bitmap = createIconBitmapNoInsetOrMask(icon, scale);
        Drawable icon2 = new BitmapDrawable(this.mContext.getResources(), maskBitmapToCircle(bitmap));
        Bitmap bitmap2 = createIconBitmap(icon2, getScale(icon2, null));
        this.mCanvas.setBitmap(bitmap2);
        recreateIcon(Bitmap.createBitmap(bitmap2), this.mCanvas);
        if (renderedAppIcon != null) {
            int i = this.mBadgeBitmapSize;
            Bitmap renderedAppIcon2 = Bitmap.createScaledBitmap(renderedAppIcon, i, i, false);
            Canvas canvas = this.mCanvas;
            int i2 = this.mIconBitmapSize;
            int i3 = this.mBadgeBitmapSize;
            canvas.drawBitmap(renderedAppIcon2, i2 - i3, i2 - i3, (Paint) null);
        }
        this.mCanvas.setBitmap(null);
        return bitmap2;
    }

    private Bitmap maskBitmapToCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(7);
        int size = bitmap.getWidth();
        int offset = Math.max((int) Math.ceil(size * BLUR_FACTOR), 1);
        paint.setColor(-1);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f, (bitmap.getWidth() / 2.0f) - offset, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private static Drawable getFullResDefaultActivityIcon(int iconDpi) {
        return Resources.getSystem().getDrawableForDensity(17629184, iconDpi);
    }

    private Bitmap createIconBitmap(Drawable icon, float scale) {
        return createIconBitmap(icon, scale, this.mIconBitmapSize, true, false);
    }

    private Bitmap createIconBitmapNoInsetOrMask(Drawable icon, float scale) {
        return createIconBitmap(icon, scale, this.mIconBitmapSize, false, true);
    }

    private Bitmap createIconBitmap(Drawable icon, float scale, int size, boolean insetAdiForShadow, boolean ignoreAdiMask) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        this.mCanvas.setBitmap(bitmap);
        this.mOldBounds.set(icon.getBounds());
        if (icon instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable adi = (AdaptiveIconDrawable) icon;
            int offset = Math.round((size * (1.0f - scale)) / 2.0f);
            if (insetAdiForShadow) {
                offset = Math.max((int) Math.ceil(size * BLUR_FACTOR), offset);
            }
            Rect bounds = new Rect(offset, offset, size - offset, size - offset);
            if (ignoreAdiMask) {
                int cX = bounds.width() / 2;
                int cY = bounds.height() / 2;
                float portScale = 1.0f / ((AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f);
                int insetWidth = (int) (bounds.width() / (portScale * 2.0f));
                int insetHeight = (int) (bounds.height() / (2.0f * portScale));
                final Rect childRect = new Rect(cX - insetWidth, cY - insetHeight, cX + insetWidth, cY + insetHeight);
                Optional.ofNullable(adi.getBackground()).ifPresent(new Consumer() { // from class: com.android.internal.app.SimpleIconFactory$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SimpleIconFactory.this.lambda$createIconBitmap$0(childRect, (Drawable) obj);
                    }
                });
                Optional.ofNullable(adi.getForeground()).ifPresent(new Consumer() { // from class: com.android.internal.app.SimpleIconFactory$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SimpleIconFactory.this.lambda$createIconBitmap$1(childRect, (Drawable) obj);
                    }
                });
            } else {
                adi.setBounds(bounds);
                adi.draw(this.mCanvas);
            }
        } else {
            if (icon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap b = bitmapDrawable.getBitmap();
                if (bitmap != null && b.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            int width = size;
            int height = size;
            int intrinsicWidth = icon.getIntrinsicWidth();
            int intrinsicHeight = icon.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float ratio = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    height = (int) (width / ratio);
                } else if (intrinsicHeight > intrinsicWidth) {
                    width = (int) (height * ratio);
                }
            }
            int left = (size - width) / 2;
            int top = (size - height) / 2;
            icon.setBounds(left, top, left + width, top + height);
            this.mCanvas.save();
            this.mCanvas.scale(scale, scale, size / 2, size / 2);
            icon.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        icon.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap(null);
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createIconBitmap$0(Rect childRect, Drawable drawable) {
        drawable.setBounds(childRect);
        drawable.draw(this.mCanvas);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createIconBitmap$1(Rect childRect, Drawable drawable) {
        drawable.setBounds(childRect);
        drawable.draw(this.mCanvas);
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable icon, RectF outIconBounds, float[] outScale) {
        if (this.mWrapperIcon == null) {
            this.mWrapperIcon = this.mContext.getDrawable(C4057R.C4058drawable.iconfactory_adaptive_icon_drawable_wrapper).mutate();
        }
        AdaptiveIconDrawable dr = (AdaptiveIconDrawable) this.mWrapperIcon;
        dr.setBounds(0, 0, 1, 1);
        float scale = getScale(icon, outIconBounds);
        if (!(icon instanceof AdaptiveIconDrawable)) {
            FixedScaleDrawable fsd = (FixedScaleDrawable) dr.getForeground();
            fsd.setDrawable(icon);
            fsd.setScale(scale);
            icon = dr;
            scale = getScale(icon, outIconBounds);
            ((ColorDrawable) dr.getBackground()).setColor(this.mWrapperBackgroundColor);
        }
        outScale[0] = scale;
        return icon;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0042, code lost:
        if (r3 <= r22.mMaxSize) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0045, code lost:
        r6 = r3;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00d3 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00ed A[Catch: all -> 0x01a2, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000c, B:9:0x0014, B:10:0x0019, B:13:0x001d, B:17:0x002a, B:35:0x0056, B:40:0x008a, B:46:0x009c, B:47:0x00a5, B:52:0x00b9, B:53:0x00c4, B:58:0x00dd, B:60:0x00ed, B:64:0x00ff, B:63:0x00f8, B:65:0x0102, B:69:0x0122, B:71:0x0134, B:73:0x0168, B:75:0x0171, B:77:0x017e, B:79:0x0186, B:81:0x018d, B:68:0x0117, B:20:0x0030, B:22:0x0040, B:29:0x004c, B:33:0x0053, B:26:0x0047), top: B:91:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0113  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0117 A[Catch: all -> 0x01a2, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000c, B:9:0x0014, B:10:0x0019, B:13:0x001d, B:17:0x002a, B:35:0x0056, B:40:0x008a, B:46:0x009c, B:47:0x00a5, B:52:0x00b9, B:53:0x00c4, B:58:0x00dd, B:60:0x00ed, B:64:0x00ff, B:63:0x00f8, B:65:0x0102, B:69:0x0122, B:71:0x0134, B:73:0x0168, B:75:0x0171, B:77:0x017e, B:79:0x0186, B:81:0x018d, B:68:0x0117, B:20:0x0030, B:22:0x0040, B:29:0x004c, B:33:0x0053, B:26:0x0047), top: B:91:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0134 A[Catch: all -> 0x01a2, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000c, B:9:0x0014, B:10:0x0019, B:13:0x001d, B:17:0x002a, B:35:0x0056, B:40:0x008a, B:46:0x009c, B:47:0x00a5, B:52:0x00b9, B:53:0x00c4, B:58:0x00dd, B:60:0x00ed, B:64:0x00ff, B:63:0x00f8, B:65:0x0102, B:69:0x0122, B:71:0x0134, B:73:0x0168, B:75:0x0171, B:77:0x017e, B:79:0x0186, B:81:0x018d, B:68:0x0117, B:20:0x0030, B:22:0x0040, B:29:0x004c, B:33:0x0053, B:26:0x0047), top: B:91:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0162  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0171 A[Catch: all -> 0x01a2, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000c, B:9:0x0014, B:10:0x0019, B:13:0x001d, B:17:0x002a, B:35:0x0056, B:40:0x008a, B:46:0x009c, B:47:0x00a5, B:52:0x00b9, B:53:0x00c4, B:58:0x00dd, B:60:0x00ed, B:64:0x00ff, B:63:0x00f8, B:65:0x0102, B:69:0x0122, B:71:0x0134, B:73:0x0168, B:75:0x0171, B:77:0x017e, B:79:0x0186, B:81:0x018d, B:68:0x0117, B:20:0x0030, B:22:0x0040, B:29:0x004c, B:33:0x0053, B:26:0x0047), top: B:91:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0198 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private synchronized float getScale(Drawable d, RectF outBounds) {
        int i;
        int topY;
        int bottomY;
        int leftX;
        int rightX;
        int y;
        int y2;
        float hullByRect;
        float scaleRequired;
        float scale;
        if ((d instanceof AdaptiveIconDrawable) && this.mAdaptiveIconScale != 0.0f) {
            if (outBounds != null) {
                outBounds.set(this.mAdaptiveIconBounds);
            }
            return this.mAdaptiveIconScale;
        }
        int width = d.getIntrinsicWidth();
        int height = d.getIntrinsicHeight();
        if (width > 0 && height > 0) {
            int i2 = this.mMaxSize;
            if (width > i2 || height > i2) {
                int max = Math.max(width, height);
                int i3 = this.mMaxSize;
                width = (i3 * width) / max;
                height = (i3 * height) / max;
            }
            this.mBitmap.eraseColor(0);
            d.setBounds(0, 0, width, height);
            d.draw(this.mScaleCheckCanvas);
            ByteBuffer buffer = ByteBuffer.wrap(this.mPixels);
            buffer.rewind();
            this.mBitmap.copyPixelsToBuffer(buffer);
            topY = -1;
            bottomY = -1;
            int i4 = this.mMaxSize;
            leftX = i4 + 1;
            rightX = -1;
            int index = 0;
            int rowSizeDiff = i4 - width;
            y = 0;
            while (y < height) {
                int lastX = -1;
                int firstX = -1;
                int x = 0;
                while (x < width) {
                    ByteBuffer buffer2 = buffer;
                    if ((this.mPixels[index] & 255) > 40) {
                        if (firstX == -1) {
                            firstX = x;
                        }
                        lastX = x;
                    }
                    index++;
                    x++;
                    buffer = buffer2;
                }
                ByteBuffer buffer3 = buffer;
                index += rowSizeDiff;
                this.mLeftBorder[y] = firstX;
                this.mRightBorder[y] = lastX;
                if (firstX != -1) {
                    int bottomY2 = y;
                    if (topY == -1) {
                        topY = y;
                    }
                    leftX = Math.min(leftX, firstX);
                    rightX = Math.max(rightX, lastX);
                    bottomY = bottomY2;
                }
                y++;
                buffer = buffer3;
            }
            if (topY == -1 && rightX != -1) {
                convertToConvexArray(this.mLeftBorder, 1, topY, bottomY);
                convertToConvexArray(this.mRightBorder, -1, topY, bottomY);
                float area = 0.0f;
                for (y2 = 0; y2 < height; y2++) {
                    float f = this.mLeftBorder[y2];
                    if (f > -1.0f) {
                        area += (this.mRightBorder[y2] - f) + 1.0f;
                    }
                }
                int y3 = bottomY + 1;
                float rectArea = (y3 - topY) * ((rightX + 1) - leftX);
                hullByRect = area / rectArea;
                if (hullByRect >= CIRCLE_AREA_BY_RECT) {
                    scaleRequired = MAX_CIRCLE_AREA_FACTOR;
                } else {
                    scaleRequired = ((1.0f - hullByRect) * LINEAR_SCALE_SLOPE) + MAX_SQUARE_AREA_FACTOR;
                }
                this.mBounds.left = leftX;
                this.mBounds.right = rightX;
                this.mBounds.top = topY;
                this.mBounds.bottom = bottomY;
                if (outBounds == null) {
                    float rectArea2 = height;
                    outBounds.set(this.mBounds.left / width, this.mBounds.top / rectArea2, 1.0f - (this.mBounds.right / width), 1.0f - (this.mBounds.bottom / height));
                }
                float areaScale = area / (width * height);
                scale = areaScale <= scaleRequired ? (float) Math.sqrt(scaleRequired / areaScale) : 1.0f;
                if ((d instanceof AdaptiveIconDrawable) && this.mAdaptiveIconScale == 0.0f) {
                    this.mAdaptiveIconScale = scale;
                    this.mAdaptiveIconBounds.set(this.mBounds);
                }
                return scale;
            }
            return 1.0f;
        }
        int i5 = this.mMaxSize;
        width = i5;
        if (height > 0 && height <= this.mMaxSize) {
            i = height;
            height = i;
            this.mBitmap.eraseColor(0);
            d.setBounds(0, 0, width, height);
            d.draw(this.mScaleCheckCanvas);
            ByteBuffer buffer4 = ByteBuffer.wrap(this.mPixels);
            buffer4.rewind();
            this.mBitmap.copyPixelsToBuffer(buffer4);
            topY = -1;
            bottomY = -1;
            int i42 = this.mMaxSize;
            leftX = i42 + 1;
            rightX = -1;
            int index2 = 0;
            int rowSizeDiff2 = i42 - width;
            y = 0;
            while (y < height) {
            }
            if (topY == -1) {
                convertToConvexArray(this.mLeftBorder, 1, topY, bottomY);
                convertToConvexArray(this.mRightBorder, -1, topY, bottomY);
                float area2 = 0.0f;
                while (y2 < height) {
                }
                int y32 = bottomY + 1;
                float rectArea3 = (y32 - topY) * ((rightX + 1) - leftX);
                hullByRect = area2 / rectArea3;
                if (hullByRect >= CIRCLE_AREA_BY_RECT) {
                }
                this.mBounds.left = leftX;
                this.mBounds.right = rightX;
                this.mBounds.top = topY;
                this.mBounds.bottom = bottomY;
                if (outBounds == null) {
                }
                float areaScale2 = area2 / (width * height);
                scale = areaScale2 <= scaleRequired ? (float) Math.sqrt(scaleRequired / areaScale2) : 1.0f;
                if (d instanceof AdaptiveIconDrawable) {
                    this.mAdaptiveIconScale = scale;
                    this.mAdaptiveIconBounds.set(this.mBounds);
                }
                return scale;
            }
            return 1.0f;
        }
        i = this.mMaxSize;
        height = i;
        this.mBitmap.eraseColor(0);
        d.setBounds(0, 0, width, height);
        d.draw(this.mScaleCheckCanvas);
        ByteBuffer buffer42 = ByteBuffer.wrap(this.mPixels);
        buffer42.rewind();
        this.mBitmap.copyPixelsToBuffer(buffer42);
        topY = -1;
        bottomY = -1;
        int i422 = this.mMaxSize;
        leftX = i422 + 1;
        rightX = -1;
        int index22 = 0;
        int rowSizeDiff22 = i422 - width;
        y = 0;
        while (y < height) {
        }
        if (topY == -1) {
        }
        return 1.0f;
    }

    private static void convertToConvexArray(float[] xCoordinates, int direction, int topY, int bottomY) {
        int start;
        int total = xCoordinates.length;
        float[] angles = new float[total - 1];
        int last = -1;
        float lastAngle = Float.MAX_VALUE;
        for (int i = topY + 1; i <= bottomY; i++) {
            if (xCoordinates[i] > -1.0f) {
                if (lastAngle == Float.MAX_VALUE) {
                    start = topY;
                } else {
                    float currentAngle = (xCoordinates[i] - xCoordinates[last]) / (i - last);
                    int start2 = last;
                    if ((currentAngle - lastAngle) * direction >= 0.0f) {
                        start = start2;
                    } else {
                        start = start2;
                        while (start > topY) {
                            start--;
                            float currentAngle2 = (xCoordinates[i] - xCoordinates[start]) / (i - start);
                            if ((currentAngle2 - angles[start]) * direction >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                float lastAngle2 = (xCoordinates[i] - xCoordinates[start]) / (i - start);
                for (int j = start; j < i; j++) {
                    angles[j] = lastAngle2;
                    xCoordinates[j] = xCoordinates[start] + ((j - start) * lastAngle2);
                }
                last = i;
                lastAngle = lastAngle2;
            }
        }
    }

    private synchronized void recreateIcon(Bitmap icon, Canvas out) {
        recreateIcon(icon, this.mDefaultBlurMaskFilter, 7, 10, out);
    }

    private synchronized void recreateIcon(Bitmap icon, BlurMaskFilter blurMaskFilter, int ambientAlpha, int keyAlpha, Canvas out) {
        int[] offset = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap shadow = icon.extractAlpha(this.mBlurPaint, offset);
        this.mDrawPaint.setAlpha(ambientAlpha);
        out.drawBitmap(shadow, offset[0], offset[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(keyAlpha);
        out.drawBitmap(shadow, offset[0], offset[1] + (this.mIconBitmapSize * KEY_SHADOW_DISTANCE), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        out.drawBitmap(icon, 0.0f, 0.0f, this.mDrawPaint);
    }

    /* loaded from: classes4.dex */
    public static class FixedScaleDrawable extends DrawableWrapper {
        private static final float LEGACY_ICON_SCALE = 0.46669f;
        private float mScaleX;
        private float mScaleY;

        public FixedScaleDrawable() {
            super(new ColorDrawable());
            this.mScaleX = LEGACY_ICON_SCALE;
            this.mScaleY = LEGACY_ICON_SCALE;
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int saveCount = canvas.save();
            canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
            super.draw(canvas);
            canvas.restoreToCount(saveCount);
        }

        @Override // android.graphics.drawable.Drawable
        public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) {
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
        }

        public void setScale(float scale) {
            float h = getIntrinsicHeight();
            float w = getIntrinsicWidth();
            float f = scale * LEGACY_ICON_SCALE;
            this.mScaleX = f;
            float f2 = LEGACY_ICON_SCALE * scale;
            this.mScaleY = f2;
            if (h > w && w > 0.0f) {
                this.mScaleX = f * (w / h);
            } else if (w > h && h > 0.0f) {
                this.mScaleY = f2 * (h / w);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class FixedSizeBitmapDrawable extends BitmapDrawable {
        FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }
}
