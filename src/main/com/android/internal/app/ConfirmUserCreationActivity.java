package com.android.internal.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.UserInfo;
import android.p008os.Bundle;
import android.p008os.PersistableBundle;
import android.p008os.UserManager;
import android.util.Log;
import com.android.internal.C4057R;
import com.android.internal.app.AlertController;
/* loaded from: classes4.dex */
public class ConfirmUserCreationActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private static final String TAG = "CreateUser";
    private static final String USER_TYPE = "android.os.usertype.full.SECONDARY";
    private String mAccountName;
    private PersistableBundle mAccountOptions;
    private String mAccountType;
    private boolean mCanProceed;
    private boolean mIsFirstClick;
    private UserManager mUserManager;
    private String mUserName;

    @Override // com.android.internal.app.AlertActivity, android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addSystemFlags(524288);
        Intent intent = getIntent();
        this.mUserName = intent.getStringExtra(UserManager.EXTRA_USER_NAME);
        this.mAccountName = intent.getStringExtra(UserManager.EXTRA_USER_ACCOUNT_NAME);
        this.mAccountType = intent.getStringExtra(UserManager.EXTRA_USER_ACCOUNT_TYPE);
        this.mAccountOptions = (PersistableBundle) intent.getParcelableExtra(UserManager.EXTRA_USER_ACCOUNT_OPTIONS, PersistableBundle.class);
        this.mUserManager = (UserManager) getSystemService(UserManager.class);
        String message = checkUserCreationRequirements();
        if (message == null) {
            finish();
            return;
        }
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mMessage = message;
        ap.mPositiveButtonText = getString(17039370);
        ap.mPositiveButtonListener = this;
        if (this.mCanProceed) {
            ap.mNegativeButtonText = getString(17039360);
            ap.mNegativeButtonListener = this;
        }
        this.mIsFirstClick = true;
        setupAlert();
    }

    private String checkUserCreationRequirements() {
        String callingPackage = getCallingPackage();
        if (callingPackage == null) {
            throw new SecurityException("User Creation intent must be launched with startActivityForResult");
        }
        try {
            boolean accountExists = false;
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(callingPackage, 0);
            boolean cantCreateUser = this.mUserManager.hasUserRestriction(UserManager.DISALLOW_ADD_USER) || !this.mUserManager.isAdminUser();
            boolean cantCreateAnyMoreUsers = !this.mUserManager.canAddMoreUsers("android.os.usertype.full.SECONDARY");
            Account account = new Account(this.mAccountName, this.mAccountType);
            if (this.mAccountName != null && this.mAccountType != null && (AccountManager.get(this).someUserHasAccount(account) | this.mUserManager.someUserHasSeedAccount(this.mAccountName, this.mAccountType))) {
                accountExists = true;
            }
            this.mCanProceed = true;
            String appName = appInfo.loadLabel(getPackageManager()).toString();
            if (cantCreateUser) {
                setResult(1);
                return null;
            } else if (cantCreateAnyMoreUsers) {
                setResult(2);
                return null;
            } else if (accountExists) {
                String message = getString(C4057R.string.user_creation_account_exists, appName, this.mAccountName);
                return message;
            } else {
                String message2 = this.mAccountName;
                return getString(C4057R.string.user_creation_adding, appName, message2);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException("Cannot find the calling package");
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        setResult(0);
        if (which == -1 && this.mCanProceed && this.mIsFirstClick) {
            this.mIsFirstClick = false;
            Log.m108i(TAG, "Ok, creating user");
            UserInfo user = this.mUserManager.createUser(this.mUserName, "android.os.usertype.full.SECONDARY", 0);
            if (user == null) {
                Log.m110e(TAG, "Couldn't create user");
                finish();
                return;
            }
            this.mUserManager.setSeedAccountData(user.f48id, this.mAccountName, this.mAccountType, this.mAccountOptions);
            setResult(-1);
        }
        finish();
    }
}
