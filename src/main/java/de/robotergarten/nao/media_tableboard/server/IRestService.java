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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gwt.rpc.server.Pair;

import de.robotergarten.nao.media_tableboard.server.tatoeba.TatoebaClientWrapper;
import de.robotergarten.nao.media_tableboard.server.xmlparsing.XMLSentenceManager;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;

/**
 * Interface that expose all basic REST Services methods for the NAO Media TableBoard application.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public interface IRestService {

    /**
     * This method calls the {@link XMLSentenceManager} module that will parse
     * the XML file where all translation sets could be persisted, and once this
     * infomation is unmarshalled, it will be send back to the REST client in JSON 
     * format. 
     * 
     * @return JSON array with the list of sentences translated.
     */
    @GET
    @Path("/getTranslationXMLSet")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pair<String, String>> getTranslationSentencesFromFileXML();

    /**
     * This method will transform the list of selected words in the GUI to a JSON
     * array that will be send back to the REST client.
     * 
     * @return JSON array with the list of selected words. 
     */
    @GET
    @Path("/getWords")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getMediaContent();

    /**
     * This method translates all retrieved pairs of sentences from the {@link TatoebaClientWrapper} module
     * and will be them send back to the REST client in JSON format.
     * 
     * @return JSON array with the list of translated Pairs of sentences. 
     */
    @GET
    @Path("/getTranslationSet")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pair<String, String>> getTranslationSentences();

    /**
     * Main method used on display all kind of media content in GUI.
     * 
     * @param pMedia
     */
    @PUT
    public void updateMediaContent(MediaElement pMedia);

    /**
     * Produces the PDF report with all exercices and send it back per mail to the account defined in the
     * app.properties file.
     */
    @PUT
    @Path("/sendReport")
    public void sendReport();

}
