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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for several services.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class Utils {

    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * This method returns the absolute path of the webb application folder.
     * @return
     */
    public String getWebAppRootPath() {
        String path = this.getClass().getClassLoader().getResource("app.properties").getPath();
        String fullPath = path.replace("/WEB-INF/classes/app.properties", "");
        logger.log(Level.FINE, "retrieved app absolute path: " + fullPath);
        return fullPath;
    }
}
