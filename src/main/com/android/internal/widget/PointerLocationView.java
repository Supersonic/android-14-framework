package com.android.internal.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.ISystemGestureExclusionListener;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.view.WindowManagerGlobal;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.widget.PointerLocationView;
/* loaded from: classes5.dex */
public class PointerLocationView extends View implements InputManager.InputDeviceListener, WindowManagerPolicyConstants.PointerEventListener {
    private static final String ALT_STRATEGY_PROPERY_KEY = "debug.velocitytracker.alt";
    private static final PointerState EMPTY_POINTER_STATE = new PointerState();
    private static final String GESTURE_EXCLUSION_PROP = "debug.pointerlocation.showexclusion";
    private static final String TAG = "Pointer";
    private int mActivePointerId;
    private final VelocityTracker mAltVelocity;
    private boolean mCurDown;
    private int mCurNumPointers;
    private final Paint mCurrentPointPaint;
    private int mHeaderBottom;
    private int mHeaderPaddingTop;
    private final InputManager mIm;
    private int mMaxNumPointers;
    private final Paint mPaint;
    private final Paint mPathPaint;
    private final SparseArray<PointerState> mPointers;
    private boolean mPrintCoords;
    private RectF mReusableOvalRect;
    private final Region mSystemGestureExclusion;
    private ISystemGestureExclusionListener mSystemGestureExclusionListener;
    private final Paint mSystemGestureExclusionPaint;
    private final Path mSystemGestureExclusionPath;
    private final Region mSystemGestureExclusionRejected;
    private final Paint mSystemGestureExclusionRejectedPaint;
    private final Paint mTargetPaint;
    private final MotionEvent.PointerCoords mTempCoords;
    private final FasterStringBuilder mText;
    private final Paint mTextBackgroundPaint;
    private final Paint mTextLevelPaint;
    private final Paint.FontMetricsInt mTextMetrics;
    private final Paint mTextPaint;
    private final ViewConfiguration mVC;
    private final VelocityTracker mVelocity;
    private Insets mWaterfallInsets;

    /* loaded from: classes5.dex */
    public static class PointerState {
        private float mAltXVelocity;
        private float mAltYVelocity;
        private float mBoundingBottom;
        private float mBoundingLeft;
        private float mBoundingRight;
        private float mBoundingTop;
        private boolean mCurDown;
        private boolean mHasBoundingBox;
        private int mToolType;
        private int mTraceCount;
        private float mXVelocity;
        private float mYVelocity;
        private float[] mTraceX = new float[32];
        private float[] mTraceY = new float[32];
        private boolean[] mTraceCurrent = new boolean[32];
        private MotionEvent.PointerCoords mCoords = new MotionEvent.PointerCoords();

        public void clearTrace() {
            this.mTraceCount = 0;
        }

        public void addTrace(float x, float y, boolean current) {
            float[] fArr = this.mTraceX;
            int traceCapacity = fArr.length;
            int i = this.mTraceCount;
            if (i == traceCapacity) {
                int traceCapacity2 = traceCapacity * 2;
                float[] newTraceX = new float[traceCapacity2];
                System.arraycopy(fArr, 0, newTraceX, 0, i);
                this.mTraceX = newTraceX;
                float[] newTraceY = new float[traceCapacity2];
                System.arraycopy(this.mTraceY, 0, newTraceY, 0, this.mTraceCount);
                this.mTraceY = newTraceY;
                boolean[] newTraceCurrent = new boolean[traceCapacity2];
                System.arraycopy(this.mTraceCurrent, 0, newTraceCurrent, 0, this.mTraceCount);
                this.mTraceCurrent = newTraceCurrent;
            }
            float[] newTraceY2 = this.mTraceX;
            int i2 = this.mTraceCount;
            newTraceY2[i2] = x;
            this.mTraceY[i2] = y;
            this.mTraceCurrent[i2] = current;
            this.mTraceCount = i2 + 1;
        }
    }

