package com.esign.signer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esign.signer.service.CertificateService;
import com.esign.signer.validation.CadesSignatureValidation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.esign.signer.BaseConfiguration;
import com.esign.signer.SmartCardManager;
import com.esign.signer.base.SearchType;
import com.esign.signer.base.ServiceResult;
import com.esign.signer.enums.ServiceResultType;
import com.esign.signer.model.CertificateModel;
import com.esign.signer.model.ParameterModel;
import com.esign.signer.model.TerminalModel;  
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.bag.Pair;
import tr.gov.tubitak.uekae.esya.api.pades.PAdESContext;
import tr.gov.tubitak.uekae.esya.api.signature.Signature;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.CertValidationValues;
import tr.gov.tubitak.uekae.esya.api.signature.config.Config;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.CardType;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartOp;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;
 
/**
 * CertificateServiceImpl
 */
@Component
public class CertificateServiceImpl implements CertificateService {

	  @Autowired
	private CadesSignatureValidation cadesSignatureValidation;
  
	private ParameterModel initializeParameters() {
		return new ParameterModel("0", SearchType.BY_SLOT_ID);

	}

	private String[] getCardTerminals() throws Exception {

		return SmartOp.getCardTerminals();
	}

	@Override
	public ServiceResult<CertificateModel> getCertificates(String[] terminals, int index) throws Exception {
		ServiceResult<byte[]> result = null;

		ParameterModel parameterModel = initializeParameters();
		SmartCardManager cardManager = SmartCardManager.getInstance(terminals, index);

		ECertificate cert = cardManager.getSignatureCertificate(parameterModel, false, false);

		CertificateModel model = new CertificateModel();

		model.setCertificateSerialNumber(cert.getSerialNumber().toString());
		model.setEmail(cert.getEmail());

		model.setValidAfter(cert.getNotBefore());
		model.setValidBefore(cert.getNotAfter());

		Calendar dateNow = Calendar.getInstance();

		Boolean isExpired = (dateNow.after(model.getValidAfter()) || dateNow.equals(model.getValidAfter()))
				&& (dateNow.before(model.getValidBefore()) || dateNow.equals(model.getValidBefore()));

		model.setIsExpired(isExpired);

		model.setIssuerName(cert.getIssuer().getCommonNameAttribute());

		model.setCountry(cert.getSubject().getCountryNameAttribute());

		model.setIsMaliMuhurCertificate(cert.isMaliMuhurCertificate());

		model.setOrganizationName(cert.getIssuer().getOrganizationNameAttribute());

		model.setOwnerName(cert.getSubject().getCommonNameAttribute());

		model.setSerial(cert.getSubject().getSerialNumberAttribute());

		return new ServiceResult<CertificateModel>(model);
	}

	@Override
	public ServiceResult<List<TerminalModel>> getTerminals() throws Exception {

		String[] terminals = getCardTerminals();

		List<TerminalModel> terminalList = new ArrayList<TerminalModel>();

		int index = 0;

		for (String terminal : terminals) {

			Pair<Long, CardType> info = SmartOp.getSlotAndCardType(terminal);

			TerminalModel terminalModel = new TerminalModel(info.getObject1(), info.getObject2());

			List<CertificateModel> certificateList = new ArrayList<CertificateModel>();

			certificateList.add(getCertificates(terminals, index).getData());

			terminalModel.setSignatureCertificates(certificateList);

			terminalList.add(terminalModel);

			index++;

		}
		return new ServiceResult<List<TerminalModel>>(terminalList);
	}

