package android.view.textclassifier;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class SelectionEvent implements Parcelable {
    public static final int ACTION_ABANDON = 107;
    public static final int ACTION_COPY = 101;
    public static final int ACTION_CUT = 103;
    public static final int ACTION_DRAG = 106;
    public static final int ACTION_OTHER = 108;
    public static final int ACTION_OVERTYPE = 100;
    public static final int ACTION_PASTE = 102;
    public static final int ACTION_RESET = 201;
    public static final int ACTION_SELECT_ALL = 200;
    public static final int ACTION_SHARE = 104;
    public static final int ACTION_SMART_SHARE = 105;
    public static final Parcelable.Creator<SelectionEvent> CREATOR = new Parcelable.Creator<SelectionEvent>() { // from class: android.view.textclassifier.SelectionEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SelectionEvent createFromParcel(Parcel in) {
            return new SelectionEvent(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SelectionEvent[] newArray(int size) {
            return new SelectionEvent[size];
        }
    };
    public static final int EVENT_AUTO_SELECTION = 5;
    public static final int EVENT_SELECTION_MODIFIED = 2;
    public static final int EVENT_SELECTION_STARTED = 1;
    public static final int EVENT_SMART_SELECTION_MULTI = 4;
    public static final int EVENT_SMART_SELECTION_SINGLE = 3;
    public static final int INVOCATION_LINK = 2;
    public static final int INVOCATION_MANUAL = 1;
    public static final int INVOCATION_UNKNOWN = 0;
    static final String NO_SIGNATURE = "";
    private final int mAbsoluteEnd;
    private final int mAbsoluteStart;
    private long mDurationSincePreviousEvent;
    private long mDurationSinceSessionStart;
    private int mEnd;
    private String mEntityType;
    private int mEventIndex;
    private long mEventTime;
    private int mEventType;
    private int mInvocationMethod;
    private String mPackageName;
    private String mResultId;
    private TextClassificationSessionId mSessionId;
    private int mSmartEnd;
    private int mSmartStart;
    private int mStart;
    private SystemTextClassifierMetadata mSystemTcMetadata;
    private String mWidgetType;
    private String mWidgetVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ActionType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface EventType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface InvocationMethod {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectionEvent(int start, int end, int eventType, String entityType, int invocationMethod, String resultId) {
        this.mPackageName = "";
        this.mWidgetType = "unknown";
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        this.mAbsoluteStart = start;
        this.mAbsoluteEnd = end;
        this.mEventType = eventType;
        this.mEntityType = (String) Objects.requireNonNull(entityType);
        this.mResultId = resultId;
        this.mInvocationMethod = invocationMethod;
    }

    private SelectionEvent(Parcel in) {
        this.mPackageName = "";
        this.mWidgetType = "unknown";
        this.mAbsoluteStart = in.readInt();
        this.mAbsoluteEnd = in.readInt();
        this.mEventType = in.readInt();
        this.mEntityType = in.readString();
        this.mWidgetVersion = in.readInt() > 0 ? in.readString() : null;
        this.mPackageName = in.readString();
        this.mWidgetType = in.readString();
        this.mInvocationMethod = in.readInt();
        this.mResultId = in.readString();
        this.mEventTime = in.readLong();
        this.mDurationSinceSessionStart = in.readLong();
        this.mDurationSincePreviousEvent = in.readLong();
        this.mEventIndex = in.readInt();
        this.mSessionId = in.readInt() > 0 ? TextClassificationSessionId.CREATOR.createFromParcel(in) : null;
        this.mStart = in.readInt();
        this.mEnd = in.readInt();
        this.mSmartStart = in.readInt();
        this.mSmartEnd = in.readInt();
        this.mSystemTcMetadata = (SystemTextClassifierMetadata) in.readParcelable(null, SystemTextClassifierMetadata.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAbsoluteStart);
        dest.writeInt(this.mAbsoluteEnd);
        dest.writeInt(this.mEventType);
        dest.writeString(this.mEntityType);
        dest.writeInt(this.mWidgetVersion != null ? 1 : 0);
        String str = this.mWidgetVersion;
        if (str != null) {
            dest.writeString(str);
        }
        dest.writeString(this.mPackageName);
        dest.writeString(this.mWidgetType);
        dest.writeInt(this.mInvocationMethod);
        dest.writeString(this.mResultId);
        dest.writeLong(this.mEventTime);
        dest.writeLong(this.mDurationSinceSessionStart);
        dest.writeLong(this.mDurationSincePreviousEvent);
        dest.writeInt(this.mEventIndex);
        dest.writeInt(this.mSessionId == null ? 0 : 1);
        TextClassificationSessionId textClassificationSessionId = this.mSessionId;
        if (textClassificationSessionId != null) {
            textClassificationSessionId.writeToParcel(dest, flags);
        }
        dest.writeInt(this.mStart);
        dest.writeInt(this.mEnd);
        dest.writeInt(this.mSmartStart);
        dest.writeInt(this.mSmartEnd);
        dest.writeParcelable(this.mSystemTcMetadata, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static SelectionEvent createSelectionStartedEvent(int invocationMethod, int start) {
        return new SelectionEvent(start, start + 1, 1, "", invocationMethod, "");
    }

    public static SelectionEvent createSelectionModifiedEvent(int start, int end) {
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        return new SelectionEvent(start, end, 2, "", 0, "");
    }

    public static SelectionEvent createSelectionModifiedEvent(int start, int end, TextClassification classification) {
        String entityType;
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        Objects.requireNonNull(classification);
        if (classification.getEntityCount() > 0) {
            entityType = classification.getEntity(0);
        } else {
            entityType = "";
        }
        return new SelectionEvent(start, end, 2, entityType, 0, classification.getId());
    }

    public static SelectionEvent createSelectionModifiedEvent(int start, int end, TextSelection selection) {
        String entityType;
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        Objects.requireNonNull(selection);
        if (selection.getEntityCount() > 0) {
            entityType = selection.getEntity(0);
        } else {
            entityType = "";
        }
        return new SelectionEvent(start, end, 5, entityType, 0, selection.getId());
    }

    public static SelectionEvent createSelectionActionEvent(int start, int end, int actionType) {
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        checkActionType(actionType);
        return new SelectionEvent(start, end, actionType, "", 0, "");
    }

    public static SelectionEvent createSelectionActionEvent(int start, int end, int actionType, TextClassification classification) {
        String entityType;
        Preconditions.checkArgument(end >= start, "end cannot be less than start");
        Objects.requireNonNull(classification);
        checkActionType(actionType);
        if (classification.getEntityCount() > 0) {
            entityType = classification.getEntity(0);
        } else {
            entityType = "";
        }
        return new SelectionEvent(start, end, actionType, entityType, 0, classification.getId());
    }

    private static void checkActionType(int eventType) throws IllegalArgumentException {
        switch (eventType) {
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 200:
            case 201:
                return;
            default:
                throw new IllegalArgumentException(String.format(Locale.US, "%d is not an eventType", Integer.valueOf(eventType)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAbsoluteStart() {
        return this.mAbsoluteStart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAbsoluteEnd() {
        return this.mAbsoluteEnd;
    }

    public int getEventType() {
        return this.mEventType;
    }

    public void setEventType(int eventType) {
        this.mEventType = eventType;
    }

    public String getEntityType() {
        return this.mEntityType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEntityType(String entityType) {
        this.mEntityType = (String) Objects.requireNonNull(entityType);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSystemTextClassifierMetadata(SystemTextClassifierMetadata systemTcMetadata) {
        this.mSystemTcMetadata = systemTcMetadata;
    }

    public SystemTextClassifierMetadata getSystemTextClassifierMetadata() {
        return this.mSystemTcMetadata;
    }

    public String getWidgetType() {
        return this.mWidgetType;
    }

    public String getWidgetVersion() {
        return this.mWidgetVersion;
    }

    public void setTextClassificationSessionContext(TextClassificationContext context) {
        this.mPackageName = context.getPackageName();
        this.mWidgetType = context.getWidgetType();
        this.mWidgetVersion = context.getWidgetVersion();
        this.mSystemTcMetadata = context.getSystemTextClassifierMetadata();
    }

    public int getInvocationMethod() {
        return this.mInvocationMethod;
    }

    public void setInvocationMethod(int invocationMethod) {
        this.mInvocationMethod = invocationMethod;
    }

    public String getResultId() {
        return this.mResultId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectionEvent setResultId(String resultId) {
        this.mResultId = resultId;
        return this;
    }

    public long getEventTime() {
        return this.mEventTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectionEvent setEventTime(long timeMs) {
        this.mEventTime = timeMs;
        return this;
    }

    public long getDurationSinceSessionStart() {
        return this.mDurationSinceSessionStart;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectionEvent setDurationSinceSessionStart(long durationMs) {
        this.mDurationSinceSessionStart = durationMs;
        return this;
    }

    public long getDurationSincePreviousEvent() {
        return this.mDurationSincePreviousEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectionEvent setDurationSincePreviousEvent(long durationMs) {
        this.mDurationSincePreviousEvent = durationMs;
        return this;
    }

    public int getEventIndex() {
        return this.mEventIndex;
    }

    public SelectionEvent setEventIndex(int index) {
        this.mEventIndex = index;
        return this;
    }

    public TextClassificationSessionId getSessionId() {
        return this.mSessionId;
    }

    public SelectionEvent setSessionId(TextClassificationSessionId id) {
        this.mSessionId = id;
        return this;
    }

    public int getStart() {
        return this.mStart;
    }

    public SelectionEvent setStart(int start) {
        this.mStart = start;
        return this;
    }

    public int getEnd() {
        return this.mEnd;
    }

    public SelectionEvent setEnd(int end) {
        this.mEnd = end;
        return this;
    }

    public int getSmartStart() {
        return this.mSmartStart;
    }

    public SelectionEvent setSmartStart(int start) {
        this.mSmartStart = start;
        return this;
    }

    public int getSmartEnd() {
        return this.mSmartEnd;
    }

    public SelectionEvent setSmartEnd(int end) {
        this.mSmartEnd = end;
        return this;
    }

    boolean isTerminal() {
        return isTerminal(this.mEventType);
    }

    public static boolean isTerminal(int eventType) {
        switch (eventType) {
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
                return true;
            default:
                return false;
        }
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mAbsoluteStart), Integer.valueOf(this.mAbsoluteEnd), Integer.valueOf(this.mEventType), this.mEntityType, this.mWidgetVersion, this.mPackageName, this.mWidgetType, Integer.valueOf(this.mInvocationMethod), this.mResultId, Long.valueOf(this.mEventTime), Long.valueOf(this.mDurationSinceSessionStart), Long.valueOf(this.mDurationSincePreviousEvent), Integer.valueOf(this.mEventIndex), this.mSessionId, Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), Integer.valueOf(this.mSmartStart), Integer.valueOf(this.mSmartEnd), this.mSystemTcMetadata);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SelectionEvent) {
            SelectionEvent other = (SelectionEvent) obj;
            return this.mAbsoluteStart == other.mAbsoluteStart && this.mAbsoluteEnd == other.mAbsoluteEnd && this.mEventType == other.mEventType && Objects.equals(this.mEntityType, other.mEntityType) && Objects.equals(this.mWidgetVersion, other.mWidgetVersion) && Objects.equals(this.mPackageName, other.mPackageName) && Objects.equals(this.mWidgetType, other.mWidgetType) && this.mInvocationMethod == other.mInvocationMethod && Objects.equals(this.mResultId, other.mResultId) && this.mEventTime == other.mEventTime && this.mDurationSinceSessionStart == other.mDurationSinceSessionStart && this.mDurationSincePreviousEvent == other.mDurationSincePreviousEvent && this.mEventIndex == other.mEventIndex && Objects.equals(this.mSessionId, other.mSessionId) && this.mStart == other.mStart && this.mEnd == other.mEnd && this.mSmartStart == other.mSmartStart && this.mSmartEnd == other.mSmartEnd && this.mSystemTcMetadata == other.mSystemTcMetadata;
        }
        return false;
    }

    public String toString() {
        return String.format(Locale.US, "SelectionEvent {absoluteStart=%d, absoluteEnd=%d, eventType=%d, entityType=%s, widgetVersion=%s, packageName=%s, widgetType=%s, invocationMethod=%s, resultId=%s, eventTime=%d, durationSinceSessionStart=%d, durationSincePreviousEvent=%d, eventIndex=%d,sessionId=%s, start=%d, end=%d, smartStart=%d, smartEnd=%d, systemTcMetadata=%s}", Integer.valueOf(this.mAbsoluteStart), Integer.valueOf(this.mAbsoluteEnd), Integer.valueOf(this.mEventType), this.mEntityType, this.mWidgetVersion, this.mPackageName, this.mWidgetType, Integer.valueOf(this.mInvocationMethod), this.mResultId, Long.valueOf(this.mEventTime), Long.valueOf(this.mDurationSinceSessionStart), Long.valueOf(this.mDurationSincePreviousEvent), Integer.valueOf(this.mEventIndex), this.mSessionId, Integer.valueOf(this.mStart), Integer.valueOf(this.mEnd), Integer.valueOf(this.mSmartStart), Integer.valueOf(this.mSmartEnd), this.mSystemTcMetadata);
    }
}
