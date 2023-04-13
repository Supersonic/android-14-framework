package android.p008os;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p001pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.media.audio.Enums;
import android.p008os.IRecoverySystemProgressListener;
import android.p008os.IVold;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import libcore.io.Streams;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
/* renamed from: android.os.RecoverySystem */
/* loaded from: classes3.dex */
public class RecoverySystem {
    private static final String ACTION_EUICC_FACTORY_RESET = "com.android.internal.action.EUICC_FACTORY_RESET";
    private static final String ACTION_EUICC_REMOVE_INVISIBLE_SUBSCRIPTIONS = "com.android.internal.action.EUICC_REMOVE_INVISIBLE_SUBSCRIPTIONS";
    public static final File BLOCK_MAP_FILE;
    private static final long DEFAULT_EUICC_FACTORY_RESET_TIMEOUT_MILLIS = 30000;
    private static final long DEFAULT_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS = 45000;
    private static final File DEFAULT_KEYSTORE = new File("/system/etc/security/otacerts.zip");
    private static final String LAST_INSTALL_PATH = "last_install";
    private static final String LAST_PREFIX = "last_";
    private static final File LOG_FILE;
    private static final int LOG_FILE_MAX_LENGTH = 65536;
    private static final long MAX_EUICC_FACTORY_RESET_TIMEOUT_MILLIS = 60000;
    private static final long MAX_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS = 90000;
    private static final long MIN_EUICC_FACTORY_RESET_TIMEOUT_MILLIS = 5000;
    private static final long MIN_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS = 15000;
    private static final String PACKAGE_NAME_EUICC_DATA_MANAGEMENT_CALLBACK = "android";
    private static final long PUBLISH_PROGRESS_INTERVAL_MS = 500;
    private static final File RECOVERY_DIR;
    @SystemApi
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_INVALID_PACKAGE_NAME = 2000;
    @SystemApi
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_LSKF_NOT_CAPTURED = 3000;
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_NONE = 0;
    @SystemApi
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_PROVIDER_PREPARATION_FAILURE = 5000;
    @SystemApi
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_SLOT_MISMATCH = 4000;
    @SystemApi
    public static final int RESUME_ON_REBOOT_REBOOT_ERROR_UNSPECIFIED = 1000;
    private static final String TAG = "RecoverySystem";
    public static final File UNCRYPT_PACKAGE_FILE;
    public static final File UNCRYPT_STATUS_FILE;
    private static final Object sRequestLock;
    private final IRecoverySystem mService;

    /* renamed from: android.os.RecoverySystem$ProgressListener */
    /* loaded from: classes3.dex */
    public interface ProgressListener {
        void onProgress(int i);
    }

    /* renamed from: android.os.RecoverySystem$ResumeOnRebootRebootErrorCode */
    /* loaded from: classes3.dex */
    public @interface ResumeOnRebootRebootErrorCode {
    }

    static {
        File file = new File("/cache/recovery");
        RECOVERY_DIR = file;
        LOG_FILE = new File(file, "log");
        BLOCK_MAP_FILE = new File(file, "block.map");
        UNCRYPT_PACKAGE_FILE = new File(file, "uncrypt_file");
        UNCRYPT_STATUS_FILE = new File(file, "uncrypt_status");
        sRequestLock = new Object();
    }

