package com.android.internal.telephony;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.provider.ContactsContract;
import android.util.SparseIntArray;
import com.android.internal.C4057R;
import com.android.internal.telephony.cdma.sms.UserData;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.internal.telephony.util.XmlUtils;
import com.android.telephony.Rlog;
/* loaded from: classes3.dex */
public class Sms7BitEncodingTranslator {
    private static final String TAG = "Sms7BitEncodingTranslator";
    private static final String XML_CHARACTOR_TAG = "Character";
    private static final String XML_FROM_TAG = "from";
    private static final String XML_START_TAG = "SmsEnforce7BitTranslationTable";
    private static final String XML_TO_TAG = "to";
    private static final String XML_TRANSLATION_TYPE_TAG = "TranslationType";
    private static final boolean DBG = TelephonyUtils.IS_DEBUGGABLE;
    private static boolean mIs7BitTranslationTableLoaded = false;
    private static SparseIntArray mTranslationTable = null;
    private static SparseIntArray mTranslationTableCommon = null;
    private static SparseIntArray mTranslationTableGSM = null;
    private static SparseIntArray mTranslationTableCDMA = null;

    public static String translate(CharSequence message, boolean isCdmaFormat) {
        SparseIntArray sparseIntArray;
        SparseIntArray sparseIntArray2;
        if (message == null) {
            Rlog.m2w(TAG, "Null message can not be translated");
            return null;
        }
        int size = message.length();
        if (size <= 0) {
            return "";
        }
        ensure7BitTranslationTableLoaded();
        SparseIntArray sparseIntArray3 = mTranslationTableCommon;
        if ((sparseIntArray3 == null || sparseIntArray3.size() <= 0) && (((sparseIntArray = mTranslationTableGSM) == null || sparseIntArray.size() <= 0) && ((sparseIntArray2 = mTranslationTableCDMA) == null || sparseIntArray2.size() <= 0))) {
            return null;
        }
        char[] output = new char[size];
        for (int i = 0; i < size; i++) {
            output[i] = translateIfNeeded(message.charAt(i), isCdmaFormat);
        }
        return String.valueOf(output);
    }

    private static char translateIfNeeded(char c, boolean isCdmaFormat) {
        if (noTranslationNeeded(c, isCdmaFormat)) {
            if (DBG) {
                Rlog.m4v(TAG, "No translation needed for " + Integer.toHexString(c));
            }
            return c;
        }
        int translation = -1;
        ensure7BitTranslationTableLoaded();
        SparseIntArray sparseIntArray = mTranslationTableCommon;
        if (sparseIntArray != null) {
            translation = sparseIntArray.get(c, -1);
        }
        if (translation == -1) {
            if (isCdmaFormat) {
                SparseIntArray sparseIntArray2 = mTranslationTableCDMA;
                if (sparseIntArray2 != null) {
                    translation = sparseIntArray2.get(c, -1);
                }
            } else {
                SparseIntArray sparseIntArray3 = mTranslationTableGSM;
                if (sparseIntArray3 != null) {
                    translation = sparseIntArray3.get(c, -1);
                }
            }
        }
        if (translation != -1) {
            if (DBG) {
                Rlog.m4v(TAG, Integer.toHexString(c) + " (" + c + ") translated to " + Integer.toHexString(translation) + " (" + ((char) translation) + NavigationBarInflaterView.KEY_CODE_END);
            }
            return (char) translation;
        } else if (DBG) {
            Rlog.m2w(TAG, "No translation found for " + Integer.toHexString(c) + "! Replacing for empty space");
            return ' ';
        } else {
            return ' ';
        }
    }

    private static boolean noTranslationNeeded(char c, boolean isCdmaFormat) {
        if (isCdmaFormat) {
            return GsmAlphabet.isGsmSeptets(c) && UserData.charToAscii.get(c, -1) != -1;
        }
        return GsmAlphabet.isGsmSeptets(c);
    }

    private static void ensure7BitTranslationTableLoaded() {
        synchronized (Sms7BitEncodingTranslator.class) {
            if (!mIs7BitTranslationTableLoaded) {
                mTranslationTableCommon = new SparseIntArray();
                mTranslationTableGSM = new SparseIntArray();
                mTranslationTableCDMA = new SparseIntArray();
                load7BitTranslationTableFromXml();
                mIs7BitTranslationTableLoaded = true;
            }
        }
    }

    private static void load7BitTranslationTableFromXml() {
        boolean z;
        XmlResourceParser parser = null;
        Resources r = Resources.getSystem();
        if (0 == 0) {
            if (DBG) {
                Rlog.m10d(TAG, "load7BitTranslationTableFromXml: open normal file");
            }
            parser = r.getXml(C4057R.xml.sms_7bit_translation_table);
        }
        try {
            try {
                XmlUtils.beginDocument(parser, XML_START_TAG);
                while (true) {
                    XmlUtils.nextElement(parser);
                    String tag = parser.getName();
                    z = DBG;
                    if (z) {
                        Rlog.m10d(TAG, "tag: " + tag);
                    }
                    if (XML_TRANSLATION_TYPE_TAG.equals(tag)) {
                        String type = parser.getAttributeValue(null, "Type");
                        if (z) {
                            Rlog.m10d(TAG, "type: " + type);
                        }
                        if (type.equals(ContactsContract.CommonDataKinds.PACKAGE_COMMON)) {
                            mTranslationTable = mTranslationTableCommon;
                        } else if (type.equals("gsm")) {
                            mTranslationTable = mTranslationTableGSM;
                        } else if (!type.equals("cdma")) {
                            Rlog.m8e(TAG, "Error Parsing 7BitTranslationTable: found incorrect type" + type);
                        } else {
                            mTranslationTable = mTranslationTableCDMA;
                        }
                    } else if (!XML_CHARACTOR_TAG.equals(tag) || mTranslationTable == null) {
                        break;
                    } else {
                        int from = parser.getAttributeUnsignedIntValue(null, XML_FROM_TAG, -1);
                        int to = parser.getAttributeUnsignedIntValue(null, XML_TO_TAG, -1);
                        if (from == -1 || to == -1) {
                            Rlog.m10d(TAG, "Invalid translation table file format");
                        } else {
                            if (z) {
                                Rlog.m10d(TAG, "Loading mapping " + Integer.toHexString(from).toUpperCase() + " -> " + Integer.toHexString(to).toUpperCase());
                            }
                            mTranslationTable.put(from, to);
                        }
                    }
                }
                if (z) {
                    Rlog.m10d(TAG, "load7BitTranslationTableFromXml: parsing successful, file loaded");
                }
                if (!(parser instanceof XmlResourceParser)) {
                    return;
                }
            } catch (Exception e) {
                Rlog.m7e(TAG, "Got exception while loading 7BitTranslationTable file.", e);
                if (!(parser instanceof XmlResourceParser)) {
                    return;
                }
            }
            parser.close();
        } catch (Throwable th) {
            if (parser instanceof XmlResourceParser) {
                parser.close();
            }
            throw th;
        }
    }
}
