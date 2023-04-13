package com.android.server.p011pm.verify.domain;

import android.content.IntentFilter;
import android.content.pm.verify.domain.DomainVerificationUtils;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Patterns;
import com.android.server.SystemConfig;
import com.android.server.compat.PlatformCompat;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedIntentInfo;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationCollector */
/* loaded from: classes2.dex */
public class DomainVerificationCollector {
    public final Matcher mDomainMatcher = DOMAIN_NAME_WITH_WILDCARD.matcher("");
    public final PlatformCompat mPlatformCompat;
    public final SystemConfig mSystemConfig;
    public static final Pattern DOMAIN_NAME_WITH_WILDCARD = Pattern.compile("(\\*\\.)?" + Patterns.DOMAIN_NAME.pattern());
    public static final BiFunction<ArraySet<String>, String, Boolean> ARRAY_SET_COLLECTOR = new BiFunction() { // from class: com.android.server.pm.verify.domain.DomainVerificationCollector$$ExternalSyntheticLambda2
        @Override // java.util.function.BiFunction
        public final Object apply(Object obj, Object obj2) {
            Boolean add;
            add = ((ArraySet) obj).add((String) obj2);
            return add;
        }
    };

    public DomainVerificationCollector(PlatformCompat platformCompat, SystemConfig systemConfig) {
        this.mPlatformCompat = platformCompat;
        this.mSystemConfig = systemConfig;
    }

    public ArraySet<String> collectAllWebDomains(AndroidPackage androidPackage) {
        return collectDomains(androidPackage, false, true);
    }

    public ArraySet<String> collectValidAutoVerifyDomains(AndroidPackage androidPackage) {
        return collectDomains(androidPackage, true, true);
    }

    public ArraySet<String> collectInvalidAutoVerifyDomains(AndroidPackage androidPackage) {
        return collectDomains(androidPackage, true, false);
    }

