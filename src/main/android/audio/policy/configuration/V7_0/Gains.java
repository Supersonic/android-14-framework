package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Gains {
    private List<Gain> gain;

    /* loaded from: classes.dex */
    public static class Gain {
        private AudioChannelMask channel_mask;
        private Integer defaultValueMB;
        private Integer maxRampMs;
        private Integer maxValueMB;
        private Integer minRampMs;
        private Integer minValueMB;
        private List<AudioGainMode> mode;
        private String name;
        private Integer stepValueMB;
        private Boolean useForVolume;

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

        public List<AudioGainMode> getMode() {
            if (this.mode == null) {
                this.mode = new ArrayList();
            }
            return this.mode;
        }

        boolean hasMode() {
            if (this.mode == null) {
                return false;
            }
            return true;
        }

        public void setMode(List<AudioGainMode> mode) {
            this.mode = mode;
        }

        public AudioChannelMask getChannel_mask() {
            return this.channel_mask;
        }

        boolean hasChannel_mask() {
            if (this.channel_mask == null) {
                return false;
            }
            return true;
        }

        public void setChannel_mask(AudioChannelMask channel_mask) {
            this.channel_mask = channel_mask;
        }

        public int getMinValueMB() {
            Integer num = this.minValueMB;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasMinValueMB() {
            if (this.minValueMB == null) {
                return false;
            }
            return true;
        }

        public void setMinValueMB(int minValueMB) {
            this.minValueMB = Integer.valueOf(minValueMB);
        }

        public int getMaxValueMB() {
            Integer num = this.maxValueMB;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasMaxValueMB() {
            if (this.maxValueMB == null) {
                return false;
            }
            return true;
        }

        public void setMaxValueMB(int maxValueMB) {
            this.maxValueMB = Integer.valueOf(maxValueMB);
        }

        public int getDefaultValueMB() {
            Integer num = this.defaultValueMB;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasDefaultValueMB() {
            if (this.defaultValueMB == null) {
                return false;
            }
            return true;
        }

        public void setDefaultValueMB(int defaultValueMB) {
            this.defaultValueMB = Integer.valueOf(defaultValueMB);
        }

        public int getStepValueMB() {
            Integer num = this.stepValueMB;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasStepValueMB() {
            if (this.stepValueMB == null) {
                return false;
            }
            return true;
        }

        public void setStepValueMB(int stepValueMB) {
            this.stepValueMB = Integer.valueOf(stepValueMB);
        }

        public int getMinRampMs() {
            Integer num = this.minRampMs;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasMinRampMs() {
            if (this.minRampMs == null) {
                return false;
            }
            return true;
        }

        public void setMinRampMs(int minRampMs) {
            this.minRampMs = Integer.valueOf(minRampMs);
        }

        public int getMaxRampMs() {
            Integer num = this.maxRampMs;
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        boolean hasMaxRampMs() {
            if (this.maxRampMs == null) {
                return false;
            }
            return true;
        }

        public void setMaxRampMs(int maxRampMs) {
            this.maxRampMs = Integer.valueOf(maxRampMs);
        }

        public boolean getUseForVolume() {
            Boolean bool = this.useForVolume;
            if (bool == null) {
                return false;
            }
            return bool.booleanValue();
        }

        boolean hasUseForVolume() {
            if (this.useForVolume == null) {
                return false;
            }
            return true;
        }

        public void setUseForVolume(boolean useForVolume) {
            this.useForVolume = Boolean.valueOf(useForVolume);
        }

        static Gain read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            String[] split;
            Gain _instance = new Gain();
            String _raw = _parser.getAttributeValue(null, "name");
            if (_raw != null) {
                _instance.setName(_raw);
            }
            String _raw2 = _parser.getAttributeValue(null, "mode");
            if (_raw2 != null) {
                List<AudioGainMode> _value = new ArrayList<>();
                for (String _token : _raw2.split("\\s+")) {
                    _value.add(AudioGainMode.fromString(_token));
                }
                _instance.setMode(_value);
            }
            String _raw3 = _parser.getAttributeValue(null, "channel_mask");
            if (_raw3 != null) {
                AudioChannelMask _value2 = AudioChannelMask.fromString(_raw3);
                _instance.setChannel_mask(_value2);
            }
            String _raw4 = _parser.getAttributeValue(null, "minValueMB");
            if (_raw4 != null) {
                int _value3 = Integer.parseInt(_raw4);
                _instance.setMinValueMB(_value3);
            }
            String _raw5 = _parser.getAttributeValue(null, "maxValueMB");
            if (_raw5 != null) {
                int _value4 = Integer.parseInt(_raw5);
                _instance.setMaxValueMB(_value4);
            }
            String _raw6 = _parser.getAttributeValue(null, "defaultValueMB");
            if (_raw6 != null) {
                int _value5 = Integer.parseInt(_raw6);
                _instance.setDefaultValueMB(_value5);
            }
            String _raw7 = _parser.getAttributeValue(null, "stepValueMB");
            if (_raw7 != null) {
                int _value6 = Integer.parseInt(_raw7);
                _instance.setStepValueMB(_value6);
            }
            String _raw8 = _parser.getAttributeValue(null, "minRampMs");
            if (_raw8 != null) {
                int _value7 = Integer.parseInt(_raw8);
                _instance.setMinRampMs(_value7);
            }
            String _raw9 = _parser.getAttributeValue(null, "maxRampMs");
            if (_raw9 != null) {
                int _value8 = Integer.parseInt(_raw9);
                _instance.setMaxRampMs(_value8);
            }
            String _raw10 = _parser.getAttributeValue(null, "useForVolume");
            if (_raw10 != null) {
                boolean _value9 = Boolean.parseBoolean(_raw10);
                _instance.setUseForVolume(_value9);
            }
            XmlParser.skip(_parser);
            return _instance;
        }
    }

    public List<Gain> getGain() {
        if (this.gain == null) {
            this.gain = new ArrayList();
        }
        return this.gain;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Gains read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        Gains _instance = new Gains();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("gain")) {
                    Gain _value = Gain.read(_parser);
                    _instance.getGain().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("Gains is not closed");
        }
        return _instance;
    }
}
