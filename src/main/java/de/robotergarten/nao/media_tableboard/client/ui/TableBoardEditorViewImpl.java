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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.gwt20.client.AtmosphereRequest;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor.TableBoardEditorView;
import de.robotergarten.nao.media_tableboard.client.ui.panels.LanguageTeacherTextPanel;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaSubtype;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaType;

/**
 * Implementation of the View component in the MVP Architecture.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class TableBoardEditorViewImpl extends Composite implements TableBoardEditorView {

    private static final Logger logger = Logger.getLogger(TableBoardEditorViewImpl.class.getName());

    private AtmosphereRequest jerseyRpcRequest;
    private HTML backgroundPanel;
    private SimplePanel imagePanel;
    private SimplePanel videoPanel;
    private LanguageTeacherTextPanel textPanel;
    private int screenHeight = 400;
    private int screenWidth = 800;
    private int windowHeight;
    private int windowWidth;
    private List<Widget> boardsLst = new ArrayList<Widget>();

    private VerticalPanel mainPanel;

    /**
     * Creates a new {@link TableBoardEditorViewImpl}.
     * This type of constructor allows to inject lately components in an dependency injection environment.
     */
    public TableBoardEditorViewImpl(final AtmosphereRequest pJerseyRpcRequest) {

        // MAIN Container
        this.mainPanel = new VerticalPanel();
        this.jerseyRpcRequest = pJerseyRpcRequest;
        
        windowHeight = Window.getClientHeight() -10;
        windowWidth = Window.getClientWidth() -10;
        
        backgroundPanel = new HTML("");
        backgroundPanel.setStylePrimaryName("bgPanel centered");
        
        imagePanel = new SimplePanel();
        imagePanel.ensureDebugId("photoUi");
        imagePanel.setStylePrimaryName("photopanelui");
        imagePanel.setVisible(false);

        videoPanel = new SimplePanel();
        videoPanel.ensureDebugId("videoUi");
        videoPanel.setStylePrimaryName("videopanelui");
        videoPanel.setVisible(false);

        textPanel = new LanguageTeacherTextPanel(jerseyRpcRequest);

        boardsLst.add(backgroundPanel);
        boardsLst.add(imagePanel);
        boardsLst.add(videoPanel);
        boardsLst.add(textPanel);
        
        mainPanel.add(backgroundPanel);
        mainPanel.add(imagePanel);
        mainPanel.add(videoPanel);
        mainPanel.add(textPanel);
        
        showPanel(backgroundPanel);

        initWidget(mainPanel);
    }

    
    /* (non-Javadoc)
     * @see de.robotergarten.nao.media_tableboard.client.ui.TableBoardEditor.TableBoardEditorView#
     * processMedia(de.robotergarten.nao.media_tableboard.shared.domain.MediaElement)
     */
    @Override
    public void processMedia(final MediaElement pMedia) {

        logger.log(Level.FINE, "Processing media content type " + pMedia.getType()); 
                
        if (null == pMedia.getValue()) {
            return;
        }
        
        MediaType type = pMedia.getType();

        switch (type) {
        
        case reset:
            logger.log(Level.FINE, "reset tableboard.");
            resetPanels();
            showPanel(backgroundPanel);
            
            break;

        case photo:

            resetPanels();
            logger.log(Level.FINE, "received MediaElement photo!");
            Image image = new Image(Window.Location.getHref() + "/images/" + pMedia.getValue());
            image.setHeight(String.valueOf(windowHeight));

            imagePanel.add(image);
            showPanel(imagePanel);

            break;

        case video:
            logger.log(Level.FINE, "received MediaElement video!");
            
            textPanel.clear();
            imagePanel.clear();
            
            showPanel(videoPanel);

            if (pMedia.getOrder() == "start") {
                addVideoContent(pMedia);
                startVid();
            }

            else if (pMedia.getOrder() == "resume") {
                startVid();
            }

            else if (pMedia.getOrder() == "pause") {
                pauseVid();
            }

            break;

        case text:
            logger.log(Level.FINE, "received text content");
            videoPanel.clear();
            imagePanel.clear();
            
            showPanel(textPanel);
            textPanel.addTextData(pMedia);

            break;

        default:
            break;
        }
    }

    
    private void resetPanels(){
        videoPanel.clear();
        textPanel.clear();
        imagePanel.clear();
    }
    
    private void addVideoContent(final MediaElement pMedia) {
        videoPanel.clear();
        videoPanel.add(new HTML(getSafeVideoHtml(pMedia.getValue())));
    }

    private String getSafeVideoHtml(final String videoId) {
        
        final String path = Window.Location.getHref(); 
        final String value = "<video id='video1' height='" + windowHeight + "'>" + "<source src=\"" + path + "video/" + videoId
                + "\" type=\"video/ogg\">" + "<source src=\"" + path + "video/" + videoId
                + "\" type=\"video/ogv\">" + "<source src=\"" + path + "video/" + videoId
                + "\" type=\"video/webm\">" + "</video>";
        return value;
    }

    
    private void showPanel(Widget pPanel){
        for (Widget widget: boardsLst){
            if (widget.equals(pPanel)){
                widget.setVisible(true);
            }
            else{
                widget.setVisible(false);
            }
        }
    }


    public static native void startVid() /*-{
	$doc.getElementById("video1").play();
	}-*/;

    public static native void pauseVid() /*-{
		$doc.getElementById("video1").pause();
	}-*/;

}
