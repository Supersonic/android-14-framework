package com.android.internal.colorextraction;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.AsyncTask;
import android.p008os.Handler;
import android.util.SparseArray;
import com.android.internal.colorextraction.types.ExtractionType;
import com.android.internal.colorextraction.types.Tonal;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class ColorExtractor implements WallpaperManager.OnColorsChangedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "ColorExtractor";
    public static final int TYPE_DARK = 1;
    public static final int TYPE_EXTRA_DARK = 2;
    public static final int TYPE_NORMAL = 0;
    private static final int[] sGradientTypes = {0, 1, 2};
    private final Context mContext;
    private final ExtractionType mExtractionType;
    protected final SparseArray<GradientColors[]> mGradientColors;
    protected WallpaperColors mLockColors;
    private final ArrayList<WeakReference<OnColorsChangedListener>> mOnColorsChangedListeners;
    protected WallpaperColors mSystemColors;

    /* loaded from: classes4.dex */
    public interface OnColorsChangedListener {
        void onColorsChanged(ColorExtractor colorExtractor, int i);
    }

    public ColorExtractor(Context context) {
        this(context, new Tonal(context), true, (WallpaperManager) context.getSystemService(WallpaperManager.class));
    }

    public ColorExtractor(Context context, ExtractionType extractionType, boolean immediately, WallpaperManager wallpaperManager) {
        this.mContext = context;
        this.mExtractionType = extractionType;
        this.mGradientColors = new SparseArray<>();
        int[] iArr = {2, 1};
        for (int i = 0; i < 2; i++) {
            int which = iArr[i];
            int[] iArr2 = sGradientTypes;
            GradientColors[] colors = new GradientColors[iArr2.length];
            this.mGradientColors.append(which, colors);
            for (int type : iArr2) {
                colors[type] = new GradientColors();
            }
        }
        this.mOnColorsChangedListeners = new ArrayList<>();
        if (wallpaperManager.isWallpaperSupported()) {
            wallpaperManager.addOnColorsChangedListener(this, (Handler) null);
            initExtractColors(wallpaperManager, immediately);
        }
    }

    private void initExtractColors(WallpaperManager wallpaperManager, boolean immediately) {
        if (immediately) {
            this.mSystemColors = wallpaperManager.getWallpaperColors(1);
            this.mLockColors = wallpaperManager.getWallpaperColors(2);
            extractWallpaperColors();
            return;
        }
        new LoadWallpaperColors().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wallpaperManager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class LoadWallpaperColors extends AsyncTask<WallpaperManager, Void, Void> {
        private WallpaperColors mLockColors;
        private WallpaperColors mSystemColors;

        private LoadWallpaperColors() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public Void doInBackground(WallpaperManager... params) {
            this.mSystemColors = params[0].getWallpaperColors(1);
            this.mLockColors = params[0].getWallpaperColors(2);
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public void onPostExecute(Void b) {
            ColorExtractor.this.mSystemColors = this.mSystemColors;
            ColorExtractor.this.mLockColors = this.mLockColors;
            ColorExtractor.this.extractWallpaperColors();
            ColorExtractor.this.triggerColorsChanged(3);
        }
    }

    protected void extractWallpaperColors() {
        GradientColors[] systemColors = this.mGradientColors.get(1);
        GradientColors[] lockColors = this.mGradientColors.get(2);
        extractInto(this.mSystemColors, systemColors[0], systemColors[1], systemColors[2]);
        extractInto(this.mLockColors, lockColors[0], lockColors[1], lockColors[2]);
    }

    public GradientColors getColors(int which) {
        return getColors(which, 1);
    }

    public GradientColors getColors(int which, int type) {
        if (type != 0 && type != 1 && type != 2) {
            throw new IllegalArgumentException("type should be TYPE_NORMAL, TYPE_DARK or TYPE_EXTRA_DARK");
        }
        if (which != 2 && which != 1) {
            throw new IllegalArgumentException("which should be FLAG_SYSTEM or FLAG_NORMAL");
        }
        return this.mGradientColors.get(which)[type];
    }

    public WallpaperColors getWallpaperColors(int which) {
        if (which == 2) {
            return this.mLockColors;
        }
        if (which == 1) {
            return this.mSystemColors;
        }
        throw new IllegalArgumentException("Invalid value for which: " + which);
    }

    @Override // android.app.WallpaperManager.OnColorsChangedListener
    public void onColorsChanged(WallpaperColors colors, int which) {
        boolean changed = false;
        if ((which & 2) != 0) {
            this.mLockColors = colors;
            GradientColors[] lockColors = this.mGradientColors.get(2);
            extractInto(colors, lockColors[0], lockColors[1], lockColors[2]);
            changed = true;
        }
        if ((which & 1) != 0) {
            this.mSystemColors = colors;
            GradientColors[] systemColors = this.mGradientColors.get(1);
            extractInto(colors, systemColors[0], systemColors[1], systemColors[2]);
            changed = true;
        }
        if (changed) {
            triggerColorsChanged(which);
        }
    }

    protected void triggerColorsChanged(int which) {
        ArrayList<WeakReference<OnColorsChangedListener>> references = new ArrayList<>(this.mOnColorsChangedListeners);
        int size = references.size();
        for (int i = 0; i < size; i++) {
            WeakReference<OnColorsChangedListener> weakReference = references.get(i);
            OnColorsChangedListener listener = weakReference.get();
            if (listener == null) {
                this.mOnColorsChangedListeners.remove(weakReference);
            } else {
                listener.onColorsChanged(this, which);
            }
        }
    }

    private void extractInto(WallpaperColors inWallpaperColors, GradientColors outGradientColorsNormal, GradientColors outGradientColorsDark, GradientColors outGradientColorsExtraDark) {
        this.mExtractionType.extractInto(inWallpaperColors, outGradientColorsNormal, outGradientColorsDark, outGradientColorsExtraDark);
    }

    public void destroy() {
        WallpaperManager wallpaperManager = (WallpaperManager) this.mContext.getSystemService(WallpaperManager.class);
        if (wallpaperManager != null) {
            wallpaperManager.removeOnColorsChangedListener(this);
        }
    }

    public void addOnColorsChangedListener(OnColorsChangedListener listener) {
        this.mOnColorsChangedListeners.add(new WeakReference<>(listener));
    }

    public void removeOnColorsChangedListener(OnColorsChangedListener listener) {
        ArrayList<WeakReference<OnColorsChangedListener>> references = new ArrayList<>(this.mOnColorsChangedListeners);
        int size = references.size();
        for (int i = 0; i < size; i++) {
            WeakReference<OnColorsChangedListener> weakReference = references.get(i);
            if (weakReference.get() == listener) {
                this.mOnColorsChangedListeners.remove(weakReference);
                return;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class GradientColors {
        private int[] mColorPalette;
        private int mMainColor;
        private int mSecondaryColor;
        private boolean mSupportsDarkText;

        public void setMainColor(int mainColor) {
            this.mMainColor = mainColor;
        }

        public void setSecondaryColor(int secondaryColor) {
            this.mSecondaryColor = secondaryColor;
        }

        public void setColorPalette(int[] colorPalette) {
            this.mColorPalette = colorPalette;
        }

        public void setSupportsDarkText(boolean supportsDarkText) {
            this.mSupportsDarkText = supportsDarkText;
        }

        public void set(GradientColors other) {
            this.mMainColor = other.mMainColor;
            this.mSecondaryColor = other.mSecondaryColor;
            this.mColorPalette = other.mColorPalette;
            this.mSupportsDarkText = other.mSupportsDarkText;
        }

        public int getMainColor() {
            return this.mMainColor;
        }

        public int getSecondaryColor() {
            return this.mSecondaryColor;
        }

        public int[] getColorPalette() {
            return this.mColorPalette;
        }

        public boolean supportsDarkText() {
            return this.mSupportsDarkText;
        }

        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) {
                return false;
            }
            GradientColors other = (GradientColors) o;
            return other.mMainColor == this.mMainColor && other.mSecondaryColor == this.mSecondaryColor && other.mSupportsDarkText == this.mSupportsDarkText;
        }

        public int hashCode() {
            int code = this.mMainColor;
            return (((code * 31) + this.mSecondaryColor) * 31) + (!this.mSupportsDarkText ? 1 : 0);
        }

        public String toString() {
            return "GradientColors(" + Integer.toHexString(this.mMainColor) + ", " + Integer.toHexString(this.mSecondaryColor) + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
