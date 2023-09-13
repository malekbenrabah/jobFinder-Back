package com.example.springsecurity.service.CV;

import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface ICvService {
    byte[] generatePdf(HttpServletRequest request) throws DocumentException, IOException, com.itextpdf.text.DocumentException;


}
