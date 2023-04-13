package com.android.server.p014wm;

import android.content.Context;
import android.os.Environment;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p014wm.PersisterQueue;
import com.android.server.wm.nano.WindowManagerProtos;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.LetterboxConfigurationPersister */
/* loaded from: classes2.dex */
public class LetterboxConfigurationPersister {
    @VisibleForTesting
    static final String LETTERBOX_CONFIGURATION_FILENAME = "letterbox_config";
    public final Consumer<String> mCompletionCallback;
    public final AtomicFile mConfigurationFile;
    public final Context mContext;
    public final Supplier<Integer> mDefaultBookModeReachabilitySupplier;
    public final Supplier<Integer> mDefaultHorizontalReachabilitySupplier;
    public final Supplier<Integer> mDefaultTabletopModeReachabilitySupplier;
    public final Supplier<Integer> mDefaultVerticalReachabilitySupplier;
    public volatile int mLetterboxPositionForBookModeReachability;
    public volatile int mLetterboxPositionForHorizontalReachability;
    public volatile int mLetterboxPositionForTabletopModeReachability;
    public volatile int mLetterboxPositionForVerticalReachability;
    public final PersisterQueue mPersisterQueue;

    public LetterboxConfigurationPersister(Context context, Supplier<Integer> supplier, Supplier<Integer> supplier2, Supplier<Integer> supplier3, Supplier<Integer> supplier4) {
        this(context, supplier, supplier2, supplier3, supplier4, Environment.getDataSystemDirectory(), new PersisterQueue(), null);
    }

    @VisibleForTesting
    public LetterboxConfigurationPersister(Context context, Supplier<Integer> supplier, Supplier<Integer> supplier2, Supplier<Integer> supplier3, Supplier<Integer> supplier4, File file, PersisterQueue persisterQueue, Consumer<String> consumer) {
        this.mContext = context.createDeviceProtectedStorageContext();
        this.mDefaultHorizontalReachabilitySupplier = supplier;
        this.mDefaultVerticalReachabilitySupplier = supplier2;
        this.mDefaultBookModeReachabilitySupplier = supplier3;
        this.mDefaultTabletopModeReachabilitySupplier = supplier4;
        this.mCompletionCallback = consumer;
        this.mConfigurationFile = new AtomicFile(new File(file, LETTERBOX_CONFIGURATION_FILENAME));
        this.mPersisterQueue = persisterQueue;
        readCurrentConfiguration();
    }

    public void start() {
        this.mPersisterQueue.startPersisting();
    }

    public int getLetterboxPositionForHorizontalReachability(boolean z) {
        if (z) {
            return this.mLetterboxPositionForBookModeReachability;
        }
        return this.mLetterboxPositionForHorizontalReachability;
    }

    public int getLetterboxPositionForVerticalReachability(boolean z) {
        if (z) {
            return this.mLetterboxPositionForTabletopModeReachability;
        }
        return this.mLetterboxPositionForVerticalReachability;
    }

    public void setLetterboxPositionForHorizontalReachability(boolean z, int i) {
        if (z) {
            if (this.mLetterboxPositionForBookModeReachability != i) {
                this.mLetterboxPositionForBookModeReachability = i;
                updateConfiguration();
            }
        } else if (this.mLetterboxPositionForHorizontalReachability != i) {
            this.mLetterboxPositionForHorizontalReachability = i;
            updateConfiguration();
        }
    }

    public void setLetterboxPositionForVerticalReachability(boolean z, int i) {
        if (z) {
            if (this.mLetterboxPositionForTabletopModeReachability != i) {
                this.mLetterboxPositionForTabletopModeReachability = i;
                updateConfiguration();
            }
        } else if (this.mLetterboxPositionForVerticalReachability != i) {
            this.mLetterboxPositionForVerticalReachability = i;
            updateConfiguration();
        }
    }

