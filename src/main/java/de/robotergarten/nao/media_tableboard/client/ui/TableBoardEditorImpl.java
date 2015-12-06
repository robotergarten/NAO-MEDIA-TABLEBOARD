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
import com.google.gwt.user.client.ui.Widget;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;

/**
 * Default implementation of the TableBoardEditor Presenter.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TableBoardEditorImpl implements TableBoardEditor {

    private final TableBoardEditorView view;

    /**
     * Contructor that builds a {@link TableBoardEditorView} element.
     * 
     * @param viewFactory
     * @param pJerseyRpcRequest
     */
    public TableBoardEditorImpl(final TableBoardEditorView.Factory viewFactory,
            final AtmosphereRequest pJerseyRpcRequest) {

        this.view = viewFactory.create(pJerseyRpcRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
     */
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor#processMedia(de.robotergarten.nao.media_tableboard.shared.domain.MediaElement)
     */
    @Override
    public void processMedia(MediaElement pElement) {
        this.view.processMedia(pElement);
    }

}
