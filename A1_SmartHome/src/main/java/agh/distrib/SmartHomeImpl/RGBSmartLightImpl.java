package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.ColorRGB;
import agh.distrib.SmartHome.DeviceOff;
import agh.distrib.SmartHome.RGBSmartLight;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RGBSmartLightImpl extends SmartLightImpl implements RGBSmartLight {
    private static final Logger log = LogManager.getLogger(RGBSmartLightImpl.class);

    private ColorRGB color = new ColorRGB(1.f, 1.f, 1.f);

    public RGBSmartLightImpl(Identity identity) {
        super(identity);
    }

    @Override
    public String typeName() {
        return "RGBSmartLight";
    }

    @Override
    public ColorRGB getColor(Current current) throws DeviceOff {
        assureOn(current);
        return color;
    }

    @Override
    public ColorRGB setColor(ColorRGB color, Current current) throws DeviceOff {
        assureOn(current);
        if(color.r < 0 || color.r > 1){
            float newR = Math.clamp(color.r, 0, 1);
            log.warn("{}: clamped R color from {} to {}", identity, color.r, newR);
            color.r = newR;
        }
        if(color.g < 0 || color.g > 1){
            float newG = Math.clamp(color.g, 0, 1);
            log.warn("{}: clamped G color from {} to {}", identity, color.g, newG);
            color.g = newG;
        }
        if(color.b < 0 || color.b > 1){
            float newB = Math.clamp(color.b, 0, 1);
            log.warn("{}: clamped B color from {} to {}", identity, color.b, newB);
            color.b = newB;
        }
        log.info(
                "{}: changed color from {} to {}",
                identity,
                "(R = %f, G = %f, B = %f)".formatted(this.color.r, this.color.g,this.color.b),
                "(R = %f, G = %f, B = %f)".formatted(color.r, color.g, color.b)
        );
        this.color = color;
        return color;
    }
}
