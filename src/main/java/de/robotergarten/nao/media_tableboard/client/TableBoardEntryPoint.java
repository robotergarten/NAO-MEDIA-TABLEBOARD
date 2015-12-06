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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.gwt20.client.Atmosphere;
import org.atmosphere.gwt20.client.AtmosphereCloseHandler;
import org.atmosphere.gwt20.client.AtmosphereMessageHandler;
import org.atmosphere.gwt20.client.AtmosphereOpenHandler;
import org.atmosphere.gwt20.client.AtmosphereRequest;
import org.atmosphere.gwt20.client.AtmosphereRequestConfig;
import org.atmosphere.gwt20.client.AtmosphereRequestConfig.Flags;
import org.atmosphere.gwt20.client.AtmosphereResponse;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

import de.robotergarten.nao.media_tableboard.client.ui.TableBoardDIContainer;
import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor;

/**
 * Entry point for the Tableboard GWT application.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TableBoardEntryPoint implements EntryPoint {

    static final Logger logger = Logger.getLogger(TableBoardEntryPoint.class.getName());
    private Atmosphere atmosphere;
    private AtmosphereRequest jerseyRpcRequest;

    private final TableBoardDIContainer container = createDIContainer();
    private TableBoardEditor tableBoardPresenter;

    @Override
    public void onModuleLoad() {

        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

            @Override
            public void onUncaughtException(Throwable e) {
                logger.log(Level.SEVERE, "Uncaught exception", e);
            }
        });

        // inject css inline styles
        Resources.INSTANCE.css().ensureInjected();

        // start websockets connection to server-side app
        initWebSocketsConnection();

        final TableBoardEditor.Factory mediaTableboardFactory = container.getTableBoardFactory();

        final FlowPanel widgetContainer = new FlowPanel();
        tableBoardPresenter = mediaTableboardFactory.create(jerseyRpcRequest);
        widgetContainer.add(tableBoardPresenter);

        RootPanel.get("containerUi").add(widgetContainer);

    }

    private void initWebSocketsConnection() {

        RPCSerializer rpc_serializer = GWT.create(RPCSerializer.class);

        AtmosphereRequestConfig jerseyRpcRequestConfig = AtmosphereRequestConfig.create(rpc_serializer);
        jerseyRpcRequestConfig.setUrl(GWT.getHostPageBaseURL() + "atmo/jersey/rpc");
        jerseyRpcRequestConfig.setTransport(AtmosphereRequestConfig.Transport.WEBSOCKET);
        jerseyRpcRequestConfig.setFallbackTransport(AtmosphereRequestConfig.Transport.STREAMING);
        jerseyRpcRequestConfig.setOpenHandler(new AtmosphereOpenHandler() {

            @Override
            public void onOpen(AtmosphereResponse response) {
                logger.info("Jersey RPC Connection opened");
            }
        });
        jerseyRpcRequestConfig.setCloseHandler(new AtmosphereCloseHandler() {

            @Override
            public void onClose(AtmosphereResponse response) {
                logger.info("Jersey RPC Connection closed");
            }
        });
        jerseyRpcRequestConfig.setMessageHandler(new AtmosphereMessageHandler() {

            @Override
            public void onMessage(AtmosphereResponse response) {

                final List<RPCEvent> events = response.getMessages();
                logger.info("Received messages in client: ");

                for (RPCEvent event : events) {
                    logger.info("received a new message through Jersey RPC: " + event.getData());

                    if (null != event.getMedia()) {
                        logger.info("Element: " + event.getMedia().toString());
                        tableBoardPresenter.processMedia(event.getMedia());
                    } else {
                        logger.info("received MediaElement was null!");
                    }
                }
            }
        });

        jerseyRpcRequestConfig.clearFlags(Flags.dropAtmosphereHeaders);

        atmosphere = Atmosphere.create();
        jerseyRpcRequest = atmosphere.subscribe(jerseyRpcRequestConfig);
    }

    /**
     * @return a dependency injection container for the Tableboard editor.
     */
    protected TableBoardDIContainer createDIContainer() {
        return new TableBoardDIContainer();
    }
}
