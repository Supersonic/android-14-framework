package com.android.server.media;

import android.app.BroadcastOptions;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Collections;
import java.util.List;
/* loaded from: classes2.dex */
public final class MediaButtonReceiverHolder {
    public final ComponentName mComponentName;
    public final int mComponentType;
    public final String mPackageName;
    public final PendingIntent mPendingIntent;
    public final int mUserId;

    public static MediaButtonReceiverHolder unflattenFromString(Context context, String str) {
        String[] split;
        ComponentName unflattenFromString;
        int componentType;
        if (TextUtils.isEmpty(str) || (split = str.split(",")) == null || ((split.length != 2 && split.length != 3) || (unflattenFromString = ComponentName.unflattenFromString(split[0])) == null)) {
            return null;
        }
        int parseInt = Integer.parseInt(split[1]);
        if (split.length == 3) {
            componentType = Integer.parseInt(split[2]);
        } else {
            componentType = getComponentType(context, unflattenFromString);
        }
        return new MediaButtonReceiverHolder(parseInt, null, unflattenFromString, componentType);
    }

    public static MediaButtonReceiverHolder create(int i, PendingIntent pendingIntent, String str) {
        if (pendingIntent == null) {
            return null;
        }
        int componentType = getComponentType(pendingIntent);
        ComponentName componentName = getComponentName(pendingIntent, componentType);
        if (componentName != null) {
            return new MediaButtonReceiverHolder(i, pendingIntent, componentName, componentType);
        }
        Log.w("PendingIntentHolder", "Unresolvable implicit intent is set, pi=" + pendingIntent);
        return new MediaButtonReceiverHolder(i, pendingIntent, str);
    }

    public static MediaButtonReceiverHolder create(int i, ComponentName componentName) {
        return new MediaButtonReceiverHolder(i, null, componentName, 1);
    }

    public MediaButtonReceiverHolder(int i, PendingIntent pendingIntent, ComponentName componentName, int i2) {
        this.mUserId = i;
        this.mPendingIntent = pendingIntent;
        this.mComponentName = componentName;
        this.mPackageName = componentName.getPackageName();
        this.mComponentType = i2;
    }

    public MediaButtonReceiverHolder(int i, PendingIntent pendingIntent, String str) {
        this.mUserId = i;
        this.mPendingIntent = pendingIntent;
        this.mComponentName = null;
        this.mPackageName = str;
        this.mComponentType = 0;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean send(Context context, KeyEvent keyEvent, String str, int i, PendingIntent.OnFinished onFinished, Handler handler, long j) {
        Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.addFlags(268435456);
        intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setTemporaryAppAllowlist(j, 0, (int) FrameworkStatsLog.f87x74364b3b, "");
        makeBasic.setBackgroundActivityStartsAllowed(true);
        if (this.mPendingIntent != null) {
            Log.d("PendingIntentHolder", "Sending " + keyEvent + " to the last known PendingIntent " + this.mPendingIntent);
            try {
                this.mPendingIntent.send(context, i, intent, onFinished, handler, null, makeBasic.toBundle());
            } catch (PendingIntent.CanceledException e) {
                Log.w("PendingIntentHolder", "Error sending key event to media button receiver " + this.mPendingIntent, e);
                return false;
            }
        } else if (this.mComponentName != null) {
            Log.d("PendingIntentHolder", "Sending " + keyEvent + " to the restored intent " + this.mComponentName + ", type=" + this.mComponentType);
            intent.setComponent(this.mComponentName);
            UserHandle of = UserHandle.of(this.mUserId);
            try {
                int i2 = this.mComponentType;
                if (i2 == 2) {
                    context.startActivityAsUser(intent, of);
                } else if (i2 == 3) {
                    context.createContextAsUser(of, 0).startForegroundService(intent);
                } else {
                    context.sendBroadcastAsUser(intent, of, null, makeBasic.toBundle());
                }
            } catch (Exception e2) {
                Log.w("PendingIntentHolder", "Error sending media button to the restored intent " + this.mComponentName + ", type=" + this.mComponentType, e2);
                return false;
            }
        } else {
            Log.e("PendingIntentHolder", "Shouldn't be happen -- pending intent or component name must be set");
            return false;
        }
        return true;
    }

    public String toString() {
        return "MBR {pi=" + this.mPendingIntent + ", componentName=" + this.mComponentName + ", type=" + this.mComponentType + ", pkg=" + this.mPackageName + "}";
    }

    public String flattenToString() {
        ComponentName componentName = this.mComponentName;
        return componentName == null ? "" : String.join(",", componentName.flattenToString(), String.valueOf(this.mUserId), String.valueOf(this.mComponentType));
    }

    public static int getComponentType(PendingIntent pendingIntent) {
        if (pendingIntent.isBroadcast()) {
            return 1;
        }
        if (pendingIntent.isActivity()) {
            return 2;
        }
        return (pendingIntent.isForegroundService() || pendingIntent.isService()) ? 3 : 0;
    }

    public static int getComponentType(Context context, ComponentName componentName) {
        if (componentName == null) {
            return 0;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            if (packageManager.getActivityInfo(componentName, 786433) != null) {
                return 2;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        try {
            return packageManager.getServiceInfo(componentName, 786436) != null ? 3 : 1;
        } catch (PackageManager.NameNotFoundException unused2) {
            return 1;
        }
    }

    public static ComponentName getComponentName(PendingIntent pendingIntent, int i) {
        List<ResolveInfo> emptyList = Collections.emptyList();
        if (i == 1) {
            emptyList = pendingIntent.queryIntentComponents(786434);
        } else if (i == 2) {
            emptyList = pendingIntent.queryIntentComponents(851969);
        } else if (i == 3) {
            emptyList = pendingIntent.queryIntentComponents(786436);
        }
        for (ResolveInfo resolveInfo : emptyList) {
            ComponentInfo componentInfo = getComponentInfo(resolveInfo);
            if (componentInfo != null && TextUtils.equals(componentInfo.packageName, pendingIntent.getCreatorPackage()) && componentInfo.packageName != null && componentInfo.name != null) {
                return new ComponentName(componentInfo.packageName, componentInfo.name);
            }
        }
        return null;
    }

    public static ComponentInfo getComponentInfo(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo != null) {
            return activityInfo;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if (serviceInfo != null) {
            return serviceInfo;
        }
        return null;
    }
}
