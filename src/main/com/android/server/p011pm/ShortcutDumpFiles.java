package com.android.server.p011pm;

import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
/* renamed from: com.android.server.pm.ShortcutDumpFiles */
/* loaded from: classes2.dex */
public class ShortcutDumpFiles {
    public final ShortcutService mService;

    public ShortcutDumpFiles(ShortcutService shortcutService) {
        this.mService = shortcutService;
    }

    public boolean save(String str, Consumer<PrintWriter> consumer) {
        try {
            File dumpPath = this.mService.getDumpPath();
            dumpPath.mkdirs();
            if (!dumpPath.exists()) {
                Slog.e("ShortcutService", "Failed to create directory: " + dumpPath);
                return false;
            }
            PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(dumpPath, str))));
            consumer.accept(printWriter);
            printWriter.close();
            return true;
        } catch (IOException | RuntimeException e) {
            Slog.w("ShortcutService", "Failed to create dump file: " + str, e);
            return false;
        }
    }

    public static /* synthetic */ void lambda$save$0(byte[] bArr, PrintWriter printWriter) {
        printWriter.println(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bArr)).toString());
    }

    public boolean save(String str, final byte[] bArr) {
        return save(str, new Consumer() { // from class: com.android.server.pm.ShortcutDumpFiles$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ShortcutDumpFiles.lambda$save$0(bArr, (PrintWriter) obj);
            }
        });
    }

    public void dumpAll(PrintWriter printWriter) {
        try {
            File dumpPath = this.mService.getDumpPath();
            File[] listFiles = dumpPath.listFiles(new FileFilter() { // from class: com.android.server.pm.ShortcutDumpFiles$$ExternalSyntheticLambda1
                @Override // java.io.FileFilter
                public final boolean accept(File file) {
                    boolean isFile;
                    isFile = file.isFile();
                    return isFile;
                }
            });
            if (dumpPath.exists() && !ArrayUtils.isEmpty(listFiles)) {
                Arrays.sort(listFiles, Comparator.comparing(new Function() { // from class: com.android.server.pm.ShortcutDumpFiles$$ExternalSyntheticLambda2
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        String name;
                        name = ((File) obj).getName();
                        return name;
                    }
                }));
                for (File file : listFiles) {
                    printWriter.print("*** Dumping: ");
                    printWriter.println(file.getName());
                    printWriter.print("mtime: ");
                    printWriter.println(ShortcutService.formatTime(file.lastModified()));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        printWriter.println(readLine);
                    }
                    bufferedReader.close();
                }
                return;
            }
            printWriter.print("  No dump files found.");
        } catch (IOException | RuntimeException e) {
            Slog.w("ShortcutService", "Failed to print dump files", e);
        }
    }
}
