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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import de.uni_leipzig.wortschatz.webservice.client.sentences.DataMatrix;
import de.uni_leipzig.wortschatz.webservice.client.sentences.DataVector;
import de.uni_leipzig.wortschatz.webservice.client.sentences.RequestParameter;
import de.uni_leipzig.wortschatz.webservice.client.sentences.ResponseParameter;
import de.uni_leipzig.wortschatz.webservice.client.sentences.SentencesClient;

/**
 * Client module for the Azure Microsoft service.
 * Set your Windows Azure Marketplace client info - See
 * http://msdn.microsoft.com/en-us/library/hh454950.aspx * 
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 *
 */
public class Translator {

    private final static String AZURECLIENTID = PropertiesUtil.getProperty("azureClientId");
    private final static String AZURECLIENTSECRET = PropertiesUtil.getProperty("azureClientSecret");
    private final static String AZURE_SOURCE_LANG = PropertiesUtil.getProperty("azureSourcelanguage");
    private final static String AZURE_DEST_LANG = PropertiesUtil.getProperty("azureDestinationLanguage");
    private final static Logger logger = Logger.getLogger(Translator.class.getName());

    /**
     * @param args
     * @throws Exception 
     */
    public static String getTranslation(final String pOriginalStr) throws Exception {

        logger.info("Looking for a translation for word: " +pOriginalStr);
        
        Translate.setClientId(AZURECLIENTID);
        Translate.setClientSecret(AZURECLIENTSECRET);
        String translatedText = "";

        if (!isAzureDataCorrect()) {
            logger.warning("Settings for the Azure Service are wrong!!");
            return null;
        }

        try {
            translatedText = Translate.execute(pOriginalStr, Language.fromString(AZURE_SOURCE_LANG), Language.fromString(AZURE_DEST_LANG));

        } catch (Exception e) {
            logger.warning("Unknown translation!!");
            return "unknown";
        }

        finally {
            if (translatedText.contains("TranslateApiException")) {
                logger.warning("Error translating word!!");
                return "error";
            }
        }
        
        logger.info("Translation found: " +translatedText);
        return translatedText;
    }

    private static boolean isAzureDataCorrect() {

        if (null == AZURECLIENTID || null == AZURECLIENTSECRET || null == AZURE_SOURCE_LANG
                || null == AZURE_DEST_LANG) {
            logger.warning("Mail information is wrong, the report will not be send!");
            return false;
        }

        return true;
    }

    /**
     * Currently this kind of translation is not used. Instead the Tatoeba service is used.
     * Get Uni Leipzig sentences examples for a word.
     * @param pWord
     * @return
     */
    public static List<String> getOccurrences(final String pWord) {

        List<String> occ = new ArrayList<String>();

        ResponseParameter response = null;

        try {
            SentencesClient objSentences = new SentencesClient();

            objSentences.setUsername("anonymous");
            objSentences.setPassword("anonymous");

            RequestParameter request = new RequestParameter();
            request.setCorpus("de");

            objSentences.addParameter("Wort", pWord);
            objSentences.addParameter("Limit", "2");

            response = objSentences.execute();
            DataMatrix resultMatrix = response.getResult();
            
            logger.log(Level.FINE, resultMatrix.toString());
            
            if (null != resultMatrix.getDataVectors()) {
                DataVector[] result = resultMatrix.getDataVectors();
                for (DataVector dataVector : result) {
                    occ.add(dataVector.getDataRow(1));
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return occ;
    }

}
