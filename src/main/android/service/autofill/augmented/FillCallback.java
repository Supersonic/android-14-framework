package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.service.autofill.Dataset;
import android.service.autofill.augmented.AugmentedAutofillService;
import android.util.Log;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class FillCallback {
    private static final String TAG = FillCallback.class.getSimpleName();
    private final AugmentedAutofillService.AutofillProxy mProxy;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FillCallback(AugmentedAutofillService.AutofillProxy proxy) {
        this.mProxy = proxy;
    }

    public void onSuccess(FillResponse response) {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "onSuccess(): " + response);
        }
        if (response == null) {
            this.mProxy.logEvent(1);
            this.mProxy.reportResult(null, null, false);
            return;
        }
        List<Dataset> inlineSuggestions = response.getInlineSuggestions();
        Bundle clientState = response.getClientState();
        FillWindow fillWindow = response.getFillWindow();
        boolean showingFillWindow = false;
        if (inlineSuggestions != null && !inlineSuggestions.isEmpty()) {
            this.mProxy.logEvent(4);
        } else if (fillWindow != null) {
            fillWindow.show();
            showingFillWindow = true;
        }
        this.mProxy.reportResult(inlineSuggestions, clientState, showingFillWindow);
    }
}
