package com.android.server.p006am;

import android.app.ActivityManagerInternal;
import android.content.Context;
import android.os.SystemClock;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateTimeSlotEventsTracker;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
/* renamed from: com.android.server.am.AppBindServiceEventsTracker */
/* loaded from: classes.dex */
public final class AppBindServiceEventsTracker extends BaseAppStateTimeSlotEventsTracker<AppBindServiceEventsPolicy, BaseAppStateTimeSlotEventsTracker.SimpleAppStateTimeslotEvents> implements ActivityManagerInternal.BindServiceEventListener {
    @Override // com.android.server.p006am.BaseAppStateTracker
    public int getType() {
        return 7;
    }

    public AppBindServiceEventsTracker(Context context, AppRestrictionController appRestrictionController) {
        this(context, appRestrictionController, null, null);
    }

    public AppBindServiceEventsTracker(Context context, AppRestrictionController appRestrictionController, Constructor<? extends BaseAppStateTracker.Injector<AppBindServiceEventsPolicy>> constructor, Object obj) {
        super(context, appRestrictionController, constructor, obj);
        BaseAppStateTracker.Injector<T> injector = this.mInjector;
        injector.setPolicy(new AppBindServiceEventsPolicy(injector, this));
    }

    public void onBindingService(String str, int i) {
        if (((AppBindServiceEventsPolicy) this.mInjector.getPolicy()).isEnabled()) {
            onNewEvent(str, i);
        }
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onSystemReady() {
        super.onSystemReady();
        this.mInjector.getActivityManagerInternal().addBindServiceEventListener(this);
    }

    @Override // com.android.server.p006am.BaseAppStateEvents.Factory
    public BaseAppStateTimeSlotEventsTracker.SimpleAppStateTimeslotEvents createAppStateEvents(int i, String str) {
        return new BaseAppStateTimeSlotEventsTracker.SimpleAppStateTimeslotEvents(i, str, ((AppBindServiceEventsPolicy) this.mInjector.getPolicy()).getTimeSlotSize(), "ActivityManager", (BaseAppStateEvents.MaxTrackingDurationConfig) this.mInjector.getPolicy());
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public byte[] getTrackerInfoForStatsd(int i) {
        int totalEventsLocked = getTotalEventsLocked(i, SystemClock.elapsedRealtime());
        if (totalEventsLocked == 0) {
            return null;
        }
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        protoOutputStream.write(1120986464257L, totalEventsLocked);
        protoOutputStream.flush();
        return protoOutputStream.getBytes();
    }

    @Override // com.android.server.p006am.BaseAppStateEventsTracker, com.android.server.p006am.BaseAppStateTracker
    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println("APP BIND SERVICE EVENT TRACKER:");
        super.dump(printWriter, "  " + str);
    }

    /* renamed from: com.android.server.am.AppBindServiceEventsTracker$AppBindServiceEventsPolicy */
    /* loaded from: classes.dex */
    public static final class AppBindServiceEventsPolicy extends BaseAppStateTimeSlotEventsTracker.BaseAppStateTimeSlotEventsPolicy<AppBindServiceEventsTracker> {
        public AppBindServiceEventsPolicy(BaseAppStateTracker.Injector injector, AppBindServiceEventsTracker appBindServiceEventsTracker) {
            super(injector, appBindServiceEventsTracker, "bg_bind_svc_monitor_enabled", true, "bg_bind_svc_window", BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS, "bg_ex_bind_svc_threshold", FrameworkStatsLog.WIFI_BYTES_TRANSFER);
        }

        @Override // com.android.server.p006am.BaseAppStateTimeSlotEventsTracker.BaseAppStateTimeSlotEventsPolicy, com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.println("APP BIND SERVICE EVENT TRACKER POLICY SETTINGS:");
            super.dump(printWriter, "  " + str);
        }
    }
}
