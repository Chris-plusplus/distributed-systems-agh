#pragma once
// ^- include guard

["java:package:agh.distrib"]
module SmartHome {
    enum DeviceState {
        on,
        off
    };

    exception DeviceOff{};

    interface IoTDevice {
        idempotent DeviceState getState();
        idempotent DeviceState setState(DeviceState newState);

        void assureOn() throws DeviceOff;
    };


    enum DoorState {
        opening,
        opened,
        closing,
        closed
    };

    exception CannotLockOpened{};
    exception DoorsMoving{};
    interface SmartLock extends IoTDevice {
        void lock() throws DeviceOff, CannotLockOpened, DoorsMoving;
        void unlock() throws DeviceOff, DoorsMoving;
        idempotent bool isLocked() throws DeviceOff;
    };

    exception DoorsLocked{};
    interface SmartDoor extends SmartLock {
        void open() throws DeviceOff, DoorsMoving, DoorsLocked;
        void unlockOpen() throws DeviceOff, DoorsMoving;
        void close() throws DeviceOff, DoorsMoving;
        void closeLock() throws DeviceOff, DoorsMoving;
        bool isOpen() throws DeviceOff;
    };


    enum LightOnState {
        on,
        off
    };

    interface SmartLight extends IoTDevice {
        idempotent LightOnState setOn(LightOnState newState) throws DeviceOff;
        idempotent LightOnState getOn() throws DeviceOff;
    };

    interface AdjustableSmartLight extends SmartLight {
        idempotent float setBrightness(float newBrightness) throws DeviceOff;
        idempotent float getBrightness() throws DeviceOff;
    };

    struct ColorRGB {
        float r;
        float g;
        float b;
    };
    interface RGBSmartLight extends SmartLight {
        idempotent ColorRGB getColor() throws DeviceOff;
        idempotent ColorRGB setColor(ColorRGB color) throws DeviceOff;
    };

    sequence<string> DeviceList;
    interface DeviceLister {
        idempotent DeviceList listDevices(bool withState);
    };
}