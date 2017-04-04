package vn.com.tma.idlesmart;

public class Faults {
    public static final int FAULT_NONE = 0;
    public static final int FAULT_NEUTRAL_FAULT = 1;
    public static final int FAULT_PARKING_BRAKE_FAULT = 2;
    public static final int FAULT_HOOD_OPEN_FAULT = 3;
    public static final int FAULT_LOW_BATTERY_VOLTAGE_FAULT = 4;
    public static final int FAULT_OIL_PRESSURE_FAULT = 5;
    public static final int FAULT_IGNITION_ON_FAULT = 6;
    public static final int FAULT_START_ABORTED_FAULT = 7;
    public static final int FAULT_AMBIENT_TEMP_SENSOR_FAULT = 8;
    public static final int FAULT_CABIN_TEMP_SENSOR_FAULT = 9;
    public static final int FAULT_COOLANT_SENSOR_OUT_FAULT = 10;
    public static final int FAULT_ENGINE_RPM_FAULT = 11;
    public static final int FAULT_RAPID_TEMP_CHANGE_FAULT = 12;
    public static final int FAULT_NO_TEMP_CHANGE_FAULT = 13;
    public static final int FAULT_LOW_OIL_PRESSURE_FAULT = 14;
    public static final int FAULT_HIGH_COOLANT_TEMP_FAULT = 15;
    public static final int FAULT_GATEWAY_NOT_CONNECTED_FAULT = 16;
    public static final int FAULT_CAN_BUS_NOT_CONNECTED_FAULT = 17;
    public static final int FAULT_LOW_ENGINE_RPM_FAULT = 18;

    public static final int FAULT_PROTOCOL_LEVEL_FAULT = 20;
    public static final int FAULT_HARDWARE_LEVEL_FAULT = 21;
    public static final int FAULT_TABLET_NOT_CONNECTED_FAULT = 22;
    public static final int FAULT_DPS_REGEN_FAULT = 23;

    public static final int FAULT_MAX = 23;

    public static final boolean Automatic = false;
    public static final boolean Manual = true;

    public String[] aFaultDesc;
    public boolean[] aFaultIntervention;
    public String[] aFaultMessage;

    public Faults() {
        this.aFaultMessage = new String[24];
        this.aFaultDesc = new String[24];
        this.aFaultIntervention = new boolean[24];
        initializeFaultCodes();
    }

    private void initFault(int fault, String msg, String desc, boolean intervention) {
        this.aFaultMessage[fault] = msg;
        this.aFaultDesc[fault] = desc;
        this.aFaultIntervention[fault] = intervention;
    }

