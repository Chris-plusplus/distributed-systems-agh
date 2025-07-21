package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.DeviceLister;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;

import java.util.List;

public class DeviceListerImpl implements DeviceLister {
    private final List<IoTDeviceImpl> devices;

    public  DeviceListerImpl(List<IoTDeviceImpl> devices) {
        this.devices = devices;
    }

    @Override
    public String[] listDevices(boolean withState, Current current) {
        return devices.stream()
                .map(device -> "%s %s/%s%s".formatted(
                        device.typeName(),
                        device.identity.category,
                        device.identity.name,
                        withState ? " (%s)".formatted(device.getState(current)) : ""
                ))
                .toArray(String[]::new);
    }
}
