package com.android.server.backup;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class UserBackupPreferences {
    public final SharedPreferences.Editor mEditor;
    public final SharedPreferences mPreferences;

    public UserBackupPreferences(Context context, File file) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(new File(file, "backup_preferences"), 0);
        this.mPreferences = sharedPreferences;
        this.mEditor = sharedPreferences.edit();
    }

    public void addExcludedKeys(String str, List<String> list) {
        HashSet hashSet = new HashSet(this.mPreferences.getStringSet(str, Collections.emptySet()));
        hashSet.addAll(list);
        this.mEditor.putStringSet(str, hashSet);
        this.mEditor.commit();
    }

    public Set<String> getExcludedRestoreKeysForPackage(String str) {
        return this.mPreferences.getStringSet(str, Collections.emptySet());
    }
}
