package com.android.server.p006am;

import android.app.AppGlobals;
import android.app.ContentProviderHolder;
import android.app.IApplicationThread;
import android.content.AttributionSource;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.pm.UserProperties;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.RescueParty;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.sdksandbox.SdkSandboxManagerLocal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
/* renamed from: com.android.server.am.ContentProviderHelper */
/* loaded from: classes.dex */
public class ContentProviderHelper {
    public static final int[] PROCESS_STATE_STATS_FORMAT = {32, FrameworkStatsLog.PACKAGE_MANAGER_SNAPSHOT_REPORTED, 10272};
    public final ArrayList<ContentProviderRecord> mLaunchingProviders = new ArrayList<>();
    public final long[] mProcessStateStatsLongs = new long[1];
    public final ProviderMap mProviderMap;
    public final ActivityManagerService mService;
    public boolean mSystemProvidersInstalled;

    public ContentProviderHelper(ActivityManagerService activityManagerService, boolean z) {
        this.mService = activityManagerService;
        this.mProviderMap = z ? new ProviderMap(activityManagerService) : null;
    }

    public ProviderMap getProviderMap() {
        return this.mProviderMap;
    }

    public ContentProviderHolder getContentProvider(IApplicationThread iApplicationThread, String str, String str2, int i, boolean z) {
        this.mService.enforceNotIsolatedCaller("getContentProvider");
        if (iApplicationThread == null) {
            String str3 = "null IApplicationThread when getting content provider " + str2;
            Slog.w("ContentProviderHelper", str3);
            throw new SecurityException(str3);
        }
        int callingUid = Binder.getCallingUid();
        if (str != null && this.mService.mAppOpsService.checkPackage(callingUid, str) != 0) {
            throw new SecurityException("Given calling package " + str + " does not match caller's uid " + callingUid);
        }
        return getContentProviderImpl(iApplicationThread, str2, null, callingUid, str, null, z, i);
    }

