# SignalTrackerManager android app

This project meant to be as a remote controller via bluetooth for SignalTracker app

## Build:

- no special build steps, just pull and run

## Manual:

1. Start the app
2. grant all permissions, turn on bluetooth
3. list of all paired bluetooth devices should be displayed
4. firstly tap connect to device
5. after a successful connect to a device with SignalTracker app you can see updating status
6. put signal tracker in active track screen
7. now you should be able to start and stop signal tracking on signal tracker app 

## TODO:

1. Check if bluetooth is enabled first and check for devices on if it is and show on screen problem when it is not enabled
2. Display only paired devices with running signal tracker app installed and running
3. Visualize loading states (connecting, starting, stopping)
4. Pull to refresh of bluetooth devices

## Potential limitation:

1. Concurrent management of multiple devices (max. 4 for bluetooth v4, max. 7 for bluetooth v5) but for Bluetooth LE there is no such a limitation, also android should had a limit to 10 (need to do more research on this) https://stackoverflow.com/questions/39163772/maximum-number-of-ble-sensors-that-could-be-connected-to-a-ble-gateway-at-a-give/39174115#39174115


## Values

- **Connected/Not connected** - if bluetooth connection is active with the device with SignalTracker app
- **Updated** - Local Date and time of the update received from SignalTracker app if connected or from bluetooth adapter if not connected
- **status** 
  - **UNKNOWN** - unknown status as we have no info from the SignalTracker app, 
  - **NOT_ACTIVE** - SignalTracker app is not in active track screen, so it is not possible to start tracking 
  - **IDLE** - Signal tracker app is in active track screen waiting for commands, tracking is not running, 
  - **RUNNING** - Signal tracker app is in active track screen waiting for commands, tracking is running,

## Testing scenarios

