package com.android.server.apphibernation;

import android.util.AtomicFile;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class HibernationStateDiskStore<T> {
    public final ScheduledExecutorService mExecutorService;
    public ScheduledFuture<?> mFuture;
    public final File mHibernationFile;
    public final ProtoReadWriter<List<T>> mProtoReadWriter;
    public List<T> mScheduledStatesToWrite;

    public HibernationStateDiskStore(File file, ProtoReadWriter<List<T>> protoReadWriter, ScheduledExecutorService scheduledExecutorService) {
        this(file, protoReadWriter, scheduledExecutorService, "states");
    }

    @VisibleForTesting
    public HibernationStateDiskStore(File file, ProtoReadWriter<List<T>> protoReadWriter, ScheduledExecutorService scheduledExecutorService, String str) {
        this.mScheduledStatesToWrite = new ArrayList();
        this.mHibernationFile = new File(file, str);
        this.mExecutorService = scheduledExecutorService;
        this.mProtoReadWriter = protoReadWriter;
    }

    public void scheduleWriteHibernationStates(List<T> list) {
        synchronized (this) {
            this.mScheduledStatesToWrite = list;
            if (this.mExecutorService.isShutdown()) {
                Slog.e("HibernationStateDiskStore", "Scheduled executor service is shut down.");
            } else if (this.mFuture != null) {
                Slog.i("HibernationStateDiskStore", "Write already scheduled. Skipping schedule.");
            } else {
                this.mFuture = this.mExecutorService.schedule(new Runnable() { // from class: com.android.server.apphibernation.HibernationStateDiskStore$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        HibernationStateDiskStore.this.writeHibernationStates();
                    }
                }, 60000L, TimeUnit.MILLISECONDS);
            }
        }
    }

    public List<T> readHibernationStates() {
        synchronized (this) {
            if (!this.mHibernationFile.exists()) {
                Slog.i("HibernationStateDiskStore", "No hibernation file on disk for file " + this.mHibernationFile.getPath());
                return null;
            }
            try {
                return this.mProtoReadWriter.readFromProto(new ProtoInputStream(new AtomicFile(this.mHibernationFile).openRead()));
            } catch (IOException e) {
                Slog.e("HibernationStateDiskStore", "Failed to read states protobuf.", e);
                return null;
            }
        }
    }

    public final void writeHibernationStates() {
        synchronized (this) {
            writeStateProto(this.mScheduledStatesToWrite);
            this.mScheduledStatesToWrite.clear();
            this.mFuture = null;
        }
    }

    public final void writeStateProto(List<T> list) {
        AtomicFile atomicFile = new AtomicFile(this.mHibernationFile);
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                ProtoOutputStream protoOutputStream = new ProtoOutputStream(startWrite);
                this.mProtoReadWriter.writeToProto(protoOutputStream, list);
                protoOutputStream.flush();
                atomicFile.finishWrite(startWrite);
            } catch (Exception e) {
                Slog.e("HibernationStateDiskStore", "Failed to finish write to states protobuf.", e);
                atomicFile.failWrite(startWrite);
            }
        } catch (IOException e2) {
            Slog.e("HibernationStateDiskStore", "Failed to start write to states protobuf.", e2);
        }
    }
}
