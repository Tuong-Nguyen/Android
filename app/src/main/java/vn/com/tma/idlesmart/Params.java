package vn.com.tma.idlesmart;

public class Params {
    public static final int BooleanType = 1;
    public static final int Disable = 0;
    public static final int Enable = 1;
    public static final int IntegerType = 2;
    public static final int PARAM_AudibleSound = 15;
    public static final int PARAM_AutoDisable = 7;
    public static final int PARAM_BatteryProtect = 2;
    public static final int PARAM_CabinComfort = 0;
    public static final int PARAM_CabinTargetTemp = 3;
    public static final int PARAM_CabinTempRange = 4;
    public static final int PARAM_ColdWeatherGuard = 1;
    public static final int PARAM_DimTabletScreen = 14;
    public static final int PARAM_DriverTempCommon = 17;
    public static final int PARAM_EngineRunTime = 9;
    public static final int PARAM_FleetCabinComfort = 23;
    public static final int PARAM_FleetCabinTargetTemp = 24;
    public static final int PARAM_HoursBetweenStart = 13;
    public static final int PARAM_IdealCoolantTemp = 10;
    public static final int PARAM_MAX = 25;
    public static final int PARAM_MinCoolantTemp = 11;
    public static final int PARAM_OutsideTargetTemp = 5;
    public static final int PARAM_OutsideTempRange = 6;
    public static final int PARAM_Password = 20;
    public static final int PARAM_PasswordEnable = 19;
    public static final int PARAM_RefreshTablet = 21;
    public static final int PARAM_SoftwareVersion = 22;
    public static final int PARAM_TemperatureSetPoint = 12;
    public static final int PARAM_TruckRPMs = 16;
    public static final int PARAM_TruckTimer = 18;
    public static final int PARAM_VoltageSetPoint = 8;
    public static final int PasswordType = 6;
    public static final int ProcessType = 7;
    public static final int StringType = 5;
    public static final int TempType = 3;
    public static final int VoltageType = 4;
    private final int None;
    public int[] aParamAPIcmd;
    public int[] aParamDef;
    public String[] aParamDesc;
    public int[] aParamIncr;
    public int[] aParamMax;
    public int[] aParamMin;
    public String[] aParamName;
    public String[] aParamPfx;
    public String[] aParamSfx;
    public int[] aParamType;

