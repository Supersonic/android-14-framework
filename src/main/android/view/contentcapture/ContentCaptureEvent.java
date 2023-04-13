package android.view.contentcapture;

import android.annotation.SystemApi;
import android.graphics.Insets;
import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.inputmethod.BaseInputConnection;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes4.dex */
public final class ContentCaptureEvent implements Parcelable {
    public static final int MAX_INVALID_VALUE = -1;
    public static final int TYPE_CONTEXT_UPDATED = 6;
    public static final int TYPE_SESSION_FINISHED = -2;
    public static final int TYPE_SESSION_PAUSED = 8;
    public static final int TYPE_SESSION_RESUMED = 7;
    public static final int TYPE_SESSION_STARTED = -1;
    public static final int TYPE_VIEW_APPEARED = 1;
    public static final int TYPE_VIEW_DISAPPEARED = 2;
    public static final int TYPE_VIEW_INSETS_CHANGED = 9;
    public static final int TYPE_VIEW_TEXT_CHANGED = 3;
    public static final int TYPE_VIEW_TREE_APPEARED = 5;
    public static final int TYPE_VIEW_TREE_APPEARING = 4;
    public static final int TYPE_WINDOW_BOUNDS_CHANGED = 10;
    private Rect mBounds;
    private ContentCaptureContext mClientContext;
    private int mComposingEnd;
    private int mComposingStart;
    private final long mEventTime;
    private AutofillId mId;
    private ArrayList<AutofillId> mIds;
    private Insets mInsets;
    private ViewNode mNode;
    private int mParentSessionId;
    private int mSelectionEndIndex;
    private int mSelectionStartIndex;
    private final int mSessionId;
    private CharSequence mText;
    private boolean mTextHasComposingSpan;
    private final int mType;
    private static final String TAG = ContentCaptureEvent.class.getSimpleName();
    public static final Parcelable.Creator<ContentCaptureEvent> CREATOR = new Parcelable.Creator<ContentCaptureEvent>() { // from class: android.view.contentcapture.ContentCaptureEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureEvent createFromParcel(Parcel parcel) {
            int sessionId = parcel.readInt();
            int type = parcel.readInt();
            long eventTime = parcel.readLong();
            ContentCaptureEvent event = new ContentCaptureEvent(sessionId, type, eventTime);
            AutofillId id = (AutofillId) parcel.readParcelable(null, AutofillId.class);
            if (id != null) {
                event.setAutofillId(id);
            }
            ArrayList<AutofillId> ids = parcel.createTypedArrayList(AutofillId.CREATOR);
            if (ids != null) {
                event.setAutofillIds(ids);
            }
            ViewNode node = ViewNode.readFromParcel(parcel);
            if (node != null) {
                event.setViewNode(node);
            }
            event.setText(parcel.readCharSequence());
            if (type == -1 || type == -2) {
                event.setParentSessionId(parcel.readInt());
            }
            if (type == -1 || type == 6) {
                event.setClientContext((ContentCaptureContext) parcel.readParcelable(null, ContentCaptureContext.class));
            }
            if (type == 9) {
                event.setInsets((Insets) parcel.readParcelable(null, Insets.class));
            }
            if (type == 10) {
                event.setBounds((Rect) parcel.readParcelable(null, Rect.class));
            }
            if (type == 3) {
                event.setComposingIndex(parcel.readInt(), parcel.readInt());
                event.restoreComposingSpan();
                event.setSelectionIndex(parcel.readInt(), parcel.readInt());
                event.restoreSelectionSpans();
            }
            return event;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureEvent[] newArray(int size) {
            return new ContentCaptureEvent[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface EventType {
    }

    public ContentCaptureEvent(int sessionId, int type, long eventTime) {
        this.mParentSessionId = 0;
        this.mComposingStart = -1;
        this.mComposingEnd = -1;
        this.mSelectionStartIndex = -1;
        this.mSelectionEndIndex = -1;
        this.mSessionId = sessionId;
        this.mType = type;
        this.mEventTime = eventTime;
    }

    public ContentCaptureEvent(int sessionId, int type) {
        this(sessionId, type, System.currentTimeMillis());
    }

    public ContentCaptureEvent setAutofillId(AutofillId id) {
        this.mId = (AutofillId) Objects.requireNonNull(id);
        return this;
    }

    public ContentCaptureEvent setAutofillIds(ArrayList<AutofillId> ids) {
        this.mIds = (ArrayList) Objects.requireNonNull(ids);
        return this;
    }

    public ContentCaptureEvent addAutofillId(AutofillId id) {
        Objects.requireNonNull(id);
        if (this.mIds == null) {
            ArrayList<AutofillId> arrayList = new ArrayList<>();
            this.mIds = arrayList;
            AutofillId autofillId = this.mId;
            if (autofillId == null) {
                Log.m104w(TAG, "addAutofillId(" + id + ") called without an initial id");
            } else {
                arrayList.add(autofillId);
                this.mId = null;
            }
        }
        this.mIds.add(id);
        return this;
    }

    public ContentCaptureEvent setParentSessionId(int parentSessionId) {
        this.mParentSessionId = parentSessionId;
        return this;
    }

    public ContentCaptureEvent setClientContext(ContentCaptureContext clientContext) {
        this.mClientContext = clientContext;
        return this;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getParentSessionId() {
        return this.mParentSessionId;
    }

    public ContentCaptureContext getContentCaptureContext() {
        return this.mClientContext;
    }

    public ContentCaptureEvent setViewNode(ViewNode node) {
        this.mNode = (ViewNode) Objects.requireNonNull(node);
        return this;
    }

    public ContentCaptureEvent setText(CharSequence text) {
        this.mText = text;
        return this;
    }

    public ContentCaptureEvent setComposingIndex(int start, int end) {
        this.mComposingStart = start;
        this.mComposingEnd = end;
        return this;
    }

    public boolean hasComposingSpan() {
        return this.mComposingStart > -1;
    }

    public ContentCaptureEvent setSelectionIndex(int start, int end) {
        this.mSelectionStartIndex = start;
        this.mSelectionEndIndex = end;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSameComposingSpan(ContentCaptureEvent other) {
        return this.mComposingStart == other.mComposingStart && this.mComposingEnd == other.mComposingEnd;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSameSelectionSpan(ContentCaptureEvent other) {
        return this.mSelectionStartIndex == other.mSelectionStartIndex && this.mSelectionEndIndex == other.mSelectionEndIndex;
    }

    private int getComposingStart() {
        return this.mComposingStart;
    }

    private int getComposingEnd() {
        return this.mComposingEnd;
    }

    private int getSelectionStart() {
        return this.mSelectionStartIndex;
    }

    private int getSelectionEnd() {
        return this.mSelectionEndIndex;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreComposingSpan() {
        int i;
        int i2 = this.mComposingStart;
        if (i2 <= -1 || (i = this.mComposingEnd) <= -1) {
            return;
        }
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spannable) {
            BaseInputConnection.setComposingSpans((Spannable) charSequence, i2, i);
        } else {
            Log.m104w(TAG, "Text is not a Spannable.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreSelectionSpans() {
        if (this.mSelectionStartIndex <= -1 || this.mSelectionEndIndex <= -1) {
            return;
        }
        CharSequence charSequence = this.mText;
        if (charSequence instanceof SpannableString) {
            SpannableString ss = (SpannableString) charSequence;
            Object obj = Selection.SELECTION_START;
            int i = this.mSelectionStartIndex;
            ss.setSpan(obj, i, i, 0);
            Object obj2 = Selection.SELECTION_END;
            int i2 = this.mSelectionEndIndex;
            ss.setSpan(obj2, i2, i2, 0);
            return;
        }
        Log.m104w(TAG, "Text is not a SpannableString.");
    }

    public ContentCaptureEvent setInsets(Insets insets) {
        this.mInsets = insets;
        return this;
    }

    public ContentCaptureEvent setBounds(Rect bounds) {
        this.mBounds = bounds;
        return this;
    }

    public int getType() {
        return this.mType;
    }

    public long getEventTime() {
        return this.mEventTime;
    }

    public ViewNode getViewNode() {
        return this.mNode;
    }

    public AutofillId getId() {
        return this.mId;
    }

    public List<AutofillId> getIds() {
        return this.mIds;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public Insets getInsets() {
        return this.mInsets;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    public void mergeEvent(ContentCaptureEvent event) {
        Objects.requireNonNull(event);
        int eventType = event.getType();
        if (this.mType != eventType) {
            Log.m110e(TAG, "mergeEvent(" + getTypeAsString(eventType) + ") cannot be merged with different eventType=" + getTypeAsString(this.mType));
        } else if (eventType == 2) {
            List<AutofillId> ids = event.getIds();
            AutofillId id = event.getId();
            if (ids != null) {
                if (id != null) {
                    Log.m104w(TAG, "got TYPE_VIEW_DISAPPEARED event with both id and ids: " + event);
                }
                for (int i = 0; i < ids.size(); i++) {
                    addAutofillId(ids.get(i));
                }
            } else if (id != null) {
                addAutofillId(id);
            } else {
                throw new IllegalArgumentException("mergeEvent(): got TYPE_VIEW_DISAPPEARED event with neither id or ids: " + event);
            }
        } else if (eventType == 3) {
            setText(event.getText());
            setComposingIndex(event.getComposingStart(), event.getComposingEnd());
            setSelectionIndex(event.getSelectionStart(), event.getSelectionEnd());
        } else {
            Log.m110e(TAG, "mergeEvent(" + getTypeAsString(eventType) + ") does not support this event type.");
        }
    }

    public void dump(PrintWriter pw) {
        pw.print("type=");
        pw.print(getTypeAsString(this.mType));
        pw.print(", time=");
        pw.print(this.mEventTime);
        if (this.mId != null) {
            pw.print(", id=");
            pw.print(this.mId);
        }
        if (this.mIds != null) {
            pw.print(", ids=");
            pw.print(this.mIds);
        }
        if (this.mNode != null) {
            pw.print(", mNode.id=");
            pw.print(this.mNode.getAutofillId());
        }
        if (this.mSessionId != 0) {
            pw.print(", sessionId=");
            pw.print(this.mSessionId);
        }
        if (this.mParentSessionId != 0) {
            pw.print(", parentSessionId=");
            pw.print(this.mParentSessionId);
        }
        if (this.mText != null) {
            pw.print(", text=");
            pw.println(ContentCaptureHelper.getSanitizedString(this.mText));
        }
        if (this.mClientContext != null) {
            pw.print(", context=");
            this.mClientContext.dump(pw);
            pw.println();
        }
        if (this.mInsets != null) {
            pw.print(", insets=");
            pw.println(this.mInsets);
        }
        if (this.mBounds != null) {
            pw.print(", bounds=");
            pw.println(this.mBounds);
        }
        if (this.mComposingStart > -1) {
            pw.print(", composing(");
            pw.print(this.mComposingStart);
            pw.print(", ");
            pw.print(this.mComposingEnd);
            pw.print(NavigationBarInflaterView.KEY_CODE_END);
        }
        if (this.mSelectionStartIndex > -1) {
            pw.print(", selection(");
            pw.print(this.mSelectionStartIndex);
            pw.print(", ");
            pw.print(this.mSelectionEndIndex);
            pw.print(NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public String toString() {
        StringBuilder string = new StringBuilder("ContentCaptureEvent[type=").append(getTypeAsString(this.mType));
        string.append(", session=").append(this.mSessionId);
        if (this.mType == -1 && this.mParentSessionId != 0) {
            string.append(", parent=").append(this.mParentSessionId);
        }
        if (this.mId != null) {
            string.append(", id=").append(this.mId);
        }
        if (this.mIds != null) {
            string.append(", ids=").append(this.mIds);
        }
        ViewNode viewNode = this.mNode;
        if (viewNode != null) {
            String className = viewNode.getClassName();
            string.append(", class=").append(className);
            string.append(", id=").append(this.mNode.getAutofillId());
            if (this.mNode.getText() != null) {
                string.append(", text=").append((CharSequence) ContentCaptureHelper.getSanitizedString(this.mNode.getText()));
            }
        }
        if (this.mText != null) {
            string.append(", text=").append((CharSequence) ContentCaptureHelper.getSanitizedString(this.mText));
        }
        if (this.mClientContext != null) {
            string.append(", context=").append(this.mClientContext);
        }
        if (this.mInsets != null) {
            string.append(", insets=").append(this.mInsets);
        }
        if (this.mBounds != null) {
            string.append(", bounds=").append(this.mBounds);
        }
        if (this.mComposingStart > -1) {
            string.append(", composing=[").append(this.mComposingStart).append(",").append(this.mComposingEnd).append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        if (this.mSelectionStartIndex > -1) {
            string.append(", selection=[").append(this.mSelectionStartIndex).append(",").append(this.mSelectionEndIndex).append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        return string.append(']').toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mSessionId);
        parcel.writeInt(this.mType);
        parcel.writeLong(this.mEventTime);
        parcel.writeParcelable(this.mId, flags);
        parcel.writeTypedList(this.mIds);
        ViewNode.writeToParcel(parcel, this.mNode, flags);
        parcel.writeCharSequence(this.mText);
        int i = this.mType;
        if (i == -1 || i == -2) {
            parcel.writeInt(this.mParentSessionId);
        }
        int i2 = this.mType;
        if (i2 == -1 || i2 == 6) {
            parcel.writeParcelable(this.mClientContext, flags);
        }
        if (this.mType == 9) {
            parcel.writeParcelable(this.mInsets, flags);
        }
        if (this.mType == 10) {
            parcel.writeParcelable(this.mBounds, flags);
        }
        if (this.mType == 3) {
            parcel.writeInt(this.mComposingStart);
            parcel.writeInt(this.mComposingEnd);
            parcel.writeInt(this.mSelectionStartIndex);
            parcel.writeInt(this.mSelectionEndIndex);
        }
    }

    public static String getTypeAsString(int type) {
        switch (type) {
            case -2:
                return "SESSION_FINISHED";
            case -1:
                return "SESSION_STARTED";
            case 0:
            default:
                return "UKNOWN_TYPE: " + type;
            case 1:
                return "VIEW_APPEARED";
            case 2:
                return "VIEW_DISAPPEARED";
            case 3:
                return "VIEW_TEXT_CHANGED";
            case 4:
                return "VIEW_TREE_APPEARING";
            case 5:
                return "VIEW_TREE_APPEARED";
            case 6:
                return "CONTEXT_UPDATED";
            case 7:
                return "SESSION_RESUMED";
            case 8:
                return "SESSION_PAUSED";
            case 9:
                return "VIEW_INSETS_CHANGED";
            case 10:
                return "TYPE_WINDOW_BOUNDS_CHANGED";
        }
    }
}
