package android.text.style;

import android.text.Spannable;
import android.util.LongArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public class SpanUtils {
    private SpanUtils() {
    }

    public static boolean toggleBold(Spannable spannable, int min, int max) {
        if (min == max) {
            return false;
        }
        StyleSpan[] boldSpans = (StyleSpan[]) spannable.getSpans(min, max, StyleSpan.class);
        ArrayList<StyleSpan> filteredBoldSpans = new ArrayList<>();
        for (StyleSpan span : boldSpans) {
            if ((span.getStyle() & 1) == 1) {
                filteredBoldSpans.add(span);
            }
        }
        if (!isCovered(spannable, filteredBoldSpans, min, max)) {
            spannable.setSpan(new StyleSpan(1), min, max, 17);
            return true;
        }
        for (int si = 0; si < filteredBoldSpans.size(); si++) {
            StyleSpan span2 = filteredBoldSpans.get(si);
            int start = spannable.getSpanStart(span2);
            int end = spannable.getSpanEnd(span2);
            int flag = spannable.getSpanFlags(span2);
            boolean needItalicSpan = (span2.getStyle() & 2) == 2;
            if (start < min) {
                if (end > max) {
                    spannable.setSpan(span2, start, min, flag);
                    spannable.setSpan(new StyleSpan(span2.getStyle()), max, end, flag);
                    if (needItalicSpan) {
                        spannable.setSpan(new StyleSpan(2), min, max, flag);
                    }
                } else {
                    spannable.setSpan(span2, start, min, flag);
                    if (needItalicSpan) {
                        spannable.setSpan(new StyleSpan(2), min, end, flag);
                    }
                }
            } else if (end > max) {
                spannable.setSpan(span2, max, end, flag);
                if (needItalicSpan) {
                    spannable.setSpan(new StyleSpan(2), max, end, flag);
                }
            } else {
                spannable.removeSpan(span2);
                if (needItalicSpan) {
                    spannable.setSpan(new StyleSpan(2), start, end, flag);
                }
            }
        }
        return true;
    }

    public static boolean toggleItalic(Spannable spannable, int min, int max) {
        if (min == max) {
            return false;
        }
        StyleSpan[] boldSpans = (StyleSpan[]) spannable.getSpans(min, max, StyleSpan.class);
        ArrayList<StyleSpan> filteredBoldSpans = new ArrayList<>();
        for (StyleSpan span : boldSpans) {
            if ((span.getStyle() & 2) == 2) {
                filteredBoldSpans.add(span);
            }
        }
        if (!isCovered(spannable, filteredBoldSpans, min, max)) {
            spannable.setSpan(new StyleSpan(2), min, max, 17);
            return true;
        }
        for (int si = 0; si < filteredBoldSpans.size(); si++) {
            StyleSpan span2 = filteredBoldSpans.get(si);
            int start = spannable.getSpanStart(span2);
            int end = spannable.getSpanEnd(span2);
            int flag = spannable.getSpanFlags(span2);
            boolean needBoldSpan = (span2.getStyle() & 1) == 1;
            if (start < min) {
                if (end > max) {
                    spannable.setSpan(span2, start, min, flag);
                    spannable.setSpan(new StyleSpan(span2.getStyle()), max, end, flag);
                    if (needBoldSpan) {
                        spannable.setSpan(new StyleSpan(1), min, max, flag);
                    }
                } else {
                    spannable.setSpan(span2, start, min, flag);
                    if (needBoldSpan) {
                        spannable.setSpan(new StyleSpan(1), min, end, flag);
                    }
                }
            } else if (end > max) {
                spannable.setSpan(span2, max, end, flag);
                if (needBoldSpan) {
                    spannable.setSpan(new StyleSpan(1), max, end, flag);
                }
            } else {
                spannable.removeSpan(span2);
                if (needBoldSpan) {
                    spannable.setSpan(new StyleSpan(1), start, end, flag);
                }
            }
        }
        return true;
    }

    public static boolean toggleUnderline(Spannable spannable, int min, int max) {
        if (min == max) {
            return false;
        }
        List<UnderlineSpan> spans = Arrays.asList((UnderlineSpan[]) spannable.getSpans(min, max, UnderlineSpan.class));
        if (!isCovered(spannable, spans, min, max)) {
            spannable.setSpan(new UnderlineSpan(), min, max, 17);
            return true;
        }
        for (int si = 0; si < spans.size(); si++) {
            UnderlineSpan span = spans.get(si);
            int start = spannable.getSpanStart(span);
            int end = spannable.getSpanEnd(span);
            int flag = spannable.getSpanFlags(span);
            if (start < min) {
                if (end > max) {
                    spannable.setSpan(span, start, min, flag);
                    spannable.setSpan(new UnderlineSpan(), max, end, flag);
                } else {
                    spannable.setSpan(span, start, min, flag);
                }
            } else if (end > max) {
                spannable.setSpan(span, max, end, flag);
            } else {
                spannable.removeSpan(span);
            }
        }
        return true;
    }

    private static long pack(int from, int to) {
        return (from << 32) | to;
    }

    private static int min(long packed) {
        return (int) (packed >> 32);
    }

    private static int max(long packed) {
        return (int) (4294967295L & packed);
    }

    private static boolean hasIntersection(int aMin, int aMax, int bMin, int bMax) {
        return aMin < bMax && bMin < aMax;
    }

    private static long intersection(int aMin, int aMax, int bMin, int bMax) {
        return pack(Math.max(aMin, bMin), Math.min(aMax, bMax));
    }

    private static <T> boolean isCovered(Spannable spannable, List<T> spans, int min, int max) {
        Spannable spannable2 = spannable;
        if (min == max) {
            return false;
        }
        LongArray uncoveredRanges = new LongArray();
        LongArray nextUncoveredRanges = new LongArray();
        uncoveredRanges.add(pack(min, max));
        int si = 0;
        while (si < spans.size()) {
            T span = spans.get(si);
            int start = spannable2.getSpanStart(span);
            int end = spannable2.getSpanEnd(span);
            for (int i = 0; i < uncoveredRanges.size(); i++) {
                long packed = uncoveredRanges.get(i);
                int uncoveredStart = min(packed);
                int uncoveredEnd = max(packed);
                if (!hasIntersection(start, end, uncoveredStart, uncoveredEnd)) {
                    nextUncoveredRanges.add(packed);
                } else {
                    long intersectionPack = intersection(start, end, uncoveredStart, uncoveredEnd);
                    int intersectStart = min(intersectionPack);
                    int intersectEnd = max(intersectionPack);
                    if (uncoveredStart != intersectStart) {
                        nextUncoveredRanges.add(pack(uncoveredStart, intersectStart));
                    }
                    if (intersectEnd != uncoveredEnd) {
                        nextUncoveredRanges.add(pack(intersectEnd, uncoveredEnd));
                    }
                }
            }
            if (nextUncoveredRanges.size() == 0) {
                return true;
            }
            LongArray tmp = nextUncoveredRanges;
            nextUncoveredRanges = uncoveredRanges;
            uncoveredRanges = tmp;
            nextUncoveredRanges.clear();
            si++;
            spannable2 = spannable;
        }
        return false;
    }
}