    public Params() {
        this.None = PARAM_CabinComfort;
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
        initParam(PARAM_CabinComfort, 40, "Cabin Comfort", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "This feature will start the engine when the cabin temperature is no longer comfortable.");
        initParam(PARAM_ColdWeatherGuard, 55, "Cold Weather Guard", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "When you are away from your vehicle, your engine will start when the outside temperature drops below a set outside temperature.");
        initParam(PARAM_BatteryProtect, 50, "Battery Protect", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "This feature will start the engine when your battery drops below a set voltage.");
        initParam(TempType, 41, "Cabin Target Temp", TempType, 70, 50, 80, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, "\u00b0", "This is the temperature you set for your cabin.");
        initParam(VoltageType, 42, "Cabin Temp Range", TempType, StringType, StringType, PARAM_Password, PARAM_ColdWeatherGuard, "\u00b1", "\u00b0", "The range from the cabin target temperature that the device will allow before turning on the engine.");
        initParam(StringType, 43, "Outside Target Temp", TempType, 60, 30, 90, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, "\u00b0", "This is the outside air temperature you set to blow in the cabin.");
        initParam(PasswordType, 44, "Outside Temp Range", TempType, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, 50, PARAM_ColdWeatherGuard, "\u00b1", "\u00b0", "The range from the outside target temperature that the device will allow before turning on the engine.");
        initParam(ProcessType, 32, "Auto Disable", PARAM_BatteryProtect, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_FleetCabinTargetTemp, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "When a value is selected, Idle Smart will automatically shut itself off and will no longer monitor cabin or ambient temperatures or battery voltage levels until the system is reset.");
        initParam(PARAM_VoltageSetPoint, 51, "Voltage Set Point", VoltageType, 122, 112, AccessoryControl.APIEVENT_HANDLER_EXCEPTION, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, "v", "Your engine will start when the battery voltage falls below this level.");
        initParam(PARAM_EngineRunTime, 52, "Engine Run Time", PARAM_BatteryProtect, PARAM_Password, PARAM_IdealCoolantTemp, 120, PARAM_IdealCoolantTemp, BuildConfig.FLAVOR, "m", "The amount of time the engine will run to recharge your batteries.");
        initParam(PARAM_IdealCoolantTemp, 60, "Ideal Cool Temp", PARAM_BatteryProtect, AccessoryControl.APIDATA_SERVER_ROUTE, PARAM_Password, AccessoryControl.APIDEBUG8, StringType, BuildConfig.FLAVOR, "\u00b0", "The engine warm-up period will be stopped when the engine coolant temperature is above this temperature and Cold Weather Guard is enabled.");
        initParam(PARAM_MinCoolantTemp, 59, "Min Coolant Temp", PARAM_BatteryProtect, 40, PARAM_Password, AccessoryControl.APIDEBUG8, StringType, BuildConfig.FLAVOR, "\u00b0", "The engine will be started when the engine coolant temperature is below this temperature and Cold Weather Guard is enabled.");
        initParam(PARAM_TemperatureSetPoint, 56, "Temperature Set Point", PARAM_BatteryProtect, PARAM_Password, PARAM_CabinComfort, 40, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, "\u00b0", "Your engine will start when the temperature outside falls below this level and when the Cold Weather Guard is enabled.");
        initParam(PARAM_HoursBetweenStart, 58, "Hours Between Start", PARAM_BatteryProtect, 120, PARAM_ColdWeatherGuard, 1440, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "This is the minimum amount of time between engine restarts when Cold Weather Guard is enabled.  (Minimum: 1 hour)");
        initParam(PARAM_DimTabletScreen, PARAM_TruckTimer, "Dim Tablet Screen", PARAM_BatteryProtect, 30, PARAM_IdealCoolantTemp, 60, StringType, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "Delay before Auto-Dimming");
        initParam(PARAM_AudibleSound, PARAM_PasswordEnable, "Audible Sound", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_PasswordEnable, 27, "Password Enable", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_Password, 28, "Password", PARAM_BatteryProtect, PARAM_CabinComfort, PARAM_CabinComfort, 9999, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_TruckRPMs, 30, "Truck RPMs", PARAM_BatteryProtect, 1000, 600, 1200, PARAM_MAX, BuildConfig.FLAVOR, "RPMs", "This is the maximum RPMs of your engine while Idle Smart is running.");
        initParam(PARAM_DriverTempCommon, 48, "Driver Temp Range", TempType, TempType, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_CabinComfort, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_TruckTimer, 45, "Truck Timer", PARAM_BatteryProtect, VoltageType, PARAM_CabinComfort, 120, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, "The amount of time the engine will run continuously.  This is best used in idle regulated areas.");
        initParam(PARAM_RefreshTablet, PARAM_CabinComfort, "Refresh Tablet", ProcessType, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_CabinComfort, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_SoftwareVersion, PARAM_CabinComfort, "Software Version", ProcessType, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_CabinComfort, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_FleetCabinComfort, AccessoryControl.APIDATA_FLEET_CABIN_COMFORT_ENABLE, "Cabin Comfort - Fleet Override", PARAM_ColdWeatherGuard, PARAM_CabinComfort, PARAM_CabinComfort, PARAM_ColdWeatherGuard, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        initParam(PARAM_FleetCabinTargetTemp, AccessoryControl.APIDATA_FLEET_CABIN_TEMP_SETPOINT, "Cabin Target Temp - Fleet Override", TempType, 70, 50, 80, PARAM_ColdWeatherGuard, BuildConfig.FLAVOR, "\u00b0", BuildConfig.FLAVOR);
        if (MainActivity.test_mode) {
            this.aParamMax[PARAM_VoltageSetPoint] = AccessoryControl.APIDATA_llEngineTimer;
            this.aParamMin[PARAM_EngineRunTime] = PARAM_CabinComfort;
            this.aParamMax[PARAM_TemperatureSetPoint] = 100;
            this.aParamMin[PARAM_HoursBetweenStart] = PARAM_CabinComfort;
        }
    }
}
