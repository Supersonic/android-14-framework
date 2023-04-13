package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.hardware.hdmi.HdmiRecordSources;
@SystemApi
/* loaded from: classes2.dex */
public abstract class HdmiRecordListener {
    public abstract HdmiRecordSources.RecordSource onOneTouchRecordSourceRequested(int i);

    public void onOneTouchRecordResult(int recorderAddress, int result) {
    }

    public void onTimerRecordingResult(int recorderAddress, TimerStatusData data) {
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static class TimerStatusData {
        private int mDurationHour;
        private int mDurationMinute;
        private int mExtraError;
        private int mMediaInfo;
        private int mNotProgrammedError;
        private boolean mOverlapped;
        private boolean mProgrammed;
        private int mProgrammedInfo;

        /* JADX INFO: Access modifiers changed from: package-private */
        public static TimerStatusData parseFrom(int result) {
            TimerStatusData data = new TimerStatusData();
            data.mOverlapped = ((result >> 31) & 1) != 0;
            data.mMediaInfo = (result >> 29) & 3;
            boolean z = ((result >> 28) & 1) != 0;
            data.mProgrammed = z;
            if (z) {
                data.mProgrammedInfo = (result >> 24) & 15;
                data.mDurationHour = bcdByteToInt((byte) ((result >> 16) & 255));
                data.mDurationMinute = bcdByteToInt((byte) ((result >> 8) & 255));
            } else {
                data.mNotProgrammedError = (result >> 24) & 15;
                data.mDurationHour = bcdByteToInt((byte) ((result >> 16) & 255));
                data.mDurationMinute = bcdByteToInt((byte) ((result >> 8) & 255));
            }
            data.mExtraError = result & 255;
            return data;
        }

        private static int bcdByteToInt(byte value) {
            return ((((value >> 4) & 15) * 10) + value) & 15;
        }

        private TimerStatusData() {
        }

        public boolean isOverlapped() {
            return this.mOverlapped;
        }

        public int getMediaInfo() {
            return this.mMediaInfo;
        }

        public boolean isProgrammed() {
            return this.mProgrammed;
        }

        public int getProgrammedInfo() {
            if (!isProgrammed()) {
                throw new IllegalStateException("No programmed info. Call getNotProgammedError() instead.");
            }
            return this.mProgrammedInfo;
        }

        public int getNotProgammedError() {
            if (isProgrammed()) {
                throw new IllegalStateException("Has no not-programmed error. Call getProgrammedInfo() instead.");
            }
            return this.mNotProgrammedError;
        }

        public int getDurationHour() {
            return this.mDurationHour;
        }

        public int getDurationMinute() {
            return this.mDurationMinute;
        }

        public int getExtraError() {
            return this.mExtraError;
        }
    }

    public void onClearTimerRecordingResult(int recorderAddress, int result) {
    }
}
