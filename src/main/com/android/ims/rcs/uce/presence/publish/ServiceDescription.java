package com.android.ims.rcs.uce.presence.publish;

import android.telephony.ims.RcsContactPresenceTuple;
import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserConstant;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Basic;
import java.util.Objects;
/* loaded from: classes.dex */
public class ServiceDescription {
    public final String description;
    public final String serviceId;
    public final String version;
    public static final ServiceDescription SERVICE_DESCRIPTION_CHAT_IM = new ServiceDescription("org.openmobilealliance:IM-session", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CHAT_SESSION = new ServiceDescription("org.openmobilealliance:ChatSession", "2.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_FT = new ServiceDescription("org.openmobilealliance:File-Transfer-HTTP", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_FT_SMS = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.ftsms", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_PRESENCE = new ServiceDescription(PidfParserConstant.SERVICE_ID_CAPS_DISCOVERY, "1.0", "Capabilities Discovery Service");
    public static final ServiceDescription SERVICE_DESCRIPTION_MMTEL_VOICE = new ServiceDescription(PidfParserConstant.SERVICE_ID_IpCall, "1.0", "Voice Service");
    public static final ServiceDescription SERVICE_DESCRIPTION_MMTEL_VOICE_VIDEO = new ServiceDescription(PidfParserConstant.SERVICE_ID_IpCall, "1.0", "Voice and Video Service");
    public static final ServiceDescription SERVICE_DESCRIPTION_GEOPUSH = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geopush", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_GEOPUSH_SMS = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.geosms", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CALL_COMPOSER = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CALL_COMPOSER_MMTEL = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callcomposer", "2.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_POST_CALL = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.callunanswered", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_SHARED_MAP = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedmap", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_SHARED_SKETCH = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-service.ims.icsi.gsma.sharedsketch", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SESSION = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SESSION_V1 = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot", "1.0", "Chatbot Session");
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SESSION_V2 = new ServiceDescription("org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot", "2.0", "Chatbot Session");
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SA_SESSION = new ServiceDescription(" org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa".trim(), "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SA_SESSION_V1 = new ServiceDescription(" org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa".trim(), "1.0", "Chatbot Standalone");
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_SA_SESSION_V2 = new ServiceDescription(" org.3gpp.urn:urn-7:3gpp-application.ims.iari.rcs.chatbot.sa".trim(), "2.0", "Chatbot Standalone");
    public static final ServiceDescription SERVICE_DESCRIPTION_CHATBOT_ROLE = new ServiceDescription("org.gsma.rcs.isbot", "1.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_SLM = new ServiceDescription("org.openmobilealliance:StandaloneMsg", "2.0", null);
    public static final ServiceDescription SERVICE_DESCRIPTION_SLM_PAGER_LARGE = new ServiceDescription("org.openmobilealliance:StandaloneMsg", "2.0", "Standalone Messaging");

    public ServiceDescription(String serviceId, String version, String description) {
        this.serviceId = serviceId;
        this.version = version;
        this.description = description;
    }

    public RcsContactPresenceTuple.Builder getTupleBuilder() {
        RcsContactPresenceTuple.Builder b = new RcsContactPresenceTuple.Builder(Basic.OPEN, this.serviceId, this.version);
        if (!TextUtils.isEmpty(this.description)) {
            b.setServiceDescription(this.description);
        }
        return b;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceDescription that = (ServiceDescription) o;
        if (this.serviceId.equals(that.serviceId) && this.version.equals(that.version) && Objects.equals(this.description, that.description)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.serviceId, this.version, this.description);
    }

    public String toString() {
        return "(id=" + this.serviceId + ", v=" + this.version + ", d=" + this.description + ')';
    }
}