    @VisibleForTesting
    public void useDefaultValue() {
        this.mLetterboxPositionForHorizontalReachability = this.mDefaultHorizontalReachabilitySupplier.get().intValue();
        this.mLetterboxPositionForVerticalReachability = this.mDefaultVerticalReachabilitySupplier.get().intValue();
        this.mLetterboxPositionForBookModeReachability = this.mDefaultBookModeReachabilitySupplier.get().intValue();
        this.mLetterboxPositionForTabletopModeReachability = this.mDefaultTabletopModeReachabilitySupplier.get().intValue();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x0047 -> B:29:0x004d). Please submit an issue!!! */
    public final void readCurrentConfiguration() {
        if (!this.mConfigurationFile.exists()) {
            useDefaultValue();
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    fileInputStream = this.mConfigurationFile.openRead();
                    WindowManagerProtos.LetterboxProto parseFrom = WindowManagerProtos.LetterboxProto.parseFrom(readInputStream(fileInputStream));
                    this.mLetterboxPositionForHorizontalReachability = parseFrom.letterboxPositionForHorizontalReachability;
                    this.mLetterboxPositionForVerticalReachability = parseFrom.letterboxPositionForVerticalReachability;
                    this.mLetterboxPositionForBookModeReachability = parseFrom.letterboxPositionForBookModeReachability;
                    this.mLetterboxPositionForTabletopModeReachability = parseFrom.letterboxPositionForTabletopModeReachability;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    Slog.e(StartingSurfaceController.TAG, "Error reading from LetterboxConfigurationPersister. Using default values!", e);
                    useDefaultValue();
                    if (fileInputStream == null) {
                        return;
                    }
                    fileInputStream.close();
                }
            } catch (IOException e2) {
                useDefaultValue();
                Slog.e(StartingSurfaceController.TAG, "Error reading from LetterboxConfigurationPersister ", e2);
            }
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e3) {
                    useDefaultValue();
                    Slog.e(StartingSurfaceController.TAG, "Error reading from LetterboxConfigurationPersister ", e3);
                }
            }
            throw th;
        }
    }

    public final void updateConfiguration() {
        this.mPersisterQueue.addItem(new UpdateValuesCommand(this.mConfigurationFile, this.mLetterboxPositionForHorizontalReachability, this.mLetterboxPositionForVerticalReachability, this.mLetterboxPositionForBookModeReachability, this.mLetterboxPositionForTabletopModeReachability, this.mCompletionCallback), true);
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bArr = new byte[1024];
            int read = inputStream.read(bArr);
            while (read > 0) {
                byteArrayOutputStream.write(bArr, 0, read);
                read = inputStream.read(bArr);
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.close();
        }
    }

    /* renamed from: com.android.server.wm.LetterboxConfigurationPersister$UpdateValuesCommand */
    /* loaded from: classes2.dex */
    public static class UpdateValuesCommand implements PersisterQueue.WriteQueueItem<UpdateValuesCommand> {
        public final int mBookModeReachability;
        public final AtomicFile mFileToUpdate;
        public final int mHorizontalReachability;
        public final Consumer<String> mOnComplete;
        public final int mTabletopModeReachability;
        public final int mVerticalReachability;

        public UpdateValuesCommand(AtomicFile atomicFile, int i, int i2, int i3, int i4, Consumer<String> consumer) {
            this.mFileToUpdate = atomicFile;
            this.mHorizontalReachability = i;
            this.mVerticalReachability = i2;
            this.mBookModeReachability = i3;
            this.mTabletopModeReachability = i4;
            this.mOnComplete = consumer;
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void process() {
            Consumer<String> consumer;
            WindowManagerProtos.LetterboxProto letterboxProto = new WindowManagerProtos.LetterboxProto();
            letterboxProto.letterboxPositionForHorizontalReachability = this.mHorizontalReachability;
            letterboxProto.letterboxPositionForVerticalReachability = this.mVerticalReachability;
            letterboxProto.letterboxPositionForBookModeReachability = this.mBookModeReachability;
            letterboxProto.letterboxPositionForTabletopModeReachability = this.mTabletopModeReachability;
            byte[] byteArray = WindowManagerProtos.LetterboxProto.toByteArray(letterboxProto);
            FileOutputStream fileOutputStream = null;
            try {
                try {
                    fileOutputStream = this.mFileToUpdate.startWrite();
                    fileOutputStream.write(byteArray);
                    this.mFileToUpdate.finishWrite(fileOutputStream);
                    consumer = this.mOnComplete;
                    if (consumer == null) {
                        return;
                    }
                } catch (IOException e) {
                    this.mFileToUpdate.failWrite(fileOutputStream);
                    Slog.e(StartingSurfaceController.TAG, "Error writing to LetterboxConfigurationPersister. Using default values!", e);
                    consumer = this.mOnComplete;
                    if (consumer == null) {
                        return;
                    }
                }
                consumer.accept("UpdateValuesCommand");
            } catch (Throwable th) {
                Consumer<String> consumer2 = this.mOnComplete;
                if (consumer2 != null) {
                    consumer2.accept("UpdateValuesCommand");
                }
                throw th;
            }
        }
    }
}
