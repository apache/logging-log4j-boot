/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.logging.log4j.boot.spring;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingInitializationContext;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Boot LoggingSystem for integration with Log4j 2.
 */
public class Log4jLoggingSystem extends AbstractLoggingSystem {

    static {
        Method factoryIsActive;
        Method factorySupportedTypes;
        try {
            factoryIsActive = ConfigurationFactory.class.getDeclaredMethod("isActive");
            ReflectionUtil.makeAccessible(factoryIsActive);
            factorySupportedTypes = ConfigurationFactory.class.getDeclaredMethod("getSupportedTypes");
            ReflectionUtil.makeAccessible(factorySupportedTypes);
        } catch (final NoSuchMethodException e) {
            Throwables.rethrow(e);
            // unreachable; make the compiler happy
            factoryIsActive = null;
            factorySupportedTypes = null;
        }
        FACTORY_IS_ACTIVE = factoryIsActive;
        FACTORY_SUPPORTED_TYPES = factorySupportedTypes;
    }

    private static final Method FACTORY_IS_ACTIVE;
    private static final Method FACTORY_SUPPORTED_TYPES;

    private final String[] standardConfigLocations;
    private LoggerContext loggerContext;

    public Log4jLoggingSystem(final ClassLoader classLoader) {
        super(classLoader);
        this.standardConfigLocations = determineStandardConfigLocations();
    }

    private static String[] determineStandardConfigLocations() {
        final List<String> locations = new ArrayList<>();
        for (final ConfigurationFactory factory : findFactories()) {
            for (final String extension : getSupportedTypes(factory)) {
                if ("*".equals(extension)) {
                    continue;
                }
                locations.add("log4j2-test" + extension);
                locations.add("log4j2" + extension);
            }
        }
        return locations.toArray(new String[0]);
    }

    private static Collection<ConfigurationFactory> findFactories() {
        final PluginManager manager = new PluginManager(ConfigurationFactory.CATEGORY);
        manager.collectPlugins();
        final Collection<ConfigurationFactory> factories = new ArrayList<>();
        for (final PluginType<?> type : manager.getPlugins().values()) {
            final ConfigurationFactory factory = tryCreateFactory(type);
            if (factory != null) {
                factories.add(factory);
            }
        }
        return factories;
    }

    private static ConfigurationFactory tryCreateFactory(final PluginType<?> pluginType) {
        try {
            return pluginType.getPluginClass().asSubclass(ConfigurationFactory.class).newInstance();
        } catch (final Exception ignored) {
            return null;
        }
    }

    private static String[] getSupportedTypes(final ConfigurationFactory factory) {
        try {
            if ((boolean) FACTORY_IS_ACTIVE.invoke(factory)) {
                return (String[]) FACTORY_SUPPORTED_TYPES.invoke(factory);
            }
        } catch (final Exception ignored) {
        }
        return new String[0];
    }

    @Override
    protected String[] getStandardConfigLocations() {
        return standardConfigLocations;
    }

    @Override
    protected void loadDefaults(final LoggingInitializationContext context, final LogFile file) {
        final String configFileName = "classpath:META-INF/log4j/default/log4j2"
            + ((file == null) ? Strings.EMPTY : "-file")
            + ".xml";
        loadConfiguration(context, configFileName, file);
    }

    @Override
    protected void loadConfiguration(final LoggingInitializationContext context, final String location,
                                     final LogFile file) {
        final URI configLocation = NetUtils.toURI(location);
        loggerContext = (LoggerContext) LogManager.getContext(
            getClassLoader(), false, this, configLocation);
    }

    @Override
    protected void reinitialize(final LoggingInitializationContext context) {
        if (loggerContext != null) {
            loggerContext.reconfigure();
        }
    }

    @Override
    public void cleanUp() {
        if (loggerContext != null) {
            loggerContext.setExternalContext(null);
            loggerContext.terminate();
        }
    }

    @Override
    public void setLogLevel(final String loggerName, final LogLevel logLevel) {
        if (loggerContext != null) {
            final Logger logger = loggerContext.getLogger(loggerName);
            final LoggerConfig config = logger.get();
            final Level level = convert(logLevel);
            if (config.getLevel() != level) {
                config.setLevel(level);
            }
        }
    }

    private static Level convert(final LogLevel logLevel) {
        switch (logLevel) {
            case FATAL:
                return Level.FATAL;
            case ERROR:
                return Level.ERROR;
            case WARN:
                return Level.WARN;
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.DEBUG;
            case TRACE:
                return Level.TRACE;
            case OFF:
                return Level.OFF;
            default:
                return Level.toLevel(logLevel.name());
        }
    }
}
