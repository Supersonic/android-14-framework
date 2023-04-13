package android.util;

import java.io.UnsupportedEncodingException;
/* loaded from: classes3.dex */
public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int CRLF = 4;
    public static final int DEFAULT = 0;
    public static final int NO_CLOSE = 16;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static abstract class Coder {

        /* renamed from: op */
        public int f467op;
        public byte[] output;

        public abstract int maxOutputSize(int i);

        public abstract boolean process(byte[] bArr, int i, int i2, boolean z);

        Coder() {
        }
    }

    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        Decoder decoder = new Decoder(flags, new byte[(len * 3) / 4]);
        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        }
        if (decoder.f467op == decoder.output.length) {
            return decoder.output;
        }
        byte[] temp = new byte[decoder.f467op];
        System.arraycopy(decoder.output, 0, temp, 0, decoder.f467op);
        return temp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class Decoder extends Coder {
        private static final int[] DECODE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int[] DECODE_WEBSAFE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int EQUALS = -2;
        private static final int SKIP = -1;
        private final int[] alphabet;
        private int state;
        private int value;

        public Decoder(int flags, byte[] output) {
            this.output = output;
            this.alphabet = (flags & 8) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = 0;
            this.value = 0;
        }

        @Override // android.util.Base64.Coder
        public int maxOutputSize(int len) {
            return ((len * 3) / 4) + 10;
        }

        /* JADX WARN: Removed duplicated region for block: B:53:0x00e7  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x00ee  */
        /* JADX WARN: Removed duplicated region for block: B:69:0x00e4 A[SYNTHETIC] */
        @Override // android.util.Base64.Coder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            if (this.state == 6) {
                return false;
            }
            int p = offset;
            int len2 = len + offset;
            int state = this.state;
            int value = this.value;
            int op = 0;
            byte[] output = this.output;
            int[] alphabet = this.alphabet;
            while (p < len2) {
                if (state == 0) {
                    while (p + 4 <= len2) {
                        int i = (alphabet[input[p] & 255] << 18) | (alphabet[input[p + 1] & 255] << 12) | (alphabet[input[p + 2] & 255] << 6) | alphabet[input[p + 3] & 255];
                        value = i;
                        if (i >= 0) {
                            output[op + 2] = (byte) value;
                            output[op + 1] = (byte) (value >> 8);
                            output[op] = (byte) (value >> 16);
                            op += 3;
                            p += 4;
                        } else if (p >= len2) {
                            if (finish) {
                                this.state = state;
                                this.value = value;
                                this.f467op = op;
                                return true;
                            }
                            switch (state) {
                                case 1:
                                    this.state = 6;
                                    return false;
                                case 2:
                                    output[op] = (byte) (value >> 4);
                                    op++;
                                    break;
                                case 3:
                                    int op2 = op + 1;
                                    output[op] = (byte) (value >> 10);
                                    op = op2 + 1;
                                    output[op2] = (byte) (value >> 2);
                                    break;
                                case 4:
                                    this.state = 6;
                                    return false;
                            }
                            this.state = state;
                            this.f467op = op;
                            return true;
                        }
                    }
                    if (p >= len2) {
                    }
                }
                int p2 = p + 1;
                int d = alphabet[input[p] & 255];
                switch (state) {
                    case 0:
                        if (d >= 0) {
                            value = d;
                            state++;
                            break;
                        } else if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                    case 1:
                        if (d >= 0) {
                            value = (value << 6) | d;
                            state++;
                            break;
                        } else if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                    case 2:
                        if (d >= 0) {
                            value = (value << 6) | d;
                            state++;
                            break;
                        } else if (d == -2) {
                            output[op] = (byte) (value >> 4);
                            state = 4;
                            op++;
                            break;
                        } else if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                    case 3:
                        if (d >= 0) {
                            value = (value << 6) | d;
                            output[op + 2] = (byte) value;
                            output[op + 1] = (byte) (value >> 8);
                            output[op] = (byte) (value >> 16);
                            op += 3;
                            state = 0;
                            break;
                        } else if (d == -2) {
                            output[op + 1] = (byte) (value >> 2);
                            output[op] = (byte) (value >> 10);
                            op += 2;
                            state = 5;
                            break;
                        } else if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                    case 4:
                        if (d == -2) {
                            state++;
                            break;
                        } else if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                    case 5:
                        if (d == -1) {
                            break;
                        } else {
                            this.state = 6;
                            return false;
                        }
                }
                p = p2;
            }
            if (finish) {
            }
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(encode(input, offset, len, flags), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);
        int output_len = (len / 3) * 4;
        if (encoder.do_padding) {
            if (len % 3 > 0) {
                output_len += 4;
            }
        } else {
            switch (len % 3) {
                case 1:
                    output_len += 2;
                    break;
                case 2:
                    output_len += 3;
                    break;
            }
        }
        if (encoder.do_newline && len > 0) {
            output_len += (((len - 1) / 57) + 1) * (encoder.do_cr ? 2 : 1);
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        return encoder.output;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class Encoder extends Coder {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final byte[] ENCODE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        private static final byte[] ENCODE_WEBSAFE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        public static final int LINE_GROUPS = 19;
        private final byte[] alphabet;
        private int count;
        public final boolean do_cr;
        public final boolean do_newline;
        public final boolean do_padding;
        private final byte[] tail;
        int tailLen;

        public Encoder(int flags, byte[] output) {
            this.output = output;
            this.do_padding = (flags & 1) == 0;
            boolean z = (flags & 2) == 0;
            this.do_newline = z;
            this.do_cr = (flags & 4) != 0;
            this.alphabet = (flags & 8) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = z ? 19 : -1;
        }

        @Override // android.util.Base64.Coder
        public int maxOutputSize(int len) {
            return ((len * 8) / 5) + 10;
        }

        /* JADX WARN: Incorrect condition in loop: B:21:0x009a */
        @Override // android.util.Base64.Coder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean process(byte[] input, int offset, int len, boolean finish) {
            int t;
            int t2;
            int p;
            int t3;
            int t4;
            byte[] alphabet = this.alphabet;
            byte[] output = this.output;
            int op = 0;
            int count = this.count;
            int p2 = offset;
            int len2 = len + offset;
            int v = -1;
            switch (this.tailLen) {
                case 1:
                    if (p2 + 2 <= len2) {
                        int p3 = p2 + 1;
                        v = ((input[p2] & 255) << 8) | ((this.tail[0] & 255) << 16) | (input[p3] & 255);
                        this.tailLen = 0;
                        p2 = p3 + 1;
                        break;
                    }
                    break;
                case 2:
                    if (p2 + 1 <= len2) {
                        byte[] bArr = this.tail;
                        v = ((bArr[1] & 255) << 8) | ((bArr[0] & 255) << 16) | (input[p2] & 255);
                        this.tailLen = 0;
                        p2++;
                        break;
                    }
                    break;
            }
            if (v != -1) {
                int op2 = 0 + 1;
                output[0] = alphabet[(v >> 18) & 63];
                int op3 = op2 + 1;
                output[op2] = alphabet[(v >> 12) & 63];
                int op4 = op3 + 1;
                output[op3] = alphabet[(v >> 6) & 63];
                op = op4 + 1;
                output[op4] = alphabet[v & 63];
                count--;
                if (count == 0) {
                    if (this.do_cr) {
                        output[op] = 13;
                        op++;
                    }
                    output[op] = 10;
                    count = 19;
                    op++;
                }
            }
            while (op <= len2) {
                int v2 = ((input[p2] & 255) << 16) | ((input[p2 + 1] & 255) << 8) | (input[p2 + 2] & 255);
                output[op] = alphabet[(v2 >> 18) & 63];
                output[op + 1] = alphabet[(v2 >> 12) & 63];
                output[op + 2] = alphabet[(v2 >> 6) & 63];
                output[op + 3] = alphabet[v2 & 63];
                p2 += 3;
                op += 4;
                count--;
                if (count == 0) {
                    if (this.do_cr) {
                        output[op] = 13;
                        op++;
                    }
                    output[op] = 10;
                    count = 19;
                    op++;
                }
            }
            if (finish) {
                int i = this.tailLen;
                if (p2 - i == len2 - 1) {
                    if (i > 0) {
                        t3 = 0 + 1;
                        t4 = this.tail[0];
                    } else {
                        int i2 = p2 + 1;
                        t3 = 0;
                        t4 = input[p2];
                    }
                    int v3 = (t4 & 255) << 4;
                    this.tailLen = i - t3;
                    int op5 = op + 1;
                    output[op] = alphabet[(v3 >> 6) & 63];
                    op = op5 + 1;
                    output[op5] = alphabet[v3 & 63];
                    if (this.do_padding) {
                        int op6 = op + 1;
                        output[op] = 61;
                        op = op6 + 1;
                        output[op6] = 61;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            output[op] = 13;
                            op++;
                        }
                        output[op] = 10;
                        op++;
                    }
                } else if (p2 - i != len2 - 2) {
                    if (this.do_newline && op > 0 && count != 19) {
                        if (this.do_cr) {
                            output[op] = 13;
                            op++;
                        }
                        output[op] = 10;
                        op++;
                    }
                } else {
                    if (i > 1) {
                        t = 0 + 1;
                        t2 = this.tail[0];
                    } else {
                        t = 0;
                        t2 = input[p2];
                        p2++;
                    }
                    int i3 = (t2 & 255) << 10;
                    if (i > 0) {
                        p = this.tail[t];
                        t++;
                    } else {
                        int i4 = p2 + 1;
                        p = input[p2];
                    }
                    int v4 = i3 | ((p & 255) << 2);
                    this.tailLen = i - t;
                    int op7 = op + 1;
                    output[op] = alphabet[(v4 >> 12) & 63];
                    int op8 = op7 + 1;
                    output[op7] = alphabet[(v4 >> 6) & 63];
                    int op9 = op8 + 1;
                    output[op8] = alphabet[v4 & 63];
                    if (this.do_padding) {
                        output[op9] = 61;
                        op9++;
                    }
                    if (this.do_newline) {
                        if (this.do_cr) {
                            output[op9] = 13;
                            op9++;
                        }
                        output[op9] = 10;
                        op9++;
                    }
                    op = op9;
                }
            } else if (p2 == len2 - 1) {
                byte[] bArr2 = this.tail;
                int i5 = this.tailLen;
                this.tailLen = i5 + 1;
                bArr2[i5] = input[p2];
            } else if (p2 == len2 - 2) {
                byte[] bArr3 = this.tail;
                int i6 = this.tailLen;
                int i7 = i6 + 1;
                this.tailLen = i7;
                bArr3[i6] = input[p2];
                this.tailLen = i7 + 1;
                bArr3[i7] = input[p2 + 1];
            }
            this.f467op = op;
            this.count = count;
            return true;
        }
    }

    private Base64() {
    }
}