    public PointerLocationView(Context c) {
        super(c);
        this.mTextMetrics = new Paint.FontMetricsInt();
        this.mHeaderPaddingTop = 0;
        this.mWaterfallInsets = Insets.NONE;
        this.mPointers = new SparseArray<>();
        this.mTempCoords = new MotionEvent.PointerCoords();
        this.mSystemGestureExclusion = new Region();
        this.mSystemGestureExclusionRejected = new Region();
        this.mSystemGestureExclusionPath = new Path();
        this.mText = new FasterStringBuilder();
        this.mPrintCoords = true;
        this.mReusableOvalRect = new RectF();
        this.mSystemGestureExclusionListener = new BinderC44491();
        setFocusableInTouchMode(true);
        this.mIm = (InputManager) c.getSystemService(InputManager.class);
        this.mVC = ViewConfiguration.get(c);
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setAntiAlias(true);
        paint.setARGB(255, 0, 0, 0);
        Paint paint2 = new Paint();
        this.mTextBackgroundPaint = paint2;
        paint2.setAntiAlias(false);
        paint2.setARGB(128, 255, 255, 255);
        Paint paint3 = new Paint();
        this.mTextLevelPaint = paint3;
        paint3.setAntiAlias(false);
        paint3.setARGB(192, 255, 0, 0);
        Paint paint4 = new Paint();
        this.mPaint = paint4;
        paint4.setAntiAlias(true);
        paint4.setARGB(255, 255, 255, 255);
        paint4.setStyle(Paint.Style.STROKE);
        Paint paint5 = new Paint();
        this.mCurrentPointPaint = paint5;
        paint5.setAntiAlias(true);
        paint5.setARGB(255, 255, 0, 0);
        paint5.setStyle(Paint.Style.STROKE);
        Paint paint6 = new Paint();
        this.mTargetPaint = paint6;
        paint6.setAntiAlias(false);
        paint6.setARGB(255, 0, 0, 192);
        Paint paint7 = new Paint();
        this.mPathPaint = paint7;
        paint7.setAntiAlias(false);
        paint7.setARGB(255, 0, 96, 255);
        paint7.setStyle(Paint.Style.STROKE);
        configureDensityDependentFactors();
        Paint paint8 = new Paint();
        this.mSystemGestureExclusionPaint = paint8;
        paint8.setARGB(25, 255, 0, 0);
        paint8.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint paint9 = new Paint();
        this.mSystemGestureExclusionRejectedPaint = paint9;
        paint9.setARGB(25, 0, 0, 255);
        paint9.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mActivePointerId = 0;
        this.mVelocity = VelocityTracker.obtain();
        String altStrategy = SystemProperties.get(ALT_STRATEGY_PROPERY_KEY);
        if (altStrategy.length() != 0) {
            Log.m112d(TAG, "Comparing default velocity tracker strategy with " + altStrategy);
            this.mAltVelocity = VelocityTracker.obtain(altStrategy);
            return;
        }
        this.mAltVelocity = null;
    }

    public void setPrintCoords(boolean state) {
        this.mPrintCoords = state;
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int headerPaddingTop = 0;
        Insets waterfallInsets = Insets.NONE;
        RoundedCorner topLeftRounded = insets.getRoundedCorner(0);
        if (topLeftRounded != null) {
            headerPaddingTop = topLeftRounded.getRadius();
        }
        RoundedCorner topRightRounded = insets.getRoundedCorner(1);
        if (topRightRounded != null) {
            headerPaddingTop = Math.max(headerPaddingTop, topRightRounded.getRadius());
        }
        if (insets.getDisplayCutout() != null) {
            headerPaddingTop = Math.max(headerPaddingTop, insets.getDisplayCutout().getSafeInsetTop());
            waterfallInsets = insets.getDisplayCutout().getWaterfallInsets();
        }
        this.mHeaderPaddingTop = headerPaddingTop;
        this.mWaterfallInsets = waterfallInsets;
        return super.onApplyWindowInsets(insets);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mTextPaint.getFontMetricsInt(this.mTextMetrics);
        this.mHeaderBottom = (this.mHeaderPaddingTop - this.mTextMetrics.ascent) + this.mTextMetrics.descent + 2;
    }

