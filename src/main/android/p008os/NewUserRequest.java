package android.p008os;

import android.annotation.SystemApi;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateFormat;
@SystemApi
/* renamed from: android.os.NewUserRequest */
/* loaded from: classes3.dex */
public final class NewUserRequest {
    private final String mAccountName;
    private final PersistableBundle mAccountOptions;
    private final String mAccountType;
    private final boolean mAdmin;
    private final boolean mEphemeral;
    private final String mName;
    private final Bitmap mUserIcon;
    private final String mUserType;

    private NewUserRequest(Builder builder) {
        this.mName = builder.mName;
        this.mAdmin = builder.mAdmin;
        this.mEphemeral = builder.mEphemeral;
        this.mUserType = builder.mUserType;
        this.mUserIcon = builder.mUserIcon;
        this.mAccountName = builder.mAccountName;
        this.mAccountType = builder.mAccountType;
        this.mAccountOptions = builder.mAccountOptions;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isEphemeral() {
        return this.mEphemeral;
    }

    public boolean isAdmin() {
        return this.mAdmin;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFlags() {
        int flags = isAdmin() ? 0 | 2 : 0;
        return isEphemeral() ? flags | 256 : flags;
    }

    public String getUserType() {
        return this.mUserType;
    }

    public Bitmap getUserIcon() {
        return this.mUserIcon;
    }

    public String getAccountName() {
        return this.mAccountName;
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    public PersistableBundle getAccountOptions() {
        return this.mAccountOptions;
    }

    public String toString() {
        return "NewUserRequest{mName='" + this.mName + DateFormat.QUOTE + ", mAdmin=" + this.mAdmin + ", mEphemeral=" + this.mEphemeral + ", mUserType='" + this.mUserType + DateFormat.QUOTE + ", mAccountName='" + this.mAccountName + DateFormat.QUOTE + ", mAccountType='" + this.mAccountType + DateFormat.QUOTE + ", mAccountOptions=" + this.mAccountOptions + '}';
    }

    /* renamed from: android.os.NewUserRequest$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mAccountName;
        private PersistableBundle mAccountOptions;
        private String mAccountType;
        private boolean mAdmin;
        private boolean mEphemeral;
        private String mName;
        private Bitmap mUserIcon;
        private String mUserType = UserManager.USER_TYPE_FULL_SECONDARY;

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setAdmin() {
            this.mAdmin = true;
            return this;
        }

        public Builder setEphemeral() {
            this.mEphemeral = true;
            return this;
        }

        public Builder setUserType(String type) {
            this.mUserType = type;
            return this;
        }

        public Builder setUserIcon(Bitmap userIcon) {
            this.mUserIcon = userIcon;
            return this;
        }

        public Builder setAccountName(String accountName) {
            this.mAccountName = accountName;
            return this;
        }

        public Builder setAccountType(String accountType) {
            this.mAccountType = accountType;
            return this;
        }

        public Builder setAccountOptions(PersistableBundle accountOptions) {
            this.mAccountOptions = accountOptions;
            return this;
        }

        public NewUserRequest build() {
            checkIfPropertiesAreCompatible();
            return new NewUserRequest(this);
        }

        private void checkIfPropertiesAreCompatible() {
            String str = this.mUserType;
            if (str == null) {
                throw new IllegalStateException("Usertype cannot be null");
            }
            if (this.mAdmin && !str.equals(UserManager.USER_TYPE_FULL_SECONDARY)) {
                throw new IllegalStateException("Admin user can't be of type: " + this.mUserType);
            }
            if (TextUtils.isEmpty(this.mAccountName) != TextUtils.isEmpty(this.mAccountType)) {
                throw new IllegalStateException("Account name and account type should be provided together.");
            }
        }
    }
}
