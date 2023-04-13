package com.google.protobuf;

import java.io.IOException;
/* JADX INFO: Access modifiers changed from: package-private */
@CheckReturnValue
/* loaded from: classes.dex */
public abstract class UnknownFieldSchema<T, B> {
    abstract void addFixed32(B b, int i, int i2);

    abstract void addFixed64(B b, int i, long j);

    abstract void addGroup(B b, int i, T t);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void addLengthDelimited(B b, int i, ByteString byteString);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void addVarint(B b, int i, long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract B getBuilderFromMessage(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract T getFromMessage(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getSerializedSize(T t);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int getSerializedSizeAsMessageSet(T t);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void makeImmutable(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract T merge(T t, T t2);

    abstract B newBuilder();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setBuilderToMessage(Object obj, B b);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setToMessage(Object obj, T t);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean shouldDiscardUnknownFields(Reader reader);

    abstract T toImmutable(B b);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeAsMessageSetTo(T t, Writer writer) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void writeTo(T t, Writer writer) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean mergeOneFieldFrom(B unknownFields, Reader reader) throws IOException {
        int tag = reader.getTag();
        int fieldNumber = WireFormat.getTagFieldNumber(tag);
        switch (WireFormat.getTagWireType(tag)) {
            case 0:
                addVarint(unknownFields, fieldNumber, reader.readInt64());
                return true;
            case 1:
                addFixed64(unknownFields, fieldNumber, reader.readFixed64());
                return true;
            case WireFormat.WIRETYPE_LENGTH_DELIMITED /* 2 */:
                addLengthDelimited(unknownFields, fieldNumber, reader.readBytes());
                return true;
            case WireFormat.WIRETYPE_START_GROUP /* 3 */:
                B subFields = newBuilder();
                int endGroupTag = WireFormat.makeTag(fieldNumber, 4);
                mergeFrom(subFields, reader);
                if (endGroupTag != reader.getTag()) {
                    throw InvalidProtocolBufferException.invalidEndTag();
                }
                addGroup(unknownFields, fieldNumber, toImmutable(subFields));
                return true;
            case 4:
                return false;
            case WireFormat.WIRETYPE_FIXED32 /* 5 */:
                addFixed32(unknownFields, fieldNumber, reader.readFixed32());
                return true;
            default:
                throw InvalidProtocolBufferException.invalidWireType();
        }
    }

    final void mergeFrom(B unknownFields, Reader reader) throws IOException {
        while (reader.getFieldNumber() != Integer.MAX_VALUE && mergeOneFieldFrom(unknownFields, reader)) {
        }
    }
}
