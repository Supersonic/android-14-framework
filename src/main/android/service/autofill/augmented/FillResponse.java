package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.service.autofill.Dataset;
import java.util.ArrayList;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class FillResponse {
    private Bundle mClientState;
    private FillWindow mFillWindow;
    private List<Dataset> mInlineSuggestions;

    /* JADX INFO: Access modifiers changed from: private */
    public static FillWindow defaultFillWindow() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<Dataset> defaultInlineSuggestions() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Bundle defaultClientState() {
        return null;
    }

    /* loaded from: classes3.dex */
    static abstract class BaseBuilder {
        abstract Builder addInlineSuggestion(Dataset dataset);

        BaseBuilder() {
        }
    }

    FillResponse(FillWindow fillWindow, List<Dataset> inlineSuggestions, Bundle clientState) {
        this.mFillWindow = fillWindow;
        this.mInlineSuggestions = inlineSuggestions;
        this.mClientState = clientState;
    }

    public FillWindow getFillWindow() {
        return this.mFillWindow;
    }

    public List<Dataset> getInlineSuggestions() {
        return this.mInlineSuggestions;
    }

    public Bundle getClientState() {
        return this.mClientState;
    }

    /* loaded from: classes3.dex */
    public static final class Builder extends BaseBuilder {
        private long mBuilderFieldsSet = 0;
        private Bundle mClientState;
        private FillWindow mFillWindow;
        private List<Dataset> mInlineSuggestions;

        public Builder setFillWindow(FillWindow value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mFillWindow = value;
            return this;
        }

        public Builder setInlineSuggestions(List<Dataset> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mInlineSuggestions = value;
            return this;
        }

        @Override // android.service.autofill.augmented.FillResponse.BaseBuilder
        Builder addInlineSuggestion(Dataset value) {
            if (this.mInlineSuggestions == null) {
                setInlineSuggestions(new ArrayList());
            }
            this.mInlineSuggestions.add(value);
            return this;
        }

        public Builder setClientState(Bundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mClientState = value;
            return this;
        }

        public FillResponse build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 8;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mFillWindow = FillResponse.defaultFillWindow();
            }
            if ((this.mBuilderFieldsSet & 2) == 0) {
                this.mInlineSuggestions = FillResponse.defaultInlineSuggestions();
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mClientState = FillResponse.defaultClientState();
            }
            FillResponse o = new FillResponse(this.mFillWindow, this.mInlineSuggestions, this.mClientState);
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