    public ContentProviderHolder getContentProviderExternal(String str, int i, IBinder iBinder, String str2) {
        this.mService.enforceCallingPermission("android.permission.ACCESS_CONTENT_PROVIDERS_EXTERNALLY", "Do not have permission in call getContentProviderExternal()");
        return getContentProviderExternalUnchecked(str, iBinder, Binder.getCallingUid(), str2 != null ? str2 : "*external*", this.mService.mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 2, "getContentProvider", null));
    }

    public ContentProviderHolder getContentProviderExternalUnchecked(String str, IBinder iBinder, int i, String str2, int i2) {
        return getContentProviderImpl(null, str, iBinder, i, null, str2, true, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x027b  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x02d5  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02d7  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x02e9  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x02eb  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x031c A[Catch: all -> 0x061e, TRY_ENTER, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x03c6 A[Catch: all -> 0x07e4, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0438  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0442 A[Catch: all -> 0x07e4, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:201:0x045f A[Catch: all -> 0x07e4, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:206:0x046d A[Catch: all -> 0x07e4, TRY_LEAVE, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:243:0x05cb  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x05db A[Catch: all -> 0x07e4, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x0611 A[Catch: all -> 0x07e4, TRY_LEAVE, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:251:0x0615  */
    /* JADX WARN: Removed duplicated region for block: B:256:0x0621  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x064b A[Catch: all -> 0x07e4, TRY_LEAVE, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x06c9 A[Catch: all -> 0x07e4, TRY_LEAVE, TryCatch #17 {all -> 0x07e4, blocks: (B:93:0x0233, B:94:0x0236, B:102:0x026d, B:112:0x028a, B:114:0x0297, B:119:0x02a6, B:122:0x02ab, B:124:0x02bb, B:128:0x02c4, B:135:0x02d8, B:139:0x02ec, B:141:0x0307, B:144:0x0313, B:145:0x031a, B:146:0x031b, B:157:0x0353, B:159:0x035e, B:160:0x0399, B:164:0x039f, B:166:0x03ba, B:173:0x03c6, B:175:0x03d6, B:180:0x040d, B:181:0x0410, B:184:0x042a, B:191:0x043b, B:193:0x0442, B:195:0x0448, B:196:0x0451, B:199:0x0456, B:201:0x045f, B:204:0x0468, B:206:0x046d, B:239:0x05c2, B:244:0x05d4, B:246:0x05db, B:247:0x05e2, B:249:0x0611, B:257:0x062f, B:259:0x064b, B:266:0x0699, B:273:0x06b9, B:274:0x06c1, B:280:0x06c9, B:233:0x059e, B:234:0x05a1, B:188:0x0434, B:127:0x02c2, B:337:0x07df, B:107:0x0277, B:108:0x027a, B:260:0x064c, B:262:0x0650, B:264:0x0654, B:265:0x0698, B:270:0x06a0, B:271:0x06a2, B:272:0x06b8, B:207:0x0471, B:209:0x047d, B:210:0x048a, B:213:0x04a2, B:214:0x04c4, B:216:0x04d7, B:218:0x04dd, B:220:0x04e3, B:222:0x04ed, B:223:0x04f7, B:225:0x04fc, B:238:0x05bb, B:226:0x051a, B:230:0x0529, B:232:0x056b, B:237:0x05aa, B:177:0x03db, B:179:0x03f5, B:183:0x0415, B:147:0x031c, B:149:0x0320, B:151:0x0328, B:154:0x0334, B:155:0x0351, B:156:0x0352), top: B:366:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:362:0x028a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00d4 A[Catch: all -> 0x07dc, TryCatch #15 {all -> 0x07dc, blocks: (B:4:0x000b, B:6:0x0014, B:12:0x004a, B:14:0x005e, B:18:0x0067, B:26:0x007c, B:28:0x0084, B:30:0x0098, B:34:0x00a1, B:48:0x00d4, B:50:0x00d8, B:54:0x00e1, B:56:0x00e9, B:58:0x00f1, B:64:0x011f, B:66:0x0123, B:68:0x0129, B:69:0x0155, B:73:0x0160, B:75:0x016a, B:79:0x0173, B:33:0x009f, B:37:0x00b0, B:40:0x00b8, B:23:0x0072, B:9:0x001e, B:10:0x0048), top: B:358:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x011f A[Catch: all -> 0x07dc, TryCatch #15 {all -> 0x07dc, blocks: (B:4:0x000b, B:6:0x0014, B:12:0x004a, B:14:0x005e, B:18:0x0067, B:26:0x007c, B:28:0x0084, B:30:0x0098, B:34:0x00a1, B:48:0x00d4, B:50:0x00d8, B:54:0x00e1, B:56:0x00e9, B:58:0x00f1, B:64:0x011f, B:66:0x0123, B:68:0x0129, B:69:0x0155, B:73:0x0160, B:75:0x016a, B:79:0x0173, B:33:0x009f, B:37:0x00b0, B:40:0x00b8, B:23:0x0072, B:9:0x001e, B:10:0x0048), top: B:358:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ContentProviderHolder getContentProviderImpl(IApplicationThread iApplicationThread, String str, IBinder iBinder, int i, String str2, String str3, boolean z, int i2) {
        ActivityManagerService activityManagerService;
        ProcessRecord processRecord;
        ContentProviderRecord providerByName;
        ContentProviderRecord contentProviderRecord;
        int i3;
        ProviderInfo providerInfo;
        boolean z2;
        boolean z3;
        ProcessRecord processRecord2;
        int i4;
        String str4;
        ContentProviderHolder contentProviderHolder;
        ProcessRecord processRecord3;
        long j;
        ContentProviderConnection contentProviderConnection;
        boolean z4;
        int i5;
        boolean z5;
        boolean z6;
        boolean z7;
        ProcessRecord processRecord4;
        ContentProviderRecord contentProviderRecord2;
        int size;
        int i6;
        ComponentName componentName;
        ProcessRecord processRecord5;
        boolean z8;
        int i7;
        long j2;
        ContentProviderConnection incProviderCountLocked;
        boolean z9;
        ContentProviderConnection contentProviderConnection2;
        ProviderInfo providerInfo2;
        ContentProviderRecord contentProviderRecord3;
        int i8;
        IApplicationThread thread;
        ApplicationInfo applicationInfo;
        boolean z10;
        String str5;
        boolean z11;
        ProcessRecord processRecord6;
        ActivityManagerService activityManagerService2 = this.mService;
        synchronized (activityManagerService2) {
            try {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    long uptimeMillis = SystemClock.uptimeMillis();
                    if (iApplicationThread != null) {
                        ProcessRecord recordForAppLOSP = this.mService.getRecordForAppLOSP(iApplicationThread);
                        if (recordForAppLOSP == null) {
                            throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + Binder.getCallingPid() + ") when getting content provider " + str);
                        }
                        processRecord = recordForAppLOSP;
                    } else {
                        processRecord = null;
                    }
                    checkTime(uptimeMillis, "getContentProviderImpl: getProviderByName");
                    UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
                    UserProperties userProperties = userManagerInternal.getUserProperties(i2);
                    boolean z12 = userProperties != null && userProperties.isMediaSharedWithParent();
                    if (ContentProvider.isAuthorityRedirectedForCloneProfile(str) && z12) {
                        providerByName = null;
                        if (providerByName == null || i2 == 0) {
                            contentProviderRecord = providerByName;
                            i3 = i2;
                        } else {
                            ContentProviderRecord providerByName2 = this.mProviderMap.getProviderByName(str, 0);
                            if (providerByName2 != null) {
                                providerInfo = providerByName2.info;
                                if (this.mService.isSingleton(providerInfo.processName, providerInfo.applicationInfo, providerInfo.name, providerInfo.flags)) {
                                    if (this.mService.isValidSingletonCall(processRecord == null ? i : processRecord.uid, providerInfo.applicationInfo.uid)) {
                                        contentProviderRecord = providerByName2;
                                        z2 = false;
                                        i3 = 0;
                                        if (contentProviderRecord != null || (processRecord6 = contentProviderRecord.proc) == null) {
                                            z3 = false;
                                        } else {
                                            boolean z13 = !processRecord6.isKilled();
                                            if (contentProviderRecord.proc.isKilled() && contentProviderRecord.proc.isKilledByAm()) {
                                                Slog.wtf("ContentProviderHelper", contentProviderRecord.proc.toString() + " was killed by AM but isn't really dead");
                                                z3 = z13;
                                                processRecord2 = contentProviderRecord.proc;
                                                if (z3) {
                                                    activityManagerService = activityManagerService2;
                                                    i4 = i3;
                                                    str4 = str;
                                                    contentProviderHolder = null;
                                                    processRecord3 = processRecord;
                                                    j = uptimeMillis;
                                                    contentProviderConnection = null;
                                                    z4 = z3;
                                                } else {
                                                    ProviderInfo providerInfo3 = contentProviderRecord.info;
                                                    if (processRecord != null && contentProviderRecord.canRunHere(processRecord)) {
                                                        checkAssociationAndPermissionLocked(processRecord, providerInfo3, i, i3, z2, contentProviderRecord.name.flattenToShortString(), uptimeMillis);
                                                        enforceContentProviderRestrictionsForSdkSandbox(providerInfo3);
                                                        ContentProviderHolder newHolder = contentProviderRecord.newHolder(null, true);
                                                        newHolder.provider = null;
                                                        FrameworkStatsLog.write((int) FrameworkStatsLog.PROVIDER_ACQUISITION_EVENT_REPORTED, processRecord.uid, i, 1, 1, providerInfo3.packageName, str2);
                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                        return newHolder;
                                                    }
                                                    ProcessRecord processRecord7 = processRecord;
                                                    try {
                                                        if (AppGlobals.getPackageManager().resolveContentProvider(str, 0L, i3) == null) {
                                                            ActivityManagerService.resetPriorityAfterLockedSection();
                                                            return null;
                                                        }
                                                    } catch (RemoteException unused) {
                                                    }
                                                    checkAssociationAndPermissionLocked(processRecord7, providerInfo3, i, i3, z2, contentProviderRecord.name.flattenToShortString(), uptimeMillis);
                                                    long clearCallingIdentity = Binder.clearCallingIdentity();
                                                    try {
                                                        checkTime(uptimeMillis, "getContentProviderImpl: incProviderCountLocked");
                                                        activityManagerService = activityManagerService2;
                                                        i4 = i3;
                                                        str4 = str;
                                                        try {
                                                            contentProviderConnection = incProviderCountLocked(processRecord7, contentProviderRecord, iBinder, i, str2, str3, z, true, uptimeMillis, this.mService.mProcessList, i2);
                                                            j = uptimeMillis;
                                                            checkTime(j, "getContentProviderImpl: before updateOomAdj");
                                                            int verifiedAdj = contentProviderRecord.proc.mState.getVerifiedAdj();
                                                            boolean updateOomAdjLocked = this.mService.updateOomAdjLocked(contentProviderRecord.proc, 7);
                                                            if (updateOomAdjLocked && verifiedAdj != contentProviderRecord.proc.mState.getSetAdj() && !isProcessAliveLocked(contentProviderRecord.proc)) {
                                                                updateOomAdjLocked = false;
                                                            }
                                                            processRecord3 = processRecord7;
                                                            maybeUpdateProviderUsageStatsLocked(processRecord3, contentProviderRecord.info.packageName, str4);
                                                            checkTime(j, "getContentProviderImpl: after updateOomAdj");
                                                            if (!updateOomAdjLocked) {
                                                                Slog.wtf("ContentProviderHelper", "Existing provider " + contentProviderRecord.name.flattenToShortString() + " is crashing; detaching " + processRecord3);
                                                                if (decProviderCountLocked(contentProviderConnection, contentProviderRecord, iBinder, z, false, false)) {
                                                                    contentProviderHolder = null;
                                                                    processRecord2 = contentProviderRecord.proc;
                                                                    contentProviderConnection = null;
                                                                    providerInfo = providerInfo3;
                                                                    z11 = false;
                                                                } else {
                                                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                                                    return null;
                                                                }
                                                            } else {
                                                                contentProviderHolder = null;
                                                                ProcessStateRecord processStateRecord = contentProviderRecord.proc.mState;
                                                                processStateRecord.setVerifiedAdj(processStateRecord.getSetAdj());
                                                                providerInfo = providerInfo3;
                                                                FrameworkStatsLog.write((int) FrameworkStatsLog.PROVIDER_ACQUISITION_EVENT_REPORTED, contentProviderRecord.proc.uid, i, 1, 1, providerInfo.packageName, str2);
                                                                z11 = z3;
                                                            }
                                                            Binder.restoreCallingIdentity(clearCallingIdentity);
                                                            z4 = z11;
                                                        } catch (Throwable th) {
                                                            th = th;
                                                            Binder.restoreCallingIdentity(clearCallingIdentity);
                                                            throw th;
                                                        }
                                                    } catch (Throwable th2) {
                                                        th = th2;
                                                    }
                                                }
                                                ProcessRecord processRecord8 = processRecord2;
                                                if (z4) {
                                                    try {
                                                        checkTime(j, "getContentProviderImpl: before resolveContentProvider");
                                                        i5 = i4;
                                                        try {
                                                            providerInfo = AppGlobals.getPackageManager().resolveContentProvider(str4, 3072L, i5);
                                                            checkTime(j, "getContentProviderImpl: after resolveContentProvider");
                                                        } catch (RemoteException unused2) {
                                                        }
                                                    } catch (RemoteException unused3) {
                                                        i5 = i4;
                                                    }
                                                    ProviderInfo providerInfo4 = providerInfo;
                                                    if (providerInfo4 == null) {
                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                        return contentProviderHolder;
                                                    }
                                                    if (this.mService.isSingleton(providerInfo4.processName, providerInfo4.applicationInfo, providerInfo4.name, providerInfo4.flags)) {
                                                        if (this.mService.isValidSingletonCall(processRecord3 == null ? i : processRecord3.uid, providerInfo4.applicationInfo.uid)) {
                                                            z5 = true;
                                                            int i9 = !z5 ? 0 : i5;
                                                            providerInfo4.applicationInfo = this.mService.getAppInfoForUser(providerInfo4.applicationInfo, i9);
                                                            checkTime(j, "getContentProviderImpl: got app info for user");
                                                            int i10 = i9;
                                                            ProcessRecord processRecord9 = processRecord3;
                                                            z6 = z4;
                                                            checkAssociationAndPermissionLocked(processRecord3, providerInfo4, i, i9, z5, str, j);
                                                            if (!this.mService.mProcessesReady && !providerInfo4.processName.equals("system")) {
                                                                throw new IllegalArgumentException("Attempt to launch content provider before system ready");
                                                            }
                                                            synchronized (this) {
                                                                if (!this.mSystemProvidersInstalled && providerInfo4.applicationInfo.isSystemApp() && "system".equals(providerInfo4.processName)) {
                                                                    throw new IllegalStateException("Cannot access system provider: '" + providerInfo4.authority + "' before system providers are installed!");
                                                                }
                                                            }
                                                            if (!this.mService.mUserController.isUserRunning(i10, 0)) {
                                                                Slog.w("ContentProviderHelper", "Unable to launch app " + providerInfo4.applicationInfo.packageName + "/" + providerInfo4.applicationInfo.uid + " for provider " + str4 + ": user " + i10 + " is stopped");
                                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                                return null;
                                                            }
                                                            ComponentName componentName2 = new ComponentName(providerInfo4.packageName, providerInfo4.name);
                                                            checkTime(j, "getContentProviderImpl: before getProviderByClass");
                                                            ContentProviderRecord providerByClass = this.mProviderMap.getProviderByClass(componentName2, i10);
                                                            checkTime(j, "getContentProviderImpl: after getProviderByClass");
                                                            if (providerByClass != null && (processRecord8 != providerByClass.proc || processRecord8 == null)) {
                                                                z7 = false;
                                                                if (z7) {
                                                                    processRecord4 = processRecord9;
                                                                } else {
                                                                    long clearCallingIdentity2 = Binder.clearCallingIdentity();
                                                                    processRecord4 = processRecord9;
                                                                    if (!requestTargetProviderPermissionsReviewIfNeededLocked(providerInfo4, processRecord4, i10, this.mService.mContext)) {
                                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                                        return null;
                                                                    }
                                                                    try {
                                                                        checkTime(j, "getContentProviderImpl: before getApplicationInfo");
                                                                        applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(providerInfo4.applicationInfo.packageName, 1024L, i10);
                                                                        checkTime(j, "getContentProviderImpl: after getApplicationInfo");
                                                                    } catch (RemoteException unused4) {
                                                                        Binder.restoreCallingIdentity(clearCallingIdentity2);
                                                                    }
                                                                    if (applicationInfo == null) {
                                                                        Slog.w("ContentProviderHelper", "No package info for content provider " + providerInfo4.name);
                                                                        Binder.restoreCallingIdentity(clearCallingIdentity2);
                                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                                        return null;
                                                                    }
                                                                    ContentProviderRecord contentProviderRecord4 = new ContentProviderRecord(this.mService, providerInfo4, this.mService.getAppInfoForUser(applicationInfo, i10), componentName2, z5);
                                                                    Binder.restoreCallingIdentity(clearCallingIdentity2);
                                                                    contentProviderRecord2 = contentProviderRecord4;
                                                                    checkTime(j, "getContentProviderImpl: now have ContentProviderRecord");
                                                                    if (processRecord4 == null && contentProviderRecord2.canRunHere(processRecord4)) {
                                                                        enforceContentProviderRestrictionsForSdkSandbox(providerInfo4);
                                                                        ContentProviderHolder newHolder2 = contentProviderRecord2.newHolder(null, true);
                                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                                        return newHolder2;
                                                                    }
                                                                    size = this.mLaunchingProviders.size();
                                                                    i6 = 0;
                                                                    while (i6 < size && this.mLaunchingProviders.get(i6) != contentProviderRecord2) {
                                                                        i6++;
                                                                    }
                                                                    if (i6 >= size) {
                                                                        long clearCallingIdentity3 = Binder.clearCallingIdentity();
                                                                        if (!TextUtils.equals(contentProviderRecord2.appInfo.packageName, str2)) {
                                                                            this.mService.mUsageStatsService.reportEvent(contentProviderRecord2.appInfo.packageName, i10, 31);
                                                                        }
                                                                        try {
                                                                            checkTime(j, "getContentProviderImpl: before set stopped state");
                                                                            this.mService.mPackageManagerInt.setPackageStoppedState(contentProviderRecord2.appInfo.packageName, false, i10);
                                                                            checkTime(j, "getContentProviderImpl: after set stopped state");
                                                                        } catch (IllegalArgumentException e) {
                                                                            Slog.w("ContentProviderHelper", "Failed trying to unstop package " + contentProviderRecord2.appInfo.packageName + ": " + e);
                                                                        }
                                                                        checkTime(j, "getContentProviderImpl: looking for process record");
                                                                        ProcessRecord processRecordLocked = this.mService.getProcessRecordLocked(providerInfo4.processName, contentProviderRecord2.appInfo.uid);
                                                                        if (processRecordLocked != null && (thread = processRecordLocked.getThread()) != null && !processRecordLocked.isKilled()) {
                                                                            ProcessProviderRecord processProviderRecord = processRecordLocked.mProviders;
                                                                            if (!processProviderRecord.hasProvider(providerInfo4.name)) {
                                                                                checkTime(j, "getContentProviderImpl: scheduling install");
                                                                                processProviderRecord.installProvider(providerInfo4.name, contentProviderRecord2);
                                                                                try {
                                                                                    thread.scheduleInstallProvider(providerInfo4);
                                                                                } catch (RemoteException unused5) {
                                                                                }
                                                                            }
                                                                            FrameworkStatsLog.write((int) FrameworkStatsLog.PROVIDER_ACQUISITION_EVENT_REPORTED, processRecordLocked.uid, i, 1, 1, providerInfo4.packageName, str2);
                                                                            componentName = componentName2;
                                                                            processRecord5 = processRecord4;
                                                                            z8 = z7;
                                                                            i7 = i10;
                                                                        } else {
                                                                            int i11 = (contentProviderRecord2.appInfo.flags & 2097152) != 0 ? 2 : 1;
                                                                            checkTime(j, "getContentProviderImpl: before start process");
                                                                            i7 = i10;
                                                                            processRecord5 = processRecord4;
                                                                            componentName = componentName2;
                                                                            z8 = z7;
                                                                            processRecordLocked = this.mService.startProcessLocked(providerInfo4.processName, contentProviderRecord2.appInfo, false, 0, new HostingRecord("content provider", new ComponentName(providerInfo4.applicationInfo.packageName, providerInfo4.name)), 0, false, false);
                                                                            checkTime(j, "getContentProviderImpl: after start process");
                                                                            if (processRecordLocked == null) {
                                                                                Slog.w("ContentProviderHelper", "Unable to launch app " + providerInfo4.applicationInfo.packageName + "/" + providerInfo4.applicationInfo.uid + " for provider " + str4 + ": process is bad");
                                                                                Binder.restoreCallingIdentity(clearCallingIdentity3);
                                                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                                                return null;
                                                                            }
                                                                            FrameworkStatsLog.write((int) FrameworkStatsLog.PROVIDER_ACQUISITION_EVENT_REPORTED, processRecordLocked.uid, i, 3, i11, providerInfo4.packageName, str2);
                                                                        }
                                                                        contentProviderRecord2.launchingApp = processRecordLocked;
                                                                        this.mLaunchingProviders.add(contentProviderRecord2);
                                                                        Binder.restoreCallingIdentity(clearCallingIdentity3);
                                                                    } else {
                                                                        componentName = componentName2;
                                                                        processRecord5 = processRecord4;
                                                                        z8 = z7;
                                                                        i7 = i10;
                                                                    }
                                                                    checkTime(j, "getContentProviderImpl: updating data structures");
                                                                    if (z8) {
                                                                        this.mProviderMap.putProviderByClass(componentName, contentProviderRecord2);
                                                                    }
                                                                    this.mProviderMap.putProviderByName(str4, contentProviderRecord2);
                                                                    ContentProviderRecord contentProviderRecord5 = contentProviderRecord2;
                                                                    int i12 = i7;
                                                                    j2 = j;
                                                                    incProviderCountLocked = incProviderCountLocked(processRecord5, contentProviderRecord2, iBinder, i, str2, str3, z, false, j, this.mService.mProcessList, i2);
                                                                    if (incProviderCountLocked != null) {
                                                                        z9 = true;
                                                                        incProviderCountLocked.waiting = true;
                                                                    } else {
                                                                        z9 = true;
                                                                    }
                                                                    contentProviderConnection2 = incProviderCountLocked;
                                                                    providerInfo2 = providerInfo4;
                                                                    contentProviderRecord3 = contentProviderRecord5;
                                                                    i8 = i12;
                                                                }
                                                                contentProviderRecord2 = providerByClass;
                                                                checkTime(j, "getContentProviderImpl: now have ContentProviderRecord");
                                                                if (processRecord4 == null) {
                                                                }
                                                                size = this.mLaunchingProviders.size();
                                                                i6 = 0;
                                                                while (i6 < size) {
                                                                    i6++;
                                                                }
                                                                if (i6 >= size) {
                                                                }
                                                                checkTime(j, "getContentProviderImpl: updating data structures");
                                                                if (z8) {
                                                                }
                                                                this.mProviderMap.putProviderByName(str4, contentProviderRecord2);
                                                                ContentProviderRecord contentProviderRecord52 = contentProviderRecord2;
                                                                int i122 = i7;
                                                                j2 = j;
                                                                incProviderCountLocked = incProviderCountLocked(processRecord5, contentProviderRecord2, iBinder, i, str2, str3, z, false, j, this.mService.mProcessList, i2);
                                                                if (incProviderCountLocked != null) {
                                                                }
                                                                contentProviderConnection2 = incProviderCountLocked;
                                                                providerInfo2 = providerInfo4;
                                                                contentProviderRecord3 = contentProviderRecord52;
                                                                i8 = i122;
                                                            }
                                                            z7 = true;
                                                            if (z7) {
                                                            }
                                                            contentProviderRecord2 = providerByClass;
                                                            checkTime(j, "getContentProviderImpl: now have ContentProviderRecord");
                                                            if (processRecord4 == null) {
                                                            }
                                                            size = this.mLaunchingProviders.size();
                                                            i6 = 0;
                                                            while (i6 < size) {
                                                            }
                                                            if (i6 >= size) {
                                                            }
                                                            checkTime(j, "getContentProviderImpl: updating data structures");
                                                            if (z8) {
                                                            }
                                                            this.mProviderMap.putProviderByName(str4, contentProviderRecord2);
                                                            ContentProviderRecord contentProviderRecord522 = contentProviderRecord2;
                                                            int i1222 = i7;
                                                            j2 = j;
                                                            incProviderCountLocked = incProviderCountLocked(processRecord5, contentProviderRecord2, iBinder, i, str2, str3, z, false, j, this.mService.mProcessList, i2);
                                                            if (incProviderCountLocked != null) {
                                                            }
                                                            contentProviderConnection2 = incProviderCountLocked;
                                                            providerInfo2 = providerInfo4;
                                                            contentProviderRecord3 = contentProviderRecord522;
                                                            i8 = i1222;
                                                        }
                                                    }
                                                    z5 = false;
                                                    if (!z5) {
                                                    }
                                                    providerInfo4.applicationInfo = this.mService.getAppInfoForUser(providerInfo4.applicationInfo, i9);
                                                    checkTime(j, "getContentProviderImpl: got app info for user");
                                                    int i102 = i9;
                                                    ProcessRecord processRecord92 = processRecord3;
                                                    z6 = z4;
                                                    checkAssociationAndPermissionLocked(processRecord3, providerInfo4, i, i9, z5, str, j);
                                                    if (!this.mService.mProcessesReady) {
                                                        throw new IllegalArgumentException("Attempt to launch content provider before system ready");
                                                    }
                                                    synchronized (this) {
                                                    }
                                                } else {
                                                    z6 = z4;
                                                    j2 = j;
                                                    z9 = true;
                                                    i8 = i4;
                                                    ContentProviderConnection contentProviderConnection3 = contentProviderConnection;
                                                    contentProviderRecord3 = contentProviderRecord;
                                                    providerInfo2 = providerInfo;
                                                    contentProviderConnection2 = contentProviderConnection3;
                                                }
                                                checkTime(j2, "getContentProviderImpl: done!");
                                                this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                                                boolean z14 = z9;
                                                if (iApplicationThread == null) {
                                                    synchronized (contentProviderRecord3) {
                                                        if (contentProviderRecord3.provider == null) {
                                                            if (contentProviderRecord3.launchingApp == null) {
                                                                Slog.w("ContentProviderHelper", "Unable to launch app " + providerInfo2.applicationInfo.packageName + "/" + providerInfo2.applicationInfo.uid + " for provider " + str4 + ": launching app became null");
                                                                int userId = UserHandle.getUserId(providerInfo2.applicationInfo.uid);
                                                                ApplicationInfo applicationInfo2 = providerInfo2.applicationInfo;
                                                                EventLogTags.writeAmProviderLostProcess(userId, applicationInfo2.packageName, applicationInfo2.uid, str4);
                                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                                return null;
                                                            }
                                                            if (contentProviderConnection2 != null) {
                                                                contentProviderConnection2.waiting = z14;
                                                            }
                                                            Message obtainMessage = this.mService.mHandler.obtainMessage(73);
                                                            obtainMessage.obj = contentProviderRecord3;
                                                            this.mService.mHandler.sendMessageDelayed(obtainMessage, ContentResolver.CONTENT_PROVIDER_READY_TIMEOUT_MILLIS);
                                                        }
                                                        enforceContentProviderRestrictionsForSdkSandbox(providerInfo2);
                                                        ContentProviderHolder newHolder3 = contentProviderRecord3.newHolder(contentProviderConnection2, false);
                                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                                        return newHolder3;
                                                    }
                                                }
                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                long uptimeMillis2 = SystemClock.uptimeMillis() + ContentResolver.CONTENT_PROVIDER_READY_TIMEOUT_MILLIS;
                                                synchronized (contentProviderRecord3) {
                                                    while (true) {
                                                        if (contentProviderRecord3.provider != null) {
                                                            z10 = false;
                                                            break;
                                                        } else if (contentProviderRecord3.launchingApp == null) {
                                                            Slog.w("ContentProviderHelper", "Unable to launch app " + providerInfo2.applicationInfo.packageName + "/" + providerInfo2.applicationInfo.uid + " for provider " + str4 + ": launching app became null");
                                                            int userId2 = UserHandle.getUserId(providerInfo2.applicationInfo.uid);
                                                            ApplicationInfo applicationInfo3 = providerInfo2.applicationInfo;
                                                            EventLogTags.writeAmProviderLostProcess(userId2, applicationInfo3.packageName, applicationInfo3.uid, str4);
                                                            return null;
                                                        } else {
                                                            try {
                                                                try {
                                                                    long max = Math.max(0L, uptimeMillis2 - SystemClock.uptimeMillis());
                                                                    if (contentProviderConnection2 != null) {
                                                                        contentProviderConnection2.waiting = z14;
                                                                    }
                                                                    contentProviderRecord3.wait(max);
                                                                } catch (InterruptedException unused6) {
                                                                    if (contentProviderConnection2 != null) {
                                                                        contentProviderConnection2.waiting = false;
                                                                    }
                                                                }
                                                            } catch (InterruptedException unused7) {
                                                            }
                                                            if (contentProviderRecord3.provider == null) {
                                                                if (contentProviderConnection2 != null) {
                                                                    contentProviderConnection2.waiting = false;
                                                                }
                                                                z10 = z14;
                                                            } else if (contentProviderConnection2 != null) {
                                                                contentProviderConnection2.waiting = false;
                                                            }
                                                        }
                                                    }
                                                    if (z10) {
                                                        str5 = "unknown";
                                                        if (iApplicationThread != null) {
                                                            synchronized (this.mService.mProcLock) {
                                                                try {
                                                                    ActivityManagerService.boostPriorityForProcLockedSection();
                                                                    ProcessRecord lRURecordForAppLOSP = this.mService.mProcessList.getLRURecordForAppLOSP(iApplicationThread);
                                                                    str5 = lRURecordForAppLOSP != null ? lRURecordForAppLOSP.processName : "unknown";
                                                                } catch (Throwable th3) {
                                                                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                                                                    throw th3;
                                                                }
                                                            }
                                                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                                                        }
                                                        Slog.wtf("ContentProviderHelper", "Timeout waiting for provider " + providerInfo2.applicationInfo.packageName + "/" + providerInfo2.applicationInfo.uid + " for provider " + str4 + " providerRunning=" + z6 + " caller=" + str5 + "/" + Binder.getCallingUid());
                                                        return null;
                                                    }
                                                    enforceContentProviderRestrictionsForSdkSandbox(providerInfo2);
                                                    return contentProviderRecord3.newHolder(contentProviderConnection2, false);
                                                }
                                            }
                                            z3 = z13;
                                        }
                                        processRecord2 = null;
                                        if (z3) {
                                        }
                                        ProcessRecord processRecord82 = processRecord2;
                                        if (z4) {
                                        }
                                        checkTime(j2, "getContentProviderImpl: done!");
                                        this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                                        boolean z142 = z9;
                                        if (iApplicationThread == null) {
                                        }
                                    }
                                }
                                if (!ContentProvider.isAuthorityRedirectedForCloneProfile(str)) {
                                    i3 = i2;
                                    contentProviderRecord = null;
                                } else if (z12) {
                                    i3 = userManagerInternal.getProfileParentId(i2);
                                    contentProviderRecord = providerByName2;
                                    z2 = false;
                                    if (contentProviderRecord != null) {
                                    }
                                    z3 = false;
                                    processRecord2 = null;
                                    if (z3) {
                                    }
                                    ProcessRecord processRecord822 = processRecord2;
                                    if (z4) {
                                    }
                                    checkTime(j2, "getContentProviderImpl: done!");
                                    this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                                    boolean z1422 = z9;
                                    if (iApplicationThread == null) {
                                    }
                                } else {
                                    i3 = i2;
                                    contentProviderRecord = providerByName2;
                                    z2 = true;
                                    if (contentProviderRecord != null) {
                                    }
                                    z3 = false;
                                    processRecord2 = null;
                                    if (z3) {
                                    }
                                    ProcessRecord processRecord8222 = processRecord2;
                                    if (z4) {
                                    }
                                    checkTime(j2, "getContentProviderImpl: done!");
                                    this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                                    boolean z14222 = z9;
                                    if (iApplicationThread == null) {
                                    }
                                }
                            } else {
                                i3 = i2;
                                contentProviderRecord = providerByName2;
                            }
                        }
                        providerInfo = null;
                        z2 = true;
                        if (contentProviderRecord != null) {
                        }
                        z3 = false;
                        processRecord2 = null;
                        if (z3) {
                        }
                        ProcessRecord processRecord82222 = processRecord2;
                        if (z4) {
                        }
                        checkTime(j2, "getContentProviderImpl: done!");
                        this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                        boolean z142222 = z9;
                        if (iApplicationThread == null) {
                        }
                    }
                    providerByName = this.mProviderMap.getProviderByName(str, i2);
                    if (providerByName == null) {
                    }
                    contentProviderRecord = providerByName;
                    i3 = i2;
                    providerInfo = null;
                    z2 = true;
                    if (contentProviderRecord != null) {
                    }
                    z3 = false;
                    processRecord2 = null;
                    if (z3) {
                    }
                    ProcessRecord processRecord822222 = processRecord2;
                    if (z4) {
                    }
                    checkTime(j2, "getContentProviderImpl: done!");
                    this.mService.grantImplicitAccess(i8, null, i, UserHandle.getAppId(providerInfo2.applicationInfo.uid));
                    boolean z1422222 = z9;
                    if (iApplicationThread == null) {
                    }
                } catch (Throwable th4) {
                    th = th4;
                    activityManagerService = activityManagerService2;
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void checkAssociationAndPermissionLocked(ProcessRecord processRecord, ProviderInfo providerInfo, int i, int i2, boolean z, String str, long j) {
        String checkContentProviderAssociation = checkContentProviderAssociation(processRecord, i, providerInfo);
        if (checkContentProviderAssociation != null) {
            throw new SecurityException("Content provider lookup " + str + " failed: association not allowed with package " + checkContentProviderAssociation);
        }
        checkTime(j, "getContentProviderImpl: before checkContentProviderPermission");
        String checkContentProviderPermission = checkContentProviderPermission(providerInfo, Binder.getCallingPid(), Binder.getCallingUid(), i2, z, processRecord != null ? processRecord.toString() : null);
        if (checkContentProviderPermission != null) {
            throw new SecurityException(checkContentProviderPermission);
        }
        checkTime(j, "getContentProviderImpl: after checkContentProviderPermission");
    }

    public void publishContentProviders(IApplicationThread iApplicationThread, List<ContentProviderHolder> list) {
        ProviderInfo providerInfo;
        int i;
        ProviderInfo providerInfo2;
        ContentProviderRecord provider;
        if (list == null) {
            return;
        }
        this.mService.enforceNotIsolatedOrSdkSandboxCaller("publishContentProviders");
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ProcessRecord recordForAppLOSP = this.mService.getRecordForAppLOSP(iApplicationThread);
                if (recordForAppLOSP == null) {
                    throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + Binder.getCallingPid() + ") when publishing content providers");
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                int size = list.size();
                int i2 = 0;
                boolean z = false;
                while (i2 < size) {
                    ContentProviderHolder contentProviderHolder = list.get(i2);
                    if (contentProviderHolder != null && (providerInfo2 = contentProviderHolder.info) != null && contentProviderHolder.provider != null && (provider = recordForAppLOSP.mProviders.getProvider(providerInfo2.name)) != null) {
                        ProviderInfo providerInfo3 = provider.info;
                        this.mProviderMap.putProviderByClass(new ComponentName(providerInfo3.packageName, providerInfo3.name), provider);
                        for (String str : provider.info.authority.split(";")) {
                            this.mProviderMap.putProviderByName(str, provider);
                        }
                        int size2 = this.mLaunchingProviders.size();
                        int i3 = 0;
                        boolean z2 = false;
                        while (i3 < size2) {
                            if (this.mLaunchingProviders.get(i3) == provider) {
                                this.mLaunchingProviders.remove(i3);
                                i3--;
                                size2--;
                                z2 = true;
                            }
                            i3++;
                        }
                        if (z2) {
                            this.mService.mHandler.removeMessages(73, provider);
                            this.mService.mHandler.removeMessages(57, recordForAppLOSP);
                        }
                        ApplicationInfo applicationInfo = provider.info.applicationInfo;
                        i = i2;
                        recordForAppLOSP.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
                        synchronized (provider) {
                            provider.provider = contentProviderHolder.provider;
                            provider.setProcess(recordForAppLOSP);
                            provider.notifyAll();
                            provider.onProviderPublishStatusLocked(true);
                        }
                        provider.mRestartCount = 0;
                        if (hasProviderConnectionLocked(recordForAppLOSP)) {
                            recordForAppLOSP.mProfile.addHostingComponentType(64);
                        }
                        z = true;
                        i2 = i + 1;
                    }
                    i = i2;
                    i2 = i + 1;
                }
                if (z) {
                    this.mService.updateOomAdjLocked(recordForAppLOSP, 7);
                    int size3 = list.size();
                    for (int i4 = 0; i4 < size3; i4++) {
                        ContentProviderHolder contentProviderHolder2 = list.get(i4);
                        if (contentProviderHolder2 != null && (providerInfo = contentProviderHolder2.info) != null && contentProviderHolder2.provider != null) {
                            maybeUpdateProviderUsageStatsLocked(recordForAppLOSP, providerInfo.packageName, providerInfo.authority);
                        }
                    }
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void removeContentProvider(IBinder iBinder, boolean z) {
        ProviderInfo providerInfo;
        this.mService.enforceNotIsolatedOrSdkSandboxCaller("removeContentProvider");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                ContentProviderConnection contentProviderConnection = (ContentProviderConnection) iBinder;
                if (contentProviderConnection == null) {
                    throw new NullPointerException("connection is null");
                }
                ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
                ActivityManagerService.traceBegin(64L, "removeContentProvider: ", (contentProviderRecord == null || (providerInfo = contentProviderRecord.info) == null) ? "" : providerInfo.authority);
                synchronized (this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        decProviderCountLocked(contentProviderConnection, null, null, z, true, true);
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                Trace.traceEnd(64L);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (ClassCastException unused) {
                String str = "removeContentProvider: " + iBinder + " not a ContentProviderConnection";
                Slog.w("ContentProviderHelper", str);
                throw new IllegalArgumentException(str);
            }
        } catch (Throwable th2) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th2;
        }
    }

    public void removeContentProviderExternalAsUser(String str, IBinder iBinder, int i) {
        this.mService.enforceCallingPermission("android.permission.ACCESS_CONTENT_PROVIDERS_EXTERNALLY", "Do not have permission in call removeContentProviderExternal()");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeContentProviderExternalUnchecked(str, iBinder, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeContentProviderExternalUnchecked(String str, IBinder iBinder, int i) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ContentProviderRecord providerByName = this.mProviderMap.getProviderByName(str, i);
                if (providerByName == null) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ProviderInfo providerInfo = providerByName.info;
                ContentProviderRecord providerByClass = this.mProviderMap.getProviderByClass(new ComponentName(providerInfo.packageName, providerInfo.name), i);
                if (providerByClass.hasExternalProcessHandles()) {
                    if (providerByClass.removeExternalProcessHandleLocked(iBinder)) {
                        this.mService.updateOomAdjLocked(providerByClass.proc, 8);
                    } else {
                        Slog.e("ContentProviderHelper", "Attempt to remove content provider " + providerByClass + " with no external reference for token: " + iBinder + ".");
                    }
                } else {
                    Slog.e("ContentProviderHelper", "Attempt to remove content provider: " + providerByClass + " with no external references.");
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean refContentProvider(IBinder iBinder, int i, int i2) {
        ProviderInfo providerInfo;
        try {
            ContentProviderConnection contentProviderConnection = (ContentProviderConnection) iBinder;
            if (contentProviderConnection == null) {
                throw new NullPointerException("connection is null");
            }
            ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
            ActivityManagerService.traceBegin(64L, "refContentProvider: ", (contentProviderRecord == null || (providerInfo = contentProviderRecord.info) == null) ? "" : providerInfo.authority);
            try {
                contentProviderConnection.adjustCounts(i, i2);
                return !contentProviderConnection.dead;
            } finally {
                Trace.traceEnd(64L);
            }
        } catch (ClassCastException unused) {
            String str = "refContentProvider: " + iBinder + " not a ContentProviderConnection";
            Slog.w("ContentProviderHelper", str);
            throw new IllegalArgumentException(str);
        }
    }

    public void unstableProviderDied(IBinder iBinder) {
        IContentProvider iContentProvider;
        ProviderInfo providerInfo;
        try {
            ContentProviderConnection contentProviderConnection = (ContentProviderConnection) iBinder;
            if (contentProviderConnection == null) {
                throw new NullPointerException("connection is null");
            }
            ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
            ActivityManagerService.traceBegin(64L, "unstableProviderDied: ", (contentProviderRecord == null || (providerInfo = contentProviderRecord.info) == null) ? "" : providerInfo.authority);
            try {
                synchronized (this.mService) {
                    ActivityManagerService.boostPriorityForLockedSection();
                    iContentProvider = contentProviderConnection.provider.provider;
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                if (iContentProvider == null) {
                    return;
                }
                if (iContentProvider.asBinder().pingBinder()) {
                    synchronized (this.mService) {
                        ActivityManagerService.boostPriorityForLockedSection();
                        Slog.w("ContentProviderHelper", "unstableProviderDied: caller " + Binder.getCallingUid() + " says " + contentProviderConnection + " died, but we don't agree");
                    }
                } else {
                    synchronized (this.mService) {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ContentProviderRecord contentProviderRecord2 = contentProviderConnection.provider;
                        if (contentProviderRecord2.provider == iContentProvider) {
                            ProcessRecord processRecord = contentProviderRecord2.proc;
                            if (processRecord != null && processRecord.getThread() != null) {
                                this.mService.reportUidInfoMessageLocked("ContentProviderHelper", "Process " + processRecord.processName + " (pid " + processRecord.getPid() + ") early provider death", processRecord.info.uid);
                                long clearCallingIdentity = Binder.clearCallingIdentity();
                                try {
                                    this.mService.appDiedLocked(processRecord, "unstable content provider");
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                    return;
                                } finally {
                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                }
                            }
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } finally {
                Trace.traceEnd(64L);
            }
        } catch (ClassCastException unused) {
            String str = "refContentProvider: " + iBinder + " not a ContentProviderConnection";
            Slog.w("ContentProviderHelper", str);
            throw new IllegalArgumentException(str);
        }
    }

    public void appNotRespondingViaProvider(IBinder iBinder) {
        ProviderInfo providerInfo;
        this.mService.enforceCallingPermission("android.permission.REMOVE_TASKS", "appNotRespondingViaProvider()");
        ContentProviderConnection contentProviderConnection = (ContentProviderConnection) iBinder;
        if (contentProviderConnection == null) {
            Slog.w("ContentProviderHelper", "ContentProviderConnection is null");
            return;
        }
        ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
        ActivityManagerService.traceBegin(64L, "appNotRespondingViaProvider: ", (contentProviderRecord == null || (providerInfo = contentProviderRecord.info) == null) ? "" : providerInfo.authority);
        try {
            ProcessRecord processRecord = contentProviderConnection.provider.proc;
            if (processRecord == null) {
                Slog.w("ContentProviderHelper", "Failed to find hosting ProcessRecord");
            } else {
                this.mService.mAnrHelper.appNotResponding(processRecord, TimeoutRecord.forContentProvider("ContentProvider not responding"));
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void getMimeTypeFilterAsync(final Uri uri, int i, final RemoteCallback remoteCallback) {
        this.mService.enforceNotIsolatedCaller("getProviderMimeTypeAsync");
        final String authority = uri.getAuthority();
        final int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        final int unsafeConvertIncomingUser = this.mService.mUserController.unsafeConvertIncomingUser(i);
        long clearCallingIdentity = canClearIdentity(callingPid, callingUid, unsafeConvertIncomingUser) ? Binder.clearCallingIdentity() : 0L;
        try {
            ContentProviderHolder contentProviderExternalUnchecked = getContentProviderExternalUnchecked(authority, null, callingUid, "*getmimetype*", unsafeConvertIncomingUser);
            try {
                if (isHolderVisibleToCaller(contentProviderExternalUnchecked, callingUid, unsafeConvertIncomingUser)) {
                    if (checkGetAnyTypePermission(callingUid, callingPid)) {
                        contentProviderExternalUnchecked.provider.getTypeAsync(new AttributionSource.Builder(callingUid).build(), uri, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.am.ContentProviderHelper$$ExternalSyntheticLambda1
                            public final void onResult(Bundle bundle) {
                                ContentProviderHelper.this.lambda$getMimeTypeFilterAsync$0(authority, unsafeConvertIncomingUser, remoteCallback, bundle);
                            }
                        }));
                        return;
                    } else {
                        contentProviderExternalUnchecked.provider.getTypeAnonymousAsync(uri, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.am.ContentProviderHelper$$ExternalSyntheticLambda2
                            public final void onResult(Bundle bundle) {
                                ContentProviderHelper.this.lambda$getMimeTypeFilterAsync$1(authority, unsafeConvertIncomingUser, remoteCallback, callingUid, uri, bundle);
                            }
                        }));
                        return;
                    }
                }
                remoteCallback.sendResult(Bundle.EMPTY);
            } catch (RemoteException e) {
                Log.w("ContentProviderHelper", "Content provider dead retrieving " + uri, e);
                remoteCallback.sendResult(Bundle.EMPTY);
            }
        } finally {
            if (clearCallingIdentity != 0) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMimeTypeFilterAsync$0(String str, int i, RemoteCallback remoteCallback, Bundle bundle) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeContentProviderExternalUnchecked(str, null, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            remoteCallback.sendResult(bundle);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMimeTypeFilterAsync$1(String str, int i, RemoteCallback remoteCallback, int i2, Uri uri, Bundle bundle) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeContentProviderExternalUnchecked(str, null, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            remoteCallback.sendResult(bundle);
            String pairValue = bundle.getPairValue();
            if (pairValue != null) {
                logGetTypeData(i2, uri, pairValue);
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final boolean checkGetAnyTypePermission(int i, int i2) {
        return this.mService.checkPermission("android.permission.GET_ANY_PROVIDER_TYPE", i2, i) == 0;
    }

    public final void logGetTypeData(int i, Uri uri, String str) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.GET_TYPE_ACCESSED_WITHOUT_PERMISSION, 1, i, uri.getAuthority(), str);
    }

    public final boolean canClearIdentity(int i, int i2, int i3) {
        return UserHandle.getUserId(i2) == i3 || ActivityManagerService.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS", i, i2, -1, true) == 0 || ActivityManagerService.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i, i2, -1, true) == 0;
    }

    public final boolean isHolderVisibleToCaller(ContentProviderHolder contentProviderHolder, int i, int i2) {
        ProviderInfo providerInfo;
        if (contentProviderHolder == null || (providerInfo = contentProviderHolder.info) == null) {
            return false;
        }
        if (ContentProvider.isAuthorityRedirectedForCloneProfile(providerInfo.authority) && resolveParentUserIdForCloneProfile(i2) != i2) {
            return !this.mService.getPackageManagerInternal().filterAppAccess(contentProviderHolder.info.packageName, i, i2, false);
        }
        return !this.mService.getPackageManagerInternal().filterAppAccess(contentProviderHolder.info.packageName, i, i2);
    }

    public static int resolveParentUserIdForCloneProfile(int i) {
        UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        UserInfo userInfo = userManagerInternal.getUserInfo(i);
        return (userInfo == null || !userInfo.isCloneProfile()) ? i : userManagerInternal.getProfileParentId(i);
    }

    public String checkContentProviderAccess(String str, int i) {
        boolean z;
        ProviderInfo providerInfo;
        UserManagerInternal userManagerInternal;
        UserInfo userInfo;
        if (i == -1) {
            this.mService.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "ContentProviderHelper");
            i = UserHandle.getCallingUserId();
        }
        if (ContentProvider.isAuthorityRedirectedForCloneProfile(str) && (userInfo = (userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserInfo(i)) != null && userInfo.isCloneProfile()) {
            i = userManagerInternal.getProfileParentId(i);
            z = false;
        } else {
            z = true;
        }
        int i2 = i;
        boolean z2 = z;
        try {
            providerInfo = AppGlobals.getPackageManager().resolveContentProvider(str, 790016L, i2);
        } catch (RemoteException unused) {
            providerInfo = null;
        }
        ProviderInfo providerInfo2 = providerInfo;
        if (providerInfo2 == null) {
            return "Failed to find provider " + str + " for user " + i2 + "; expected to find a valid ContentProvider for this authority";
        }
        int callingPid = Binder.getCallingPid();
        synchronized (this.mService.mPidsSelfLocked) {
            ProcessRecord processRecord = this.mService.mPidsSelfLocked.get(callingPid);
            if (processRecord == null) {
                return "Failed to find PID " + callingPid;
            }
            String processRecord2 = processRecord.toString();
            enforceContentProviderRestrictionsForSdkSandbox(providerInfo2);
            return checkContentProviderPermission(providerInfo2, callingPid, Binder.getCallingUid(), i2, z2, processRecord2);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v3, types: [boolean] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int checkContentProviderUriPermission(Uri uri, int i, int i2, int i3) {
        ContentProviderHolder contentProviderHolder;
        ?? holdsLock = Thread.holdsLock(this.mService.mActivityTaskManager.getGlobalLock());
        if (holdsLock != 0) {
            Slog.wtf("ContentProviderHelper", new IllegalStateException("Unable to check Uri permission because caller is holding WM lock; assuming permission denied"));
            return -1;
        }
        String authority = uri.getAuthority();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                contentProviderHolder = getContentProviderExternalUnchecked(authority, null, i2, "*checkContentProviderUriPermission*", i);
                if (contentProviderHolder == null) {
                    if (contentProviderHolder != null) {
                        try {
                            removeContentProviderExternalUnchecked(authority, null, i);
                        } finally {
                        }
                    }
                    return -1;
                }
                try {
                    AndroidPackage androidPackage = this.mService.getPackageManagerInternal().getPackage(Binder.getCallingUid());
                    if (androidPackage == null) {
                        try {
                            removeContentProviderExternalUnchecked(authority, null, i);
                            return -1;
                        } finally {
                        }
                    }
                    int checkUriPermission = contentProviderHolder.provider.checkUriPermission(new AttributionSource(i2, androidPackage.getPackageName(), null), uri, i2, i3);
                    try {
                        removeContentProviderExternalUnchecked(authority, null, i);
                        return checkUriPermission;
                    } finally {
                    }
                } catch (RemoteException e) {
                    e = e;
                    Log.w("ContentProviderHelper", "Content provider dead retrieving " + uri, e);
                    if (contentProviderHolder != null) {
                        try {
                            removeContentProviderExternalUnchecked(authority, null, i);
                        } finally {
                        }
                    }
                    return -1;
                } catch (Exception e2) {
                    e = e2;
                    Log.w("ContentProviderHelper", "Exception while determining type of " + uri, e);
                    if (contentProviderHolder != null) {
                        try {
                            removeContentProviderExternalUnchecked(authority, null, i);
                        } finally {
                        }
                    }
                    return -1;
                }
            } catch (Throwable th) {
                th = th;
                if (holdsLock != 0) {
                    try {
                        removeContentProviderExternalUnchecked(authority, null, i);
                    } finally {
                    }
                }
                throw th;
            }
        } catch (RemoteException e3) {
            e = e3;
            contentProviderHolder = null;
        } catch (Exception e4) {
            e = e4;
            contentProviderHolder = null;
        } catch (Throwable th2) {
            th = th2;
            holdsLock = 0;
            if (holdsLock != 0) {
            }
            throw th;
        }
    }

    @GuardedBy({"mService"})
    public void processContentProviderPublishTimedOutLocked(ProcessRecord processRecord) {
        cleanupAppInLaunchingProvidersLocked(processRecord, true);
        this.mService.mProcessList.removeProcessLocked(processRecord, false, true, 7, 0, "timeout publishing content providers");
    }

    public List<ProviderInfo> generateApplicationProvidersLocked(ProcessRecord processRecord) {
        try {
            List<ProviderInfo> list = AppGlobals.getPackageManager().queryContentProviders(processRecord.processName, processRecord.uid, 268438528L, (String) null).getList();
            if (list == null) {
                return null;
            }
            int size = list.size();
            ProcessProviderRecord processProviderRecord = processRecord.mProviders;
            processProviderRecord.ensureProviderCapacity(processProviderRecord.numberOfProviders() + size);
            int i = 0;
            while (i < size) {
                ProviderInfo providerInfo = list.get(i);
                boolean isSingleton = this.mService.isSingleton(providerInfo.processName, providerInfo.applicationInfo, providerInfo.name, providerInfo.flags);
                if (isSingleton && processRecord.userId != 0) {
                    list.remove(i);
                } else {
                    boolean isInstantApp = providerInfo.applicationInfo.isInstantApp();
                    String str = providerInfo.splitName;
                    boolean z = str == null || ArrayUtils.contains(providerInfo.applicationInfo.splitNames, str);
                    if (isInstantApp && !z) {
                        list.remove(i);
                    } else {
                        ComponentName componentName = new ComponentName(providerInfo.packageName, providerInfo.name);
                        ContentProviderRecord providerByClass = this.mProviderMap.getProviderByClass(componentName, processRecord.userId);
                        if (providerByClass == null) {
                            ContentProviderRecord contentProviderRecord = new ContentProviderRecord(this.mService, providerInfo, processRecord.info, componentName, isSingleton);
                            this.mProviderMap.putProviderByClass(componentName, contentProviderRecord);
                            providerByClass = contentProviderRecord;
                        }
                        processProviderRecord.installProvider(providerInfo.name, providerByClass);
                        if (!providerInfo.multiprocess || !PackageManagerShellCommandDataLoader.PACKAGE.equals(providerInfo.packageName)) {
                            ApplicationInfo applicationInfo = providerInfo.applicationInfo;
                            processRecord.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
                        }
                        this.mService.notifyPackageUse(providerInfo.applicationInfo.packageName, 4);
                        i++;
                    }
                }
                size--;
                i--;
                i++;
            }
            if (list.isEmpty()) {
                return null;
            }
            return list;
        } catch (RemoteException unused) {
            return null;
        }
    }

    /* renamed from: com.android.server.am.ContentProviderHelper$DevelopmentSettingsObserver */
    /* loaded from: classes.dex */
    public final class DevelopmentSettingsObserver extends ContentObserver {
        public final ComponentName mBugreportStorageProvider;
        public final Uri mUri;

        public DevelopmentSettingsObserver() {
            super(ContentProviderHelper.this.mService.mHandler);
            Uri uriFor = Settings.Global.getUriFor("development_settings_enabled");
            this.mUri = uriFor;
            this.mBugreportStorageProvider = new ComponentName("com.android.shell", "com.android.shell.BugreportStorageProvider");
            ContentProviderHelper.this.mService.mContext.getContentResolver().registerContentObserver(uriFor, false, this, -1);
            onChange();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.mUri.equals(uri)) {
                onChange();
            }
        }

        public final void onChange() {
            ContentProviderHelper.this.mService.mContext.getPackageManager().setComponentEnabledSetting(this.mBugreportStorageProvider, Settings.Global.getInt(ContentProviderHelper.this.mService.mContext.getContentResolver(), "development_settings_enabled", Build.IS_ENG ? 1 : 0) != 0 ? 1 : 0, 0);
        }
    }

    public final void installSystemProviders() {
        List<ProviderInfo> generateApplicationProvidersLocked;
        ProviderInfo providerInfo;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                generateApplicationProvidersLocked = generateApplicationProvidersLocked((ProcessRecord) this.mService.mProcessList.getProcessNamesLOSP().get("system", 1000));
                if (generateApplicationProvidersLocked != null) {
                    for (int size = generateApplicationProvidersLocked.size() - 1; size >= 0; size--) {
                        if ((generateApplicationProvidersLocked.get(size).applicationInfo.flags & 1) == 0) {
                            Slog.w("ContentProviderHelper", "Not installing system proc provider " + providerInfo.name + ": not system .apk");
                            generateApplicationProvidersLocked.remove(size);
                        }
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if (generateApplicationProvidersLocked != null) {
            this.mService.mSystemThread.installSystemProviders(generateApplicationProvidersLocked);
        }
        synchronized (this) {
            this.mSystemProvidersInstalled = true;
        }
        ActivityManagerService activityManagerService = this.mService;
        activityManagerService.mConstants.start(activityManagerService.mContext.getContentResolver());
        this.mService.mCoreSettingsObserver = new CoreSettingsObserver(this.mService);
        this.mService.mActivityTaskManager.installSystemProviders();
        new DevelopmentSettingsObserver();
        SettingsToPropertiesMapper.start(this.mService.mContext.getContentResolver());
        this.mService.mOomAdjuster.initSettings();
        RescueParty.onSettingsProviderPublished(this.mService.mContext);
    }

    public void installEncryptionUnawareProviders(int i) {
        synchronized (this.mService.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                ArrayMap map = this.mService.mProcessList.getProcessNamesLOSP().getMap();
                int size = map.size();
                for (int i2 = 0; i2 < size; i2++) {
                    SparseArray sparseArray = (SparseArray) map.valueAt(i2);
                    int size2 = sparseArray.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        final ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i3);
                        if (processRecord.userId == i && processRecord.getThread() != null && !processRecord.isUnlocked()) {
                            processRecord.getPkgList().forEachPackage(new Consumer() { // from class: com.android.server.am.ContentProviderHelper$$ExternalSyntheticLambda4
                                @Override // java.util.function.Consumer
                                public final void accept(Object obj) {
                                    ContentProviderHelper.this.lambda$installEncryptionUnawareProviders$2(processRecord, (String) obj);
                                }
                            });
                        }
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0059 A[Catch: RemoteException -> 0x009e, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x009e, blocks: (B:2:0x0000, B:4:0x0013, B:6:0x001b, B:8:0x0022, B:10:0x002f, B:15:0x0037, B:17:0x0047, B:22:0x004f, B:24:0x0059, B:33:0x006f, B:34:0x0087), top: B:38:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0069 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$installEncryptionUnawareProviders$2(ProcessRecord processRecord, String str) {
        ProviderInfo[] providerInfoArr;
        boolean z;
        boolean z2;
        String str2;
        try {
            PackageInfo packageInfo = AppGlobals.getPackageManager().getPackageInfo(str, 262152L, processRecord.userId);
            IApplicationThread thread = processRecord.getThread();
            if (packageInfo == null || ArrayUtils.isEmpty(packageInfo.providers)) {
                return;
            }
            for (ProviderInfo providerInfo : packageInfo.providers) {
                boolean z3 = true;
                if (!Objects.equals(providerInfo.processName, processRecord.processName) && !providerInfo.multiprocess) {
                    z = false;
                    if (this.mService.isSingleton(providerInfo.processName, providerInfo.applicationInfo, providerInfo.name, providerInfo.flags) && processRecord.userId != 0) {
                        z2 = false;
                        boolean isInstantApp = providerInfo.applicationInfo.isInstantApp();
                        str2 = providerInfo.splitName;
                        if (str2 != null && !ArrayUtils.contains(providerInfo.applicationInfo.splitNames, str2)) {
                            z3 = false;
                        }
                        if (!z && z2 && (!isInstantApp || z3)) {
                            Log.v("ContentProviderHelper", "Installing " + providerInfo);
                            thread.scheduleInstallProvider(providerInfo);
                        } else {
                            Log.v("ContentProviderHelper", "Skipping " + providerInfo);
                        }
                    }
                    z2 = true;
                    boolean isInstantApp2 = providerInfo.applicationInfo.isInstantApp();
                    str2 = providerInfo.splitName;
                    if (str2 != null) {
                        z3 = false;
                    }
                    if (z) {
                    }
                    Log.v("ContentProviderHelper", "Skipping " + providerInfo);
                }
                z = true;
                if (this.mService.isSingleton(providerInfo.processName, providerInfo.applicationInfo, providerInfo.name, providerInfo.flags)) {
                    z2 = false;
                    boolean isInstantApp22 = providerInfo.applicationInfo.isInstantApp();
                    str2 = providerInfo.splitName;
                    if (str2 != null) {
                    }
                    if (z) {
                    }
                    Log.v("ContentProviderHelper", "Skipping " + providerInfo);
                }
                z2 = true;
                boolean isInstantApp222 = providerInfo.applicationInfo.isInstantApp();
                str2 = providerInfo.splitName;
                if (str2 != null) {
                }
                if (z) {
                }
                Log.v("ContentProviderHelper", "Skipping " + providerInfo);
            }
        } catch (RemoteException unused) {
        }
    }

    @GuardedBy({"mService"})
    public final ContentProviderConnection incProviderCountLocked(ProcessRecord processRecord, ContentProviderRecord contentProviderRecord, IBinder iBinder, int i, String str, String str2, boolean z, boolean z2, long j, ProcessList processList, int i2) {
        if (processRecord == null) {
            contentProviderRecord.addExternalProcessHandleLocked(iBinder, i, str2);
            return null;
        }
        ProcessProviderRecord processProviderRecord = processRecord.mProviders;
        int numberOfProviderConnections = processProviderRecord.numberOfProviderConnections();
        for (int i3 = 0; i3 < numberOfProviderConnections; i3++) {
            ContentProviderConnection providerConnectionAt = processProviderRecord.getProviderConnectionAt(i3);
            if (providerConnectionAt.provider == contentProviderRecord) {
                providerConnectionAt.incrementCount(z);
                return providerConnectionAt;
            }
        }
        ContentProviderConnection contentProviderConnection = new ContentProviderConnection(contentProviderRecord, processRecord, str, i2);
        contentProviderConnection.startAssociationIfNeeded();
        contentProviderConnection.initializeCount(z);
        contentProviderRecord.connections.add(contentProviderConnection);
        ProcessRecord processRecord2 = contentProviderRecord.proc;
        if (processRecord2 != null) {
            processRecord2.mProfile.addHostingComponentType(64);
        }
        processProviderRecord.addProviderConnection(contentProviderConnection);
        this.mService.startAssociationLocked(processRecord.uid, processRecord.processName, processRecord.mState.getCurProcState(), contentProviderRecord.uid, contentProviderRecord.appInfo.longVersionCode, contentProviderRecord.name, contentProviderRecord.info.processName);
        if (z2 && contentProviderRecord.proc != null && processRecord.mState.getSetAdj() <= 250) {
            checkTime(j, "getContentProviderImpl: before updateLruProcess");
            processList.updateLruProcessLocked(contentProviderRecord.proc, false, null);
            checkTime(j, "getContentProviderImpl: after updateLruProcess");
        }
        return contentProviderConnection;
    }

    @GuardedBy({"mService"})
    public final boolean decProviderCountLocked(final ContentProviderConnection contentProviderConnection, ContentProviderRecord contentProviderRecord, IBinder iBinder, final boolean z, boolean z2, final boolean z3) {
        if (contentProviderConnection == null) {
            contentProviderRecord.removeExternalProcessHandleLocked(iBinder);
            return false;
        } else if (contentProviderConnection.totalRefCount() > 1) {
            contentProviderConnection.decrementCount(z);
            return false;
        } else {
            if (z2) {
                BackgroundThread.getHandler().postDelayed(new Runnable() { // from class: com.android.server.am.ContentProviderHelper$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContentProviderHelper.this.lambda$decProviderCountLocked$3(contentProviderConnection, z, z3);
                    }
                }, 5000L);
            } else {
                lambda$decProviderCountLocked$3(contentProviderConnection, z, z3);
            }
            return true;
        }
    }

    @GuardedBy({"mService"})
    public final boolean hasProviderConnectionLocked(ProcessRecord processRecord) {
        for (int numberOfProviders = processRecord.mProviders.numberOfProviders() - 1; numberOfProviders >= 0; numberOfProviders--) {
            if (!processRecord.mProviders.getProviderAt(numberOfProviders).connections.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: handleProviderRemoval */
    public final void lambda$decProviderCountLocked$3(ContentProviderConnection contentProviderConnection, boolean z, boolean z2) {
        ProcessRecord processRecord;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (contentProviderConnection != null && contentProviderConnection.provider != null && contentProviderConnection.decrementCount(z) == 0) {
                    ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
                    contentProviderConnection.stopAssociation();
                    contentProviderRecord.connections.remove(contentProviderConnection);
                    ProcessRecord processRecord2 = contentProviderRecord.proc;
                    if (processRecord2 != null && !hasProviderConnectionLocked(processRecord2)) {
                        contentProviderRecord.proc.mProfile.clearHostingComponentType(64);
                    }
                    contentProviderConnection.client.mProviders.removeProviderConnection(contentProviderConnection);
                    if (contentProviderConnection.client.mState.getSetProcState() < 15 && (processRecord = contentProviderRecord.proc) != null) {
                        processRecord.mProviders.setLastProviderTime(SystemClock.uptimeMillis());
                    }
                    ActivityManagerService activityManagerService = this.mService;
                    ProcessRecord processRecord3 = contentProviderConnection.client;
                    activityManagerService.stopAssociationLocked(processRecord3.uid, processRecord3.processName, contentProviderRecord.uid, contentProviderRecord.appInfo.longVersionCode, contentProviderRecord.name, contentProviderRecord.info.processName);
                    if (z2) {
                        this.mService.updateOomAdjLocked(contentProviderConnection.provider.proc, 8);
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final String checkContentProviderPermission(ProviderInfo providerInfo, int i, int i2, int i3, boolean z, String str) {
        int i4;
        String str2;
        boolean z2;
        boolean z3 = false;
        if (z) {
            int unsafeConvertIncomingUser = this.mService.mUserController.unsafeConvertIncomingUser(i3);
            if (unsafeConvertIncomingUser == UserHandle.getUserId(i2)) {
                z2 = false;
            } else if (this.mService.mUgmInternal.checkAuthorityGrants(i2, providerInfo, unsafeConvertIncomingUser, z)) {
                return null;
            } else {
                z2 = true;
            }
            i4 = this.mService.mUserController.handleIncomingUser(i, i2, i3, false, 0, "checkContentProviderPermissionLocked " + providerInfo.authority, null);
            if (i4 == unsafeConvertIncomingUser) {
                z3 = z2;
            }
        } else {
            i4 = i3;
        }
        if (ActivityManagerService.checkComponentPermission(providerInfo.readPermission, i, i2, providerInfo.applicationInfo.uid, providerInfo.exported) == 0 || ActivityManagerService.checkComponentPermission(providerInfo.writePermission, i, i2, providerInfo.applicationInfo.uid, providerInfo.exported) == 0) {
            return null;
        }
        PathPermission[] pathPermissionArr = providerInfo.pathPermissions;
        if (pathPermissionArr != null) {
            int length = pathPermissionArr.length;
            while (length > 0) {
                length--;
                PathPermission pathPermission = pathPermissionArr[length];
                String readPermission = pathPermission.getReadPermission();
                if (readPermission != null && ActivityManagerService.checkComponentPermission(readPermission, i, i2, providerInfo.applicationInfo.uid, providerInfo.exported) == 0) {
                    return null;
                }
                String writePermission = pathPermission.getWritePermission();
                if (writePermission != null && ActivityManagerService.checkComponentPermission(writePermission, i, i2, providerInfo.applicationInfo.uid, providerInfo.exported) == 0) {
                    return null;
                }
            }
        }
        if (z3 || !this.mService.mUgmInternal.checkAuthorityGrants(i2, providerInfo, i4, z)) {
            if (!providerInfo.exported) {
                str2 = " that is not exported from UID " + providerInfo.applicationInfo.uid;
            } else if ("android.permission.MANAGE_DOCUMENTS".equals(providerInfo.readPermission)) {
                str2 = " requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs";
            } else {
                str2 = " requires " + providerInfo.readPermission + " or " + providerInfo.writePermission;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Permission Denial: opening provider ");
            sb.append(providerInfo.name);
            sb.append(" from ");
            sb.append(str != null ? str : "(null)");
            sb.append(" (pid=");
            sb.append(i);
            sb.append(", uid=");
            sb.append(i2);
            sb.append(")");
            sb.append(str2);
            String sb2 = sb.toString();
            Slog.w("ContentProviderHelper", sb2);
            return sb2;
        }
        return null;
    }

    public final String checkContentProviderAssociation(final ProcessRecord processRecord, int i, final ProviderInfo providerInfo) {
        if (processRecord == null) {
            if (this.mService.validateAssociationAllowedLocked(providerInfo.packageName, providerInfo.applicationInfo.uid, null, i)) {
                return null;
            }
            return "<null>";
        }
        return (String) processRecord.getPkgList().searchEachPackage(new Function() { // from class: com.android.server.am.ContentProviderHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$checkContentProviderAssociation$4;
                lambda$checkContentProviderAssociation$4 = ContentProviderHelper.this.lambda$checkContentProviderAssociation$4(processRecord, providerInfo, (String) obj);
                return lambda$checkContentProviderAssociation$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$checkContentProviderAssociation$4(ProcessRecord processRecord, ProviderInfo providerInfo, String str) {
        if (this.mService.validateAssociationAllowedLocked(str, processRecord.uid, providerInfo.packageName, providerInfo.applicationInfo.uid)) {
            return null;
        }
        return providerInfo.packageName;
    }

    public ProviderInfo getProviderInfoLocked(String str, int i, int i2) {
        ContentProviderRecord providerByName = this.mProviderMap.getProviderByName(str, i);
        if (providerByName != null) {
            return providerByName.info;
        }
        try {
            return AppGlobals.getPackageManager().resolveContentProvider(str, i2 | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES, i);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public final void maybeUpdateProviderUsageStatsLocked(ProcessRecord processRecord, String str, String str2) {
        UserState startedUserState;
        if (processRecord == null || processRecord.mState.getCurProcState() > 6 || (startedUserState = this.mService.mUserController.getStartedUserState(processRecord.userId)) == null) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Long l = startedUserState.mProviderLastReportedFg.get(str2);
        if (l == null || l.longValue() < elapsedRealtime - 60000) {
            if (this.mService.mSystemReady) {
                this.mService.mUsageStatsService.reportContentProviderUsage(str2, str, processRecord.userId);
            }
            startedUserState.mProviderLastReportedFg.put(str2, Long.valueOf(elapsedRealtime));
        }
    }

    public final boolean isProcessAliveLocked(ProcessRecord processRecord) {
        int pid = processRecord.getPid();
        if (pid <= 0) {
            return false;
        }
        String str = "/proc/" + pid + "/stat";
        long[] jArr = this.mProcessStateStatsLongs;
        jArr[0] = 0;
        if (Process.readProcFile(str, PROCESS_STATE_STATS_FORMAT, null, jArr, null)) {
            long j = this.mProcessStateStatsLongs[0];
            return (j == 90 || j == 88 || j == 120 || j == 75 || Process.getUidForPid(pid) != processRecord.uid) ? false : true;
        }
        return false;
    }

    /* renamed from: com.android.server.am.ContentProviderHelper$StartActivityRunnable */
    /* loaded from: classes.dex */
    public static final class StartActivityRunnable implements Runnable {
        public final Context mContext;
        public final Intent mIntent;
        public final UserHandle mUserHandle;

        public StartActivityRunnable(Context context, Intent intent, UserHandle userHandle) {
            this.mContext = context;
            this.mIntent = intent;
            this.mUserHandle = userHandle;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mContext.startActivityAsUser(this.mIntent, this.mUserHandle);
        }
    }

    public final boolean requestTargetProviderPermissionsReviewIfNeededLocked(ProviderInfo providerInfo, ProcessRecord processRecord, int i, Context context) {
        boolean z = true;
        if (this.mService.getPackageManagerInternal().isPermissionsReviewRequired(providerInfo.packageName, i)) {
            if (processRecord != null && processRecord.mState.getSetSchedGroup() == 0) {
                z = false;
            }
            if (!z) {
                Slog.w("ContentProviderHelper", "u" + i + " Instantiating a provider in package " + providerInfo.packageName + " requires a permissions review");
                return false;
            }
            Intent intent = new Intent("android.intent.action.REVIEW_PERMISSIONS");
            intent.addFlags(276824064);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", providerInfo.packageName);
            this.mService.mHandler.post(new StartActivityRunnable(context, intent, new UserHandle(i)));
            return false;
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0023  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean removeDyingProviderLocked(ProcessRecord processRecord, ContentProviderRecord contentProviderRecord, boolean z) {
        boolean z2;
        IContentProvider iContentProvider;
        boolean contains = this.mLaunchingProviders.contains(contentProviderRecord);
        if (contains && !z) {
            int i = contentProviderRecord.mRestartCount + 1;
            contentProviderRecord.mRestartCount = i;
            if (i > 3) {
                z2 = true;
                if (contains || z2) {
                    synchronized (contentProviderRecord) {
                        contentProviderRecord.launchingApp = null;
                        contentProviderRecord.notifyAll();
                        contentProviderRecord.onProviderPublishStatusLocked(false);
                        this.mService.mHandler.removeMessages(73, contentProviderRecord);
                    }
                    int userId = UserHandle.getUserId(contentProviderRecord.uid);
                    if (this.mProviderMap.getProviderByClass(contentProviderRecord.name, userId) == contentProviderRecord) {
                        this.mProviderMap.removeProviderByClass(contentProviderRecord.name, userId);
                    }
                    String[] split = contentProviderRecord.info.authority.split(";");
                    for (int i2 = 0; i2 < split.length; i2++) {
                        if (this.mProviderMap.getProviderByName(split[i2], userId) == contentProviderRecord) {
                            this.mProviderMap.removeProviderByName(split[i2], userId);
                        }
                    }
                }
                for (int size = contentProviderRecord.connections.size() - 1; size >= 0; size--) {
                    ContentProviderConnection contentProviderConnection = contentProviderRecord.connections.get(size);
                    if (!contentProviderConnection.waiting || !contains || z2) {
                        ProcessRecord processRecord2 = contentProviderConnection.client;
                        IApplicationThread thread = processRecord2.getThread();
                        contentProviderConnection.dead = true;
                        if (contentProviderConnection.stableCount() > 0) {
                            int pid = processRecord2.getPid();
                            if (!processRecord2.isPersistent() && thread != null && pid != 0 && pid != ActivityManagerService.MY_PID) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("depends on provider ");
                                sb.append(contentProviderRecord.name.flattenToShortString());
                                sb.append(" in dying proc ");
                                sb.append(processRecord != null ? processRecord.processName : "??");
                                sb.append(" (adj ");
                                sb.append(processRecord != null ? Integer.valueOf(processRecord.mState.getSetAdj()) : "??");
                                sb.append(")");
                                processRecord2.killLocked(sb.toString(), 12, 0, true);
                            }
                        } else if (thread != null && (iContentProvider = contentProviderConnection.provider.provider) != null) {
                            try {
                                thread.unstableProviderDied(iContentProvider.asBinder());
                            } catch (RemoteException unused) {
                            }
                            contentProviderRecord.connections.remove(size);
                            ProcessRecord processRecord3 = contentProviderRecord.proc;
                            if (processRecord3 != null && !hasProviderConnectionLocked(processRecord3)) {
                                contentProviderRecord.proc.mProfile.clearHostingComponentType(64);
                            }
                            if (contentProviderConnection.client.mProviders.removeProviderConnection(contentProviderConnection)) {
                                this.mService.stopAssociationLocked(processRecord2.uid, processRecord2.processName, contentProviderRecord.uid, contentProviderRecord.appInfo.longVersionCode, contentProviderRecord.name, contentProviderRecord.info.processName);
                            }
                        }
                    }
                }
                if (contains && z2) {
                    this.mLaunchingProviders.remove(contentProviderRecord);
                    contentProviderRecord.mRestartCount = 0;
                    return false;
                }
                return contains;
            }
        }
        z2 = z;
        if (contains) {
        }
        synchronized (contentProviderRecord) {
        }
    }

    public boolean checkAppInLaunchingProvidersLocked(ProcessRecord processRecord) {
        for (int size = this.mLaunchingProviders.size() - 1; size >= 0; size--) {
            if (this.mLaunchingProviders.get(size).launchingApp == processRecord) {
                return true;
            }
        }
        return false;
    }

    public boolean cleanupAppInLaunchingProvidersLocked(ProcessRecord processRecord, boolean z) {
        boolean z2 = false;
        for (int size = this.mLaunchingProviders.size() - 1; size >= 0; size--) {
            ContentProviderRecord contentProviderRecord = this.mLaunchingProviders.get(size);
            if (contentProviderRecord.launchingApp == processRecord) {
                int i = contentProviderRecord.mRestartCount + 1;
                contentProviderRecord.mRestartCount = i;
                if (i > 3) {
                    z = true;
                }
                if (z || processRecord.mErrorState.isBad() || !contentProviderRecord.hasConnectionOrHandle()) {
                    removeDyingProviderLocked(processRecord, contentProviderRecord, true);
                } else {
                    z2 = true;
                }
            }
        }
        return z2;
    }

    public final void checkTime(long j, String str) {
        long uptimeMillis = SystemClock.uptimeMillis() - j;
        if (uptimeMillis > 50) {
            Slog.w("ContentProviderHelper", "Slow operation: " + uptimeMillis + "ms so far, now at " + str);
        }
    }

    public void dumpProvidersLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        new ActivityManagerService.ItemMatcher().build(strArr, i);
        printWriter.println("ACTIVITY MANAGER CONTENT PROVIDERS (dumpsys activity providers)");
        boolean dumpProvidersLocked = this.mProviderMap.dumpProvidersLocked(printWriter, z, str);
        if (this.mLaunchingProviders.size() > 0) {
            boolean z2 = false;
            boolean z3 = dumpProvidersLocked;
            for (int size = this.mLaunchingProviders.size() - 1; size >= 0; size--) {
                ContentProviderRecord contentProviderRecord = this.mLaunchingProviders.get(size);
                if (str == null || str.equals(contentProviderRecord.name.getPackageName())) {
                    if (!z2) {
                        if (z3) {
                            printWriter.println();
                        }
                        printWriter.println("  Launching content providers:");
                        dumpProvidersLocked = true;
                        z3 = true;
                        z2 = true;
                    }
                    printWriter.print("  Launching #");
                    printWriter.print(size);
                    printWriter.print(": ");
                    printWriter.println(contentProviderRecord);
                }
            }
        }
        if (dumpProvidersLocked) {
            return;
        }
        printWriter.println("  (nothing)");
    }

    public final void enforceContentProviderRestrictionsForSdkSandbox(ProviderInfo providerInfo) {
        if (Process.isSdkSandboxUid(Binder.getCallingUid())) {
            SdkSandboxManagerLocal sdkSandboxManagerLocal = (SdkSandboxManagerLocal) LocalManagerRegistry.getManager(SdkSandboxManagerLocal.class);
            if (sdkSandboxManagerLocal == null) {
                throw new IllegalStateException("SdkSandboxManagerLocal not found when checking whether SDK sandbox uid may access the contentprovider.");
            }
            if (sdkSandboxManagerLocal.canAccessContentProviderFromSdkSandbox(providerInfo)) {
                return;
            }
            throw new SecurityException("SDK sandbox uid may not access contentprovider " + providerInfo.name);
        }
    }

    public boolean dumpProvider(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr, int i, boolean z) {
        return this.mProviderMap.dumpProvider(fileDescriptor, printWriter, str, strArr, i, z);
    }

    public boolean dumpProviderProto(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, String[] strArr) {
        return this.mProviderMap.dumpProviderProto(fileDescriptor, printWriter, str, strArr);
    }
}
