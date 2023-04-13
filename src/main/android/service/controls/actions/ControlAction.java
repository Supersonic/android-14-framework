package android.service.controls.actions;

import android.p008os.Bundle;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class ControlAction {
    public static final ControlAction ERROR_ACTION = new ControlAction() { // from class: android.service.controls.actions.ControlAction.1
        @Override // android.service.controls.actions.ControlAction
        public int getActionType() {
            return -1;
        }
    };
    private static final String KEY_ACTION_TYPE = "key_action_type";
    private static final String KEY_CHALLENGE_VALUE = "key_challenge_value";
    private static final String KEY_TEMPLATE_ID = "key_template_id";
    private static final int NUM_RESPONSE_TYPES = 6;
    public static final int RESPONSE_CHALLENGE_ACK = 3;
    public static final int RESPONSE_CHALLENGE_PASSPHRASE = 5;
    public static final int RESPONSE_CHALLENGE_PIN = 4;
    public static final int RESPONSE_FAIL = 2;
    public static final int RESPONSE_OK = 1;
    public static final int RESPONSE_UNKNOWN = 0;
    private static final String TAG = "ControlAction";
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_COMMAND = 5;
    public static final int TYPE_ERROR = -1;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_MODE = 4;
    private final String mChallengeValue;
    private final String mTemplateId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ActionType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ResponseResult {
    }

    public abstract int getActionType();

    public static final boolean isValidResponse(int response) {
        return response >= 0 && response < 6;
    }

    private ControlAction() {
        this.mTemplateId = "";
        this.mChallengeValue = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ControlAction(String templateId, String challengeValue) {
        Preconditions.checkNotNull(templateId);
        this.mTemplateId = templateId;
        this.mChallengeValue = challengeValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ControlAction(Bundle b) {
        this.mTemplateId = b.getString(KEY_TEMPLATE_ID);
        this.mChallengeValue = b.getString(KEY_CHALLENGE_VALUE);
    }

    public String getTemplateId() {
        return this.mTemplateId;
    }

    public String getChallengeValue() {
        return this.mChallengeValue;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle getDataBundle() {
        Bundle b = new Bundle();
        b.putInt(KEY_ACTION_TYPE, getActionType());
        b.putString(KEY_TEMPLATE_ID, this.mTemplateId);
        b.putString(KEY_CHALLENGE_VALUE, this.mChallengeValue);
        return b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ControlAction createActionFromBundle(Bundle bundle) {
        if (bundle == null) {
            Log.m110e(TAG, "Null bundle");
            return ERROR_ACTION;
        }
        int type = bundle.getInt(KEY_ACTION_TYPE, -1);
        try {
            switch (type) {
                case 1:
                    return new BooleanAction(bundle);
                case 2:
                    return new FloatAction(bundle);
                case 3:
                default:
                    return ERROR_ACTION;
                case 4:
                    return new ModeAction(bundle);
                case 5:
                    return new CommandAction(bundle);
            }
        } catch (Exception e) {
            Log.m109e(TAG, "Error creating action", e);
            return ERROR_ACTION;
        }
    }

    public static ControlAction getErrorAction() {
        return ERROR_ACTION;
    }
}
