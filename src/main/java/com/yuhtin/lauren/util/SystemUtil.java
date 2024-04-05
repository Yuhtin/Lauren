package com.yuhtin.lauren.util;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SystemUtil {

    /**
     * Get cpu load <p>
     * <a href="https://stackoverflow.com/a/21962037">Source</a>
     *
     * @return cpu load in %
     */
    public static double getProcessCpuLoad() {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            if (list.isEmpty()) return Double.NaN;

            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();

            // usually takes a couple of seconds before we get real values
            if (value == -1.0) return Double.NaN;
            // returns a percentage value with 1 decimal point precision
            return ((int) (value * 1000) / 10.0);

        } catch (Exception exception) {
            return 0;
        }

    }

    public static String totalMemory() {
        return readableBinary(Runtime.getRuntime().totalMemory());
    }

    public static String usedMemory() {
        return readableBinary(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    public static String readableBinary(long bytes) {

        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) return bytes + " B";

        long value = absB;

        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {

            value >>= 10;
            ci.next();

        }

        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());

    }

}
