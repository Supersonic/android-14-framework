package com.android.server.p006am;

import android.app.ActivityManagerInternal;
import android.app.BackgroundStartPrivileges;
import android.app.BroadcastOptions;
import android.app.compat.CompatChanges;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.PrintWriterPrinter;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
/* renamed from: com.android.server.am.BroadcastRecord */
/* loaded from: classes.dex */
public final class BroadcastRecord extends Binder {
    public static final List<Object> EMPTY_RECEIVERS = List.of();
    public static AtomicInteger sNextToken = new AtomicInteger(1);
    public final boolean alarm;
    public int anrCount;
    public final int appOp;
    public final int[] blockedUntilTerminalCount;
    public final ProcessRecord callerApp;
    public final String callerFeatureId;
    public final boolean callerInstantApp;
    public final boolean callerInstrumented;
    public final String callerPackage;
    public final int callingPid;
    public final int callingUid;
    public ProcessRecord curApp;
    public ComponentName curComponent;
    public BroadcastFilter curFilter;
    public Bundle curFilteredExtras;
    public ActivityInfo curReceiver;
    public final boolean deferUntilActive;
    public boolean deferred;
    public int deferredCount;
    public final boolean[] deferredUntilActive;
    public final int[] delivery;
    public final String[] deliveryReasons;
    public long dispatchClockTime;
    public long dispatchRealTime;
    public long dispatchTime;
    public long enqueueClockTime;
    public long enqueueRealTime;
    public long enqueueTime;
    public final String[] excludedPackages;
    public final String[] excludedPermissions;
    public final BiFunction<Integer, Bundle, Bundle> filterExtrasForReceiver;
    public long finishTime;
    public final boolean initialSticky;
    public final Intent intent;
    public final boolean interactive;
    public final BackgroundStartPrivileges mBackgroundStartPrivileges;
    public String mCachedToShortString;
    public String mCachedToString;
    public boolean mIsReceiverAppRunning;
    public boolean mWasReceiverAppStopped;
    public int manifestCount;
    public int manifestSkipCount;
    public int nextReceiver;
    public final BroadcastOptions options;
    public final boolean ordered;
    public long originalEnqueueClockTime;
    public final boolean prioritized;
    public final boolean pushMessage;
    public final boolean pushMessageOverQuota;
    public BroadcastQueue queue;
    public long receiverTime;
    public final List<Object> receivers;
    public final String[] requiredPermissions;
    public final String resolvedType;
    public boolean resultAbort;
    public int resultCode;
    public String resultData;
    public Bundle resultExtras;
    public IIntentReceiver resultTo;
    public ProcessRecord resultToApp;
    public final long[] scheduledTime;
    public final boolean shareIdentity;
    public int splitToken;
    public int state;
    public final boolean sticky;
    public final ComponentName targetComp;
    public int terminalCount;
    public final long[] terminalTime;
    public final boolean timeoutExempt;
    public final int userId;

    public static boolean isDeliveryStateTerminal(int i) {
        return i == 1 || i == 2 || i == 3 || i == 5;
    }

    public static String deliveryStateToString(int i) {
        switch (i) {
            case 0:
                return "PENDING";
            case 1:
                return "DELIVERED";
            case 2:
                return "SKIPPED";
            case 3:
                return "TIMEOUT";
            case 4:
                return "SCHEDULED";
            case 5:
                return "FAILURE";
            case 6:
                return "DEFERRED";
            default:
                return Integer.toString(i);
        }
    }

