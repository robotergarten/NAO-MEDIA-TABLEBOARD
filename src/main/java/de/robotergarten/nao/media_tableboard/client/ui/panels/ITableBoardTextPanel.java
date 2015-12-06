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

import de.robotergarten.nao.media_tableboard.shared.domain.MediaElement;

/**
 * Interface for all text panels that use a websockets channel to send/receive
 * data between client and server modules. 
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public interface ITableBoardTextPanel  {
	
	/**
	 * Displays formatted fonts for a paragraph title.
	 * 
	 * @param pMedia
	 */
	public void addTextTitle(final MediaElement pMedia);
	
	/**
	 * Displays content in the text panel.
	 * 
	 * @param pMedia
	 */
	public void addTextContent(final MediaElement pMedia);

}
