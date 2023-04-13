package com.android.internal.util;

import java.io.File;
import java.io.IOException;
@Deprecated
/* loaded from: classes3.dex */
public class JournaledFile {
    File mReal;
    File mTemp;
    boolean mWriting;

    public JournaledFile(File real, File temp) {
        this.mReal = real;
        this.mTemp = temp;
    }

    public File chooseForRead() {
        if (this.mReal.exists()) {
            File result = this.mReal;
            if (this.mTemp.exists()) {
                this.mTemp.delete();
                return result;
            }
            return result;
        }
        File result2 = this.mTemp;
        if (result2.exists()) {
            File result3 = this.mTemp;
            this.mTemp.renameTo(this.mReal);
            return result3;
        }
        File result4 = this.mReal;
        return result4;
    }

    public File chooseForWrite() {
        if (this.mWriting) {
            throw new IllegalStateException("uncommitted write already in progress");
        }
        if (!this.mReal.exists()) {
            try {
                this.mReal.createNewFile();
            } catch (IOException e) {
            }
        }
        if (this.mTemp.exists()) {
            this.mTemp.delete();
        }
        this.mWriting = true;
        return this.mTemp;
    }

    public void commit() {
        if (!this.mWriting) {
            throw new IllegalStateException("no file to commit");
        }
        this.mWriting = false;
        this.mTemp.renameTo(this.mReal);
    }

    public void rollback() {
        if (!this.mWriting) {
            throw new IllegalStateException("no file to roll back");
        }
        this.mWriting = false;
        this.mTemp.delete();
    }
}
