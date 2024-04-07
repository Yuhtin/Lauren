package com.yuhtin.lauren.module;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.database.MongoModule;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.PathFinder;
import lombok.Getter;
import lombok.val;

import java.util.*;

public class ModuleManager {

    @Getter
    private static final HashMap<String, Module> modules = new HashMap<>();

    private static final HashMap<String, Module> loadedModules = new HashMap<>();

    public static void load(Lauren lauren) {
        bind(lauren, new MongoModule());

        for (val className : PathFinder.from("com.yuhtin.lauren.module.impl")) {
            try {
                if (modules.containsKey(className.getSimpleName())) continue;

                Object object = className.newInstance();

                if (Module.class.isAssignableFrom(className)) {
                    Module module = (Module) object;
                    loadedModules.put(className.getSimpleName(), module);
                } else {
                    throw new InstantiationException();
                }
            } catch (Exception exception) {
                LoggerUtil.printException(exception);
                lauren.getLogger().severe("The " + className.getSimpleName() + " class could not be instantiated");
            }
        }

        // sort by module dependencies
        List<Module> topologicaled = topologicalSort(loadedModules);
        Collections.reverse(topologicaled);

        topologicaled.removeAll(modules.values());

        for (Module module : topologicaled) {
            bind(lauren, module);
        }

        loadedModules.clear();
    }

    public static void bind(Lauren lauren, Module module) {
        try {
            if (modules.containsKey(module.getClass().getSimpleName())) {
                throw new Exception("The module is already loaded");
            }

            if (!module.setup(lauren)) {
                throw new Exception("The module could not be setup");
            }

            lauren.getJda().addEventListener(module);
            modules.put(module.getClass().getSimpleName(), module);
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
            lauren.getLogger().severe("Error loading module " + module.getClass().getSimpleName() + ": " + exception.getMessage());
        }
    }

    public static void unload(String name) {
        Module module = modules.get(name);
        if (module != null) {
            try {
                module.close();
            } catch (Exception exception) {
                LoggerUtil.printException(exception);
            }

            modules.remove(name);
        }
    }

    public static void unloadAll() {
        for (Module module : modules.values()) {
            try {
                module.close();
            } catch (Exception exception) {
                LoggerUtil.printException(exception);
            }
        }
    }

    public static List<Module> topologicalSort(HashMap<String, Module> toSort) {
        List<Module> result = new ArrayList<>();
        Map<Module, Boolean> visited = new HashMap<>();

        for (Module module : toSort.values()) {
            if (!visited.getOrDefault(module, false)) {
                topologicalSortUtil(module, visited, result);
            }
        }

        return result;
    }

    private static void topologicalSortUtil(Module module, Map<Module, Boolean> visited, List<Module> result) {
        visited.put(module, true);

        List<Class<? extends Module>> dependencies = module.getDependencies() == null
                ? new ArrayList<>()
                : module.getDependencies();

        for (Class<? extends Module> dependencyClass : dependencies) {
            Module dependency = getInstance(dependencyClass);
            if (dependency != null && !visited.getOrDefault(dependency, false)) {
                topologicalSortUtil(dependency, visited, result);
            }
        }

        result.add(module);
    }

    public static <T> T getInstance(Class<T> module) {
        return (T) modules.get(module.getSimpleName());
    }

    public static <T> T getInstance(String name) {
        return (T) modules.get(name);
    }
}
