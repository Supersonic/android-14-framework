package android.net;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
/* loaded from: classes2.dex */
public final class UriCodec {
    private static final char INVALID_INPUT_CHARACTER = 65533;

    private UriCodec() {
    }

    private static int hexCharToValue(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return (c + '\n') - 97;
        }
        if ('A' <= c && c <= 'F') {
            return (c + '\n') - 65;
        }
        return -1;
    }

    private static URISyntaxException unexpectedCharacterException(String uri, String name, char unexpected, int index) {
        String nameString = name == null ? "" : " in [" + name + NavigationBarInflaterView.SIZE_MOD_END;
        return new URISyntaxException(uri, "Unexpected character" + nameString + ": " + unexpected, index);
    }

    private static char getNextCharacter(String uri, int index, int end, String name) throws URISyntaxException {
        if (index >= end) {
            String nameString = name == null ? "" : " in [" + name + NavigationBarInflaterView.SIZE_MOD_END;
            throw new URISyntaxException(uri, "Unexpected end of string" + nameString, index);
        }
        return uri.charAt(index);
    }

    public static String decode(String s, boolean convertPlus, Charset charset, boolean throwOnFailure) {
        StringBuilder builder = new StringBuilder(s.length());
        appendDecoded(builder, s, convertPlus, charset, throwOnFailure);
        return builder.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x008a, code lost:
        r1.put(r4);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static void appendDecoded(StringBuilder builder, String s, boolean convertPlus, Charset charset, boolean throwOnFailure) {
        CharsetDecoder decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).replaceWith("�").onUnmappableCharacter(CodingErrorAction.REPORT);
        ByteBuffer byteBuffer = ByteBuffer.allocate(s.length());
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            i++;
            switch (c) {
                case '%':
                    byte hexValue = 0;
                    int j = 0;
                    while (true) {
                        if (j >= 2) {
                            break;
                        } else {
                            try {
                                char c2 = getNextCharacter(s, i, s.length(), null);
                                i++;
                                int newDigit = hexCharToValue(c2);
                                if (newDigit < 0) {
                                    if (throwOnFailure) {
                                        throw new IllegalArgumentException(unexpectedCharacterException(s, null, c2, i - 1));
                                    }
                                    flushDecodingByteAccumulator(builder, decoder, byteBuffer, throwOnFailure);
                                    builder.append(INVALID_INPUT_CHARACTER);
                                    break;
                                } else {
                                    hexValue = (byte) ((hexValue * 16) + newDigit);
                                    j++;
                                }
                            } catch (URISyntaxException e) {
                                if (throwOnFailure) {
                                    throw new IllegalArgumentException(e);
                                }
                                flushDecodingByteAccumulator(builder, decoder, byteBuffer, throwOnFailure);
                                builder.append(INVALID_INPUT_CHARACTER);
                                return;
                            }
                        }
                    }
                case '+':
                    flushDecodingByteAccumulator(builder, decoder, byteBuffer, throwOnFailure);
                    builder.append(convertPlus ? ' ' : '+');
                    break;
                default:
                    flushDecodingByteAccumulator(builder, decoder, byteBuffer, throwOnFailure);
                    builder.append(c);
                    break;
            }
        }
        flushDecodingByteAccumulator(builder, decoder, byteBuffer, throwOnFailure);
    }

    private static void flushDecodingByteAccumulator(StringBuilder builder, CharsetDecoder decoder, ByteBuffer byteBuffer, boolean throwOnFailure) {
        if (byteBuffer.position() == 0) {
            return;
        }
        byteBuffer.flip();
        try {
            try {
                builder.append((CharSequence) decoder.decode(byteBuffer));
            } catch (CharacterCodingException e) {
                if (throwOnFailure) {
                    throw new IllegalArgumentException(e);
                }
                builder.append(INVALID_INPUT_CHARACTER);
            }
        } finally {
            byteBuffer.flip();
            byteBuffer.limit(byteBuffer.capacity());
        }
    }
}
