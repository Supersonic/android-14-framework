package com.android.internal.telephony.test;

import android.os.Bundle;
import android.telephony.ims.ImsConferenceState;
import android.util.Log;
import android.util.Xml;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class TestConferenceEventPackageParser {
    private InputStream mInputStream;

    public TestConferenceEventPackageParser(InputStream inputStream) {
        this.mInputStream = inputStream;
    }

    public ImsConferenceState parse() {
        ImsConferenceState imsConferenceState = new ImsConferenceState();
        try {
            try {
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(this.mInputStream, null);
                newPullParser.nextTag();
                int depth = newPullParser.getDepth();
                while (XmlUtils.nextElementWithin(newPullParser, depth)) {
                    if (newPullParser.getName().equals("participant")) {
                        Log.v("TestConferenceEventPackageParser", "Found participant.");
                        Bundle parseParticipant = parseParticipant(newPullParser);
                        imsConferenceState.mParticipants.put(parseParticipant.getString("endpoint"), parseParticipant);
                    }
                }
                try {
                    this.mInputStream.close();
                    return imsConferenceState;
                } catch (IOException e) {
                    Log.e("TestConferenceEventPackageParser", "Failed to close test conference event package InputStream", e);
                    return null;
                }
            } catch (IOException | XmlPullParserException e2) {
                Log.e("TestConferenceEventPackageParser", "Failed to read test conference event package from XML file", e2);
                try {
                    this.mInputStream.close();
                    return null;
                } catch (IOException e3) {
                    Log.e("TestConferenceEventPackageParser", "Failed to close test conference event package InputStream", e3);
                    return null;
                }
            }
        } catch (Throwable th) {
            try {
                this.mInputStream.close();
                throw th;
            } catch (IOException e4) {
                Log.e("TestConferenceEventPackageParser", "Failed to close test conference event package InputStream", e4);
                return null;
            }
        }
    }

    private Bundle parseParticipant(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        Bundle bundle = new Bundle();
        int depth = xmlPullParser.getDepth();
        String str = PhoneConfigurationManager.SSSS;
        String str2 = PhoneConfigurationManager.SSSS;
        String str3 = str2;
        String str4 = str3;
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            if (xmlPullParser.getName().equals("user")) {
                xmlPullParser.next();
                str = xmlPullParser.getText();
            } else if (xmlPullParser.getName().equals("display-text")) {
                xmlPullParser.next();
                str2 = xmlPullParser.getText();
            } else if (xmlPullParser.getName().equals("endpoint")) {
                xmlPullParser.next();
                str3 = xmlPullParser.getText();
            } else if (xmlPullParser.getName().equals("status")) {
                xmlPullParser.next();
                str4 = xmlPullParser.getText();
            }
        }
        Log.v("TestConferenceEventPackageParser", "User: " + str);
        Log.v("TestConferenceEventPackageParser", "DisplayText: " + str2);
        Log.v("TestConferenceEventPackageParser", "Endpoint: " + str3);
        Log.v("TestConferenceEventPackageParser", "Status: " + str4);
        bundle.putString("user", str);
        bundle.putString("display-text", str2);
        bundle.putString("endpoint", str3);
        bundle.putString("status", str4);
        return bundle;
    }
}
