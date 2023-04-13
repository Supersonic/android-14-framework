package com.android.server.display.config;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class IntegerArray {
    public List<BigInteger> item;

    public List<BigInteger> getItem() {
        if (this.item == null) {
            this.item = new ArrayList();
        }
        return this.item;
    }

    public static IntegerArray read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        IntegerArray integerArray = new IntegerArray();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("item")) {
                    integerArray.getItem().add(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return integerArray;
        }
        throw new DatatypeConfigurationException("IntegerArray is not closed");
    }
}
