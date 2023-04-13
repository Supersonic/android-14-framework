package com.android.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.util.Objects;
import java.util.function.Consumer;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class CachingIconView extends ImageView {
    private int mBackgroundColor;
    private int mDesiredVisibility;
    private boolean mForceHidden;
    private int mIconColor;
    private boolean mInternalSetDrawable;
    private String mLastPackage;
    private int mLastResId;
    private int mMaxDrawableHeight;
    private int mMaxDrawableWidth;
    private Consumer<Boolean> mOnForceHiddenChangedListener;
    private Consumer<Integer> mOnVisibilityChangedListener;
    private boolean mWillBeForceHidden;

    public CachingIconView(Context context) {
        this(context, null, 0, 0);
    }

    public CachingIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CachingIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CachingIconView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMaxDrawableWidth = -1;
        this.mMaxDrawableHeight = -1;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, C4057R.styleable.CachingIconView, defStyleAttr, defStyleRes);
        this.mMaxDrawableWidth = ta.getDimensionPixelSize(0, -1);
        this.mMaxDrawableHeight = ta.getDimensionPixelSize(1, -1);
        ta.recycle();
    }

    @Override // android.widget.ImageView
    @RemotableViewMethod(asyncImpl = "setImageIconAsync")
    public void setImageIcon(Icon icon) {
        if (!testAndSetCache(icon)) {
            this.mInternalSetDrawable = true;
            Drawable drawable = loadSizeRestrictedIcon(icon);
            if (drawable == null) {
                super.setImageIcon(icon);
            } else {
                super.setImageDrawable(drawable);
            }
            this.mInternalSetDrawable = false;
        }
    }

    private Drawable loadSizeRestrictedIcon(Icon icon) {
        return LocalImageResolver.resolveImage(icon, getContext(), this.mMaxDrawableWidth, this.mMaxDrawableHeight);
    }

    @Override // android.widget.ImageView
    public Runnable setImageIconAsync(Icon icon) {
        resetCache();
        final Drawable drawable = loadSizeRestrictedIcon(icon);
        if (drawable != null) {
            return new Runnable() { // from class: com.android.internal.widget.CachingIconView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CachingIconView.this.lambda$setImageIconAsync$0(drawable);
                }
            };
        }
        return super.setImageIconAsync(icon);
    }

    @Override // android.widget.ImageView
    @RemotableViewMethod(asyncImpl = "setImageResourceAsync")
    public void setImageResource(int resId) {
        if (!testAndSetCache(resId)) {
            this.mInternalSetDrawable = true;
            Drawable drawable = loadSizeRestrictedDrawable(resId);
            if (drawable == null) {
                super.setImageResource(resId);
            } else {
                super.setImageDrawable(drawable);
            }
            this.mInternalSetDrawable = false;
        }
    }

    private Drawable loadSizeRestrictedDrawable(int resId) {
        return LocalImageResolver.resolveImage(resId, getContext(), this.mMaxDrawableWidth, this.mMaxDrawableHeight);
    }

    @Override // android.widget.ImageView
    public Runnable setImageResourceAsync(int resId) {
        resetCache();
        final Drawable drawable = loadSizeRestrictedDrawable(resId);
        if (drawable != null) {
            return new Runnable() { // from class: com.android.internal.widget.CachingIconView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CachingIconView.this.lambda$setImageResourceAsync$1(drawable);
                }
            };
        }
        return super.setImageResourceAsync(resId);
    }

    @Override // android.widget.ImageView
    @RemotableViewMethod(asyncImpl = "setImageURIAsync")
    public void setImageURI(Uri uri) {
        resetCache();
        Drawable drawable = loadSizeRestrictedUri(uri);
        if (drawable == null) {
            super.setImageURI(uri);
            return;
        }
        this.mInternalSetDrawable = true;
        super.setImageDrawable(drawable);
        this.mInternalSetDrawable = false;
    }

    private Drawable loadSizeRestrictedUri(Uri uri) {
        return LocalImageResolver.resolveImage(uri, getContext(), this.mMaxDrawableWidth, this.mMaxDrawableHeight);
    }

    @Override // android.widget.ImageView
    public Runnable setImageURIAsync(Uri uri) {
        resetCache();
        final Drawable drawable = loadSizeRestrictedUri(uri);
        if (drawable == null) {
            return super.setImageURIAsync(uri);
        }
        return new Runnable() { // from class: com.android.internal.widget.CachingIconView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                CachingIconView.this.lambda$setImageURIAsync$2(drawable);
            }
        };
    }

    @Override // android.widget.ImageView, android.inputmethodservice.navigationbar.ButtonInterface
    /* renamed from: setImageDrawable */
    public void lambda$setImageURIAsync$2(Drawable drawable) {
        if (!this.mInternalSetDrawable) {
            resetCache();
        }
        super.setImageDrawable(drawable);
    }

    @Override // android.widget.ImageView
    @RemotableViewMethod
    public void setImageBitmap(Bitmap bm) {
        resetCache();
        super.setImageBitmap(bm);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetCache();
    }

    private synchronized boolean testAndSetCache(Icon icon) {
        boolean isCached = false;
        if (icon != null) {
            if (icon.getType() == 2) {
                String iconPackage = normalizeIconPackage(icon);
                if (this.mLastResId != 0 && icon.getResId() == this.mLastResId && Objects.equals(iconPackage, this.mLastPackage)) {
                    isCached = true;
                }
                this.mLastPackage = iconPackage;
                this.mLastResId = icon.getResId();
                return isCached;
            }
        }
        resetCache();
        return false;
    }

    private synchronized boolean testAndSetCache(int resId) {
        boolean isCached;
        if (resId != 0) {
            try {
                int i = this.mLastResId;
                if (i != 0) {
                    isCached = resId == i && this.mLastPackage == null;
                    this.mLastPackage = null;
                    this.mLastResId = resId;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        isCached = false;
        this.mLastPackage = null;
        this.mLastResId = resId;
        return isCached;
    }

    private String normalizeIconPackage(Icon icon) {
        if (icon == null) {
            return null;
        }
        String pkg = icon.getResPackage();
        if (TextUtils.isEmpty(pkg) || pkg.equals(this.mContext.getPackageName())) {
            return null;
        }
        return pkg;
    }

    private synchronized void resetCache() {
        this.mLastResId = 0;
        this.mLastPackage = null;
    }

    public void setForceHidden(boolean forceHidden) {
        if (forceHidden != this.mForceHidden) {
            this.mForceHidden = forceHidden;
            this.mWillBeForceHidden = false;
            updateVisibility();
            Consumer<Boolean> consumer = this.mOnForceHiddenChangedListener;
            if (consumer != null) {
                consumer.accept(Boolean.valueOf(forceHidden));
            }
        }
    }

    @Override // android.widget.ImageView, android.view.View
    @RemotableViewMethod
    public void setVisibility(int visibility) {
        this.mDesiredVisibility = visibility;
        updateVisibility();
    }

    private void updateVisibility() {
        int visibility = this.mDesiredVisibility;
        if (visibility == 0 && this.mForceHidden) {
            visibility = 4;
        }
        Consumer<Integer> consumer = this.mOnVisibilityChangedListener;
        if (consumer != null) {
            consumer.accept(Integer.valueOf(visibility));
        }
        super.setVisibility(visibility);
    }

    public void setOnVisibilityChangedListener(Consumer<Integer> listener) {
        this.mOnVisibilityChangedListener = listener;
    }

    public void setOnForceHiddenChangedListener(Consumer<Boolean> listener) {
        this.mOnForceHiddenChangedListener = listener;
    }

    public boolean isForceHidden() {
        return this.mForceHidden;
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    @RemotableViewMethod
    public void setOriginalIconColor(int color) {
        this.mIconColor = color;
        Drawable background = getBackground();
        Drawable icon = getDrawable();
        boolean hasColor = color != 1;
        if (background == null) {
            if (hasColor && icon != null) {
                icon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        } else if (hasColor) {
            background.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            if (icon != null) {
                icon.mutate().setColorFilter(this.mBackgroundColor, PorterDuff.Mode.SRC_ATOP);
            }
        } else {
            background.mutate().setColorFilter(this.mBackgroundColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setGrayedOut(boolean grayedOut) {
        Drawable drawable = getBackground();
        if (drawable == null) {
            drawable = getDrawable();
        }
        ColoredIconHelper.applyGrayTint(this.mContext, drawable, grayedOut, this.mIconColor);
    }

    public int getOriginalIconColor() {
        return this.mIconColor;
    }

    public boolean willBeForceHidden() {
        return this.mWillBeForceHidden;
    }

    public void setWillBeForceHidden(boolean forceHidden) {
        this.mWillBeForceHidden = forceHidden;
    }

    public int getMaxDrawableWidth() {
        return this.mMaxDrawableWidth;
    }

    public int getMaxDrawableHeight() {
        return this.mMaxDrawableHeight;
    }
}
