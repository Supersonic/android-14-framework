package com.android.server.devicepolicy;

import android.os.Environment;
import java.io.File;
/* loaded from: classes.dex */
public interface PolicyPathProvider {
    default File getDataSystemDirectory() {
        return Environment.getDataSystemDirectory();
    }

    default File getUserSystemDirectory(int i) {
        return Environment.getUserSystemDirectory(i);
    }
}
