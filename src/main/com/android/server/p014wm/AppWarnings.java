package com.android.server.p014wm;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AtomicFile;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.wm.AppWarnings */
/* loaded from: classes2.dex */
public class AppWarnings {
    public final ActivityTaskManagerService mAtm;
    public final AtomicFile mConfigFile;
    public DeprecatedTargetSdkVersionDialog mDeprecatedTargetSdkVersionDialog;
    public final ConfigHandler mHandler;
    public final Context mUiContext;
    public final UiHandler mUiHandler;
    public UnsupportedCompileSdkDialog mUnsupportedCompileSdkDialog;
    public UnsupportedDisplaySizeDialog mUnsupportedDisplaySizeDialog;
    public final HashMap<String, Integer> mPackageFlags = new HashMap<>();
    public HashSet<ComponentName> mAlwaysShowUnsupportedCompileSdkWarningActivities = new HashSet<>();

    public void alwaysShowUnsupportedCompileSdkWarning(ComponentName componentName) {
        this.mAlwaysShowUnsupportedCompileSdkWarningActivities.add(componentName);
    }

    public AppWarnings(ActivityTaskManagerService activityTaskManagerService, Context context, Handler handler, Handler handler2, File file) {
        this.mAtm = activityTaskManagerService;
        this.mUiContext = context;
        this.mHandler = new ConfigHandler(handler.getLooper());
        this.mUiHandler = new UiHandler(handler2.getLooper());
        this.mConfigFile = new AtomicFile(new File(file, "packages-warnings.xml"), "warnings-config");
        readConfigFromFileAmsThread();
    }

    public void showUnsupportedDisplaySizeDialogIfNeeded(ActivityRecord activityRecord) {
        Configuration globalConfiguration = this.mAtm.getGlobalConfiguration();
        if (globalConfiguration.densityDpi == DisplayMetrics.DENSITY_DEVICE_STABLE || activityRecord.info.applicationInfo.requiresSmallestWidthDp <= globalConfiguration.smallestScreenWidthDp) {
            return;
        }
        this.mUiHandler.showUnsupportedDisplaySizeDialog(activityRecord);
    }

    public void showUnsupportedCompileSdkDialogIfNeeded(ActivityRecord activityRecord) {
        ApplicationInfo applicationInfo = activityRecord.info.applicationInfo;
        if (applicationInfo.compileSdkVersion == 0 || applicationInfo.compileSdkVersionCodename == null || !this.mAlwaysShowUnsupportedCompileSdkWarningActivities.contains(activityRecord.mActivityComponent)) {
            return;
        }
        ApplicationInfo applicationInfo2 = activityRecord.info.applicationInfo;
        int i = applicationInfo2.compileSdkVersion;
        int i2 = Build.VERSION.SDK_INT;
        boolean z = !"REL".equals(applicationInfo2.compileSdkVersionCodename);
        String str = Build.VERSION.CODENAME;
        boolean z2 = !"REL".equals(str);
        if ((!z || i >= i2) && ((!z2 || i2 >= i) && !(z && z2 && i2 == i && !str.equals(activityRecord.info.applicationInfo.compileSdkVersionCodename)))) {
            return;
        }
        this.mUiHandler.showUnsupportedCompileSdkDialog(activityRecord);
    }

    public void showDeprecatedTargetDialogIfNeeded(ActivityRecord activityRecord) {
        if (activityRecord.info.applicationInfo.targetSdkVersion < Build.VERSION.MIN_SUPPORTED_TARGET_SDK_INT) {
            this.mUiHandler.showDeprecatedTargetDialog(activityRecord);
        }
    }

    public void onStartActivity(ActivityRecord activityRecord) {
        showUnsupportedCompileSdkDialogIfNeeded(activityRecord);
        showUnsupportedDisplaySizeDialogIfNeeded(activityRecord);
        showDeprecatedTargetDialogIfNeeded(activityRecord);
    }

    public void onResumeActivity(ActivityRecord activityRecord) {
        showUnsupportedDisplaySizeDialogIfNeeded(activityRecord);
    }

    public void onPackageDataCleared(String str) {
        removePackageAndHideDialogs(str);
    }

