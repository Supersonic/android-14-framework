package com.android.server.integrity.serializer;

import android.content.integrity.AtomicFormula;
import android.content.integrity.CompoundFormula;
import android.content.integrity.IntegrityFormula;
import android.content.integrity.Rule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class RuleIndexingDetailsIdentifier {
    public static Map<Integer, Map<String, List<Rule>>> splitRulesIntoIndexBuckets(List<Rule> list) {
        if (list == null) {
            throw new IllegalArgumentException("Index buckets cannot be created for null rule list.");
        }
        HashMap hashMap = new HashMap();
        hashMap.put(0, new HashMap());
        hashMap.put(1, new HashMap());
        hashMap.put(2, new HashMap());
        for (Rule rule : list) {
            try {
                RuleIndexingDetails indexingDetails = getIndexingDetails(rule.getFormula());
                int indexType = indexingDetails.getIndexType();
                String ruleKey = indexingDetails.getRuleKey();
                if (!((Map) hashMap.get(Integer.valueOf(indexType))).containsKey(ruleKey)) {
                    ((Map) hashMap.get(Integer.valueOf(indexType))).put(ruleKey, new ArrayList());
                }
                ((List) ((Map) hashMap.get(Integer.valueOf(indexType))).get(ruleKey)).add(rule);
            } catch (Exception unused) {
                throw new IllegalArgumentException(String.format("Malformed rule identified. [%s]", rule.toString()));
            }
        }
        return hashMap;
    }

    public static RuleIndexingDetails getIndexingDetails(IntegrityFormula integrityFormula) {
        int tag = integrityFormula.getTag();
        if (tag != 0) {
            if (tag != 1) {
                if (tag == 2 || tag == 3 || tag == 4) {
                    return new RuleIndexingDetails(0);
                }
                throw new IllegalArgumentException(String.format("Invalid formula tag type: %s", Integer.valueOf(integrityFormula.getTag())));
            }
            return getIndexingDetailsForStringAtomicFormula((AtomicFormula.StringAtomicFormula) integrityFormula);
        }
        return getIndexingDetailsForCompoundFormula((CompoundFormula) integrityFormula);
    }

    public static RuleIndexingDetails getIndexingDetailsForCompoundFormula(CompoundFormula compoundFormula) {
        int connector = compoundFormula.getConnector();
        List formulas = compoundFormula.getFormulas();
        if (connector == 0 || connector == 1) {
            Optional findAny = formulas.stream().map(new Function() { // from class: com.android.server.integrity.serializer.RuleIndexingDetailsIdentifier$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    RuleIndexingDetails indexingDetails;
                    indexingDetails = RuleIndexingDetailsIdentifier.getIndexingDetails((IntegrityFormula) obj);
                    return indexingDetails;
                }
            }).filter(new Predicate() { // from class: com.android.server.integrity.serializer.RuleIndexingDetailsIdentifier$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getIndexingDetailsForCompoundFormula$1;
                    lambda$getIndexingDetailsForCompoundFormula$1 = RuleIndexingDetailsIdentifier.lambda$getIndexingDetailsForCompoundFormula$1((RuleIndexingDetails) obj);
                    return lambda$getIndexingDetailsForCompoundFormula$1;
                }
            }).findAny();
            if (findAny.isPresent()) {
                return (RuleIndexingDetails) findAny.get();
            }
            Optional findAny2 = formulas.stream().map(new Function() { // from class: com.android.server.integrity.serializer.RuleIndexingDetailsIdentifier$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    RuleIndexingDetails indexingDetails;
                    indexingDetails = RuleIndexingDetailsIdentifier.getIndexingDetails((IntegrityFormula) obj);
                    return indexingDetails;
                }
            }).filter(new Predicate() { // from class: com.android.server.integrity.serializer.RuleIndexingDetailsIdentifier$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getIndexingDetailsForCompoundFormula$3;
                    lambda$getIndexingDetailsForCompoundFormula$3 = RuleIndexingDetailsIdentifier.lambda$getIndexingDetailsForCompoundFormula$3((RuleIndexingDetails) obj);
                    return lambda$getIndexingDetailsForCompoundFormula$3;
                }
            }).findAny();
            if (findAny2.isPresent()) {
                return (RuleIndexingDetails) findAny2.get();
            }
            return new RuleIndexingDetails(0);
        }
        return new RuleIndexingDetails(0);
    }

    public static /* synthetic */ boolean lambda$getIndexingDetailsForCompoundFormula$1(RuleIndexingDetails ruleIndexingDetails) {
        return ruleIndexingDetails.getIndexType() == 1;
    }

    public static /* synthetic */ boolean lambda$getIndexingDetailsForCompoundFormula$3(RuleIndexingDetails ruleIndexingDetails) {
        return ruleIndexingDetails.getIndexType() == 2;
    }

    public static RuleIndexingDetails getIndexingDetailsForStringAtomicFormula(AtomicFormula.StringAtomicFormula stringAtomicFormula) {
        int key = stringAtomicFormula.getKey();
        if (key != 0) {
            if (key == 1) {
                return new RuleIndexingDetails(2, stringAtomicFormula.getValue());
            }
            return new RuleIndexingDetails(0);
        }
        return new RuleIndexingDetails(1, stringAtomicFormula.getValue());
    }
}
