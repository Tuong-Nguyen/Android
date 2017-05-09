package vn.com.tma.idlesmart;

public class Params {
    public static final int Disable = 0;
    public static final int Enable = 1;

    // region Param index
    public static final int PARAM_CabinComfort = 0;
    public static final int PARAM_ColdWeatherGuard = 1;
    public static final int PARAM_BatteryProtect = 2;
    public static final int PARAM_CabinTargetTemp = 3;
    public static final int PARAM_CabinTempRange = 4;
    public static final int PARAM_OutsideTargetTemp = 5;
    public static final int PARAM_OutsideTempRange = 6;
    public static final int PARAM_AutoDisable = 7;
    public static final int PARAM_VoltageSetPoint = 8;
    public static final int PARAM_EngineRunTime = 9;
    public static final int PARAM_IdealCoolantTemp = 10;
    public static final int PARAM_MinCoolantTemp = 11;
    public static final int PARAM_TemperatureSetPoint = 12;
    public static final int PARAM_HoursBetweenStart = 13;
    public static final int PARAM_DimTabletScreen = 14;
    public static final int PARAM_AudibleSound = 15;
    public static final int PARAM_TruckRPMs = 16;
    public static final int PARAM_DriverTempCommon = 17;
    public static final int PARAM_TruckTimer = 18;
    public static final int PARAM_PasswordEnable = 19;
    public static final int PARAM_Password = 20;
    public static final int PARAM_RefreshTablet = 21;
    public static final int PARAM_SoftwareVersion = 22;
    public static final int PARAM_FleetCabinComfort = 23;
    public static final int PARAM_FleetCabinTargetTemp = 24;
    public static final int PARAM_MAX = 25;
    // endregion

    // region Data Type
    public static final int BooleanType = 1;
    public static final int IntegerType = 2;
    public static final int TempType = 3;
    public static final int VoltageType = 4;
    public static final int StringType = 5;
    public static final int PasswordType = 6;
    public static final int ProcessType = 7;
    // endregion

    public int[] aParamAPIcmd;      // Command API
    public int[] aParamDef;         // Default value
    public String[] aParamDesc;     // Description
    public int[] aParamIncr;        // Increment value
    public int[] aParamMax;         // Maximum value
    public int[] aParamMin;         // Mininum value
    public String[] aParamName;     // Parameter name
    public String[] aParamPfx;      // Parameter prefix
    public String[] aParamSfx;      // Parameter suffix
    public int[] aParamType;        // Data type of parameter
    private int[] aParam;


    public int[] getAParam() {
        return aParam;
    }

    public Params() {
        this.aParamName = new String[PARAM_MAX];
        this.aParamAPIcmd = new int[PARAM_MAX];
        this.aParamDesc = new String[PARAM_MAX];
        this.aParamType = new int[PARAM_MAX];
        this.aParamMin = new int[PARAM_MAX];
        this.aParamMax = new int[PARAM_MAX];
        this.aParamIncr = new int[PARAM_MAX];
        this.aParamDef = new int[PARAM_MAX];
        this.aParamPfx = new String[PARAM_MAX];
        this.aParamSfx = new String[PARAM_MAX];
        this.aParam = new int[Params.PARAM_MAX];
        initializeParameters();
    }

    private void initParam(int param, int apicmd, String name, int type, int def, int min, int max, int incr, String pfx, String sfx, String desc) {
        this.aParamAPIcmd[param] = apicmd;
        this.aParamName[param] = name;
        this.aParamDesc[param] = desc;
        this.aParamType[param] = type;
        this.aParamDef[param] = def;
        this.aParamMin[param] = min;
        this.aParamMax[param] = max;
        this.aParamIncr[param] = incr;
        this.aParamPfx[param] = pfx;
        this.aParamSfx[param] = sfx;
    }

