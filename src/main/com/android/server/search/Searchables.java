package com.android.server.search;

import android.app.AppGlobals;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.LocalServices;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes2.dex */
public class Searchables {
    public static final Comparator<ResolveInfo> GLOBAL_SEARCH_RANKER = new Comparator<ResolveInfo>() { // from class: com.android.server.search.Searchables.1
        @Override // java.util.Comparator
        public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
            if (resolveInfo == resolveInfo2) {
                return 0;
            }
            boolean isSystemApp = Searchables.isSystemApp(resolveInfo);
            boolean isSystemApp2 = Searchables.isSystemApp(resolveInfo2);
            if (!isSystemApp || isSystemApp2) {
                if (!isSystemApp2 || isSystemApp) {
                    return resolveInfo2.priority - resolveInfo.priority;
                }
                return 1;
            }
            return -1;
        }
    };
    public Context mContext;
    public List<ResolveInfo> mGlobalSearchActivities;
    public int mUserId;
    public HashMap<ComponentName, SearchableInfo> mSearchablesMap = null;
    public ArrayList<SearchableInfo> mSearchablesList = null;
    public ArrayList<SearchableInfo> mSearchablesInGlobalSearchList = null;
    public ComponentName mCurrentGlobalSearchActivity = null;
    public ComponentName mWebSearchActivity = null;
    public final IPackageManager mPm = AppGlobals.getPackageManager();

    public Searchables(Context context, int i) {
        this.mContext = context;
        this.mUserId = i;
    }

    public SearchableInfo getSearchableInfo(ComponentName componentName) {
        ComponentName componentName2;
        SearchableInfo searchableInfo;
        Bundle bundle;
        synchronized (this) {
            SearchableInfo searchableInfo2 = this.mSearchablesMap.get(componentName);
            if (searchableInfo2 != null) {
                if (((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).canAccessComponent(Binder.getCallingUid(), searchableInfo2.getSearchActivity(), UserHandle.getCallingUserId())) {
                    return searchableInfo2;
                }
                return null;
            }
            try {
                ActivityInfo activityInfo = this.mPm.getActivityInfo(componentName, 128L, this.mUserId);
                Bundle bundle2 = activityInfo.metaData;
                String string = bundle2 != null ? bundle2.getString("android.app.default_searchable") : null;
                if (string == null && (bundle = activityInfo.applicationInfo.metaData) != null) {
                    string = bundle.getString("android.app.default_searchable");
                }
                if (string == null || string.equals("*")) {
                    return null;
                }
                String packageName = componentName.getPackageName();
                if (string.charAt(0) == '.') {
                    componentName2 = new ComponentName(packageName, packageName + string);
                } else {
                    componentName2 = new ComponentName(packageName, string);
                }
                synchronized (this) {
                    searchableInfo = this.mSearchablesMap.get(componentName2);
                    if (searchableInfo != null) {
                        this.mSearchablesMap.put(componentName, searchableInfo);
                    }
                }
                if (searchableInfo == null || !((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).canAccessComponent(Binder.getCallingUid(), searchableInfo.getSearchActivity(), UserHandle.getCallingUserId())) {
                    return null;
                }
                return searchableInfo;
            } catch (RemoteException e) {
                Log.e("Searchables", "Error getting activity info " + e);
                return null;
            }
        }
    }

    public void updateSearchableList() {
        ResolveInfo resolveInfo;
        SearchableInfo activityMetaData;
        HashMap<ComponentName, SearchableInfo> hashMap = new HashMap<>();
        ArrayList<SearchableInfo> arrayList = new ArrayList<>();
        ArrayList<SearchableInfo> arrayList2 = new ArrayList<>();
        Intent intent = new Intent("android.intent.action.SEARCH");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<ResolveInfo> queryIntentActivities = queryIntentActivities(intent, 268435584);
            List<ResolveInfo> queryIntentActivities2 = queryIntentActivities(new Intent("android.intent.action.WEB_SEARCH"), 268435584);
            if (queryIntentActivities != null || queryIntentActivities2 != null) {
                int size = queryIntentActivities == null ? 0 : queryIntentActivities.size();
                int size2 = (queryIntentActivities2 == null ? 0 : queryIntentActivities2.size()) + size;
                for (int i = 0; i < size2; i++) {
                    if (i < size) {
                        resolveInfo = queryIntentActivities.get(i);
                    } else {
                        resolveInfo = queryIntentActivities2.get(i - size);
                    }
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    if (hashMap.get(new ComponentName(activityInfo.packageName, activityInfo.name)) == null && (activityMetaData = SearchableInfo.getActivityMetaData(this.mContext, activityInfo, this.mUserId)) != null) {
                        arrayList.add(activityMetaData);
                        hashMap.put(activityMetaData.getSearchActivity(), activityMetaData);
                        if (activityMetaData.shouldIncludeInGlobalSearch()) {
                            arrayList2.add(activityMetaData);
                        }
                    }
                }
            }
            List<ResolveInfo> findGlobalSearchActivities = findGlobalSearchActivities();
            ComponentName findGlobalSearchActivity = findGlobalSearchActivity(findGlobalSearchActivities);
            ComponentName findWebSearchActivity = findWebSearchActivity(findGlobalSearchActivity);
            synchronized (this) {
                this.mSearchablesMap = hashMap;
                this.mSearchablesList = arrayList;
                this.mSearchablesInGlobalSearchList = arrayList2;
                this.mGlobalSearchActivities = findGlobalSearchActivities;
                this.mCurrentGlobalSearchActivity = findGlobalSearchActivity;
                this.mWebSearchActivity = findWebSearchActivity;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final List<ResolveInfo> findGlobalSearchActivities() {
        List<ResolveInfo> queryIntentActivities = queryIntentActivities(new Intent("android.search.action.GLOBAL_SEARCH"), 268500992);
        if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
            Collections.sort(queryIntentActivities, GLOBAL_SEARCH_RANKER);
        }
        return queryIntentActivities;
    }

    public final ComponentName findGlobalSearchActivity(List<ResolveInfo> list) {
        ComponentName unflattenFromString;
        String globalSearchProviderSetting = getGlobalSearchProviderSetting();
        return (TextUtils.isEmpty(globalSearchProviderSetting) || (unflattenFromString = ComponentName.unflattenFromString(globalSearchProviderSetting)) == null || !isInstalled(unflattenFromString)) ? getDefaultGlobalSearchProvider(list) : unflattenFromString;
    }

    public final boolean isInstalled(ComponentName componentName) {
        Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
        intent.setComponent(componentName);
        List<ResolveInfo> queryIntentActivities = queryIntentActivities(intent, 65536);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    public static final boolean isSystemApp(ResolveInfo resolveInfo) {
        return (resolveInfo.activityInfo.applicationInfo.flags & 1) != 0;
    }

    public final ComponentName getDefaultGlobalSearchProvider(List<ResolveInfo> list) {
        if (list != null && !list.isEmpty()) {
            ActivityInfo activityInfo = list.get(0).activityInfo;
            return new ComponentName(activityInfo.packageName, activityInfo.name);
        }
        Log.w("Searchables", "No global search activity found");
        return null;
    }

    public final String getGlobalSearchProviderSetting() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        return Settings.Secure.getStringForUser(contentResolver, "search_global_search_activity", contentResolver.getUserId());
    }

    public final ComponentName findWebSearchActivity(ComponentName componentName) {
        if (componentName == null) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.WEB_SEARCH");
        intent.setPackage(componentName.getPackageName());
        List<ResolveInfo> queryIntentActivities = queryIntentActivities(intent, 65536);
        if (queryIntentActivities != null && !queryIntentActivities.isEmpty()) {
            ActivityInfo activityInfo = queryIntentActivities.get(0).activityInfo;
            return new ComponentName(activityInfo.packageName, activityInfo.name);
        }
        Log.w("Searchables", "No web search activity found");
        return null;
    }

    public final List<ResolveInfo> queryIntentActivities(Intent intent, int i) {
        try {
            return this.mPm.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), i | 8388608, this.mUserId).getList();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public synchronized ArrayList<SearchableInfo> getSearchablesInGlobalSearchList() {
        return createFilterdSearchableInfoList(this.mSearchablesInGlobalSearchList);
    }

    public synchronized ArrayList<ResolveInfo> getGlobalSearchActivities() {
        return createFilterdResolveInfoList(this.mGlobalSearchActivities);
    }

    public final ArrayList<SearchableInfo> createFilterdSearchableInfoList(List<SearchableInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList<SearchableInfo> arrayList = new ArrayList<>(list.size());
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        for (SearchableInfo searchableInfo : list) {
            if (packageManagerInternal.canAccessComponent(callingUid, searchableInfo.getSearchActivity(), callingUserId)) {
                arrayList.add(searchableInfo);
            }
        }
        return arrayList;
    }

    public final ArrayList<ResolveInfo> createFilterdResolveInfoList(List<ResolveInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList<ResolveInfo> arrayList = new ArrayList<>(list.size());
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        for (ResolveInfo resolveInfo : list) {
            if (packageManagerInternal.canAccessComponent(callingUid, resolveInfo.activityInfo.getComponentName(), callingUserId)) {
                arrayList.add(resolveInfo);
            }
        }
        return arrayList;
    }

    public synchronized ComponentName getGlobalSearchActivity() {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        ComponentName componentName = this.mCurrentGlobalSearchActivity;
        if (componentName == null || !packageManagerInternal.canAccessComponent(callingUid, componentName, callingUserId)) {
            return null;
        }
        return this.mCurrentGlobalSearchActivity;
    }

    public synchronized ComponentName getWebSearchActivity() {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        ComponentName componentName = this.mWebSearchActivity;
        if (componentName == null || !packageManagerInternal.canAccessComponent(callingUid, componentName, callingUserId)) {
            return null;
        }
        return this.mWebSearchActivity;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Searchable authorities:");
        synchronized (this) {
            ArrayList<SearchableInfo> arrayList = this.mSearchablesList;
            if (arrayList != null) {
                Iterator<SearchableInfo> it = arrayList.iterator();
                while (it.hasNext()) {
                    printWriter.print("  ");
                    printWriter.println(it.next().getSuggestAuthority());
                }
            }
        }
    }
}
