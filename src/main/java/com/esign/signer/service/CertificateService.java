package com.esign.signer.service;

import java.util.List;
 
import com.esign.signer.base.ServiceResult;
import com.esign.signer.model.CertificateModel;
import com.esign.signer.model.TerminalModel;

import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.CertValidationValues;
 
/**
 * 
 * CertificateService.java uygulama içerisinde e-imza süreçlerini uygulanacak metodların imzasını barınıdır.
 * CertificateServiceImpl.java sınıfına uygulanmıştır(İmplemente edilmiştir.).
 * @author mujdat.karacan
 *
 */
public interface CertificateService {

/**
 * İlgili indexteki sertifika(imzay) bilgilerini döndürür.
 * @param terminals
 * @param index
 * @return
 * @throws Exception
 */
  public ServiceResult<CertificateModel> getCertificates(String[] terminals, int index) throws Exception;

  /**
   * Terminal listesini döndürür.
   * @return 
   * @throws Exception
   */
    public ServiceResult<List<TerminalModel>> getTerminals() throws Exception;

    public ServiceResult<CertValidationValues> signPAdES(String pin, int index, String filePath) throws Exception;

    public ServiceResult<byte[]> sign(String pin, int index, String filePath, ESignatureType signatureType) throws Exception;

    public ServiceResult<byte[]> serialSign(String pin, int index, String filePath, ESignatureType signatureType) throws Exception; 
}