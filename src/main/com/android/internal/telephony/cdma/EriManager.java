package com.android.internal.telephony.cdma;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.util.Xml;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.util.XmlUtils;
import com.android.telephony.Rlog;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class EriManager {
    public static final int ERI_FROM_XML = 0;
    private Context mContext;
    private EriFile mEriFile = new EriFile();
    private int mEriFileSource;
    private boolean mIsEriFileLoaded;
    private final Phone mPhone;

    private void loadEriFileFromFileSystem() {
    }

    private void loadEriFileFromModem() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class EriFile {
        int mVersionNumber = -1;
        int mNumberOfEriEntries = 0;
        int mEriFileType = -1;
        String[] mCallPromptId = {PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS};
        HashMap<Integer, EriInfo> mRoamIndTable = new HashMap<>();

        EriFile() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class EriDisplayInformation {
        int mEriIconIndex;
        int mEriIconMode;
        @UnsupportedAppUsage
        String mEriIconText;

        EriDisplayInformation(int i, int i2, String str) {
            this.mEriIconIndex = i;
            this.mEriIconMode = i2;
            this.mEriIconText = str;
        }

        public String toString() {
            return "EriDisplayInformation: { IconIndex: " + this.mEriIconIndex + " EriIconMode: " + this.mEriIconMode + " EriIconText: " + this.mEriIconText + " }";
        }
    }

    public EriManager(Phone phone, int i) {
        this.mEriFileSource = 0;
        this.mPhone = phone;
        this.mContext = phone.getContext();
        this.mEriFileSource = i;
    }

    public void dispose() {
        this.mEriFile = new EriFile();
        this.mIsEriFileLoaded = false;
    }

    public void loadEriFile() {
        int i = this.mEriFileSource;
        if (i == 1) {
            loadEriFileFromFileSystem();
        } else if (i == 2) {
            loadEriFileFromModem();
        } else {
            loadEriFileFromXml();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00ed A[Catch: all -> 0x01dd, Exception -> 0x01df, TryCatch #0 {Exception -> 0x01df, blocks: (B:26:0x00ae, B:27:0x00de, B:29:0x00e7, B:31:0x00ed, B:32:0x0112, B:39:0x0149, B:41:0x014f, B:45:0x0164, B:46:0x016c, B:47:0x0187, B:49:0x018f), top: B:71:0x00ae, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0149 A[Catch: all -> 0x01dd, Exception -> 0x01df, TRY_ENTER, TryCatch #0 {Exception -> 0x01df, blocks: (B:26:0x00ae, B:27:0x00de, B:29:0x00e7, B:31:0x00ed, B:32:0x0112, B:39:0x0149, B:41:0x014f, B:45:0x0164, B:46:0x016c, B:47:0x0187, B:49:0x018f), top: B:71:0x00ae, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01f2 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x00e7 A[EDGE_INSN: B:85:0x00e7->B:29:0x00e7 ?: BREAK  , SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void loadEriFileFromXml() {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        XmlPullParser xmlPullParser;
        int i;
        String name;
        PersistableBundle configForSubId;
        Resources resources = this.mContext.getResources();
        try {
            Rlog.d("EriManager", "loadEriFileFromXml: check for alternate file");
            fileInputStream = new FileInputStream(resources.getString(17039661));
            try {
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(fileInputStream, null);
                Rlog.d("EriManager", "loadEriFileFromXml: opened alternate file");
                fileInputStream2 = fileInputStream;
                xmlPullParser = newPullParser;
            } catch (FileNotFoundException unused) {
                Rlog.d("EriManager", "loadEriFileFromXml: no alternate file");
                fileInputStream2 = fileInputStream;
                xmlPullParser = null;
                if (xmlPullParser == null) {
                }
                try {
                    XmlUtils.beginDocument(xmlPullParser, "EriFile");
                    this.mEriFile.mVersionNumber = Integer.parseInt(xmlPullParser.getAttributeValue(null, "VersionNumber"));
                    this.mEriFile.mNumberOfEriEntries = Integer.parseInt(xmlPullParser.getAttributeValue(null, "NumberOfEriEntries"));
                    this.mEriFile.mEriFileType = Integer.parseInt(xmlPullParser.getAttributeValue(null, "EriFileType"));
                    i = 0;
                    while (true) {
                        XmlUtils.nextElement(xmlPullParser);
                        name = xmlPullParser.getName();
                        if (name == null) {
                        }
                    }
                    if (i != this.mEriFile.mNumberOfEriEntries) {
                    }
                    Rlog.d("EriManager", "loadEriFileFromXml: eri parsing successful, file loaded. ver = " + this.mEriFile.mVersionNumber + ", # of entries = " + this.mEriFile.mNumberOfEriEntries);
                    this.mIsEriFileLoaded = true;
                    if (xmlPullParser instanceof XmlResourceParser) {
                    }
                    if (fileInputStream2 == null) {
                    }
                } catch (Exception e) {
                    Rlog.e("EriManager", "Got exception while loading ERI file.", e);
                    if (xmlPullParser instanceof XmlResourceParser) {
                        ((XmlResourceParser) xmlPullParser).close();
                    }
                    if (fileInputStream2 == null) {
                        return;
                    }
                }
                try {
                    fileInputStream2.close();
                } catch (IOException unused2) {
                    return;
                }
            } catch (XmlPullParserException unused3) {
                Rlog.d("EriManager", "loadEriFileFromXml: no parser for alternate file");
                fileInputStream2 = fileInputStream;
                xmlPullParser = null;
                if (xmlPullParser == null) {
                }
                XmlUtils.beginDocument(xmlPullParser, "EriFile");
                this.mEriFile.mVersionNumber = Integer.parseInt(xmlPullParser.getAttributeValue(null, "VersionNumber"));
                this.mEriFile.mNumberOfEriEntries = Integer.parseInt(xmlPullParser.getAttributeValue(null, "NumberOfEriEntries"));
                this.mEriFile.mEriFileType = Integer.parseInt(xmlPullParser.getAttributeValue(null, "EriFileType"));
                i = 0;
                while (true) {
                    XmlUtils.nextElement(xmlPullParser);
                    name = xmlPullParser.getName();
                    if (name == null) {
                    }
                }
                if (i != this.mEriFile.mNumberOfEriEntries) {
                }
                Rlog.d("EriManager", "loadEriFileFromXml: eri parsing successful, file loaded. ver = " + this.mEriFile.mVersionNumber + ", # of entries = " + this.mEriFile.mNumberOfEriEntries);
                this.mIsEriFileLoaded = true;
                if (xmlPullParser instanceof XmlResourceParser) {
                }
                if (fileInputStream2 == null) {
                }
                fileInputStream2.close();
            }
        } catch (FileNotFoundException unused4) {
            fileInputStream = null;
        } catch (XmlPullParserException unused5) {
            fileInputStream = null;
        }
        if (xmlPullParser == null) {
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
            String string = (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId())) == null) ? null : configForSubId.getString("carrier_eri_file_name_string");
            Rlog.d("EriManager", "eriFile = " + string);
            if (string == null) {
                Rlog.e("EriManager", "loadEriFileFromXml: Can't find ERI file to load");
                return;
            }
            try {
                xmlPullParser = Xml.newPullParser();
                xmlPullParser.setInput(this.mContext.getAssets().open(string), null);
            } catch (IOException | XmlPullParserException e2) {
                Rlog.e("EriManager", "loadEriFileFromXml: no parser for " + string + ". Exception = " + e2.toString());
            }
        }
        try {
            XmlUtils.beginDocument(xmlPullParser, "EriFile");
            this.mEriFile.mVersionNumber = Integer.parseInt(xmlPullParser.getAttributeValue(null, "VersionNumber"));
            this.mEriFile.mNumberOfEriEntries = Integer.parseInt(xmlPullParser.getAttributeValue(null, "NumberOfEriEntries"));
            this.mEriFile.mEriFileType = Integer.parseInt(xmlPullParser.getAttributeValue(null, "EriFileType"));
            i = 0;
            while (true) {
                XmlUtils.nextElement(xmlPullParser);
                name = xmlPullParser.getName();
                if (name == null) {
                    break;
                } else if (name.equals("CallPromptId")) {
                    int parseInt = Integer.parseInt(xmlPullParser.getAttributeValue(null, "Id"));
                    String attributeValue = xmlPullParser.getAttributeValue(null, "CallPromptText");
                    if (parseInt >= 0 && parseInt <= 2) {
                        this.mEriFile.mCallPromptId[parseInt] = attributeValue;
                    } else {
                        Rlog.e("EriManager", "Error Parsing ERI file: found" + parseInt + " CallPromptId");
                    }
                } else if (name.equals("EriInfo")) {
                    int parseInt2 = Integer.parseInt(xmlPullParser.getAttributeValue(null, "RoamingIndicator"));
                    i++;
                    this.mEriFile.mRoamIndTable.put(Integer.valueOf(parseInt2), new EriInfo(parseInt2, Integer.parseInt(xmlPullParser.getAttributeValue(null, "IconIndex")), Integer.parseInt(xmlPullParser.getAttributeValue(null, "IconMode")), xmlPullParser.getAttributeValue(null, "EriText"), Integer.parseInt(xmlPullParser.getAttributeValue(null, "CallPromptId")), Integer.parseInt(xmlPullParser.getAttributeValue(null, "AlertId"))));
                }
            }
            if (i != this.mEriFile.mNumberOfEriEntries) {
                Rlog.e("EriManager", "Error Parsing ERI file: " + this.mEriFile.mNumberOfEriEntries + " defined, " + i + " parsed!");
            }
            Rlog.d("EriManager", "loadEriFileFromXml: eri parsing successful, file loaded. ver = " + this.mEriFile.mVersionNumber + ", # of entries = " + this.mEriFile.mNumberOfEriEntries);
            this.mIsEriFileLoaded = true;
            if (xmlPullParser instanceof XmlResourceParser) {
                ((XmlResourceParser) xmlPullParser).close();
            }
            if (fileInputStream2 == null) {
                return;
            }
            fileInputStream2.close();
        } catch (Throwable th) {
            if (xmlPullParser instanceof XmlResourceParser) {
                ((XmlResourceParser) xmlPullParser).close();
            }
            if (fileInputStream2 != null) {
                try {
                    fileInputStream2.close();
                } catch (IOException unused6) {
                }
            }
            throw th;
        }
    }

    public int getEriFileVersion() {
        return this.mEriFile.mVersionNumber;
    }

    public int getEriNumberOfEntries() {
        return this.mEriFile.mNumberOfEriEntries;
    }

    public int getEriFileType() {
        return this.mEriFile.mEriFileType;
    }

    public boolean isEriFileLoaded() {
        return this.mIsEriFileLoaded;
    }

    private EriInfo getEriInfo(int i) {
        if (this.mEriFile.mRoamIndTable.containsKey(Integer.valueOf(i))) {
            return this.mEriFile.mRoamIndTable.get(Integer.valueOf(i));
        }
        return null;
    }

    @UnsupportedAppUsage
    private EriDisplayInformation getEriDisplayInformation(int i, int i2) {
        EriInfo eriInfo;
        if (this.mIsEriFileLoaded && (eriInfo = getEriInfo(i)) != null) {
            return new EriDisplayInformation(eriInfo.iconIndex, eriInfo.iconMode, eriInfo.eriText);
        }
        switch (i) {
            case 0:
                return new EriDisplayInformation(0, 0, this.mContext.getText(17041448).toString());
            case 1:
                return new EriDisplayInformation(1, 0, this.mContext.getText(17041449).toString());
            case 2:
                return new EriDisplayInformation(2, 1, this.mContext.getText(17041453).toString());
            case 3:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041454).toString());
            case 4:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041455).toString());
            case 5:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041456).toString());
            case 6:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041457).toString());
            case 7:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041458).toString());
            case 8:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041459).toString());
            case 9:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041460).toString());
            case 10:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041450).toString());
            case 11:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041451).toString());
            case 12:
                return new EriDisplayInformation(i, 0, this.mContext.getText(17041452).toString());
            default:
                if (!this.mIsEriFileLoaded) {
                    Rlog.d("EriManager", "ERI File not loaded");
                    if (i2 > 2) {
                        return new EriDisplayInformation(2, 1, this.mContext.getText(17041453).toString());
                    }
                    if (i2 != 0) {
                        if (i2 != 1) {
                            if (i2 == 2) {
                                return new EriDisplayInformation(2, 1, this.mContext.getText(17041453).toString());
                            }
                            return new EriDisplayInformation(-1, -1, "ERI text");
                        }
                        return new EriDisplayInformation(1, 0, this.mContext.getText(17041449).toString());
                    }
                    return new EriDisplayInformation(0, 0, this.mContext.getText(17041448).toString());
                }
                EriInfo eriInfo2 = getEriInfo(i);
                EriInfo eriInfo3 = getEriInfo(i2);
                if (eriInfo2 == null) {
                    if (eriInfo3 == null) {
                        Rlog.e("EriManager", "ERI defRoamInd " + i2 + " not found in ERI file ...on");
                        return new EriDisplayInformation(0, 0, this.mContext.getText(17041448).toString());
                    }
                    return new EriDisplayInformation(eriInfo3.iconIndex, eriInfo3.iconMode, eriInfo3.eriText);
                }
                return new EriDisplayInformation(eriInfo2.iconIndex, eriInfo2.iconMode, eriInfo2.eriText);
        }
    }

    public int getCdmaEriIconIndex(int i, int i2) {
        return getEriDisplayInformation(i, i2).mEriIconIndex;
    }

    public int getCdmaEriIconMode(int i, int i2) {
        return getEriDisplayInformation(i, i2).mEriIconMode;
    }

    public String getCdmaEriText(int i, int i2) {
        return getEriDisplayInformation(i, i2).mEriIconText;
    }
}
