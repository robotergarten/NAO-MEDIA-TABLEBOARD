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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This Servlet allows to map an external directory with media content (images, video)
 * in the web application. If the properties file has a correct path, all http requests
 * using the '/video' or 'images' token, will be forwarded to this external resource. 
 * 
 * Otherwise, the referenced folders will be those declared in the webbapp folder.   
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class MediaServlet extends HttpServlet {
    
    private final static Logger logger = Logger.getLogger(MediaServlet.class.getName());
    
    private static final String IMAGE_PATTERN = 
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    
    private static final String VIDEO_PATTERN = 
            "([^\\s]+(\\.(?i)(webm|ogg|ogv))$)";
    
    public static String appPath = null;

    Pattern imagePattern = Pattern.compile(IMAGE_PATTERN);
    Pattern videoPattern = Pattern.compile(VIDEO_PATTERN);
    
    Matcher matcherImag;
    Matcher matcherVideo;
    
    public void init(ServletConfig config) {
        appPath = config.getServletContext().getRealPath("/");
    }
    
    /**
     * default id generated.
     */
    private static final long serialVersionUID = -6441374734378784428L;

    public void doGet(HttpServletRequest request, HttpServletResponse resp)
            throws IOException {

        logger.info("MediaServlet forwarding request with media content");
        
        // Set content size
        String mediaFolder = RestServiceImpl.MEDIA_FOLDER;
        
        if (null == mediaFolder) {
            logger.warning("Suspending servlet forwarding because no properties for external resources"
                    + " are defined in properties file!, local resources will be delivered.");
            
            Utils utils = new Utils();
            mediaFolder = utils.getWebAppRootPath();
        }
        
        matcherImag = imagePattern.matcher(request.getPathInfo());
        matcherVideo = videoPattern.matcher(request.getPathInfo());
        
        File file = null;
        
        // images
        if (matcherImag.matches()) {
            resp.setContentType("image/jpeg");
            
            file = new File(mediaFolder + "/images" + request.getPathInfo());
        }
        
        else if(matcherVideo.matches()) {
            resp.setContentType("video/webm");
            
            file = new File(mediaFolder + "/video" + request.getPathInfo());
        }
        
        
        if (null != file){
            // Set content size
            resp.setContentLength((int)file.length());
            
            // Open the file and output streams
            FileInputStream in = new FileInputStream(file);
            OutputStream out = resp.getOutputStream();
            
            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            in.close();
            out.close();
        }
        else {
            logger.severe("Media content could not be delivered because this file"
                    + " was not found in the external folder defined in the properties file!!");
        }
    } 
}
