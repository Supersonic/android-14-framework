package android.service.autofill;

import android.content.IntentSender;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.view.autofill.AutofillId;
import android.view.autofill.Helper;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SaveInfo implements Parcelable {
    public static final Parcelable.Creator<SaveInfo> CREATOR = new Parcelable.Creator<SaveInfo>() { // from class: android.service.autofill.SaveInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SaveInfo createFromParcel(Parcel parcel) {
            Builder builder;
            int type = parcel.readInt();
            AutofillId[] requiredIds = (AutofillId[]) parcel.readParcelableArray(null, AutofillId.class);
            if (requiredIds != null) {
                builder = new Builder(type, requiredIds);
            } else {
                builder = new Builder(type);
            }
            AutofillId[] optionalIds = (AutofillId[]) parcel.readParcelableArray(null, AutofillId.class);
            if (optionalIds != null) {
                builder.setOptionalIds(optionalIds);
            }
            builder.setNegativeAction(parcel.readInt(), (IntentSender) parcel.readParcelable(null, IntentSender.class));
            builder.setPositiveAction(parcel.readInt());
            builder.setDescription(parcel.readCharSequence());
            CustomDescription customDescripton = (CustomDescription) parcel.readParcelable(null, CustomDescription.class);
            if (customDescripton != null) {
                builder.setCustomDescription(customDescripton);
            }
            InternalValidator validator = (InternalValidator) parcel.readParcelable(null, InternalValidator.class);
            if (validator != null) {
                builder.setValidator(validator);
            }
            InternalSanitizer[] sanitizers = (InternalSanitizer[]) parcel.readParcelableArray(null, InternalSanitizer.class);
            if (sanitizers != null) {
                for (InternalSanitizer internalSanitizer : sanitizers) {
                    AutofillId[] autofillIds = (AutofillId[]) parcel.readParcelableArray(null, AutofillId.class);
                    builder.addSanitizer(internalSanitizer, autofillIds);
                }
            }
            AutofillId triggerId = (AutofillId) parcel.readParcelable(null, AutofillId.class);
            if (triggerId != null) {
                builder.setTriggerId(triggerId);
            }
            builder.setFlags(parcel.readInt());
            return builder.build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SaveInfo[] newArray(int size) {
            return new SaveInfo[size];
        }
    };
    public static final int FLAG_DELAY_SAVE = 4;
    public static final int FLAG_DONT_SAVE_ON_FINISH = 2;
    public static final int FLAG_SAVE_ON_ALL_VIEWS_INVISIBLE = 1;
    public static final int NEGATIVE_BUTTON_STYLE_CANCEL = 0;
    public static final int NEGATIVE_BUTTON_STYLE_NEVER = 2;
    public static final int NEGATIVE_BUTTON_STYLE_REJECT = 1;
    public static final int POSITIVE_BUTTON_STYLE_CONTINUE = 1;
    public static final int POSITIVE_BUTTON_STYLE_SAVE = 0;
    public static final int SAVE_DATA_TYPE_ADDRESS = 2;
    public static final int SAVE_DATA_TYPE_CREDIT_CARD = 4;
    public static final int SAVE_DATA_TYPE_DEBIT_CARD = 32;
    public static final int SAVE_DATA_TYPE_EMAIL_ADDRESS = 16;
    public static final int SAVE_DATA_TYPE_GENERIC = 0;
    public static final int SAVE_DATA_TYPE_GENERIC_CARD = 128;
    public static final int SAVE_DATA_TYPE_PASSWORD = 1;
    public static final int SAVE_DATA_TYPE_PAYMENT_CARD = 64;
    public static final int SAVE_DATA_TYPE_USERNAME = 8;
    private final CustomDescription mCustomDescription;
    private final CharSequence mDescription;
    private final int mFlags;
    private final IntentSender mNegativeActionListener;
    private final int mNegativeButtonStyle;
    private final AutofillId[] mOptionalIds;
    private final int mPositiveButtonStyle;
    private final AutofillId[] mRequiredIds;
    private final InternalSanitizer[] mSanitizerKeys;
    private final AutofillId[][] mSanitizerValues;
    private final AutofillId mTriggerId;
    private final int mType;
    private final InternalValidator mValidator;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface NegativeButtonStyle {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface PositiveButtonStyle {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface SaveDataType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface SaveInfoFlags {
    }

    private SaveInfo(Builder builder) {
        this.mType = builder.mType;
        this.mNegativeButtonStyle = builder.mNegativeButtonStyle;
        this.mNegativeActionListener = builder.mNegativeActionListener;
        this.mPositiveButtonStyle = builder.mPositiveButtonStyle;
        this.mRequiredIds = builder.mRequiredIds;
        this.mOptionalIds = builder.mOptionalIds;
        this.mDescription = builder.mDescription;
        this.mFlags = builder.mFlags;
        this.mCustomDescription = builder.mCustomDescription;
        this.mValidator = builder.mValidator;
        if (builder.mSanitizers == null) {
            this.mSanitizerKeys = null;
            this.mSanitizerValues = null;
        } else {
            int size = builder.mSanitizers.size();
            this.mSanitizerKeys = new InternalSanitizer[size];
            this.mSanitizerValues = new AutofillId[size];
            for (int i = 0; i < size; i++) {
                this.mSanitizerKeys[i] = (InternalSanitizer) builder.mSanitizers.keyAt(i);
                this.mSanitizerValues[i] = (AutofillId[]) builder.mSanitizers.valueAt(i);
            }
        }
        this.mTriggerId = builder.mTriggerId;
    }

    public int getNegativeActionStyle() {
        return this.mNegativeButtonStyle;
    }

    public IntentSender getNegativeActionListener() {
        return this.mNegativeActionListener;
    }

    public int getPositiveActionStyle() {
        return this.mPositiveButtonStyle;
    }

    public AutofillId[] getRequiredIds() {
        return this.mRequiredIds;
    }

    public AutofillId[] getOptionalIds() {
        return this.mOptionalIds;
    }

    public int getType() {
        return this.mType;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public CustomDescription getCustomDescription() {
        return this.mCustomDescription;
    }

    public InternalValidator getValidator() {
        return this.mValidator;
    }

    public InternalSanitizer[] getSanitizerKeys() {
        return this.mSanitizerKeys;
    }

    public AutofillId[][] getSanitizerValues() {
        return this.mSanitizerValues;
    }

    public AutofillId getTriggerId() {
        return this.mTriggerId;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private CustomDescription mCustomDescription;
        private CharSequence mDescription;
        private boolean mDestroyed;
        private int mFlags;
        private IntentSender mNegativeActionListener;
        private int mNegativeButtonStyle;
        private AutofillId[] mOptionalIds;
        private int mPositiveButtonStyle;
        private final AutofillId[] mRequiredIds;
        private ArraySet<AutofillId> mSanitizerIds;
        private ArrayMap<InternalSanitizer, AutofillId[]> mSanitizers;
        private AutofillId mTriggerId;
        private final int mType;
        private InternalValidator mValidator;

        public Builder(int type, AutofillId[] requiredIds) {
            this.mNegativeButtonStyle = 0;
            this.mPositiveButtonStyle = 0;
            this.mType = type;
            this.mRequiredIds = AutofillServiceHelper.assertValid(requiredIds);
        }

        public Builder(int type) {
            this.mNegativeButtonStyle = 0;
            this.mPositiveButtonStyle = 0;
            this.mType = type;
            this.mRequiredIds = null;
        }

        public Builder setFlags(int flags) {
            throwIfDestroyed();
            this.mFlags = Preconditions.checkFlagsArgument(flags, 7);
            return this;
        }

        public Builder setOptionalIds(AutofillId[] ids) {
            throwIfDestroyed();
            this.mOptionalIds = AutofillServiceHelper.assertValid(ids);
            return this;
        }

        public Builder setDescription(CharSequence description) {
            throwIfDestroyed();
            Preconditions.checkState(this.mCustomDescription == null, "Can call setDescription() or setCustomDescription(), but not both");
            this.mDescription = description;
            return this;
        }

        public Builder setCustomDescription(CustomDescription customDescription) {
            throwIfDestroyed();
            Preconditions.checkState(this.mDescription == null, "Can call setDescription() or setCustomDescription(), but not both");
            this.mCustomDescription = customDescription;
            return this;
        }

        public Builder setNegativeAction(int style, IntentSender listener) {
            throwIfDestroyed();
            Preconditions.checkArgumentInRange(style, 0, 2, "style");
            this.mNegativeButtonStyle = style;
            this.mNegativeActionListener = listener;
            return this;
        }

        public Builder setPositiveAction(int style) {
            throwIfDestroyed();
            Preconditions.checkArgumentInRange(style, 0, 1, "style");
            this.mPositiveButtonStyle = style;
            return this;
        }

        public Builder setValidator(Validator validator) {
            throwIfDestroyed();
            Preconditions.checkArgument(validator instanceof InternalValidator, "not provided by Android System: %s", validator);
            this.mValidator = (InternalValidator) validator;
            return this;
        }

        public Builder addSanitizer(Sanitizer sanitizer, AutofillId... ids) {
            throwIfDestroyed();
            Preconditions.checkArgument(!ArrayUtils.isEmpty(ids), "ids cannot be empty or null");
            Preconditions.checkArgument(sanitizer instanceof InternalSanitizer, "not provided by Android System: %s", sanitizer);
            if (this.mSanitizers == null) {
                this.mSanitizers = new ArrayMap<>();
                this.mSanitizerIds = new ArraySet<>(ids.length);
            }
            for (AutofillId id : ids) {
                Preconditions.checkArgument(!this.mSanitizerIds.contains(id), "already added %s", id);
                this.mSanitizerIds.add(id);
            }
            this.mSanitizers.put((InternalSanitizer) sanitizer, ids);
            return this;
        }

        public Builder setTriggerId(AutofillId id) {
            throwIfDestroyed();
            this.mTriggerId = (AutofillId) Objects.requireNonNull(id);
            return this;
        }

        public SaveInfo build() {
            throwIfDestroyed();
            this.mDestroyed = true;
            return new SaveInfo(this);
        }

        private void throwIfDestroyed() {
            if (this.mDestroyed) {
                throw new IllegalStateException("Already called #build()");
            }
        }
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder builder = new StringBuilder("SaveInfo: [type=").append(DebugUtils.flagsToString(SaveInfo.class, "SAVE_DATA_TYPE_", this.mType)).append(", requiredIds=").append(Arrays.toString(this.mRequiredIds)).append(", negative style=").append(DebugUtils.flagsToString(SaveInfo.class, "NEGATIVE_BUTTON_STYLE_", this.mNegativeButtonStyle)).append(", positive style=").append(DebugUtils.flagsToString(SaveInfo.class, "POSITIVE_BUTTON_STYLE_", this.mPositiveButtonStyle));
            if (this.mOptionalIds != null) {
                builder.append(", optionalIds=").append(Arrays.toString(this.mOptionalIds));
            }
            if (this.mDescription != null) {
                builder.append(", description=").append(this.mDescription);
            }
            if (this.mFlags != 0) {
                builder.append(", flags=").append(this.mFlags);
            }
            if (this.mCustomDescription != null) {
                builder.append(", customDescription=").append(this.mCustomDescription);
            }
            if (this.mValidator != null) {
                builder.append(", validator=").append(this.mValidator);
            }
            if (this.mSanitizerKeys != null) {
                builder.append(", sanitizerKeys=").append(this.mSanitizerKeys.length);
            }
            if (this.mSanitizerValues != null) {
                builder.append(", sanitizerValues=").append(this.mSanitizerValues.length);
            }
            if (this.mTriggerId != null) {
                builder.append(", triggerId=").append(this.mTriggerId);
            }
            return builder.append(NavigationBarInflaterView.SIZE_MOD_END).toString();
        }
        return super.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        parcel.writeParcelableArray(this.mRequiredIds, flags);
        parcel.writeParcelableArray(this.mOptionalIds, flags);
        parcel.writeInt(this.mNegativeButtonStyle);
        parcel.writeParcelable(this.mNegativeActionListener, flags);
        parcel.writeInt(this.mPositiveButtonStyle);
        parcel.writeCharSequence(this.mDescription);
        parcel.writeParcelable(this.mCustomDescription, flags);
        parcel.writeParcelable(this.mValidator, flags);
        parcel.writeParcelableArray(this.mSanitizerKeys, flags);
        if (this.mSanitizerKeys != null) {
            int i = 0;
            while (true) {
                AutofillId[][] autofillIdArr = this.mSanitizerValues;
                if (i >= autofillIdArr.length) {
                    break;
                }
                parcel.writeParcelableArray(autofillIdArr[i], flags);
                i++;
            }
        }
        parcel.writeParcelable(this.mTriggerId, flags);
        parcel.writeInt(this.mFlags);
    }
}
