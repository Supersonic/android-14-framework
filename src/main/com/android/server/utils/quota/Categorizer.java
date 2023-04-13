package com.android.server.utils.quota;
/* loaded from: classes2.dex */
public interface Categorizer {
    public static final Categorizer SINGLE_CATEGORIZER = new Categorizer() { // from class: com.android.server.utils.quota.Categorizer$$ExternalSyntheticLambda0
        @Override // com.android.server.utils.quota.Categorizer
        public final Category getCategory(int i, String str, String str2) {
            Category category;
            category = Category.SINGLE_CATEGORY;
            return category;
        }
    };

    Category getCategory(int i, String str, String str2);
}
