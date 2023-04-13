package com.android.server.inputmethod;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Pair;
import android.util.Printer;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.textservices.TextServicesManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class InputMethodUtils {
    public static final String NOT_A_SUBTYPE_ID_STR = String.valueOf(-1);

    public static boolean isSoftInputModeStateVisibleAllowed(int i, int i2) {
        if (i < 28) {
            return true;
        }
        return ((i2 & 1) == 0 || (i2 & 2) == 0) ? false : true;
    }

    public static boolean canAddToLastInputMethod(InputMethodSubtype inputMethodSubtype) {
        if (inputMethodSubtype == null) {
            return true;
        }
        return !inputMethodSubtype.isAuxiliary();
    }

    public static void setNonSelectedSystemImesDisabledUntilUsed(PackageManager packageManager, List<InputMethodInfo> list) {
        boolean z;
        String[] stringArray = Resources.getSystem().getStringArray(17236036);
        if (stringArray == null || stringArray.length == 0) {
            return;
        }
        SpellCheckerInfo currentSpellCheckerForUser = TextServicesManagerInternal.get().getCurrentSpellCheckerForUser(packageManager.getUserId());
        for (String str : stringArray) {
            int i = 0;
            while (true) {
                if (i >= list.size()) {
                    z = false;
                    break;
                } else if (str.equals(list.get(i).getPackageName())) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z && (currentSpellCheckerForUser == null || !str.equals(currentSpellCheckerForUser.getPackageName()))) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, PackageManager.ApplicationInfoFlags.of(32768L));
                    if (applicationInfo != null) {
                        if ((applicationInfo.flags & 1) != 0) {
                            setDisabledUntilUsed(packageManager, str);
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
    }

    public static void setDisabledUntilUsed(PackageManager packageManager, String str) {
        try {
            int applicationEnabledSetting = packageManager.getApplicationEnabledSetting(str);
            if (applicationEnabledSetting == 0 || applicationEnabledSetting == 1) {
                try {
                    packageManager.setApplicationEnabledSetting(str, 4, 0);
                } catch (IllegalArgumentException e) {
                    Slog.w("InputMethodUtils", "setApplicationEnabledSetting failed. packageName=" + str + " userId=" + packageManager.getUserId(), e);
                }
            }
        } catch (IllegalArgumentException e2) {
            Slog.w("InputMethodUtils", "getApplicationEnabledSetting failed. packageName=" + str + " userId=" + packageManager.getUserId(), e2);
        }
    }

    public static boolean checkIfPackageBelongsToUid(PackageManagerInternal packageManagerInternal, int i, String str) {
        return packageManagerInternal.getPackageUid(str, 0L, UserHandle.getUserId(i)) == i;
    }

    /* loaded from: classes.dex */
    public static class InputMethodSettings {
        public static final ArraySet<String> CLONE_TO_MANAGED_PROFILE;
        public static final UserManagerInternal sUserManagerInternal;
        public int mCurrentUserId;
        public final ArrayMap<String, InputMethodInfo> mMethodMap;
        public Resources mRes;
        public ContentResolver mResolver;
        public Context mUserAwareContext;
        public final TextUtils.SimpleStringSplitter mInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
        public final TextUtils.SimpleStringSplitter mSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');
        public final ArrayMap<String, String> mCopyOnWriteDataStore = new ArrayMap<>();
        public boolean mCopyOnWrite = false;
        public String mEnabledInputMethodsStrCache = "";
        public int[] mCurrentProfileIds = new int[0];

        static {
            ArraySet<String> arraySet = new ArraySet<>();
            CLONE_TO_MANAGED_PROFILE = arraySet;
            Settings.Secure.getCloneToManagedProfileSettings(arraySet);
            sUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }

        public static void buildEnabledInputMethodsSettingString(StringBuilder sb, Pair<String, ArrayList<String>> pair) {
            sb.append((String) pair.first);
            Iterator it = ((ArrayList) pair.second).iterator();
            while (it.hasNext()) {
                sb.append(';');
                sb.append((String) it.next());
            }
        }

        public static List<Pair<String, ArrayList<String>>> buildInputMethodsAndSubtypeList(String str, TextUtils.SimpleStringSplitter simpleStringSplitter, TextUtils.SimpleStringSplitter simpleStringSplitter2) {
            ArrayList arrayList = new ArrayList();
            if (TextUtils.isEmpty(str)) {
                return arrayList;
            }
            simpleStringSplitter.setString(str);
            while (simpleStringSplitter.hasNext()) {
                simpleStringSplitter2.setString(simpleStringSplitter.next());
                if (simpleStringSplitter2.hasNext()) {
                    ArrayList arrayList2 = new ArrayList();
                    String next = simpleStringSplitter2.next();
                    while (simpleStringSplitter2.hasNext()) {
                        arrayList2.add(simpleStringSplitter2.next());
                    }
                    arrayList.add(new Pair(next, arrayList2));
                }
            }
            return arrayList;
        }

        public final void initContentWithUserContext(Context context, int i) {
            if (context.getUserId() != i) {
                context = context.createContextAsUser(UserHandle.of(i), 0);
            }
            this.mUserAwareContext = context;
            this.mRes = context.getResources();
            this.mResolver = this.mUserAwareContext.getContentResolver();
        }

        public InputMethodSettings(Context context, ArrayMap<String, InputMethodInfo> arrayMap, int i, boolean z) {
            this.mMethodMap = arrayMap;
            initContentWithUserContext(context, i);
            switchCurrentUser(i, z);
        }

        public void switchCurrentUser(int i, boolean z) {
            if (this.mCurrentUserId != i || this.mCopyOnWrite != z) {
                this.mCopyOnWriteDataStore.clear();
                this.mEnabledInputMethodsStrCache = "";
            }
            if (this.mUserAwareContext.getUserId() != i) {
                initContentWithUserContext(this.mUserAwareContext, i);
            }
            this.mCurrentUserId = i;
            this.mCopyOnWrite = z;
        }

        public final void putString(String str, String str2) {
            if (this.mCopyOnWrite) {
                this.mCopyOnWriteDataStore.put(str, str2);
            } else {
                Settings.Secure.putStringForUser(this.mResolver, str, str2, CLONE_TO_MANAGED_PROFILE.contains(str) ? sUserManagerInternal.getProfileParentId(this.mCurrentUserId) : this.mCurrentUserId);
            }
        }

        public final String getString(String str, String str2) {
            return getStringForUser(str, str2, this.mCurrentUserId);
        }

        public final String getStringForUser(String str, String str2, int i) {
            String stringForUser;
            if (this.mCopyOnWrite && this.mCopyOnWriteDataStore.containsKey(str)) {
                stringForUser = this.mCopyOnWriteDataStore.get(str);
            } else {
                stringForUser = Settings.Secure.getStringForUser(this.mResolver, str, i);
            }
            return stringForUser != null ? stringForUser : str2;
        }

        public final void putInt(String str, int i) {
            if (this.mCopyOnWrite) {
                this.mCopyOnWriteDataStore.put(str, String.valueOf(i));
            } else {
                Settings.Secure.putIntForUser(this.mResolver, str, i, CLONE_TO_MANAGED_PROFILE.contains(str) ? sUserManagerInternal.getProfileParentId(this.mCurrentUserId) : this.mCurrentUserId);
            }
        }

        public final int getInt(String str, int i) {
            if (this.mCopyOnWrite && this.mCopyOnWriteDataStore.containsKey(str)) {
                String str2 = this.mCopyOnWriteDataStore.get(str);
                return str2 != null ? Integer.parseInt(str2) : i;
            }
            return Settings.Secure.getIntForUser(this.mResolver, str, i, this.mCurrentUserId);
        }

        public final void putBoolean(String str, boolean z) {
            putInt(str, z ? 1 : 0);
        }

        public final boolean getBoolean(String str, boolean z) {
            return getInt(str, z ? 1 : 0) == 1;
        }

        public void setCurrentProfileIds(int[] iArr) {
            synchronized (this) {
                this.mCurrentProfileIds = iArr;
            }
        }

        public boolean isCurrentProfile(int i) {
            synchronized (this) {
                if (i == this.mCurrentUserId) {
                    return true;
                }
                int i2 = 0;
                while (true) {
                    int[] iArr = this.mCurrentProfileIds;
                    if (i2 >= iArr.length) {
                        return false;
                    }
                    if (i == iArr[i2]) {
                        return true;
                    }
                    i2++;
                }
            }
        }

        public ArrayList<InputMethodInfo> getEnabledInputMethodListLocked() {
            return getEnabledInputMethodListWithFilterLocked(null);
        }

        public ArrayList<InputMethodInfo> getEnabledInputMethodListWithFilterLocked(Predicate<InputMethodInfo> predicate) {
            return createEnabledInputMethodListLocked(getEnabledInputMethodsAndSubtypeListLocked(), predicate);
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(InputMethodInfo inputMethodInfo, boolean z) {
            List<InputMethodSubtype> enabledInputMethodSubtypeListLocked = getEnabledInputMethodSubtypeListLocked(inputMethodInfo);
            if (z && enabledInputMethodSubtypeListLocked.isEmpty()) {
                enabledInputMethodSubtypeListLocked = SubtypeUtils.getImplicitlyApplicableSubtypesLocked(this.mRes, inputMethodInfo);
            }
            return InputMethodSubtype.sort(inputMethodInfo, enabledInputMethodSubtypeListLocked);
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(InputMethodInfo inputMethodInfo) {
            List<Pair<String, ArrayList<String>>> enabledInputMethodsAndSubtypeListLocked = getEnabledInputMethodsAndSubtypeListLocked();
            ArrayList arrayList = new ArrayList();
            if (inputMethodInfo != null) {
                Iterator<Pair<String, ArrayList<String>>> it = enabledInputMethodsAndSubtypeListLocked.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Pair<String, ArrayList<String>> next = it.next();
                    InputMethodInfo inputMethodInfo2 = this.mMethodMap.get(next.first);
                    if (inputMethodInfo2 != null && inputMethodInfo2.getId().equals(inputMethodInfo.getId())) {
                        int subtypeCount = inputMethodInfo2.getSubtypeCount();
                        for (int i = 0; i < subtypeCount; i++) {
                            InputMethodSubtype subtypeAt = inputMethodInfo2.getSubtypeAt(i);
                            Iterator it2 = ((ArrayList) next.second).iterator();
                            while (it2.hasNext()) {
                                if (String.valueOf(subtypeAt.hashCode()).equals((String) it2.next())) {
                                    arrayList.add(subtypeAt);
                                }
                            }
                        }
                    }
                }
            }
            return arrayList;
        }

        public List<Pair<String, ArrayList<String>>> getEnabledInputMethodsAndSubtypeListLocked() {
            return buildInputMethodsAndSubtypeList(getEnabledInputMethodsStr(), this.mInputMethodSplitter, this.mSubtypeSplitter);
        }

        public List<String> getEnabledInputMethodNames() {
            ArrayList arrayList = new ArrayList();
            for (Pair<String, ArrayList<String>> pair : getEnabledInputMethodsAndSubtypeListLocked()) {
                arrayList.add((String) pair.first);
            }
            return arrayList;
        }

        public void appendAndPutEnabledInputMethodLocked(String str, boolean z) {
            if (z) {
                getEnabledInputMethodsStr();
            }
            if (TextUtils.isEmpty(this.mEnabledInputMethodsStrCache)) {
                putEnabledInputMethodsStr(str);
                return;
            }
            putEnabledInputMethodsStr(this.mEnabledInputMethodsStrCache + ':' + str);
        }

        public boolean buildAndPutEnabledInputMethodsStrRemovingIdLocked(StringBuilder sb, List<Pair<String, ArrayList<String>>> list, String str) {
            boolean z = false;
            boolean z2 = false;
            for (Pair<String, ArrayList<String>> pair : list) {
                if (((String) pair.first).equals(str)) {
                    z = true;
                } else {
                    if (z2) {
                        sb.append(':');
                    } else {
                        z2 = true;
                    }
                    buildEnabledInputMethodsSettingString(sb, pair);
                }
            }
            if (z) {
                putEnabledInputMethodsStr(sb.toString());
            }
            return z;
        }

        public final ArrayList<InputMethodInfo> createEnabledInputMethodListLocked(List<Pair<String, ArrayList<String>>> list, Predicate<InputMethodInfo> predicate) {
            ArrayList<InputMethodInfo> arrayList = new ArrayList<>();
            for (Pair<String, ArrayList<String>> pair : list) {
                InputMethodInfo inputMethodInfo = this.mMethodMap.get(pair.first);
                if (inputMethodInfo != null && !inputMethodInfo.isVrOnly() && (predicate == null || predicate.test(inputMethodInfo))) {
                    arrayList.add(inputMethodInfo);
                }
            }
            return arrayList;
        }

        public void putEnabledInputMethodsStr(String str) {
            if (TextUtils.isEmpty(str)) {
                putString("enabled_input_methods", null);
            } else {
                putString("enabled_input_methods", str);
            }
            if (str == null) {
                str = "";
            }
            this.mEnabledInputMethodsStrCache = str;
        }

        public String getEnabledInputMethodsStr() {
            String string = getString("enabled_input_methods", "");
            this.mEnabledInputMethodsStrCache = string;
            return string;
        }

        public final void saveSubtypeHistory(List<Pair<String, String>> list, String str, String str2) {
            boolean z;
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
                z = false;
            } else {
                sb.append(str);
                sb.append(';');
                sb.append(str2);
                z = true;
            }
            for (Pair<String, String> pair : list) {
                String str3 = (String) pair.first;
                String str4 = (String) pair.second;
                if (TextUtils.isEmpty(str4)) {
                    str4 = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
                if (z) {
                    sb.append(':');
                } else {
                    z = true;
                }
                sb.append(str3);
                sb.append(';');
                sb.append(str4);
            }
            putSubtypeHistoryStr(sb.toString());
        }

        public final void addSubtypeToHistory(String str, String str2) {
            List<Pair<String, String>> loadInputMethodAndSubtypeHistoryLocked = loadInputMethodAndSubtypeHistoryLocked();
            Iterator<Pair<String, String>> it = loadInputMethodAndSubtypeHistoryLocked.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Pair<String, String> next = it.next();
                if (((String) next.first).equals(str)) {
                    loadInputMethodAndSubtypeHistoryLocked.remove(next);
                    break;
                }
            }
            saveSubtypeHistory(loadInputMethodAndSubtypeHistoryLocked, str, str2);
        }

        public final void putSubtypeHistoryStr(String str) {
            if (TextUtils.isEmpty(str)) {
                putString("input_methods_subtype_history", null);
            } else {
                putString("input_methods_subtype_history", str);
            }
        }

        public Pair<String, String> getLastInputMethodAndSubtypeLocked() {
            return getLastSubtypeForInputMethodLockedInternal(null);
        }

        public InputMethodSubtype getLastInputMethodSubtypeLocked() {
            InputMethodInfo inputMethodInfo;
            Pair<String, String> lastInputMethodAndSubtypeLocked = getLastInputMethodAndSubtypeLocked();
            if (lastInputMethodAndSubtypeLocked == null || TextUtils.isEmpty((CharSequence) lastInputMethodAndSubtypeLocked.first) || TextUtils.isEmpty((CharSequence) lastInputMethodAndSubtypeLocked.second) || (inputMethodInfo = this.mMethodMap.get(lastInputMethodAndSubtypeLocked.first)) == null) {
                return null;
            }
            try {
                int subtypeIdFromHashCode = SubtypeUtils.getSubtypeIdFromHashCode(inputMethodInfo, Integer.parseInt((String) lastInputMethodAndSubtypeLocked.second));
                if (subtypeIdFromHashCode >= 0 && subtypeIdFromHashCode < inputMethodInfo.getSubtypeCount()) {
                    return inputMethodInfo.getSubtypeAt(subtypeIdFromHashCode);
                }
            } catch (NumberFormatException unused) {
            }
            return null;
        }

        public String getLastSubtypeForInputMethodLocked(String str) {
            Pair<String, String> lastSubtypeForInputMethodLockedInternal = getLastSubtypeForInputMethodLockedInternal(str);
            if (lastSubtypeForInputMethodLockedInternal != null) {
                return (String) lastSubtypeForInputMethodLockedInternal.second;
            }
            return null;
        }

        public final Pair<String, String> getLastSubtypeForInputMethodLockedInternal(String str) {
            List<Pair<String, ArrayList<String>>> enabledInputMethodsAndSubtypeListLocked = getEnabledInputMethodsAndSubtypeListLocked();
            for (Pair<String, String> pair : loadInputMethodAndSubtypeHistoryLocked()) {
                String str2 = (String) pair.first;
                if (TextUtils.isEmpty(str) || str2.equals(str)) {
                    String enabledSubtypeHashCodeForInputMethodAndSubtypeLocked = getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(enabledInputMethodsAndSubtypeListLocked, str2, (String) pair.second);
                    if (!TextUtils.isEmpty(enabledSubtypeHashCodeForInputMethodAndSubtypeLocked)) {
                        return new Pair<>(str2, enabledSubtypeHashCodeForInputMethodAndSubtypeLocked);
                    }
                }
            }
            return null;
        }

        public final String getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(List<Pair<String, ArrayList<String>>> list, String str, String str2) {
            for (Pair<String, ArrayList<String>> pair : list) {
                if (((String) pair.first).equals(str)) {
                    ArrayList arrayList = (ArrayList) pair.second;
                    InputMethodInfo inputMethodInfo = this.mMethodMap.get(str);
                    if (arrayList.size() == 0) {
                        if (inputMethodInfo != null && inputMethodInfo.getSubtypeCount() > 0) {
                            ArrayList<InputMethodSubtype> implicitlyApplicableSubtypesLocked = SubtypeUtils.getImplicitlyApplicableSubtypesLocked(this.mRes, inputMethodInfo);
                            int size = implicitlyApplicableSubtypesLocked.size();
                            for (int i = 0; i < size; i++) {
                                if (String.valueOf(implicitlyApplicableSubtypesLocked.get(i).hashCode()).equals(str2)) {
                                    return str2;
                                }
                            }
                        }
                    } else {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            String str3 = (String) it.next();
                            if (str3.equals(str2)) {
                                try {
                                    return SubtypeUtils.isValidSubtypeId(inputMethodInfo, Integer.parseInt(str2)) ? str3 : InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                } catch (NumberFormatException unused) {
                                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                }
                            }
                        }
                    }
                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
            }
            return null;
        }

        public final List<Pair<String, String>> loadInputMethodAndSubtypeHistoryLocked() {
            ArrayList arrayList = new ArrayList();
            String subtypeHistoryStr = getSubtypeHistoryStr();
            if (TextUtils.isEmpty(subtypeHistoryStr)) {
                return arrayList;
            }
            this.mInputMethodSplitter.setString(subtypeHistoryStr);
            while (this.mInputMethodSplitter.hasNext()) {
                this.mSubtypeSplitter.setString(this.mInputMethodSplitter.next());
                if (this.mSubtypeSplitter.hasNext()) {
                    String str = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                    String next = this.mSubtypeSplitter.next();
                    if (this.mSubtypeSplitter.hasNext()) {
                        str = this.mSubtypeSplitter.next();
                    }
                    arrayList.add(new Pair(next, str));
                }
            }
            return arrayList;
        }

        public final String getSubtypeHistoryStr() {
            return getString("input_methods_subtype_history", "");
        }

        public void putSelectedInputMethod(String str) {
            putString("default_input_method", str);
        }

        public void putSelectedSubtype(int i) {
            putInt("selected_input_method_subtype", i);
        }

        public String getSelectedInputMethod() {
            return getString("default_input_method", null);
        }

        public String getSelectedInputMethodForUser(int i) {
            return getStringForUser("default_input_method", null, i);
        }

        public void putDefaultVoiceInputMethod(String str) {
            putString("default_voice_input_method", str);
        }

        public String getDefaultVoiceInputMethod() {
            return getString("default_voice_input_method", null);
        }

        public boolean isSubtypeSelected() {
            return getSelectedInputMethodSubtypeHashCode() != -1;
        }

        public final int getSelectedInputMethodSubtypeHashCode() {
            return getInt("selected_input_method_subtype", -1);
        }

        public boolean isShowImeWithHardKeyboardEnabled() {
            return getBoolean("show_ime_with_hard_keyboard", false);
        }

        public void setShowImeWithHardKeyboard(boolean z) {
            putBoolean("show_ime_with_hard_keyboard", z);
        }

        public int getCurrentUserId() {
            return this.mCurrentUserId;
        }

        public int getSelectedInputMethodSubtypeId(String str) {
            InputMethodInfo inputMethodInfo = this.mMethodMap.get(str);
            if (inputMethodInfo == null) {
                return -1;
            }
            return SubtypeUtils.getSubtypeIdFromHashCode(inputMethodInfo, getSelectedInputMethodSubtypeHashCode());
        }

        public void saveCurrentInputMethodAndSubtypeToHistory(String str, InputMethodSubtype inputMethodSubtype) {
            String str2 = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
            if (inputMethodSubtype != null) {
                str2 = String.valueOf(inputMethodSubtype.hashCode());
            }
            if (InputMethodUtils.canAddToLastInputMethod(inputMethodSubtype)) {
                addSubtypeToHistory(str, str2);
            }
        }

        public InputMethodSubtype getCurrentInputMethodSubtypeForNonCurrentUsers() {
            InputMethodInfo inputMethodInfo;
            int subtypeIdFromHashCode;
            String selectedInputMethod = getSelectedInputMethod();
            if (selectedInputMethod == null || (inputMethodInfo = this.mMethodMap.get(selectedInputMethod)) == null || inputMethodInfo.getSubtypeCount() == 0) {
                return null;
            }
            int selectedInputMethodSubtypeHashCode = getSelectedInputMethodSubtypeHashCode();
            if (selectedInputMethodSubtypeHashCode != -1 && (subtypeIdFromHashCode = SubtypeUtils.getSubtypeIdFromHashCode(inputMethodInfo, selectedInputMethodSubtypeHashCode)) >= 0) {
                return inputMethodInfo.getSubtypeAt(subtypeIdFromHashCode);
            }
            List<InputMethodSubtype> enabledInputMethodSubtypeListLocked = getEnabledInputMethodSubtypeListLocked(inputMethodInfo, true);
            if (enabledInputMethodSubtypeListLocked.isEmpty()) {
                return null;
            }
            if (enabledInputMethodSubtypeListLocked.size() == 1) {
                return enabledInputMethodSubtypeListLocked.get(0);
            }
            InputMethodSubtype findLastResortApplicableSubtypeLocked = SubtypeUtils.findLastResortApplicableSubtypeLocked(this.mRes, enabledInputMethodSubtypeListLocked, "keyboard", null, true);
            return findLastResortApplicableSubtypeLocked != null ? findLastResortApplicableSubtypeLocked : SubtypeUtils.findLastResortApplicableSubtypeLocked(this.mRes, enabledInputMethodSubtypeListLocked, null, null, true);
        }

        public boolean setAdditionalInputMethodSubtypes(String str, ArrayList<InputMethodSubtype> arrayList, ArrayMap<String, List<InputMethodSubtype>> arrayMap, PackageManagerInternal packageManagerInternal, int i) {
            InputMethodInfo inputMethodInfo = this.mMethodMap.get(str);
            if (inputMethodInfo != null && InputMethodUtils.checkIfPackageBelongsToUid(packageManagerInternal, i, inputMethodInfo.getPackageName())) {
                if (arrayList.isEmpty()) {
                    arrayMap.remove(inputMethodInfo.getId());
                } else {
                    arrayMap.put(inputMethodInfo.getId(), arrayList);
                }
                AdditionalSubtypeUtils.save(arrayMap, this.mMethodMap, getCurrentUserId());
                return true;
            }
            return false;
        }

        public boolean setEnabledInputMethodSubtypes(String str, int[] iArr) {
            InputMethodInfo inputMethodInfo = this.mMethodMap.get(str);
            if (inputMethodInfo == null) {
                return false;
            }
            IntArray intArray = new IntArray(iArr.length);
            for (int i : iArr) {
                if (i != -1 && SubtypeUtils.isValidSubtypeId(inputMethodInfo, i) && intArray.indexOf(i) < 0) {
                    intArray.add(i);
                }
            }
            String enabledInputMethodsStr = getEnabledInputMethodsStr();
            String updateEnabledImeString = updateEnabledImeString(enabledInputMethodsStr, inputMethodInfo.getId(), intArray);
            if (TextUtils.equals(enabledInputMethodsStr, updateEnabledImeString)) {
                return false;
            }
            putEnabledInputMethodsStr(updateEnabledImeString);
            return true;
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
        public static String updateEnabledImeString(String str, String str2, IntArray intArray) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
            TextUtils.SimpleStringSplitter simpleStringSplitter2 = new TextUtils.SimpleStringSplitter(';');
            StringBuilder sb = new StringBuilder();
            simpleStringSplitter.setString(str);
            boolean z = false;
            while (simpleStringSplitter.hasNext()) {
                String next = simpleStringSplitter.next();
                simpleStringSplitter2.setString(next);
                if (simpleStringSplitter2.hasNext()) {
                    if (z) {
                        sb.append(':');
                    }
                    if (TextUtils.equals(str2, simpleStringSplitter2.next())) {
                        sb.append(str2);
                        for (int i = 0; i < intArray.size(); i++) {
                            sb.append(';');
                            sb.append(intArray.get(i));
                        }
                    } else {
                        sb.append(next);
                    }
                    z = true;
                }
            }
            return sb.toString();
        }

        public void dumpLocked(Printer printer, String str) {
            printer.println(str + "mCurrentUserId=" + this.mCurrentUserId);
            printer.println(str + "mCurrentProfileIds=" + Arrays.toString(this.mCurrentProfileIds));
            printer.println(str + "mCopyOnWrite=" + this.mCopyOnWrite);
            printer.println(str + "mEnabledInputMethodsStrCache=" + this.mEnabledInputMethodsStrCache);
        }
    }

    public static int[] resolveUserId(int i, int i2, PrintWriter printWriter) {
        UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        if (i == -1) {
            return userManagerInternal.getUserIds();
        }
        if (i == -2) {
            i = i2;
        } else if (i < 0) {
            if (printWriter != null) {
                printWriter.print("Pseudo user ID ");
                printWriter.print(i);
                printWriter.println(" is not supported.");
            }
            return new int[0];
        } else if (!userManagerInternal.exists(i)) {
            if (printWriter != null) {
                printWriter.print("User #");
                printWriter.print(i);
                printWriter.println(" does not exit.");
            }
            return new int[0];
        }
        return new int[]{i};
    }

    public static ComponentName convertIdToComponentName(String str) {
        return ComponentName.unflattenFromString(str);
    }
}