    @NeverCompile
    public void dump(PrintWriter printWriter, String str, SimpleDateFormat simpleDateFormat) {
        long uptimeMillis = SystemClock.uptimeMillis();
        printWriter.print(str);
        printWriter.print(this);
        printWriter.print(" to user ");
        printWriter.println(this.userId);
        printWriter.print(str);
        printWriter.println(this.intent.toInsecureString());
        ComponentName componentName = this.targetComp;
        if (componentName != null && componentName != this.intent.getComponent()) {
            printWriter.print(str);
            printWriter.print("  targetComp: ");
            printWriter.println(this.targetComp.toShortString());
        }
        Bundle extras = this.intent.getExtras();
        if (extras != null) {
            printWriter.print(str);
            printWriter.print("  extras: ");
            printWriter.println(extras.toString());
        }
        printWriter.print(str);
        printWriter.print("caller=");
        printWriter.print(this.callerPackage);
        printWriter.print(" ");
        ProcessRecord processRecord = this.callerApp;
        printWriter.print(processRecord != null ? processRecord.toShortString() : "null");
        printWriter.print(" pid=");
        printWriter.print(this.callingPid);
        printWriter.print(" uid=");
        printWriter.println(this.callingUid);
        String[] strArr = this.requiredPermissions;
        if ((strArr != null && strArr.length > 0) || this.appOp != -1) {
            printWriter.print(str);
            printWriter.print("requiredPermissions=");
            printWriter.print(Arrays.toString(this.requiredPermissions));
            printWriter.print("  appOp=");
            printWriter.println(this.appOp);
        }
        String[] strArr2 = this.excludedPermissions;
        if (strArr2 != null && strArr2.length > 0) {
            printWriter.print(str);
            printWriter.print("excludedPermissions=");
            printWriter.print(Arrays.toString(this.excludedPermissions));
        }
        String[] strArr3 = this.excludedPackages;
        if (strArr3 != null && strArr3.length > 0) {
            printWriter.print(str);
            printWriter.print("excludedPackages=");
            printWriter.print(Arrays.toString(this.excludedPackages));
        }
        if (this.options != null) {
            printWriter.print(str);
            printWriter.print("options=");
            printWriter.println(this.options.toBundle());
        }
        printWriter.print(str);
        printWriter.print("enqueueClockTime=");
        printWriter.print(simpleDateFormat.format(new Date(this.enqueueClockTime)));
        printWriter.print(" dispatchClockTime=");
        printWriter.print(simpleDateFormat.format(new Date(this.dispatchClockTime)));
        if (this.originalEnqueueClockTime > 0) {
            printWriter.print(" originalEnqueueClockTime=");
            printWriter.print(simpleDateFormat.format(new Date(this.originalEnqueueClockTime)));
        }
        printWriter.println();
        printWriter.print(str);
        printWriter.print("dispatchTime=");
        TimeUtils.formatDuration(this.dispatchTime, uptimeMillis, printWriter);
        printWriter.print(" (");
        TimeUtils.formatDuration(this.dispatchTime - this.enqueueTime, printWriter);
        printWriter.print(" since enq)");
        if (this.finishTime != 0) {
            printWriter.print(" finishTime=");
            TimeUtils.formatDuration(this.finishTime, uptimeMillis, printWriter);
            printWriter.print(" (");
            TimeUtils.formatDuration(this.finishTime - this.dispatchTime, printWriter);
            printWriter.print(" since disp)");
        } else {
            printWriter.print(" receiverTime=");
            TimeUtils.formatDuration(this.receiverTime, uptimeMillis, printWriter);
        }
        printWriter.println("");
        if (this.anrCount != 0) {
            printWriter.print(str);
            printWriter.print("anrCount=");
            printWriter.println(this.anrCount);
        }
        if (this.resultTo != null || this.resultCode != -1 || this.resultData != null) {
            printWriter.print(str);
            printWriter.print("resultTo=");
            printWriter.print(this.resultTo);
            printWriter.print(" resultCode=");
            printWriter.print(this.resultCode);
            printWriter.print(" resultData=");
            printWriter.println(this.resultData);
        }
        if (this.resultExtras != null) {
            printWriter.print(str);
            printWriter.print("resultExtras=");
            printWriter.println(this.resultExtras);
        }
        if (this.resultAbort || this.ordered || this.sticky || this.initialSticky) {
            printWriter.print(str);
            printWriter.print("resultAbort=");
            printWriter.print(this.resultAbort);
            printWriter.print(" ordered=");
            printWriter.print(this.ordered);
            printWriter.print(" sticky=");
            printWriter.print(this.sticky);
            printWriter.print(" initialSticky=");
            printWriter.println(this.initialSticky);
        }
        if (this.nextReceiver != 0) {
            printWriter.print(str);
            printWriter.print("nextReceiver=");
            printWriter.println(this.nextReceiver);
        }
        if (this.curFilter != null) {
            printWriter.print(str);
            printWriter.print("curFilter=");
            printWriter.println(this.curFilter);
        }
        if (this.curReceiver != null) {
            printWriter.print(str);
            printWriter.print("curReceiver=");
            printWriter.println(this.curReceiver);
        }
        if (this.curApp != null) {
            printWriter.print(str);
            printWriter.print("curApp=");
            printWriter.println(this.curApp);
            printWriter.print(str);
            printWriter.print("curComponent=");
            ComponentName componentName2 = this.curComponent;
            printWriter.println(componentName2 != null ? componentName2.toShortString() : "--");
            ActivityInfo activityInfo = this.curReceiver;
            if (activityInfo != null && activityInfo.applicationInfo != null) {
                printWriter.print(str);
                printWriter.print("curSourceDir=");
                printWriter.println(this.curReceiver.applicationInfo.sourceDir);
            }
        }
        if (this.curFilteredExtras != null) {
            printWriter.print(" filtered extras: ");
            printWriter.println(this.curFilteredExtras);
        }
        int i = this.state;
        if (i != 0) {
            String str2 = i != 1 ? i != 2 ? i != 3 ? i != 4 ? " (?)" : " (WAITING_SERVICES)" : " (CALL_DONE_RECEIVE)" : " (CALL_IN_RECEIVE)" : " (APP_RECEIVE)";
            printWriter.print(str);
            printWriter.print("state=");
            printWriter.print(this.state);
            printWriter.println(str2);
        }
        printWriter.print(str);
        printWriter.print("terminalCount=");
        printWriter.println(this.terminalCount);
        List<Object> list = this.receivers;
        int size = list != null ? list.size() : 0;
        String str3 = str + "  ";
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        for (int i2 = 0; i2 < size; i2++) {
            Object obj = this.receivers.get(i2);
            printWriter.print(str);
            printWriter.print(deliveryStateToString(this.delivery[i2]));
            printWriter.print(' ');
            if (this.scheduledTime[i2] != 0) {
                printWriter.print("scheduled ");
                TimeUtils.formatDuration(this.scheduledTime[i2] - this.enqueueTime, printWriter);
                printWriter.print(' ');
            }
            if (this.terminalTime[i2] != 0) {
                printWriter.print("terminal ");
                TimeUtils.formatDuration(this.terminalTime[i2] - this.scheduledTime[i2], printWriter);
                printWriter.print(' ');
            }
            printWriter.print("(");
            printWriter.print(this.blockedUntilTerminalCount[i2]);
            printWriter.print(") ");
            printWriter.print("#");
            printWriter.print(i2);
            printWriter.print(": ");
            if (obj instanceof BroadcastFilter) {
                printWriter.println(obj);
                ((BroadcastFilter) obj).dumpBrief(printWriter, str3);
            } else if (obj instanceof ResolveInfo) {
                printWriter.println("(manifest)");
                ((ResolveInfo) obj).dump(printWriterPrinter, str3, 0);
            } else {
                printWriter.println(obj);
            }
            if (this.deliveryReasons[i2] != null) {
                printWriter.print(str3);
                printWriter.print("reason: ");
                printWriter.println(this.deliveryReasons[i2]);
            }
        }
    }

