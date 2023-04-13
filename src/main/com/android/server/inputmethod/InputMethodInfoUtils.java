package com.android.server.inputmethod;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public final class InputMethodInfoUtils {
    public static final Locale[] SEARCH_ORDER_OF_FALLBACK_LOCALES = {Locale.ENGLISH, Locale.US, Locale.UK};
    public static final Locale ENGLISH_LOCALE = new Locale("en");

    /* loaded from: classes.dex */
    public static final class InputMethodListBuilder {
        public final LinkedHashSet<InputMethodInfo> mInputMethodSet;

        public InputMethodListBuilder() {
            this.mInputMethodSet = new LinkedHashSet<>();
        }

        public InputMethodListBuilder fillImes(ArrayList<InputMethodInfo> arrayList, Context context, boolean z, Locale locale, boolean z2, String str) {
            for (int i = 0; i < arrayList.size(); i++) {
                InputMethodInfo inputMethodInfo = arrayList.get(i);
                if (InputMethodInfoUtils.isSystemImeThatHasSubtypeOf(inputMethodInfo, context, z, locale, z2, str)) {
                    this.mInputMethodSet.add(inputMethodInfo);
                }
            }
            return this;
        }

        public InputMethodListBuilder fillAuxiliaryImes(ArrayList<InputMethodInfo> arrayList, Context context) {
            Iterator<InputMethodInfo> it = this.mInputMethodSet.iterator();
            while (it.hasNext()) {
                if (it.next().isAuxiliaryIme()) {
                    return this;
                }
            }
            boolean z = false;
            for (int i = 0; i < arrayList.size(); i++) {
                InputMethodInfo inputMethodInfo = arrayList.get(i);
                if (InputMethodInfoUtils.isSystemAuxilialyImeThatHasAutomaticSubtype(inputMethodInfo, context, true)) {
                    this.mInputMethodSet.add(inputMethodInfo);
                    z = true;
                }
            }
            if (z) {
                return this;
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                InputMethodInfo inputMethodInfo2 = arrayList.get(i2);
                if (InputMethodInfoUtils.isSystemAuxilialyImeThatHasAutomaticSubtype(inputMethodInfo2, context, false)) {
                    this.mInputMethodSet.add(inputMethodInfo2);
                }
            }
            return this;
        }

        public boolean isEmpty() {
            return this.mInputMethodSet.isEmpty();
        }

        public ArrayList<InputMethodInfo> build() {
            return new ArrayList<>(this.mInputMethodSet);
        }
    }

    public static InputMethodListBuilder getMinimumKeyboardSetWithSystemLocale(ArrayList<InputMethodInfo> arrayList, Context context, Locale locale, Locale locale2) {
        InputMethodListBuilder inputMethodListBuilder = new InputMethodListBuilder();
        inputMethodListBuilder.fillImes(arrayList, context, true, locale, true, "keyboard");
        if (inputMethodListBuilder.isEmpty()) {
            inputMethodListBuilder.fillImes(arrayList, context, true, locale, false, "keyboard");
            if (inputMethodListBuilder.isEmpty()) {
                inputMethodListBuilder.fillImes(arrayList, context, true, locale2, true, "keyboard");
                if (inputMethodListBuilder.isEmpty()) {
                    inputMethodListBuilder.fillImes(arrayList, context, true, locale2, false, "keyboard");
                    if (inputMethodListBuilder.isEmpty()) {
                        inputMethodListBuilder.fillImes(arrayList, context, false, locale2, true, "keyboard");
                        if (inputMethodListBuilder.isEmpty()) {
                            inputMethodListBuilder.fillImes(arrayList, context, false, locale2, false, "keyboard");
                            if (inputMethodListBuilder.isEmpty()) {
                                Slog.w("InputMethodInfoUtils", "No software keyboard is found. imis=" + Arrays.toString(arrayList.toArray()) + " systemLocale=" + locale + " fallbackLocale=" + locale2);
                                return inputMethodListBuilder;
                            }
                            return inputMethodListBuilder;
                        }
                        return inputMethodListBuilder;
                    }
                    return inputMethodListBuilder;
                }
                return inputMethodListBuilder;
            }
            return inputMethodListBuilder;
        }
        return inputMethodListBuilder;
    }

    public static ArrayList<InputMethodInfo> getDefaultEnabledImes(Context context, ArrayList<InputMethodInfo> arrayList, boolean z) {
        Locale fallbackLocaleForDefaultIme = getFallbackLocaleForDefaultIme(arrayList, context);
        Locale systemLocaleFromContext = LocaleUtils.getSystemLocaleFromContext(context);
        InputMethodListBuilder minimumKeyboardSetWithSystemLocale = getMinimumKeyboardSetWithSystemLocale(arrayList, context, systemLocaleFromContext, fallbackLocaleForDefaultIme);
        if (!z) {
            minimumKeyboardSetWithSystemLocale.fillImes(arrayList, context, true, systemLocaleFromContext, true, SubtypeUtils.SUBTYPE_MODE_ANY).fillAuxiliaryImes(arrayList, context);
        }
        return minimumKeyboardSetWithSystemLocale.build();
    }

    public static ArrayList<InputMethodInfo> getDefaultEnabledImes(Context context, ArrayList<InputMethodInfo> arrayList) {
        return getDefaultEnabledImes(context, arrayList, false);
    }

    public static InputMethodInfo chooseSystemVoiceIme(ArrayMap<String, InputMethodInfo> arrayMap, String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        InputMethodInfo inputMethodInfo = arrayMap.get(str2);
        if (inputMethodInfo != null && inputMethodInfo.isSystem() && inputMethodInfo.getPackageName().equals(str)) {
            return inputMethodInfo;
        }
        int size = arrayMap.size();
        InputMethodInfo inputMethodInfo2 = null;
        for (int i = 0; i < size; i++) {
            InputMethodInfo valueAt = arrayMap.valueAt(i);
            if (valueAt.isSystem() && TextUtils.equals(valueAt.getPackageName(), str)) {
                if (inputMethodInfo2 != null) {
                    Slog.e("InputMethodInfoUtils", "At most one InputMethodService can be published in systemSpeechRecognizer: " + str + ". Ignoring all of them.");
                    return null;
                }
                inputMethodInfo2 = valueAt;
            }
        }
        return inputMethodInfo2;
    }

    public static InputMethodInfo getMostApplicableDefaultIME(List<InputMethodInfo> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        int i = -1;
        while (size > 0) {
            size--;
            InputMethodInfo inputMethodInfo = list.get(size);
            if (!inputMethodInfo.isAuxiliaryIme()) {
                if (inputMethodInfo.isSystem() && SubtypeUtils.containsSubtypeOf(inputMethodInfo, ENGLISH_LOCALE, false, "keyboard")) {
                    return inputMethodInfo;
                }
                if (i < 0 && inputMethodInfo.isSystem()) {
                    i = size;
                }
            }
        }
        return list.get(Math.max(i, 0));
    }

    public static boolean isSystemAuxilialyImeThatHasAutomaticSubtype(InputMethodInfo inputMethodInfo, Context context, boolean z) {
        if (inputMethodInfo.isSystem()) {
            if ((!z || inputMethodInfo.isDefault(context)) && inputMethodInfo.isAuxiliaryIme()) {
                int subtypeCount = inputMethodInfo.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    if (inputMethodInfo.getSubtypeAt(i).overridesImplicitlyEnabledSubtype()) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static Locale getFallbackLocaleForDefaultIme(ArrayList<InputMethodInfo> arrayList, Context context) {
        Locale[] localeArr;
        Locale[] localeArr2;
        for (Locale locale : SEARCH_ORDER_OF_FALLBACK_LOCALES) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (isSystemImeThatHasSubtypeOf(arrayList.get(i), context, true, locale, true, "keyboard")) {
                    return locale;
                }
            }
        }
        for (Locale locale2 : SEARCH_ORDER_OF_FALLBACK_LOCALES) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (isSystemImeThatHasSubtypeOf(arrayList.get(i2), context, false, locale2, true, "keyboard")) {
                    return locale2;
                }
            }
        }
        Slog.w("InputMethodInfoUtils", "Found no fallback locale. imis=" + Arrays.toString(arrayList.toArray()));
        return null;
    }

    public static boolean isSystemImeThatHasSubtypeOf(InputMethodInfo inputMethodInfo, Context context, boolean z, Locale locale, boolean z2, String str) {
        if (inputMethodInfo.isSystem()) {
            if (!z || inputMethodInfo.isDefault(context)) {
                return SubtypeUtils.containsSubtypeOf(inputMethodInfo, locale, z2, str);
            }
            return false;
        }
        return false;
    }
}
