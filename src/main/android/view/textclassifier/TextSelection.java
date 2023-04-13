package android.view.textclassifier;

import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.SpannedString;
import android.util.ArrayMap;
import android.view.textclassifier.TextClassifier;
import com.android.internal.util.Preconditions;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextSelection implements Parcelable {
    public static final Parcelable.Creator<TextSelection> CREATOR = new Parcelable.Creator<TextSelection>() { // from class: android.view.textclassifier.TextSelection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextSelection createFromParcel(Parcel in) {
            return new TextSelection(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextSelection[] newArray(int size) {
            return new TextSelection[size];
        }
    };
    private final int mEndIndex;
    private final EntityConfidence mEntityConfidence;
    private final Bundle mExtras;
    private final String mId;
    private final int mStartIndex;
    private final TextClassification mTextClassification;

    private TextSelection(int startIndex, int endIndex, Map<String, Float> entityConfidence, String id, TextClassification textClassification, Bundle extras) {
        this.mStartIndex = startIndex;
        this.mEndIndex = endIndex;
        this.mEntityConfidence = new EntityConfidence(entityConfidence);
        this.mId = id;
        this.mTextClassification = textClassification;
        this.mExtras = extras;
    }

    public int getSelectionStartIndex() {
        return this.mStartIndex;
    }

    public int getSelectionEndIndex() {
        return this.mEndIndex;
    }

    public int getEntityCount() {
        return this.mEntityConfidence.getEntities().size();
    }

    public String getEntity(int index) {
        return this.mEntityConfidence.getEntities().get(index);
    }

    public float getConfidenceScore(String entity) {
        return this.mEntityConfidence.getConfidenceScore(entity);
    }

    public String getId() {
        return this.mId;
    }

    public TextClassification getTextClassification() {
        return this.mTextClassification;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Builder toBuilder() {
        return new Builder(this.mStartIndex, this.mEndIndex).setId(this.mId).setEntityConfidence(this.mEntityConfidence).setTextClassification(this.mTextClassification).setExtras(this.mExtras);
    }

    public String toString() {
        return String.format(Locale.US, "TextSelection {id=%s, startIndex=%d, endIndex=%d, entities=%s}", this.mId, Integer.valueOf(this.mStartIndex), Integer.valueOf(this.mEndIndex), this.mEntityConfidence);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private final int mEndIndex;
        private final Map<String, Float> mEntityConfidence = new ArrayMap();
        private Bundle mExtras;
        private String mId;
        private final int mStartIndex;
        private TextClassification mTextClassification;

        public Builder(int startIndex, int endIndex) {
            Preconditions.checkArgument(startIndex >= 0);
            Preconditions.checkArgument(endIndex > startIndex);
            this.mStartIndex = startIndex;
            this.mEndIndex = endIndex;
        }

        public Builder setEntityType(String type, float confidenceScore) {
            Objects.requireNonNull(type);
            this.mEntityConfidence.put(type, Float.valueOf(confidenceScore));
            return this;
        }

        Builder setEntityConfidence(EntityConfidence scores) {
            this.mEntityConfidence.clear();
            this.mEntityConfidence.putAll(scores.toMap());
            return this;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setTextClassification(TextClassification textClassification) {
            this.mTextClassification = textClassification;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public TextSelection build() {
            int i = this.mStartIndex;
            int i2 = this.mEndIndex;
            Map<String, Float> map = this.mEntityConfidence;
            String str = this.mId;
            TextClassification textClassification = this.mTextClassification;
            Bundle bundle = this.mExtras;
            if (bundle == null) {
                bundle = Bundle.EMPTY;
            }
            return new TextSelection(i, i2, map, str, textClassification, bundle);
        }
    }

    /* loaded from: classes4.dex */
    public static final class Request implements Parcelable {
        public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() { // from class: android.view.textclassifier.TextSelection.Request.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Request createFromParcel(Parcel in) {
                return Request.readFromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Request[] newArray(int size) {
                return new Request[size];
            }
        };
        private final boolean mDarkLaunchAllowed;
        private final LocaleList mDefaultLocales;
        private final int mEndIndex;
        private final Bundle mExtras;
        private final boolean mIncludeTextClassification;
        private final int mStartIndex;
        private SystemTextClassifierMetadata mSystemTcMetadata;
        private final CharSequence mText;

        private Request(CharSequence text, int startIndex, int endIndex, LocaleList defaultLocales, boolean darkLaunchAllowed, boolean includeTextClassification, Bundle extras) {
            this.mText = text;
            this.mStartIndex = startIndex;
            this.mEndIndex = endIndex;
            this.mDefaultLocales = defaultLocales;
            this.mDarkLaunchAllowed = darkLaunchAllowed;
            this.mIncludeTextClassification = includeTextClassification;
            this.mExtras = extras;
        }

        public CharSequence getText() {
            return this.mText;
        }

        public int getStartIndex() {
            return this.mStartIndex;
        }

        public int getEndIndex() {
            return this.mEndIndex;
        }

        public boolean isDarkLaunchAllowed() {
            return this.mDarkLaunchAllowed;
        }

        public LocaleList getDefaultLocales() {
            return this.mDefaultLocales;
        }

        public String getCallingPackageName() {
            SystemTextClassifierMetadata systemTextClassifierMetadata = this.mSystemTcMetadata;
            if (systemTextClassifierMetadata != null) {
                return systemTextClassifierMetadata.getCallingPackageName();
            }
            return null;
        }

        public void setSystemTextClassifierMetadata(SystemTextClassifierMetadata systemTcMetadata) {
            this.mSystemTcMetadata = systemTcMetadata;
        }

        public SystemTextClassifierMetadata getSystemTextClassifierMetadata() {
            return this.mSystemTcMetadata;
        }

        public boolean shouldIncludeTextClassification() {
            return this.mIncludeTextClassification;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        /* loaded from: classes4.dex */
        public static final class Builder {
            private boolean mDarkLaunchAllowed;
            private LocaleList mDefaultLocales;
            private final int mEndIndex;
            private Bundle mExtras;
            private boolean mIncludeTextClassification;
            private final int mStartIndex;
            private final CharSequence mText;

            public Builder(CharSequence text, int startIndex, int endIndex) {
                TextClassifier.Utils.checkArgument(text, startIndex, endIndex);
                this.mText = text;
                this.mStartIndex = startIndex;
                this.mEndIndex = endIndex;
            }

            public Builder setDefaultLocales(LocaleList defaultLocales) {
                this.mDefaultLocales = defaultLocales;
                return this;
            }

            public Builder setDarkLaunchAllowed(boolean allowed) {
                this.mDarkLaunchAllowed = allowed;
                return this;
            }

            public Builder setIncludeTextClassification(boolean includeTextClassification) {
                this.mIncludeTextClassification = includeTextClassification;
                return this;
            }

            public Builder setExtras(Bundle extras) {
                this.mExtras = extras;
                return this;
            }

            public Request build() {
                SpannedString spannedString = new SpannedString(this.mText);
                int i = this.mStartIndex;
                int i2 = this.mEndIndex;
                LocaleList localeList = this.mDefaultLocales;
                boolean z = this.mDarkLaunchAllowed;
                boolean z2 = this.mIncludeTextClassification;
                Bundle bundle = this.mExtras;
                if (bundle == null) {
                    bundle = Bundle.EMPTY;
                }
                return new Request(spannedString, i, i2, localeList, z, z2, bundle);
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeCharSequence(this.mText);
            dest.writeInt(this.mStartIndex);
            dest.writeInt(this.mEndIndex);
            dest.writeParcelable(this.mDefaultLocales, flags);
            dest.writeBundle(this.mExtras);
            dest.writeParcelable(this.mSystemTcMetadata, flags);
            dest.writeBoolean(this.mIncludeTextClassification);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Request readFromParcel(Parcel in) {
            CharSequence text = in.readCharSequence();
            int startIndex = in.readInt();
            int endIndex = in.readInt();
            LocaleList defaultLocales = (LocaleList) in.readParcelable(null, LocaleList.class);
            Bundle extras = in.readBundle();
            SystemTextClassifierMetadata systemTcMetadata = (SystemTextClassifierMetadata) in.readParcelable(null, SystemTextClassifierMetadata.class);
            boolean includeTextClassification = in.readBoolean();
            Request request = new Request(text, startIndex, endIndex, defaultLocales, false, includeTextClassification, extras);
            request.setSystemTextClassifierMetadata(systemTcMetadata);
            return request;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStartIndex);
        dest.writeInt(this.mEndIndex);
        this.mEntityConfidence.writeToParcel(dest, flags);
        dest.writeString(this.mId);
        dest.writeBundle(this.mExtras);
        dest.writeParcelable(this.mTextClassification, flags);
    }

    private TextSelection(Parcel in) {
        this.mStartIndex = in.readInt();
        this.mEndIndex = in.readInt();
        this.mEntityConfidence = EntityConfidence.CREATOR.createFromParcel(in);
        this.mId = in.readString();
        this.mExtras = in.readBundle();
        this.mTextClassification = (TextClassification) in.readParcelable(TextClassification.class.getClassLoader(), TextClassification.class);
    }
}
