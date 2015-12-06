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
package de.robotergarten.nao.media_tableboard.client;

import java.io.Serializable;

import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;

/**
 * Rpc class used on send/receive data on the websockets channel.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
@SuppressWarnings("serial")
public class RPCEvent implements Serializable {

    private String data;

    private MediaElement media;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public MediaElement getMedia() {
        return media;
    }

    public void setMedia(MediaElement media) {
        this.media = media;
    }

}
