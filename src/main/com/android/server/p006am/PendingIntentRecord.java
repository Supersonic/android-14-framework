package com.android.server.p006am;

import android.annotation.RequiresPermission;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.BackgroundStartPrivileges;
import android.app.BroadcastOptions;
import android.app.IApplicationThread;
import android.app.compat.CompatChanges;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerWhitelistManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p014wm.SafeActivityOptions;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.PendingIntentRecord */
/* loaded from: classes.dex */
public final class PendingIntentRecord extends IIntentSender.Stub {
    public final PendingIntentController controller;
    public final Key key;
    public String lastTag;
    public String lastTagPrefix;
    public ArrayMap<IBinder, TempAllowListDuration> mAllowlistDuration;
    public RemoteCallbackList<IResultReceiver> mCancelCallbacks;
    public String stringName;
    public final int uid;
    public boolean sent = false;
    public boolean canceled = false;
    public ArraySet<IBinder> mAllowBgActivityStartsForActivitySender = new ArraySet<>();
    public ArraySet<IBinder> mAllowBgActivityStartsForBroadcastSender = new ArraySet<>();
    public ArraySet<IBinder> mAllowBgActivityStartsForServiceSender = new ArraySet<>();
    public final WeakReference<PendingIntentRecord> ref = new WeakReference<>(this);

    /* renamed from: com.android.server.am.PendingIntentRecord$Key */
    /* loaded from: classes.dex */
    public static final class Key {
        public final IBinder activity;
        public Intent[] allIntents;
        public String[] allResolvedTypes;
        public final String featureId;
        public final int flags;
        public final int hashCode;
        public final SafeActivityOptions options;
        public final String packageName;
        public final int requestCode;
        public final Intent requestIntent;
        public final String requestResolvedType;
        public final int type;
        public final int userId;
        public final String who;

        public Key(int i, String str, String str2, IBinder iBinder, String str3, int i2, Intent[] intentArr, String[] strArr, int i3, SafeActivityOptions safeActivityOptions, int i4) {
            this.type = i;
            this.packageName = str;
            this.featureId = str2;
            this.activity = iBinder;
            this.who = str3;
            this.requestCode = i2;
            Intent intent = intentArr != null ? intentArr[intentArr.length - 1] : null;
            this.requestIntent = intent;
            String str4 = strArr != null ? strArr[strArr.length - 1] : null;
            this.requestResolvedType = str4;
            this.allIntents = intentArr;
            this.allResolvedTypes = strArr;
            this.flags = i3;
            this.options = safeActivityOptions;
            this.userId = i4;
            int i5 = ((((851 + i3) * 37) + i2) * 37) + i4;
            i5 = str3 != null ? (i5 * 37) + str3.hashCode() : i5;
            i5 = iBinder != null ? (i5 * 37) + iBinder.hashCode() : i5;
            i5 = intent != null ? (i5 * 37) + intent.filterHashCode() : i5;
            this.hashCode = ((((str4 != null ? (i5 * 37) + str4.hashCode() : i5) * 37) + (str != null ? str.hashCode() : 0)) * 37) + i;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            try {
                Key key = (Key) obj;
                if (this.type == key.type && this.userId == key.userId && Objects.equals(this.packageName, key.packageName) && Objects.equals(this.featureId, key.featureId) && this.activity == key.activity && Objects.equals(this.who, key.who) && this.requestCode == key.requestCode) {
                    Intent intent = this.requestIntent;
                    Intent intent2 = key.requestIntent;
                    if (intent != intent2) {
                        if (intent != null) {
                            if (!intent.filterEquals(intent2)) {
                                return false;
                            }
                        } else if (intent2 != null) {
                            return false;
                        }
                    }
                    if (Objects.equals(this.requestResolvedType, key.requestResolvedType)) {
                        return this.flags == key.flags;
                    }
                    return false;
                }
                return false;
            } catch (ClassCastException unused) {
                return false;
            }
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("Key{");
            sb.append(typeName());
            sb.append(" pkg=");
            sb.append(this.packageName);
            if (this.featureId != null) {
                str = "/" + this.featureId;
            } else {
                str = "";
            }
            sb.append(str);
            sb.append(" intent=");
            Intent intent = this.requestIntent;
            sb.append(intent != null ? intent.toShortString(false, true, false, false) : "<null>");
            sb.append(" flags=0x");
            sb.append(Integer.toHexString(this.flags));
            sb.append(" u=");
            sb.append(this.userId);
            sb.append("} requestCode=");
            sb.append(this.requestCode);
            return sb.toString();
        }