    public BroadcastRecord(BroadcastQueue broadcastQueue, Intent intent, ProcessRecord processRecord, String str, String str2, int i, int i2, boolean z, String str3, String[] strArr, String[] strArr2, String[] strArr3, int i3, BroadcastOptions broadcastOptions, List list, ProcessRecord processRecord2, IIntentReceiver iIntentReceiver, int i4, String str4, Bundle bundle, boolean z2, boolean z3, boolean z4, int i5, BackgroundStartPrivileges backgroundStartPrivileges, boolean z5, BiFunction<Integer, Bundle, Bundle> biFunction) {
        if (intent == null) {
            throw new NullPointerException("Can't construct with a null intent");
        }
        this.queue = broadcastQueue;
        this.intent = intent;
        this.targetComp = intent.getComponent();
        this.callerApp = processRecord;
        this.callerPackage = str;
        this.callerFeatureId = str2;
        this.callingPid = i;
        this.callingUid = i2;
        this.callerInstantApp = z;
        this.callerInstrumented = isCallerInstrumented(processRecord, i2);
        this.resolvedType = str3;
        this.requiredPermissions = strArr;
        this.excludedPermissions = strArr2;
        this.excludedPackages = strArr3;
        this.appOp = i3;
        this.options = broadcastOptions;
        List list2 = list != null ? list : EMPTY_RECEIVERS;
        this.receivers = list2;
        boolean z6 = false;
        int[] iArr = new int[list != null ? list.size() : 0];
        this.delivery = iArr;
        this.deliveryReasons = new String[iArr.length];
        boolean isDeferUntilActive = broadcastOptions != null ? broadcastOptions.isDeferUntilActive() : false;
        this.deferUntilActive = isDeferUntilActive;
        this.deferredUntilActive = new boolean[isDeferUntilActive ? iArr.length : 0];
        int[] calculateBlockedUntilTerminalCount = calculateBlockedUntilTerminalCount(list2, z2);
        this.blockedUntilTerminalCount = calculateBlockedUntilTerminalCount;
        this.scheduledTime = new long[iArr.length];
        this.terminalTime = new long[iArr.length];
        this.resultToApp = processRecord2;
        this.resultTo = iIntentReceiver;
        this.resultCode = i4;
        this.resultData = str4;
        this.resultExtras = bundle;
        this.ordered = z2;
        this.sticky = z3;
        this.initialSticky = z4;
        this.prioritized = isPrioritized(calculateBlockedUntilTerminalCount, z2);
        this.userId = i5;
        this.nextReceiver = 0;
        this.state = 0;
        this.mBackgroundStartPrivileges = backgroundStartPrivileges;
        this.timeoutExempt = z5;
        this.alarm = broadcastOptions != null && broadcastOptions.isAlarmBroadcast();
        this.pushMessage = broadcastOptions != null && broadcastOptions.isPushMessagingBroadcast();
        this.pushMessageOverQuota = broadcastOptions != null && broadcastOptions.isPushMessagingOverQuotaBroadcast();
        this.interactive = broadcastOptions != null && broadcastOptions.isInteractive();
        if (broadcastOptions != null && broadcastOptions.isShareIdentityEnabled()) {
            z6 = true;
        }
        this.shareIdentity = z6;
        this.filterExtrasForReceiver = biFunction;
    }

