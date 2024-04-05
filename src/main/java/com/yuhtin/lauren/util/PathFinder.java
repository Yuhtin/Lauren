package com.yuhtin.lauren.util;

import com.google.common.reflect.ClassPath;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class PathFinder {

    public static List<Class<?>> from(String path) {
        try {
            return ClassPath.from(PathFinder.class.getClassLoader())
                    .getTopLevelClassesRecursive(path)
                    .stream()
                    .map(info -> {
                        try {
                            return Class.forName(info.getName());
                        } catch (ClassNotFoundException exception) {
                            LoggerUtil.printException(exception);
                            return null;}
                    })
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
            return List.of();
        }
    }

}
