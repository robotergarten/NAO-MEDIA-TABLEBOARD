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

import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor;
import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor.TableBoardEditorView;
import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditorImpl;
import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditorViewImpl;

/**
 * Tableboard builder class.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TableBoardDIContainer {

    /**
     * @return an anonymous implementation.
     */
    public TableBoardEditor.Factory getTableBoardFactory() {
        return new TableBoardEditor.Factory() {

            @Override
            public TableBoardEditor create(final AtmosphereRequest pJerseyRpcRequest) {
                return new TableBoardEditorImpl(getTableBoardEditorViewFactory(), pJerseyRpcRequest);
            }
        };
    }

    /**
     * @return a {@link TableBoardEditorViewImpl instance}
     */
    private TableBoardEditorView.Factory getTableBoardEditorViewFactory() {
        return new TableBoardEditorView.Factory() {

            @Override
            public TableBoardEditorView create(final AtmosphereRequest pJerseyRpcRequest) {
                return new TableBoardEditorViewImpl(pJerseyRpcRequest);
            }

        };
    }
}
