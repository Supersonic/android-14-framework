package android.view.autofill;

import android.p008os.CancellationSignal;
import android.service.autofill.FillCallback;
import android.view.inputmethod.InlineSuggestionsRequest;
/* loaded from: classes4.dex */
public interface AutofillRequestCallback {
    void onFillRequest(InlineSuggestionsRequest inlineSuggestionsRequest, CancellationSignal cancellationSignal, FillCallback fillCallback);
}
