package android.media;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Cea708CCParser;
import android.media.ClosedCaptionWidget;
import android.p008os.Handler;
import android.p008os.Message;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.RelativeLayout;
import com.android.internal.widget.SubtitleView;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/* compiled from: Cea708CaptionRenderer.java */
/* loaded from: classes2.dex */
class Cea708CCWidget extends ClosedCaptionWidget implements Cea708CCParser.DisplayListener {
    private final CCHandler mCCHandler;

    public Cea708CCWidget(Context context) {
        this(context, null);
    }

    public Cea708CCWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cea708CCWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Cea708CCWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCCHandler = new CCHandler((CCLayout) this.mClosedCaptionLayout);
    }

    @Override // android.media.ClosedCaptionWidget
    public ClosedCaptionWidget.ClosedCaptionLayout createCaptionLayout(Context context) {
        return new CCLayout(context);
    }

    @Override // android.media.Cea708CCParser.DisplayListener
    public void emitEvent(Cea708CCParser.CaptionEvent event) {
        this.mCCHandler.processCaptionEvent(event);
        setSize(getWidth(), getHeight());
        if (this.mListener != null) {
            this.mListener.onChanged(this);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ((ViewGroup) this.mClosedCaptionLayout).draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class ScaledLayout extends ViewGroup {
        private static final boolean DEBUG = false;
        private static final String TAG = "ScaledLayout";
        private static final Comparator<Rect> mRectTopLeftSorter = new Comparator<Rect>() { // from class: android.media.Cea708CCWidget.ScaledLayout.1
            @Override // java.util.Comparator
            public int compare(Rect lhs, Rect rhs) {
                if (lhs.top != rhs.top) {
                    return lhs.top - rhs.top;
                }
                return lhs.left - rhs.left;
            }
        };
        private Rect[] mRectArray;

        public ScaledLayout(Context context) {
            super(context);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* compiled from: Cea708CaptionRenderer.java */
        /* loaded from: classes2.dex */
        public static class ScaledLayoutParams extends ViewGroup.LayoutParams {
            public static final float SCALE_UNSPECIFIED = -1.0f;
            public float scaleEndCol;
            public float scaleEndRow;
            public float scaleStartCol;
            public float scaleStartRow;

            public ScaledLayoutParams(float scaleStartRow, float scaleEndRow, float scaleStartCol, float scaleEndCol) {
                super(-1, -1);
                this.scaleStartRow = scaleStartRow;
                this.scaleEndRow = scaleEndRow;
                this.scaleStartCol = scaleStartCol;
                this.scaleEndCol = scaleEndCol;
            }

            public ScaledLayoutParams(Context context, AttributeSet attrs) {
                super(-1, -1);
            }
        }

        @Override // android.view.ViewGroup
        public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
            return new ScaledLayoutParams(getContext(), attrs);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup
        public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
            return p instanceof ScaledLayoutParams;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            int width = (widthSpecSize - getPaddingLeft()) - getPaddingRight();
            int height = (heightSpecSize - getPaddingTop()) - getPaddingBottom();
            int count = getChildCount();
            this.mRectArray = new Rect[count];
            int i = 0;
            while (i < count) {
                View child = getChildAt(i);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                if (!(params instanceof ScaledLayoutParams)) {
                    throw new RuntimeException("A child of ScaledLayout cannot have the UNSPECIFIED scale factors");
                }
                float scaleStartRow = ((ScaledLayoutParams) params).scaleStartRow;
                float scaleEndRow = ((ScaledLayoutParams) params).scaleEndRow;
                float scaleStartCol = ((ScaledLayoutParams) params).scaleStartCol;
                float scaleEndCol = ((ScaledLayoutParams) params).scaleEndCol;
                if (scaleStartRow < 0.0f || scaleStartRow > 1.0f) {
                    throw new RuntimeException("A child of ScaledLayout should have a range of scaleStartRow between 0 and 1");
                }
                if (scaleEndRow < scaleStartRow || scaleStartRow > 1.0f) {
                    throw new RuntimeException("A child of ScaledLayout should have a range of scaleEndRow between scaleStartRow and 1");
                }
                if (scaleEndCol < 0.0f || scaleEndCol > 1.0f) {
                    throw new RuntimeException("A child of ScaledLayout should have a range of scaleStartCol between 0 and 1");
                }
                if (scaleEndCol < scaleStartCol || scaleEndCol > 1.0f) {
                    throw new RuntimeException("A child of ScaledLayout should have a range of scaleEndCol between scaleStartCol and 1");
                }
                int widthSpecSize2 = widthSpecSize;
                int heightSpecSize2 = heightSpecSize;
                this.mRectArray[i] = new Rect((int) (width * scaleStartCol), (int) (height * scaleStartRow), (int) (width * scaleEndCol), (int) (height * scaleEndRow));
                int childWidthSpec = View.MeasureSpec.makeMeasureSpec((int) (width * (scaleEndCol - scaleStartCol)), 1073741824);
                int childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                child.measure(childWidthSpec, childHeightSpec);
                if (child.getMeasuredHeight() > this.mRectArray[i].height()) {
                    int overflowedHeight = ((child.getMeasuredHeight() - this.mRectArray[i].height()) + 1) / 2;
                    this.mRectArray[i].bottom += overflowedHeight;
                    this.mRectArray[i].top -= overflowedHeight;
                    if (this.mRectArray[i].top < 0) {
                        this.mRectArray[i].bottom -= this.mRectArray[i].top;
                        this.mRectArray[i].top = 0;
                    }
                    if (this.mRectArray[i].bottom > height) {
                        this.mRectArray[i].top -= this.mRectArray[i].bottom - height;
                        this.mRectArray[i].bottom = height;
                    }
                }
                int childHeightSpec2 = View.MeasureSpec.makeMeasureSpec((int) (height * (scaleEndRow - scaleStartRow)), 1073741824);
                child.measure(childWidthSpec, childHeightSpec2);
                i++;
                widthSpecSize = widthSpecSize2;
                heightSpecSize = heightSpecSize2;
            }
            int widthSpecSize3 = widthSpecSize;
            int heightSpecSize3 = heightSpecSize;
            int visibleRectCount = 0;
            int[] visibleRectGroup = new int[count];
            Rect[] visibleRectArray = new Rect[count];
            for (int i2 = 0; i2 < count; i2++) {
                if (getChildAt(i2).getVisibility() == 0) {
                    visibleRectGroup[visibleRectCount] = visibleRectCount;
                    visibleRectArray[visibleRectCount] = this.mRectArray[i2];
                    visibleRectCount++;
                }
            }
            Arrays.sort(visibleRectArray, 0, visibleRectCount, mRectTopLeftSorter);
            for (int i3 = 0; i3 < visibleRectCount - 1; i3++) {
                for (int j = i3 + 1; j < visibleRectCount; j++) {
                    if (Rect.intersects(visibleRectArray[i3], visibleRectArray[j])) {
                        visibleRectGroup[j] = visibleRectGroup[i3];
                        visibleRectArray[j].set(visibleRectArray[j].left, visibleRectArray[i3].bottom, visibleRectArray[j].right, visibleRectArray[i3].bottom + visibleRectArray[j].height());
                    }
                }
            }
            for (int i4 = visibleRectCount - 1; i4 >= 0; i4--) {
                if (visibleRectArray[i4].bottom > height) {
                    int overflowedHeight2 = visibleRectArray[i4].bottom - height;
                    for (int j2 = 0; j2 <= i4; j2++) {
                        if (visibleRectGroup[i4] == visibleRectGroup[j2]) {
                            visibleRectArray[j2].set(visibleRectArray[j2].left, visibleRectArray[j2].top - overflowedHeight2, visibleRectArray[j2].right, visibleRectArray[j2].bottom - overflowedHeight2);
                        }
                    }
                }
            }
            setMeasuredDimension(widthSpecSize3, heightSpecSize3);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int childLeft = this.mRectArray[i].left + paddingLeft;
                    int childTop = this.mRectArray[i].top + paddingTop;
                    int childBottom = this.mRectArray[i].bottom + paddingLeft;
                    int childRight = this.mRectArray[i].right + paddingTop;
                    child.layout(childLeft, childTop, childRight, childBottom);
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    Rect[] rectArr = this.mRectArray;
                    if (i < rectArr.length) {
                        int childLeft = rectArr[i].left + paddingLeft;
                        int childTop = this.mRectArray[i].top + paddingTop;
                        int saveCount = canvas.save();
                        canvas.translate(childLeft, childTop);
                        child.draw(canvas);
                        canvas.restoreToCount(saveCount);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CCLayout extends ScaledLayout implements ClosedCaptionWidget.ClosedCaptionLayout {
        private static final float SAFE_TITLE_AREA_SCALE_END_X = 0.9f;
        private static final float SAFE_TITLE_AREA_SCALE_END_Y = 0.9f;
        private static final float SAFE_TITLE_AREA_SCALE_START_X = 0.1f;
        private static final float SAFE_TITLE_AREA_SCALE_START_Y = 0.1f;
        private final ScaledLayout mSafeTitleAreaLayout;

        public CCLayout(Context context) {
            super(context);
            ScaledLayout scaledLayout = new ScaledLayout(context);
            this.mSafeTitleAreaLayout = scaledLayout;
            addView(scaledLayout, new ScaledLayout.ScaledLayoutParams(0.1f, 0.9f, 0.1f, 0.9f));
        }

        public void addOrUpdateViewToSafeTitleArea(CCWindowLayout captionWindowLayout, ScaledLayout.ScaledLayoutParams scaledLayoutParams) {
            int index = this.mSafeTitleAreaLayout.indexOfChild(captionWindowLayout);
            if (index < 0) {
                this.mSafeTitleAreaLayout.addView(captionWindowLayout, scaledLayoutParams);
            } else {
                this.mSafeTitleAreaLayout.updateViewLayout(captionWindowLayout, scaledLayoutParams);
            }
        }

        public void removeViewFromSafeTitleArea(CCWindowLayout captionWindowLayout) {
            this.mSafeTitleAreaLayout.removeView(captionWindowLayout);
        }

        @Override // android.media.ClosedCaptionWidget.ClosedCaptionLayout
        public void setCaptionStyle(CaptioningManager.CaptionStyle style) {
            int count = this.mSafeTitleAreaLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                CCWindowLayout windowLayout = (CCWindowLayout) this.mSafeTitleAreaLayout.getChildAt(i);
                windowLayout.setCaptionStyle(style);
            }
        }

        @Override // android.media.ClosedCaptionWidget.ClosedCaptionLayout
        public void setFontScale(float fontScale) {
            int count = this.mSafeTitleAreaLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                CCWindowLayout windowLayout = (CCWindowLayout) this.mSafeTitleAreaLayout.getChildAt(i);
                windowLayout.setFontScale(fontScale);
            }
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    static class CCHandler implements Handler.Callback {
        private static final int CAPTION_ALL_WINDOWS_BITMAP = 255;
        private static final long CAPTION_CLEAR_INTERVAL_MS = 60000;
        private static final int CAPTION_WINDOWS_MAX = 8;
        private static final boolean DEBUG = false;
        private static final int MSG_CAPTION_CLEAR = 2;
        private static final int MSG_DELAY_CANCEL = 1;
        private static final String TAG = "CCHandler";
        private static final int TENTHS_OF_SECOND_IN_MILLIS = 100;
        private final CCLayout mCCLayout;
        private CCWindowLayout mCurrentWindowLayout;
        private boolean mIsDelayed = false;
        private final CCWindowLayout[] mCaptionWindowLayouts = new CCWindowLayout[8];
        private final ArrayList<Cea708CCParser.CaptionEvent> mPendingCaptionEvents = new ArrayList<>();
        private final Handler mHandler = new Handler(this);

        public CCHandler(CCLayout ccLayout) {
            this.mCCLayout = ccLayout;
        }

        @Override // android.p008os.Handler.Callback
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    delayCancel();
                    return true;
                case 2:
                    clearWindows(255);
                    return true;
                default:
                    return false;
            }
        }

        public void processCaptionEvent(Cea708CCParser.CaptionEvent event) {
            if (this.mIsDelayed) {
                this.mPendingCaptionEvents.add(event);
                return;
            }
            switch (event.type) {
                case 1:
                    sendBufferToCurrentWindow((String) event.obj);
                    return;
                case 2:
                    sendControlToCurrentWindow(((Character) event.obj).charValue());
                    return;
                case 3:
                    setCurrentWindowLayout(((Integer) event.obj).intValue());
                    return;
                case 4:
                    clearWindows(((Integer) event.obj).intValue());
                    return;
                case 5:
                    displayWindows(((Integer) event.obj).intValue());
                    return;
                case 6:
                    hideWindows(((Integer) event.obj).intValue());
                    return;
                case 7:
                    toggleWindows(((Integer) event.obj).intValue());
                    return;
                case 8:
                    deleteWindows(((Integer) event.obj).intValue());
                    return;
                case 9:
                    delay(((Integer) event.obj).intValue());
                    return;
                case 10:
                    delayCancel();
                    return;
                case 11:
                    reset();
                    return;
                case 12:
                    setPenAttr((Cea708CCParser.CaptionPenAttr) event.obj);
                    return;
                case 13:
                    setPenColor((Cea708CCParser.CaptionPenColor) event.obj);
                    return;
                case 14:
                    setPenLocation((Cea708CCParser.CaptionPenLocation) event.obj);
                    return;
                case 15:
                    setWindowAttr((Cea708CCParser.CaptionWindowAttr) event.obj);
                    return;
                case 16:
                    defineWindow((Cea708CCParser.CaptionWindow) event.obj);
                    return;
                default:
                    return;
            }
        }

        private void setCurrentWindowLayout(int windowId) {
            CCWindowLayout windowLayout;
            if (windowId < 0) {
                return;
            }
            CCWindowLayout[] cCWindowLayoutArr = this.mCaptionWindowLayouts;
            if (windowId >= cCWindowLayoutArr.length || (windowLayout = cCWindowLayoutArr[windowId]) == null) {
                return;
            }
            this.mCurrentWindowLayout = windowLayout;
        }

        private ArrayList<CCWindowLayout> getWindowsFromBitmap(int windowBitmap) {
            CCWindowLayout windowLayout;
            ArrayList<CCWindowLayout> windows = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                if (((1 << i) & windowBitmap) != 0 && (windowLayout = this.mCaptionWindowLayouts[i]) != null) {
                    windows.add(windowLayout);
                }
            }
            return windows;
        }

        private void clearWindows(int windowBitmap) {
            if (windowBitmap == 0) {
                return;
            }
            Iterator<CCWindowLayout> it = getWindowsFromBitmap(windowBitmap).iterator();
            while (it.hasNext()) {
                CCWindowLayout windowLayout = it.next();
                windowLayout.clear();
            }
        }

        private void displayWindows(int windowBitmap) {
            if (windowBitmap == 0) {
                return;
            }
            Iterator<CCWindowLayout> it = getWindowsFromBitmap(windowBitmap).iterator();
            while (it.hasNext()) {
                CCWindowLayout windowLayout = it.next();
                windowLayout.show();
            }
        }

        private void hideWindows(int windowBitmap) {
            if (windowBitmap == 0) {
                return;
            }
            Iterator<CCWindowLayout> it = getWindowsFromBitmap(windowBitmap).iterator();
            while (it.hasNext()) {
                CCWindowLayout windowLayout = it.next();
                windowLayout.hide();
            }
        }

        private void toggleWindows(int windowBitmap) {
            if (windowBitmap == 0) {
                return;
            }
            Iterator<CCWindowLayout> it = getWindowsFromBitmap(windowBitmap).iterator();
            while (it.hasNext()) {
                CCWindowLayout windowLayout = it.next();
                if (windowLayout.isShown()) {
                    windowLayout.hide();
                } else {
                    windowLayout.show();
                }
            }
        }

        private void deleteWindows(int windowBitmap) {
            if (windowBitmap == 0) {
                return;
            }
            Iterator<CCWindowLayout> it = getWindowsFromBitmap(windowBitmap).iterator();
            while (it.hasNext()) {
                CCWindowLayout windowLayout = it.next();
                windowLayout.removeFromCaptionView();
                this.mCaptionWindowLayouts[windowLayout.getCaptionWindowId()] = null;
            }
        }

        public void reset() {
            this.mCurrentWindowLayout = null;
            this.mIsDelayed = false;
            this.mPendingCaptionEvents.clear();
            for (int i = 0; i < 8; i++) {
                CCWindowLayout cCWindowLayout = this.mCaptionWindowLayouts[i];
                if (cCWindowLayout != null) {
                    cCWindowLayout.removeFromCaptionView();
                }
                this.mCaptionWindowLayouts[i] = null;
            }
            this.mCCLayout.setVisibility(4);
            this.mHandler.removeMessages(2);
        }

        private void setWindowAttr(Cea708CCParser.CaptionWindowAttr windowAttr) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.setWindowAttr(windowAttr);
            }
        }

        private void defineWindow(Cea708CCParser.CaptionWindow window) {
            int windowId;
            if (window != null && (windowId = window.f264id) >= 0) {
                CCWindowLayout[] cCWindowLayoutArr = this.mCaptionWindowLayouts;
                if (windowId >= cCWindowLayoutArr.length) {
                    return;
                }
                CCWindowLayout windowLayout = cCWindowLayoutArr[windowId];
                if (windowLayout == null) {
                    windowLayout = new CCWindowLayout(this.mCCLayout.getContext());
                }
                windowLayout.initWindow(this.mCCLayout, window);
                this.mCaptionWindowLayouts[windowId] = windowLayout;
                this.mCurrentWindowLayout = windowLayout;
            }
        }

        private void delay(int tenthsOfSeconds) {
            if (tenthsOfSeconds < 0 || tenthsOfSeconds > 255) {
                return;
            }
            this.mIsDelayed = true;
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), tenthsOfSeconds * 100);
        }

        private void delayCancel() {
            this.mIsDelayed = false;
            processPendingBuffer();
        }

        private void processPendingBuffer() {
            Iterator<Cea708CCParser.CaptionEvent> it = this.mPendingCaptionEvents.iterator();
            while (it.hasNext()) {
                Cea708CCParser.CaptionEvent event = it.next();
                processCaptionEvent(event);
            }
            this.mPendingCaptionEvents.clear();
        }

        private void sendControlToCurrentWindow(char control) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.sendControl(control);
            }
        }

        private void sendBufferToCurrentWindow(String buffer) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.sendBuffer(buffer);
                this.mHandler.removeMessages(2);
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(2), 60000L);
            }
        }

        private void setPenAttr(Cea708CCParser.CaptionPenAttr attr) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.setPenAttr(attr);
            }
        }

        private void setPenColor(Cea708CCParser.CaptionPenColor color) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.setPenColor(color);
            }
        }

        private void setPenLocation(Cea708CCParser.CaptionPenLocation location) {
            CCWindowLayout cCWindowLayout = this.mCurrentWindowLayout;
            if (cCWindowLayout != null) {
                cCWindowLayout.setPenLocation(location.row, location.column);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CCWindowLayout extends RelativeLayout implements View.OnLayoutChangeListener {
        private static final int ANCHOR_HORIZONTAL_16_9_MAX = 209;
        private static final int ANCHOR_HORIZONTAL_MODE_CENTER = 1;
        private static final int ANCHOR_HORIZONTAL_MODE_LEFT = 0;
        private static final int ANCHOR_HORIZONTAL_MODE_RIGHT = 2;
        private static final int ANCHOR_MODE_DIVIDER = 3;
        private static final int ANCHOR_RELATIVE_POSITIONING_MAX = 99;
        private static final int ANCHOR_VERTICAL_MAX = 74;
        private static final int ANCHOR_VERTICAL_MODE_BOTTOM = 2;
        private static final int ANCHOR_VERTICAL_MODE_CENTER = 1;
        private static final int ANCHOR_VERTICAL_MODE_TOP = 0;
        private static final int MAX_COLUMN_COUNT_16_9 = 42;
        private static final float PROPORTION_PEN_SIZE_LARGE = 1.25f;
        private static final float PROPORTION_PEN_SIZE_SMALL = 0.75f;
        private static final String TAG = "CCWindowLayout";
        private final SpannableStringBuilder mBuilder;
        private CCLayout mCCLayout;
        private CCView mCCView;
        private CaptioningManager.CaptionStyle mCaptionStyle;
        private int mCaptionWindowId;
        private final List<CharacterStyle> mCharacterStyles;
        private float mFontScale;
        private int mLastCaptionLayoutHeight;
        private int mLastCaptionLayoutWidth;
        private int mRow;
        private int mRowLimit;
        private float mTextSize;
        private String mWidestChar;

        public CCWindowLayout(Context context) {
            this(context, null);
        }

        public CCWindowLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CCWindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public CCWindowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.mRowLimit = 0;
            this.mBuilder = new SpannableStringBuilder();
            this.mCharacterStyles = new ArrayList();
            this.mRow = -1;
            this.mCCView = new CCView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
            addView(this.mCCView, params);
            CaptioningManager captioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
            this.mFontScale = captioningManager.getFontScale();
            setCaptionStyle(captioningManager.getUserStyle());
            this.mCCView.setText("");
            updateWidestChar();
        }

        public void setCaptionStyle(CaptioningManager.CaptionStyle style) {
            this.mCaptionStyle = style;
            this.mCCView.setCaptionStyle(style);
        }

        public void setFontScale(float fontScale) {
            this.mFontScale = fontScale;
            updateTextSize();
        }

        public int getCaptionWindowId() {
            return this.mCaptionWindowId;
        }

        public void setCaptionWindowId(int captionWindowId) {
            this.mCaptionWindowId = captionWindowId;
        }

        public void clear() {
            clearText();
            hide();
        }

        public void show() {
            setVisibility(0);
            requestLayout();
        }

        public void hide() {
            setVisibility(4);
            requestLayout();
        }

        public void setPenAttr(Cea708CCParser.CaptionPenAttr penAttr) {
            this.mCharacterStyles.clear();
            if (penAttr.italic) {
                this.mCharacterStyles.add(new StyleSpan(2));
            }
            if (penAttr.underline) {
                this.mCharacterStyles.add(new UnderlineSpan());
            }
            switch (penAttr.penSize) {
                case 0:
                    this.mCharacterStyles.add(new RelativeSizeSpan(0.75f));
                    break;
                case 2:
                    this.mCharacterStyles.add(new RelativeSizeSpan((float) PROPORTION_PEN_SIZE_LARGE));
                    break;
            }
            switch (penAttr.penOffset) {
                case 0:
                    this.mCharacterStyles.add(new SubscriptSpan());
                    return;
                case 1:
                default:
                    return;
                case 2:
                    this.mCharacterStyles.add(new SuperscriptSpan());
                    return;
            }
        }

        public void setPenColor(Cea708CCParser.CaptionPenColor penColor) {
        }

        public void setPenLocation(int row, int column) {
            if (this.mRow >= 0) {
                for (int r = this.mRow; r < row; r++) {
                    appendText("\n");
                }
            }
            this.mRow = row;
        }

        public void setWindowAttr(Cea708CCParser.CaptionWindowAttr windowAttr) {
        }

        public void sendBuffer(String buffer) {
            appendText(buffer);
        }

        public void sendControl(char control) {
        }

        public void initWindow(CCLayout ccLayout, Cea708CCParser.CaptionWindow captionWindow) {
            CCLayout cCLayout = this.mCCLayout;
            if (cCLayout != ccLayout) {
                if (cCLayout != null) {
                    cCLayout.removeOnLayoutChangeListener(this);
                }
                this.mCCLayout = ccLayout;
                ccLayout.addOnLayoutChangeListener(this);
                updateWidestChar();
            }
            float scaleRow = captionWindow.anchorVertical / (captionWindow.relativePositioning ? 99 : 74);
            float scaleCol = captionWindow.anchorHorizontal / (captionWindow.relativePositioning ? 99 : 209);
            if (scaleRow < 0.0f || scaleRow > 1.0f) {
                Log.m108i(TAG, "The vertical position of the anchor point should be at the range of 0 and 1 but " + scaleRow);
                scaleRow = Math.max(0.0f, Math.min(scaleRow, 1.0f));
            }
            if (scaleCol < 0.0f || scaleCol > 1.0f) {
                Log.m108i(TAG, "The horizontal position of the anchor point should be at the range of 0 and 1 but " + scaleCol);
                scaleCol = Math.max(0.0f, Math.min(scaleCol, 1.0f));
            }
            int gravity = 17;
            int horizontalMode = captionWindow.anchorId % 3;
            int verticalMode = captionWindow.anchorId / 3;
            float scaleStartRow = 0.0f;
            float scaleEndRow = 1.0f;
            float scaleStartCol = 0.0f;
            float scaleEndCol = 1.0f;
            switch (horizontalMode) {
                case 0:
                    gravity = 3;
                    this.mCCView.setAlignment(Layout.Alignment.ALIGN_NORMAL);
                    scaleStartCol = scaleCol;
                    break;
                case 1:
                    float gap = Math.min(1.0f - scaleCol, scaleCol);
                    int columnCount = captionWindow.columnCount + 1;
                    int columnCount2 = Math.min(getScreenColumnCount(), columnCount);
                    StringBuilder widestTextBuilder = new StringBuilder();
                    for (int i = 0; i < columnCount2; i++) {
                        widestTextBuilder.append(this.mWidestChar);
                    }
                    Paint paint = new Paint();
                    paint.setTypeface(this.mCaptionStyle.getTypeface());
                    paint.setTextSize(this.mTextSize);
                    float maxWindowWidth = paint.measureText(widestTextBuilder.toString());
                    float halfMaxWidthScale = this.mCCLayout.getWidth() > 0 ? (maxWindowWidth / 2.0f) / (this.mCCLayout.getWidth() * 0.8f) : 0.0f;
                    if (halfMaxWidthScale > 0.0f && halfMaxWidthScale < scaleCol) {
                        this.mCCView.setAlignment(Layout.Alignment.ALIGN_NORMAL);
                        scaleStartCol = scaleCol - halfMaxWidthScale;
                        scaleEndCol = 1.0f;
                        gravity = 3;
                        break;
                    } else {
                        gravity = 1;
                        this.mCCView.setAlignment(Layout.Alignment.ALIGN_CENTER);
                        scaleStartCol = scaleCol - gap;
                        scaleEndCol = scaleCol + gap;
                        break;
                    }
                    break;
                case 2:
                    gravity = 5;
                    this.mCCView.setAlignment(Layout.Alignment.ALIGN_RIGHT);
                    scaleEndCol = scaleCol;
                    break;
            }
            switch (verticalMode) {
                case 0:
                    gravity |= 48;
                    scaleStartRow = scaleRow;
                    break;
                case 1:
                    gravity |= 16;
                    float gap2 = Math.min(1.0f - scaleRow, scaleRow);
                    scaleStartRow = scaleRow - gap2;
                    scaleEndRow = scaleRow + gap2;
                    break;
                case 2:
                    gravity |= 80;
                    scaleEndRow = scaleRow;
                    break;
            }
            this.mCCLayout.addOrUpdateViewToSafeTitleArea(this, new ScaledLayout.ScaledLayoutParams(scaleStartRow, scaleEndRow, scaleStartCol, scaleEndCol));
            setCaptionWindowId(captionWindow.f264id);
            setRowLimit(captionWindow.rowCount);
            setGravity(gravity);
            if (captionWindow.visible) {
                show();
            } else {
                hide();
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int width = right - left;
            int height = bottom - top;
            if (width != this.mLastCaptionLayoutWidth || height != this.mLastCaptionLayoutHeight) {
                this.mLastCaptionLayoutWidth = width;
                this.mLastCaptionLayoutHeight = height;
                updateTextSize();
            }
        }

        private void updateWidestChar() {
            Paint paint = new Paint();
            paint.setTypeface(this.mCaptionStyle.getTypeface());
            Charset latin1 = Charset.forName("ISO-8859-1");
            float widestCharWidth = 0.0f;
            for (int i = 0; i < 256; i++) {
                String ch = new String(new byte[]{(byte) i}, latin1);
                float charWidth = paint.measureText(ch);
                if (widestCharWidth < charWidth) {
                    widestCharWidth = charWidth;
                    this.mWidestChar = ch;
                }
            }
            updateTextSize();
        }

        private void updateTextSize() {
            if (this.mCCLayout == null) {
                return;
            }
            StringBuilder widestTextBuilder = new StringBuilder();
            int screenColumnCount = getScreenColumnCount();
            for (int i = 0; i < screenColumnCount; i++) {
                widestTextBuilder.append(this.mWidestChar);
            }
            String widestText = widestTextBuilder.toString();
            Paint paint = new Paint();
            paint.setTypeface(this.mCaptionStyle.getTypeface());
            float startFontSize = 0.0f;
            float endFontSize = 255.0f;
            while (startFontSize < endFontSize) {
                float testTextSize = (startFontSize + endFontSize) / 2.0f;
                paint.setTextSize(testTextSize);
                float width = paint.measureText(widestText);
                if (this.mCCLayout.getWidth() * 0.8f > width) {
                    startFontSize = 0.01f + testTextSize;
                } else {
                    endFontSize = testTextSize - 0.01f;
                }
            }
            float f = this.mFontScale * endFontSize;
            this.mTextSize = f;
            this.mCCView.setTextSize(f);
        }

        private int getScreenColumnCount() {
            return 42;
        }

        public void removeFromCaptionView() {
            CCLayout cCLayout = this.mCCLayout;
            if (cCLayout != null) {
                cCLayout.removeViewFromSafeTitleArea(this);
                this.mCCLayout.removeOnLayoutChangeListener(this);
                this.mCCLayout = null;
            }
        }

        public void setText(String text) {
            updateText(text, false);
        }

        public void appendText(String text) {
            updateText(text, true);
        }

        public void clearText() {
            this.mBuilder.clear();
            this.mCCView.setText("");
        }

        private void updateText(String text, boolean appended) {
            if (!appended) {
                this.mBuilder.clear();
            }
            if (text != null && text.length() > 0) {
                int length = this.mBuilder.length();
                this.mBuilder.append((CharSequence) text);
                for (CharacterStyle characterStyle : this.mCharacterStyles) {
                    SpannableStringBuilder spannableStringBuilder = this.mBuilder;
                    spannableStringBuilder.setSpan(characterStyle, length, spannableStringBuilder.length(), 33);
                }
            }
            String[] lines = TextUtils.split(this.mBuilder.toString(), "\n");
            String truncatedText = TextUtils.join("\n", Arrays.copyOfRange(lines, Math.max(0, lines.length - (this.mRowLimit + 1)), lines.length));
            SpannableStringBuilder spannableStringBuilder2 = this.mBuilder;
            spannableStringBuilder2.delete(0, spannableStringBuilder2.length() - truncatedText.length());
            int start = 0;
            int last = this.mBuilder.length() - 1;
            int end = last;
            while (start <= end && this.mBuilder.charAt(start) <= ' ') {
                start++;
            }
            while (end >= start && this.mBuilder.charAt(end) <= ' ') {
                end--;
            }
            if (start == 0 && end == last) {
                this.mCCView.setText(this.mBuilder);
                return;
            }
            SpannableStringBuilder trim = new SpannableStringBuilder();
            trim.append((CharSequence) this.mBuilder);
            if (end < last) {
                trim.delete(end + 1, last + 1);
            }
            if (start > 0) {
                trim.delete(0, start);
            }
            this.mCCView.setText(trim);
        }

        public void setRowLimit(int rowLimit) {
            if (rowLimit < 0) {
                throw new IllegalArgumentException("A rowLimit should have a positive number");
            }
            this.mRowLimit = rowLimit;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CCView extends SubtitleView {
        private static final CaptioningManager.CaptionStyle DEFAULT_CAPTION_STYLE = CaptioningManager.CaptionStyle.DEFAULT;

        public CCView(Context context) {
            this(context, null);
        }

        public CCView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public CCView(Context context, AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public CCView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setCaptionStyle(CaptioningManager.CaptionStyle style) {
            setForegroundColor(style.hasForegroundColor() ? style.foregroundColor : DEFAULT_CAPTION_STYLE.foregroundColor);
            setBackgroundColor(style.hasBackgroundColor() ? style.backgroundColor : DEFAULT_CAPTION_STYLE.backgroundColor);
            setEdgeType(style.hasEdgeType() ? style.edgeType : DEFAULT_CAPTION_STYLE.edgeType);
            setEdgeColor(style.hasEdgeColor() ? style.edgeColor : DEFAULT_CAPTION_STYLE.edgeColor);
            setTypeface(style.getTypeface());
        }
    }
}
