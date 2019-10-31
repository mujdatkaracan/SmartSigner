package com.esign.signer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
 
import com.esign.signer.base.ServiceResult;
import com.esign.signer.service.CertificateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam; 

import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.CertValidationValues;
 
/**
 * CertificateController
 */
@RestController
@RequestMapping("certs")
public class CertificateController extends BaseController  {

	 
    @Autowired
    CertificateService service;
    
    @GetMapping("test")
    public GenericResponse getTest() {

        return GenericResponse.createSuccessResponse(true);
    }

    @GetMapping
    public GenericResponse get() throws Exception {

        return GenericResponse.createSuccessResponse(service.getTerminals());
    }

    @PostMapping("sign")
    public GenericResponse sign(@RequestParam(name = "terminal") int terminal,
            @RequestParam(name = "certificateNumber") String certificateNumber,
            @RequestParam(name = "password") String password, @RequestParam(name = "content") String content,
            @RequestParam(name = "isContentBase64") Boolean isContentBase64) throws Exception {

    	 ServiceResult<CertValidationValues> result = service.signPAdES(password, terminal, content);

        return GenericResponse.createSuccessResponse(result.getData()); 
    }

    @PostMapping("signByType")
    public GenericResponse sign(@RequestParam(name = "terminal") int terminal,
            @RequestParam(name = "certificateNumber") String certificateNumber,
            @RequestParam(name = "password") String password, @RequestParam(name = "content") String content,
            @RequestParam(name = "isContentBase64") Boolean isContentBase64,
            @RequestParam(name = "signatureType") ESignatureType signatureType) throws Exception {

      ServiceResult<byte[]> result =  service.sign(password, terminal, content, signatureType);
 
        return GenericResponse.createSuccessResponse(result.getData());
    }

    @PostMapping("serialSign")
    public GenericResponse serialSign(@RequestParam(name = "terminal") int terminal,
            @RequestParam(name = "certificateNumber") String certificateNumber,
            @RequestParam(name = "password") String password, @RequestParam(name = "content") String content,
            @RequestParam(name = "isContentBase64") Boolean isContentBase64,
            @RequestParam(name = "signatureType") ESignatureType signatureType) throws Exception {

        ServiceResult<byte[]> result =    service.serialSign(password, terminal, content, signatureType);

        return GenericResponse.createSuccessResponse(result.getData());
    } 
}