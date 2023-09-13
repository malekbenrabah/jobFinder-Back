package com.example.springsecurity.controller;

import com.example.springsecurity.service.CV.ICvService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/cv")
public class CvController {

    @Autowired
    ICvService  cvService;

    @GetMapping("/generatePDF")
    public ResponseEntity<byte[]> generatePdf(@NonNull HttpServletRequest request) throws DocumentException, IOException, com.itextpdf.text.DocumentException {
        byte[] pdfContent = cvService.generatePdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "cv.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