    public BroadcastRecord(BroadcastRecord broadcastRecord, Intent intent) {
        Objects.requireNonNull(intent);
        this.intent = intent;
        this.targetComp = intent.getComponent();
        this.callerApp = broadcastRecord.callerApp;
        this.callerPackage = broadcastRecord.callerPackage;
        this.callerFeatureId = broadcastRecord.callerFeatureId;
        this.callingPid = broadcastRecord.callingPid;
        this.callingUid = broadcastRecord.callingUid;
        this.callerInstantApp = broadcastRecord.callerInstantApp;
        this.callerInstrumented = broadcastRecord.callerInstrumented;
        this.ordered = broadcastRecord.ordered;
        this.sticky = broadcastRecord.sticky;
        this.initialSticky = broadcastRecord.initialSticky;
        this.prioritized = broadcastRecord.prioritized;
        this.userId = broadcastRecord.userId;
        this.resolvedType = broadcastRecord.resolvedType;
        this.requiredPermissions = broadcastRecord.requiredPermissions;
        this.excludedPermissions = broadcastRecord.excludedPermissions;
        this.excludedPackages = broadcastRecord.excludedPackages;
        this.appOp = broadcastRecord.appOp;
        this.options = broadcastRecord.options;
        this.receivers = broadcastRecord.receivers;
        this.delivery = broadcastRecord.delivery;
        this.deliveryReasons = broadcastRecord.deliveryReasons;
        this.deferUntilActive = broadcastRecord.deferUntilActive;
        this.deferredUntilActive = broadcastRecord.deferredUntilActive;
        this.blockedUntilTerminalCount = broadcastRecord.blockedUntilTerminalCount;
        this.scheduledTime = broadcastRecord.scheduledTime;
        this.terminalTime = broadcastRecord.terminalTime;
        this.resultToApp = broadcastRecord.resultToApp;
        this.resultTo = broadcastRecord.resultTo;
        this.enqueueTime = broadcastRecord.enqueueTime;
        this.enqueueRealTime = broadcastRecord.enqueueRealTime;
        this.enqueueClockTime = broadcastRecord.enqueueClockTime;
        this.dispatchTime = broadcastRecord.dispatchTime;
        this.dispatchRealTime = broadcastRecord.dispatchRealTime;
        this.dispatchClockTime = broadcastRecord.dispatchClockTime;
        this.receiverTime = broadcastRecord.receiverTime;
        this.finishTime = broadcastRecord.finishTime;
        this.resultCode = broadcastRecord.resultCode;
        this.resultData = broadcastRecord.resultData;
        this.resultExtras = broadcastRecord.resultExtras;
        this.resultAbort = broadcastRecord.resultAbort;
        this.nextReceiver = broadcastRecord.nextReceiver;
        this.state = broadcastRecord.state;
        this.anrCount = broadcastRecord.anrCount;
        this.manifestCount = broadcastRecord.manifestCount;
        this.manifestSkipCount = broadcastRecord.manifestSkipCount;
        this.queue = broadcastRecord.queue;
        this.mBackgroundStartPrivileges = broadcastRecord.mBackgroundStartPrivileges;
        this.timeoutExempt = broadcastRecord.timeoutExempt;
        this.alarm = broadcastRecord.alarm;
        this.pushMessage = broadcastRecord.pushMessage;
        this.pushMessageOverQuota = broadcastRecord.pushMessageOverQuota;
        this.interactive = broadcastRecord.interactive;
        this.shareIdentity = broadcastRecord.shareIdentity;
        this.filterExtrasForReceiver = broadcastRecord.filterExtrasForReceiver;
    }

