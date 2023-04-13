package com.android.server.display;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayedContentSample;
import android.hardware.display.DisplayedContentSamplingAttributes;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.RingBuffer;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class BrightnessTracker {
    public AmbientBrightnessStatsTracker mAmbientBrightnessStatsTracker;
    public final Handler mBgHandler;
    public BroadcastReceiver mBroadcastReceiver;
    public boolean mColorSamplingEnabled;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    public DisplayListener mDisplayListener;
    @GuardedBy({"mEventsLock"})
    public boolean mEventsDirty;
    public float mFrameRate;
    public final Injector mInjector;
    public Sensor mLightSensor;
    public int mNoFramesToSample;
    public SensorListener mSensorListener;
    public boolean mSensorRegistered;
    public SettingsObserver mSettingsObserver;
    @GuardedBy({"mDataCollectionLock"})
    public boolean mStarted;
    public final UserManager mUserManager;
    public volatile boolean mWriteBrightnessTrackerStateScheduled;
    public static final long MAX_EVENT_AGE = TimeUnit.DAYS.toMillis(30);
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public static final long COLOR_SAMPLE_DURATION = TimeUnit.SECONDS.toSeconds(10);
    public final Object mEventsLock = new Object();
    @GuardedBy({"mEventsLock"})
    public RingBuffer<BrightnessChangeEvent> mEvents = new RingBuffer<>(BrightnessChangeEvent.class, 100);
    public boolean mShouldCollectColorSample = false;
    public int mCurrentUserId = -10000;
    public final Object mDataCollectionLock = new Object();
    @GuardedBy({"mDataCollectionLock"})
    public float mLastBatteryLevel = Float.NaN;
    @GuardedBy({"mDataCollectionLock"})
    public float mLastBrightness = -1.0f;

    public BrightnessTracker(Context context, Injector injector) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        if (injector != null) {
            this.mInjector = injector;
        } else {
            this.mInjector = new Injector();
        }
        this.mBgHandler = new TrackerHandler(this.mInjector.getBackgroundHandler().getLooper());
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    public void start(float f) {
        this.mCurrentUserId = ActivityManager.getCurrentUser();
        this.mBgHandler.obtainMessage(0, Float.valueOf(f)).sendToTarget();
    }

    public void setShouldCollectColorSample(boolean z) {
        this.mBgHandler.obtainMessage(4, Boolean.valueOf(z)).sendToTarget();
    }

    public final void backgroundStart(float f) {
        synchronized (this.mDataCollectionLock) {
            if (this.mStarted) {
                return;
            }
            readEvents();
            readAmbientBrightnessStats();
            this.mSensorListener = new SensorListener();
            SettingsObserver settingsObserver = new SettingsObserver(this.mBgHandler);
            this.mSettingsObserver = settingsObserver;
            this.mInjector.registerBrightnessModeObserver(this.mContentResolver, settingsObserver);
            startSensorListener();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            Receiver receiver = new Receiver();
            this.mBroadcastReceiver = receiver;
            this.mInjector.registerReceiver(this.mContext, receiver, intentFilter);
            this.mInjector.scheduleIdleJob(this.mContext);
            synchronized (this.mDataCollectionLock) {
                this.mLastBrightness = f;
                this.mStarted = true;
            }
            enableColorSampling();
        }
    }

    public void stop() {
        synchronized (this.mDataCollectionLock) {
            if (this.mStarted) {
                this.mBgHandler.removeMessages(0);
                stopSensorListener();
                this.mInjector.unregisterSensorListener(this.mContext, this.mSensorListener);
                this.mInjector.unregisterBrightnessModeObserver(this.mContext, this.mSettingsObserver);
                this.mInjector.unregisterReceiver(this.mContext, this.mBroadcastReceiver);
                this.mInjector.cancelIdleJob(this.mContext);
                synchronized (this.mDataCollectionLock) {
                    this.mStarted = false;
                }
                disableColorSampling();
            }
        }
    }

    public void onSwitchUser(int i) {
        this.mCurrentUserId = i;
    }

    public ParceledListSlice<BrightnessChangeEvent> getEvents(int i, boolean z) {
        BrightnessChangeEvent[] brightnessChangeEventArr;
        synchronized (this.mEventsLock) {
            brightnessChangeEventArr = (BrightnessChangeEvent[]) this.mEvents.toArray();
        }
        int[] profileIds = this.mInjector.getProfileIds(this.mUserManager, i);
        HashMap hashMap = new HashMap();
        int i2 = 0;
        while (true) {
            boolean z2 = true;
            if (i2 >= profileIds.length) {
                break;
            }
            int i3 = profileIds[i2];
            if (z && i3 == i) {
                z2 = false;
            }
            hashMap.put(Integer.valueOf(i3), Boolean.valueOf(z2));
            i2++;
        }
        ArrayList arrayList = new ArrayList(brightnessChangeEventArr.length);
        for (int i4 = 0; i4 < brightnessChangeEventArr.length; i4++) {
            Boolean bool = (Boolean) hashMap.get(Integer.valueOf(brightnessChangeEventArr[i4].userId));
            if (bool != null) {
                if (!bool.booleanValue()) {
                    arrayList.add(brightnessChangeEventArr[i4]);
                } else {
                    arrayList.add(new BrightnessChangeEvent(brightnessChangeEventArr[i4], true));
                }
            }
        }
        return new ParceledListSlice<>(arrayList);
    }

    public void persistBrightnessTrackerState() {
        scheduleWriteBrightnessTrackerState();
    }

    public void notifyBrightnessChanged(float f, boolean z, float f2, boolean z2, boolean z3, String str, float[] fArr, long[] jArr) {
        this.mBgHandler.obtainMessage(1, z ? 1 : 0, 0, new BrightnessChangeValues(f, f2, z2, z3, this.mInjector.currentTimeMillis(), str, fArr, jArr)).sendToTarget();
    }

    public void setLightSensor(Sensor sensor) {
        this.mBgHandler.obtainMessage(5, 0, 0, sensor).sendToTarget();
    }

    public final void handleBrightnessChanged(float f, boolean z, float f2, boolean z2, boolean z3, long j, String str, float[] fArr, long[] jArr) {
        DisplayedContentSample sampleColor;
        synchronized (this.mDataCollectionLock) {
            if (this.mStarted) {
                float f3 = this.mLastBrightness;
                this.mLastBrightness = f;
                if (z) {
                    BrightnessChangeEvent.Builder builder = new BrightnessChangeEvent.Builder();
                    builder.setBrightness(f);
                    builder.setTimeStamp(j);
                    builder.setPowerBrightnessFactor(f2);
                    builder.setUserBrightnessPoint(z2);
                    builder.setIsDefaultBrightnessConfig(z3);
                    builder.setUniqueDisplayId(str);
                    if (fArr.length == 0) {
                        return;
                    }
                    long[] jArr2 = new long[jArr.length];
                    long currentTimeMillis = this.mInjector.currentTimeMillis();
                    long elapsedRealtimeNanos = this.mInjector.elapsedRealtimeNanos();
                    for (int i = 0; i < jArr.length; i++) {
                        jArr2[i] = currentTimeMillis - (TimeUnit.NANOSECONDS.toMillis(elapsedRealtimeNanos) - jArr[i]);
                    }
                    builder.setLuxValues(fArr);
                    builder.setLuxTimestamps(jArr2);
                    builder.setBatteryLevel(this.mLastBatteryLevel);
                    builder.setLastBrightness(f3);
                    try {
                        ActivityTaskManager.RootTaskInfo focusedStack = this.mInjector.getFocusedStack();
                        if (focusedStack == null || focusedStack.topActivity == null) {
                            return;
                        }
                        builder.setUserId(focusedStack.userId);
                        builder.setPackageName(focusedStack.topActivity.getPackageName());
                        builder.setNightMode(this.mInjector.isNightDisplayActivated(this.mContext));
                        builder.setColorTemperature(this.mInjector.getNightDisplayColorTemperature(this.mContext));
                        builder.setReduceBrightColors(this.mInjector.isReduceBrightColorsActivated(this.mContext));
                        builder.setReduceBrightColorsStrength(this.mInjector.getReduceBrightColorsStrength(this.mContext));
                        builder.setReduceBrightColorsOffset(this.mInjector.getReduceBrightColorsOffsetFactor(this.mContext) * f);
                        if (this.mColorSamplingEnabled && (sampleColor = this.mInjector.sampleColor(this.mNoFramesToSample)) != null && sampleColor.getSampleComponent(DisplayedContentSample.ColorComponent.CHANNEL2) != null) {
                            builder.setColorValues(sampleColor.getSampleComponent(DisplayedContentSample.ColorComponent.CHANNEL2), Math.round((((float) sampleColor.getNumFrames()) / this.mFrameRate) * 1000.0f));
                        }
                        BrightnessChangeEvent build = builder.build();
                        synchronized (this.mEventsLock) {
                            this.mEventsDirty = true;
                            this.mEvents.append(build);
                        }
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public final void handleSensorChanged(Sensor sensor) {
        if (this.mLightSensor != sensor) {
            this.mLightSensor = sensor;
            stopSensorListener();
            startSensorListener();
        }
    }

    public final void startSensorListener() {
        if (this.mSensorRegistered || this.mLightSensor == null || this.mAmbientBrightnessStatsTracker == null || !this.mInjector.isInteractive(this.mContext) || !this.mInjector.isBrightnessModeAutomatic(this.mContentResolver)) {
            return;
        }
        this.mAmbientBrightnessStatsTracker.start();
        this.mSensorRegistered = true;
        Injector injector = this.mInjector;
        injector.registerSensorListener(this.mContext, this.mSensorListener, this.mLightSensor, injector.getBackgroundHandler());
    }

    public final void stopSensorListener() {
        if (this.mSensorRegistered) {
            this.mAmbientBrightnessStatsTracker.stop();
            this.mInjector.unregisterSensorListener(this.mContext, this.mSensorListener);
            this.mSensorRegistered = false;
        }
    }

    public final void scheduleWriteBrightnessTrackerState() {
        if (this.mWriteBrightnessTrackerStateScheduled) {
            return;
        }
        this.mBgHandler.post(new Runnable() { // from class: com.android.server.display.BrightnessTracker$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BrightnessTracker.this.lambda$scheduleWriteBrightnessTrackerState$0();
            }
        });
        this.mWriteBrightnessTrackerStateScheduled = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleWriteBrightnessTrackerState$0() {
        this.mWriteBrightnessTrackerStateScheduled = false;
        writeEvents();
        writeAmbientBrightnessStats();
    }

    public final void writeEvents() {
        FileOutputStream fileOutputStream;
        synchronized (this.mEventsLock) {
            if (this.mEventsDirty) {
                AtomicFile file = this.mInjector.getFile("brightness_events.xml");
                if (file == null) {
                    return;
                }
                if (this.mEvents.isEmpty()) {
                    if (file.exists()) {
                        file.delete();
                    }
                    this.mEventsDirty = false;
                } else {
                    try {
                        fileOutputStream = file.startWrite();
                    } catch (IOException e) {
                        e = e;
                        fileOutputStream = null;
                    }
                    try {
                        writeEventsLocked(fileOutputStream);
                        file.finishWrite(fileOutputStream);
                        this.mEventsDirty = false;
                    } catch (IOException e2) {
                        e = e2;
                        file.failWrite(fileOutputStream);
                        Slog.e("BrightnessTracker", "Failed to write change mEvents.", e);
                    }
                }
            }
        }
    }

    public final void writeAmbientBrightnessStats() {
        FileOutputStream fileOutputStream;
        AtomicFile file = this.mInjector.getFile("ambient_brightness_stats.xml");
        if (file == null) {
            return;
        }
        try {
            fileOutputStream = file.startWrite();
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            this.mAmbientBrightnessStatsTracker.writeStats(fileOutputStream);
            file.finishWrite(fileOutputStream);
        } catch (IOException e2) {
            e = e2;
            file.failWrite(fileOutputStream);
            Slog.e("BrightnessTracker", "Failed to write ambient brightness stats.", e);
        }
    }

    public final AtomicFile getFileWithLegacyFallback(String str) {
        AtomicFile legacyFile;
        AtomicFile file = this.mInjector.getFile(str);
        if (file == null || file.exists() || (legacyFile = this.mInjector.getLegacyFile(str)) == null || !legacyFile.exists()) {
            return file;
        }
        Slog.i("BrightnessTracker", "Reading " + str + " from old location");
        return legacyFile;
    }

    public final void readEvents() {
        synchronized (this.mEventsLock) {
            this.mEventsDirty = true;
            this.mEvents.clear();
            AtomicFile fileWithLegacyFallback = getFileWithLegacyFallback("brightness_events.xml");
            if (fileWithLegacyFallback != null && fileWithLegacyFallback.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = fileWithLegacyFallback.openRead();
                    readEventsLocked(fileInputStream);
                } catch (IOException e) {
                    fileWithLegacyFallback.delete();
                    Slog.e("BrightnessTracker", "Failed to read change mEvents.", e);
                }
                IoUtils.closeQuietly(fileInputStream);
            }
        }
    }

    public final void readAmbientBrightnessStats() {
        FileInputStream fileInputStream = null;
        this.mAmbientBrightnessStatsTracker = new AmbientBrightnessStatsTracker(this.mUserManager, null);
        AtomicFile fileWithLegacyFallback = getFileWithLegacyFallback("ambient_brightness_stats.xml");
        if (fileWithLegacyFallback == null || !fileWithLegacyFallback.exists()) {
            return;
        }
        try {
            try {
                fileInputStream = fileWithLegacyFallback.openRead();
                this.mAmbientBrightnessStatsTracker.readStats(fileInputStream);
            } catch (IOException e) {
                fileWithLegacyFallback.delete();
                Slog.e("BrightnessTracker", "Failed to read ambient brightness stats.", e);
            }
        } finally {
            IoUtils.closeQuietly(fileInputStream);
        }
    }

    @GuardedBy({"mEventsLock"})
    @VisibleForTesting
    public void writeEventsLocked(OutputStream outputStream) throws IOException {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
        resolveSerializer.startDocument((String) null, Boolean.TRUE);
        resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        resolveSerializer.startTag((String) null, "events");
        BrightnessChangeEvent[] brightnessChangeEventArr = (BrightnessChangeEvent[]) this.mEvents.toArray();
        this.mEvents.clear();
        long currentTimeMillis = this.mInjector.currentTimeMillis() - MAX_EVENT_AGE;
        for (int i = 0; i < brightnessChangeEventArr.length; i++) {
            int userSerialNumber = this.mInjector.getUserSerialNumber(this.mUserManager, brightnessChangeEventArr[i].userId);
            if (userSerialNumber != -1) {
                BrightnessChangeEvent brightnessChangeEvent = brightnessChangeEventArr[i];
                if (brightnessChangeEvent.timeStamp > currentTimeMillis) {
                    this.mEvents.append(brightnessChangeEvent);
                    resolveSerializer.startTag((String) null, "event");
                    resolveSerializer.attributeFloat((String) null, "nits", brightnessChangeEventArr[i].brightness);
                    resolveSerializer.attributeLong((String) null, "timestamp", brightnessChangeEventArr[i].timeStamp);
                    resolveSerializer.attribute((String) null, "packageName", brightnessChangeEventArr[i].packageName);
                    resolveSerializer.attributeInt((String) null, "user", userSerialNumber);
                    String str = brightnessChangeEventArr[i].uniqueDisplayId;
                    if (str == null) {
                        str = "";
                    }
                    resolveSerializer.attribute((String) null, "uniqueDisplayId", str);
                    resolveSerializer.attributeFloat((String) null, "batteryLevel", brightnessChangeEventArr[i].batteryLevel);
                    resolveSerializer.attributeBoolean((String) null, "nightMode", brightnessChangeEventArr[i].nightMode);
                    resolveSerializer.attributeInt((String) null, "colorTemperature", brightnessChangeEventArr[i].colorTemperature);
                    resolveSerializer.attributeBoolean((String) null, "reduceBrightColors", brightnessChangeEventArr[i].reduceBrightColors);
                    resolveSerializer.attributeInt((String) null, "reduceBrightColorsStrength", brightnessChangeEventArr[i].reduceBrightColorsStrength);
                    resolveSerializer.attributeFloat((String) null, "reduceBrightColorsOffset", brightnessChangeEventArr[i].reduceBrightColorsOffset);
                    resolveSerializer.attributeFloat((String) null, "lastNits", brightnessChangeEventArr[i].lastBrightness);
                    resolveSerializer.attributeBoolean((String) null, "defaultConfig", brightnessChangeEventArr[i].isDefaultBrightnessConfig);
                    resolveSerializer.attributeFloat((String) null, "powerSaveFactor", brightnessChangeEventArr[i].powerBrightnessFactor);
                    resolveSerializer.attributeBoolean((String) null, "userPoint", brightnessChangeEventArr[i].isUserSetBrightness);
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    for (int i2 = 0; i2 < brightnessChangeEventArr[i].luxValues.length; i2++) {
                        if (i2 > 0) {
                            sb.append(',');
                            sb2.append(',');
                        }
                        sb.append(Float.toString(brightnessChangeEventArr[i].luxValues[i2]));
                        sb2.append(Long.toString(brightnessChangeEventArr[i].luxTimestamps[i2]));
                    }
                    resolveSerializer.attribute((String) null, "lux", sb.toString());
                    resolveSerializer.attribute((String) null, "luxTimestamps", sb2.toString());
                    BrightnessChangeEvent brightnessChangeEvent2 = brightnessChangeEventArr[i];
                    long[] jArr = brightnessChangeEvent2.colorValueBuckets;
                    if (jArr != null && jArr.length > 0) {
                        resolveSerializer.attributeLong((String) null, "colorSampleDuration", brightnessChangeEvent2.colorSampleDuration);
                        StringBuilder sb3 = new StringBuilder();
                        for (int i3 = 0; i3 < brightnessChangeEventArr[i].colorValueBuckets.length; i3++) {
                            if (i3 > 0) {
                                sb3.append(',');
                            }
                            sb3.append(Long.toString(brightnessChangeEventArr[i].colorValueBuckets[i3]));
                        }
                        resolveSerializer.attribute((String) null, "colorValueBuckets", sb3.toString());
                    }
                    resolveSerializer.endTag((String) null, "event");
                }
            }
        }
        resolveSerializer.endTag((String) null, "events");
        resolveSerializer.endDocument();
        outputStream.flush();
    }

    @GuardedBy({"mEventsLock"})
    @VisibleForTesting
    public void readEventsLocked(InputStream inputStream) throws IOException {
        try {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
            while (true) {
                int next = resolvePullParser.next();
                if (next == 1 || next == 2) {
                    break;
                }
            }
            String name = resolvePullParser.getName();
            if (!"events".equals(name)) {
                throw new XmlPullParserException("Events not found in brightness tracker file " + name);
            }
            long currentTimeMillis = this.mInjector.currentTimeMillis() - MAX_EVENT_AGE;
            int depth = resolvePullParser.getDepth();
            while (true) {
                int next2 = resolvePullParser.next();
                if (next2 == 1) {
                    return;
                }
                if (next2 == 3 && resolvePullParser.getDepth() <= depth) {
                    return;
                }
                if (next2 != 3 && next2 != 4 && "event".equals(resolvePullParser.getName())) {
                    BrightnessChangeEvent.Builder builder = new BrightnessChangeEvent.Builder();
                    builder.setBrightness(resolvePullParser.getAttributeFloat((String) null, "nits"));
                    builder.setTimeStamp(resolvePullParser.getAttributeLong((String) null, "timestamp"));
                    builder.setPackageName(resolvePullParser.getAttributeValue((String) null, "packageName"));
                    builder.setUserId(this.mInjector.getUserId(this.mUserManager, resolvePullParser.getAttributeInt((String) null, "user")));
                    String attributeValue = resolvePullParser.getAttributeValue((String) null, "uniqueDisplayId");
                    if (attributeValue == null) {
                        attributeValue = "";
                    }
                    builder.setUniqueDisplayId(attributeValue);
                    builder.setBatteryLevel(resolvePullParser.getAttributeFloat((String) null, "batteryLevel"));
                    builder.setNightMode(resolvePullParser.getAttributeBoolean((String) null, "nightMode"));
                    builder.setColorTemperature(resolvePullParser.getAttributeInt((String) null, "colorTemperature"));
                    builder.setReduceBrightColors(resolvePullParser.getAttributeBoolean((String) null, "reduceBrightColors"));
                    builder.setReduceBrightColorsStrength(resolvePullParser.getAttributeInt((String) null, "reduceBrightColorsStrength"));
                    builder.setReduceBrightColorsOffset(resolvePullParser.getAttributeFloat((String) null, "reduceBrightColorsOffset"));
                    builder.setLastBrightness(resolvePullParser.getAttributeFloat((String) null, "lastNits"));
                    String attributeValue2 = resolvePullParser.getAttributeValue((String) null, "lux");
                    String attributeValue3 = resolvePullParser.getAttributeValue((String) null, "luxTimestamps");
                    String[] split = attributeValue2.split(",");
                    String[] split2 = attributeValue3.split(",");
                    if (split.length == split2.length) {
                        int length = split.length;
                        float[] fArr = new float[length];
                        long[] jArr = new long[split.length];
                        for (int i = 0; i < length; i++) {
                            fArr[i] = Float.parseFloat(split[i]);
                            jArr[i] = Long.parseLong(split2[i]);
                        }
                        builder.setLuxValues(fArr);
                        builder.setLuxTimestamps(jArr);
                        builder.setIsDefaultBrightnessConfig(resolvePullParser.getAttributeBoolean((String) null, "defaultConfig", false));
                        builder.setPowerBrightnessFactor(resolvePullParser.getAttributeFloat((String) null, "powerSaveFactor", 1.0f));
                        builder.setUserBrightnessPoint(resolvePullParser.getAttributeBoolean((String) null, "userPoint", false));
                        long attributeLong = resolvePullParser.getAttributeLong((String) null, "colorSampleDuration", -1L);
                        String attributeValue4 = resolvePullParser.getAttributeValue((String) null, "colorValueBuckets");
                        if (attributeLong != -1 && attributeValue4 != null) {
                            String[] split3 = attributeValue4.split(",");
                            int length2 = split3.length;
                            long[] jArr2 = new long[length2];
                            for (int i2 = 0; i2 < length2; i2++) {
                                jArr2[i2] = Long.parseLong(split3[i2]);
                            }
                            builder.setColorValues(jArr2, attributeLong);
                        }
                        BrightnessChangeEvent build = builder.build();
                        if (build.userId != -1 && build.timeStamp > currentTimeMillis && build.luxValues.length > 0) {
                            this.mEvents.append(build);
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException | NumberFormatException | XmlPullParserException e) {
            this.mEvents = new RingBuffer<>(BrightnessChangeEvent.class, 100);
            Slog.e("BrightnessTracker", "Failed to parse brightness event", e);
            throw new IOException("failed to parse file", e);
        }
    }

    public void dump(final PrintWriter printWriter) {
        printWriter.println("BrightnessTracker state:");
        synchronized (this.mDataCollectionLock) {
            printWriter.println("  mStarted=" + this.mStarted);
            printWriter.println("  mLightSensor=" + this.mLightSensor);
            printWriter.println("  mLastBatteryLevel=" + this.mLastBatteryLevel);
            printWriter.println("  mLastBrightness=" + this.mLastBrightness);
        }
        synchronized (this.mEventsLock) {
            printWriter.println("  mEventsDirty=" + this.mEventsDirty);
            printWriter.println("  mEvents.size=" + this.mEvents.size());
            BrightnessChangeEvent[] brightnessChangeEventArr = (BrightnessChangeEvent[]) this.mEvents.toArray();
            for (int i = 0; i < brightnessChangeEventArr.length; i++) {
                printWriter.print("    " + FORMAT.format(new Date(brightnessChangeEventArr[i].timeStamp)));
                printWriter.print(", userId=" + brightnessChangeEventArr[i].userId);
                printWriter.print(", " + brightnessChangeEventArr[i].lastBrightness + "->" + brightnessChangeEventArr[i].brightness);
                StringBuilder sb = new StringBuilder();
                sb.append(", isUserSetBrightness=");
                sb.append(brightnessChangeEventArr[i].isUserSetBrightness);
                printWriter.print(sb.toString());
                printWriter.print(", powerBrightnessFactor=" + brightnessChangeEventArr[i].powerBrightnessFactor);
                printWriter.print(", isDefaultBrightnessConfig=" + brightnessChangeEventArr[i].isDefaultBrightnessConfig);
                printWriter.print(" {");
                for (int i2 = 0; i2 < brightnessChangeEventArr[i].luxValues.length; i2++) {
                    if (i2 != 0) {
                        printWriter.print(", ");
                    }
                    printWriter.print("(" + brightnessChangeEventArr[i].luxValues[i2] + "," + brightnessChangeEventArr[i].luxTimestamps[i2] + ")");
                }
                printWriter.println("}");
            }
        }
        printWriter.println("  mWriteBrightnessTrackerStateScheduled=" + this.mWriteBrightnessTrackerStateScheduled);
        this.mBgHandler.runWithScissors(new Runnable() { // from class: com.android.server.display.BrightnessTracker$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BrightnessTracker.this.lambda$dump$1(printWriter);
            }
        }, 1000L);
        if (this.mAmbientBrightnessStatsTracker != null) {
            printWriter.println();
            this.mAmbientBrightnessStatsTracker.dump(printWriter);
        }
    }

    /* renamed from: dumpLocal */
    public final void lambda$dump$1(PrintWriter printWriter) {
        printWriter.println("  mSensorRegistered=" + this.mSensorRegistered);
        printWriter.println("  mColorSamplingEnabled=" + this.mColorSamplingEnabled);
        printWriter.println("  mNoFramesToSample=" + this.mNoFramesToSample);
        printWriter.println("  mFrameRate=" + this.mFrameRate);
    }

    public final void enableColorSampling() {
        if (this.mInjector.isBrightnessModeAutomatic(this.mContentResolver) && this.mInjector.isInteractive(this.mContext) && !this.mColorSamplingEnabled && this.mShouldCollectColorSample) {
            float frameRate = this.mInjector.getFrameRate(this.mContext);
            this.mFrameRate = frameRate;
            if (frameRate <= 0.0f) {
                Slog.wtf("BrightnessTracker", "Default display has a zero or negative framerate.");
                return;
            }
            this.mNoFramesToSample = (int) (frameRate * ((float) COLOR_SAMPLE_DURATION));
            DisplayedContentSamplingAttributes samplingAttributes = this.mInjector.getSamplingAttributes();
            if (samplingAttributes != null && samplingAttributes.getPixelFormat() == 55 && (samplingAttributes.getComponentMask() & 4) != 0) {
                this.mColorSamplingEnabled = this.mInjector.enableColorSampling(true, this.mNoFramesToSample);
            }
            if (this.mColorSamplingEnabled && this.mDisplayListener == null) {
                DisplayListener displayListener = new DisplayListener();
                this.mDisplayListener = displayListener;
                this.mInjector.registerDisplayListener(this.mContext, displayListener, this.mBgHandler);
            }
        }
    }

    public final void disableColorSampling() {
        if (this.mColorSamplingEnabled) {
            this.mInjector.enableColorSampling(false, 0);
            this.mColorSamplingEnabled = false;
            DisplayListener displayListener = this.mDisplayListener;
            if (displayListener != null) {
                this.mInjector.unRegisterDisplayListener(this.mContext, displayListener);
                this.mDisplayListener = null;
            }
        }
    }

    public final void updateColorSampling() {
        if (this.mColorSamplingEnabled && this.mInjector.getFrameRate(this.mContext) != this.mFrameRate) {
            disableColorSampling();
            enableColorSampling();
        }
    }

    public ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats(int i) {
        ArrayList<AmbientBrightnessDayStats> userStats;
        AmbientBrightnessStatsTracker ambientBrightnessStatsTracker = this.mAmbientBrightnessStatsTracker;
        if (ambientBrightnessStatsTracker != null && (userStats = ambientBrightnessStatsTracker.getUserStats(i)) != null) {
            return new ParceledListSlice<>(userStats);
        }
        return ParceledListSlice.emptyList();
    }

    public final void recordAmbientBrightnessStats(SensorEvent sensorEvent) {
        this.mAmbientBrightnessStatsTracker.add(this.mCurrentUserId, sensorEvent.values[0]);
    }

    public final void batteryLevelChanged(int i, int i2) {
        synchronized (this.mDataCollectionLock) {
            this.mLastBatteryLevel = i / i2;
        }
    }

    /* loaded from: classes.dex */
    public final class SensorListener implements SensorEventListener {
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public SensorListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            BrightnessTracker.this.recordAmbientBrightnessStats(sensorEvent);
        }
    }

    /* loaded from: classes.dex */
    public final class DisplayListener implements DisplayManager.DisplayListener {
        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int i) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int i) {
        }

        public DisplayListener() {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int i) {
            if (i == 0) {
                BrightnessTracker.this.updateColorSampling();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (BrightnessTracker.this.mInjector.isBrightnessModeAutomatic(BrightnessTracker.this.mContentResolver)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(3).sendToTarget();
            } else {
                BrightnessTracker.this.mBgHandler.obtainMessage(2).sendToTarget();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.ACTION_SHUTDOWN".equals(action)) {
                BrightnessTracker.this.stop();
                BrightnessTracker.this.scheduleWriteBrightnessTrackerState();
            } else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("level", -1);
                int intExtra2 = intent.getIntExtra("scale", 0);
                if (intExtra == -1 || intExtra2 == 0) {
                    return;
                }
                BrightnessTracker.this.batteryLevelChanged(intExtra, intExtra2);
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(2).sendToTarget();
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                BrightnessTracker.this.mBgHandler.obtainMessage(3).sendToTarget();
            }
        }
    }

    /* loaded from: classes.dex */
    public final class TrackerHandler extends Handler {
        public TrackerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                BrightnessTracker.this.backgroundStart(((Float) message.obj).floatValue());
                return;
            }
            if (i == 1) {
                BrightnessChangeValues brightnessChangeValues = (BrightnessChangeValues) message.obj;
                BrightnessTracker.this.handleBrightnessChanged(brightnessChangeValues.brightness, message.arg1 == 1, brightnessChangeValues.powerBrightnessFactor, brightnessChangeValues.wasShortTermModelActive, brightnessChangeValues.isDefaultBrightnessConfig, brightnessChangeValues.timestamp, brightnessChangeValues.uniqueDisplayId, brightnessChangeValues.luxValues, brightnessChangeValues.luxTimestamps);
            } else if (i == 2) {
                BrightnessTracker.this.stopSensorListener();
                BrightnessTracker.this.disableColorSampling();
            } else if (i == 3) {
                BrightnessTracker.this.startSensorListener();
                BrightnessTracker.this.enableColorSampling();
            } else if (i != 4) {
                if (i != 5) {
                    return;
                }
                BrightnessTracker.this.handleSensorChanged((Sensor) message.obj);
            } else {
                BrightnessTracker.this.mShouldCollectColorSample = ((Boolean) message.obj).booleanValue();
                if (BrightnessTracker.this.mShouldCollectColorSample && !BrightnessTracker.this.mColorSamplingEnabled) {
                    BrightnessTracker.this.enableColorSampling();
                } else if (BrightnessTracker.this.mShouldCollectColorSample || !BrightnessTracker.this.mColorSamplingEnabled) {
                } else {
                    BrightnessTracker.this.disableColorSampling();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class BrightnessChangeValues {
        public final float brightness;
        public final boolean isDefaultBrightnessConfig;
        public final long[] luxTimestamps;
        public final float[] luxValues;
        public final float powerBrightnessFactor;
        public final long timestamp;
        public final String uniqueDisplayId;
        public final boolean wasShortTermModelActive;

        public BrightnessChangeValues(float f, float f2, boolean z, boolean z2, long j, String str, float[] fArr, long[] jArr) {
            this.brightness = f;
            this.powerBrightnessFactor = f2;
            this.wasShortTermModelActive = z;
            this.isDefaultBrightnessConfig = z2;
            this.timestamp = j;
            this.uniqueDisplayId = str;
            this.luxValues = fArr;
            this.luxTimestamps = jArr;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public void registerSensorListener(Context context, SensorEventListener sensorEventListener, Sensor sensor, Handler handler) {
            ((SensorManager) context.getSystemService(SensorManager.class)).registerListener(sensorEventListener, sensor, 3, handler);
        }

        public void unregisterSensorListener(Context context, SensorEventListener sensorEventListener) {
            ((SensorManager) context.getSystemService(SensorManager.class)).unregisterListener(sensorEventListener);
        }

        public void registerBrightnessModeObserver(ContentResolver contentResolver, ContentObserver contentObserver) {
            contentResolver.registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, contentObserver, -1);
        }

        public void unregisterBrightnessModeObserver(Context context, ContentObserver contentObserver) {
            context.getContentResolver().unregisterContentObserver(contentObserver);
        }

        public void registerReceiver(Context context, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
            context.registerReceiver(broadcastReceiver, intentFilter, 2);
        }

        public void unregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
            context.unregisterReceiver(broadcastReceiver);
        }

        public Handler getBackgroundHandler() {
            return BackgroundThread.getHandler();
        }

        public boolean isBrightnessModeAutomatic(ContentResolver contentResolver) {
            return Settings.System.getIntForUser(contentResolver, "screen_brightness_mode", 0, -2) == 1;
        }

        public AtomicFile getFile(String str) {
            return new AtomicFile(new File(Environment.getDataSystemDirectory(), str));
        }

        public AtomicFile getLegacyFile(String str) {
            return new AtomicFile(new File(Environment.getDataSystemDeDirectory(), str));
        }

        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }

        public long elapsedRealtimeNanos() {
            return SystemClock.elapsedRealtimeNanos();
        }

        public int getUserSerialNumber(UserManager userManager, int i) {
            return userManager.getUserSerialNumber(i);
        }

        public int getUserId(UserManager userManager, int i) {
            return userManager.getUserHandle(i);
        }

        public int[] getProfileIds(UserManager userManager, int i) {
            if (userManager != null) {
                return userManager.getProfileIds(i, false);
            }
            return new int[]{i};
        }

        public ActivityTaskManager.RootTaskInfo getFocusedStack() throws RemoteException {
            return ActivityTaskManager.getService().getFocusedRootTaskInfo();
        }

        public void scheduleIdleJob(Context context) {
            BrightnessIdleJob.scheduleJob(context);
        }

        public void cancelIdleJob(Context context) {
            BrightnessIdleJob.cancelJob(context);
        }

        public boolean isInteractive(Context context) {
            return ((PowerManager) context.getSystemService(PowerManager.class)).isInteractive();
        }

        public int getNightDisplayColorTemperature(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getNightDisplayColorTemperature();
        }

        public boolean isNightDisplayActivated(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).isNightDisplayActivated();
        }

        public int getReduceBrightColorsStrength(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getReduceBrightColorsStrength();
        }

        public float getReduceBrightColorsOffsetFactor(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getReduceBrightColorsOffsetFactor();
        }

        public boolean isReduceBrightColorsActivated(Context context) {
            return ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).isReduceBrightColorsActivated();
        }

        public DisplayedContentSample sampleColor(int i) {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).getDisplayedContentSample(0, i, 0L);
        }

        public float getFrameRate(Context context) {
            return ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(0).getRefreshRate();
        }

        public DisplayedContentSamplingAttributes getSamplingAttributes() {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).getDisplayedContentSamplingAttributes(0);
        }

        public boolean enableColorSampling(boolean z, int i) {
            return ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).setDisplayedContentSamplingEnabled(0, z, 4, i);
        }

        public void registerDisplayListener(Context context, DisplayManager.DisplayListener displayListener, Handler handler) {
            ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(displayListener, handler);
        }

        public void unRegisterDisplayListener(Context context, DisplayManager.DisplayListener displayListener) {
            ((DisplayManager) context.getSystemService(DisplayManager.class)).unregisterDisplayListener(displayListener);
        }
    }
}
