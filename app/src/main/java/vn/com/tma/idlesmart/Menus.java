package vn.com.tma.idlesmart;

public class Menus {
    public static final int[] Submenu1;
    public static final int[] Submenu2;
    public static final int[] Submenu3;
    public static final int[] Submenu4;
    public static final int[] Submenu5;
    public static final int[] Submenu6;
    public String[] aBatteryProtectMenu;
    public String[] aCabinComfortMenu;
    public String[] aColdWeatherGuardMenu;
    public String[] aDeviceSettings;
    public String[] aMainMenu;
    public String[] aRefreshDevice;
    public String[] aVehicleSettings;

    public Menus() {
        this.aMainMenu = new String[]{"Cabin Comfort", "Battery Protect", "Cold Weather Guard", "Device Settings", "Vehicle Settings", "Refresh Device"};
        this.aCabinComfortMenu = new String[]{"Feature On/Off", "Cabin Target Temp", "Cabin Temp Range", "Outside Target Temp", "Outside Temp Range", "Auto Disable"};
        this.aBatteryProtectMenu = new String[]{"Feature On/Off", "Voltage Set Point", "Engine Run Time"};
        this.aColdWeatherGuardMenu = new String[]{"Feature On/Off", "Ideal Coolant Temp", "Min Coolant Temp", "Temperature Set Point", "Hours Between Starts"};
        this.aDeviceSettings = new String[]{"Dim Tablet Screen", "Audible Sound"};
        this.aVehicleSettings = new String[]{"Truck RPMs", "Driver Temp Range", "Truck Timer"};
        this.aRefreshDevice = new String[]{"Refresh Tablet", "Software Version"};
    }

    static {
        Submenu1 = new int[]{0, 3, 4, 5, 6, 7};
        Submenu2 = new int[]{2, 8, 9};
        Submenu3 = new int[]{1, 10, 11, 12, 13};
        Submenu4 = new int[]{14, 15};
        Submenu5 = new int[]{16, 17, 18};
        Submenu6 = new int[]{21, 22};
    }

    public String getSubmenuName(int level1, int level2) {
        String result = BuildConfig.FLAVOR;
        level2--;
        switch (level1) {
            case httpClient.PHONEHOME_OK /*1*/:
                if (level2 < this.aCabinComfortMenu.length) {
                    return this.aCabinComfortMenu[level2];
                }
                return result;
            case httpClient.PHONEHOME_GATEWAY_UPDATE /*2*/:
                if (level2 < this.aBatteryProtectMenu.length) {
                    return this.aBatteryProtectMenu[level2];
                }
                return result;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                if (level2 < this.aColdWeatherGuardMenu.length) {
                    return this.aColdWeatherGuardMenu[level2];
                }
                return result;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                if (level2 < this.aDeviceSettings.length) {
                    return this.aDeviceSettings[level2];
                }
                return result;
            case httpClient.PHONEHOME_NONE /*5*/:
                if (level2 < this.aVehicleSettings.length) {
                    return this.aVehicleSettings[level2];
                }
                return result;
            case Params.PasswordType /*6*/:
                if (level2 < this.aRefreshDevice.length) {
                    return this.aRefreshDevice[level2];
                }
                return result;
            default:
                return result;
        }
    }

    public static int getSubmenuId(int level1, int level2) {
        level2--;
        switch (level1) {
            case httpClient.PHONEHOME_OK /*1*/:
                if (level2 < Submenu1.length) {
                    return Submenu1[level2];
                }
                return -1;
            case httpClient.PHONEHOME_GATEWAY_UPDATE /*2*/:
                if (level2 < Submenu2.length) {
                    return Submenu2[level2];
                }
                return -1;
            case httpClient.PHONEHOME_TABLET_UPDATE /*3*/:
                if (level2 < Submenu3.length) {
                    return Submenu3[level2];
                }
                return -1;
            case httpClient.PHONEHOME_APK_PENDING /*4*/:
                if (level2 < Submenu4.length) {
                    return Submenu4[level2];
                }
                return -1;
            case httpClient.PHONEHOME_NONE /*5*/:
                if (level2 < Submenu5.length) {
                    return Submenu5[level2];
                }
                return -1;
            case Params.PasswordType /*6*/:
                if (level2 < Submenu6.length) {
                    return Submenu6[level2];
                }
                return -1;
            default:
                return -1;
        }
    }
}
