package android.text.util;

import android.app.ActivityThread;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.EventLog;
import android.util.Log;
import android.util.Patterns;
import android.webkit.WebView;
import android.widget.TextView;
import com.android.i18n.phonenumbers.PhoneNumberMatch;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
public class Linkify {
    @Deprecated
    public static final int ALL = 15;
    public static final int EMAIL_ADDRESSES = 2;
    private static final String LOG_TAG = "Linkify";
    @Deprecated
    public static final int MAP_ADDRESSES = 8;
    public static final int PHONE_NUMBERS = 4;
    private static final int PHONE_NUMBER_MINIMUM_DIGITS = 5;
    public static final int WEB_URLS = 1;
    public static final MatchFilter sUrlMatchFilter = new MatchFilter() { // from class: android.text.util.Linkify.1
        @Override // android.text.util.Linkify.MatchFilter
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            if (start == 0 || s.charAt(start - 1) != '@') {
                return true;
            }
            return false;
        }
    };
    public static final MatchFilter sPhoneNumberMatchFilter = new MatchFilter() { // from class: android.text.util.Linkify.2
        @Override // android.text.util.Linkify.MatchFilter
        public final boolean acceptMatch(CharSequence s, int start, int end) {
            int digitCount = 0;
            for (int i = start; i < end; i++) {
                if (Character.isDigit(s.charAt(i)) && (digitCount = digitCount + 1) >= 5) {
                    return true;
                }
            }
            return false;
        }
    };
    public static final TransformFilter sPhoneNumberTransformFilter = new TransformFilter() { // from class: android.text.util.Linkify.3
        @Override // android.text.util.Linkify.TransformFilter
        public final String transformUrl(Matcher match, String url) {
            return Patterns.digitsAndPlusOnly(match);
        }
    };
    private static final Function<String, URLSpan> DEFAULT_SPAN_FACTORY = new Function() { // from class: android.text.util.Linkify$$ExternalSyntheticLambda0
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Linkify.lambda$static$0((String) obj);
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LinkifyMask {
    }

    /* loaded from: classes3.dex */
    public interface MatchFilter {
        boolean acceptMatch(CharSequence charSequence, int i, int i2);
    }

    /* loaded from: classes3.dex */
    public interface TransformFilter {
        String transformUrl(Matcher matcher, String str);
    }

    public static final boolean addLinks(Spannable text, int mask) {
        return addLinks(text, mask, null, null);
    }

    public static final boolean addLinks(Spannable text, int mask, Function<String, URLSpan> urlSpanFactory) {
        return addLinks(text, mask, null, urlSpanFactory);
    }

    private static boolean addLinks(Spannable text, int mask, Context context, Function<String, URLSpan> urlSpanFactory) {
        if (text != null && containsUnsupportedCharacters(text.toString())) {
            EventLog.writeEvent(1397638484, "116321860", -1, "");
            return false;
        } else if (mask == 0) {
            return false;
        } else {
            URLSpan[] old = (URLSpan[]) text.getSpans(0, text.length(), URLSpan.class);
            for (int i = old.length - 1; i >= 0; i--) {
                text.removeSpan(old[i]);
            }
            ArrayList<LinkSpec> links = new ArrayList<>();
            if ((mask & 1) != 0) {
                gatherLinks(links, text, Patterns.AUTOLINK_WEB_URL, new String[]{"http://", "https://", "rtsp://", "ftp://"}, sUrlMatchFilter, null);
            }
            if ((mask & 2) != 0) {
                gatherLinks(links, text, Patterns.AUTOLINK_EMAIL_ADDRESS, new String[]{"mailto:"}, null, null);
            }
            if ((mask & 4) != 0) {
                gatherTelLinks(links, text, context);
            }
            if ((mask & 8) != 0) {
                gatherMapLinks(links, text);
            }
            pruneOverlaps(links);
            if (links.size() == 0) {
                return false;
            }
            Iterator<LinkSpec> it = links.iterator();
            while (it.hasNext()) {
                LinkSpec link = it.next();
                applyLink(link.url, link.start, link.end, text, urlSpanFactory);
            }
            return true;
        }
    }

    public static boolean containsUnsupportedCharacters(String text) {
        if (text.contains("\u202c")) {
            Log.m110e(LOG_TAG, "Unsupported character for applying links: u202C");
            return true;
        } else if (text.contains("\u202d")) {
            Log.m110e(LOG_TAG, "Unsupported character for applying links: u202D");
            return true;
        } else if (text.contains("\u202e")) {
            Log.m110e(LOG_TAG, "Unsupported character for applying links: u202E");
            return true;
        } else {
            return false;
        }
    }

    public static final boolean addLinks(TextView text, int mask) {
        if (mask == 0) {
            return false;
        }
        Context context = text.getContext();
        CharSequence t = text.getText();
        if (t instanceof Spannable) {
            if (!addLinks((Spannable) t, mask, context, null)) {
                return false;
            }
            addLinkMovementMethod(text);
            return true;
        }
        SpannableString s = SpannableString.valueOf(t);
        if (!addLinks(s, mask, context, null)) {
            return false;
        }
        addLinkMovementMethod(text);
        text.setText(s);
        return true;
    }

    private static final void addLinkMovementMethod(TextView t) {
        MovementMethod m = t.getMovementMethod();
        if ((m == null || !(m instanceof LinkMovementMethod)) && t.getLinksClickable()) {
            t.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static final void addLinks(TextView text, Pattern pattern, String scheme) {
        addLinks(text, pattern, scheme, (String[]) null, (MatchFilter) null, (TransformFilter) null);
    }

    public static final void addLinks(TextView text, Pattern pattern, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
        addLinks(text, pattern, scheme, (String[]) null, matchFilter, transformFilter);
    }

    public static final void addLinks(TextView text, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        SpannableString spannable = SpannableString.valueOf(text.getText());
        boolean linksAdded = addLinks(spannable, pattern, defaultScheme, schemes, matchFilter, transformFilter);
        if (linksAdded) {
            text.setText(spannable);
            addLinkMovementMethod(text);
        }
    }

    public static final boolean addLinks(Spannable text, Pattern pattern, String scheme) {
        return addLinks(text, pattern, scheme, (String[]) null, (MatchFilter) null, (TransformFilter) null);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String scheme, MatchFilter matchFilter, TransformFilter transformFilter) {
        return addLinks(spannable, pattern, scheme, (String[]) null, matchFilter, transformFilter);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        return addLinks(spannable, pattern, defaultScheme, schemes, matchFilter, transformFilter, null);
    }

    public static final boolean addLinks(Spannable spannable, Pattern pattern, String defaultScheme, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter, Function<String, URLSpan> urlSpanFactory) {
        if (spannable != null && containsUnsupportedCharacters(spannable.toString())) {
            EventLog.writeEvent(1397638484, "116321860", -1, "");
            return false;
        }
        if (defaultScheme == null) {
            defaultScheme = "";
        }
        if (schemes == null || schemes.length < 1) {
            schemes = EmptyArray.STRING;
        }
        String[] schemesCopy = new String[schemes.length + 1];
        schemesCopy[0] = defaultScheme.toLowerCase(Locale.ROOT);
        for (int index = 0; index < schemes.length; index++) {
            String scheme = schemes[index];
            schemesCopy[index + 1] = scheme == null ? "" : scheme.toLowerCase(Locale.ROOT);
        }
        boolean hasMatches = false;
        Matcher m = pattern.matcher(spannable);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            boolean allowed = true;
            if (matchFilter != null) {
                allowed = matchFilter.acceptMatch(spannable, start, end);
            }
            if (allowed) {
                String url = makeUrl(m.group(0), schemesCopy, m, transformFilter);
                applyLink(url, start, end, spannable, urlSpanFactory);
                hasMatches = true;
            }
        }
        return hasMatches;
    }

    private static void applyLink(String url, int start, int end, Spannable text, Function<String, URLSpan> urlSpanFactory) {
        if (urlSpanFactory == null) {
            urlSpanFactory = DEFAULT_SPAN_FACTORY;
        }
        URLSpan span = urlSpanFactory.apply(url);
        text.setSpan(span, start, end, 33);
    }

    private static final String makeUrl(String url, String[] prefixes, Matcher matcher, TransformFilter filter) {
        if (filter != null) {
            url = filter.transformUrl(matcher, url);
        }
        boolean hasPrefix = false;
        int i = 0;
        while (true) {
            if (i >= prefixes.length) {
                break;
            }
            if (!url.regionMatches(true, 0, prefixes[i], 0, prefixes[i].length())) {
                i++;
            } else {
                hasPrefix = true;
                if (!url.regionMatches(false, 0, prefixes[i], 0, prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }
            }
        }
        if (!hasPrefix && prefixes.length > 0) {
            return prefixes[0] + url;
        }
        return url;
    }

    private static final void gatherLinks(ArrayList<LinkSpec> links, Spannable s, Pattern pattern, String[] schemes, MatchFilter matchFilter, TransformFilter transformFilter) {
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                LinkSpec spec = new LinkSpec();
                String url = makeUrl(m.group(0), schemes, m, transformFilter);
                spec.url = url;
                spec.start = start;
                spec.end = end;
                links.add(spec);
            }
        }
    }

    private static void gatherTelLinks(ArrayList<LinkSpec> links, Spannable s, Context context) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Context ctx = context != null ? context : ActivityThread.currentApplication();
        String regionCode = ctx != null ? ((TelephonyManager) ctx.getSystemService(TelephonyManager.class)).getSimCountryIso().toUpperCase(Locale.US) : Locale.getDefault().getCountry();
        Iterable<PhoneNumberMatch> matches = phoneUtil.findNumbers(s.toString(), regionCode, PhoneNumberUtil.Leniency.POSSIBLE, Long.MAX_VALUE);
        for (PhoneNumberMatch match : matches) {
            LinkSpec spec = new LinkSpec();
            spec.url = WebView.SCHEME_TEL + PhoneNumberUtils.normalizeNumber(match.rawString());
            spec.start = match.start();
            spec.end = match.end();
            links.add(spec);
        }
    }

    private static final void gatherMapLinks(ArrayList<LinkSpec> links, Spannable s) {
        int start;
        String string = s.toString();
        int base = 0;
        while (true) {
            try {
                String address = WebView.findAddress(string);
                if (address != null && (start = string.indexOf(address)) >= 0) {
                    LinkSpec spec = new LinkSpec();
                    int length = address.length();
                    int end = start + length;
                    spec.start = base + start;
                    spec.end = base + end;
                    string = string.substring(end);
                    base += end;
                    try {
                        String encodedAddress = URLEncoder.encode(address, "UTF-8");
                        spec.url = WebView.SCHEME_GEO + encodedAddress;
                        links.add(spec);
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                return;
            } catch (UnsupportedOperationException e2) {
                return;
            }
        }
    }

    private static final void pruneOverlaps(ArrayList<LinkSpec> links) {
        Comparator<LinkSpec> c = new Comparator<LinkSpec>() { // from class: android.text.util.Linkify.4
            @Override // java.util.Comparator
            public final int compare(LinkSpec a, LinkSpec b) {
                if (a.start < b.start) {
                    return -1;
                }
                if (a.start <= b.start && a.end >= b.end) {
                    return a.end > b.end ? -1 : 0;
                }
                return 1;
            }
        };
        Collections.sort(links, c);
        int len = links.size();
        int i = 0;
        while (i < len - 1) {
            LinkSpec a = links.get(i);
            LinkSpec b = links.get(i + 1);
            int remove = -1;
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1;
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i;
                }
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                }
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ URLSpan lambda$static$0(String string) {
        return new URLSpan(string);
    }
}
