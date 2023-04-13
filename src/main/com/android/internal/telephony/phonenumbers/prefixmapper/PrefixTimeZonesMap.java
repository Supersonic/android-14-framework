package com.android.internal.telephony.phonenumbers.prefixmapper;

import com.android.internal.telephony.phonenumbers.PhoneNumberUtil;
import com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
/* loaded from: classes.dex */
public class PrefixTimeZonesMap implements Externalizable {
    private final PhonePrefixMap phonePrefixMap = new PhonePrefixMap();

    public void readPrefixTimeZonesMap(SortedMap<Integer, String> sortedMap) {
        this.phonePrefixMap.readPhonePrefixMap(sortedMap);
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        this.phonePrefixMap.writeExternal(objectOutput);
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput objectInput) throws IOException {
        this.phonePrefixMap.readExternal(objectInput);
    }

    private List<String> lookupTimeZonesForNumber(long j) {
        String lookup = this.phonePrefixMap.lookup(j);
        if (lookup == null) {
            return new LinkedList();
        }
        return tokenizeRawOutputString(lookup);
    }

    public List<String> lookupTimeZonesForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return lookupTimeZonesForNumber(Long.parseLong(phonenumber$PhoneNumber.getCountryCode() + PhoneNumberUtil.getInstance().getNationalSignificantNumber(phonenumber$PhoneNumber)));
    }

    public List<String> lookupCountryLevelTimeZonesForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return lookupTimeZonesForNumber(phonenumber$PhoneNumber.getCountryCode());
    }

    private List<String> tokenizeRawOutputString(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
        LinkedList linkedList = new LinkedList();
        while (stringTokenizer.hasMoreTokens()) {
            linkedList.add(stringTokenizer.nextToken());
        }
        return linkedList;
    }

    public String toString() {
        return this.phonePrefixMap.toString();
    }
}
