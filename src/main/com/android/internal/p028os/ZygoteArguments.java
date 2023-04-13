package com.android.internal.p028os;

import java.io.EOFException;
import java.util.ArrayList;
/* renamed from: com.android.internal.os.ZygoteArguments */
/* loaded from: classes4.dex */
class ZygoteArguments {
    boolean mAbiListQuery;
    String[] mAllowlistedDataInfoList;
    String[] mApiDenylistExemptions;
    String mAppDataDir;
    boolean mBindMountAppDataDirs;
    boolean mBindMountAppStorageDirs;
    boolean mBootCompleted;
    private boolean mCapabilitiesSpecified;
    long mEffectiveCapabilities;
    boolean mGidSpecified;
    int[] mGids;
    String mInstructionSet;
    String mInvokeWith;
    boolean mIsTopApp;
    String mNiceName;
    String mPackageName;
    long mPermittedCapabilities;
    boolean mPidQuery;
    String[] mPkgDataInfoList;
    String mPreloadApp;
    boolean mPreloadDefault;
    String mPreloadPackage;
    String mPreloadPackageCacheKey;
    String mPreloadPackageLibFileName;
    String mPreloadPackageLibs;
    ArrayList<int[]> mRLimits;
    String[] mRemainingArgs;
    int mRuntimeFlags;
    String mSeInfo;
    private boolean mSeInfoSpecified;
    boolean mStartChildZygote;
    int mTargetSdkVersion;
    private boolean mTargetSdkVersionSpecified;
    boolean mUidSpecified;
    boolean mUsapPoolEnabled;
    int mUid = 0;
    int mGid = 0;
    int mMountExternal = 0;
    boolean mUsapPoolStatusSpecified = false;
    int mHiddenApiAccessLogSampleRate = -1;
    int mHiddenApiAccessStatslogSampleRate = -1;
    long[] mDisabledCompatChanges = null;

    private ZygoteArguments(ZygoteCommandBuffer args, int argCount) throws IllegalArgumentException, EOFException {
        parseArgs(args, argCount);
    }

    public static ZygoteArguments getInstance(ZygoteCommandBuffer args) throws IllegalArgumentException, EOFException {
        int argCount = args.getCount();
        if (argCount == 0) {
            return null;
        }
        return new ZygoteArguments(args, argCount);
    }

