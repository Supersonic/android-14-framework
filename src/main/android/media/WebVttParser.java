package android.media;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.provider.BrowserContract;
import android.util.Log;
import java.util.Vector;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: classes2.dex */
public class WebVttParser {
    private static final String TAG = "WebVttParser";
    private String mBuffer;
    private TextTrackCue mCue;
    private Vector<String> mCueTexts;
    private WebVttCueListener mListener;
    private final Phase mParseCueId;
    private final Phase mParseCueText;
    private final Phase mParseCueTime;
    private final Phase mParseHeader;
    private final Phase mParseStart;
    private Phase mPhase;
    private final Phase mSkipRest = new Phase() { // from class: android.media.WebVttParser.1
        @Override // android.media.WebVttParser.Phase
        public void parse(String line) {
        }
    };

    /* compiled from: WebVttRenderer.java */
    /* loaded from: classes2.dex */
    interface Phase {
        void parse(String str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebVttParser(WebVttCueListener listener) {
        Phase phase = new Phase() { // from class: android.media.WebVttParser.2
            @Override // android.media.WebVttParser.Phase
            public void parse(String line) {
                if (line.startsWith("\ufeff")) {
                    line = line.substring(1);
                }
                if (!line.equals("WEBVTT") && !line.startsWith("WEBVTT ") && !line.startsWith("WEBVTT\t")) {
                    WebVttParser.this.log_warning("Not a WEBVTT header", line);
                    WebVttParser webVttParser = WebVttParser.this;
                    webVttParser.mPhase = webVttParser.mSkipRest;
                    return;
                }
                WebVttParser webVttParser2 = WebVttParser.this;
                webVttParser2.mPhase = webVttParser2.mParseHeader;
            }
        };
        this.mParseStart = phase;
        this.mParseHeader = new Phase() { // from class: android.media.WebVttParser.3
            static final /* synthetic */ boolean $assertionsDisabled = false;

            TextTrackRegion parseRegion(String s) {
                int i;
                TextTrackRegion region = new TextTrackRegion();
                String[] split = s.split(" +");
                int length = split.length;
                int i2 = 0;
                int i3 = 0;
                while (i3 < length) {
                    String setting = split[i3];
                    int equalAt = setting.indexOf(61);
                    if (equalAt <= 0) {
                        i = i2;
                    } else if (equalAt == setting.length() - 1) {
                        i = i2;
                    } else {
                        String name = setting.substring(i2, equalAt);
                        String value = setting.substring(equalAt + 1);
                        if (name.equals("id")) {
                            region.mId = value;
                            i = i2;
                        } else if (name.equals("width")) {
                            try {
                                region.mWidth = WebVttParser.parseFloatPercentage(value);
                                i = i2;
                            } catch (NumberFormatException e) {
                                WebVttParser.this.log_warning("region setting", name, "has invalid value", e.getMessage(), value);
                                i = 0;
                            }
                        } else if (name.equals("lines")) {
                            if (value.matches(".*[^0-9].*")) {
                                WebVttParser.this.log_warning("lines", name, "contains an invalid character", value);
                                i = 0;
                            } else {
                                try {
                                    region.mLines = Integer.parseInt(value);
                                } catch (NumberFormatException e2) {
                                    WebVttParser.this.log_warning("region setting", name, "is not numeric", value);
                                }
                                i = 0;
                            }
                        } else if (name.equals("regionanchor") || name.equals("viewportanchor")) {
                            int commaAt = value.indexOf(",");
                            if (commaAt < 0) {
                                WebVttParser.this.log_warning("region setting", name, "contains no comma", value);
                                i = 0;
                            } else {
                                String anchorX = value.substring(0, commaAt);
                                String anchorY = value.substring(commaAt + 1);
                                try {
                                    float x = WebVttParser.parseFloatPercentage(anchorX);
                                    try {
                                        float y = WebVttParser.parseFloatPercentage(anchorY);
                                        if (name.charAt(0) == 'r') {
                                            region.mAnchorPointX = x;
                                            region.mAnchorPointY = y;
                                        } else {
                                            region.mViewportAnchorPointX = x;
                                            region.mViewportAnchorPointY = y;
                                        }
                                        i = 0;
                                    } catch (NumberFormatException e3) {
                                        i = 0;
                                        WebVttParser.this.log_warning("region setting", name, "has invalid y component", e3.getMessage(), anchorY);
                                    }
                                } catch (NumberFormatException e4) {
                                    i = 0;
                                    WebVttParser.this.log_warning("region setting", name, "has invalid x component", e4.getMessage(), anchorX);
                                }
                            }
                        } else if (name.equals("scroll")) {
                            if (!value.equals(MediaMetrics.Value.f271UP)) {
                                WebVttParser.this.log_warning("region setting", name, "has invalid value", value);
                                i = 0;
                            } else {
                                region.mScrollValue = 301;
                                i = 0;
                            }
                        } else {
                            i = 0;
                        }
                    }
                    i3++;
                    i2 = i;
                }
                return region;
            }

            @Override // android.media.WebVttParser.Phase
            public void parse(String line) {
                if (line.length() == 0) {
                    WebVttParser webVttParser = WebVttParser.this;
                    webVttParser.mPhase = webVttParser.mParseCueId;
                } else if (line.contains("-->")) {
                    WebVttParser webVttParser2 = WebVttParser.this;
                    webVttParser2.mPhase = webVttParser2.mParseCueTime;
                    WebVttParser.this.mPhase.parse(line);
                } else {
                    int colonAt = line.indexOf(58);
                    if (colonAt <= 0 || colonAt >= line.length() - 1) {
                        WebVttParser.this.log_warning("meta data header has invalid format", line);
                    }
                    String name = line.substring(0, colonAt);
                    String value = line.substring(colonAt + 1);
                    if (name.equals("Region")) {
                        TextTrackRegion region = parseRegion(value);
                        WebVttParser.this.mListener.onRegionParsed(region);
                    }
                }
            }
        };
        this.mParseCueId = new Phase() { // from class: android.media.WebVttParser.4
            static final /* synthetic */ boolean $assertionsDisabled = false;

            @Override // android.media.WebVttParser.Phase
            public void parse(String line) {
                if (line.length() == 0) {
                    return;
                }
                if (line.equals("NOTE") || line.startsWith("NOTE ")) {
                    WebVttParser webVttParser = WebVttParser.this;
                    webVttParser.mPhase = webVttParser.mParseCueText;
                }
                WebVttParser.this.mCue = new TextTrackCue();
                WebVttParser.this.mCueTexts.clear();
                WebVttParser webVttParser2 = WebVttParser.this;
                webVttParser2.mPhase = webVttParser2.mParseCueTime;
                if (line.contains("-->")) {
                    WebVttParser.this.mPhase.parse(line);
                } else {
                    WebVttParser.this.mCue.mId = line;
                }
            }
        };
        this.mParseCueTime = new Phase() { // from class: android.media.WebVttParser.5
            static final /* synthetic */ boolean $assertionsDisabled = false;

            @Override // android.media.WebVttParser.Phase
            public void parse(String line) {
                int arrowAt;
                int i;
                String start;
                int arrowAt2 = line.indexOf("-->");
                if (arrowAt2 < 0) {
                    WebVttParser.this.mCue = null;
                    WebVttParser webVttParser = WebVttParser.this;
                    webVttParser.mPhase = webVttParser.mParseCueId;
                    return;
                }
                int i2 = 0;
                String start2 = line.substring(0, arrowAt2).trim();
                String rest = line.substring(arrowAt2 + 3).replaceFirst("^\\s+", "").replaceFirst("\\s+", " ");
                int spaceAt = rest.indexOf(32);
                String end = spaceAt > 0 ? rest.substring(0, spaceAt) : rest;
                String rest2 = spaceAt > 0 ? rest.substring(spaceAt + 1) : "";
                WebVttParser.this.mCue.mStartTimeMs = WebVttParser.parseTimestampMs(start2);
                WebVttParser.this.mCue.mEndTimeMs = WebVttParser.parseTimestampMs(end);
                String[] split = rest2.split(" +");
                int length = split.length;
                int i3 = 0;
                while (i3 < length) {
                    String setting = split[i3];
                    int colonAt = setting.indexOf(58);
                    if (colonAt <= 0) {
                        arrowAt = arrowAt2;
                        i = i2;
                        start = start2;
                    } else if (colonAt == setting.length() - 1) {
                        arrowAt = arrowAt2;
                        i = i2;
                        start = start2;
                    } else {
                        String name = setting.substring(i2, colonAt);
                        String value = setting.substring(colonAt + 1);
                        if (name.equals(TtmlUtils.TAG_REGION)) {
                            WebVttParser.this.mCue.mRegionId = value;
                            arrowAt = arrowAt2;
                            start = start2;
                            i = 0;
                        } else {
                            arrowAt = arrowAt2;
                            if (name.equals("vertical")) {
                                if (value.equals("rl")) {
                                    WebVttParser.this.mCue.mWritingDirection = 101;
                                    start = start2;
                                    i = 0;
                                } else if (value.equals("lr")) {
                                    WebVttParser.this.mCue.mWritingDirection = 102;
                                    start = start2;
                                    i = 0;
                                } else {
                                    WebVttParser.this.log_warning("cue setting", name, "has invalid value", value);
                                    start = start2;
                                    i = 0;
                                }
                            } else {
                                start = start2;
                                if (name.equals("line")) {
                                    try {
                                        if (value.endsWith("%")) {
                                            WebVttParser.this.mCue.mSnapToLines = false;
                                            WebVttParser.this.mCue.mLinePosition = Integer.valueOf(WebVttParser.parseIntPercentage(value));
                                        } else if (value.matches(".*[^0-9].*")) {
                                            WebVttParser.this.log_warning("cue setting", name, "contains an invalid character", value);
                                        } else {
                                            WebVttParser.this.mCue.mSnapToLines = true;
                                            WebVttParser.this.mCue.mLinePosition = Integer.valueOf(Integer.parseInt(value));
                                        }
                                    } catch (NumberFormatException e) {
                                        WebVttParser.this.log_warning("cue setting", name, "is not numeric or percentage", value);
                                    }
                                    i = 0;
                                } else {
                                    i = 0;
                                    if (name.equals(BrowserContract.Bookmarks.POSITION)) {
                                        try {
                                            WebVttParser.this.mCue.mTextPosition = WebVttParser.parseIntPercentage(value);
                                        } catch (NumberFormatException e2) {
                                            WebVttParser.this.log_warning("cue setting", name, "is not numeric or percentage", value);
                                        }
                                    } else if (name.equals("size")) {
                                        try {
                                            WebVttParser.this.mCue.mSize = WebVttParser.parseIntPercentage(value);
                                        } catch (NumberFormatException e3) {
                                            WebVttParser.this.log_warning("cue setting", name, "is not numeric or percentage", value);
                                        }
                                    } else if (name.equals("align")) {
                                        if (value.equals("start")) {
                                            WebVttParser.this.mCue.mAlignment = 201;
                                        } else if (value.equals("middle")) {
                                            WebVttParser.this.mCue.mAlignment = 200;
                                        } else if (value.equals("end")) {
                                            WebVttParser.this.mCue.mAlignment = 202;
                                        } else if (value.equals(NavigationBarInflaterView.LEFT)) {
                                            WebVttParser.this.mCue.mAlignment = 203;
                                        } else if (value.equals(NavigationBarInflaterView.RIGHT)) {
                                            WebVttParser.this.mCue.mAlignment = 204;
                                        } else {
                                            WebVttParser.this.log_warning("cue setting", name, "has invalid value", value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    i3++;
                    i2 = i;
                    arrowAt2 = arrowAt;
                    start2 = start;
                }
                if (WebVttParser.this.mCue.mLinePosition != null || WebVttParser.this.mCue.mSize != 100 || WebVttParser.this.mCue.mWritingDirection != 100) {
                    WebVttParser.this.mCue.mRegionId = "";
                }
                WebVttParser webVttParser2 = WebVttParser.this;
                webVttParser2.mPhase = webVttParser2.mParseCueText;
            }
        };
        this.mParseCueText = new Phase() { // from class: android.media.WebVttParser.6
            @Override // android.media.WebVttParser.Phase
            public void parse(String line) {
                if (line.length() == 0) {
                    WebVttParser.this.yieldCue();
                    WebVttParser webVttParser = WebVttParser.this;
                    webVttParser.mPhase = webVttParser.mParseCueId;
                } else if (WebVttParser.this.mCue != null) {
                    WebVttParser.this.mCueTexts.add(line);
                }
            }
        };
        this.mPhase = phase;
        this.mBuffer = "";
        this.mListener = listener;
        this.mCueTexts = new Vector<>();
    }

    public static float parseFloatPercentage(String s) throws NumberFormatException {
        if (!s.endsWith("%")) {
            throw new NumberFormatException("does not end in %");
        }
        String s2 = s.substring(0, s.length() - 1);
        if (s2.matches(".*[^0-9.].*")) {
            throw new NumberFormatException("contains an invalid character");
        }
        try {
            float value = Float.parseFloat(s2);
            if (value < 0.0f || value > 100.0f) {
                throw new NumberFormatException("is out of range");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("is not a number");
        }
    }

    public static int parseIntPercentage(String s) throws NumberFormatException {
        if (!s.endsWith("%")) {
            throw new NumberFormatException("does not end in %");
        }
        String s2 = s.substring(0, s.length() - 1);
        if (s2.matches(".*[^0-9].*")) {
            throw new NumberFormatException("contains an invalid character");
        }
        try {
            int value = Integer.parseInt(s2);
            if (value < 0 || value > 100) {
                throw new NumberFormatException("is out of range");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("is not a number");
        }
    }

    public static long parseTimestampMs(String s) throws NumberFormatException {
        String[] split;
        if (!s.matches("(\\d+:)?[0-5]\\d:[0-5]\\d\\.\\d{3}")) {
            throw new NumberFormatException("has invalid format");
        }
        String[] parts = s.split("\\.", 2);
        long value = 0;
        for (String group : parts[0].split(":")) {
            value = (60 * value) + Long.parseLong(group);
        }
        return (1000 * value) + Long.parseLong(parts[1]);
    }

    public static String timeToString(long timeMs) {
        return String.format("%d:%02d:%02d.%03d", Long.valueOf(timeMs / 3600000), Long.valueOf((timeMs / 60000) % 60), Long.valueOf((timeMs / 1000) % 60), Long.valueOf(timeMs % 1000));
    }

    public void parse(String s) {
        String str;
        boolean trailingCR = false;
        String replace = (this.mBuffer + s.replace("\u0000", "�")).replace("\r\n", "\n");
        this.mBuffer = replace;
        if (replace.endsWith("\r")) {
            trailingCR = true;
            this.mBuffer = this.mBuffer.substring(0, str.length() - 1);
        }
        String[] lines = this.mBuffer.split("[\r\n]");
        for (int i = 0; i < lines.length - 1; i++) {
            this.mPhase.parse(lines[i]);
        }
        int i2 = lines.length;
        this.mBuffer = lines[i2 - 1];
        if (trailingCR) {
            this.mBuffer += "\r";
        }
    }

    public void eos() {
        if (this.mBuffer.endsWith("\r")) {
            String str = this.mBuffer;
            this.mBuffer = str.substring(0, str.length() - 1);
        }
        this.mPhase.parse(this.mBuffer);
        this.mBuffer = "";
        yieldCue();
        this.mPhase = this.mParseStart;
    }

    public void yieldCue() {
        if (this.mCue != null && this.mCueTexts.size() > 0) {
            this.mCue.mStrings = new String[this.mCueTexts.size()];
            this.mCueTexts.toArray(this.mCue.mStrings);
            this.mCueTexts.clear();
            this.mListener.onCueParsed(this.mCue);
        }
        this.mCue = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log_warning(String nameType, String name, String message, String subMessage, String value) {
        Log.m104w(getClass().getName(), nameType + " '" + name + "' " + message + " ('" + value + "' " + subMessage + NavigationBarInflaterView.KEY_CODE_END);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log_warning(String nameType, String name, String message, String value) {
        Log.m104w(getClass().getName(), nameType + " '" + name + "' " + message + " ('" + value + "')");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log_warning(String message, String value) {
        Log.m104w(getClass().getName(), message + " ('" + value + "')");
    }
}
