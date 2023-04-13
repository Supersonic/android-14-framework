package android.audio.policy.configuration.V7_0;

import android.provider.Telephony;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Profile {
    private List<AudioChannelMask> channelMasks;
    private AudioEncapsulationType encapsulationType;
    private String format;
    private String name;
    private List<BigInteger> samplingRates;

    public String getName() {
        return this.name;
    }

    boolean hasName() {
        if (this.name == null) {
            return false;
        }
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return this.format;
    }

    boolean hasFormat() {
        if (this.format == null) {
            return false;
        }
        return true;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<BigInteger> getSamplingRates() {
        if (this.samplingRates == null) {
            this.samplingRates = new ArrayList();
        }
        return this.samplingRates;
    }

    boolean hasSamplingRates() {
        if (this.samplingRates == null) {
            return false;
        }
        return true;
    }

    public void setSamplingRates(List<BigInteger> samplingRates) {
        this.samplingRates = samplingRates;
    }

    public List<AudioChannelMask> getChannelMasks() {
        if (this.channelMasks == null) {
            this.channelMasks = new ArrayList();
        }
        return this.channelMasks;
    }

    boolean hasChannelMasks() {
        if (this.channelMasks == null) {
            return false;
        }
        return true;
    }

    public void setChannelMasks(List<AudioChannelMask> channelMasks) {
        this.channelMasks = channelMasks;
    }

    public AudioEncapsulationType getEncapsulationType() {
        return this.encapsulationType;
    }

    boolean hasEncapsulationType() {
        if (this.encapsulationType == null) {
            return false;
        }
        return true;
    }

    public void setEncapsulationType(AudioEncapsulationType encapsulationType) {
        this.encapsulationType = encapsulationType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Profile read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        String[] split;
        String[] split2;
        Profile _instance = new Profile();
        String _raw = _parser.getAttributeValue(null, "name");
        if (_raw != null) {
            _instance.setName(_raw);
        }
        String _raw2 = _parser.getAttributeValue(null, Telephony.CellBroadcasts.MESSAGE_FORMAT);
        if (_raw2 != null) {
            _instance.setFormat(_raw2);
        }
        String _raw3 = _parser.getAttributeValue(null, "samplingRates");
        if (_raw3 != null) {
            List<BigInteger> _value = new ArrayList<>();
            for (String _token : _raw3.split("\\s+")) {
                _value.add(new BigInteger(_token));
            }
            _instance.setSamplingRates(_value);
        }
        String _raw4 = _parser.getAttributeValue(null, "channelMasks");
        if (_raw4 != null) {
            List<AudioChannelMask> _value2 = new ArrayList<>();
            for (String _token2 : _raw4.split("\\s+")) {
                _value2.add(AudioChannelMask.fromString(_token2));
            }
            _instance.setChannelMasks(_value2);
        }
        String _raw5 = _parser.getAttributeValue(null, "encapsulationType");
        if (_raw5 != null) {
            _instance.setEncapsulationType(AudioEncapsulationType.fromString(_raw5));
        }
        XmlParser.skip(_parser);
        return _instance;
    }
}
