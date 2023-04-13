package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.util.Slog;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.PreferredComponent */
/* loaded from: classes2.dex */
public class PreferredComponent {
    public boolean mAlways;
    public final Callbacks mCallbacks;
    public final ComponentName mComponent;
    public final int mMatch;
    public String mParseError;
    public final String[] mSetClasses;
    public final String[] mSetComponents;
    public final String[] mSetPackages;
    public final String mShortComponent;

    /* renamed from: com.android.server.pm.PreferredComponent$Callbacks */
    /* loaded from: classes2.dex */
    public interface Callbacks {
        boolean onReadTag(String str, TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException;
    }

    public PreferredComponent(Callbacks callbacks, int i, ComponentName[] componentNameArr, ComponentName componentName, boolean z) {
        this.mCallbacks = callbacks;
        this.mMatch = 268369920 & i;
        this.mComponent = componentName;
        this.mAlways = z;
        this.mShortComponent = componentName.flattenToShortString();
        this.mParseError = null;
        if (componentNameArr != null) {
            int length = componentNameArr.length;
            String[] strArr = new String[length];
            String[] strArr2 = new String[length];
            String[] strArr3 = new String[length];
            for (int i2 = 0; i2 < length; i2++) {
                ComponentName componentName2 = componentNameArr[i2];
                if (componentName2 == null) {
                    this.mSetPackages = null;
                    this.mSetClasses = null;
                    this.mSetComponents = null;
                    return;
                }
                strArr[i2] = componentName2.getPackageName().intern();
                strArr2[i2] = componentName2.getClassName().intern();
                strArr3[i2] = componentName2.flattenToShortString();
            }
            this.mSetPackages = strArr;
            this.mSetClasses = strArr2;
            this.mSetComponents = strArr3;
            return;
        }
        this.mSetPackages = null;
        this.mSetClasses = null;
        this.mSetComponents = null;
    }

