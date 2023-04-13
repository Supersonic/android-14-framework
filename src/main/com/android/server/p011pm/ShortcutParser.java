package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.ShareTargetInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.ShortcutParser */
/* loaded from: classes2.dex */
public class ShortcutParser {
    @VisibleForTesting
    static final String METADATA_KEY = "android.app.shortcuts";

    public static List<ShortcutInfo> parseShortcuts(ShortcutService shortcutService, String str, int i, List<ShareTargetInfo> list) throws IOException, XmlPullParserException {
        ActivityInfo activityInfoWithMetadata;
        List<ResolveInfo> injectGetMainActivities = shortcutService.injectGetMainActivities(str, i);
        if (injectGetMainActivities != null && injectGetMainActivities.size() != 0) {
            list.clear();
            try {
                int size = injectGetMainActivities.size();
                List<ShortcutInfo> list2 = null;
                for (int i2 = 0; i2 < size; i2++) {
                    ActivityInfo activityInfo = injectGetMainActivities.get(i2).activityInfo;
                    if (activityInfo != null && (activityInfoWithMetadata = shortcutService.getActivityInfoWithMetadata(activityInfo.getComponentName(), i)) != null) {
                        list2 = parseShortcutsOneFile(shortcutService, activityInfoWithMetadata, str, i, list2, list);
                    }
                }
                return list2;
            } catch (RuntimeException e) {
                shortcutService.wtf("Exception caught while parsing shortcut XML for package=" + str, e);
            }
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:149:0x0378, code lost:
        r11 = r5;
        r9.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x037c, code lost:
        return r11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static List<ShortcutInfo> parseShortcutsOneFile(ShortcutService shortcutService, ActivityInfo activityInfo, String str, int i, List<ShortcutInfo> list, List<ShareTargetInfo> list2) throws IOException, XmlPullParserException {
        XmlResourceParser xmlResourceParser;
        XmlResourceParser injectXmlMetaData;
        ComponentName componentName;
        AttributeSet asAttributeSet;
        int maxActivityShortcuts;
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        ShareTargetInfo shareTargetInfo;
        ArraySet arraySet;
        int i2;
        ShortcutInfo shortcutInfo;
        int i3;
        int i4;
        ShortcutInfo shortcutInfo2;
        ShareTargetInfo shareTargetInfo2;
        ArraySet arraySet2;
        int i5;
        List<ShortcutInfo> list3;
        char c;
        char c2;
        AttributeSet attributeSet;
        ShortcutService shortcutService2;
        ComponentName componentName2;
        int i6;
        ArraySet arraySet3;
        try {
            injectXmlMetaData = shortcutService.injectXmlMetaData(activityInfo, METADATA_KEY);
        } catch (Throwable th) {
            th = th;
            xmlResourceParser = null;
        }
        if (injectXmlMetaData == null) {
            if (injectXmlMetaData != null) {
                injectXmlMetaData.close();
            }
            return list;
        }
        try {
            componentName = new ComponentName(str, activityInfo.name);
            asAttributeSet = Xml.asAttributeSet(injectXmlMetaData);
            maxActivityShortcuts = shortcutService.getMaxActivityShortcuts();
            arrayList = new ArrayList();
            arrayList2 = new ArrayList();
            arrayList3 = list;
            shareTargetInfo = null;
            arraySet = null;
            i2 = 0;
            shortcutInfo = null;
            i3 = 0;
        } catch (Throwable th2) {
            th = th2;
            xmlResourceParser = injectXmlMetaData;
        }
        while (true) {
            int next = injectXmlMetaData.next();
            if (next == 1 || (next == 3 && injectXmlMetaData.getDepth() <= 0)) {
                break;
            }
            int depth = injectXmlMetaData.getDepth();
            String name = injectXmlMetaData.getName();
            ComponentName componentName3 = componentName;
            AttributeSet attributeSet2 = asAttributeSet;
            if (next != 3 || depth != 2 || !"shortcut".equals(name)) {
                i4 = i2;
                if (next != 3 || depth != 2 || !"share-target".equals(name)) {
                    shortcutInfo2 = shortcutInfo;
                    shareTargetInfo2 = shareTargetInfo;
                    arraySet2 = arraySet;
                    i5 = 2;
                } else if (shareTargetInfo == null) {
                    componentName = componentName3;
                    asAttributeSet = attributeSet2;
                    i2 = i4;
                } else {
                    if (arraySet != null && !arraySet.isEmpty() && !arrayList2.isEmpty()) {
                        shortcutInfo2 = shortcutInfo;
                        list2.add(new ShareTargetInfo((ShareTargetInfo.TargetData[]) arrayList2.toArray(new ShareTargetInfo.TargetData[arrayList2.size()]), shareTargetInfo.mTargetClass, (String[]) arraySet.toArray(new String[arraySet.size()])));
                        arrayList2.clear();
                        i5 = 2;
                        shareTargetInfo2 = null;
                        arraySet2 = null;
                    }
                    componentName = componentName3;
                    asAttributeSet = attributeSet2;
                    i2 = i4;
                    shortcutInfo = shortcutInfo;
                    shareTargetInfo = null;
                }
                if (next == i5 && (depth != 1 || !"shortcuts".equals(name))) {
                    int i7 = 2;
                    if (depth != 2) {
                        list3 = arrayList3;
                        c = 1;
                        c2 = 0;
                    } else if ("shortcut".equals(name)) {
                        list3 = arrayList3;
                        shortcutInfo = parseShortcutAttributes(shortcutService, attributeSet2, str, componentName3, i, i3);
                        if (shortcutInfo != null) {
                            if (list3 != null) {
                                for (int size = list3.size() - 1; size >= 0; size--) {
                                    if (shortcutInfo.getId().equals(list3.get(size).getId())) {
                                        Log.e("ShortcutService", "Duplicate shortcut ID detected. Skipping it.");
                                    }
                                }
                            }
                            arrayList3 = list3;
                            componentName = componentName3;
                            asAttributeSet = attributeSet2;
                            i2 = i4;
                            shareTargetInfo = shareTargetInfo2;
                            arraySet = null;
                        }
                        shortcutService2 = shortcutService;
                        componentName2 = componentName3;
                        attributeSet = attributeSet2;
                        asAttributeSet = attributeSet;
                        componentName = componentName2;
                        arrayList3 = list3;
                        i2 = i4;
                        shortcutInfo = shortcutInfo2;
                        shareTargetInfo = shareTargetInfo2;
                        arraySet = arraySet2;
                    } else {
                        list3 = arrayList3;
                        c = 1;
                        c2 = 0;
                        i7 = 2;
                    }
                    if (depth == i7 && "share-target".equals(name)) {
                        shortcutService2 = shortcutService;
                        attributeSet = attributeSet2;
                        ShareTargetInfo parseShareTargetAttributes = parseShareTargetAttributes(shortcutService2, attributeSet);
                        if (parseShareTargetAttributes == null) {
                            componentName2 = componentName3;
                        } else {
                            arrayList2.clear();
                            asAttributeSet = attributeSet;
                            shareTargetInfo = parseShareTargetAttributes;
                            arrayList3 = list3;
                            componentName = componentName3;
                            i2 = i4;
                            shortcutInfo = shortcutInfo2;
                            arraySet = null;
                        }
                    } else {
                        int i8 = i7;
                        attributeSet = attributeSet2;
                        shortcutService2 = shortcutService;
                        if (depth == 3 && "intent".equals(name)) {
                            if (shortcutInfo2 != null && shortcutInfo2.isEnabled()) {
                                Intent parseIntent = Intent.parseIntent(shortcutService2.mContext.getResources(), injectXmlMetaData, attributeSet);
                                if (TextUtils.isEmpty(parseIntent.getAction())) {
                                    Log.e("ShortcutService", "Shortcut intent action must be provided. activity=" + componentName3);
                                    asAttributeSet = attributeSet;
                                    componentName = componentName3;
                                    arrayList3 = list3;
                                    i2 = i4;
                                    shareTargetInfo = shareTargetInfo2;
                                    arraySet = arraySet2;
                                    shortcutInfo = null;
                                } else {
                                    componentName2 = componentName3;
                                    arrayList.add(parseIntent);
                                }
                            }
                            componentName2 = componentName3;
                            Log.e("ShortcutService", "Ignoring excessive intent tag.");
                        } else {
                            componentName2 = componentName3;
                            int i9 = 3;
                            if (depth == 3) {
                                if (!"categories".equals(name)) {
                                    i9 = 3;
                                } else if (shortcutInfo2 != null && shortcutInfo2.getCategories() == null) {
                                    String parseCategories = parseCategories(shortcutService2, attributeSet);
                                    if (TextUtils.isEmpty(parseCategories)) {
                                        Log.e("ShortcutService", "Empty category found. activity=" + componentName2);
                                    } else {
                                        arraySet3 = arraySet2 == null ? new ArraySet() : arraySet2;
                                        arraySet3.add(parseCategories);
                                        asAttributeSet = attributeSet;
                                        componentName = componentName2;
                                        arraySet = arraySet3;
                                        arrayList3 = list3;
                                        i2 = i4;
                                        shortcutInfo = shortcutInfo2;
                                        shareTargetInfo = shareTargetInfo2;
                                    }
                                }
                            }
                            if (depth != i9) {
                                i6 = i9;
                            } else if (!"category".equals(name)) {
                                i6 = 3;
                            } else if (shareTargetInfo2 != null) {
                                String parseCategory = parseCategory(shortcutService2, attributeSet);
                                if (TextUtils.isEmpty(parseCategory)) {
                                    Log.e("ShortcutService", "Empty category found. activity=" + componentName2);
                                } else {
                                    arraySet3 = arraySet2 == null ? new ArraySet() : arraySet2;
                                    arraySet3.add(parseCategory);
                                    asAttributeSet = attributeSet;
                                    componentName = componentName2;
                                    arraySet = arraySet3;
                                    arrayList3 = list3;
                                    i2 = i4;
                                    shortcutInfo = shortcutInfo2;
                                    shareTargetInfo = shareTargetInfo2;
                                }
                            }
                            if (depth != i6 || !"data".equals(name)) {
                                Object[] objArr = new Object[i8];
                                objArr[c2] = name;
                                objArr[c] = Integer.valueOf(depth);
                                Log.w("ShortcutService", String.format("Invalid tag '%s' found at depth %d", objArr));
                            } else if (shareTargetInfo2 != null) {
                                ShareTargetInfo.TargetData parseShareTargetData = parseShareTargetData(shortcutService2, attributeSet);
                                if (parseShareTargetData == null) {
                                    Log.e("ShortcutService", "Invalid data tag found. activity=" + componentName2);
                                } else {
                                    arrayList2.add(parseShareTargetData);
                                }
                            }
                        }
                    }
                    asAttributeSet = attributeSet;
                    componentName = componentName2;
                    arrayList3 = list3;
                    i2 = i4;
                    shortcutInfo = shortcutInfo2;
                    shareTargetInfo = shareTargetInfo2;
                    arraySet = arraySet2;
                }
                shortcutService2 = shortcutService;
                list3 = arrayList3;
                componentName2 = componentName3;
                attributeSet = attributeSet2;
                asAttributeSet = attributeSet;
                componentName = componentName2;
                arrayList3 = list3;
                i2 = i4;
                shortcutInfo = shortcutInfo2;
                shareTargetInfo = shareTargetInfo2;
                arraySet = arraySet2;
            } else if (shortcutInfo != null) {
                if (shortcutInfo.isEnabled()) {
                    if (arrayList.size() == 0) {
                        Log.e("ShortcutService", "Shortcut " + shortcutInfo.getId() + " has no intent. Skipping it.");
                        componentName = componentName3;
                        asAttributeSet = attributeSet2;
                        shortcutInfo = null;
                    }
                } else {
                    arrayList.clear();
                    arrayList.add(new Intent("android.intent.action.VIEW"));
                }
                if (i2 >= maxActivityShortcuts) {
                    Log.e("ShortcutService", "More than " + maxActivityShortcuts + " shortcuts found for " + activityInfo.getComponentName() + ". Skipping the rest.");
                    injectXmlMetaData.close();
                    return arrayList3;
                }
                ((Intent) arrayList.get(0)).addFlags(268484608);
                try {
                    shortcutInfo.setIntents((Intent[]) arrayList.toArray(new Intent[arrayList.size()]));
                    arrayList.clear();
                    if (arraySet != null) {
                        shortcutInfo.setCategories(arraySet);
                        arraySet = null;
                    }
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                    }
                    arrayList3.add(shortcutInfo);
                    i2++;
                    i3++;
                } catch (RuntimeException unused) {
                    Log.e("ShortcutService", "Shortcut's extras contain un-persistable values. Skipping it.");
                }
                componentName = componentName3;
                asAttributeSet = attributeSet2;
                shortcutInfo = null;
                th = th2;
                xmlResourceParser = injectXmlMetaData;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw th;
            } else {
                i4 = i2;
                componentName = componentName3;
                asAttributeSet = attributeSet2;
                i2 = i4;
            }
        }
    }

    public static String parseCategories(ShortcutService shortcutService, AttributeSet attributeSet) {
        TypedArray obtainAttributes = shortcutService.mContext.getResources().obtainAttributes(attributeSet, R.styleable.ShortcutCategories);
        try {
            if (obtainAttributes.getType(0) == 3) {
                return obtainAttributes.getNonResourceString(0);
            }
            Log.w("ShortcutService", "android:name for shortcut category must be string literal.");
            obtainAttributes.recycle();
            return null;
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ShortcutInfo parseShortcutAttributes(ShortcutService shortcutService, AttributeSet attributeSet, String str, ComponentName componentName, int i, int i2) {
        TypedArray obtainAttributes = shortcutService.mContext.getResources().obtainAttributes(attributeSet, R.styleable.Shortcut);
        try {
            if (obtainAttributes.getType(2) != 3) {
                Log.w("ShortcutService", "android:shortcutId must be string literal. activity=" + componentName);
                return null;
            }
            String nonResourceString = obtainAttributes.getNonResourceString(2);
            boolean z = obtainAttributes.getBoolean(1, true);
            int resourceId = obtainAttributes.getResourceId(0, 0);
            int resourceId2 = obtainAttributes.getResourceId(3, 0);
            int resourceId3 = obtainAttributes.getResourceId(4, 0);
            int resourceId4 = obtainAttributes.getResourceId(5, 0);
            int resourceId5 = obtainAttributes.getResourceId(6, 0);
            String resourceName = resourceId5 != 0 ? shortcutService.mContext.getResources().getResourceName(resourceId5) : null;
            if (TextUtils.isEmpty(nonResourceString)) {
                Log.w("ShortcutService", "android:shortcutId must be provided. activity=" + componentName);
                return null;
            } else if (resourceId2 == 0) {
                Log.w("ShortcutService", "android:shortcutShortLabel must be provided. activity=" + componentName);
                return null;
            } else {
                return createShortcutFromManifest(shortcutService, i, nonResourceString, str, componentName, resourceId2, resourceId3, resourceId4, i2, resourceId, z, resourceName);
            }
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ShortcutInfo createShortcutFromManifest(ShortcutService shortcutService, int i, String str, String str2, ComponentName componentName, int i2, int i3, int i4, int i5, int i6, boolean z, String str3) {
        int i7 = (z ? 32 : 64) | 256;
        int i8 = i6 != 0 ? 4 : 0;
        return new ShortcutInfo(i, str, str2, componentName, null, null, i2, null, null, i3, null, null, i4, null, null, null, i5, null, shortcutService.injectCurrentTimeMillis(), i7 | i8, i6, null, null, null, !z ? 1 : 0, null, null, str3, null);
    }

    public static String parseCategory(ShortcutService shortcutService, AttributeSet attributeSet) {
        TypedArray obtainAttributes = shortcutService.mContext.getResources().obtainAttributes(attributeSet, R.styleable.IntentCategory);
        try {
            if (obtainAttributes.getType(0) != 3) {
                Log.w("ShortcutService", "android:name must be string literal.");
                obtainAttributes.recycle();
                return null;
            }
            return obtainAttributes.getString(0);
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ShareTargetInfo parseShareTargetAttributes(ShortcutService shortcutService, AttributeSet attributeSet) {
        TypedArray obtainAttributes = shortcutService.mContext.getResources().obtainAttributes(attributeSet, R.styleable.Intent);
        try {
            String string = obtainAttributes.getString(4);
            if (TextUtils.isEmpty(string)) {
                Log.w("ShortcutService", "android:targetClass must be provided.");
                return null;
            }
            return new ShareTargetInfo(null, string, null);
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ShareTargetInfo.TargetData parseShareTargetData(ShortcutService shortcutService, AttributeSet attributeSet) {
        TypedArray obtainAttributes = shortcutService.mContext.getResources().obtainAttributes(attributeSet, R.styleable.AndroidManifestData);
        try {
            if (obtainAttributes.getType(0) != 3) {
                Log.w("ShortcutService", "android:mimeType must be string literal.");
                obtainAttributes.recycle();
                return null;
            }
            return new ShareTargetInfo.TargetData(obtainAttributes.getString(1), obtainAttributes.getString(2), obtainAttributes.getString(3), obtainAttributes.getString(4), obtainAttributes.getString(6), obtainAttributes.getString(5), obtainAttributes.getString(0));
        } finally {
            obtainAttributes.recycle();
        }
    }
}
