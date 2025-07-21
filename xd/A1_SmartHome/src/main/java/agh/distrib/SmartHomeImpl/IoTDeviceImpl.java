package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.DeviceOff;
import agh.distrib.SmartHome.DeviceState;
import agh.distrib.SmartHome.IoTDevice;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IoTDeviceImpl implements IoTDevice {
    private static final Logger log = LogManager.getLogger(IoTDeviceImpl.class);

    private DeviceState deviceState = DeviceState.off;
    protected Identity identity;

    public IoTDeviceImpl(Identity identity) {
        this.identity = identity;
    }

    public Identity getIdentity() {
        return identity;
    }

    public abstract String typeName();

    @Override
    public DeviceState getState(Current current) {
        return deviceState;
    }

    @Override
    public DeviceState setState(DeviceState newState, Current current) {
        log.info("{}: changed state from {} to {}", identity, deviceState, newState);
        deviceState = newState;
        return deviceState;
    }

    @Override
    public void assureOn(Current current) throws DeviceOff {
        if(deviceState != DeviceState.on) {
            throw new DeviceOff();
        }
    }
}
