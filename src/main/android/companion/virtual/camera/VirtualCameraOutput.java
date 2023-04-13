package android.companion.virtual.camera;

import android.companion.virtual.camera.VirtualCameraOutput;
import android.hardware.camera2.params.InputConfiguration;
import android.p008os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class VirtualCameraOutput {
    private static final String TAG = "VirtualCameraDeviceImpl";
    private VirtualCameraStream mCameraStream;
    private final Executor mExecutor;
    private final VirtualCameraInput mVirtualCameraInput;

    public VirtualCameraOutput(VirtualCameraInput cameraInput, Executor executor) {
        this.mVirtualCameraInput = (VirtualCameraInput) Objects.requireNonNull(cameraInput);
        this.mExecutor = (Executor) Objects.requireNonNull(executor);
    }

    public ParcelFileDescriptor getStreamDescriptor(InputConfiguration imageConfiguration) {
        Objects.requireNonNull(imageConfiguration);
        try {
            VirtualCameraStream virtualCameraStream = this.mCameraStream;
            if (virtualCameraStream == null) {
                this.mCameraStream = new VirtualCameraStream(imageConfiguration, this.mExecutor);
            } else if (!virtualCameraStream.isSameConfiguration(imageConfiguration)) {
                this.mCameraStream.close();
                this.mCameraStream = new VirtualCameraStream(imageConfiguration, this.mExecutor);
            }
            InputStream imageStream = this.mVirtualCameraInput.openStream(imageConfiguration);
            this.mCameraStream.startSending(imageStream);
            return this.mCameraStream.getDescriptor();
        } catch (IOException exception) {
            Log.m109e(TAG, "Unable to open file descriptor.", exception);
            return null;
        }
    }

    public void closeStream() {
        this.mVirtualCameraInput.closeStream();
        VirtualCameraStream virtualCameraStream = this.mCameraStream;
        if (virtualCameraStream != null) {
            virtualCameraStream.close();
            this.mCameraStream = null;
        }
        try {
            this.mVirtualCameraInput.closeStream();
        } catch (Exception e) {
            Log.m109e(TAG, "Error during closing stream.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VirtualCameraStream implements AutoCloseable {
        private static final int BUFFER_SIZE = 1024;
        private static final int SENDING_STATE_CLOSED = 2;
        private static final int SENDING_STATE_INITIAL = 0;
        private static final int SENDING_STATE_IN_PROGRESS = 1;
        private static final String TAG = "VirtualCameraStream";
        private final Executor mExecutor;
        private final InputConfiguration mImageConfiguration;
        private final ParcelFileDescriptor mReadDescriptor;
        private int mSendingState = 0;
        private final ParcelFileDescriptor mWriteDescriptor;

        VirtualCameraStream(InputConfiguration imageConfiguration, Executor executor) throws IOException {
            this.mImageConfiguration = (InputConfiguration) Objects.requireNonNull(imageConfiguration);
            this.mExecutor = (Executor) Objects.requireNonNull(executor);
            ParcelFileDescriptor[] parcels = ParcelFileDescriptor.createPipe();
            this.mReadDescriptor = parcels[0];
            this.mWriteDescriptor = parcels[1];
        }

        boolean isSameConfiguration(InputConfiguration imageConfiguration) {
            return this.mImageConfiguration == Objects.requireNonNull(imageConfiguration);
        }

        ParcelFileDescriptor getDescriptor() {
            return this.mReadDescriptor;
        }

        public void startSending(final InputStream inputStream) {
            Objects.requireNonNull(inputStream);
            if (this.mSendingState != 0) {
                return;
            }
            this.mSendingState = 1;
            this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.camera.VirtualCameraOutput$VirtualCameraStream$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VirtualCameraOutput.VirtualCameraStream.this.lambda$startSending$0(inputStream);
                }
            });
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.mSendingState = 2;
            try {
                this.mReadDescriptor.close();
            } catch (IOException e) {
                Log.m109e(TAG, "Unable to close read descriptor.", e);
            }
            try {
                this.mWriteDescriptor.close();
            } catch (IOException e2) {
                Log.m109e(TAG, "Unable to close write descriptor.", e2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: sendData */
        public void lambda$startSending$0(InputStream inputStream) {
            Objects.requireNonNull(inputStream);
            byte[] buffer = new byte[1024];
            FileDescriptor fd = this.mWriteDescriptor.getFileDescriptor();
            try {
                FileOutputStream outputStream = new FileOutputStream(fd);
                while (this.mSendingState == 1) {
                    int bytesRead = inputStream.read(buffer, 0, 1024);
                    if (bytesRead >= 1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                outputStream.close();
            } catch (IOException e) {
                Log.m109e(TAG, "Error while sending camera data.", e);
            }
        }
    }
}