    public void onPackageUninstalled(String str) {
        removePackageAndHideDialogs(str);
    }

    public void onDensityChanged() {
        this.mUiHandler.hideUnsupportedDisplaySizeDialog();
    }

    public final void removePackageAndHideDialogs(String str) {
        this.mUiHandler.hideDialogsForPackage(str);
        synchronized (this.mPackageFlags) {
            this.mPackageFlags.remove(str);
            this.mHandler.scheduleWrite();
        }
    }

    public final void hideUnsupportedDisplaySizeDialogUiThread() {
        UnsupportedDisplaySizeDialog unsupportedDisplaySizeDialog = this.mUnsupportedDisplaySizeDialog;
        if (unsupportedDisplaySizeDialog != null) {
            unsupportedDisplaySizeDialog.dismiss();
            this.mUnsupportedDisplaySizeDialog = null;
        }
    }

    public final void showUnsupportedDisplaySizeDialogUiThread(ActivityRecord activityRecord) {
        UnsupportedDisplaySizeDialog unsupportedDisplaySizeDialog = this.mUnsupportedDisplaySizeDialog;
        if (unsupportedDisplaySizeDialog != null) {
            unsupportedDisplaySizeDialog.dismiss();
            this.mUnsupportedDisplaySizeDialog = null;
        }
        if (activityRecord == null || hasPackageFlag(activityRecord.packageName, 1)) {
            return;
        }
        UnsupportedDisplaySizeDialog unsupportedDisplaySizeDialog2 = new UnsupportedDisplaySizeDialog(this, this.mUiContext, activityRecord.info.applicationInfo);
        this.mUnsupportedDisplaySizeDialog = unsupportedDisplaySizeDialog2;
        unsupportedDisplaySizeDialog2.show();
    }

    public final void showUnsupportedCompileSdkDialogUiThread(ActivityRecord activityRecord) {
        UnsupportedCompileSdkDialog unsupportedCompileSdkDialog = this.mUnsupportedCompileSdkDialog;
        if (unsupportedCompileSdkDialog != null) {
            unsupportedCompileSdkDialog.dismiss();
            this.mUnsupportedCompileSdkDialog = null;
        }
        if (activityRecord == null || hasPackageFlag(activityRecord.packageName, 2)) {
            return;
        }
        UnsupportedCompileSdkDialog unsupportedCompileSdkDialog2 = new UnsupportedCompileSdkDialog(this, this.mUiContext, activityRecord.info.applicationInfo);
        this.mUnsupportedCompileSdkDialog = unsupportedCompileSdkDialog2;
        unsupportedCompileSdkDialog2.show();
    }

    public final void showDeprecatedTargetSdkDialogUiThread(ActivityRecord activityRecord) {
        DeprecatedTargetSdkVersionDialog deprecatedTargetSdkVersionDialog = this.mDeprecatedTargetSdkVersionDialog;
        if (deprecatedTargetSdkVersionDialog != null) {
            deprecatedTargetSdkVersionDialog.dismiss();
            this.mDeprecatedTargetSdkVersionDialog = null;
        }
        if (activityRecord == null || hasPackageFlag(activityRecord.packageName, 4)) {
            return;
        }
        DeprecatedTargetSdkVersionDialog deprecatedTargetSdkVersionDialog2 = new DeprecatedTargetSdkVersionDialog(this, this.mUiContext, activityRecord.info.applicationInfo);
        this.mDeprecatedTargetSdkVersionDialog = deprecatedTargetSdkVersionDialog2;
        deprecatedTargetSdkVersionDialog2.show();
    }

