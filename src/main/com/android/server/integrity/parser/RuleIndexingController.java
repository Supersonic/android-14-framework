package com.android.server.integrity.parser;

import android.content.integrity.AppInstallMetadata;
import com.android.server.integrity.model.BitInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class RuleIndexingController {
    public static LinkedHashMap<String, Integer> sAppCertificateBasedIndexes;
    public static LinkedHashMap<String, Integer> sPackageNameBasedIndexes;
    public static LinkedHashMap<String, Integer> sUnindexedRuleIndexes;

    public RuleIndexingController(InputStream inputStream) throws IOException {
        BitInputStream bitInputStream = new BitInputStream(inputStream);
        sPackageNameBasedIndexes = getNextIndexGroup(bitInputStream);
        sAppCertificateBasedIndexes = getNextIndexGroup(bitInputStream);
        sUnindexedRuleIndexes = getNextIndexGroup(bitInputStream);
    }

    public List<RuleIndexRange> identifyRulesToEvaluate(AppInstallMetadata appInstallMetadata) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(searchIndexingKeysRangeContainingKey(sPackageNameBasedIndexes, appInstallMetadata.getPackageName()));
        for (String str : appInstallMetadata.getAppCertificates()) {
            arrayList.add(searchIndexingKeysRangeContainingKey(sAppCertificateBasedIndexes, str));
        }
        arrayList.add(new RuleIndexRange(sUnindexedRuleIndexes.get("START_KEY").intValue(), sUnindexedRuleIndexes.get("END_KEY").intValue()));
        return arrayList;
    }

    public final LinkedHashMap<String, Integer> getNextIndexGroup(BitInputStream bitInputStream) throws IOException {
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        while (bitInputStream.hasNext()) {
            String stringValue = BinaryFileOperations.getStringValue(bitInputStream);
            linkedHashMap.put(stringValue, Integer.valueOf(BinaryFileOperations.getIntValue(bitInputStream)));
            if (stringValue.matches("END_KEY")) {
                break;
            }
        }
        if (linkedHashMap.size() >= 2) {
            return linkedHashMap;
        }
        throw new IllegalStateException("Indexing file is corrupt.");
    }

    public static RuleIndexRange searchIndexingKeysRangeContainingKey(LinkedHashMap<String, Integer> linkedHashMap, String str) {
        List list = (List) linkedHashMap.keySet().stream().collect(Collectors.toList());
        List<String> searchKeysRangeContainingKey = searchKeysRangeContainingKey(list, str, 0, list.size() - 1);
        return new RuleIndexRange(linkedHashMap.get(searchKeysRangeContainingKey.get(0)).intValue(), linkedHashMap.get(searchKeysRangeContainingKey.get(1)).intValue());
    }

    public static List<String> searchKeysRangeContainingKey(List<String> list, String str, int i, int i2) {
        if (i2 > i) {
            int i3 = i2 - i;
            if (i3 == 1) {
                return Arrays.asList(list.get(i), list.get(i2));
            }
            int i4 = (i3 / 2) + i;
            if (str.compareTo(list.get(i4)) >= 0) {
                return searchKeysRangeContainingKey(list, str, i4, i2);
            }
            return searchKeysRangeContainingKey(list, str, i, i4);
        }
        throw new IllegalStateException("Indexing file is corrupt.");
    }
}