    /* JADX WARN: Code restructure failed: missing block: B:195:0x0395, code lost:
        if (r12.mBootCompleted == false) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x0397, code lost:
        if (r14 > r1) goto L248;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x03a2, code lost:
        throw new java.lang.IllegalArgumentException("Unexpected arguments after --boot-completed");
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x03a5, code lost:
        if (r12.mAbiListQuery != false) goto L295;
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x03a9, code lost:
        if (r12.mPidQuery == false) goto L269;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x03ae, code lost:
        if (r12.mPreloadPackage == null) goto L274;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x03b0, code lost:
        if (r14 > r1) goto L272;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x03ba, code lost:
        throw new java.lang.IllegalArgumentException("Unexpected arguments after --preload-package.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:212:0x03bd, code lost:
        if (r12.mPreloadApp == null) goto L279;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x03bf, code lost:
        if (r14 > r1) goto L277;
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x03c9, code lost:
        throw new java.lang.IllegalArgumentException("Unexpected arguments after --preload-app.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x03ca, code lost:
        if (r3 == false) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x03cc, code lost:
        if (r2 != false) goto L287;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x03ce, code lost:
        r5 = new java.lang.StringBuilder().append("Unexpected argument : ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:220:0x03db, code lost:
        if (r0 != null) goto L286;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x03dd, code lost:
        r6 = r13.nextArg();
     */
    /* JADX WARN: Code restructure failed: missing block: B:222:0x03e2, code lost:
        r6 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x03ee, code lost:
        throw new java.lang.IllegalArgumentException(r5.append(r6).toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x03ef, code lost:
        r5 = new java.lang.String[r14 - r1];
        r12.mRemainingArgs = r5;
        r6 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:226:0x03f6, code lost:
        if (r0 == null) goto L290;
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x03f8, code lost:
        r5[0] = r0;
        r6 = 0 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x03fe, code lost:
        if (r6 >= (r14 - r1)) goto L294;
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x0400, code lost:
        r12.mRemainingArgs[r6] = r13.nextArg();
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:231:0x040b, code lost:
        if (r14 > r1) goto L296;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x040f, code lost:
        if (r12.mStartChildZygote == false) goto L264;
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x0411, code lost:
        r5 = false;
        r6 = r12.mRemainingArgs;
        r7 = r6.length;
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x0415, code lost:
        if (r4 >= r7) goto L263;
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x041f, code lost:
        if (r6[r4].startsWith(com.android.internal.p028os.Zygote.CHILD_ZYGOTE_SOCKET_NAME_ARG) == false) goto L256;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x0421, code lost:
        r5 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x0423, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:240:0x0426, code lost:
        if (r5 == false) goto L260;
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x0430, code lost:
        throw new java.lang.IllegalArgumentException("--start-child-zygote specified without --zygote-socket=");
     */
    /* JADX WARN: Code restructure failed: missing block: B:244:0x0431, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:246:0x0439, code lost:
        throw new java.lang.IllegalArgumentException("Unexpected arguments after --query-abi-list.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:309:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void parseArgs(ZygoteCommandBuffer args, int argCount) throws IllegalArgumentException, EOFException {
        String unprocessedArg = null;
        int curArg = 0;
        boolean seenRuntimeArgs = false;
        boolean expectRuntimeArgs = true;
        while (true) {
            int i = 0;
            if (curArg >= argCount) {
                break;
            }
            String arg = args.nextArg();
            if (arg.equals("--")) {
                curArg++;
                break;
            }
            if (arg.startsWith("--setuid=")) {
                if (this.mUidSpecified) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mUidSpecified = true;
                this.mUid = Integer.parseInt(getAssignmentValue(arg));
            } else if (arg.startsWith("--setgid=")) {
                if (this.mGidSpecified) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mGidSpecified = true;
                this.mGid = Integer.parseInt(getAssignmentValue(arg));
            } else if (arg.startsWith("--target-sdk-version=")) {
                if (this.mTargetSdkVersionSpecified) {
                    throw new IllegalArgumentException("Duplicate target-sdk-version specified");
                }
                this.mTargetSdkVersionSpecified = true;
                this.mTargetSdkVersion = Integer.parseInt(getAssignmentValue(arg));
            } else if (arg.equals("--runtime-args")) {
                seenRuntimeArgs = true;
            } else if (arg.startsWith("--runtime-flags=")) {
                this.mRuntimeFlags = Integer.parseInt(getAssignmentValue(arg));
            } else if (arg.startsWith("--seinfo=")) {
                if (this.mSeInfoSpecified) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mSeInfoSpecified = true;
                this.mSeInfo = getAssignmentValue(arg);
            } else if (arg.startsWith("--capabilities=")) {
                if (this.mCapabilitiesSpecified) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mCapabilitiesSpecified = true;
                String capString = getAssignmentValue(arg);
                String[] capStrings = capString.split(",", 2);
                if (capStrings.length == 1) {
                    long longValue = Long.decode(capStrings[0]).longValue();
                    this.mEffectiveCapabilities = longValue;
                    this.mPermittedCapabilities = longValue;
                } else {
                    this.mPermittedCapabilities = Long.decode(capStrings[0]).longValue();
                    this.mEffectiveCapabilities = Long.decode(capStrings[1]).longValue();
                }
            } else if (arg.startsWith("--rlimit=")) {
                String[] limitStrings = getAssignmentList(arg);
                if (limitStrings.length != 3) {
                    throw new IllegalArgumentException("--rlimit= should have 3 comma-delimited ints");
                }
                int[] rlimitTuple = new int[limitStrings.length];
                for (int i2 = 0; i2 < limitStrings.length; i2++) {
                    rlimitTuple[i2] = Integer.parseInt(limitStrings[i2]);
                }
                if (this.mRLimits == null) {
                    this.mRLimits = new ArrayList<>();
                }
                this.mRLimits.add(rlimitTuple);
            } else if (arg.startsWith("--setgroups=")) {
                if (this.mGids != null) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                String[] params = getAssignmentList(arg);
                this.mGids = new int[params.length];
                for (int i3 = params.length - 1; i3 >= 0; i3--) {
                    this.mGids[i3] = Integer.parseInt(params[i3]);
                }
            } else if (arg.equals("--invoke-with")) {
                if (this.mInvokeWith != null) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                curArg++;
                try {
                    this.mInvokeWith = args.nextArg();
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("--invoke-with requires argument");
                }
            } else if (arg.startsWith("--nice-name=")) {
                if (this.mNiceName != null) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mNiceName = getAssignmentValue(arg);
            } else if (arg.equals("--mount-external-default")) {
                this.mMountExternal = 1;
            } else if (arg.equals("--mount-external-installer")) {
                this.mMountExternal = 2;
            } else if (arg.equals("--mount-external-pass-through")) {
                this.mMountExternal = 3;
            } else if (arg.equals("--mount-external-android-writable")) {
                this.mMountExternal = 4;
            } else if (arg.equals("--query-abi-list")) {
                this.mAbiListQuery = true;
            } else if (arg.equals("--get-pid")) {
                this.mPidQuery = true;
            } else if (arg.equals("--boot-completed")) {
                this.mBootCompleted = true;
            } else if (arg.startsWith("--instruction-set=")) {
                this.mInstructionSet = getAssignmentValue(arg);
            } else if (arg.startsWith("--app-data-dir=")) {
                this.mAppDataDir = getAssignmentValue(arg);
            } else if (arg.equals("--preload-app")) {
                curArg++;
                this.mPreloadApp = args.nextArg();
            } else if (arg.equals("--preload-package")) {
                curArg += 4;
                this.mPreloadPackage = args.nextArg();
                this.mPreloadPackageLibs = args.nextArg();
                this.mPreloadPackageLibFileName = args.nextArg();
                this.mPreloadPackageCacheKey = args.nextArg();
            } else if (arg.equals("--preload-default")) {
                this.mPreloadDefault = true;
                expectRuntimeArgs = false;
            } else if (arg.equals("--start-child-zygote")) {
                this.mStartChildZygote = true;
            } else if (arg.equals("--set-api-denylist-exemptions")) {
                this.mApiDenylistExemptions = new String[(argCount - curArg) - 1];
                curArg++;
                int i4 = 0;
                while (curArg < argCount) {
                    this.mApiDenylistExemptions[i4] = args.nextArg();
                    curArg++;
                    i4++;
                }
                expectRuntimeArgs = false;
            } else if (arg.startsWith("--hidden-api-log-sampling-rate=")) {
                String rateStr = getAssignmentValue(arg);
                try {
                    this.mHiddenApiAccessLogSampleRate = Integer.parseInt(rateStr);
                    expectRuntimeArgs = false;
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid log sampling rate: " + rateStr, nfe);
                }
            } else if (arg.startsWith("--hidden-api-statslog-sampling-rate=")) {
                String rateStr2 = getAssignmentValue(arg);
                try {
                    this.mHiddenApiAccessStatslogSampleRate = Integer.parseInt(rateStr2);
                    expectRuntimeArgs = false;
                } catch (NumberFormatException nfe2) {
                    throw new IllegalArgumentException("Invalid statslog sampling rate: " + rateStr2, nfe2);
                }
            } else if (arg.startsWith("--package-name=")) {
                if (this.mPackageName != null) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                this.mPackageName = getAssignmentValue(arg);
            } else if (arg.startsWith("--usap-pool-enabled=")) {
                this.mUsapPoolStatusSpecified = true;
                this.mUsapPoolEnabled = Boolean.parseBoolean(getAssignmentValue(arg));
                expectRuntimeArgs = false;
            } else if (arg.startsWith(Zygote.START_AS_TOP_APP_ARG)) {
                this.mIsTopApp = true;
            } else if (arg.startsWith("--disabled-compat-changes=")) {
                if (this.mDisabledCompatChanges != null) {
                    throw new IllegalArgumentException("Duplicate arg specified");
                }
                String[] params2 = getAssignmentList(arg);
                int length = params2.length;
                this.mDisabledCompatChanges = new long[length];
                for (int i5 = 0; i5 < length; i5++) {
                    this.mDisabledCompatChanges[i5] = Long.parseLong(params2[i5]);
                }
            } else if (arg.startsWith(Zygote.PKG_DATA_INFO_MAP)) {
                this.mPkgDataInfoList = getAssignmentList(arg);
            } else if (arg.startsWith(Zygote.ALLOWLISTED_DATA_INFO_MAP)) {
                this.mAllowlistedDataInfoList = getAssignmentList(arg);
            } else if (arg.equals(Zygote.BIND_MOUNT_APP_STORAGE_DIRS)) {
                this.mBindMountAppStorageDirs = true;
            } else if (arg.equals(Zygote.BIND_MOUNT_APP_DATA_DIRS)) {
                this.mBindMountAppDataDirs = true;
            } else {
                unprocessedArg = arg;
                break;
            }
            curArg++;
        }
    }

    private static String getAssignmentValue(String arg) {
        return arg.substring(arg.indexOf(61) + 1);
    }

    private static String[] getAssignmentList(String arg) {
        return getAssignmentValue(arg).split(",");
    }
}
