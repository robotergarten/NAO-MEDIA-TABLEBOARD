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
package de.robotergarten.nao.media_tableboard.server.xmlparsing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.gwt.rpc.server.Pair;

import de.robotergarten.nao.media_tableboard.server.PropertiesUtil;
import de.robotergarten.nao.media_tableboard.server.RestServiceImpl;

/**
 * Handler class for all operations related to the sentences.xm file; save, delete, retrieve, etc.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class XMLSentenceManager {

    private final static Logger logger = Logger.getLogger(RestServiceImpl.class.getName());
    final static String FILESENTENCESXML = PropertiesUtil.getProperty("xmlSentencesFilePath");

    /**
     * Adds a new {@link Item} to the xml file.
     * 
     * @param pPairTranslation
     */
    public static void addItemToXML(final Item pPairTranslation) {

        ArrayList<Item> listOfItems = xmlToJava();
        boolean found = false;
        for (Item item : listOfItems) {
            if (item.getOriginal().getSentence().equals(pPairTranslation.getOriginal().getSentence())) {
                found = true;
                break;
            }
        }

        if (found) {
            logger.info("Item '" + pPairTranslation.getOriginal().getSentence()
                    + "' already persisted!");
            return;
        }

        listOfItems.add(pPairTranslation);

        // persist root
        javaToXML(listOfItems);

    }

    /**
     * Deletes an {@link Item} from the xml file.
     * 
     * @param pPairTranslation
     */
    public static void deleteItemFromXML(final Item pPairTranslation) {

        ArrayList<Item> listOfItems = xmlToJava();

        boolean found = false;
        for (Item item : listOfItems) {
            if (item.getOriginal().getSentence().equals(pPairTranslation.getOriginal().getSentence())) {
                found = true;
                listOfItems.remove(item);
                break;
            }
        }

        if (found) {
            // persist root
            logger.info("Persisting size: " + listOfItems.size());
            javaToXML(listOfItems);
        }
    }

    private static void javaToXML(final ArrayList<Item> itemLst) {

        // specify the location and name of xml file to be read
        File XMLfile = new File(FILESENTENCESXML);

        // this will create Java object - Item from the XML file
        XMLRoot root = new XMLRoot();

        root.setListOfItems(itemLst);

        try {

            // create JAXB context and initializing Marshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLRoot.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // for getting nice formatted output
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Writing to XML file
            jaxbMarshaller.marshal(root, XMLfile);

        } catch (JAXBException e) {
            logger.warning("JAXBException on marshalling java objects!");
            e.printStackTrace();
        }

    }

    private static ArrayList<Item> xmlToJava() {

        ArrayList<Item> itemLst = new ArrayList<Item>();

        try {

            // create JAXB context and initializing Marshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLRoot.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // specify the location and name of xml file to be read
            File XMLfile = new File(FILESENTENCESXML);

            // this will create Java object - Item from the XML file
            XMLRoot root = (XMLRoot) jaxbUnmarshaller.unmarshal(XMLfile);

            itemLst = root.getListOfItems();

        } catch (JAXBException e) {
            logger.warning("JAXBException on unmarshalling XML file!");
            e.printStackTrace();
        }

        return itemLst;
    }

    /**
     * Retrieves the whole list of persisted items.
     * 
     * @return list of pairs persisted.
     */
    public static List<Pair<String, String>> getTranslatedPairs() {

        List<Pair<String, String>> translatedPairsLst = new ArrayList<Pair<String, String>>();

        ArrayList<Item> itemLst = xmlToJava();

        int i = 0;
        for (Item item : itemLst) {
            i++;
            logger.log(Level.FINE, "Original:" + i + " " + item.getOriginal().getSentence());
            logger.log(Level.FINE, "Translated:" + i + " " + item.getTranslated().getSentence());

            Pair<String, String> pair = new Pair<String, String>(item.getOriginal().getSentence(), item.getTranslated().getSentence());
            translatedPairsLst.add(pair);
        }

        return translatedPairsLst;

    }

}