    public boolean containsWebDomain(AndroidPackage androidPackage, final String str) {
        return collectDomains(androidPackage, false, true, null, new BiFunction() { // from class: com.android.server.pm.verify.domain.DomainVerificationCollector$$ExternalSyntheticLambda1
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                Boolean lambda$containsWebDomain$1;
                lambda$containsWebDomain$1 = DomainVerificationCollector.lambda$containsWebDomain$1(str, (Void) obj, (String) obj2);
                return lambda$containsWebDomain$1;
            }
        }) != null;
    }

    public static /* synthetic */ Boolean lambda$containsWebDomain$1(String str, Void r1, String str2) {
        if (Objects.equals(str, str2)) {
            return Boolean.TRUE;
        }
        return null;
    }

    public boolean containsAutoVerifyDomain(AndroidPackage androidPackage, final String str) {
        return collectDomains(androidPackage, true, true, null, new BiFunction() { // from class: com.android.server.pm.verify.domain.DomainVerificationCollector$$ExternalSyntheticLambda0
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                Boolean lambda$containsAutoVerifyDomain$2;
                lambda$containsAutoVerifyDomain$2 = DomainVerificationCollector.lambda$containsAutoVerifyDomain$2(str, (Void) obj, (String) obj2);
                return lambda$containsAutoVerifyDomain$2;
            }
        }) != null;
    }

    public static /* synthetic */ Boolean lambda$containsAutoVerifyDomain$2(String str, Void r1, String str2) {
        if (Objects.equals(str, str2)) {
            return Boolean.TRUE;
        }
        return null;
    }

    public final ArraySet<String> collectDomains(AndroidPackage androidPackage, boolean z, boolean z2) {
        ArraySet<String> arraySet = new ArraySet<>();
        collectDomains(androidPackage, z, z2, arraySet, ARRAY_SET_COLLECTOR);
        return arraySet;
    }

    public final <InitialValue, ReturnValue> ReturnValue collectDomains(AndroidPackage androidPackage, boolean z, boolean z2, InitialValue initialvalue, BiFunction<InitialValue, String, ReturnValue> biFunction) {
        if (DomainVerificationUtils.isChangeEnabled(this.mPlatformCompat, androidPackage, 175408749L)) {
            return (ReturnValue) collectDomainsInternal(androidPackage, z, z2, initialvalue, biFunction);
        }
        return (ReturnValue) collectDomainsLegacy(androidPackage, z, z2, initialvalue, biFunction);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v0 */
    /* JADX WARN: Type inference failed for: r11v1, types: [int] */
    /* JADX WARN: Type inference failed for: r14v0 */
    /* JADX WARN: Type inference failed for: r14v1, types: [int] */
    public final <InitialValue, ReturnValue> ReturnValue collectDomainsLegacy(AndroidPackage androidPackage, boolean z, boolean z2, InitialValue initialvalue, BiFunction<InitialValue, String, ReturnValue> biFunction) {
        InitialValue initialvalue2;
        BiFunction<InitialValue, String, ReturnValue> biFunction2;
        boolean z3;
        if (!z) {
            return (ReturnValue) collectDomainsInternal(androidPackage, false, true, initialvalue, biFunction);
        }
        List<ParsedActivity> activities = androidPackage.getActivities();
        int size = activities.size();
        boolean contains = this.mSystemConfig.getLinkedApps().contains(androidPackage.getPackageName());
        boolean z4 = false;
        if (!contains) {
            for (int i = 0; i < size && !contains; i++) {
                List<ParsedIntentInfo> intents = activities.get(i).getIntents();
                int size2 = intents.size();
                for (int i2 = 0; i2 < size2 && !contains; i2++) {
                    contains = intents.get(i2).getIntentFilter().needsVerification();
                }
            }
            if (!contains) {
                return null;
            }
        }
        int i3 = 0;
        int i4 = 0;
        boolean z5 = true;
        while (i3 < size && z5) {
            List<ParsedIntentInfo> intents2 = activities.get(i3).getIntents();
            int size3 = intents2.size();
            for (int i5 = z4; i5 < size3 && z5; i5++) {
                IntentFilter intentFilter = intents2.get(i5).getIntentFilter();
                if (intentFilter.handlesWebUris(z4)) {
                    int countDataAuthorities = intentFilter.countDataAuthorities();
                    for (int i6 = z4; i6 < countDataAuthorities; i6++) {
                        String host = intentFilter.getDataAuthority(i6).getHost();
                        if (isValidHost(host) == z2) {
                            i4 += byteSizeOf(host);
                            if (i4 < 1048576) {
                                initialvalue2 = initialvalue;
                                biFunction2 = biFunction;
                                z3 = true;
                            } else {
                                initialvalue2 = initialvalue;
                                biFunction2 = biFunction;
                                z3 = false;
                            }
                            ReturnValue apply = biFunction2.apply(initialvalue2, host);
                            if (apply != null) {
                                return apply;
                            }
                            z5 = z3;
                        }
                    }
                    continue;
                }
                z4 = false;
            }
            i3++;
            z4 = false;
        }
        return null;
    }

    public final <InitialValue, ReturnValue> ReturnValue collectDomainsInternal(AndroidPackage androidPackage, boolean z, boolean z2, InitialValue initialvalue, BiFunction<InitialValue, String, ReturnValue> biFunction) {
        InitialValue initialvalue2;
        BiFunction<InitialValue, String, ReturnValue> biFunction2;
        boolean z3;
        List<ParsedActivity> activities = androidPackage.getActivities();
        int size = activities.size();
        boolean z4 = true;
        int i = 0;
        for (int i2 = 0; i2 < size && z4; i2++) {
            List<ParsedIntentInfo> intents = activities.get(i2).getIntents();
            int size2 = intents.size();
            for (int i3 = 0; i3 < size2 && z4; i3++) {
                IntentFilter intentFilter = intents.get(i3).getIntentFilter();
                if ((!z || intentFilter.getAutoVerify()) && intentFilter.hasCategory("android.intent.category.DEFAULT") && intentFilter.handlesWebUris(z)) {
                    int countDataAuthorities = intentFilter.countDataAuthorities();
                    for (int i4 = 0; i4 < countDataAuthorities && z4; i4++) {
                        String host = intentFilter.getDataAuthority(i4).getHost();
                        if (isValidHost(host) == z2) {
                            i += byteSizeOf(host);
                            if (i < 1048576) {
                                initialvalue2 = initialvalue;
                                biFunction2 = biFunction;
                                z3 = true;
                            } else {
                                initialvalue2 = initialvalue;
                                biFunction2 = biFunction;
                                z3 = false;
                            }
                            ReturnValue apply = biFunction2.apply(initialvalue2, host);
                            if (apply != null) {
                                return apply;
                            }
                            z4 = z3;
                        }
                    }
                }
            }
        }
        return null;
    }

    public final int byteSizeOf(String str) {
        return DomainVerificationUtils.estimatedByteSizeOf(str);
    }

    public final boolean isValidHost(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        this.mDomainMatcher.reset(str);
        return this.mDomainMatcher.matches();
    }
}