    public void initializeParameters() {
        initParam(PARAM_CabinComfort, AccessoryControl.APICMD_CABIN_COMFORT_ENABLE, "Cabin Comfort",
                BooleanType, 0, 0, 1, 1, "", "", "This feature will start the engine when the cabin temperature is no longer comfortable.");
        initParam(PARAM_ColdWeatherGuard, AccessoryControl.APICMD_COLD_WEATHER_GUARD_ENABLE, "Cold Weather Guard",
                BooleanType, 0, 0, 1, 1, "", "", "When you are away from your vehicle, your engine will start when the outside temperature drops below a set outside temperature.");
        initParam(PARAM_BatteryProtect, AccessoryControl.APICMD_BATTERY_MONITOR_ENABLE, "Battery Protect",
                BooleanType, 0, 0, 1, 1, "", "", "This feature will start the engine when your battery drops below a set voltage.");
        initParam(PARAM_CabinTargetTemp, AccessoryControl.APICMD_CABIN_TEMP_SETPOINT, "Cabin Target Temp",
                TempType, 70, 50, 80, 1, "", "\u00b0", "This is the temperature you set for your cabin.");
        initParam(PARAM_CabinTempRange, AccessoryControl.APICMD_CABIN_TEMP_RANGE, "Cabin Temp Range",
                TempType, 5, 5, 20, 1, "\u00b1", "\u00b0", "The range from the cabin target temperature that the device will allow before turning on the engine.");
        initParam(PARAM_OutsideTargetTemp, AccessoryControl.APICMD_AMBIENT_TEMP_SETPOINT, "Outside Target Temp",
                TempType, 60, 30, 90, 1, "", "\u00b0", "This is the outside air temperature you set to blow in the cabin.");
        initParam(PARAM_OutsideTempRange, AccessoryControl.APICMD_AMBIENT_TEMP_RANGE, "Outside Temp Range",
                TempType, 1, 1, 50, 1, "\u00b1", "\u00b0", "The range from the outside target temperature that the device will allow before turning on the engine.");
        initParam(PARAM_AutoDisable, AccessoryControl.APICMD_AUTO_SHUTOFF_TIMEOUT, "Auto Disable",
                IntegerType, 0, 0, 24, 1, "", "", "When a value is selected, Idle Smart will automatically shut itself off and will no longer monitor cabin or ambient temperatures or battery voltage levels until the system is reset.");
        initParam(PARAM_VoltageSetPoint, AccessoryControl.APICMD_BATTERY_MONITOR_VOLTAGE, "Voltage Set Point",
                VoltageType, 122, 112, 127, 1, "", "v", "Your engine will start when the battery voltage falls below this level.");
        initParam(PARAM_EngineRunTime, AccessoryControl.APICMD_BATTERY_MONITOR_RUNTIME, "Engine Run Time",
                IntegerType, 20, 10, 120, 10, "", "m", "The amount of time the engine will run to recharge your batteries.");
        initParam(PARAM_IdealCoolantTemp, AccessoryControl.APICMD_COLD_WEATHER_GUARD_IDEAL_COOLANT, "Ideal Cool Temp",
                IntegerType, 180, 20, 210, 5, "", "\u00b0", "The engine warm-up period will be stopped when the engine coolant temperature is above this temperature and Cold Weather Guard is enabled.");
        initParam(PARAM_MinCoolantTemp, AccessoryControl.APICMD_COLD_WEATHER_GUARD_MIN_COOLANT, "Min Coolant Temp",
                IntegerType, 40, 20, 210, 5, "", "\u00b0", "The engine will be started when the engine coolant temperature is below this temperature and Cold Weather Guard is enabled.");
        initParam(PARAM_TemperatureSetPoint, AccessoryControl.APICMD_COLD_WEATHER_GUARD_START_TEMP, "Temperature Set Point",
                IntegerType, 20, 0, 40, 1, "", "\u00b0", "Your engine will start when the temperature outside falls below this level and when the Cold Weather Guard is enabled.");
        initParam(PARAM_HoursBetweenStart, AccessoryControl.APICMD_COLD_WEATHER_GUARD_RESTART_INTERVAL, "Hours Between Start",
                IntegerType, 120, 1, 1440, 1, "", "", "This is the minimum amount of time between engine restarts when Cold Weather Guard is enabled.  (Minimum: 1 hour)");
        initParam(PARAM_DimTabletScreen, AccessoryControl.APICMD_AUTODIM, "Dim Tablet Screen",
                IntegerType, 30, 10, 60, 5, "", "", "Delay before Auto-Dimming");
        initParam(PARAM_AudibleSound, AccessoryControl.APICMD_AUDIBLE, "Audible Sound",
                BooleanType, 0, 0, 1, 1, "", "", "");
        initParam(PARAM_PasswordEnable, AccessoryControl.APICMD_PASSWORD_ENABLE, "Password Enable",
                BooleanType, 0, 0, 1, 1, "", "", "");
        initParam(PARAM_Password, AccessoryControl.APICMD_PASSWORD, "Password",
                IntegerType, 0, 0, 9999, 1, "", "", "");
        initParam(PARAM_TruckRPMs, AccessoryControl.APICMD_ENGINE_IDLE_RPM, "Truck RPMs",
                IntegerType, 1000, 600, 1200, 25, "", "RPMs", "This is the maximum RPMs of your engine while Idle Smart is running.");
        initParam(PARAM_DriverTempCommon, AccessoryControl.APICMD_DRIVER_TEMP_COMMON, "Driver Temp Range",
                TempType, 3, 0, 4, 1, "", "", ""); // TODO: Original value for (default, min, max, increment) is (3, 0, 0, 0). This changes to (3, 0, 4, 1) as documented.
        initParam(PARAM_TruckTimer, AccessoryControl.APICMD_SYSTEMTIMER, "Truck Timer",
                IntegerType, 4, 0, 120, 1, "", "", "The amount of time the engine will run continuously.  This is best used in idle regulated areas.");
        initParam(PARAM_RefreshTablet, AccessoryControl.APICMD_BASE, "Refresh Tablet",
                ProcessType, 0, 0, 0, 0, "", "", "");
        initParam(PARAM_SoftwareVersion, AccessoryControl.APICMD_BASE, "Software Version",
                ProcessType, 0, 0, 0, 0, "", "", "");
        initParam(PARAM_FleetCabinComfort, AccessoryControl.APIDATA_FLEET_CABIN_COMFORT_ENABLE, "Cabin Comfort - Fleet Override",
                BooleanType, 0, 0, 1, 1, "", "", "");
        initParam(PARAM_FleetCabinTargetTemp, AccessoryControl.APIDATA_FLEET_CABIN_TEMP_SETPOINT, "Cabin Target Temp - Fleet Override",
                TempType, 70, 50, 80, 1, "", "\u00b0", "");

        if (MainActivity.test_mode) {
            this.aParamMax[PARAM_VoltageSetPoint] = 150;
            this.aParamMin[PARAM_EngineRunTime] = 0;
            this.aParamMax[PARAM_TemperatureSetPoint] = 100;
            this.aParamMin[PARAM_HoursBetweenStart] = 0;
        }
    }

