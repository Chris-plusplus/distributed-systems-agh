package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.AdjustableSmartLight;
import agh.distrib.SmartHome.DeviceOff;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdjustableSmartLightImpl extends SmartLightImpl implements AdjustableSmartLight {
    private static final Logger log = LogManager.getLogger(AdjustableSmartLightImpl.class);

    private float brightness = 1.f;

    public AdjustableSmartLightImpl(Identity identity) {
        super(identity);
    }

    @Override
    public String typeName() {
        return "AdjustableSmartLight";
    }

    @Override
    public float setBrightness(float newBrightness, Current current) throws DeviceOff {
        assureOn(current);
        if(newBrightness < 0.f || newBrightness > 1.f) {
            float oldNewBrightness = newBrightness;
            newBrightness = Math.clamp(newBrightness, 0.f, 1.f);
            log.warn("{}: newBrightness {} clamped to {}", identity, oldNewBrightness, newBrightness);
        }
        log.info("{}: changed brightness from {} to {}", identity, brightness, newBrightness);
        brightness = newBrightness;
        return brightness;
    }

    @Override
    public float getBrightness(Current current) throws DeviceOff {
        assureOn(current);
        return brightness;
    }
}