	@Override
	public ServiceResult<CertValidationValues> signPAdES(String pin, int index, String filePath) throws Exception {

		ServiceResult<CertValidationValues> result = null;
		ParameterModel parameterModel = initializeParameters();
		SmartCardManager cardManager = getSmartCardManager(index);

		try {

			ECertificate eCertificate = cardManager.getSignatureCertificate(parameterModel, true);
			BaseSigner signer = cardManager.getSigner(pin, eCertificate);

			SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
					new FileInputStream(new File(filePath)), createContext());

			Signature signature = signatureContainer.createSignature(eCertificate);
			signature.setSigningTime(Calendar.getInstance());
			signature.sign(signer);
			signatureContainer.write(new FileOutputStream(filePath + "_copy"));
			CertValidationValues arrays = signature.getCertValidationValues();
			result.setData(arrays);
			result.setResultType(ServiceResultType.SUCCESS);
		} catch (Exception e) {
			result.setResultType(ServiceResultType.ERROR);
			result.setException(e);
		} finally {

			cardManager.logout();

		}
		return result;
	}

	@Override
	public ServiceResult<byte[]> sign(String pin, int index, String filePath, ESignatureType signatureType)
			throws Exception { 
		byte[] signature = null;
		ServiceResult<byte[]> result = new ServiceResult<byte[]>(signature); 
		ParameterModel parameterModel = initializeParameters();
		SmartCardManager cardManager = getSmartCardManager(index);

		try {
			BaseSignedData baseSignedData = new BaseSignedData();
			ISignable content = new SignableByteArray("test".getBytes());// new File(filePath)
			baseSignedData.addContent(content);

			ECertificate eCertificate = cardManager.getSignatureCertificate(parameterModel, true);
			BaseSigner signer = cardManager.getSigner(pin, eCertificate);

			HashMap<String, Object> params = new HashMap<String, Object>();

			params.put(EParameters.P_EXTERNAL_CONTENT, content);

			params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
			params.put(EParameters.P_CERT_VALIDATION_POLICY, cadesSignatureValidation.getPolicy());

			baseSignedData.addSigner(signatureType, eCertificate, signer, null, params);

			signature = baseSignedData.getEncoded();
			//cardManager.logout();
			// TODO:Cert validate

			 AsnIO.dosyayaz(signature, "C:/ma3api-java-bundle/" + "AS4444İMAN.p7s");
			result.setData(signature);
			result.setResultType(ServiceResultType.SUCCESS);
		} finally {

			//cardManager.logout();
		}
		return result;
	}

	private SmartCardManager getSmartCardManager(int index) throws Exception {

		String[] terminals = getCardTerminals();

		return SmartCardManager.getInstance(terminals, index);
	}

	private byte[] base64ToByteArray(String data) {
		byte[] name = null;
		byte[] decodedString = null;
		try {
			decodedString = Base64.getDecoder().decode(data.getBytes("UTF-8"));
			System.out.println(decodedString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decodedString;
	}

	@Override
	public ServiceResult<byte[]> serialSign(String pin, int index, String filePath, ESignatureType signatureType) throws Exception {
		ServiceResult<byte[]> result = null;
		ParameterModel parameterModel = initializeParameters();
		SmartCardManager cardManager = getSmartCardManager(index);

		try {
			byte[] signature = AsnIO.dosyadanOKU(filePath);

			BaseSignedData baseSignedData = new BaseSignedData();
			baseSignedData = new BaseSignedData(signature);

			List<Signer> signerList = baseSignedData.getSignerList();

			Signer lastSigner = signerList.get(0);

			HashMap<String, Object> params = new HashMap<String, Object>();

			params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
			params.put(EParameters.P_CERT_VALIDATION_POLICY, cadesSignatureValidation.getPolicy());

			ECertificate eCertificate = cardManager.getSignatureCertificate(parameterModel, true);
			BaseSigner signer = cardManager.getSigner(pin, eCertificate);

			lastSigner.addCounterSigner(ESignatureType.TYPE_BES, eCertificate, signer, null, params);

			cardManager.logout();

			AsnIO.dosyayaz(baseSignedData.getEncoded(), filePath + ".p7s");
			result.setData(baseSignedData.getEncoded());
			result.setResultType(ServiceResultType.SUCCESS);
		} catch (Exception e) {
			result.setResultType(ServiceResultType.ERROR);
			result.setException(e);
		} finally {

			cardManager.logout();
		}
		return result;

	}
	 protected PAdESContext createContext() {
	        PAdESContext c = new PAdESContext(new File("/requirements/").toURI());
	        c.setConfig(new Config("/requirements/esya-signature-config.xml"));
	        return c;
	    }
	 
	 
}