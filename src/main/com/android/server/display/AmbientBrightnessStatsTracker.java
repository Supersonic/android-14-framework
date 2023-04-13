package com.android.server.display;

import android.hardware.display.AmbientBrightnessDayStats;
import android.os.SystemClock;
import android.os.UserManager;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AmbientBrightnessStatsTracker {
    @VisibleForTesting
    static final float[] BUCKET_BOUNDARIES_FOR_NEW_STATS = {0.0f, 0.1f, 0.3f, 1.0f, 3.0f, 10.0f, 30.0f, 100.0f, 300.0f, 1000.0f, 3000.0f, 10000.0f};
    @VisibleForTesting
    static final int MAX_DAYS_TO_TRACK = 7;
    public final AmbientBrightnessStats mAmbientBrightnessStats;
    public float mCurrentAmbientBrightness;
    public int mCurrentUserId;
    public final Injector mInjector;
    public final Timer mTimer;
    public final UserManager mUserManager;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Clock {
        long elapsedTimeMillis();
    }

    public AmbientBrightnessStatsTracker(UserManager userManager, Injector injector) {
        this.mUserManager = userManager;
        if (injector != null) {
            this.mInjector = injector;
        } else {
            this.mInjector = new Injector();
        }
        this.mAmbientBrightnessStats = new AmbientBrightnessStats();
        this.mTimer = new Timer(new Clock() { // from class: com.android.server.display.AmbientBrightnessStatsTracker$$ExternalSyntheticLambda0
            @Override // com.android.server.display.AmbientBrightnessStatsTracker.Clock
            public final long elapsedTimeMillis() {
                long lambda$new$0;
                lambda$new$0 = AmbientBrightnessStatsTracker.this.lambda$new$0();
                return lambda$new$0;
            }
        });
        this.mCurrentAmbientBrightness = -1.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ long lambda$new$0() {
        return this.mInjector.elapsedRealtimeMillis();
    }

    public synchronized void start() {
        this.mTimer.reset();
        this.mTimer.start();
    }

    public synchronized void stop() {
        if (this.mTimer.isRunning()) {
            this.mAmbientBrightnessStats.log(this.mCurrentUserId, this.mInjector.getLocalDate(), this.mCurrentAmbientBrightness, this.mTimer.totalDurationSec());
        }
        this.mTimer.reset();
        this.mCurrentAmbientBrightness = -1.0f;
    }

    public synchronized void add(int i, float f) {
        if (this.mTimer.isRunning()) {
            int i2 = this.mCurrentUserId;
            if (i == i2) {
                this.mAmbientBrightnessStats.log(i2, this.mInjector.getLocalDate(), this.mCurrentAmbientBrightness, this.mTimer.totalDurationSec());
            } else {
                this.mCurrentUserId = i;
            }
            this.mTimer.reset();
            this.mTimer.start();
            this.mCurrentAmbientBrightness = f;
        }
    }

    public synchronized void writeStats(OutputStream outputStream) throws IOException {
        this.mAmbientBrightnessStats.writeToXML(outputStream);
    }

    public synchronized void readStats(InputStream inputStream) throws IOException {
        this.mAmbientBrightnessStats.readFromXML(inputStream);
    }

    public synchronized ArrayList<AmbientBrightnessDayStats> getUserStats(int i) {
        return this.mAmbientBrightnessStats.getUserStats(i);
    }

    public synchronized void dump(PrintWriter printWriter) {
        printWriter.println("AmbientBrightnessStats:");
        printWriter.print(this.mAmbientBrightnessStats);
    }

    /* loaded from: classes.dex */
    public class AmbientBrightnessStats {
        public Map<Integer, Deque<AmbientBrightnessDayStats>> mStats = new HashMap();

        public AmbientBrightnessStats() {
        }

        public void log(int i, LocalDate localDate, float f, float f2) {
            getOrCreateDayStats(getOrCreateUserStats(this.mStats, i), localDate).log(f, f2);
        }

        public ArrayList<AmbientBrightnessDayStats> getUserStats(int i) {
            if (this.mStats.containsKey(Integer.valueOf(i))) {
                return new ArrayList<>(this.mStats.get(Integer.valueOf(i)));
            }
            return null;
        }

        public void writeToXML(OutputStream outputStream) throws IOException {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            LocalDate minusDays = AmbientBrightnessStatsTracker.this.mInjector.getLocalDate().minusDays(7L);
            resolveSerializer.startTag((String) null, "ambient-brightness-stats");
            for (Map.Entry<Integer, Deque<AmbientBrightnessDayStats>> entry : this.mStats.entrySet()) {
                for (AmbientBrightnessDayStats ambientBrightnessDayStats : entry.getValue()) {
                    int userSerialNumber = AmbientBrightnessStatsTracker.this.mInjector.getUserSerialNumber(AmbientBrightnessStatsTracker.this.mUserManager, entry.getKey().intValue());
                    if (userSerialNumber != -1 && ambientBrightnessDayStats.getLocalDate().isAfter(minusDays)) {
                        resolveSerializer.startTag((String) null, "ambient-brightness-day-stats");
                        resolveSerializer.attributeInt((String) null, "user", userSerialNumber);
                        resolveSerializer.attribute((String) null, "local-date", ambientBrightnessDayStats.getLocalDate().toString());
                        StringBuilder sb = new StringBuilder();
                        StringBuilder sb2 = new StringBuilder();
                        for (int i = 0; i < ambientBrightnessDayStats.getBucketBoundaries().length; i++) {
                            if (i > 0) {
                                sb.append(",");
                                sb2.append(",");
                            }
                            sb.append(ambientBrightnessDayStats.getBucketBoundaries()[i]);
                            sb2.append(ambientBrightnessDayStats.getStats()[i]);
                        }
                        resolveSerializer.attribute((String) null, "bucket-boundaries", sb.toString());
                        resolveSerializer.attribute((String) null, "bucket-stats", sb2.toString());
                        resolveSerializer.endTag((String) null, "ambient-brightness-day-stats");
                    }
                }
            }
            resolveSerializer.endTag((String) null, "ambient-brightness-stats");
            resolveSerializer.endDocument();
            outputStream.flush();
        }

        /* JADX WARN: Code restructure failed: missing block: B:39:0x00d0, code lost:
            r13.mStats = r1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x00d2, code lost:
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void readFromXML(InputStream inputStream) throws IOException {
            try {
                HashMap hashMap = new HashMap();
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
                while (true) {
                    int next = resolvePullParser.next();
                    if (next == 1 || next == 2) {
                        break;
                    }
                }
                String name = resolvePullParser.getName();
                if (!"ambient-brightness-stats".equals(name)) {
                    throw new XmlPullParserException("Ambient brightness stats not found in tracker file " + name);
                }
                LocalDate minusDays = AmbientBrightnessStatsTracker.this.mInjector.getLocalDate().minusDays(7L);
                int depth = resolvePullParser.getDepth();
                while (true) {
                    int next2 = resolvePullParser.next();
                    if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                        break;
                    } else if (next2 != 3 && next2 != 4 && "ambient-brightness-day-stats".equals(resolvePullParser.getName())) {
                        int attributeInt = resolvePullParser.getAttributeInt((String) null, "user");
                        LocalDate parse = LocalDate.parse(resolvePullParser.getAttributeValue((String) null, "local-date"));
                        String[] split = resolvePullParser.getAttributeValue((String) null, "bucket-boundaries").split(",");
                        String[] split2 = resolvePullParser.getAttributeValue((String) null, "bucket-stats").split(",");
                        if (split.length != split2.length || split.length < 1) {
                            break;
                        }
                        float[] fArr = new float[split.length];
                        float[] fArr2 = new float[split2.length];
                        for (int i = 0; i < split.length; i++) {
                            fArr[i] = Float.parseFloat(split[i]);
                            fArr2[i] = Float.parseFloat(split2[i]);
                        }
                        int userId = AmbientBrightnessStatsTracker.this.mInjector.getUserId(AmbientBrightnessStatsTracker.this.mUserManager, attributeInt);
                        if (userId != -1 && parse.isAfter(minusDays)) {
                            getOrCreateUserStats(hashMap, userId).offer(new AmbientBrightnessDayStats(parse, fArr, fArr2));
                        }
                    }
                }
                throw new IOException("Invalid brightness stats string.");
            } catch (IOException | NullPointerException | NumberFormatException | DateTimeParseException | XmlPullParserException e) {
                throw new IOException("Failed to parse brightness stats file.", e);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Deque<AmbientBrightnessDayStats>> entry : this.mStats.entrySet()) {
                for (AmbientBrightnessDayStats ambientBrightnessDayStats : entry.getValue()) {
                    sb.append("  ");
                    sb.append(entry.getKey());
                    sb.append(" ");
                    sb.append(ambientBrightnessDayStats);
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        public final Deque<AmbientBrightnessDayStats> getOrCreateUserStats(Map<Integer, Deque<AmbientBrightnessDayStats>> map, int i) {
            if (!map.containsKey(Integer.valueOf(i))) {
                map.put(Integer.valueOf(i), new ArrayDeque());
            }
            return map.get(Integer.valueOf(i));
        }

        public final AmbientBrightnessDayStats getOrCreateDayStats(Deque<AmbientBrightnessDayStats> deque, LocalDate localDate) {
            AmbientBrightnessDayStats peekLast = deque.peekLast();
            if (peekLast == null || !peekLast.getLocalDate().equals(localDate)) {
                if (peekLast != null) {
                    FrameworkStatsLog.write((int) FrameworkStatsLog.AMBIENT_BRIGHTNESS_STATS_REPORTED, peekLast.getStats(), peekLast.getBucketBoundaries());
                }
                AmbientBrightnessDayStats ambientBrightnessDayStats = new AmbientBrightnessDayStats(localDate, AmbientBrightnessStatsTracker.BUCKET_BOUNDARIES_FOR_NEW_STATS);
                if (deque.size() == 7) {
                    deque.poll();
                }
                deque.offer(ambientBrightnessDayStats);
                return ambientBrightnessDayStats;
            }
            return peekLast;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Timer {
        public final Clock clock;
        public long startTimeMillis;
        public boolean started;

        public Timer(Clock clock) {
            this.clock = clock;
        }

        public void reset() {
            this.started = false;
        }

        public void start() {
            if (this.started) {
                return;
            }
            this.startTimeMillis = this.clock.elapsedTimeMillis();
            this.started = true;
        }

        public boolean isRunning() {
            return this.started;
        }

        public float totalDurationSec() {
            if (this.started) {
                return (float) ((this.clock.elapsedTimeMillis() - this.startTimeMillis) / 1000.0d);
            }
            return 0.0f;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public long elapsedRealtimeMillis() {
            return SystemClock.elapsedRealtime();
        }

        public int getUserSerialNumber(UserManager userManager, int i) {
            return userManager.getUserSerialNumber(i);
        }

        public int getUserId(UserManager userManager, int i) {
            return userManager.getUserHandle(i);
        }

        public LocalDate getLocalDate() {
            return LocalDate.now();
        }
    }
}
