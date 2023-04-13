package android.media;

import android.content.Context;
import android.media.SubtitleTrack;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.LinearLayout;
import com.android.internal.widget.SubtitleView;
import java.util.ArrayList;
import java.util.Vector;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: classes2.dex */
public class WebVttRenderingWidget extends ViewGroup implements SubtitleTrack.RenderingWidget {
    private static final boolean DEBUG = false;
    private static final int DEBUG_CUE_BACKGROUND = -2130771968;
    private static final int DEBUG_REGION_BACKGROUND = -2147483393;
    private static final CaptioningManager.CaptionStyle DEFAULT_CAPTION_STYLE = CaptioningManager.CaptionStyle.DEFAULT;
    private static final float LINE_HEIGHT_RATIO = 0.0533f;
    private CaptioningManager.CaptionStyle mCaptionStyle;
    private final CaptioningManager.CaptioningChangeListener mCaptioningListener;
    private final ArrayMap<TextTrackCue, CueLayout> mCueBoxes;
    private float mFontSize;
    private boolean mHasChangeListener;
    private SubtitleTrack.RenderingWidget.OnChangedListener mListener;
    private final CaptioningManager mManager;
    private final ArrayMap<TextTrackRegion, RegionLayout> mRegionBoxes;

    public WebVttRenderingWidget(Context context) {
        this(context, null);
    }

    public WebVttRenderingWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebVttRenderingWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WebVttRenderingWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mRegionBoxes = new ArrayMap<>();
        this.mCueBoxes = new ArrayMap<>();
        this.mCaptioningListener = new CaptioningManager.CaptioningChangeListener() { // from class: android.media.WebVttRenderingWidget.1
            @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
            public void onFontScaleChanged(float fontScale) {
                float fontSize = WebVttRenderingWidget.this.getHeight() * fontScale * WebVttRenderingWidget.LINE_HEIGHT_RATIO;
                WebVttRenderingWidget webVttRenderingWidget = WebVttRenderingWidget.this;
                webVttRenderingWidget.setCaptionStyle(webVttRenderingWidget.mCaptionStyle, fontSize);
            }

            @Override // android.view.accessibility.CaptioningManager.CaptioningChangeListener
            public void onUserStyleChanged(CaptioningManager.CaptionStyle userStyle) {
                WebVttRenderingWidget webVttRenderingWidget = WebVttRenderingWidget.this;
                webVttRenderingWidget.setCaptionStyle(userStyle, webVttRenderingWidget.mFontSize);
            }
        };
        setLayerType(1, null);
        CaptioningManager captioningManager = (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
        this.mManager = captioningManager;
        this.mCaptionStyle = captioningManager.getUserStyle();
        this.mFontSize = captioningManager.getFontScale() * getHeight() * LINE_HEIGHT_RATIO;
    }

