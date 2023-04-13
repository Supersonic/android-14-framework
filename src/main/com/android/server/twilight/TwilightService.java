package com.android.server.twilight;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemService;
import com.ibm.icu.impl.CalendarAstronomer;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class TwilightService extends SystemService implements AlarmManager.OnAlarmListener, Handler.Callback, LocationListener {
    public AlarmManager mAlarmManager;
    public boolean mBootCompleted;
    public final Handler mHandler;
    public boolean mHasListeners;
    public Location mLastLocation;
    @GuardedBy({"mListeners"})
    public TwilightState mLastTwilightState;
    @GuardedBy({"mListeners"})
    public final ArrayMap<TwilightListener, Handler> mListeners;
    public LocationManager mLocationManager;
    public BroadcastReceiver mTimeChangedReceiver;

    @Override // android.location.LocationListener
    public void onProviderDisabled(String str) {
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String str) {
    }

    @Override // android.location.LocationListener
    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    public TwilightService(Context context) {
        super(context.createAttributionContext("TwilightService"));
        this.mListeners = new ArrayMap<>();
        this.mHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishLocalService(TwilightManager.class, new TwilightManager() { // from class: com.android.server.twilight.TwilightService.1
            @Override // com.android.server.twilight.TwilightManager
            public void registerListener(TwilightListener twilightListener, Handler handler) {
                synchronized (TwilightService.this.mListeners) {
                    boolean isEmpty = TwilightService.this.mListeners.isEmpty();
                    TwilightService.this.mListeners.put(twilightListener, handler);
                    if (isEmpty && !TwilightService.this.mListeners.isEmpty()) {
                        TwilightService.this.mHandler.sendEmptyMessage(1);
                    }
                }
            }

            @Override // com.android.server.twilight.TwilightManager
            public void unregisterListener(TwilightListener twilightListener) {
                synchronized (TwilightService.this.mListeners) {
                    boolean isEmpty = TwilightService.this.mListeners.isEmpty();
                    TwilightService.this.mListeners.remove(twilightListener);
                    if (!isEmpty && TwilightService.this.mListeners.isEmpty()) {
                        TwilightService.this.mHandler.sendEmptyMessage(2);
                    }
                }
            }

            @Override // com.android.server.twilight.TwilightManager
            public TwilightState getLastTwilightState() {
                TwilightState twilightState;
                synchronized (TwilightService.this.mListeners) {
                    twilightState = TwilightService.this.mLastTwilightState;
                }
                return twilightState;
            }
        });
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 1000) {
            Context context = getContext();
            this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
            this.mLocationManager = (LocationManager) context.getSystemService("location");
            this.mBootCompleted = true;
            if (this.mHasListeners) {
                startListening();
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            if (!this.mHasListeners) {
                this.mHasListeners = true;
                if (this.mBootCompleted) {
                    startListening();
                }
            }
            return true;
        } else if (i != 2) {
            return false;
        } else {
            if (this.mHasListeners) {
                this.mHasListeners = false;
                if (this.mBootCompleted) {
                    stopListening();
                }
            }
            return true;
        }
    }

    public final void startListening() {
        Slog.d("TwilightService", "startListening");
        this.mLocationManager.requestLocationUpdates((LocationRequest) null, this, Looper.getMainLooper());
        if (this.mLocationManager.getLastLocation() == null) {
            if (this.mLocationManager.isProviderEnabled("network")) {
                this.mLocationManager.getCurrentLocation("network", null, getContext().getMainExecutor(), new Consumer() { // from class: com.android.server.twilight.TwilightService$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        TwilightService.this.onLocationChanged((Location) obj);
                    }
                });
            } else if (this.mLocationManager.isProviderEnabled("gps")) {
                this.mLocationManager.getCurrentLocation("gps", null, getContext().getMainExecutor(), new Consumer() { // from class: com.android.server.twilight.TwilightService$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        TwilightService.this.onLocationChanged((Location) obj);
                    }
                });
            }
        }
        if (this.mTimeChangedReceiver == null) {
            this.mTimeChangedReceiver = new BroadcastReceiver() { // from class: com.android.server.twilight.TwilightService.2
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    Slog.d("TwilightService", "onReceive: " + intent);
                    TwilightService.this.updateTwilightState();
                }
            };
            IntentFilter intentFilter = new IntentFilter("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            getContext().registerReceiver(this.mTimeChangedReceiver, intentFilter);
        }
        updateTwilightState();
    }

    public final void stopListening() {
        Slog.d("TwilightService", "stopListening");
        if (this.mTimeChangedReceiver != null) {
            getContext().unregisterReceiver(this.mTimeChangedReceiver);
            this.mTimeChangedReceiver = null;
        }
        if (this.mLastTwilightState != null) {
            this.mAlarmManager.cancel(this);
        }
        this.mLocationManager.removeUpdates(this);
        this.mLastLocation = null;
    }

    public final void updateTwilightState() {
        long currentTimeMillis = System.currentTimeMillis();
        Location location = this.mLastLocation;
        if (location == null) {
            location = this.mLocationManager.getLastLocation();
        }
        final TwilightState calculateTwilightState = calculateTwilightState(location, currentTimeMillis);
        synchronized (this.mListeners) {
            if (!Objects.equals(this.mLastTwilightState, calculateTwilightState)) {
                this.mLastTwilightState = calculateTwilightState;
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    final TwilightListener keyAt = this.mListeners.keyAt(size);
                    this.mListeners.valueAt(size).post(new Runnable() { // from class: com.android.server.twilight.TwilightService$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            TwilightListener.this.onTwilightStateChanged(calculateTwilightState);
                        }
                    });
                }
            }
        }
        if (calculateTwilightState != null) {
            this.mAlarmManager.setExact(1, calculateTwilightState.isNight() ? calculateTwilightState.sunriseTimeMillis() : calculateTwilightState.sunsetTimeMillis(), "TwilightService", this, this.mHandler);
        }
    }

    @Override // android.app.AlarmManager.OnAlarmListener
    public void onAlarm() {
        Slog.d("TwilightService", "onAlarm");
        updateTwilightState();
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location location) {
        if (location != null) {
            Slog.d("TwilightService", "onLocationChanged: provider=" + location.getProvider() + " accuracy=" + location.getAccuracy() + " time=" + location.getTime());
            this.mLastLocation = location;
            updateTwilightState();
        }
    }

    public static TwilightState calculateTwilightState(Location location, long j) {
        if (location == null) {
            return null;
        }
        CalendarAstronomer calendarAstronomer = new CalendarAstronomer(location.getLongitude(), location.getLatitude());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        calendar.set(11, 12);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendarAstronomer.setTime(calendar.getTimeInMillis());
        long sunRiseSet = calendarAstronomer.getSunRiseSet(true);
        long sunRiseSet2 = calendarAstronomer.getSunRiseSet(false);
        if (sunRiseSet2 < j) {
            calendar.add(5, 1);
            calendarAstronomer.setTime(calendar.getTimeInMillis());
            sunRiseSet = calendarAstronomer.getSunRiseSet(true);
        } else if (sunRiseSet > j) {
            calendar.add(5, -1);
            calendarAstronomer.setTime(calendar.getTimeInMillis());
            sunRiseSet2 = calendarAstronomer.getSunRiseSet(false);
        }
        return new TwilightState(sunRiseSet, sunRiseSet2);
    }
}
