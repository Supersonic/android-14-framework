package com.android.server.display.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DisplayConfiguration {
    public Thresholds ambientBrightnessChangeThresholds;
    public Thresholds ambientBrightnessChangeThresholdsIdle;
    public BigInteger ambientLightHorizonLong;
    public BigInteger ambientLightHorizonShort;
    public AutoBrightness autoBrightness;
    public DensityMapping densityMapping;
    public Thresholds displayBrightnessChangeThresholds;
    public Thresholds displayBrightnessChangeThresholdsIdle;
    public HighBrightnessMode highBrightnessMode;
    public SensorDetails lightSensor;
    public String name;
    public SensorDetails proxSensor;
    public DisplayQuirks quirks;
    public RefreshRateConfigs refreshRate;
    public BigDecimal screenBrightnessDefault;
    public NitsMap screenBrightnessMap;
    public BigInteger screenBrightnessRampDecreaseMaxMillis;
    public BigDecimal screenBrightnessRampFastDecrease;
    public BigDecimal screenBrightnessRampFastIncrease;
    public BigInteger screenBrightnessRampIncreaseMaxMillis;
    public BigDecimal screenBrightnessRampSlowDecrease;
    public BigDecimal screenBrightnessRampSlowIncrease;
    public SensorDetails screenOffBrightnessSensor;
    public IntegerArray screenOffBrightnessSensorValueToLux;
    public ThermalThrottling thermalThrottling;
    public UsiVersion usiVersion;

    public final String getName() {
        return this.name;
    }

    public final void setName(String str) {
        this.name = str;
    }

    public final DensityMapping getDensityMapping() {
        return this.densityMapping;
    }

    public final void setDensityMapping(DensityMapping densityMapping) {
        this.densityMapping = densityMapping;
    }

    public final NitsMap getScreenBrightnessMap() {
        return this.screenBrightnessMap;
    }

    public final void setScreenBrightnessMap(NitsMap nitsMap) {
        this.screenBrightnessMap = nitsMap;
    }

    public final BigDecimal getScreenBrightnessDefault() {
        return this.screenBrightnessDefault;
    }

    public final void setScreenBrightnessDefault(BigDecimal bigDecimal) {
        this.screenBrightnessDefault = bigDecimal;
    }

    public final ThermalThrottling getThermalThrottling() {
        return this.thermalThrottling;
    }

    public final void setThermalThrottling(ThermalThrottling thermalThrottling) {
        this.thermalThrottling = thermalThrottling;
    }

    public HighBrightnessMode getHighBrightnessMode() {
        return this.highBrightnessMode;
    }

    public void setHighBrightnessMode(HighBrightnessMode highBrightnessMode) {
        this.highBrightnessMode = highBrightnessMode;
    }

    public DisplayQuirks getQuirks() {
        return this.quirks;
    }

    public void setQuirks(DisplayQuirks displayQuirks) {
        this.quirks = displayQuirks;
    }

    public AutoBrightness getAutoBrightness() {
        return this.autoBrightness;
    }

    public void setAutoBrightness(AutoBrightness autoBrightness) {
        this.autoBrightness = autoBrightness;
    }

    public RefreshRateConfigs getRefreshRate() {
        return this.refreshRate;
    }

    public void setRefreshRate(RefreshRateConfigs refreshRateConfigs) {
        this.refreshRate = refreshRateConfigs;
    }

    public final BigDecimal getScreenBrightnessRampFastDecrease() {
        return this.screenBrightnessRampFastDecrease;
    }

    public final void setScreenBrightnessRampFastDecrease(BigDecimal bigDecimal) {
        this.screenBrightnessRampFastDecrease = bigDecimal;
    }

    public final BigDecimal getScreenBrightnessRampFastIncrease() {
        return this.screenBrightnessRampFastIncrease;
    }

    public final void setScreenBrightnessRampFastIncrease(BigDecimal bigDecimal) {
        this.screenBrightnessRampFastIncrease = bigDecimal;
    }

    public final BigDecimal getScreenBrightnessRampSlowDecrease() {
        return this.screenBrightnessRampSlowDecrease;
    }

    public final void setScreenBrightnessRampSlowDecrease(BigDecimal bigDecimal) {
        this.screenBrightnessRampSlowDecrease = bigDecimal;
    }

    public final BigDecimal getScreenBrightnessRampSlowIncrease() {
        return this.screenBrightnessRampSlowIncrease;
    }

    public final void setScreenBrightnessRampSlowIncrease(BigDecimal bigDecimal) {
        this.screenBrightnessRampSlowIncrease = bigDecimal;
    }

    public final BigInteger getScreenBrightnessRampIncreaseMaxMillis() {
        return this.screenBrightnessRampIncreaseMaxMillis;
    }

    public final void setScreenBrightnessRampIncreaseMaxMillis(BigInteger bigInteger) {
        this.screenBrightnessRampIncreaseMaxMillis = bigInteger;
    }

    public final BigInteger getScreenBrightnessRampDecreaseMaxMillis() {
        return this.screenBrightnessRampDecreaseMaxMillis;
    }

    public final void setScreenBrightnessRampDecreaseMaxMillis(BigInteger bigInteger) {
        this.screenBrightnessRampDecreaseMaxMillis = bigInteger;
    }

    public final SensorDetails getLightSensor() {
        return this.lightSensor;
    }

    public final void setLightSensor(SensorDetails sensorDetails) {
        this.lightSensor = sensorDetails;
    }

    public final SensorDetails getScreenOffBrightnessSensor() {
        return this.screenOffBrightnessSensor;
    }

    public final void setScreenOffBrightnessSensor(SensorDetails sensorDetails) {
        this.screenOffBrightnessSensor = sensorDetails;
    }

    public final SensorDetails getProxSensor() {
        return this.proxSensor;
    }

    public final void setProxSensor(SensorDetails sensorDetails) {
        this.proxSensor = sensorDetails;
    }

    public final BigInteger getAmbientLightHorizonLong() {
        return this.ambientLightHorizonLong;
    }

    public final void setAmbientLightHorizonLong(BigInteger bigInteger) {
        this.ambientLightHorizonLong = bigInteger;
    }

    public final BigInteger getAmbientLightHorizonShort() {
        return this.ambientLightHorizonShort;
    }

    public final void setAmbientLightHorizonShort(BigInteger bigInteger) {
        this.ambientLightHorizonShort = bigInteger;
    }

    public final Thresholds getDisplayBrightnessChangeThresholds() {
        return this.displayBrightnessChangeThresholds;
    }

    public final void setDisplayBrightnessChangeThresholds(Thresholds thresholds) {
        this.displayBrightnessChangeThresholds = thresholds;
    }

    public final Thresholds getAmbientBrightnessChangeThresholds() {
        return this.ambientBrightnessChangeThresholds;
    }

    public final void setAmbientBrightnessChangeThresholds(Thresholds thresholds) {
        this.ambientBrightnessChangeThresholds = thresholds;
    }

    public final Thresholds getDisplayBrightnessChangeThresholdsIdle() {
        return this.displayBrightnessChangeThresholdsIdle;
    }

    public final void setDisplayBrightnessChangeThresholdsIdle(Thresholds thresholds) {
        this.displayBrightnessChangeThresholdsIdle = thresholds;
    }

    public final Thresholds getAmbientBrightnessChangeThresholdsIdle() {
        return this.ambientBrightnessChangeThresholdsIdle;
    }

    public final void setAmbientBrightnessChangeThresholdsIdle(Thresholds thresholds) {
        this.ambientBrightnessChangeThresholdsIdle = thresholds;
    }

    public final IntegerArray getScreenOffBrightnessSensorValueToLux() {
        return this.screenOffBrightnessSensorValueToLux;
    }

    public final void setScreenOffBrightnessSensorValueToLux(IntegerArray integerArray) {
        this.screenOffBrightnessSensorValueToLux = integerArray;
    }

    public final UsiVersion getUsiVersion() {
        return this.usiVersion;
    }

    public final void setUsiVersion(UsiVersion usiVersion) {
        this.usiVersion = usiVersion;
    }

    public static DisplayConfiguration read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        DisplayConfiguration displayConfiguration = new DisplayConfiguration();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("name")) {
                    displayConfiguration.setName(XmlParser.readText(xmlPullParser));
                } else if (name.equals("densityMapping")) {
                    displayConfiguration.setDensityMapping(DensityMapping.read(xmlPullParser));
                } else if (name.equals("screenBrightnessMap")) {
                    displayConfiguration.setScreenBrightnessMap(NitsMap.read(xmlPullParser));
                } else if (name.equals("screenBrightnessDefault")) {
                    displayConfiguration.setScreenBrightnessDefault(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("thermalThrottling")) {
                    displayConfiguration.setThermalThrottling(ThermalThrottling.read(xmlPullParser));
                } else if (name.equals("highBrightnessMode")) {
                    displayConfiguration.setHighBrightnessMode(HighBrightnessMode.read(xmlPullParser));
                } else if (name.equals("quirks")) {
                    displayConfiguration.setQuirks(DisplayQuirks.read(xmlPullParser));
                } else if (name.equals("autoBrightness")) {
                    displayConfiguration.setAutoBrightness(AutoBrightness.read(xmlPullParser));
                } else if (name.equals("refreshRate")) {
                    displayConfiguration.setRefreshRate(RefreshRateConfigs.read(xmlPullParser));
                } else if (name.equals("screenBrightnessRampFastDecrease")) {
                    displayConfiguration.setScreenBrightnessRampFastDecrease(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("screenBrightnessRampFastIncrease")) {
                    displayConfiguration.setScreenBrightnessRampFastIncrease(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("screenBrightnessRampSlowDecrease")) {
                    displayConfiguration.setScreenBrightnessRampSlowDecrease(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("screenBrightnessRampSlowIncrease")) {
                    displayConfiguration.setScreenBrightnessRampSlowIncrease(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("screenBrightnessRampIncreaseMaxMillis")) {
                    displayConfiguration.setScreenBrightnessRampIncreaseMaxMillis(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("screenBrightnessRampDecreaseMaxMillis")) {
                    displayConfiguration.setScreenBrightnessRampDecreaseMaxMillis(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("lightSensor")) {
                    displayConfiguration.setLightSensor(SensorDetails.read(xmlPullParser));
                } else if (name.equals("screenOffBrightnessSensor")) {
                    displayConfiguration.setScreenOffBrightnessSensor(SensorDetails.read(xmlPullParser));
                } else if (name.equals("proxSensor")) {
                    displayConfiguration.setProxSensor(SensorDetails.read(xmlPullParser));
                } else if (name.equals("ambientLightHorizonLong")) {
                    displayConfiguration.setAmbientLightHorizonLong(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("ambientLightHorizonShort")) {
                    displayConfiguration.setAmbientLightHorizonShort(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("displayBrightnessChangeThresholds")) {
                    displayConfiguration.setDisplayBrightnessChangeThresholds(Thresholds.read(xmlPullParser));
                } else if (name.equals("ambientBrightnessChangeThresholds")) {
                    displayConfiguration.setAmbientBrightnessChangeThresholds(Thresholds.read(xmlPullParser));
                } else if (name.equals("displayBrightnessChangeThresholdsIdle")) {
                    displayConfiguration.setDisplayBrightnessChangeThresholdsIdle(Thresholds.read(xmlPullParser));
                } else if (name.equals("ambientBrightnessChangeThresholdsIdle")) {
                    displayConfiguration.setAmbientBrightnessChangeThresholdsIdle(Thresholds.read(xmlPullParser));
                } else if (name.equals("screenOffBrightnessSensorValueToLux")) {
                    displayConfiguration.setScreenOffBrightnessSensorValueToLux(IntegerArray.read(xmlPullParser));
                } else if (name.equals("usiVersion")) {
                    displayConfiguration.setUsiVersion(UsiVersion.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return displayConfiguration;
        }
        throw new DatatypeConfigurationException("DisplayConfiguration is not closed");
    }
}