    @Override // android.media.SubtitleTrack.RenderingWidget
    public void setSize(int width, int height) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
        measure(widthSpec, heightSpec);
        layout(0, 0, width, height);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        manageChangeListener();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        manageChangeListener();
    }

    @Override // android.media.SubtitleTrack.RenderingWidget
    public void setOnChangedListener(SubtitleTrack.RenderingWidget.OnChangedListener listener) {
        this.mListener = listener;
    }

    @Override // android.media.SubtitleTrack.RenderingWidget
    public void setVisible(boolean visible) {
        if (visible) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
        manageChangeListener();
    }

    private void manageChangeListener() {
        boolean needsListener = isAttachedToWindow() && getVisibility() == 0;
        if (this.mHasChangeListener != needsListener) {
            this.mHasChangeListener = needsListener;
            if (needsListener) {
                this.mManager.addCaptioningChangeListener(this.mCaptioningListener);
                CaptioningManager.CaptionStyle captionStyle = this.mManager.getUserStyle();
                float fontSize = this.mManager.getFontScale() * getHeight() * LINE_HEIGHT_RATIO;
                setCaptionStyle(captionStyle, fontSize);
                return;
            }
            this.mManager.removeCaptioningChangeListener(this.mCaptioningListener);
        }
    }

    public void setActiveCues(Vector<SubtitleTrack.Cue> activeCues) {
        Context context = getContext();
        CaptioningManager.CaptionStyle captionStyle = this.mCaptionStyle;
        float fontSize = this.mFontSize;
        prepForPrune();
        int count = activeCues.size();
        for (int i = 0; i < count; i++) {
            TextTrackCue cue = (TextTrackCue) activeCues.get(i);
            TextTrackRegion region = cue.mRegion;
            if (region != null) {
                RegionLayout regionBox = this.mRegionBoxes.get(region);
                if (regionBox == null) {
                    regionBox = new RegionLayout(context, region, captionStyle, fontSize);
                    this.mRegionBoxes.put(region, regionBox);
                    addView(regionBox, -2, -2);
                }
                regionBox.put(cue);
            } else {
                CueLayout cueBox = this.mCueBoxes.get(cue);
                if (cueBox == null) {
                    cueBox = new CueLayout(context, cue, captionStyle, fontSize);
                    this.mCueBoxes.put(cue, cueBox);
                    addView(cueBox, -2, -2);
                }
                cueBox.update();
                cueBox.setOrder(i);
            }
        }
        prune();
        int width = getWidth();
        int height = getHeight();
        setSize(width, height);
        SubtitleTrack.RenderingWidget.OnChangedListener onChangedListener = this.mListener;
        if (onChangedListener != null) {
            onChangedListener.onChanged(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCaptionStyle(CaptioningManager.CaptionStyle captionStyle, float fontSize) {
        CaptioningManager.CaptionStyle captionStyle2 = DEFAULT_CAPTION_STYLE.applyStyle(captionStyle);
        this.mCaptionStyle = captionStyle2;
        this.mFontSize = fontSize;
        int cueCount = this.mCueBoxes.size();
        for (int i = 0; i < cueCount; i++) {
            CueLayout cueBox = this.mCueBoxes.valueAt(i);
            cueBox.setCaptionStyle(captionStyle2, fontSize);
        }
        int regionCount = this.mRegionBoxes.size();
        for (int i2 = 0; i2 < regionCount; i2++) {
            RegionLayout regionBox = this.mRegionBoxes.valueAt(i2);
            regionBox.setCaptionStyle(captionStyle2, fontSize);
        }
    }

    private void prune() {
        int regionCount = this.mRegionBoxes.size();
        int i = 0;
        while (i < regionCount) {
            RegionLayout regionBox = this.mRegionBoxes.valueAt(i);
            if (regionBox.prune()) {
                removeView(regionBox);
                this.mRegionBoxes.removeAt(i);
                regionCount--;
                i--;
            }
            i++;
        }
        int cueCount = this.mCueBoxes.size();
        int i2 = 0;
        while (i2 < cueCount) {
            CueLayout cueBox = this.mCueBoxes.valueAt(i2);
            if (!cueBox.isActive()) {
                removeView(cueBox);
                this.mCueBoxes.removeAt(i2);
                cueCount--;
                i2--;
            }
            i2++;
        }
    }

    private void prepForPrune() {
        int regionCount = this.mRegionBoxes.size();
        for (int i = 0; i < regionCount; i++) {
            RegionLayout regionBox = this.mRegionBoxes.valueAt(i);
            regionBox.prepForPrune();
        }
        int cueCount = this.mCueBoxes.size();
        for (int i2 = 0; i2 < cueCount; i2++) {
            CueLayout cueBox = this.mCueBoxes.valueAt(i2);
            cueBox.prepForPrune();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int regionCount = this.mRegionBoxes.size();
        for (int i = 0; i < regionCount; i++) {
            RegionLayout regionBox = this.mRegionBoxes.valueAt(i);
            regionBox.measureForParent(widthMeasureSpec, heightMeasureSpec);
        }
        int cueCount = this.mCueBoxes.size();
        for (int i2 = 0; i2 < cueCount; i2++) {
            CueLayout cueBox = this.mCueBoxes.valueAt(i2);
            cueBox.measureForParent(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewportWidth = r - l;
        int viewportHeight = b - t;
        setCaptionStyle(this.mCaptionStyle, this.mManager.getFontScale() * LINE_HEIGHT_RATIO * viewportHeight);
        int regionCount = this.mRegionBoxes.size();
        for (int i = 0; i < regionCount; i++) {
            RegionLayout regionBox = this.mRegionBoxes.valueAt(i);
            layoutRegion(viewportWidth, viewportHeight, regionBox);
        }
        int cueCount = this.mCueBoxes.size();
        for (int i2 = 0; i2 < cueCount; i2++) {
            CueLayout cueBox = this.mCueBoxes.valueAt(i2);
            layoutCue(viewportWidth, viewportHeight, cueBox);
        }
    }

    private void layoutRegion(int viewportWidth, int viewportHeight, RegionLayout regionBox) {
        TextTrackRegion region = regionBox.getRegion();
        int regionHeight = regionBox.getMeasuredHeight();
        int regionWidth = regionBox.getMeasuredWidth();
        float x = region.mViewportAnchorPointX;
        float y = region.mViewportAnchorPointY;
        int left = (int) (((viewportWidth - regionWidth) * x) / 100.0f);
        int top = (int) (((viewportHeight - regionHeight) * y) / 100.0f);
        regionBox.layout(left, top, left + regionWidth, top + regionHeight);
    }

    private void layoutCue(int viewportWidth, int viewportHeight, CueLayout cueBox) {
        int xPosition;
        int top;
        TextTrackCue cue = cueBox.getCue();
        int direction = getLayoutDirection();
        int absAlignment = resolveCueAlignment(direction, cue.mAlignment);
        boolean cueSnapToLines = cue.mSnapToLines;
        int size = (cueBox.getMeasuredWidth() * 100) / viewportWidth;
        switch (absAlignment) {
            case 203:
                xPosition = cue.mTextPosition;
                break;
            case 204:
                int xPosition2 = cue.mTextPosition;
                xPosition = xPosition2 - size;
                break;
            default:
                xPosition = cue.mTextPosition - (size / 2);
                break;
        }
        if (direction == 1) {
            xPosition = 100 - xPosition;
        }
        if (cueSnapToLines) {
            int paddingLeft = (getPaddingLeft() * 100) / viewportWidth;
            int paddingRight = (getPaddingRight() * 100) / viewportWidth;
            if (xPosition < paddingLeft && xPosition + size > paddingLeft) {
                xPosition += paddingLeft;
                size -= paddingLeft;
            }
            float rightEdge = 100 - paddingRight;
            if (xPosition < rightEdge && xPosition + size > rightEdge) {
                size -= paddingRight;
            }
        }
        int left = (xPosition * viewportWidth) / 100;
        int width = (size * viewportWidth) / 100;
        int yPosition = calculateLinePosition(cueBox);
        int height = cueBox.getMeasuredHeight();
        if (yPosition < 0) {
            top = viewportHeight + (yPosition * height);
        } else {
            int top2 = viewportHeight - height;
            top = (top2 * yPosition) / 100;
        }
        cueBox.layout(left, top, left + width, top + height);
    }

    private int calculateLinePosition(CueLayout cueBox) {
        TextTrackCue cue = cueBox.getCue();
        Integer linePosition = cue.mLinePosition;
        boolean snapToLines = cue.mSnapToLines;
        boolean autoPosition = linePosition == null;
        if (!snapToLines && !autoPosition && (linePosition.intValue() < 0 || linePosition.intValue() > 100)) {
            return 100;
        }
        if (!autoPosition) {
            return linePosition.intValue();
        }
        if (snapToLines) {
            return -(cueBox.mOrder + 1);
        }
        return 100;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int resolveCueAlignment(int layoutDirection, int alignment) {
        switch (alignment) {
            case 201:
                return layoutDirection == 0 ? 203 : 204;
            case 202:
                return layoutDirection == 0 ? 204 : 203;
            default:
                return alignment;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: WebVttRenderer.java */
    /* loaded from: classes2.dex */
    public static class RegionLayout extends LinearLayout {
        private CaptioningManager.CaptionStyle mCaptionStyle;
        private float mFontSize;
        private final TextTrackRegion mRegion;
        private final ArrayList<CueLayout> mRegionCueBoxes;

        public RegionLayout(Context context, TextTrackRegion region, CaptioningManager.CaptionStyle captionStyle, float fontSize) {
            super(context);
            this.mRegionCueBoxes = new ArrayList<>();
            this.mRegion = region;
            this.mCaptionStyle = captionStyle;
            this.mFontSize = fontSize;
            setOrientation(1);
            setBackgroundColor(captionStyle.windowColor);
        }

        public void setCaptionStyle(CaptioningManager.CaptionStyle captionStyle, float fontSize) {
            this.mCaptionStyle = captionStyle;
            this.mFontSize = fontSize;
            int cueCount = this.mRegionCueBoxes.size();
            for (int i = 0; i < cueCount; i++) {
                CueLayout cueBox = this.mRegionCueBoxes.get(i);
                cueBox.setCaptionStyle(captionStyle, fontSize);
            }
            int i2 = captionStyle.windowColor;
            setBackgroundColor(i2);
        }

        public void measureForParent(int widthMeasureSpec, int heightMeasureSpec) {
            TextTrackRegion region = this.mRegion;
            int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            int width = (int) region.mWidth;
            int size = (width * specWidth) / 100;
            int widthMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(specHeight, Integer.MIN_VALUE);
            measure(widthMeasureSpec2, heightMeasureSpec2);
        }

        public void prepForPrune() {
            int cueCount = this.mRegionCueBoxes.size();
            for (int i = 0; i < cueCount; i++) {
                CueLayout cueBox = this.mRegionCueBoxes.get(i);
                cueBox.prepForPrune();
            }
        }

        public void put(TextTrackCue cue) {
            int cueCount = this.mRegionCueBoxes.size();
            for (int i = 0; i < cueCount; i++) {
                CueLayout cueBox = this.mRegionCueBoxes.get(i);
                if (cueBox.getCue() == cue) {
                    cueBox.update();
                    return;
                }
            }
            CueLayout cueBox2 = new CueLayout(getContext(), cue, this.mCaptionStyle, this.mFontSize);
            this.mRegionCueBoxes.add(cueBox2);
            addView(cueBox2, -2, -2);
            if (getChildCount() > this.mRegion.mLines) {
                removeViewAt(0);
            }
        }

        public boolean prune() {
            int cueCount = this.mRegionCueBoxes.size();
            int i = 0;
            while (i < cueCount) {
                CueLayout cueBox = this.mRegionCueBoxes.get(i);
                if (!cueBox.isActive()) {
                    this.mRegionCueBoxes.remove(i);
                    removeView(cueBox);
                    cueCount--;
                    i--;
                }
                i++;
            }
            return this.mRegionCueBoxes.isEmpty();
        }

        public TextTrackRegion getRegion() {
            return this.mRegion;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: WebVttRenderer.java */
    /* loaded from: classes2.dex */
    public static class CueLayout extends LinearLayout {
        private boolean mActive;
        private CaptioningManager.CaptionStyle mCaptionStyle;
        public final TextTrackCue mCue;
        private float mFontSize;
        private int mOrder;

        public CueLayout(Context context, TextTrackCue cue, CaptioningManager.CaptionStyle captionStyle, float fontSize) {
            super(context);
            this.mCue = cue;
            this.mCaptionStyle = captionStyle;
            this.mFontSize = fontSize;
            boolean horizontal = cue.mWritingDirection == 100;
            setOrientation(horizontal ? 1 : 0);
            switch (cue.mAlignment) {
                case 200:
                    setGravity(horizontal ? 1 : 16);
                    break;
                case 201:
                    setGravity(Gravity.START);
                    break;
                case 202:
                    setGravity(Gravity.END);
                    break;
                case 203:
                    setGravity(3);
                    break;
                case 204:
                    setGravity(5);
                    break;
            }
            update();
        }

        public void setCaptionStyle(CaptioningManager.CaptionStyle style, float fontSize) {
            this.mCaptionStyle = style;
            this.mFontSize = fontSize;
            int n = getChildCount();
            for (int i = 0; i < n; i++) {
                View child = getChildAt(i);
                if (child instanceof SpanLayout) {
                    ((SpanLayout) child).setCaptionStyle(style, fontSize);
                }
            }
        }

        public void prepForPrune() {
            this.mActive = false;
        }

        public void update() {
            Layout.Alignment alignment;
            this.mActive = true;
            removeAllViews();
            int cueAlignment = WebVttRenderingWidget.resolveCueAlignment(getLayoutDirection(), this.mCue.mAlignment);
            switch (cueAlignment) {
                case 203:
                    alignment = Layout.Alignment.ALIGN_LEFT;
                    break;
                case 204:
                    alignment = Layout.Alignment.ALIGN_RIGHT;
                    break;
                default:
                    alignment = Layout.Alignment.ALIGN_CENTER;
                    break;
            }
            CaptioningManager.CaptionStyle captionStyle = this.mCaptionStyle;
            float fontSize = this.mFontSize;
            TextTrackCueSpan[][] lines = this.mCue.mLines;
            for (TextTrackCueSpan[] textTrackCueSpanArr : lines) {
                SpanLayout lineBox = new SpanLayout(getContext(), textTrackCueSpanArr);
                lineBox.setAlignment(alignment);
                lineBox.setCaptionStyle(captionStyle, fontSize);
                addView(lineBox, -2, -2);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.LinearLayout, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void measureForParent(int widthMeasureSpec, int heightMeasureSpec) {
            int maximumSize;
            TextTrackCue cue = this.mCue;
            int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            int direction = getLayoutDirection();
            int absAlignment = WebVttRenderingWidget.resolveCueAlignment(direction, cue.mAlignment);
            switch (absAlignment) {
                case 200:
                    int maximumSize2 = cue.mTextPosition;
                    if (maximumSize2 <= 50) {
                        maximumSize = cue.mTextPosition * 2;
                        break;
                    } else {
                        int maximumSize3 = cue.mTextPosition;
                        maximumSize = (100 - maximumSize3) * 2;
                        break;
                    }
                case 201:
                case 202:
                default:
                    maximumSize = 0;
                    break;
                case 203:
                    int maximumSize4 = cue.mTextPosition;
                    maximumSize = 100 - maximumSize4;
                    break;
                case 204:
                    maximumSize = cue.mTextPosition;
                    break;
            }
            int size = (Math.min(cue.mSize, maximumSize) * specWidth) / 100;
            int widthMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(specHeight, Integer.MIN_VALUE);
            measure(widthMeasureSpec2, heightMeasureSpec2);
        }

        public void setOrder(int order) {
            this.mOrder = order;
        }

        public boolean isActive() {
            return this.mActive;
        }

        public TextTrackCue getCue() {
            return this.mCue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: WebVttRenderer.java */
    /* loaded from: classes2.dex */
    public static class SpanLayout extends SubtitleView {
        private final SpannableStringBuilder mBuilder;
        private final TextTrackCueSpan[] mSpans;

        public SpanLayout(Context context, TextTrackCueSpan[] spans) {
            super(context);
            this.mBuilder = new SpannableStringBuilder();
            this.mSpans = spans;
            update();
        }

        public void update() {
            SpannableStringBuilder builder = this.mBuilder;
            TextTrackCueSpan[] spans = this.mSpans;
            builder.clear();
            builder.clearSpans();
            int spanCount = spans.length;
            for (int i = 0; i < spanCount; i++) {
                TextTrackCueSpan span = spans[i];
                if (span.mEnabled) {
                    builder.append((CharSequence) spans[i].mText);
                }
            }
            setText(builder);
        }

        public void setCaptionStyle(CaptioningManager.CaptionStyle captionStyle, float fontSize) {
            setBackgroundColor(captionStyle.backgroundColor);
            setForegroundColor(captionStyle.foregroundColor);
            setEdgeColor(captionStyle.edgeColor);
            setEdgeType(captionStyle.edgeType);
            setTypeface(captionStyle.getTypeface());
            setTextSize(fontSize);
        }
    }
}
