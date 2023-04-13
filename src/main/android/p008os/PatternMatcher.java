package android.p008os;

import android.p008os.Parcelable;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import java.util.Arrays;
/* renamed from: android.os.PatternMatcher */
/* loaded from: classes3.dex */
public class PatternMatcher implements Parcelable {
    private static final int MAX_PATTERN_STORAGE = 2048;
    private static final int NO_MATCH = -1;
    private static final int PARSED_MODIFIER_ONE_OR_MORE = -8;
    private static final int PARSED_MODIFIER_RANGE_START = -5;
    private static final int PARSED_MODIFIER_RANGE_STOP = -6;
    private static final int PARSED_MODIFIER_ZERO_OR_MORE = -7;
    private static final int PARSED_TOKEN_CHAR_ANY = -4;
    private static final int PARSED_TOKEN_CHAR_SET_INVERSE_START = -2;
    private static final int PARSED_TOKEN_CHAR_SET_START = -1;
    private static final int PARSED_TOKEN_CHAR_SET_STOP = -3;
    public static final int PATTERN_ADVANCED_GLOB = 3;
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    public static final int PATTERN_SUFFIX = 4;
    private static final String TAG = "PatternMatcher";
    private static final int TOKEN_TYPE_ANY = 1;
    private static final int TOKEN_TYPE_INVERSE_SET = 3;
    private static final int TOKEN_TYPE_LITERAL = 0;
    private static final int TOKEN_TYPE_SET = 2;
    private final int[] mParsedPattern;
    private final String mPattern;
    private final int mType;
    private static final int[] sParsedPatternScratch = new int[2048];
    public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator<PatternMatcher>() { // from class: android.os.PatternMatcher.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PatternMatcher createFromParcel(Parcel source) {
            return new PatternMatcher(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PatternMatcher[] newArray(int size) {
            return new PatternMatcher[size];
        }
    };

    public PatternMatcher(String pattern, int type) {
        this.mPattern = pattern;
        this.mType = type;
        if (type == 3) {
            this.mParsedPattern = parseAndVerifyAdvancedPattern(pattern);
        } else {
            this.mParsedPattern = null;
        }
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(str, this.mPattern, this.mParsedPattern, this.mType);
    }

    public String toString() {
        String type = "? ";
        switch (this.mType) {
            case 0:
                type = "LITERAL: ";
                break;
            case 1:
                type = "PREFIX: ";
                break;
            case 2:
                type = "GLOB: ";
                break;
            case 3:
                type = "ADVANCED: ";
                break;
            case 4:
                type = "SUFFIX: ";
                break;
        }
        return "PatternMatcher{" + type + this.mPattern + "}";
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, this.mPattern);
        proto.write(1159641169922L, this.mType);
        proto.end(token);
    }

