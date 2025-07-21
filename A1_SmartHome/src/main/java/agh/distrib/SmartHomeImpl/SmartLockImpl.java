package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.CannotLockOpened;
import agh.distrib.SmartHome.DeviceOff;
import agh.distrib.SmartHome.DoorsMoving;
import agh.distrib.SmartHome.SmartLock;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmartLockImpl extends IoTDeviceImpl implements SmartLock {
    private static final Logger log = LogManager.getLogger(SmartLockImpl.class);

    private boolean locked = false;

    public SmartLockImpl(Identity identity) {
        super(identity);
    }

    protected void assureCanLock(Current current) throws CannotLockOpened, DeviceOff, DoorsMoving {}
    protected void assureCanUnlock(Current current) throws DoorsMoving {}

    @Override
    public String typeName() {
        return "SmartLock";
    }

    @Override
    public void lock(Current current) throws DeviceOff, CannotLockOpened, DoorsMoving {
        assureOn(current);
        assureCanLock(current);

        locked = true;
        log.info("{}: locked", identity);
    }

    @Override
    public void unlock(Current current) throws DeviceOff, DoorsMoving {
        assureOn(current);
        assureCanUnlock(current);

        locked = false;
        log.info("{}: unlocked", identity);
    }

    @Override
    public boolean isLocked(Current current) throws DeviceOff {
        assureOn(current);

        return locked;
    }
}
