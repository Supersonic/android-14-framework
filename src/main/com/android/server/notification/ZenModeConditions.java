package com.android.server.notification;

import android.content.ComponentName;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.notification.ConditionProviders;
import java.io.PrintWriter;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class ZenModeConditions implements ConditionProviders.Callback {
    public static final boolean DEBUG = ZenModeHelper.DEBUG;
    public final ConditionProviders mConditionProviders;
    public final ZenModeHelper mHelper;
    @VisibleForTesting
    protected final ArrayMap<Uri, ComponentName> mSubscriptions = new ArrayMap<>();

    @Override // com.android.server.notification.ConditionProviders.Callback
    public void onBootComplete() {
    }

    @Override // com.android.server.notification.ConditionProviders.Callback
    public void onUserSwitched() {
    }

    public ZenModeConditions(ZenModeHelper zenModeHelper, ConditionProviders conditionProviders) {
        this.mHelper = zenModeHelper;
        this.mConditionProviders = conditionProviders;
        if (conditionProviders.isSystemProviderEnabled("countdown")) {
            conditionProviders.addSystemProvider(new CountdownConditionProvider());
        }
        if (conditionProviders.isSystemProviderEnabled("schedule")) {
            conditionProviders.addSystemProvider(new ScheduleConditionProvider());
        }
        if (conditionProviders.isSystemProviderEnabled("event")) {
            conditionProviders.addSystemProvider(new EventConditionProvider());
        }
        conditionProviders.setCallback(this);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("mSubscriptions=");
        printWriter.println(this.mSubscriptions);
    }

    public void evaluateConfig(ZenModeConfig zenModeConfig, ComponentName componentName, boolean z) {
        if (zenModeConfig == null) {
            return;
        }
        ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
        if (zenRule != null && zenRule.condition != null && !zenRule.isTrueOrUnknown()) {
            if (DEBUG) {
                Log.d("ZenModeHelper", "evaluateConfig: clearing manual rule");
            }
            zenModeConfig.manualRule = null;
        }
        ArraySet<Uri> arraySet = new ArraySet<>();
        evaluateRule(zenModeConfig.manualRule, arraySet, null, z);
        for (ZenModeConfig.ZenRule zenRule2 : zenModeConfig.automaticRules.values()) {
            if (zenRule2.component != null) {
                evaluateRule(zenRule2, arraySet, componentName, z);
                updateSnoozing(zenRule2);
            }
        }
        synchronized (this.mSubscriptions) {
            for (int size = this.mSubscriptions.size() - 1; size >= 0; size--) {
                Uri keyAt = this.mSubscriptions.keyAt(size);
                ComponentName valueAt = this.mSubscriptions.valueAt(size);
                if (z && !arraySet.contains(keyAt)) {
                    this.mConditionProviders.unsubscribeIfNecessary(valueAt, keyAt);
                    this.mSubscriptions.removeAt(size);
                }
            }
        }
    }

    @Override // com.android.server.notification.ConditionProviders.Callback
    public void onServiceAdded(ComponentName componentName) {
        if (DEBUG) {
            Log.d("ZenModeHelper", "onServiceAdded " + componentName);
        }
        ZenModeHelper zenModeHelper = this.mHelper;
        ZenModeConfig config = zenModeHelper.getConfig();
        zenModeHelper.setConfig(config, componentName, "zmc.onServiceAdded:" + componentName);
    }

    @Override // com.android.server.notification.ConditionProviders.Callback
    public void onConditionChanged(Uri uri, Condition condition) {
        if (DEBUG) {
            Log.d("ZenModeHelper", "onConditionChanged " + uri + " " + condition);
        }
        if (this.mHelper.getConfig() == null) {
            return;
        }
        this.mHelper.setAutomaticZenRuleState(uri, condition);
    }

    public final void evaluateRule(ZenModeConfig.ZenRule zenRule, ArraySet<Uri> arraySet, ComponentName componentName, boolean z) {
        Uri uri;
        if (zenRule == null || (uri = zenRule.conditionId) == null || zenRule.configurationActivity != null) {
            return;
        }
        Iterator<SystemConditionProviderService> it = this.mConditionProviders.getSystemProviders().iterator();
        boolean z2 = false;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SystemConditionProviderService next = it.next();
            if (next.isValidConditionId(uri)) {
                this.mConditionProviders.ensureRecordExists(next.getComponent(), uri, next.asInterface());
                zenRule.component = next.getComponent();
                z2 = true;
            }
        }
        if (!z2) {
            IConditionProvider findConditionProvider = this.mConditionProviders.findConditionProvider(zenRule.component);
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Ensure external rule exists: ");
                sb.append(findConditionProvider != null);
                sb.append(" for ");
                sb.append(uri);
                Log.d("ZenModeHelper", sb.toString());
            }
            if (findConditionProvider != null) {
                this.mConditionProviders.ensureRecordExists(zenRule.component, uri, findConditionProvider);
            }
        }
        if (zenRule.component == null && zenRule.enabler == null) {
            Log.w("ZenModeHelper", "No component found for automatic rule: " + zenRule.conditionId);
            zenRule.enabled = false;
            return;
        }
        if (arraySet != null) {
            arraySet.add(uri);
        }
        if (z && ((componentName != null && componentName.equals(zenRule.component)) || z2)) {
            boolean z3 = DEBUG;
            if (z3) {
                Log.d("ZenModeHelper", "Subscribing to " + zenRule.component);
            }
            if (this.mConditionProviders.subscribeIfNecessary(zenRule.component, zenRule.conditionId)) {
                synchronized (this.mSubscriptions) {
                    this.mSubscriptions.put(zenRule.conditionId, zenRule.component);
                }
            } else {
                zenRule.condition = null;
                if (z3) {
                    Log.d("ZenModeHelper", "zmc failed to subscribe");
                }
            }
        }
        ComponentName componentName2 = zenRule.component;
        if (componentName2 == null || zenRule.condition != null) {
            return;
        }
        Condition findCondition = this.mConditionProviders.findCondition(componentName2, zenRule.conditionId);
        zenRule.condition = findCondition;
        if (findCondition == null || !DEBUG) {
            return;
        }
        Log.d("ZenModeHelper", "Found existing condition for: " + zenRule.conditionId);
    }

    public final boolean updateSnoozing(ZenModeConfig.ZenRule zenRule) {
        if (zenRule == null || !zenRule.snoozing || zenRule.isTrueOrUnknown()) {
            return false;
        }
        zenRule.snoozing = false;
        if (DEBUG) {
            Log.d("ZenModeHelper", "Snoozing reset for " + zenRule.conditionId);
        }
        return true;
    }
}