    private void drawOval(Canvas canvas, float x, float y, float major, float minor, float angle, Paint paint) {
        canvas.save(1);
        canvas.rotate((float) ((180.0f * angle) / 3.141592653589793d), x, y);
        this.mReusableOvalRect.left = x - (minor / 2.0f);
        this.mReusableOvalRect.right = (minor / 2.0f) + x;
        this.mReusableOvalRect.top = y - (major / 2.0f);
        this.mReusableOvalRect.bottom = (major / 2.0f) + y;
        canvas.drawOval(this.mReusableOvalRect, paint);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int NP;
        float arrowSize;
        int i;
        if (!this.mSystemGestureExclusion.isEmpty()) {
            this.mSystemGestureExclusionPath.reset();
            this.mSystemGestureExclusion.getBoundaryPath(this.mSystemGestureExclusionPath);
            canvas.drawPath(this.mSystemGestureExclusionPath, this.mSystemGestureExclusionPaint);
        }
        if (!this.mSystemGestureExclusionRejected.isEmpty()) {
            this.mSystemGestureExclusionPath.reset();
            this.mSystemGestureExclusionRejected.getBoundaryPath(this.mSystemGestureExclusionPath);
            canvas.drawPath(this.mSystemGestureExclusionPath, this.mSystemGestureExclusionRejectedPaint);
        }
        drawLabels(canvas);
        int p = 0;
        for (int NP2 = this.mPointers.size(); p < NP2; NP2 = NP) {
            PointerState ps = this.mPointers.valueAt(p);
            int N = ps.mTraceCount;
            this.mPaint.setARGB(255, 128, 255, 255);
            float lastX = 0.0f;
            float lastY = 0.0f;
            boolean haveLast = false;
            boolean drawn = false;
            int i2 = 0;
            while (i2 < N) {
                float x = ps.mTraceX[i2];
                float y = ps.mTraceY[i2];
                if (Float.isNaN(x)) {
                    i = i2;
                } else if (Float.isNaN(y)) {
                    i = i2;
                } else {
                    if (!haveLast) {
                        i = i2;
                    } else {
                        i = i2;
                        canvas.drawLine(lastX, lastY, x, y, this.mPathPaint);
                        Paint paint = ps.mTraceCurrent[i + (-1)] ? this.mCurrentPointPaint : this.mPaint;
                        canvas.drawPoint(lastX, lastY, paint);
                        drawn = true;
                    }
                    lastX = x;
                    lastY = y;
                    haveLast = true;
                    i2 = i + 1;
                }
                haveLast = false;
                i2 = i + 1;
            }
            if (!drawn) {
                NP = NP2;
            } else {
                this.mPaint.setARGB(255, 255, 64, 128);
                float xVel = ps.mXVelocity * 16.0f;
                float yVel = ps.mYVelocity * 16.0f;
                NP = NP2;
                canvas.drawLine(lastX, lastY, lastX + xVel, lastY + yVel, this.mPaint);
                if (this.mAltVelocity != null) {
                    this.mPaint.setARGB(255, 64, 255, 128);
                    float xVel2 = ps.mAltXVelocity * 16.0f;
                    float yVel2 = 16.0f * ps.mAltYVelocity;
                    canvas.drawLine(lastX, lastY, lastX + xVel2, lastY + yVel2, this.mPaint);
                }
            }
            if (this.mCurDown && ps.mCurDown) {
                canvas.drawLine(0.0f, ps.mCoords.f483y, getWidth(), ps.mCoords.f483y, this.mTargetPaint);
                canvas.drawLine(ps.mCoords.f482x, -getHeight(), ps.mCoords.f482x, Math.max(getHeight(), getWidth()), this.mTargetPaint);
                int pressureLevel = (int) (ps.mCoords.pressure * 255.0f);
                this.mPaint.setARGB(255, pressureLevel, 255, 255 - pressureLevel);
                canvas.drawPoint(ps.mCoords.f482x, ps.mCoords.f483y, this.mPaint);
                this.mPaint.setARGB(255, pressureLevel, 255 - pressureLevel, 128);
                drawOval(canvas, ps.mCoords.f482x, ps.mCoords.f483y, ps.mCoords.touchMajor, ps.mCoords.touchMinor, ps.mCoords.orientation, this.mPaint);
                this.mPaint.setARGB(255, pressureLevel, 128, 255 - pressureLevel);
                drawOval(canvas, ps.mCoords.f482x, ps.mCoords.f483y, ps.mCoords.toolMajor, ps.mCoords.toolMinor, ps.mCoords.orientation, this.mPaint);
                float arrowSize2 = ps.mCoords.toolMajor * 0.7f;
                if (arrowSize2 >= 20.0f) {
                    arrowSize = arrowSize2;
                } else {
                    arrowSize = 20.0f;
                }
                this.mPaint.setARGB(255, pressureLevel, 255, 0);
                float orientationVectorX = (float) (Math.sin(ps.mCoords.orientation) * arrowSize);
                float orientationVectorY = (float) ((-Math.cos(ps.mCoords.orientation)) * arrowSize);
                if (ps.mToolType == 2 || ps.mToolType == 4) {
                    canvas.drawLine(ps.mCoords.f482x, ps.mCoords.f483y, ps.mCoords.f482x + orientationVectorX, ps.mCoords.f483y + orientationVectorY, this.mPaint);
                } else {
                    canvas.drawLine(ps.mCoords.f482x - orientationVectorX, ps.mCoords.f483y - orientationVectorY, ps.mCoords.f482x + orientationVectorX, ps.mCoords.f483y + orientationVectorY, this.mPaint);
                }
                float tiltScale = (float) Math.sin(ps.mCoords.getAxisValue(25));
                canvas.drawCircle(ps.mCoords.f482x + (orientationVectorX * tiltScale), ps.mCoords.f483y + (orientationVectorY * tiltScale), 3.0f, this.mPaint);
                if (ps.mHasBoundingBox) {
                    canvas.drawRect(ps.mBoundingLeft, ps.mBoundingTop, ps.mBoundingRight, ps.mBoundingBottom, this.mPaint);
                }
            }
            p++;
        }
    }

