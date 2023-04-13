package com.android.ims.rcs.uce.presence.pidfparser;

import android.net.Uri;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Audio;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.CapsConstant;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Duplex;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.ServiceCaps;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Video;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.OmaPresConstant;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Basic;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.PidfConstant;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Presence;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Tuple;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class PidfParser {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PidfParser";
    private static final Pattern PIDF_PATTERN = Pattern.compile("\t|\r|\n");
    private static final TimestampProxy sLocalTimestampProxy = new TimestampProxy() { // from class: com.android.ims.rcs.uce.presence.pidfparser.PidfParser$$ExternalSyntheticLambda0
        @Override // com.android.ims.rcs.uce.presence.pidfparser.PidfParser.TimestampProxy
        public final Instant getTimestamp() {
            Instant now;
            now = Instant.now();
            return now;
        }
    };
    private static TimestampProxy sOverrideTimestampProxy;

    /* loaded from: classes.dex */
    public interface TimestampProxy {
        Instant getTimestamp();
    }

    public static void setTimestampProxy(TimestampProxy proxy) {
        sOverrideTimestampProxy = proxy;
    }

    private static TimestampProxy getTimestampProxy() {
        TimestampProxy timestampProxy = sOverrideTimestampProxy;
        return timestampProxy != null ? timestampProxy : sLocalTimestampProxy;
    }

    public static String convertToPidf(RcsContactUceCapability capabilities) {
        StringWriter pidfWriter = new StringWriter();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer serializer = factory.newSerializer();
            serializer.setOutput(pidfWriter);
            serializer.setPrefix("", PidfConstant.NAMESPACE);
            serializer.setPrefix("op", OmaPresConstant.NAMESPACE);
            serializer.setPrefix("caps", CapsConstant.NAMESPACE);
            Presence presence = PidfParserUtils.getPresence(capabilities);
            serializer.startDocument(PidfParserConstant.ENCODING_UTF_8, true);
            presence.serialize(serializer);
            serializer.endDocument();
            serializer.flush();
            return pidfWriter.toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        } catch (XmlPullParserException parserEx) {
            parserEx.printStackTrace();
            return null;
        }
    }

    public static RcsContactUceCapabilityWrapper getRcsContactUceCapabilityWrapper(String pidf) {
        if (TextUtils.isEmpty(pidf)) {
            Log.w(LOG_TAG, "getRcsContactUceCapabilityWrapper: The given pidf is empty");
            return null;
        }
        Matcher matcher = PIDF_PATTERN.matcher(pidf);
        String formattedPidf = matcher.replaceAll("");
        if (TextUtils.isEmpty(formattedPidf)) {
            Log.w(LOG_TAG, "getRcsContactUceCapabilityWrapper: The formatted pidf is empty");
            return null;
        }
        Reader reader = null;
        try {
            try {
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
                reader = new StringReader(formattedPidf);
                parser.setInput(reader);
                Presence presence = parsePidf(parser);
                RcsContactUceCapabilityWrapper convertToRcsContactUceCapability = convertToRcsContactUceCapability(presence);
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return convertToRcsContactUceCapability;
            } catch (IOException | XmlPullParserException e2) {
                e2.printStackTrace();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static Presence parsePidf(XmlPullParser parser) throws IOException, XmlPullParserException {
        Presence presence = null;
        int nextType = parser.next();
        boolean findPresenceTag = false;
        while (true) {
            if (nextType == 2 && Presence.ELEMENT_NAME.equals(parser.getName())) {
                findPresenceTag = true;
                presence = new Presence();
                presence.parse(parser);
                break;
            }
            nextType = parser.next();
            if (nextType == 1) {
                break;
            }
        }
        if (!findPresenceTag) {
            Log.w(LOG_TAG, "parsePidf: The presence start tag not found.");
        }
        return presence;
    }

    private static RcsContactUceCapabilityWrapper convertToRcsContactUceCapability(Presence presence) {
        if (presence == null) {
            Log.w(LOG_TAG, "convertToRcsContactUceCapability: The presence is null");
            return null;
        } else if (TextUtils.isEmpty(presence.getEntity())) {
            Log.w(LOG_TAG, "convertToRcsContactUceCapability: The entity is empty");
            return null;
        } else {
            final RcsContactUceCapabilityWrapper uceCapabilityWrapper = new RcsContactUceCapabilityWrapper(Uri.parse(presence.getEntity()), 0, 3);
            presence.getTupleList().forEach(new Consumer() { // from class: com.android.ims.rcs.uce.presence.pidfparser.PidfParser$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PidfParser.lambda$convertToRcsContactUceCapability$1(RcsContactUceCapabilityWrapper.this, (Tuple) obj);
                }
            });
            uceCapabilityWrapper.setEntityUri(Uri.parse(presence.getEntity()));
            return uceCapabilityWrapper;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$convertToRcsContactUceCapability$1(RcsContactUceCapabilityWrapper uceCapabilityWrapper, Tuple tuple) {
        if (!tuple.getMalformed()) {
            RcsContactPresenceTuple capabilityTuple = getRcsContactPresenceTuple(tuple);
            if (capabilityTuple != null) {
                uceCapabilityWrapper.addCapabilityTuple(capabilityTuple);
                return;
            }
            return;
        }
        uceCapabilityWrapper.setMalformedContents();
    }

    private static RcsContactPresenceTuple getRcsContactPresenceTuple(Tuple tuple) {
        List<ElementBase> serviceCapsList;
        if (tuple == null) {
            return null;
        }
        String status = Basic.CLOSED;
        if (Basic.OPEN.equals(PidfParserUtils.getTupleStatus(tuple))) {
            status = Basic.OPEN;
        }
        String serviceId = PidfParserUtils.getTupleServiceId(tuple);
        String serviceVersion = PidfParserUtils.getTupleServiceVersion(tuple);
        String serviceDescription = PidfParserUtils.getTupleServiceDescription(tuple);
        RcsContactPresenceTuple.Builder builder = new RcsContactPresenceTuple.Builder(status, serviceId, serviceVersion);
        String contact = PidfParserUtils.getTupleContact(tuple);
        if (!TextUtils.isEmpty(contact)) {
            builder.setContactUri(Uri.parse(contact));
        }
        builder.setTime(getTimestampProxy().getTimestamp());
        if (!TextUtils.isEmpty(serviceDescription)) {
            builder.setServiceDescription(serviceDescription);
        }
        ServiceCaps serviceCaps = tuple.getServiceCaps();
        if (serviceCaps != null && (serviceCapsList = serviceCaps.getElements()) != null && !serviceCapsList.isEmpty()) {
            boolean isAudioSupported = false;
            boolean isVideoSupported = false;
            List<String> supportedTypes = null;
            List<String> notSupportedTypes = null;
            for (ElementBase element : serviceCapsList) {
                if (element instanceof Audio) {
                    isAudioSupported = ((Audio) element).isAudioSupported();
                } else if (element instanceof Video) {
                    isVideoSupported = ((Video) element).isVideoSupported();
                } else if (element instanceof Duplex) {
                    supportedTypes = ((Duplex) element).getSupportedTypes();
                    notSupportedTypes = ((Duplex) element).getNotSupportedTypes();
                }
            }
            RcsContactPresenceTuple.ServiceCapabilities.Builder capabilitiesBuilder = new RcsContactPresenceTuple.ServiceCapabilities.Builder(isAudioSupported, isVideoSupported);
            if (supportedTypes != null && !supportedTypes.isEmpty()) {
                for (String supportedType : supportedTypes) {
                    capabilitiesBuilder.addSupportedDuplexMode(supportedType);
                }
            }
            if (notSupportedTypes != null && !notSupportedTypes.isEmpty()) {
                for (String notSupportedType : notSupportedTypes) {
                    capabilitiesBuilder.addUnsupportedDuplexMode(notSupportedType);
                }
            }
            builder.setServiceCapabilities(capabilitiesBuilder.build());
        }
        return builder.build();
    }
}
