package android.content;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
/* loaded from: classes.dex */
public class ClipDescription implements Parcelable {
    public static final int CLASSIFICATION_COMPLETE = 3;
    public static final int CLASSIFICATION_NOT_COMPLETE = 1;
    public static final int CLASSIFICATION_NOT_PERFORMED = 2;
    public static final Parcelable.Creator<ClipDescription> CREATOR = new Parcelable.Creator<ClipDescription>() { // from class: android.content.ClipDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClipDescription createFromParcel(Parcel source) {
            return new ClipDescription(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClipDescription[] newArray(int size) {
            return new ClipDescription[size];
        }
    };
    public static final String EXTRA_ACTIVITY_OPTIONS = "android.intent.extra.ACTIVITY_OPTIONS";
    public static final String EXTRA_IS_REMOTE_DEVICE = "android.content.extra.IS_REMOTE_DEVICE";
    public static final String EXTRA_IS_SENSITIVE = "android.content.extra.IS_SENSITIVE";
    public static final String EXTRA_LOGGING_INSTANCE_ID = "android.intent.extra.LOGGING_INSTANCE_ID";
    public static final String EXTRA_PENDING_INTENT = "android.intent.extra.PENDING_INTENT";
    public static final String MIMETYPE_APPLICATION_ACTIVITY = "application/vnd.android.activity";
    public static final String MIMETYPE_APPLICATION_SHORTCUT = "application/vnd.android.shortcut";
    public static final String MIMETYPE_APPLICATION_TASK = "application/vnd.android.task";
    public static final String MIMETYPE_TEXT_HTML = "text/html";
    public static final String MIMETYPE_TEXT_INTENT = "text/vnd.android.intent";
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_TEXT_URILIST = "text/uri-list";
    public static final String MIMETYPE_UNKNOWN = "application/octet-stream";
    private int mClassificationStatus;
    private final ArrayMap<String, Float> mEntityConfidence;
    private PersistableBundle mExtras;
    private boolean mIsStyledText;
    final CharSequence mLabel;
    private final ArrayList<String> mMimeTypes;
    private long mTimeStamp;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface ClassificationStatus {
    }

    public ClipDescription(CharSequence label, String[] mimeTypes) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mClassificationStatus = 1;
        if (mimeTypes == null) {
            throw new NullPointerException("mimeTypes is null");
        }
        this.mLabel = label;
        this.mMimeTypes = new ArrayList<>(Arrays.asList(mimeTypes));
    }

