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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Model for every set of two sentences (Original/translated) persisted in the sentences.xml file.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
@XmlType(propOrder = { "timestamp", "original", "translated" })
@XmlRootElement(namespace = "de.robotergarten.nao.media_tableboard.server.xmlparsing.XMLRoot")
public class Item {

    private SentenceOriginal original;
    private SentenceTranslated translated;
    private long timestamp;

    public Item() {}

    public Item(SentenceTranslated pTrans, SentenceOriginal pOrig) {
        super();
        this.timestamp = new Date().getTime();
        this.original = pOrig;
        this.translated = pTrans;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long pTimestamp) {
        this.timestamp = pTimestamp;
    }

    public SentenceOriginal getOriginal() {
        return original;
    }

    public void setOriginal(SentenceOriginal original) {
        this.original = original;
    }

    public SentenceTranslated getTranslated() {
        return translated;
    }

    public void setTranslated(SentenceTranslated translated) {
        this.translated = translated;
    }

}
