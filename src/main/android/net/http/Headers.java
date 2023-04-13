package android.net.http;

import java.util.ArrayList;
import org.apache.http.HeaderElement;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
/* loaded from: classes.dex */
public final class Headers {
    public static final int CONN_CLOSE = 1;
    public static final int CONN_KEEP_ALIVE = 2;
    public static final String EXPIRES = "expires";
    private static final int HASH_ACCEPT_RANGES = 1397189435;
    private static final int HASH_CACHE_CONTROL = -208775662;
    private static final int HASH_CONN_DIRECTIVE = -775651618;
    private static final int HASH_CONTENT_DISPOSITION = -1267267485;
    private static final int HASH_CONTENT_ENCODING = 2095084583;
    private static final int HASH_CONTENT_LEN = -1132779846;
    private static final int HASH_CONTENT_TYPE = 785670158;
    private static final int HASH_ETAG = 3123477;
    private static final int HASH_EXPIRES = -1309235404;
    private static final int HASH_LAST_MODIFIED = 150043680;
    private static final int HASH_LOCATION = 1901043637;
    private static final int HASH_PRAGMA = -980228804;
    private static final int HASH_PROXY_AUTHENTICATE = -301767724;
    private static final int HASH_PROXY_CONNECTION = 285929373;
    private static final int HASH_REFRESH = 1085444827;
    private static final int HASH_SET_COOKIE = 1237214767;
    private static final int HASH_TRANSFER_ENCODING = 1274458357;
    private static final int HASH_WWW_AUTHENTICATE = -243037365;
    private static final int HASH_X_PERMITTED_CROSS_DOMAIN_POLICIES = -1345594014;
    private static final int HEADER_COUNT = 19;
    private static final int IDX_ACCEPT_RANGES = 10;
    private static final int IDX_CACHE_CONTROL = 12;
    private static final int IDX_CONN_DIRECTIVE = 4;
    private static final int IDX_CONTENT_DISPOSITION = 9;
    private static final int IDX_CONTENT_ENCODING = 3;
    private static final int IDX_CONTENT_LEN = 1;
    private static final int IDX_CONTENT_TYPE = 2;
    private static final int IDX_ETAG = 14;
    private static final int IDX_EXPIRES = 11;
    private static final int IDX_LAST_MODIFIED = 13;
    private static final int IDX_LOCATION = 5;
    private static final int IDX_PRAGMA = 16;
    private static final int IDX_PROXY_AUTHENTICATE = 8;
    private static final int IDX_PROXY_CONNECTION = 6;
    private static final int IDX_REFRESH = 17;
    private static final int IDX_SET_COOKIE = 15;
    private static final int IDX_TRANSFER_ENCODING = 0;
    private static final int IDX_WWW_AUTHENTICATE = 7;
    private static final int IDX_X_PERMITTED_CROSS_DOMAIN_POLICIES = 18;
    private static final String LOGTAG = "Http";
    public static final int NO_CONN_TYPE = 0;
    public static final long NO_CONTENT_LENGTH = -1;
    public static final long NO_TRANSFER_ENCODING = 0;
    public static final String TRANSFER_ENCODING = "transfer-encoding";
    public static final String CONTENT_LEN = "content-length";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_ENCODING = "content-encoding";
    public static final String CONN_DIRECTIVE = "connection";
    public static final String LOCATION = "location";
    public static final String PROXY_CONNECTION = "proxy-connection";
    public static final String WWW_AUTHENTICATE = "www-authenticate";
    public static final String PROXY_AUTHENTICATE = "proxy-authenticate";
    public static final String CONTENT_DISPOSITION = "content-disposition";
    public static final String ACCEPT_RANGES = "accept-ranges";
    public static final String CACHE_CONTROL = "cache-control";
    public static final String LAST_MODIFIED = "last-modified";
    public static final String ETAG = "etag";
    public static final String SET_COOKIE = "set-cookie";
    public static final String PRAGMA = "pragma";
    public static final String REFRESH = "refresh";
    public static final String X_PERMITTED_CROSS_DOMAIN_POLICIES = "x-permitted-cross-domain-policies";
    private static final String[] sHeaderNames = {TRANSFER_ENCODING, CONTENT_LEN, CONTENT_TYPE, CONTENT_ENCODING, CONN_DIRECTIVE, LOCATION, PROXY_CONNECTION, WWW_AUTHENTICATE, PROXY_AUTHENTICATE, CONTENT_DISPOSITION, ACCEPT_RANGES, "expires", CACHE_CONTROL, LAST_MODIFIED, ETAG, SET_COOKIE, PRAGMA, REFRESH, X_PERMITTED_CROSS_DOMAIN_POLICIES};
    private ArrayList<String> cookies = new ArrayList<>(2);
    private String[] mHeaders = new String[HEADER_COUNT];
    private ArrayList<String> mExtraHeaderNames = new ArrayList<>(4);
    private ArrayList<String> mExtraHeaderValues = new ArrayList<>(4);
    private long transferEncoding = 0;
    private long contentLength = -1;
    private int connectionType = 0;

    /* loaded from: classes.dex */
    public interface HeaderCallback {
        void header(String str, String str2);
    }

