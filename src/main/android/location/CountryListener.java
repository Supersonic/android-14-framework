package android.location;

import java.util.function.Consumer;
/* loaded from: classes2.dex */
public interface CountryListener extends Consumer<Country> {
    void onCountryDetected(Country country);

    @Override // java.util.function.Consumer
    default void accept(Country country) {
        onCountryDetected(country);
    }
}
