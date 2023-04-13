package com.android.server.integrity.model;

import android.content.integrity.Rule;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class IntegrityCheckResult {
    public final Effect mEffect;
    public final List<Rule> mRuleList;

    /* loaded from: classes.dex */
    public enum Effect {
        ALLOW,
        DENY
    }

    public IntegrityCheckResult(Effect effect, List<Rule> list) {
        this.mEffect = effect;
        this.mRuleList = list;
    }

    public Effect getEffect() {
        return this.mEffect;
    }

    public List<Rule> getMatchedRules() {
        return this.mRuleList;
    }

    public static IntegrityCheckResult allow() {
        return new IntegrityCheckResult(Effect.ALLOW, Collections.emptyList());
    }

    public static IntegrityCheckResult allow(List<Rule> list) {
        return new IntegrityCheckResult(Effect.ALLOW, list);
    }

    public static IntegrityCheckResult deny(List<Rule> list) {
        return new IntegrityCheckResult(Effect.DENY, list);
    }

    public int getLoggingResponse() {
        if (getEffect() == Effect.DENY) {
            return 2;
        }
        Effect effect = getEffect();
        Effect effect2 = Effect.ALLOW;
        if (effect == effect2 && getMatchedRules().isEmpty()) {
            return 1;
        }
        if (getEffect() != effect2 || getMatchedRules().isEmpty()) {
            throw new IllegalStateException("IntegrityCheckResult is not valid.");
        }
        return 3;
    }

    public static /* synthetic */ boolean lambda$isCausedByAppCertRule$0(Rule rule) {
        return rule.getFormula().isAppCertificateFormula();
    }

    public boolean isCausedByAppCertRule() {
        return this.mRuleList.stream().anyMatch(new Predicate() { // from class: com.android.server.integrity.model.IntegrityCheckResult$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isCausedByAppCertRule$0;
                lambda$isCausedByAppCertRule$0 = IntegrityCheckResult.lambda$isCausedByAppCertRule$0((Rule) obj);
                return lambda$isCausedByAppCertRule$0;
            }
        });
    }

    public static /* synthetic */ boolean lambda$isCausedByInstallerRule$1(Rule rule) {
        return rule.getFormula().isInstallerFormula();
    }

    public boolean isCausedByInstallerRule() {
        return this.mRuleList.stream().anyMatch(new Predicate() { // from class: com.android.server.integrity.model.IntegrityCheckResult$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isCausedByInstallerRule$1;
                lambda$isCausedByInstallerRule$1 = IntegrityCheckResult.lambda$isCausedByInstallerRule$1((Rule) obj);
                return lambda$isCausedByInstallerRule$1;
            }
        });
    }
}
