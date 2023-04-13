package com.android.server.location.countrydetector;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.os.Handler;
/* loaded from: classes.dex */
public abstract class CountryDetectorBase {
    public final Context mContext;
    public Country mDetectedCountry;
    public final Handler mHandler = new Handler();
    public CountryListener mListener;

    public abstract Country detectCountry();

    public abstract void stop();

    public CountryDetectorBase(Context context) {
        this.mContext = context.createAttributionContext("CountryDetector");
    }

    public void setCountryListener(CountryListener countryListener) {
        this.mListener = countryListener;
    }

    public void notifyListener(Country country) {
        CountryListener countryListener = this.mListener;
        if (countryListener != null) {
            countryListener.onCountryDetected(country);
        }
    }
}
