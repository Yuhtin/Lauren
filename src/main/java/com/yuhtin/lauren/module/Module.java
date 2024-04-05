package com.yuhtin.lauren.module;

import com.yuhtin.lauren.Lauren;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface Module {

    /**
     * Sets up the module with the provided Lauren instance.
     *
     * @param lauren The Lauren instance to set up the module with.
     * @return true if the setup was successful, false otherwise.
     * @throws Exception if an error occurs during setup.
     */
    boolean setup(Lauren lauren) throws Exception;

    /**
     * Closes the module. This is a default method and does nothing by default.
     * Override this method in a subclass if specific close behavior is needed.
     */
    default void close() {

    }

    /**
     * Returns a list of dependencies for this module. This is a default method and returns an empty list by default.
     * Override this method in a subclass if the module has specific dependencies.
     *
     * @return A list of classes that this module depends on.
     */
    default List<Class<? extends Module>> getDependencies() {
        return new ArrayList<>();
    }

    /**
     * Returns an instance of the specified module class. If the module class is not currently managed,
     * this method will return null.
     *
     * @param moduleClass The class of the module to get an instance of.
     * @return An instance of the specified module class, or null if the module class is not currently managed.
     */
    @Nullable
    static <T extends Module> T instance(Class<T> moduleClass) {
        return ModuleManager.getInstance(moduleClass);
    }

}