    /**
     * Increase Params
     * @param paramId
     */
    public void incrParam(int paramId) {
        int[] iArr = aParam;
        iArr[paramId] = iArr[paramId] + aParamIncr[paramId];
        if (aParam[paramId] > aParamMax[paramId]) {
            aParam[paramId] = aParamMax[paramId];
        }
    }

    /**
     * Decrease Params
     * @param paramId
     */
    public void decrParam(int paramId) {
        int[] iArr = aParam;
        iArr[paramId] = iArr[paramId] - aParamIncr[paramId];
        if (aParam[paramId] < aParamMin[paramId]) {
            aParam[paramId] = aParamMin[paramId];
        }
    }

    /**
     * Checking for Cabin Temperature Common increase is validate
     * @param value
     * @return
     */
    public boolean isCabinTempCommonIncrValid(int value) {
        if (aParamIncr[Params.PARAM_CabinTargetTemp] + value <= aParam[Params.PARAM_FleetCabinTargetTemp] + aParam[Params.PARAM_DriverTempCommon]) {
            return true;
        }
        return false;
    }

    /**
     * Checking for Cabin Temperature Common decrease is validate
     * @param value
     * @return
     */
    public boolean isCabinTempCommonDecrValid(int value) {
        if (value - aParamIncr[Params.PARAM_CabinTargetTemp] >= aParam[Params.PARAM_FleetCabinTargetTemp] - aParam[Params.PARAM_DriverTempCommon]) {
            return true;
        }
        return false;
    }

    /**
     * Initialize for running params
     */
    public void initializeRunningParams() {
        for (int i = 0; i < Params.PARAM_MAX; i += 1) {
            aParam[i] = aParamDef[i];
        }
    }

}
