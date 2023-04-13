package android.view.inputmethod;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Printer;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public final class InputMethodInfo implements Parcelable {
    public static final String ACTION_STYLUS_HANDWRITING_SETTINGS = "android.view.inputmethod.action.STYLUS_HANDWRITING_SETTINGS";
    public static final int COMPONENT_NAME_MAX_LENGTH = 1000;
    public static final Parcelable.Creator<InputMethodInfo> CREATOR = new Parcelable.Creator<InputMethodInfo>() { // from class: android.view.inputmethod.InputMethodInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMethodInfo createFromParcel(Parcel source) {
            return new InputMethodInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMethodInfo[] newArray(int size) {
            return new InputMethodInfo[size];
        }
    };
    public static final int MAX_IMES_PER_PACKAGE = 20;
    static final String TAG = "InputMethodInfo";
    private final boolean mForceDefault;
    private final int mHandledConfigChanges;
    final String mId;
    private final boolean mInlineSuggestionsEnabled;
    private final boolean mIsAuxIme;
    final int mIsDefaultResId;
    final boolean mIsVrOnly;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    private final boolean mShowInInputMethodPicker;
    private final String mStylusHandwritingSettingsActivityAttr;
    private final InputMethodSubtypeArray mSubtypes;
    private final boolean mSupportsInlineSuggestionsWithTouchExploration;
    private final boolean mSupportsStylusHandwriting;
    private final boolean mSupportsSwitchingToNextInputMethod;
    private final boolean mSuppressesSpellChecker;

    public static String computeId(ResolveInfo service) {
        ServiceInfo si = service.serviceInfo;
        return new ComponentName(si.packageName, si.name).flattenToShortString();
    }

    public InputMethodInfo(Context context, ResolveInfo service) throws XmlPullParserException, IOException {
        this(context, service, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x02dc, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x02ea, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x02f8, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0305, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0312, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x031d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x0328, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x0333, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:118:0x0353, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Meta-data does not start with input-method tag");
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x0354, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x0361, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x03be, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Unable to create context for: " + r4.packageName);
     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x03bf, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x03c0, code lost:
        if (r15 != null) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x03c2, code lost:
        r15.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x03c5, code lost:
        throw r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0057, code lost:
        r5 = r15.getName();
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0063, code lost:
        if ("input-method".equals(r5) == false) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0065, code lost:
        r5 = r18;
        r2 = r0.obtainAttributes(r5, com.android.internal.C4057R.styleable.InputMethod);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0072, code lost:
        r23 = r2.getString(2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x007e, code lost:
        if (r4.name == null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0086, code lost:
        if (r4.name.length() > 1000) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0089, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0094, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x009f, code lost:
        if (r23 == null) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00a5, code lost:
        if (r23.length() > 1000) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00af, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Activity name exceeds maximum of 1000 characters");
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00b2, code lost:
        r16 = r2.getBoolean(4, false);
        r16 = r2.getResourceId(1, 0);
        r16 = r2.getBoolean(3, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00c7, code lost:
        r16 = r2.getBoolean(5, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00cf, code lost:
        r16 = r2.getBoolean(9, false);
        r16 = r2.getBoolean(6, false);
        r25 = r2.getBoolean(7, true);
        r31.mHandledConfigChanges = r2.getInt(0, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ef, code lost:
        r31.mSupportsStylusHandwriting = r2.getBoolean(8, false);
        r11 = r2.getString(11);
        r2.recycle();
        r11 = r15.getDepth();
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0103, code lost:
        r6 = r15.next();
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x010a, code lost:
        if (r6 != 3) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0110, code lost:
        if (r15.getDepth() <= r11) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0113, code lost:
        r6 = r17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0117, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0120, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x012c, code lost:
        if (r6 == 1) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x012f, code lost:
        if (r6 != 2) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0131, code lost:
        r7 = r15.getName();
        r27 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x013e, code lost:
        if ("subtype".equals(r7) == false) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0140, code lost:
        r2 = r0.obtainAttributes(r5, com.android.internal.C4057R.styleable.InputMethod_Subtype);
        r28 = r0;
        r0 = r2.getString(10);
        r29 = r5;
        r21 = r2.getString(11);
        r5 = new android.view.inputmethod.InputMethodSubtype.InputMethodSubtypeBuilder().setSubtypeNameResId(r2.getResourceId(0, 0));
        r18 = r11;
        r11 = r2.getResourceId(1, 0);
        r5 = r5.setSubtypeIconResId(r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0173, code lost:
        if (r0 != null) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0175, code lost:
        r6 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0177, code lost:
        r6 = new android.icu.util.ULocale(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x017c, code lost:
        if (r21 != null) goto L76;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x017e, code lost:
        r11 = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0181, code lost:
        r11 = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0183, code lost:
        r5 = r5.setPhysicalKeyboardHint(r6, r11).setLanguageTag(r2.getString(9)).setSubtypeLocale(r2.getString(2)).setSubtypeMode(r2.getString(3)).setSubtypeExtraValue(r2.getString(4)).setIsAuxiliary(r2.getBoolean(5, false)).setOverridesImplicitlyEnabledSubtype(r2.getBoolean(6, false)).setSubtypeId(r2.getInt(7, 0)).setIsAsciiCapable(r2.getBoolean(8, false)).build();
        r2.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01dd, code lost:
        if (r5.isAuxiliary() != false) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01df, code lost:
        r20 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x01e2, code lost:
        r6 = r17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01e4, code lost:
        r6.add(r5);
        r17 = r6;
        r11 = r18;
        r2 = r27;
        r0 = r28;
        r5 = r29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x020b, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Meta-data in input-method does not start with subtype tag");
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x020c, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0215, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x021e, code lost:
        r11 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0236, code lost:
        r6 = r17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0242, code lost:
        if (r15 == null) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x0244, code lost:
        r15.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x024b, code lost:
        if (r6.size() != 0) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x024d, code lost:
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0252, code lost:
        r0 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x0254, code lost:
        if (r34 == null) goto L108;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0256, code lost:
        r2 = r34.size();
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x025b, code lost:
        if (r5 >= r2) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x025d, code lost:
        r7 = r34.get(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0267, code lost:
        if (r6.contains(r7) != false) goto L104;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0269, code lost:
        r6.add(r7);
        r16 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x026f, code lost:
        r16 = r2;
        android.util.Slog.m90w(android.view.inputmethod.InputMethodInfo.TAG, "Duplicated subtype definition found: " + r7.getLocale() + ", " + r7.getMode());
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x029b, code lost:
        r5 = r5 + 1;
        r2 = r16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x02a2, code lost:
        r31.mSubtypes = new android.view.inputmethod.InputMethodSubtypeArray(r6);
        r31.mSettingsActivityName = r23;
        r31.mStylusHandwritingSettingsActivityAttr = r11;
        r31.mIsDefaultResId = r16;
        r31.mIsAuxIme = r0;
        r31.mSupportsSwitchingToNextInputMethod = r16;
        r31.mInlineSuggestionsEnabled = r16;
        r31.mSupportsInlineSuggestionsWithTouchExploration = r16;
        r31.mSuppressesSpellChecker = r16;
        r31.mShowInInputMethodPicker = r25;
        r31.mIsVrOnly = r16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x02c3, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x02c4, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x02d0, code lost:
        r0 = e;
     */
    /* JADX WARN: Not initialized variable reg: 20, insn: 0x0380: MOVE  (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r20 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('isAuxIme' boolean)]), block:B:126:0x0380 */
    /* JADX WARN: Not initialized variable reg: 20, insn: 0x0386: MOVE  (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r20 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('isAuxIme' boolean)]), block:B:128:0x0386 */
    /* JADX WARN: Not initialized variable reg: 23, insn: 0x0382: MOVE  (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r23 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('inlineSuggestionsEnabled' boolean)]), block:B:126:0x0380 */
    /* JADX WARN: Not initialized variable reg: 23, insn: 0x0388: MOVE  (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r23 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('inlineSuggestionsEnabled' boolean)]), block:B:128:0x0386 */
    /* JADX WARN: Removed duplicated region for block: B:137:0x03c2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public InputMethodInfo(Context context, ResolveInfo service, List<InputMethodSubtype> additionalSubtypes) throws XmlPullParserException, IOException {
        int type;
        this.mService = service;
        ServiceInfo si = service.serviceInfo;
        this.mId = computeId(service);
        boolean isAuxIme = true;
        this.mForceDefault = false;
        PackageManager pm = context.getPackageManager();
        XmlResourceParser parser = null;
        ArrayList<InputMethodSubtype> subtypes = new ArrayList<>();
        try {
            parser = si.loadXmlMetaData(pm, InputMethod.SERVICE_META_DATA);
            try {
                if (parser == null) {
                    throw new XmlPullParserException("No android.view.im meta-data");
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type2 = parser.next();
                    boolean isAuxIme2 = isAuxIme;
                    if (type2 == 1) {
                        type = type2;
                        break;
                    }
                    type = type2;
                    if (type != 2) {
                        isAuxIme = isAuxIme2;
                    }
                }
            } catch (PackageManager.NameNotFoundException | IndexOutOfBoundsException | NumberFormatException e) {
                e = e;
            } catch (Throwable th) {
                th = th;
            }
        } catch (PackageManager.NameNotFoundException | IndexOutOfBoundsException | NumberFormatException e2) {
            e = e2;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public InputMethodInfo(InputMethodInfo source) {
        this.mId = source.mId;
        this.mSettingsActivityName = source.mSettingsActivityName;
        this.mIsDefaultResId = source.mIsDefaultResId;
        this.mIsAuxIme = source.mIsAuxIme;
        this.mSupportsSwitchingToNextInputMethod = source.mSupportsSwitchingToNextInputMethod;
        this.mInlineSuggestionsEnabled = source.mInlineSuggestionsEnabled;
        this.mSupportsInlineSuggestionsWithTouchExploration = source.mSupportsInlineSuggestionsWithTouchExploration;
        this.mSuppressesSpellChecker = source.mSuppressesSpellChecker;
        this.mShowInInputMethodPicker = source.mShowInInputMethodPicker;
        this.mIsVrOnly = source.mIsVrOnly;
        this.mService = source.mService;
        this.mSubtypes = source.mSubtypes;
        this.mHandledConfigChanges = source.mHandledConfigChanges;
        this.mSupportsStylusHandwriting = source.mSupportsStylusHandwriting;
        this.mForceDefault = source.mForceDefault;
        this.mStylusHandwritingSettingsActivityAttr = source.mStylusHandwritingSettingsActivityAttr;
    }

    InputMethodInfo(Parcel source) {
        this.mId = source.readString();
        this.mSettingsActivityName = source.readString();
        this.mIsDefaultResId = source.readInt();
        this.mIsAuxIme = source.readInt() == 1;
        this.mSupportsSwitchingToNextInputMethod = source.readInt() == 1;
        this.mInlineSuggestionsEnabled = source.readInt() == 1;
        this.mSupportsInlineSuggestionsWithTouchExploration = source.readInt() == 1;
        this.mSuppressesSpellChecker = source.readBoolean();
        this.mShowInInputMethodPicker = source.readBoolean();
        this.mIsVrOnly = source.readBoolean();
        this.mService = ResolveInfo.CREATOR.createFromParcel(source);
        this.mSubtypes = new InputMethodSubtypeArray(source);
        this.mHandledConfigChanges = source.readInt();
        this.mSupportsStylusHandwriting = source.readBoolean();
        this.mStylusHandwritingSettingsActivityAttr = source.readString8();
        this.mForceDefault = false;
    }

    public InputMethodInfo(String packageName, String className, CharSequence label, String settingsActivity) {
        this(buildFakeResolveInfo(packageName, className, label), false, settingsActivity, null, 0, false, true, false, false, 0, false, null, false);
    }

    public InputMethodInfo(String packageName, String className, CharSequence label, String settingsActivity, boolean supportStylusHandwriting, String stylusHandwritingSettingsActivityAttr) {
        this(buildFakeResolveInfo(packageName, className, label), false, settingsActivity, null, 0, false, true, false, false, 0, supportStylusHandwriting, stylusHandwritingSettingsActivityAttr, false);
    }

    public InputMethodInfo(String packageName, String className, CharSequence label, String settingsActivity, int handledConfigChanges) {
        this(buildFakeResolveInfo(packageName, className, label), false, settingsActivity, null, 0, false, true, false, false, handledConfigChanges, false, null, false);
    }

    public InputMethodInfo(ResolveInfo ri, boolean isAuxIme, String settingsActivity, List<InputMethodSubtype> subtypes, int isDefaultResId, boolean forceDefault) {
        this(ri, isAuxIme, settingsActivity, subtypes, isDefaultResId, forceDefault, true, false, false, 0, false, null, false);
    }

    public InputMethodInfo(ResolveInfo ri, boolean isAuxIme, String settingsActivity, List<InputMethodSubtype> subtypes, int isDefaultResId, boolean forceDefault, boolean supportsSwitchingToNextInputMethod, boolean isVrOnly) {
        this(ri, isAuxIme, settingsActivity, subtypes, isDefaultResId, forceDefault, supportsSwitchingToNextInputMethod, false, isVrOnly, 0, false, null, false);
    }

    public InputMethodInfo(ResolveInfo ri, boolean isAuxIme, String settingsActivity, List<InputMethodSubtype> subtypes, int isDefaultResId, boolean forceDefault, boolean supportsSwitchingToNextInputMethod, boolean inlineSuggestionsEnabled, boolean isVrOnly, int handledConfigChanges, boolean supportsStylusHandwriting, String stylusHandwritingSettingsActivityAttr, boolean supportsInlineSuggestionsWithTouchExploration) {
        ServiceInfo si = ri.serviceInfo;
        this.mService = ri;
        this.mId = new ComponentName(si.packageName, si.name).flattenToShortString();
        this.mSettingsActivityName = settingsActivity;
        this.mIsDefaultResId = isDefaultResId;
        this.mIsAuxIme = isAuxIme;
        this.mSubtypes = new InputMethodSubtypeArray(subtypes);
        this.mForceDefault = forceDefault;
        this.mSupportsSwitchingToNextInputMethod = supportsSwitchingToNextInputMethod;
        this.mInlineSuggestionsEnabled = inlineSuggestionsEnabled;
        this.mSupportsInlineSuggestionsWithTouchExploration = supportsInlineSuggestionsWithTouchExploration;
        this.mSuppressesSpellChecker = false;
        this.mShowInInputMethodPicker = true;
        this.mIsVrOnly = isVrOnly;
        this.mHandledConfigChanges = handledConfigChanges;
        this.mSupportsStylusHandwriting = supportsStylusHandwriting;
        this.mStylusHandwritingSettingsActivityAttr = stylusHandwritingSettingsActivityAttr;
    }

    private static ResolveInfo buildFakeResolveInfo(String packageName, String className, CharSequence label) {
        ResolveInfo ri = new ResolveInfo();
        ServiceInfo si = new ServiceInfo();
        ApplicationInfo ai = new ApplicationInfo();
        ai.packageName = packageName;
        ai.enabled = true;
        si.applicationInfo = ai;
        si.enabled = true;
        si.packageName = packageName;
        si.name = className;
        si.exported = true;
        si.nonLocalizedLabel = label;
        ri.serviceInfo = si;
        return ri;
    }

    public String getId() {
        return this.mId;
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public String getServiceName() {
        return this.mService.serviceInfo.name;
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mService.loadLabel(pm);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public boolean isVrOnly() {
        return this.mIsVrOnly;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.getCount();
    }

    public InputMethodSubtype getSubtypeAt(int index) {
        return this.mSubtypes.get(index);
    }

    public int getIsDefaultResourceId() {
        return this.mIsDefaultResId;
    }

    public boolean isDefault(Context context) {
        if (this.mForceDefault) {
            return true;
        }
        try {
            if (getIsDefaultResourceId() == 0) {
                return false;
            }
            Resources res = context.createPackageContext(getPackageName(), 0).getResources();
            return res.getBoolean(getIsDefaultResourceId());
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            return false;
        }
    }

    public int getConfigChanges() {
        return this.mHandledConfigChanges;
    }

    public boolean supportsStylusHandwriting() {
        return this.mSupportsStylusHandwriting;
    }

    public Intent createStylusHandwritingSettingsActivityIntent() {
        if (TextUtils.isEmpty(this.mStylusHandwritingSettingsActivityAttr) || !this.mSupportsStylusHandwriting) {
            return null;
        }
        return new Intent(ACTION_STYLUS_HANDWRITING_SETTINGS).setComponent(new ComponentName(getServiceInfo().packageName, this.mStylusHandwritingSettingsActivityAttr));
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + "mId=" + this.mId + " mSettingsActivityName=" + this.mSettingsActivityName + " mIsVrOnly=" + this.mIsVrOnly + " mSupportsSwitchingToNextInputMethod=" + this.mSupportsSwitchingToNextInputMethod + " mInlineSuggestionsEnabled=" + this.mInlineSuggestionsEnabled + " mSupportsInlineSuggestionsWithTouchExploration=" + this.mSupportsInlineSuggestionsWithTouchExploration + " mSuppressesSpellChecker=" + this.mSuppressesSpellChecker + " mShowInInputMethodPicker=" + this.mShowInInputMethodPicker + " mSupportsStylusHandwriting=" + this.mSupportsStylusHandwriting + " mStylusHandwritingSettingsActivityAttr=" + this.mStylusHandwritingSettingsActivityAttr);
        pw.println(prefix + "mIsDefaultResId=0x" + Integer.toHexString(this.mIsDefaultResId));
        pw.println(prefix + "Service:");
        this.mService.dump(pw, prefix + "  ");
    }

    public String toString() {
        return "InputMethodInfo{" + this.mId + ", settings: " + this.mSettingsActivityName + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof InputMethodInfo)) {
            return false;
        }
        InputMethodInfo obj = (InputMethodInfo) o;
        return this.mId.equals(obj.mId);
    }

    public int hashCode() {
        return this.mId.hashCode();
    }

    public boolean isSystem() {
        return (this.mService.serviceInfo.applicationInfo.flags & 1) != 0;
    }

    public boolean isAuxiliaryIme() {
        return this.mIsAuxIme;
    }

    public boolean supportsSwitchingToNextInputMethod() {
        return this.mSupportsSwitchingToNextInputMethod;
    }

    public boolean isInlineSuggestionsEnabled() {
        return this.mInlineSuggestionsEnabled;
    }

    public boolean supportsInlineSuggestionsWithTouchExploration() {
        return this.mSupportsInlineSuggestionsWithTouchExploration;
    }

    public boolean suppressesSpellChecker() {
        return this.mSuppressesSpellChecker;
    }

    public boolean shouldShowInInputMethodPicker() {
        return this.mShowInInputMethodPicker;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mSettingsActivityName);
        dest.writeInt(this.mIsDefaultResId);
        dest.writeInt(this.mIsAuxIme ? 1 : 0);
        dest.writeInt(this.mSupportsSwitchingToNextInputMethod ? 1 : 0);
        dest.writeInt(this.mInlineSuggestionsEnabled ? 1 : 0);
        dest.writeInt(this.mSupportsInlineSuggestionsWithTouchExploration ? 1 : 0);
        dest.writeBoolean(this.mSuppressesSpellChecker);
        dest.writeBoolean(this.mShowInInputMethodPicker);
        dest.writeBoolean(this.mIsVrOnly);
        this.mService.writeToParcel(dest, flags);
        this.mSubtypes.writeToParcel(dest);
        dest.writeInt(this.mHandledConfigChanges);
        dest.writeBoolean(this.mSupportsStylusHandwriting);
        dest.writeString8(this.mStylusHandwritingSettingsActivityAttr);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
