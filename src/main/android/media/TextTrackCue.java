package android.media;

import android.app.slice.Slice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.SubtitleTrack;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebVttRenderer.java */
/* loaded from: classes2.dex */
public class TextTrackCue extends SubtitleTrack.Cue {
    static final int ALIGNMENT_END = 202;
    static final int ALIGNMENT_LEFT = 203;
    static final int ALIGNMENT_MIDDLE = 200;
    static final int ALIGNMENT_RIGHT = 204;
    static final int ALIGNMENT_START = 201;
    private static final String TAG = "TTCue";
    static final int WRITING_DIRECTION_HORIZONTAL = 100;
    static final int WRITING_DIRECTION_VERTICAL_LR = 102;
    static final int WRITING_DIRECTION_VERTICAL_RL = 101;
    boolean mAutoLinePosition;
    String[] mStrings;
    String mId = "";
    boolean mPauseOnExit = false;
    int mWritingDirection = 100;
    String mRegionId = "";
    boolean mSnapToLines = true;
    Integer mLinePosition = null;
    int mTextPosition = 50;
    int mSize = 100;
    int mAlignment = 200;
    TextTrackCueSpan[][] mLines = null;
    TextTrackRegion mRegion = null;

    public boolean equals(Object o) {
        boolean z;
        Integer num;
        if (o instanceof TextTrackCue) {
            if (this == o) {
                return true;
            }
            try {
                TextTrackCue cue = (TextTrackCue) o;
                boolean res = this.mId.equals(cue.mId) && this.mPauseOnExit == cue.mPauseOnExit && this.mWritingDirection == cue.mWritingDirection && this.mRegionId.equals(cue.mRegionId) && this.mSnapToLines == cue.mSnapToLines && (z = this.mAutoLinePosition) == cue.mAutoLinePosition && (z || (((num = this.mLinePosition) != null && num.equals(cue.mLinePosition)) || (this.mLinePosition == null && cue.mLinePosition == null))) && this.mTextPosition == cue.mTextPosition && this.mSize == cue.mSize && this.mAlignment == cue.mAlignment && this.mLines.length == cue.mLines.length;
                if (res) {
                    int line = 0;
                    while (true) {
                        TextTrackCueSpan[][] textTrackCueSpanArr = this.mLines;
                        if (line >= textTrackCueSpanArr.length) {
                            break;
                        } else if (!Arrays.equals(textTrackCueSpanArr[line], cue.mLines[line])) {
                            return false;
                        } else {
                            line++;
                        }
                    }
                }
                return res;
            } catch (IncompatibleClassChangeError e) {
                return false;
            }
        }
        return false;
    }

    public StringBuilder appendStringsToBuilder(StringBuilder builder) {
        String[] strArr;
        if (this.mStrings == null) {
            builder.append("null");
        } else {
            builder.append(NavigationBarInflaterView.SIZE_MOD_START);
            boolean first = true;
            for (String s : this.mStrings) {
                if (!first) {
                    builder.append(", ");
                }
                if (s == null) {
                    builder.append("null");
                } else {
                    builder.append("\"");
                    builder.append(s);
                    builder.append("\"");
                }
                first = false;
            }
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        return builder;
    }

    public StringBuilder appendLinesToBuilder(StringBuilder builder) {
        String str;
        TextTrackCueSpan[][] textTrackCueSpanArr;
        TextTrackCueSpan[][] textTrackCueSpanArr2;
        String str2 = "null";
        if (this.mLines == null) {
            builder.append("null");
        } else {
            builder.append(NavigationBarInflaterView.SIZE_MOD_START);
            boolean first = true;
            TextTrackCueSpan[][] textTrackCueSpanArr3 = this.mLines;
            int length = textTrackCueSpanArr3.length;
            int i = 0;
            while (i < length) {
                TextTrackCueSpan[] spans = textTrackCueSpanArr3[i];
                if (!first) {
                    builder.append(", ");
                }
                if (spans == null) {
                    builder.append(str2);
                    str = str2;
                    textTrackCueSpanArr = textTrackCueSpanArr3;
                } else {
                    builder.append("\"");
                    boolean innerFirst = true;
                    long lastTimestamp = -1;
                    int length2 = spans.length;
                    int i2 = 0;
                    while (i2 < length2) {
                        TextTrackCueSpan span = spans[i2];
                        if (!innerFirst) {
                            builder.append(" ");
                        }
                        boolean first2 = first;
                        String str3 = str2;
                        if (span.mTimestampMs == lastTimestamp) {
                            textTrackCueSpanArr2 = textTrackCueSpanArr3;
                        } else {
                            textTrackCueSpanArr2 = textTrackCueSpanArr3;
                            builder.append("<").append(WebVttParser.timeToString(span.mTimestampMs)).append(">");
                            lastTimestamp = span.mTimestampMs;
                        }
                        builder.append(span.mText);
                        innerFirst = false;
                        i2++;
                        str2 = str3;
                        first = first2;
                        textTrackCueSpanArr3 = textTrackCueSpanArr2;
                    }
                    str = str2;
                    textTrackCueSpanArr = textTrackCueSpanArr3;
                    builder.append("\"");
                }
                first = false;
                i++;
                str2 = str;
                textTrackCueSpanArr3 = textTrackCueSpanArr;
            }
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        return builder;
    }

    public String toString() {
        String str;
        StringBuilder res = new StringBuilder();
        StringBuilder append = res.append(WebVttParser.timeToString(this.mStartTimeMs)).append(" --> ").append(WebVttParser.timeToString(this.mEndTimeMs)).append(" {id:\"").append(this.mId).append("\", pauseOnExit:").append(this.mPauseOnExit).append(", direction:");
        int i = this.mWritingDirection;
        String str2 = "INVALID";
        if (i == 100) {
            str = Slice.HINT_HORIZONTAL;
        } else if (i == 102) {
            str = "vertical_lr";
        } else {
            str = i == 101 ? "vertical_rl" : "INVALID";
        }
        StringBuilder append2 = append.append(str).append(", regionId:\"").append(this.mRegionId).append("\", snapToLines:").append(this.mSnapToLines).append(", linePosition:").append(this.mAutoLinePosition ? "auto" : this.mLinePosition).append(", textPosition:").append(this.mTextPosition).append(", size:").append(this.mSize).append(", alignment:");
        int i2 = this.mAlignment;
        if (i2 == 202) {
            str2 = "end";
        } else if (i2 == 203) {
            str2 = NavigationBarInflaterView.LEFT;
        } else if (i2 == 200) {
            str2 = "middle";
        } else if (i2 == 204) {
            str2 = NavigationBarInflaterView.RIGHT;
        } else if (i2 == 201) {
            str2 = "start";
        }
        append2.append(str2).append(", text:");
        appendStringsToBuilder(res).append("}");
        return res.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override // android.media.SubtitleTrack.Cue
    public void onTime(long timeMs) {
        TextTrackCueSpan[][] textTrackCueSpanArr;
        for (TextTrackCueSpan[] line : this.mLines) {
            for (TextTrackCueSpan span : line) {
                span.mEnabled = timeMs >= span.mTimestampMs;
            }
        }
    }
}
