package com.android.server.backup;

import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public final class ProcessedPackagesJournal {
    @GuardedBy({"mProcessedPackages"})
    public final Set<String> mProcessedPackages = new HashSet();
    public final File mStateDirectory;

    public ProcessedPackagesJournal(File file) {
        this.mStateDirectory = file;
    }

    public void init() {
        synchronized (this.mProcessedPackages) {
            loadFromDisk();
        }
    }

    public boolean hasBeenProcessed(String str) {
        boolean contains;
        synchronized (this.mProcessedPackages) {
            contains = this.mProcessedPackages.contains(str);
        }
        return contains;
    }

    public void addPackage(String str) {
        RandomAccessFile randomAccessFile;
        synchronized (this.mProcessedPackages) {
            if (this.mProcessedPackages.add(str)) {
                File file = new File(this.mStateDirectory, "processed");
                try {
                    randomAccessFile = new RandomAccessFile(file, "rws");
                } catch (IOException unused) {
                    Slog.e("ProcessedPackagesJournal", "Can't log backup of " + str + " to " + file);
                }
                try {
                    randomAccessFile.seek(randomAccessFile.length());
                    randomAccessFile.writeUTF(str);
                    randomAccessFile.close();
                } catch (Throwable th) {
                    try {
                        randomAccessFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
        }
    }

    public Set<String> getPackagesCopy() {
        HashSet hashSet;
        synchronized (this.mProcessedPackages) {
            hashSet = new HashSet(this.mProcessedPackages);
        }
        return hashSet;
    }

    public void reset() {
        synchronized (this.mProcessedPackages) {
            this.mProcessedPackages.clear();
            new File(this.mStateDirectory, "processed").delete();
        }
    }

    public final void loadFromDisk() {
        File file = new File(this.mStateDirectory, "processed");
        if (!file.exists()) {
            return;
        }
        try {
            while (true) {
                String readUTF = new DataInputStream(new BufferedInputStream(new FileInputStream(file))).readUTF();
                Slog.v("ProcessedPackagesJournal", "   + " + readUTF);
                this.mProcessedPackages.add(readUTF);
            }
        } catch (EOFException unused) {
        } catch (IOException e) {
            Slog.e("ProcessedPackagesJournal", "Error reading processed packages journal", e);
        }
    }
}
