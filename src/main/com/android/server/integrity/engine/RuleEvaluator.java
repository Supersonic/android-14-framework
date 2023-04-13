package com.android.server.integrity.engine;

import android.content.integrity.AppInstallMetadata;
import android.content.integrity.Rule;
import com.android.server.integrity.model.IntegrityCheckResult;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class RuleEvaluator {
    public static IntegrityCheckResult evaluateRules(List<Rule> list, final AppInstallMetadata appInstallMetadata) {
        List list2 = (List) list.stream().filter(new Predicate() { // from class: com.android.server.integrity.engine.RuleEvaluator$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$evaluateRules$0;
                lambda$evaluateRules$0 = RuleEvaluator.lambda$evaluateRules$0(appInstallMetadata, (Rule) obj);
                return lambda$evaluateRules$0;
            }
        }).collect(Collectors.toList());
        List list3 = (List) list2.stream().filter(new Predicate() { // from class: com.android.server.integrity.engine.RuleEvaluator$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$evaluateRules$1;
                lambda$evaluateRules$1 = RuleEvaluator.lambda$evaluateRules$1((Rule) obj);
                return lambda$evaluateRules$1;
            }
        }).collect(Collectors.toList());
        if (!list3.isEmpty()) {
            return IntegrityCheckResult.allow(list3);
        }
        List list4 = (List) list2.stream().filter(new Predicate() { // from class: com.android.server.integrity.engine.RuleEvaluator$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$evaluateRules$2;
                lambda$evaluateRules$2 = RuleEvaluator.lambda$evaluateRules$2((Rule) obj);
                return lambda$evaluateRules$2;
            }
        }).collect(Collectors.toList());
        if (!list4.isEmpty()) {
            return IntegrityCheckResult.deny(list4);
        }
        return IntegrityCheckResult.allow();
    }

    public static /* synthetic */ boolean lambda$evaluateRules$0(AppInstallMetadata appInstallMetadata, Rule rule) {
        return rule.getFormula().matches(appInstallMetadata);
    }

    public static /* synthetic */ boolean lambda$evaluateRules$1(Rule rule) {
        return rule.getEffect() == 1;
    }

    public static /* synthetic */ boolean lambda$evaluateRules$2(Rule rule) {
        return rule.getEffect() == 0;
    }
}
