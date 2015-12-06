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
package de.robotergarten.nao.media_tableboard.server;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.rpc.server.Pair;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Builder class for the PDF report that will be created from all exercises done.
 * 
 * @author <a href="robotergarten@gmail.com">robotergarten</a>
 */
public class WordsReportPdf {

    private final static String PDF_TITLE = PropertiesUtil.getProperty("pdfTitle");
    private final static String PDF_DICTATION = PropertiesUtil.getProperty("pdfDictation");
    public final static String PDF_REPORT_FILE = PropertiesUtil.getProperty("pdfReportFilePath");
    private final static Logger logger = Logger.getLogger(RestServiceImpl.class.getName());
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font italicFontBold = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLDITALIC);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    /**
     * Builds the whole pdf document from the List of {@link TranslatedSet} objects together with the 
     * Dictation content.
     * 
     * @param pList
     * @param pDictation
     */
    public static void createReportPdf(List<TranslatedSet> pList, final List<String> pDictation) {
        try {
            logger.info("Received request to create pdf report...");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(PDF_REPORT_FILE));
            document.open();
            addContent(document, pList, pDictation);
            document.close();
            logger.info("Created pdf report.");
            
        } catch (Exception e) {
            logger.warning("Exception on create PDF report! \n" + e.toString());
            e.printStackTrace();
        }
    }

    private static void addContent(Document document, List<TranslatedSet> pList,
            final List<String> pDictation) throws DocumentException {
        Anchor anchor = new Anchor(PDF_TITLE, catFont);

        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);
        Paragraph dateP = new Paragraph((new Date()).toString(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        smallBold);
        addEmptyLine(dateP, 1);
        catPart.add(dateP);

        for (TranslatedSet element : pList) {

            Paragraph subPara = new Paragraph(element.getWord() + " ["
                    + element.getTranslatedWord() + "]", italicFontBold);
            Section subCatPart = catPart.addSection(subPara);
            List<Pair<String, String>> sentencesLst = element.getPairSentences();
            for (Pair<String, String> pair : sentencesLst) {
                subCatPart.add(new Paragraph("-Ori. " + pair.getA()));
                subCatPart.add(new Paragraph("-Tra. " + pair.getB()));
                subCatPart.add(new Paragraph("                                                                           "));
            }
        }

        if (null != pDictation && pDictation.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (String dictationParagraph : pDictation) {
                sb.append(dictationParagraph);
            }

            catPart.add(new Paragraph(PDF_DICTATION, catFont));
            catPart.add(new Paragraph(sb.toString()));
        }

        // now add all this to the document
        document.add(catPart);

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
