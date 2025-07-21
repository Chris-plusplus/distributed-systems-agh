package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.DeviceOff;
import agh.distrib.SmartHome.LightOnState;
import agh.distrib.SmartHome.SmartLight;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmartLightImpl extends IoTDeviceImpl implements SmartLight {
    private static final Logger log = LogManager.getLogger(SmartLightImpl.class);

    private LightOnState onState = LightOnState.off;

    public SmartLightImpl(Identity identity) {
        super(identity);
    }

    @Override
    public String typeName() {
        return "SmartLight";
    }

    @Override
    public LightOnState setOn(LightOnState newState, Current current) throws DeviceOff {
        assureOn(current);
        log.info("{}: changed on from {} to {}", identity, onState, newState);
        onState = newState;
        return onState;
    }

    @Override
    public LightOnState getOn(Current current) throws DeviceOff {
        assureOn(current);
        return onState;
    }
}