    public boolean check() {
        try {
            if (this.mType == 3) {
                return Arrays.equals(this.mParsedPattern, parseAndVerifyAdvancedPattern(this.mPattern));
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.m104w(TAG, "Failed to verify advanced pattern: " + e.getMessage());
            return false;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPattern);
        dest.writeInt(this.mType);
        dest.writeIntArray(this.mParsedPattern);
    }

    public PatternMatcher(Parcel src) {
        this.mPattern = src.readString();
        this.mType = src.readInt();
        this.mParsedPattern = src.createIntArray();
    }

    static boolean matchPattern(String match, String pattern, int[] parsedPattern, int type) {
        if (match == null) {
            return false;
        }
        if (type == 0) {
            return pattern.equals(match);
        }
        if (type == 1) {
            return match.startsWith(pattern);
        }
        if (type == 2) {
            return matchGlobPattern(pattern, match);
        }
        if (type == 3) {
            return matchAdvancedPattern(parsedPattern, match);
        }
        if (type != 4) {
            return false;
        }
        return match.endsWith(pattern);
    }

    static boolean matchGlobPattern(String pattern, String match) {
        int NP = pattern.length();
        if (NP <= 0) {
            return match.length() <= 0;
        }
        int NM = match.length();
        int ip = 0;
        int im = 0;
        char nextChar = pattern.charAt(0);
        while (ip < NP && im < NM) {
            char c = nextChar;
            ip++;
            nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
            boolean escaped = c == '\\';
            if (escaped) {
                c = nextChar;
                ip++;
                nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
            }
            if (nextChar == '*') {
                if (!escaped && c == '.') {
                    if (ip >= NP - 1) {
                        return true;
                    }
                    int ip2 = ip + 1;
                    char nextChar2 = pattern.charAt(ip2);
                    if (nextChar2 == '\\') {
                        ip2++;
                        nextChar2 = ip2 < NP ? pattern.charAt(ip2) : (char) 0;
                    }
                    while (match.charAt(im) != nextChar2 && (im = im + 1) < NM) {
                    }
                    if (im == NM) {
                        return false;
                    }
                    ip = ip2 + 1;
                    nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
                    im++;
                } else {
                    while (match.charAt(im) == c && (im = im + 1) < NM) {
                    }
                    ip++;
                    nextChar = ip < NP ? pattern.charAt(ip) : (char) 0;
                }
            } else if (c != '.' && match.charAt(im) != c) {
                return false;
            } else {
                im++;
            }
        }
        if (ip < NP || im < NM) {
            return ip == NP + (-2) && pattern.charAt(ip) == '.' && pattern.charAt(ip + 1) == '*';
        }
        return true;
    }

    static synchronized int[] parseAndVerifyAdvancedPattern(String pattern) {
        int[] copyOf;
        int it;
        int it2;
        int it3;
        int rangeMin;
        int rangeMax;
        synchronized (PatternMatcher.class) {
            int ip = 0;
            int LP = pattern.length();
            int it4 = 0;
            boolean inSet = false;
            boolean inRange = false;
            boolean inCharClass = false;
            while (ip < LP) {
                if (it4 > 2045) {
                    throw new IllegalArgumentException("Pattern is too large!");
                }
                char c = pattern.charAt(ip);
                boolean addToParsedPattern = false;
                switch (c) {
                    case '*':
                        if (!inSet) {
                            if (it4 != 0) {
                                int[] iArr = sParsedPatternScratch;
                                if (!isParsedModifier(iArr[it4 - 1])) {
                                    it = it4 + 1;
                                    iArr[it4] = -7;
                                    it2 = ip;
                                    break;
                                }
                            }
                            throw new IllegalArgumentException("Modifier must follow a token.");
                        }
                        it = it4;
                        it2 = ip;
                        break;
                    case '+':
                        if (!inSet) {
                            if (it4 != 0) {
                                int[] iArr2 = sParsedPatternScratch;
                                if (!isParsedModifier(iArr2[it4 - 1])) {
                                    it = it4 + 1;
                                    iArr2[it4] = -8;
                                    it2 = ip;
                                    break;
                                }
                            }
                            throw new IllegalArgumentException("Modifier must follow a token.");
                        }
                        it = it4;
                        it2 = ip;
                        break;
                    case '.':
                        if (!inSet) {
                            it = it4 + 1;
                            sParsedPatternScratch[it4] = -4;
                            it2 = ip;
                            break;
                        }
                        it = it4;
                        it2 = ip;
                        break;
                    case '[':
                        if (inSet) {
                            addToParsedPattern = true;
                            it = it4;
                            it2 = ip;
                            break;
                        } else {
                            if (pattern.charAt(ip + 1) == '^') {
                                sParsedPatternScratch[it4] = -2;
                                ip++;
                                it4++;
                            } else {
                                sParsedPatternScratch[it4] = -1;
                                it4++;
                            }
                            ip++;
                            inSet = true;
                            continue;
                        }
                    case '\\':
                        if (ip + 1 >= LP) {
                            throw new IllegalArgumentException("Escape found at end of pattern!");
                        }
                        int ip2 = ip + 1;
                        c = pattern.charAt(ip2);
                        addToParsedPattern = true;
                        it = it4;
                        it2 = ip2;
                        break;
                    case ']':
                        if (!inSet) {
                            addToParsedPattern = true;
                            it = it4;
                            it2 = ip;
                            break;
                        } else {
                            int[] iArr3 = sParsedPatternScratch;
                            int parsedToken = iArr3[it4 - 1];
                            if (parsedToken == -1 || parsedToken == -2) {
                                throw new IllegalArgumentException("You must define characters in a set.");
                            }
                            iArr3[it4] = -3;
                            inCharClass = false;
                            it = it4 + 1;
                            inSet = false;
                            it2 = ip;
                            break;
                        }
                        break;
                    case '{':
                        if (!inSet) {
                            if (it4 != 0) {
                                int[] iArr4 = sParsedPatternScratch;
                                if (!isParsedModifier(iArr4[it4 - 1])) {
                                    it = it4 + 1;
                                    iArr4[it4] = -5;
                                    inRange = true;
                                    it2 = ip + 1;
                                    break;
                                }
                            }
                            throw new IllegalArgumentException("Modifier must follow a token.");
                        }
                        it = it4;
                        it2 = ip;
                        break;
                    case '}':
                        if (inRange) {
                            it = it4 + 1;
                            sParsedPatternScratch[it4] = -6;
                            inRange = false;
                            it2 = ip;
                            break;
                        }
                        it = it4;
                        it2 = ip;
                        break;
                    default:
                        addToParsedPattern = true;
                        it = it4;
                        it2 = ip;
                        break;
                }
                if (inSet) {
                    if (inCharClass) {
                        it3 = it + 1;
                        sParsedPatternScratch[it] = c;
                        inCharClass = false;
                    } else if (it2 + 2 < LP && pattern.charAt(it2 + 1) == '-' && pattern.charAt(it2 + 2) != ']') {
                        it3 = it + 1;
                        sParsedPatternScratch[it] = c;
                        it2++;
                        inCharClass = true;
                    } else {
                        int[] iArr5 = sParsedPatternScratch;
                        int it5 = it + 1;
                        iArr5[it] = c;
                        iArr5[it5] = c;
                        it3 = it5 + 1;
                    }
                } else if (inRange) {
                    int endOfSet = pattern.indexOf(125, it2);
                    if (endOfSet < 0) {
                        throw new IllegalArgumentException("Range not ended with '}'");
                    }
                    String rangeString = pattern.substring(it2, endOfSet);
                    int commaIndex = rangeString.indexOf(44);
                    if (commaIndex < 0) {
                        try {
                            rangeMin = Integer.parseInt(rangeString);
                            rangeMax = rangeMin;
                        } catch (NumberFormatException e) {
                            e = e;
                            throw new IllegalArgumentException("Range number format incorrect", e);
                        }
                    } else {
                        rangeMin = Integer.parseInt(rangeString.substring(0, commaIndex));
                        if (commaIndex == rangeString.length() - 1) {
                            rangeMax = Integer.MAX_VALUE;
                        } else {
                            int rangeMax2 = commaIndex + 1;
                            rangeMax = Integer.parseInt(rangeString.substring(rangeMax2));
                        }
                    }
                    if (rangeMin > rangeMax) {
                        throw new IllegalArgumentException("Range quantifier minimum is greater than maximum");
                    }
                    int[] iArr6 = sParsedPatternScratch;
                    int it6 = it + 1;
                    try {
                        iArr6[it] = rangeMin;
                        int it7 = it6 + 1;
                        iArr6[it6] = rangeMax;
                        ip = endOfSet;
                        it4 = it7;
                    } catch (NumberFormatException e2) {
                        e = e2;
                        throw new IllegalArgumentException("Range number format incorrect", e);
                    }
                } else if (!addToParsedPattern) {
                    it3 = it;
                } else {
                    it3 = it + 1;
                    sParsedPatternScratch[it] = c;
                }
                ip = it2 + 1;
                it4 = it3;
            }
            if (inSet) {
                throw new IllegalArgumentException("Set was not terminated!");
            }
            copyOf = Arrays.copyOf(sParsedPatternScratch, it4);
        }
        return copyOf;
    }

    private static boolean isParsedModifier(int parsedChar) {
        return parsedChar == -8 || parsedChar == -7 || parsedChar == -6 || parsedChar == -5;
    }

    static boolean matchAdvancedPattern(int[] parsedPattern, String match) {
        int ip;
        int charSetStart;
        int charSetEnd;
        int tokenType;
        int tokenType2;
        int ip2;
        int minRepetition;
        int maxRepetition;
        int ip3 = 0;
        int LP = parsedPattern.length;
        int LM = match.length();
        int charSetStart2 = 0;
        int charSetEnd2 = 0;
        int im = 0;
        while (ip3 < LP) {
            int patternChar = parsedPattern[ip3];
            switch (patternChar) {
                case -4:
                    ip = ip3 + 1;
                    charSetStart = charSetStart2;
                    charSetEnd = charSetEnd2;
                    tokenType = 1;
                    break;
                case -3:
                default:
                    int charSetStart3 = ip3;
                    ip = ip3 + 1;
                    charSetStart = charSetStart3;
                    charSetEnd = charSetEnd2;
                    tokenType = 0;
                    break;
                case -2:
                case -1:
                    if (patternChar == -1) {
                        tokenType2 = 2;
                    } else {
                        tokenType2 = 3;
                    }
                    int charSetStart4 = ip3 + 1;
                    do {
                        ip3++;
                        if (ip3 < LP) {
                        }
                        int charSetEnd3 = ip3 - 1;
                        ip = ip3 + 1;
                        charSetStart = charSetStart4;
                        charSetEnd = charSetEnd3;
                        tokenType = tokenType2;
                        break;
                    } while (parsedPattern[ip3] != -3);
                    int charSetEnd32 = ip3 - 1;
                    ip = ip3 + 1;
                    charSetStart = charSetStart4;
                    charSetEnd = charSetEnd32;
                    tokenType = tokenType2;
            }
            if (ip >= LP) {
                ip2 = ip;
                minRepetition = 1;
                maxRepetition = 1;
            } else {
                switch (parsedPattern[ip]) {
                    case -8:
                        ip2 = ip + 1;
                        minRepetition = 1;
                        maxRepetition = Integer.MAX_VALUE;
                        break;
                    case -7:
                        ip2 = ip + 1;
                        minRepetition = 0;
                        maxRepetition = Integer.MAX_VALUE;
                        break;
                    case -6:
                    default:
                        ip2 = ip;
                        minRepetition = 1;
                        maxRepetition = 1;
                        break;
                    case -5:
                        int ip4 = ip + 1;
                        int minRepetition2 = parsedPattern[ip4];
                        int ip5 = ip4 + 1;
                        int maxRepetition2 = parsedPattern[ip5];
                        ip2 = ip5 + 2;
                        maxRepetition = maxRepetition2;
                        minRepetition = minRepetition2;
                        break;
                }
            }
            if (minRepetition > maxRepetition) {
                return false;
            }
            int i = minRepetition;
            int i2 = maxRepetition;
            int maxRepetition3 = charSetStart;
            int minRepetition3 = charSetEnd;
            int matched = matchChars(match, im, LM, tokenType, i, i2, parsedPattern, maxRepetition3, minRepetition3);
            if (matched == -1) {
                return false;
            }
            im += matched;
            charSetStart2 = charSetStart;
            charSetEnd2 = charSetEnd;
            ip3 = ip2;
        }
        return ip3 >= LP && im >= LM;
    }

    private static int matchChars(String match, int im, int lm, int tokenType, int minRepetition, int maxRepetition, int[] parsedPattern, int tokenStart, int tokenEnd) {
        int matched = 0;
        while (matched < maxRepetition && matchChar(match, im + matched, lm, tokenType, parsedPattern, tokenStart, tokenEnd)) {
            matched++;
        }
        if (matched < minRepetition) {
            return -1;
        }
        return matched;
    }

    private static boolean matchChar(String match, int im, int lm, int tokenType, int[] parsedPattern, int tokenStart, int tokenEnd) {
        if (im >= lm) {
            return false;
        }
        switch (tokenType) {
            case 0:
                if (match.charAt(im) != parsedPattern[tokenStart]) {
                    return false;
                }
                return true;
            case 1:
                return true;
            case 2:
                for (int i = tokenStart; i < tokenEnd; i += 2) {
                    char matchChar = match.charAt(im);
                    if (matchChar >= parsedPattern[i] && matchChar <= parsedPattern[i + 1]) {
                        return true;
                    }
                }
                return false;
            case 3:
                for (int i2 = tokenStart; i2 < tokenEnd; i2 += 2) {
                    char matchChar2 = match.charAt(im);
                    if (matchChar2 >= parsedPattern[i2] && matchChar2 <= parsedPattern[i2 + 1]) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