    public PreferredComponent(Callbacks callbacks, TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        this.mCallbacks = callbacks;
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        this.mShortComponent = attributeValue;
        ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue);
        this.mComponent = unflattenFromString;
        if (unflattenFromString == null) {
            this.mParseError = "Bad activity name " + attributeValue;
        }
        int i = 0;
        this.mMatch = typedXmlPullParser.getAttributeIntHex((String) null, "match", 0);
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "set", 0);
        this.mAlways = typedXmlPullParser.getAttributeBoolean((String) null, "always", true);
        String[] strArr = attributeInt > 0 ? new String[attributeInt] : null;
        String[] strArr2 = attributeInt > 0 ? new String[attributeInt] : null;
        String[] strArr3 = attributeInt > 0 ? new String[attributeInt] : null;
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (name.equals("set")) {
                    String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "name");
                    if (attributeValue2 == null) {
                        if (this.mParseError == null) {
                            this.mParseError = "No name in set tag in preferred activity " + this.mShortComponent;
                        }
                    } else if (i >= attributeInt) {
                        if (this.mParseError == null) {
                            this.mParseError = "Too many set tags in preferred activity " + this.mShortComponent;
                        }
                    } else {
                        ComponentName unflattenFromString2 = ComponentName.unflattenFromString(attributeValue2);
                        if (unflattenFromString2 == null) {
                            if (this.mParseError == null) {
                                this.mParseError = "Bad set name " + attributeValue2 + " in preferred activity " + this.mShortComponent;
                            }
                        } else {
                            strArr[i] = unflattenFromString2.getPackageName();
                            strArr2[i] = unflattenFromString2.getClassName();
                            strArr3[i] = attributeValue2;
                            i++;
                        }
                    }
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                } else if (!this.mCallbacks.onReadTag(name, typedXmlPullParser)) {
                    Slog.w("PreferredComponent", "Unknown element: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        if (i != attributeInt && this.mParseError == null) {
            this.mParseError = "Not enough set tags (expected " + attributeInt + " but found " + i + ") in " + this.mShortComponent;
        }
        this.mSetPackages = strArr;
        this.mSetClasses = strArr2;
        this.mSetComponents = strArr3;
    }

    public String getParseError() {
        return this.mParseError;
    }

    public void writeToXml(TypedXmlSerializer typedXmlSerializer, boolean z) throws IOException {
        String[] strArr = this.mSetClasses;
        int length = strArr != null ? strArr.length : 0;
        typedXmlSerializer.attribute((String) null, "name", this.mShortComponent);
        if (z) {
            int i = this.mMatch;
            if (i != 0) {
                typedXmlSerializer.attributeIntHex((String) null, "match", i);
            }
            typedXmlSerializer.attributeBoolean((String) null, "always", this.mAlways);
            typedXmlSerializer.attributeInt((String) null, "set", length);
            for (int i2 = 0; i2 < length; i2++) {
                typedXmlSerializer.startTag((String) null, "set");
                typedXmlSerializer.attribute((String) null, "name", this.mSetComponents[i2]);
                typedXmlSerializer.endTag((String) null, "set");
            }
        }
    }

    public boolean sameSet(List<ResolveInfo> list, boolean z, int i) {
        PackageUserStateInternal packageUserStateInternal;
        boolean z2;
        if (this.mSetPackages == null) {
            return list == null;
        } else if (list == null) {
            return false;
        } else {
            int size = list.size();
            int length = this.mSetPackages.length;
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            String setupWizardPackageName = packageManagerInternal.getSetupWizardPackageName();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                ActivityInfo activityInfo = list.get(i3).activityInfo;
                if ((!z || !activityInfo.packageName.equals(setupWizardPackageName)) && ((packageUserStateInternal = packageManagerInternal.getPackageStateInternal(activityInfo.packageName).getUserStates().get(i)) == null || packageUserStateInternal.getInstallReason() != 3)) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= length) {
                            z2 = false;
                            break;
                        } else if (this.mSetPackages[i4].equals(activityInfo.packageName) && this.mSetClasses[i4].equals(activityInfo.name)) {
                            i2++;
                            z2 = true;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (!z2) {
                        return false;
                    }
                }
            }
            return i2 == length;
        }
    }

    public boolean sameSet(ComponentName[] componentNameArr) {
        String[] strArr = this.mSetPackages;
        if (strArr == null) {
            return false;
        }
        int length = componentNameArr.length;
        int length2 = strArr.length;
        int i = 0;
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i >= length) {
                return i2 == length2;
            }
            ComponentName componentName = componentNameArr[i];
            int i3 = 0;
            while (true) {
                if (i3 >= length2) {
                    z = false;
                    break;
                } else if (this.mSetPackages[i3].equals(componentName.getPackageName()) && this.mSetClasses[i3].equals(componentName.getClassName())) {
                    i2++;
                    break;
                } else {
                    i3++;
                }
            }
            if (!z) {
                return false;
            }
            i++;
        }
    }

    public boolean sameSet(PreferredComponent preferredComponent) {
        if (this.mSetPackages == null || preferredComponent == null || preferredComponent.mSetPackages == null || !sameComponent(preferredComponent.mComponent)) {
            return false;
        }
        int length = preferredComponent.mSetPackages.length;
        int length2 = this.mSetPackages.length;
        if (length != length2) {
            return false;
        }
        for (int i = 0; i < length2; i++) {
            if (!this.mSetPackages[i].equals(preferredComponent.mSetPackages[i]) || !this.mSetClasses[i].equals(preferredComponent.mSetClasses[i])) {
                return false;
            }
        }
        return true;
    }

    public final boolean sameComponent(ComponentName componentName) {
        ComponentName componentName2 = this.mComponent;
        return componentName2 != null && componentName != null && componentName2.getPackageName().equals(componentName.getPackageName()) && this.mComponent.getClassName().equals(componentName.getClassName());
    }

    public boolean isSuperset(List<ResolveInfo> list, boolean z) {
        boolean z2;
        if (this.mSetPackages == null) {
            return list == null;
        } else if (list == null) {
            return true;
        } else {
            int size = list.size();
            int length = this.mSetPackages.length;
            if (z || length >= size) {
                String setupWizardPackageName = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getSetupWizardPackageName();
                for (int i = 0; i < size; i++) {
                    ActivityInfo activityInfo = list.get(i).activityInfo;
                    if (!z || !activityInfo.packageName.equals(setupWizardPackageName)) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length) {
                                z2 = false;
                                break;
                            } else if (this.mSetPackages[i2].equals(activityInfo.packageName) && this.mSetClasses[i2].equals(activityInfo.name)) {
                                z2 = true;
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (!z2) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }

    public ComponentName[] discardObsoleteComponents(List<ResolveInfo> list) {
        if (this.mSetPackages == null || list == null) {
            return new ComponentName[0];
        }
        int size = list.size();
        int length = this.mSetPackages.length;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            ActivityInfo activityInfo = list.get(i).activityInfo;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                if (this.mSetPackages[i2].equals(activityInfo.packageName) && this.mSetClasses[i2].equals(activityInfo.name)) {
                    arrayList.add(new ComponentName(this.mSetPackages[i2], this.mSetClasses[i2]));
                    break;
                }
                i2++;
            }
        }
        return (ComponentName[]) arrayList.toArray(new ComponentName[arrayList.size()]);
    }

    public void dump(PrintWriter printWriter, String str, Object obj) {
        printWriter.print(str);
        printWriter.print(Integer.toHexString(System.identityHashCode(obj)));
        printWriter.print(' ');
        printWriter.println(this.mShortComponent);
        printWriter.print(str);
        printWriter.print(" mMatch=0x");
        printWriter.print(Integer.toHexString(this.mMatch));
        printWriter.print(" mAlways=");
        printWriter.println(this.mAlways);
        if (this.mSetComponents != null) {
            printWriter.print(str);
            printWriter.println("  Selected from:");
            for (int i = 0; i < this.mSetComponents.length; i++) {
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.println(this.mSetComponents[i]);
            }
        }
    }
}
