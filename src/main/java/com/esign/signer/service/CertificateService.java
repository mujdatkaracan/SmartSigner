package com.esign.signer.service;

import java.util.List;

import com.esign.signer.base.CommonResult;
import com.esign.signer.model.CertificateModel;
import com.esign.signer.model.TerminalModel;

import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.CertValidationValues;

/**
 * CertificateService
 */
public interface CertificateService {

   public CommonResult<CertificateModel> getCertificates(String[] terminals, int index) throws Exception;

    public CommonResult<List<TerminalModel>> getTerminals() throws Exception;

    public CommonResult<CertValidationValues> signPAdES(String pin, int index, String filePath) throws Exception;

    public CommonResult<byte[]> sign(String pin, int index, String filePath, ESignatureType signatureType) throws Exception;

    public CommonResult<byte[]> serialSign(String pin, int index, String filePath, ESignatureType signatureType) throws Exception;
}