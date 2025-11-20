package com.learning.cliente_app.lecciones.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@Service
public class ExtractorTextoService {

    public String extraerTexto(MultipartFile file) throws Exception {
        String ext = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1)
                .toLowerCase();

        try (InputStream is = file.getInputStream()) {
            return switch (ext) {
                case "pptx" -> extraerTextoPPT(is);
                case "pdf" -> extraerTextoPDF(is);
                case "docx" -> extraerTextoDOCX(is);
                default -> throw new RuntimeException("Tipo de archivo no soportado");
            };
        }
    }

    private String extraerTextoPPT(InputStream is) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(is);
        StringBuilder sb = new StringBuilder();

        for (XSLFSlide slide : ppt.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape textShape) {
                    String text = textShape.getText();
                    if (text != null && !text.isEmpty()) {
                        sb.append(text).append("\n");
                    }
                }
            }
        }

        ppt.close();
        return sb.toString();
    }

    private String extraerTextoPDF(InputStream is) throws Exception {
        PDDocument doc = PDDocument.load(is);
        PDFTextStripper stripper = new PDFTextStripper();
        String texto = stripper.getText(doc);
        doc.close();
        return texto;
    }

    private String extraerTextoDOCX(InputStream is) throws Exception {
        XWPFDocument doc = new XWPFDocument(is);
        String texto = doc.getParagraphs().stream()
                .map(p -> p.getText())
                .reduce("", (a, b) -> a + "\n" + b);
        doc.close();
        return texto;
    }
}
