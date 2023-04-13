package com.android.internal.p028os;

import dalvik.system.ZipPathValidator;
import java.io.File;
import java.util.zip.ZipException;
/* renamed from: com.android.internal.os.SafeZipPathValidatorCallback */
/* loaded from: classes4.dex */
public class SafeZipPathValidatorCallback implements ZipPathValidator.Callback {
    public static final long VALIDATE_ZIP_PATH_FOR_PATH_TRAVERSAL = 242716250;

    public void onZipEntryAccess(String path) throws ZipException {
        if (path.startsWith("/")) {
            throw new ZipException("Invalid zip entry path: " + path);
        }
        if (path.contains("..")) {
            for (File file = new File(path); file != null; file = file.getParentFile()) {
                if (file.getName().equals("..")) {
                    throw new ZipException("Invalid zip entry path: " + path);
                }
            }
        }
    }
}
