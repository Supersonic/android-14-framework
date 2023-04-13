package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.android.server.usage.IntervalStats;
import java.io.IOException;
import java.net.ProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public final class UsageStatsXmlV1 {
    public static void loadUsageStats(XmlPullParser xmlPullParser, IntervalStats intervalStats) throws XmlPullParserException, IOException {
        String attributeValue = xmlPullParser.getAttributeValue(null, "package");
        if (attributeValue == null) {
            throw new ProtocolException("no package attribute present");
        }
        UsageStats orCreateUsageStats = intervalStats.getOrCreateUsageStats(attributeValue);
        orCreateUsageStats.mLastTimeUsed = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "lastTimeActive");
        try {
            orCreateUsageStats.mLastTimeVisible = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "lastTimeVisible");
        } catch (IOException unused) {
            Log.i("UsageStatsXmlV1", "Failed to parse mLastTimeVisible");
        }
        try {
            orCreateUsageStats.mLastTimeForegroundServiceUsed = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "lastTimeServiceUsed");
        } catch (IOException unused2) {
            Log.i("UsageStatsXmlV1", "Failed to parse mLastTimeForegroundServiceUsed");
        }
        orCreateUsageStats.mTotalTimeInForeground = XmlUtils.readLongAttribute(xmlPullParser, "timeActive");
        try {
            orCreateUsageStats.mTotalTimeVisible = XmlUtils.readLongAttribute(xmlPullParser, "timeVisible");
        } catch (IOException unused3) {
            Log.i("UsageStatsXmlV1", "Failed to parse mTotalTimeVisible");
        }
        try {
            orCreateUsageStats.mTotalTimeForegroundServiceUsed = XmlUtils.readLongAttribute(xmlPullParser, "timeServiceUsed");
        } catch (IOException unused4) {
            Log.i("UsageStatsXmlV1", "Failed to parse mTotalTimeForegroundServiceUsed");
        }
        orCreateUsageStats.mLastEvent = XmlUtils.readIntAttribute(xmlPullParser, "lastEvent");
        orCreateUsageStats.mAppLaunchCount = XmlUtils.readIntAttribute(xmlPullParser, "appLaunchCount", 0);
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            String name = xmlPullParser.getName();
            if (next == 3 && name.equals("package")) {
                return;
            }
            if (next == 2 && name.equals("chosen_action")) {
                loadChooserCounts(xmlPullParser, orCreateUsageStats, XmlUtils.readStringAttribute(xmlPullParser, "name"));
            }
        }
    }

    public static void loadCountAndTime(XmlPullParser xmlPullParser, IntervalStats.EventTracker eventTracker) throws IOException, XmlPullParserException {
        eventTracker.count = XmlUtils.readIntAttribute(xmlPullParser, "count", 0);
        eventTracker.duration = XmlUtils.readLongAttribute(xmlPullParser, "time", 0L);
        XmlUtils.skipCurrentTag(xmlPullParser);
    }

    public static void loadChooserCounts(XmlPullParser xmlPullParser, UsageStats usageStats, String str) throws XmlPullParserException, IOException {
        if (str == null) {
            return;
        }
        if (usageStats.mChooserCounts == null) {
            usageStats.mChooserCounts = new ArrayMap();
        }
        if (!usageStats.mChooserCounts.containsKey(str)) {
            usageStats.mChooserCounts.put(str, new ArrayMap());
        }
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            String name = xmlPullParser.getName();
            if (next == 3 && name.equals("chosen_action")) {
                return;
            }
            if (next == 2 && name.equals("category")) {
                ((ArrayMap) usageStats.mChooserCounts.get(str)).put(XmlUtils.readStringAttribute(xmlPullParser, "name"), Integer.valueOf(XmlUtils.readIntAttribute(xmlPullParser, "count")));
            }
        }
    }

    public static void loadConfigStats(XmlPullParser xmlPullParser, IntervalStats intervalStats) throws XmlPullParserException, IOException {
        Configuration configuration = new Configuration();
        Configuration.readXmlAttrs(xmlPullParser, configuration);
        ConfigurationStats orCreateConfigurationStats = intervalStats.getOrCreateConfigurationStats(configuration);
        orCreateConfigurationStats.mLastTimeActive = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "lastTimeActive");
        orCreateConfigurationStats.mTotalTimeActive = XmlUtils.readLongAttribute(xmlPullParser, "timeActive");
        orCreateConfigurationStats.mActivationCount = XmlUtils.readIntAttribute(xmlPullParser, "count");
        if (XmlUtils.readBooleanAttribute(xmlPullParser, "active")) {
            intervalStats.activeConfiguration = orCreateConfigurationStats.mConfiguration;
        }
    }

    public static void loadEvent(XmlPullParser xmlPullParser, IntervalStats intervalStats) throws XmlPullParserException, IOException {
        String readStringAttribute = XmlUtils.readStringAttribute(xmlPullParser, "package");
        if (readStringAttribute == null) {
            throw new ProtocolException("no package attribute present");
        }
        UsageEvents.Event buildEvent = intervalStats.buildEvent(readStringAttribute, XmlUtils.readStringAttribute(xmlPullParser, "class"));
        buildEvent.mFlags = XmlUtils.readIntAttribute(xmlPullParser, "flags", 0);
        buildEvent.mTimeStamp = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "time");
        buildEvent.mEventType = XmlUtils.readIntAttribute(xmlPullParser, "type");
        try {
            buildEvent.mInstanceId = XmlUtils.readIntAttribute(xmlPullParser, "instanceId");
        } catch (IOException unused) {
            Log.i("UsageStatsXmlV1", "Failed to parse mInstanceId");
        }
        int i = buildEvent.mEventType;
        if (i != 5) {
            if (i == 8) {
                String readStringAttribute2 = XmlUtils.readStringAttribute(xmlPullParser, "shortcutId");
                buildEvent.mShortcutId = readStringAttribute2 != null ? readStringAttribute2.intern() : null;
            } else if (i == 11) {
                buildEvent.mBucketAndReason = XmlUtils.readIntAttribute(xmlPullParser, "standbyBucket", 0);
            } else if (i == 12) {
                String readStringAttribute3 = XmlUtils.readStringAttribute(xmlPullParser, "notificationChannel");
                buildEvent.mNotificationChannelId = readStringAttribute3 != null ? readStringAttribute3.intern() : null;
            }
        } else {
            Configuration configuration = new Configuration();
            buildEvent.mConfiguration = configuration;
            Configuration.readXmlAttrs(xmlPullParser, configuration);
        }
        intervalStats.addEvent(buildEvent);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00a2, code lost:
        if (r1.equals("keyguard-hidden") == false) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void read(XmlPullParser xmlPullParser, IntervalStats intervalStats) throws XmlPullParserException, IOException {
        intervalStats.packageStats.clear();
        intervalStats.configurations.clear();
        intervalStats.activeConfiguration = null;
        intervalStats.events.clear();
        intervalStats.endTime = intervalStats.beginTime + XmlUtils.readLongAttribute(xmlPullParser, "endTime");
        try {
            intervalStats.majorVersion = XmlUtils.readIntAttribute(xmlPullParser, "majorVersion");
        } catch (IOException unused) {
            Log.i("UsageStatsXmlV1", "Failed to parse majorVersion");
        }
        try {
            intervalStats.minorVersion = XmlUtils.readIntAttribute(xmlPullParser, "minorVersion");
        } catch (IOException unused2) {
            Log.i("UsageStatsXmlV1", "Failed to parse minorVersion");
        }
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            char c = 1;
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                name.hashCode();
                switch (name.hashCode()) {
                    case -1354792126:
                        if (name.equals("config")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1169351247:
                        break;
                    case -807157790:
                        if (name.equals("non-interactive")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -807062458:
                        if (name.equals("package")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 96891546:
                        if (name.equals("event")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 526608426:
                        if (name.equals("keyguard-shown")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1844104930:
                        if (name.equals("interactive")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        loadConfigStats(xmlPullParser, intervalStats);
                        continue;
                    case 1:
                        loadCountAndTime(xmlPullParser, intervalStats.keyguardHiddenTracker);
                        continue;
                    case 2:
                        loadCountAndTime(xmlPullParser, intervalStats.nonInteractiveTracker);
                        continue;
                    case 3:
                        loadUsageStats(xmlPullParser, intervalStats);
                        continue;
                    case 4:
                        loadEvent(xmlPullParser, intervalStats);
                        continue;
                    case 5:
                        loadCountAndTime(xmlPullParser, intervalStats.keyguardShownTracker);
                        continue;
                    case 6:
                        loadCountAndTime(xmlPullParser, intervalStats.interactiveTracker);
                        continue;
                }
            }
        }
    }
}
