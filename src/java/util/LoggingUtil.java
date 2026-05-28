/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.logging.Logger;

/**
 * Utility class for obtaining configured Logger instances across the
 * application.
 * <p>
 * This class provides convenience methods to retrieve
 * {@link java.util.logging.Logger} instances either by Class reference or by a
 * custom name string. Centralizing logger retrieval ensures consistent naming
 * conventions and simplifies potential future migration to alternative logging
 * frameworks.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see java.util.logging.Logger
 */
public class LoggingUtil {

    /**
     * Retrieves a Logger instance named according to the fully qualified name
     * of the provided Class.
     * <p>
     * This is the preferred method for obtaining loggers within specific
     * classes, as it automatically maintains a hierarchical naming structure
     * matching the application's package hierarchy.
     * </p>
     *
     * @param clazz The Class for which to retrieve a Logger. Must not be null.
     * @return A Logger instance named after the provided Class's fully
     * qualified name.
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Retrieves a Logger instance with a custom specified name.
     * <p>
     * This method is useful for obtaining loggers for cross-cutting concerns,
     * functional areas, or components that do not map directly to a single
     * class.
     * </p>
     *
     * @param name The custom name for the Logger instance. Must not be null.
     * @return A Logger instance with the specified custom name.
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
}
