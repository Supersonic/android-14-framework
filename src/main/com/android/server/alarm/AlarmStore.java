package com.android.server.alarm;

import android.util.IndentingPrintWriter;
import android.util.proto.ProtoOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public interface AlarmStore {

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface AlarmDeliveryCalculator {
        boolean updateAlarmDelivery(Alarm alarm);
    }

    void add(Alarm alarm);

    ArrayList<Alarm> asList();

    void dump(IndentingPrintWriter indentingPrintWriter, long j, SimpleDateFormat simpleDateFormat);

    void dumpProto(ProtoOutputStream protoOutputStream, long j);

    int getCount(Predicate<Alarm> predicate);

    long getNextDeliveryTime();

    Alarm getNextWakeFromIdleAlarm();

    long getNextWakeupDeliveryTime();

    ArrayList<Alarm> remove(Predicate<Alarm> predicate);

    ArrayList<Alarm> removePendingAlarms(long j);

    void setAlarmClockRemovalListener(Runnable runnable);

    int size();

    boolean updateAlarmDeliveries(AlarmDeliveryCalculator alarmDeliveryCalculator);
}