    public final void hideDialogsForPackageUiThread(String str) {
        UnsupportedDisplaySizeDialog unsupportedDisplaySizeDialog = this.mUnsupportedDisplaySizeDialog;
        if (unsupportedDisplaySizeDialog != null && (str == null || str.equals(unsupportedDisplaySizeDialog.mPackageName))) {
            this.mUnsupportedDisplaySizeDialog.dismiss();
            this.mUnsupportedDisplaySizeDialog = null;
        }
        UnsupportedCompileSdkDialog unsupportedCompileSdkDialog = this.mUnsupportedCompileSdkDialog;
        if (unsupportedCompileSdkDialog != null && (str == null || str.equals(unsupportedCompileSdkDialog.mPackageName))) {
            this.mUnsupportedCompileSdkDialog.dismiss();
            this.mUnsupportedCompileSdkDialog = null;
        }
        DeprecatedTargetSdkVersionDialog deprecatedTargetSdkVersionDialog = this.mDeprecatedTargetSdkVersionDialog;
        if (deprecatedTargetSdkVersionDialog != null) {
            if (str == null || str.equals(deprecatedTargetSdkVersionDialog.mPackageName)) {
                this.mDeprecatedTargetSdkVersionDialog.dismiss();
                this.mDeprecatedTargetSdkVersionDialog = null;
            }
        }
    }

    public boolean hasPackageFlag(String str, int i) {
        return (getPackageFlags(str) & i) == i;
    }

    public void setPackageFlag(String str, int i, boolean z) {
        synchronized (this.mPackageFlags) {
            int packageFlags = getPackageFlags(str);
            int i2 = z ? i | packageFlags : (~i) & packageFlags;
            if (packageFlags != i2) {
                if (i2 != 0) {
                    this.mPackageFlags.put(str, Integer.valueOf(i2));
                } else {
                    this.mPackageFlags.remove(str);
                }
                this.mHandler.scheduleWrite();
            }
        }
    }

    public final int getPackageFlags(String str) {
        int intValue;
        synchronized (this.mPackageFlags) {
            intValue = this.mPackageFlags.getOrDefault(str, 0).intValue();
        }
        return intValue;
    }

