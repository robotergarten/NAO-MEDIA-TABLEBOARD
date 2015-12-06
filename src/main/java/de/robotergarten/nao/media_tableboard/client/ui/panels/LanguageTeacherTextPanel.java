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
package de.robotergarten.nao.media_tableboard.client.ui.panels;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.gwt20.client.AtmosphereRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import de.robotergarten.nao.media_tableboard.client.RPCEvent;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaSubtype;
import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement.MediaType;

/**
 * Implementation for the {@link ITableBoardTextPanel} Interface used on the NAO Tableboard Language application. This class displays
 * content for news, translations or dictation exercises with formatted text. 
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class LanguageTeacherTextPanel extends FlowPanel implements ITableBoardTextPanel {

    /**
     * The websockets channel.
     */
    private final AtmosphereRequest jerseyRpcRequest;
    private final FlowPanel panelButton;
    private static final Logger logger = Logger.getLogger(LanguageTeacherTextPanel.class.getName());

    /**
     * Constructor injecting websockets channel component.
     * 
     * @param pJerseyRpcRequest
     */
    public LanguageTeacherTextPanel(final AtmosphereRequest pJerseyRpcRequest) {

        this.jerseyRpcRequest = pJerseyRpcRequest;
        ensureDebugId("textUi");
        setStylePrimaryName("textPanelId centered");
        
        this.panelButton= new FlowPanel();
        panelButton.ensureDebugId("panelButtonId");
        panelButton.setStylePrimaryName("panelButton");

    }

    @Override
    public void addTextTitle(final MediaElement pMedia) {
        String mess = pMedia.getValue();
        logger.log(Level.FINE, "adding headline/translationTranslated");
        clear();
        add(new HTML("<div class=\"headerId\">" + mess + "</div>"));

    }

    @Override
    public void addTextContent(final MediaElement pMedia) {
        String mess = pMedia.getValue();
        MediaSubtype subType = pMedia.getSubType();

        switch (subType) {

        case translationOriginal:
            logger.log(Level.FINE, "adding translationOriginal");
            addTranslationInTextPanelWithButton(mess, pMedia.isPersisted());

            break;

        case dictation:
            logger.log(Level.FINE, "adding dictation");
            clear();
            addButtonsFromContent(mess);

            break;
            
        case rawText:
            logger.log(Level.FINE, "adding raw text.");
            clear();
            add(new HTML("<p>" + pMedia.getValue() + "</p>"));
            
            break;

        default:
            logger.log(Level.FINE, "adding others");
            addButtonsFromContent(mess);

            break;
        }

    }

    /**
     * Parses information and displays text content.
     * @param pMedia
     */
    public void addTextData(final MediaElement pMedia) {

        MediaSubtype subType = pMedia.getSubType();
        logger.log(Level.FINE, "adding text content");

        switch (subType) {
        
        case headline:
        case translationTranslated:
            addTextTitle(pMedia);

            break;

        default:
            addTextContent(pMedia);
            break;
        }
    }

    private void addTranslationInTextPanelWithButton(final String pContent,
            final boolean isPersisted) {

        addButtonsFromContent(pContent);
        HorizontalPanel interactButtPanel = new HorizontalPanel();
        Button interactionButton;

        if (isPersisted) {
            interactionButton = buildDeleteButton();
        } else {
            interactionButton = buildSaveButton();
        }

        interactButtPanel.add(interactionButton);
        interactButtPanel.setStylePrimaryName("interactionButtonPanel");
        
        panelButton.add(interactButtPanel);
        add(panelButton);
    }

    private void addButtonsFromContent(final String pContent) {
        String[] words = (escapeCharacters(pContent)).split(" ");
        String exclusions = "!?.,;:-";
        String patternNumber = "^[0-9]+[\\.\\,]{1}[0-9]+$";
        
        panelButton.clear();
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStylePrimaryName("contentPanel");
        
        
        for (String w : words) {
            logger.info("Word: " + w);

            if (!w.isEmpty()) {
                //
                if (w.length() < 2 || w.matches(patternNumber)) {
                    contentPanel.add(createCharacterButton(w, true));
                }

                else if (-1 != exclusions.lastIndexOf(w.charAt(w.length() - 1))) {
                    char wordChar = w.charAt(w.length() - 1);
                    contentPanel.add(createButtonFromWord(w.replace(wordChar, '\0')));
                    contentPanel.add(createCharacterButton(String.valueOf(wordChar), false));
                } else {

                    if (!exclusions.contains(w)) {
                        contentPanel.add(createButtonFromWord(w));
                    } else {
                        contentPanel.add(createCharacterButton(w, false));
                    }
                }
            }
        }
        
     panelButton.add(contentPanel);
     add(panelButton);
      
    }

    private Button createButtonFromWord(final String pWord) {
        Button button = new Button(pWord);
        button.setStylePrimaryName("wordButton");
        DOM.setElementAttribute(button.getElement(), "id", "word-button-id");

        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                playAudioTag();

                try {

                    RPCEvent myevent = new RPCEvent();
                    MediaElement wordTranslated = new MediaElement();
                    wordTranslated.setType(MediaType.clientSelectedWord);
                    wordTranslated.setValue(pWord);
                    myevent.setMedia(wordTranslated);

                    jerseyRpcRequest.push(myevent);
                } catch (SerializationException ex) {
                    logger.log(Level.SEVERE, "Failed to serialize message", ex);
                }

            }
        });

        return button;
    }

    /**
     * Native function that plays a sound on press a button/word in GUI.
     */
    public native void playAudioTag() /*-{
		$doc.getElementById('playerId').play();
		//$doc and $wnd are JSNI-speak for document and window
    }-*/;

    
    private Button createCharacterButton(final String pWord, boolean isNumber) {
        Button button = new Button(pWord);
        if (isNumber) {
            button.setStylePrimaryName("numberButton");
        }

        else {
            button.setStylePrimaryName("characterButton");
        }

        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                return;
            }
        });

        return button;
    }

    private String escapeCharacters(String pMedia) {
        String parsedText = pMedia;
        String[] characters = new String[] { ",", ":", ";" };
        for (String elem : characters) {
            parsedText = parsedText.replace(elem, " " + elem + " ");
        }
        return parsedText;
    }

    private Button buildSaveButton() {

        Button buttonSave = new Button("Save");
        DOM.setElementAttribute(buttonSave.getElement(), "id", "save-button-id");
        buttonSave.setStylePrimaryName("saveButton");

        buttonSave.ensureDebugId("buttonSaveId");

        buttonSave.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                playAudioTag();

                try {
                    RPCEvent myevent = new RPCEvent();
                    MediaElement sentenceTranslated = new MediaElement();
                    sentenceTranslated.setType(MediaType.clientAddSentence);
                    sentenceTranslated.setValue(null);
                    myevent.setMedia(sentenceTranslated);

                    jerseyRpcRequest.push(myevent);
                } catch (SerializationException ex) {
                    logger.log(Level.SEVERE, "Failed to serialize message", ex);
                }

            }
        });

        return buttonSave;

    }

    private Button buildDeleteButton() {

        Button buttonSave = new Button("Delete");
        DOM.setElementAttribute(buttonSave.getElement(), "id", "delete-button-id");
        buttonSave.setStylePrimaryName("deleteButton");

        buttonSave.ensureDebugId("buttonDeleteId");

        buttonSave.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                playAudioTag();

                try {
                    RPCEvent myevent = new RPCEvent();
                    MediaElement sentenceTranslated = new MediaElement();
                    sentenceTranslated.setType(MediaType.clientDeleteSentence);
                    sentenceTranslated.setValue(null);
                    myevent.setMedia(sentenceTranslated);

                    jerseyRpcRequest.push(myevent);
                } catch (SerializationException ex) {
                    logger.log(Level.SEVERE, "Failed to serialize message", ex);
                }

            }
        });

        return buttonSave;

    }

}
