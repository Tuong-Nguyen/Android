package vn.com.tma.idlesmart;

/**
 * Created by lnthao on 3/30/17.
 */

/**
 *   |-----|-----|-------|------|------|------|...|------|
 *   <---Length--><--ID--><--    Data                    >
 *   Length: 2 byte (excluding these 2 bytes)
 *   ID: 1 byte
 *   Data: Remaining bytes
 *
 */

public class AoaMessage {
    public static final int HEADER_LENGTH = 2;
    public static final int MESSAGE_ID_POSITION = 2;
    public static final int START_DATA_POSITION = 3;
}