    public void parseHeader(CharArrayBuffer buffer) {
        int pos = setLowercaseIndexOf(buffer, 58);
        if (pos == -1) {
            return;
        }
        String name = buffer.substringTrimmed(0, pos);
        if (name.length() == 0) {
            return;
        }
        int pos2 = pos + 1;
        String val = buffer.substringTrimmed(pos2, buffer.length());
        switch (name.hashCode()) {
            case HASH_X_PERMITTED_CROSS_DOMAIN_POLICIES /* -1345594014 */:
                if (name.equals(X_PERMITTED_CROSS_DOMAIN_POLICIES)) {
                    this.mHeaders[IDX_X_PERMITTED_CROSS_DOMAIN_POLICIES] = val;
                    return;
                }
                return;
            case HASH_EXPIRES /* -1309235404 */:
                if (name.equals("expires")) {
                    this.mHeaders[IDX_EXPIRES] = val;
                    return;
                }
                return;
            case HASH_CONTENT_DISPOSITION /* -1267267485 */:
                if (name.equals(CONTENT_DISPOSITION)) {
                    this.mHeaders[9] = val;
                    return;
                }
                return;
            case HASH_CONTENT_LEN /* -1132779846 */:
                if (name.equals(CONTENT_LEN)) {
                    this.mHeaders[1] = val;
                    try {
                        this.contentLength = Long.parseLong(val);
                        return;
                    } catch (NumberFormatException e) {
                        return;
                    }
                }
                return;
            case HASH_PRAGMA /* -980228804 */:
                if (name.equals(PRAGMA)) {
                    this.mHeaders[16] = val;
                    return;
                }
                return;
            case HASH_CONN_DIRECTIVE /* -775651618 */:
                if (name.equals(CONN_DIRECTIVE)) {
                    this.mHeaders[4] = val;
                    setConnectionType(buffer, pos2);
                    return;
                }
                return;
            case HASH_PROXY_AUTHENTICATE /* -301767724 */:
                if (name.equals(PROXY_AUTHENTICATE)) {
                    this.mHeaders[IDX_PROXY_AUTHENTICATE] = val;
                    return;
                }
                return;
            case HASH_WWW_AUTHENTICATE /* -243037365 */:
                if (name.equals(WWW_AUTHENTICATE)) {
                    this.mHeaders[7] = val;
                    return;
                }
                return;
            case HASH_CACHE_CONTROL /* -208775662 */:
                if (name.equals(CACHE_CONTROL)) {
                    String str = this.mHeaders[IDX_CACHE_CONTROL];
                    if (str != null && str.length() > 0) {
                        StringBuilder sb = new StringBuilder();
                        String[] strArr = this.mHeaders;
                        strArr[IDX_CACHE_CONTROL] = sb.append(strArr[IDX_CACHE_CONTROL]).append(',').append(val).toString();
                        return;
                    }
                    this.mHeaders[IDX_CACHE_CONTROL] = val;
                    return;
                }
                return;
            case HASH_ETAG /* 3123477 */:
                if (name.equals(ETAG)) {
                    this.mHeaders[IDX_ETAG] = val;
                    return;
                }
                return;
            case HASH_LAST_MODIFIED /* 150043680 */:
                if (name.equals(LAST_MODIFIED)) {
                    this.mHeaders[13] = val;
                    return;
                }
                return;
            case HASH_PROXY_CONNECTION /* 285929373 */:
                if (name.equals(PROXY_CONNECTION)) {
                    this.mHeaders[6] = val;
                    setConnectionType(buffer, pos2);
                    return;
                }
                return;
            case HASH_CONTENT_TYPE /* 785670158 */:
                if (name.equals(CONTENT_TYPE)) {
                    this.mHeaders[2] = val;
                    return;
                }
                return;
            case HASH_REFRESH /* 1085444827 */:
                if (name.equals(REFRESH)) {
                    this.mHeaders[17] = val;
                    return;
                }
                return;
            case HASH_SET_COOKIE /* 1237214767 */:
                if (name.equals(SET_COOKIE)) {
                    this.mHeaders[IDX_SET_COOKIE] = val;
                    this.cookies.add(val);
                    return;
                }
                return;
            case HASH_TRANSFER_ENCODING /* 1274458357 */:
                if (name.equals(TRANSFER_ENCODING)) {
                    this.mHeaders[0] = val;
                    HeaderElement[] encodings = BasicHeaderValueParser.DEFAULT.parseElements(buffer, new ParserCursor(pos2, buffer.length()));
                    int len = encodings.length;
                    if (HTTP.IDENTITY_CODING.equalsIgnoreCase(val)) {
                        this.transferEncoding = -1L;
                        return;
                    } else if (len > 0 && HTTP.CHUNK_CODING.equalsIgnoreCase(encodings[len - 1].getName())) {
                        this.transferEncoding = -2L;
                        return;
                    } else {
                        this.transferEncoding = -1L;
                        return;
                    }
                }
                return;
            case HASH_ACCEPT_RANGES /* 1397189435 */:
                if (name.equals(ACCEPT_RANGES)) {
                    this.mHeaders[10] = val;
                    return;
                }
                return;
            case HASH_LOCATION /* 1901043637 */:
                if (name.equals(LOCATION)) {
                    this.mHeaders[5] = val;
                    return;
                }
                return;
            case HASH_CONTENT_ENCODING /* 2095084583 */:
                if (name.equals(CONTENT_ENCODING)) {
                    this.mHeaders[3] = val;
                    return;
                }
                return;
            default:
                this.mExtraHeaderNames.add(name);
                this.mExtraHeaderValues.add(val);
                return;
        }
    }