        public String typeName() {
            int i = this.type;
            return i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? Integer.toString(i) : "startForegroundService" : "startService" : "activityResult" : "startActivity" : "broadcastIntent";
        }
    }

    /* renamed from: com.android.server.am.PendingIntentRecord$TempAllowListDuration */
    /* loaded from: classes.dex */
    public static final class TempAllowListDuration {
        public long duration;
        public String reason;
        public int reasonCode;
        public int type;

        public TempAllowListDuration(long j, int i, int i2, String str) {
            this.duration = j;
            this.type = i;
            this.reasonCode = i2;
            this.reason = str;
        }
    }

    public PendingIntentRecord(PendingIntentController pendingIntentController, Key key, int i) {
        this.controller = pendingIntentController;
        this.key = key;
        this.uid = i;
    }

    public void setAllowlistDurationLocked(IBinder iBinder, long j, int i, int i2, String str) {
        if (j > 0) {
            if (this.mAllowlistDuration == null) {
                this.mAllowlistDuration = new ArrayMap<>();
            }
            this.mAllowlistDuration.put(iBinder, new TempAllowListDuration(j, i, i2, str));
        } else {
            ArrayMap<IBinder, TempAllowListDuration> arrayMap = this.mAllowlistDuration;
            if (arrayMap != null) {
                arrayMap.remove(iBinder);
                if (this.mAllowlistDuration.size() <= 0) {
                    this.mAllowlistDuration = null;
                }
            }
        }
        this.stringName = null;
    }

    public void setAllowBgActivityStarts(IBinder iBinder, int i) {
        if (iBinder == null) {
            return;
        }
        if ((i & 1) != 0) {
            this.mAllowBgActivityStartsForActivitySender.add(iBinder);
        }
        if ((i & 2) != 0) {
            this.mAllowBgActivityStartsForBroadcastSender.add(iBinder);
        }
        if ((i & 4) != 0) {
            this.mAllowBgActivityStartsForServiceSender.add(iBinder);
        }
    }

    public void clearAllowBgActivityStarts(IBinder iBinder) {
        if (iBinder == null) {
            return;
        }
        this.mAllowBgActivityStartsForActivitySender.remove(iBinder);
        this.mAllowBgActivityStartsForBroadcastSender.remove(iBinder);
        this.mAllowBgActivityStartsForServiceSender.remove(iBinder);
    }

    public void registerCancelListenerLocked(IResultReceiver iResultReceiver) {
        if (this.mCancelCallbacks == null) {
            this.mCancelCallbacks = new RemoteCallbackList<>();
        }
        this.mCancelCallbacks.register(iResultReceiver);
    }

    public void unregisterCancelListenerLocked(IResultReceiver iResultReceiver) {
        RemoteCallbackList<IResultReceiver> remoteCallbackList = this.mCancelCallbacks;
        if (remoteCallbackList == null) {
            return;
        }
        remoteCallbackList.unregister(iResultReceiver);
        if (this.mCancelCallbacks.getRegisteredCallbackCount() <= 0) {
            this.mCancelCallbacks = null;
        }
    }

    public RemoteCallbackList<IResultReceiver> detachCancelListenersLocked() {
        RemoteCallbackList<IResultReceiver> remoteCallbackList = this.mCancelCallbacks;
        this.mCancelCallbacks = null;
        return remoteCallbackList;
    }

    public void send(int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
        sendInner(null, i, intent, str, iBinder, iIntentReceiver, str2, null, null, 0, 0, 0, bundle);
    }

    public int sendWithResult(IApplicationThread iApplicationThread, int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
        return sendInner(iApplicationThread, i, intent, str, iBinder, iIntentReceiver, str2, null, null, 0, 0, 0, bundle);
    }

