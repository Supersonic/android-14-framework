package android.net;

import android.annotation.SystemApi;
import android.content.IntentFilter;
import com.android.internal.telephony.TelephonyStatsLog;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SystemApi
/* loaded from: classes2.dex */
public class WebAddress {
    static final int MATCH_GROUP_AUTHORITY = 2;
    static final int MATCH_GROUP_HOST = 3;
    static final int MATCH_GROUP_PATH = 5;
    static final int MATCH_GROUP_PORT = 4;
    static final int MATCH_GROUP_SCHEME = 1;
    static Pattern sAddressPattern = Pattern.compile("(?:(http|https|file)\\:\\/\\/)?(?:([-A-Za-z0-9$_.+!*'(),;?&=]+(?:\\:[-A-Za-z0-9$_.+!*'(),;?&=]+)?)@)?([a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef%_-][a-zA-Z0-9 -\ud7ff豈-\ufdcfﷰ-\uffef%_\\.-]*|\\[[0-9a-fA-F:\\.]+\\])?(?:\\:([0-9]*))?(\\/?[^#]*)?.*", 2);
    private String mAuthInfo;
    private String mHost;
    private String mPath;
    private int mPort;
    private String mScheme;

    public WebAddress(String address) throws ParseException {
        if (address == null) {
            throw new NullPointerException();
        }
        this.mScheme = "";
        this.mHost = "";
        this.mPort = -1;
        this.mPath = "/";
        this.mAuthInfo = "";
        Matcher m = sAddressPattern.matcher(address);
        if (m.matches()) {
            String t = m.group(1);
            if (t != null) {
                this.mScheme = t.toLowerCase(Locale.ROOT);
            }
            String t2 = m.group(2);
            if (t2 != null) {
                this.mAuthInfo = t2;
            }
            String t3 = m.group(3);
            if (t3 != null) {
                this.mHost = t3;
            }
            String t4 = m.group(4);
            if (t4 != null && t4.length() > 0) {
                try {
                    this.mPort = Integer.parseInt(t4);
                } catch (NumberFormatException e) {
                    throw new ParseException("Bad port");
                }
            }
            String t5 = m.group(5);
            if (t5 != null && t5.length() > 0) {
                if (t5.charAt(0) != '/') {
                    this.mPath = "/" + t5;
                } else {
                    this.mPath = t5;
                }
            }
            if (this.mPort != 443 || !this.mScheme.equals("")) {
                if (this.mPort == -1) {
                    if (this.mScheme.equals(IntentFilter.SCHEME_HTTPS)) {
                        this.mPort = TelephonyStatsLog.MMS_SMS_DATABASE_HELPER_ON_UPGRADE_FAILED;
                    } else {
                        this.mPort = 80;
                    }
                }
            } else {
                this.mScheme = IntentFilter.SCHEME_HTTPS;
            }
            if (this.mScheme.equals("")) {
                this.mScheme = IntentFilter.SCHEME_HTTP;
                return;
            }
            return;
        }
        throw new ParseException("Bad address");
    }

    public String toString() {
        String port = "";
        if ((this.mPort != 443 && this.mScheme.equals(IntentFilter.SCHEME_HTTPS)) || (this.mPort != 80 && this.mScheme.equals(IntentFilter.SCHEME_HTTP))) {
            port = ":" + Integer.toString(this.mPort);
        }
        String authInfo = "";
        if (this.mAuthInfo.length() > 0) {
            authInfo = this.mAuthInfo + "@";
        }
        return this.mScheme + "://" + authInfo + this.mHost + port + this.mPath;
    }

    public void setScheme(String scheme) {
        this.mScheme = scheme;
    }

    public String getScheme() {
        return this.mScheme;
    }

    public void setHost(String host) {
        this.mHost = host;
    }

    public String getHost() {
        return this.mHost;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setAuthInfo(String authInfo) {
        this.mAuthInfo = authInfo;
    }

    public String getAuthInfo() {
        return this.mAuthInfo;
    }
}