    public ClipDescription(ClipDescription o) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mClassificationStatus = 1;
        this.mLabel = o.mLabel;
        this.mMimeTypes = new ArrayList<>(o.mMimeTypes);
        this.mTimeStamp = o.mTimeStamp;
    }

    public static boolean compareMimeTypes(String concreteType, String desiredType) {
        int typeLength = desiredType.length();
        if (typeLength == 3 && desiredType.equals("*/*")) {
            return true;
        }
        int slashpos = desiredType.indexOf(47);
        if (slashpos > 0) {
            if (typeLength == slashpos + 2 && desiredType.charAt(slashpos + 1) == '*') {
                if (desiredType.regionMatches(0, concreteType, 0, slashpos + 1)) {
                    return true;
                }
            } else if (desiredType.equals(concreteType)) {
                return true;
            }
        }
        return false;
    }

    public void setTimestamp(long timeStamp) {
        this.mTimeStamp = timeStamp;
    }

    public long getTimestamp() {
        return this.mTimeStamp;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public boolean hasMimeType(String mimeType) {
        int size = this.mMimeTypes.size();
        for (int i = 0; i < size; i++) {
            if (compareMimeTypes(this.mMimeTypes.get(i), mimeType)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMimeType(String[] targetMimeTypes) {
        for (String targetMimeType : targetMimeTypes) {
            if (hasMimeType(targetMimeType)) {
                return true;
            }
        }
        return false;
    }

    public String[] filterMimeTypes(String mimeType) {
        ArrayList<String> array = null;
        int size = this.mMimeTypes.size();
        for (int i = 0; i < size; i++) {
            if (compareMimeTypes(this.mMimeTypes.get(i), mimeType)) {
                if (array == null) {
                    array = new ArrayList<>();
                }
                array.add(this.mMimeTypes.get(i));
            }
        }
        if (array == null) {
            return null;
        }
        String[] rawArray = new String[array.size()];
        array.toArray(rawArray);
        return rawArray;
    }

    public int getMimeTypeCount() {
        return this.mMimeTypes.size();
    }

    public String getMimeType(int index) {
        return this.mMimeTypes.get(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addMimeTypes(String[] mimeTypes) {
        for (int i = 0; i != mimeTypes.length; i++) {
            String mimeType = mimeTypes[i];
            if (!this.mMimeTypes.contains(mimeType)) {
                this.mMimeTypes.add(mimeType);
            }
        }
    }

    public PersistableBundle getExtras() {
        return this.mExtras;
    }

    public void setExtras(PersistableBundle extras) {
        this.mExtras = new PersistableBundle(extras);
    }

    public void validate() {
        ArrayList<String> arrayList = this.mMimeTypes;
        if (arrayList == null) {
            throw new NullPointerException("null mime types");
        }
        int size = arrayList.size();
        if (size <= 0) {
            throw new IllegalArgumentException("must have at least 1 mime type");
        }
        for (int i = 0; i < size; i++) {
            if (this.mMimeTypes.get(i) == null) {
                throw new NullPointerException("mime type at " + i + " is null");
            }
        }
    }

    public boolean isStyledText() {
        return this.mIsStyledText;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIsStyledText(boolean isStyledText) {
        this.mIsStyledText = isStyledText;
    }

    public void setClassificationStatus(int status) {
        this.mClassificationStatus = status;
    }

    public float getConfidenceScore(String entity) {
        if (this.mClassificationStatus != 3) {
            throw new IllegalStateException("Classification not complete");
        }
        return this.mEntityConfidence.getOrDefault(entity, Float.valueOf(0.0f)).floatValue();
    }

    public int getClassificationStatus() {
        return this.mClassificationStatus;
    }

    public void setConfidenceScores(Map<String, Float> confidences) {
        this.mEntityConfidence.clear();
        this.mEntityConfidence.putAll(confidences);
        this.mClassificationStatus = 3;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(128);
        b.append("ClipDescription { ");
        toShortString(b, true);
        b.append(" }");
        return b.toString();
    }

    public boolean toShortString(StringBuilder b, boolean redactContent) {
        boolean first = !toShortStringTypesOnly(b);
        if (this.mLabel != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            if (redactContent) {
                b.append("hasLabel(").append(this.mLabel.length()).append(')');
            } else {
                b.append('\"').append(this.mLabel).append('\"');
            }
        }
        if (this.mExtras != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            if (redactContent) {
                if (this.mExtras.isParcelled()) {
                    b.append("hasExtras");
                } else {
                    b.append("hasExtras(").append(this.mExtras.size()).append(')');
                }
            } else {
                b.append(this.mExtras.toString());
            }
        }
        if (this.mTimeStamp > 0) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append('<');
            b.append(TimeUtils.logTimeOfDay(this.mTimeStamp));
            b.append('>');
        }
        return !first;
    }

    public boolean toShortStringTypesOnly(StringBuilder b) {
        boolean first = true;
        int size = this.mMimeTypes.size();
        for (int i = 0; i < size; i++) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append(this.mMimeTypes.get(i));
        }
        return !first;
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        int size = this.mMimeTypes.size();
        for (int i = 0; i < size; i++) {
            proto.write(2237677961217L, this.mMimeTypes.get(i));
        }
        CharSequence charSequence = this.mLabel;
        if (charSequence != null) {
            proto.write(1138166333442L, charSequence.toString());
        }
        PersistableBundle persistableBundle = this.mExtras;
        if (persistableBundle != null) {
            persistableBundle.dumpDebug(proto, 1146756268035L);
        }
        long j = this.mTimeStamp;
        if (j > 0) {
            proto.write(1112396529668L, j);
        }
        proto.end(token);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.mLabel, dest, flags);
        dest.writeStringList(this.mMimeTypes);
        dest.writePersistableBundle(this.mExtras);
        dest.writeLong(this.mTimeStamp);
        dest.writeBoolean(this.mIsStyledText);
        dest.writeInt(this.mClassificationStatus);
        dest.writeBundle(confidencesToBundle());
    }

    private Bundle confidencesToBundle() {
        Bundle bundle = new Bundle();
        int size = this.mEntityConfidence.size();
        for (int i = 0; i < size; i++) {
            bundle.putFloat(this.mEntityConfidence.keyAt(i), this.mEntityConfidence.valueAt(i).floatValue());
        }
        return bundle;
    }

    private void readBundleToConfidences(Bundle bundle) {
        for (String key : bundle.keySet()) {
            this.mEntityConfidence.put(key, Float.valueOf(bundle.getFloat(key)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClipDescription(Parcel in) {
        this.mEntityConfidence = new ArrayMap<>();
        this.mClassificationStatus = 1;
        this.mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mMimeTypes = in.createStringArrayList();
        this.mExtras = in.readPersistableBundle();
        this.mTimeStamp = in.readLong();
        this.mIsStyledText = in.readBoolean();
        this.mClassificationStatus = in.readInt();
        readBundleToConfidences(in.readBundle());
    }
}
