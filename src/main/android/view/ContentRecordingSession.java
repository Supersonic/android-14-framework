package android.view;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ContentRecordingSession implements Parcelable {
    public static final Parcelable.Creator<ContentRecordingSession> CREATOR = new Parcelable.Creator<ContentRecordingSession>() { // from class: android.view.ContentRecordingSession.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentRecordingSession[] newArray(int size) {
            return new ContentRecordingSession[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentRecordingSession createFromParcel(Parcel in) {
            return new ContentRecordingSession(in);
        }
    };
    public static final int RECORD_CONTENT_DISPLAY = 0;
    public static final int RECORD_CONTENT_TASK = 1;
    private int mContentToRecord;
    private int mDisplayId;
    private IBinder mTokenToRecord;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface RecordContent {
    }

    private ContentRecordingSession() {
        this.mDisplayId = -1;
        this.mContentToRecord = 0;
        this.mTokenToRecord = null;
    }

    public static ContentRecordingSession createDisplaySession(IBinder displayContentWindowToken) {
        return new ContentRecordingSession().setContentToRecord(0).setTokenToRecord(displayContentWindowToken);
    }

    public static ContentRecordingSession createTaskSession(IBinder taskWindowContainerToken) {
        return new ContentRecordingSession().setContentToRecord(1).setTokenToRecord(taskWindowContainerToken);
    }

    public static boolean isValid(ContentRecordingSession session) {
        return (session == null || session.getDisplayId() <= -1 || session.getTokenToRecord() == null) ? false : true;
    }

    public static boolean isSameDisplay(ContentRecordingSession session, ContentRecordingSession incomingSession) {
        return (session == null || incomingSession == null || session.getDisplayId() != incomingSession.getDisplayId()) ? false : true;
    }

    public static String recordContentToString(int value) {
        switch (value) {
            case 0:
                return "RECORD_CONTENT_DISPLAY";
            case 1:
                return "RECORD_CONTENT_TASK";
            default:
                return Integer.toHexString(value);
        }
    }

    ContentRecordingSession(int displayId, int contentToRecord, IBinder tokenToRecord) {
        this.mDisplayId = -1;
        this.mContentToRecord = 0;
        this.mTokenToRecord = null;
        this.mDisplayId = displayId;
        this.mContentToRecord = contentToRecord;
        if (contentToRecord != 0 && contentToRecord != 1) {
            throw new IllegalArgumentException("contentToRecord was " + this.mContentToRecord + " but must be one of: RECORD_CONTENT_DISPLAY(0), RECORD_CONTENT_TASK(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mTokenToRecord = tokenToRecord;
        AnnotationValidations.validate(VisibleForTesting.class, (Annotation) null, tokenToRecord);
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public int getContentToRecord() {
        return this.mContentToRecord;
    }

    public IBinder getTokenToRecord() {
        return this.mTokenToRecord;
    }

    public ContentRecordingSession setDisplayId(int value) {
        this.mDisplayId = value;
        return this;
    }

    public ContentRecordingSession setContentToRecord(int value) {
        this.mContentToRecord = value;
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("contentToRecord was " + this.mContentToRecord + " but must be one of: RECORD_CONTENT_DISPLAY(0), RECORD_CONTENT_TASK(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
        return this;
    }

    public ContentRecordingSession setTokenToRecord(IBinder value) {
        this.mTokenToRecord = value;
        AnnotationValidations.validate(VisibleForTesting.class, (Annotation) null, value);
        return this;
    }

    public String toString() {
        return "ContentRecordingSession { displayId = " + this.mDisplayId + ", contentToRecord = " + recordContentToString(this.mContentToRecord) + ", tokenToRecord = " + this.mTokenToRecord + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentRecordingSession that = (ContentRecordingSession) o;
        if (this.mDisplayId == that.mDisplayId && this.mContentToRecord == that.mContentToRecord && Objects.equals(this.mTokenToRecord, that.mTokenToRecord)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mDisplayId;
        return (((_hash * 31) + this.mContentToRecord) * 31) + Objects.hashCode(this.mTokenToRecord);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mTokenToRecord != null ? (byte) (0 | 4) : (byte) 0;
        dest.writeByte(flg);
        dest.writeInt(this.mDisplayId);
        dest.writeInt(this.mContentToRecord);
        IBinder iBinder = this.mTokenToRecord;
        if (iBinder != null) {
            dest.writeStrongBinder(iBinder);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    ContentRecordingSession(Parcel in) {
        this.mDisplayId = -1;
        this.mContentToRecord = 0;
        this.mTokenToRecord = null;
        byte flg = in.readByte();
        int displayId = in.readInt();
        int contentToRecord = in.readInt();
        IBinder tokenToRecord = (flg & 4) == 0 ? null : in.readStrongBinder();
        this.mDisplayId = displayId;
        this.mContentToRecord = contentToRecord;
        if (contentToRecord != 0 && contentToRecord != 1) {
            throw new IllegalArgumentException("contentToRecord was " + this.mContentToRecord + " but must be one of: RECORD_CONTENT_DISPLAY(0), RECORD_CONTENT_TASK(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mTokenToRecord = tokenToRecord;
        AnnotationValidations.validate(VisibleForTesting.class, (Annotation) null, tokenToRecord);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private int mContentToRecord;
        private int mDisplayId;
        private IBinder mTokenToRecord;

        public Builder setDisplayId(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mDisplayId = value;
            return this;
        }

        public Builder setContentToRecord(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mContentToRecord = value;
            return this;
        }

        public Builder setTokenToRecord(IBinder value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mTokenToRecord = value;
            return this;
        }

        public ContentRecordingSession build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 8;
            this.mBuilderFieldsSet = j;
            if ((1 & j) == 0) {
                this.mDisplayId = -1;
            }
            if ((2 & j) == 0) {
                this.mContentToRecord = 0;
            }
            if ((j & 4) == 0) {
                this.mTokenToRecord = null;
            }
            ContentRecordingSession o = new ContentRecordingSession(this.mDisplayId, this.mContentToRecord, this.mTokenToRecord);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 8) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
