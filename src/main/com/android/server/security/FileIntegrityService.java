package com.android.server.security;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.security.IFileIntegrityService;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.security.VerityUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class FileIntegrityService extends SystemService {
    public static CertificateFactory sCertFactory;
    public final IBinder mService;
    @GuardedBy({"mTrustedCertificates"})
    public final ArrayList<X509Certificate> mTrustedCertificates;

    public static FileIntegrityService getService() {
        return (FileIntegrityService) LocalServices.getService(FileIntegrityService.class);
    }

    public FileIntegrityService(Context context) {
        super(context);
        this.mTrustedCertificates = new ArrayList<>();
        this.mService = new IFileIntegrityService.Stub() { // from class: com.android.server.security.FileIntegrityService.1
            public boolean isApkVeritySupported() {
                return VerityUtils.isFsVeritySupported();
            }

            public boolean isAppSourceCertificateTrusted(byte[] bArr, String str) {
                boolean contains;
                checkCallerPermission(str);
                try {
                    if (VerityUtils.isFsVeritySupported()) {
                        if (bArr == null) {
                            Slog.w("FileIntegrityService", "Received a null certificate");
                            return false;
                        }
                        synchronized (FileIntegrityService.this.mTrustedCertificates) {
                            contains = FileIntegrityService.this.mTrustedCertificates.contains(FileIntegrityService.toCertificate(bArr));
                        }
                        return contains;
                    }
                    return false;
                } catch (CertificateException e) {
                    Slog.e("FileIntegrityService", "Failed to convert the certificate: " + e);
                    return false;
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
                new FileIntegrityServiceShellCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
            }

            public final void checkCallerPermission(String str) {
                int callingUid = Binder.getCallingUid();
                if (callingUid != ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageUid(str, 0L, UserHandle.getUserId(callingUid))) {
                    throw new SecurityException("Calling uid " + callingUid + " does not own package " + str);
                } else if (FileIntegrityService.this.getContext().checkCallingPermission("android.permission.INSTALL_PACKAGES") != 0 && ((AppOpsManager) FileIntegrityService.this.getContext().getSystemService(AppOpsManager.class)).checkOpNoThrow(66, callingUid, str) != 0) {
                    throw new SecurityException("Caller should have INSTALL_PACKAGES or REQUEST_INSTALL_PACKAGES");
                }
            }
        };
        try {
            sCertFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException unused) {
            Slog.wtf("FileIntegrityService", "Cannot get an instance of X.509 certificate factory");
        }
        LocalServices.addService(FileIntegrityService.class, this);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        loadAllCertificates();
        publishBinderService("file_integrity", this.mService);
    }

    public boolean verifyPkcs7DetachedSignature(String str, String str2) throws IOException {
        if (Files.size(Paths.get(str, new String[0])) > 8192) {
            throw new SecurityException("Signature file is unexpectedly large: " + str);
        }
        byte[] readAllBytes = Files.readAllBytes(Paths.get(str, new String[0]));
        byte[] fsverityDigest = VerityUtils.getFsverityDigest(str2);
        synchronized (this.mTrustedCertificates) {
            Iterator<X509Certificate> it = this.mTrustedCertificates.iterator();
            while (it.hasNext()) {
                try {
                } catch (CertificateEncodingException e) {
                    Slog.w("FileIntegrityService", "Ignoring ill-formed certificate: " + e);
                }
                if (VerityUtils.verifyPkcs7DetachedSignature(readAllBytes, fsverityDigest, new ByteArrayInputStream(it.next().getEncoded()))) {
                    return true;
                }
            }
            return false;
        }
    }

    public final void loadAllCertificates() {
        loadCertificatesFromDirectory(Environment.getRootDirectory().toPath().resolve("etc/security/fsverity"));
        loadCertificatesFromDirectory(Environment.getProductDirectory().toPath().resolve("etc/security/fsverity"));
    }

    public final void loadCertificatesFromDirectory(Path path) {
        try {
            File[] listFiles = path.toFile().listFiles();
            if (listFiles == null) {
                return;
            }
            for (File file : listFiles) {
                collectCertificate(Files.readAllBytes(file.toPath()));
            }
        } catch (IOException e) {
            Slog.wtf("FileIntegrityService", "Failed to load fs-verity certificate from " + path, e);
        }
    }

    public final void collectCertificate(byte[] bArr) {
        try {
            synchronized (this.mTrustedCertificates) {
                this.mTrustedCertificates.add(toCertificate(bArr));
            }
        } catch (CertificateException e) {
            Slog.e("FileIntegrityService", "Invalid certificate, ignored: " + e);
        }
    }

    public static X509Certificate toCertificate(byte[] bArr) throws CertificateException {
        Certificate generateCertificate = sCertFactory.generateCertificate(new ByteArrayInputStream(bArr));
        if (!(generateCertificate instanceof X509Certificate)) {
            throw new CertificateException("Expected to contain an X.509 certificate");
        }
        return (X509Certificate) generateCertificate;
    }

    /* loaded from: classes2.dex */
    public class FileIntegrityServiceShellCommand extends ShellCommand {
        public FileIntegrityServiceShellCommand() {
        }

        public int onCommand(String str) {
            if (Build.IS_DEBUGGABLE) {
                if (str == null) {
                    return handleDefaultCommands(str);
                }
                PrintWriter outPrintWriter = getOutPrintWriter();
                if (!str.equals("append-cert")) {
                    if (str.equals("remove-last-cert")) {
                        synchronized (FileIntegrityService.this.mTrustedCertificates) {
                            if (FileIntegrityService.this.mTrustedCertificates.size() == 0) {
                                outPrintWriter.println("Certificate list is already empty");
                                return -1;
                            }
                            FileIntegrityService.this.mTrustedCertificates.remove(FileIntegrityService.this.mTrustedCertificates.size() - 1);
                            outPrintWriter.println("Certificate is removed successfully");
                            return 0;
                        }
                    }
                    outPrintWriter.println("Unknown action");
                    outPrintWriter.println("");
                    onHelp();
                    return -1;
                }
                String nextArg = getNextArg();
                if (nextArg == null) {
                    outPrintWriter.println("Invalid argument");
                    outPrintWriter.println("");
                    onHelp();
                    return -1;
                }
                ParcelFileDescriptor openFileForSystem = openFileForSystem(nextArg, "r");
                if (openFileForSystem == null) {
                    outPrintWriter.println("Cannot open the file");
                    return -1;
                }
                try {
                    FileIntegrityService.this.collectCertificate(new ParcelFileDescriptor.AutoCloseInputStream(openFileForSystem).readAllBytes());
                    outPrintWriter.println("Certificate is added successfully");
                    return 0;
                } catch (IOException e) {
                    outPrintWriter.println("Failed to add certificate: " + e);
                    return -1;
                }
            }
            return -1;
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("File integrity service commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Print this help text.");
            outPrintWriter.println("  append-cert path/to/cert.der");
            outPrintWriter.println("    Add the DER-encoded certificate (only in debug builds)");
            outPrintWriter.println("  remove-last-cert");
            outPrintWriter.println("    Remove the last certificate in the key list (only in debug builds)");
            outPrintWriter.println("");
        }
    }
}
