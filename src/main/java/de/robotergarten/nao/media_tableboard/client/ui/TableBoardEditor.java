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
package de.robotergarten.nao.media_tableboard.client.ui;

import org.atmosphere.gwt20.client.AtmosphereRequest;

import com.google.gwt.user.client.ui.IsWidget;

import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;

/**
 * Presenter component in the MVP pattern.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public interface TableBoardEditor extends IsWidget {

    /**
     * MVP-style pattern. The view of {@link TableBoardEditor}.
     */
    public interface TableBoardEditorView extends IsWidget {

        /**
         * Factory interface to create a {@link TableBoardEditorView}.
         * Possible point to prevent tight integration and use assisted inject feature of gin.
         */
        public interface Factory {

            /**
             * Create new {@link TableBoardEditorView}.
             *
             * @return Created {@link TableBoardEditorView}.
             */
            TableBoardEditorView create(final AtmosphereRequest pJerseyRpcRequest);
        }

        /**
         * Process all needed actions in order to display Media content in the GUI.
         * 
         * @param pMedia
         */
        public void processMedia(final MediaElement pMedia);
    }

    /**
     * Factory interface to create a {@link TableBoardEditor}.
     * Possible point to prevent tight integration and use assisted inject feature of gin.
     */
    public interface Factory {

        /**
         * Create new {@link TableBoardEditor}.
         * 
         * @param defaultData
         * @return Created {@link TableBoardEditor}.
         */
        TableBoardEditor create(final AtmosphereRequest pJerseyRpcRequest);

    }

    /**
     * Displays Media content in the GUI.
     * 
     * @param pElement
     */
    public void processMedia(final MediaElement pElement);

}
