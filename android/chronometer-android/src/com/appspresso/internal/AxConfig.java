/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package com.appspresso.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * *** INTERNAL USE ONLY***
 * 
 * @version 1.0
 */
public class AxConfig {
    // property file location - merry/src/appspresso-config.properties
    private static final String APPSPRESSO_PROPERTY_FILE = "appspresso-config.properties";

    private static final Properties properties = new Properties() {

        private static final long serialVersionUID = 62687805286491362L;

        {
            try {
                InputStream in =
                        AxConfig.class.getClassLoader().getResourceAsStream(
                                APPSPRESSO_PROPERTY_FILE);
                if (in != null) {
                    load(in);
                }
            }
            catch (IOException ignore) {}
        }
    };

    public static String getAttribute(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public static String getAttribute(String name) {
        return properties.getProperty(name);
    }

    public static int getAttributeAsInteger(String name, int defaultValue) {
        try {
            return Integer.valueOf(getAttribute(name));
        }
        catch (Exception e) {
            // NumberFormatException
            // NullPointerException
            return defaultValue;
        }
    }

    public static boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        try {
            String value = getAttribute(name);
            if (value == null) { return defaultValue; }
            return Boolean.valueOf(value);
        }
        catch (Exception e) {
            // NumberFormatException
            // NullPointerException
            return defaultValue;
        }
    }

    public static double getAttributeAsDouble(String name, double defaultValue) {
        try {
            return Double.valueOf(getAttribute(name));
        }
        catch (Exception e) {
            // NumberFormatException
            // NullPointerException
            return defaultValue;
        }
    }

}