    private void drawLabels(Canvas canvas) {
        Paint paint;
        Paint paint2;
        int w = (getWidth() - this.mWaterfallInsets.left) - this.mWaterfallInsets.right;
        int itemW = w / 7;
        int base = (this.mHeaderPaddingTop - this.mTextMetrics.ascent) + 1;
        int bottom = this.mHeaderBottom;
        canvas.save();
        canvas.translate(this.mWaterfallInsets.left, 0.0f);
        PointerState ps = this.mPointers.get(this.mActivePointerId, EMPTY_POINTER_STATE);
        canvas.drawRect(0.0f, this.mHeaderPaddingTop, itemW - 1, bottom, this.mTextBackgroundPaint);
        canvas.drawText(this.mText.clear().append("P: ").append(this.mCurNumPointers).append(" / ").append(this.mMaxNumPointers).toString(), 1.0f, base, this.mTextPaint);
        int count = ps.mTraceCount;
        if ((this.mCurDown && ps.mCurDown) || count == 0) {
            canvas.drawRect(itemW, this.mHeaderPaddingTop, (itemW * 2) - 1, bottom, this.mTextBackgroundPaint);
            canvas.drawText(this.mText.clear().append("X: ").append(ps.mCoords.f482x, 1).toString(), itemW + 1, base, this.mTextPaint);
            canvas.drawRect(itemW * 2, this.mHeaderPaddingTop, (itemW * 3) - 1, bottom, this.mTextBackgroundPaint);
            canvas.drawText(this.mText.clear().append("Y: ").append(ps.mCoords.f483y, 1).toString(), (itemW * 2) + 1, base, this.mTextPaint);
        } else {
            float dx = ps.mTraceX[count - 1] - ps.mTraceX[0];
            float dy = ps.mTraceY[count - 1] - ps.mTraceY[0];
            float f = itemW;
            float f2 = this.mHeaderPaddingTop;
            float f3 = (itemW * 2) - 1;
            float f4 = bottom;
            if (Math.abs(dx) >= this.mVC.getScaledTouchSlop()) {
                paint = this.mTextLevelPaint;
            } else {
                paint = this.mTextBackgroundPaint;
            }
            canvas.drawRect(f, f2, f3, f4, paint);
            canvas.drawText(this.mText.clear().append("dX: ").append(dx, 1).toString(), itemW + 1, base, this.mTextPaint);
            float f5 = itemW * 2;
            float f6 = this.mHeaderPaddingTop;
            float f7 = (itemW * 3) - 1;
            float f8 = bottom;
            if (Math.abs(dy) >= this.mVC.getScaledTouchSlop()) {
                paint2 = this.mTextLevelPaint;
            } else {
                paint2 = this.mTextBackgroundPaint;
            }
            canvas.drawRect(f5, f6, f7, f8, paint2);
            canvas.drawText(this.mText.clear().append("dY: ").append(dy, 1).toString(), (itemW * 2) + 1, base, this.mTextPaint);
        }
        canvas.drawRect(itemW * 3, this.mHeaderPaddingTop, (itemW * 4) - 1, bottom, this.mTextBackgroundPaint);
        canvas.drawText(this.mText.clear().append("Xv: ").append(ps.mXVelocity, 3).toString(), (itemW * 3) + 1, base, this.mTextPaint);
        canvas.drawRect(itemW * 4, this.mHeaderPaddingTop, (itemW * 5) - 1, bottom, this.mTextBackgroundPaint);
        canvas.drawText(this.mText.clear().append("Yv: ").append(ps.mYVelocity, 3).toString(), (itemW * 4) + 1, base, this.mTextPaint);
        canvas.drawRect(itemW * 5, this.mHeaderPaddingTop, (itemW * 6) - 1, bottom, this.mTextBackgroundPaint);
        canvas.drawRect(itemW * 5, this.mHeaderPaddingTop, ((itemW * 5) + (ps.mCoords.pressure * itemW)) - 1.0f, bottom, this.mTextLevelPaint);
        canvas.drawText(this.mText.clear().append("Prs: ").append(ps.mCoords.pressure, 2).toString(), (itemW * 5) + 1, base, this.mTextPaint);
        canvas.drawRect(itemW * 6, this.mHeaderPaddingTop, w, bottom, this.mTextBackgroundPaint);
        canvas.drawRect(itemW * 6, this.mHeaderPaddingTop, ((itemW * 6) + (ps.mCoords.size * itemW)) - 1.0f, bottom, this.mTextLevelPaint);
        canvas.drawText(this.mText.clear().append("Size: ").append(ps.mCoords.size, 2).toString(), (itemW * 6) + 1, base, this.mTextPaint);
        canvas.restore();
    }

