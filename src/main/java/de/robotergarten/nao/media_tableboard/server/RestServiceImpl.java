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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.MetaBroadcaster;
import org.atmosphere.gwt20.shared.Constants;

import com.google.gwt.rpc.server.Pair;

import de.robotergarten.nao.media_tableboard.client.RPCEvent;
import de.robotergarten.nao.media_tableboard.server.tatoeba.TatoebaClientWrapper;
import de.robotergarten.nao.media_tableboard.server.xmlparsing.Item;
import de.robotergarten.nao.media_tableboard.server.xmlparsing.SentenceOriginal;
import de.robotergarten.nao.media_tableboard.server.xmlparsing.SentenceTranslated;
import de.robotergarten.nao.media_tableboard.server.xmlparsing.XMLSentenceManager;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaSubtype;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaType;

/**
 * Implementation of the {@link IRestService} interface, for all REST declared methods.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
@Path("/jersey/rpc")
public class RestServiceImpl extends UUIDBroadcasterCache  implements IRestService {

    private static List<String> translatedWordsLst = new ArrayList<String>();
    private static List<TranslatedSet> tatoebaPairs = new ArrayList<TranslatedSet>();
    private static List<Pair<String, String>> pairsFromXMLFileLst = new ArrayList<Pair<String, String>>();
    private final static Logger logger = Logger.getLogger(RestServiceImpl.class.getName());
    private static String currentTranslatedSentence;
    private static String currentOriginalSentence;
    private static List<String> dictation = new ArrayList<String>();

    private final static String PDFREPORTPATH = PropertiesUtil.getProperty("pdfReportFilePath");
    private final static String GOOGLEMAILOWNUSERNAME = PropertiesUtil.getProperty("googlemailOwnUsername");
    private final static String GOOGLEMAILOWNPASSWORD = PropertiesUtil.getProperty("googlemailOwnPassword");
    private final static String GOOGLEMAILADDRESSEE = PropertiesUtil.getProperty("googlemailAddressee");
    private final static String GOOGLEMAILSUBJECT = PropertiesUtil.getProperty("googlemailSubject");
    private final static String GOOGLEMAILMESSAGE = PropertiesUtil.getProperty("googlemailMessage");
    public final static String MEDIA_FOLDER = PropertiesUtil.getProperty("media_folder");

    /**
     * Starts REST connection process.
     * 
     * @param ar
     * @return
     */
    @GET
    @Suspend
    @Produces(Constants.GWT_RPC_MEDIA_TYPE)
    public String connect(@Context AtmosphereResource ar) {
        
        return "";
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.server.IRestService#getMediaContent()
     */
    @Override
    public List<String> getMediaContent() {
        return translatedWordsLst;
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.server.IRestService#getTranslationSentences()
     */
    @Override
    public List<Pair<String, String>> getTranslationSentences() {

        List<Pair<String, String>> lst = new ArrayList<Pair<String, String>>();
        for (TranslatedSet elem : tatoebaPairs) {
            if (elem.getPairSentences().size() > 0) {
                lst.add(elem.getPairSentences().get(0));
            }
        }

        if (lst.size() == 0) {
            logger.warning("There are no words selected");
        }
        return lst;
    }

    /**
     * Communication CLIENT > SERVER
     * Manages all connections from the GUI, interactions with forms, buttons, etc.
     * 
     * @param event
     * @param b
     */
    @POST
    @Consumes(Constants.GWT_RPC_MEDIA_TYPE)
    public void receive(RPCEvent event, @Context Broadcaster b) {
        logger.info("Received RPC event on Server: " + event.getMedia());

        MediaElement media = event.getMedia();
        if (null == media || null == media.getType()) {
            logger.warning("Received RPC event with NULL mediaElement or Type!");
            return;
        }

        MediaType type = media.getType();
        
        switch (type) {
        
        case clientSelectedWord:
            addWord(media.getValue());
            break;
            
        case clientAddSentence:
            addCurrentPairSentenceToXMLFile();
            break;
            
        case clientDeleteSentence:
            deleteCurrentPairSentenceInXMLFile();
            break;    

        default:
            logger.warning("Received RPC event with unknown mediaElement " + media.getType());
            break;
        }
        
        b.broadcast(event);
    }

    private void deleteCurrentPairSentenceInXMLFile() {

        if (null != currentOriginalSentence && null != currentTranslatedSentence) {
            Item it = new Item();
            it.setOriginal(new SentenceOriginal(currentOriginalSentence));
            it.setTranslated(new SentenceTranslated(currentTranslatedSentence));
            XMLSentenceManager.deleteItemFromXML(it);
            logger.info("Deleted pair: " + it.toString());
        }
    }

    private void addCurrentPairSentenceToXMLFile() {

        if (null != currentOriginalSentence && null != currentTranslatedSentence) {
            Item it = new Item(new SentenceTranslated(currentTranslatedSentence), new SentenceOriginal(currentOriginalSentence));

            XMLSentenceManager.addItemToXML(it);
            logger.log(Level.FINE ,"Added new pair: " + it.toString());
        }
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.server.IRestService#updateMediaContent(de.robotergarten.nao.media_tableboard.shared.domain.MediaElement)
     */
    @Override
    public void updateMediaContent(MediaElement pMedia) {
        logger.info("updateMediaContent: Calling update with element \n" + pMedia);
        RPCEvent event = new RPCEvent();
        
        final MediaSubtype subType = pMedia.getSubType();
        
        if (null == pMedia.getValue()) {
            logger.warning("Media object received with no value!!");
            return;
        }

        
        if (null != subType) {

            switch (subType) {

            case translationOriginal:
                currentOriginalSentence = pMedia.getValue();

                if (isAlreadyPersisted(pMedia.getValue())) {
                    pMedia.setPersisted(true);
                } else {
                    pMedia.setPersisted(false);
                }

                break;

            case translationTranslated:
                currentTranslatedSentence = pMedia.getValue();
                break;

            case dictation:
                dictation.add(pMedia.getValue());
                break;
                
            default:
                logger.log(Level.INFO, "Adding " + pMedia.getType() + " with value: " + pMedia.getValue() + " to the channel.");
                
                break;
            }

        }

        event.setMedia(pMedia);
        MetaBroadcaster.getDefault().broadcastTo("/", event);
    }


    private boolean isAlreadyPersisted(String id) {
        for (Pair<String, String> pair : pairsFromXMLFileLst) {
            if (pair.getA().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.server.IRestService#sendReport()
     */
    @Override
    public void sendReport() {

        WordsReportPdf.createReportPdf(tatoebaPairs, dictation);

        if (mailInformationIsCorrect()) {

            try {
                GoogleMail.Send(GOOGLEMAILOWNUSERNAME, GOOGLEMAILOWNPASSWORD, GOOGLEMAILADDRESSEE, GOOGLEMAILSUBJECT, GOOGLEMAILMESSAGE, PDFREPORTPATH);
            } catch (AddressException e) {
                logger.log(Level.WARNING , "AddressException on sending report!");
                e.printStackTrace();
            } catch (MessagingException e) {
                logger.log(Level.WARNING , "MessagingException on sending report!");
                e.printStackTrace();
            } catch (IOException e) {
                logger.log(Level.WARNING , "IOException on sending report!");
                e.printStackTrace();
            } finally {
                //clear objects
                translatedWordsLst = new ArrayList<String>();

                File report = new File(PDFREPORTPATH);
                if (report.exists()) {
                    report.delete();
                }

            }
        }

    }

    private boolean mailInformationIsCorrect() {
        if (null == GOOGLEMAILOWNUSERNAME || null == GOOGLEMAILOWNPASSWORD
                || null == GOOGLEMAILADDRESSEE || null == GOOGLEMAILSUBJECT
                || null == GOOGLEMAILMESSAGE) {
            logger.warning("Mail information is wrong, the report will not be send!");
            return false;
        }

        return true;
    }

    private void addWord(final String pWord) {

        String trans = null;

        for (TranslatedSet set : tatoebaPairs) {
            String word = set.getWord();
            if (word.equals(pWord)) {
                logger.log( Level.FINE, "Word '" + pWord + "' already stored, nothing to do!");
                return;
            }
        }

        try {
            // get azure translated word
            trans = Translator.getTranslation(pWord);
            logger.log( Level.FINE, "Adding " + trans + " to translated word to " + pWord);

            // get tatoeba pair sentences for this word
            TatoebaClientWrapper tatoebaClient = new TatoebaClientWrapper(pWord);

            TranslatedSet transWordSet = new TranslatedSet();
            transWordSet.setWord(pWord);
            transWordSet.setTranslatedWord(trans);

            transWordSet.setPairSentences(tatoebaClient.getTatoebaPairs());

            tatoebaPairs.add(transWordSet);
            logger.log( Level.FINE, "Adding to list: \n" + transWordSet.toString());

        } catch (Exception e) {
            logger.warning("Exception on translate word " + pWord);
            trans = "unknown";
        }

        translatedWordsLst.add(pWord + "#" + trans);
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.server.IRestService#getTranslationSentencesFromFileXML()
     */
    @Override
    public List<Pair<String, String>> getTranslationSentencesFromFileXML() {

        //1) return sentences form XML file
        pairsFromXMLFileLst = XMLSentenceManager.getTranslatedPairs();
        List<Pair<String, String>> generalLst = new ArrayList<Pair<String, String>>();
        generalLst.addAll(pairsFromXMLFileLst);

        if (generalLst.size() == 0) {
            logger.warning("There are no words selected");
        }
        //2) Add pairs created on select a word in GUI
        for (TranslatedSet set : tatoebaPairs) {
            for (Pair<String, String> pair : set.getPairSentences()) {
                generalLst.add(pair);
            }
        }

        Collections.reverse(generalLst);
        return generalLst;

    }

}
