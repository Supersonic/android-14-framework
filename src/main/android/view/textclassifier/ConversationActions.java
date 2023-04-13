package android.view.textclassifier;

import android.app.Person;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.SpannedString;
import android.view.textclassifier.TextClassifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ConversationActions implements Parcelable {
    public static final Parcelable.Creator<ConversationActions> CREATOR = new Parcelable.Creator<ConversationActions>() { // from class: android.view.textclassifier.ConversationActions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationActions createFromParcel(Parcel in) {
            return new ConversationActions(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationActions[] newArray(int size) {
            return new ConversationActions[size];
        }
    };
    private final List<ConversationAction> mConversationActions;
    private final String mId;

    public ConversationActions(List<ConversationAction> conversationActions, String id) {
        this.mConversationActions = Collections.unmodifiableList((List) Objects.requireNonNull(conversationActions));
        this.mId = id;
    }

    private ConversationActions(Parcel in) {
        this.mConversationActions = Collections.unmodifiableList(in.createTypedArrayList(ConversationAction.CREATOR));
        this.mId = in.readString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedList(this.mConversationActions);
        parcel.writeString(this.mId);
    }

    public List<ConversationAction> getConversationActions() {
        return this.mConversationActions;
    }

    public String getId() {
        return this.mId;
    }

    /* loaded from: classes4.dex */
    public static final class Message implements Parcelable {
        private final Person mAuthor;
        private final Bundle mExtras;
        private final ZonedDateTime mReferenceTime;
        private final CharSequence mText;
        public static final Person PERSON_USER_SELF = new Person.Builder().setKey("text-classifier-conversation-actions-user-self").build();
        public static final Person PERSON_USER_OTHERS = new Person.Builder().setKey("text-classifier-conversation-actions-user-others").build();
        public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() { // from class: android.view.textclassifier.ConversationActions.Message.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Message createFromParcel(Parcel in) {
                return new Message(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Message[] newArray(int size) {
                return new Message[size];
            }
        };

        private Message(Person author, ZonedDateTime referenceTime, CharSequence text, Bundle bundle) {
            this.mAuthor = author;
            this.mReferenceTime = referenceTime;
            this.mText = text;
            this.mExtras = (Bundle) Objects.requireNonNull(bundle);
        }

        private Message(Parcel in) {
            this.mAuthor = (Person) in.readParcelable(null, Person.class);
            this.mReferenceTime = in.readInt() != 0 ? ZonedDateTime.parse(in.readString(), DateTimeFormatter.ISO_ZONED_DATE_TIME) : null;
            this.mText = in.readCharSequence();
            this.mExtras = in.readBundle();
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeParcelable(this.mAuthor, flags);
            parcel.writeInt(this.mReferenceTime != null ? 1 : 0);
            ZonedDateTime zonedDateTime = this.mReferenceTime;
            if (zonedDateTime != null) {
                parcel.writeString(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
            }
            parcel.writeCharSequence(this.mText);
            parcel.writeBundle(this.mExtras);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Person getAuthor() {
            return this.mAuthor;
        }

        public ZonedDateTime getReferenceTime() {
            return this.mReferenceTime;
        }

        public CharSequence getText() {
            return this.mText;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        /* loaded from: classes4.dex */
        public static final class Builder {
            private Person mAuthor;
            private Bundle mExtras;
            private ZonedDateTime mReferenceTime;
            private CharSequence mText;

            public Builder(Person author) {
                this.mAuthor = (Person) Objects.requireNonNull(author);
            }

            public Builder setText(CharSequence text) {
                this.mText = text;
                return this;
            }

            public Builder setReferenceTime(ZonedDateTime referenceTime) {
                this.mReferenceTime = referenceTime;
                return this;
            }

            public Builder setExtras(Bundle bundle) {
                this.mExtras = bundle;
                return this;
            }

            public Message build() {
                Person person = this.mAuthor;
                ZonedDateTime zonedDateTime = this.mReferenceTime;
                SpannedString spannedString = this.mText == null ? null : new SpannedString(this.mText);
                Bundle bundle = this.mExtras;
                if (bundle == null) {
                    bundle = Bundle.EMPTY;
                }
                return new Message(person, zonedDateTime, spannedString, bundle);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class Request implements Parcelable {
        public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() { // from class: android.view.textclassifier.ConversationActions.Request.1
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
        public static final String HINT_FOR_IN_APP = "in_app";
        public static final String HINT_FOR_NOTIFICATION = "notification";
        private final List<Message> mConversation;
        private Bundle mExtras;
        private final List<String> mHints;
        private final int mMaxSuggestions;
        private SystemTextClassifierMetadata mSystemTcMetadata;
        private final TextClassifier.EntityConfig mTypeConfig;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes4.dex */
        public @interface Hint {
        }

        private Request(List<Message> conversation, TextClassifier.EntityConfig typeConfig, int maxSuggestions, List<String> hints, Bundle extras) {
            this.mConversation = (List) Objects.requireNonNull(conversation);
            this.mTypeConfig = (TextClassifier.EntityConfig) Objects.requireNonNull(typeConfig);
            this.mMaxSuggestions = maxSuggestions;
            this.mHints = hints;
            this.mExtras = extras;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Request readFromParcel(Parcel in) {
            ArrayList arrayList = new ArrayList();
            in.readParcelableList(arrayList, null, Message.class);
            TextClassifier.EntityConfig typeConfig = (TextClassifier.EntityConfig) in.readParcelable(null, TextClassifier.EntityConfig.class);
            int maxSuggestions = in.readInt();
            List<String> hints = new ArrayList<>();
            in.readStringList(hints);
            Bundle extras = in.readBundle();
            SystemTextClassifierMetadata systemTcMetadata = (SystemTextClassifierMetadata) in.readParcelable(null, SystemTextClassifierMetadata.class);
            Request request = new Request(arrayList, typeConfig, maxSuggestions, hints, extras);
            request.setSystemTextClassifierMetadata(systemTcMetadata);
            return request;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeParcelableList(this.mConversation, flags);
            parcel.writeParcelable(this.mTypeConfig, flags);
            parcel.writeInt(this.mMaxSuggestions);
            parcel.writeStringList(this.mHints);
            parcel.writeBundle(this.mExtras);
            parcel.writeParcelable(this.mSystemTcMetadata, flags);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public TextClassifier.EntityConfig getTypeConfig() {
            return this.mTypeConfig;
        }

        public List<Message> getConversation() {
            return this.mConversation;
        }

        public int getMaxSuggestions() {
            return this.mMaxSuggestions;
        }

        public List<String> getHints() {
            return this.mHints;
        }

        public String getCallingPackageName() {
            SystemTextClassifierMetadata systemTextClassifierMetadata = this.mSystemTcMetadata;
            if (systemTextClassifierMetadata != null) {
                return systemTextClassifierMetadata.getCallingPackageName();
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setSystemTextClassifierMetadata(SystemTextClassifierMetadata systemTcData) {
            this.mSystemTcMetadata = systemTcData;
        }

        public SystemTextClassifierMetadata getSystemTextClassifierMetadata() {
            return this.mSystemTcMetadata;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        /* loaded from: classes4.dex */
        public static final class Builder {
            private List<Message> mConversation;
            private Bundle mExtras;
            private List<String> mHints;
            private int mMaxSuggestions = -1;
            private TextClassifier.EntityConfig mTypeConfig;

            public Builder(List<Message> conversation) {
                this.mConversation = (List) Objects.requireNonNull(conversation);
            }

            public Builder setHints(List<String> hints) {
                this.mHints = hints;
                return this;
            }

            public Builder setTypeConfig(TextClassifier.EntityConfig typeConfig) {
                this.mTypeConfig = typeConfig;
                return this;
            }

            public Builder setMaxSuggestions(int maxSuggestions) {
                if (maxSuggestions < -1) {
                    throw new IllegalArgumentException("maxSuggestions has to be greater than or equal to -1.");
                }
                this.mMaxSuggestions = maxSuggestions;
                return this;
            }

            public Builder setExtras(Bundle bundle) {
                this.mExtras = bundle;
                return this;
            }

            public Request build() {
                TextClassifier.EntityConfig entityConfig;
                List unmodifiableList;
                List unmodifiableList2 = Collections.unmodifiableList(this.mConversation);
                TextClassifier.EntityConfig entityConfig2 = this.mTypeConfig;
                if (entityConfig2 == null) {
                    entityConfig = new TextClassifier.EntityConfig.Builder().build();
                } else {
                    entityConfig = entityConfig2;
                }
                int i = this.mMaxSuggestions;
                List<String> list = this.mHints;
                if (list == null) {
                    unmodifiableList = Collections.emptyList();
                } else {
                    unmodifiableList = Collections.unmodifiableList(list);
                }
                Bundle bundle = this.mExtras;
                if (bundle == null) {
                    bundle = Bundle.EMPTY;
                }
                return new Request(unmodifiableList2, entityConfig, i, unmodifiableList, bundle);
            }
        }
    }
}
