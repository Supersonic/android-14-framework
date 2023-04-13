package android.telecom;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IBinder;
/* loaded from: classes3.dex */
public class AuthenticatorService extends Service {
    private static Authenticator mAuthenticator;

    @Override // android.app.Service
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    /* loaded from: classes3.dex */
    public class Authenticator extends AbstractAccountAuthenticator {
        public Authenticator(Context context) {
            super(context);
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
            throw new UnsupportedOperationException();
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s2, String[] strings, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public String getAuthTokenLabel(String s) {
            throw new UnsupportedOperationException();
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override // android.accounts.AbstractAccountAuthenticator
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }
    }
}
