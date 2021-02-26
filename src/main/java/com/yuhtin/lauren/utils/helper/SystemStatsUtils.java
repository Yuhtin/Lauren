package com.yuhtin.lauren.utils.helper;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class SystemStatsUtils {

    /**
     * Get cpu load
     * https://stackoverflow.com/a/21962037
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

    public static long totalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long usedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

}
