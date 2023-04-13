package android.content.integrity;

import android.annotation.SystemApi;
import android.content.IntentSender;
import android.content.integrity.RuleSet;
import android.content.p001pm.ParceledListSlice;
import android.p008os.RemoteException;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public class AppIntegrityManager {
    public static final String EXTRA_STATUS = "android.content.integrity.extra.STATUS";
    public static final int STATUS_FAILURE = 1;
    public static final int STATUS_SUCCESS = 0;
    IAppIntegrityManager mManager;

    public AppIntegrityManager(IAppIntegrityManager manager) {
        this.mManager = manager;
    }

    public void updateRuleSet(RuleSet updateRequest, IntentSender statusReceiver) {
        try {
            this.mManager.updateRuleSet(updateRequest.getVersion(), new ParceledListSlice<>(updateRequest.getRules()), statusReceiver);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String getCurrentRuleSetVersion() {
        try {
            return this.mManager.getCurrentRuleSetVersion();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public String getCurrentRuleSetProvider() {
        try {
            return this.mManager.getCurrentRuleSetProvider();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public RuleSet getCurrentRuleSet() {
        try {
            ParceledListSlice<Rule> rules = this.mManager.getCurrentRules();
            String version = this.mManager.getCurrentRuleSetVersion();
            return new RuleSet.Builder().setVersion(version).addRules(rules.getList()).build();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public List<String> getWhitelistedRuleProviders() {
        try {
            return this.mManager.getWhitelistedRuleProviders();
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }
}
