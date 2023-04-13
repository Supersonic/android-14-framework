package com.android.server.people.data;

import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public class Event {
    public static final Set<Integer> ALL_EVENT_TYPES;
    public static final Set<Integer> CALL_EVENT_TYPES;
    public static final Set<Integer> NOTIFICATION_EVENT_TYPES;
    public static final Set<Integer> SHARE_EVENT_TYPES;
    public static final Set<Integer> SMS_EVENT_TYPES;
    public static final String TAG = "Event";
    public final int mDurationSeconds;
    public final long mTimestamp;
    public final int mType;

    static {
        ArraySet arraySet = new ArraySet();
        NOTIFICATION_EVENT_TYPES = arraySet;
        ArraySet arraySet2 = new ArraySet();
        SHARE_EVENT_TYPES = arraySet2;
        ArraySet arraySet3 = new ArraySet();
        SMS_EVENT_TYPES = arraySet3;
        ArraySet arraySet4 = new ArraySet();
        CALL_EVENT_TYPES = arraySet4;
        ArraySet arraySet5 = new ArraySet();
        ALL_EVENT_TYPES = arraySet5;
        arraySet.add(2);
        arraySet.add(3);
        arraySet2.add(4);
        arraySet2.add(5);
        arraySet2.add(6);
        arraySet2.add(7);
        arraySet3.add(9);
        arraySet3.add(8);
        arraySet4.add(11);
        arraySet4.add(10);
        arraySet4.add(12);
        arraySet5.add(1);
        arraySet5.add(13);
        arraySet5.addAll((Collection) arraySet);
        arraySet5.addAll((Collection) arraySet2);
        arraySet5.addAll((Collection) arraySet3);
        arraySet5.addAll((Collection) arraySet4);
    }

    public Event(long j, int i) {
        this.mTimestamp = j;
        this.mType = i;
        this.mDurationSeconds = 0;
    }

    public Event(Builder builder) {
        this.mTimestamp = builder.mTimestamp;
        this.mType = builder.mType;
        this.mDurationSeconds = builder.mDurationSeconds;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public int getType() {
        return this.mType;
    }

    public void writeToProto(ProtoOutputStream protoOutputStream) {
        protoOutputStream.write(1120986464257L, this.mType);
        protoOutputStream.write(1112396529666L, this.mTimestamp);
        protoOutputStream.write(1120986464259L, this.mDurationSeconds);
    }

    public static Event readFromProto(ProtoInputStream protoInputStream) throws IOException {
        Builder builder = new Builder();
        while (protoInputStream.nextField() != -1) {
            int fieldNumber = protoInputStream.getFieldNumber();
            if (fieldNumber == 1) {
                builder.setType(protoInputStream.readInt(1120986464257L));
            } else if (fieldNumber == 2) {
                builder.setTimestamp(protoInputStream.readLong(1112396529666L));
            } else if (fieldNumber == 3) {
                builder.setDurationSeconds(protoInputStream.readInt(1120986464259L));
            } else {
                String str = TAG;
                Slog.w(str, "Could not read undefined field: " + protoInputStream.getFieldNumber());
            }
        }
        return builder.build();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Event) {
            Event event = (Event) obj;
            return this.mTimestamp == event.mTimestamp && this.mType == event.mType && this.mDurationSeconds == event.mDurationSeconds;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mTimestamp), Integer.valueOf(this.mType), Integer.valueOf(this.mDurationSeconds));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Event {");
        sb.append("timestamp=");
        sb.append(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mTimestamp));
        sb.append(", type=");
        sb.append(this.mType);
        if (this.mDurationSeconds > 0) {
            sb.append(", durationSeconds=");
            sb.append(this.mDurationSeconds);
        }
        sb.append("}");
        return sb.toString();
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        public int mDurationSeconds;
        public long mTimestamp;
        public int mType;

        public Builder() {
        }

        public Builder(long j, int i) {
            this.mTimestamp = j;
            this.mType = i;
        }

        public Builder setDurationSeconds(int i) {
            this.mDurationSeconds = i;
            return this;
        }

        public final Builder setTimestamp(long j) {
            this.mTimestamp = j;
            return this;
        }

        public final Builder setType(int i) {
            this.mType = i;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }
}