    private void logMotionEvent(String type, MotionEvent event) {
        int action = event.getAction();
        int N = event.getHistorySize();
        int NI = event.getPointerCount();
        for (int historyPos = 0; historyPos < N; historyPos++) {
            for (int i = 0; i < NI; i++) {
                int id = event.getPointerId(i);
                event.getHistoricalPointerCoords(i, historyPos, this.mTempCoords);
                logCoords(type, action, i, this.mTempCoords, id, event);
            }
        }
        for (int i2 = 0; i2 < NI; i2++) {
            int id2 = event.getPointerId(i2);
            event.getPointerCoords(i2, this.mTempCoords);
            logCoords(type, action, i2, this.mTempCoords, id2, event);
        }
    }

    private void logCoords(String type, int action, int index, MotionEvent.PointerCoords coords, int id, MotionEvent event) {
        String prefix;
        int toolType = event.getToolType(index);
        int buttonState = event.getButtonState();
        switch (action & 255) {
            case 0:
                prefix = "DOWN";
                break;
            case 1:
                prefix = "UP";
                break;
            case 2:
                prefix = "MOVE";
                break;
            case 3:
                prefix = "CANCEL";
                break;
            case 4:
                prefix = "OUTSIDE";
                break;
            case 5:
                if (index == ((action & 65280) >> 8)) {
                    prefix = "DOWN";
                    break;
                } else {
                    prefix = "MOVE";
                    break;
                }
            case 6:
                if (index == ((action & 65280) >> 8)) {
                    prefix = "UP";
                    break;
                } else {
                    prefix = "MOVE";
                    break;
                }
            case 7:
                prefix = "HOVER MOVE";
                break;
            case 8:
                prefix = "SCROLL";
                break;
            case 9:
                prefix = "HOVER ENTER";
                break;
            case 10:
                prefix = "HOVER EXIT";
                break;
            default:
                prefix = Integer.toString(action);
                break;
        }
        Log.m108i(TAG, this.mText.clear().append(type).append(" id ").append(id + 1).append(": ").append(prefix).append(" (").append(coords.f482x, 3).append(", ").append(coords.f483y, 3).append(") Pressure=").append(coords.pressure, 3).append(" Size=").append(coords.size, 3).append(" TouchMajor=").append(coords.touchMajor, 3).append(" TouchMinor=").append(coords.touchMinor, 3).append(" ToolMajor=").append(coords.toolMajor, 3).append(" ToolMinor=").append(coords.toolMinor, 3).append(" Orientation=").append((float) ((coords.orientation * 180.0f) / 3.141592653589793d), 1).append("deg").append(" Tilt=").append((float) ((coords.getAxisValue(25) * 180.0f) / 3.141592653589793d), 1).append("deg").append(" Distance=").append(coords.getAxisValue(24), 1).append(" VScroll=").append(coords.getAxisValue(9), 1).append(" HScroll=").append(coords.getAxisValue(10), 1).append(" BoundingBox=[(").append(event.getAxisValue(32), 3).append(", ").append(event.getAxisValue(33), 3).append(NavigationBarInflaterView.KEY_CODE_END).append(", (").append(event.getAxisValue(34), 3).append(", ").append(event.getAxisValue(35), 3).append(")]").append(" ToolType=").append(MotionEvent.toolTypeToString(toolType)).append(" ButtonState=").append(MotionEvent.buttonStateToString(buttonState)).toString());
    }

    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8, types: [int, boolean] */
    @Override // android.view.WindowManagerPolicyConstants.PointerEventListener
    public void onPointerEvent(MotionEvent event) {
        ?? r4;
        MotionEvent.PointerCoords coords;
        PointerState ps;
        int id;
        char c;
        MotionEvent.PointerCoords coords2;
        PointerState ps2;
        int i;
        int historyPos;
        int action = event.getAction();
        if (action == 0 || (action & 255) == 5) {
            int index = (action & 65280) >> 8;
            if (action == 0) {
                this.mPointers.clear();
                this.mCurDown = true;
                this.mCurNumPointers = 0;
                this.mMaxNumPointers = 0;
                this.mVelocity.clear();
                VelocityTracker velocityTracker = this.mAltVelocity;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            }
            int i2 = this.mCurNumPointers + 1;
            this.mCurNumPointers = i2;
            if (this.mMaxNumPointers < i2) {
                this.mMaxNumPointers = i2;
            }
            int id2 = event.getPointerId(index);
            PointerState ps3 = this.mPointers.get(id2);
            if (ps3 == null) {
                ps3 = new PointerState();
                this.mPointers.put(id2, ps3);
            }
            if (!this.mPointers.contains(this.mActivePointerId) || !this.mPointers.get(this.mActivePointerId).mCurDown) {
                this.mActivePointerId = id2;
            }
            ps3.mCurDown = true;
            InputDevice device = InputDevice.getDevice(event.getDeviceId());
            ps3.mHasBoundingBox = (device == null || device.getMotionRange(32) == null) ? false : true;
        }
        int NI = event.getPointerCount();
        this.mVelocity.addMovement(event);
        this.mVelocity.computeCurrentVelocity(1);
        VelocityTracker velocityTracker2 = this.mAltVelocity;
        if (velocityTracker2 != null) {
            velocityTracker2.addMovement(event);
            this.mAltVelocity.computeCurrentVelocity(1);
        }
        int N = event.getHistorySize();
        int historyPos2 = 0;
        while (historyPos2 < N) {
            int i3 = 0;
            while (i3 < NI) {
                int id3 = event.getPointerId(i3);
                PointerState ps4 = this.mCurDown ? this.mPointers.get(id3) : null;
                MotionEvent.PointerCoords coords3 = ps4 != null ? ps4.mCoords : this.mTempCoords;
                event.getHistoricalPointerCoords(i3, historyPos2, coords3);
                if (!this.mPrintCoords) {
                    coords2 = coords3;
                    ps2 = ps4;
                    i = i3;
                    historyPos = historyPos2;
                } else {
                    coords2 = coords3;
                    ps2 = ps4;
                    i = i3;
                    historyPos = historyPos2;
                    logCoords(TAG, action, i3, coords2, id3, event);
                }
                if (ps2 != null) {
                    MotionEvent.PointerCoords coords4 = coords2;
                    ps2.addTrace(coords4.f482x, coords4.f483y, false);
                }
                i3 = i + 1;
                historyPos2 = historyPos;
            }
            historyPos2++;
        }
        for (int i4 = 0; i4 < NI; i4++) {
            int id4 = event.getPointerId(i4);
            PointerState ps5 = this.mCurDown ? this.mPointers.get(id4) : null;
            MotionEvent.PointerCoords coords5 = ps5 != null ? ps5.mCoords : this.mTempCoords;
            event.getPointerCoords(i4, coords5);
            if (!this.mPrintCoords) {
                coords = coords5;
                ps = ps5;
                id = id4;
            } else {
                coords = coords5;
                ps = ps5;
                id = id4;
                logCoords(TAG, action, i4, coords5, id4, event);
            }
            if (ps != null) {
                MotionEvent.PointerCoords coords6 = coords;
                ps.addTrace(coords6.f482x, coords6.f483y, true);
                ps.mXVelocity = this.mVelocity.getXVelocity(id);
                ps.mYVelocity = this.mVelocity.getYVelocity(id);
                VelocityTracker velocityTracker3 = this.mAltVelocity;
                if (velocityTracker3 != null) {
                    ps.mAltXVelocity = velocityTracker3.getXVelocity(id);
                    ps.mAltYVelocity = this.mAltVelocity.getYVelocity(id);
                }
                ps.mToolType = event.getToolType(i4);
                if (!ps.mHasBoundingBox) {
                    c = ' ';
                } else {
                    c = ' ';
                    ps.mBoundingLeft = event.getAxisValue(32, i4);
                    ps.mBoundingTop = event.getAxisValue(33, i4);
                    ps.mBoundingRight = event.getAxisValue(34, i4);
                    ps.mBoundingBottom = event.getAxisValue(35, i4);
                }
            } else {
                c = ' ';
            }
        }
        if (action == 1 || action == 3 || (action & 255) == 6) {
            int index2 = (65280 & action) >> 8;
            int id5 = event.getPointerId(index2);
            PointerState ps6 = this.mPointers.get(id5);
            if (ps6 == null) {
                Slog.wtf(TAG, "Could not find pointer id=" + id5 + " in mPointers map, size=" + this.mPointers.size() + " pointerindex=" + index2 + " action=0x" + Integer.toHexString(action));
                return;
            }
            ps6.mCurDown = false;
            if (action != 1) {
                if (action == 3) {
                    r4 = 0;
                } else {
                    this.mCurNumPointers--;
                    if (this.mActivePointerId == id5) {
                        this.mActivePointerId = event.getPointerId(index2 != 0 ? 0 : 1);
                    }
                    ps6.addTrace(Float.NaN, Float.NaN, false);
                }
            } else {
                r4 = 0;
            }
            this.mCurDown = r4;
            this.mCurNumPointers = r4;
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        onPointerEvent(event);
        if (event.getAction() == 0 && !isFocused()) {
            requestFocus();
            return true;
        }
        return true;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        int source = event.getSource();
        if ((source & 2) != 0) {
            onPointerEvent(event);
            return true;
        } else if ((source & 16) != 0) {
            logMotionEvent("Joystick", event);
            return true;
        } else if ((source & 8) != 0) {
            logMotionEvent("Position", event);
            return true;
        } else {
            logMotionEvent("Generic", event);
            return true;
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (shouldLogKey(keyCode)) {
            int repeatCount = event.getRepeatCount();
            if (repeatCount == 0) {
                Log.m108i(TAG, "Key Down: " + event);
                return true;
            }
            Log.m108i(TAG, "Key Repeat #" + repeatCount + ": " + event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (shouldLogKey(keyCode)) {
            Log.m108i(TAG, "Key Up: " + event);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private static boolean shouldLogKey(int keyCode) {
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                if (KeyEvent.isGamepadButton(keyCode) || KeyEvent.isModifierKey(keyCode)) {
                    return true;
                }
                return false;
        }
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent event) {
        logMotionEvent("Trackball", event);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIm.registerInputDeviceListener(this, getHandler());
        if (shouldShowSystemGestureExclusion()) {
            try {
                WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mSystemGestureExclusionListener, this.mContext.getDisplayId());
                int alpha = systemGestureExclusionOpacity();
                this.mSystemGestureExclusionPaint.setAlpha(alpha);
                this.mSystemGestureExclusionRejectedPaint.setAlpha(alpha);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } else {
            this.mSystemGestureExclusion.setEmpty();
        }
        logInputDevices();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIm.unregisterInputDeviceListener(this);
        try {
            WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mSystemGestureExclusionListener, this.mContext.getDisplayId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (IllegalArgumentException e2) {
            Log.m109e(TAG, "Failed to unregister window manager callbacks", e2);
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int deviceId) {
        logInputDeviceState(deviceId, "Device Added");
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int deviceId) {
        logInputDeviceState(deviceId, "Device Changed");
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int deviceId) {
        logInputDeviceState(deviceId, "Device Removed");
    }

    private void logInputDevices() {
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int i : deviceIds) {
            logInputDeviceState(i, "Device Enumerated");
        }
    }

    private void logInputDeviceState(int deviceId, String state) {
        InputDevice device = this.mIm.getInputDevice(deviceId);
        if (device != null) {
            Log.m108i(TAG, state + ": " + device);
        } else {
            Log.m108i(TAG, state + ": " + deviceId);
        }
    }

    private static boolean shouldShowSystemGestureExclusion() {
        return systemGestureExclusionOpacity() > 0;
    }

    private static int systemGestureExclusionOpacity() {
        int x = SystemProperties.getInt(GESTURE_EXCLUSION_PROP, 0);
        if (x < 0 || x > 255) {
            return 0;
        }
        return x;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static final class FasterStringBuilder {
        private char[] mChars = new char[64];
        private int mLength;

        public FasterStringBuilder clear() {
            this.mLength = 0;
            return this;
        }

        public FasterStringBuilder append(String value) {
            int valueLength = value.length();
            int index = reserve(valueLength);
            value.getChars(0, valueLength, this.mChars, index);
            this.mLength += valueLength;
            return this;
        }

        public FasterStringBuilder append(int value) {
            return append(value, 0);
        }

        public FasterStringBuilder append(int value, int zeroPadWidth) {
            boolean negative = value < 0;
            if (negative && (value = -value) < 0) {
                append("-2147483648");
                return this;
            }
            int index = reserve(11);
            char[] chars = this.mChars;
            if (value == 0) {
                int i = index + 1;
                chars[index] = '0';
                this.mLength++;
                return this;
            }
            if (negative) {
                chars[index] = '-';
                index++;
            }
            int divisor = 1000000000;
            int numberWidth = 10;
            while (value < divisor) {
                divisor /= 10;
                numberWidth--;
                if (numberWidth < zeroPadWidth) {
                    chars[index] = '0';
                    index++;
                }
            }
            while (true) {
                int digit = value / divisor;
                value -= digit * divisor;
                divisor /= 10;
                int index2 = index + 1;
                chars[index] = (char) (digit + 48);
                if (divisor != 0) {
                    index = index2;
                } else {
                    this.mLength = index2;
                    return this;
                }
            }
        }

        public FasterStringBuilder append(float value, int precision) {
            int scale = 1;
            for (int i = 0; i < precision; i++) {
                scale *= 10;
            }
            float value2 = (float) (Math.rint(scale * value) / scale);
            if (((int) value2) == 0 && value2 < 0.0f) {
                append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            }
            append((int) value2);
            if (precision != 0) {
                append(MediaMetrics.SEPARATOR);
                float value3 = Math.abs(value2);
                append((int) (scale * ((float) (value3 - Math.floor(value3)))), precision);
            }
            return this;
        }

        public String toString() {
            return new String(this.mChars, 0, this.mLength);
        }

        private int reserve(int length) {
            int oldLength = this.mLength;
            int newLength = this.mLength + length;
            char[] oldChars = this.mChars;
            int oldCapacity = oldChars.length;
            if (newLength > oldCapacity) {
                int newCapacity = oldCapacity * 2;
                char[] newChars = new char[newCapacity];
                System.arraycopy(oldChars, 0, newChars, 0, oldLength);
                this.mChars = newChars;
            }
            return oldLength;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.widget.PointerLocationView$1 */
    /* loaded from: classes5.dex */
    public class BinderC44491 extends ISystemGestureExclusionListener.Stub {
        BinderC44491() {
        }

        @Override // android.view.ISystemGestureExclusionListener
        public void onSystemGestureExclusionChanged(int displayId, Region systemGestureExclusion, Region systemGestureExclusionUnrestricted) {
            final Region exclusion = Region.obtain(systemGestureExclusion);
            final Region rejected = Region.obtain();
            if (systemGestureExclusionUnrestricted != null) {
                rejected.set(systemGestureExclusionUnrestricted);
                rejected.m177op(exclusion, Region.EnumC0813Op.DIFFERENCE);
            }
            Handler handler = PointerLocationView.this.getHandler();
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.android.internal.widget.PointerLocationView$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PointerLocationView.BinderC44491.this.lambda$onSystemGestureExclusionChanged$0(exclusion, rejected);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemGestureExclusionChanged$0(Region exclusion, Region rejected) {
            PointerLocationView.this.mSystemGestureExclusion.set(exclusion);
            PointerLocationView.this.mSystemGestureExclusionRejected.set(rejected);
            exclusion.recycle();
            PointerLocationView.this.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureDensityDependentFactors();
    }

    private void configureDensityDependentFactors() {
        float density = getResources().getDisplayMetrics().density;
        this.mTextPaint.setTextSize(10.0f * density);
        this.mPaint.setStrokeWidth(density * 1.0f);
        this.mCurrentPointPaint.setStrokeWidth(density * 1.0f);
        this.mPathPaint.setStrokeWidth(1.0f * density);
    }
}
