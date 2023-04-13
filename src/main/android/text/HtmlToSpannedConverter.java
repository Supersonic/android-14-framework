package android.text;

import android.app.ActivityThread;
import android.app.Application;
import android.app.backup.FullBackup;
import android.app.blob.XmlTags;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.TtmlUtils;
import android.provider.Telephony;
import android.text.Html;
import android.text.Layout;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
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
import com.android.internal.C4057R;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Html.java */
/* loaded from: classes3.dex */
public class HtmlToSpannedConverter implements ContentHandler {
    private static final float[] HEADING_SIZES = {1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1.0f};
    private static Pattern sBackgroundColorPattern;
    private static final Map<String, Integer> sColorMap;
    private static Pattern sForegroundColorPattern;
    private static Pattern sTextAlignPattern;
    private static Pattern sTextDecorationPattern;
    private int mFlags;
    private Html.ImageGetter mImageGetter;
    private XMLReader mReader;
    private String mSource;
    private SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder();
    private Html.TagHandler mTagHandler;

    static {
        HashMap hashMap = new HashMap();
        sColorMap = hashMap;
        hashMap.put("darkgray", -5658199);
        hashMap.put("gray", -8355712);
        hashMap.put("lightgray", -2894893);
        hashMap.put("darkgrey", -5658199);
        hashMap.put("grey", -8355712);
        hashMap.put("lightgrey", -2894893);
        hashMap.put("green", -16744448);
    }

    private static Pattern getTextAlignPattern() {
        if (sTextAlignPattern == null) {
            sTextAlignPattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b");
        }
        return sTextAlignPattern;
    }

    private static Pattern getForegroundColorPattern() {
        if (sForegroundColorPattern == null) {
            sForegroundColorPattern = Pattern.compile("(?:\\s+|\\A)color\\s*:\\s*(\\S*)\\b");
        }
        return sForegroundColorPattern;
    }

    private static Pattern getBackgroundColorPattern() {
        if (sBackgroundColorPattern == null) {
            sBackgroundColorPattern = Pattern.compile("(?:\\s+|\\A)background(?:-color)?\\s*:\\s*(\\S*)\\b");
        }
        return sBackgroundColorPattern;
    }

    private static Pattern getTextDecorationPattern() {
        if (sTextDecorationPattern == null) {
            sTextDecorationPattern = Pattern.compile("(?:\\s+|\\A)text-decoration\\s*:\\s*(\\S*)\\b");
        }
        return sTextDecorationPattern;
    }

    public HtmlToSpannedConverter(String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler, Parser parser, int flags) {
        this.mSource = source;
        this.mImageGetter = imageGetter;
        this.mTagHandler = tagHandler;
        this.mReader = parser;
        this.mFlags = flags;
    }