    public static boolean isPendingIntentBalAllowedByPermission(ActivityOptions activityOptions) {
        if (activityOptions == null) {
            return false;
        }
        return activityOptions.isPendingIntentBackgroundActivityLaunchAllowedByPermission();
    }

    public static BackgroundStartPrivileges getBackgroundStartPrivilegesAllowedByCaller(ActivityOptions activityOptions, int i) {
        if (activityOptions == null) {
            return getDefaultBackgroundStartPrivileges(i);
        }
        return getBackgroundStartPrivilegesAllowedByCaller(activityOptions.toBundle(), i);
    }

    public static BackgroundStartPrivileges getBackgroundStartPrivilegesAllowedByCaller(Bundle bundle, int i) {
        if (bundle == null || !bundle.containsKey("android.pendingIntent.backgroundActivityAllowed")) {
            return getDefaultBackgroundStartPrivileges(i);
        }
        if (bundle.getBoolean("android.pendingIntent.backgroundActivityAllowed")) {
            return BackgroundStartPrivileges.ALLOW_BAL;
        }
        return BackgroundStartPrivileges.NONE;
    }

    public static boolean isDefaultRescindBalPrivilegesFromPendingIntentSenderEnabled() {
        return DeviceConfig.getBoolean("window_manager", "DefaultRescindBalPrivilegesFromPendingIntentSender__enable_default_rescind_bal_privileges_from_pending_intent_sender", false);
    }

    @RequiresPermission(allOf = {"android.permission.READ_COMPAT_CHANGE_CONFIG", "android.permission.LOG_COMPAT_CHANGE"})
    public static BackgroundStartPrivileges getDefaultBackgroundStartPrivileges(int i) {
        boolean isDefaultRescindBalPrivilegesFromPendingIntentSenderEnabled = isDefaultRescindBalPrivilegesFromPendingIntentSenderEnabled();
        boolean isChangeEnabled = CompatChanges.isChangeEnabled(244637991L, i);
        if (isDefaultRescindBalPrivilegesFromPendingIntentSenderEnabled && isChangeEnabled) {
            return BackgroundStartPrivileges.ALLOW_FGS;
        }
        return BackgroundStartPrivileges.ALLOW_BAL;
    }

