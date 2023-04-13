package com.android.commands.p000am;

import android.app.IActivityManager;
import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.app.UiAutomationConnection;
import android.content.ComponentName;
import android.content.pm.IPackageManager;
import android.content.pm.InstrumentationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ServiceManager;
import android.util.AndroidException;
import android.util.proto.ProtoOutputStream;
import android.view.IWindowManager;
import com.android.commands.p000am.InstrumentationData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/* renamed from: com.android.commands.am.Instrument */
/* loaded from: classes.dex */
public class Instrument {
    public static final String DEFAULT_LOG_DIR = "instrument-logs";
    private static final int STATUS_TEST_FAILED_ASSERTION = -1;
    private static final int STATUS_TEST_FAILED_OTHER = -2;
    private static final int STATUS_TEST_PASSED = 0;
    private static final int STATUS_TEST_STARTED = 1;
    private static final String TAG = "am";
    public String componentNameArg;
    private final IActivityManager mAm;
    private final IPackageManager mPm;
    public String profileFile = null;
    public boolean wait = false;
    public boolean rawMode = false;
    boolean protoStd = false;
    boolean protoFile = false;
    String logPath = null;
    public boolean noWindowAnimation = false;
    public boolean disableHiddenApiChecks = false;
    public boolean disableTestApiChecks = true;
    public boolean disableIsolatedStorage = false;
    public String abi = null;
    public boolean noRestart = false;
    public int userId = STATUS_TEST_FAILED_OTHER;
    public Bundle args = new Bundle();
    public boolean alwaysCheckSignature = false;
    public boolean instrumentSdkSandbox = false;
    private final IWindowManager mWm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

    /* renamed from: com.android.commands.am.Instrument$StatusReporter */
    /* loaded from: classes.dex */
    private interface StatusReporter {
        void onError(String str, boolean z);

        void onInstrumentationFinishedLocked(ComponentName componentName, int i, Bundle bundle);

        void onInstrumentationStatusLocked(ComponentName componentName, int i, Bundle bundle);
    }

