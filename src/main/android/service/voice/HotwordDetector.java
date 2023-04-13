package android.service.voice;

import android.annotation.SystemApi;
import android.graphics.FontListParser;
import android.media.AudioFormat;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.SharedMemory;
import android.service.voice.AlwaysOnHotwordDetector;
import android.util.AndroidException;
import java.io.PrintWriter;
@SystemApi
/* loaded from: classes3.dex */
public interface HotwordDetector {
    public static final int DETECTOR_TYPE_NORMAL = 0;
    public static final int DETECTOR_TYPE_TRUSTED_HOTWORD_DSP = 1;
    public static final int DETECTOR_TYPE_TRUSTED_HOTWORD_SOFTWARE = 2;
    public static final int DETECTOR_TYPE_VISUAL_QUERY_DETECTOR = 3;
    public static final long HOTWORD_DETECTOR_THROW_CHECKED_EXCEPTION = 226355112;

    boolean startRecognition() throws IllegalDetectorStateException;

    boolean startRecognition(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle) throws IllegalDetectorStateException;

    boolean stopRecognition() throws IllegalDetectorStateException;

    void updateState(PersistableBundle persistableBundle, SharedMemory sharedMemory) throws IllegalDetectorStateException;

    default void destroy() {
        throw new UnsupportedOperationException("Not implemented. Must override in a subclass.");
    }

    default boolean isUsingSandboxedDetectionService() {
        throw new UnsupportedOperationException("Not implemented. Must override in a subclass.");
    }

    static String detectorTypeToString(int detectorType) {
        switch (detectorType) {
            case 0:
                return FontListParser.STYLE_NORMAL;
            case 1:
                return "trusted_hotword_dsp";
            case 2:
                return "trusted_hotword_software";
            case 3:
                return "visual_query_detector";
            default:
                return Integer.toString(detectorType);
        }
    }

    default void dump(String prefix, PrintWriter pw) {
        throw new UnsupportedOperationException("Not implemented. Must override in a subclass.");
    }

    /* loaded from: classes3.dex */
    public interface Callback {
        void onDetected(AlwaysOnHotwordDetector.EventPayload eventPayload);

        @Deprecated
        void onError();

        void onHotwordDetectionServiceInitialized(int i);

        void onHotwordDetectionServiceRestarted();

        void onRecognitionPaused();

        void onRecognitionResumed();

        void onRejected(HotwordRejectedResult hotwordRejectedResult);

        default void onFailure(DetectorFailure detectorFailure) {
            onError();
        }
    }

    /* loaded from: classes3.dex */
    public static class IllegalDetectorStateException extends AndroidException {
        /* JADX INFO: Access modifiers changed from: package-private */
        public IllegalDetectorStateException(String message) {
            super(message);
        }
    }
}
