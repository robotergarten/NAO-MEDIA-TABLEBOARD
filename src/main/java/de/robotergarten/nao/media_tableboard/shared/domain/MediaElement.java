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
package de.robotergarten.nao.media_tableboard.shared.domain;

import java.io.Serializable;

/**
 * Rpc object used in the communication between the GWT client-side module and the server.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
@SuppressWarnings("serial")
public class MediaElement implements Serializable {

    public enum MediaSubtype {
        headline,
        translationOriginal,
        translationTranslated,
        dictation,
        content,
        rawText
    }

    public enum MediaType {
        video,
        text,
        photo,
        clientSelectedWord,
        clientAddSentence,
        clientDeleteSentence,
        reset
    }

    /** id of the media content */
    private String id;

    /** useful for e.g. video player, play, resume, pause */
    private String order;

    /** every type of media content */
    private MediaType type;
    private MediaSubtype subType;
    private String value;
    private boolean isPersisted;

    /**
     * Default constructor
     * @param args
     */
    public MediaElement() {}

    public boolean isPersisted() {
        return isPersisted;
    }

    public void setPersisted(boolean isPersisted) {
        this.isPersisted = isPersisted;
    }

    public MediaSubtype getSubType() {
        return subType;
    }

    public void setSubType(MediaSubtype subType) {
        this.subType = subType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(final MediaType pType) {
        this.type = pType;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String value) {
        this.order = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("ID: 	").append(getId()).append("\n");
        sb.append("type:").append(getType()).append("\n");
        sb.append("Subtype: ").append(getSubType()).append("\n");
        sb.append("Value:").append(getValue()).append("\n");
        sb.append("order: ").append(getOrder()).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
