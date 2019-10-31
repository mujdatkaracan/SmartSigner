package com.esign.signer;
 
import java.io.File;
import java.io.IOException;
import java.io.InputStream; 
import java.text.SimpleDateFormat;
import java.util.Date; 
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException; 

import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;

/**
 * BaseConfiguration.java sınıfı gerekli değişkenleri ve fonksiyonları sağlar.
 * 
 * @author mujdat.karacan
 */
@Component
public class BaseConfiguration {

	// Projenin paket kök dizini.
	public static final String ROOT_DIR = "./";
// Lisans sosya path'ini tutar.
	public static final String LICENSE_ROOT = "/requirements/lisans.xml";

	// Akıllı kartta yalnızca nitelikli sertifikalar alıyor.
	public static final boolean IS_QUALIFIED = true;

	/**
	 * Uygulama ayağa kalktığı zamana @PostConstruct anotasyonu sayesinde yalnızca bir defa çalışır.
	 * Lisans dosyasını LicenseUtil.java sınıfına tanıtır.
	 */
	@PostConstruct
	public static void init() {

		try {	
			System.out.println("init");
			 

			 System.out.println("Java System Temp Directory : " + System.getProperty("java.io.tmpdir"));

			InputStream inputStream = BaseConfiguration.class.getResourceAsStream(LICENSE_ROOT);

			LicenseUtil.setLicenseXml(inputStream);

			Date expirationDate = LicenseUtil.getExpirationDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("License Expiration Date :" + dateFormat.format(expirationDate));
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initializeFile() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File("src/resources/requirements/esya-signature-config.xml"));
		doc.getDocumentElement().normalize();
		
		 
		
		  Element root = doc.getDocumentElement();
	      System.out.println(root.getNodeName());
	       
	      //Get all employees
	      NodeList nList = doc.getElementsByTagName("certificate-validation-policy-file");
	      System.out.println(nList.toString());
	      
	}
	/**
	 * Projenin paket kök dizinini alır
	 *
	 * @return Kök dizin
	 */
	protected static String getRootDir() {
		return ROOT_DIR;
	}

	/**
	 * Akıllı kartta uygun sertifikaları seçme parametresi
	 *
	 * @return boolean değer döndürür.
	 */
	protected static boolean isQualified() {
		return IS_QUALIFIED;
	}

}
