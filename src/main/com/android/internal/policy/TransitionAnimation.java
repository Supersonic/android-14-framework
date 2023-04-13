package com.android.internal.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ResourceId;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.HardwareBuffer;
import android.media.Image;
import android.media.ImageReader;
import android.p008os.SystemProperties;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.window.ScreenCapture;
import com.android.internal.C4057R;
import com.android.internal.policy.AttributeCache;
import java.nio.ByteBuffer;
import java.util.List;
/* loaded from: classes4.dex */
public class TransitionAnimation {
    private static final int CLIP_REVEAL_TRANSLATION_Y_DP = 8;
    public static final int DEFAULT_APP_TRANSITION_DURATION = 336;
    private static final String DEFAULT_PACKAGE = "android";
    private static final int MAX_CLIP_REVEAL_TRANSITION_DURATION = 420;
    private static final float RECENTS_THUMBNAIL_FADEIN_FRACTION = 0.5f;
    private static final float RECENTS_THUMBNAIL_FADEOUT_FRACTION = 0.5f;
    private static final int THUMBNAIL_APP_TRANSITION_DURATION = 336;
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_DOWN = 2;
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_UP = 0;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_DOWN = 3;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_UP = 1;
    static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    public static final int WALLPAPER_TRANSITION_CLOSE = 2;
    public static final int WALLPAPER_TRANSITION_INTRA_CLOSE = 4;
    public static final int WALLPAPER_TRANSITION_INTRA_OPEN = 3;
    public static final int WALLPAPER_TRANSITION_NONE = 0;
    public static final int WALLPAPER_TRANSITION_OPEN = 1;
    private final int mClipRevealTranslationY;
    private final int mConfigShortAnimTime;
    private final Context mContext;
    private final boolean mDebug;
    private final Interpolator mDecelerateInterpolator;
    private final int mDefaultWindowAnimationStyleResId;
    private final Interpolator mFastOutLinearInInterpolator;
    private final Interpolator mLinearOutSlowInInterpolator;
    private final String mTag;
    private final LogDecelerateInterpolator mInterpolator = new LogDecelerateInterpolator(100, 0);
    private final Interpolator mTouchResponseInterpolator = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    private final Interpolator mClipHorizontalInterpolator = new PathInterpolator(0.0f, 0.0f, 0.4f, 1.0f);
    private final Rect mTmpFromClipRect = new Rect();
    private final Rect mTmpToClipRect = new Rect();
    private final Rect mTmpRect = new Rect();
    private final Interpolator mThumbnailFadeInInterpolator = new Interpolator() { // from class: com.android.internal.policy.TransitionAnimation$$ExternalSyntheticLambda0
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            float lambda$new$0;
            lambda$new$0 = TransitionAnimation.this.lambda$new$0(f);
            return lambda$new$0;
        }
    };
    private final Interpolator mThumbnailFadeOutInterpolator = new Interpolator() { // from class: com.android.internal.policy.TransitionAnimation$$ExternalSyntheticLambda1
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            float lambda$new$1;
            lambda$new$1 = TransitionAnimation.this.lambda$new$1(f);
            return lambda$new$1;
        }
    };
    private final boolean mGridLayoutRecentsEnabled = SystemProperties.getBoolean("ro.recents.grid", false);
    private final boolean mLowRamRecentsEnabled = ActivityManager.isLowRamDeviceStatic();

    public TransitionAnimation(Context context, boolean debug, String tag) {
        this.mContext = context;
        this.mDebug = debug;
        this.mTag = tag;
        this.mDecelerateInterpolator = AnimationUtils.loadInterpolator(context, 17563651);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mClipRevealTranslationY = (int) (context.getResources().getDisplayMetrics().density * 8.0f);
        this.mConfigShortAnimTime = context.getResources().getInteger(17694720);
        TypedArray windowStyle = context.getTheme().obtainStyledAttributes(C4057R.styleable.Window);
        this.mDefaultWindowAnimationStyleResId = windowStyle.getResourceId(8, 0);
        windowStyle.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ float lambda$new$0(float input) {
        if (input >= 0.5f) {
            float t = (input - 0.5f) / 0.5f;
            return this.mFastOutLinearInInterpolator.getInterpolation(t);
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ float lambda$new$1(float input) {
        if (input < 0.5f) {
            float t = input / 0.5f;
            return this.mLinearOutSlowInInterpolator.getInterpolation(t);
        }
        return 1.0f;
    }

    public Animation loadKeyguardExitAnimation(int transitionFlags, boolean onWallpaper) {
        if ((transitionFlags & 2) != 0) {
            return null;
        }
        boolean toShade = (transitionFlags & 1) != 0;
        boolean subtle = (transitionFlags & 8) != 0;
        return createHiddenByKeyguardExit(this.mContext, this.mInterpolator, onWallpaper, toShade, subtle);
    }

    public Animation loadKeyguardUnoccludeAnimation() {
        return loadDefaultAnimationRes(C4057R.anim.wallpaper_open_exit);
    }

    public Animation loadVoiceActivityOpenAnimation(boolean enter) {
        int i;
        if (enter) {
            i = C4057R.anim.voice_activity_open_enter;
        } else {
            i = C4057R.anim.voice_activity_open_exit;
        }
        return loadDefaultAnimationRes(i);
    }

    public Animation loadVoiceActivityExitAnimation(boolean enter) {
        int i;
        if (enter) {
            i = C4057R.anim.voice_activity_close_enter;
        } else {
            i = C4057R.anim.voice_activity_close_exit;
        }
        return loadDefaultAnimationRes(i);
    }

    public Animation loadAppTransitionAnimation(String packageName, int resId) {
        return loadAnimationRes(packageName, resId);
    }

    public Animation loadCrossProfileAppEnterAnimation() {
        return loadAnimationRes("android", C4057R.anim.task_open_enter_cross_profile_apps);
    }

    public Animation loadCrossProfileAppThumbnailEnterAnimation() {
        return loadAnimationRes("android", C4057R.anim.cross_profile_apps_thumbnail_enter);
    }

    public Animation createCrossProfileAppsThumbnailAnimationLocked(Rect appRect) {
        Animation animation = loadCrossProfileAppThumbnailEnterAnimation();
        return prepareThumbnailAnimationWithDuration(animation, appRect.width(), appRect.height(), 0L, null);
    }

    public Animation loadAnimationRes(String packageName, int resId) {
        AttributeCache.Entry ent;
        if (ResourceId.isValid(resId) && (ent = getCachedAnimations(packageName, resId)) != null) {
            return loadAnimationSafely(ent.context, resId, this.mTag);
        }
        return null;
    }

    public Animation loadDefaultAnimationRes(int resId) {
        return loadAnimationRes("android", resId);
    }

    public Animation loadAnimationAttr(WindowManager.LayoutParams lp, int animAttr, int transit) {
        AttributeCache.Entry ent;
        int resId = 0;
        Context context = this.mContext;
        if (animAttr >= 0 && (ent = getCachedAnimations(lp)) != null) {
            context = ent.context;
            resId = ent.array.getResourceId(animAttr, 0);
        }
        int resId2 = updateToTranslucentAnimIfNeeded(resId, transit);
        if (ResourceId.isValid(resId2)) {
            return loadAnimationSafely(context, resId2, this.mTag);
        }
        return null;
    }

    public int getAnimationResId(WindowManager.LayoutParams lp, int animAttr, int transit) {
        AttributeCache.Entry ent;
        int resId = 0;
        if (animAttr >= 0 && (ent = getCachedAnimations(lp)) != null) {
            resId = ent.array.getResourceId(animAttr, 0);
        }
        return updateToTranslucentAnimIfNeeded(resId, transit);
    }

    public int getDefaultAnimationResId(int animAttr, int transit) {
        AttributeCache.Entry ent;
        int resId = 0;
        if (animAttr >= 0 && (ent = getCachedAnimations("android", this.mDefaultWindowAnimationStyleResId)) != null) {
            resId = ent.array.getResourceId(animAttr, 0);
        }
        return updateToTranslucentAnimIfNeeded(resId, transit);
    }

    private Animation loadAnimationAttr(String packageName, int animStyleResId, int animAttr, boolean translucent, int transit) {
        if (animStyleResId == 0) {
            return null;
        }
        int resId = 0;
        Context context = this.mContext;
        if (animAttr >= 0) {
            AttributeCache.Entry ent = getCachedAnimations(packageName != null ? packageName : "android", animStyleResId);
            if (ent != null) {
                context = ent.context;
                resId = ent.array.getResourceId(animAttr, 0);
            }
        }
        if (translucent) {
            resId = updateToTranslucentAnimIfNeeded(resId);
        } else if (transit != -1) {
            resId = updateToTranslucentAnimIfNeeded(resId, transit);
        }
        if (!ResourceId.isValid(resId)) {
            return null;
        }
        return loadAnimationSafely(context, resId, this.mTag);
    }

    public Animation loadAnimationAttr(String packageName, int animStyleResId, int animAttr, boolean translucent) {
        return loadAnimationAttr(packageName, animStyleResId, animAttr, translucent, -1);
    }

    public Animation loadDefaultAnimationAttr(int animAttr, boolean translucent) {
        return loadAnimationAttr("android", this.mDefaultWindowAnimationStyleResId, animAttr, translucent);
    }

    public Animation loadDefaultAnimationAttr(int animAttr, int transit) {
        return loadAnimationAttr("android", this.mDefaultWindowAnimationStyleResId, animAttr, false, transit);
    }

    private AttributeCache.Entry getCachedAnimations(WindowManager.LayoutParams lp) {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "Loading animations: layout params pkg=" + (lp != null ? lp.packageName : null) + " resId=0x" + (lp != null ? Integer.toHexString(lp.windowAnimations) : null));
        }
        if (lp == null || lp.windowAnimations == 0) {
            return null;
        }
        String packageName = lp.packageName != null ? lp.packageName : "android";
        int resId = getAnimationStyleResId(lp);
        if (((-16777216) & resId) == 16777216) {
            packageName = "android";
        }
        if (this.mDebug) {
            Slog.m92v(this.mTag, "Loading animations: picked package=" + packageName);
        }
        return AttributeCache.instance().get(packageName, resId, C4057R.styleable.WindowAnimation);
    }

    private AttributeCache.Entry getCachedAnimations(String packageName, int resId) {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "Loading animations: package=" + packageName + " resId=0x" + Integer.toHexString(resId));
        }
        if (packageName != null) {
            if (((-16777216) & resId) == 16777216) {
                packageName = "android";
            }
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Loading animations: picked package=" + packageName);
            }
            return AttributeCache.instance().get(packageName, resId, C4057R.styleable.WindowAnimation);
        }
        return null;
    }

    public int getAnimationStyleResId(WindowManager.LayoutParams lp) {
        int resId = lp.windowAnimations;
        if (lp.type == 3) {
            int resId2 = this.mDefaultWindowAnimationStyleResId;
            return resId2;
        }
        return resId;
    }

    public Animation createRelaunchAnimation(Rect containingFrame, Rect contentInsets, Rect startRect) {
        setupDefaultNextAppTransitionStartRect(startRect, this.mTmpFromClipRect);
        int left = this.mTmpFromClipRect.left;
        int top = this.mTmpFromClipRect.top;
        this.mTmpFromClipRect.offset(-left, -top);
        this.mTmpToClipRect.set(0, 0, containingFrame.width(), containingFrame.height());
        AnimationSet set = new AnimationSet(true);
        float fromWidth = this.mTmpFromClipRect.width();
        float toWidth = this.mTmpToClipRect.width();
        float fromHeight = this.mTmpFromClipRect.height();
        float toHeight = (this.mTmpToClipRect.height() - contentInsets.top) - contentInsets.bottom;
        int translateAdjustment = 0;
        if (fromWidth <= toWidth && fromHeight <= toHeight) {
            set.addAnimation(new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect));
        } else {
            set.addAnimation(new ScaleAnimation(fromWidth / toWidth, 1.0f, fromHeight / toHeight, 1.0f));
            translateAdjustment = (int) ((contentInsets.top * fromHeight) / toHeight);
        }
        TranslateAnimation translate = new TranslateAnimation(left - containingFrame.left, 0.0f, (top - containingFrame.top) - translateAdjustment, 0.0f);
        set.addAnimation(translate);
        set.setDuration(336L);
        set.setZAdjustment(1);
        return set;
    }

    private void setupDefaultNextAppTransitionStartRect(Rect startRect, Rect rect) {
        if (startRect == null) {
            Slog.m95e(this.mTag, "Starting rect for app requested, but none available", new Throwable());
            rect.setEmpty();
            return;
        }
        rect.set(startRect);
    }

    public Animation createClipRevealAnimationLocked(int transit, int wallpaperTransit, boolean enter, Rect appFrame, Rect displayFrame, Rect startRect) {
        return createClipRevealAnimationLockedCompat(getTransitCompatType(transit, wallpaperTransit), enter, appFrame, displayFrame, startRect);
    }

    public Animation createClipRevealAnimationLockedCompat(int transit, boolean enter, Rect appFrame, Rect displayFrame, Rect startRect) {
        long duration;
        float f;
        boolean z;
        Animation anim;
        float t;
        int translationY;
        int translationYCorrection;
        int clipStartY;
        int translationX;
        boolean cutOff;
        int clipStartX;
        Interpolator interpolator;
        if (enter) {
            int appWidth = appFrame.width();
            int appHeight = appFrame.height();
            setupDefaultNextAppTransitionStartRect(startRect, this.mTmpRect);
            if (appHeight <= 0) {
                t = 0.0f;
            } else {
                float t2 = this.mTmpRect.top / displayFrame.height();
                t = t2;
            }
            int translationY2 = this.mClipRevealTranslationY + ((int) ((displayFrame.height() / 7.0f) * t));
            int translationX2 = 0;
            int centerX = this.mTmpRect.centerX();
            int centerY = this.mTmpRect.centerY();
            int halfWidth = this.mTmpRect.width() / 2;
            int halfHeight = this.mTmpRect.height() / 2;
            int clipStartX2 = (centerX - halfWidth) - appFrame.left;
            int clipStartY2 = (centerY - halfHeight) - appFrame.top;
            boolean cutOff2 = false;
            if (appFrame.top <= centerY - halfHeight) {
                translationY = translationY2;
                translationYCorrection = translationY2;
                clipStartY = clipStartY2;
            } else {
                cutOff2 = true;
                translationY = (centerY - halfHeight) - appFrame.top;
                translationYCorrection = 0;
                clipStartY = 0;
            }
            if (appFrame.left > centerX - halfWidth) {
                translationX2 = (centerX - halfWidth) - appFrame.left;
                clipStartX2 = 0;
                cutOff2 = true;
            }
            if (appFrame.right >= centerX + halfWidth) {
                translationX = translationX2;
                cutOff = cutOff2;
                clipStartX = clipStartX2;
            } else {
                int translationX3 = (centerX + halfWidth) - appFrame.right;
                int clipStartX3 = appWidth - this.mTmpRect.width();
                translationX = translationX3;
                cutOff = true;
                clipStartX = clipStartX3;
            }
            long duration2 = calculateClipRevealTransitionDuration(cutOff, translationX, translationY, displayFrame);
            Animation clipAnimLR = new ClipRectLRAnimation(clipStartX, this.mTmpRect.width() + clipStartX, 0, appWidth);
            clipAnimLR.setInterpolator(this.mClipHorizontalInterpolator);
            boolean cutOff3 = cutOff;
            clipAnimLR.setDuration(((float) duration2) / 2.5f);
            TranslateAnimation translate = new TranslateAnimation(translationX, 0.0f, translationY, 0.0f);
            if (cutOff3) {
                interpolator = this.mTouchResponseInterpolator;
            } else {
                interpolator = this.mLinearOutSlowInInterpolator;
            }
            translate.setInterpolator(interpolator);
            translate.setDuration(duration2);
            int clipStartX4 = translationYCorrection;
            Animation clipAnimTB = new ClipRectTBAnimation(clipStartY, clipStartY + this.mTmpRect.height(), 0, appHeight, clipStartX4, 0, this.mLinearOutSlowInInterpolator);
            clipAnimTB.setInterpolator(this.mTouchResponseInterpolator);
            clipAnimTB.setDuration(duration2);
            long alphaDuration = duration2 / 4;
            AlphaAnimation alpha = new AlphaAnimation(0.5f, 1.0f);
            alpha.setDuration(alphaDuration);
            alpha.setInterpolator(this.mLinearOutSlowInInterpolator);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(clipAnimLR);
            set.addAnimation(clipAnimTB);
            set.addAnimation(translate);
            set.addAnimation(alpha);
            set.setZAdjustment(1);
            set.initialize(appWidth, appHeight, appWidth, appHeight);
            return set;
        }
        switch (transit) {
            case 6:
            case 7:
                duration = this.mConfigShortAnimTime;
                break;
            default:
                duration = 336;
                break;
        }
        if (transit == 14) {
            f = 1.0f;
        } else if (transit != 15) {
            anim = new AlphaAnimation(1.0f, 1.0f);
            z = true;
            anim.setInterpolator(this.mDecelerateInterpolator);
            anim.setDuration(duration);
            anim.setFillAfter(z);
            return anim;
        } else {
            f = 1.0f;
        }
        anim = new AlphaAnimation(f, 0.0f);
        z = true;
        anim.setDetachWallpaper(true);
        anim.setInterpolator(this.mDecelerateInterpolator);
        anim.setDuration(duration);
        anim.setFillAfter(z);
        return anim;
    }

    public Animation createScaleUpAnimationLocked(int transit, int wallpaperTransit, boolean enter, Rect containingFrame, Rect startRect) {
        return createScaleUpAnimationLockedCompat(getTransitCompatType(transit, wallpaperTransit), enter, containingFrame, startRect);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Animation createScaleUpAnimationLockedCompat(int transit, boolean enter, Rect containingFrame, Rect startRect) {
        Animation alpha;
        long duration;
        setupDefaultNextAppTransitionStartRect(startRect, this.mTmpRect);
        int appWidth = containingFrame.width();
        int appHeight = containingFrame.height();
        if (enter) {
            float scaleW = this.mTmpRect.width() / appWidth;
            float scaleH = this.mTmpRect.height() / appHeight;
            Animation scale = new ScaleAnimation(scaleW, 1.0f, scaleH, 1.0f, computePivot(this.mTmpRect.left, scaleW), computePivot(this.mTmpRect.top, scaleH));
            scale.setInterpolator(this.mDecelerateInterpolator);
            Animation alpha2 = new AlphaAnimation(0.0f, 1.0f);
            alpha2.setInterpolator(this.mThumbnailFadeOutInterpolator);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(scale);
            set.addAnimation(alpha2);
            set.setDetachWallpaper(true);
            alpha = set;
        } else if (transit == 14 || transit == 15) {
            alpha = new AlphaAnimation(1.0f, 0.0f);
            alpha.setDetachWallpaper(true);
        } else {
            alpha = new AlphaAnimation(1.0f, 1.0f);
        }
        switch (transit) {
            case 6:
            case 7:
                duration = this.mConfigShortAnimTime;
                break;
            default:
                duration = 336;
                break;
        }
        alpha.setDuration(duration);
        alpha.setFillAfter(true);
        alpha.setInterpolator(this.mDecelerateInterpolator);
        alpha.initialize(appWidth, appHeight, appWidth, appHeight);
        return alpha;
    }

    public Animation createThumbnailEnterExitAnimationLocked(boolean enter, boolean scaleUp, Rect containingFrame, int transit, int wallpaperTransit, HardwareBuffer thumbnailHeader, Rect startRect) {
        return createThumbnailEnterExitAnimationLockedCompat(enter, scaleUp, containingFrame, getTransitCompatType(transit, wallpaperTransit), thumbnailHeader, startRect);
    }

    public Animation createThumbnailEnterExitAnimationLockedCompat(boolean enter, boolean scaleUp, Rect containingFrame, int transit, HardwareBuffer thumbnailHeader, Rect startRect) {
        Animation a;
        int appWidth = containingFrame.width();
        int appHeight = containingFrame.height();
        setupDefaultNextAppTransitionStartRect(startRect, this.mTmpRect);
        int thumbWidthI = thumbnailHeader != null ? thumbnailHeader.getWidth() : appWidth;
        float thumbWidth = thumbWidthI > 0 ? thumbWidthI : 1.0f;
        int thumbHeightI = thumbnailHeader != null ? thumbnailHeader.getHeight() : appHeight;
        float thumbHeight = thumbHeightI > 0 ? thumbHeightI : 1.0f;
        int thumbTransitState = getThumbnailTransitionState(enter, scaleUp);
        switch (thumbTransitState) {
            case 0:
                float scaleW = thumbWidth / appWidth;
                float scaleH = thumbHeight / appHeight;
                a = new ScaleAnimation(scaleW, 1.0f, scaleH, 1.0f, computePivot(this.mTmpRect.left, scaleW), computePivot(this.mTmpRect.top, scaleH));
                break;
            case 1:
                if (transit == 14) {
                    a = new AlphaAnimation(1.0f, 0.0f);
                    break;
                } else {
                    a = new AlphaAnimation(1.0f, 1.0f);
                    break;
                }
            case 2:
                a = new AlphaAnimation(1.0f, 1.0f);
                break;
            case 3:
                float scaleW2 = thumbWidth / appWidth;
                float scaleH2 = thumbHeight / appHeight;
                Animation scale = new ScaleAnimation(1.0f, scaleW2, 1.0f, scaleH2, computePivot(this.mTmpRect.left, scaleW2), computePivot(this.mTmpRect.top, scaleH2));
                Animation alpha = new AlphaAnimation(1.0f, 0.0f);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(scale);
                set.addAnimation(alpha);
                set.setZAdjustment(1);
                a = set;
                break;
            default:
                throw new RuntimeException("Invalid thumbnail transition state");
        }
        return prepareThumbnailAnimation(a, appWidth, appHeight, transit);
    }

    public Animation createAspectScaledThumbnailEnterExitAnimationLocked(boolean enter, boolean scaleUp, int orientation, int transit, Rect containingFrame, Rect contentInsets, Rect surfaceInsets, Rect stableInsets, boolean freeform, Rect startRect, Rect defaultStartRect) {
        int appHeight;
        Animation clipAnim;
        Animation translateAnim;
        Animation a;
        Animation clipRectAnimation;
        Animation translateAnim2;
        int appWidth = containingFrame.width();
        int appHeight2 = containingFrame.height();
        setupDefaultNextAppTransitionStartRect(defaultStartRect, this.mTmpRect);
        int thumbWidthI = this.mTmpRect.width();
        float thumbWidth = thumbWidthI > 0 ? thumbWidthI : 1.0f;
        int thumbHeightI = this.mTmpRect.height();
        float thumbHeight = thumbHeightI > 0 ? thumbHeightI : 1.0f;
        int thumbStartX = (this.mTmpRect.left - containingFrame.left) - contentInsets.left;
        int thumbStartY = this.mTmpRect.top - containingFrame.top;
        int thumbTransitState = getThumbnailTransitionState(enter, scaleUp);
        switch (thumbTransitState) {
            case 0:
            case 3:
                if (freeform && scaleUp) {
                    a = createAspectScaledThumbnailEnterFreeformAnimationLocked(containingFrame, surfaceInsets, startRect, defaultStartRect);
                    appHeight = appHeight2;
                    break;
                } else if (freeform) {
                    a = createAspectScaledThumbnailExitFreeformAnimationLocked(containingFrame, surfaceInsets, startRect, defaultStartRect);
                    appHeight = appHeight2;
                    break;
                } else {
                    AnimationSet set = new AnimationSet(true);
                    this.mTmpFromClipRect.set(containingFrame);
                    this.mTmpToClipRect.set(containingFrame);
                    this.mTmpFromClipRect.offsetTo(0, 0);
                    this.mTmpToClipRect.offsetTo(0, 0);
                    this.mTmpFromClipRect.inset(contentInsets);
                    if (shouldScaleDownThumbnailTransition(orientation)) {
                        float scale = thumbWidth / ((appWidth - contentInsets.left) - contentInsets.right);
                        if (!this.mGridLayoutRecentsEnabled) {
                            int unscaledThumbHeight = (int) (thumbHeight / scale);
                            Rect rect = this.mTmpFromClipRect;
                            rect.bottom = rect.top + unscaledThumbHeight;
                        }
                        Animation scaleAnim = new ScaleAnimation(scaleUp ? scale : 1.0f, scaleUp ? 1.0f : scale, scaleUp ? scale : 1.0f, scaleUp ? 1.0f : scale, containingFrame.width() / 2.0f, (containingFrame.height() / 2.0f) + contentInsets.top);
                        float targetX = this.mTmpRect.left - containingFrame.left;
                        float x = (containingFrame.width() / 2.0f) - ((containingFrame.width() / 2.0f) * scale);
                        float targetY = this.mTmpRect.top - containingFrame.top;
                        float y = (containingFrame.height() / 2.0f) - ((containingFrame.height() / 2.0f) * scale);
                        if (this.mLowRamRecentsEnabled && contentInsets.top == 0 && scaleUp) {
                            appHeight = appHeight2;
                            this.mTmpFromClipRect.top += stableInsets.top;
                            y += stableInsets.top;
                        } else {
                            appHeight = appHeight2;
                        }
                        float scale2 = targetX - x;
                        float startY = targetY - y;
                        if (!scaleUp) {
                            clipRectAnimation = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
                        } else {
                            clipRectAnimation = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
                        }
                        Animation clipAnim2 = clipRectAnimation;
                        if (scaleUp) {
                            translateAnim2 = createCurvedMotion(scale2, 0.0f, startY - contentInsets.top, 0.0f);
                        } else {
                            translateAnim2 = createCurvedMotion(0.0f, scale2, 0.0f, startY - contentInsets.top);
                        }
                        set.addAnimation(clipAnim2);
                        set.addAnimation(scaleAnim);
                        set.addAnimation(translateAnim2);
                    } else {
                        appHeight = appHeight2;
                        Rect rect2 = this.mTmpFromClipRect;
                        rect2.bottom = rect2.top + thumbHeightI;
                        Rect rect3 = this.mTmpFromClipRect;
                        rect3.right = rect3.left + thumbWidthI;
                        if (scaleUp) {
                            clipAnim = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
                        } else {
                            clipAnim = new ClipRectAnimation(this.mTmpToClipRect, this.mTmpFromClipRect);
                        }
                        if (scaleUp) {
                            translateAnim = createCurvedMotion(thumbStartX, 0.0f, thumbStartY - contentInsets.top, 0.0f);
                        } else {
                            translateAnim = createCurvedMotion(0.0f, thumbStartX, 0.0f, thumbStartY - contentInsets.top);
                        }
                        set.addAnimation(clipAnim);
                        set.addAnimation(translateAnim);
                    }
                    set.setZAdjustment(1);
                    a = set;
                    break;
                }
                break;
            case 1:
                if (transit == 14) {
                    a = new AlphaAnimation(1.0f, 0.0f);
                    appHeight = appHeight2;
                    break;
                } else {
                    a = new AlphaAnimation(1.0f, 1.0f);
                    appHeight = appHeight2;
                    break;
                }
            case 2:
                if (transit == 14) {
                    a = new AlphaAnimation(0.0f, 1.0f);
                    appHeight = appHeight2;
                    break;
                } else {
                    a = new AlphaAnimation(1.0f, 1.0f);
                    appHeight = appHeight2;
                    break;
                }
            default:
                throw new RuntimeException("Invalid thumbnail transition state");
        }
        return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, 336L, this.mTouchResponseInterpolator);
    }

    public Animation createThumbnailAspectScaleAnimationLocked(Rect appRect, Rect contentInsets, HardwareBuffer thumbnailHeader, int orientation, Rect startRect, Rect defaultStartRect, boolean scaleUp) {
        float pivotY;
        float fromY;
        float pivotX;
        float pivotX2;
        float toX;
        float fromX;
        float toY;
        float fromY2;
        float toX2;
        AnimationSet animationSet;
        Rect rect;
        Rect rect2;
        int thumbWidthI = thumbnailHeader.getWidth();
        float thumbWidth = thumbWidthI > 0 ? thumbWidthI : 1.0f;
        int thumbHeightI = thumbnailHeader.getHeight();
        int appWidth = appRect.width();
        float scaleW = appWidth / thumbWidth;
        getNextAppTransitionStartRect(startRect, defaultStartRect, this.mTmpRect);
        if (shouldScaleDownThumbnailTransition(orientation)) {
            float fromX2 = this.mTmpRect.left;
            fromY = this.mTmpRect.top;
            float toX3 = ((this.mTmpRect.width() / 2) * (scaleW - 1.0f)) + appRect.left;
            float toY2 = ((appRect.height() / 2) * (1.0f - (1.0f / scaleW))) + appRect.top;
            float pivotX3 = this.mTmpRect.width() / 2;
            float pivotY2 = (appRect.height() / 2) / scaleW;
            if (!this.mGridLayoutRecentsEnabled) {
                pivotX = pivotX3;
                pivotY = pivotY2;
                pivotX2 = toY2;
                toX = fromX2;
                fromX = toX3;
            } else {
                fromY -= thumbHeightI;
                pivotX = pivotX3;
                pivotY = pivotY2;
                pivotX2 = toY2 - (thumbHeightI * scaleW);
                toX = fromX2;
                fromX = toX3;
            }
        } else {
            pivotY = 0.0f;
            float fromX3 = this.mTmpRect.left;
            fromY = this.mTmpRect.top;
            float toX4 = appRect.left;
            pivotX = 0.0f;
            pivotX2 = appRect.top;
            toX = fromX3;
            fromX = toX4;
        }
        float toY3 = pivotX2;
        if (scaleUp) {
            toY = toY3;
            Animation scale = new ScaleAnimation(1.0f, scaleW, 1.0f, scaleW, pivotX, pivotY);
            Interpolator interpolator = TOUCH_RESPONSE_INTERPOLATOR;
            scale.setInterpolator(interpolator);
            scale.setDuration(336L);
            Animation alpha = new AlphaAnimation(1.0f, 0.0f);
            alpha.setInterpolator(this.mThumbnailFadeOutInterpolator);
            alpha.setDuration(336L);
            Animation translate = createCurvedMotion(toX, fromX, fromY, toY);
            translate.setInterpolator(interpolator);
            float toX5 = fromX;
            float fromY3 = fromY;
            translate.setDuration(336L);
            this.mTmpFromClipRect.set(0, 0, thumbWidthI, thumbHeightI);
            this.mTmpToClipRect.set(appRect);
            this.mTmpToClipRect.offsetTo(0, 0);
            this.mTmpToClipRect.right = (int) (rect.right / scaleW);
            this.mTmpToClipRect.bottom = (int) (rect2.bottom / scaleW);
            if (contentInsets != null) {
                this.mTmpToClipRect.inset((int) ((-contentInsets.left) * scaleW), (int) ((-contentInsets.top) * scaleW), (int) ((-contentInsets.right) * scaleW), (int) ((-contentInsets.bottom) * scaleW));
            }
            Animation clipAnim = new ClipRectAnimation(this.mTmpFromClipRect, this.mTmpToClipRect);
            clipAnim.setInterpolator(interpolator);
            clipAnim.setDuration(336L);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(scale);
            if (!this.mGridLayoutRecentsEnabled) {
                set.addAnimation(alpha);
            }
            set.addAnimation(translate);
            set.addAnimation(clipAnim);
            animationSet = set;
            fromY2 = fromY3;
            toX2 = toX5;
        } else {
            float toX6 = fromX;
            toY = toY3;
            Animation scale2 = new ScaleAnimation(scaleW, 1.0f, scaleW, 1.0f, pivotX, pivotY);
            Interpolator interpolator2 = TOUCH_RESPONSE_INTERPOLATOR;
            scale2.setInterpolator(interpolator2);
            scale2.setDuration(336L);
            Animation alpha2 = new AlphaAnimation(0.0f, 1.0f);
            alpha2.setInterpolator(this.mThumbnailFadeInInterpolator);
            alpha2.setDuration(336L);
            fromY2 = fromY;
            toX2 = toX6;
            Animation translate2 = createCurvedMotion(toX2, toX, toY, fromY2);
            translate2.setInterpolator(interpolator2);
            translate2.setDuration(336L);
            AnimationSet set2 = new AnimationSet(false);
            set2.addAnimation(scale2);
            if (!this.mGridLayoutRecentsEnabled) {
                set2.addAnimation(alpha2);
            }
            set2.addAnimation(translate2);
            animationSet = set2;
        }
        return prepareThumbnailAnimationWithDuration(animationSet, appWidth, appRect.height(), 0L, null);
    }

    public HardwareBuffer createCrossProfileAppsThumbnail(Drawable thumbnailDrawable, Rect frame) {
        int width = frame.width();
        int height = frame.height();
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(width, height);
        canvas.drawColor(Color.argb(0.6f, 0.0f, 0.0f, 0.0f));
        int thumbnailSize = this.mContext.getResources().getDimensionPixelSize(C4057R.dimen.cross_profile_apps_thumbnail_size);
        thumbnailDrawable.setBounds((width - thumbnailSize) / 2, (height - thumbnailSize) / 2, (width + thumbnailSize) / 2, (height + thumbnailSize) / 2);
        thumbnailDrawable.setTint(this.mContext.getColor(17170443));
        thumbnailDrawable.draw(canvas);
        picture.endRecording();
        return Bitmap.createBitmap(picture).getHardwareBuffer();
    }

    private Animation prepareThumbnailAnimation(Animation a, int appWidth, int appHeight, int transit) {
        int duration;
        switch (transit) {
            case 6:
            case 7:
                duration = this.mConfigShortAnimTime;
                break;
            default:
                duration = 336;
                break;
        }
        return prepareThumbnailAnimationWithDuration(a, appWidth, appHeight, duration, this.mDecelerateInterpolator);
    }

    private Animation createAspectScaledThumbnailEnterFreeformAnimationLocked(Rect frame, Rect surfaceInsets, Rect startRect, Rect defaultStartRect) {
        getNextAppTransitionStartRect(startRect, defaultStartRect, this.mTmpRect);
        return createAspectScaledThumbnailFreeformAnimationLocked(this.mTmpRect, frame, surfaceInsets, true);
    }

    private Animation createAspectScaledThumbnailExitFreeformAnimationLocked(Rect frame, Rect surfaceInsets, Rect startRect, Rect defaultStartRect) {
        getNextAppTransitionStartRect(startRect, defaultStartRect, this.mTmpRect);
        return createAspectScaledThumbnailFreeformAnimationLocked(frame, this.mTmpRect, surfaceInsets, false);
    }

    private void getNextAppTransitionStartRect(Rect startRect, Rect defaultStartRect, Rect rect) {
        if (startRect == null && defaultStartRect == null) {
            Slog.m95e(this.mTag, "Starting rect for container not available", new Throwable());
            rect.setEmpty();
            return;
        }
        rect.set(startRect != null ? startRect : defaultStartRect);
    }

    private AnimationSet createAspectScaledThumbnailFreeformAnimationLocked(Rect sourceFrame, Rect destFrame, Rect surfaceInsets, boolean enter) {
        float sourceWidth = sourceFrame.width();
        float sourceHeight = sourceFrame.height();
        float destWidth = destFrame.width();
        float destHeight = destFrame.height();
        float scaleH = enter ? sourceWidth / destWidth : destWidth / sourceWidth;
        float scaleV = enter ? sourceHeight / destHeight : destHeight / sourceHeight;
        AnimationSet set = new AnimationSet(true);
        int surfaceInsetsH = surfaceInsets == null ? 0 : surfaceInsets.left + surfaceInsets.right;
        int surfaceInsetsV = surfaceInsets != null ? surfaceInsets.top + surfaceInsets.bottom : 0;
        float scaleHCenter = ((enter ? destWidth : sourceWidth) + surfaceInsetsH) / 2.0f;
        float scaleVCenter = ((enter ? destHeight : sourceHeight) + surfaceInsetsV) / 2.0f;
        ScaleAnimation scale = enter ? new ScaleAnimation(scaleH, 1.0f, scaleV, 1.0f, scaleHCenter, scaleVCenter) : new ScaleAnimation(1.0f, scaleH, 1.0f, scaleV, scaleHCenter, scaleVCenter);
        int sourceHCenter = sourceFrame.left + (sourceFrame.width() / 2);
        int sourceVCenter = sourceFrame.top + (sourceFrame.height() / 2);
        int destHCenter = destFrame.left + (destFrame.width() / 2);
        int destVCenter = destFrame.top + (destFrame.height() / 2);
        int fromX = enter ? sourceHCenter - destHCenter : destHCenter - sourceHCenter;
        int fromY = enter ? sourceVCenter - destVCenter : destVCenter - sourceVCenter;
        TranslateAnimation translation = enter ? new TranslateAnimation(fromX, 0.0f, fromY, 0.0f) : new TranslateAnimation(0.0f, fromX, 0.0f, fromY);
        set.addAnimation(scale);
        set.addAnimation(translation);
        return set;
    }

    private boolean shouldScaleDownThumbnailTransition(int orientation) {
        return this.mGridLayoutRecentsEnabled || orientation == 1;
    }

    private static int updateToTranslucentAnimIfNeeded(int anim, int transit) {
        if (transit == 24 && anim == 17432591) {
            return C4057R.anim.activity_translucent_open_enter;
        }
        if (transit == 25 && anim == 17432590) {
            return C4057R.anim.activity_translucent_close_exit;
        }
        return anim;
    }

    private static int updateToTranslucentAnimIfNeeded(int anim) {
        if (anim == 17432591) {
            return C4057R.anim.activity_translucent_open_enter;
        }
        if (anim == 17432590) {
            return C4057R.anim.activity_translucent_close_exit;
        }
        return anim;
    }

    private static int getTransitCompatType(int transit, int wallpaperTransit) {
        if (wallpaperTransit == 3) {
            return 14;
        }
        if (wallpaperTransit == 4) {
            return 15;
        }
        if (transit == 1) {
            return 6;
        }
        if (transit == 2) {
            return 7;
        }
        return 0;
    }

    private static long calculateClipRevealTransitionDuration(boolean cutOff, float translationX, float translationY, Rect displayFrame) {
        if (!cutOff) {
            return 336L;
        }
        float fraction = Math.max(Math.abs(translationX) / displayFrame.width(), Math.abs(translationY) / displayFrame.height());
        return (84.0f * fraction) + 336.0f;
    }

    private int getThumbnailTransitionState(boolean enter, boolean scaleUp) {
        if (enter) {
            if (scaleUp) {
                return 0;
            }
            return 2;
        } else if (scaleUp) {
            return 1;
        } else {
            return 3;
        }
    }

    public static Animation prepareThumbnailAnimationWithDuration(Animation a, int appWidth, int appHeight, long duration, Interpolator interpolator) {
        if (a == null) {
            return null;
        }
        if (duration > 0) {
            a.setDuration(duration);
        }
        a.setFillAfter(true);
        if (interpolator != null) {
            a.setInterpolator(interpolator);
        }
        a.initialize(appWidth, appHeight, appWidth, appHeight);
        return a;
    }

    private static Animation createCurvedMotion(float fromX, float toX, float fromY, float toY) {
        return new TranslateAnimation(fromX, toX, fromY, toY);
    }

    public static float computePivot(int startPos, float finalScale) {
        float denom = finalScale - 1.0f;
        if (Math.abs(denom) < 1.0E-4f) {
            return startPos;
        }
        return (-startPos) / denom;
    }

    public static Animation loadAnimationSafely(Context context, int resId, String tag) {
        try {
            return AnimationUtils.loadAnimation(context, resId);
        } catch (Resources.NotFoundException e) {
            Slog.m89w(tag, "Unable to load animation resource", e);
            return null;
        }
    }

    public static Animation createHiddenByKeyguardExit(Context context, LogDecelerateInterpolator interpolator, boolean onWallpaper, boolean goingToNotificationShade, boolean subtleAnimation) {
        int resource;
        if (goingToNotificationShade) {
            return AnimationUtils.loadAnimation(context, C4057R.anim.lock_screen_behind_enter_fade_in);
        }
        if (subtleAnimation) {
            resource = C4057R.anim.lock_screen_behind_enter_subtle;
        } else if (onWallpaper) {
            resource = C4057R.anim.lock_screen_behind_enter_wallpaper;
        } else {
            resource = C4057R.anim.lock_screen_behind_enter;
        }
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(context, resource);
        List<Animation> animations = set.getAnimations();
        for (int i = animations.size() - 1; i >= 0; i--) {
            animations.get(i).setInterpolator(interpolator);
        }
        return set;
    }

    public static boolean hasProtectedContent(HardwareBuffer hardwareBuffer) {
        return (hardwareBuffer.getUsage() & 16384) == 16384;
    }

    public static float getBorderLuma(SurfaceControl surfaceControl, int w, int h) {
        ScreenCapture.ScreenshotHardwareBuffer buffer = ScreenCapture.captureLayers(surfaceControl, new Rect(0, 0, w, h), 1.0f);
        if (buffer == null) {
            return 0.0f;
        }
        HardwareBuffer hwBuffer = buffer.getHardwareBuffer();
        float luma = getBorderLuma(hwBuffer, buffer.getColorSpace());
        if (hwBuffer != null) {
            hwBuffer.close();
        }
        return luma;
    }

    public static float getBorderLuma(HardwareBuffer hwBuffer, ColorSpace colorSpace) {
        int format;
        if (hwBuffer != null && (format = hwBuffer.getFormat()) == 1 && !hasProtectedContent(hwBuffer)) {
            ImageReader ir = ImageReader.newInstance(hwBuffer.getWidth(), hwBuffer.getHeight(), format, 1);
            ir.getSurface().attachAndQueueBufferWithColorSpace(hwBuffer, colorSpace);
            Image image = ir.acquireLatestImage();
            if (image != null && image.getPlaneCount() >= 1) {
                Image.Plane plane = image.getPlanes()[0];
                ByteBuffer buffer = plane.getBuffer();
                int width = image.getWidth();
                int height = image.getHeight();
                int pixelStride = plane.getPixelStride();
                int rowStride = plane.getRowStride();
                int[] borderLumas = new int[((width + height) * 2) / 10];
                int i = 0;
                int size = width - 10;
                for (int x = 0; x < size; x += 10) {
                    int i2 = i + 1;
                    borderLumas[i] = getPixelLuminance(buffer, x, 0, pixelStride, rowStride);
                    i = i2 + 1;
                    borderLumas[i2] = getPixelLuminance(buffer, x, height - 1, pixelStride, rowStride);
                }
                int y = 0;
                int size2 = height - 10;
                while (y < size2) {
                    int i3 = i + 1;
                    borderLumas[i] = getPixelLuminance(buffer, 0, y, pixelStride, rowStride);
                    i = i3 + 1;
                    borderLumas[i3] = getPixelLuminance(buffer, width - 1, y, pixelStride, rowStride);
                    y += 10;
                    plane = plane;
                }
                ir.close();
                int[] histogram = new int[256];
                int maxCount = 0;
                int mostLuma = 0;
                int length = borderLumas.length;
                int format2 = 0;
                while (format2 < length) {
                    int luma = borderLumas[format2];
                    int count = histogram[luma] + 1;
                    histogram[luma] = count;
                    int[] histogram2 = histogram;
                    if (count > maxCount) {
                        maxCount = count;
                        mostLuma = luma;
                    }
                    format2++;
                    histogram = histogram2;
                }
                return mostLuma / 255.0f;
            }
            return 0.0f;
        }
        return 0.0f;
    }

    private static int getPixelLuminance(ByteBuffer buffer, int x, int y, int pixelStride, int rowStride) {
        int color = buffer.getInt((y * rowStride) + (x * pixelStride));
        int r = color & 255;
        int g = (color >> 8) & 255;
        int b = (color >> 16) & 255;
        return (((r * 8) + (g * 22)) + (b * 2)) >> 5;
    }
}
