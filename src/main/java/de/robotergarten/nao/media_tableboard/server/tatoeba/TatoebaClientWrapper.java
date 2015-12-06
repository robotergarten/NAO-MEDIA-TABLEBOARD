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
package de.robotergarten.nao.media_tableboard.server.tatoeba;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.rpc.server.Pair;

import de.robotergarten.nao.media_tableboard.server.MediaServlet;
import de.robotergarten.nao.media_tableboard.server.PropertiesUtil;
import de.robotergarten.nao.media_tableboard.server.Utils;

/**
 * Wrapper class for the TatoebaClient.py python script. This class executes this script 
 * and translates to java objects the list of retrieved pairs of sentences form a word send as parameter. 
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TatoebaClientWrapper {

    private final static String TATOEBA_SOURCE_LANG = PropertiesUtil.getProperty("tatoebaSourceLanguage");
    private final static String TATOEBA_DEST_LANG = PropertiesUtil.getProperty("tatoebaDestinationLanguage");
    private final static String TATOEBA_ALTN_DEST_LANG = PropertiesUtil.getProperty("tatoebaAlternativeDestinationLanguage");
    private final static String TATOEBA_TEMP_FILE_PATH = PropertiesUtil.getPropertyWithDefault("tatoebaPathTempFile", "/tmp");
    private final static String TATOEBA_PATH_PYTHON = PropertiesUtil.getPropertyWithDefault("tatoebaPathPythonBinary", "/usr/bin/python");

    /**
     * Limit to this value the number of retrieved pair of sentences.
     */
    private final static String NUMBER_RETERIEVED_SENTENCES = "5";

    private String outputFile;
    private String word;
    private String sourceLang;
    private String destLang;
    private String destLangAlt;

    /**
     * Relative path to the tatoeba script in the webapp folder.
     */
    private static String tatoebaScript = "/scripts/TatoebaClient.py";

    private final static Logger logger = Logger.getLogger(TatoebaClientWrapper.class.getName());
    private final static int MAX_LENGTH_SENTENCE = 20;
    private final static int MAX_RETRIEVED_SENTENCE = 2;

    public TatoebaClientWrapper(final String pWord) {
        this.word = pWord;
        this.sourceLang = TATOEBA_SOURCE_LANG;
        this.destLang = TATOEBA_DEST_LANG;
        this.destLangAlt = TATOEBA_ALTN_DEST_LANG;
        this.outputFile = TATOEBA_TEMP_FILE_PATH + "/" + new Date().getTime();
    }

    /**
     * Gets the list of tatoeba sentences.
     * 
     * @return List of pairs of sentences Original/translated.
     * @throws Exception
     */
    public List<Pair<String, String>> getTatoebaPairs() throws Exception {

        List<Pair<String, String>> orderedLst;
        List<List<String>> resultLst = new ArrayList<List<String>>();
        executePythonClient();

        resultLst = parseFile();

        List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
        for (List<String> listTatoeba : resultLst) {
            Pair<String, String> pair = new Pair<String, String>(listTatoeba.get(0), listTatoeba.get(1));
            pairs.add(pair);
        }

        PairsSorter sorter = new PairsSorter(MAX_LENGTH_SENTENCE);
        orderedLst = sorter.getOrderedList(pairs, MAX_RETRIEVED_SENTENCE);

        return orderedLst;
    }

    private List<List<String>> parseFile() {

        logger.info("parsing file " + outputFile);
        FileInputStream fis = null;
        BufferedReader reader = null;
        List<List<String>> arraySentences = new ArrayList<List<String>>();
        List<String> rowLst = new ArrayList<String>();

        try {
            fis = new FileInputStream(outputFile);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            while (true) {

                String line = reader.readLine();
                if (line == null)
                    break;

                rowLst.add(line);                
            }
        } catch (IOException e) {
            logger.warning("Error performing request to Tatoeba. Requested word: " + word);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        List<String> tmpLst = new ArrayList<String>();
        
        if (rowLst.size() > 0) {
            logger.info("Adding " + rowLst.size() + " sentences to the tatoeba list.");
        }
        
        for (String sentence : rowLst) {
            if (sentence.contains("---")) {
                if (tmpLst.size() > 1) {
                    arraySentences.add(tmpLst);
                }

                tmpLst = new ArrayList<String>();
            } else {
                sentence = sentence.replace(sourceLang + ':', "");
                sentence = sentence.replace(destLang + ':', "");
                sentence = sentence.replace(destLangAlt + ':', "");
                tmpLst.add(sentence.trim());
            }
        }
        logger.info("Tatoeba list size: " + arraySentences.size());
        return arraySentences;

    }

    private void executePythonClient() throws IOException, InterruptedException {
        Utils utils = new Utils();
        String tatoebaScriptpath = utils.getWebAppRootPath()  + tatoebaScript;
        
        String[] cmd = { TATOEBA_PATH_PYTHON, tatoebaScriptpath, "-w", word, "-s", sourceLang, "-d",
            destLang, "-a", destLangAlt, "-n", NUMBER_RETERIEVED_SENTENCES, "-f", outputFile };
        Runtime.getRuntime().exec(cmd);

        int index = 10;
        while (index > 1) {
            logger.log(Level.FINE, "waiting for tatoeba answer...");
            Thread.sleep(1000);
            index = index - 1;
        }
        logger.info("finished request");
    }
}
