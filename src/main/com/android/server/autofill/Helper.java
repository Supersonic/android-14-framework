package com.android.server.autofill;

import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.metrics.LogMaker;
import android.service.autofill.Dataset;
import android.service.autofill.InternalSanitizer;
import android.service.autofill.SaveInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.view.WindowManager;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import com.android.internal.util.ArrayUtils;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class Helper {
    public static boolean sDebug = false;
    public static Boolean sFullScreenMode = null;
    public static boolean sVerbose = false;

    /* loaded from: classes.dex */
    public interface ViewNodeFilter {
        boolean matches(AssistStructure.ViewNode viewNode);
    }

    public static AutofillId[] toArray(ArraySet<AutofillId> arraySet) {
        if (arraySet == null) {
            return null;
        }
        AutofillId[] autofillIdArr = new AutofillId[arraySet.size()];
        for (int i = 0; i < arraySet.size(); i++) {
            autofillIdArr[i] = arraySet.valueAt(i);
        }
        return autofillIdArr;
    }

    public static String paramsToString(WindowManager.LayoutParams layoutParams) {
        StringBuilder sb = new StringBuilder(25);
        layoutParams.dumpDimensions(sb);
        return sb.toString();
    }

    public static ArrayMap<AutofillId, AutofillValue> getFields(Dataset dataset) {
        ArrayList fieldIds = dataset.getFieldIds();
        ArrayList fieldValues = dataset.getFieldValues();
        int size = fieldIds == null ? 0 : fieldIds.size();
        ArrayMap<AutofillId, AutofillValue> arrayMap = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            arrayMap.put((AutofillId) fieldIds.get(i), (AutofillValue) fieldValues.get(i));
        }
        return arrayMap;
    }

    public static LogMaker newLogMaker(int i, String str, int i2, boolean z) {
        LogMaker addTaggedData = new LogMaker(i).addTaggedData(908, str).addTaggedData(1456, Integer.toString(i2));
        if (z) {
            addTaggedData.addTaggedData(1414, 1);
        }
        return addTaggedData;
    }

    public static LogMaker newLogMaker(int i, String str, String str2, int i2, boolean z) {
        return newLogMaker(i, str2, i2, z).setPackageName(str);
    }

    public static LogMaker newLogMaker(int i, ComponentName componentName, String str, int i2, boolean z) {
        return newLogMaker(i, str, i2, z).setComponentName(new ComponentName(componentName.getPackageName(), ""));
    }

    public static void printlnRedactedText(PrintWriter printWriter, CharSequence charSequence) {
        if (charSequence == null) {
            printWriter.println("null");
            return;
        }
        printWriter.print(charSequence.length());
        printWriter.println("_chars");
    }

    public static AssistStructure.ViewNode findViewNodeByAutofillId(AssistStructure assistStructure, final AutofillId autofillId) {
        return findViewNode(assistStructure, new ViewNodeFilter() { // from class: com.android.server.autofill.Helper$$ExternalSyntheticLambda0
            @Override // com.android.server.autofill.Helper.ViewNodeFilter
            public final boolean matches(AssistStructure.ViewNode viewNode) {
                boolean lambda$findViewNodeByAutofillId$0;
                lambda$findViewNodeByAutofillId$0 = Helper.lambda$findViewNodeByAutofillId$0(autofillId, viewNode);
                return lambda$findViewNodeByAutofillId$0;
            }
        });
    }

    public static /* synthetic */ boolean lambda$findViewNodeByAutofillId$0(AutofillId autofillId, AssistStructure.ViewNode viewNode) {
        return autofillId.equals(viewNode.getAutofillId());
    }

    public static AssistStructure.ViewNode findViewNode(AssistStructure assistStructure, ViewNodeFilter viewNodeFilter) {
        ArrayDeque arrayDeque = new ArrayDeque();
        int windowNodeCount = assistStructure.getWindowNodeCount();
        for (int i = 0; i < windowNodeCount; i++) {
            arrayDeque.add(assistStructure.getWindowNodeAt(i).getRootViewNode());
        }
        while (!arrayDeque.isEmpty()) {
            AssistStructure.ViewNode viewNode = (AssistStructure.ViewNode) arrayDeque.removeFirst();
            if (viewNodeFilter.matches(viewNode)) {
                return viewNode;
            }
            for (int i2 = 0; i2 < viewNode.getChildCount(); i2++) {
                arrayDeque.addLast(viewNode.getChildAt(i2));
            }
        }
        return null;
    }

    public static AssistStructure.ViewNode sanitizeUrlBar(AssistStructure assistStructure, final String[] strArr) {
        AssistStructure.ViewNode findViewNode = findViewNode(assistStructure, new ViewNodeFilter() { // from class: com.android.server.autofill.Helper$$ExternalSyntheticLambda1
            @Override // com.android.server.autofill.Helper.ViewNodeFilter
            public final boolean matches(AssistStructure.ViewNode viewNode) {
                boolean lambda$sanitizeUrlBar$1;
                lambda$sanitizeUrlBar$1 = Helper.lambda$sanitizeUrlBar$1(strArr, viewNode);
                return lambda$sanitizeUrlBar$1;
            }
        });
        if (findViewNode != null) {
            String charSequence = findViewNode.getText().toString();
            if (charSequence.isEmpty()) {
                if (sDebug) {
                    Slog.d("AutofillHelper", "sanitizeUrlBar(): empty on " + findViewNode.getIdEntry());
                    return null;
                }
                return null;
            }
            findViewNode.setWebDomain(charSequence);
            if (sDebug) {
                Slog.d("AutofillHelper", "sanitizeUrlBar(): id=" + findViewNode.getIdEntry() + ", domain=" + findViewNode.getWebDomain());
            }
        }
        return findViewNode;
    }

    public static /* synthetic */ boolean lambda$sanitizeUrlBar$1(String[] strArr, AssistStructure.ViewNode viewNode) {
        return ArrayUtils.contains(strArr, viewNode.getIdEntry());
    }

    public static int getNumericValue(LogMaker logMaker, int i) {
        Object taggedData = logMaker.getTaggedData(i);
        if (taggedData instanceof Number) {
            return ((Number) taggedData).intValue();
        }
        return 0;
    }

    public static ArrayList<AutofillId> getAutofillIds(AssistStructure assistStructure, boolean z) {
        ArrayList<AutofillId> arrayList = new ArrayList<>();
        int windowNodeCount = assistStructure.getWindowNodeCount();
        for (int i = 0; i < windowNodeCount; i++) {
            addAutofillableIds(assistStructure.getWindowNodeAt(i).getRootViewNode(), arrayList, z);
        }
        return arrayList;
    }

    public static void addAutofillableIds(AssistStructure.ViewNode viewNode, ArrayList<AutofillId> arrayList, boolean z) {
        if (!z || viewNode.getAutofillType() != 0) {
            arrayList.add(viewNode.getAutofillId());
        }
        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            addAutofillableIds(viewNode.getChildAt(i), arrayList, z);
        }
    }

    public static ArrayMap<AutofillId, InternalSanitizer> createSanitizers(SaveInfo saveInfo) {
        InternalSanitizer[] sanitizerKeys;
        if (saveInfo == null || (sanitizerKeys = saveInfo.getSanitizerKeys()) == null) {
            return null;
        }
        int length = sanitizerKeys.length;
        ArrayMap<AutofillId, InternalSanitizer> arrayMap = new ArrayMap<>(length);
        if (sDebug) {
            Slog.d("AutofillHelper", "Service provided " + length + " sanitizers");
        }
        AutofillId[][] sanitizerValues = saveInfo.getSanitizerValues();
        for (int i = 0; i < length; i++) {
            InternalSanitizer internalSanitizer = sanitizerKeys[i];
            AutofillId[] autofillIdArr = sanitizerValues[i];
            if (sDebug) {
                Slog.d("AutofillHelper", "sanitizer #" + i + " (" + internalSanitizer + ") for ids " + Arrays.toString(autofillIdArr));
            }
            for (AutofillId autofillId : autofillIdArr) {
                arrayMap.put(autofillId, internalSanitizer);
            }
        }
        return arrayMap;
    }

    public static boolean containsCharsInOrder(String str, String str2) {
        int i = -1;
        for (char c : str2.toCharArray()) {
            i = TextUtils.indexOf(str, c, i + 1);
            if (i == -1) {
                return false;
            }
        }
        return true;
    }
}
