package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.FileUtils;
import android.provider.Settings;
import android.util.Slog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class CertBlacklister extends Binder {
    public static final String BLACKLIST_ROOT;
    public static final String PUBKEY_PATH;
    public static final String SERIAL_PATH;

    static {
        String str = System.getenv("ANDROID_DATA") + "/misc/keychain/";
        BLACKLIST_ROOT = str;
        PUBKEY_PATH = str + "pubkey_blacklist.txt";
        SERIAL_PATH = str + "serial_blacklist.txt";
    }

    /* loaded from: classes.dex */
    public static class BlacklistObserver extends ContentObserver {
        public final ContentResolver mContentResolver;
        public final String mKey;
        public final String mName;
        public final String mPath;
        public final File mTmpDir;

        public BlacklistObserver(String str, String str2, String str3, ContentResolver contentResolver) {
            super(null);
            this.mKey = str;
            this.mName = str2;
            this.mPath = str3;
            this.mTmpDir = new File(str3).getParentFile();
            this.mContentResolver = contentResolver;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            writeBlacklist();
        }

        public String getValue() {
            return Settings.Secure.getString(this.mContentResolver, this.mKey);
        }

        public final void writeBlacklist() {
            new Thread("BlacklistUpdater") { // from class: com.android.server.CertBlacklister.BlacklistObserver.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    File createTempFile;
                    FileOutputStream fileOutputStream;
                    synchronized (BlacklistObserver.this.mTmpDir) {
                        String value = BlacklistObserver.this.getValue();
                        if (value != null) {
                            Slog.i("CertBlacklister", "Certificate blacklist changed, updating...");
                            FileOutputStream fileOutputStream2 = null;
                            try {
                                try {
                                    createTempFile = File.createTempFile("journal", "", BlacklistObserver.this.mTmpDir);
                                    createTempFile.setReadable(true, false);
                                    fileOutputStream = new FileOutputStream(createTempFile);
                                } catch (IOException e) {
                                    e = e;
                                }
                            } catch (Throwable th) {
                                th = th;
                            }
                            try {
                                fileOutputStream.write(value.getBytes());
                                FileUtils.sync(fileOutputStream);
                                createTempFile.renameTo(new File(BlacklistObserver.this.mPath));
                                Slog.i("CertBlacklister", "Certificate blacklist updated");
                                IoUtils.closeQuietly(fileOutputStream);
                            } catch (IOException e2) {
                                e = e2;
                                fileOutputStream2 = fileOutputStream;
                                Slog.e("CertBlacklister", "Failed to write blacklist", e);
                                IoUtils.closeQuietly(fileOutputStream2);
                            } catch (Throwable th2) {
                                th = th2;
                                fileOutputStream2 = fileOutputStream;
                                IoUtils.closeQuietly(fileOutputStream2);
                                throw th;
                            }
                        }
                    }
                }
            }.start();
        }
    }

    public CertBlacklister(Context context) {
        registerObservers(context.getContentResolver());
    }

    public final BlacklistObserver buildPubkeyObserver(ContentResolver contentResolver) {
        return new BlacklistObserver("pubkey_blacklist", "pubkey", PUBKEY_PATH, contentResolver);
    }

    public final BlacklistObserver buildSerialObserver(ContentResolver contentResolver) {
        return new BlacklistObserver("serial_blacklist", "serial", SERIAL_PATH, contentResolver);
    }

    public final void registerObservers(ContentResolver contentResolver) {
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("pubkey_blacklist"), true, buildPubkeyObserver(contentResolver));
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("serial_blacklist"), true, buildSerialObserver(contentResolver));
    }
}
