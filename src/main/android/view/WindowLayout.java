package android.view;

import android.app.WindowConfiguration;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.window.ClientWindowFrames;
/* loaded from: classes4.dex */
public class WindowLayout {
    private static final boolean DEBUG = false;
    static final int MAX_X = 100000;
    static final int MAX_Y = 100000;
    static final int MIN_X = -100000;
    static final int MIN_Y = -100000;
    private static final String TAG = WindowLayout.class.getSimpleName();
    public static final int UNSPECIFIED_LENGTH = -1;
    private final Rect mTempDisplayCutoutSafeExceptMaybeBarsRect = new Rect();
    private final Rect mTempRect = new Rect();

    /* JADX WARN: Code restructure failed: missing block: B:164:0x0287, code lost:
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x028c, code lost:
        if (r43.type == 1) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x028e, code lost:
        if (r2 != false) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x0291, code lost:
        r17 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void computeFrames(WindowManager.LayoutParams attrs, InsetsState state, Rect displayCutoutSafe, Rect windowBounds, int windowingMode, int requestedWidth, int requestedHeight, int requestedVisibleTypes, float compatScale, ClientWindowFrames frames) {
        Rect outFrame;
        int i;
        int rw;
        int w;
        int rw2;
        float x;
        float y;
        int w2;
        InsetsSource source;
        int type = attrs.type;
        int fl = attrs.flags;
        int pfl = attrs.privateFlags;
        boolean layoutInScreen = (fl & 256) == 256;
        Rect attachedWindowFrame = frames.attachedFrame;
        Rect outDisplayFrame = frames.displayFrame;
        Rect outParentFrame = frames.parentFrame;
        Rect outFrame2 = frames.frame;
        Insets insets = state.calculateInsets(windowBounds, attrs.getFitInsetsTypes(), attrs.isFitInsetsIgnoringVisibility());
        int sides = attrs.getFitInsetsSides();
        int fl2 = (sides & 1) != 0 ? insets.left : 0;
        if ((sides & 2) != 0) {
            outFrame = outFrame2;
            i = insets.top;
        } else {
            outFrame = outFrame2;
            i = 0;
        }
        int top = i;
        int right = (sides & 4) != 0 ? insets.right : 0;
        int bottom = (sides & 8) != 0 ? insets.bottom : 0;
        int i2 = windowBounds.left + fl2;
        int left = windowBounds.top;
        outDisplayFrame.set(i2, left + top, windowBounds.right - right, windowBounds.bottom - bottom);
        if (attachedWindowFrame == null) {
            outParentFrame.set(outDisplayFrame);
            if ((1073741824 & pfl) != 0 && (source = state.peekSource(InsetsSource.ID_IME)) != null) {
                outParentFrame.inset(source.calculateInsets(outParentFrame, false));
            }
        } else {
            outParentFrame.set(!layoutInScreen ? attachedWindowFrame : outDisplayFrame);
        }
        int cutoutMode = attrs.layoutInDisplayCutoutMode;
        DisplayCutout cutout = state.getDisplayCutout();
        Rect displayCutoutSafeExceptMaybeBars = this.mTempDisplayCutoutSafeExceptMaybeBarsRect;
        displayCutoutSafeExceptMaybeBars.set(displayCutoutSafe);
        frames.isParentFrameClippedByDisplayCutout = false;
        if (cutoutMode != 3 && !cutout.isEmpty()) {
            Rect displayFrame = state.getDisplayFrame();
            if (cutoutMode == 1) {
                int width = displayFrame.width();
                int pfl2 = displayFrame.height();
                if (width < pfl2) {
                    displayCutoutSafeExceptMaybeBars.top = -100000;
                    displayCutoutSafeExceptMaybeBars.bottom = 100000;
                } else {
                    displayCutoutSafeExceptMaybeBars.left = -100000;
                    displayCutoutSafeExceptMaybeBars.right = 100000;
                }
            }
            boolean layoutInsetDecor = (attrs.flags & 65536) != 0;
            if (layoutInScreen && layoutInsetDecor) {
                if (cutoutMode == 0 || cutoutMode == 1) {
                    Insets systemBarsInsets = state.calculateInsets(displayFrame, WindowInsets.Type.systemBars(), requestedVisibleTypes);
                    if (systemBarsInsets.left > 0) {
                        displayCutoutSafeExceptMaybeBars.left = -100000;
                    }
                    if (systemBarsInsets.top > 0) {
                        displayCutoutSafeExceptMaybeBars.top = -100000;
                    }
                    if (systemBarsInsets.right > 0) {
                        displayCutoutSafeExceptMaybeBars.right = 100000;
                    }
                    if (systemBarsInsets.bottom > 0) {
                        displayCutoutSafeExceptMaybeBars.bottom = 100000;
                    }
                }
            }
            if (type == 2011 && displayCutoutSafeExceptMaybeBars.bottom != 100000 && state.calculateInsets(displayFrame, WindowInsets.Type.navigationBars(), true).bottom > 0) {
                displayCutoutSafeExceptMaybeBars.bottom = 100000;
            }
            boolean attachedInParent = (attachedWindowFrame == null || layoutInScreen) ? false : true;
            boolean floatingInScreenWindow = (attrs.isFullscreen() || !layoutInScreen || type == 1) ? false : true;
            if (!attachedInParent && !floatingInScreenWindow) {
                this.mTempRect.set(outParentFrame);
                outParentFrame.intersectUnchecked(displayCutoutSafeExceptMaybeBars);
                frames.isParentFrameClippedByDisplayCutout = !this.mTempRect.equals(outParentFrame);
            }
            outDisplayFrame.intersectUnchecked(displayCutoutSafeExceptMaybeBars);
        }
        boolean noLimits = (attrs.flags & 512) != 0;
        boolean inMultiWindowMode = WindowConfiguration.inMultiWindowMode(windowingMode);
        if (noLimits && type != 2010 && !inMultiWindowMode) {
            outDisplayFrame.left = -100000;
            outDisplayFrame.top = -100000;
            outDisplayFrame.right = 100000;
            outDisplayFrame.bottom = 100000;
        }
        boolean hasCompatScale = compatScale != 1.0f;
        int pw = outParentFrame.width();
        int ph = outParentFrame.height();
        boolean extendedByCutout = (attrs.privateFlags & 8192) != 0;
        if (requestedWidth != -1 && !extendedByCutout) {
            rw = requestedWidth;
        } else {
            rw = attrs.width >= 0 ? attrs.width : pw;
        }
        int rh = requestedHeight;
        if (rh == -1 || extendedByCutout) {
            rh = attrs.height >= 0 ? attrs.height : ph;
        }
        if ((attrs.flags & 16384) != 0) {
            if (attrs.width < 0) {
                w2 = pw;
            } else if (hasCompatScale) {
                w2 = (int) ((attrs.width * compatScale) + 0.5f);
            } else {
                w2 = attrs.width;
            }
            int w3 = w2;
            int w4 = attrs.height;
            if (w4 < 0) {
                rw2 = ph;
                w = w3;
            } else if (hasCompatScale) {
                rw2 = (int) ((attrs.height * compatScale) + 0.5f);
                w = w3;
            } else {
                int h = attrs.height;
                rw2 = h;
                w = w3;
            }
        } else {
            int h2 = attrs.width;
            if (h2 == -1) {
                w = pw;
            } else if (hasCompatScale) {
                w = (int) ((rw * compatScale) + 0.5f);
            } else {
                w = rw;
            }
            if (attrs.height == -1) {
                rw2 = ph;
            } else if (hasCompatScale) {
                rw2 = (int) ((rh * compatScale) + 0.5f);
            } else {
                rw2 = rh;
            }
        }
        if (hasCompatScale) {
            x = attrs.f504x * compatScale;
            y = attrs.f505y * compatScale;
        } else {
            x = attrs.f504x;
            y = attrs.f505y;
        }
        if (inMultiWindowMode && (attrs.privateFlags & 65536) == 0) {
            w = Math.min(w, pw);
            rw2 = Math.min(rw2, ph);
        }
        boolean inMultiWindowMode2 = true;
        boolean z = inMultiWindowMode2;
        boolean fitToDisplay = z;
        float x2 = ph;
        Rect outFrame3 = outFrame;
        Gravity.apply(attrs.gravity, w, rw2, outParentFrame, (int) ((attrs.horizontalMargin * pw) + x), (int) ((attrs.verticalMargin * x2) + y), outFrame3);
        if (fitToDisplay) {
            Gravity.applyDisplay(attrs.gravity, outDisplayFrame, outFrame3);
        }
        if (extendedByCutout) {
            extendFrameByCutout(displayCutoutSafe, outDisplayFrame, outFrame3, this.mTempRect);
        }
    }

    public static void extendFrameByCutout(Rect displayCutoutSafe, Rect displayFrame, Rect inOutFrame, Rect tempRect) {
        if (displayCutoutSafe.contains(inOutFrame)) {
            return;
        }
        tempRect.set(inOutFrame);
        Gravity.applyDisplay(0, displayCutoutSafe, tempRect);
        if (tempRect.intersect(displayFrame)) {
            inOutFrame.union(tempRect);
        }
    }

    public static void computeSurfaceSize(WindowManager.LayoutParams attrs, Rect maxBounds, int requestedWidth, int requestedHeight, Rect winFrame, boolean dragResizing, Point outSurfaceSize) {
        int width;
        int height;
        if ((attrs.flags & 16384) != 0) {
            width = requestedWidth;
            height = requestedHeight;
        } else if (dragResizing) {
            width = maxBounds.width();
            height = maxBounds.height();
        } else {
            width = winFrame.width();
            height = winFrame.height();
        }
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        Rect surfaceInsets = attrs.surfaceInsets;
        outSurfaceSize.set(width + surfaceInsets.left + surfaceInsets.right, height + surfaceInsets.top + surfaceInsets.bottom);
    }
}
