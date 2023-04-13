package android.content.p001pm;

import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.IntentSender;
import android.content.p001pm.ILauncherApps;
import android.content.p001pm.IOnAppsChangedListener;
import android.content.p001pm.IPackageInstallerCallback;
import android.content.p001pm.IShortcutChangeCallback;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.PackageInstaller;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import com.android.internal.infra.AndroidFuture;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
/* renamed from: android.content.pm.ILauncherApps */
/* loaded from: classes.dex */
public interface ILauncherApps extends IInterface {
    void addOnAppsChangedListener(String str, IOnAppsChangedListener iOnAppsChangedListener) throws RemoteException;

    void cacheShortcuts(String str, String str2, List<String> list, UserHandle userHandle, int i) throws RemoteException;

    PendingIntent getActivityLaunchIntent(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException;

    Map<String, LauncherActivityInfoInternal> getActivityOverrides(String str, int i) throws RemoteException;

    ParceledListSlice getAllSessions(String str) throws RemoteException;

    LauncherApps.AppUsageLimit getAppUsageLimit(String str, String str2, UserHandle userHandle) throws RemoteException;

    ApplicationInfo getApplicationInfo(String str, String str2, int i, UserHandle userHandle) throws RemoteException;

    ParceledListSlice getLauncherActivities(String str, String str2, UserHandle userHandle) throws RemoteException;

    ParceledListSlice getShortcutConfigActivities(String str, String str2, UserHandle userHandle) throws RemoteException;

    IntentSender getShortcutConfigActivityIntent(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException;

    ParcelFileDescriptor getShortcutIconFd(String str, String str2, String str3, int i) throws RemoteException;

    int getShortcutIconResId(String str, String str2, String str3, int i) throws RemoteException;

    String getShortcutIconUri(String str, String str2, String str3, int i) throws RemoteException;

    PendingIntent getShortcutIntent(String str, String str2, String str3, Bundle bundle, UserHandle userHandle) throws RemoteException;

    ParceledListSlice getShortcuts(String str, ShortcutQueryWrapper shortcutQueryWrapper, UserHandle userHandle) throws RemoteException;

    void getShortcutsAsync(String str, ShortcutQueryWrapper shortcutQueryWrapper, UserHandle userHandle, AndroidFuture<List<ShortcutInfo>> androidFuture) throws RemoteException;

    Bundle getSuspendedPackageLauncherExtras(String str, UserHandle userHandle) throws RemoteException;

    boolean hasShortcutHostPermission(String str) throws RemoteException;

    boolean isActivityEnabled(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException;

    boolean isPackageEnabled(String str, String str2, UserHandle userHandle) throws RemoteException;

    void pinShortcuts(String str, String str2, List<String> list, UserHandle userHandle) throws RemoteException;

    void registerPackageInstallerCallback(String str, IPackageInstallerCallback iPackageInstallerCallback) throws RemoteException;

    void registerShortcutChangeCallback(String str, ShortcutQueryWrapper shortcutQueryWrapper, IShortcutChangeCallback iShortcutChangeCallback) throws RemoteException;

    void removeOnAppsChangedListener(IOnAppsChangedListener iOnAppsChangedListener) throws RemoteException;

    LauncherActivityInfoInternal resolveLauncherActivityInternal(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException;

    boolean shouldHideFromSuggestions(String str, UserHandle userHandle) throws RemoteException;

    void showAppDetailsAsUser(IApplicationThread iApplicationThread, String str, String str2, ComponentName componentName, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException;

    void startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, ComponentName componentName, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException;

    void startSessionDetailsActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, PackageInstaller.SessionInfo sessionInfo, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException;

    boolean startShortcut(String str, String str2, String str3, String str4, Rect rect, Bundle bundle, int i) throws RemoteException;

    void uncacheShortcuts(String str, String str2, List<String> list, UserHandle userHandle, int i) throws RemoteException;

    void unregisterShortcutChangeCallback(String str, IShortcutChangeCallback iShortcutChangeCallback) throws RemoteException;

    /* renamed from: android.content.pm.ILauncherApps$Default */
    /* loaded from: classes.dex */
    public static class Default implements ILauncherApps {
        @Override // android.content.p001pm.ILauncherApps
        public void addOnAppsChangedListener(String callingPackage, IOnAppsChangedListener listener) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void removeOnAppsChangedListener(IOnAppsChangedListener listener) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public ParceledListSlice getLauncherActivities(String callingPackage, String packageName, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public LauncherActivityInfoInternal resolveLauncherActivityInternal(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public void startSessionDetailsActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, PackageInstaller.SessionInfo sessionInfo, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public PendingIntent getActivityLaunchIntent(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public void showAppDetailsAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public boolean isPackageEnabled(String callingPackage, String packageName, UserHandle user) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ILauncherApps
        public Bundle getSuspendedPackageLauncherExtras(String packageName, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public boolean isActivityEnabled(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ILauncherApps
        public ApplicationInfo getApplicationInfo(String callingPackage, String packageName, int flags, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public LauncherApps.AppUsageLimit getAppUsageLimit(String callingPackage, String packageName, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public ParceledListSlice getShortcuts(String callingPackage, ShortcutQueryWrapper query, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public void getShortcutsAsync(String callingPackage, ShortcutQueryWrapper query, UserHandle user, AndroidFuture<List<ShortcutInfo>> cb) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void pinShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public boolean startShortcut(String callingPackage, String packageName, String featureId, String id, Rect sourceBounds, Bundle startActivityOptions, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ILauncherApps
        public int getShortcutIconResId(String callingPackage, String packageName, String id, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.p001pm.ILauncherApps
        public ParcelFileDescriptor getShortcutIconFd(String callingPackage, String packageName, String id, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public boolean hasShortcutHostPermission(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ILauncherApps
        public boolean shouldHideFromSuggestions(String packageName, UserHandle user) throws RemoteException {
            return false;
        }

        @Override // android.content.p001pm.ILauncherApps
        public ParceledListSlice getShortcutConfigActivities(String callingPackage, String packageName, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public IntentSender getShortcutConfigActivityIntent(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public PendingIntent getShortcutIntent(String callingPackage, String packageName, String shortcutId, Bundle opts, UserHandle user) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public void registerPackageInstallerCallback(String callingPackage, IPackageInstallerCallback callback) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public ParceledListSlice getAllSessions(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public void registerShortcutChangeCallback(String callingPackage, ShortcutQueryWrapper query, IShortcutChangeCallback callback) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void unregisterShortcutChangeCallback(String callingPackage, IShortcutChangeCallback callback) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void cacheShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user, int cacheFlags) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public void uncacheShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user, int cacheFlags) throws RemoteException {
        }

        @Override // android.content.p001pm.ILauncherApps
        public String getShortcutIconUri(String callingPackage, String packageName, String shortcutId, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.p001pm.ILauncherApps
        public Map<String, LauncherActivityInfoInternal> getActivityOverrides(String callingPackage, int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.ILauncherApps$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ILauncherApps {
        public static final String DESCRIPTOR = "android.content.pm.ILauncherApps";
        static final int TRANSACTION_addOnAppsChangedListener = 1;
        static final int TRANSACTION_cacheShortcuts = 29;
        static final int TRANSACTION_getActivityLaunchIntent = 7;
        static final int TRANSACTION_getActivityOverrides = 32;
        static final int TRANSACTION_getAllSessions = 26;
        static final int TRANSACTION_getAppUsageLimit = 13;
        static final int TRANSACTION_getApplicationInfo = 12;
        static final int TRANSACTION_getLauncherActivities = 3;
        static final int TRANSACTION_getShortcutConfigActivities = 22;
        static final int TRANSACTION_getShortcutConfigActivityIntent = 23;
        static final int TRANSACTION_getShortcutIconFd = 19;
        static final int TRANSACTION_getShortcutIconResId = 18;
        static final int TRANSACTION_getShortcutIconUri = 31;
        static final int TRANSACTION_getShortcutIntent = 24;
        static final int TRANSACTION_getShortcuts = 14;
        static final int TRANSACTION_getShortcutsAsync = 15;
        static final int TRANSACTION_getSuspendedPackageLauncherExtras = 10;
        static final int TRANSACTION_hasShortcutHostPermission = 20;
        static final int TRANSACTION_isActivityEnabled = 11;
        static final int TRANSACTION_isPackageEnabled = 9;
        static final int TRANSACTION_pinShortcuts = 16;
        static final int TRANSACTION_registerPackageInstallerCallback = 25;
        static final int TRANSACTION_registerShortcutChangeCallback = 27;
        static final int TRANSACTION_removeOnAppsChangedListener = 2;
        static final int TRANSACTION_resolveLauncherActivityInternal = 4;
        static final int TRANSACTION_shouldHideFromSuggestions = 21;
        static final int TRANSACTION_showAppDetailsAsUser = 8;
        static final int TRANSACTION_startActivityAsUser = 6;
        static final int TRANSACTION_startSessionDetailsActivityAsUser = 5;
        static final int TRANSACTION_startShortcut = 17;
        static final int TRANSACTION_uncacheShortcuts = 30;
        static final int TRANSACTION_unregisterShortcutChangeCallback = 28;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILauncherApps asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILauncherApps)) {
                return (ILauncherApps) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "addOnAppsChangedListener";
                case 2:
                    return "removeOnAppsChangedListener";
                case 3:
                    return "getLauncherActivities";
                case 4:
                    return "resolveLauncherActivityInternal";
                case 5:
                    return "startSessionDetailsActivityAsUser";
                case 6:
                    return "startActivityAsUser";
                case 7:
                    return "getActivityLaunchIntent";
                case 8:
                    return "showAppDetailsAsUser";
                case 9:
                    return "isPackageEnabled";
                case 10:
                    return "getSuspendedPackageLauncherExtras";
                case 11:
                    return "isActivityEnabled";
                case 12:
                    return "getApplicationInfo";
                case 13:
                    return "getAppUsageLimit";
                case 14:
                    return "getShortcuts";
                case 15:
                    return "getShortcutsAsync";
                case 16:
                    return "pinShortcuts";
                case 17:
                    return "startShortcut";
                case 18:
                    return "getShortcutIconResId";
                case 19:
                    return "getShortcutIconFd";
                case 20:
                    return "hasShortcutHostPermission";
                case 21:
                    return "shouldHideFromSuggestions";
                case 22:
                    return "getShortcutConfigActivities";
                case 23:
                    return "getShortcutConfigActivityIntent";
                case 24:
                    return "getShortcutIntent";
                case 25:
                    return "registerPackageInstallerCallback";
                case 26:
                    return "getAllSessions";
                case 27:
                    return "registerShortcutChangeCallback";
                case 28:
                    return "unregisterShortcutChangeCallback";
                case 29:
                    return "cacheShortcuts";
                case 30:
                    return "uncacheShortcuts";
                case 31:
                    return "getShortcutIconUri";
                case 32:
                    return "getActivityOverrides";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, final Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            IOnAppsChangedListener _arg1 = IOnAppsChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnAppsChangedListener(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            IOnAppsChangedListener _arg02 = IOnAppsChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnAppsChangedListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg12 = data.readString();
                            UserHandle _arg2 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            ParceledListSlice _result = getLauncherActivities(_arg03, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            ComponentName _arg13 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg22 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            LauncherActivityInfoInternal _result2 = resolveLauncherActivityInternal(_arg04, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            IApplicationThread _arg05 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg14 = data.readString();
                            String _arg23 = data.readString();
                            PackageInstaller.SessionInfo _arg3 = (PackageInstaller.SessionInfo) data.readTypedObject(PackageInstaller.SessionInfo.CREATOR);
                            Rect _arg4 = (Rect) data.readTypedObject(Rect.CREATOR);
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            UserHandle _arg6 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            startSessionDetailsActivityAsUser(_arg05, _arg14, _arg23, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            break;
                        case 6:
                            IApplicationThread _arg06 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg15 = data.readString();
                            String _arg24 = data.readString();
                            ComponentName _arg32 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Rect _arg42 = (Rect) data.readTypedObject(Rect.CREATOR);
                            Bundle _arg52 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            UserHandle _arg62 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            startActivityAsUser(_arg06, _arg15, _arg24, _arg32, _arg42, _arg52, _arg62);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            ComponentName _arg16 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg25 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            PendingIntent _result3 = getActivityLaunchIntent(_arg07, _arg16, _arg25);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 8:
                            IApplicationThread _arg08 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            String _arg26 = data.readString();
                            ComponentName _arg33 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Rect _arg43 = (Rect) data.readTypedObject(Rect.CREATOR);
                            Bundle _arg53 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            UserHandle _arg63 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            showAppDetailsAsUser(_arg08, _arg17, _arg26, _arg33, _arg43, _arg53, _arg63);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg18 = data.readString();
                            UserHandle _arg27 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = isPackageEnabled(_arg09, _arg18, _arg27);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            UserHandle _arg19 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            Bundle _result5 = getSuspendedPackageLauncherExtras(_arg010, _arg19);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            ComponentName _arg110 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg28 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result6 = isActivityEnabled(_arg011, _arg110, _arg28);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            String _arg111 = data.readString();
                            int _arg29 = data.readInt();
                            UserHandle _arg34 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            ApplicationInfo _result7 = getApplicationInfo(_arg012, _arg111, _arg29, _arg34);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            String _arg112 = data.readString();
                            UserHandle _arg210 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            LauncherApps.AppUsageLimit _result8 = getAppUsageLimit(_arg013, _arg112, _arg210);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            ShortcutQueryWrapper _arg113 = (ShortcutQueryWrapper) data.readTypedObject(ShortcutQueryWrapper.CREATOR);
                            UserHandle _arg211 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            ParceledListSlice _result9 = getShortcuts(_arg014, _arg113, _arg211);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            ShortcutQueryWrapper _arg114 = (ShortcutQueryWrapper) data.readTypedObject(ShortcutQueryWrapper.CREATOR);
                            UserHandle _arg212 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            AndroidFuture<List<ShortcutInfo>> _arg35 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getShortcutsAsync(_arg015, _arg114, _arg212, _arg35);
                            reply.writeNoException();
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            String _arg115 = data.readString();
                            List<String> _arg213 = data.createStringArrayList();
                            UserHandle _arg36 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            pinShortcuts(_arg016, _arg115, _arg213, _arg36);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            String _arg116 = data.readString();
                            String _arg214 = data.readString();
                            String _arg37 = data.readString();
                            Rect _arg44 = (Rect) data.readTypedObject(Rect.CREATOR);
                            Bundle _arg54 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg64 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = startShortcut(_arg017, _arg116, _arg214, _arg37, _arg44, _arg54, _arg64);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            String _arg117 = data.readString();
                            String _arg215 = data.readString();
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result11 = getShortcutIconResId(_arg018, _arg117, _arg215, _arg38);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            String _arg118 = data.readString();
                            String _arg216 = data.readString();
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result12 = getShortcutIconFd(_arg019, _arg118, _arg216, _arg39);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result13 = hasShortcutHostPermission(_arg020);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            UserHandle _arg119 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result14 = shouldHideFromSuggestions(_arg021, _arg119);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            String _arg120 = data.readString();
                            UserHandle _arg217 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            ParceledListSlice _result15 = getShortcutConfigActivities(_arg022, _arg120, _arg217);
                            reply.writeNoException();
                            reply.writeTypedObject(_result15, 1);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            ComponentName _arg121 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            UserHandle _arg218 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            IntentSender _result16 = getShortcutConfigActivityIntent(_arg023, _arg121, _arg218);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            break;
                        case 24:
                            String _arg024 = data.readString();
                            String _arg122 = data.readString();
                            String _arg219 = data.readString();
                            Bundle _arg310 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            UserHandle _arg45 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            PendingIntent _result17 = getShortcutIntent(_arg024, _arg122, _arg219, _arg310, _arg45);
                            reply.writeNoException();
                            reply.writeTypedObject(_result17, 1);
                            break;
                        case 25:
                            String _arg025 = data.readString();
                            IPackageInstallerCallback _arg123 = IPackageInstallerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerPackageInstallerCallback(_arg025, _arg123);
                            reply.writeNoException();
                            break;
                        case 26:
                            String _arg026 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result18 = getAllSessions(_arg026);
                            reply.writeNoException();
                            reply.writeTypedObject(_result18, 1);
                            break;
                        case 27:
                            String _arg027 = data.readString();
                            ShortcutQueryWrapper _arg124 = (ShortcutQueryWrapper) data.readTypedObject(ShortcutQueryWrapper.CREATOR);
                            IShortcutChangeCallback _arg220 = IShortcutChangeCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerShortcutChangeCallback(_arg027, _arg124, _arg220);
                            reply.writeNoException();
                            break;
                        case 28:
                            String _arg028 = data.readString();
                            IShortcutChangeCallback _arg125 = IShortcutChangeCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterShortcutChangeCallback(_arg028, _arg125);
                            reply.writeNoException();
                            break;
                        case 29:
                            String _arg029 = data.readString();
                            String _arg126 = data.readString();
                            List<String> _arg221 = data.createStringArrayList();
                            UserHandle _arg311 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg46 = data.readInt();
                            data.enforceNoDataAvail();
                            cacheShortcuts(_arg029, _arg126, _arg221, _arg311, _arg46);
                            reply.writeNoException();
                            break;
                        case 30:
                            String _arg030 = data.readString();
                            String _arg127 = data.readString();
                            List<String> _arg222 = data.createStringArrayList();
                            UserHandle _arg312 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg47 = data.readInt();
                            data.enforceNoDataAvail();
                            uncacheShortcuts(_arg030, _arg127, _arg222, _arg312, _arg47);
                            reply.writeNoException();
                            break;
                        case 31:
                            String _arg031 = data.readString();
                            String _arg128 = data.readString();
                            String _arg223 = data.readString();
                            int _arg313 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result19 = getShortcutIconUri(_arg031, _arg128, _arg223, _arg313);
                            reply.writeNoException();
                            reply.writeString(_result19);
                            break;
                        case 32:
                            String _arg032 = data.readString();
                            int _arg129 = data.readInt();
                            data.enforceNoDataAvail();
                            Map<String, LauncherActivityInfoInternal> _result20 = getActivityOverrides(_arg032, _arg129);
                            reply.writeNoException();
                            if (_result20 == null) {
                                reply.writeInt(-1);
                                break;
                            } else {
                                reply.writeInt(_result20.size());
                                _result20.forEach(new BiConsumer() { // from class: android.content.pm.ILauncherApps$Stub$$ExternalSyntheticLambda0
                                    @Override // java.util.function.BiConsumer
                                    public final void accept(Object obj, Object obj2) {
                                        ILauncherApps.Stub.lambda$onTransact$0(Parcel.this, (String) obj, (LauncherActivityInfoInternal) obj2);
                                    }
                                });
                                break;
                            }
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onTransact$0(Parcel reply, String k, LauncherActivityInfoInternal v) {
            reply.writeString(k);
            reply.writeTypedObject(v, 1);
        }

        /* renamed from: android.content.pm.ILauncherApps$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements ILauncherApps {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.content.p001pm.ILauncherApps
            public void addOnAppsChangedListener(String callingPackage, IOnAppsChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void removeOnAppsChangedListener(IOnAppsChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ParceledListSlice getLauncherActivities(String callingPackage, String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public LauncherActivityInfoInternal resolveLauncherActivityInternal(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    LauncherActivityInfoInternal _result = (LauncherActivityInfoInternal) _reply.readTypedObject(LauncherActivityInfoInternal.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void startSessionDetailsActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, PackageInstaller.SessionInfo sessionInfo, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(sessionInfo, 0);
                    _data.writeTypedObject(sourceBounds, 0);
                    _data.writeTypedObject(opts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void startActivityAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(sourceBounds, 0);
                    _data.writeTypedObject(opts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public PendingIntent getActivityLaunchIntent(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    PendingIntent _result = (PendingIntent) _reply.readTypedObject(PendingIntent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void showAppDetailsAsUser(IApplicationThread caller, String callingPackage, String callingFeatureId, ComponentName component, Rect sourceBounds, Bundle opts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(caller);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(sourceBounds, 0);
                    _data.writeTypedObject(opts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public boolean isPackageEnabled(String callingPackage, String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public Bundle getSuspendedPackageLauncherExtras(String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public boolean isActivityEnabled(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ApplicationInfo getApplicationInfo(String callingPackage, String packageName, int flags, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    ApplicationInfo _result = (ApplicationInfo) _reply.readTypedObject(ApplicationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public LauncherApps.AppUsageLimit getAppUsageLimit(String callingPackage, String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    LauncherApps.AppUsageLimit _result = (LauncherApps.AppUsageLimit) _reply.readTypedObject(LauncherApps.AppUsageLimit.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ParceledListSlice getShortcuts(String callingPackage, ShortcutQueryWrapper query, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(query, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void getShortcutsAsync(String callingPackage, ShortcutQueryWrapper query, UserHandle user, AndroidFuture<List<ShortcutInfo>> cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(query, 0);
                    _data.writeTypedObject(user, 0);
                    _data.writeTypedObject(cb, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void pinShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeStringList(shortcutIds);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public boolean startShortcut(String callingPackage, String packageName, String featureId, String id, Rect sourceBounds, Bundle startActivityOptions, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeString(featureId);
                    _data.writeString(id);
                    _data.writeTypedObject(sourceBounds, 0);
                    _data.writeTypedObject(startActivityOptions, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public int getShortcutIconResId(String callingPackage, String packageName, String id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeString(id);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ParcelFileDescriptor getShortcutIconFd(String callingPackage, String packageName, String id, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeString(id);
                    _data.writeInt(userId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public boolean hasShortcutHostPermission(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public boolean shouldHideFromSuggestions(String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ParceledListSlice getShortcutConfigActivities(String callingPackage, String packageName, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public IntentSender getShortcutConfigActivityIntent(String callingPackage, ComponentName component, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(component, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    IntentSender _result = (IntentSender) _reply.readTypedObject(IntentSender.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public PendingIntent getShortcutIntent(String callingPackage, String packageName, String shortcutId, Bundle opts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeString(shortcutId);
                    _data.writeTypedObject(opts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    PendingIntent _result = (PendingIntent) _reply.readTypedObject(PendingIntent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void registerPackageInstallerCallback(String callingPackage, IPackageInstallerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public ParceledListSlice getAllSessions(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void registerShortcutChangeCallback(String callingPackage, ShortcutQueryWrapper query, IShortcutChangeCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(query, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void unregisterShortcutChangeCallback(String callingPackage, IShortcutChangeCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void cacheShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user, int cacheFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeStringList(shortcutIds);
                    _data.writeTypedObject(user, 0);
                    _data.writeInt(cacheFlags);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public void uncacheShortcuts(String callingPackage, String packageName, List<String> shortcutIds, UserHandle user, int cacheFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeStringList(shortcutIds);
                    _data.writeTypedObject(user, 0);
                    _data.writeInt(cacheFlags);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public String getShortcutIconUri(String callingPackage, String packageName, String shortcutId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(packageName);
                    _data.writeString(shortcutId);
                    _data.writeInt(userId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.ILauncherApps
            public Map<String, LauncherActivityInfoInternal> getActivityOverrides(String callingPackage, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                final Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int N = _reply.readInt();
                    final Map<String, LauncherActivityInfoInternal> _result = N < 0 ? null : new HashMap<>();
                    IntStream.range(0, N).forEach(new IntConsumer() { // from class: android.content.pm.ILauncherApps$Stub$Proxy$$ExternalSyntheticLambda0
                        @Override // java.util.function.IntConsumer
                        public final void accept(int i) {
                            ILauncherApps.Stub.Proxy.lambda$getActivityOverrides$0(Parcel.this, _result, i);
                        }
                    });
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static /* synthetic */ void lambda$getActivityOverrides$0(Parcel _reply, Map _result, int i) {
                String k = _reply.readString();
                LauncherActivityInfoInternal v = (LauncherActivityInfoInternal) _reply.readTypedObject(LauncherActivityInfoInternal.CREATOR);
                _result.put(k, v);
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 31;
        }
    }
}