    public BroadcastRecord splitRecipientsLocked(int i, int i2) {
        int i3 = i2;
        ArrayList arrayList = null;
        while (i3 < this.receivers.size()) {
            Object obj = this.receivers.get(i3);
            if (getReceiverUid(obj) == i) {
                ArrayList arrayList2 = arrayList == null ? new ArrayList() : arrayList;
                arrayList2.add(obj);
                this.receivers.remove(i3);
                arrayList = arrayList2;
            } else {
                i3++;
            }
        }
        if (arrayList == null) {
            return null;
        }
        BroadcastRecord broadcastRecord = new BroadcastRecord(this.queue, this.intent, this.callerApp, this.callerPackage, this.callerFeatureId, this.callingPid, this.callingUid, this.callerInstantApp, this.resolvedType, this.requiredPermissions, this.excludedPermissions, this.excludedPackages, this.appOp, this.options, arrayList, this.resultToApp, this.resultTo, this.resultCode, this.resultData, this.resultExtras, this.ordered, this.sticky, this.initialSticky, this.userId, this.mBackgroundStartPrivileges, this.timeoutExempt, this.filterExtrasForReceiver);
        broadcastRecord.enqueueTime = this.enqueueTime;
        broadcastRecord.enqueueRealTime = this.enqueueRealTime;
        broadcastRecord.enqueueClockTime = this.enqueueClockTime;
        broadcastRecord.splitToken = this.splitToken;
        return broadcastRecord;
    }

