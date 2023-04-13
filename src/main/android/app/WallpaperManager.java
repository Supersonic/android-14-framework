package android.app;

import android.annotation.SystemApi;
import android.app.ILocalWallpaperColorConsumer;
import android.app.IWallpaperManagerCallback;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorSpace;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.DeadSystemException;
import android.p008os.Environment;
import android.p008os.FileUtils;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.StrictMode;
import android.p008os.SystemProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManagerGlobal;
import com.android.internal.C4057R;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class WallpaperManager {
    public static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
    public static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
    public static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER";
    public static final String COMMAND_DROP = "android.home.drop";
    public static final String COMMAND_FREEZE = "android.wallpaper.freeze";
    public static final String COMMAND_GOING_TO_SLEEP = "android.wallpaper.goingtosleep";
    public static final String COMMAND_REAPPLY = "android.wallpaper.reapply";
    public static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap";
    public static final String COMMAND_TAP = "android.wallpaper.tap";
    public static final String COMMAND_UNFREEZE = "android.wallpaper.unfreeze";
    public static final String COMMAND_WAKING_UP = "android.wallpaper.wakingup";
    private static final boolean DEBUG = false;
    public static final String EXTRA_FROM_FOREGROUND_APP = "android.service.wallpaper.extra.FROM_FOREGROUND_APP";
    public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
    public static final String EXTRA_NEW_WALLPAPER_ID = "android.service.wallpaper.extra.ID";
    public static final int FLAG_LOCK = 2;
    public static final int FLAG_SYSTEM = 1;
    private static final String PROP_LOCK_WALLPAPER = "ro.config.lock_wallpaper";
    private static final String PROP_WALLPAPER = "ro.config.wallpaper";
    private static final String PROP_WALLPAPER_COMPONENT = "ro.config.wallpaper_component";
    private static final String WALLPAPER_CMF_PATH = "/wallpaper/image/";
    public static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview";
    private static Globals sGlobals;
    private final ColorManagementProxy mCmProxy;
    private final Context mContext;
    private final boolean mLockscreenLiveWallpaper;
    private float mWallpaperXStep;
    private float mWallpaperYStep;
    private final boolean mWcgEnabled;
    private static String TAG = "WallpaperManager";
    private static final RectF LOCAL_COLOR_BOUNDS = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    private static final String VALUE_CMF_COLOR = SystemProperties.get("ro.boot.hardware.color");
    private static final Object sSync = new Object[0];

    /* loaded from: classes.dex */
    public interface LocalWallpaperColorConsumer {
        void onColorsChanged(RectF rectF, WallpaperColors wallpaperColors);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SetWallpaperFlags {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FastBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private int mDrawLeft;
        private int mDrawTop;
        private final int mHeight;
        private final Paint mPaint;
        private final int mWidth;

        private FastBitmapDrawable(Bitmap bitmap) {
            this.mBitmap = bitmap;
            int width = bitmap.getWidth();
            this.mWidth = width;
            int height = bitmap.getHeight();
            this.mHeight = height;
            setBounds(0, 0, width, height);
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, this.mDrawLeft, this.mDrawTop, this.mPaint);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -1;
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int left, int top, int right, int bottom) {
            this.mDrawLeft = (((right - left) - this.mWidth) / 2) + left;
            this.mDrawTop = (((bottom - top) - this.mHeight) / 2) + top;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setDither(boolean dither) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setFilterBitmap(boolean filter) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mHeight;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumHeight() {
            return this.mHeight;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CachedWallpaper {
        final Bitmap mCachedWallpaper;
        final int mCachedWallpaperUserId;
        final int mWhich;

        CachedWallpaper(Bitmap cachedWallpaper, int cachedWallpaperUserId, int which) {
            this.mCachedWallpaper = cachedWallpaper;
            this.mCachedWallpaperUserId = cachedWallpaperUserId;
            this.mWhich = which;
        }

        boolean isValid(int userId, int which) {
            return userId == this.mCachedWallpaperUserId && which == this.mWhich && !this.mCachedWallpaper.isRecycled();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Globals extends IWallpaperManagerCallback.Stub {
        private CachedWallpaper mCachedWallpaper;
        private boolean mColorCallbackRegistered;
        private Bitmap mDefaultWallpaper;
        private Handler mMainLooperHandler;
        private final IWallpaperManager mService;
        private final ArrayList<Pair<OnColorsChangedListener, Handler>> mColorListeners = new ArrayList<>();
        private ArrayMap<LocalWallpaperColorConsumer, ArraySet<RectF>> mLocalColorCallbackAreas = new ArrayMap<>();
        private ILocalWallpaperColorConsumer mLocalColorCallback = new ILocalWallpaperColorConsumer.Stub() { // from class: android.app.WallpaperManager.Globals.1
            @Override // android.app.ILocalWallpaperColorConsumer
            public void onColorsChanged(RectF area, WallpaperColors colors) {
                for (LocalWallpaperColorConsumer callback : Globals.this.mLocalColorCallbackAreas.keySet()) {
                    ArraySet<RectF> areas = (ArraySet) Globals.this.mLocalColorCallbackAreas.get(callback);
                    if (areas != null && areas.contains(area)) {
                        callback.onColorsChanged(area, colors);
                    }
                }
            }
        };

        Globals(IWallpaperManager service, Looper looper) {
            this.mService = service;
            this.mMainLooperHandler = new Handler(looper);
            forgetLoadedWallpaper();
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperChanged() {
            forgetLoadedWallpaper();
        }

        public void addOnColorsChangedListener(OnColorsChangedListener callback, Handler handler, int userId, int displayId) {
            synchronized (this) {
                if (!this.mColorCallbackRegistered) {
                    try {
                        this.mService.registerWallpaperColorsCallback(this, userId, displayId);
                        this.mColorCallbackRegistered = true;
                    } catch (RemoteException e) {
                        Log.m103w(WallpaperManager.TAG, "Can't register for color updates", e);
                    }
                }
                this.mColorListeners.add(new Pair<>(callback, handler));
            }
        }

        public void addOnColorsChangedListener(LocalWallpaperColorConsumer callback, List<RectF> regions, int which, int userId, int displayId) {
            synchronized (this) {
                for (RectF area : regions) {
                    ArraySet<RectF> areas = this.mLocalColorCallbackAreas.get(callback);
                    if (areas == null) {
                        areas = new ArraySet<>();
                        this.mLocalColorCallbackAreas.put(callback, areas);
                    }
                    areas.add(area);
                }
                try {
                    this.mService.addOnLocalColorsChangedListener(this.mLocalColorCallback, regions, which, userId, displayId);
                } catch (RemoteException e) {
                    Log.m109e(WallpaperManager.TAG, "Can't register for local color updates", e);
                }
            }
        }

        public void removeOnColorsChangedListener(LocalWallpaperColorConsumer callback, int which, int userId, int displayId) {
            synchronized (this) {
                ArraySet<RectF> removeAreas = this.mLocalColorCallbackAreas.remove(callback);
                if (removeAreas != null && removeAreas.size() != 0) {
                    for (LocalWallpaperColorConsumer cb : this.mLocalColorCallbackAreas.keySet()) {
                        ArraySet<RectF> areas = this.mLocalColorCallbackAreas.get(cb);
                        if (areas != null && cb != callback) {
                            removeAreas.removeAll((ArraySet<? extends RectF>) areas);
                        }
                    }
                    try {
                        if (removeAreas.size() > 0) {
                            this.mService.removeOnLocalColorsChangedListener(this.mLocalColorCallback, new ArrayList(removeAreas), which, userId, displayId);
                        }
                    } catch (RemoteException e) {
                        Log.m109e(WallpaperManager.TAG, "Can't unregister for local color updates", e);
                    }
                }
            }
        }

        public void removeOnColorsChangedListener(final OnColorsChangedListener callback, int userId, int displayId) {
            synchronized (this) {
                this.mColorListeners.removeIf(new Predicate() { // from class: android.app.WallpaperManager$Globals$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return WallpaperManager.Globals.lambda$removeOnColorsChangedListener$0(WallpaperManager.OnColorsChangedListener.this, (Pair) obj);
                    }
                });
                if (this.mColorListeners.size() == 0 && this.mColorCallbackRegistered) {
                    this.mColorCallbackRegistered = false;
                    try {
                        this.mService.unregisterWallpaperColorsCallback(this, userId, displayId);
                    } catch (RemoteException e) {
                        Log.m103w(WallpaperManager.TAG, "Can't unregister color updates", e);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$removeOnColorsChangedListener$0(OnColorsChangedListener callback, Pair pair) {
            return pair.first == callback;
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperColorsChanged(final WallpaperColors colors, final int which, final int userId) {
            Handler handler;
            synchronized (this) {
                Iterator<Pair<OnColorsChangedListener, Handler>> it = this.mColorListeners.iterator();
                while (it.hasNext()) {
                    final Pair<OnColorsChangedListener, Handler> listener = it.next();
                    Handler handler2 = listener.second;
                    if (listener.second != null) {
                        handler = handler2;
                    } else {
                        Handler handler3 = this.mMainLooperHandler;
                        handler = handler3;
                    }
                    handler.post(new Runnable() { // from class: android.app.WallpaperManager$Globals$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WallpaperManager.Globals.this.lambda$onWallpaperColorsChanged$1(listener, colors, which, userId);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWallpaperColorsChanged$1(Pair listener, WallpaperColors colors, int which, int userId) {
            boolean stillExists;
            synchronized (WallpaperManager.sGlobals) {
                stillExists = this.mColorListeners.contains(listener);
            }
            if (stillExists) {
                ((OnColorsChangedListener) listener.first).onColorsChanged(colors, which, userId);
            }
        }

        WallpaperColors getWallpaperColors(int which, int userId, int displayId) {
            WallpaperManager.checkExactlyOneWallpaperFlagSet(which);
            try {
                return this.mService.getWallpaperColors(which, userId, displayId);
            } catch (RemoteException e) {
                return null;
            }
        }

        public Bitmap peekWallpaperBitmap(Context context, boolean returnDefault, int which, ColorManagementProxy cmProxy) {
            return peekWallpaperBitmap(context, returnDefault, which, context.getUserId(), false, cmProxy);
        }

        public Bitmap peekWallpaperBitmap(Context context, boolean returnDefault, int which, int userId, boolean hardware, ColorManagementProxy cmProxy) {
            IWallpaperManager iWallpaperManager = this.mService;
            if (iWallpaperManager != null) {
                try {
                    if (!iWallpaperManager.isWallpaperSupported(context.getOpPackageName())) {
                        return null;
                    }
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            synchronized (this) {
                CachedWallpaper cachedWallpaper = this.mCachedWallpaper;
                if (cachedWallpaper != null && cachedWallpaper.isValid(userId, which)) {
                    return this.mCachedWallpaper.mCachedWallpaper;
                }
                this.mCachedWallpaper = null;
                Bitmap currentWallpaper = null;
                try {
                    currentWallpaper = getCurrentWallpaperLocked(context, which, userId, hardware, cmProxy);
                } catch (OutOfMemoryError e2) {
                    Log.m104w(WallpaperManager.TAG, "Out of memory loading the current wallpaper: " + e2);
                } catch (SecurityException e3) {
                    if (context.getApplicationInfo().targetSdkVersion >= 27) {
                        throw e3;
                    }
                    Log.m104w(WallpaperManager.TAG, "No permission to access wallpaper, suppressing exception to avoid crashing legacy app.");
                }
                if (currentWallpaper != null) {
                    this.mCachedWallpaper = new CachedWallpaper(currentWallpaper, userId, which);
                    return currentWallpaper;
                } else if (returnDefault) {
                    Bitmap defaultWallpaper = this.mDefaultWallpaper;
                    if (defaultWallpaper == null || defaultWallpaper.isRecycled()) {
                        defaultWallpaper = getDefaultWallpaper(context, which);
                        synchronized (this) {
                            this.mDefaultWallpaper = defaultWallpaper;
                        }
                    }
                    return defaultWallpaper;
                } else {
                    return null;
                }
            }
        }

        public Rect peekWallpaperDimensions(Context context, boolean returnDefault, int which, int userId) {
            InputStream is;
            IWallpaperManager iWallpaperManager = this.mService;
            if (iWallpaperManager != null) {
                try {
                    if (!iWallpaperManager.isWallpaperSupported(context.getOpPackageName())) {
                        return new Rect();
                    }
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            Rect dimensions = null;
            synchronized (this) {
                try {
                    Bundle params = new Bundle();
                    try {
                        ParcelFileDescriptor pfd = this.mService.getWallpaperWithFeature(context.getOpPackageName(), context.getAttributionTag(), this, which, params, userId, true);
                        if (pfd != null) {
                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
                                dimensions = new Rect(0, 0, options.outWidth, options.outHeight);
                            } catch (Throwable th) {
                                if (pfd != null) {
                                    try {
                                        pfd.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                }
                                throw th;
                            }
                        }
                        if (pfd != null) {
                            pfd.close();
                        }
                    } catch (RemoteException ex) {
                        Log.m103w(WallpaperManager.TAG, "peek wallpaper dimensions failed", ex);
                    } catch (IOException e2) {
                    }
                } catch (Throwable th3) {
                    th = th3;
                    while (true) {
                        try {
                            break;
                        } catch (Throwable th4) {
                            th = th4;
                        }
                    }
                    throw th;
                }
            }
            if ((dimensions == null || dimensions.width() == 0 || dimensions.height() == 0) && returnDefault && (is = WallpaperManager.openDefaultWallpaper(context, which)) != null) {
                try {
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is, null, options2);
                    return new Rect(0, 0, options2.outWidth, options2.outHeight);
                } finally {
                    IoUtils.closeQuietly(is);
                }
            }
            return dimensions;
        }

        void forgetLoadedWallpaper() {
            synchronized (this) {
                this.mCachedWallpaper = null;
                this.mDefaultWallpaper = null;
            }
        }

        private Bitmap getCurrentWallpaperLocked(Context context, int which, int userId, final boolean hardware, final ColorManagementProxy cmProxy) {
            if (this.mService == null) {
                Log.m104w(WallpaperManager.TAG, "WallpaperService not running");
                return null;
            }
            try {
                Bundle params = new Bundle();
                ParcelFileDescriptor pfd = this.mService.getWallpaperWithFeature(context.getOpPackageName(), context.getAttributionTag(), this, which, params, userId, true);
                try {
                    if (pfd != null) {
                        try {
                            BufferedInputStream bis = new BufferedInputStream(new ParcelFileDescriptor.AutoCloseInputStream(pfd));
                            try {
                                try {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    while (true) {
                                        int data = bis.read();
                                        if (data != -1) {
                                            baos.write(data);
                                        } else {
                                            ImageDecoder.Source src = ImageDecoder.createSource(baos.toByteArray());
                                            try {
                                                Bitmap decodeBitmap = ImageDecoder.decodeBitmap(src, new ImageDecoder.OnHeaderDecodedListener() { // from class: android.app.WallpaperManager$Globals$$ExternalSyntheticLambda2
                                                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                                                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                                                        WallpaperManager.Globals.lambda$getCurrentWallpaperLocked$2(hardware, cmProxy, imageDecoder, imageInfo, source);
                                                    }
                                                });
                                                bis.close();
                                                return decodeBitmap;
                                            } catch (Throwable th) {
                                                th = th;
                                                Throwable th2 = th;
                                                try {
                                                    bis.close();
                                                } catch (Throwable th3) {
                                                    th2.addSuppressed(th3);
                                                }
                                                throw th2;
                                            }
                                        }
                                    }
                                } catch (Throwable th4) {
                                    th = th4;
                                }
                            } catch (IOException | OutOfMemoryError e) {
                                e = e;
                                Log.m103w(WallpaperManager.TAG, "Can't decode file", e);
                                return null;
                            }
                        } catch (IOException | OutOfMemoryError e2) {
                            e = e2;
                        }
                    }
                    return null;
                } catch (RemoteException e3) {
                    e = e3;
                    throw e.rethrowFromSystemServer();
                }
            } catch (RemoteException e4) {
                e = e4;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$getCurrentWallpaperLocked$2(boolean hardware, ColorManagementProxy cmProxy, ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source source) {
            decoder.setMutableRequired(!hardware);
            if (cmProxy != null) {
                cmProxy.doColorManagement(decoder, info);
            }
        }

        private Bitmap getDefaultWallpaper(Context context, int which) {
            InputStream is = WallpaperManager.openDefaultWallpaper(context, which);
            if (is != null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    return BitmapFactory.decodeStream(is, null, options);
                } catch (OutOfMemoryError e) {
                    Log.m103w(WallpaperManager.TAG, "Can't decode stream", e);
                } finally {
                    IoUtils.closeQuietly(is);
                }
            }
            return null;
        }
    }

    static void initGlobals(IWallpaperManager service, Looper looper) {
        synchronized (sSync) {
            if (sGlobals == null) {
                sGlobals = new Globals(service, looper);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WallpaperManager(IWallpaperManager service, Context context, Handler handler) {
        this.mWallpaperXStep = -1.0f;
        this.mWallpaperYStep = -1.0f;
        this.mContext = context;
        if (service != null) {
            initGlobals(service, context.getMainLooper());
        }
        this.mWcgEnabled = context.getResources().getConfiguration().isScreenWideColorGamut() && context.getResources().getBoolean(C4057R.bool.config_enableWcgMode);
        this.mCmProxy = new ColorManagementProxy(context);
        this.mLockscreenLiveWallpaper = context.getResources().getBoolean(C4057R.bool.config_independentLockscreenLiveWallpaper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WallpaperManager() {
        this.mWallpaperXStep = -1.0f;
        this.mWallpaperYStep = -1.0f;
        this.mContext = null;
        this.mCmProxy = null;
        this.mWcgEnabled = false;
        this.mLockscreenLiveWallpaper = false;
    }

    public static WallpaperManager getInstance(Context context) {
        return (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
    }

    public IWallpaperManager getIWallpaperManager() {
        return sGlobals.mService;
    }

    public boolean isLockscreenLiveWallpaperEnabled() {
        return this.mLockscreenLiveWallpaper;
    }

    public boolean shouldEnableWideColorGamut() {
        return this.mWcgEnabled;
    }

    public Drawable getDrawable() {
        return getDrawable(1);
    }

    public Drawable getDrawable(int which) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true, which, cmProxy);
        if (bm != null) {
            Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
            dr.setDither(false);
            return dr;
        }
        return null;
    }

    public Drawable getBuiltInDrawable() {
        return getBuiltInDrawable(0, 0, false, 0.0f, 0.0f, 1);
    }

    public Drawable getBuiltInDrawable(int which) {
        return getBuiltInDrawable(0, 0, false, 0.0f, 0.0f, which);
    }

    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment) {
        return getBuiltInDrawable(outWidth, outHeight, scaleToFit, horizontalAlignment, verticalAlignment, 1);
    }

    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [android.graphics.Rect, android.graphics.BitmapFactory$Options] */
    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment, int which) {
        ?? r1;
        InputStream is;
        boolean z;
        int outWidth2;
        RectF cropRectF;
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        checkExactlyOneWallpaperFlagSet(which);
        Resources resources = this.mContext.getResources();
        float horizontalAlignment2 = Math.max(0.0f, Math.min(1.0f, horizontalAlignment));
        float verticalAlignment2 = Math.max(0.0f, Math.min(1.0f, verticalAlignment));
        InputStream wpStream = openDefaultWallpaper(this.mContext, which);
        if (wpStream == null) {
            return null;
        }
        InputStream is2 = new BufferedInputStream(wpStream);
        if (outWidth <= 0) {
            r1 = 0;
        } else if (outHeight <= 0) {
            r1 = 0;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is2, null, options);
            if (options.outWidth != 0 && options.outHeight != 0) {
                int inWidth = options.outWidth;
                int inHeight = options.outHeight;
                InputStream is3 = new BufferedInputStream(openDefaultWallpaper(this.mContext, which));
                int outWidth3 = Math.min(inWidth, outWidth);
                int outHeight2 = Math.min(inHeight, outHeight);
                if (scaleToFit) {
                    is = is3;
                    z = true;
                    outWidth2 = outWidth3;
                    cropRectF = getMaxCropRect(inWidth, inHeight, outWidth3, outHeight2, horizontalAlignment2, verticalAlignment2);
                } else {
                    is = is3;
                    z = true;
                    outWidth2 = outWidth3;
                    float left = (inWidth - outWidth2) * horizontalAlignment2;
                    float right = outWidth2 + left;
                    float top = (inHeight - outHeight2) * verticalAlignment2;
                    float bottom = outHeight2 + top;
                    cropRectF = new RectF(left, top, right, bottom);
                }
                Rect roundedTrueCrop = new Rect();
                cropRectF.roundOut(roundedTrueCrop);
                if (roundedTrueCrop.width() > 0 && roundedTrueCrop.height() > 0) {
                    int scaleDownSampleSize = Math.min(roundedTrueCrop.width() / outWidth2, roundedTrueCrop.height() / outHeight2);
                    BitmapRegionDecoder decoder = null;
                    try {
                        decoder = BitmapRegionDecoder.newInstance(is, z);
                    } catch (IOException e) {
                        Log.m104w(TAG, "cannot open region decoder for default wallpaper");
                    }
                    Bitmap crop = null;
                    if (decoder != null) {
                        BitmapFactory.Options options2 = new BitmapFactory.Options();
                        if (scaleDownSampleSize > 1) {
                            options2.inSampleSize = scaleDownSampleSize;
                        }
                        crop = decoder.decodeRegion(roundedTrueCrop, options2);
                        decoder.recycle();
                    }
                    if (crop == null) {
                        InputStream is4 = new BufferedInputStream(openDefaultWallpaper(this.mContext, which));
                        BitmapFactory.Options options3 = new BitmapFactory.Options();
                        if (scaleDownSampleSize > 1) {
                            options3.inSampleSize = scaleDownSampleSize;
                        }
                        Bitmap fullSize = BitmapFactory.decodeStream(is4, null, options3);
                        if (fullSize != null) {
                            crop = Bitmap.createBitmap(fullSize, roundedTrueCrop.left, roundedTrueCrop.top, roundedTrueCrop.width(), roundedTrueCrop.height());
                        }
                    }
                    if (crop == null) {
                        Log.m104w(TAG, "cannot decode default wallpaper");
                        return null;
                    }
                    if (outWidth2 > 0 && outHeight2 > 0) {
                        if (crop.getWidth() != outWidth2 || crop.getHeight() != outHeight2) {
                            Matrix m = new Matrix();
                            RectF cropRect = new RectF(0.0f, 0.0f, crop.getWidth(), crop.getHeight());
                            float horizontalAlignment3 = outHeight2;
                            RectF returnRect = new RectF(0.0f, 0.0f, outWidth2, horizontalAlignment3);
                            m.setRectToRect(cropRect, returnRect, Matrix.ScaleToFit.FILL);
                            Bitmap tmp = Bitmap.createBitmap((int) returnRect.width(), (int) returnRect.height(), Bitmap.Config.ARGB_8888);
                            if (tmp != null) {
                                Canvas c = new Canvas(tmp);
                                Paint p = new Paint();
                                p.setFilterBitmap(true);
                                c.drawBitmap(crop, m, p);
                                crop = tmp;
                            }
                        }
                    }
                    return new BitmapDrawable(resources, crop);
                }
                Log.m104w(TAG, "crop has bad values for full size image");
                return null;
            }
            Log.m110e(TAG, "default wallpaper dimensions are 0");
            return null;
        }
        return new BitmapDrawable(resources, BitmapFactory.decodeStream(is2, r1, r1));
    }

    private static RectF getMaxCropRect(int inWidth, int inHeight, int outWidth, int outHeight, float horizontalAlignment, float verticalAlignment) {
        RectF cropRect = new RectF();
        if (inWidth / inHeight > outWidth / outHeight) {
            cropRect.top = 0.0f;
            cropRect.bottom = inHeight;
            float cropWidth = outWidth * (inHeight / outHeight);
            cropRect.left = (inWidth - cropWidth) * horizontalAlignment;
            cropRect.right = cropRect.left + cropWidth;
        } else {
            cropRect.left = 0.0f;
            cropRect.right = inWidth;
            float cropHeight = outHeight * (inWidth / outWidth);
            cropRect.top = (inHeight - cropHeight) * verticalAlignment;
            cropRect.bottom = cropRect.top + cropHeight;
        }
        return cropRect;
    }

    public Drawable peekDrawable() {
        return peekDrawable(1);
    }

    public Drawable peekDrawable(int which) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false, which, cmProxy);
        if (bm != null) {
            Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
            dr.setDither(false);
            return dr;
        }
        return null;
    }

    public Drawable getFastDrawable() {
        return getFastDrawable(1);
    }

    public Drawable getFastDrawable(int which) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true, which, cmProxy);
        if (bm == null) {
            return null;
        }
        return new FastBitmapDrawable(bm);
    }

    public Drawable peekFastDrawable() {
        return peekFastDrawable(1);
    }

    public Drawable peekFastDrawable(int which) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false, which, cmProxy);
        if (bm == null) {
            return null;
        }
        return new FastBitmapDrawable(bm);
    }

    public boolean wallpaperSupportsWcg(int which) {
        ColorManagementProxy cmProxy;
        Bitmap bitmap;
        return (!shouldEnableWideColorGamut() || (bitmap = sGlobals.peekWallpaperBitmap(this.mContext, false, which, (cmProxy = getColorManagementProxy()))) == null || bitmap.getColorSpace() == null || bitmap.getColorSpace() == ColorSpace.get(ColorSpace.Named.SRGB) || !cmProxy.isSupportedColorSpace(bitmap.getColorSpace())) ? false : true;
    }

    public Bitmap getBitmap() {
        return getBitmap(false);
    }

    public Bitmap getBitmap(boolean hardware) {
        return getBitmapAsUser(this.mContext.getUserId(), hardware);
    }

    public Bitmap getBitmap(boolean hardware, int which) {
        return getBitmapAsUser(this.mContext.getUserId(), hardware, which);
    }

    public Bitmap getBitmapAsUser(int userId, boolean hardware) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        return sGlobals.peekWallpaperBitmap(this.mContext, true, 1, userId, hardware, cmProxy);
    }

    public Bitmap getBitmapAsUser(int userId, boolean hardware, int which) {
        ColorManagementProxy cmProxy = getColorManagementProxy();
        return sGlobals.peekWallpaperBitmap(this.mContext, true, which, userId, hardware, cmProxy);
    }

    public Rect peekBitmapDimensions() {
        return peekBitmapDimensions(1);
    }

    public Rect peekBitmapDimensions(int which) {
        checkExactlyOneWallpaperFlagSet(which);
        Globals globals = sGlobals;
        Context context = this.mContext;
        return globals.peekWallpaperDimensions(context, true, which, context.getUserId());
    }

    public ParcelFileDescriptor getWallpaperFile(int which) {
        return getWallpaperFile(which, this.mContext.getUserId());
    }

    public void addOnColorsChangedListener(OnColorsChangedListener listener, Handler handler) {
        addOnColorsChangedListener(listener, handler, this.mContext.getUserId());
    }

    public void addOnColorsChangedListener(OnColorsChangedListener listener, Handler handler, int userId) {
        sGlobals.addOnColorsChangedListener(listener, handler, userId, this.mContext.getDisplayId());
    }

    public void removeOnColorsChangedListener(OnColorsChangedListener callback) {
        removeOnColorsChangedListener(callback, this.mContext.getUserId());
    }

    public void removeOnColorsChangedListener(OnColorsChangedListener callback, int userId) {
        sGlobals.removeOnColorsChangedListener(callback, userId, this.mContext.getDisplayId());
    }

    public WallpaperColors getWallpaperColors(int which) {
        return getWallpaperColors(which, this.mContext.getUserId());
    }

    public WallpaperColors getWallpaperColors(int which, int userId) {
        StrictMode.assertUiContext(this.mContext, "getWallpaperColors");
        return sGlobals.getWallpaperColors(which, userId, this.mContext.getDisplayId());
    }

    public void addOnColorsChangedListener(LocalWallpaperColorConsumer callback, List<RectF> regions) throws IllegalArgumentException {
        for (RectF region : regions) {
            RectF rectF = LOCAL_COLOR_BOUNDS;
            if (!rectF.contains(region)) {
                throw new IllegalArgumentException("Regions must be within bounds " + rectF);
            }
        }
        sGlobals.addOnColorsChangedListener(callback, regions, 1, this.mContext.getUserId(), this.mContext.getDisplayId());
    }

    public void removeOnColorsChangedListener(LocalWallpaperColorConsumer callback) {
        sGlobals.removeOnColorsChangedListener(callback, 1, this.mContext.getUserId(), this.mContext.getDisplayId());
    }

    public ParcelFileDescriptor getWallpaperFile(int which, int userId) {
        return getWallpaperFile(which, userId, true);
    }

    public ParcelFileDescriptor getWallpaperFile(int which, boolean getCropped) {
        return getWallpaperFile(which, this.mContext.getUserId(), getCropped);
    }

    private ParcelFileDescriptor getWallpaperFile(int which, int userId, boolean getCropped) {
        checkExactlyOneWallpaperFlagSet(which);
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            Bundle outParams = new Bundle();
            return sGlobals.mService.getWallpaperWithFeature(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), null, which, outParams, userId, getCropped);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (SecurityException e2) {
            if (this.mContext.getApplicationInfo().targetSdkVersion < 27) {
                Log.m104w(TAG, "No permission to access wallpaper, suppressing exception to avoid crashing legacy app.");
                return null;
            }
            throw e2;
        }
    }

    public void forgetLoadedWallpaper() {
        sGlobals.forgetLoadedWallpaper();
    }

    public WallpaperInfo getWallpaperInfo() {
        return getWallpaperInfoForUser(this.mContext.getUserId());
    }

    public WallpaperInfo getWallpaperInfoForUser(int userId) {
        return getWallpaperInfo(1, userId);
    }

    public WallpaperInfo getWallpaperInfo(int which) {
        return getWallpaperInfo(which, this.mContext.getUserId());
    }

    public WallpaperInfo getWallpaperInfo(int which, int userId) {
        checkExactlyOneWallpaperFlagSet(which);
        try {
            if (sGlobals.mService == null) {
                Log.m104w(TAG, "WallpaperService not running");
                throw new RuntimeException(new DeadSystemException());
            }
            return sGlobals.mService.getWallpaperInfoWithFlags(which, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ParcelFileDescriptor getWallpaperInfoFile() {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.getWallpaperInfoFile(this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getWallpaperId(int which) {
        return getWallpaperIdForUser(which, this.mContext.getUserId());
    }

    public int getWallpaperIdForUser(int which, int userId) {
        try {
            if (sGlobals.mService == null) {
                Log.m104w(TAG, "WallpaperService not running");
                throw new RuntimeException(new DeadSystemException());
            }
            return sGlobals.mService.getWallpaperIdForUser(which, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Intent getCropAndSetWallpaperIntent(Uri imageUri) {
        if (imageUri == null) {
            throw new IllegalArgumentException("Image URI must not be null");
        }
        if (!"content".equals(imageUri.getScheme())) {
            throw new IllegalArgumentException("Image URI must be of the content scheme type");
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent cropAndSetWallpaperIntent = new Intent(ACTION_CROP_AND_SET_WALLPAPER, imageUri);
        cropAndSetWallpaperIntent.addFlags(1);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolvedHome = packageManager.resolveActivity(homeIntent, 65536);
        if (resolvedHome != null) {
            cropAndSetWallpaperIntent.setPackage(resolvedHome.activityInfo.packageName);
            List<ResolveInfo> cropAppList = packageManager.queryIntentActivities(cropAndSetWallpaperIntent, 0);
            if (cropAppList.size() > 0) {
                return cropAndSetWallpaperIntent;
            }
        }
        String cropperPackage = this.mContext.getString(C4057R.string.config_wallpaperCropperPackage);
        cropAndSetWallpaperIntent.setPackage(cropperPackage);
        List<ResolveInfo> cropAppList2 = packageManager.queryIntentActivities(cropAndSetWallpaperIntent, 0);
        if (cropAppList2.size() > 0) {
            return cropAndSetWallpaperIntent;
        }
        throw new IllegalArgumentException("Cannot use passed URI to set wallpaper; check that the type returned by ContentProvider matches image/*");
    }

    public void setResource(int resid) throws IOException {
        setResource(resid, 3);
    }

    public int setResource(int resid, int which) throws IOException {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        Bundle result = new Bundle();
        WallpaperSetCompletion completion = new WallpaperSetCompletion();
        try {
            Resources resources = this.mContext.getResources();
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper("res:" + resources.getResourceName(resid), this.mContext.getOpPackageName(), null, false, result, which, completion, this.mContext.getUserId());
            if (fd != null) {
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                copyStreamToWallpaperFile(resources.openRawResource(resid), fos);
                fos.close();
                completion.waitForCompletion();
                IoUtils.closeQuietly(fos);
            }
            return result.getInt(EXTRA_NEW_WALLPAPER_ID, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setBitmap(Bitmap bitmap) throws IOException {
        setBitmap(bitmap, null, true);
    }

    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup) throws IOException {
        return setBitmap(fullImage, visibleCropHint, allowBackup, 3);
    }

    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup, int which) throws IOException {
        return setBitmap(fullImage, visibleCropHint, allowBackup, which, this.mContext.getUserId());
    }

    public int setBitmap(Bitmap fullImage, Rect visibleCropHint, boolean allowBackup, int which, int userId) throws IOException {
        validateRect(visibleCropHint);
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        Bundle result = new Bundle();
        WallpaperSetCompletion completion = new WallpaperSetCompletion();
        try {
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null, this.mContext.getOpPackageName(), visibleCropHint, allowBackup, result, which, completion, userId);
            if (fd != null) {
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                fullImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                completion.waitForCompletion();
                IoUtils.closeQuietly(fos);
            }
            return result.getInt(EXTRA_NEW_WALLPAPER_ID, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private final void validateRect(Rect rect) {
        if (rect != null && rect.isEmpty()) {
            throw new IllegalArgumentException("visibleCrop rectangle must be valid and non-empty");
        }
    }

    public void setStream(InputStream bitmapData) throws IOException {
        setStream(bitmapData, null, true);
    }

    private void copyStreamToWallpaperFile(InputStream data, FileOutputStream fos) throws IOException {
        FileUtils.copy(data, fos);
    }

    public int setStream(InputStream bitmapData, Rect visibleCropHint, boolean allowBackup) throws IOException {
        return setStream(bitmapData, visibleCropHint, allowBackup, 3);
    }

    public int setStream(InputStream bitmapData, Rect visibleCropHint, boolean allowBackup, int which) throws IOException {
        validateRect(visibleCropHint);
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        Bundle result = new Bundle();
        WallpaperSetCompletion completion = new WallpaperSetCompletion();
        try {
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null, this.mContext.getOpPackageName(), visibleCropHint, allowBackup, result, which, completion, this.mContext.getUserId());
            if (fd != null) {
                FileOutputStream fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
                copyStreamToWallpaperFile(bitmapData, fos);
                fos.close();
                completion.waitForCompletion();
                IoUtils.closeQuietly(fos);
            }
            return result.getInt(EXTRA_NEW_WALLPAPER_ID, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasResourceWallpaper(int resid) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            Resources resources = this.mContext.getResources();
            String name = "res:" + resources.getResourceName(resid);
            return sGlobals.mService.hasNamedWallpaper(name);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDesiredMinimumWidth() {
        StrictMode.assertUiContext(this.mContext, "getDesiredMinimumWidth");
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.getWidthHint(this.mContext.getDisplayId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDesiredMinimumHeight() {
        StrictMode.assertUiContext(this.mContext, "getDesiredMinimumHeight");
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.getHeightHint(this.mContext.getDisplayId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void suggestDesiredDimensions(int minimumWidth, int minimumHeight) {
        int maximumTextureSize;
        StrictMode.assertUiContext(this.mContext, "suggestDesiredDimensions");
        try {
            maximumTextureSize = SystemProperties.getInt("sys.max_texture_size", 0);
        } catch (Exception e) {
            maximumTextureSize = 0;
        }
        if (maximumTextureSize > 0 && (minimumWidth > maximumTextureSize || minimumHeight > maximumTextureSize)) {
            float aspect = minimumHeight / minimumWidth;
            if (minimumWidth > minimumHeight) {
                minimumWidth = maximumTextureSize;
                minimumHeight = (int) ((minimumWidth * aspect) + 0.5d);
            } else {
                minimumHeight = maximumTextureSize;
                minimumWidth = (int) ((minimumHeight / aspect) + 0.5d);
            }
        }
        try {
            if (sGlobals.mService == null) {
                Log.m104w(TAG, "WallpaperService not running");
                throw new RuntimeException(new DeadSystemException());
            } else {
                sGlobals.mService.setDimensionHints(minimumWidth, minimumHeight, this.mContext.getOpPackageName(), this.mContext.getDisplayId());
            }
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void setDisplayPadding(Rect padding) {
        StrictMode.assertUiContext(this.mContext, "setDisplayPadding");
        try {
            if (sGlobals.mService == null) {
                Log.m104w(TAG, "WallpaperService not running");
                throw new RuntimeException(new DeadSystemException());
            } else {
                sGlobals.mService.setDisplayPadding(padding, this.mContext.getOpPackageName(), this.mContext.getDisplayId());
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setDisplayOffset(IBinder windowToken, int x, int y) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperDisplayOffset(windowToken, x, y);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearWallpaper() {
        clearWallpaper(2, this.mContext.getUserId());
        clearWallpaper(1, this.mContext.getUserId());
    }

    @SystemApi
    public void clearWallpaper(int which, int userId) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            sGlobals.mService.clearWallpaper(this.mContext.getOpPackageName(), which, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean setWallpaperComponent(ComponentName name) {
        return setWallpaperComponent(name, this.mContext.getUserId());
    }

    @SystemApi
    public void setWallpaperDimAmount(float dimAmount) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            sGlobals.mService.setWallpaperDimAmount(MathUtils.saturate(dimAmount));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public float getWallpaperDimAmount() {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.getWallpaperDimAmount();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean lockScreenWallpaperExists() {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.lockScreenWallpaperExists();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setWallpaperComponent(ComponentName name, int userId) {
        return setWallpaperComponentWithFlags(name, 3, userId);
    }

    @SystemApi
    public boolean setWallpaperComponentWithFlags(ComponentName name, int which) {
        return setWallpaperComponentWithFlags(name, which, this.mContext.getUserId());
    }

    public boolean setWallpaperComponentWithFlags(ComponentName name, int which, int userId) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperManagerService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            sGlobals.mService.setWallpaperComponentChecked(name, this.mContext.getOpPackageName(), which, userId);
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setWallpaperOffsets(IBinder windowToken, float xOffset, float yOffset) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, xOffset, yOffset, this.mWallpaperXStep, this.mWallpaperYStep);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setWallpaperOffsetSteps(float xStep, float yStep) {
        this.mWallpaperXStep = xStep;
        this.mWallpaperYStep = yStep;
    }

    public void sendWallpaperCommand(IBinder windowToken, String action, int x, int y, int z, Bundle extras) {
        try {
            WindowManagerGlobal.getWindowSession().sendWallpaperCommand(windowToken, action, x, y, z, extras, false);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setWallpaperZoomOut(IBinder windowToken, float zoom) {
        if (zoom < 0.0f || zoom > 1.0f) {
            throw new IllegalArgumentException("zoom must be between 0 and 1: " + zoom);
        }
        if (windowToken == null) {
            throw new IllegalArgumentException("windowToken must not be null");
        }
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperZoomOut(windowToken, zoom);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isWallpaperSupported() {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.isWallpaperSupported(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isSetWallpaperAllowed() {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.isSetWallpaperAllowed(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearWallpaperOffsets(IBinder windowToken) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, -1.0f, -1.0f, -1.0f, -1.0f);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clear() throws IOException {
        setStream(openDefaultWallpaper(this.mContext, 1), null, false);
    }

    public void clear(int which) throws IOException {
        if ((which & 1) != 0) {
            clear();
        }
        if ((which & 2) != 0) {
            clearWallpaper(2, this.mContext.getUserId());
        }
    }

    public static InputStream openDefaultWallpaper(Context context, int which) {
        if (which == 2) {
            return null;
        }
        String path = SystemProperties.get(PROP_WALLPAPER);
        InputStream wallpaperInputStream = getWallpaperInputStream(path);
        if (wallpaperInputStream != null) {
            return wallpaperInputStream;
        }
        String cmfPath = getCmfWallpaperPath();
        InputStream cmfWallpaperInputStream = getWallpaperInputStream(cmfPath);
        if (cmfWallpaperInputStream == null) {
            try {
                return context.getResources().openRawResource(C4057R.C4058drawable.default_wallpaper);
            } catch (Resources.NotFoundException e) {
                return null;
            }
        }
        return cmfWallpaperInputStream;
    }

    private static InputStream getWallpaperInputStream(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (IOException e) {
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    private static String getCmfWallpaperPath() {
        return Environment.getProductDirectory() + WALLPAPER_CMF_PATH + "default_wallpaper_" + VALUE_CMF_COLOR;
    }

    public static ComponentName getDefaultWallpaperComponent(Context context) {
        ComponentName cn = null;
        String flat = SystemProperties.get(PROP_WALLPAPER_COMPONENT);
        if (!TextUtils.isEmpty(flat)) {
            cn = ComponentName.unflattenFromString(flat);
        }
        if (cn == null) {
            String flat2 = context.getString(C4057R.string.default_wallpaper_component);
            if (!TextUtils.isEmpty(flat2)) {
                cn = ComponentName.unflattenFromString(flat2);
            }
        }
        if (cn != null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                packageManager.getPackageInfo(cn.getPackageName(), 786432);
                return cn;
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        return cn;
    }

    public boolean setLockWallpaperCallback(IWallpaperManagerCallback callback) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.setLockWallpaperCallback(callback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isWallpaperBackupEligible(int which) {
        if (sGlobals.mService == null) {
            Log.m104w(TAG, "WallpaperService not running");
            throw new RuntimeException(new DeadSystemException());
        }
        try {
            return sGlobals.mService.isWallpaperBackupEligible(which, this.mContext.getUserId());
        } catch (RemoteException e) {
            Log.m110e(TAG, "Exception querying wallpaper backup eligibility: " + e.getMessage());
            return false;
        }
    }

    public ColorManagementProxy getColorManagementProxy() {
        return this.mCmProxy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkExactlyOneWallpaperFlagSet(int which) {
        if (which == 1 || which == 2) {
            return;
        }
        throw new IllegalArgumentException("Must specify exactly one kind of wallpaper");
    }

    /* loaded from: classes.dex */
    public static class ColorManagementProxy {
        private final Set<ColorSpace> mSupportedColorSpaces;

        public ColorManagementProxy(Context context) {
            HashSet hashSet = new HashSet();
            this.mSupportedColorSpaces = hashSet;
            Display display = context.getDisplayNoVerify();
            if (display != null) {
                hashSet.addAll(Arrays.asList(display.getSupportedWideColorGamut()));
            }
        }

        public Set<ColorSpace> getSupportedColorSpaces() {
            return this.mSupportedColorSpaces;
        }

        boolean isSupportedColorSpace(ColorSpace colorSpace) {
            return colorSpace != null && (colorSpace == ColorSpace.get(ColorSpace.Named.SRGB) || getSupportedColorSpaces().contains(colorSpace));
        }

        void doColorManagement(ImageDecoder decoder, ImageDecoder.ImageInfo info) {
            if (!isSupportedColorSpace(info.getColorSpace())) {
                decoder.setTargetColorSpace(ColorSpace.get(ColorSpace.Named.SRGB));
                Log.m104w(WallpaperManager.TAG, "Not supported color space: " + info.getColorSpace());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WallpaperSetCompletion extends IWallpaperManagerCallback.Stub {
        final CountDownLatch mLatch = new CountDownLatch(1);

        public WallpaperSetCompletion() {
        }

        public void waitForCompletion() {
            try {
                boolean completed = this.mLatch.await(30L, TimeUnit.SECONDS);
                if (completed) {
                    Log.m112d(WallpaperManager.TAG, "Wallpaper set completion.");
                } else {
                    Log.m112d(WallpaperManager.TAG, "Timeout waiting for wallpaper set completion!");
                }
            } catch (InterruptedException e) {
            }
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperChanged() throws RemoteException {
            this.mLatch.countDown();
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperColorsChanged(WallpaperColors colors, int which, int userId) throws RemoteException {
            WallpaperManager.sGlobals.onWallpaperColorsChanged(colors, which, userId);
        }
    }

    /* loaded from: classes.dex */
    public interface OnColorsChangedListener {
        void onColorsChanged(WallpaperColors wallpaperColors, int i);

        default void onColorsChanged(WallpaperColors colors, int which, int userId) {
            onColorsChanged(colors, which);
        }
    }
}