    /* JADX WARN: Removed duplicated region for block: B:142:0x0326  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int sendInner(IApplicationThread iApplicationThread, int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, IBinder iBinder2, String str3, int i2, int i3, int i4, Bundle bundle) {
        String str4;
        Intent[] intentArr;
        String[] strArr;
        String[] strArr2;
        int i5;
        int i6;
        Intent[] intentArr2;
        int startActivityInPackage;
        Intent[] intentArr3;
        if (intent != null) {
            intent.setDefusable(true);
        }
        if (bundle != null) {
            bundle.setDefusable(true);
        }
        synchronized (this.controller.mLock) {
            if (this.canceled) {
                return -96;
            }
            this.sent = true;
            if ((this.key.flags & 1073741824) != 0) {
                this.controller.cancelIntentSender(this, true);
            }
            Intent intent2 = this.key.requestIntent != null ? new Intent(this.key.requestIntent) : new Intent();
            Key key = this.key;
            int i7 = key.flags;
            if ((67108864 & i7) != 0) {
                str4 = key.requestResolvedType;
            } else {
                if (intent != null) {
                    str4 = (intent2.fillIn(intent, i7) & 2) == 0 ? this.key.requestResolvedType : str;
                } else {
                    str4 = key.requestResolvedType;
                }
                int i8 = i3 & (-196);
                intent2.setFlags(((~i8) & intent2.getFlags()) | (i4 & i8));
            }
            String str5 = str4;
            ActivityOptions fromBundle = ActivityOptions.fromBundle(bundle);
            if (fromBundle != null) {
                intent2.addFlags(fromBundle.getPendingIntentLaunchFlags());
            }
            SafeActivityOptions safeActivityOptions = this.key.options;
            if (safeActivityOptions == null) {
                safeActivityOptions = new SafeActivityOptions(fromBundle);
            } else {
                safeActivityOptions.setCallerOptions(fromBundle);
            }
            SafeActivityOptions safeActivityOptions2 = safeActivityOptions;
            ArrayMap<IBinder, TempAllowListDuration> arrayMap = this.mAllowlistDuration;
            TempAllowListDuration tempAllowListDuration = arrayMap != null ? arrayMap.get(iBinder) : null;
            Key key2 = this.key;
            if (key2.type != 2 || (intentArr3 = key2.allIntents) == null || intentArr3.length <= 1) {
                intentArr = null;
                strArr = null;
            } else {
                int length = intentArr3.length;
                Intent[] intentArr4 = new Intent[length];
                int length2 = intentArr3.length;
                strArr = new String[length2];
                System.arraycopy(intentArr3, 0, intentArr4, 0, intentArr3.length);
                String[] strArr3 = this.key.allResolvedTypes;
                if (strArr3 != null) {
                    System.arraycopy(strArr3, 0, strArr, 0, strArr3.length);
                }
                intentArr4[length - 1] = intent2;
                strArr[length2 - 1] = str5;
                intentArr = intentArr4;
            }
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            if (this.key.type == 1) {
                this.controller.mAmInternal.enforceBroadcastOptionsPermissions(bundle, callingUid);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (tempAllowListDuration != null) {
                    StringBuilder sb = new StringBuilder(64);
                    sb.append("setPendingIntentAllowlistDuration,reason:");
                    String str6 = tempAllowListDuration.reason;
                    if (str6 == null) {
                        str6 = "";
                    }
                    sb.append(str6);
                    sb.append(",pendingintent:");
                    UserHandle.formatUid(sb, callingUid);
                    sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
                    if (intent2.getAction() != null) {
                        sb.append(intent2.getAction());
                    } else if (intent2.getComponent() != null) {
                        intent2.getComponent().appendShortString(sb);
                    } else if (intent2.getData() != null) {
                        sb.append(intent2.getData().toSafeString());
                    }
                    strArr2 = strArr;
                    this.controller.mAmInternal.tempAllowlistForPendingIntent(callingPid, callingUid, this.uid, tempAllowListDuration.duration, tempAllowListDuration.type, tempAllowListDuration.reasonCode, sb.toString());
                } else {
                    strArr2 = strArr;
                    if (this.key.type == 5 && bundle != null) {
                        BroadcastOptions broadcastOptions = new BroadcastOptions(bundle);
                        if (broadcastOptions.getTemporaryAppAllowlistDuration() > 0) {
                            this.controller.mAmInternal.tempAllowlistForPendingIntent(callingPid, callingUid, this.uid, broadcastOptions.getTemporaryAppAllowlistDuration(), broadcastOptions.getTemporaryAppAllowlistType(), broadcastOptions.getTemporaryAppAllowlistReasonCode(), broadcastOptions.getTemporaryAppAllowlistReason());
                        }
                    }
                }
                boolean z = iIntentReceiver != null;
                if (iIntentReceiver != null && iApplicationThread == null) {
                    Slog.w("ActivityManager", "Sending of " + intent + " from " + Binder.getCallingUid() + " requested resultTo without an IApplicationThread!", new Throwable());
                }
                int i9 = this.key.userId;
                if (i9 == -2) {
                    i9 = this.controller.mUserController.getCurrentOrTargetUserId();
                }
                int i10 = i9;
                Key key3 = this.key;
                int i11 = key3.type;
                if (i11 == 1) {
                    intent = intent2;
                    i5 = 0;
                    try {
                        BackgroundStartPrivileges backgroundStartPrivilegesForActivitySender = getBackgroundStartPrivilegesForActivitySender(this.mAllowBgActivityStartsForBroadcastSender, iBinder, bundle, callingUid);
                        ActivityManagerInternal activityManagerInternal = this.controller.mAmInternal;
                        Key key4 = this.key;
                        z = activityManagerInternal.broadcastIntentInPackage(key4.packageName, key4.featureId, this.uid, callingUid, callingPid, intent, str5, iApplicationThread, iIntentReceiver, i, (String) null, (Bundle) null, str2, bundle, iIntentReceiver != null, false, i10, backgroundStartPrivilegesForActivitySender, (int[]) null) == 0 ? false : z;
                    } catch (RuntimeException e) {
                        Slog.w("ActivityManager", "Unable to send startActivity intent", e);
                    }
                    i6 = i5;
                    if (z) {
                    }
                    return i6;
                } else if (i11 != 2) {
                    if (i11 == 3) {
                        this.controller.mAtmInternal.sendActivityResult(-1, key3.activity, key3.who, key3.requestCode, i, intent2);
                    } else if (i11 == 4 || i11 == 5) {
                        try {
                            BackgroundStartPrivileges backgroundStartPrivilegesForActivitySender2 = getBackgroundStartPrivilegesForActivitySender(this.mAllowBgActivityStartsForServiceSender, iBinder, bundle, callingUid);
                            ActivityManagerInternal activityManagerInternal2 = this.controller.mAmInternal;
                            int i12 = this.uid;
                            Key key5 = this.key;
                            activityManagerInternal2.startServiceInPackage(i12, intent2, str5, key5.type == 5, key5.packageName, key5.featureId, i10, backgroundStartPrivilegesForActivitySender2);
                        } catch (TransactionTooLargeException unused) {
                            intent = intent2;
                            i6 = -96;
                        } catch (RuntimeException e2) {
                            Slog.w("ActivityManager", "Unable to send startService intent", e2);
                        }
                    }
                    intent = intent2;
                    i5 = 0;
                    i6 = i5;
                    if (z && i6 != -96) {
                        try {
                            iIntentReceiver.performReceive(new Intent(intent), 0, (String) null, (Bundle) null, false, false, this.key.userId);
                        } catch (RemoteException unused2) {
                        }
                    }
                    return i6;
                } else {
                    try {
                        intentArr2 = key3.allIntents;
                    } catch (RuntimeException e3) {
                        e = e3;
                        intent = intent2;
                        i5 = 0;
                    }
                    try {
                        if (intentArr2 != null && intentArr2.length > 1) {
                            i5 = 0;
                            intent = intent2;
                            startActivityInPackage = this.controller.mAtmInternal.startActivitiesInPackage(this.uid, callingPid, callingUid, key3.packageName, key3.featureId, intentArr, strArr2, iBinder2, safeActivityOptions2, i10, false, this, getBackgroundStartPrivilegesForActivitySender(iBinder));
                        } else {
                            intent = intent2;
                            i5 = 0;
                            startActivityInPackage = this.controller.mAtmInternal.startActivityInPackage(this.uid, callingPid, callingUid, key3.packageName, key3.featureId, intent, str5, iBinder2, str3, i2, 0, safeActivityOptions2, i10, null, "PendingIntentRecord", false, this, getBackgroundStartPrivilegesForActivitySender(iBinder));
                        }
                        i6 = startActivityInPackage;
                    } catch (RuntimeException e4) {
                        e = e4;
                        Slog.w("ActivityManager", "Unable to send startActivity intent", e);
                        i6 = i5;
                        if (z) {
                        }
                        return i6;
                    }
                    if (z) {
                        iIntentReceiver.performReceive(new Intent(intent), 0, (String) null, (Bundle) null, false, false, this.key.userId);
                    }
                    return i6;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final BackgroundStartPrivileges getBackgroundStartPrivilegesForActivitySender(IBinder iBinder) {
        if (this.mAllowBgActivityStartsForActivitySender.contains(iBinder)) {
            return BackgroundStartPrivileges.allowBackgroundActivityStarts(iBinder);
        }
        return BackgroundStartPrivileges.NONE;
    }

    public final BackgroundStartPrivileges getBackgroundStartPrivilegesForActivitySender(ArraySet<IBinder> arraySet, IBinder iBinder, Bundle bundle, int i) {
        if (arraySet.contains(iBinder)) {
            return BackgroundStartPrivileges.allowBackgroundActivityStarts(iBinder);
        }
        if (this.uid != i && this.controller.mAtmInternal.isUidForeground(i)) {
            return getBackgroundStartPrivilegesAllowedByCaller(bundle, i);
        }
        return BackgroundStartPrivileges.NONE;
    }

    public void finalize() throws Throwable {
        try {
            if (!this.canceled) {
                this.controller.f1120mH.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.am.PendingIntentRecord$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((PendingIntentRecord) obj).completeFinalize();
                    }
                }, this));
            }
        } finally {
            super/*java.lang.Object*/.finalize();
        }
    }

    public final void completeFinalize() {
        synchronized (this.controller.mLock) {
            if (this.controller.mIntentSenderRecords.get(this.key) == this.ref) {
                this.controller.mIntentSenderRecords.remove(this.key);
                this.controller.decrementUidStatLocked(this);
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("uid=");
        printWriter.print(this.uid);
        printWriter.print(" packageName=");
        printWriter.print(this.key.packageName);
        printWriter.print(" featureId=");
        printWriter.print(this.key.featureId);
        printWriter.print(" type=");
        printWriter.print(this.key.typeName());
        printWriter.print(" flags=0x");
        printWriter.println(Integer.toHexString(this.key.flags));
        Key key = this.key;
        if (key.activity != null || key.who != null) {
            printWriter.print(str);
            printWriter.print("activity=");
            printWriter.print(this.key.activity);
            printWriter.print(" who=");
            printWriter.println(this.key.who);
        }
        Key key2 = this.key;
        if (key2.requestCode != 0 || key2.requestResolvedType != null) {
            printWriter.print(str);
            printWriter.print("requestCode=");
            printWriter.print(this.key.requestCode);
            printWriter.print(" requestResolvedType=");
            printWriter.println(this.key.requestResolvedType);
        }
        if (this.key.requestIntent != null) {
            printWriter.print(str);
            printWriter.print("requestIntent=");
            printWriter.println(this.key.requestIntent.toShortString(false, true, true, false));
        }
        if (this.sent || this.canceled) {
            printWriter.print(str);
            printWriter.print("sent=");
            printWriter.print(this.sent);
            printWriter.print(" canceled=");
            printWriter.println(this.canceled);
        }
        if (this.mAllowlistDuration != null) {
            printWriter.print(str);
            printWriter.print("allowlistDuration=");
            for (int i = 0; i < this.mAllowlistDuration.size(); i++) {
                if (i != 0) {
                    printWriter.print(", ");
                }
                TempAllowListDuration valueAt = this.mAllowlistDuration.valueAt(i);
                printWriter.print(Integer.toHexString(System.identityHashCode(this.mAllowlistDuration.keyAt(i))));
                printWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                TimeUtils.formatDuration(valueAt.duration, printWriter);
                printWriter.print("/");
                printWriter.print(valueAt.type);
                printWriter.print("/");
                printWriter.print(PowerWhitelistManager.reasonCodeToString(valueAt.reasonCode));
                printWriter.print("/");
                printWriter.print(valueAt.reason);
            }
            printWriter.println();
        }
        if (this.mCancelCallbacks != null) {
            printWriter.print(str);
            printWriter.println("mCancelCallbacks:");
            for (int i2 = 0; i2 < this.mCancelCallbacks.getRegisteredCallbackCount(); i2++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i2);
                printWriter.print(": ");
                printWriter.println(this.mCancelCallbacks.getRegisteredCallbackItem(i2));
            }
        }
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("PendingIntentRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.key.packageName);
        if (this.key.featureId != null) {
            sb.append('/');
            sb.append(this.key.featureId);
        }
        sb.append(' ');
        sb.append(this.key.typeName());
        if (this.mAllowlistDuration != null) {
            sb.append(" (allowlist: ");
            for (int i = 0; i < this.mAllowlistDuration.size(); i++) {
                if (i != 0) {
                    sb.append(",");
                }
                TempAllowListDuration valueAt = this.mAllowlistDuration.valueAt(i);
                sb.append(Integer.toHexString(System.identityHashCode(this.mAllowlistDuration.keyAt(i))));
                sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
                TimeUtils.formatDuration(valueAt.duration, sb);
                sb.append("/");
                sb.append(valueAt.type);
                sb.append("/");
                sb.append(PowerWhitelistManager.reasonCodeToString(valueAt.reasonCode));
                sb.append("/");
                sb.append(valueAt.reason);
            }
            sb.append(")");
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}
