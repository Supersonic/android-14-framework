package com.android.server.updates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.util.HexDump;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import libcore.io.IoUtils;
import libcore.io.Streams;
/* loaded from: classes2.dex */
public class ConfigUpdateInstallReceiver extends BroadcastReceiver {
    public final File updateContent;
    public final File updateDir;
    public final File updateVersion;

    public void postInstall(Context context, Intent intent) {
    }

    public boolean verifyVersion(int i, int i2) {
        return i < i2;
    }

    public ConfigUpdateInstallReceiver(String str, String str2, String str3, String str4) {
        this.updateDir = new File(str);
        this.updateContent = new File(str, str2);
        this.updateVersion = new File(new File(str, str3), str4);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        new Thread() { // from class: com.android.server.updates.ConfigUpdateInstallReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    int versionFromIntent = ConfigUpdateInstallReceiver.this.getVersionFromIntent(intent);
                    String requiredHashFromIntent = ConfigUpdateInstallReceiver.this.getRequiredHashFromIntent(intent);
                    int currentVersion = ConfigUpdateInstallReceiver.this.getCurrentVersion();
                    String currentHash = ConfigUpdateInstallReceiver.getCurrentHash(ConfigUpdateInstallReceiver.this.getCurrentContent());
                    if (!ConfigUpdateInstallReceiver.this.verifyVersion(currentVersion, versionFromIntent)) {
                        Slog.i("ConfigUpdateInstallReceiver", "Not installing, new version is <= current version");
                    } else if (!ConfigUpdateInstallReceiver.this.verifyPreviousHash(currentHash, requiredHashFromIntent)) {
                        EventLog.writeEvent(51300, "Current hash did not match required value");
                    } else {
                        Slog.i("ConfigUpdateInstallReceiver", "Found new update, installing...");
                        BufferedInputStream altContent = ConfigUpdateInstallReceiver.this.getAltContent(context, intent);
                        ConfigUpdateInstallReceiver.this.install(altContent, versionFromIntent);
                        if (altContent != null) {
                            altContent.close();
                        }
                        Slog.i("ConfigUpdateInstallReceiver", "Installation successful");
                        ConfigUpdateInstallReceiver.this.postInstall(context, intent);
                    }
                } catch (Exception e) {
                    Slog.e("ConfigUpdateInstallReceiver", "Could not update content!", e);
                    String exc = e.toString();
                    if (exc.length() > 100) {
                        exc = exc.substring(0, 99);
                    }
                    EventLog.writeEvent(51300, exc);
                }
            }
        }.start();
    }

    public final Uri getContentFromIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            return data;
        }
        throw new IllegalStateException("Missing required content path, ignoring.");
    }

    public final int getVersionFromIntent(Intent intent) throws NumberFormatException {
        String stringExtra = intent.getStringExtra("VERSION");
        if (stringExtra == null) {
            throw new IllegalStateException("Missing required version number, ignoring.");
        }
        return Integer.parseInt(stringExtra.trim());
    }

    public final String getRequiredHashFromIntent(Intent intent) {
        String stringExtra = intent.getStringExtra("REQUIRED_HASH");
        if (stringExtra == null) {
            throw new IllegalStateException("Missing required previous hash, ignoring.");
        }
        return stringExtra.trim();
    }

    public final int getCurrentVersion() throws NumberFormatException {
        try {
            return Integer.parseInt(IoUtils.readFileAsString(this.updateVersion.getCanonicalPath()).trim());
        } catch (IOException unused) {
            Slog.i("ConfigUpdateInstallReceiver", "Couldn't find current metadata, assuming first update");
            return 0;
        }
    }

    public final BufferedInputStream getAltContent(Context context, Intent intent) throws IOException {
        Uri contentFromIntent = getContentFromIntent(intent);
        Binder.allowBlockingForCurrentThread();
        try {
            return new BufferedInputStream(context.getContentResolver().openInputStream(contentFromIntent));
        } finally {
            Binder.defaultBlockingForCurrentThread();
        }
    }

    public final byte[] getCurrentContent() {
        try {
            return IoUtils.readFileAsByteArray(this.updateContent.getCanonicalPath());
        } catch (IOException unused) {
            Slog.i("ConfigUpdateInstallReceiver", "Failed to read current content, assuming first update!");
            return null;
        }
    }

    public static String getCurrentHash(byte[] bArr) {
        if (bArr == null) {
            return "0";
        }
        try {
            return HexDump.toHexString(MessageDigest.getInstance("SHA512").digest(bArr), false);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    public final boolean verifyPreviousHash(String str, String str2) {
        if (str2.equals("NONE")) {
            return true;
        }
        return str.equals(str2);
    }

    public void writeUpdate(File file, File file2, InputStream inputStream) throws IOException {
        FileOutputStream fileOutputStream;
        File file3 = null;
        try {
            File parentFile = file2.getParentFile();
            parentFile.mkdirs();
            if (!parentFile.exists()) {
                throw new IOException("Failed to create directory " + parentFile.getCanonicalPath());
            }
            while (!parentFile.equals(this.updateDir)) {
                parentFile.setExecutable(true, false);
                parentFile = parentFile.getParentFile();
            }
            File createTempFile = File.createTempFile("journal", "", file);
            try {
                createTempFile.setReadable(true, false);
                fileOutputStream = new FileOutputStream(createTempFile);
                try {
                    Streams.copy(inputStream, fileOutputStream);
                    fileOutputStream.getFD().sync();
                    if (!createTempFile.renameTo(file2)) {
                        throw new IOException("Failed to atomically rename " + file2.getCanonicalPath());
                    }
                    createTempFile.delete();
                    IoUtils.closeQuietly(fileOutputStream);
                } catch (Throwable th) {
                    file3 = createTempFile;
                    th = th;
                    if (file3 != null) {
                        file3.delete();
                    }
                    IoUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                file3 = createTempFile;
                th = th2;
                fileOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
    }

    public void install(InputStream inputStream, int i) throws IOException {
        writeUpdate(this.updateDir, this.updateContent, inputStream);
        writeUpdate(this.updateDir, this.updateVersion, new ByteArrayInputStream(Long.toString(i).getBytes()));
    }
}
