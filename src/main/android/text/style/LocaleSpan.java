package android.text.style;

import android.graphics.Paint;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import com.android.internal.util.Preconditions;
import java.util.Locale;
/* loaded from: classes3.dex */
public class LocaleSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final LocaleList mLocales;

    public LocaleSpan(Locale locale) {
        this.mLocales = locale == null ? LocaleList.getEmptyLocaleList() : new LocaleList(locale);
    }

    public LocaleSpan(LocaleList locales) {
        Preconditions.checkNotNull(locales, "locales cannot be null");
        this.mLocales = locales;
    }

    public LocaleSpan(Parcel source) {
        this.mLocales = LocaleList.CREATOR.createFromParcel(source);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 23;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel dest, int flags) {
        this.mLocales.writeToParcel(dest, flags);
    }

    public Locale getLocale() {
        return this.mLocales.get(0);
    }

    public LocaleList getLocales() {
        return this.mLocales;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        apply(ds, this.mLocales);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint paint) {
        apply(paint, this.mLocales);
    }

    private static void apply(Paint paint, LocaleList locales) {
        paint.setTextLocales(locales);
    }

    public String toString() {
        return "LocaleSpan{locales=" + getLocales() + '}';
    }
}
