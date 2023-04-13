package com.android.server.p011pm;

import android.accounts.IAccountManager;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.SuspendDialogInfo;
import android.content.pm.UserInfo;
import android.content.pm.VersionedPackage;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.dex.ISnapshotRuntimeProfileCallback;
import android.content.pm.parsing.ApkLite;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.PackageLite;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.content.rollback.RollbackManager;
import android.net.Uri;
import android.net.util.NetworkConstants;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.incremental.V4Signature;
import android.p005os.IInstalld;
import android.permission.PermissionManager;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.IntArray;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.content.InstallLocationUtils;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.art.ArtManagerLocal;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import com.android.server.p011pm.permission.PermissionAllowlist;
import com.android.server.p011pm.verify.domain.DomainVerificationShell;
import dalvik.system.DexFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import libcore.io.IoUtils;
import libcore.io.Streams;
/* renamed from: com.android.server.pm.PackageManagerShellCommand */
/* loaded from: classes2.dex */
public class PackageManagerShellCommand extends ShellCommand {
    public static final Set<String> ART_SERVICE_COMMANDS;
    public static final SecureRandom RANDOM;
    public static final Map<String, Integer> SUPPORTED_PERMISSION_FLAGS;
    public static final List<String> SUPPORTED_PERMISSION_FLAGS_LIST;
    public static final Set<String> UNSUPPORTED_INSTALL_CMD_OPTS = Set.of("--multi-package");
    public static final Set<String> UNSUPPORTED_SESSION_CREATE_OPTS = Collections.emptySet();
    public boolean mBrief;
    public boolean mComponents;
    public final Context mContext;
    public final DomainVerificationShell mDomainVerificationShell;
    public final IPackageManager mInterface;
    public final PermissionManager mPermissionManager;
    public int mQueryFlags;
    public int mTargetUser;
    public final WeakHashMap<String, Resources> mResourceCache = new WeakHashMap<>();
    public final PackageManagerInternal mPm = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
    public final LegacyPermissionManagerInternal mLegacyPermissionManager = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);

    public static String enabledSettingToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "unknown" : "disabled-until-used" : "disabled-user" : "disabled" : "enabled" : "default";
    }

    static {
        ArrayMap arrayMap = new ArrayMap();
        SUPPORTED_PERMISSION_FLAGS = arrayMap;
        SUPPORTED_PERMISSION_FLAGS_LIST = List.of("review-required", "revoked-compat", "revoke-when-requested", "user-fixed", "user-set");
        arrayMap.put("user-set", 1);
        arrayMap.put("user-fixed", 2);
        arrayMap.put("revoked-compat", 8);
        arrayMap.put("review-required", 64);
        arrayMap.put("revoke-when-requested", 128);
        ART_SERVICE_COMMANDS = Set.of("compile", "reconcile-secondary-dex-files", "force-dex-opt", "bg-dexopt-job", "cancel-bg-dexopt-job", "delete-dexopt", "dump-profiles", "snapshot-profile", "art");
        RANDOM = new SecureRandom();
    }

    public PackageManagerShellCommand(IPackageManager iPackageManager, Context context, DomainVerificationShell domainVerificationShell) {
        this.mInterface = iPackageManager;
        this.mPermissionManager = (PermissionManager) context.getSystemService(PermissionManager.class);
        this.mContext = context;
        this.mDomainVerificationShell = domainVerificationShell;
    }

    public int onCommand(String str) {
        char c;
        Boolean runCommand;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            switch (str.hashCode()) {
                case -2102802879:
                    if (str.equals("set-harmful-app-warning")) {
                        c = '8';
                        break;
                    }
                    c = 65535;
                    break;
                case -1967190973:
                    if (str.equals("install-abandon")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -1937348290:
                    if (str.equals("get-install-location")) {
                        c = 19;
                        break;
                    }
                    c = 65535;
                    break;
                case -1852006340:
                    if (str.equals("suspend")) {
                        c = ' ';
                        break;
                    }
                    c = 65535;
                    break;
                case -1846646502:
                    if (str.equals("get-max-running-users")) {
                        c = '3';
                        break;
                    }
                    c = 65535;
                    break;
                case -1741208611:
                    if (str.equals("set-installer")) {
                        c = '5';
                        break;
                    }
                    c = 65535;
                    break;
                case -1534455582:
                    if (str.equals("set-silent-updates-policy")) {
                        c = 'B';
                        break;
                    }
                    c = 65535;
                    break;
                case -1347307837:
                    if (str.equals("has-feature")) {
                        c = '7';
                        break;
                    }
                    c = 65535;
                    break;
                case -1298848381:
                    if (str.equals("enable")) {
                        c = 25;
                        break;
                    }
                    c = 65535;
                    break;
                case -1267782244:
                    if (str.equals("get-instantapp-resolver")) {
                        c = '6';
                        break;
                    }
                    c = 65535;
                    break;
                case -1231004208:
                    if (str.equals("resolve-activity")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1102348235:
                    if (str.equals("get-privapp-deny-permissions")) {
                        c = '*';
                        break;
                    }
                    c = 65535;
                    break;
                case -1091400553:
                    if (str.equals("get-oem-permissions")) {
                        c = '+';
                        break;
                    }
                    c = 65535;
                    break;
                case -1070704814:
                    if (str.equals("get-privapp-permissions")) {
                        c = ')';
                        break;
                    }
                    c = 65535;
                    break;
                case -1032029296:
                    if (str.equals("disable-user")) {
                        c = 27;
                        break;
                    }
                    c = 65535;
                    break;
                case -944325712:
                    if (str.equals("set-distracting-restriction")) {
                        c = '\"';
                        break;
                    }
                    c = 65535;
                    break;
                case -934343034:
                    if (str.equals("revoke")) {
                        c = '$';
                        break;
                    }
                    c = 65535;
                    break;
                case -840566949:
                    if (str.equals("unhide")) {
                        c = 31;
                        break;
                    }
                    c = 65535;
                    break;
                case -761393825:
                    if (str.equals("disable-verification-for-uid")) {
                        c = 'A';
                        break;
                    }
                    c = 65535;
                    break;
                case -740352344:
                    if (str.equals("install-incremental")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -703497631:
                    if (str.equals("bypass-staged-installer-check")) {
                        c = '?';
                        break;
                    }
                    c = 65535;
                    break;
                case -625596190:
                    if (str.equals("uninstall")) {
                        c = 23;
                        break;
                    }
                    c = 65535;
                    break;
                case -539710980:
                    if (str.equals("create-user")) {
                        c = '-';
                        break;
                    }
                    c = 65535;
                    break;
                case -458695741:
                    if (str.equals("query-services")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -440994401:
                    if (str.equals("query-receivers")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case -416698598:
                    if (str.equals("get-stagedsessions")) {
                        c = ':';
                        break;
                    }
                    c = 65535;
                    break;
                case -339687564:
                    if (str.equals("remove-user")) {
                        c = '.';
                        break;
                    }
                    c = 65535;
                    break;
                case -220055275:
                    if (str.equals("set-permission-enforced")) {
                        c = '(';
                        break;
                    }
                    c = 65535;
                    break;
                case -174281478:
                    if (str.equals("rename-user")) {
                        c = '/';
                        break;
                    }
                    c = 65535;
                    break;
                case -140205181:
                    if (str.equals("unsuspend")) {
                        c = '!';
                        break;
                    }
                    c = 65535;
                    break;
                case -132384343:
                    if (str.equals("install-commit")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case -129863314:
                    if (str.equals("install-create")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case -115000827:
                    if (str.equals("default-state")) {
                        c = 29;
                        break;
                    }
                    c = 65535;
                    break;
                case -87258188:
                    if (str.equals("move-primary-storage")) {
                        c = 22;
                        break;
                    }
                    c = 65535;
                    break;
                case 3292:
                    if (str.equals("gc")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 3095028:
                    if (str.equals("dump")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 3202370:
                    if (str.equals("hide")) {
                        c = 30;
                        break;
                    }
                    c = 65535;
                    break;
                case 3322014:
                    if (str.equals("list")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 3433509:
                    if (str.equals("path")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 18936394:
                    if (str.equals("move-package")) {
                        c = 21;
                        break;
                    }
                    c = 65535;
                    break;
                case 86600360:
                    if (str.equals("get-max-users")) {
                        c = '2';
                        break;
                    }
                    c = 65535;
                    break;
                case 88069748:
                    if (str.equals("supports-multiple-users")) {
                        c = '1';
                        break;
                    }
                    c = 65535;
                    break;
                case 93657776:
                    if (str.equals("install-streaming")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 94746189:
                    if (str.equals("clear")) {
                        c = 24;
                        break;
                    }
                    c = 65535;
                    break;
                case 98615580:
                    if (str.equals("grant")) {
                        c = '#';
                        break;
                    }
                    c = 65535;
                    break;
                case 107262333:
                    if (str.equals("install-existing")) {
                        c = 17;
                        break;
                    }
                    c = 65535;
                    break;
                case 139892533:
                    if (str.equals("get-harmful-app-warning")) {
                        c = '9';
                        break;
                    }
                    c = 65535;
                    break;
                case 237392952:
                    if (str.equals("install-add-session")) {
                        c = 20;
                        break;
                    }
                    c = 65535;
                    break;
                case 287820022:
                    if (str.equals("install-remove")) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case 359572742:
                    if (str.equals("reset-permissions")) {
                        c = '%';
                        break;
                    }
                    c = 65535;
                    break;
                case 377019320:
                    if (str.equals("rollback-app")) {
                        c = '<';
                        break;
                    }
                    c = 65535;
                    break;
                case 798023112:
                    if (str.equals("install-destroy")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case 826473335:
                    if (str.equals("uninstall-system-updates")) {
                        c = ';';
                        break;
                    }
                    c = 65535;
                    break;
                case 925176533:
                    if (str.equals("set-user-restriction")) {
                        c = '0';
                        break;
                    }
                    c = 65535;
                    break;
                case 1053409810:
                    if (str.equals("query-activities")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1177857340:
                    if (str.equals("trim-caches")) {
                        c = ',';
                        break;
                    }
                    c = 65535;
                    break;
                case 1396442249:
                    if (str.equals("clear-permission-flags")) {
                        c = '\'';
                        break;
                    }
                    c = 65535;
                    break;
                case 1429366290:
                    if (str.equals("set-home-activity")) {
                        c = '4';
                        break;
                    }
                    c = 65535;
                    break;
                case 1538306349:
                    if (str.equals("install-write")) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case 1671308008:
                    if (str.equals("disable")) {
                        c = 26;
                        break;
                    }
                    c = 65535;
                    break;
                case 1697997009:
                    if (str.equals("disable-until-used")) {
                        c = 28;
                        break;
                    }
                    c = 65535;
                    break;
                case 1738820372:
                    if (str.equals("set-permission-flags")) {
                        c = PackageManagerShellCommandDataLoader.ARGS_DELIM;
                        break;
                    }
                    c = 65535;
                    break;
                case 1746695602:
                    if (str.equals("set-install-location")) {
                        c = 18;
                        break;
                    }
                    c = 65535;
                    break;
                case 1757370437:
                    if (str.equals("bypass-allowed-apex-update-check")) {
                        c = '@';
                        break;
                    }
                    c = 65535;
                    break;
                case 1824799035:
                    if (str.equals("log-visibility")) {
                        c = '>';
                        break;
                    }
                    c = 65535;
                    break;
                case 1858863089:
                    if (str.equals("get-moduleinfo")) {
                        c = '=';
                        break;
                    }
                    c = 65535;
                    break;
                case 1957569947:
                    if (str.equals("install")) {
                        c = '\b';
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
                    return runPath();
                case 1:
                    return runDump();
                case 2:
                    return runList();
                case 3:
                    return runGc();
                case 4:
                    return runResolveActivity();
                case 5:
                    return runQueryIntentActivities();
                case 6:
                    return runQueryIntentServices();
                case 7:
                    return runQueryIntentReceivers();
                case '\b':
                    return runInstall();
                case '\t':
                    return runStreamingInstall();
                case '\n':
                    return runIncrementalInstall();
                case 11:
                case '\f':
                    return runInstallAbandon();
                case '\r':
                    return runInstallCommit();
                case 14:
                    return runInstallCreate();
                case 15:
                    return runInstallRemove();
                case 16:
                    return runInstallWrite();
                case 17:
                    return runInstallExisting();
                case 18:
                    return runSetInstallLocation();
                case 19:
                    return runGetInstallLocation();
                case 20:
                    return runInstallAddSession();
                case 21:
                    return runMovePackage();
                case 22:
                    return runMovePrimaryStorage();
                case 23:
                    return runUninstall();
                case 24:
                    return runClear();
                case 25:
                    return runSetEnabledSetting(1);
                case 26:
                    return runSetEnabledSetting(2);
                case 27:
                    return runSetEnabledSetting(3);
                case 28:
                    return runSetEnabledSetting(4);
                case 29:
                    return runSetEnabledSetting(0);
                case 30:
                    return runSetHiddenSetting(true);
                case 31:
                    return runSetHiddenSetting(false);
                case ' ':
                    return runSuspend(true);
                case '!':
                    return runSuspend(false);
                case '\"':
                    return runSetDistractingRestriction();
                case '#':
                    return runGrantRevokePermission(true);
                case '$':
                    return runGrantRevokePermission(false);
                case '%':
                    return runResetPermissions();
                case '&':
                    return setOrClearPermissionFlags(true);
                case '\'':
                    return setOrClearPermissionFlags(false);
                case '(':
                    return runSetPermissionEnforced();
                case ')':
                    return runGetPrivappPermissions();
                case '*':
                    return runGetPrivappDenyPermissions();
                case '+':
                    return runGetOemPermissions();
                case ',':
                    return runTrimCaches();
                case '-':
                    return runCreateUser();
                case '.':
                    return runRemoveUser();
                case '/':
                    return runRenameUser();
                case '0':
                    return runSetUserRestriction();
                case '1':
                    return runSupportsMultipleUsers();
                case '2':
                    return runGetMaxUsers();
                case '3':
                    return runGetMaxRunningUsers();
                case '4':
                    return runSetHomeActivity();
                case '5':
                    return runSetInstaller();
                case '6':
                    return runGetInstantAppResolver();
                case '7':
                    return runHasFeature();
                case '8':
                    return runSetHarmfulAppWarning();
                case '9':
                    return runGetHarmfulAppWarning();
                case ':':
                    return runListStagedSessions();
                case ';':
                    return uninstallSystemUpdates(getNextArg());
                case '<':
                    return runRollbackApp();
                case '=':
                    return runGetModuleInfo();
                case '>':
                    return runLogVisibility();
                case '?':
                    return runBypassStagedInstallerCheck();
                case '@':
                    return runBypassAllowedApexUpdateCheck();
                case 'A':
                    return runDisableVerificationForUid();
                case 'B':
                    return runSetSilentUpdatesPolicy();
                default:
                    if (ART_SERVICE_COMMANDS.contains(str)) {
                        if (DexOptHelper.useArtService()) {
                            return runArtServiceCommand();
                        }
                        try {
                            return runLegacyDexoptCommand(str);
                        } catch (Installer.LegacyDexoptDisabledException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (this.mDomainVerificationShell.runCommand(this, str) != null) {
                        return !runCommand.booleanValue();
                    }
                    String nextArg = getNextArg();
                    if (nextArg == null) {
                        if (str.equalsIgnoreCase("-l")) {
                            return runListPackages(false);
                        }
                        if (str.equalsIgnoreCase("-lf")) {
                            return runListPackages(true);
                        }
                    } else if (getNextArg() == null && str.equalsIgnoreCase("-p")) {
                        return displayPackageFilePath(nextArg, 0);
                    }
                    return handleDefaultCommands(str);
            }
        } catch (RemoteException e2) {
            outPrintWriter.println("Remote exception: " + e2);
            return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runLegacyDexoptCommand(String str) throws RemoteException, Installer.LegacyDexoptDisabledException {
        char c;
        Installer.checkLegacyDexoptDisabled();
        if (!PackageManagerServiceUtils.isRootOrShell(Binder.getCallingUid())) {
            throw new SecurityException("Dexopt shell commands need root or shell access");
        }
        str.hashCode();
        switch (str.hashCode()) {
            case -1921557090:
                if (str.equals("delete-dexopt")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1440979423:
                if (str.equals("cancel-bg-dexopt-job")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -919935069:
                if (str.equals("dump-profiles")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -444750796:
                if (str.equals("bg-dexopt-job")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 96867:
                if (str.equals("art")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 467549856:
                if (str.equals("snapshot-profile")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 950491699:
                if (str.equals("compile")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1124603675:
                if (str.equals("force-dex-opt")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1783979817:
                if (str.equals("reconcile-secondary-dex-files")) {
                    c = '\b';
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
                return runDeleteDexOpt();
            case 1:
                return cancelBgDexOptJob();
            case 2:
                return runDumpProfiles();
            case 3:
                return runBgDexOpt();
            case 4:
                getOutPrintWriter().println("ART Service not enabled");
                return -1;
            case 5:
                return runSnapshotProfile();
            case 6:
                return runCompile();
            case 7:
                return runForceDexOpt();
            case '\b':
                return runreconcileSecondaryDexFiles();
            default:
                throw new IllegalArgumentException();
        }
    }

    public final int runGetModuleInfo() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--all")) {
                    i |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                } else if (!nextOption.equals("--installed")) {
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return -1;
                }
            } else {
                String nextArg = getNextArg();
                try {
                    if (nextArg != null) {
                        ModuleInfo moduleInfo = this.mInterface.getModuleInfo(nextArg, i);
                        outPrintWriter.println(moduleInfo.toString() + " packageName: " + moduleInfo.getPackageName());
                        return 1;
                    }
                    for (ModuleInfo moduleInfo2 : this.mInterface.getInstalledModules(i)) {
                        outPrintWriter.println(moduleInfo2.toString() + " packageName: " + moduleInfo2.getPackageName());
                    }
                    return 1;
                } catch (RemoteException e) {
                    outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
                    return -1;
                }
            }
        }
    }

    public final int runLogVisibility() {
        String nextOption;
        PrintWriter outPrintWriter = getOutPrintWriter();
        do {
            boolean z = true;
            while (true) {
                nextOption = getNextOption();
                if (nextOption != null) {
                    if (!nextOption.equals("--disable")) {
                        break;
                    }
                    z = false;
                } else {
                    String nextArg = getNextArg();
                    if (nextArg != null) {
                        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setVisibilityLogging(nextArg, z);
                        return 1;
                    }
                    getErrPrintWriter().println("Error: no package specified");
                    return -1;
                }
            }
        } while (nextOption.equals("--enable"));
        outPrintWriter.println("Error: Unknown option: " + nextOption);
        return -1;
    }

    public final int runBypassStagedInstallerCheck() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            this.mInterface.getPackageInstaller().bypassNextStagedInstallerCheck(Boolean.parseBoolean(getNextArg()));
            return 0;
        } catch (RemoteException e) {
            outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return -1;
        }
    }

    public final int runBypassAllowedApexUpdateCheck() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            this.mInterface.getPackageInstaller().bypassNextAllowedApexUpdateCheck(Boolean.parseBoolean(getNextArg()));
            return 0;
        } catch (RemoteException e) {
            outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return -1;
        }
    }

    public final int runDisableVerificationForUid() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            int parseInt = Integer.parseInt(getNextArgRequired());
            if (((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getInstrumentationSourceUid(parseInt) != -1) {
                this.mInterface.getPackageInstaller().disableVerificationForUid(parseInt);
                return 0;
            }
            outPrintWriter.println("Error: must specify an instrumented uid");
            return -1;
        } catch (RemoteException e) {
            outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return -1;
        }
    }

    public final int uninstallSystemUpdates(String str) {
        List<ApplicationInfo> list;
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            IPackageInstaller packageInstaller = this.mInterface.getPackageInstaller();
            if (str == null) {
                list = this.mInterface.getInstalledApplications(1056768L, 0).getList();
            } else {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(this.mInterface.getApplicationInfo(str, 1056768L, 0));
                list = arrayList;
            }
            boolean z = false;
            for (ApplicationInfo applicationInfo : list) {
                if (applicationInfo.isUpdatedSystemApp()) {
                    outPrintWriter.println("Uninstalling updates to " + applicationInfo.packageName + "...");
                    LocalIntentReceiver localIntentReceiver = new LocalIntentReceiver();
                    packageInstaller.uninstall(new VersionedPackage(applicationInfo.packageName, applicationInfo.versionCode), (String) null, 0, localIntentReceiver.getIntentSender(), 0);
                    if (localIntentReceiver.getResult().getIntExtra("android.content.pm.extra.STATUS", 1) != 0) {
                        outPrintWriter.println("Couldn't uninstall package: " + applicationInfo.packageName);
                        z = true;
                    }
                }
            }
            if (z) {
                return 0;
            }
            outPrintWriter.println("Success");
            return 1;
        } catch (RemoteException e) {
            outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
            return 0;
        }
    }

    public final int runRollbackApp() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        long j = 60000;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--staged-ready-timeout")) {
                    j = Long.parseLong(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                String nextArgRequired = getNextArgRequired();
                if (nextArgRequired == null) {
                    outPrintWriter.println("Error: package name not specified");
                    return 1;
                }
                try {
                    Context createPackageContextAsUser = this.mContext.createPackageContextAsUser("com.android.shell", 0, Binder.getCallingUserHandle());
                    RollbackInfo rollbackInfo = null;
                    LocalIntentReceiver localIntentReceiver = new LocalIntentReceiver();
                    RollbackManager rollbackManager = (RollbackManager) createPackageContextAsUser.getSystemService(RollbackManager.class);
                    for (RollbackInfo rollbackInfo2 : rollbackManager.getAvailableRollbacks()) {
                        Iterator it = rollbackInfo2.getPackages().iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (nextArgRequired.equals(((PackageRollbackInfo) it.next()).getPackageName())) {
                                    rollbackInfo = rollbackInfo2;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    if (rollbackInfo == null) {
                        outPrintWriter.println("No available rollbacks for: " + nextArgRequired);
                        return 1;
                    }
                    rollbackManager.commitRollback(rollbackInfo.getRollbackId(), Collections.emptyList(), localIntentReceiver.getIntentSender());
                    Intent result = localIntentReceiver.getResult();
                    if (result.getIntExtra("android.content.rollback.extra.STATUS", 1) != 0) {
                        outPrintWriter.println("Failure [" + result.getStringExtra("android.content.rollback.extra.STATUS_MESSAGE") + "]");
                        return 1;
                    } else if (rollbackInfo.isStaged() && j > 0) {
                        return doWaitForStagedSessionReady(rollbackInfo.getCommittedSessionId(), j, outPrintWriter);
                    } else {
                        outPrintWriter.println("Success");
                        return 0;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public final void setParamsSize(InstallParams installParams, List<String> list) {
        if (installParams.sessionParams.sizeBytes != -1 || PackageManagerShellCommandDataLoader.STDIN_PATH.equals(list.get(0))) {
            return;
        }
        ParseTypeImpl forDefaultParsing = ParseTypeImpl.forDefaultParsing();
        long j = 0;
        for (String str : list) {
            ParcelFileDescriptor openFileForSystem = openFileForSystem(str, "r");
            if (openFileForSystem == null) {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Error: Can't open file: " + str);
                throw new IllegalArgumentException("Error: Can't open file: " + str);
            }
            try {
                try {
                    ParseResult parseApkLite = ApkLiteParseUtils.parseApkLite(forDefaultParsing.reset(), openFileForSystem.getFileDescriptor(), str, 0);
                    if (parseApkLite.isError()) {
                        throw new IllegalArgumentException("Error: Failed to parse APK file: " + str + ": " + parseApkLite.getErrorMessage(), parseApkLite.getException());
                    }
                    ApkLite apkLite = (ApkLite) parseApkLite.getResult();
                    j += InstallLocationUtils.calculateInstalledSize(new PackageLite((String) null, apkLite.getPath(), apkLite, (String[]) null, (boolean[]) null, (String[]) null, (String[]) null, (String[]) null, (int[]) null, apkLite.getTargetSdkVersion(), (Set[]) null, (Set[]) null), installParams.sessionParams.abiOverride, openFileForSystem.getFileDescriptor());
                    try {
                        openFileForSystem.close();
                    } catch (IOException unused) {
                    }
                } catch (IOException e) {
                    PrintWriter errPrintWriter2 = getErrPrintWriter();
                    errPrintWriter2.println("Error: Failed to parse APK file: " + str);
                    throw new IllegalArgumentException("Error: Failed to parse APK file: " + str, e);
                }
            } catch (Throwable th) {
                try {
                    openFileForSystem.close();
                } catch (IOException unused2) {
                }
                throw th;
            }
        }
        installParams.sessionParams.setSize(j);
    }

    public final int displayPackageFilePath(String str, int i) throws RemoteException {
        String[] strArr;
        PackageInfo packageInfo = this.mInterface.getPackageInfo(str, 1073741824L, i);
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return 1;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.print("package:");
        outPrintWriter.println(packageInfo.applicationInfo.sourceDir);
        if (!ArrayUtils.isEmpty(packageInfo.applicationInfo.splitSourceDirs)) {
            for (String str2 : packageInfo.applicationInfo.splitSourceDirs) {
                outPrintWriter.print("package:");
                outPrintWriter.println(str2);
            }
        }
        return 0;
    }

    public final int runPath() throws RemoteException {
        String nextOption = getNextOption();
        int parseUserArg = (nextOption == null || !nextOption.equals("--user")) ? 0 : UserHandle.parseUserArg(getNextArgRequired());
        String nextArgRequired = getNextArgRequired();
        if (nextArgRequired == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        return displayPackageFilePath(nextArgRequired, translateUserId(parseUserArg, -10000, "runPath"));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runList() throws RemoteException {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to list");
            return -1;
        }
        switch (nextArg.hashCode()) {
            case -1126096540:
                if (nextArg.equals("staged-sessions")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -997447790:
                if (nextArg.equals("permission-groups")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -807062458:
                if (nextArg.equals("package")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -290659267:
                if (nextArg.equals("features")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 3525497:
                if (nextArg.equals("sdks")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 111578632:
                if (nextArg.equals("users")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 544550766:
                if (nextArg.equals("instrumentation")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 750867693:
                if (nextArg.equals("packages")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 812757657:
                if (nextArg.equals("libraries")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1133704324:
                if (nextArg.equals("permissions")) {
                    c = '\t';
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
                return runListStagedSessions();
            case 1:
                return runListPermissionGroups();
            case 2:
            case 7:
                return runListPackages(false);
            case 3:
                return runListFeatures();
            case 4:
                return runListSdks();
            case 5:
                ServiceManager.getService("user").shellCommand(getInFileDescriptor(), getOutFileDescriptor(), getErrFileDescriptor(), new String[]{"list"}, getShellCallback(), adoptResultReceiver());
                return 0;
            case 6:
                return runListInstrumentation();
            case '\b':
                return runListLibraries();
            case '\t':
                return runListPermissions();
            default:
                outPrintWriter.println("Error: unknown list type '" + nextArg + "'");
                return -1;
        }
    }

    public final int runGc() throws RemoteException {
        Runtime.getRuntime().gc();
        getOutPrintWriter().println("Ok");
        return 0;
    }

    public final int runListFeatures() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        List list = this.mInterface.getSystemAvailableFeatures().getList();
        Collections.sort(list, new Comparator<FeatureInfo>() { // from class: com.android.server.pm.PackageManagerShellCommand.1
            @Override // java.util.Comparator
            public int compare(FeatureInfo featureInfo, FeatureInfo featureInfo2) {
                String str = featureInfo.name;
                String str2 = featureInfo2.name;
                if (str == str2) {
                    return 0;
                }
                if (str == null) {
                    return -1;
                }
                if (str2 == null) {
                    return 1;
                }
                return str.compareTo(str2);
            }
        });
        int size = list != null ? list.size() : 0;
        for (int i = 0; i < size; i++) {
            FeatureInfo featureInfo = (FeatureInfo) list.get(i);
            outPrintWriter.print("feature:");
            String str = featureInfo.name;
            if (str != null) {
                outPrintWriter.print(str);
                if (featureInfo.version > 0) {
                    outPrintWriter.print("=");
                    outPrintWriter.print(featureInfo.version);
                }
                outPrintWriter.println();
            } else {
                outPrintWriter.println("reqGlEsVersion=0x" + Integer.toHexString(featureInfo.reqGlEsVersion));
            }
        }
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0043 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0024 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runListInstrumentation() throws RemoteException {
        boolean z;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String str = null;
        boolean z2 = false;
        while (true) {
            try {
                String nextArg = getNextArg();
                if (nextArg != null) {
                    if (nextArg.hashCode() == 1497 && nextArg.equals("-f")) {
                        z = false;
                        if (z) {
                            z2 = true;
                        } else if (nextArg.charAt(0) == '-') {
                            outPrintWriter.println("Error: Unknown option: " + nextArg);
                            return -1;
                        } else {
                            str = nextArg;
                        }
                    }
                    z = true;
                    if (z) {
                    }
                } else {
                    List list = this.mInterface.queryInstrumentationAsUser(str, 4202496, 0).getList();
                    Collections.sort(list, new Comparator<InstrumentationInfo>() { // from class: com.android.server.pm.PackageManagerShellCommand.2
                        @Override // java.util.Comparator
                        public int compare(InstrumentationInfo instrumentationInfo, InstrumentationInfo instrumentationInfo2) {
                            return instrumentationInfo.targetPackage.compareTo(instrumentationInfo2.targetPackage);
                        }
                    });
                    int size = list != null ? list.size() : 0;
                    for (int i = 0; i < size; i++) {
                        InstrumentationInfo instrumentationInfo = (InstrumentationInfo) list.get(i);
                        outPrintWriter.print("instrumentation:");
                        if (z2) {
                            outPrintWriter.print(instrumentationInfo.sourceDir);
                            outPrintWriter.print("=");
                        }
                        outPrintWriter.print(new ComponentName(instrumentationInfo.packageName, instrumentationInfo.name).flattenToShortString());
                        outPrintWriter.print(" (target=");
                        outPrintWriter.print(instrumentationInfo.targetPackage);
                        outPrintWriter.println(")");
                    }
                    return 0;
                }
            } catch (RuntimeException e) {
                outPrintWriter.println("Error: " + e.toString());
                return -1;
            }
        }
    }

    public final int runListLibraries() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        ArrayList arrayList = new ArrayList();
        for (String str : this.mInterface.getSystemSharedLibraryNames()) {
            arrayList.add(str);
        }
        Collections.sort(arrayList, new Comparator<String>() { // from class: com.android.server.pm.PackageManagerShellCommand.3
            @Override // java.util.Comparator
            public int compare(String str2, String str3) {
                if (str2 == str3) {
                    return 0;
                }
                if (str2 == null) {
                    return -1;
                }
                if (str3 == null) {
                    return 1;
                }
                return str2.compareTo(str3);
            }
        });
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            outPrintWriter.print("library:");
            outPrintWriter.println((String) arrayList.get(i));
        }
        return 0;
    }

    public final int runListPackages(boolean z) throws RemoteException {
        return runListPackages(z, false);
    }

    public final int runListSdks() throws RemoteException {
        return runListPackages(false, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:139:0x0264, code lost:
        if (r2 == null) goto L129;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runListPackages(boolean z, boolean z2) throws RemoteException {
        String str;
        int i;
        int i2;
        long j;
        String str2;
        HashMap hashMap;
        SharedLibraryInfo sharedLibraryInfo;
        char c;
        String str3 = z2 ? "sdk:" : "package:";
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z3 = z;
        int i3 = -1;
        int i4 = 0;
        int i5 = -1;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        boolean z9 = false;
        boolean z10 = false;
        boolean z11 = false;
        while (true) {
            try {
                String nextOption = getNextOption();
                if (nextOption != null) {
                    switch (nextOption.hashCode()) {
                        case -493830763:
                            if (nextOption.equals("--show-versioncode")) {
                                c = '\n';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1446:
                            if (nextOption.equals("-3")) {
                                c = '\t';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1480:
                            if (nextOption.equals("-U")) {
                                c = 7;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1492:
                            if (nextOption.equals("-a")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1495:
                            if (nextOption.equals("-d")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1496:
                            if (nextOption.equals("-e")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1497:
                            if (nextOption.equals("-f")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case NetworkConstants.ETHER_MTU /* 1500 */:
                            if (nextOption.equals("-i")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1503:
                            if (nextOption.equals("-l")) {
                                c = 5;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1510:
                            if (nextOption.equals("-s")) {
                                c = 6;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1512:
                            if (nextOption.equals("-u")) {
                                c = '\b';
                                break;
                            }
                            c = 65535;
                            break;
                        case 43014832:
                            if (nextOption.equals("--uid")) {
                                c = 14;
                                break;
                            }
                            c = 65535;
                            break;
                        case 774814767:
                            if (nextOption.equals("--factory-only")) {
                                c = '\f';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1333469547:
                            if (nextOption.equals("--user")) {
                                c = '\r';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1464517361:
                            if (nextOption.equals("--match-libraries")) {
                                c = 15;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1809263575:
                            if (nextOption.equals("--apex-only")) {
                                c = 11;
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
                            z4 = true;
                            continue;
                        case 1:
                            z5 = true;
                            continue;
                        case 2:
                            i4 = 4202496 | i4 | 536870912;
                            continue;
                        case 3:
                            z3 = true;
                            continue;
                        case 4:
                            z9 = true;
                            continue;
                        case 5:
                        case 6:
                            z6 = true;
                            continue;
                        case 7:
                            break;
                        case '\b':
                            i4 |= IInstalld.FLAG_FORCE;
                            continue;
                        case '\t':
                            z7 = true;
                            continue;
                        case '\n':
                            z11 = true;
                            continue;
                        case 11:
                            i4 |= 1073741824;
                            z8 = true;
                            continue;
                        case '\f':
                            i4 |= 2097152;
                            continue;
                        case '\r':
                            i3 = UserHandle.parseUserArg(getNextArgRequired());
                            continue;
                        case 14:
                            i5 = Integer.parseInt(getNextArgRequired());
                            break;
                        case 15:
                            i4 |= 67108864;
                            continue;
                        default:
                            outPrintWriter.println("Error: Unknown option: " + nextOption);
                            return -1;
                    }
                    z10 = true;
                } else {
                    String nextArg = getNextArg();
                    int[] userIds = i3 == -1 ? ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds() : new int[]{i3};
                    if (z2) {
                        i4 |= 67108864;
                    }
                    HashMap hashMap2 = new HashMap();
                    PrintWriter printWriter = outPrintWriter;
                    int length = userIds.length;
                    boolean z12 = z10;
                    int i6 = 0;
                    while (i6 < length) {
                        int i7 = length;
                        int i8 = userIds[i6];
                        int[] iArr = userIds;
                        int i9 = i6;
                        boolean z13 = z3;
                        HashMap hashMap3 = hashMap2;
                        long j2 = i4;
                        List list = this.mInterface.getInstalledPackages(j2, translateUserId(i8, 0, "runListPackages")).getList();
                        int size = list.size();
                        int i10 = i4;
                        int i11 = 0;
                        while (i11 < size) {
                            List list2 = list;
                            PackageInfo packageInfo = (PackageInfo) list.get(i11);
                            int i12 = size;
                            StringBuilder sb = new StringBuilder();
                            int i13 = i11;
                            if (nextArg == null || packageInfo.packageName.contains(nextArg)) {
                                boolean z14 = packageInfo.isApex;
                                str = nextArg;
                                if (i5 == -1 || z14 || packageInfo.applicationInfo.uid == i5) {
                                    boolean z15 = (z14 || (packageInfo.applicationInfo.flags & 1) == 0) ? false : true;
                                    i = i5;
                                    boolean z16 = !z14 && packageInfo.applicationInfo.enabled;
                                    if ((!z4 || !z16) && ((!z5 || z16) && ((!z6 || z15) && ((!z7 || !z15) && (!z8 || z14))))) {
                                        if (z2) {
                                            ParceledListSlice declaredSharedLibraries = this.mInterface.getDeclaredSharedLibraries(packageInfo.packageName, j2, i8);
                                            if (declaredSharedLibraries != null) {
                                                List list3 = declaredSharedLibraries.getList();
                                                int size2 = list3.size();
                                                i2 = i8;
                                                int i14 = 0;
                                                while (true) {
                                                    if (i14 < size2) {
                                                        List list4 = list3;
                                                        j = j2;
                                                        if (((SharedLibraryInfo) list3.get(i14)).getType() == 3) {
                                                            str2 = sharedLibraryInfo.getName() + XmlUtils.STRING_ARRAY_SEPARATOR + sharedLibraryInfo.getLongVersion();
                                                        } else {
                                                            i14++;
                                                            list3 = list4;
                                                            j2 = j;
                                                        }
                                                    } else {
                                                        j = j2;
                                                        str2 = null;
                                                    }
                                                }
                                            }
                                        } else {
                                            i2 = i8;
                                            j = j2;
                                            str2 = packageInfo.packageName;
                                        }
                                        sb.append(str3);
                                        if (z13) {
                                            sb.append(packageInfo.applicationInfo.sourceDir);
                                            sb.append("=");
                                        }
                                        sb.append(str2);
                                        if (z11) {
                                            sb.append(" versionCode:");
                                            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                                            if (applicationInfo != null) {
                                                sb.append(applicationInfo.longVersionCode);
                                            } else {
                                                sb.append(packageInfo.getLongVersionCode());
                                            }
                                        }
                                        if (z9) {
                                            sb.append("  installer=");
                                            sb.append(this.mInterface.getInstallerPackageName(packageInfo.packageName));
                                        }
                                        hashMap = hashMap3;
                                        List list5 = (List) hashMap.computeIfAbsent(sb.toString(), new Function() { // from class: com.android.server.pm.PackageManagerShellCommand$$ExternalSyntheticLambda0
                                            @Override // java.util.function.Function
                                            public final Object apply(Object obj) {
                                                List lambda$runListPackages$0;
                                                lambda$runListPackages$0 = PackageManagerShellCommand.lambda$runListPackages$0((String) obj);
                                                return lambda$runListPackages$0;
                                            }
                                        });
                                        if (z12 && !z14) {
                                            list5.add(String.valueOf(packageInfo.applicationInfo.uid));
                                        }
                                        i11 = i13 + 1;
                                        hashMap3 = hashMap;
                                        size = i12;
                                        list = list2;
                                        nextArg = str;
                                        i5 = i;
                                        i8 = i2;
                                        j2 = j;
                                    }
                                    i2 = i8;
                                    j = j2;
                                    hashMap = hashMap3;
                                    i11 = i13 + 1;
                                    hashMap3 = hashMap;
                                    size = i12;
                                    list = list2;
                                    nextArg = str;
                                    i5 = i;
                                    i8 = i2;
                                    j2 = j;
                                } else {
                                    i2 = i8;
                                }
                            } else {
                                i2 = i8;
                                str = nextArg;
                            }
                            j = j2;
                            i = i5;
                            hashMap = hashMap3;
                            i11 = i13 + 1;
                            hashMap3 = hashMap;
                            size = i12;
                            list = list2;
                            nextArg = str;
                            i5 = i;
                            i8 = i2;
                            j2 = j;
                        }
                        i6 = i9 + 1;
                        hashMap2 = hashMap3;
                        length = i7;
                        userIds = iArr;
                        z3 = z13;
                        i4 = i10;
                    }
                    for (Map.Entry entry : hashMap2.entrySet()) {
                        PrintWriter printWriter2 = printWriter;
                        printWriter2.print((String) entry.getKey());
                        List list6 = (List) entry.getValue();
                        if (!list6.isEmpty()) {
                            printWriter2.print(" uid:");
                            printWriter2.print(String.join(",", list6));
                        }
                        printWriter2.println();
                        printWriter = printWriter2;
                    }
                    return 0;
                }
            } catch (RuntimeException e) {
                outPrintWriter.println("Error: " + e.toString());
                return -1;
            }
        }
    }

    public static /* synthetic */ List lambda$runListPackages$0(String str) {
        return new ArrayList();
    }

    public final int runListPermissionGroups() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        List allPermissionGroups = this.mPermissionManager.getAllPermissionGroups(0);
        int size = allPermissionGroups.size();
        for (int i = 0; i < size; i++) {
            outPrintWriter.print("permission group:");
            outPrintWriter.println(((PermissionGroupInfo) allPermissionGroups.get(i)).name);
        }
        return 0;
    }

    public final int runListPermissions() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                ArrayList<String> arrayList = new ArrayList<>();
                if (z2) {
                    List allPermissionGroups = this.mPermissionManager.getAllPermissionGroups(0);
                    int size = allPermissionGroups.size();
                    for (int i = 0; i < size; i++) {
                        arrayList.add(((PermissionGroupInfo) allPermissionGroups.get(i)).name);
                    }
                    arrayList.add(null);
                } else {
                    arrayList.add(getNextArg());
                }
                if (z) {
                    outPrintWriter.println("Dangerous Permissions:");
                    outPrintWriter.println("");
                    doListPermissions(arrayList, z2, z3, z4, 1, 1);
                    if (z5) {
                        outPrintWriter.println("Normal Permissions:");
                        outPrintWriter.println("");
                        doListPermissions(arrayList, z2, z3, z4, 0, 0);
                    }
                } else if (z5) {
                    outPrintWriter.println("Dangerous and Normal Permissions:");
                    outPrintWriter.println("");
                    doListPermissions(arrayList, z2, z3, z4, 0, 1);
                } else {
                    outPrintWriter.println("All Permissions:");
                    outPrintWriter.println("");
                    doListPermissions(arrayList, z2, z3, z4, -10000, FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                }
                return 0;
            }
            char c = 65535;
            switch (nextOption.hashCode()) {
                case 1495:
                    if (nextOption.equals("-d")) {
                        c = 0;
                        break;
                    }
                    break;
                case 1497:
                    if (nextOption.equals("-f")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1498:
                    if (nextOption.equals("-g")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1510:
                    if (nextOption.equals("-s")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1512:
                    if (nextOption.equals("-u")) {
                        c = 4;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    z = true;
                    break;
                case 1:
                    z3 = true;
                    break;
                case 2:
                    z2 = true;
                    break;
                case 3:
                    z2 = true;
                    z3 = true;
                    z4 = true;
                    break;
                case 4:
                    z5 = true;
                    break;
                default:
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
            }
        }
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommand$SessionDump */
    /* loaded from: classes2.dex */
    public static class SessionDump {
        public boolean onlyParent;
        public boolean onlyReady;
        public boolean onlySessionId;

        public SessionDump() {
        }
    }

    public final boolean setSessionFlag(String str, SessionDump sessionDump) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2056597429:
                if (str.equals("--only-parent")) {
                    c = 0;
                    break;
                }
                break;
            case -1847964944:
                if (str.equals("--only-sessionid")) {
                    c = 1;
                    break;
                }
                break;
            case 1321081314:
                if (str.equals("--only-ready")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                sessionDump.onlyParent = true;
                break;
            case 1:
                sessionDump.onlySessionId = true;
                break;
            case 2:
                sessionDump.onlyReady = true;
                break;
            default:
                return false;
        }
        return true;
    }

    public final int runListStagedSessions() {
        String nextOption;
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(getOutPrintWriter(), "  ", 120);
        try {
            SessionDump sessionDump = new SessionDump();
            do {
                nextOption = getNextOption();
                if (nextOption == null) {
                    try {
                        printSessionList(indentingPrintWriter, this.mInterface.getPackageInstaller().getStagedSessions().getList(), sessionDump);
                        indentingPrintWriter.close();
                        return 1;
                    } catch (RemoteException e) {
                        indentingPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
                        indentingPrintWriter.close();
                        return -1;
                    }
                }
            } while (setSessionFlag(nextOption, sessionDump));
            indentingPrintWriter.println("Error: Unknown option: " + nextOption);
            indentingPrintWriter.close();
            return -1;
        } catch (Throwable th) {
            try {
                indentingPrintWriter.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public final void printSessionList(IndentingPrintWriter indentingPrintWriter, List<PackageInstaller.SessionInfo> list, SessionDump sessionDump) {
        SparseArray sparseArray = new SparseArray(list.size());
        for (PackageInstaller.SessionInfo sessionInfo : list) {
            sparseArray.put(sessionInfo.getSessionId(), sessionInfo);
        }
        for (PackageInstaller.SessionInfo sessionInfo2 : list) {
            if (!sessionDump.onlyReady || sessionInfo2.isStagedSessionReady()) {
                if (sessionInfo2.getParentSessionId() == -1) {
                    printSession(indentingPrintWriter, sessionInfo2, sessionDump);
                    if (sessionInfo2.isMultiPackage() && !sessionDump.onlyParent) {
                        indentingPrintWriter.increaseIndent();
                        int[] childSessionIds = sessionInfo2.getChildSessionIds();
                        for (int i = 0; i < childSessionIds.length; i++) {
                            PackageInstaller.SessionInfo sessionInfo3 = (PackageInstaller.SessionInfo) sparseArray.get(childSessionIds[i]);
                            if (sessionInfo3 == null) {
                                if (sessionDump.onlySessionId) {
                                    indentingPrintWriter.println(childSessionIds[i]);
                                } else {
                                    indentingPrintWriter.println("sessionId = " + childSessionIds[i] + "; not found");
                                }
                            } else {
                                printSession(indentingPrintWriter, sessionInfo3, sessionDump);
                            }
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                }
            }
        }
    }

    public static void printSession(PrintWriter printWriter, PackageInstaller.SessionInfo sessionInfo, SessionDump sessionDump) {
        if (sessionDump.onlySessionId) {
            printWriter.println(sessionInfo.getSessionId());
            return;
        }
        printWriter.println("sessionId = " + sessionInfo.getSessionId() + "; appPackageName = " + sessionInfo.getAppPackageName() + "; isStaged = " + sessionInfo.isStaged() + "; isReady = " + sessionInfo.isStagedSessionReady() + "; isApplied = " + sessionInfo.isStagedSessionApplied() + "; isFailed = " + sessionInfo.isStagedSessionFailed() + "; errorMsg = " + sessionInfo.getStagedSessionErrorMessage() + ";");
    }

    public final Intent parseIntentAndUser() throws URISyntaxException {
        this.mTargetUser = -2;
        this.mBrief = false;
        this.mComponents = false;
        Intent parseCommandArgs = Intent.parseCommandArgs(this, new Intent.CommandOptionHandler() { // from class: com.android.server.pm.PackageManagerShellCommand.4
            public boolean handleOption(String str, ShellCommand shellCommand) {
                if ("--user".equals(str)) {
                    PackageManagerShellCommand.this.mTargetUser = UserHandle.parseUserArg(shellCommand.getNextArgRequired());
                    return true;
                } else if ("--brief".equals(str)) {
                    PackageManagerShellCommand.this.mBrief = true;
                    return true;
                } else if ("--components".equals(str)) {
                    PackageManagerShellCommand.this.mComponents = true;
                    return true;
                } else if ("--query-flags".equals(str)) {
                    PackageManagerShellCommand.this.mQueryFlags = Integer.decode(shellCommand.getNextArgRequired()).intValue();
                    return true;
                } else {
                    return false;
                }
            }
        });
        this.mTargetUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), this.mTargetUser, false, false, null, null);
        return parseCommandArgs;
    }

    public final void printResolveInfo(PrintWriterPrinter printWriterPrinter, String str, ResolveInfo resolveInfo, boolean z, boolean z2) {
        ComponentName componentName;
        if (z || z2) {
            if (resolveInfo.activityInfo != null) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
            } else if (resolveInfo.serviceInfo != null) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            } else if (resolveInfo.providerInfo != null) {
                ProviderInfo providerInfo = resolveInfo.providerInfo;
                componentName = new ComponentName(providerInfo.packageName, providerInfo.name);
            } else {
                componentName = null;
            }
            if (componentName != null) {
                if (!z2) {
                    printWriterPrinter.println(str + "priority=" + resolveInfo.priority + " preferredOrder=" + resolveInfo.preferredOrder + " match=0x" + Integer.toHexString(resolveInfo.match) + " specificIndex=" + resolveInfo.specificIndex + " isDefault=" + resolveInfo.isDefault);
                }
                printWriterPrinter.println(str + componentName.flattenToShortString());
                return;
            }
        }
        resolveInfo.dump(printWriterPrinter, str);
    }

    public final int runResolveActivity() {
        try {
            Intent parseIntentAndUser = parseIntentAndUser();
            try {
                ResolveInfo resolveIntent = this.mInterface.resolveIntent(parseIntentAndUser, parseIntentAndUser.getType(), this.mQueryFlags, this.mTargetUser);
                PrintWriter outPrintWriter = getOutPrintWriter();
                if (resolveIntent == null) {
                    outPrintWriter.println("No activity found");
                    return 0;
                }
                printResolveInfo(new PrintWriterPrinter(outPrintWriter), "", resolveIntent, this.mBrief, this.mComponents);
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    public final int runQueryIntentActivities() {
        try {
            Intent parseIntentAndUser = parseIntentAndUser();
            try {
                List list = this.mInterface.queryIntentActivities(parseIntentAndUser, parseIntentAndUser.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter outPrintWriter = getOutPrintWriter();
                if (list != null && list.size() > 0) {
                    if (!this.mComponents) {
                        outPrintWriter.print(list.size());
                        outPrintWriter.println(" activities found:");
                        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(outPrintWriter);
                        for (int i = 0; i < list.size(); i++) {
                            outPrintWriter.print("  Activity #");
                            outPrintWriter.print(i);
                            outPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                            printResolveInfo(printWriterPrinter, "    ", (ResolveInfo) list.get(i), this.mBrief, this.mComponents);
                        }
                    } else {
                        PrintWriterPrinter printWriterPrinter2 = new PrintWriterPrinter(outPrintWriter);
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            printResolveInfo(printWriterPrinter2, "", (ResolveInfo) list.get(i2), this.mBrief, this.mComponents);
                        }
                    }
                    return 0;
                }
                outPrintWriter.println("No activities found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    public final int runQueryIntentServices() {
        try {
            Intent parseIntentAndUser = parseIntentAndUser();
            try {
                List list = this.mInterface.queryIntentServices(parseIntentAndUser, parseIntentAndUser.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter outPrintWriter = getOutPrintWriter();
                if (list != null && list.size() > 0) {
                    if (!this.mComponents) {
                        outPrintWriter.print(list.size());
                        outPrintWriter.println(" services found:");
                        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(outPrintWriter);
                        for (int i = 0; i < list.size(); i++) {
                            outPrintWriter.print("  Service #");
                            outPrintWriter.print(i);
                            outPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                            printResolveInfo(printWriterPrinter, "    ", (ResolveInfo) list.get(i), this.mBrief, this.mComponents);
                        }
                    } else {
                        PrintWriterPrinter printWriterPrinter2 = new PrintWriterPrinter(outPrintWriter);
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            printResolveInfo(printWriterPrinter2, "", (ResolveInfo) list.get(i2), this.mBrief, this.mComponents);
                        }
                    }
                    return 0;
                }
                outPrintWriter.println("No services found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    public final int runQueryIntentReceivers() {
        try {
            Intent parseIntentAndUser = parseIntentAndUser();
            try {
                List list = this.mInterface.queryIntentReceivers(parseIntentAndUser, parseIntentAndUser.getType(), this.mQueryFlags, this.mTargetUser).getList();
                PrintWriter outPrintWriter = getOutPrintWriter();
                if (list != null && list.size() > 0) {
                    if (!this.mComponents) {
                        outPrintWriter.print(list.size());
                        outPrintWriter.println(" receivers found:");
                        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(outPrintWriter);
                        for (int i = 0; i < list.size(); i++) {
                            outPrintWriter.print("  Receiver #");
                            outPrintWriter.print(i);
                            outPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                            printResolveInfo(printWriterPrinter, "    ", (ResolveInfo) list.get(i), this.mBrief, this.mComponents);
                        }
                    } else {
                        PrintWriterPrinter printWriterPrinter2 = new PrintWriterPrinter(outPrintWriter);
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            printResolveInfo(printWriterPrinter2, "", (ResolveInfo) list.get(i2), this.mBrief, this.mComponents);
                        }
                    }
                    return 0;
                }
                outPrintWriter.println("No receivers found");
                return 0;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed calling service", e);
            }
        } catch (URISyntaxException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }

    public final int runStreamingInstall() throws RemoteException {
        InstallParams makeInstallParams = makeInstallParams(UNSUPPORTED_INSTALL_CMD_OPTS);
        PackageInstaller.SessionParams sessionParams = makeInstallParams.sessionParams;
        if (sessionParams.dataLoaderParams == null) {
            sessionParams.setDataLoaderParams(PackageManagerShellCommandDataLoader.getStreamingDataLoaderParams(this));
        }
        return doRunInstall(makeInstallParams);
    }

    public final int runIncrementalInstall() throws RemoteException {
        InstallParams makeInstallParams = makeInstallParams(UNSUPPORTED_INSTALL_CMD_OPTS);
        PackageInstaller.SessionParams sessionParams = makeInstallParams.sessionParams;
        if (sessionParams.dataLoaderParams == null) {
            sessionParams.setDataLoaderParams(PackageManagerShellCommandDataLoader.getIncrementalDataLoaderParams(this));
        }
        return doRunInstall(makeInstallParams);
    }

    public final int runInstall() throws RemoteException {
        return doRunInstall(makeInstallParams(UNSUPPORTED_INSTALL_CMD_OPTS));
    }

    public final int doRunInstall(InstallParams installParams) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.SessionParams sessionParams = installParams.sessionParams;
        boolean z = true;
        boolean z2 = sessionParams.dataLoaderParams != null;
        boolean z3 = (sessionParams.installFlags & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0;
        ArrayList<String> remainingArgs = getRemainingArgs();
        boolean z4 = remainingArgs.isEmpty() || PackageManagerShellCommandDataLoader.STDIN_PATH.equals(remainingArgs.get(0));
        boolean z5 = remainingArgs.size() > 1;
        if (z4 && installParams.sessionParams.sizeBytes == -1) {
            outPrintWriter.println("Error: must either specify a package size or an APK file");
            return 1;
        } else if (z3 && z5) {
            outPrintWriter.println("Error: can't specify SPLIT(s) for APEX");
            return 1;
        } else {
            if (!z2) {
                if (z4 && z5) {
                    outPrintWriter.println("Error: can't specify SPLIT(s) along with STDIN");
                    return 1;
                } else if (remainingArgs.isEmpty()) {
                    remainingArgs.add(PackageManagerShellCommandDataLoader.STDIN_PATH);
                } else {
                    setParamsSize(installParams, remainingArgs);
                }
            }
            int doCreateSession = doCreateSession(installParams.sessionParams, installParams.installerPackageName, installParams.userId);
            try {
                if (z2) {
                    if (doAddFiles(doCreateSession, remainingArgs, installParams.sessionParams.sizeBytes, z3) != 0) {
                        try {
                            doAbandonSession(doCreateSession, false);
                        } catch (Exception unused) {
                        }
                        return 1;
                    }
                } else if (doWriteSplits(doCreateSession, remainingArgs, installParams.sessionParams.sizeBytes, z3) != 0) {
                    try {
                        doAbandonSession(doCreateSession, false);
                    } catch (Exception unused2) {
                    }
                    return 1;
                }
                if (doCommitSession(doCreateSession, false) != 0) {
                    try {
                        doAbandonSession(doCreateSession, false);
                    } catch (Exception unused3) {
                    }
                    return 1;
                }
                try {
                    if (installParams.sessionParams.isStaged) {
                        long j = installParams.stagedReadyTimeoutMs;
                        if (j > 0) {
                            return doWaitForStagedSessionReady(doCreateSession, j, outPrintWriter);
                        }
                    }
                    outPrintWriter.println("Success");
                    return 0;
                } catch (Throwable th) {
                    th = th;
                    z = false;
                    if (z) {
                        try {
                            doAbandonSession(doCreateSession, false);
                        } catch (Exception unused4) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0089, code lost:
        r14.println("Failure [failed to retrieve SessionInfo]");
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x008e, code lost:
        return 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int doWaitForStagedSessionReady(int i, long j, PrintWriter printWriter) throws RemoteException {
        Preconditions.checkArgument(j > 0);
        PackageInstaller.SessionInfo sessionInfo = this.mInterface.getPackageInstaller().getSessionInfo(i);
        if (sessionInfo == null) {
            printWriter.println("Failure [Unknown session " + i + "]");
            return 1;
        } else if (!sessionInfo.isStaged()) {
            printWriter.println("Failure [Session " + i + " is not a staged session]");
            return 1;
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis + j;
            while (currentTimeMillis < j2 && (sessionInfo == null || (!sessionInfo.isStagedSessionReady() && !sessionInfo.isStagedSessionFailed()))) {
                SystemClock.sleep(Math.min(j2 - currentTimeMillis, 100L));
                currentTimeMillis = System.currentTimeMillis();
                sessionInfo = this.mInterface.getPackageInstaller().getSessionInfo(i);
            }
            if (!sessionInfo.isStagedSessionReady() && !sessionInfo.isStagedSessionFailed()) {
                printWriter.println("Failure [timed out after " + j + " ms]");
                return 1;
            } else if (!sessionInfo.isStagedSessionReady()) {
                printWriter.println("Error [" + sessionInfo.getStagedSessionErrorCode() + "] [" + sessionInfo.getStagedSessionErrorMessage() + "]");
                return 1;
            } else {
                printWriter.println("Success. Reboot device to apply staged session");
                return 0;
            }
        }
    }

    public final int runInstallAbandon() throws RemoteException {
        return doAbandonSession(Integer.parseInt(getNextArg()), true);
    }

    public final int runInstallCommit() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        long j = 60000;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--staged-ready-timeout")) {
                    j = Long.parseLong(getNextArgRequired());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                int parseInt = Integer.parseInt(getNextArg());
                if (doCommitSession(parseInt, false) != 0) {
                    return 1;
                }
                PackageInstaller.SessionInfo sessionInfo = this.mInterface.getPackageInstaller().getSessionInfo(parseInt);
                if (sessionInfo != null && sessionInfo.isStaged() && j > 0) {
                    return doWaitForStagedSessionReady(parseInt, j, outPrintWriter);
                }
                outPrintWriter.println("Success");
                return 0;
            }
        }
    }

    public final int runInstallCreate() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        InstallParams makeInstallParams = makeInstallParams(UNSUPPORTED_SESSION_CREATE_OPTS);
        int doCreateSession = doCreateSession(makeInstallParams.sessionParams, makeInstallParams.installerPackageName, makeInstallParams.userId);
        outPrintWriter.println("Success: created install session [" + doCreateSession + "]");
        return 0;
    }

    public final int runInstallWrite() throws RemoteException {
        long j = -1;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("-S")) {
                    j = Long.parseLong(getNextArg());
                } else {
                    throw new IllegalArgumentException("Unknown option: " + nextOption);
                }
            } else {
                return doWriteSplit(Integer.parseInt(getNextArg()), getNextArg(), j, getNextArg(), true);
            }
        }
    }

    public final int runInstallAddSession() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int parseInt = Integer.parseInt(getNextArg());
        IntArray intArray = new IntArray();
        while (true) {
            String nextArg = getNextArg();
            if (nextArg == null) {
                break;
            }
            intArray.add(Integer.parseInt(nextArg));
        }
        if (intArray.size() == 0) {
            outPrintWriter.println("Error: At least two sessions are required.");
            return 1;
        }
        return doInstallAddSession(parseInt, intArray.toArray(), true);
    }

    public final int runInstallRemove() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int parseInt = Integer.parseInt(getNextArg());
        ArrayList<String> remainingArgs = getRemainingArgs();
        if (remainingArgs.isEmpty()) {
            outPrintWriter.println("Error: split name not specified");
            return 1;
        }
        return doRemoveSplits(parseInt, remainingArgs, true);
    }

    public final int runInstallExisting() throws RemoteException {
        int i;
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i2 = -2;
        int i3 = 4194304;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                char c = 65535;
                switch (nextOption.hashCode()) {
                    case -951415743:
                        if (nextOption.equals("--instant")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1051781117:
                        if (nextOption.equals("--ephemeral")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1333024815:
                        if (nextOption.equals("--full")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1333511957:
                        if (nextOption.equals("--wait")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1494514835:
                        if (nextOption.equals("--restrict-permissions")) {
                            c = 5;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 1:
                        i = (i3 | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) & (-16385);
                        break;
                    case 2:
                        i = (i3 & (-2049)) | 16384;
                        break;
                    case 3:
                        i2 = UserHandle.parseUserArg(getNextArgRequired());
                        continue;
                    case 4:
                        z = true;
                        continue;
                    case 5:
                        i = (-4194305) & i3;
                        break;
                    default:
                        outPrintWriter.println("Error: Unknown option: " + nextOption);
                        return 1;
                }
                i3 = i;
            } else {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    outPrintWriter.println("Error: package name not specified");
                    return 1;
                }
                int translateUserId = translateUserId(i2, -10000, "runInstallExisting");
                try {
                    if (z) {
                        LocalIntentReceiver localIntentReceiver = new LocalIntentReceiver();
                        IPackageInstaller packageInstaller = this.mInterface.getPackageInstaller();
                        outPrintWriter.println("Installing package " + nextArg + " for user: " + translateUserId);
                        packageInstaller.installExistingPackage(nextArg, i3, 0, localIntentReceiver.getIntentSender(), translateUserId, (List) null);
                        int intExtra = localIntentReceiver.getResult().getIntExtra("android.content.pm.extra.STATUS", 1);
                        outPrintWriter.println("Received intent for package install");
                        return intExtra == 0 ? 0 : 1;
                    } else if (this.mInterface.installExistingPackageAsUser(nextArg, translateUserId, i3, 0, (List) null) == -3) {
                        throw new PackageManager.NameNotFoundException("Package " + nextArg + " doesn't exist");
                    } else {
                        outPrintWriter.println("Package " + nextArg + " installed for user: " + translateUserId);
                        return 0;
                    }
                } catch (PackageManager.NameNotFoundException | RemoteException e) {
                    outPrintWriter.println(e.toString());
                    return 1;
                }
            }
        }
    }

    public final int runSetInstallLocation() throws RemoteException {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no install location specified.");
            return 1;
        }
        try {
            if (this.mInterface.setInstallLocation(Integer.parseInt(nextArg))) {
                return 0;
            }
            getErrPrintWriter().println("Error: install location has to be a number.");
            return 1;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: install location has to be a number.");
            return 1;
        }
    }

    public final int runGetInstallLocation() throws RemoteException {
        int installLocation = this.mInterface.getInstallLocation();
        String str = installLocation == 0 ? "auto" : installLocation == 1 ? "internal" : installLocation == 2 ? "external" : "invalid";
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println(installLocation + "[" + str + "]");
        return 0;
    }

    public int runMovePackage() throws RemoteException {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: package name not specified");
            return 1;
        }
        String nextArg2 = getNextArg();
        if ("internal".equals(nextArg2)) {
            nextArg2 = null;
        }
        int movePackage = this.mInterface.movePackage(nextArg, nextArg2);
        int moveStatus = this.mInterface.getMoveStatus(movePackage);
        while (!PackageManager.isMoveStatusFinished(moveStatus)) {
            SystemClock.sleep(1000L);
            moveStatus = this.mInterface.getMoveStatus(movePackage);
        }
        if (moveStatus == -100) {
            getOutPrintWriter().println("Success");
            return 0;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Failure [" + moveStatus + "]");
        return 1;
    }

    public int runMovePrimaryStorage() throws RemoteException {
        String nextArg = getNextArg();
        if ("internal".equals(nextArg)) {
            nextArg = null;
        }
        int movePrimaryStorage = this.mInterface.movePrimaryStorage(nextArg);
        int moveStatus = this.mInterface.getMoveStatus(movePrimaryStorage);
        while (!PackageManager.isMoveStatusFinished(moveStatus)) {
            SystemClock.sleep(1000L);
            moveStatus = this.mInterface.getMoveStatus(movePrimaryStorage);
        }
        if (moveStatus == -100) {
            getOutPrintWriter().println("Success");
            return 0;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Failure [" + moveStatus + "]");
        return 1;
    }

    public final int runCompile() throws RemoteException {
        List<String> singletonList;
        String str;
        boolean z;
        boolean performDexOptMode;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        while (true) {
            String nextOption = getNextOption();
            char c = 65535;
            if (nextOption == null) {
                boolean z6 = str3 != null;
                boolean z7 = str4 != null;
                if (z6 && z7) {
                    outPrintWriter.println("Cannot use compilation filter (\"-m\") and compilation reason (\"-r\") at the same time");
                    return 1;
                } else if (!z6 && !z7) {
                    outPrintWriter.println("Cannot run without any of compilation filter (\"-m\") and compilation reason (\"-r\")");
                    return 1;
                } else if (z2 && str5 != null) {
                    outPrintWriter.println("-a cannot be specified together with --split");
                    return 1;
                } else if (z4 && str5 != null) {
                    outPrintWriter.println("--secondary-dex cannot be specified together with --split");
                    return 1;
                } else {
                    if (z6) {
                        if (!DexFile.isValidCompilerFilter(str3)) {
                            outPrintWriter.println("Error: \"" + str3 + "\" is not a valid compilation filter.");
                            return 1;
                        }
                        str2 = str3;
                    }
                    if (z7) {
                        int i = 0;
                        while (true) {
                            String[] strArr = PackageManagerServiceCompilerMapping.REASON_STRINGS;
                            if (i >= strArr.length) {
                                i = -1;
                            } else if (!strArr[i].equals(str4)) {
                                i++;
                            }
                        }
                        if (i == -1) {
                            outPrintWriter.println("Error: Unknown compilation reason: " + str4);
                            return 1;
                        }
                        str2 = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(i);
                    }
                    if (z2) {
                        singletonList = this.mInterface.getAllPackages();
                    } else {
                        String nextArg = getNextArg();
                        if (nextArg == null) {
                            outPrintWriter.println("Error: package name not specified");
                            return 1;
                        }
                        singletonList = Collections.singletonList(nextArg);
                    }
                    ArrayList<String> arrayList = new ArrayList();
                    int i2 = 0;
                    for (String str6 : singletonList) {
                        if (z5) {
                            this.mInterface.clearApplicationProfileData(str6);
                        }
                        if (z2) {
                            StringBuilder sb = new StringBuilder();
                            i2++;
                            sb.append(i2);
                            sb.append("/");
                            sb.append(singletonList.size());
                            sb.append(": ");
                            sb.append(str6);
                            outPrintWriter.println(sb.toString());
                            outPrintWriter.flush();
                        }
                        int i3 = i2;
                        if (z4) {
                            performDexOptMode = this.mInterface.performDexOptSecondary(str6, str2, z3);
                            str = str6;
                            z = z3;
                        } else {
                            str = str6;
                            z = z3;
                            performDexOptMode = this.mInterface.performDexOptMode(str6, true, str2, z3, true, str5);
                        }
                        if (!performDexOptMode) {
                            arrayList.add(str);
                        }
                        i2 = i3;
                        z3 = z;
                    }
                    if (arrayList.isEmpty()) {
                        outPrintWriter.println("Success");
                        return 0;
                    } else if (arrayList.size() == 1) {
                        outPrintWriter.println("Failure: package " + ((String) arrayList.get(0)) + " could not be compiled");
                        return 1;
                    } else {
                        outPrintWriter.print("Failure: the following packages could not be compiled: ");
                        boolean z8 = true;
                        for (String str7 : arrayList) {
                            if (z8) {
                                z8 = false;
                            } else {
                                outPrintWriter.print(", ");
                            }
                            outPrintWriter.print(str7);
                        }
                        outPrintWriter.println();
                        return 1;
                    }
                }
            }
            switch (nextOption.hashCode()) {
                case -1615291473:
                    if (nextOption.equals("--reset")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1614046854:
                    if (nextOption.equals("--split")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1492:
                    if (nextOption.equals("-a")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1494:
                    if (nextOption.equals("-c")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1497:
                    if (nextOption.equals("-f")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1504:
                    if (nextOption.equals("-m")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1509:
                    if (nextOption.equals("-r")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1269477022:
                    if (nextOption.equals("--secondary-dex")) {
                        c = 7;
                        break;
                    }
                    break;
                case 1690714782:
                    if (nextOption.equals("--check-prof")) {
                        c = '\b';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    str4 = "install";
                    z3 = true;
                    z5 = true;
                    break;
                case 1:
                    str5 = getNextArgRequired();
                    break;
                case 2:
                    z2 = true;
                    break;
                case 3:
                    z5 = true;
                    break;
                case 4:
                    z3 = true;
                    break;
                case 5:
                    str3 = getNextArgRequired();
                    break;
                case 6:
                    str4 = getNextArgRequired();
                    break;
                case 7:
                    z4 = true;
                    break;
                case '\b':
                    getNextArgRequired();
                    outPrintWriter.println("Warning: Ignoring obsolete flag --check-prof - it is unconditionally enabled now");
                    break;
                default:
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
            }
        }
    }

    public final int runreconcileSecondaryDexFiles() throws RemoteException, Installer.LegacyDexoptDisabledException {
        this.mPm.legacyReconcileSecondaryDexFiles(getNextArg());
        return 0;
    }

    public int runForceDexOpt() throws RemoteException, Installer.LegacyDexoptDisabledException {
        this.mPm.legacyForceDexOpt(getNextArgRequired());
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runBgDexOpt() throws RemoteException, Installer.LegacyDexoptDisabledException {
        char c;
        String nextOption = getNextOption();
        if (nextOption == null) {
            ArrayList arrayList = new ArrayList();
            while (true) {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    break;
                }
                arrayList.add(nextArg);
            }
            BackgroundDexOptService service = BackgroundDexOptService.getService();
            if (arrayList.isEmpty()) {
                arrayList = null;
            }
            if (!service.runBackgroundDexoptJob(arrayList)) {
                getOutPrintWriter().println("Failure");
                return -1;
            }
        } else {
            String nextArg2 = getNextArg();
            if (nextArg2 != null) {
                getErrPrintWriter().println("Invalid argument: " + nextArg2);
                return -1;
            }
            switch (nextOption.hashCode()) {
                case -1237677752:
                    if (nextOption.equals("--disable")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1032289306:
                    if (nextOption.equals("--cancel")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1101165347:
                    if (nextOption.equals("--enable")) {
                        c = 2;
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
                    BackgroundDexOptService.getService().setDisableJobSchedulerJobs(true);
                    break;
                case 1:
                    return cancelBgDexOptJob();
                case 2:
                    BackgroundDexOptService.getService().setDisableJobSchedulerJobs(false);
                    break;
                default:
                    getErrPrintWriter().println("Unknown option: " + nextOption);
                    return -1;
            }
        }
        getOutPrintWriter().println("Success");
        return 0;
    }

    public final int cancelBgDexOptJob() throws RemoteException, Installer.LegacyDexoptDisabledException {
        BackgroundDexOptService.getService().cancelBackgroundDexoptJob();
        getOutPrintWriter().println("Success");
        return 0;
    }

    public final int runDeleteDexOpt() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (TextUtils.isEmpty(nextArg)) {
            outPrintWriter.println("Error: no package name");
            return 1;
        }
        long deleteOatArtifactsOfPackage = this.mPm.deleteOatArtifactsOfPackage(nextArg);
        if (deleteOatArtifactsOfPackage < 0) {
            outPrintWriter.println("Error: delete failed");
            return 1;
        }
        outPrintWriter.println("Success: freed " + deleteOatArtifactsOfPackage + " bytes");
        Slog.i("PackageManagerShellCommand", "delete-dexopt " + nextArg + " ,freed " + deleteOatArtifactsOfPackage + " bytes");
        return 0;
    }

    public final int runDumpProfiles() throws RemoteException, Installer.LegacyDexoptDisabledException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("--dump-classes-and-methods")) {
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
                z = true;
            } else {
                this.mPm.legacyDumpProfiles(getNextArg(), z);
                return 0;
            }
        }
    }

    public final int runSnapshotProfile() throws RemoteException {
        String str;
        String str2;
        String str3;
        FileOutputStream fileOutputStream;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        boolean equals = PackageManagerShellCommandDataLoader.PACKAGE.equals(nextArg);
        String str4 = null;
        while (true) {
            String nextArg2 = getNextArg();
            if (nextArg2 != null) {
                if (!nextArg2.equals("--code-path")) {
                    outPrintWriter.write("Unknown arg: " + nextArg2);
                    return -1;
                } else if (equals) {
                    outPrintWriter.write("--code-path cannot be used for the boot image.");
                    return -1;
                } else {
                    str4 = getNextArg();
                }
            } else {
                if (equals) {
                    str = null;
                    str2 = str4;
                } else {
                    PackageInfo packageInfo = this.mInterface.getPackageInfo(nextArg, 0L, 0);
                    if (packageInfo == null) {
                        outPrintWriter.write("Package not found " + nextArg);
                        return -1;
                    }
                    String baseCodePath = packageInfo.applicationInfo.getBaseCodePath();
                    if (str4 == null) {
                        str2 = baseCodePath;
                        str = str2;
                    } else {
                        str2 = str4;
                        str = baseCodePath;
                    }
                }
                SnapshotRuntimeProfileCallback snapshotRuntimeProfileCallback = new SnapshotRuntimeProfileCallback();
                String str5 = Binder.getCallingUid() == 0 ? "root" : "com.android.shell";
                if (!this.mInterface.getArtManager().isRuntimeProfilingEnabled(equals ? 1 : 0, str5)) {
                    outPrintWriter.println("Error: Runtime profiling is not enabled");
                    return -1;
                }
                this.mInterface.getArtManager().snapshotRuntimeProfile(equals ? 1 : 0, nextArg, str2, snapshotRuntimeProfileCallback, str5);
                if (!snapshotRuntimeProfileCallback.waitTillDone()) {
                    outPrintWriter.println("Error: callback not called");
                    return snapshotRuntimeProfileCallback.mErrCode;
                }
                try {
                    ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(snapshotRuntimeProfileCallback.mProfileReadFd);
                    try {
                        if (!equals && !Objects.equals(str, str2)) {
                            str3 = PackageManagerShellCommandDataLoader.STDIN_PATH + new File(str2).getName();
                            String str6 = "/data/misc/profman/" + nextArg + str3 + ".prof";
                            fileOutputStream = new FileOutputStream(str6);
                            Streams.copy(autoCloseInputStream, fileOutputStream);
                            fileOutputStream.close();
                            Os.chmod(str6, FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
                            autoCloseInputStream.close();
                            return 0;
                        }
                        Streams.copy(autoCloseInputStream, fileOutputStream);
                        fileOutputStream.close();
                        Os.chmod(str6, FrameworkStatsLog.VBMETA_DIGEST_REPORTED);
                        autoCloseInputStream.close();
                        return 0;
                    } catch (Throwable th) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                    str3 = "";
                    String str62 = "/data/misc/profman/" + nextArg + str3 + ".prof";
                    fileOutputStream = new FileOutputStream(str62);
                } catch (ErrnoException | IOException e) {
                    outPrintWriter.println("Error when reading the profile fd: " + e.getMessage());
                    e.printStackTrace(outPrintWriter);
                    return -1;
                }
            }
        }
    }

    public final ArrayList<String> getRemainingArgs() {
        ArrayList<String> arrayList = new ArrayList<>();
        while (true) {
            String nextArg = getNextArg();
            if (nextArg == null) {
                return arrayList;
            }
            arrayList.add(nextArg);
        }
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommand$SnapshotRuntimeProfileCallback */
    /* loaded from: classes2.dex */
    public static class SnapshotRuntimeProfileCallback extends ISnapshotRuntimeProfileCallback.Stub {
        public CountDownLatch mDoneSignal;
        public int mErrCode;
        public ParcelFileDescriptor mProfileReadFd;
        public boolean mSuccess;

        public SnapshotRuntimeProfileCallback() {
            this.mSuccess = false;
            this.mErrCode = -1;
            this.mProfileReadFd = null;
            this.mDoneSignal = new CountDownLatch(1);
        }

        public void onSuccess(ParcelFileDescriptor parcelFileDescriptor) {
            this.mSuccess = true;
            try {
                this.mProfileReadFd = parcelFileDescriptor.dup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mDoneSignal.countDown();
        }

        public void onError(int i) {
            this.mSuccess = false;
            this.mErrCode = i;
            this.mDoneSignal.countDown();
        }

        public boolean waitTillDone() {
            boolean z;
            try {
                z = this.mDoneSignal.await(10000000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
                z = false;
            }
            return z && this.mSuccess;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runUninstall() throws RemoteException {
        String str;
        Intent result;
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        long j = -1;
        int i = 0;
        int i2 = -1;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1502:
                        if (nextOption.equals("-k")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1884113221:
                        if (nextOption.equals("--versionCode")) {
                            c = 2;
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
                        i |= 1;
                        break;
                    case 1:
                        i2 = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    case 2:
                        j = Long.parseLong(getNextArgRequired());
                        break;
                    default:
                        outPrintWriter.println("Error: Unknown option: " + nextOption);
                        return 1;
                }
            } else {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    outPrintWriter.println("Error: package name not specified");
                    return 1;
                }
                ArrayList<String> remainingArgs = getRemainingArgs();
                if (!remainingArgs.isEmpty()) {
                    return runRemoveSplits(nextArg, remainingArgs);
                }
                int i3 = i2 == -1 ? i | 2 : i;
                int translateUserId = translateUserId(i2, 0, "runUninstall");
                LocalIntentReceiver localIntentReceiver = new LocalIntentReceiver();
                PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                if (packageManagerInternal.isApexPackage(nextArg)) {
                    packageManagerInternal.uninstallApex(nextArg, j, translateUserId, localIntentReceiver.getIntentSender(), i3);
                    str = "]";
                } else {
                    if ((i3 & 2) == 0) {
                        PackageInfo packageInfo = this.mInterface.getPackageInfo(nextArg, 67108864L, translateUserId);
                        if (packageInfo == null) {
                            outPrintWriter.println("Failure [not installed for " + translateUserId + "]");
                            return 1;
                        }
                        if ((packageInfo.applicationInfo.flags & 1) != 0) {
                            i3 |= 4;
                        }
                    }
                    str = "]";
                    this.mInterface.getPackageInstaller().uninstall(new VersionedPackage(nextArg, j), (String) null, i3, localIntentReceiver.getIntentSender(), translateUserId);
                }
                if (localIntentReceiver.getResult().getIntExtra("android.content.pm.extra.STATUS", 1) == 0) {
                    outPrintWriter.println("Success");
                    return 0;
                }
                outPrintWriter.println("Failure [" + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + str);
                return 1;
            }
        }
    }

    public final int runRemoveSplits(String str, Collection<String> collection) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(2);
        sessionParams.installFlags = 2 | sessionParams.installFlags;
        sessionParams.appPackageName = str;
        int doCreateSession = doCreateSession(sessionParams, null, -1);
        boolean z = true;
        try {
            if (doRemoveSplits(doCreateSession, collection, false) == 0) {
                if (doCommitSession(doCreateSession, false) == 0) {
                    try {
                        outPrintWriter.println("Success");
                        return 0;
                    } catch (Throwable th) {
                        th = th;
                        z = false;
                        if (z) {
                            try {
                                doAbandonSession(doCreateSession, false);
                            } catch (RuntimeException unused) {
                            }
                        }
                        throw th;
                    }
                }
                try {
                    doAbandonSession(doCreateSession, false);
                } catch (RuntimeException unused2) {
                }
                return 1;
            }
            try {
                doAbandonSession(doCreateSession, false);
            } catch (RuntimeException unused3) {
            }
            return 1;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommand$ClearDataObserver */
    /* loaded from: classes2.dex */
    public static class ClearDataObserver extends IPackageDataObserver.Stub {
        public boolean finished;
        public boolean result;

        public void onRemoveCompleted(String str, boolean z) throws RemoteException {
            synchronized (this) {
                this.finished = true;
                this.result = z;
                notifyAll();
            }
        }
    }

    public final int runClear() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i = 0;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--cache-only")) {
                    z = true;
                } else if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    getErrPrintWriter().println("Error: no package specified");
                    return 1;
                }
                int translateUserId = translateUserId(i, -10000, "runClear");
                ClearDataObserver clearDataObserver = new ClearDataObserver();
                if (!z) {
                    ActivityManager.getService().clearApplicationUserData(nextArg, false, clearDataObserver, translateUserId);
                } else {
                    this.mInterface.deleteApplicationCacheFilesAsUser(nextArg, translateUserId, clearDataObserver);
                }
                synchronized (clearDataObserver) {
                    while (!clearDataObserver.finished) {
                        try {
                            clearDataObserver.wait();
                        } catch (InterruptedException unused) {
                        }
                    }
                }
                if (clearDataObserver.result) {
                    getOutPrintWriter().println("Success");
                    return 0;
                }
                getErrPrintWriter().println("Failed");
                return 1;
            }
        }
    }

    public final int runSetEnabledSetting(int i) throws RemoteException {
        String nextOption = getNextOption();
        int parseUserArg = (nextOption == null || !nextOption.equals("--user")) ? 0 : UserHandle.parseUserArg(getNextArgRequired());
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package or component specified");
            return 1;
        }
        int translateUserId = translateUserId(parseUserArg, -10000, "runSetEnabledSetting");
        ComponentName unflattenFromString = ComponentName.unflattenFromString(nextArg);
        if (unflattenFromString == null) {
            IPackageManager iPackageManager = this.mInterface;
            iPackageManager.setApplicationEnabledSetting(nextArg, i, 0, translateUserId, "shell:" + Process.myUid());
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Package " + nextArg + " new state: " + enabledSettingToString(this.mInterface.getApplicationEnabledSetting(nextArg, translateUserId)));
            return 0;
        }
        this.mInterface.setComponentEnabledSetting(unflattenFromString, i, 0, translateUserId);
        PrintWriter outPrintWriter2 = getOutPrintWriter();
        outPrintWriter2.println("Component " + unflattenFromString.toShortString() + " new state: " + enabledSettingToString(this.mInterface.getComponentEnabledSetting(unflattenFromString, translateUserId)));
        return 0;
    }

    public final int runSetHiddenSetting(boolean z) throws RemoteException {
        String nextOption = getNextOption();
        int parseUserArg = (nextOption == null || !nextOption.equals("--user")) ? 0 : UserHandle.parseUserArg(getNextArgRequired());
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package or component specified");
            return 1;
        }
        int translateUserId = translateUserId(parseUserArg, -10000, "runSetHiddenSetting");
        this.mInterface.setApplicationHiddenSettingAsUser(nextArg, z, translateUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Package " + nextArg + " new hidden state: " + this.mInterface.getApplicationHiddenSettingAsUser(nextArg, translateUserId));
        return 0;
    }

    public final int runSetDistractingRestriction() {
        String[] distractingPackageRestrictionsAsUser;
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i = 0;
        int i2 = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--flag")) {
                    String nextArgRequired = getNextArgRequired();
                    nextArgRequired.hashCode();
                    if (nextArgRequired.equals("hide-notifications")) {
                        i2 |= 2;
                    } else if (!nextArgRequired.equals("hide-from-suggestions")) {
                        outPrintWriter.println("Unrecognized flag: " + nextArgRequired);
                        return 1;
                    } else {
                        i2 |= 1;
                    }
                } else if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                ArrayList<String> remainingArgs = getRemainingArgs();
                if (remainingArgs.isEmpty()) {
                    outPrintWriter.println("Error: package name not specified");
                    return 1;
                }
                try {
                    if (this.mInterface.setDistractingPackageRestrictionsAsUser((String[]) remainingArgs.toArray(new String[0]), i2, translateUserId(i, -10000, "set-distracting")).length > 0) {
                        outPrintWriter.println("Could not set restriction for: " + Arrays.toString(distractingPackageRestrictionsAsUser));
                        return 1;
                    }
                    return 0;
                } catch (RemoteException | IllegalArgumentException e) {
                    outPrintWriter.println(e.toString());
                    return 1;
                }
            }
        }
    }

    public final int runSuspend(boolean z) {
        int translateUserId;
        String str;
        PrintWriter outPrintWriter = getOutPrintWriter();
        PersistableBundle persistableBundle = new PersistableBundle();
        PersistableBundle persistableBundle2 = new PersistableBundle();
        int i = 0;
        String str2 = null;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                char c = 65535;
                switch (nextOption.hashCode()) {
                    case -39471105:
                        if (nextOption.equals("--dialogMessage")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 42995488:
                        if (nextOption.equals("--aed")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 42995496:
                        if (nextOption.equals("--ael")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 42995503:
                        if (nextOption.equals("--aes")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 43006059:
                        if (nextOption.equals("--led")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 43006067:
                        if (nextOption.equals("--lel")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 43006074:
                        if (nextOption.equals("--les")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 7;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        str2 = getNextArgRequired();
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        String nextArgRequired = getNextArgRequired();
                        String nextArgRequired2 = getNextArgRequired();
                        if (z) {
                            PersistableBundle persistableBundle3 = nextOption.startsWith("--a") ? persistableBundle : persistableBundle2;
                            char charAt = nextOption.charAt(4);
                            if (charAt != 'd') {
                                if (charAt != 'l') {
                                    if (charAt != 's') {
                                        break;
                                    } else {
                                        persistableBundle3.putString(nextArgRequired, nextArgRequired2);
                                        break;
                                    }
                                } else {
                                    persistableBundle3.putLong(nextArgRequired, Long.valueOf(nextArgRequired2).longValue());
                                    break;
                                }
                            } else {
                                persistableBundle3.putDouble(nextArgRequired, Double.valueOf(nextArgRequired2).doubleValue());
                                break;
                            }
                        } else {
                            break;
                        }
                    case 7:
                        i = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        outPrintWriter.println("Error: Unknown option: " + nextOption);
                        return 1;
                }
            } else {
                ArrayList<String> remainingArgs = getRemainingArgs();
                if (remainingArgs.isEmpty()) {
                    outPrintWriter.println("Error: package name not specified");
                    return 1;
                }
                String str3 = Binder.getCallingUid() == 0 ? "root" : "com.android.shell";
                try {
                    this.mInterface.setPackagesSuspendedAsUser((String[]) remainingArgs.toArray(new String[0]), z, persistableBundle.size() > 0 ? persistableBundle : null, persistableBundle2.size() > 0 ? persistableBundle2 : null, !TextUtils.isEmpty(str2) ? new SuspendDialogInfo.Builder().setMessage(str2).build() : null, str3, translateUserId(i, -10000, "runSuspend"));
                    for (int i2 = 0; i2 < remainingArgs.size(); i2++) {
                        outPrintWriter.println("Package " + remainingArgs.get(i2) + " new suspended state: " + this.mInterface.isPackageSuspendedForUser(str, translateUserId));
                    }
                    return 0;
                } catch (RemoteException | IllegalArgumentException e) {
                    outPrintWriter.println(e.toString());
                    return 1;
                }
            }
        }
    }

    public final int runGrantRevokePermission(boolean z) throws RemoteException {
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                break;
            } else if (nextOption.equals("--user")) {
                i = UserHandle.parseUserArg(getNextArgRequired());
            }
        }
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            getErrPrintWriter().println("Error: no permission specified");
            return 1;
        }
        UserHandle of = UserHandle.of(translateUserId(i, -10000, "runGrantRevokePermission"));
        if (z) {
            this.mPermissionManager.grantRuntimePermission(nextArg, nextArg2, of);
        } else {
            this.mPermissionManager.revokeRuntimePermission(nextArg, nextArg2, of, (String) null);
        }
        return 0;
    }

    public final int runResetPermissions() throws RemoteException {
        this.mLegacyPermissionManager.resetRuntimePermissions();
        return 0;
    }

    public final int setOrClearPermissionFlags(boolean z) {
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                break;
            } else if (nextOption.equals("--user")) {
                i = UserHandle.parseUserArg(getNextArgRequired());
            }
        }
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            getErrPrintWriter().println("Error: no permission specified");
            return 1;
        }
        String nextArg3 = getNextArg();
        if (nextArg3 == null) {
            getErrPrintWriter().println("Error: no permission flags specified");
            return 1;
        }
        int i2 = 0;
        while (nextArg3 != null) {
            Map<String, Integer> map = SUPPORTED_PERMISSION_FLAGS;
            if (!map.containsKey(nextArg3)) {
                getErrPrintWriter().println("Error: specified flag " + nextArg3 + " is not one of " + SUPPORTED_PERMISSION_FLAGS_LIST);
                return 1;
            }
            i2 |= map.get(nextArg3).intValue();
            nextArg3 = getNextArg();
        }
        this.mPermissionManager.updatePermissionFlags(nextArg, nextArg2, i2, z ? i2 : 0, UserHandle.of(translateUserId(i, -10000, "runGrantRevokePermission")));
        return 0;
    }

    public final int runSetPermissionEnforced() throws RemoteException {
        if (getNextArg() == null) {
            getErrPrintWriter().println("Error: no permission specified");
            return 1;
        } else if (getNextArg() == null) {
            getErrPrintWriter().println("Error: no enforcement specified");
            return 1;
        } else {
            return 0;
        }
    }

    public final boolean isVendorApp(String str) {
        try {
            PackageInfo packageInfo = this.mInterface.getPackageInfo(str, 4194304L, 0);
            if (packageInfo != null) {
                return packageInfo.applicationInfo.isVendor();
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public final boolean isProductApp(String str) {
        try {
            PackageInfo packageInfo = this.mInterface.getPackageInfo(str, 4194304L, 0);
            if (packageInfo != null) {
                return packageInfo.applicationInfo.isProduct();
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public final boolean isSystemExtApp(String str) {
        try {
            PackageInfo packageInfo = this.mInterface.getPackageInfo(str, 4194304L, 0);
            if (packageInfo != null) {
                return packageInfo.applicationInfo.isSystemExt();
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public final String getApexPackageNameContainingPackage(String str) {
        return ApexManager.getInstance().getActiveApexPackageNameContainingPackage(str);
    }

    public final boolean isApexApp(String str) {
        return getApexPackageNameContainingPackage(str) != null;
    }

    public final int runGetPrivappPermissions() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        getOutPrintWriter().println(getPrivAppPermissionsString(nextArg, true));
        return 0;
    }

    public final int runGetPrivappDenyPermissions() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        getOutPrintWriter().println(getPrivAppPermissionsString(nextArg, false));
        return 0;
    }

    public final String getPrivAppPermissionsString(String str, boolean z) {
        ArrayMap<String, ArrayMap<String, Boolean>> privilegedAppAllowlist;
        PermissionAllowlist permissionAllowlist = SystemConfig.getInstance().getPermissionAllowlist();
        if (isVendorApp(str)) {
            privilegedAppAllowlist = permissionAllowlist.getVendorPrivilegedAppAllowlist();
        } else if (isProductApp(str)) {
            privilegedAppAllowlist = permissionAllowlist.getProductPrivilegedAppAllowlist();
        } else if (isSystemExtApp(str)) {
            privilegedAppAllowlist = permissionAllowlist.getSystemExtPrivilegedAppAllowlist();
        } else if (isApexApp(str)) {
            privilegedAppAllowlist = permissionAllowlist.getApexPrivilegedAppAllowlists().get(ApexManager.getInstance().getApexModuleNameForPackageName(getApexPackageNameContainingPackage(str)));
        } else {
            privilegedAppAllowlist = permissionAllowlist.getPrivilegedAppAllowlist();
        }
        ArrayMap<String, Boolean> arrayMap = privilegedAppAllowlist != null ? privilegedAppAllowlist.get(str) : null;
        if (arrayMap == null) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        int size = arrayMap.size();
        boolean z2 = true;
        for (int i = 0; i < size; i++) {
            if (arrayMap.valueAt(i).booleanValue() == z) {
                if (z2) {
                    z2 = false;
                } else {
                    sb.append(", ");
                }
                sb.append(arrayMap.keyAt(i));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public final int runGetOemPermissions() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified.");
            return 1;
        }
        ArrayMap<String, Boolean> arrayMap = SystemConfig.getInstance().getPermissionAllowlist().getOemAppAllowlist().get(nextArg);
        if (arrayMap == null || arrayMap.isEmpty()) {
            getOutPrintWriter().println("{}");
            return 0;
        }
        arrayMap.forEach(new BiConsumer() { // from class: com.android.server.pm.PackageManagerShellCommand$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PackageManagerShellCommand.this.lambda$runGetOemPermissions$1((String) obj, (Boolean) obj2);
            }
        });
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runGetOemPermissions$1(String str, Boolean bool) {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println(str + " granted:" + bool);
    }

    public final int runTrimCaches() throws RemoteException {
        long j;
        long j2;
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no size specified");
            return 1;
        }
        int length = nextArg.length() - 1;
        char charAt = nextArg.charAt(length);
        if (charAt < '0' || charAt > '9') {
            if (charAt == 'K' || charAt == 'k') {
                j = 1024;
            } else if (charAt == 'M' || charAt == 'm') {
                j = 1048576;
            } else if (charAt != 'G' && charAt != 'g') {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Invalid suffix: " + charAt);
                return 1;
            } else {
                j = 1073741824;
            }
            nextArg = nextArg.substring(0, length);
            j2 = j;
        } else {
            j2 = 1;
        }
        try {
            long parseLong = Long.parseLong(nextArg) * j2;
            String nextArg2 = getNextArg();
            if ("internal".equals(nextArg2)) {
                nextArg2 = null;
            }
            ClearDataObserver clearDataObserver = new ClearDataObserver();
            this.mInterface.freeStorageAndNotify(nextArg2, parseLong, 2, clearDataObserver);
            synchronized (clearDataObserver) {
                while (!clearDataObserver.finished) {
                    try {
                        clearDataObserver.wait();
                    } catch (InterruptedException unused) {
                    }
                }
            }
            return 0;
        } catch (NumberFormatException unused2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: expected number at: " + nextArg);
            return 1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:85:0x0084 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0007 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int runCreateUser() throws RemoteException {
        UserInfo createUserWithThrow;
        String nextArgRequired;
        UserInfo userInfo = null;
        int i = -1;
        boolean z = false;
        int i2 = 0;
        String str = null;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--profileOf".equals(nextOption)) {
                    i = translateUserId(UserHandle.parseUserArg(getNextArgRequired()), -1, "runCreateUser");
                } else {
                    if ("--managed".equals(nextOption)) {
                        nextArgRequired = "android.os.usertype.profile.MANAGED";
                    } else if ("--restricted".equals(nextOption)) {
                        nextArgRequired = "android.os.usertype.full.RESTRICTED";
                    } else if ("--guest".equals(nextOption)) {
                        nextArgRequired = "android.os.usertype.full.GUEST";
                    } else if ("--demo".equals(nextOption)) {
                        nextArgRequired = "android.os.usertype.full.DEMO";
                    } else if ("--ephemeral".equals(nextOption)) {
                        i2 |= 256;
                    } else if ("--for-testing".equals(nextOption)) {
                        i2 |= 32768;
                    } else if ("--pre-create-only".equals(nextOption)) {
                        nextArgRequired = null;
                        z = true;
                    } else if ("--user-type".equals(nextOption)) {
                        nextArgRequired = getNextArgRequired();
                    } else {
                        getErrPrintWriter().println("Error: unknown option " + nextOption);
                        return 1;
                    }
                    if (nextArgRequired != null) {
                        if (str != null && !str.equals(nextArgRequired)) {
                            getErrPrintWriter().println("Error: more than one user type was specified (" + str + " and " + nextArgRequired + ")");
                            return 1;
                        }
                        str = nextArgRequired;
                    }
                }
                nextArgRequired = null;
                if (nextArgRequired != null) {
                }
            } else {
                String nextArg = getNextArg();
                if (nextArg == null && !z) {
                    getErrPrintWriter().println("Error: no user name specified.");
                    return 1;
                }
                if (nextArg != null && z) {
                    getErrPrintWriter().println("Warning: name is ignored for pre-created users");
                }
                IUserManager asInterface = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
                IAccountManager asInterface2 = IAccountManager.Stub.asInterface(ServiceManager.getService("account"));
                if (str == null) {
                    str = UserInfo.getDefaultUserType(i2);
                }
                String str2 = str;
                Trace.traceBegin(262144L, "shell_runCreateUser");
                try {
                    try {
                        if (UserManager.isUserTypeRestricted(str2)) {
                            int i3 = i >= 0 ? i : 0;
                            userInfo = asInterface.createRestrictedProfileWithThrow(nextArg, i3);
                            asInterface2.addSharedAccountsFromParentUser(i3, i, Process.myUid() == 0 ? "root" : "com.android.shell");
                        } else if (i < 0) {
                            if (z) {
                                createUserWithThrow = asInterface.preCreateUserWithThrow(str2);
                            } else {
                                createUserWithThrow = asInterface.createUserWithThrow(nextArg, str2, i2);
                            }
                            userInfo = createUserWithThrow;
                        } else {
                            userInfo = asInterface.createProfileForUserWithThrow(nextArg, str2, i2, i, (String[]) null);
                        }
                    } catch (ServiceSpecificException e) {
                        getErrPrintWriter().println("Error: " + e);
                    }
                    if (userInfo != null) {
                        getOutPrintWriter().println("Success: created user id " + userInfo.id);
                        return 0;
                    }
                    getErrPrintWriter().println("Error: couldn't create User.");
                    return 1;
                } finally {
                    Trace.traceEnd(262144L);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int runRemoveUser() throws RemoteException {
        char c;
        boolean z = false;
        boolean z2 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -1095309356:
                        if (nextOption.equals("--set-ephemeral-if-in-use")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1514:
                        if (nextOption.equals("-w")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333511957:
                        if (nextOption.equals("--wait")) {
                            c = 2;
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
                        z = true;
                        break;
                    case 1:
                    case 2:
                        z2 = true;
                        break;
                    default:
                        getErrPrintWriter().println("Error: unknown option: " + nextOption);
                        return -1;
                }
            } else {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    getErrPrintWriter().println("Error: no user id specified.");
                    return 1;
                }
                int parseUserArg = UserHandle.parseUserArg(nextArg);
                IUserManager asInterface = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
                if (z) {
                    return removeUserWhenPossible(asInterface, parseUserArg);
                }
                if (z2 ? removeUserAndWait(asInterface, parseUserArg) : removeUser(asInterface, parseUserArg)) {
                    getOutPrintWriter().println("Success: removed user");
                    return 0;
                }
                return 1;
            }
        }
    }

    public final boolean removeUser(IUserManager iUserManager, int i) throws RemoteException {
        Slog.i("PackageManagerShellCommand", "Removing user " + i);
        if (iUserManager.removeUser(i)) {
            return true;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Error: couldn't remove user id " + i);
        return false;
    }

    public final boolean removeUserAndWait(IUserManager iUserManager, final int i) throws RemoteException {
        Slog.i("PackageManagerShellCommand", "Removing (and waiting for completion) user " + i);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UserManagerInternal.UserLifecycleListener userLifecycleListener = new UserManagerInternal.UserLifecycleListener() { // from class: com.android.server.pm.PackageManagerShellCommand.5
            @Override // com.android.server.p011pm.UserManagerInternal.UserLifecycleListener
            public void onUserRemoved(UserInfo userInfo) {
                if (i == userInfo.id) {
                    countDownLatch.countDown();
                }
            }
        };
        UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        userManagerInternal.addUserLifecycleListener(userLifecycleListener);
        try {
            if (iUserManager.removeUser(i)) {
                if (countDownLatch.await(10L, TimeUnit.MINUTES)) {
                    return true;
                }
                getErrPrintWriter().printf("Error: Remove user %d timed out\n", Integer.valueOf(i));
                return false;
            }
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: couldn't remove user id " + i);
            return false;
        } catch (InterruptedException e) {
            getErrPrintWriter().printf("Error: Remove user %d wait interrupted: %s\n", Integer.valueOf(i), e);
            Thread.currentThread().interrupt();
            return false;
        } finally {
            userManagerInternal.removeUserLifecycleListener(userLifecycleListener);
        }
    }

    public final int removeUserWhenPossible(IUserManager iUserManager, int i) throws RemoteException {
        Slog.i("PackageManagerShellCommand", "Removing " + i + " or set as ephemeral if in use.");
        int removeUserWhenPossible = iUserManager.removeUserWhenPossible(i, false);
        if (removeUserWhenPossible == -5) {
            getErrPrintWriter().printf("Error: user %d is a permanent admin main user\n", Integer.valueOf(i));
            return 1;
        } else if (removeUserWhenPossible == 0) {
            getOutPrintWriter().printf("Success: user %d removed\n", Integer.valueOf(i));
            return 0;
        } else if (removeUserWhenPossible == 1) {
            getOutPrintWriter().printf("Success: user %d set as ephemeral\n", Integer.valueOf(i));
            return 0;
        } else if (removeUserWhenPossible == 2) {
            getOutPrintWriter().printf("Success: user %d is already being removed\n", Integer.valueOf(i));
            return 0;
        } else {
            getErrPrintWriter().printf("Error: couldn't remove or mark ephemeral user id %d\n", Integer.valueOf(i));
            return 1;
        }
    }

    public final int runRenameUser() throws RemoteException {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no user id specified.");
            return 1;
        }
        int resolveUserId = resolveUserId(UserHandle.parseUserArg(nextArg));
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            Slog.i("PackageManagerShellCommand", "Resetting name of user " + resolveUserId);
        } else {
            Slog.i("PackageManagerShellCommand", "Renaming user " + resolveUserId + " to '" + nextArg2 + "'");
        }
        IUserManager.Stub.asInterface(ServiceManager.getService("user")).setUserName(resolveUserId, nextArg2);
        return 0;
    }

    public int runSetUserRestriction() throws RemoteException {
        String nextOption = getNextOption();
        int parseUserArg = (nextOption == null || !"--user".equals(nextOption)) ? 0 : UserHandle.parseUserArg(getNextArgRequired());
        String nextArg = getNextArg();
        String nextArg2 = getNextArg();
        boolean z = true;
        if (!"1".equals(nextArg2)) {
            if (!"0".equals(nextArg2)) {
                getErrPrintWriter().println("Error: valid value not specified");
                return 1;
            }
            z = false;
        }
        IUserManager.Stub.asInterface(ServiceManager.getService("user")).setUserRestriction(nextArg, z, translateUserId(parseUserArg, -10000, "runSetUserRestriction"));
        return 0;
    }

    public int runSupportsMultipleUsers() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Is multiuser supported: " + UserManager.supportsMultipleUsers());
        return 0;
    }

    public int runGetMaxUsers() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Maximum supported users: " + UserManager.getMaxSupportedUsers());
        return 0;
    }

    public int runGetMaxRunningUsers() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Maximum supported running users: " + ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getMaxRunningUsers());
        return 0;
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommand$InstallParams */
    /* loaded from: classes2.dex */
    public static class InstallParams {
        public String installerPackageName;
        public PackageInstaller.SessionParams sessionParams;
        public long stagedReadyTimeoutMs;
        public int userId;

        public InstallParams() {
            this.userId = -1;
            this.stagedReadyTimeoutMs = 60000L;
        }
    }

    public final InstallParams makeInstallParams(Set<String> set) {
        PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
        InstallParams installParams = new InstallParams();
        installParams.sessionParams = sessionParams;
        sessionParams.installFlags |= 4194304;
        sessionParams.setPackageSource(1);
        boolean z = true;
        boolean z2 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                if (z) {
                    sessionParams.installFlags |= 2;
                }
                if (z2) {
                    sessionParams.isStaged = false;
                }
                return installParams;
            } else if (set.contains(nextOption)) {
                throw new IllegalArgumentException("Unsupported option " + nextOption);
            } else {
                char c = 65535;
                switch (nextOption.hashCode()) {
                    case -2091380650:
                        if (nextOption.equals("--install-reason")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -2041347087:
                        if (nextOption.equals("--skip-enable")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1950997763:
                        if (nextOption.equals("--force-uuid")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1816313368:
                        if (nextOption.equals("--force-non-staged")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -1777984902:
                        if (nextOption.equals("--dont-kill")) {
                            c = 4;
                            break;
                        }
                        break;
                    case -1313152697:
                        if (nextOption.equals("--install-location")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -1137116608:
                        if (nextOption.equals("--instantapp")) {
                            c = 6;
                            break;
                        }
                        break;
                    case -951415743:
                        if (nextOption.equals("--instant")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -706813505:
                        if (nextOption.equals("--referrer")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case -653924786:
                        if (nextOption.equals("--enable-rollback")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case -365988597:
                        if (nextOption.equals("--update-ownership")) {
                            c = '\n';
                            break;
                        }
                        break;
                    case -170474990:
                        if (nextOption.equals("--multi-package")) {
                            c = 11;
                            break;
                        }
                        break;
                    case -158482320:
                        if (nextOption.equals("--staged-ready-timeout")) {
                            c = '\f';
                            break;
                        }
                        break;
                    case 1477:
                        if (nextOption.equals("-R")) {
                            c = '\r';
                            break;
                        }
                        break;
                    case 1478:
                        if (nextOption.equals("-S")) {
                            c = 14;
                            break;
                        }
                        break;
                    case 1495:
                        if (nextOption.equals("-d")) {
                            c = 15;
                            break;
                        }
                        break;
                    case 1497:
                        if (nextOption.equals("-f")) {
                            c = 16;
                            break;
                        }
                        break;
                    case 1498:
                        if (nextOption.equals("-g")) {
                            c = 17;
                            break;
                        }
                        break;
                    case NetworkConstants.ETHER_MTU /* 1500 */:
                        if (nextOption.equals("-i")) {
                            c = 18;
                            break;
                        }
                        break;
                    case 1507:
                        if (nextOption.equals("-p")) {
                            c = 19;
                            break;
                        }
                        break;
                    case 1509:
                        if (nextOption.equals("-r")) {
                            c = 20;
                            break;
                        }
                        break;
                    case 1511:
                        if (nextOption.equals("-t")) {
                            c = 21;
                            break;
                        }
                        break;
                    case 42995400:
                        if (nextOption.equals("--abi")) {
                            c = 22;
                            break;
                        }
                        break;
                    case 43010092:
                        if (nextOption.equals("--pkg")) {
                            c = 23;
                            break;
                        }
                        break;
                    case 50011004:
                        if (nextOption.equals("--bypass-low-target-sdk-block")) {
                            c = 24;
                            break;
                        }
                        break;
                    case 77141024:
                        if (nextOption.equals("--force-queryable")) {
                            c = 25;
                            break;
                        }
                        break;
                    case 148207464:
                        if (nextOption.equals("--originating-uri")) {
                            c = 26;
                            break;
                        }
                        break;
                    case 1051781117:
                        if (nextOption.equals("--ephemeral")) {
                            c = 27;
                            break;
                        }
                        break;
                    case 1067504745:
                        if (nextOption.equals("--preload")) {
                            c = 28;
                            break;
                        }
                        break;
                    case 1332870850:
                        if (nextOption.equals("--apex")) {
                            c = 29;
                            break;
                        }
                        break;
                    case 1333024815:
                        if (nextOption.equals("--full")) {
                            c = 30;
                            break;
                        }
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 31;
                            break;
                        }
                        break;
                    case 1494514835:
                        if (nextOption.equals("--restrict-permissions")) {
                            c = ' ';
                            break;
                        }
                        break;
                    case 1507519174:
                        if (nextOption.equals("--staged")) {
                            c = '!';
                            break;
                        }
                        break;
                    case 2015272120:
                        if (nextOption.equals("--force-sdk")) {
                            c = '\"';
                            break;
                        }
                        break;
                    case 2037590537:
                        if (nextOption.equals("--skip-verification")) {
                            c = '#';
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        sessionParams.installReason = Integer.parseInt(getNextArg());
                        break;
                    case 1:
                        sessionParams.setApplicationEnabledSettingPersistent();
                        break;
                    case 2:
                        sessionParams.installFlags |= 512;
                        String nextArg = getNextArg();
                        sessionParams.volumeUuid = nextArg;
                        if (!"internal".equals(nextArg)) {
                            break;
                        } else {
                            sessionParams.volumeUuid = null;
                            break;
                        }
                    case 3:
                        z2 = true;
                        break;
                    case 4:
                        sessionParams.installFlags |= IInstalld.FLAG_USE_QUOTA;
                        break;
                    case 5:
                        sessionParams.installLocation = Integer.parseInt(getNextArg());
                        break;
                    case 6:
                    case 7:
                    case 27:
                        sessionParams.setInstallAsInstantApp(true);
                        break;
                    case '\b':
                        sessionParams.referrerUri = Uri.parse(getNextArg());
                        break;
                    case '\t':
                        if (installParams.installerPackageName == null) {
                            installParams.installerPackageName = "com.android.shell";
                        }
                        sessionParams.installFlags |= 262144;
                        break;
                    case '\n':
                        if (installParams.installerPackageName == null) {
                            installParams.installerPackageName = "com.android.shell";
                        }
                        sessionParams.installFlags |= 33554432;
                        break;
                    case 11:
                        sessionParams.setMultiPackage();
                        break;
                    case '\f':
                        installParams.stagedReadyTimeoutMs = Long.parseLong(getNextArgRequired());
                        break;
                    case '\r':
                        z = false;
                        break;
                    case 14:
                        long parseLong = Long.parseLong(getNextArg());
                        if (parseLong <= 0) {
                            throw new IllegalArgumentException("Size must be positive");
                        }
                        sessionParams.setSize(parseLong);
                        break;
                    case 15:
                        sessionParams.installFlags |= 128;
                        break;
                    case 16:
                        sessionParams.installFlags |= 16;
                        break;
                    case 17:
                        sessionParams.installFlags |= 256;
                        break;
                    case 18:
                        String nextArg2 = getNextArg();
                        installParams.installerPackageName = nextArg2;
                        if (nextArg2 != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing installer package");
                        }
                    case 19:
                        sessionParams.mode = 2;
                        String nextArg3 = getNextArg();
                        sessionParams.appPackageName = nextArg3;
                        if (nextArg3 != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing inherit package name");
                        }
                    case 20:
                    case '\"':
                        break;
                    case 21:
                        sessionParams.installFlags |= 4;
                        break;
                    case 22:
                        sessionParams.abiOverride = checkAbiArgument(getNextArg());
                        break;
                    case 23:
                        String nextArg4 = getNextArg();
                        sessionParams.appPackageName = nextArg4;
                        if (nextArg4 != null) {
                            break;
                        } else {
                            throw new IllegalArgumentException("Missing package name");
                        }
                    case 24:
                        sessionParams.installFlags |= 16777216;
                        break;
                    case 25:
                        sessionParams.setForceQueryable();
                        break;
                    case 26:
                        sessionParams.originatingUri = Uri.parse(getNextArg());
                        break;
                    case 28:
                        sessionParams.setInstallAsVirtualPreload();
                        break;
                    case 29:
                        sessionParams.setInstallAsApex();
                        sessionParams.setStaged();
                        break;
                    case 30:
                        sessionParams.setInstallAsInstantApp(false);
                        break;
                    case 31:
                        installParams.userId = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    case ' ':
                        sessionParams.installFlags &= -4194305;
                        break;
                    case '!':
                        sessionParams.setStaged();
                        break;
                    case '#':
                        sessionParams.installFlags |= 524288;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option " + nextOption);
                }
            }
        }
    }

    public final int runSetHomeActivity() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    outPrintWriter.println("Error: Unknown option: " + nextOption);
                    return 1;
                }
            } else {
                String nextArg = getNextArg();
                if (nextArg.indexOf(47) >= 0) {
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(nextArg);
                    if (unflattenFromString == null) {
                        outPrintWriter.println("Error: invalid component name");
                        return 1;
                    }
                    nextArg = unflattenFromString.getPackageName();
                }
                String str = nextArg;
                int translateUserId = translateUserId(i, -10000, "runSetHomeActivity");
                final CompletableFuture completableFuture = new CompletableFuture();
                try {
                    ((RoleManager) this.mContext.getSystemService(RoleManager.class)).addRoleHolderAsUser("android.app.role.HOME", str, 0, UserHandle.of(translateUserId), FgThread.getExecutor(), new Consumer() { // from class: com.android.server.pm.PackageManagerShellCommand$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            completableFuture.complete((Boolean) obj);
                        }
                    });
                    if (((Boolean) completableFuture.get()).booleanValue()) {
                        outPrintWriter.println("Success");
                        return 0;
                    }
                    outPrintWriter.println("Error: Failed to set default home.");
                    return 1;
                } catch (Exception e) {
                    outPrintWriter.println(e.toString());
                    return 1;
                }
            }
        }
    }

    public final int runSetInstaller() throws RemoteException {
        String nextArg = getNextArg();
        String nextArg2 = getNextArg();
        if (nextArg == null || nextArg2 == null) {
            getErrPrintWriter().println("Must provide both target and installer package names");
            return 1;
        }
        this.mInterface.setInstallerPackageName(nextArg, nextArg2);
        getOutPrintWriter().println("Success");
        return 0;
    }

    public final int runGetInstantAppResolver() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            ComponentName instantAppResolverComponent = this.mInterface.getInstantAppResolverComponent();
            if (instantAppResolverComponent == null) {
                return 1;
            }
            outPrintWriter.println(instantAppResolverComponent.flattenToString());
            return 0;
        } catch (Exception e) {
            outPrintWriter.println(e.toString());
            return 1;
        }
    }

    public final int runHasFeature() {
        int parseInt;
        PrintWriter errPrintWriter = getErrPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            errPrintWriter.println("Error: expected FEATURE name");
            return 1;
        }
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            parseInt = 0;
        } else {
            try {
                parseInt = Integer.parseInt(nextArg2);
            } catch (RemoteException e) {
                errPrintWriter.println(e.toString());
                return 1;
            } catch (NumberFormatException unused) {
                errPrintWriter.println("Error: illegal version number " + nextArg2);
                return 1;
            }
        }
        boolean hasSystemFeature = this.mInterface.hasSystemFeature(nextArg, parseInt);
        getOutPrintWriter().println(hasSystemFeature);
        return !hasSystemFeature ? 1 : 0;
    }

    public final int runDump() {
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: no package specified");
            return 1;
        }
        ActivityManager.dumpPackageStateStatic(getOutFileDescriptor(), nextArg);
        return 0;
    }

    public final int runSetHarmfulAppWarning() throws RemoteException {
        int i = -2;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return -1;
                }
            } else {
                int translateUserId = translateUserId(i, -10000, "runSetHarmfulAppWarning");
                this.mInterface.setHarmfulAppWarning(getNextArgRequired(), getNextArg(), translateUserId);
                return 0;
            }
        }
    }

    public final int runGetHarmfulAppWarning() throws RemoteException {
        int i = -2;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Error: Unknown option: " + nextOption);
                    return -1;
                }
            } else {
                int translateUserId = translateUserId(i, -10000, "runGetHarmfulAppWarning");
                CharSequence harmfulAppWarning = this.mInterface.getHarmfulAppWarning(getNextArgRequired(), translateUserId);
                if (TextUtils.isEmpty(harmfulAppWarning)) {
                    return 1;
                }
                getOutPrintWriter().println(harmfulAppWarning);
                return 0;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSetSilentUpdatesPolicy() {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        Long l = null;
        String str = null;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -1615291473:
                        if (nextOption.equals("--reset")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 771584496:
                        if (nextOption.equals("--throttle-time")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1002172770:
                        if (nextOption.equals("--allow-unlimited-silent-updates")) {
                            c = 2;
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
                        z = true;
                        break;
                    case 1:
                        l = Long.valueOf(Long.parseLong(getNextArgRequired()));
                        break;
                    case 2:
                        str = getNextArgRequired();
                        break;
                    default:
                        outPrintWriter.println("Error: Unknown option: " + nextOption);
                        return -1;
                }
            } else if (l != null && l.longValue() < 0) {
                outPrintWriter.println("Error: Invalid value for \"--throttle-time\":" + l);
                return -1;
            } else {
                try {
                    IPackageInstaller packageInstaller = this.mInterface.getPackageInstaller();
                    if (z) {
                        packageInstaller.setAllowUnlimitedSilentUpdates((String) null);
                        packageInstaller.setSilentUpdatesThrottleTime(-1L);
                    } else {
                        if (str != null) {
                            packageInstaller.setAllowUnlimitedSilentUpdates(str);
                        }
                        if (l != null) {
                            packageInstaller.setSilentUpdatesThrottleTime(l.longValue());
                        }
                    }
                    return 1;
                } catch (RemoteException e) {
                    outPrintWriter.println("Failure [" + e.getClass().getName() + " - " + e.getMessage() + "]");
                    return -1;
                }
            }
        }
    }

    public final int runArtServiceCommand() {
        try {
            ParcelFileDescriptor dup = ParcelFileDescriptor.dup(getInFileDescriptor());
            ParcelFileDescriptor dup2 = ParcelFileDescriptor.dup(getOutFileDescriptor());
            ParcelFileDescriptor dup3 = ParcelFileDescriptor.dup(getErrFileDescriptor());
            try {
                int handleShellCommand = ((ArtManagerLocal) LocalManagerRegistry.getManagerOrThrow(ArtManagerLocal.class)).handleShellCommand(getTarget(), dup, dup2, dup3, getAllArgs());
                if (dup3 != null) {
                    dup3.close();
                }
                if (dup2 != null) {
                    dup2.close();
                }
                if (dup != null) {
                    dup.close();
                }
                return handleShellCommand;
            } catch (Throwable th) {
                if (dup3 != null) {
                    try {
                        dup3.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (LocalManagerRegistry.ManagerNotFoundException unused) {
            getErrPrintWriter().println("ART Service is not ready. Please try again later");
            return -1;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String checkAbiArgument(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Missing ABI argument");
        }
        if (PackageManagerShellCommandDataLoader.STDIN_PATH.equals(str)) {
            return str;
        }
        for (String str2 : Build.SUPPORTED_ABIS) {
            if (str2.equals(str)) {
                return str;
            }
        }
        throw new IllegalArgumentException("ABI " + str + " not supported on this device");
    }

    public final int translateUserId(int i, int i2, String str) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, i2 != -10000, true, str, "pm command");
        return handleIncomingUser == -1 ? i2 : handleIncomingUser;
    }

    public final int doCreateSession(PackageInstaller.SessionParams sessionParams, String str, int i) throws RemoteException {
        if (i == -1) {
            sessionParams.installFlags |= 64;
        }
        return this.mInterface.getPackageInstaller().createSession(sessionParams, str, (String) null, translateUserId(i, 0, "doCreateSession"));
    }

    public final int doAddFiles(int i, ArrayList<String> arrayList, long j, boolean z) throws RemoteException {
        PackageInstaller.Session session;
        PackageInstaller.Session session2 = null;
        try {
            try {
                session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            } catch (IllegalArgumentException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (!arrayList.isEmpty() && !PackageManagerShellCommandDataLoader.STDIN_PATH.equals(arrayList.get(0))) {
                Iterator<String> it = arrayList.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    if (next.indexOf(58) == -1) {
                        processArgForLocalFile(next, session);
                    } else if (processArgForStdin(next, session) != 0) {
                        IoUtils.closeQuietly(session);
                        return 1;
                    }
                }
                IoUtils.closeQuietly(session);
                return 0;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("base");
            sb.append(RANDOM.nextInt());
            sb.append(".");
            sb.append(z ? "apex" : "apk");
            String sb2 = sb.toString();
            session.addFile(0, sb2, j, PackageManagerShellCommandDataLoader.Metadata.forStdIn(sb2).toByteArray(), null);
            IoUtils.closeQuietly(session);
            return 0;
        } catch (IllegalArgumentException e2) {
            e = e2;
            session2 = session;
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Failed to add file(s), reason: " + e);
            getOutPrintWriter().println("Failure [failed to add file(s)]");
            IoUtils.closeQuietly(session2);
            return 1;
        } catch (Throwable th2) {
            th = th2;
            session2 = session;
            IoUtils.closeQuietly(session2);
            throw th;
        }
    }

    public final int processArgForStdin(String str, PackageInstaller.Session session) {
        int i;
        PackageManagerShellCommandDataLoader.Metadata forStdIn;
        String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR);
        try {
            if (split.length < 2) {
                getErrPrintWriter().println("Must specify file name and size");
                return 1;
            }
            String str2 = split[0];
            long parseUnsignedLong = Long.parseUnsignedLong(split[1]);
            String str3 = (split.length <= 2 || TextUtils.isEmpty(split[2])) ? str2 : split[2];
            byte[] decode = split.length > 3 ? Base64.getDecoder().decode(split[3]) : null;
            if (split.length > 4) {
                i = Integer.parseUnsignedInt(split[4]);
                if (i < 0 || i > 1) {
                    getErrPrintWriter().println("Unsupported streaming version: " + i);
                    return 1;
                }
            } else {
                i = 0;
            }
            if (TextUtils.isEmpty(str2)) {
                getErrPrintWriter().println("Empty file name in: " + str);
                return 1;
            }
            if (decode != null) {
                if (i == 0) {
                    forStdIn = PackageManagerShellCommandDataLoader.Metadata.forDataOnlyStreaming(str3);
                } else {
                    forStdIn = PackageManagerShellCommandDataLoader.Metadata.forStreaming(str3);
                }
                try {
                    if (decode.length > 0 && V4Signature.readFrom(decode) == null) {
                        getErrPrintWriter().println("V4 signature is invalid in: " + str);
                        return 1;
                    }
                } catch (Exception e) {
                    getErrPrintWriter().println("V4 signature is invalid: " + e + " in " + str);
                    return 1;
                }
            } else {
                forStdIn = PackageManagerShellCommandDataLoader.Metadata.forStdIn(str3);
            }
            session.addFile(0, str2, parseUnsignedLong, forStdIn.toByteArray(), decode);
            return 0;
        } catch (IllegalArgumentException e2) {
            getErrPrintWriter().println("Unable to parse file parameters: " + str + ", reason: " + e2);
            return 1;
        }
    }

    public final long getFileStatSize(File file) {
        ParcelFileDescriptor openFileForSystem = openFileForSystem(file.getPath(), "r");
        if (openFileForSystem == null) {
            throw new IllegalArgumentException("Error: Can't open file: " + file.getPath());
        }
        try {
            return openFileForSystem.getStatSize();
        } finally {
            IoUtils.closeQuietly(openFileForSystem);
        }
    }

    public final void processArgForLocalFile(String str, PackageInstaller.Session session) {
        byte[] bArr;
        File file = new File(str);
        String name = file.getName();
        long fileStatSize = getFileStatSize(file);
        PackageManagerShellCommandDataLoader.Metadata forLocalFile = PackageManagerShellCommandDataLoader.Metadata.forLocalFile(str);
        ParcelFileDescriptor openFileForSystem = openFileForSystem(str + ".idsig", "r");
        if (openFileForSystem != null) {
            try {
                try {
                    byte[] byteArray = V4Signature.readFrom(openFileForSystem).toByteArray();
                    IoUtils.closeQuietly(openFileForSystem);
                    bArr = byteArray;
                } catch (IOException e) {
                    Slog.e("PackageManagerShellCommand", "V4 signature file exists but failed to be parsed.", e);
                    IoUtils.closeQuietly(openFileForSystem);
                }
                session.addFile(0, name, fileStatSize, forLocalFile.toByteArray(), bArr);
            } catch (Throwable th) {
                IoUtils.closeQuietly(openFileForSystem);
                throw th;
            }
        }
        bArr = null;
        session.addFile(0, name, fileStatSize, forLocalFile.toByteArray(), bArr);
    }

    public final int doWriteSplits(int i, ArrayList<String> arrayList, long j, boolean z) throws RemoteException {
        String sb;
        boolean z2 = arrayList.size() > 1;
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (z2) {
                sb = new File(next).getName();
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("base.");
                sb2.append(z ? "apex" : "apk");
                sb = sb2.toString();
            }
            if (doWriteSplit(i, next, j, sb, false) != 0) {
                return 1;
            }
        }
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0073 A[Catch: all -> 0x00a9, IOException -> 0x00ac, TRY_LEAVE, TryCatch #4 {IOException -> 0x00ac, all -> 0x00a9, blocks: (B:4:0x0015, B:6:0x0023, B:23:0x0073, B:27:0x0086, B:29:0x008b, B:9:0x0031, B:14:0x003f, B:16:0x0047, B:20:0x0066), top: B:47:0x0015 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0080  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int doWriteSplit(int i, String str, long j, String str2, boolean z) throws RemoteException {
        PackageInstaller.Session session;
        ParcelFileDescriptor dup;
        ParcelFileDescriptor parcelFileDescriptor;
        long j2;
        PackageInstaller.Session session2 = null;
        try {
            try {
                session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            } catch (IOException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            PrintWriter outPrintWriter = getOutPrintWriter();
            if (PackageManagerShellCommandDataLoader.STDIN_PATH.equals(str)) {
                dup = ParcelFileDescriptor.dup(getInFileDescriptor());
            } else if (str != null) {
                ParcelFileDescriptor openFileForSystem = openFileForSystem(str, "r");
                if (openFileForSystem == null) {
                    IoUtils.closeQuietly(session);
                    return -1;
                }
                long statSize = openFileForSystem.getStatSize();
                if (statSize < 0) {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Unable to get size of: " + str);
                    IoUtils.closeQuietly(session);
                    return -1;
                }
                parcelFileDescriptor = openFileForSystem;
                j2 = statSize;
                if (j2 > 0) {
                    getErrPrintWriter().println("Error: must specify an APK size");
                    IoUtils.closeQuietly(session);
                    return 1;
                }
                session.write(str2, 0L, j2, parcelFileDescriptor);
                if (z) {
                    outPrintWriter.println("Success: streamed " + j2 + " bytes");
                }
                IoUtils.closeQuietly(session);
                return 0;
            } else {
                dup = ParcelFileDescriptor.dup(getInFileDescriptor());
            }
            j2 = j;
            parcelFileDescriptor = dup;
            if (j2 > 0) {
            }
        } catch (IOException e2) {
            e = e2;
            session2 = session;
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: failed to write; " + e.getMessage());
            IoUtils.closeQuietly(session2);
            return 1;
        } catch (Throwable th2) {
            th = th2;
            session2 = session;
            IoUtils.closeQuietly(session2);
            throw th;
        }
    }

    public final int doInstallAddSession(int i, int[] iArr, boolean z) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            PackageInstaller.Session session2 = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            try {
                if (!session2.isMultiPackage()) {
                    getErrPrintWriter().println("Error: parent session ID is not a multi-package session");
                    IoUtils.closeQuietly(session2);
                    return 1;
                }
                for (int i2 : iArr) {
                    session2.addChildSessionId(i2);
                }
                if (z) {
                    outPrintWriter.println("Success");
                }
                IoUtils.closeQuietly(session2);
                return 0;
            } catch (Throwable th) {
                th = th;
                session = session2;
                IoUtils.closeQuietly(session);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public final int doRemoveSplits(int i, Collection<String> collection, boolean z) throws RemoteException {
        PackageInstaller.Session session;
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.Session session2 = null;
        try {
            try {
                session = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            for (String str : collection) {
                session.removeSplit(str);
            }
            if (z) {
                outPrintWriter.println("Success");
            }
            IoUtils.closeQuietly(session);
            return 0;
        } catch (IOException e2) {
            e = e2;
            session2 = session;
            outPrintWriter.println("Error: failed to remove split; " + e.getMessage());
            IoUtils.closeQuietly(session2);
            return 1;
        } catch (Throwable th2) {
            th = th2;
            session2 = session;
            IoUtils.closeQuietly(session2);
            throw th;
        }
    }

    public final int doCommitSession(int i, boolean z) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            PackageInstaller.Session session2 = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            try {
                if (!session2.isMultiPackage() && !session2.isStaged()) {
                    try {
                        DexMetadataHelper.validateDexPaths(session2.getNames());
                    } catch (IOException | IllegalStateException e) {
                        outPrintWriter.println("Warning [Could not validate the dex paths: " + e.getMessage() + "]");
                    }
                }
                LocalIntentReceiver localIntentReceiver = new LocalIntentReceiver();
                session2.commit(localIntentReceiver.getIntentSender());
                if (!session2.isStaged()) {
                    Intent result = localIntentReceiver.getResult();
                    int intExtra = result.getIntExtra("android.content.pm.extra.STATUS", 1);
                    if (intExtra != 0) {
                        outPrintWriter.println("Failure [" + result.getStringExtra("android.content.pm.extra.STATUS_MESSAGE") + "]");
                    } else if (z) {
                        outPrintWriter.println("Success");
                    }
                    IoUtils.closeQuietly(session2);
                    return intExtra;
                }
                if (z) {
                    outPrintWriter.println("Success");
                }
                IoUtils.closeQuietly(session2);
                return 0;
            } catch (Throwable th) {
                th = th;
                session = session2;
                IoUtils.closeQuietly(session);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public final int doAbandonSession(int i, boolean z) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        PackageInstaller.Session session = null;
        try {
            PackageInstaller.Session session2 = new PackageInstaller.Session(this.mInterface.getPackageInstaller().openSession(i));
            try {
                session2.abandon();
                if (z) {
                    outPrintWriter.println("Success");
                }
                IoUtils.closeQuietly(session2);
                return 0;
            } catch (Throwable th) {
                th = th;
                session = session2;
                IoUtils.closeQuietly(session);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public final void doListPermissions(ArrayList<String> arrayList, boolean z, boolean z2, boolean z3, int i, int i2) throws RemoteException {
        String str;
        int i3;
        String str2;
        String str3;
        ArrayList<String> arrayList2 = arrayList;
        PrintWriter outPrintWriter = getOutPrintWriter();
        int size = arrayList.size();
        int i4 = 0;
        int i5 = 0;
        while (i5 < size) {
            String str4 = arrayList2.get(i5);
            String str5 = "  label:";
            if (z) {
                if (i5 > 0) {
                    outPrintWriter.println("");
                }
                if (str4 != null) {
                    PermissionGroupInfo permissionGroupInfo = this.mInterface.getPermissionGroupInfo(str4, i4);
                    if (z3) {
                        if (getResources(permissionGroupInfo) != null) {
                            outPrintWriter.print(loadText(permissionGroupInfo, permissionGroupInfo.labelRes, permissionGroupInfo.nonLocalizedLabel) + ": ");
                        } else {
                            outPrintWriter.print(permissionGroupInfo.name + ": ");
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(z2 ? "+ " : "");
                        sb.append("group:");
                        sb.append(permissionGroupInfo.name);
                        outPrintWriter.println(sb.toString());
                        if (z2) {
                            outPrintWriter.println("  package:" + permissionGroupInfo.packageName);
                            if (getResources(permissionGroupInfo) != null) {
                                outPrintWriter.println("  label:" + loadText(permissionGroupInfo, permissionGroupInfo.labelRes, permissionGroupInfo.nonLocalizedLabel));
                                outPrintWriter.println("  description:" + loadText(permissionGroupInfo, permissionGroupInfo.descriptionRes, permissionGroupInfo.nonLocalizedDescription));
                            }
                        }
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append((!z2 || z3) ? "" : "+ ");
                    sb2.append("ungrouped:");
                    outPrintWriter.println(sb2.toString());
                }
                str = "  ";
            } else {
                str = "";
            }
            int i6 = 0;
            List queryPermissionsByGroup = this.mPermissionManager.queryPermissionsByGroup(arrayList2.get(i5), 0);
            int size2 = queryPermissionsByGroup == null ? 0 : queryPermissionsByGroup.size();
            boolean z4 = true;
            while (i6 < size2) {
                PermissionInfo permissionInfo = (PermissionInfo) queryPermissionsByGroup.get(i6);
                if (z && str4 == null) {
                    i3 = size;
                    if (permissionInfo.group != null) {
                        str2 = str4;
                        str3 = str5;
                        i6++;
                        size = i3;
                        str4 = str2;
                        str5 = str3;
                    }
                } else {
                    i3 = size;
                }
                int i7 = permissionInfo.protectionLevel & 15;
                str2 = str4;
                if (i7 >= i && i7 <= i2) {
                    if (z3) {
                        if (z4) {
                            z4 = false;
                        } else {
                            outPrintWriter.print(", ");
                        }
                        if (getResources(permissionInfo) != null) {
                            outPrintWriter.print(loadText(permissionInfo, permissionInfo.labelRes, permissionInfo.nonLocalizedLabel));
                        } else {
                            outPrintWriter.print(permissionInfo.name);
                        }
                    } else {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(str);
                        sb3.append(z2 ? "+ " : "");
                        sb3.append("permission:");
                        sb3.append(permissionInfo.name);
                        outPrintWriter.println(sb3.toString());
                        if (z2) {
                            outPrintWriter.println(str + "  package:" + permissionInfo.packageName);
                            if (getResources(permissionInfo) != null) {
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(str);
                                sb4.append(str5);
                                str3 = str5;
                                sb4.append(loadText(permissionInfo, permissionInfo.labelRes, permissionInfo.nonLocalizedLabel));
                                outPrintWriter.println(sb4.toString());
                                outPrintWriter.println(str + "  description:" + loadText(permissionInfo, permissionInfo.descriptionRes, permissionInfo.nonLocalizedDescription));
                            } else {
                                str3 = str5;
                            }
                            outPrintWriter.println(str + "  protectionLevel:" + PermissionInfo.protectionToString(permissionInfo.protectionLevel));
                            i6++;
                            size = i3;
                            str4 = str2;
                            str5 = str3;
                        }
                    }
                }
                str3 = str5;
                i6++;
                size = i3;
                str4 = str2;
                str5 = str3;
            }
            int i8 = size;
            if (z3) {
                outPrintWriter.println("");
            }
            i5++;
            arrayList2 = arrayList;
            size = i8;
            i4 = 0;
        }
    }

    public final String loadText(PackageItemInfo packageItemInfo, int i, CharSequence charSequence) throws RemoteException {
        Resources resources;
        if (charSequence != null) {
            return charSequence.toString();
        }
        if (i == 0 || (resources = getResources(packageItemInfo)) == null) {
            return null;
        }
        try {
            return resources.getString(i);
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }

    public final Resources getResources(PackageItemInfo packageItemInfo) throws RemoteException {
        Resources resources = this.mResourceCache.get(packageItemInfo.packageName);
        if (resources != null) {
            return resources;
        }
        ApplicationInfo applicationInfo = this.mInterface.getApplicationInfo(packageItemInfo.packageName, 536904192L, 0);
        AssetManager assetManager = new AssetManager();
        assetManager.addAssetPath(applicationInfo.publicSourceDir);
        Resources resources2 = new Resources(assetManager, null, null);
        this.mResourceCache.put(packageItemInfo.packageName, resources2);
        return resources2;
    }

    public final int resolveUserId(int i) {
        return i == -2 ? ActivityManager.getCurrentUser() : i;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Package manager (package) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  path [--user USER_ID] PACKAGE");
        outPrintWriter.println("    Print the path to the .apk of the given PACKAGE.");
        outPrintWriter.println("");
        outPrintWriter.println("  dump PACKAGE");
        outPrintWriter.println("    Print various system state associated with the given PACKAGE.");
        outPrintWriter.println("");
        outPrintWriter.println("  has-feature FEATURE_NAME [version]");
        outPrintWriter.println("    Prints true and returns exit status 0 when system has a FEATURE_NAME,");
        outPrintWriter.println("    otherwise prints false and returns exit status 1");
        outPrintWriter.println("");
        outPrintWriter.println("  list features");
        outPrintWriter.println("    Prints all features of the system.");
        outPrintWriter.println("");
        outPrintWriter.println("  list instrumentation [-f] [TARGET-PACKAGE]");
        outPrintWriter.println("    Prints all test packages; optionally only those targeting TARGET-PACKAGE");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -f: dump the name of the .apk file containing the test package");
        outPrintWriter.println("");
        outPrintWriter.println("  list libraries");
        outPrintWriter.println("    Prints all system libraries.");
        outPrintWriter.println("");
        outPrintWriter.println("  list packages [-f] [-d] [-e] [-s] [-3] [-i] [-l] [-u] [-U] ");
        outPrintWriter.println("      [--show-versioncode] [--apex-only] [--factory-only]");
        outPrintWriter.println("      [--uid UID] [--user USER_ID] [FILTER]");
        outPrintWriter.println("    Prints all packages; optionally only those whose name contains");
        outPrintWriter.println("    the text in FILTER.  Options are:");
        outPrintWriter.println("      -f: see their associated file");
        outPrintWriter.println("      -a: all known packages (but excluding APEXes)");
        outPrintWriter.println("      -d: filter to only show disabled packages");
        outPrintWriter.println("      -e: filter to only show enabled packages");
        outPrintWriter.println("      -s: filter to only show system packages");
        outPrintWriter.println("      -3: filter to only show third party packages");
        outPrintWriter.println("      -i: see the installer for the packages");
        outPrintWriter.println("      -l: ignored (used for compatibility with older releases)");
        outPrintWriter.println("      -U: also show the package UID");
        outPrintWriter.println("      -u: also include uninstalled packages");
        outPrintWriter.println("      --show-versioncode: also show the version code");
        outPrintWriter.println("      --apex-only: only show APEX packages");
        outPrintWriter.println("      --factory-only: only show system packages excluding updates");
        outPrintWriter.println("      --uid UID: filter to only show packages with the given UID");
        outPrintWriter.println("      --user USER_ID: only list packages belonging to the given user");
        outPrintWriter.println("      --match-libraries: include packages that declare static shared and SDK libraries");
        outPrintWriter.println("");
        outPrintWriter.println("  list permission-groups");
        outPrintWriter.println("    Prints all known permission groups.");
        outPrintWriter.println("");
        outPrintWriter.println("  list permissions [-g] [-f] [-d] [-u] [GROUP]");
        outPrintWriter.println("    Prints all known permissions; optionally only those in GROUP.  Options are:");
        outPrintWriter.println("      -g: organize by group");
        outPrintWriter.println("      -f: print all information");
        outPrintWriter.println("      -s: short summary");
        outPrintWriter.println("      -d: only list dangerous permissions");
        outPrintWriter.println("      -u: list only the permissions users will see");
        outPrintWriter.println("");
        outPrintWriter.println("  list staged-sessions [--only-ready] [--only-sessionid] [--only-parent]");
        outPrintWriter.println("    Prints all staged sessions.");
        outPrintWriter.println("      --only-ready: show only staged sessions that are ready");
        outPrintWriter.println("      --only-sessionid: show only sessionId of each session");
        outPrintWriter.println("      --only-parent: hide all children sessions");
        outPrintWriter.println("");
        outPrintWriter.println("  list users");
        outPrintWriter.println("    Prints all users.");
        outPrintWriter.println("");
        outPrintWriter.println("  resolve-activity [--brief] [--components] [--query-flags FLAGS]");
        outPrintWriter.println("       [--user USER_ID] INTENT");
        outPrintWriter.println("    Prints the activity that resolves to the given INTENT.");
        outPrintWriter.println("");
        outPrintWriter.println("  query-activities [--brief] [--components] [--query-flags FLAGS]");
        outPrintWriter.println("       [--user USER_ID] INTENT");
        outPrintWriter.println("    Prints all activities that can handle the given INTENT.");
        outPrintWriter.println("");
        outPrintWriter.println("  query-services [--brief] [--components] [--query-flags FLAGS]");
        outPrintWriter.println("       [--user USER_ID] INTENT");
        outPrintWriter.println("    Prints all services that can handle the given INTENT.");
        outPrintWriter.println("");
        outPrintWriter.println("  query-receivers [--brief] [--components] [--query-flags FLAGS]");
        outPrintWriter.println("       [--user USER_ID] INTENT");
        outPrintWriter.println("    Prints all broadcast receivers that can handle the given INTENT.");
        outPrintWriter.println("");
        outPrintWriter.println("  install [-rtfdg] [-i PACKAGE] [--user USER_ID|all|current]");
        outPrintWriter.println("       [-p INHERIT_PACKAGE] [--install-location 0/1/2]");
        outPrintWriter.println("       [--install-reason 0/1/2/3/4] [--originating-uri URI]");
        outPrintWriter.println("       [--referrer URI] [--abi ABI_NAME] [--force-sdk]");
        outPrintWriter.println("       [--preload] [--instant] [--full] [--dont-kill]");
        outPrintWriter.println("       [--enable-rollback]");
        outPrintWriter.println("       [--force-uuid internal|UUID] [--pkg PACKAGE] [-S BYTES]");
        outPrintWriter.println("       [--apex] [--staged-ready-timeout TIMEOUT]");
        outPrintWriter.println("       [PATH [SPLIT...]|-]");
        outPrintWriter.println("    Install an application.  Must provide the apk data to install, either as");
        outPrintWriter.println("    file path(s) or '-' to read from stdin.  Options are:");
        outPrintWriter.println("      -R: disallow replacement of existing application");
        outPrintWriter.println("      -t: allow test packages");
        outPrintWriter.println("      -i: specify package name of installer owning the app");
        outPrintWriter.println("      -f: install application on internal flash");
        outPrintWriter.println("      -d: allow version code downgrade (debuggable packages only)");
        outPrintWriter.println("      -p: partial application install (new split on top of existing pkg)");
        outPrintWriter.println("      -g: grant all runtime permissions");
        outPrintWriter.println("      -S: size in bytes of package, required for stdin");
        outPrintWriter.println("      --user: install under the given user.");
        outPrintWriter.println("      --dont-kill: installing a new feature split, don't kill running app");
        outPrintWriter.println("      --restrict-permissions: don't whitelist restricted permissions at install");
        outPrintWriter.println("      --originating-uri: set URI where app was downloaded from");
        outPrintWriter.println("      --referrer: set URI that instigated the install of the app");
        outPrintWriter.println("      --pkg: specify expected package name of app being installed");
        outPrintWriter.println("      --abi: override the default ABI of the platform");
        outPrintWriter.println("      --instant: cause the app to be installed as an ephemeral install app");
        outPrintWriter.println("      --full: cause the app to be installed as a non-ephemeral full app");
        outPrintWriter.println("      --install-location: force the install location:");
        outPrintWriter.println("          0=auto, 1=internal only, 2=prefer external");
        outPrintWriter.println("      --install-reason: indicates why the app is being installed:");
        outPrintWriter.println("          0=unknown, 1=admin policy, 2=device restore,");
        outPrintWriter.println("          3=device setup, 4=user request");
        outPrintWriter.println("      --update-ownership: request the update ownership enforcement");
        outPrintWriter.println("      --force-uuid: force install on to disk volume with given UUID");
        outPrintWriter.println("      --apex: install an .apex file, not an .apk");
        outPrintWriter.println("      --staged-ready-timeout: By default, staged sessions wait 60000");
        outPrintWriter.println("          milliseconds for pre-reboot verification to complete when");
        outPrintWriter.println("          performing staged install. This flag is used to alter the waiting");
        outPrintWriter.println("          time. You can skip the waiting time by specifying a TIMEOUT of '0'");
        outPrintWriter.println("");
        outPrintWriter.println("  install-existing [--user USER_ID|all|current]");
        outPrintWriter.println("       [--instant] [--full] [--wait] [--restrict-permissions] PACKAGE");
        outPrintWriter.println("    Installs an existing application for a new user.  Options are:");
        outPrintWriter.println("      --user: install for the given user.");
        outPrintWriter.println("      --instant: install as an instant app");
        outPrintWriter.println("      --full: install as a full app");
        outPrintWriter.println("      --wait: wait until the package is installed");
        outPrintWriter.println("      --restrict-permissions: don't whitelist restricted permissions");
        outPrintWriter.println("");
        outPrintWriter.println("  install-create [-lrtsfdg] [-i PACKAGE] [--user USER_ID|all|current]");
        outPrintWriter.println("       [-p INHERIT_PACKAGE] [--install-location 0/1/2]");
        outPrintWriter.println("       [--install-reason 0/1/2/3/4] [--originating-uri URI]");
        outPrintWriter.println("       [--referrer URI] [--abi ABI_NAME] [--force-sdk]");
        outPrintWriter.println("       [--preload] [--instant] [--full] [--dont-kill]");
        outPrintWriter.println("       [--force-uuid internal|UUID] [--pkg PACKAGE] [--apex] [-S BYTES]");
        outPrintWriter.println("       [--multi-package] [--staged] [--update-ownership]");
        outPrintWriter.println("    Like \"install\", but starts an install session.  Use \"install-write\"");
        outPrintWriter.println("    to push data into the session, and \"install-commit\" to finish.");
        outPrintWriter.println("");
        outPrintWriter.println("  install-write [-S BYTES] SESSION_ID SPLIT_NAME [PATH|-]");
        outPrintWriter.println("    Write an apk into the given install session.  If the path is '-', data");
        outPrintWriter.println("    will be read from stdin.  Options are:");
        outPrintWriter.println("      -S: size in bytes of package, required for stdin");
        outPrintWriter.println("");
        outPrintWriter.println("  install-remove SESSION_ID SPLIT...");
        outPrintWriter.println("    Mark SPLIT(s) as removed in the given install session.");
        outPrintWriter.println("");
        outPrintWriter.println("  install-add-session MULTI_PACKAGE_SESSION_ID CHILD_SESSION_IDs");
        outPrintWriter.println("    Add one or more session IDs to a multi-package session.");
        outPrintWriter.println("");
        outPrintWriter.println("  install-commit SESSION_ID");
        outPrintWriter.println("    Commit the given active install session, installing the app.");
        outPrintWriter.println("");
        outPrintWriter.println("  install-abandon SESSION_ID");
        outPrintWriter.println("    Delete the given active install session.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-install-location LOCATION");
        outPrintWriter.println("    Changes the default install location.  NOTE this is only intended for debugging;");
        outPrintWriter.println("    using this can cause applications to break and other undersireable behavior.");
        outPrintWriter.println("    LOCATION is one of:");
        outPrintWriter.println("    0 [auto]: Let system decide the best location");
        outPrintWriter.println("    1 [internal]: Install on internal device storage");
        outPrintWriter.println("    2 [external]: Install on external media");
        outPrintWriter.println("");
        outPrintWriter.println("  get-install-location");
        outPrintWriter.println("    Returns the current install location: 0, 1 or 2 as per set-install-location.");
        outPrintWriter.println("");
        outPrintWriter.println("  move-package PACKAGE [internal|UUID]");
        outPrintWriter.println("");
        outPrintWriter.println("  move-primary-storage [internal|UUID]");
        outPrintWriter.println("");
        outPrintWriter.println("  uninstall [-k] [--user USER_ID] [--versionCode VERSION_CODE]");
        outPrintWriter.println("       PACKAGE [SPLIT...]");
        outPrintWriter.println("    Remove the given package name from the system.  May remove an entire app");
        outPrintWriter.println("    if no SPLIT names specified, otherwise will remove only the splits of the");
        outPrintWriter.println("    given app.  Options are:");
        outPrintWriter.println("      -k: keep the data and cache directories around after package removal.");
        outPrintWriter.println("      --user: remove the app from the given user.");
        outPrintWriter.println("      --versionCode: only uninstall if the app has the given version code.");
        outPrintWriter.println("");
        outPrintWriter.println("  clear [--user USER_ID] [--cache-only] PACKAGE");
        outPrintWriter.println("    Deletes data associated with a package. Options are:");
        outPrintWriter.println("    --user: specifies the user for which we need to clear data");
        outPrintWriter.println("    --cache-only: a flag which tells if we only need to clear cache data");
        outPrintWriter.println("");
        outPrintWriter.println("  enable [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("  disable [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("  disable-user [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("  disable-until-used [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("  default-state [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("    These commands change the enabled state of a given package or");
        outPrintWriter.println("    component (written as \"package/class\").");
        outPrintWriter.println("");
        outPrintWriter.println("  hide [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("  unhide [--user USER_ID] PACKAGE_OR_COMPONENT");
        outPrintWriter.println("");
        outPrintWriter.println("  suspend [--user USER_ID] PACKAGE [PACKAGE...]");
        outPrintWriter.println("    Suspends the specified package(s) (as user).");
        outPrintWriter.println("");
        outPrintWriter.println("  unsuspend [--user USER_ID] PACKAGE [PACKAGE...]");
        outPrintWriter.println("    Unsuspends the specified package(s) (as user).");
        outPrintWriter.println("");
        outPrintWriter.println("  set-distracting-restriction [--user USER_ID] [--flag FLAG ...]");
        outPrintWriter.println("      PACKAGE [PACKAGE...]");
        outPrintWriter.println("    Sets the specified restriction flags to given package(s) (for user).");
        outPrintWriter.println("    Flags are:");
        outPrintWriter.println("      hide-notifications: Hides notifications from this package");
        outPrintWriter.println("      hide-from-suggestions: Hides this package from suggestions");
        outPrintWriter.println("        (by the launcher, etc.)");
        outPrintWriter.println("    Any existing flags are overwritten, which also means that if no flags are");
        outPrintWriter.println("    specified then all existing flags will be cleared.");
        outPrintWriter.println("");
        outPrintWriter.println("  grant [--user USER_ID] PACKAGE PERMISSION");
        outPrintWriter.println("  revoke [--user USER_ID] PACKAGE PERMISSION");
        outPrintWriter.println("    These commands either grant or revoke permissions to apps.  The permissions");
        outPrintWriter.println("    must be declared as used in the app's manifest, be runtime permissions");
        outPrintWriter.println("    (protection level dangerous), and the app targeting SDK greater than Lollipop MR1.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-permission-flags [--user USER_ID] PACKAGE PERMISSION [FLAGS..]");
        outPrintWriter.println("  clear-permission-flags [--user USER_ID] PACKAGE PERMISSION [FLAGS..]");
        outPrintWriter.println("    These commands either set or clear permission flags on apps.  The permissions");
        outPrintWriter.println("    must be declared as used in the app's manifest, be runtime permissions");
        outPrintWriter.println("    (protection level dangerous), and the app targeting SDK greater than Lollipop MR1.");
        outPrintWriter.println("    The flags must be one or more of " + SUPPORTED_PERMISSION_FLAGS_LIST);
        outPrintWriter.println("");
        outPrintWriter.println("  reset-permissions");
        outPrintWriter.println("    Revert all runtime permissions to their default state.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-permission-enforced PERMISSION [true|false]");
        outPrintWriter.println("");
        outPrintWriter.println("  get-privapp-permissions TARGET-PACKAGE");
        outPrintWriter.println("    Prints all privileged permissions for a package.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-privapp-deny-permissions TARGET-PACKAGE");
        outPrintWriter.println("    Prints all privileged permissions that are denied for a package.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-oem-permissions TARGET-PACKAGE");
        outPrintWriter.println("    Prints all OEM permissions for a package.");
        outPrintWriter.println("");
        outPrintWriter.println("  trim-caches DESIRED_FREE_SPACE [internal|UUID]");
        outPrintWriter.println("    Trim cache files to reach the given free space.");
        outPrintWriter.println("");
        outPrintWriter.println("  list users");
        outPrintWriter.println("    Lists the current users.");
        outPrintWriter.println("");
        outPrintWriter.println("  create-user [--profileOf USER_ID] [--managed] [--restricted] [--guest]");
        outPrintWriter.println("       [--user-type USER_TYPE] [--ephemeral] [--for-testing] [--pre-create-only]   USER_NAME");
        outPrintWriter.println("    Create a new user with the given USER_NAME, printing the new user identifier");
        outPrintWriter.println("    of the user.");
        outPrintWriter.println("    USER_TYPE is the name of a user type, e.g. android.os.usertype.profile.MANAGED.");
        outPrintWriter.println("      If not specified, the default user type is android.os.usertype.full.SECONDARY.");
        outPrintWriter.println("      --managed is shorthand for '--user-type android.os.usertype.profile.MANAGED'.");
        outPrintWriter.println("      --restricted is shorthand for '--user-type android.os.usertype.full.RESTRICTED'.");
        outPrintWriter.println("      --guest is shorthand for '--user-type android.os.usertype.full.GUEST'.");
        outPrintWriter.println("");
        outPrintWriter.println("  remove-user [--set-ephemeral-if-in-use | --wait] USER_ID");
        outPrintWriter.println("    Remove the user with the given USER_IDENTIFIER, deleting all data");
        outPrintWriter.println("    associated with that user.");
        outPrintWriter.println("      --set-ephemeral-if-in-use: If the user is currently running and");
        outPrintWriter.println("        therefore cannot be removed immediately, mark the user as ephemeral");
        outPrintWriter.println("        so that it will be automatically removed when possible (after user");
        outPrintWriter.println("        switch or reboot)");
        outPrintWriter.println("      --wait: Wait until user is removed. Ignored if set-ephemeral-if-in-use");
        outPrintWriter.println("");
        outPrintWriter.println("  rename-user USER_ID [USER_NAME]");
        outPrintWriter.println("    Rename USER_ID with USER_NAME (or null when [USER_NAME] is not set)");
        outPrintWriter.println("");
        outPrintWriter.println("  set-user-restriction [--user USER_ID] RESTRICTION VALUE");
        outPrintWriter.println("");
        outPrintWriter.println("  get-max-users");
        outPrintWriter.println("");
        outPrintWriter.println("  get-max-running-users");
        outPrintWriter.println("");
        outPrintWriter.println("  set-home-activity [--user USER_ID] TARGET-COMPONENT");
        outPrintWriter.println("    Set the default home activity (aka launcher).");
        outPrintWriter.println("    TARGET-COMPONENT can be a package name (com.package.my) or a full");
        outPrintWriter.println("    component (com.package.my/component.name). However, only the package name");
        outPrintWriter.println("    matters: the actual component used will be determined automatically from");
        outPrintWriter.println("    the package.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-installer PACKAGE INSTALLER");
        outPrintWriter.println("    Set installer package name");
        outPrintWriter.println("");
        outPrintWriter.println("  get-instantapp-resolver");
        outPrintWriter.println("    Return the name of the component that is the current instant app installer.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-harmful-app-warning [--user <USER_ID>] <PACKAGE> [<WARNING>]");
        outPrintWriter.println("    Mark the app as harmful with the given warning message.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-harmful-app-warning [--user <USER_ID>] <PACKAGE>");
        outPrintWriter.println("    Return the harmful app warning message for the given app, if present");
        outPrintWriter.println();
        outPrintWriter.println("  uninstall-system-updates [<PACKAGE>]");
        outPrintWriter.println("    Removes updates to the given system application and falls back to its");
        outPrintWriter.println("    /system version. Does nothing if the given package is not a system app.");
        outPrintWriter.println("    If no package is specified, removes updates to all system applications.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-moduleinfo [--all | --installed] [module-name]");
        outPrintWriter.println("    Displays module info. If module-name is specified only that info is shown");
        outPrintWriter.println("    By default, without any argument only installed modules are shown.");
        outPrintWriter.println("      --all: show all module info");
        outPrintWriter.println("      --installed: show only installed modules");
        outPrintWriter.println("");
        outPrintWriter.println("  log-visibility [--enable|--disable] <PACKAGE>");
        outPrintWriter.println("    Turns on debug logging when visibility is blocked for the given package.");
        outPrintWriter.println("      --enable: turn on debug logging (default)");
        outPrintWriter.println("      --disable: turn off debug logging");
        outPrintWriter.println("");
        outPrintWriter.println("  set-silent-updates-policy [--allow-unlimited-silent-updates <INSTALLER>]");
        outPrintWriter.println("                            [--throttle-time <SECONDS>] [--reset]");
        outPrintWriter.println("    Sets the policies of the silent updates.");
        outPrintWriter.println("      --allow-unlimited-silent-updates: allows unlimited silent updated");
        outPrintWriter.println("        installation requests from the installer without the throttle time.");
        outPrintWriter.println("      --throttle-time: update the silent updates throttle time in seconds.");
        outPrintWriter.println("      --reset: restore the installer and throttle time to the default, and");
        outPrintWriter.println("        clear tracks of silent updates in the system.");
        outPrintWriter.println("");
        if (DexOptHelper.useArtService()) {
            printArtServiceHelp();
        } else {
            printLegacyDexoptHelp();
        }
        outPrintWriter.println("");
        this.mDomainVerificationShell.printHelp(outPrintWriter);
        outPrintWriter.println("");
        Intent.printIntentArgsHelp(outPrintWriter, "");
    }

    public final void printArtServiceHelp() {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(getOutPrintWriter(), "  ");
        indentingPrintWriter.increaseIndent();
        try {
            ((ArtManagerLocal) LocalManagerRegistry.getManagerOrThrow(ArtManagerLocal.class)).printShellCommandHelp(indentingPrintWriter);
        } catch (LocalManagerRegistry.ManagerNotFoundException unused) {
            indentingPrintWriter.println("ART Service is not ready. Please try again later");
        }
        indentingPrintWriter.decreaseIndent();
    }

    public final void printLegacyDexoptHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("  compile [-m MODE | -r REASON] [-f] [-c] [--split SPLIT_NAME]");
        outPrintWriter.println("          [--reset] [--check-prof (true | false)] (-a | TARGET-PACKAGE)");
        outPrintWriter.println("    Trigger compilation of TARGET-PACKAGE or all packages if \"-a\".  Options are:");
        outPrintWriter.println("      -a: compile all packages");
        outPrintWriter.println("      -c: clear profile data before compiling");
        outPrintWriter.println("      -f: force compilation even if not needed");
        outPrintWriter.println("      -m: select compilation mode");
        outPrintWriter.println("          MODE is one of the dex2oat compiler filters:");
        outPrintWriter.println("            assume-verified");
        outPrintWriter.println("            extract");
        outPrintWriter.println("            verify");
        outPrintWriter.println("            quicken");
        outPrintWriter.println("            space-profile");
        outPrintWriter.println("            space");
        outPrintWriter.println("            speed-profile");
        outPrintWriter.println("            speed");
        outPrintWriter.println("            everything");
        outPrintWriter.println("      -r: select compilation reason");
        outPrintWriter.println("          REASON is one of:");
        int i = 0;
        while (true) {
            String[] strArr = PackageManagerServiceCompilerMapping.REASON_STRINGS;
            if (i < strArr.length) {
                outPrintWriter.println("            " + strArr[i]);
                i++;
            } else {
                outPrintWriter.println("      --reset: restore package to its post-install state");
                outPrintWriter.println("      --check-prof (true | false): ignored - this is always true");
                outPrintWriter.println("      --secondary-dex: compile app secondary dex files");
                outPrintWriter.println("      --split SPLIT: compile only the given split name");
                outPrintWriter.println("");
                outPrintWriter.println("  force-dex-opt PACKAGE");
                outPrintWriter.println("    Force immediate execution of dex opt for the given PACKAGE.");
                outPrintWriter.println("");
                outPrintWriter.println("  delete-dexopt PACKAGE");
                outPrintWriter.println("    Delete dex optimization results for the given PACKAGE.");
                outPrintWriter.println("");
                outPrintWriter.println("  bg-dexopt-job [PACKAGE... | --cancel | --disable | --enable]");
                outPrintWriter.println("    Controls the background job that optimizes dex files:");
                outPrintWriter.println("    Without flags, run background optimization immediately on the given");
                outPrintWriter.println("    PACKAGEs, or all packages if none is specified, and wait until the job");
                outPrintWriter.println("    finishes. Note that the command only runs the background optimizer logic.");
                outPrintWriter.println("    It will run even if the device is not in the idle maintenance mode. If a");
                outPrintWriter.println("    job is already running (including one started automatically by the");
                outPrintWriter.println("    system) it will wait for it to finish before starting. A background job");
                outPrintWriter.println("    will not be started automatically while one started this way is running.");
                outPrintWriter.println("      --cancel: Cancels any currently running background optimization job");
                outPrintWriter.println("        immediately. This cancels jobs started either automatically by the");
                outPrintWriter.println("        system or through this command. Note that cancelling a currently");
                outPrintWriter.println("        running bg-dexopt-job command requires running this command from a");
                outPrintWriter.println("        separate adb shell.");
                outPrintWriter.println("      --disable: Disables background jobs from being started by the job");
                outPrintWriter.println("        scheduler. Does not affect bg-dexopt-job invocations from the shell.");
                outPrintWriter.println("        Does not imply --cancel. This state will be lost when the");
                outPrintWriter.println("        system_server process exits.");
                outPrintWriter.println("      --enable: Enables background jobs to be started by the job scheduler");
                outPrintWriter.println("        again, if previously disabled by --disable.");
                outPrintWriter.println("  cancel-bg-dexopt-job");
                outPrintWriter.println("    Same as bg-dexopt-job --cancel.");
                outPrintWriter.println("");
                outPrintWriter.println("  reconcile-secondary-dex-files TARGET-PACKAGE");
                outPrintWriter.println("    Reconciles the package secondary dex files with the generated oat files.");
                outPrintWriter.println("");
                outPrintWriter.println("  dump-profiles [--dump-classes-and-methods] TARGET-PACKAGE");
                outPrintWriter.println("    Dumps method/class profile files to");
                outPrintWriter.println("    /data/misc/profman/TARGET-PACKAGE-primary.prof.txt.");
                outPrintWriter.println("      --dump-classes-and-methods: passed along to the profman binary to");
                outPrintWriter.println("        switch to the format used by 'profman --create-profile-from'.");
                outPrintWriter.println("");
                outPrintWriter.println("  snapshot-profile TARGET-PACKAGE [--code-path path]");
                outPrintWriter.println("    Take a snapshot of the package profiles to");
                outPrintWriter.println("    /data/misc/profman/TARGET-PACKAGE[-code-path].prof");
                outPrintWriter.println("    If TARGET-PACKAGE=android it will take a snapshot of the boot image");
                return;
            }
        }
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommand$LocalIntentReceiver */
    /* loaded from: classes2.dex */
    public static class LocalIntentReceiver {
        public IIntentSender.Stub mLocalSender;
        public final LinkedBlockingQueue<Intent> mResult;

        public LocalIntentReceiver() {
            this.mResult = new LinkedBlockingQueue<>();
            this.mLocalSender = new IIntentSender.Stub() { // from class: com.android.server.pm.PackageManagerShellCommand.LocalIntentReceiver.1
                public void send(int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
                    try {
                        LocalIntentReceiver.this.mResult.offer(intent, 5L, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        public IntentSender getIntentSender() {
            return new IntentSender(this.mLocalSender);
        }

        public Intent getResult() {
            try {
                return this.mResult.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