    public long getTransferEncoding() {
        return this.transferEncoding;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public int getConnectionType() {
        return this.connectionType;
    }

    public String getContentType() {
        return this.mHeaders[2];
    }

    public String getContentEncoding() {
        return this.mHeaders[3];
    }

    public String getLocation() {
        return this.mHeaders[5];
    }

    public String getWwwAuthenticate() {
        return this.mHeaders[7];
    }

    public String getProxyAuthenticate() {
        return this.mHeaders[IDX_PROXY_AUTHENTICATE];
    }

    public String getContentDisposition() {
        return this.mHeaders[9];
    }

    public String getAcceptRanges() {
        return this.mHeaders[10];
    }

    public String getExpires() {
        return this.mHeaders[IDX_EXPIRES];
    }

    public String getCacheControl() {
        return this.mHeaders[IDX_CACHE_CONTROL];
    }

    public String getLastModified() {
        return this.mHeaders[13];
    }

    public String getEtag() {
        return this.mHeaders[IDX_ETAG];
    }

    public ArrayList<String> getSetCookie() {
        return this.cookies;
    }

    public String getPragma() {
        return this.mHeaders[16];
    }

    public String getRefresh() {
        return this.mHeaders[17];
    }

    public String getXPermittedCrossDomainPolicies() {
        return this.mHeaders[IDX_X_PERMITTED_CROSS_DOMAIN_POLICIES];
    }

    public void setContentLength(long value) {
        this.contentLength = value;
    }

    public void setContentType(String value) {
        this.mHeaders[2] = value;
    }

    public void setContentEncoding(String value) {
        this.mHeaders[3] = value;
    }

    public void setLocation(String value) {
        this.mHeaders[5] = value;
    }

    public void setWwwAuthenticate(String value) {
        this.mHeaders[7] = value;
    }

    public void setProxyAuthenticate(String value) {
        this.mHeaders[IDX_PROXY_AUTHENTICATE] = value;
    }

    public void setContentDisposition(String value) {
        this.mHeaders[9] = value;
    }

    public void setAcceptRanges(String value) {
        this.mHeaders[10] = value;
    }

    public void setExpires(String value) {
        this.mHeaders[IDX_EXPIRES] = value;
    }

    public void setCacheControl(String value) {
        this.mHeaders[IDX_CACHE_CONTROL] = value;
    }

    public void setLastModified(String value) {
        this.mHeaders[13] = value;
    }

    public void setEtag(String value) {
        this.mHeaders[IDX_ETAG] = value;
    }

    public void setXPermittedCrossDomainPolicies(String value) {
        this.mHeaders[IDX_X_PERMITTED_CROSS_DOMAIN_POLICIES] = value;
    }

    public void getHeaders(HeaderCallback hcb) {
        for (int i = 0; i < HEADER_COUNT; i++) {
            String h = this.mHeaders[i];
            if (h != null) {
                hcb.header(sHeaderNames[i], h);
            }
        }
        int extraLen = this.mExtraHeaderNames.size();
        for (int i2 = 0; i2 < extraLen; i2++) {
            hcb.header(this.mExtraHeaderNames.get(i2), this.mExtraHeaderValues.get(i2));
        }
    }

    private void setConnectionType(CharArrayBuffer buffer, int pos) {
        if (containsIgnoreCaseTrimmed(buffer, pos, HTTP.CONN_CLOSE)) {
            this.connectionType = 1;
        } else if (containsIgnoreCaseTrimmed(buffer, pos, HTTP.CONN_KEEP_ALIVE)) {
            this.connectionType = 2;
        }
    }

    static boolean containsIgnoreCaseTrimmed(CharArrayBuffer buffer, int beginIndex, String str) {
        int len = buffer.length();
        char[] chars = buffer.buffer();
        while (beginIndex < len && HTTP.isWhitespace(chars[beginIndex])) {
            beginIndex++;
        }
        int size = str.length();
        boolean ok = len >= beginIndex + size;
        for (int j = 0; ok && j < size; j++) {
            char a = chars[beginIndex + j];
            char b = str.charAt(j);
            if (a != b) {
                ok = Character.toLowerCase(a) == Character.toLowerCase(b);
            }
        }
        return true;
    }

    static int setLowercaseIndexOf(CharArrayBuffer buffer, int ch) {
        int endIndex = buffer.length();
        char[] chars = buffer.buffer();
        for (int i = 0; i < endIndex; i++) {
            char current = chars[i];
            if (current == ch) {
                return i;
            }
            chars[i] = Character.toLowerCase(current);
        }
        return -1;
    }
}
