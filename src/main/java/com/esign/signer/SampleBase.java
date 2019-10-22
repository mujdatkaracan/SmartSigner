package com.esign.signer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;

/**
 * Provides required variables and functions
 */
 @Component
public class SampleBase {

    // bundle root directory of project
    public static final String ROOT_DIR = "./";

  //  public static final String LICENSE_ROOT = "./src/main/resources/requirements/license-file.xml";

    // gets only qualified certificates in smart card
    public static final boolean IS_QUALIFIED = true;
 

    @PostConstruct
    public static void init() {

        try {
        	 System.out.println(System.getProperty("java.io.tmpdir"));
        	
         InputStream in = SampleBase.class.getClassLoader().getResourceAsStream("requirements/lisans");
        	
        	 
        	/* URL root = SampleBase.class.getResource("/");
             String classPath = root.getPath();
             File binDir = new File(classPath);
             String dir = binDir.getParentFile().getParent();
             System.out.println("DIRECTORY>>>>>>>>>>>>>>>>>>>>>>>>>>>" + dir); 
            System.out.println("init");*/
            


              ClassPathResource cl = new ClassPathResource("requirements/license-file.xml");
            URL url = cl.getURL();
      // LicenseUtil.setLicenseXml(new FileInputStream(url.getPath()));
         //  LicenseUtil.setLicenseXml(new FileInputStream("C:\\Users\\mujdat.karacan\\Desktop\\Documentations\\MA3-JAVA-API-2.2.1\\lisans\\lisans.xml"));
            
             //new FileInputStream(url.getPath())
            //LicenseUtil.setLicenseXml(new FileInputStream(dir + "/licence/lisans.xml"));
 

         //   Date expirationDate = LicenseUtil.getExpirationDate();
          // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
          //System.out.println("License expiration date :" + dateFormat.format(expirationDate)); 
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the bundle root directory of project
     *
     * @return the root dir
     */
    protected static String getRootDir() {
        return ROOT_DIR;
    } 
    /**
     * The parameter to choose the qualified certificates in smart card
     *
     * @return the
     */
    protected static boolean isQualified() {
        return IS_QUALIFIED;
    }

}
