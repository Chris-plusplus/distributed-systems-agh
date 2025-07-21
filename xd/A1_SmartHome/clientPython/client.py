import sys
import Ice
from smarthome_ice import *
from SmartHome import *

IoTMap = {
    'doors': SmartDoorPrx,
    'locks': SmartLockPrx,
    'lights': SmartLightPrx,
    'lightsAdj': AdjustableSmartLightPrx,
    'lightsRGB': RGBSmartLightPrx
}

def colorShowCase(color: ColorRGB):
    print(f'\033[48;2;{int(255*color.r)};{int(255*color.g)};{int(255*color.b)}m  \033[0m')

with Ice.initialize(sys.argv) as communicator:
    serverID = None
    objectID = None
    objectCat = None
    objectName = None
    objBase = None
    obj = None
    objExists = False
    # objExistsFirst = True
    connected = False
    # connectedFirst = True

    while not connected:
        devLister1Base = communicator.stringToProxy(f"deviceLister/deviceLister:tcp -h 127.0.0.1 -p {10000 + 1} -z : udp -h 127.0.0.1 -p {10000 + 1} -z")
        devLister2Base = communicator.stringToProxy(f"deviceLister/deviceLister:tcp -h 127.0.0.1 -p {10000 + 2} -z : udp -h 127.0.0.1 -p {10000 + 2} -z")

        deviceMap = {}

        try:
            devLister1 = DeviceListerPrx.checkedCast(devLister1Base)
            devLister2 = DeviceListerPrx.checkedCast(devLister2Base)
            assert isinstance(devLister1, DeviceListerPrx)
            assert isinstance(devLister2, DeviceListerPrx)
            print("Device list:")
            for d in devLister1.listDevices(False):
                d = d.split()
                print(f"- {d[0]} {d[1]}")
                deviceMap[d[1]] = 1
            for d in devLister2.listDevices(False):
                d = d.split()
                print(f"- {d[0]} {d[1]}")
                deviceMap[d[1]] = 2
        except Ice.ObjectNotExistException:
            print("Server cannot list devices")
        except Exception as e:
            print(e)
            print("Failed to connect")
            continue

        while not objExists:
            objectID = input("Object ID (category/name): ")
            objectCat, objectName = tuple(objectID.split('/'))
            while objectCat not in IoTMap:
                print("unsupported object category")
                objectID = input("Object ID (category/name): ")
                objectCat, objectName = tuple(objectID.split('/'))

            objBase = communicator.stringToProxy(f"{objectID}:tcp -h 127.0.0.1 -p {10000 + deviceMap[objectID]} -z : udp -h 127.0.0.1 -p {10000 + deviceMap[objectID]} -z")

            objExists = True
            connected = True
            try:
                obj = IoTMap[objectCat].checkedCast(objBase)
            except Ice.ObjectNotExistException:
                print("Object does not exist")
                objExists = False
            except:
                print("Failed to connect")
                objExists = False
                connected = False
                break

            reconnect = False
            while connected and objExists:
                prompt = input('> ').split()
                if len(prompt) == 0:
                    continue
                cmd = prompt[0]

                try:
                    cmdHandled = False
                    if cmd == 'listDevices':
                        cmdHandled = True
                        withState = False
                        if len(prompt) - 1 >= 1 and prompt[1].lower() == 'true':
                            withState = True
                        
                        print("Device list:")
                        deviceMap = {}
                        for d in devLister1.listDevices(False):
                            d = d.split()
                            print(f"- {d[0]} {d[1]}")
                            deviceMap[d[1]] = 1
                        for d in devLister2.listDevices(False):
                            d = d.split()
                            print(f"- {d[0]} {d[1]}")
                            deviceMap[d[1]] = 2
                        continue

                    if isinstance(obj, RGBSmartLightPrx):
                        if cmd == 'getColor':
                            cmdHandled = True
                            color = obj.getColor()
                            print(f'(R = {color.r}, G = {color.g}, B = {color.b})', end=' ')
                            colorShowCase(color)
                        elif cmd == 'setColor':
                            cmdHandled = True
                            if len(prompt) - 1 < 3:
                                print("usage: setColor <R> <G> <B>")
                                continue
                            try:
                                r = float(prompt[1])
                                g = float(prompt[2])
                                b = float(prompt[3])

                                color = obj.setColor(ColorRGB(r, g, b))
                                print(f'(R = {color.r}, G = {color.g}, B = {color.b})', end=' ')
                                colorShowCase(color)
                            except ValueError:
                                print('error converting rgb value')
                                continue
                        elif cmd == 'help':
                            cmdHandled = True
                            print('getColor - obtains current color')
                            print('setColor <R> <G> <B> - sets color, in range [0, 1]')
                    if isinstance(obj, AdjustableSmartLightPrx):
                        if cmd == 'getBrightness':
                            cmdHandled = True
                            brightness = obj.getBrightness()
                            print(brightness, end=' ')
                            colorShowCase(ColorRGB(brightness, brightness, brightness))
                        elif cmd == 'setBrightness':
                            cmdHandled = True
                            if len(prompt) - 1 < 1:
                                print("usage: setBrightness <brightness>")
                                continue
                            try:
                                brightness = float(prompt[1])

                                brightness = obj.setBrightness(brightness)
                                print(brightness, end=' ')
                                colorShowCase(ColorRGB(brightness, brightness, brightness))
                            except ValueError:
                                print('error converting brightness value')
                                continue
                        elif cmd == 'help':
                            cmdHandled = True
                            print('getBrightness - obtains brightness')
                            print('setBrightness <new brightness> - sets brightness')
                    if isinstance(obj, SmartLightPrx):
                        if cmd == 'getOn':
                            cmdHandled = True
                            print(obj.getOn())
                        elif cmd == 'setOn':
                            cmdHandled = True
                            if len(prompt) - 1 < 1:
                                print("usage: setOn <on/off>")
                                continue

                            isOn = LightOnState.on if prompt[1] == 'on' else (LightOnState.off if prompt[1] == 'off' else None)
                            if isOn == None:
                                print("usage: setOn <on/off>")
                                continue

                            obj.setOn(isOn)
                        elif cmd == 'help':
                            cmdHandled = True
                            print('getOn - obtains if light is turned on')
                            print('setOn <on/off> - sets if light is turned on')
                    if isinstance(obj, SmartDoorPrx):
                        if cmd == 'open':
                            cmdHandled = True
                            obj.open()
                        elif cmd == 'unlockOpen':
                            cmdHandled = True
                            obj.unlockOpen()
                        elif cmd == 'close':
                            cmdHandled = True
                            obj.close()
                        elif cmd == 'closeLock':
                            cmdHandled = True
                            obj.closeLock()
                        elif cmd == 'isOpen':
                            cmdHandled = True
                            print(obj.isOpen())
                        elif cmd == 'help':
                            cmdHandled = True
                            print('open - opens door')
                            print('unlockOpen - unlocks and opens door')
                            print('close - closes door')
                            print('closeLock - closes and locks door')
                    if isinstance(obj, SmartLockPrx):
                        if cmd == 'lock':
                            cmdHandled = True
                            obj.lock()
                        elif cmd == 'unlock':
                            cmdHandled = True
                            obj.unlock()
                        elif cmd == 'isLocked':
                            cmdHandled = True
                            print(obj.isLocked())
                        elif cmd == 'help':
                            cmdHandled = True
                            print(f'lock - locks {'door' if isinstance(obj, SmartDoorPrx) else 'lock'}')
                            print(f'unlock - unlocks {'door' if isinstance(obj, SmartDoorPrx) else 'lock'}')
                            print(f'isLocked - checks if {'door' if isinstance(obj, SmartDoorPrx) else 'lock'} is locked')
                    if isinstance(obj, IoTDevicePrx):
                        if cmd == 'getState':
                            cmdHandled = True
                            print(obj.getState())
                        elif cmd == 'setState':
                            cmdHandled = True
                            if len(prompt) - 1 < 1:
                                print("usage: setState <on/off>")
                                continue

                            isOn = DeviceState.on if prompt[1] == 'on' else (DeviceState.off if prompt[1] == 'off' else None)
                            if isOn == None:
                                print("usage: setState <on/off>")
                                continue

                            obj.setState(isOn)
                        elif cmd == 'help':
                            cmdHandled = True
                            print('getState - obtains state')
                            print('setState <on/off> - sets state')
                    if cmd == 'otherDevice':
                        cmdHandled = True
                        objExists = False
                    elif cmd == 'help':
                        cmdHandled = True
                        print('listDevices <state = false> - lists devices')
                        print('otherDevice - allows to control other device')
                    if not cmdHandled:
                        print("unknown command")
                    # elif cmd == 'otherServer':
                    #     objExists = False
                    #     connected = False
                    #     reconnect = True
                except DeviceOff:
                    print("device is off, use 'setState on'")
                except CannotLockOpened:
                    print("cannot lock opened doors")
                except DoorsLocked:
                    print("cannot open locked doors")
                except DoorsMoving:
                    print("cannot perform while doors are moving")
            if reconnect:
                break