    public void initializeFaultCodes() {
        initFault(FAULT_NEUTRAL_FAULT, "Vehicle is not in Neutral", "For your safety, please place your vehicle in Neutral and Idle Smart will resume functioning.", Automatic);
        initFault(FAULT_PARKING_BRAKE_FAULT, "Parking Brake is Disengaged", "For your safety, please engage your Parking Brake and Idle Smart will resume functioning.", Manual);
        initFault(FAULT_HOOD_OPEN_FAULT, "Hood Switch Error", "The hood of your vehicle is not closed properly.  Please completely close the hood and Idle Smart will resume functioning.", Automatic);
        initFault(FAULT_LOW_BATTERY_VOLTAGE_FAULT, "Battery Voltage is Low", "Your vehicle`s battery voltage is below 11.0V.  For the protection of your battery, Idle Smart will not resume functioning until voltage exceeds 10.9V.", Manual);
        initFault(FAULT_OIL_PRESSURE_FAULT, "Error - Oil Pressure Detected", "Your engine oil pressure is above 2 PSI in Standby Mode.  Double check to make sure your vehicle's engine is turned Off.", Manual);
        initFault(FAULT_IGNITION_ON_FAULT, "Ignition Switch is On", "Your ignition switch is On.  Turn your ignition switch Off and Idle Smart will resume functioning.", Automatic);
        initFault(FAULT_START_ABORTED_FAULT, "Engine Start Aborted", "The engine has not reached 400 RPMs and/or your Oil PSI has not exceeded 15 PSI after 3 attempts.  For the protection of your vehicle, Idle Smart will not function below these levels.", Manual);
        initFault(FAULT_AMBIENT_TEMP_SENSOR_FAULT, "Outside Temperature Sensor Error", "The outside air temperature sensor is not registering a correct reading.  Check to see if the sensor is connected properly.", Automatic);
        initFault(FAULT_CABIN_TEMP_SENSOR_FAULT, "Cabin Temperature Sensor Error", "The cabin air temperature sensor is not registering a correct reading.  Check to see if the sensor is connected properly.", Automatic);
        initFault(FAULT_COOLANT_SENSOR_OUT_FAULT, "Engine Coolant Sensor Error", "The engine coolant sensor is not registering a proper value.", Manual);
        initFault(FAULT_ENGINE_RPM_FAULT, "Error - Engine RPMs Detected", "Engine RPMs are above 2 RPMs in Standby Mode.  Double check to make sure your vehicle's engine is turned Off.", Manual);
        initFault(FAULT_RAPID_TEMP_CHANGE_FAULT, "Rapid Temperature Change", "The cabin or outside temperature changed by more than 5 degrees in a 20 second period.  When the temperature becomes more stable, Idle Smart will resume functioning.", Automatic);
        initFault(FAULT_NO_TEMP_CHANGE_FAULT, "Cabin Temperature is not Changing", "Your cabin temperature has not changed in the last hour.  This may be due to the location of your inside temperature sensor or an issue with your vehicle heating/cooling system.", Manual);
        initFault(FAULT_LOW_OIL_PRESSURE_FAULT, "Loss of Oil Pressure", "Your oil pressure has dropped below 10 PSI.  For the protection of your vehicle, Idle Smart will resume functioning when Oil PSI exceeds 10 PSI.", Manual);
        initFault(FAULT_HIGH_COOLANT_TEMP_FAULT, "Engine Coolant Temperature is too High", "Engine coolant temperature is above 220 degrees.  For the protection of your vehicle, Idle Smart will resume functioning when the temperature drops below 220 degrees.", Manual);
        initFault(FAULT_GATEWAY_NOT_CONNECTED_FAULT, "Lost connection with the Gateway Unit", "Please check the connection with the Gateway Unit to be sure that it is connected.  Idle Smart will resume functioning when it has been reconnected.", Manual);
        initFault(FAULT_CAN_BUS_NOT_CONNECTED_FAULT, "The Diagnostic cord is not Connected", "Please check that Idle Smart is connected to your diagnostic port.", Manual);
        initFault(FAULT_LOW_ENGINE_RPM_FAULT, "Low Engine Speed", "The engine speed has not reached its target RPMs within the last minute and will shut down for the safety of your engine.  Idle Smart will resume functioning when this has been solved.", Manual);
        initFault(FAULT_PROTOCOL_LEVEL_FAULT, "Protocol Level Fault", "The gateway unit contains an old protocol level.  Please update the under-dash unit with current firmware.", Manual);
        initFault(FAULT_HARDWARE_LEVEL_FAULT, "Hardware Level Fault", "The gateway unit contains an older version PCB.  Some of the new features and enhancements are not supported with this under-dash unit.  Please replace the under-dash unit.", Manual);
        initFault(FAULT_TABLET_NOT_CONNECTED_FAULT, "Lost connection with the Tablet", "Please check the connection with the Tablet to be sure that it is connected.  Idle Smart will resume functioning when it has been reconnected.", Automatic);
        initFault(FAULT_MAX, "Diesel Filter Regeneration", "Your vehicle needs to run a Reg.  Idle Smart will resume functioning after the regeneration has been completed.", Manual);
    }
}