    public SparseArray<BroadcastRecord> splitDeferredBootCompletedBroadcastLocked(ActivityManagerInternal activityManagerInternal, int i) {
        int i2;
        SparseArray<BroadcastRecord> sparseArray = new SparseArray<>();
        if (i == 0 || this.receivers == null) {
            return sparseArray;
        }
        String action = this.intent.getAction();
        if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(action) || "android.intent.action.BOOT_COMPLETED".equals(action)) {
            SparseArray sparseArray2 = new SparseArray();
            int size = this.receivers.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                Object obj = this.receivers.get(size);
                int receiverUid = getReceiverUid(obj);
                if (i != 1) {
                    if ((i & 2) != 0 && activityManagerInternal.getRestrictionLevel(receiverUid) < 50) {
                        size--;
                    }
                    if ((i & 4) != 0 && !CompatChanges.isChangeEnabled(203704822L, receiverUid)) {
                        size--;
                    }
                }
                this.receivers.remove(size);
                List list = (List) sparseArray2.get(receiverUid);
                if (list != null) {
                    list.add(0, obj);
                } else {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(0, obj);
                    sparseArray2.put(receiverUid, arrayList);
                }
                size--;
            }
            int size2 = sparseArray2.size();
            for (i2 = 0; i2 < size2; i2++) {
                BroadcastRecord broadcastRecord = new BroadcastRecord(this.queue, this.intent, this.callerApp, this.callerPackage, this.callerFeatureId, this.callingPid, this.callingUid, this.callerInstantApp, this.resolvedType, this.requiredPermissions, this.excludedPermissions, this.excludedPackages, this.appOp, this.options, (List) sparseArray2.valueAt(i2), null, null, this.resultCode, this.resultData, this.resultExtras, this.ordered, this.sticky, this.initialSticky, this.userId, this.mBackgroundStartPrivileges, this.timeoutExempt, this.filterExtrasForReceiver);
                broadcastRecord.enqueueTime = this.enqueueTime;
                broadcastRecord.enqueueRealTime = this.enqueueRealTime;
                broadcastRecord.enqueueClockTime = this.enqueueClockTime;
                sparseArray.put(sparseArray2.keyAt(i2), broadcastRecord);
            }
            return sparseArray;
        }
        return sparseArray;
    }

    public void setDeliveryState(int i, int i2, String str) {
        this.delivery[i] = i2;
        this.deliveryReasons[i] = str;
        boolean z = this.deferUntilActive;
        if (z) {
            this.deferredUntilActive[i] = false;
        }
        switch (i2) {
            case 1:
            case 2:
            case 3:
            case 5:
                this.terminalTime[i] = SystemClock.uptimeMillis();
                return;
            case 4:
                this.scheduledTime[i] = SystemClock.uptimeMillis();
                return;
            case 6:
                if (z) {
                    this.deferredUntilActive[i] = true;
                    return;
                }
                return;
            default:
                return;
        }
    }

    public int getDeliveryState(int i) {
        return this.delivery[i];
    }

    public boolean wasDeliveryAttempted(int i) {
        int deliveryState = getDeliveryState(i);
        return deliveryState == 1 || deliveryState == 3 || deliveryState == 5;
    }

    public void copyEnqueueTimeFrom(BroadcastRecord broadcastRecord) {
        this.originalEnqueueClockTime = this.enqueueClockTime;
        this.enqueueTime = broadcastRecord.enqueueTime;
        this.enqueueRealTime = broadcastRecord.enqueueRealTime;
        this.enqueueClockTime = broadcastRecord.enqueueClockTime;
    }

    public boolean isForeground() {
        return (this.intent.getFlags() & 268435456) != 0;
    }

    public boolean isReplacePending() {
        return (this.intent.getFlags() & 536870912) != 0;
    }

    public boolean isNoAbort() {
        return (this.intent.getFlags() & 134217728) != 0;
    }

    public boolean isOffload() {
        return (this.intent.getFlags() & Integer.MIN_VALUE) != 0;
    }

    public boolean isDeferUntilActive() {
        return this.deferUntilActive;
    }

    public boolean isUrgent() {
        return isForeground() || this.interactive || this.alarm;
    }

    public String getHostingRecordTriggerType() {
        return this.alarm ? "alarm" : this.pushMessage ? "push_message" : this.pushMessageOverQuota ? "push_message_over_quota" : "unknown";
    }

    public Intent getReceiverIntent(Object obj) {
        Bundle extras;
        Intent intent = null;
        if (this.filterExtrasForReceiver != null && (extras = this.intent.getExtras()) != null) {
            Bundle apply = this.filterExtrasForReceiver.apply(Integer.valueOf(getReceiverUid(obj)), extras);
            if (apply == null) {
                return null;
            }
            intent = new Intent(this.intent);
            intent.replaceExtras(apply);
        }
        if (obj instanceof ResolveInfo) {
            if (intent == null) {
                intent = new Intent(this.intent);
            }
            intent.setComponent(((ResolveInfo) obj).activityInfo.getComponentName());
        }
        return intent != null ? intent : this.intent;
    }

    public static boolean isCallerInstrumented(ProcessRecord processRecord, int i) {
        int appId = UserHandle.getAppId(i);
        if (appId == 0 || appId == 2000) {
            return true;
        }
        return (processRecord == null || processRecord.getActiveInstrumentation() == null) ? false : true;
    }

    @VisibleForTesting
    public static boolean isPrioritized(int[] iArr, boolean z) {
        return (z || iArr.length <= 0 || iArr[0] == -1) ? false : true;
    }

    @VisibleForTesting
    public static int[] calculateBlockedUntilTerminalCount(List<Object> list, boolean z) {
        int size = list.size();
        int[] iArr = new int[size];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (z) {
                iArr[i3] = i3;
            } else {
                int receiverPriority = getReceiverPriority(list.get(i3));
                if (i3 == 0 || receiverPriority != i) {
                    iArr[i3] = i3;
                    i2 = i3;
                    i = receiverPriority;
                } else {
                    iArr[i3] = i2;
                }
            }
        }
        if (size > 0 && iArr[size - 1] == 0) {
            Arrays.fill(iArr, -1);
        }
        return iArr;
    }

    public static int getReceiverUid(Object obj) {
        if (obj instanceof BroadcastFilter) {
            return ((BroadcastFilter) obj).owningUid;
        }
        return ((ResolveInfo) obj).activityInfo.applicationInfo.uid;
    }

    public static String getReceiverProcessName(Object obj) {
        if (obj instanceof BroadcastFilter) {
            return ((BroadcastFilter) obj).receiverList.app.processName;
        }
        return ((ResolveInfo) obj).activityInfo.processName;
    }

    public static String getReceiverPackageName(Object obj) {
        if (obj instanceof BroadcastFilter) {
            return ((BroadcastFilter) obj).receiverList.app.info.packageName;
        }
        return ((ResolveInfo) obj).activityInfo.packageName;
    }

    public static int getReceiverPriority(Object obj) {
        if (obj instanceof BroadcastFilter) {
            return ((BroadcastFilter) obj).getPriority();
        }
        return ((ResolveInfo) obj).priority;
    }

    public static boolean isReceiverEquals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if ((obj instanceof ResolveInfo) && (obj2 instanceof ResolveInfo)) {
            ResolveInfo resolveInfo = (ResolveInfo) obj;
            ResolveInfo resolveInfo2 = (ResolveInfo) obj2;
            return Objects.equals(resolveInfo.activityInfo.packageName, resolveInfo2.activityInfo.packageName) && Objects.equals(resolveInfo.activityInfo.name, resolveInfo2.activityInfo.name);
        }
        return false;
    }

    public BroadcastRecord maybeStripForHistory() {
        return !this.intent.canStripForHistory() ? this : new BroadcastRecord(this, this.intent.maybeStripForHistory());
    }

    @VisibleForTesting
    public boolean cleanupDisabledPackageReceiversLocked(String str, Set<String> set, int i, boolean z) {
        List<Object> list = this.receivers;
        if (list == null) {
            return false;
        }
        boolean z2 = i == -1;
        int i2 = this.userId;
        boolean z3 = i2 == -1;
        if (i2 == i || z2 || z3) {
            boolean z4 = false;
            for (int size = list.size() - 1; size >= 0; size--) {
                Object obj = this.receivers.get(size);
                if (obj instanceof ResolveInfo) {
                    ActivityInfo activityInfo = ((ResolveInfo) obj).activityInfo;
                    if ((str == null || (activityInfo.applicationInfo.packageName.equals(str) && (set == null || set.contains(activityInfo.name)))) && (z2 || UserHandle.getUserId(activityInfo.applicationInfo.uid) == i)) {
                        if (!z) {
                            return true;
                        }
                        this.receivers.remove(size);
                        int i3 = this.nextReceiver;
                        if (size < i3) {
                            this.nextReceiver = i3 - 1;
                        }
                        z4 = true;
                    }
                }
            }
            this.nextReceiver = Math.min(this.nextReceiver, this.receivers.size());
            return z4;
        }
        return false;
    }

    public void applySingletonPolicy(ActivityManagerService activityManagerService) {
        boolean z;
        if (this.receivers == null) {
            return;
        }
        for (int i = 0; i < this.receivers.size(); i++) {
            Object obj = this.receivers.get(i);
            if (obj instanceof ResolveInfo) {
                ResolveInfo resolveInfo = (ResolveInfo) obj;
                try {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    z = activityManagerService.isSingleton(activityInfo.processName, activityInfo.applicationInfo, activityInfo.name, activityInfo.flags);
                } catch (SecurityException e) {
                    BroadcastQueue.logw(e.getMessage());
                    z = false;
                }
                int i2 = resolveInfo.activityInfo.applicationInfo.uid;
                int i3 = this.callingUid;
                if (i3 != 1000 && z && activityManagerService.isValidSingletonCall(i3, i2)) {
                    resolveInfo.activityInfo = activityManagerService.getActivityInfoForUser(resolveInfo.activityInfo, 0);
                }
            }
        }
    }

    public boolean matchesDeliveryGroup(BroadcastRecord broadcastRecord) {
        return matchesDeliveryGroup(this, broadcastRecord);
    }

    public static boolean matchesDeliveryGroup(BroadcastRecord broadcastRecord, BroadcastRecord broadcastRecord2) {
        String deliveryGroupMatchingKey = getDeliveryGroupMatchingKey(broadcastRecord);
        String deliveryGroupMatchingKey2 = getDeliveryGroupMatchingKey(broadcastRecord2);
        IntentFilter deliveryGroupMatchingFilter = getDeliveryGroupMatchingFilter(broadcastRecord);
        if (deliveryGroupMatchingKey == null && deliveryGroupMatchingKey2 == null && deliveryGroupMatchingFilter == null) {
            return broadcastRecord.intent.filterEquals(broadcastRecord2.intent);
        }
        if (deliveryGroupMatchingFilter == null || deliveryGroupMatchingFilter.asPredicate().test(broadcastRecord2.intent)) {
            return Objects.equals(deliveryGroupMatchingKey, deliveryGroupMatchingKey2);
        }
        return false;
    }

    public static String getDeliveryGroupMatchingKey(BroadcastRecord broadcastRecord) {
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        if (broadcastOptions == null) {
            return null;
        }
        return broadcastOptions.getDeliveryGroupMatchingKey();
    }

    public static IntentFilter getDeliveryGroupMatchingFilter(BroadcastRecord broadcastRecord) {
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        if (broadcastOptions == null) {
            return null;
        }
        return broadcastOptions.getDeliveryGroupMatchingFilter();
    }

    public boolean allReceiversPending() {
        return this.terminalCount == 0 && this.dispatchTime <= 0;
    }

    public String toString() {
        if (this.mCachedToString == null) {
            String action = this.intent.getAction();
            if (action == null) {
                action = this.intent.toString();
            }
            this.mCachedToString = "BroadcastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " u" + this.userId + " " + action + "}";
        }
        return this.mCachedToString;
    }

    public String toShortString() {
        if (this.mCachedToShortString == null) {
            String action = this.intent.getAction();
            if (action == null) {
                action = this.intent.toString();
            }
            this.mCachedToShortString = Integer.toHexString(System.identityHashCode(this)) + XmlUtils.STRING_ARRAY_SEPARATOR + action + "/u" + this.userId;
        }
        return this.mCachedToShortString;
    }

    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.userId);
        protoOutputStream.write(1138166333442L, this.intent.getAction());
        protoOutputStream.end(start);
    }
}
