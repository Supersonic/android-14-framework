package com.android.location.provider;
/* loaded from: classes.dex */
public class ActivityRecognitionEvent {
    private final String mActivity;
    private final int mEventType;
    private final long mTimestampNs;

    public ActivityRecognitionEvent(String activity, int eventType, long timestampNs) {
        this.mActivity = activity;
        this.mEventType = eventType;
        this.mTimestampNs = timestampNs;
    }

    public String getActivity() {
        return this.mActivity;
    }

    public int getEventType() {
        return this.mEventType;
    }

    public long getTimestampNs() {
        return this.mTimestampNs;
    }

    public String toString() {
        String eventString;
        int i = this.mEventType;
        switch (i) {
            case 0:
                eventString = "FlushComplete";
                break;
            case ActivityRecognitionProvider.EVENT_TYPE_ENTER /* 1 */:
                eventString = "Enter";
                break;
            case ActivityRecognitionProvider.EVENT_TYPE_EXIT /* 2 */:
                eventString = "Exit";
                break;
            default:
                eventString = "<Invalid>";
                break;
        }
        return String.format("Activity='%s', EventType=%s(%s), TimestampNs=%s", this.mActivity, eventString, Integer.valueOf(i), Long.valueOf(this.mTimestampNs));
    }
}
