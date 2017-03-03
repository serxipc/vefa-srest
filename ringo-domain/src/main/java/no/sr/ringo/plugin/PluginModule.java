/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.sr.ringo.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import no.sr.ringo.config.ClassLoaderUtils;
import no.sr.ringo.config.RingoConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * This module enables extension of Oxalis using jar-files outside classpath.
 *
 * @author steinar
 * @author erlend
 * @since 4.0.0
 */
public class PluginModule extends AbstractModule {

    public static final Logger LOGGER = LoggerFactory.getLogger(PluginModule.class);

    @Override
    protected void configure() {
        bind(PluginFactory.class)
                .to(PluginFactoryImpl.class)
                .in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named("plugin")
    public ClassLoader providesClassLoader(@Named(RingoConfigProperty.HOME_DIR_PATH) Path homeDirectory, Config config) {
        Path pluginPath = null;

        // If there is plugin path, use it
        if (config.hasPath(RingoConfigProperty.PLUGIN_PATH)) {
            String pluginPathString = config.getString(RingoConfigProperty.PLUGIN_PATH);
            pluginPath = homeDirectory.resolve(pluginPathString);
            LOGGER.debug("Loading plugins from " + pluginPath);
        }

        return ClassLoaderUtils.initiate(pluginPath);
    }
}
