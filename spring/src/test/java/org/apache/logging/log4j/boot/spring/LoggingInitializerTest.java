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

import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.jcl.LogFactoryImpl;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.LogManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify Spring Boot uses the proper logging facades.
 */
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest
public class LoggingInitializerTest {

    @Test
    public void testJavaUtilLogManagerSet() throws Throwable {
        LogManager logManager = LogManager.getLogManager();
        assertThat(logManager).isInstanceOf(org.apache.logging.log4j.jul.LogManager.class);
    }

    @Test
    public void testCommonsLogFactorySet() throws Throwable {
        LogFactory logFactory = LogFactory.getFactory();
        assertThat(logFactory).isInstanceOf(LogFactoryImpl.class);
    }

    @Test
    public void testSlf4jFactorySet() throws Exception {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        assertThat(factory).isInstanceOf(Log4jLoggerFactory.class);
    }
}