package android.service.autofill;

import android.p008os.Bundle;
import android.util.ArrayMap;
/* loaded from: classes3.dex */
public interface FieldClassificationUserData {
    String[] getCategoryIds();

    Bundle getDefaultFieldClassificationArgs();

    String getFieldClassificationAlgorithm();

    String getFieldClassificationAlgorithmForCategory(String str);

    ArrayMap<String, String> getFieldClassificationAlgorithms();

    ArrayMap<String, Bundle> getFieldClassificationArgs();

    String[] getValues();
}
