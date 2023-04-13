package com.android.commands.incident;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
/* loaded from: classes.dex */
public interface Section {
    void run(InputStream inputStream, OutputStream outputStream, List<String> list) throws ExecutionException;
}
