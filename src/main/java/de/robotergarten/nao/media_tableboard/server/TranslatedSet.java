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

import com.google.gwt.rpc.server.Pair;

/**
 * Data structure with one original word, its translation, and a list of pairs of
 * sentences containing this word and their translation sentence.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TranslatedSet {

    public String word;
    public String translatedWord;
    public List<Pair<String, String>> pairSentences;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }

    public List<Pair<String, String>> getPairSentences() {
        return pairSentences;
    }

    public void setPairSentences(List<Pair<String, String>> pairSentences) {
        this.pairSentences = pairSentences;
    }

    public TranslatedSet() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        sb.append("Word: " + word + "\n");
        sb.append("Translation: " + translatedWord + "\n");
        sb.append("[\n");
        for (Pair<String, String> pairs : pairSentences) {
            sb.append("[" + pairs.getA() + " --- " + pairs.getB() + "]\n");
        }
        sb.append("]\n");
        sb.append("]\n");
        return super.toString();
    }

}
