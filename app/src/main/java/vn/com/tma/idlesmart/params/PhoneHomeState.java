package vn.com.tma.idlesmart.params;

/**
 * Created by lnthao on 3/31/2017.
 */

public class PhoneHomeState {
    public static final int IDLE = 0;
    public static final int CONNECT = 1;
    public static final int FREEZE_GATEWAY = 2;
    public static final int ACTIVATE = 3;
    public static final int UPDATE = 4;
    public static final int LOG = 5;
    public static final int VERSION = 6;
    public static final int APK_UPDATE = 7;
    public static final int CSC_UPDATE = 8;
    public static final int CSC_AUTO_UPDATE = 9;
    public static final int LOG_STATUS = 15;
    public static final int DATUM = 20;
    public static final int DATUM_LIMIT_LINE = 25;
    public static final int DONE = 90;
    public static final int ERROR = 99;
    public static final int CLEANUP = 100;
}