    public Instrument(IActivityManager am, IPackageManager pm) {
        this.mAm = am;
        this.mPm = pm;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Collection<String> sorted(Collection<String> list) {
        ArrayList<String> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    /* renamed from: com.android.commands.am.Instrument$TextStatusReporter */
    /* loaded from: classes.dex */
    private class TextStatusReporter implements StatusReporter {
        private boolean mRawMode;

        public TextStatusReporter(boolean rawMode) {
            this.mRawMode = rawMode;
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onInstrumentationStatusLocked(ComponentName name, int resultCode, Bundle results) {
            String pretty = null;
            if (!this.mRawMode && results != null) {
                pretty = results.getString("stream");
            }
            if (pretty != null) {
                System.out.print(pretty);
                return;
            }
            if (results != null) {
                for (String key : Instrument.sorted(results.keySet())) {
                    System.out.println("INSTRUMENTATION_STATUS: " + key + "=" + results.get(key));
                }
            }
            System.out.println("INSTRUMENTATION_STATUS_CODE: " + resultCode);
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onInstrumentationFinishedLocked(ComponentName name, int resultCode, Bundle results) {
            String pretty = null;
            if (!this.mRawMode && results != null) {
                pretty = results.getString("stream");
            }
            if (pretty != null) {
                System.out.println(pretty);
                return;
            }
            if (results != null) {
                for (String key : Instrument.sorted(results.keySet())) {
                    System.out.println("INSTRUMENTATION_RESULT: " + key + "=" + results.get(key));
                }
            }
            System.out.println("INSTRUMENTATION_CODE: " + resultCode);
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onError(String errorText, boolean commandError) {
            if (this.mRawMode) {
                System.out.println("onError: commandError=" + commandError + " message=" + errorText);
            }
            if (!commandError) {
                System.out.println(errorText);
            }
        }
    }

    /* renamed from: com.android.commands.am.Instrument$ProtoStatusReporter */
    /* loaded from: classes.dex */
    private class ProtoStatusReporter implements StatusReporter {
        private File mLog;
        private long mTestStartMs;

        ProtoStatusReporter() {
            if (Instrument.this.protoFile) {
                if (Instrument.this.logPath == null) {
                    File logDir = new File(Environment.getLegacyExternalStorageDirectory(), Instrument.DEFAULT_LOG_DIR);
                    if (!logDir.exists() && !logDir.mkdirs()) {
                        System.err.format("Unable to create log directory: %s\n", logDir.getAbsolutePath());
                        Instrument.this.protoFile = false;
                        return;
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss-SSS", Locale.US);
                    String fileName = String.format("log-%s.instrumentation_data_proto", format.format(new Date()));
                    this.mLog = new File(logDir, fileName);
                } else {
                    File file = new File(Environment.getLegacyExternalStorageDirectory(), Instrument.this.logPath);
                    this.mLog = file;
                    File logDir2 = file.getParentFile();
                    if (!logDir2.exists() && !logDir2.mkdirs()) {
                        System.err.format("Unable to create log directory: %s\n", logDir2.getAbsolutePath());
                        Instrument.this.protoFile = false;
                        return;
                    }
                }
                if (this.mLog.exists()) {
                    this.mLog.delete();
                }
            }
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onInstrumentationStatusLocked(ComponentName name, int resultCode, Bundle results) {
            ProtoOutputStream proto = new ProtoOutputStream();
            long testStatusToken = proto.start(2246267895809L);
            proto.write(1172526071811L, resultCode);
            writeBundle(proto, 1146756268036L, results);
            if (resultCode == 1) {
                this.mTestStartMs = System.currentTimeMillis();
            } else {
                long j = this.mTestStartMs;
                if (j > 0) {
                    proto.write(InstrumentationData.TestStatus.LOGCAT, Instrument.readLogcat(j));
                }
                this.mTestStartMs = 0L;
            }
            proto.end(testStatusToken);
            outputProto(proto);
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onInstrumentationFinishedLocked(ComponentName name, int resultCode, Bundle results) {
            ProtoOutputStream proto = new ProtoOutputStream();
            long sessionStatusToken = proto.start(InstrumentationData.Session.SESSION_STATUS);
            proto.write(InstrumentationData.SessionStatus.STATUS_CODE, Instrument.STATUS_TEST_PASSED);
            proto.write(1172526071811L, resultCode);
            writeBundle(proto, 1146756268036L, results);
            proto.end(sessionStatusToken);
            outputProto(proto);
        }

        @Override // com.android.commands.p000am.Instrument.StatusReporter
        public void onError(String errorText, boolean commandError) {
            ProtoOutputStream proto = new ProtoOutputStream();
            long sessionStatusToken = proto.start(InstrumentationData.Session.SESSION_STATUS);
            proto.write(InstrumentationData.SessionStatus.STATUS_CODE, 1);
            proto.write(1138166333442L, errorText);
            proto.end(sessionStatusToken);
            outputProto(proto);
        }

        private void writeBundle(ProtoOutputStream proto, long fieldId, Bundle bundle) {
            long bundleToken = proto.start(fieldId);
            for (String key : Instrument.sorted(bundle.keySet())) {
                long entryToken = proto.startRepeatedObject(2246267895809L);
                proto.write(InstrumentationData.ResultsBundleEntry.KEY, key);
                Object val = bundle.get(key);
                if (val instanceof String) {
                    proto.write(1138166333442L, (String) val);
                } else if (val instanceof Byte) {
                    proto.write(1172526071811L, ((Byte) val).intValue());
                } else if (val instanceof Double) {
                    proto.write(InstrumentationData.ResultsBundleEntry.VALUE_DOUBLE, ((Double) val).doubleValue());
                } else if (val instanceof Float) {
                    proto.write(InstrumentationData.ResultsBundleEntry.VALUE_FLOAT, ((Float) val).floatValue());
                } else if (val instanceof Integer) {
                    proto.write(1172526071811L, ((Integer) val).intValue());
                } else if (val instanceof Long) {
                    proto.write(InstrumentationData.ResultsBundleEntry.VALUE_LONG, ((Long) val).longValue());
                } else if (val instanceof Short) {
                    proto.write(1172526071811L, (int) ((Short) val).shortValue());
                } else if (val instanceof Bundle) {
                    writeBundle(proto, InstrumentationData.ResultsBundleEntry.VALUE_BUNDLE, (Bundle) val);
                } else if (val instanceof byte[]) {
                    proto.write(InstrumentationData.ResultsBundleEntry.VALUE_BYTES, (byte[]) val);
                }
                proto.end(entryToken);
            }
            proto.end(bundleToken);
        }

        private void outputProto(ProtoOutputStream proto) {
            byte[] out = proto.getBytes();
            if (Instrument.this.protoStd) {
                try {
                    System.out.write(out);
                    System.out.flush();
                } catch (IOException ex) {
                    System.err.println("Error writing finished response: ");
                    ex.printStackTrace(System.err);
                }
            }
            if (Instrument.this.protoFile) {
                try {
                    OutputStream os = new FileOutputStream(this.mLog, true);
                    os.write(proto.getBytes());
                    os.flush();
                    os.close();
                } catch (IOException ex2) {
                    System.err.format("Cannot write to %s:\n", this.mLog.getAbsolutePath());
                    ex2.printStackTrace();
                }
            }
        }
    }

    /* renamed from: com.android.commands.am.Instrument$InstrumentationWatcher */
    /* loaded from: classes.dex */
    private class InstrumentationWatcher extends IInstrumentationWatcher.Stub {
        private boolean mFinished = false;
        private final StatusReporter mReporter;

        public InstrumentationWatcher(StatusReporter reporter) {
            this.mReporter = reporter;
        }

        public void instrumentationStatus(ComponentName name, int resultCode, Bundle results) {
            synchronized (this) {
                this.mReporter.onInstrumentationStatusLocked(name, resultCode, results);
                notifyAll();
            }
        }

        public void instrumentationFinished(ComponentName name, int resultCode, Bundle results) {
            synchronized (this) {
                this.mReporter.onInstrumentationFinishedLocked(name, resultCode, results);
                this.mFinished = true;
                notifyAll();
            }
        }

        public boolean waitForFinish() {
            synchronized (this) {
                while (!this.mFinished) {
                    try {
                        if (!Instrument.this.mAm.asBinder().pingBinder()) {
                            return false;
                        }
                        wait(1000L);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return true;
            }
        }
    }

    private ComponentName parseComponentName(String cnArg) throws Exception {
        if (cnArg.contains("/")) {
            ComponentName cn = ComponentName.unflattenFromString(cnArg);
            if (cn == null) {
                throw new IllegalArgumentException("Bad component name: " + cnArg);
            }
            return cn;
        }
        List<InstrumentationInfo> infos = this.mPm.queryInstrumentationAsUser((String) null, (int) STATUS_TEST_PASSED, this.userId).getList();
        int numInfos = infos == null ? STATUS_TEST_PASSED : infos.size();
        ArrayList<ComponentName> cns = new ArrayList<>();
        for (int i = STATUS_TEST_PASSED; i < numInfos; i++) {
            InstrumentationInfo info = infos.get(i);
            ComponentName c = new ComponentName(info.packageName, info.name);
            if (cnArg.equals(info.packageName)) {
                cns.add(c);
            }
        }
        int i2 = cns.size();
        if (i2 == 0) {
            throw new IllegalArgumentException("No instrumentation found for: " + cnArg);
        }
        if (cns.size() == 1) {
            return cns.get(STATUS_TEST_PASSED);
        }
        StringBuilder cnsStr = new StringBuilder();
        int numCns = cns.size();
        for (int i3 = STATUS_TEST_PASSED; i3 < numCns; i3++) {
            cnsStr.append(cns.get(i3).flattenToString());
            cnsStr.append(", ");
        }
        int i4 = cnsStr.length();
        cnsStr.setLength(i4 + STATUS_TEST_FAILED_OTHER);
        throw new IllegalArgumentException("Found multiple instrumentations: " + cnsStr.toString());
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0025 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0036 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0058 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0091 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0097 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x009d A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00a3 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00a9 A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00af A[Catch: all -> 0x0107, Exception -> 0x0109, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00b4  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00ea A[Catch: all -> 0x0107, Exception -> 0x0109, TRY_ENTER, TryCatch #0 {Exception -> 0x0109, blocks: (B:3:0x0005, B:5:0x0009, B:8:0x000e, B:10:0x0012, B:14:0x0025, B:15:0x0031, B:17:0x0036, B:18:0x004e, B:20:0x0058, B:22:0x005f, B:25:0x006b, B:28:0x0071, B:29:0x008b, B:30:0x008c, B:32:0x0091, B:33:0x0093, B:35:0x0097, B:36:0x0099, B:38:0x009d, B:39:0x009f, B:41:0x00a3, B:42:0x00a5, B:44:0x00a9, B:45:0x00ab, B:47:0x00af, B:49:0x00b6, B:52:0x00cc, B:54:0x00d2, B:62:0x00ea, B:63:0x0106, B:11:0x001b), top: B:74:0x0005, outer: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void run() throws Exception {
        InstrumentationWatcher watcher;
        IUiAutomationConnection iUiAutomationConnection;
        ComponentName cn;
        int flags;
        StatusReporter reporter = null;
        float[] oldAnims = null;
        try {
            try {
                if (!this.protoFile && !this.protoStd) {
                    if (this.wait) {
                        reporter = new TextStatusReporter(this.rawMode);
                    }
                    watcher = null;
                    iUiAutomationConnection = null;
                    if (reporter != null) {
                        watcher = new InstrumentationWatcher(reporter);
                        iUiAutomationConnection = new UiAutomationConnection();
                    }
                    if (this.noWindowAnimation) {
                        oldAnims = this.mWm.getAnimationScales();
                        this.mWm.setAnimationScale((int) STATUS_TEST_PASSED, 0.0f);
                        this.mWm.setAnimationScale(1, 0.0f);
                        this.mWm.setAnimationScale(2, 0.0f);
                    }
                    cn = parseComponentName(this.componentNameArg);
                    if (this.abi != null) {
                        String[] supportedAbis = Build.SUPPORTED_ABIS;
                        boolean matched = false;
                        int length = supportedAbis.length;
                        int i = STATUS_TEST_PASSED;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            String supportedAbi = supportedAbis[i];
                            if (supportedAbi.equals(this.abi)) {
                                matched = true;
                                break;
                            }
                            i++;
                        }
                        if (!matched) {
                            throw new AndroidException("INSTRUMENTATION_FAILED: Unsupported instruction set " + this.abi);
                        }
                    }
                    flags = STATUS_TEST_PASSED;
                    if (this.disableHiddenApiChecks) {
                        flags = STATUS_TEST_PASSED | 1;
                    }
                    if (this.disableTestApiChecks) {
                        flags |= 4;
                    }
                    if (this.disableIsolatedStorage) {
                        flags |= 2;
                    }
                    if (this.noRestart) {
                        flags |= 8;
                    }
                    if (this.alwaysCheckSignature) {
                        flags |= 16;
                    }
                    if (this.mAm.startInstrumentation(cn, this.profileFile, !this.instrumentSdkSandbox ? flags | 32 : flags, this.args, watcher, iUiAutomationConnection, this.userId, this.abi)) {
                        throw new AndroidException("INSTRUMENTATION_FAILED: " + cn.flattenToString());
                    }
                    if (watcher != null && !watcher.waitForFinish()) {
                        reporter.onError("INSTRUMENTATION_ABORTED: System has crashed.", false);
                        if (oldAnims != null) {
                            return;
                        }
                        return;
                    }
                    if (oldAnims != null) {
                        this.mWm.setAnimationScales(oldAnims);
                    }
                    System.exit(STATUS_TEST_PASSED);
                    return;
                }
                reporter = new ProtoStatusReporter();
                watcher = null;
                iUiAutomationConnection = null;
                if (reporter != null) {
                }
                if (this.noWindowAnimation) {
                }
                cn = parseComponentName(this.componentNameArg);
                if (this.abi != null) {
                }
                flags = STATUS_TEST_PASSED;
                if (this.disableHiddenApiChecks) {
                }
                if (this.disableTestApiChecks) {
                }
                if (this.disableIsolatedStorage) {
                }
                if (this.noRestart) {
                }
                if (this.alwaysCheckSignature) {
                }
                if (this.mAm.startInstrumentation(cn, this.profileFile, !this.instrumentSdkSandbox ? flags | 32 : flags, this.args, watcher, iUiAutomationConnection, this.userId, this.abi)) {
                }
            } catch (Exception ex) {
                if (STATUS_TEST_PASSED != 0) {
                    reporter.onError(ex.getMessage(), true);
                }
                throw ex;
            }
        } finally {
            if (STATUS_TEST_PASSED != 0) {
                this.mWm.setAnimationScales((float[]) null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String readLogcat(long startTimeMs) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = format.format(new Date(startTimeMs));
            Process process = new ProcessBuilder(new String[STATUS_TEST_PASSED]).command("logcat", "-d", "-v", "threadtime,uid", "-T", timestamp).start();
            process.getOutputStream().close();
            StringBuilder str = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            char[] buffer = new char[4096];
            while (true) {
                int amt = reader.read(buffer, STATUS_TEST_PASSED, buffer.length);
                if (amt < 0) {
                    try {
                        break;
                    } catch (InterruptedException e) {
                    }
                } else if (amt > 0) {
                    str.append(buffer, STATUS_TEST_PASSED, amt);
                }
            }
            process.waitFor();
            return str.toString();
        } catch (IOException ex) {
            return "Error reading logcat command:\n" + ex.toString();
        }
    }
}
