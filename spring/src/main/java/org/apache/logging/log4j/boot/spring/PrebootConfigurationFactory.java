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

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.springframework.boot.logging.LoggingSystem;

/**
 * Fake ConfigurationFactory to prevent startup error log messages related to not finding a log4j configuration file.
 * This log message is misleading because a default configuration file will be provided by this module.
 */
@Plugin(name = "PrebootConfigurationFactory", category = ConfigurationFactory.CATEGORY)
public class PrebootConfigurationFactory extends ConfigurationFactory {

    private static final String[] SUPPORTED_TYPES = {".preboot"};

    @Override
    protected String[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        if (source != null && source != ConfigurationSource.NULL_SOURCE) {
            if (LoggingSystem.get(loggerContext.getClass().getClassLoader()) != null) {
                return new DefaultConfiguration();
            }
        }
        return null;
    }
}
