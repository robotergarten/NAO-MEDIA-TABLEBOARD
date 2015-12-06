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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.rpc.server.Pair;

/**
 * Comparator utility class to order unmarshalled Tatoeba pairs of sentences.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class PairsSorter implements java.util.Comparator<String> {

    private int referenceLength;

    public PairsSorter(int reference) {
        super();
        this.referenceLength = reference;
    }

    public int compare(String s1, String s2) {
        int dist1 = Math.abs(s1.length() - referenceLength);
        int dist2 = Math.abs(s2.length() - referenceLength);

        return dist1 - dist2;
    }

    /**
     * Orders the list of Tatoeba sentences using as argument the number of tokens.
     * @param pList
     * @param numberSentences
     * @return
     */
    public List<Pair<String, String>> getOrderedList(List<Pair<String, String>> pList,
            final int numberSentences) {

        if (pList.size() == 0 || numberSentences < 1) {
            return pList;
        }

        List<Pair<String, String>> orderedPairs = new ArrayList<Pair<String, String>>();

        List<String> sortedLst = new ArrayList<String>();
        for (Pair<String, String> p : pList) {
            sortedLst.add(p.getA());
        }
        Collections.sort(sortedLst, new PairsSorter(20));

        for (String orderedString : sortedLst) {
            for (Pair<String, String> unorderedPair : pList) {
                if (unorderedPair.getA().equals(orderedString)) {
                    orderedPairs.add(unorderedPair);
                    break;
                }
            }
        }

        if (orderedPairs.size() < numberSentences) {
            return orderedPairs;
        } else {
            while (orderedPairs.size() > numberSentences) {
                orderedPairs.remove(orderedPairs.size() - 1);
            }
            return orderedPairs;
        }
    }
}