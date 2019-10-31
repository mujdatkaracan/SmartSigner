package com.esign.signer.validation;
  
import org.springframework.stereotype.Component; 
import com.esign.signer.BaseConfiguration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable; 
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult; 
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil; 

 
@Component 
public class CadesSignatureValidation extends BaseConfiguration {

	    private static final String POLICY_FILE = "/requirements/certval-policy.xml";
	    private static ValidationPolicy VALIDATION_POLICY ;
	     
		public synchronized ValidationPolicy getPolicy() throws ESYAException {

			 
	        if (VALIDATION_POLICY == null) {
	            
	            	InputStream inputStream = BaseConfiguration.class.getResourceAsStream(POLICY_FILE);
 
	            	VALIDATION_POLICY = PolicyReader.readValidationPolicy(inputStream);
	            
	        }
	        return VALIDATION_POLICY;
	    }
		
	    public SignedDataValidationResult validate(byte[] signature, ISignable externalContent) throws Exception {
	        return validate(signature, externalContent, getPolicy());
	    }

	    public SignedDataValidationResult validate(byte[] signature, ISignable externalContent, ValidationPolicy policy) throws Exception {
	        Hashtable<String, Object> params = new Hashtable<String, Object>();
	        params.put(EParameters.P_CERT_VALIDATION_POLICY, policy);
	        if (externalContent != null)
	            params.put(EParameters.P_EXTERNAL_CONTENT, externalContent);

	        //Use only reference and their corresponding value to validate signature
	        params.put(EParameters.P_FORCE_STRICT_REFERENCE_USE, true);

	        //Ignore grace period which means allow usage of CRL published before signature time
	        //params.put(EParameters.P_IGNORE_GRACE, true);

	        //Use multiple policies if you want to use different policies to validate different types of certificate
	        //CertValidationPolicies certificateValidationPolicies = new CertValidationPolicies();
	        //certificateValidationPolicies.register(CertificateType.DEFAULT.toString(), policy);
	        //ValidationPolicy maliMuhurPolicy=PolicyReader.readValidationPolicy(new FileInputStream("./config/certval-policy-malimuhur.xml"));
	        //certificateValidationPolicies.register(CertificateType.MaliMuhurCertificate.toString(), maliMuhurPolicy);
	        //params.put(EParameters.P_CERT_VALIDATION_POLICIES, certificateValidationPolicies);

	        SignedDataValidation sdv = new SignedDataValidation();
	        SignedDataValidationResult validationResult = sdv.verify(signature, params);

	        return validationResult;
	    }
 
	}