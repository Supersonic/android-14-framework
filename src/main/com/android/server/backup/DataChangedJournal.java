package com.android.server.backup;

import android.p005os.IInstalld;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class DataChangedJournal {
    public final File mFile;

    public DataChangedJournal(File file) {
        Objects.requireNonNull(file);
        this.mFile = file;
    }

    public void addPackage(String str) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(this.mFile, "rws");
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

    public void forEach(Consumer<String> consumer) throws IOException {
        try {
            try {
                while (true) {
                    consumer.accept(new DataInputStream(new BufferedInputStream(new FileInputStream(this.mFile), IInstalld.FLAG_FORCE)).readUTF());
                }
            } finally {
            }
        } catch (EOFException unused) {
        }
    }

    public boolean delete() {
        return this.mFile.delete();
    }

    public int hashCode() {
        return this.mFile.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof DataChangedJournal) {
            return this.mFile.equals(((DataChangedJournal) obj).mFile);
        }
        return false;
    }

    public String toString() {
        return this.mFile.toString();
    }

    public static DataChangedJournal newJournal(File file) throws IOException {
        Objects.requireNonNull(file);
        return new DataChangedJournal(File.createTempFile("journal", null, file));
    }

    public static ArrayList<DataChangedJournal> listJournals(File file) {
        ArrayList<DataChangedJournal> arrayList = new ArrayList<>();
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            Slog.w("DataChangedJournal", "Failed to read journal files");
            return arrayList;
        }
        for (File file2 : listFiles) {
            arrayList.add(new DataChangedJournal(file2));
        }
        return arrayList;
    }
}
