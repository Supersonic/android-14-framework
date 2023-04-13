package com.android.framework.protobuf;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public final class TextFormatEscaper {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface ByteSequence {
        byte byteAt(int i);

        int size();
    }

    private TextFormatEscaper() {
    }

    static String escapeBytes(ByteSequence input) {
        StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
            byte b = input.byteAt(i);
            switch (b) {
                case 7:
                    builder.append("\\a");
                    break;
                case 8:
                    builder.append("\\b");
                    break;
                case 9:
                    builder.append("\\t");
                    break;
                case 10:
                    builder.append("\\n");
                    break;
                case 11:
                    builder.append("\\v");
                    break;
                case 12:
                    builder.append("\\f");
                    break;
                case 13:
                    builder.append("\\r");
                    break;
                case 34:
                    builder.append("\\\"");
                    break;
                case 39:
                    builder.append("\\'");
                    break;
                case 92:
                    builder.append("\\\\");
                    break;
                default:
                    if (b >= 32 && b <= 126) {
                        builder.append((char) b);
                        break;
                    } else {
                        builder.append('\\');
                        builder.append((char) (((b >>> 6) & 3) + 48));
                        builder.append((char) (((b >>> 3) & 7) + 48));
                        builder.append((char) ((b & 7) + 48));
                        break;
                    }
            }
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escapeBytes(final ByteString input) {
        return escapeBytes(new ByteSequence() { // from class: com.android.framework.protobuf.TextFormatEscaper.1
            @Override // com.android.framework.protobuf.TextFormatEscaper.ByteSequence
            public int size() {
                return ByteString.this.size();
            }

            @Override // com.android.framework.protobuf.TextFormatEscaper.ByteSequence
            public byte byteAt(int offset) {
                return ByteString.this.byteAt(offset);
            }
        });
    }

    static String escapeBytes(final byte[] input) {
        return escapeBytes(new ByteSequence() { // from class: com.android.framework.protobuf.TextFormatEscaper.2
            @Override // com.android.framework.protobuf.TextFormatEscaper.ByteSequence
            public int size() {
                return input.length;
            }

            @Override // com.android.framework.protobuf.TextFormatEscaper.ByteSequence
            public byte byteAt(int offset) {
                return input[offset];
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escapeText(String input) {
        return escapeBytes(ByteString.copyFromUtf8(input));
    }

    static String escapeDoubleQuotesAndBackslashes(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
