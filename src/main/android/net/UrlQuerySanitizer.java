package android.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class UrlQuerySanitizer {
    private boolean mAllowUnregisteredParamaters;
    private boolean mPreferFirstRepeatedParameter;
    private static final ValueSanitizer sAllIllegal = new IllegalCharacterValueSanitizer(0);
    private static final ValueSanitizer sAllButNulLegal = new IllegalCharacterValueSanitizer(1535);
    private static final ValueSanitizer sAllButWhitespaceLegal = new IllegalCharacterValueSanitizer(1532);
    private static final ValueSanitizer sURLLegal = new IllegalCharacterValueSanitizer(404);
    private static final ValueSanitizer sUrlAndSpaceLegal = new IllegalCharacterValueSanitizer(405);
    private static final ValueSanitizer sAmpLegal = new IllegalCharacterValueSanitizer(128);
    private static final ValueSanitizer sAmpAndSpaceLegal = new IllegalCharacterValueSanitizer(129);
    private static final ValueSanitizer sSpaceLegal = new IllegalCharacterValueSanitizer(1);
    private static final ValueSanitizer sAllButNulAndAngleBracketsLegal = new IllegalCharacterValueSanitizer(1439);
    private static final Pattern plusOrPercent = Pattern.compile("[+%]");
    private final HashMap<String, ValueSanitizer> mSanitizers = new HashMap<>();
    private final HashMap<String, String> mEntries = new HashMap<>();
    private final ArrayList<ParameterValuePair> mEntriesList = new ArrayList<>();
    private ValueSanitizer mUnregisteredParameterValueSanitizer = getAllIllegal();

    /* loaded from: classes2.dex */
    public interface ValueSanitizer {
        String sanitize(String str);
    }

    /* loaded from: classes2.dex */
    public class ParameterValuePair {
        public String mParameter;
        public String mValue;

        public ParameterValuePair(String parameter, String value) {
            this.mParameter = parameter;
            this.mValue = value;
        }
    }

    /* loaded from: classes2.dex */
    public static class IllegalCharacterValueSanitizer implements ValueSanitizer {
        public static final int ALL_BUT_NUL_AND_ANGLE_BRACKETS_LEGAL = 1439;
        public static final int ALL_BUT_NUL_LEGAL = 1535;
        public static final int ALL_BUT_WHITESPACE_LEGAL = 1532;
        public static final int ALL_ILLEGAL = 0;
        public static final int ALL_OK = 2047;
        public static final int ALL_WHITESPACE_OK = 3;
        public static final int AMP_AND_SPACE_LEGAL = 129;
        public static final int AMP_LEGAL = 128;
        public static final int AMP_OK = 128;
        public static final int DQUOTE_OK = 8;
        public static final int GT_OK = 64;
        public static final int LT_OK = 32;
        public static final int NON_7_BIT_ASCII_OK = 4;
        public static final int NUL_OK = 512;
        public static final int OTHER_WHITESPACE_OK = 2;
        public static final int PCT_OK = 256;
        public static final int SCRIPT_URL_OK = 1024;
        public static final int SPACE_LEGAL = 1;
        public static final int SPACE_OK = 1;
        public static final int SQUOTE_OK = 16;
        public static final int URL_AND_SPACE_LEGAL = 405;
        public static final int URL_LEGAL = 404;
        private int mFlags;
        private static final String JAVASCRIPT_PREFIX = "javascript:";
        private static final String VBSCRIPT_PREFIX = "vbscript:";
        private static final int MIN_SCRIPT_PREFIX_LENGTH = Math.min(JAVASCRIPT_PREFIX.length(), VBSCRIPT_PREFIX.length());

        public IllegalCharacterValueSanitizer(int flags) {
            this.mFlags = flags;
        }

        @Override // android.net.UrlQuerySanitizer.ValueSanitizer
        public String sanitize(String value) {
            if (value == null) {
                return null;
            }
            int length = value.length();
            if ((this.mFlags & 1024) == 0 && length >= MIN_SCRIPT_PREFIX_LENGTH) {
                String asLower = value.toLowerCase(Locale.ROOT);
                if (asLower.startsWith(JAVASCRIPT_PREFIX) || asLower.startsWith(VBSCRIPT_PREFIX)) {
                    return "";
                }
            }
            if ((this.mFlags & 3) == 0) {
                value = trimWhitespace(value);
                length = value.length();
            }
            StringBuilder stringBuilder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                char c = value.charAt(i);
                if (!characterIsLegal(c)) {
                    if ((this.mFlags & 1) != 0) {
                        c = ' ';
                    } else {
                        c = '_';
                    }
                }
                stringBuilder.append(c);
            }
            return stringBuilder.toString();
        }

        private String trimWhitespace(String value) {
            int start = 0;
            int last = value.length() - 1;
            int end = last;
            while (start <= end && isWhitespace(value.charAt(start))) {
                start++;
            }
            while (end >= start && isWhitespace(value.charAt(end))) {
                end--;
            }
            if (start == 0 && end == last) {
                return value;
            }
            return value.substring(start, end + 1);
        }

        private boolean isWhitespace(char c) {
            switch (c) {
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case ' ':
                    return true;
                default:
                    return false;
            }
        }

        private boolean characterIsLegal(char c) {
            switch (c) {
                case 0:
                    return (this.mFlags & 512) != 0;
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                    return (this.mFlags & 2) != 0;
                case ' ':
                    return (this.mFlags & 1) != 0;
                case '\"':
                    return (this.mFlags & 8) != 0;
                case '%':
                    return (this.mFlags & 256) != 0;
                case '&':
                    return (this.mFlags & 128) != 0;
                case '\'':
                    return (this.mFlags & 16) != 0;
                case '<':
                    return (32 & this.mFlags) != 0;
                case '>':
                    return (this.mFlags & 64) != 0;
                default:
                    return (c >= ' ' && c < 127) || (c >= 128 && (this.mFlags & 4) != 0);
            }
        }
    }

    public ValueSanitizer getUnregisteredParameterValueSanitizer() {
        return this.mUnregisteredParameterValueSanitizer;
    }

    public void setUnregisteredParameterValueSanitizer(ValueSanitizer sanitizer) {
        this.mUnregisteredParameterValueSanitizer = sanitizer;
    }

    public static final ValueSanitizer getAllIllegal() {
        return sAllIllegal;
    }

    public static final ValueSanitizer getAllButNulLegal() {
        return sAllButNulLegal;
    }

    public static final ValueSanitizer getAllButWhitespaceLegal() {
        return sAllButWhitespaceLegal;
    }

    public static final ValueSanitizer getUrlLegal() {
        return sURLLegal;
    }

    public static final ValueSanitizer getUrlAndSpaceLegal() {
        return sUrlAndSpaceLegal;
    }

    public static final ValueSanitizer getAmpLegal() {
        return sAmpLegal;
    }

    public static final ValueSanitizer getAmpAndSpaceLegal() {
        return sAmpAndSpaceLegal;
    }

    public static final ValueSanitizer getSpaceLegal() {
        return sSpaceLegal;
    }

    public static final ValueSanitizer getAllButNulAndAngleBracketsLegal() {
        return sAllButNulAndAngleBracketsLegal;
    }

    public UrlQuerySanitizer() {
    }

    public UrlQuerySanitizer(String url) {
        setAllowUnregisteredParamaters(true);
        parseUrl(url);
    }

    public void parseUrl(String url) {
        String query;
        int queryIndex = url.indexOf(63);
        if (queryIndex >= 0) {
            query = url.substring(queryIndex + 1);
        } else {
            query = "";
        }
        parseQuery(query);
    }

    public void parseQuery(String query) {
        clear();
        StringTokenizer tokenizer = new StringTokenizer(query, "&");
        while (tokenizer.hasMoreElements()) {
            String attributeValuePair = tokenizer.nextToken();
            if (attributeValuePair.length() > 0) {
                int assignmentIndex = attributeValuePair.indexOf(61);
                if (assignmentIndex < 0) {
                    parseEntry(attributeValuePair, "");
                } else {
                    parseEntry(attributeValuePair.substring(0, assignmentIndex), attributeValuePair.substring(assignmentIndex + 1));
                }
            }
        }
    }

    public Set<String> getParameterSet() {
        return this.mEntries.keySet();
    }

    public List<ParameterValuePair> getParameterList() {
        return this.mEntriesList;
    }

    public boolean hasParameter(String parameter) {
        return this.mEntries.containsKey(parameter);
    }

    public String getValue(String parameter) {
        return this.mEntries.get(parameter);
    }

    public void registerParameter(String parameter, ValueSanitizer valueSanitizer) {
        if (valueSanitizer == null) {
            this.mSanitizers.remove(parameter);
        }
        this.mSanitizers.put(parameter, valueSanitizer);
    }

    public void registerParameters(String[] parameters, ValueSanitizer valueSanitizer) {
        for (String str : parameters) {
            this.mSanitizers.put(str, valueSanitizer);
        }
    }

    public void setAllowUnregisteredParamaters(boolean allowUnregisteredParamaters) {
        this.mAllowUnregisteredParamaters = allowUnregisteredParamaters;
    }

    public boolean getAllowUnregisteredParamaters() {
        return this.mAllowUnregisteredParamaters;
    }

    public void setPreferFirstRepeatedParameter(boolean preferFirstRepeatedParameter) {
        this.mPreferFirstRepeatedParameter = preferFirstRepeatedParameter;
    }

    public boolean getPreferFirstRepeatedParameter() {
        return this.mPreferFirstRepeatedParameter;
    }

    protected void parseEntry(String parameter, String value) {
        String unescapedParameter = unescape(parameter);
        ValueSanitizer valueSanitizer = getEffectiveValueSanitizer(unescapedParameter);
        if (valueSanitizer == null) {
            return;
        }
        String unescapedValue = unescape(value);
        String sanitizedValue = valueSanitizer.sanitize(unescapedValue);
        addSanitizedEntry(unescapedParameter, sanitizedValue);
    }

    protected void addSanitizedEntry(String parameter, String value) {
        this.mEntriesList.add(new ParameterValuePair(parameter, value));
        if (this.mPreferFirstRepeatedParameter && this.mEntries.containsKey(parameter)) {
            return;
        }
        this.mEntries.put(parameter, value);
    }

    public ValueSanitizer getValueSanitizer(String parameter) {
        return this.mSanitizers.get(parameter);
    }

    public ValueSanitizer getEffectiveValueSanitizer(String parameter) {
        ValueSanitizer sanitizer = getValueSanitizer(parameter);
        if (sanitizer == null && this.mAllowUnregisteredParamaters) {
            return getUnregisteredParameterValueSanitizer();
        }
        return sanitizer;
    }

    public String unescape(String string) {
        Matcher matcher = plusOrPercent.matcher(string);
        if (matcher.find()) {
            int firstEscape = matcher.start();
            int length = string.length();
            StringBuilder stringBuilder = new StringBuilder(length);
            stringBuilder.append(string.substring(0, firstEscape));
            int i = firstEscape;
            while (i < length) {
                char c = string.charAt(i);
                if (c == '+') {
                    c = ' ';
                } else if (c == '%' && i + 2 < length) {
                    char c1 = string.charAt(i + 1);
                    char c2 = string.charAt(i + 2);
                    if (isHexDigit(c1) && isHexDigit(c2)) {
                        c = (char) ((decodeHexDigit(c1) * 16) + decodeHexDigit(c2));
                        i += 2;
                    }
                }
                stringBuilder.append(c);
                i++;
            }
            return stringBuilder.toString();
        }
        return string;
    }

    protected boolean isHexDigit(char c) {
        return decodeHexDigit(c) >= 0;
    }

    protected int decodeHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A') + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        return -1;
    }

    protected void clear() {
        this.mEntries.clear();
        this.mEntriesList.clear();
    }
}
