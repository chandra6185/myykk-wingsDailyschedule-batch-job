package com.myykk.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

@Component
public class InvoiceJasperBulkPdfGenerator {
	private static final Logger log = LoggerFactory.getLogger(WingsDailyJobService.class);
	
	@Value("${dynamic.invoice.pdf.gen.shared.path}")
	String pdfPath;

    public boolean multiplePdfs(String newInvNum, List<String> invoiceNos) throws IOException {
        log.info("Entered into multiplePdfs method of class " + this.getClass().getName());

       // InputStream is = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        List<InputStream> isList = new ArrayList<InputStream>();
        try {
            for (String invoiceNo : invoiceNos) {
                fis = new FileInputStream(pdfPath + "/" + invoiceNo + ".pdf");
                isList.add(fis);
                break;
            }
            if (!CollectionUtils.isEmpty(isList)) {
                File f = new File(pdfPath + "/" + newInvNum + ".pdf");
                fos = new FileOutputStream(f);
                merge(isList, fos);
                log.info("PDFs merged successfully with invoice number"+newInvNum);
                return true;
            }
        } catch (DocumentException de) {
            log.error("DocumentException in multiplePdfs method of " + this.getClass().getName(), de);
        } catch (Exception e) {
            log.error("Exception in multiplePdfs method of " + this.getClass().getName(), e);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return false;
    }

    public void merge(List streamOfPDFFiles, OutputStream outputStream)
            throws IOException, DocumentException {
        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            //int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                //totalPages += pdfReader.getNumberOfPages();
            }

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF

            PdfImportedPage page;
           // int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                   // currentPageNumber++;
                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);

                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();

        } finally {
            if (document.isOpen()) {
                document.close();
            }
            outputStream.close();
        }
    }

}
