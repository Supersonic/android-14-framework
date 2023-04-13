package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
import android.service.autofill.augmented.AugmentedAutofillService;
import android.util.Log;
import android.util.Pair;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class FillController {
    private static final String TAG = FillController.class.getSimpleName();
    private final AugmentedAutofillService.AutofillProxy mProxy;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FillController(AugmentedAutofillService.AutofillProxy proxy) {
        this.mProxy = proxy;
    }

    public void autofill(List<Pair<AutofillId, AutofillValue>> values) {
        Objects.requireNonNull(values);
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "autofill() with " + values.size() + " values");
        }
        try {
            this.mProxy.autofill(values);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
        FillWindow fillWindow = this.mProxy.getFillWindow();
        if (fillWindow != null) {
            fillWindow.destroy();
        }
    }
}
