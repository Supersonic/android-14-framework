package android.service.autofill;

import android.service.autofill.Dataset;
import android.view.autofill.AutofillValue;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class Field {
    private Dataset.DatasetFieldFilter mFilter;
    private Presentations mPresentations;
    private AutofillValue mValue;

    Field(AutofillValue value, Dataset.DatasetFieldFilter filter, Presentations presentations) {
        this.mValue = value;
        this.mFilter = filter;
        this.mPresentations = presentations;
    }

    public AutofillValue getValue() {
        return this.mValue;
    }

    public Dataset.DatasetFieldFilter getDatasetFieldFilter() {
        return this.mFilter;
    }

    public Pattern getFilter() {
        Dataset.DatasetFieldFilter datasetFieldFilter = this.mFilter;
        if (datasetFieldFilter == null) {
            return null;
        }
        return datasetFieldFilter.pattern;
    }

    public Presentations getPresentations() {
        return this.mPresentations;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private AutofillValue mValue = null;
        private Dataset.DatasetFieldFilter mFilter = null;
        private Presentations mPresentations = null;
        private boolean mDestroyed = false;

        public Builder setValue(AutofillValue value) {
            checkNotUsed();
            this.mValue = value;
            return this;
        }

        public Builder setFilter(Pattern value) {
            checkNotUsed();
            this.mFilter = new Dataset.DatasetFieldFilter(value);
            return this;
        }

        public Builder setPresentations(Presentations value) {
            checkNotUsed();
            this.mPresentations = value;
            return this;
        }

        public Field build() {
            checkNotUsed();
            this.mDestroyed = true;
            Field o = new Field(this.mValue, this.mFilter, this.mPresentations);
            return o;
        }

        private void checkNotUsed() {
            if (this.mDestroyed) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
