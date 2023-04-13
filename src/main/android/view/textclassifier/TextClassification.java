package android.view.textclassifier;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.audio.Enums;
import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.SpannedString;
import android.util.ArrayMap;
import android.view.View;
import android.view.textclassifier.TextClassifier;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextClassification implements Parcelable {
    private static final String LOG_TAG = "TextClassification";
    private static final int MAX_LEGACY_ICON_SIZE = 192;
    private final List<RemoteAction> mActions;
    private final EntityConfidence mEntityConfidence;
    private final Bundle mExtras;
    private final String mId;
    private final Drawable mLegacyIcon;
    private final Intent mLegacyIntent;
    private final String mLegacyLabel;
    private final View.OnClickListener mLegacyOnClickListener;
    private final String mText;
    public static final TextClassification EMPTY = new Builder().build();
    public static final Parcelable.Creator<TextClassification> CREATOR = new Parcelable.Creator<TextClassification>() { // from class: android.view.textclassifier.TextClassification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassification createFromParcel(Parcel in) {
            return new TextClassification(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassification[] newArray(int size) {
            return new TextClassification[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    private @interface IntentType {
        public static final int ACTIVITY = 0;
        public static final int SERVICE = 1;
        public static final int UNSUPPORTED = -1;
    }

    private TextClassification(String text, Drawable legacyIcon, String legacyLabel, Intent legacyIntent, View.OnClickListener legacyOnClickListener, List<RemoteAction> actions, EntityConfidence entityConfidence, String id, Bundle extras) {
        this.mText = text;
        this.mLegacyIcon = legacyIcon;
        this.mLegacyLabel = legacyLabel;
        this.mLegacyIntent = legacyIntent;
        this.mLegacyOnClickListener = legacyOnClickListener;
        this.mActions = Collections.unmodifiableList(actions);
        this.mEntityConfidence = (EntityConfidence) Objects.requireNonNull(entityConfidence);
        this.mId = id;
        this.mExtras = extras;
    }

    public String getText() {
        return this.mText;
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

    public List<RemoteAction> getActions() {
        return this.mActions;
    }

    @Deprecated
    public Drawable getIcon() {
        return this.mLegacyIcon;
    }

    @Deprecated
    public CharSequence getLabel() {
        return this.mLegacyLabel;
    }

    @Deprecated
    public Intent getIntent() {
        return this.mLegacyIntent;
    }

    public View.OnClickListener getOnClickListener() {
        return this.mLegacyOnClickListener;
    }

    public String getId() {
        return this.mId;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Builder toBuilder() {
        return new Builder().setId(this.mId).setText(this.mText).addActions(this.mActions).setEntityConfidence(this.mEntityConfidence).setIcon(this.mLegacyIcon).setLabel(this.mLegacyLabel).setIntent(this.mLegacyIntent).setOnClickListener(this.mLegacyOnClickListener).setExtras(this.mExtras);
    }

    public String toString() {
        return String.format(Locale.US, "TextClassification {text=%s, entities=%s, actions=%s, id=%s, extras=%s}", this.mText, this.mEntityConfidence, this.mActions, this.mId, this.mExtras);
    }

    public static View.OnClickListener createIntentOnClickListener(final PendingIntent intent) {
        Objects.requireNonNull(intent);
        return new View.OnClickListener() { // from class: android.view.textclassifier.TextClassification$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TextClassification.lambda$createIntentOnClickListener$0(PendingIntent.this, view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$createIntentOnClickListener$0(PendingIntent intent, View v) {
        try {
            intent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.m78e(LOG_TAG, "Error sending PendingIntent", e);
        }
    }

    public static PendingIntent createPendingIntent(Context context, Intent intent, int requestCode) {
        return PendingIntent.getActivity(context, requestCode, intent, Enums.AUDIO_FORMAT_DTS_HD);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private Bundle mExtras;
        private String mId;
        private Drawable mLegacyIcon;
        private Intent mLegacyIntent;
        private String mLegacyLabel;
        private View.OnClickListener mLegacyOnClickListener;
        private String mText;
        private final List<RemoteAction> mActions = new ArrayList();
        private final Map<String, Float> mTypeScoreMap = new ArrayMap();

        public Builder setText(String text) {
            this.mText = text;
            return this;
        }

        public Builder setEntityType(String type, float confidenceScore) {
            this.mTypeScoreMap.put(type, Float.valueOf(confidenceScore));
            return this;
        }

        Builder setEntityConfidence(EntityConfidence scores) {
            this.mTypeScoreMap.clear();
            this.mTypeScoreMap.putAll(scores.toMap());
            return this;
        }

        public Builder clearEntityTypes() {
            this.mTypeScoreMap.clear();
            return this;
        }

        public Builder addAction(RemoteAction action) {
            Preconditions.checkArgument(action != null);
            this.mActions.add(action);
            return this;
        }

        public Builder addActions(Collection<RemoteAction> actions) {
            Objects.requireNonNull(actions);
            this.mActions.addAll(actions);
            return this;
        }

        public Builder clearActions() {
            this.mActions.clear();
            return this;
        }

        @Deprecated
        public Builder setIcon(Drawable icon) {
            this.mLegacyIcon = icon;
            return this;
        }

        @Deprecated
        public Builder setLabel(String label) {
            this.mLegacyLabel = label;
            return this;
        }

        @Deprecated
        public Builder setIntent(Intent intent) {
            this.mLegacyIntent = intent;
            return this;
        }

        @Deprecated
        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            this.mLegacyOnClickListener = onClickListener;
            return this;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public TextClassification build() {
            EntityConfidence entityConfidence = new EntityConfidence(this.mTypeScoreMap);
            String str = this.mText;
            Drawable drawable = this.mLegacyIcon;
            String str2 = this.mLegacyLabel;
            Intent intent = this.mLegacyIntent;
            View.OnClickListener onClickListener = this.mLegacyOnClickListener;
            List<RemoteAction> list = this.mActions;
            String str3 = this.mId;
            Bundle bundle = this.mExtras;
            if (bundle == null) {
                bundle = Bundle.EMPTY;
            }
            return new TextClassification(str, drawable, str2, intent, onClickListener, list, entityConfidence, str3, bundle);
        }
    }

    /* loaded from: classes4.dex */
    public static final class Request implements Parcelable {
        public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() { // from class: android.view.textclassifier.TextClassification.Request.1
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
        private final LocaleList mDefaultLocales;
        private final int mEndIndex;
        private final Bundle mExtras;
        private final ZonedDateTime mReferenceTime;
        private final int mStartIndex;
        private SystemTextClassifierMetadata mSystemTcMetadata;
        private final CharSequence mText;

        private Request(CharSequence text, int startIndex, int endIndex, LocaleList defaultLocales, ZonedDateTime referenceTime, Bundle extras) {
            this.mText = text;
            this.mStartIndex = startIndex;
            this.mEndIndex = endIndex;
            this.mDefaultLocales = defaultLocales;
            this.mReferenceTime = referenceTime;
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

        public LocaleList getDefaultLocales() {
            return this.mDefaultLocales;
        }

        public ZonedDateTime getReferenceTime() {
            return this.mReferenceTime;
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

        public Bundle getExtras() {
            return this.mExtras;
        }

        /* loaded from: classes4.dex */
        public static final class Builder {
            private LocaleList mDefaultLocales;
            private final int mEndIndex;
            private Bundle mExtras;
            private ZonedDateTime mReferenceTime;
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

            public Builder setReferenceTime(ZonedDateTime referenceTime) {
                this.mReferenceTime = referenceTime;
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
                ZonedDateTime zonedDateTime = this.mReferenceTime;
                Bundle bundle = this.mExtras;
                if (bundle == null) {
                    bundle = Bundle.EMPTY;
                }
                return new Request(spannedString, i, i2, localeList, zonedDateTime, bundle);
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
            ZonedDateTime zonedDateTime = this.mReferenceTime;
            dest.writeString(zonedDateTime == null ? null : zonedDateTime.toString());
            dest.writeBundle(this.mExtras);
            dest.writeParcelable(this.mSystemTcMetadata, flags);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Request readFromParcel(Parcel in) {
            CharSequence text = in.readCharSequence();
            int startIndex = in.readInt();
            int endIndex = in.readInt();
            LocaleList defaultLocales = (LocaleList) in.readParcelable(null, LocaleList.class);
            String referenceTimeString = in.readString();
            ZonedDateTime referenceTime = referenceTimeString == null ? null : ZonedDateTime.parse(referenceTimeString);
            Bundle extras = in.readBundle();
            SystemTextClassifierMetadata systemTcMetadata = (SystemTextClassifierMetadata) in.readParcelable(null, SystemTextClassifierMetadata.class);
            Request request = new Request(text, startIndex, endIndex, defaultLocales, referenceTime, extras);
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
        dest.writeString(this.mText);
        dest.writeTypedList(this.mActions);
        this.mEntityConfidence.writeToParcel(dest, flags);
        dest.writeString(this.mId);
        dest.writeBundle(this.mExtras);
    }

    private TextClassification(Parcel in) {
        this.mText = in.readString();
        ArrayList createTypedArrayList = in.createTypedArrayList(RemoteAction.CREATOR);
        this.mActions = createTypedArrayList;
        if (!createTypedArrayList.isEmpty()) {
            RemoteAction action = (RemoteAction) createTypedArrayList.get(0);
            this.mLegacyIcon = maybeLoadDrawable(action.getIcon());
            this.mLegacyLabel = action.getTitle().toString();
            this.mLegacyOnClickListener = createIntentOnClickListener(((RemoteAction) createTypedArrayList.get(0)).getActionIntent());
        } else {
            this.mLegacyIcon = null;
            this.mLegacyLabel = null;
            this.mLegacyOnClickListener = null;
        }
        this.mLegacyIntent = null;
        this.mEntityConfidence = EntityConfidence.CREATOR.createFromParcel(in);
        this.mId = in.readString();
        this.mExtras = in.readBundle();
    }

    private static Drawable maybeLoadDrawable(Icon icon) {
        if (icon == null) {
            return null;
        }
        switch (icon.getType()) {
            case 1:
                return new BitmapDrawable(Resources.getSystem(), icon.getBitmap());
            case 2:
            case 4:
            default:
                return null;
            case 3:
                return new BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(icon.getDataBytes(), icon.getDataOffset(), icon.getDataLength()));
            case 5:
                return new AdaptiveIconDrawable((Drawable) null, new BitmapDrawable(Resources.getSystem(), icon.getBitmap()));
        }
    }
}
