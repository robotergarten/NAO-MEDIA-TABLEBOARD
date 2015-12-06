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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Root model for the main structure that will be translated to the sentences.xml file.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
@XmlRootElement
@XmlType(propOrder = { "listOfItems" })
public class XMLRoot {

    private ArrayList<Item> itemsLst;

    public XMLRoot() {

    }

    public ArrayList<Item> getListOfItems() {
        return itemsLst;
    }

    @XmlElementWrapper(name = "translations")
    @XmlElement(name = "item")
    public void setListOfItems(ArrayList<Item> listOfItems) {
        this.itemsLst = listOfItems;
    }

}
