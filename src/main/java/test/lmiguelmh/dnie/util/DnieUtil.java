package test.lmiguelmh.dnie.util;

import javax.xml.bind.DatatypeConverter;

public class DnieUtil {

    public static String toString(byte[] bytes) {
        // for now just use JAXB's DataTypeConverter
        return DatatypeConverter.printHexBinary(bytes);
    }

    public static byte[] toBytes(String hex) {
        // for now just use JAXB's DataTypeConverter
        return DatatypeConverter.parseHexBinary(hex);
    }
}
