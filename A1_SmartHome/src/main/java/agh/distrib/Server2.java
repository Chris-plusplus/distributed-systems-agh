package agh.distrib;

import agh.distrib.SmartHomeImpl.*;
import com.zeroc.Ice.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;

public class Server2 {
    private static final Logger log = LogManager.getLogger(Server2.class);

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.INFO);

        int id = 2;
        int port = 10000 + id;

        // List<Identity> identities = new ArrayList<Identity>();
        List<IoTDeviceImpl> devices = new ArrayList<>();
        int status = 0;
        Communicator communicator = null;

        try {
            Properties properties = Util.createProperties();
            properties.setProperty("adapter.ThreadPool.Size", "10");

            InitializationData initializationData = new InitializationData();
            initializationData.properties = properties;

            communicator = Util.initialize(initializationData);

            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                    "adapter",
                    "tcp -h 127.0.0.1 -p %d -z -t 10000 : udp -h 127.0.0.1 -p %d -z".formatted(port, port)
            );

            // devices.add(new SmartDoorImpl(new Identity("garageDoor", "doors")));
            // devices.add(new SmartLockImpl(new Identity("frontDoorLock", "locks")));
            //  devices.add(new SmartLockImpl(new Identity("backDoorLock", "locks")));
            devices.add(new SmartLightImpl(new Identity("kitchenLight", "lights")));
            devices.add(new AdjustableSmartLightImpl(new Identity("livingRoomLight", "lightsAdj")));
            devices.add(new AdjustableSmartLightImpl(new Identity("bathroomLight", "lightsAdj")));
            devices.add(new RGBSmartLightImpl(new Identity("bedroomLight", "lightsRGB")));

            for (var device : devices) {
                adapter.add(device, device.getIdentity());
                log.info("Created {} {}/{}", device.typeName(), device.getIdentity().category, device.getIdentity().name);
            }

            adapter.add(new DeviceListerImpl(devices), new Identity("deviceLister", "deviceLister"));

            adapter.activate();
            log.info("Entering main loop");
            communicator.waitForShutdown();
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            status = 1;
        }
        if(communicator != null) {
            try{
                communicator.destroy();
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        }
        System.exit(status);
    }
}