/*
 * Copyright 2014
 * 
 * @author:  Francesc Gonzalez  - robotergarten -
 * @contact: robotergarten@gmail.com
 *           http://www.robotergarten.de
 *           Karlsruhe (Germany)
 *           
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.robotergarten.nao.media_tableboard.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading key-value properties set in a props file.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class PropertiesUtil {

    private static Properties props;
    private static final String FILE_PROPERTIES = "app.properties";
    private final static Logger logger = Logger.getLogger(PropertiesUtil.class.getName());

    static {
        props = new Properties();
        try {
            PropertiesUtil util = new PropertiesUtil();
            props = util.getPropertiesFromClasspath(FILE_PROPERTIES);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Error! properties file could not be found!!");
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error processing properties file!!");
            e.printStackTrace();
        }
    }

    // private constructor
    private PropertiesUtil() {}

    /**
     * Retrieves the value associated to a key, and in case this key could not be found, a default
     * value is send back.
     * 
     * @param key
     * @param pDefaultValue
     * @return
     */
    public static String getPropertyWithDefault(String key, final String pDefaultValue) {
        String result = props.getProperty(key);

        if (null == result) {
            logger.log(Level.WARNING, "Attention, property " + key
                    + " cound not be found in properties file!! " + " setting default value '"
                    + pDefaultValue + "'");
            return pDefaultValue;
        }

        return props.getProperty(key);
    }

    /**
     * Retrieves the value associated to a key.
     * 
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        String result = props.getProperty(key);

        if (null == result) {
            logger.log(Level.WARNING, "Attention, property " + key
                    + " could not be found in properties file!!");
        }

        return result;
    }

    public static Set<Object> getkeys() {
        return props.keySet();
    }

    /**
     * loads properties file from classpath
     * 
     * @param propFileName
     * @return
     * @throws IOException
     */
    private Properties getPropertiesFromClasspath(String propFileName) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName
                    + "' not found in the classpath");
        }

        props.load(inputStream);
        return props;
    }

}
