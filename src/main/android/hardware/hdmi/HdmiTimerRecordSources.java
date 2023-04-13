package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.hardware.hdmi.HdmiRecordSources;
import android.util.Log;
@SystemApi
/* loaded from: classes2.dex */
public class HdmiTimerRecordSources {
    private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PHYSICAL_ADDRESS = 5;
    private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PLUG = 4;
    public static final int RECORDING_SEQUENCE_REPEAT_FRIDAY = 32;
    private static final int RECORDING_SEQUENCE_REPEAT_MASK = 127;
    public static final int RECORDING_SEQUENCE_REPEAT_MONDAY = 2;
    public static final int RECORDING_SEQUENCE_REPEAT_ONCE_ONLY = 0;
    public static final int RECORDING_SEQUENCE_REPEAT_SATUREDAY = 64;
    public static final int RECORDING_SEQUENCE_REPEAT_SUNDAY = 1;
    public static final int RECORDING_SEQUENCE_REPEAT_THURSDAY = 16;
    public static final int RECORDING_SEQUENCE_REPEAT_TUESDAY = 4;
    public static final int RECORDING_SEQUENCE_REPEAT_WEDNESDAY = 8;
    private static final String TAG = "HdmiTimerRecordingSources";

    private HdmiTimerRecordSources() {
    }

