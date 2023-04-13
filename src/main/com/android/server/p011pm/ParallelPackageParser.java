package com.android.server.p011pm;

import android.os.Trace;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ConcurrentUtils;
import com.android.server.p011pm.parsing.PackageParser2;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
/* renamed from: com.android.server.pm.ParallelPackageParser */
/* loaded from: classes2.dex */
public class ParallelPackageParser {
    public final ExecutorService mExecutorService;
    public volatile String mInterruptedInThread;
    public final PackageParser2 mPackageParser;
    public final BlockingQueue<ParseResult> mQueue = new ArrayBlockingQueue(30);

    public static ExecutorService makeExecutorService() {
        return ConcurrentUtils.newFixedThreadPool(4, "package-parsing-thread", -2);
    }

    public ParallelPackageParser(PackageParser2 packageParser2, ExecutorService executorService) {
        this.mPackageParser = packageParser2;
        this.mExecutorService = executorService;
    }

    /* renamed from: com.android.server.pm.ParallelPackageParser$ParseResult */
    /* loaded from: classes2.dex */
    public static class ParseResult {
        public ParsedPackage parsedPackage;
        public File scanFile;
        public Throwable throwable;

        public String toString() {
            return "ParseResult{parsedPackage=" + this.parsedPackage + ", scanFile=" + this.scanFile + ", throwable=" + this.throwable + '}';
        }
    }

    public ParseResult take() {
        try {
            if (this.mInterruptedInThread != null) {
                throw new InterruptedException("Interrupted in " + this.mInterruptedInThread);
            }
            return this.mQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    public void submit(final File file, final int i) {
        this.mExecutorService.submit(new Runnable() { // from class: com.android.server.pm.ParallelPackageParser$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ParallelPackageParser.this.lambda$submit$0(file, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$submit$0(File file, int i) {
        ParseResult parseResult = new ParseResult();
        Trace.traceBegin(262144L, "parallel parsePackage [" + file + "]");
        try {
            parseResult.scanFile = file;
            parseResult.parsedPackage = parsePackage(file, i);
        } finally {
            try {
                this.mQueue.put(parseResult);
            } finally {
            }
        }
        try {
            this.mQueue.put(parseResult);
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
            this.mInterruptedInThread = Thread.currentThread().getName();
        }
    }

    @VisibleForTesting
    public ParsedPackage parsePackage(File file, int i) throws PackageManagerException {
        return this.mPackageParser.parsePackage(file, i, true);
    }
}
