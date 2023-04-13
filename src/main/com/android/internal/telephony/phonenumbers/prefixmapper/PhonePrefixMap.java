package com.android.internal.telephony.phonenumbers.prefixmapper;

import com.android.internal.telephony.phonenumbers.PhoneNumberUtil;
import com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.logging.Logger;
/* loaded from: classes.dex */
public class PhonePrefixMap implements Externalizable {
    private static final Logger logger = Logger.getLogger(PhonePrefixMap.class.getName());
    private PhonePrefixMapStorageStrategy phonePrefixMapStorage;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private static int getSizeOfPhonePrefixMapStorage(PhonePrefixMapStorageStrategy phonePrefixMapStorageStrategy, SortedMap<Integer, String> sortedMap) throws IOException {
        phonePrefixMapStorageStrategy.readFromSortedMap(sortedMap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        phonePrefixMapStorageStrategy.writeExternal(objectOutputStream);
        objectOutputStream.flush();
        int size = byteArrayOutputStream.size();
        objectOutputStream.close();
        return size;
    }

    private PhonePrefixMapStorageStrategy createDefaultMapStorage() {
        return new DefaultMapStorage();
    }

    private PhonePrefixMapStorageStrategy createFlyweightMapStorage() {
        return new FlyweightMapStorage();
    }

    PhonePrefixMapStorageStrategy getSmallerMapStorage(SortedMap<Integer, String> sortedMap) {
        try {
            PhonePrefixMapStorageStrategy createFlyweightMapStorage = createFlyweightMapStorage();
            int sizeOfPhonePrefixMapStorage = getSizeOfPhonePrefixMapStorage(createFlyweightMapStorage, sortedMap);
            PhonePrefixMapStorageStrategy createDefaultMapStorage = createDefaultMapStorage();
            return sizeOfPhonePrefixMapStorage < getSizeOfPhonePrefixMapStorage(createDefaultMapStorage, sortedMap) ? createFlyweightMapStorage : createDefaultMapStorage;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return this.createFlyweightMapStorage();
        }
    }

    public void readPhonePrefixMap(SortedMap<Integer, String> sortedMap) {
        this.phonePrefixMapStorage = getSmallerMapStorage(sortedMap);
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput objectInput) throws IOException {
        if (objectInput.readBoolean()) {
            this.phonePrefixMapStorage = new FlyweightMapStorage();
        } else {
            this.phonePrefixMapStorage = new DefaultMapStorage();
        }
        this.phonePrefixMapStorage.readExternal(objectInput);
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.phonePrefixMapStorage instanceof FlyweightMapStorage);
        this.phonePrefixMapStorage.writeExternal(objectOutput);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String lookup(long j) {
        int numOfEntries = this.phonePrefixMapStorage.getNumOfEntries();
        if (numOfEntries == 0) {
            return null;
        }
        int i = numOfEntries - 1;
        SortedSet possibleLengths = this.phonePrefixMapStorage.getPossibleLengths();
        while (possibleLengths.size() > 0) {
            Integer num = (Integer) possibleLengths.last();
            String valueOf = String.valueOf(j);
            if (valueOf.length() > num.intValue()) {
                j = Long.parseLong(valueOf.substring(0, num.intValue()));
            }
            i = binarySearch(0, i, j);
            if (i < 0) {
                return null;
            }
            if (j == this.phonePrefixMapStorage.getPrefix(i)) {
                return this.phonePrefixMapStorage.getDescription(i);
            }
            possibleLengths = possibleLengths.headSet(num);
        }
        return null;
    }

    public String lookup(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return lookup(Long.parseLong(phonenumber$PhoneNumber.getCountryCode() + this.phoneUtil.getNationalSignificantNumber(phonenumber$PhoneNumber)));
    }

    private int binarySearch(int i, int i2, long j) {
        int i3 = 0;
        while (i <= i2) {
            i3 = (i + i2) >>> 1;
            int i4 = (this.phonePrefixMapStorage.getPrefix(i3) > j ? 1 : (this.phonePrefixMapStorage.getPrefix(i3) == j ? 0 : -1));
            if (i4 == 0) {
                return i3;
            }
            if (i4 > 0) {
                i3--;
                i2 = i3;
            } else {
                i = i3 + 1;
            }
        }
        return i3;
    }

    public String toString() {
        return this.phonePrefixMapStorage.toString();
    }
}