    private static HashSet<X509Certificate> getTrustedCerts(File keystore) throws IOException, GeneralSecurityException {
        HashSet<X509Certificate> trusted = new HashSet<>();
        if (keystore == null) {
            keystore = DEFAULT_KEYSTORE;
        }
        ZipFile zip = new ZipFile(keystore);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream is = zip.getInputStream(entry);
                trusted.add((X509Certificate) cf.generateCertificate(is));
                is.close();
            }
            return trusted;
        } finally {
            zip.close();
        }
    }

    public static void verifyPackage(File packageFile, ProgressListener listener, File deviceCertsZipFile) throws IOException, GeneralSecurityException {
        byte[] eocd;
        long fileLen = packageFile.length();
        RandomAccessFile raf = new RandomAccessFile(packageFile, "r");
        try {
            long startTimeMillis = System.currentTimeMillis();
            if (listener != null) {
                listener.onProgress(0);
            }
            raf.seek(fileLen - 6);
            byte[] footer = new byte[6];
            raf.readFully(footer);
            if (footer[2] != -1 || footer[3] != -1) {
                throw new SignatureException("no signature in file (no footer)");
            }
            int commentSize = (footer[4] & 255) | ((footer[5] & 255) << 8);
            int signatureStart = (footer[0] & 255) | ((footer[1] & 255) << 8);
            byte[] eocd2 = new byte[commentSize + 22];
            raf.seek(fileLen - (commentSize + 22));
            raf.readFully(eocd2);
            byte b = 80;
            if (eocd2[0] != 80 || eocd2[1] != 75 || eocd2[2] != 5 || eocd2[3] != 6) {
                throw new SignatureException("no signature in file (bad footer)");
            }
            int i = 4;
            while (i < eocd2.length - 3) {
                if (eocd2[i] == b && eocd2[i + 1] == 75 && eocd2[i + 2] == 5) {
                    if (eocd2[i + 3] == 6) {
                        throw new SignatureException("EOCD marker found after start of EOCD");
                    }
                }
                i++;
                b = 80;
            }
            PKCS7 block = new PKCS7(new ByteArrayInputStream(eocd2, (commentSize + 22) - signatureStart, signatureStart));
            X509Certificate[] certificates = block.getCertificates();
            if (certificates == null || certificates.length == 0) {
                throw new SignatureException("signature contains no certificates");
            }
            X509Certificate cert = certificates[0];
            PublicKey signatureKey = cert.getPublicKey();
            SignerInfo[] signerInfos = block.getSignerInfos();
            if (signerInfos == null || signerInfos.length == 0) {
                throw new SignatureException("signature contains no signedData");
            }
            SignerInfo signerInfo = signerInfos[0];
            boolean verified = false;
            HashSet<X509Certificate> trusted = getTrustedCerts(deviceCertsZipFile == null ? DEFAULT_KEYSTORE : deviceCertsZipFile);
            Iterator<X509Certificate> it = trusted.iterator();
            while (true) {
                if (!it.hasNext()) {
                    eocd = eocd2;
                    break;
                }
                X509Certificate c = it.next();
                eocd = eocd2;
                if (c.getPublicKey().equals(signatureKey)) {
                    verified = true;
                    break;
                }
                eocd2 = eocd;
            }
            if (!verified) {
                throw new SignatureException("signature doesn't match any trusted key");
            }
            raf.seek(0L);
            SignerInfo verifyResult = block.verify(signerInfo, new InputStream(fileLen, commentSize, startTimeMillis, raf, listener) { // from class: android.os.RecoverySystem.1
                long lastPublishTime;
                long toRead;
                final /* synthetic */ int val$commentSize;
                final /* synthetic */ long val$fileLen;
                final /* synthetic */ ProgressListener val$listenerForInner;
                final /* synthetic */ RandomAccessFile val$raf;
                final /* synthetic */ long val$startTimeMillis;
                long soFar = 0;
                int lastPercent = 0;

                {
                    this.val$fileLen = fileLen;
                    this.val$commentSize = commentSize;
                    this.val$startTimeMillis = startTimeMillis;
                    this.val$raf = raf;
                    this.val$listenerForInner = listener;
                    this.toRead = (fileLen - commentSize) - 2;
                    this.lastPublishTime = startTimeMillis;
                }

                @Override // java.io.InputStream
                public int read() throws IOException {
                    throw new UnsupportedOperationException();
                }

                @Override // java.io.InputStream
                public int read(byte[] b2, int off, int len) throws IOException {
                    if (this.soFar < this.toRead && !Thread.currentThread().isInterrupted()) {
                        int size = len;
                        long j = this.soFar;
                        long j2 = this.toRead;
                        if (size + j > j2) {
                            size = (int) (j2 - j);
                        }
                        int read = this.val$raf.read(b2, off, size);
                        this.soFar += read;
                        if (this.val$listenerForInner != null) {
                            long now = System.currentTimeMillis();
                            int p = (int) ((this.soFar * 100) / this.toRead);
                            if (p > this.lastPercent && now - this.lastPublishTime > RecoverySystem.PUBLISH_PROGRESS_INTERVAL_MS) {
                                this.lastPercent = p;
                                this.lastPublishTime = now;
                                this.val$listenerForInner.onProgress(p);
                            }
                        }
                        return read;
                    }
                    return -1;
                }
            });
            boolean interrupted = Thread.interrupted();
            if (listener != null) {
                listener.onProgress(100);
            }
            if (interrupted) {
                throw new SignatureException("verification was interrupted");
            }
            if (verifyResult == null) {
                throw new SignatureException("signature digest verification failed");
            }
            raf.close();
            if (!readAndVerifyPackageCompatibilityEntry(packageFile)) {
                throw new SignatureException("package compatibility verification failed");
            }
        } catch (Throwable th) {
            raf.close();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0052, code lost:
        throw new java.io.IOException("invalid entry size (" + r4 + ") in the compatibility file");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static boolean verifyPackageCompatibility(InputStream inputStream) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(inputStream);
        while (true) {
            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                long entrySize = entry.getSize();
                if (entrySize > 2147483647L || entrySize < 0) {
                    break;
                }
                byte[] bytes = new byte[(int) entrySize];
                Streams.readFully(zis, bytes);
                list.add(new String(bytes, StandardCharsets.UTF_8));
            } else if (list.isEmpty()) {
                throw new IOException("no entries found in the compatibility file");
            } else {
                return VintfObject.verify((String[]) list.toArray(new String[list.size()])) == 0;
            }
        }
    }

    private static boolean readAndVerifyPackageCompatibilityEntry(File packageFile) throws IOException {
        ZipFile zip = new ZipFile(packageFile);
        try {
            ZipEntry entry = zip.getEntry("compatibility.zip");
            if (entry != null) {
                InputStream inputStream = zip.getInputStream(entry);
                boolean verifyPackageCompatibility = verifyPackageCompatibility(inputStream);
                zip.close();
                return verifyPackageCompatibility;
            }
            zip.close();
            return true;
        } catch (Throwable th) {
            try {
                zip.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @SystemApi
    public static boolean verifyPackageCompatibility(File compatibilityFile) throws IOException {
        InputStream inputStream = new FileInputStream(compatibilityFile);
        try {
            boolean verifyPackageCompatibility = verifyPackageCompatibility(inputStream);
            inputStream.close();
            return verifyPackageCompatibility;
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @SystemApi
    public static void processPackage(Context context, File packageFile, ProgressListener listener, Handler handler) throws IOException {
        Handler progressHandler;
        String filename = packageFile.getCanonicalPath();
        if (!filename.startsWith("/data/")) {
            return;
        }
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        IRecoverySystemProgressListener progressListener = null;
        if (listener != null) {
            if (handler != null) {
                progressHandler = handler;
            } else {
                progressHandler = new Handler(context.getMainLooper());
            }
            progressListener = new IRecoverySystemProgressListener$StubC22172(progressHandler, listener);
        }
        if (!rs.uncrypt(filename, progressListener)) {
            throw new IOException("process package failed");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.RecoverySystem$2 */
    /* loaded from: classes3.dex */
    public class IRecoverySystemProgressListener$StubC22172 extends IRecoverySystemProgressListener.Stub {
        int lastProgress = 0;
        long lastPublishTime = System.currentTimeMillis();
        final /* synthetic */ ProgressListener val$listener;
        final /* synthetic */ Handler val$progressHandler;

        IRecoverySystemProgressListener$StubC22172(Handler handler, ProgressListener progressListener) {
            this.val$progressHandler = handler;
            this.val$listener = progressListener;
        }

        @Override // android.p008os.IRecoverySystemProgressListener
        public void onProgress(final int progress) {
            final long now = System.currentTimeMillis();
            this.val$progressHandler.post(new Runnable() { // from class: android.os.RecoverySystem.2.1
                @Override // java.lang.Runnable
                public void run() {
                    if (progress > IRecoverySystemProgressListener$StubC22172.this.lastProgress && now - IRecoverySystemProgressListener$StubC22172.this.lastPublishTime > RecoverySystem.PUBLISH_PROGRESS_INTERVAL_MS) {
                        IRecoverySystemProgressListener$StubC22172.this.lastProgress = progress;
                        IRecoverySystemProgressListener$StubC22172.this.lastPublishTime = now;
                        IRecoverySystemProgressListener$StubC22172.this.val$listener.onProgress(progress);
                    }
                }
            });
        }
    }

    @SystemApi
    public static void processPackage(Context context, File packageFile, ProgressListener listener) throws IOException {
        processPackage(context, packageFile, listener, null);
    }

    public static void installPackage(Context context, File packageFile) throws IOException {
        installPackage(context, packageFile, false);
    }

    @SystemApi
    public static void installPackage(Context context, File packageFile, boolean processed) throws IOException {
        synchronized (sRequestLock) {
            LOG_FILE.delete();
            File file = UNCRYPT_PACKAGE_FILE;
            file.delete();
            String filename = packageFile.getCanonicalPath();
            Log.m104w(TAG, "!!! REBOOTING TO INSTALL " + filename + " !!!");
            boolean securityUpdate = filename.endsWith("_s.zip");
            if (filename.startsWith("/data/")) {
                if (processed) {
                    if (!BLOCK_MAP_FILE.exists()) {
                        Log.m110e(TAG, "Package claimed to have been processed but failed to find the block map file.");
                        throw new IOException("Failed to find block map file");
                    }
                } else {
                    FileWriter uncryptFile = new FileWriter(file);
                    uncryptFile.write(filename + "\n");
                    uncryptFile.close();
                    if (!file.setReadable(true, false) || !file.setWritable(true, false)) {
                        Log.m110e(TAG, "Error setting permission for " + file);
                    }
                    BLOCK_MAP_FILE.delete();
                }
                filename = "@/cache/recovery/block.map";
            }
            String filenameArg = "--update_package=" + filename + "\n";
            String localeArg = "--locale=" + Locale.getDefault().toLanguageTag() + "\n";
            String command = filenameArg + localeArg;
            if (securityUpdate) {
                command = command + "--security\n";
            }
            RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
            if (!rs.setupBcb(command)) {
                throw new IOException("Setup BCB failed");
            }
            try {
            } catch (RemoteException e) {
                rs.clearBcb();
                e.rethrowAsRuntimeException();
            }
            if (!rs.allocateSpaceForUpdate(packageFile)) {
                rs.clearBcb();
                throw new IOException("Failed to allocate space for update " + packageFile.getAbsolutePath());
            }
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            String reason = PowerManager.REBOOT_RECOVERY_UPDATE;
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
                DisplayManager dm = (DisplayManager) context.getSystemService(DisplayManager.class);
                if (dm.getDisplay(0).getState() != 2) {
                    reason = PowerManager.REBOOT_RECOVERY_UPDATE + ",quiescent";
                }
            }
            pm.reboot(reason);
            throw new IOException("Reboot failed (no permissions?)");
        }
    }

    @SystemApi
    public static void prepareForUnattendedUpdate(Context context, String updateToken, IntentSender intentSender) throws IOException {
        if (updateToken == null) {
            throw new NullPointerException("updateToken == null");
        }
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        if (keyguardManager == null || !keyguardManager.isDeviceSecure()) {
            throw new IOException("Failed to request LSKF because the device doesn't have a lock screen. ");
        }
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        if (!rs.requestLskf(context.getPackageName(), intentSender)) {
            throw new IOException("preparation for update failed");
        }
    }

    @SystemApi
    public static void clearPrepareForUnattendedUpdate(Context context) throws IOException {
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        if (!rs.clearLskf(context.getPackageName())) {
            throw new IOException("could not reset unattended update state");
        }
    }

    @SystemApi
    public static void rebootAndApply(Context context, String updateToken, String reason) throws IOException {
        if (updateToken == null) {
            throw new NullPointerException("updateToken == null");
        }
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        if (rs.rebootWithLskfAssumeSlotSwitch(context.getPackageName(), reason) != 0) {
            throw new IOException("system not prepared to apply update");
        }
    }

    @SystemApi
    public static boolean isPreparedForUnattendedUpdate(Context context) throws IOException {
        RecoverySystem rs = (RecoverySystem) context.getSystemService(RecoverySystem.class);
        return rs.isLskfCaptured(context.getPackageName());
    }

    @SystemApi
    public static int rebootAndApply(Context context, String reason, boolean slotSwitch) throws IOException {
        RecoverySystem rs = (RecoverySystem) context.getSystemService(RecoverySystem.class);
        return rs.rebootWithLskf(context.getPackageName(), reason, slotSwitch);
    }

    @SystemApi
    public static void scheduleUpdateOnBoot(Context context, File packageFile) throws IOException {
        String filename = packageFile.getCanonicalPath();
        boolean securityUpdate = filename.endsWith("_s.zip");
        if (filename.startsWith("/data/")) {
            filename = "@/cache/recovery/block.map";
        }
        String filenameArg = "--update_package=" + filename + "\n";
        String localeArg = "--locale=" + Locale.getDefault().toLanguageTag() + "\n";
        String command = filenameArg + localeArg;
        if (securityUpdate) {
            command = command + "--security\n";
        }
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        if (!rs.setupBcb(command)) {
            throw new IOException("schedule update on boot failed");
        }
    }

    @SystemApi
    public static void cancelScheduledUpdate(Context context) throws IOException {
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        if (!rs.clearBcb()) {
            throw new IOException("cancel scheduled update failed");
        }
    }

    public static void rebootWipeUserData(Context context) throws IOException {
        rebootWipeUserData(context, false, context.getPackageName(), false, false);
    }

    public static void rebootWipeUserData(Context context, String reason) throws IOException {
        rebootWipeUserData(context, false, reason, false, false);
    }

    public static void rebootWipeUserData(Context context, boolean shutdown) throws IOException {
        rebootWipeUserData(context, shutdown, context.getPackageName(), false, false);
    }

    public static void rebootWipeUserData(Context context, boolean shutdown, String reason, boolean force) throws IOException {
        rebootWipeUserData(context, shutdown, reason, force, false);
    }

    public static void rebootWipeUserData(Context context, boolean shutdown, String reason, boolean force, boolean wipeEuicc) throws IOException {
        UserManager um = (UserManager) context.getSystemService("user");
        if (!force && um.hasUserRestriction(UserManager.DISALLOW_FACTORY_RESET)) {
            throw new SecurityException("Wiping data is not allowed for this user.");
        }
        final ConditionVariable condition = new ConditionVariable();
        Intent intent = new Intent(Intent.ACTION_MASTER_CLEAR_NOTIFICATION);
        intent.addFlags(285212672);
        context.sendOrderedBroadcastAsUser(intent, UserHandle.SYSTEM, Manifest.C0000permission.MASTER_CLEAR, new BroadcastReceiver() { // from class: android.os.RecoverySystem.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent2) {
                ConditionVariable.this.open();
            }
        }, null, 0, null, null);
        condition.block();
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        if (wipeEuicc) {
            wipeEuiccData(context, "android");
        } else {
            removeEuiccInvisibleSubs(context, euiccManager);
        }
        String shutdownArg = null;
        if (shutdown) {
            shutdownArg = "--shutdown_after";
        }
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            String timeStamp = DateFormat.format("yyyy-MM-ddTHH:mm:ssZ", System.currentTimeMillis()).toString();
            reasonArg = "--reason=" + sanitizeArg(reason + "," + timeStamp);
        }
        String localeArg = "--locale=" + Locale.getDefault().toLanguageTag();
        bootCommand(context, shutdownArg, "--wipe_data", reasonArg, localeArg);
    }

    public static boolean wipeEuiccData(Context context, String packageName) {
        ContentResolver cr = context.getContentResolver();
        if (Settings.Global.getInt(cr, Settings.Global.EUICC_PROVISIONED, 0) == 0) {
            Log.m112d(TAG, "Skipping eUICC wipe/retain as it is not provisioned");
            return true;
        }
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(Context.EUICC_SERVICE);
        if (euiccManager == null || !euiccManager.isEnabled()) {
            return false;
        }
        final CountDownLatch euiccFactoryResetLatch = new CountDownLatch(1);
        final AtomicBoolean wipingSucceeded = new AtomicBoolean(false);
        BroadcastReceiver euiccWipeFinishReceiver = new BroadcastReceiver() { // from class: android.os.RecoverySystem.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (RecoverySystem.ACTION_EUICC_FACTORY_RESET.equals(intent.getAction())) {
                    if (getResultCode() != 0) {
                        int detailedCode = intent.getIntExtra(EuiccManager.EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE, 0);
                        Log.m110e(RecoverySystem.TAG, "Error wiping euicc data, Detailed code = " + detailedCode);
                    } else {
                        Log.m112d(RecoverySystem.TAG, "Successfully wiped euicc data.");
                        wipingSucceeded.set(true);
                    }
                    euiccFactoryResetLatch.countDown();
                }
            }
        };
        Intent intent = new Intent(ACTION_EUICC_FACTORY_RESET);
        intent.setPackage(packageName);
        PendingIntent callbackIntent = PendingIntent.getBroadcastAsUser(context, 0, intent, Enums.AUDIO_FORMAT_DTS_HD, UserHandle.SYSTEM);
        IntentFilter filterConsent = new IntentFilter();
        filterConsent.addAction(ACTION_EUICC_FACTORY_RESET);
        HandlerThread euiccHandlerThread = new HandlerThread("euiccWipeFinishReceiverThread");
        euiccHandlerThread.start();
        Handler euiccHandler = new Handler(euiccHandlerThread.getLooper());
        context.getApplicationContext().registerReceiver(euiccWipeFinishReceiver, filterConsent, null, euiccHandler);
        euiccManager.eraseSubscriptions(callbackIntent);
        try {
            try {
                long waitingTimeMillis = Settings.Global.getLong(context.getContentResolver(), Settings.Global.EUICC_FACTORY_RESET_TIMEOUT_MILLIS, 30000L);
                if (waitingTimeMillis < 5000) {
                    waitingTimeMillis = 5000;
                } else if (waitingTimeMillis > 60000) {
                    waitingTimeMillis = 60000;
                }
                try {
                    try {
                        if (euiccFactoryResetLatch.await(waitingTimeMillis, TimeUnit.MILLISECONDS)) {
                            context.getApplicationContext().unregisterReceiver(euiccWipeFinishReceiver);
                            return wipingSucceeded.get();
                        }
                        Log.m110e(TAG, "Timeout wiping eUICC data.");
                        context.getApplicationContext().unregisterReceiver(euiccWipeFinishReceiver);
                        return false;
                    } catch (InterruptedException e) {
                        e = e;
                        Thread.currentThread().interrupt();
                        Log.m109e(TAG, "Wiping eUICC data interrupted", e);
                        context.getApplicationContext().unregisterReceiver(euiccWipeFinishReceiver);
                        return false;
                    }
                } catch (Throwable th) {
                    e = th;
                    context.getApplicationContext().unregisterReceiver(euiccWipeFinishReceiver);
                    throw e;
                }
            } catch (InterruptedException e2) {
                e = e2;
            } catch (Throwable th2) {
                e = th2;
                context.getApplicationContext().unregisterReceiver(euiccWipeFinishReceiver);
                throw e;
            }
        } catch (InterruptedException e3) {
            e = e3;
        } catch (Throwable th3) {
            e = th3;
        }
    }

    private static void removeEuiccInvisibleSubs(Context context, EuiccManager euiccManager) {
        ContentResolver cr = context.getContentResolver();
        if (Settings.Global.getInt(cr, Settings.Global.EUICC_PROVISIONED, 0) == 0) {
            Log.m108i(TAG, "Skip removing eUICC invisible profiles as it is not provisioned.");
        } else if (euiccManager == null || !euiccManager.isEnabled()) {
            Log.m108i(TAG, "Skip removing eUICC invisible profiles as eUICC manager is not available.");
        } else {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
            List<SubscriptionInfo> availableSubs = subscriptionManager.getAvailableSubscriptionInfoList();
            if (availableSubs == null || availableSubs.isEmpty()) {
                Log.m108i(TAG, "Skip removing eUICC invisible profiles as no available profiles found.");
                return;
            }
            List<SubscriptionInfo> invisibleSubs = new ArrayList<>();
            for (SubscriptionInfo sub : availableSubs) {
                if (sub.isEmbedded() && sub.getGroupUuid() != null && sub.isOpportunistic()) {
                    invisibleSubs.add(sub);
                }
            }
            removeEuiccInvisibleSubs(context, invisibleSubs, euiccManager);
        }
    }

    private static boolean removeEuiccInvisibleSubs(Context context, List<SubscriptionInfo> subscriptionInfos, EuiccManager euiccManager) {
        if (subscriptionInfos != null && !subscriptionInfos.isEmpty()) {
            final CountDownLatch removeSubsLatch = new CountDownLatch(subscriptionInfos.size());
            final AtomicInteger removedSubsCount = new AtomicInteger(0);
            BroadcastReceiver removeEuiccSubsReceiver = new BroadcastReceiver() { // from class: android.os.RecoverySystem.5
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (RecoverySystem.ACTION_EUICC_REMOVE_INVISIBLE_SUBSCRIPTIONS.equals(intent.getAction())) {
                        if (getResultCode() != 0) {
                            int detailedCode = intent.getIntExtra(EuiccManager.EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE, 0);
                            Log.m110e(RecoverySystem.TAG, "Error removing euicc opportunistic profile, Detailed code = " + detailedCode);
                        } else {
                            Log.m110e(RecoverySystem.TAG, "Successfully remove euicc opportunistic profile.");
                            removedSubsCount.incrementAndGet();
                        }
                        removeSubsLatch.countDown();
                    }
                }
            };
            Intent intent = new Intent(ACTION_EUICC_REMOVE_INVISIBLE_SUBSCRIPTIONS);
            intent.setPackage("android");
            PendingIntent callbackIntent = PendingIntent.getBroadcastAsUser(context, 0, intent, Enums.AUDIO_FORMAT_DTS_HD, UserHandle.SYSTEM);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_EUICC_REMOVE_INVISIBLE_SUBSCRIPTIONS);
            HandlerThread euiccHandlerThread = new HandlerThread("euiccRemovingSubsReceiverThread");
            euiccHandlerThread.start();
            Handler euiccHandler = new Handler(euiccHandlerThread.getLooper());
            context.getApplicationContext().registerReceiver(removeEuiccSubsReceiver, intentFilter, null, euiccHandler);
            for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {
                Log.m108i(TAG, "Remove invisible subscription " + subscriptionInfo.getSubscriptionId() + " from card " + subscriptionInfo.getCardId());
                euiccManager.createForCardId(subscriptionInfo.getCardId()).deleteSubscription(subscriptionInfo.getSubscriptionId(), callbackIntent);
            }
            try {
                long waitingTimeMillis = Settings.Global.getLong(context.getContentResolver(), Settings.Global.EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS, DEFAULT_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS);
                if (waitingTimeMillis < MIN_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS) {
                    waitingTimeMillis = MIN_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS;
                } else if (waitingTimeMillis > MAX_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS) {
                    waitingTimeMillis = MAX_EUICC_REMOVING_INVISIBLE_PROFILES_TIMEOUT_MILLIS;
                }
                if (!removeSubsLatch.await(waitingTimeMillis, TimeUnit.MILLISECONDS)) {
                    Log.m110e(TAG, "Timeout removing invisible euicc profiles.");
                    return false;
                }
                context.getApplicationContext().unregisterReceiver(removeEuiccSubsReceiver);
                euiccHandlerThread.quit();
                return removedSubsCount.get() == subscriptionInfos.size();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.m109e(TAG, "Removing invisible euicc profiles interrupted", e);
                return false;
            } finally {
                context.getApplicationContext().unregisterReceiver(removeEuiccSubsReceiver);
                euiccHandlerThread.quit();
            }
        }
        Log.m108i(TAG, "There are no eUICC invisible profiles needed to be removed.");
        return true;
    }

    public static void rebootPromptAndWipeUserData(Context context, String reason) throws IOException {
        boolean checkpointing = false;
        IVold vold = null;
        try {
            vold = IVold.Stub.asInterface(ServiceManager.checkService("vold"));
            if (vold != null) {
                checkpointing = vold.needsCheckpoint();
            } else {
                Log.m104w(TAG, "Failed to get vold");
            }
        } catch (Exception e) {
            Log.m104w(TAG, "Failed to check for checkpointing");
        }
        if (checkpointing) {
            try {
                vold.abortChanges("rescueparty", false);
                Log.m108i(TAG, "Rescue Party requested wipe. Aborting update");
                return;
            } catch (Exception e2) {
                Log.m108i(TAG, "Rescue Party requested wipe. Rebooting instead.");
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                pm.reboot("rescueparty");
                return;
            }
        }
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            reasonArg = "--reason=" + sanitizeArg(reason);
        }
        String localeArg = "--locale=" + Locale.getDefault().toString();
        bootCommand(context, null, "--prompt_and_wipe_data", reasonArg, localeArg);
    }

    public static void rebootWipeCache(Context context) throws IOException {
        rebootWipeCache(context, context.getPackageName());
    }

    public static void rebootWipeCache(Context context, String reason) throws IOException {
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            reasonArg = "--reason=" + sanitizeArg(reason);
        }
        String localeArg = "--locale=" + Locale.getDefault().toLanguageTag();
        bootCommand(context, "--wipe_cache", reasonArg, localeArg);
    }

    @SystemApi
    public static void rebootWipeAb(Context context, File packageFile, String reason) throws IOException {
        String reasonArg = null;
        if (!TextUtils.isEmpty(reason)) {
            reasonArg = "--reason=" + sanitizeArg(reason);
        }
        String filename = packageFile.getCanonicalPath();
        String filenameArg = "--wipe_package=" + filename;
        String localeArg = "--locale=" + Locale.getDefault().toLanguageTag();
        bootCommand(context, "--wipe_ab", filenameArg, reasonArg, localeArg);
    }

    private static void bootCommand(Context context, String... args) throws IOException {
        LOG_FILE.delete();
        StringBuilder command = new StringBuilder();
        for (String arg : args) {
            if (!TextUtils.isEmpty(arg)) {
                command.append(arg);
                command.append("\n");
            }
        }
        RecoverySystem rs = (RecoverySystem) context.getSystemService("recovery");
        rs.rebootRecoveryWithCommand(command.toString());
        throw new IOException("Reboot failed (no permissions?)");
    }

    public static String handleAftermath(Context context) {
        String log = null;
        try {
            log = FileUtils.readTextFile(LOG_FILE, -65536, "...\n");
        } catch (FileNotFoundException e) {
            Log.m108i(TAG, "No recovery log file");
        } catch (IOException e2) {
            Log.m109e(TAG, "Error reading recovery log", e2);
        }
        boolean reservePackage = BLOCK_MAP_FILE.exists();
        if (!reservePackage) {
            File file = UNCRYPT_PACKAGE_FILE;
            if (file.exists()) {
                String filename = null;
                try {
                    filename = FileUtils.readTextFile(file, 0, null);
                } catch (IOException e3) {
                    Log.m109e(TAG, "Error reading uncrypt file", e3);
                }
                if (filename != null && filename.startsWith("/data")) {
                    if (UNCRYPT_PACKAGE_FILE.delete()) {
                        Log.m108i(TAG, "Deleted: " + filename);
                    } else {
                        Log.m110e(TAG, "Can't delete: " + filename);
                    }
                }
            }
        }
        String[] names = RECOVERY_DIR.list();
        for (int i = 0; names != null && i < names.length; i++) {
            if (!names[i].startsWith(LAST_PREFIX) && !names[i].equals(LAST_INSTALL_PATH) && ((!reservePackage || !names[i].equals(BLOCK_MAP_FILE.getName())) && (!reservePackage || !names[i].equals(UNCRYPT_PACKAGE_FILE.getName())))) {
                recursiveDelete(new File(RECOVERY_DIR, names[i]));
            }
        }
        return log;
    }

    private static void recursiveDelete(File name) {
        if (name.isDirectory()) {
            String[] files = name.list();
            for (int i = 0; files != null && i < files.length; i++) {
                File f = new File(name, files[i]);
                recursiveDelete(f);
            }
        }
        if (!name.delete()) {
            Log.m110e(TAG, "Can't delete: " + name);
        } else {
            Log.m108i(TAG, "Deleted: " + name);
        }
    }

    private boolean uncrypt(String packageFile, IRecoverySystemProgressListener listener) {
        try {
            return this.mService.uncrypt(packageFile, listener);
        } catch (RemoteException e) {
            return false;
        }
    }

    private boolean setupBcb(String command) {
        try {
            return this.mService.setupBcb(command);
        } catch (RemoteException e) {
            return false;
        }
    }

    private boolean allocateSpaceForUpdate(File packageFile) throws RemoteException {
        return this.mService.allocateSpaceForUpdate(packageFile.getAbsolutePath());
    }

    private boolean clearBcb() {
        try {
            return this.mService.clearBcb();
        } catch (RemoteException e) {
            return false;
        }
    }

    private void rebootRecoveryWithCommand(String command) {
        try {
            this.mService.rebootRecoveryWithCommand(command);
        } catch (RemoteException e) {
        }
    }

    private boolean requestLskf(String packageName, IntentSender sender) throws IOException {
        try {
            return this.mService.requestLskf(packageName, sender);
        } catch (RemoteException | SecurityException e) {
            throw new IOException("could not request LSKF capture", e);
        }
    }

    private boolean clearLskf(String packageName) throws IOException {
        try {
            return this.mService.clearLskf(packageName);
        } catch (RemoteException | SecurityException e) {
            throw new IOException("could not clear LSKF", e);
        }
    }

    private boolean isLskfCaptured(String packageName) throws IOException {
        try {
            return this.mService.isLskfCaptured(packageName);
        } catch (RemoteException | SecurityException e) {
            throw new IOException("could not get LSKF capture state", e);
        }
    }

    private int rebootWithLskf(String packageName, String reason, boolean slotSwitch) throws IOException {
        try {
            return this.mService.rebootWithLskf(packageName, reason, slotSwitch);
        } catch (RemoteException | SecurityException e) {
            throw new IOException("could not reboot for update", e);
        }
    }

    private int rebootWithLskfAssumeSlotSwitch(String packageName, String reason) throws IOException {
        try {
            return this.mService.rebootWithLskfAssumeSlotSwitch(packageName, reason);
        } catch (RemoteException | RuntimeException e) {
            throw new IOException("could not reboot for update", e);
        }
    }

    private static String sanitizeArg(String arg) {
        return arg.replace((char) 0, '?').replace('\n', '?');
    }

    public RecoverySystem() {
        this.mService = null;
    }

    public RecoverySystem(IRecoverySystem service) {
        this.mService = service;
    }
}
