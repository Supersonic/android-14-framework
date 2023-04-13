package com.android.i18n.phonenumbers.prefixmapper;

import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
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

    PhonePrefixMapStorageStrategy getPhonePrefixMapStorage() {
        return this.phonePrefixMapStorage;
    }

    private static int getSizeOfPhonePrefixMapStorage(PhonePrefixMapStorageStrategy mapStorage, SortedMap<Integer, String> phonePrefixMap) throws IOException {
        mapStorage.readFromSortedMap(phonePrefixMap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        mapStorage.writeExternal(objectOutputStream);
        objectOutputStream.flush();
        int sizeOfStorage = byteArrayOutputStream.size();
        objectOutputStream.close();
        return sizeOfStorage;
    }

    private PhonePrefixMapStorageStrategy createDefaultMapStorage() {
        return new DefaultMapStorage();
    }

    private PhonePrefixMapStorageStrategy createFlyweightMapStorage() {
        return new FlyweightMapStorage();
    }

    PhonePrefixMapStorageStrategy getSmallerMapStorage(SortedMap<Integer, String> phonePrefixMap) {
        try {
            PhonePrefixMapStorageStrategy flyweightMapStorage = createFlyweightMapStorage();
            int sizeOfFlyweightMapStorage = getSizeOfPhonePrefixMapStorage(flyweightMapStorage, phonePrefixMap);
            PhonePrefixMapStorageStrategy defaultMapStorage = createDefaultMapStorage();
            int sizeOfDefaultMapStorage = getSizeOfPhonePrefixMapStorage(defaultMapStorage, phonePrefixMap);
            return sizeOfFlyweightMapStorage < sizeOfDefaultMapStorage ? flyweightMapStorage : defaultMapStorage;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return createFlyweightMapStorage();
        }
    }

    public void readPhonePrefixMap(SortedMap<Integer, String> sortedPhonePrefixMap) {
        this.phonePrefixMapStorage = getSmallerMapStorage(sortedPhonePrefixMap);
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput objectInput) throws IOException {
        boolean useFlyweightMapStorage = objectInput.readBoolean();
        if (useFlyweightMapStorage) {
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
    public String lookup(long number) {
        int numOfEntries = this.phonePrefixMapStorage.getNumOfEntries();
        if (numOfEntries == 0) {
            return null;
        }
        long phonePrefix = number;
        int currentIndex = numOfEntries - 1;
        SortedSet<Integer> currentSetOfLengths = this.phonePrefixMapStorage.getPossibleLengths();
        while (currentSetOfLengths.size() > 0) {
            Integer possibleLength = currentSetOfLengths.last();
            String phonePrefixStr = String.valueOf(phonePrefix);
            if (phonePrefixStr.length() > possibleLength.intValue()) {
                phonePrefix = Long.parseLong(phonePrefixStr.substring(0, possibleLength.intValue()));
            }
            currentIndex = binarySearch(0, currentIndex, phonePrefix);
            if (currentIndex < 0) {
                return null;
            }
            int currentPrefix = this.phonePrefixMapStorage.getPrefix(currentIndex);
            if (phonePrefix == currentPrefix) {
                return this.phonePrefixMapStorage.getDescription(currentIndex);
            }
            currentSetOfLengths = currentSetOfLengths.headSet(possibleLength);
        }
        return null;
    }

    public String lookup(Phonenumber.PhoneNumber number) {
        long phonePrefix = Long.parseLong(number.getCountryCode() + this.phoneUtil.getNationalSignificantNumber(number));
        return lookup(phonePrefix);
    }

    private int binarySearch(int start, int end, long value) {
        int current = 0;
        while (start <= end) {
            current = (start + end) >>> 1;
            int currentValue = this.phonePrefixMapStorage.getPrefix(current);
            if (currentValue == value) {
                return current;
            }
            if (currentValue > value) {
                current--;
                end = current;
            } else {
                start = current + 1;
            }
        }
        return current;
    }

    public String toString() {
        return this.phonePrefixMapStorage.toString();
    }
}
