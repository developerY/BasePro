package com.ylabz.basepro.core.model.ble.tools

val allGattCharacteristics = AllGattCharacteristics

fun getHumanReadableName(uuid: String): String {
    return allGattCharacteristics.lookup(uuid.trim())
        ?: uuidToDescriptionMap[uuid]
        ?: combinedGattMap[uuid]
        ?: "Unknown Service/Characteristic/Descriptor -- Full Lookup Done ($uuid)"
}

private val uuidToDescriptionMap = mapOf(
    // Generic services
    "00001800-0000-1000-8000-00805f9b34fb" to "Generic Access Service",
    "00001801-0000-1000-8000-00805f9b34fb" to "Generic Attribute Service",

    // Device Information Service (DIS)
    "0000180a-0000-1000-8000-00805f9b34fb" to "Device Information Service",
    "00002a23-0000-1000-8000-00805f9b34fb" to "System ID",
    "00002a24-0000-1000-8000-00805f9b34fb" to "Model Number String",
    "00002a25-0000-1000-8000-00805f9b34fb" to "Serial Number String",
    "00002a26-0000-1000-8000-00805f9b34fb" to "Firmware Revision String",
    "00002a27-0000-1000-8000-00805f9b34fb" to "Hardware Revision String",
    "00002a28-0000-1000-8000-00805f9b34fb" to "Software Revision String",
    "00002a29-0000-1000-8000-00805f9b34fb" to "Manufacturer Name String",
    "00002a2a-0000-1000-8000-00805f9b34fb" to "IEEE 11073-20601 Regulatory Certification Data List",
    "00002a50-0000-1000-8000-00805f9b34fb" to "PNP ID",

    // Battery Service
    "0000180f-0000-1000-8000-00805f9b34fb" to "Battery Service",
    "00002a19-0000-1000-8000-00805f9b34fb" to "Battery Level",

    // TI SensorTag Services
    "f000aa00-0451-4000-b000-000000000000" to "TI SensorTag Temperature Service",
    "f000aa01-0451-4000-b000-000000000000" to "Temperature Data Characteristic",
    "f000aa02-0451-4000-b000-000000000000" to "Temperature Configuration Characteristic",
    "f000aa03-0451-4000-b000-000000000000" to "Temperature Period Characteristic",

    "f000aa20-0451-4000-b000-000000000000" to "TI SensorTag Humidity Service",
    "f000aa21-0451-4000-b000-000000000000" to "Humidity Data Characteristic",
    "f000aa22-0451-4000-b000-000000000000" to "Humidity Configuration Characteristic",
    "f000aa23-0451-4000-b000-000000000000" to "Humidity Period Characteristic",

    "f000aa40-0451-4000-b000-000000000000" to "TI SensorTag Barometer Service",
    "f000aa41-0451-4000-b000-000000000000" to "Barometer Data Characteristic",
    "f000aa42-0451-4000-b000-000000000000" to "Barometer Configuration Characteristic",
    "f000aa44-0451-4000-b000-000000000000" to "Barometer Period Characteristic",

    "f000aa70-0451-4000-b000-000000000000" to "TI SensorTag Gyroscope Service",
    "f000aa71-0451-4000-b000-000000000000" to "Gyroscope Data Characteristic",
    "f000aa72-0451-4000-b000-000000000000" to "Gyroscope Configuration Characteristic",
    "f000aa73-0451-4000-b000-000000000000" to "Gyroscope Period Characteristic",

    "f000aa80-0451-4000-b000-000000000000" to "TI SensorTag Magnetometer Service",
    "f000aa81-0451-4000-b000-000000000000" to "Magnetometer Data Characteristic",
    "f000aa82-0451-4000-b000-000000000000" to "Magnetometer Configuration Characteristic",
    "f000aa83-0451-4000-b000-000000000000" to "Magnetometer Period Characteristic",

    "f000ffc0-0451-4000-b000-000000000000" to "TI SensorTag Connection Control Service",
    "f000ffc1-0451-4000-b000-000000000000" to "Connection Control Data Characteristic",
    "f000ffc2-0451-4000-b000-000000000000" to "Connection Control Configuration Characteristic",
    "f000ffc3-0451-4000-b000-000000000000" to "Connection Control Interval Characteristic",
    "f000ffc4-0451-4000-b000-000000000000" to "Connection Control Latency Characteristic",

    // Generic GATT Services
    "00002a00-0000-1000-8000-00805f9b34fb" to "Device Name",
    "00002a01-0000-1000-8000-00805f9b34fb" to "Appearance",
    "00002a04-0000-1000-8000-00805f9b34fb" to "Peripheral Preferred Connection Parameters",
    "00002a05-0000-1000-8000-00805f9b34fb" to "Service Changed",

    // UART service (common in BLE debugging and custom boards)
    "0000ffe0-0000-1000-8000-00805f9b34fb" to "UART RX/TX Service",
    "0000ffe1-0000-1000-8000-00805f9b34fb" to "UART Data Characteristic",

    // Custom Service Examples
    "f000ac00-0451-4000-b000-000000000000" to "TI SensorTag Accelerometer Service",
    "f000ac01-0451-4000-b000-000000000000" to "Accelerometer Data Characteristic",
    "f000ac02-0451-4000-b000-000000000000" to "Accelerometer Configuration Characteristic",
    "f000ac03-0451-4000-b000-000000000000" to "Accelerometer Period Characteristic",

    "f000ccc0-0451-4000-b000-000000000000" to "TI SensorTag Key Press Service",
    "f000ccc1-0451-4000-b000-000000000000" to "Key Press Data Characteristic",
    "f000ccc2-0451-4000-b000-000000000000" to "Key Press Configuration Characteristic",
    "f000ccc3-0451-4000-b000-000000000000" to "Key Press Notification Characteristic"
)