    public Spanned convert() {
        this.mReader.setContentHandler(this);
        try {
            this.mReader.parse(new InputSource(new StringReader(this.mSource)));
            SpannableStringBuilder spannableStringBuilder = this.mSpannableStringBuilder;
            Object[] obj = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ParagraphStyle.class);
            for (int i = 0; i < obj.length; i++) {
                int start = this.mSpannableStringBuilder.getSpanStart(obj[i]);
                int end = this.mSpannableStringBuilder.getSpanEnd(obj[i]);
                if (end - 2 >= 0 && this.mSpannableStringBuilder.charAt(end - 1) == '\n' && this.mSpannableStringBuilder.charAt(end - 2) == '\n') {
                    end--;
                }
                if (end == start) {
                    this.mSpannableStringBuilder.removeSpan(obj[i]);
                } else {
                    this.mSpannableStringBuilder.setSpan(obj[i], start, end, 51);
                }
            }
            return this.mSpannableStringBuilder;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e2) {
            throw new RuntimeException(e2);
        }
    }

    private void handleStartTag(String tag, Attributes attributes) {
        if (!tag.equalsIgnoreCase(TtmlUtils.TAG_BR)) {
            if (tag.equalsIgnoreCase("p")) {
                startBlockElement(this.mSpannableStringBuilder, attributes, getMarginParagraph());
                startCssStyle(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase("ul")) {
                startBlockElement(this.mSpannableStringBuilder, attributes, getMarginList());
            } else if (tag.equalsIgnoreCase("li")) {
                startLi(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_DIV)) {
                startBlockElement(this.mSpannableStringBuilder, attributes, getMarginDiv());
            } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_SPAN)) {
                startCssStyle(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase("strong")) {
                start(this.mSpannableStringBuilder, new Bold());
            } else if (tag.equalsIgnoreCase(XmlTags.TAG_BLOB)) {
                start(this.mSpannableStringBuilder, new Bold());
            } else if (tag.equalsIgnoreCase("em")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("cite")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("dfn")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("i")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("big")) {
                start(this.mSpannableStringBuilder, new Big());
            } else if (tag.equalsIgnoreCase("small")) {
                start(this.mSpannableStringBuilder, new Small());
            } else if (tag.equalsIgnoreCase(Context.FONT_SERVICE)) {
                startFont(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase("blockquote")) {
                startBlockquote(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_TT)) {
                start(this.mSpannableStringBuilder, new Monospace());
            } else if (tag.equalsIgnoreCase(FullBackup.APK_TREE_TOKEN)) {
                startA(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase(XmlTags.ATTR_UID)) {
                start(this.mSpannableStringBuilder, new Underline());
            } else if (tag.equalsIgnoreCase("del")) {
                start(this.mSpannableStringBuilder, new Strikethrough());
            } else if (tag.equalsIgnoreCase(XmlTags.TAG_SESSION)) {
                start(this.mSpannableStringBuilder, new Strikethrough());
            } else if (tag.equalsIgnoreCase("strike")) {
                start(this.mSpannableStringBuilder, new Strikethrough());
            } else if (tag.equalsIgnoreCase("sup")) {
                start(this.mSpannableStringBuilder, new Super());
            } else if (tag.equalsIgnoreCase(Telephony.BaseMmsColumns.SUBJECT)) {
                start(this.mSpannableStringBuilder, new Sub());
            } else if (tag.length() == 2 && Character.toLowerCase(tag.charAt(0)) == 'h' && tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
                startHeading(this.mSpannableStringBuilder, attributes, tag.charAt(1) - '1');
            } else if (tag.equalsIgnoreCase("img")) {
                startImg(this.mSpannableStringBuilder, attributes, this.mImageGetter);
            } else {
                Html.TagHandler tagHandler = this.mTagHandler;
                if (tagHandler != null) {
                    tagHandler.handleTag(true, tag, this.mSpannableStringBuilder, this.mReader);
                }
            }
        }
    }

    private void handleEndTag(String tag) {
        if (tag.equalsIgnoreCase(TtmlUtils.TAG_BR)) {
            handleBr(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("p")) {
            endCssStyle(this.mSpannableStringBuilder);
            endBlockElement(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("ul")) {
            endBlockElement(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("li")) {
            endLi(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_DIV)) {
            endBlockElement(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_SPAN)) {
            endCssStyle(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("strong")) {
            Application application = ActivityThread.currentApplication();
            int fontWeightAdjustment = application.getResources().getConfiguration().fontWeightAdjustment;
            end(this.mSpannableStringBuilder, Bold.class, new StyleSpan(1, fontWeightAdjustment));
        } else if (tag.equalsIgnoreCase(XmlTags.TAG_BLOB)) {
            Application application2 = ActivityThread.currentApplication();
            int fontWeightAdjustment2 = application2.getResources().getConfiguration().fontWeightAdjustment;
            end(this.mSpannableStringBuilder, Bold.class, new StyleSpan(1, fontWeightAdjustment2));
        } else if (tag.equalsIgnoreCase("em")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("cite")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("dfn")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("i")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("big")) {
            end(this.mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
        } else if (tag.equalsIgnoreCase("small")) {
            end(this.mSpannableStringBuilder, Small.class, new RelativeSizeSpan(0.8f));
        } else if (tag.equalsIgnoreCase(Context.FONT_SERVICE)) {
            endFont(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            endBlockquote(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase(TtmlUtils.TAG_TT)) {
            end(this.mSpannableStringBuilder, Monospace.class, new TypefaceSpan("monospace"));
        } else if (tag.equalsIgnoreCase(FullBackup.APK_TREE_TOKEN)) {
            endA(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase(XmlTags.ATTR_UID)) {
            end(this.mSpannableStringBuilder, Underline.class, new UnderlineSpan());
        } else if (tag.equalsIgnoreCase("del")) {
            end(this.mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase(XmlTags.TAG_SESSION)) {
            end(this.mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase("strike")) {
            end(this.mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase("sup")) {
            end(this.mSpannableStringBuilder, Super.class, new SuperscriptSpan());
        } else if (tag.equalsIgnoreCase(Telephony.BaseMmsColumns.SUBJECT)) {
            end(this.mSpannableStringBuilder, Sub.class, new SubscriptSpan());
        } else if (tag.length() == 2 && Character.toLowerCase(tag.charAt(0)) == 'h' && tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            endHeading(this.mSpannableStringBuilder);
        } else {
            Html.TagHandler tagHandler = this.mTagHandler;
            if (tagHandler != null) {
                tagHandler.handleTag(false, tag, this.mSpannableStringBuilder, this.mReader);
            }
        }
    }

    private int getMarginParagraph() {
        return getMargin(1);
    }

    private int getMarginHeading() {
        return getMargin(2);
    }

    private int getMarginListItem() {
        return getMargin(4);
    }

    private int getMarginList() {
        return getMargin(8);
    }

    private int getMarginDiv() {
        return getMargin(16);
    }

    private int getMarginBlockquote() {
        return getMargin(32);
    }

    private int getMargin(int flag) {
        if ((this.mFlags & flag) != 0) {
            return 1;
        }
        return 2;
    }

    private static void appendNewlines(Editable text, int minNewline) {
        int len = text.length();
        if (len == 0) {
            return;
        }
        int existingNewlines = 0;
        for (int i = len - 1; i >= 0 && text.charAt(i) == '\n'; i--) {
            existingNewlines++;
        }
        for (int j = existingNewlines; j < minNewline; j++) {
            text.append("\n");
        }
    }

    private static void startBlockElement(Editable text, Attributes attributes, int margin) {
        text.length();
        if (margin > 0) {
            appendNewlines(text, margin);
            start(text, new Newline(margin));
        }
        String style = attributes.getValue("", "style");
        if (style != null) {
            Matcher m = getTextAlignPattern().matcher(style);
            if (m.find()) {
                String alignment = m.group(1);
                if (alignment.equalsIgnoreCase("start")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_NORMAL));
                } else if (alignment.equalsIgnoreCase("center")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_CENTER));
                } else if (alignment.equalsIgnoreCase("end")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_OPPOSITE));
                }
            }
        }
    }

    private static void endBlockElement(Editable text) {
        Newline n = (Newline) getLast(text, Newline.class);
        if (n != null) {
            appendNewlines(text, n.mNumNewlines);
            text.removeSpan(n);
        }
        Alignment a = (Alignment) getLast(text, Alignment.class);
        if (a != null) {
            setSpanFromMark(text, a, new AlignmentSpan.Standard(a.mAlignment));
        }
    }

    private static void handleBr(Editable text) {
        text.append('\n');
    }

    private void startLi(Editable text, Attributes attributes) {
        startBlockElement(text, attributes, getMarginListItem());
        start(text, new Bullet());
        startCssStyle(text, attributes);
    }

    private static void endLi(Editable text) {
        endCssStyle(text);
        endBlockElement(text);
        end(text, Bullet.class, new BulletSpan());
    }

    private void startBlockquote(Editable text, Attributes attributes) {
        startBlockElement(text, attributes, getMarginBlockquote());
        start(text, new Blockquote());
    }

    private static void endBlockquote(Editable text) {
        endBlockElement(text);
        end(text, Blockquote.class, new QuoteSpan());
    }

    private void startHeading(Editable text, Attributes attributes, int level) {
        startBlockElement(text, attributes, getMarginHeading());
        start(text, new Heading(level));
    }

    private static void endHeading(Editable text) {
        Heading h = (Heading) getLast(text, Heading.class);
        if (h != null) {
            Application application = ActivityThread.currentApplication();
            int fontWeightAdjustment = application.getResources().getConfiguration().fontWeightAdjustment;
            setSpanFromMark(text, h, new RelativeSizeSpan(HEADING_SIZES[h.mLevel]), new StyleSpan(1, fontWeightAdjustment));
        }
        endBlockElement(text);
    }

    private static <T> T getLast(Spanned text, Class<T> kind) {
        Object[] spans = text.getSpans(0, text.length(), kind);
        if (spans.length == 0) {
            return null;
        }
        return (T) spans[spans.length - 1];
    }

    private static void setSpanFromMark(Spannable text, Object mark, Object... spans) {
        int where = text.getSpanStart(mark);
        text.removeSpan(mark);
        int len = text.length();
        if (where != len) {
            for (Object span : spans) {
                text.setSpan(span, where, len, 33);
            }
        }
    }

    private static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, 17);
    }

    private static void end(Editable text, Class kind, Object repl) {
        text.length();
        Object obj = getLast(text, kind);
        if (obj != null) {
            setSpanFromMark(text, obj, repl);
        }
    }

    private void startCssStyle(Editable text, Attributes attributes) {
        int c;
        int c2;
        String style = attributes.getValue("", "style");
        if (style != null) {
            Matcher m = getForegroundColorPattern().matcher(style);
            if (m.find() && (c2 = getHtmlColor(m.group(1))) != -1) {
                start(text, new Foreground(c2 | (-16777216)));
            }
            Matcher m2 = getBackgroundColorPattern().matcher(style);
            if (m2.find() && (c = getHtmlColor(m2.group(1))) != -1) {
                start(text, new Background((-16777216) | c));
            }
            Matcher m3 = getTextDecorationPattern().matcher(style);
            if (m3.find()) {
                String textDecoration = m3.group(1);
                if (textDecoration.equalsIgnoreCase("line-through")) {
                    start(text, new Strikethrough());
                }
            }
        }
    }

    private static void endCssStyle(Editable text) {
        Strikethrough s = (Strikethrough) getLast(text, Strikethrough.class);
        if (s != null) {
            setSpanFromMark(text, s, new StrikethroughSpan());
        }
        Background b = (Background) getLast(text, Background.class);
        if (b != null) {
            setSpanFromMark(text, b, new BackgroundColorSpan(b.mBackgroundColor));
        }
        Foreground f = (Foreground) getLast(text, Foreground.class);
        if (f != null) {
            setSpanFromMark(text, f, new ForegroundColorSpan(f.mForegroundColor));
        }
    }

    private static void startImg(Editable text, Attributes attributes, Html.ImageGetter img) {
        String src = attributes.getValue("", "src");
        Drawable d = null;
        if (img != null) {
            d = img.getDrawable(src);
        }
        if (d == null) {
            d = Resources.getSystem().getDrawable(C4057R.C4058drawable.unknown_image);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
        int len = text.length();
        text.append("￼");
        text.setSpan(new ImageSpan(d, src), len, text.length(), 33);
    }

    private void startFont(Editable text, Attributes attributes) {
        int c;
        String color = attributes.getValue("", "color");
        String face = attributes.getValue("", Context.FACE_SERVICE);
        if (!TextUtils.isEmpty(color) && (c = getHtmlColor(color)) != -1) {
            start(text, new Foreground((-16777216) | c));
        }
        if (!TextUtils.isEmpty(face)) {
            start(text, new Font(face));
        }
    }

    private static void endFont(Editable text) {
        Font font = (Font) getLast(text, Font.class);
        if (font != null) {
            setSpanFromMark(text, font, new TypefaceSpan(font.mFace));
        }
        Foreground foreground = (Foreground) getLast(text, Foreground.class);
        if (foreground != null) {
            setSpanFromMark(text, foreground, new ForegroundColorSpan(foreground.mForegroundColor));
        }
    }

    private static void startA(Editable text, Attributes attributes) {
        String href = attributes.getValue("", "href");
        start(text, new Href(href));
    }

    private static void endA(Editable text) {
        Href h = (Href) getLast(text, Href.class);
        if (h != null && h.mHref != null) {
            setSpanFromMark(text, h, new URLSpan(h.mHref));
        }
    }

    private int getHtmlColor(String color) {
        Integer i;
        if ((this.mFlags & 256) == 256 && (i = sColorMap.get(color.toLowerCase(Locale.US))) != null) {
            return i.intValue();
        }
        if (Character.isLetter(color.charAt(0))) {
            try {
                return Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                return -1;
            }
        }
        try {
            return XmlUtils.convertValueToInt(color, -1);
        } catch (NumberFormatException e2) {
            return -1;
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        handleStartTag(localName, attributes);
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        char pred;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = ch[i + start];
            if (c == ' ' || c == '\n') {
                int len = sb.length();
                if (len == 0) {
                    int len2 = this.mSpannableStringBuilder.length();
                    if (len2 == 0) {
                        pred = '\n';
                    } else {
                        pred = this.mSpannableStringBuilder.charAt(len2 - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }
                if (pred != ' ' && pred != '\n') {
                    sb.append(' ');
                }
            } else {
                sb.append(c);
            }
        }
        this.mSpannableStringBuilder.append((CharSequence) sb);
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Bold {
        private Bold() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Italic {
        private Italic() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Underline {
        private Underline() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Strikethrough {
        private Strikethrough() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Big {
        private Big() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Small {
        private Small() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Monospace {
        private Monospace() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Blockquote {
        private Blockquote() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Super {
        private Super() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Sub {
        private Sub() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Bullet {
        private Bullet() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Font {
        public String mFace;

        public Font(String face) {
            this.mFace = face;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Href {
        public String mHref;

        public Href(String href) {
            this.mHref = href;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Foreground {
        private int mForegroundColor;

        public Foreground(int foregroundColor) {
            this.mForegroundColor = foregroundColor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Background {
        private int mBackgroundColor;

        public Background(int backgroundColor) {
            this.mBackgroundColor = backgroundColor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Heading {
        private int mLevel;

        public Heading(int level) {
            this.mLevel = level;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Newline {
        private int mNumNewlines;

        public Newline(int numNewlines) {
            this.mNumNewlines = numNewlines;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: classes3.dex */
    public static class Alignment {
        private Layout.Alignment mAlignment;

        public Alignment(Layout.Alignment alignment) {
            this.mAlignment = alignment;
        }
    }
}