    public static TimerRecordSource ofDigitalSource(TimerInfo timerInfo, HdmiRecordSources.DigitalServiceSource source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, source);
    }

    public static TimerRecordSource ofAnalogueSource(TimerInfo timerInfo, HdmiRecordSources.AnalogueServiceSource source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, source);
    }

    public static TimerRecordSource ofExternalPlug(TimerInfo timerInfo, HdmiRecordSources.ExternalPlugData source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, new ExternalSourceDecorator(source, 4));
    }

    public static TimerRecordSource ofExternalPhysicalAddress(TimerInfo timerInfo, HdmiRecordSources.ExternalPhysicalAddress source) {
        checkTimerRecordSourceInputs(timerInfo, source);
        return new TimerRecordSource(timerInfo, new ExternalSourceDecorator(source, 5));
    }

    private static void checkTimerRecordSourceInputs(TimerInfo timerInfo, HdmiRecordSources.RecordSource source) {
        if (timerInfo == null) {
            Log.m104w(TAG, "TimerInfo should not be null.");
            throw new IllegalArgumentException("TimerInfo should not be null.");
        } else if (source == null) {
            Log.m104w(TAG, "source should not be null.");
            throw new IllegalArgumentException("source should not be null.");
        }
    }

    public static Time timeOf(int hour, int minute) {
        checkTimeValue(hour, minute);
        return new Time(hour, minute);
    }

    private static void checkTimeValue(int hour, int minute) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour should be in rage of [0, 23]:" + hour);
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Minute should be in rage of [0, 59]:" + minute);
        }
    }

    public static Duration durationOf(int hour, int minute) {
        checkDurationValue(hour, minute);
        return new Duration(hour, minute);
    }

    private static void checkDurationValue(int hour, int minute) {
        if (hour < 0 || hour > 99) {
            throw new IllegalArgumentException("Hour should be in rage of [0, 99]:" + hour);
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("minute should be in rage of [0, 59]:" + minute);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class TimeUnit {
        final int mHour;
        final int mMinute;

        TimeUnit(int hour, int minute) {
            this.mHour = hour;
            this.mMinute = minute;
        }

        int toByteArray(byte[] data, int index) {
            data[index] = toBcdByte(this.mHour);
            data[index + 1] = toBcdByte(this.mMinute);
            return 2;
        }

        static byte toBcdByte(int value) {
            int digitOfTen = (value / 10) % 10;
            int digitOfOne = value % 10;
            return (byte) ((digitOfTen << 4) | digitOfOne);
        }
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class Time extends TimeUnit {
        private Time(int hour, int minute) {
            super(hour, minute);
        }
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class Duration extends TimeUnit {
        private Duration(int hour, int minute) {
            super(hour, minute);
        }
    }

    public static TimerInfo timerInfoOf(int dayOfMonth, int monthOfYear, Time startTime, Duration duration, int recordingSequence) {
        if (dayOfMonth < 0 || dayOfMonth > 31) {
            throw new IllegalArgumentException("Day of month should be in range of [0, 31]:" + dayOfMonth);
        }
        if (monthOfYear < 1 || monthOfYear > 12) {
            throw new IllegalArgumentException("Month of year should be in range of [1, 12]:" + monthOfYear);
        }
        checkTimeValue(startTime.mHour, startTime.mMinute);
        checkDurationValue(duration.mHour, duration.mMinute);
        if (recordingSequence != 0 && (recordingSequence & (-128)) != 0) {
            throw new IllegalArgumentException("Invalid reecording sequence value:" + recordingSequence);
        }
        return new TimerInfo(dayOfMonth, monthOfYear, startTime, duration, recordingSequence);
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class TimerInfo {
        private static final int BASIC_INFO_SIZE = 7;
        private static final int DAY_OF_MONTH_SIZE = 1;
        private static final int DURATION_SIZE = 2;
        private static final int MONTH_OF_YEAR_SIZE = 1;
        private static final int RECORDING_SEQUENCE_SIZE = 1;
        private static final int START_TIME_SIZE = 2;
        private final int mDayOfMonth;
        private final Duration mDuration;
        private final int mMonthOfYear;
        private final int mRecordingSequence;
        private final Time mStartTime;

        private TimerInfo(int dayOfMonth, int monthOfYear, Time startTime, Duration duration, int recordingSequence) {
            this.mDayOfMonth = dayOfMonth;
            this.mMonthOfYear = monthOfYear;
            this.mStartTime = startTime;
            this.mDuration = duration;
            this.mRecordingSequence = recordingSequence;
        }

        int toByteArray(byte[] data, int index) {
            data[index] = (byte) this.mDayOfMonth;
            int index2 = index + 1;
            data[index2] = (byte) this.mMonthOfYear;
            int index3 = index2 + 1;
            int index4 = index3 + this.mStartTime.toByteArray(data, index3);
            data[index4 + this.mDuration.toByteArray(data, index4)] = (byte) this.mRecordingSequence;
            return getDataSize();
        }

        int getDataSize() {
            return 7;
        }
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class TimerRecordSource {
        private final HdmiRecordSources.RecordSource mRecordSource;
        private final TimerInfo mTimerInfo;

        private TimerRecordSource(TimerInfo timerInfo, HdmiRecordSources.RecordSource recordSource) {
            this.mTimerInfo = timerInfo;
            this.mRecordSource = recordSource;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getDataSize() {
            return this.mTimerInfo.getDataSize() + this.mRecordSource.getDataSize(false);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int toByteArray(byte[] data, int index) {
            this.mRecordSource.toByteArray(false, data, index + this.mTimerInfo.toByteArray(data, index));
            return getDataSize();
        }
    }

    /* loaded from: classes2.dex */
    private static class ExternalSourceDecorator extends HdmiRecordSources.RecordSource {
        private final int mExternalSourceSpecifier;
        private final HdmiRecordSources.RecordSource mRecordSource;

        private ExternalSourceDecorator(HdmiRecordSources.RecordSource recordSource, int externalSourceSpecifier) {
            super(recordSource.mSourceType, recordSource.getDataSize(false) + 1);
            this.mRecordSource = recordSource;
            this.mExternalSourceSpecifier = externalSourceSpecifier;
        }

        @Override // android.hardware.hdmi.HdmiRecordSources.RecordSource
        int extraParamToByteArray(byte[] data, int index) {
            data[index] = (byte) this.mExternalSourceSpecifier;
            this.mRecordSource.toByteArray(false, data, index + 1);
            return getDataSize(false);
        }
    }

    @SystemApi
    public static boolean checkTimerRecordSource(int sourcetype, byte[] recordSource) {
        int recordSourceSize = recordSource.length - 7;
        switch (sourcetype) {
            case 1:
                return 7 == recordSourceSize;
            case 2:
                return 4 == recordSourceSize;
            case 3:
                int specifier = recordSource[7];
                return specifier == 4 ? 2 == recordSourceSize : specifier == 5 && 3 == recordSourceSize;
            default:
                return false;
        }
    }
}
