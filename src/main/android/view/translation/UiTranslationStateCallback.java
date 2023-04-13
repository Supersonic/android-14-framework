package android.view.translation;

import android.icu.util.ULocale;
/* loaded from: classes4.dex */
public interface UiTranslationStateCallback {
    void onFinished();

    void onPaused();

    @Deprecated
    default void onStarted(String sourceLocale, String targetLocale) {
    }

    default void onStarted(ULocale sourceLocale, ULocale targetLocale) {
        onStarted(sourceLocale.getLanguage(), targetLocale.getLanguage());
    }

    default void onStarted(ULocale sourceLocale, ULocale targetLocale, String packageName) {
        onStarted(sourceLocale, targetLocale);
    }

    default void onPaused(String packageName) {
        onPaused();
    }

    default void onResumed(ULocale sourceLocale, ULocale targetLocale) {
    }

    default void onResumed(ULocale sourceLocale, ULocale targetLocale, String packageName) {
        onResumed(sourceLocale, targetLocale);
    }

    default void onFinished(String packageName) {
        onFinished();
    }
}