    /* renamed from: com.android.server.wm.AppWarnings$UiHandler */
    /* loaded from: classes2.dex */
    public final class UiHandler extends Handler {
        public UiHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AppWarnings.this.showUnsupportedDisplaySizeDialogUiThread((ActivityRecord) message.obj);
            } else if (i == 2) {
                AppWarnings.this.hideUnsupportedDisplaySizeDialogUiThread();
            } else if (i == 3) {
                AppWarnings.this.showUnsupportedCompileSdkDialogUiThread((ActivityRecord) message.obj);
            } else if (i == 4) {
                AppWarnings.this.hideDialogsForPackageUiThread((String) message.obj);
            } else if (i != 5) {
            } else {
                AppWarnings.this.showDeprecatedTargetSdkDialogUiThread((ActivityRecord) message.obj);
            }
        }

        public void showUnsupportedDisplaySizeDialog(ActivityRecord activityRecord) {
            removeMessages(1);
            obtainMessage(1, activityRecord).sendToTarget();
        }

        public void hideUnsupportedDisplaySizeDialog() {
            removeMessages(2);
            sendEmptyMessage(2);
        }

        public void showUnsupportedCompileSdkDialog(ActivityRecord activityRecord) {
            removeMessages(3);
            obtainMessage(3, activityRecord).sendToTarget();
        }

        public void showDeprecatedTargetDialog(ActivityRecord activityRecord) {
            removeMessages(5);
            obtainMessage(5, activityRecord).sendToTarget();
        }

        public void hideDialogsForPackage(String str) {
            obtainMessage(4, str).sendToTarget();
        }
    }

    /* renamed from: com.android.server.wm.AppWarnings$BaseDialog */
    /* loaded from: classes2.dex */
    public static class BaseDialog {
        public BroadcastReceiver mCloseReceiver;
        public AlertDialog mDialog;
        public final AppWarnings mManager;
        public final String mPackageName;

        public BaseDialog(AppWarnings appWarnings, String str) {
            this.mManager = appWarnings;
            this.mPackageName = str;
        }

        public void show() {
            if (this.mDialog == null) {
                return;
            }
            if (this.mCloseReceiver == null) {
                this.mCloseReceiver = new BroadcastReceiver() { // from class: com.android.server.wm.AppWarnings.BaseDialog.1
                    @Override // android.content.BroadcastReceiver
                    public void onReceive(Context context, Intent intent) {
                        if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                            BaseDialog.this.mManager.mUiHandler.hideDialogsForPackage(BaseDialog.this.mPackageName);
                        }
                    }
                };
                this.mManager.mUiContext.registerReceiver(this.mCloseReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"), 2);
            }
            Slog.w("AppWarnings", "Showing " + getClass().getSimpleName() + " for package " + this.mPackageName);
            this.mDialog.show();
        }

        public void dismiss() {
            if (this.mDialog == null) {
                return;
            }
            if (this.mCloseReceiver != null) {
                this.mManager.mUiContext.unregisterReceiver(this.mCloseReceiver);
                this.mCloseReceiver = null;
            }
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    /* renamed from: com.android.server.wm.AppWarnings$ConfigHandler */
    /* loaded from: classes2.dex */
    public final class ConfigHandler extends Handler {
        public ConfigHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            AppWarnings.this.writeConfigToFileAmsThread();
        }

        public void scheduleWrite() {
            removeMessages(1);
            sendEmptyMessageDelayed(1, 10000L);
        }
    }

    public final void writeConfigToFileAmsThread() {
        HashMap hashMap;
        FileOutputStream fileOutputStream;
        IOException e;
        synchronized (this.mPackageFlags) {
            hashMap = new HashMap(this.mPackageFlags);
        }
        try {
            fileOutputStream = this.mConfigFile.startWrite();
        } catch (IOException e2) {
            fileOutputStream = null;
            e = e2;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(fileOutputStream);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "packages");
            for (Map.Entry entry : hashMap.entrySet()) {
                String str = (String) entry.getKey();
                int intValue = ((Integer) entry.getValue()).intValue();
                if (intValue != 0) {
                    resolveSerializer.startTag((String) null, "package");
                    resolveSerializer.attribute((String) null, "name", str);
                    resolveSerializer.attributeInt((String) null, "flags", intValue);
                    resolveSerializer.endTag((String) null, "package");
                }
            }
            resolveSerializer.endTag((String) null, "packages");
            resolveSerializer.endDocument();
            this.mConfigFile.finishWrite(fileOutputStream);
        } catch (IOException e3) {
            e = e3;
            Slog.w("AppWarnings", "Error writing package metadata", e);
            if (fileOutputStream != null) {
                this.mConfigFile.failWrite(fileOutputStream);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x006c A[Catch: IOException -> 0x008e, TRY_ENTER, TRY_LEAVE, TryCatch #6 {IOException -> 0x008e, blocks: (B:28:0x006c, B:42:0x0083), top: B:61:0x0005 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readConfigFromFileAmsThread() {
        FileInputStream openRead;
        String attributeValue;
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    openRead = this.mConfigFile.openRead();
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                    int eventType = resolvePullParser.getEventType();
                    while (eventType != 2 && eventType != 1) {
                        eventType = resolvePullParser.next();
                    }
                    if (eventType == 1) {
                        if (openRead != null) {
                            try {
                                openRead.close();
                            } catch (IOException unused) {
                            }
                        }
                    } else if ("packages".equals(resolvePullParser.getName())) {
                        int next = resolvePullParser.next();
                        do {
                            if (next == 2) {
                                String name = resolvePullParser.getName();
                                if (resolvePullParser.getDepth() == 2 && "package".equals(name) && (attributeValue = resolvePullParser.getAttributeValue((String) null, "name")) != null) {
                                    this.mPackageFlags.put(attributeValue, Integer.valueOf(resolvePullParser.getAttributeInt((String) null, "flags", 0)));
                                }
                            }
                            next = resolvePullParser.next();
                        } while (next != 1);
                        if (openRead == null) {
                            openRead.close();
                        }
                    } else if (openRead == null) {
                    }
                } catch (IOException e) {
                    e = e;
                    fileInputStream = openRead;
                    if (fileInputStream != null) {
                        Slog.w("AppWarnings", "Error reading package metadata", e);
                    }
                    if (fileInputStream == null) {
                        return;
                    }
                    fileInputStream.close();
                } catch (XmlPullParserException e2) {
                    e = e2;
                    fileInputStream = openRead;
                    Slog.w("AppWarnings", "Error reading package metadata", e);
                    if (fileInputStream == null) {
                        return;
                    }
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th = th2;
                    fileInputStream = openRead;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused2) {
                        }
                    }
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
            } catch (XmlPullParserException e4) {
                e = e4;
            }
        } catch (IOException unused3) {
        }
    }
}
