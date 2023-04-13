package android.text;

import android.app.ActivityThread;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.text.Layout;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
/* loaded from: classes3.dex */
public class Html {
    public static final int FROM_HTML_MODE_COMPACT = 63;
    public static final int FROM_HTML_MODE_LEGACY = 0;
    public static final int FROM_HTML_OPTION_USE_CSS_COLORS = 256;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 32;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 16;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 2;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 8;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 4;
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 1;
    private static final int TO_HTML_PARAGRAPH_FLAG = 1;
    public static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0;
    public static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 1;

    /* loaded from: classes3.dex */
    public interface ImageGetter {
        Drawable getDrawable(String str);
    }

    /* loaded from: classes3.dex */
    public interface TagHandler {
        void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader);
    }

    private Html() {
    }

    @Deprecated
    public static Spanned fromHtml(String source) {
        return fromHtml(source, 0, null, null);
    }

    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();

        private HtmlParser() {
        }
    }

    @Deprecated
    public static Spanned fromHtml(String source, ImageGetter imageGetter, TagHandler tagHandler) {
        return fromHtml(source, 0, imageGetter, tagHandler);
    }

    public static Spanned fromHtml(String source, int flags, ImageGetter imageGetter, TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty("http://www.ccil.org/~cowan/tagsoup/properties/schema", HtmlParser.schema);
            HtmlToSpannedConverter converter = new HtmlToSpannedConverter(source, imageGetter, tagHandler, parser, flags);
            return converter.convert();
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e2) {
            throw new RuntimeException(e2);
        }
    }

    @Deprecated
    public static String toHtml(Spanned text) {
        return toHtml(text, 0);
    }

    public static String toHtml(Spanned text, int option) {
        StringBuilder out = new StringBuilder();
        withinHtml(out, text, option);
        return out.toString();
    }

    public static String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private static void withinHtml(StringBuilder out, Spanned text, int option) {
        if ((option & 1) == 0) {
            encodeTextAlignmentByDiv(out, text, option);
        } else {
            withinDiv(out, text, 0, text.length(), option);
        }
    }

    private static void encodeTextAlignmentByDiv(StringBuilder out, Spanned text, int option) {
        int len = text.length();
        int i = 0;
        while (i < len) {
            int next = text.nextSpanTransition(i, len, ParagraphStyle.class);
            ParagraphStyle[] style = (ParagraphStyle[]) text.getSpans(i, next, ParagraphStyle.class);
            String elements = " ";
            boolean needDiv = false;
            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof AlignmentSpan) {
                    Layout.Alignment align = ((AlignmentSpan) style[j]).getAlignment();
                    needDiv = true;
                    if (align == Layout.Alignment.ALIGN_CENTER) {
                        elements = "align=\"center\" " + elements;
                    } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                        elements = "align=\"right\" " + elements;
                    } else {
                        elements = "align=\"left\" " + elements;
                    }
                }
            }
            if (needDiv) {
                out.append("<div ").append(elements).append(">");
            }
            withinDiv(out, text, i, next, option);
            if (needDiv) {
                out.append("</div>");
            }
            i = next;
        }
    }

    private static void withinDiv(StringBuilder out, Spanned text, int start, int end, int option) {
        int i = start;
        while (i < end) {
            int next = text.nextSpanTransition(i, end, QuoteSpan.class);
            QuoteSpan[] quotes = (QuoteSpan[]) text.getSpans(i, next, QuoteSpan.class);
            for (QuoteSpan quoteSpan : quotes) {
                out.append("<blockquote>");
            }
            withinBlockquote(out, text, i, next, option);
            for (QuoteSpan quoteSpan2 : quotes) {
                out.append("</blockquote>\n");
            }
            i = next;
        }
    }

    private static String getTextDirection(Spanned text, int start, int end) {
        if (TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(text, start, end - start)) {
            return " dir=\"rtl\"";
        }
        return " dir=\"ltr\"";
    }

    private static String getTextStyles(Spanned text, int start, int end, boolean forceNoVerticalMargin, boolean includeTextAlign) {
        String margin = null;
        String textAlign = null;
        if (forceNoVerticalMargin) {
            margin = "margin-top:0; margin-bottom:0;";
        }
        if (includeTextAlign) {
            AlignmentSpan[] alignmentSpans = (AlignmentSpan[]) text.getSpans(start, end, AlignmentSpan.class);
            int i = alignmentSpans.length - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                AlignmentSpan s = alignmentSpans[i];
                if ((text.getSpanFlags(s) & 51) != 51) {
                    i--;
                } else {
                    Layout.Alignment alignment = s.getAlignment();
                    if (alignment == Layout.Alignment.ALIGN_NORMAL) {
                        textAlign = "text-align:start;";
                    } else if (alignment == Layout.Alignment.ALIGN_CENTER) {
                        textAlign = "text-align:center;";
                    } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        textAlign = "text-align:end;";
                    }
                }
            }
        }
        if (margin == null && textAlign == null) {
            return "";
        }
        StringBuilder style = new StringBuilder(" style=\"");
        if (margin != null && textAlign != null) {
            style.append(margin).append(" ").append(textAlign);
        } else if (margin != null) {
            style.append(margin);
        } else if (textAlign != null) {
            style.append(textAlign);
        }
        return style.append("\"").toString();
    }

    private static void withinBlockquote(StringBuilder out, Spanned text, int start, int end, int option) {
        if ((option & 1) == 0) {
            withinBlockquoteConsecutive(out, text, start, end);
        } else {
            withinBlockquoteIndividual(out, text, start, end);
        }
    }

    private static void withinBlockquoteIndividual(StringBuilder out, Spanned text, int start, int end) {
        int next;
        boolean isInList = false;
        for (int i = start; i <= end; i = next + 1) {
            next = TextUtils.indexOf((CharSequence) text, '\n', i, end);
            if (next < 0) {
                next = end;
            }
            if (next == i) {
                if (isInList) {
                    isInList = false;
                    out.append("</ul>\n");
                }
                out.append("<br>\n");
            } else {
                boolean isListItem = false;
                ParagraphStyle[] paragraphStyles = (ParagraphStyle[]) text.getSpans(i, next, ParagraphStyle.class);
                int length = paragraphStyles.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    ParagraphStyle paragraphStyle = paragraphStyles[i2];
                    int spanFlags = text.getSpanFlags(paragraphStyle);
                    if ((spanFlags & 51) != 51 || !(paragraphStyle instanceof BulletSpan)) {
                        i2++;
                    } else {
                        isListItem = true;
                        break;
                    }
                }
                if (isListItem && !isInList) {
                    isInList = true;
                    out.append("<ul").append(getTextStyles(text, i, next, true, false)).append(">\n");
                }
                if (isInList && !isListItem) {
                    isInList = false;
                    out.append("</ul>\n");
                }
                String tagType = isListItem ? "li" : "p";
                out.append("<").append(tagType).append(getTextDirection(text, i, next)).append(getTextStyles(text, i, next, !isListItem, true)).append(">");
                withinParagraph(out, text, i, next);
                out.append("</");
                out.append(tagType);
                out.append(">\n");
                if (next == end && isInList) {
                    isInList = false;
                    out.append("</ul>\n");
                }
            }
        }
    }

    private static void withinBlockquoteConsecutive(StringBuilder out, Spanned text, int start, int end) {
        out.append("<p").append(getTextDirection(text, start, end)).append(">");
        int i = start;
        while (i < end) {
            int next = TextUtils.indexOf((CharSequence) text, '\n', i, end);
            if (next < 0) {
                next = end;
            }
            int nl = 0;
            while (next < end && text.charAt(next) == '\n') {
                nl++;
                next++;
            }
            withinParagraph(out, text, i, next - nl);
            if (nl == 1) {
                out.append("<br>\n");
            } else {
                for (int j = 2; j < nl; j++) {
                    out.append("<br>");
                }
                if (next != end) {
                    out.append("</p>\n");
                    out.append("<p").append(getTextDirection(text, start, end)).append(">");
                }
            }
            i = next;
        }
        out.append("</p>\n");
    }

    private static void withinParagraph(StringBuilder out, Spanned text, int start, int end) {
        int i = start;
        while (i < end) {
            int next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] style = (CharacterStyle[]) text.getSpans(i, next, CharacterStyle.class);
            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();
                    if ((s & 1) != 0) {
                        out.append("<b>");
                    }
                    if ((s & 2) != 0) {
                        out.append("<i>");
                    }
                }
                if ((style[j] instanceof TypefaceSpan) && "monospace".equals(((TypefaceSpan) style[j]).getFamily())) {
                    out.append("<tt>");
                }
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("<sup>");
                }
                if (style[j] instanceof SubscriptSpan) {
                    out.append("<sub>");
                }
                if (style[j] instanceof UnderlineSpan) {
                    out.append("<u>");
                }
                if (style[j] instanceof StrikethroughSpan) {
                    out.append("<span style=\"text-decoration:line-through;\">");
                }
                if (style[j] instanceof URLSpan) {
                    out.append("<a href=\"");
                    out.append(((URLSpan) style[j]).getURL());
                    out.append("\">");
                }
                if (style[j] instanceof ImageSpan) {
                    out.append("<img src=\"");
                    out.append(((ImageSpan) style[j]).getSource());
                    out.append("\">");
                    i = next;
                }
                if (style[j] instanceof AbsoluteSizeSpan) {
                    AbsoluteSizeSpan s2 = (AbsoluteSizeSpan) style[j];
                    float sizeDip = s2.getSize();
                    if (!s2.getDip()) {
                        Application application = ActivityThread.currentApplication();
                        sizeDip /= application.getResources().getDisplayMetrics().density;
                    }
                    out.append(String.format("<span style=\"font-size:%.0fpx\";>", Float.valueOf(sizeDip)));
                }
                if (style[j] instanceof RelativeSizeSpan) {
                    float sizeEm = ((RelativeSizeSpan) style[j]).getSizeChange();
                    out.append(String.format("<span style=\"font-size:%.2fem;\">", Float.valueOf(sizeEm)));
                }
                if (style[j] instanceof ForegroundColorSpan) {
                    int color = ((ForegroundColorSpan) style[j]).getForegroundColor();
                    out.append(String.format("<span style=\"color:#%06X;\">", Integer.valueOf(color & 16777215)));
                }
                if (style[j] instanceof BackgroundColorSpan) {
                    int color2 = ((BackgroundColorSpan) style[j]).getBackgroundColor();
                    out.append(String.format("<span style=\"background-color:#%06X;\">", Integer.valueOf(16777215 & color2)));
                }
            }
            withinStyle(out, text, i, next);
            for (int j2 = style.length - 1; j2 >= 0; j2--) {
                if (style[j2] instanceof BackgroundColorSpan) {
                    out.append("</span>");
                }
                if (style[j2] instanceof ForegroundColorSpan) {
                    out.append("</span>");
                }
                if (style[j2] instanceof RelativeSizeSpan) {
                    out.append("</span>");
                }
                if (style[j2] instanceof AbsoluteSizeSpan) {
                    out.append("</span>");
                }
                if (style[j2] instanceof URLSpan) {
                    out.append("</a>");
                }
                if (style[j2] instanceof StrikethroughSpan) {
                    out.append("</span>");
                }
                if (style[j2] instanceof UnderlineSpan) {
                    out.append("</u>");
                }
                if (style[j2] instanceof SubscriptSpan) {
                    out.append("</sub>");
                }
                if (style[j2] instanceof SuperscriptSpan) {
                    out.append("</sup>");
                }
                if ((style[j2] instanceof TypefaceSpan) && ((TypefaceSpan) style[j2]).getFamily().equals("monospace")) {
                    out.append("</tt>");
                }
                if (style[j2] instanceof StyleSpan) {
                    int s3 = ((StyleSpan) style[j2]).getStyle();
                    if ((s3 & 1) != 0) {
                        out.append("</b>");
                    }
                    if ((s3 & 2) != 0) {
                        out.append("</i>");
                    }
                }
            }
            i = next;
        }
    }

    private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
        char d;
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 55296 && c <= 57343) {
                if (c < 56320 && i + 1 < end && (d = text.charAt(i + 1)) >= 56320 && d <= 57343) {
                    i++;
                    int codepoint = ((c - 55296) << 10) | 65536 | (d - 56320);
                    out.append("&#").append(codepoint).append(NavigationBarInflaterView.GRAVITY_SEPARATOR);
                }
            } else if (c > '~' || c < ' ') {
                out.append("&#").append((int) c).append(NavigationBarInflaterView.GRAVITY_SEPARATOR);
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }
                out.append(' ');
            } else {
                out.append(c);
            }
            i++;
        }
    }
}
