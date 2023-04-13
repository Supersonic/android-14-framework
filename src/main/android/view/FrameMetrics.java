package android.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class FrameMetrics {
    public static final int ANIMATION_DURATION = 2;
    public static final int COMMAND_ISSUE_DURATION = 6;
    public static final int DEADLINE = 13;
    public static final int DRAW_DURATION = 4;
    private static final int[] DURATIONS = {2, 5, 5, 6, 6, 7, 7, 8, 8, 12, 13, 14, 14, 15, 15, 20, 2, 16, 0, 0, 0, 0, 0, 0, 22, 19, 2, 9};
    public static final int FIRST_DRAW_FRAME = 9;
    public static final int GPU_DURATION = 12;
    public static final int INPUT_HANDLING_DURATION = 1;
    public static final int INTENDED_VSYNC_TIMESTAMP = 10;
    public static final int LAYOUT_MEASURE_DURATION = 3;
    public static final int SWAP_BUFFERS_DURATION = 7;
    public static final int SYNC_DURATION = 5;
    public static final int TOTAL_DURATION = 8;
    public static final int UNKNOWN_DELAY_DURATION = 0;
    public static final int VSYNC_TIMESTAMP = 11;
    public final long[] mTimingData;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Index {
        public static final int ANIMATION_START = 6;
        public static final int COMMAND_SUBMISSION_COMPLETED = 22;
        public static final int DEQUEUE_BUFFER_DURATION = 17;
        public static final int DISPLAY_PRESENT_TIME = 21;
        public static final int DRAW_START = 8;
        public static final int FLAGS = 0;
        public static final int FRAME_COMPLETED = 16;
        public static final int FRAME_DEADLINE = 9;
        public static final int FRAME_INTERVAL = 11;
        public static final int FRAME_START_TIME = 10;
        public static final int FRAME_STATS_COUNT = 23;
        public static final int FRAME_TIMELINE_VSYNC_ID = 1;
        public static final int GPU_COMPLETED = 19;
        public static final int HANDLE_INPUT_START = 5;
        public static final int INPUT_EVENT_ID = 4;
        public static final int INTENDED_VSYNC = 2;
        public static final int ISSUE_DRAW_COMMANDS_START = 14;
        public static final int PERFORM_TRAVERSALS_START = 7;
        public static final int QUEUE_BUFFER_DURATION = 18;
        public static final int SWAP_BUFFERS = 15;
        public static final int SWAP_BUFFERS_COMPLETED = 20;
        public static final int SYNC_QUEUED = 12;
        public static final int SYNC_START = 13;
        public static final int VSYNC = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Metric {
    }

    public FrameMetrics(FrameMetrics other) {
        long[] jArr = new long[23];
        this.mTimingData = jArr;
        System.arraycopy(other.mTimingData, 0, jArr, 0, jArr.length);
    }

    public FrameMetrics() {
        this.mTimingData = new long[23];
    }

    public long getMetric(int id) {
        long[] jArr;
        if (id < 0 || id > 13 || (jArr = this.mTimingData) == null) {
            return -1L;
        }
        if (id == 9) {
            return (jArr[0] & 1) != 0 ? 1L : 0L;
        } else if (id == 10) {
            return jArr[2];
        } else {
            if (id == 11) {
                return jArr[3];
            }
            int durationsIdx = id * 2;
            int[] iArr = DURATIONS;
            return jArr[iArr[durationsIdx + 1]] - jArr[iArr[durationsIdx]];
        }
    }
}
