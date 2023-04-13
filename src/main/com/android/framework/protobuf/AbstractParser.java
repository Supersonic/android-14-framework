package com.android.framework.protobuf;

import com.android.framework.protobuf.AbstractMessageLite;
import com.android.framework.protobuf.MessageLite;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
/* loaded from: classes4.dex */
public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType> {
    private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();

    private UninitializedMessageException newUninitializedMessageException(MessageType message) {
        if (message instanceof AbstractMessageLite) {
            return ((AbstractMessageLite) message).newUninitializedMessageException();
        }
        return new UninitializedMessageException(message);
    }

    private MessageType checkMessageInitialized(MessageType message) throws InvalidProtocolBufferException {
        if (message != null && !message.isInitialized()) {
            throw newUninitializedMessageException(message).asInvalidProtocolBufferException().setUnfinishedMessage(message);
        }
        return message;
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(CodedInputStream input) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(input, EMPTY_REGISTRY);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized((MessageLite) parsePartialFrom(input, extensionRegistry));
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(CodedInputStream input) throws InvalidProtocolBufferException {
        return parseFrom(input, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            CodedInputStream input = data.newCodedInput();
            MessageType message = (MessageType) parsePartialFrom(input, extensionRegistry);
            try {
                input.checkLastTagWas(0);
                return message;
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(message);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(ByteString data) throws InvalidProtocolBufferException {
        return parsePartialFrom(data, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(data, extensionRegistry));
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return parseFrom(data, EMPTY_REGISTRY);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            CodedInputStream input = CodedInputStream.newInstance(data);
            MessageLite messageLite = (MessageLite) parsePartialFrom(input, extensionRegistry);
            try {
                input.checkLastTagWas(0);
                return (MessageType) checkMessageInitialized(messageLite);
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(messageLite);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return parseFrom(data, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            CodedInputStream input = CodedInputStream.newInstance(data, off, len);
            MessageType message = (MessageType) parsePartialFrom(input, extensionRegistry);
            try {
                input.checkLastTagWas(0);
                return message;
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(message);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
        return parsePartialFrom(data, off, len, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return parsePartialFrom(data, 0, data.length, extensionRegistry);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(byte[] data) throws InvalidProtocolBufferException {
        return parsePartialFrom(data, 0, data.length, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(data, off, len, extensionRegistry));
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
        return parseFrom(data, off, len, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return parseFrom(data, 0, data.length, extensionRegistry);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return parseFrom(data, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        CodedInputStream codedInput = CodedInputStream.newInstance(input);
        MessageType message = (MessageType) parsePartialFrom(codedInput, extensionRegistry);
        try {
            codedInput.checkLastTagWas(0);
            return message;
        } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        }
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialFrom(InputStream input) throws InvalidProtocolBufferException {
        return parsePartialFrom(input, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialFrom(input, extensionRegistry));
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseFrom(InputStream input) throws InvalidProtocolBufferException {
        return parseFrom(input, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            int firstByte = input.read();
            if (firstByte == -1) {
                return null;
            }
            int size = CodedInputStream.readRawVarint32(firstByte, input);
            InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
            return parsePartialFrom(limitedInput, extensionRegistry);
        } catch (IOException e) {
            throw new InvalidProtocolBufferException(e);
        }
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parsePartialDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
        return parsePartialDelimitedFrom(input, EMPTY_REGISTRY);
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return checkMessageInitialized(parsePartialDelimitedFrom(input, extensionRegistry));
    }

    @Override // com.android.framework.protobuf.Parser
    public MessageType parseDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
        return parseDelimitedFrom(input, EMPTY_REGISTRY);
    }
}
