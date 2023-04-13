package android.accounts;

import android.app.Activity;
import android.companion.CompanionDeviceManager;
import android.p008os.Bundle;
@Deprecated
/* loaded from: classes.dex */
public class AccountAuthenticatorActivity extends Activity {
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    public final void setAccountAuthenticatorResult(Bundle result) {
        this.mResultBundle = result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        AccountAuthenticatorResponse accountAuthenticatorResponse = (AccountAuthenticatorResponse) getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, AccountAuthenticatorResponse.class);
        this.mAccountAuthenticatorResponse = accountAuthenticatorResponse;
        if (accountAuthenticatorResponse != null) {
            accountAuthenticatorResponse.onRequestContinued();
        }
    }

    @Override // android.app.Activity
    public void finish() {
        AccountAuthenticatorResponse accountAuthenticatorResponse = this.mAccountAuthenticatorResponse;
        if (accountAuthenticatorResponse != null) {
            Bundle bundle = this.mResultBundle;
            if (bundle != null) {
                accountAuthenticatorResponse.onResult(bundle);
            } else {
                accountAuthenticatorResponse.onError(4, CompanionDeviceManager.REASON_CANCELED);
            }
            this.mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}
