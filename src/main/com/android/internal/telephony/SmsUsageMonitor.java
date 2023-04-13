package com.android.internal.telephony;

import android.app.role.RoleManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.AtomicFile;
import android.util.Xml;
import com.android.internal.telephony.util.XmlUtils;
import com.android.telephony.Rlog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SmsUsageMonitor {
    public static final int PREMIUM_SMS_PERMISSION_ALWAYS_ALLOW = 3;
    public static final int PREMIUM_SMS_PERMISSION_ASK_USER = 1;
    public static final int PREMIUM_SMS_PERMISSION_NEVER_ALLOW = 2;
    public static final int PREMIUM_SMS_PERMISSION_UNKNOWN = 0;
    private final AtomicBoolean mCheckEnabled;
    private final int mCheckPeriod;
    private final Context mContext;
    private String mCurrentCountry;
    private ShortCodePatternMatcher mCurrentPatternMatcher;
    private final int mMaxAllowed;
    private final File mPatternFile;
    private long mPatternFileLastModified;
    private int mPatternFileVersion;
    private AtomicFile mPolicyFile;
    private final HashMap<String, Integer> mPremiumSmsPolicy;
    private RoleManager mRoleManager;
    private final SettingsObserverHandler mSettingsObserverHandler;
    private final HashMap<String, ArrayList<Long>> mSmsStamp = new HashMap<>();

    public static int mergeShortCodeCategories(int i, int i2) {
        return i > i2 ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ShortCodePatternMatcher {
        private final Pattern mFreeShortCodePattern;
        private final Pattern mPremiumShortCodePattern;
        private final Pattern mShortCodePattern;
        private final Pattern mStandardShortCodePattern;

        ShortCodePatternMatcher(String str, String str2, String str3, String str4) {
            this.mShortCodePattern = str != null ? Pattern.compile(str) : null;
            this.mPremiumShortCodePattern = str2 != null ? Pattern.compile(str2) : null;
            this.mFreeShortCodePattern = str3 != null ? Pattern.compile(str3) : null;
            this.mStandardShortCodePattern = str4 != null ? Pattern.compile(str4) : null;
        }

        int getNumberCategory(String str) {
            Pattern pattern = this.mFreeShortCodePattern;
            if (pattern == null || !pattern.matcher(str).matches()) {
                Pattern pattern2 = this.mStandardShortCodePattern;
                if (pattern2 == null || !pattern2.matcher(str).matches()) {
                    Pattern pattern3 = this.mPremiumShortCodePattern;
                    if (pattern3 == null || !pattern3.matcher(str).matches()) {
                        Pattern pattern4 = this.mShortCodePattern;
                        return (pattern4 == null || !pattern4.matcher(str).matches()) ? 0 : 3;
                    }
                    return 4;
                }
                return 2;
            }
            return 1;
        }
    }

    /* loaded from: classes.dex */
    private static class SettingsObserver extends ContentObserver {
        private final Context mContext;
        private final AtomicBoolean mEnabled;

        SettingsObserver(Handler handler, Context context, AtomicBoolean atomicBoolean) {
            super(handler);
            this.mContext = context;
            this.mEnabled = atomicBoolean;
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            this.mEnabled.set(Settings.Global.getInt(this.mContext.getContentResolver(), "sms_short_code_confirmation", 1) != 0);
        }
    }

    /* loaded from: classes.dex */
    private static class SettingsObserverHandler extends Handler {
        SettingsObserverHandler(Context context, AtomicBoolean atomicBoolean) {
            context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("sms_short_code_confirmation"), false, new SettingsObserver(this, context, atomicBoolean));
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public SmsUsageMonitor(Context context) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        this.mCheckEnabled = atomicBoolean;
        this.mPatternFile = new File("/data/misc/sms/codes");
        this.mPatternFileLastModified = 0L;
        this.mPatternFileVersion = -1;
        this.mPremiumSmsPolicy = new HashMap<>();
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        this.mRoleManager = (RoleManager) context.getSystemService("role");
        this.mMaxAllowed = Settings.Global.getInt(contentResolver, "sms_outgoing_check_max_count", 30);
        this.mCheckPeriod = Settings.Global.getInt(contentResolver, "sms_outgoing_check_interval_ms", ServiceStateTracker.DEFAULT_GPRS_CHECK_PERIOD_MILLIS);
        this.mSettingsObserverHandler = new SettingsObserverHandler(context, atomicBoolean);
        loadPremiumSmsPolicyDb();
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0034, code lost:
        if (r2 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0036, code lost:
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0048, code lost:
        if (r2 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x004b, code lost:
        return null;
     */
    /* JADX WARN: Not initialized variable reg: 2, insn: 0x004d: MOVE  (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:26:0x004d */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0058 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private ShortCodePatternMatcher getPatternMatcherFromFile(String str) {
        FileReader fileReader;
        FileReader fileReader2;
        FileReader fileReader3 = null;
        try {
            try {
                fileReader = new FileReader(this.mPatternFile);
                try {
                    XmlPullParser newPullParser = Xml.newPullParser();
                    newPullParser.setInput(fileReader);
                    ShortCodePatternMatcher patternMatcherFromXmlParser = getPatternMatcherFromXmlParser(newPullParser, str);
                    this.mPatternFileLastModified = this.mPatternFile.lastModified();
                    try {
                        fileReader.close();
                    } catch (IOException unused) {
                    }
                    return patternMatcherFromXmlParser;
                } catch (FileNotFoundException unused2) {
                    Rlog.e("SmsUsageMonitor", "Short Code Pattern File not found");
                    this.mPatternFileLastModified = this.mPatternFile.lastModified();
                } catch (XmlPullParserException e) {
                    e = e;
                    Rlog.e("SmsUsageMonitor", "XML parser exception reading short code pattern file", e);
                    this.mPatternFileLastModified = this.mPatternFile.lastModified();
                }
            } catch (Throwable th) {
                th = th;
                fileReader3 = fileReader2;
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (fileReader3 != null) {
                    try {
                        fileReader3.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException unused4) {
            fileReader = null;
        } catch (XmlPullParserException e2) {
            e = e2;
            fileReader = null;
        } catch (Throwable th2) {
            th = th2;
            this.mPatternFileLastModified = this.mPatternFile.lastModified();
            if (fileReader3 != null) {
            }
            throw th;
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromResource(String str) {
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = this.mContext.getResources().getXml(18284566);
            return getPatternMatcherFromXmlParser(xmlResourceParser, str);
        } finally {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromXmlParser(XmlPullParser xmlPullParser, String str) {
        try {
            XmlUtils.beginDocument(xmlPullParser, "shortcodes");
            while (true) {
                XmlUtils.nextElement(xmlPullParser);
                String name = xmlPullParser.getName();
                if (name == null) {
                    Rlog.e("SmsUsageMonitor", "Parsing pattern data found null");
                    break;
                } else if (name.equals("shortcode")) {
                    if (str.equals(xmlPullParser.getAttributeValue(null, "country"))) {
                        return new ShortCodePatternMatcher(xmlPullParser.getAttributeValue(null, "pattern"), xmlPullParser.getAttributeValue(null, "premium"), xmlPullParser.getAttributeValue(null, "free"), xmlPullParser.getAttributeValue(null, "standard"));
                    }
                } else {
                    Rlog.e("SmsUsageMonitor", "Error: skipping unknown XML tag " + name);
                }
            }
        } catch (IOException e) {
            Rlog.e("SmsUsageMonitor", "I/O exception reading short code patterns", e);
        } catch (XmlPullParserException e2) {
            Rlog.e("SmsUsageMonitor", "XML parser exception reading short code patterns", e2);
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean check(String str, int i) {
        synchronized (this.mSmsStamp) {
            removeExpiredTimestamps();
            ArrayList<Long> arrayList = this.mSmsStamp.get(str);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mSmsStamp.put(str, arrayList);
            }
            if (this.mRoleManager.getRoleHolders("android.app.role.SMS").contains(str)) {
                return true;
            }
            return isUnderLimit(arrayList, i);
        }
    }

    public int checkDestination(String str, String str2) {
        String str3;
        synchronized (this.mSettingsObserverHandler) {
            if (((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).isEmergencyNumber(str)) {
                return 0;
            }
            if (this.mCheckEnabled.get()) {
                if (str2 != null && ((str3 = this.mCurrentCountry) == null || !str2.equals(str3) || this.mPatternFile.lastModified() != this.mPatternFileLastModified)) {
                    if (this.mPatternFile.exists()) {
                        this.mCurrentPatternMatcher = getPatternMatcherFromFile(str2);
                        this.mPatternFileVersion = getPatternFileVersionFromFile();
                    } else {
                        this.mCurrentPatternMatcher = getPatternMatcherFromResource(str2);
                        this.mPatternFileVersion = -1;
                    }
                    this.mCurrentCountry = str2;
                }
                ShortCodePatternMatcher shortCodePatternMatcher = this.mCurrentPatternMatcher;
                if (shortCodePatternMatcher != null) {
                    return shortCodePatternMatcher.getNumberCategory(str);
                }
                Rlog.e("SmsUsageMonitor", "No patterns for \"" + str2 + "\": using generic short code rule");
                return str.length() <= 5 ? 3 : 0;
            }
            return 0;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x00c6, code lost:
        if (r1 == null) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00df, code lost:
        if (r1 == null) goto L43;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void loadPremiumSmsPolicyDb() {
        FileInputStream openRead;
        synchronized (this.mPremiumSmsPolicy) {
            if (this.mPolicyFile == null) {
                this.mPolicyFile = new AtomicFile(new File(new File("/data/misc/sms"), "premium_sms_policy.xml"));
                this.mPremiumSmsPolicy.clear();
                FileInputStream fileInputStream = null;
                try {
                    try {
                        try {
                            openRead = this.mPolicyFile.openRead();
                        } catch (IOException unused) {
                        }
                        try {
                            try {
                                XmlPullParser newPullParser = Xml.newPullParser();
                                newPullParser.setInput(openRead, StandardCharsets.UTF_8.name());
                                XmlUtils.beginDocument(newPullParser, "premium-sms-policy");
                                while (true) {
                                    XmlUtils.nextElement(newPullParser);
                                    String name = newPullParser.getName();
                                    if (name == null) {
                                        break;
                                    } else if (name.equals("package")) {
                                        String attributeValue = newPullParser.getAttributeValue(null, "name");
                                        String attributeValue2 = newPullParser.getAttributeValue(null, "sms-policy");
                                        if (attributeValue == null) {
                                            Rlog.e("SmsUsageMonitor", "Error: missing package name attribute");
                                        } else if (attributeValue2 == null) {
                                            Rlog.e("SmsUsageMonitor", "Error: missing package policy attribute");
                                        } else {
                                            try {
                                                this.mPremiumSmsPolicy.put(attributeValue, Integer.valueOf(Integer.parseInt(attributeValue2)));
                                            } catch (NumberFormatException unused2) {
                                                Rlog.e("SmsUsageMonitor", "Error: non-numeric policy type " + attributeValue2);
                                            }
                                        }
                                    } else {
                                        Rlog.e("SmsUsageMonitor", "Error: skipping unknown XML tag " + name);
                                    }
                                }
                                if (openRead != null) {
                                    openRead.close();
                                }
                            } catch (NumberFormatException e) {
                                e = e;
                                fileInputStream = openRead;
                                Rlog.e("SmsUsageMonitor", "Unable to parse premium SMS policy database", e);
                                if (fileInputStream != null) {
                                    fileInputStream.close();
                                }
                            }
                        } catch (FileNotFoundException unused3) {
                            fileInputStream = openRead;
                            if (fileInputStream != null) {
                                fileInputStream.close();
                            }
                        } catch (IOException e2) {
                            e = e2;
                            fileInputStream = openRead;
                            Rlog.e("SmsUsageMonitor", "Unable to read premium SMS policy database", e);
                        } catch (XmlPullParserException e3) {
                            e = e3;
                            fileInputStream = openRead;
                            Rlog.e("SmsUsageMonitor", "Unable to parse premium SMS policy database", e);
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = openRead;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException unused4) {
                                }
                            }
                            throw th;
                        }
                    } catch (FileNotFoundException unused5) {
                    } catch (IOException e4) {
                        e = e4;
                    } catch (NumberFormatException e5) {
                        e = e5;
                    } catch (XmlPullParserException e6) {
                        e = e6;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writePremiumSmsPolicyDb() {
        FileOutputStream fileOutputStream;
        IOException e;
        synchronized (this.mPremiumSmsPolicy) {
            try {
                fileOutputStream = this.mPolicyFile.startWrite();
            } catch (IOException e2) {
                fileOutputStream = null;
                e = e2;
            }
            try {
                FastXmlSerializer fastXmlSerializer = new FastXmlSerializer();
                fastXmlSerializer.setOutput(fileOutputStream, StandardCharsets.UTF_8.name());
                fastXmlSerializer.startDocument(null, Boolean.TRUE);
                fastXmlSerializer.startTag(null, "premium-sms-policy");
                for (Map.Entry<String, Integer> entry : this.mPremiumSmsPolicy.entrySet()) {
                    fastXmlSerializer.startTag(null, "package");
                    fastXmlSerializer.attribute(null, "name", entry.getKey());
                    fastXmlSerializer.attribute(null, "sms-policy", entry.getValue().toString());
                    fastXmlSerializer.endTag(null, "package");
                }
                fastXmlSerializer.endTag(null, "premium-sms-policy");
                fastXmlSerializer.endDocument();
                this.mPolicyFile.finishWrite(fileOutputStream);
            } catch (IOException e3) {
                e = e3;
                Rlog.e("SmsUsageMonitor", "Unable to write premium SMS policy database", e);
                if (fileOutputStream != null) {
                    this.mPolicyFile.failWrite(fileOutputStream);
                }
            }
        }
    }

    public int getPremiumSmsPermission(String str) {
        checkCallerIsSystemOrPhoneOrSameApp(str);
        synchronized (this.mPremiumSmsPolicy) {
            Integer num = this.mPremiumSmsPolicy.get(str);
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }
    }

    public void setPremiumSmsPermission(String str, int i) {
        checkCallerIsSystemOrPhoneApp();
        if (i < 1 || i > 3) {
            throw new IllegalArgumentException("invalid SMS permission type " + i);
        }
        synchronized (this.mPremiumSmsPolicy) {
            this.mPremiumSmsPolicy.put(str, Integer.valueOf(i));
        }
        new Thread(new Runnable() { // from class: com.android.internal.telephony.SmsUsageMonitor.1
            @Override // java.lang.Runnable
            public void run() {
                SmsUsageMonitor.this.writePremiumSmsPolicyDb();
            }
        }).start();
    }

    private void checkCallerIsSystemOrPhoneOrSameApp(String str) {
        int callingUid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(callingUid);
        if (appId == 1000 || appId == 1001 || callingUid == 0) {
            return;
        }
        String str2 = "Calling uid " + callingUid + " gave package " + str + " which is either unknown or owned by another uid";
        try {
            if (UserHandle.getAppId(this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserHandleForUid(callingUid)).uid) == UserHandle.getAppId(callingUid)) {
                return;
            }
            throw new SecurityException(str2);
        } catch (PackageManager.NameNotFoundException unused) {
            throw new SecurityException(str2);
        }
    }

    private static void checkCallerIsSystemOrPhoneApp() {
        int callingUid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(callingUid);
        if (appId == 1000 || appId == 1001 || callingUid == 0) {
            return;
        }
        throw new SecurityException("Disallowed call for uid " + callingUid);
    }

    private void removeExpiredTimestamps() {
        long currentTimeMillis = System.currentTimeMillis() - this.mCheckPeriod;
        synchronized (this.mSmsStamp) {
            Iterator<Map.Entry<String, ArrayList<Long>>> it = this.mSmsStamp.entrySet().iterator();
            while (it.hasNext()) {
                ArrayList<Long> value = it.next().getValue();
                if (value.isEmpty() || value.get(value.size() - 1).longValue() < currentTimeMillis) {
                    it.remove();
                }
            }
        }
    }

    private boolean isUnderLimit(ArrayList<Long> arrayList, int i) {
        int i2;
        Long valueOf = Long.valueOf(System.currentTimeMillis());
        long longValue = valueOf.longValue() - this.mCheckPeriod;
        while (true) {
            if (arrayList.isEmpty() || arrayList.get(0).longValue() >= longValue) {
                break;
            }
            arrayList.remove(0);
        }
        if (arrayList.size() + i <= this.mMaxAllowed) {
            for (i2 = 0; i2 < i; i2++) {
                arrayList.add(valueOf);
            }
            return true;
        }
        return false;
    }

    private int getPatternFileVersionFromFile() {
        BufferedReader bufferedReader;
        String readLine;
        File file = new File("/data/misc/sms/metadata/version");
        if (file.exists()) {
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    try {
                        bufferedReader = new BufferedReader(new FileReader(file));
                    } catch (IOException e) {
                        Rlog.e("SmsUsageMonitor", "File reader exception closing short code pattern file version reader", e);
                    }
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                readLine = bufferedReader.readLine();
            } catch (IOException e3) {
                e = e3;
                bufferedReader2 = bufferedReader;
                Rlog.e("SmsUsageMonitor", "File reader exception reading short code pattern file version", e);
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
                return -1;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader2 = bufferedReader;
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException e4) {
                        Rlog.e("SmsUsageMonitor", "File reader exception closing short code pattern file version reader", e4);
                    }
                }
                throw th;
            }
            if (readLine == null) {
                bufferedReader.close();
                return -1;
            }
            int parseInt = Integer.parseInt(readLine);
            try {
                bufferedReader.close();
            } catch (IOException e5) {
                Rlog.e("SmsUsageMonitor", "File reader exception closing short code pattern file version reader", e5);
            }
            return parseInt;
        }
        return -1;
    }

    public int getShortCodeXmlFileVersion() {
        return this.mPatternFileVersion;
    }
}
