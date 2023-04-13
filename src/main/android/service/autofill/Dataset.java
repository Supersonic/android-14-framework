package android.service.autofill;

import android.annotation.SystemApi;
import android.content.ClipData;
import android.content.IntentSender;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.service.autofill.Presentations;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import android.widget.RemoteViews;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class Dataset implements Parcelable {
    public static final Parcelable.Creator<Dataset> CREATOR = new Parcelable.Creator<Dataset>() { // from class: android.service.autofill.Dataset.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Dataset createFromParcel(Parcel parcel) {
            int eligibleReason;
            Presentations.Builder presentationsBuilder;
            Builder builder;
            RemoteViews presentation = (RemoteViews) parcel.readParcelable(null, RemoteViews.class);
            RemoteViews dialogPresentation = (RemoteViews) parcel.readParcelable(null, RemoteViews.class);
            InlinePresentation inlinePresentation = (InlinePresentation) parcel.readParcelable(null, InlinePresentation.class);
            InlinePresentation inlineTooltipPresentation = (InlinePresentation) parcel.readParcelable(null, InlinePresentation.class);
            ArrayList<AutofillId> ids = parcel.createTypedArrayList(AutofillId.CREATOR);
            ArrayList<AutofillValue> values = parcel.createTypedArrayList(AutofillValue.CREATOR);
            ArrayList<RemoteViews> presentations = parcel.createTypedArrayList(RemoteViews.CREATOR);
            ArrayList<RemoteViews> dialogPresentations = parcel.createTypedArrayList(RemoteViews.CREATOR);
            ArrayList<InlinePresentation> inlinePresentations = parcel.createTypedArrayList(InlinePresentation.CREATOR);
            ArrayList<InlinePresentation> inlineTooltipPresentations = parcel.createTypedArrayList(InlinePresentation.CREATOR);
            ArrayList<DatasetFieldFilter> filters = parcel.createTypedArrayList(DatasetFieldFilter.CREATOR);
            ArrayList<String> autofillDatatypes = parcel.createStringArrayList();
            ClipData fieldContent = (ClipData) parcel.readParcelable(null, ClipData.class);
            IntentSender authentication = (IntentSender) parcel.readParcelable(null, IntentSender.class);
            String datasetId = parcel.readString();
            int eligibleReason2 = parcel.readInt();
            if (presentation != null || inlinePresentation != null || dialogPresentation != null) {
                Presentations.Builder presentationsBuilder2 = new Presentations.Builder();
                if (presentation == null) {
                    eligibleReason = eligibleReason2;
                    presentationsBuilder = presentationsBuilder2;
                } else {
                    eligibleReason = eligibleReason2;
                    presentationsBuilder = presentationsBuilder2;
                    presentationsBuilder.setMenuPresentation(presentation);
                }
                if (inlinePresentation != null) {
                    presentationsBuilder.setInlinePresentation(inlinePresentation);
                }
                if (inlineTooltipPresentation != null) {
                    presentationsBuilder.setInlineTooltipPresentation(inlineTooltipPresentation);
                }
                if (dialogPresentation != null) {
                    presentationsBuilder.setDialogPresentation(dialogPresentation);
                }
                builder = new Builder(presentationsBuilder.build());
            } else {
                eligibleReason = eligibleReason2;
                builder = new Builder();
            }
            if (fieldContent != null) {
                builder.setContent(ids.get(0), fieldContent);
            }
            int inlinePresentationsSize = inlinePresentations.size();
            if (ids.size() == 0 && autofillDatatypes.size() > 0) {
                int i = 0;
                while (true) {
                    InlinePresentation inlinePresentation2 = inlinePresentation;
                    if (i >= autofillDatatypes.size()) {
                        break;
                    }
                    String datatype = autofillDatatypes.get(i);
                    AutofillValue value = values.get(i);
                    RemoteViews fieldPresentation = presentations.get(i);
                    RemoteViews fieldDialogPresentation = dialogPresentations.get(i);
                    InlinePresentation fieldInlinePresentation = i < inlinePresentationsSize ? inlinePresentations.get(i) : null;
                    InlinePresentation fieldInlineTooltipPresentation = i < inlinePresentationsSize ? inlineTooltipPresentations.get(i) : null;
                    DatasetFieldFilter filter = filters.get(i);
                    builder.setLifeTheUniverseAndEverything(datatype, value, fieldPresentation, fieldInlinePresentation, fieldInlineTooltipPresentation, filter, fieldDialogPresentation);
                    i++;
                    inlinePresentation = inlinePresentation2;
                }
            } else {
                int i2 = 0;
                while (i2 < ids.size()) {
                    AutofillId id = ids.get(i2);
                    AutofillValue value2 = values.get(i2);
                    RemoteViews fieldPresentation2 = presentations.get(i2);
                    RemoteViews fieldDialogPresentation2 = dialogPresentations.get(i2);
                    InlinePresentation fieldInlinePresentation2 = i2 < inlinePresentationsSize ? inlinePresentations.get(i2) : null;
                    InlinePresentation fieldInlineTooltipPresentation2 = i2 < inlinePresentationsSize ? inlineTooltipPresentations.get(i2) : null;
                    DatasetFieldFilter filter2 = filters.get(i2);
                    builder.setLifeTheUniverseAndEverything(id, value2, fieldPresentation2, fieldInlinePresentation2, fieldInlineTooltipPresentation2, filter2, fieldDialogPresentation2);
                    i2++;
                }
            }
            builder.setAuthentication(authentication);
            builder.setId(datasetId);
            Dataset dataset = builder.build();
            dataset.mEligibleReason = eligibleReason;
            return dataset;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Dataset[] newArray(int size) {
            return new Dataset[size];
        }
    };
    public static final int PICK_REASON_AUTOFILL_PROVIDER_DETECTION = 1;
    public static final int PICK_REASON_AUTOFILL_PROVIDER_FALLBACK = 4;
    public static final int PICK_REASON_FRAMEWORK_DETECTION = 3;
    public static final int PICK_REASON_FRAMEWORK_FALLBACK = 6;
    public static final int PICK_REASON_PCC_DETECTION = 2;
    public static final int PICK_REASON_PCC_DETECTION_FALLBACK = 5;
    public static final int PICK_REASON_UNKNOWN = 0;
    private final IntentSender mAuthentication;
    private final ArrayList<String> mAutofillDatatypes;
    private final RemoteViews mDialogPresentation;
    private int mEligibleReason;
    private final ClipData mFieldContent;
    private final ArrayList<RemoteViews> mFieldDialogPresentations;
    private final ArrayList<DatasetFieldFilter> mFieldFilters;
    private final ArrayList<AutofillId> mFieldIds;
    private final ArrayList<InlinePresentation> mFieldInlinePresentations;
    private final ArrayList<InlinePresentation> mFieldInlineTooltipPresentations;
    private final ArrayList<RemoteViews> mFieldPresentations;
    private final ArrayList<AutofillValue> mFieldValues;
    String mId;
    private final InlinePresentation mInlinePresentation;
    private final InlinePresentation mInlineTooltipPresentation;
    private final RemoteViews mPresentation;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface DatasetEligibleReason {
    }

    public Dataset(ArrayList<AutofillId> fieldIds, ArrayList<AutofillValue> fieldValues, ArrayList<RemoteViews> fieldPresentations, ArrayList<RemoteViews> fieldDialogPresentations, ArrayList<InlinePresentation> fieldInlinePresentations, ArrayList<InlinePresentation> fieldInlineTooltipPresentations, ArrayList<DatasetFieldFilter> fieldFilters, ArrayList<String> autofillDatatypes, ClipData fieldContent, RemoteViews presentation, RemoteViews dialogPresentation, InlinePresentation inlinePresentation, InlinePresentation inlineTooltipPresentation, String id, IntentSender authentication) {
        this.mFieldIds = fieldIds;
        this.mFieldValues = fieldValues;
        this.mFieldPresentations = fieldPresentations;
        this.mFieldDialogPresentations = fieldDialogPresentations;
        this.mFieldInlinePresentations = fieldInlinePresentations;
        this.mFieldInlineTooltipPresentations = fieldInlineTooltipPresentations;
        this.mAutofillDatatypes = autofillDatatypes;
        this.mFieldFilters = fieldFilters;
        this.mFieldContent = fieldContent;
        this.mPresentation = presentation;
        this.mDialogPresentation = dialogPresentation;
        this.mInlinePresentation = inlinePresentation;
        this.mInlineTooltipPresentation = inlineTooltipPresentation;
        this.mAuthentication = authentication;
        this.mId = id;
    }

    public Dataset(Dataset dataset, ArrayList<AutofillId> ids) {
        this.mFieldIds = ids;
        this.mFieldValues = dataset.mFieldValues;
        this.mFieldPresentations = dataset.mFieldPresentations;
        this.mFieldDialogPresentations = dataset.mFieldDialogPresentations;
        this.mFieldInlinePresentations = dataset.mFieldInlinePresentations;
        this.mFieldInlineTooltipPresentations = dataset.mFieldInlineTooltipPresentations;
        this.mFieldFilters = dataset.mFieldFilters;
        this.mFieldContent = dataset.mFieldContent;
        this.mPresentation = dataset.mPresentation;
        this.mDialogPresentation = dataset.mDialogPresentation;
        this.mInlinePresentation = dataset.mInlinePresentation;
        this.mInlineTooltipPresentation = dataset.mInlineTooltipPresentation;
        this.mAuthentication = dataset.mAuthentication;
        this.mId = dataset.mId;
        this.mAutofillDatatypes = dataset.mAutofillDatatypes;
    }

    private Dataset(Builder builder) {
        this.mFieldIds = builder.mFieldIds;
        this.mFieldValues = builder.mFieldValues;
        this.mFieldPresentations = builder.mFieldPresentations;
        this.mFieldDialogPresentations = builder.mFieldDialogPresentations;
        this.mFieldInlinePresentations = builder.mFieldInlinePresentations;
        this.mFieldInlineTooltipPresentations = builder.mFieldInlineTooltipPresentations;
        this.mFieldFilters = builder.mFieldFilters;
        this.mFieldContent = builder.mFieldContent;
        this.mPresentation = builder.mPresentation;
        this.mDialogPresentation = builder.mDialogPresentation;
        this.mInlinePresentation = builder.mInlinePresentation;
        this.mInlineTooltipPresentation = builder.mInlineTooltipPresentation;
        this.mAuthentication = builder.mAuthentication;
        this.mId = builder.mId;
        this.mAutofillDatatypes = builder.mAutofillDatatypes;
    }

    public ArrayList<String> getAutofillDatatypes() {
        return this.mAutofillDatatypes;
    }

    public ArrayList<AutofillId> getFieldIds() {
        return this.mFieldIds;
    }

    public ArrayList<AutofillValue> getFieldValues() {
        return this.mFieldValues;
    }

    public RemoteViews getFieldPresentation(int index) {
        RemoteViews customPresentation = this.mFieldPresentations.get(index);
        return customPresentation != null ? customPresentation : this.mPresentation;
    }

    public RemoteViews getFieldDialogPresentation(int index) {
        RemoteViews customPresentation = this.mFieldDialogPresentations.get(index);
        return customPresentation != null ? customPresentation : this.mDialogPresentation;
    }

    public InlinePresentation getFieldInlinePresentation(int index) {
        InlinePresentation inlinePresentation = this.mFieldInlinePresentations.get(index);
        return inlinePresentation != null ? inlinePresentation : this.mInlinePresentation;
    }

    public InlinePresentation getFieldInlineTooltipPresentation(int index) {
        InlinePresentation inlineTooltipPresentation = this.mFieldInlineTooltipPresentations.get(index);
        return inlineTooltipPresentation != null ? inlineTooltipPresentation : this.mInlineTooltipPresentation;
    }

    public DatasetFieldFilter getFilter(int index) {
        return this.mFieldFilters.get(index);
    }

    public ClipData getFieldContent() {
        return this.mFieldContent;
    }

    public IntentSender getAuthentication() {
        return this.mAuthentication;
    }

    public boolean isEmpty() {
        ArrayList<AutofillId> arrayList = this.mFieldIds;
        return arrayList == null || arrayList.isEmpty();
    }

    public String toString() {
        if (Helper.sDebug) {
            StringBuilder builder = new StringBuilder("Dataset[");
            if (this.mId == null) {
                builder.append("noId");
            } else {
                builder.append("id=").append(this.mId.length()).append("_chars");
            }
            if (this.mFieldIds != null) {
                builder.append(", fieldIds=").append(this.mFieldIds);
            }
            if (this.mFieldValues != null) {
                builder.append(", fieldValues=").append(this.mFieldValues);
            }
            if (this.mFieldContent != null) {
                builder.append(", fieldContent=").append(this.mFieldContent);
            }
            if (this.mFieldPresentations != null) {
                builder.append(", fieldPresentations=").append(this.mFieldPresentations.size());
            }
            if (this.mFieldDialogPresentations != null) {
                builder.append(", fieldDialogPresentations=").append(this.mFieldDialogPresentations.size());
            }
            if (this.mFieldInlinePresentations != null) {
                builder.append(", fieldInlinePresentations=").append(this.mFieldInlinePresentations.size());
            }
            if (this.mFieldInlineTooltipPresentations != null) {
                builder.append(", fieldInlineTooltipInlinePresentations=").append(this.mFieldInlineTooltipPresentations.size());
            }
            if (this.mFieldFilters != null) {
                builder.append(", fieldFilters=").append(this.mFieldFilters.size());
            }
            if (this.mPresentation != null) {
                builder.append(", hasPresentation");
            }
            if (this.mDialogPresentation != null) {
                builder.append(", hasDialogPresentation");
            }
            if (this.mInlinePresentation != null) {
                builder.append(", hasInlinePresentation");
            }
            if (this.mInlineTooltipPresentation != null) {
                builder.append(", hasInlineTooltipPresentation");
            }
            if (this.mAuthentication != null) {
                builder.append(", hasAuthentication");
            }
            if (this.mAutofillDatatypes != null) {
                builder.append(", autofillDatatypes=").append(this.mAutofillDatatypes);
            }
            return builder.append(']').toString();
        }
        return super.toString();
    }

    public String getId() {
        return this.mId;
    }

    public void setEligibleReasonReason(int eligibleReason) {
        this.mEligibleReason = eligibleReason;
    }

    public int getEligibleReason() {
        return this.mEligibleReason;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private IntentSender mAuthentication;
        private ArrayList<String> mAutofillDatatypes;
        private boolean mDestroyed;
        private RemoteViews mDialogPresentation;
        private ClipData mFieldContent;
        private ArrayList<RemoteViews> mFieldDialogPresentations;
        private ArrayList<DatasetFieldFilter> mFieldFilters;
        private ArrayList<AutofillId> mFieldIds;
        private ArrayList<InlinePresentation> mFieldInlinePresentations;
        private ArrayList<InlinePresentation> mFieldInlineTooltipPresentations;
        private ArrayList<RemoteViews> mFieldPresentations;
        private ArrayList<AutofillValue> mFieldValues;
        private String mId;
        private InlinePresentation mInlinePresentation;
        private InlinePresentation mInlineTooltipPresentation;
        private RemoteViews mPresentation;

        @Deprecated
        public Builder(RemoteViews presentation) {
            Objects.requireNonNull(presentation, "presentation must be non-null");
            this.mPresentation = presentation;
        }

        @SystemApi
        @Deprecated
        public Builder(InlinePresentation inlinePresentation) {
            Objects.requireNonNull(inlinePresentation, "inlinePresentation must be non-null");
            this.mInlinePresentation = inlinePresentation;
        }

        public Builder(Presentations presentations) {
            Objects.requireNonNull(presentations, "presentations must be non-null");
            this.mPresentation = presentations.getMenuPresentation();
            this.mInlinePresentation = presentations.getInlinePresentation();
            this.mInlineTooltipPresentation = presentations.getInlineTooltipPresentation();
            this.mDialogPresentation = presentations.getDialogPresentation();
        }

        public Builder() {
        }

        @Deprecated
        public Builder setInlinePresentation(InlinePresentation inlinePresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(inlinePresentation, "inlinePresentation must be non-null");
            this.mInlinePresentation = inlinePresentation;
            return this;
        }

        @Deprecated
        public Builder setInlinePresentation(InlinePresentation inlinePresentation, InlinePresentation inlineTooltipPresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(inlinePresentation, "inlinePresentation must be non-null");
            Objects.requireNonNull(inlineTooltipPresentation, "inlineTooltipPresentation must be non-null");
            this.mInlinePresentation = inlinePresentation;
            this.mInlineTooltipPresentation = inlineTooltipPresentation;
            return this;
        }

        public Builder setAuthentication(IntentSender authentication) {
            throwIfDestroyed();
            this.mAuthentication = authentication;
            return this;
        }

        public Builder setId(String id) {
            throwIfDestroyed();
            this.mId = id;
            return this;
        }

        @SystemApi
        public Builder setContent(AutofillId id, ClipData content) {
            throwIfDestroyed();
            if (content != null) {
                for (int i = 0; i < content.getItemCount(); i++) {
                    Preconditions.checkArgument(content.getItemAt(i).getIntent() == null, "Content items cannot contain an Intent: content=" + content);
                }
            }
            setLifeTheUniverseAndEverything(id, (AutofillValue) null, (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, (DatasetFieldFilter) null, (RemoteViews) null);
            this.mFieldContent = content;
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value) {
            throwIfDestroyed();
            setLifeTheUniverseAndEverything(id, value, (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, (DatasetFieldFilter) null, (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, RemoteViews presentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, (InlinePresentation) null, (InlinePresentation) null, (DatasetFieldFilter) null, (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, Pattern filter) {
            throwIfDestroyed();
            Preconditions.checkState(this.mPresentation != null, "Dataset presentation not set on constructor");
            setLifeTheUniverseAndEverything(id, value, (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, new DatasetFieldFilter(filter), (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, Pattern filter, RemoteViews presentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, (InlinePresentation) null, (InlinePresentation) null, new DatasetFieldFilter(filter), (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, RemoteViews presentation, InlinePresentation inlinePresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            Objects.requireNonNull(inlinePresentation, "inlinePresentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, inlinePresentation, (InlinePresentation) null, (DatasetFieldFilter) null, (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, RemoteViews presentation, InlinePresentation inlinePresentation, InlinePresentation inlineTooltipPresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            Objects.requireNonNull(inlinePresentation, "inlinePresentation cannot be null");
            Objects.requireNonNull(inlineTooltipPresentation, "inlineTooltipPresentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, inlinePresentation, inlineTooltipPresentation, (DatasetFieldFilter) null, (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, Pattern filter, RemoteViews presentation, InlinePresentation inlinePresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            Objects.requireNonNull(inlinePresentation, "inlinePresentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, inlinePresentation, (InlinePresentation) null, new DatasetFieldFilter(filter), (RemoteViews) null);
            return this;
        }

        @Deprecated
        public Builder setValue(AutofillId id, AutofillValue value, Pattern filter, RemoteViews presentation, InlinePresentation inlinePresentation, InlinePresentation inlineTooltipPresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(presentation, "presentation cannot be null");
            Objects.requireNonNull(inlinePresentation, "inlinePresentation cannot be null");
            Objects.requireNonNull(inlineTooltipPresentation, "inlineTooltipPresentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, presentation, inlinePresentation, inlineTooltipPresentation, new DatasetFieldFilter(filter), (RemoteViews) null);
            return this;
        }

        public Builder setField(AutofillId id, Field field) {
            throwIfDestroyed();
            if (field == null) {
                setLifeTheUniverseAndEverything(id, (AutofillValue) null, (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, (DatasetFieldFilter) null, (RemoteViews) null);
            } else {
                DatasetFieldFilter filter = field.getDatasetFieldFilter();
                Presentations presentations = field.getPresentations();
                if (presentations == null) {
                    setLifeTheUniverseAndEverything(id, field.getValue(), (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, filter, (RemoteViews) null);
                } else {
                    setLifeTheUniverseAndEverything(id, field.getValue(), presentations.getMenuPresentation(), presentations.getInlinePresentation(), presentations.getInlineTooltipPresentation(), filter, presentations.getDialogPresentation());
                }
            }
            return this;
        }

        public Builder setField(String hint, Field field) {
            throwIfDestroyed();
            DatasetFieldFilter filter = field.getDatasetFieldFilter();
            Presentations presentations = field.getPresentations();
            if (presentations == null) {
                setLifeTheUniverseAndEverything(hint, field.getValue(), (RemoteViews) null, (InlinePresentation) null, (InlinePresentation) null, filter, (RemoteViews) null);
            } else {
                setLifeTheUniverseAndEverything(hint, field.getValue(), presentations.getMenuPresentation(), presentations.getInlinePresentation(), presentations.getInlineTooltipPresentation(), filter, presentations.getDialogPresentation());
            }
            return this;
        }

        public Builder setFieldForAllHints(Field field) {
            return setField(AutofillManager.ANY_HINT, field);
        }

        @SystemApi
        @Deprecated
        public Builder setFieldInlinePresentation(AutofillId id, AutofillValue value, Pattern filter, InlinePresentation inlinePresentation) {
            throwIfDestroyed();
            Objects.requireNonNull(inlinePresentation, "inlinePresentation cannot be null");
            setLifeTheUniverseAndEverything(id, value, (RemoteViews) null, inlinePresentation, (InlinePresentation) null, new DatasetFieldFilter(filter), (RemoteViews) null);
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLifeTheUniverseAndEverything(String datatype, AutofillValue value, RemoteViews presentation, InlinePresentation inlinePresentation, InlinePresentation tooltip, DatasetFieldFilter filter, RemoteViews dialogPresentation) {
            if (this.mAutofillDatatypes == null) {
                this.mFieldValues = new ArrayList<>();
                this.mFieldPresentations = new ArrayList<>();
                this.mFieldDialogPresentations = new ArrayList<>();
                this.mFieldInlinePresentations = new ArrayList<>();
                this.mFieldInlineTooltipPresentations = new ArrayList<>();
                this.mFieldFilters = new ArrayList<>();
                this.mAutofillDatatypes = new ArrayList<>();
                this.mFieldIds = null;
            }
            this.mFieldValues.add(value);
            this.mFieldPresentations.add(presentation);
            this.mFieldDialogPresentations.add(dialogPresentation);
            this.mFieldInlinePresentations.add(inlinePresentation);
            this.mFieldInlineTooltipPresentations.add(tooltip);
            this.mFieldFilters.add(filter);
            this.mAutofillDatatypes.add(datatype);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLifeTheUniverseAndEverything(AutofillId id, AutofillValue value, RemoteViews presentation, InlinePresentation inlinePresentation, InlinePresentation tooltip, DatasetFieldFilter filter, RemoteViews dialogPresentation) {
            Objects.requireNonNull(id, "id cannot be null");
            ArrayList<AutofillId> arrayList = this.mFieldIds;
            if (arrayList != null) {
                int existingIdx = arrayList.indexOf(id);
                if (existingIdx >= 0) {
                    this.mFieldValues.set(existingIdx, value);
                    this.mFieldPresentations.set(existingIdx, presentation);
                    this.mFieldDialogPresentations.set(existingIdx, dialogPresentation);
                    this.mFieldInlinePresentations.set(existingIdx, inlinePresentation);
                    this.mFieldInlineTooltipPresentations.set(existingIdx, tooltip);
                    this.mFieldFilters.set(existingIdx, filter);
                    return;
                }
            } else {
                this.mFieldIds = new ArrayList<>();
                this.mFieldValues = new ArrayList<>();
                this.mFieldPresentations = new ArrayList<>();
                this.mFieldDialogPresentations = new ArrayList<>();
                this.mFieldInlinePresentations = new ArrayList<>();
                this.mFieldInlineTooltipPresentations = new ArrayList<>();
                this.mFieldFilters = new ArrayList<>();
                this.mAutofillDatatypes = null;
            }
            this.mFieldIds.add(id);
            this.mFieldValues.add(value);
            this.mFieldPresentations.add(presentation);
            this.mFieldDialogPresentations.add(dialogPresentation);
            this.mFieldInlinePresentations.add(inlinePresentation);
            this.mFieldInlineTooltipPresentations.add(tooltip);
            this.mFieldFilters.add(filter);
        }

        public Dataset build() {
            throwIfDestroyed();
            this.mDestroyed = true;
            ArrayList<AutofillId> arrayList = this.mFieldIds;
            if (arrayList == null && this.mAutofillDatatypes == null) {
                throw new IllegalStateException("at least one value must be set");
            }
            if (arrayList != null && this.mAutofillDatatypes != null && arrayList.size() > 0 && this.mAutofillDatatypes.size() > 0) {
                throw new IllegalStateException("both field and datatype were populated");
            }
            if (this.mFieldContent != null) {
                if (this.mFieldIds.size() > 1) {
                    throw new IllegalStateException("when filling content, only one field can be filled");
                }
                if (this.mFieldValues.get(0) != null) {
                    throw new IllegalStateException("cannot fill both content and values");
                }
            }
            return new Dataset(this);
        }

        private void throwIfDestroyed() {
            if (this.mDestroyed) {
                throw new IllegalStateException("Already called #build()");
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mPresentation, flags);
        parcel.writeParcelable(this.mDialogPresentation, flags);
        parcel.writeParcelable(this.mInlinePresentation, flags);
        parcel.writeParcelable(this.mInlineTooltipPresentation, flags);
        parcel.writeTypedList(this.mFieldIds, flags);
        parcel.writeTypedList(this.mFieldValues, flags);
        parcel.writeTypedList(this.mFieldPresentations, flags);
        parcel.writeTypedList(this.mFieldDialogPresentations, flags);
        parcel.writeTypedList(this.mFieldInlinePresentations, flags);
        parcel.writeTypedList(this.mFieldInlineTooltipPresentations, flags);
        parcel.writeTypedList(this.mFieldFilters, flags);
        parcel.writeStringList(this.mAutofillDatatypes);
        parcel.writeParcelable(this.mFieldContent, flags);
        parcel.writeParcelable(this.mAuthentication, flags);
        parcel.writeString(this.mId);
        parcel.writeInt(this.mEligibleReason);
    }

    /* loaded from: classes3.dex */
    public static final class DatasetFieldFilter implements Parcelable {
        public static final Parcelable.Creator<DatasetFieldFilter> CREATOR = new Parcelable.Creator<DatasetFieldFilter>() { // from class: android.service.autofill.Dataset.DatasetFieldFilter.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DatasetFieldFilter createFromParcel(Parcel parcel) {
                return new DatasetFieldFilter((Pattern) parcel.readSerializable(Pattern.class.getClassLoader(), Pattern.class));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DatasetFieldFilter[] newArray(int size) {
                return new DatasetFieldFilter[size];
            }
        };
        public final Pattern pattern;

        /* JADX INFO: Access modifiers changed from: package-private */
        public DatasetFieldFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        public String toString() {
            return !Helper.sDebug ? super.toString() : this.pattern == null ? "null" : this.pattern.pattern().length() + "_chars";
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeSerializable(this.pattern);
        }
    }
}
