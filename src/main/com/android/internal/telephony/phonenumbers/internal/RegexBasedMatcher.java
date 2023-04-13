package com.android.internal.telephony.phonenumbers.internal;

import com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneNumberDesc;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class RegexBasedMatcher implements MatcherApi {
    private final RegexCache regexCache = new RegexCache(100);

    public static MatcherApi create() {
        return new RegexBasedMatcher();
    }

    private RegexBasedMatcher() {
    }

    @Override // com.android.internal.telephony.phonenumbers.internal.MatcherApi
    public boolean matchNationalNumber(CharSequence charSequence, Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc, boolean z) {
        String nationalNumberPattern = phonemetadata$PhoneNumberDesc.getNationalNumberPattern();
        if (nationalNumberPattern.length() == 0) {
            return false;
        }
        return match(charSequence, this.regexCache.getPatternForRegex(nationalNumberPattern), z);
    }

    private static boolean match(CharSequence charSequence, Pattern pattern, boolean z) {
        Matcher matcher = pattern.matcher(charSequence);
        if (matcher.lookingAt()) {
            if (matcher.matches()) {
                return true;
            }
            return z;
        }
        return false;
    }
}
