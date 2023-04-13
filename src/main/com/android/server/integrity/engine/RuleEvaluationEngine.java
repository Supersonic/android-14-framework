package com.android.server.integrity.engine;

import android.content.integrity.AppInstallMetadata;
import android.content.integrity.Rule;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.integrity.IntegrityFileManager;
import com.android.server.integrity.model.IntegrityCheckResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class RuleEvaluationEngine {
    public static RuleEvaluationEngine sRuleEvaluationEngine;
    public final IntegrityFileManager mIntegrityFileManager;

    @VisibleForTesting
    public RuleEvaluationEngine(IntegrityFileManager integrityFileManager) {
        this.mIntegrityFileManager = integrityFileManager;
    }

    public static synchronized RuleEvaluationEngine getRuleEvaluationEngine() {
        synchronized (RuleEvaluationEngine.class) {
            RuleEvaluationEngine ruleEvaluationEngine = sRuleEvaluationEngine;
            if (ruleEvaluationEngine == null) {
                return new RuleEvaluationEngine(IntegrityFileManager.getInstance());
            }
            return ruleEvaluationEngine;
        }
    }

    public IntegrityCheckResult evaluate(AppInstallMetadata appInstallMetadata) {
        return RuleEvaluator.evaluateRules(loadRules(appInstallMetadata), appInstallMetadata);
    }

    public final List<Rule> loadRules(AppInstallMetadata appInstallMetadata) {
        if (!this.mIntegrityFileManager.initialized()) {
            Slog.w("RuleEvaluation", "Integrity rule files are not available.");
            return Collections.emptyList();
        }
        try {
            return this.mIntegrityFileManager.readRules(appInstallMetadata);
        } catch (Exception e) {
            Slog.e("RuleEvaluation", "Error loading rules.", e);
            return new ArrayList();
        }
    }
}
