package com.android.internal.telephony.phonenumbers.prefixmapper;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
/* loaded from: classes.dex */
final class FlyweightMapStorage extends PhonePrefixMapStorageStrategy {
    private int descIndexSizeInBytes;
    private ByteBuffer descriptionIndexes;
    private String[] descriptionPool;
    private ByteBuffer phoneNumberPrefixes;
    private int prefixSizeInBytes;

    private static int getOptimalNumberOfBytesForValue(int i) {
        return i <= 32767 ? 2 : 4;
    }

    @Override // com.android.internal.telephony.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public int getPrefix(int i) {
        return readWordFromBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, i);
    }

    @Override // com.android.internal.telephony.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public String getDescription(int i) {
        return this.descriptionPool[readWordFromBuffer(this.descriptionIndexes, this.descIndexSizeInBytes, i)];
    }

    @Override // com.android.internal.telephony.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void readFromSortedMap(SortedMap<Integer, String> sortedMap) {
        TreeSet treeSet = new TreeSet();
        this.numOfEntries = sortedMap.size();
        int optimalNumberOfBytesForValue = getOptimalNumberOfBytesForValue(sortedMap.lastKey().intValue());
        this.prefixSizeInBytes = optimalNumberOfBytesForValue;
        this.phoneNumberPrefixes = ByteBuffer.allocate(this.numOfEntries * optimalNumberOfBytesForValue);
        int i = 0;
        for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {
            int intValue = entry.getKey().intValue();
            storeWordInBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, i, intValue);
            this.possibleLengths.add(Integer.valueOf(((int) Math.log10(intValue)) + 1));
            treeSet.add(entry.getValue());
            i++;
        }
        createDescriptionPool(treeSet, sortedMap);
    }

    private void createDescriptionPool(SortedSet<String> sortedSet, SortedMap<Integer, String> sortedMap) {
        int optimalNumberOfBytesForValue = getOptimalNumberOfBytesForValue(sortedSet.size() - 1);
        this.descIndexSizeInBytes = optimalNumberOfBytesForValue;
        this.descriptionIndexes = ByteBuffer.allocate(this.numOfEntries * optimalNumberOfBytesForValue);
        String[] strArr = new String[sortedSet.size()];
        this.descriptionPool = strArr;
        sortedSet.toArray(strArr);
        int i = 0;
        for (int i2 = 0; i2 < this.numOfEntries; i2++) {
            storeWordInBuffer(this.descriptionIndexes, this.descIndexSizeInBytes, i, Arrays.binarySearch(this.descriptionPool, sortedMap.get(Integer.valueOf(readWordFromBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, i2)))));
            i++;
        }
    }

    @Override // com.android.internal.telephony.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void readExternal(ObjectInput objectInput) throws IOException {
        this.prefixSizeInBytes = objectInput.readInt();
        this.descIndexSizeInBytes = objectInput.readInt();
        int readInt = objectInput.readInt();
        this.possibleLengths.clear();
        for (int i = 0; i < readInt; i++) {
            this.possibleLengths.add(Integer.valueOf(objectInput.readInt()));
        }
        int readInt2 = objectInput.readInt();
        String[] strArr = this.descriptionPool;
        if (strArr == null || strArr.length < readInt2) {
            this.descriptionPool = new String[readInt2];
        }
        for (int i2 = 0; i2 < readInt2; i2++) {
            this.descriptionPool[i2] = objectInput.readUTF();
        }
        readEntries(objectInput);
    }

    private void readEntries(ObjectInput objectInput) throws IOException {
        this.numOfEntries = objectInput.readInt();
        ByteBuffer byteBuffer = this.phoneNumberPrefixes;
        if (byteBuffer == null || byteBuffer.capacity() < this.numOfEntries) {
            this.phoneNumberPrefixes = ByteBuffer.allocate(this.numOfEntries * this.prefixSizeInBytes);
        }
        ByteBuffer byteBuffer2 = this.descriptionIndexes;
        if (byteBuffer2 == null || byteBuffer2.capacity() < this.numOfEntries) {
            this.descriptionIndexes = ByteBuffer.allocate(this.numOfEntries * this.descIndexSizeInBytes);
        }
        for (int i = 0; i < this.numOfEntries; i++) {
            readExternalWord(objectInput, this.prefixSizeInBytes, this.phoneNumberPrefixes, i);
            readExternalWord(objectInput, this.descIndexSizeInBytes, this.descriptionIndexes, i);
        }
    }

    @Override // com.android.internal.telephony.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.prefixSizeInBytes);
        objectOutput.writeInt(this.descIndexSizeInBytes);
        objectOutput.writeInt(this.possibleLengths.size());
        Iterator<Integer> it = this.possibleLengths.iterator();
        while (it.hasNext()) {
            objectOutput.writeInt(it.next().intValue());
        }
        objectOutput.writeInt(this.descriptionPool.length);
        for (String str : this.descriptionPool) {
            objectOutput.writeUTF(str);
        }
        objectOutput.writeInt(this.numOfEntries);
        for (int i = 0; i < this.numOfEntries; i++) {
            writeExternalWord(objectOutput, this.prefixSizeInBytes, this.phoneNumberPrefixes, i);
            writeExternalWord(objectOutput, this.descIndexSizeInBytes, this.descriptionIndexes, i);
        }
    }

    private static void readExternalWord(ObjectInput objectInput, int i, ByteBuffer byteBuffer, int i2) throws IOException {
        int i3 = i2 * i;
        if (i == 2) {
            byteBuffer.putShort(i3, objectInput.readShort());
        } else {
            byteBuffer.putInt(i3, objectInput.readInt());
        }
    }

    private static void writeExternalWord(ObjectOutput objectOutput, int i, ByteBuffer byteBuffer, int i2) throws IOException {
        int i3 = i2 * i;
        if (i == 2) {
            objectOutput.writeShort(byteBuffer.getShort(i3));
        } else {
            objectOutput.writeInt(byteBuffer.getInt(i3));
        }
    }

    private static int readWordFromBuffer(ByteBuffer byteBuffer, int i, int i2) {
        int i3 = i2 * i;
        return i == 2 ? byteBuffer.getShort(i3) : byteBuffer.getInt(i3);
    }

    private static void storeWordInBuffer(ByteBuffer byteBuffer, int i, int i2, int i3) {
        int i4 = i2 * i;
        if (i == 2) {
            byteBuffer.putShort(i4, (short) i3);
        } else {
            byteBuffer.putInt(i4, i3);
        }
    }
}
