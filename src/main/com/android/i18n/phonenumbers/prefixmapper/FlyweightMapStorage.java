package com.android.i18n.phonenumbers.prefixmapper;

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
    private static final int INT_NUM_BYTES = 4;
    private static final int SHORT_NUM_BYTES = 2;
    private int descIndexSizeInBytes;
    private ByteBuffer descriptionIndexes;
    private String[] descriptionPool;
    private ByteBuffer phoneNumberPrefixes;
    private int prefixSizeInBytes;

    @Override // com.android.i18n.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public int getPrefix(int index) {
        return readWordFromBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, index);
    }

    @Override // com.android.i18n.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public String getDescription(int index) {
        int indexInDescriptionPool = readWordFromBuffer(this.descriptionIndexes, this.descIndexSizeInBytes, index);
        return this.descriptionPool[indexInDescriptionPool];
    }

    @Override // com.android.i18n.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void readFromSortedMap(SortedMap<Integer, String> phonePrefixMap) {
        SortedSet<String> descriptionsSet = new TreeSet<>();
        this.numOfEntries = phonePrefixMap.size();
        this.prefixSizeInBytes = getOptimalNumberOfBytesForValue(phonePrefixMap.lastKey().intValue());
        this.phoneNumberPrefixes = ByteBuffer.allocate(this.numOfEntries * this.prefixSizeInBytes);
        int index = 0;
        for (Map.Entry<Integer, String> entry : phonePrefixMap.entrySet()) {
            int prefix = entry.getKey().intValue();
            storeWordInBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, index, prefix);
            this.possibleLengths.add(Integer.valueOf(((int) Math.log10(prefix)) + 1));
            descriptionsSet.add(entry.getValue());
            index++;
        }
        createDescriptionPool(descriptionsSet, phonePrefixMap);
    }

    private void createDescriptionPool(SortedSet<String> descriptionsSet, SortedMap<Integer, String> phonePrefixMap) {
        this.descIndexSizeInBytes = getOptimalNumberOfBytesForValue(descriptionsSet.size() - 1);
        this.descriptionIndexes = ByteBuffer.allocate(this.numOfEntries * this.descIndexSizeInBytes);
        String[] strArr = new String[descriptionsSet.size()];
        this.descriptionPool = strArr;
        descriptionsSet.toArray(strArr);
        int index = 0;
        for (int i = 0; i < this.numOfEntries; i++) {
            int prefix = readWordFromBuffer(this.phoneNumberPrefixes, this.prefixSizeInBytes, i);
            String description = phonePrefixMap.get(Integer.valueOf(prefix));
            int positionInDescriptionPool = Arrays.binarySearch(this.descriptionPool, description);
            storeWordInBuffer(this.descriptionIndexes, this.descIndexSizeInBytes, index, positionInDescriptionPool);
            index++;
        }
    }

    @Override // com.android.i18n.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void readExternal(ObjectInput objectInput) throws IOException {
        this.prefixSizeInBytes = objectInput.readInt();
        this.descIndexSizeInBytes = objectInput.readInt();
        int sizeOfLengths = objectInput.readInt();
        this.possibleLengths.clear();
        for (int i = 0; i < sizeOfLengths; i++) {
            this.possibleLengths.add(Integer.valueOf(objectInput.readInt()));
        }
        int descriptionPoolSize = objectInput.readInt();
        String[] strArr = this.descriptionPool;
        if (strArr == null || strArr.length < descriptionPoolSize) {
            this.descriptionPool = new String[descriptionPoolSize];
        }
        for (int i2 = 0; i2 < descriptionPoolSize; i2++) {
            String description = objectInput.readUTF();
            this.descriptionPool[i2] = description;
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

    @Override // com.android.i18n.phonenumbers.prefixmapper.PhonePrefixMapStorageStrategy
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        String[] strArr;
        objectOutput.writeInt(this.prefixSizeInBytes);
        objectOutput.writeInt(this.descIndexSizeInBytes);
        int sizeOfLengths = this.possibleLengths.size();
        objectOutput.writeInt(sizeOfLengths);
        Iterator<Integer> it = this.possibleLengths.iterator();
        while (it.hasNext()) {
            Integer length = it.next();
            objectOutput.writeInt(length.intValue());
        }
        objectOutput.writeInt(this.descriptionPool.length);
        for (String description : this.descriptionPool) {
            objectOutput.writeUTF(description);
        }
        objectOutput.writeInt(this.numOfEntries);
        for (int i = 0; i < this.numOfEntries; i++) {
            writeExternalWord(objectOutput, this.prefixSizeInBytes, this.phoneNumberPrefixes, i);
            writeExternalWord(objectOutput, this.descIndexSizeInBytes, this.descriptionIndexes, i);
        }
    }

    private static int getOptimalNumberOfBytesForValue(int value) {
        return value <= 32767 ? 2 : 4;
    }

    private static void readExternalWord(ObjectInput objectInput, int wordSize, ByteBuffer outputBuffer, int index) throws IOException {
        int wordIndex = index * wordSize;
        if (wordSize == 2) {
            outputBuffer.putShort(wordIndex, objectInput.readShort());
        } else {
            outputBuffer.putInt(wordIndex, objectInput.readInt());
        }
    }

    private static void writeExternalWord(ObjectOutput objectOutput, int wordSize, ByteBuffer inputBuffer, int index) throws IOException {
        int wordIndex = index * wordSize;
        if (wordSize == 2) {
            objectOutput.writeShort(inputBuffer.getShort(wordIndex));
        } else {
            objectOutput.writeInt(inputBuffer.getInt(wordIndex));
        }
    }

    private static int readWordFromBuffer(ByteBuffer buffer, int wordSize, int index) {
        int wordIndex = index * wordSize;
        return wordSize == 2 ? buffer.getShort(wordIndex) : buffer.getInt(wordIndex);
    }

    private static void storeWordInBuffer(ByteBuffer buffer, int wordSize, int index, int value) {
        int wordIndex = index * wordSize;
        if (wordSize == 2) {
            buffer.putShort(wordIndex, (short) value);
        } else {
            buffer.putInt(wordIndex, value);
        }
    }
}
