package android.webkit;

import android.annotation.SystemApi;
import java.util.Map;
/* loaded from: classes4.dex */
public class WebStorage {

    @Deprecated
    /* loaded from: classes4.dex */
    public interface QuotaUpdater {
        void updateQuota(long j);
    }

    /* loaded from: classes4.dex */
    public static class Origin {
        private String mOrigin;
        private long mQuota;
        private long mUsage;

        @SystemApi
        protected Origin(String origin, long quota, long usage) {
            this.mOrigin = null;
            this.mQuota = 0L;
            this.mUsage = 0L;
            this.mOrigin = origin;
            this.mQuota = quota;
            this.mUsage = usage;
        }

        public String getOrigin() {
            return this.mOrigin;
        }

        public long getQuota() {
            return this.mQuota;
        }

        public long getUsage() {
            return this.mUsage;
        }
    }

    public void getOrigins(ValueCallback<Map> callback) {
    }

    public void getUsageForOrigin(String origin, ValueCallback<Long> callback) {
    }

    public void getQuotaForOrigin(String origin, ValueCallback<Long> callback) {
    }

    @Deprecated
    public void setQuotaForOrigin(String origin, long quota) {
    }

    public void deleteOrigin(String origin) {
    }

    public void deleteAllData() {
    }

    public static WebStorage getInstance() {
        return WebViewFactory.getProvider().getWebStorage();
    }
}
