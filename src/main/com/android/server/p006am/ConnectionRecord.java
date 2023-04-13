package com.android.server.p006am;

import android.app.IServiceConnection;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.os.SystemClock;
import android.p005os.IInstalld;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.app.procstats.AssociationState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.server.p014wm.ActivityServiceConnectionsHolder;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.ConnectionRecord */
/* loaded from: classes.dex */
public final class ConnectionRecord {
    public static final int[] BIND_ORIG_ENUMS = {1, 2, 4, 8388608, 8, 16, 32, 64, 128, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, 256, IInstalld.FLAG_USE_QUOTA, 512};
    public static final int[] BIND_PROTO_ENUMS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 18};
    public final ActivityServiceConnectionsHolder<ConnectionRecord> activity;
    public final ComponentName aliasComponent;
    public AssociationState.SourceState association;
    public final AppBindRecord binding;
    public final PendingIntent clientIntent;
    public final int clientLabel;
    public final String clientPackageName;
    public final String clientProcessName;
    public final int clientUid;
    public final IServiceConnection conn;
    public final long flags;
    public Object mProcStatsLock;
    public boolean serviceDead;
    public String stringName;

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "binding=" + this.binding);
        ActivityServiceConnectionsHolder<ConnectionRecord> activityServiceConnectionsHolder = this.activity;
        if (activityServiceConnectionsHolder != null) {
            activityServiceConnectionsHolder.dump(printWriter, str);
        }
        printWriter.println(str + "conn=" + this.conn.asBinder() + " flags=0x" + Long.toHexString(this.flags));
    }

    public ConnectionRecord(AppBindRecord appBindRecord, ActivityServiceConnectionsHolder<ConnectionRecord> activityServiceConnectionsHolder, IServiceConnection iServiceConnection, long j, int i, PendingIntent pendingIntent, int i2, String str, String str2, ComponentName componentName) {
        this.binding = appBindRecord;
        this.activity = activityServiceConnectionsHolder;
        this.conn = iServiceConnection;
        this.flags = j;
        this.clientLabel = i;
        this.clientIntent = pendingIntent;
        this.clientUid = i2;
        this.clientProcessName = str;
        this.clientPackageName = str2;
        this.aliasComponent = componentName;
    }

    public long getFlags() {
        return this.flags;
    }

    public boolean hasFlag(int i) {
        return (Integer.toUnsignedLong(i) & this.flags) != 0;
    }

    public boolean notHasFlag(int i) {
        return !hasFlag(i);
    }

    public void startAssociationIfNeeded() {
        if (this.association == null) {
            ServiceRecord serviceRecord = this.binding.service;
            if (serviceRecord.app != null) {
                if (serviceRecord.appInfo.uid == this.clientUid && serviceRecord.processName.equals(this.clientProcessName)) {
                    return;
                }
                ProcessStats.ProcessStateHolder processStateHolder = this.binding.service.app.getPkgList().get(this.binding.service.instanceName.getPackageName());
                if (processStateHolder == null) {
                    Slog.wtf("ActivityManager", "No package in referenced service " + this.binding.service.shortInstanceName + ": proc=" + this.binding.service.app);
                } else if (processStateHolder.pkg == null) {
                    Slog.wtf("ActivityManager", "Inactive holder in referenced service " + this.binding.service.shortInstanceName + ": proc=" + this.binding.service.app);
                } else {
                    Object obj = this.binding.service.app.mService.mProcessStats.mLock;
                    this.mProcStatsLock = obj;
                    synchronized (obj) {
                        this.association = processStateHolder.pkg.getAssociationStateLocked(processStateHolder.state, this.binding.service.instanceName.getClassName()).startSource(this.clientUid, this.clientProcessName, this.clientPackageName);
                    }
                }
            }
        }
    }

    public void trackProcState(int i, int i2) {
        if (this.association != null) {
            synchronized (this.mProcStatsLock) {
                this.association.trackProcState(i, i2, SystemClock.uptimeMillis());
            }
        }
    }

    public void stopAssociation() {
        if (this.association != null) {
            synchronized (this.mProcStatsLock) {
                this.association.stop();
            }
            this.association = null;
        }
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ConnectionRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" u");
        sb.append(this.binding.client.userId);
        sb.append(' ');
        if (hasFlag(1)) {
            sb.append("CR ");
        }
        if (hasFlag(2)) {
            sb.append("DBG ");
        }
        if (hasFlag(4)) {
            sb.append("!FG ");
        }
        if (hasFlag(8388608)) {
            sb.append("IMPB ");
        }
        if (hasFlag(8)) {
            sb.append("ABCLT ");
        }
        if (hasFlag(16)) {
            sb.append("OOM ");
        }
        if (hasFlag(32)) {
            sb.append("WPRI ");
        }
        if (hasFlag(64)) {
            sb.append("IMP ");
        }
        if (hasFlag(128)) {
            sb.append("WACT ");
        }
        if (hasFlag(33554432)) {
            sb.append("FGSA ");
        }
        if (hasFlag(67108864)) {
            sb.append("FGS ");
        }
        if (hasFlag(134217728)) {
            sb.append("LACT ");
        }
        if (hasFlag(524288)) {
            sb.append("SLTA ");
        }
        if (hasFlag(268435456)) {
            sb.append("VFGS ");
        }
        if (hasFlag(536870912)) {
            sb.append("UI ");
        }
        if (hasFlag(1073741824)) {
            sb.append("!VIS ");
        }
        if (hasFlag(256)) {
            sb.append("!PRCP ");
        }
        if (hasFlag(512)) {
            sb.append("BALF ");
        }
        if (hasFlag(IInstalld.FLAG_USE_QUOTA)) {
            sb.append("CAPS ");
        }
        if (this.serviceDead) {
            sb.append("DEAD ");
        }
        sb.append(this.binding.service.shortInstanceName);
        sb.append(":@");
        sb.append(Integer.toHexString(System.identityHashCode(this.conn.asBinder())));
        sb.append(" flags=0x" + Long.toHexString(this.flags));
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        if (this.binding == null) {
            return;
        }
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, Integer.toHexString(System.identityHashCode(this)));
        ProcessRecord processRecord = this.binding.client;
        if (processRecord != null) {
            protoOutputStream.write(1120986464258L, processRecord.userId);
        }
        ProtoUtils.writeBitWiseFlagsToProtoEnum(protoOutputStream, 2259152797699L, this.flags, BIND_ORIG_ENUMS, BIND_PROTO_ENUMS);
        if (this.serviceDead) {
            protoOutputStream.write(2259152797699L, 15);
        }
        ServiceRecord serviceRecord = this.binding.service;
        if (serviceRecord != null) {
            protoOutputStream.write(1138166333444L, serviceRecord.shortInstanceName);
        }
        protoOutputStream.end(start);
    }
}
