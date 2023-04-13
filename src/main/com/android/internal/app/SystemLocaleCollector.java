package com.android.internal.app;

import android.content.Context;
import android.p008os.LocaleList;
import com.android.internal.app.LocalePickerWithRegion;
import com.android.internal.app.LocaleStore;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes4.dex */
class SystemLocaleCollector implements LocalePickerWithRegion.LocaleCollectorBase {
    private final Context mContext;
    private LocaleList mExplicitLocales;

    SystemLocaleCollector(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SystemLocaleCollector(Context context, LocaleList explicitLocales) {
        this.mContext = context;
        this.mExplicitLocales = explicitLocales;
    }

    @Override // com.android.internal.app.LocalePickerWithRegion.LocaleCollectorBase
    public HashSet<String> getIgnoredLocaleList(boolean translatedOnly) {
        HashSet<String> ignoreList = new HashSet<>();
        if (!translatedOnly) {
            LocaleList userLocales = LocalePicker.getLocales();
            String[] langTags = userLocales.toLanguageTags().split(",");
            Collections.addAll(ignoreList, langTags);
        }
        return ignoreList;
    }

    @Override // com.android.internal.app.LocalePickerWithRegion.LocaleCollectorBase
    public Set<LocaleStore.LocaleInfo> getSupportedLocaleList(LocaleStore.LocaleInfo parent, boolean translatedOnly, boolean isForCountryMode) {
        Set<String> langTagsToIgnore = getIgnoredLocaleList(translatedOnly);
        if (isForCountryMode) {
            Set<LocaleStore.LocaleInfo> localeList = LocaleStore.getLevelLocales(this.mContext, langTagsToIgnore, parent, translatedOnly, this.mExplicitLocales);
            return localeList;
        }
        Set<LocaleStore.LocaleInfo> localeList2 = LocaleStore.getLevelLocales(this.mContext, langTagsToIgnore, null, translatedOnly, this.mExplicitLocales);
        return localeList2;
    }

    @Override // com.android.internal.app.LocalePickerWithRegion.LocaleCollectorBase
    public boolean hasSpecificPackageName() {
        return false;
    }
}
