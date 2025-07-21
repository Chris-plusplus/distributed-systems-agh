package agh.distrib.SmartHomeImpl;

import agh.distrib.SmartHome.*;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class SmartDoorImpl extends SmartLockImpl implements SmartDoor {
    private static final Logger log = LogManager.getLogger(SmartDoorImpl.class);

    private DoorState doorState = DoorState.closed;

    private static final int DOORS_OPEN_TIME_MS = 3000;
    private static final int DOORS_CLOSE_TIME_MS = 3000;

    public SmartDoorImpl(Identity identity) {
        super(identity);
    }

    @Override
    protected void assureCanLock(Current current) throws CannotLockOpened, DeviceOff, DoorsMoving {
        if(isOpen(current)) {
            throw new CannotLockOpened();
        }
        else if(doorState == DoorState.opening || doorState == DoorState.closing) {
            throw new DoorsMoving();
        }
    }

    @Override
    protected void assureCanUnlock(Current current) throws DoorsMoving {
        if(doorState == DoorState.opening || doorState == DoorState.closing) {
            throw new DoorsMoving();
        }
    }

    @Override
    public String typeName() {
        return "SmartDoor";
    }

    @Override
    public void open(Current current) throws DeviceOff, DoorsLocked, DoorsMoving {
        assureOn(current);
        if(isLocked(current)){
            throw new DoorsLocked();
        }

        if(doorState == DoorState.opening || doorState == DoorState.closing){
            throw new DoorsMoving();
        }
        else if(doorState == DoorState.opened){}
        else if(doorState == DoorState.closed){
            doorState = DoorState.opening;
            new Timer().schedule(new TimerTask() {
                public void run() {
                    log.info("{}: opened door", identity);
                    doorState = DoorState.opened;
                }
            }, DOORS_OPEN_TIME_MS);
        }
    }

    @Override
    public void unlockOpen(Current current) throws DeviceOff, DoorsMoving {
        unlock(current);
        try {
            open(current);
        }
        catch (DoorsLocked ignored){}
    }

    private void _close(Current current, Runnable afterClose) throws DeviceOff, DoorsMoving {
        assureOn(current);

        if(doorState == DoorState.closing || doorState == DoorState.opening){
            throw new DoorsMoving();
        }
        else if(doorState == DoorState.closed){}
        else if(doorState == DoorState.opened){
            doorState = DoorState.closing;
            new Timer().schedule(new TimerTask() {
                public void run() {
                    log.info("{}: closed door", identity);
                    doorState = DoorState.closed;
                    afterClose.run();
                }
            }, DOORS_CLOSE_TIME_MS);
        }
    }

    @Override
    public void close(Current current) throws DeviceOff, DoorsMoving {
        _close(current, ()->{});
    }

    @Override
    public void closeLock(Current current) throws DeviceOff, DoorsMoving {
        _close(current, () -> {
            try{
                lock(current);
            }
            catch (DeviceOff | DoorsMoving | CannotLockOpened ignored){}
        });
    }

    @Override
    public boolean isOpen(Current current) throws DeviceOff {
        assureOn(current);
        return doorState == DoorState.opened;
    }
}
