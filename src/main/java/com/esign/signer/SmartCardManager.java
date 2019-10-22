package com.esign.signer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esign.signer.base.SearchType;
import com.esign.signer.model.ParameterModel;

import sun.security.pkcs11.wrapper.PKCS11Exception;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.Algorithms;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.common.util.bag.Pair;
import tr.gov.tubitak.uekae.esya.api.smartcard.apdu.APDUSmartCard;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.*;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * SmartCardManager, kullanıcının akıllı kart işlemlerini gerçekleştirir.
 * Varsayılan olarak karta APDU(Application Protocol Data Unit) erişimini kullanır.
 * APDU:Akıllı kartlar bağlamında, bir uygulama protokolü veri birimi bir akıllı kart okuyucusu
 *  ve bir akıllı kart arasındaki iletişim birimidir
 * @author mujdat.karacan
 *
 */
public class SmartCardManager {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartCardManager.class);

    private static Object lockObject = new Object();

    private static SmartCardManager mSCManager;
    private static boolean useAPDU = true;
    protected BaseSmartCard bsc;
    protected BaseSigner mSigner;
    private int mSlotCount = 0;
    private String mSerialNumber;
    private ECertificate mSignatureCert;
    private ECertificate mEncryptionCert;

  /**
   * Terminal bilgileri ve slotId ile initialize olan yapılandırıcıdır.
   * Burda statik değişkenleri singleton olarak dolduruyoruz.
   * Uygulama içerisindeki servislerde aktif bir şekilde kullanıyoruz
   * @param terminals Terminal bilgilerini tutan string array.
   * @param index slot bilgisini tutar Smartcard içindeki hangi imza ile işlem yapılacağının index bilgisi
   * @throws SmartCardException Karşılaşılabilecek hataları burdan yakaladık.
   */
    public SmartCardManager(String[] terminals, int index) throws SmartCardException {

        try {
            
            LOGGER.debug("New SmartCardManager will be created");
            String terminal;

            if (terminals == null || terminals.length == 0)
                throw new SmartCardException("No terminal found");

            LOGGER.debug("Terminal count : " + terminals.length);

            terminal = terminals.length == 1
			                ? terminals[index]
			                : terminals[index]; 

            boolean apduSupport = false;

            try {
                apduSupport = APDUSmartCard.isSupported(terminal);
            } catch (Exception ex) {
                LOGGER.error("APDU Smartcard cannot be created. Probably AkisCIF.jar is missing");
                apduSupport = false;
            }

            if (useAPDU && apduSupport) {
                LOGGER.debug("APDU Smartcard will be created");
                APDUSmartCard asc = new APDUSmartCard();
                CardTerminal ct = TerminalFactory.getDefault().terminals().getTerminal(terminal);
                asc.openSession(ct);
                bsc = asc;
            } else {
                LOGGER.debug("PKCS11 Smartcard will be created");
                Pair<Long, CardType> slotAndCardType = SmartOp.getSlotAndCardType(terminal);
                bsc = new P11SmartCard(slotAndCardType.getObject2());
                bsc.openSession(slotAndCardType.getObject1());
            }

            mSerialNumber = StringUtil.toString(bsc.getSerial());
            mSlotCount = terminals.length;
        } catch (SmartCardException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (PKCS11Exception e) {
            LOGGER.error(e.getMessage());
            throw new SmartCardException("Pkcs11 exception", e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new SmartCardException("Smart Card IO exception", e);
        }
    }

    public static void useAPDU(boolean aUseAPDU) {
        useAPDU = aUseAPDU;
    }

    /**
     *  
      *Bu sınıf için Singleton kullanılır. Çok sayıda kart yerleştirilmişse, kullanıcının kartlardan birini seçmesi istenir.
      * Akıllı kart ortamında etkili bir değişiklik varsa, seçim işlemini tekrarlar.
      * Etkili değişim şunlar olabilir:
      * Sisteme bağlı yeni bir akıllı kart varsa.
      * Önbellek kartı sistemden çıkarıldı.
      * Bu durumlar getInstance () işlevinde kontrol edilir. Bu nedenle, squential olmayan SmartCard İşleminiz için,
      * sistemdeki herhangi bir değişikliği kontrol etmek için getInstance () işlevini çağırın.
     *
     ** Bu seçenekleri sıfırlamak için sıfırlama işlevini çağırın.
     *
     * @return SmartCardManager instance
     * @throws SmartCardException
     */
    
    /**
      *Bu sınıf için Singleton kullanılır. Çok sayıda kart yerleştirilmişse, kullanıcının kartlardan birini seçmesi istenir.
      * Akıllı kart ortamında etkili bir değişiklik varsa, seçim işlemini tekrarlar.
      * Etkili değişim şunlar olabilir:
      * Sisteme bağlı yeni bir akıllı kart varsa.
      * Önbellek kartı sistemden çıkarıldı.
      * Bu durumlar getInstance () işlevinde kontrol edilir. Bu nedenle, squential olmayan SmartCard İşleminiz için,
      * sistemdeki herhangi bir değişikliği kontrol etmek için getInstance () işlevini çağırın.
      *
      * ->Bu seçenekleri sıfırlamak için sıfırlama işlevi çağrılır.
     * @param terminals Terminal bilgileri (Smartcard'ları tutan string array.)
     * @param index hangi terminalin kullanılacağını belirlediğimiz slotId
     * @return SmartCard nesnesini döndürür.
     * @throws SmartCardException Hataları trowladık.
     */
    public static SmartCardManager getInstance(String[] terminals, int index) throws SmartCardException {

        synchronized (lockObject) {
            if (mSCManager == null) {
                mSCManager = new SmartCardManager(terminals, index);
                return mSCManager;
            } else {
                //Check is there any change
                try {
                    //If there is a new card in the system, user will select a smartcard.
                    //Create new SmartCard.
                    if (mSCManager.getSlotCount() < SmartOp.getCardTerminals().length) {
                        LOGGER.debug("New card pluged in to system");
                        mSCManager = null;
                        return getInstance(terminals, index);
                    }

                    //If used card is removed, select new card.
                    String availableSerial = null;
                    try {
                        availableSerial = StringUtil.toString(mSCManager.getBasicSmartCard().getSerial());
                    } catch (SmartCardException ex) {
                        LOGGER.debug("Card removed");
                        mSCManager = null;
                        return getInstance(terminals, index);
                    }
                    if (!mSCManager.getSelectedSerialNumber().equals(availableSerial)) {
                        LOGGER.debug("Serial number changed. New card is placed to system");
                        mSCManager = null;
                        return getInstance(terminals, index);
                    }

                    return mSCManager;
                } catch (SmartCardException e) {
                    mSCManager = null;
                    throw e;
                }
            }
        }
    }

    /**
     * SmartCard nesnesini siler.
     * @throws SmartCardException
     */
    public static void reset() throws SmartCardException {
        synchronized (lockObject) {
            mSCManager = null;
        }
    }

 
    /**
     * İstenen sertifika için BaseSigner arayüzünü döndürür.
     * Kriptodan sonra oturumu kapatmayı unutmayın. işlem tamamlandı
     * @param aCardPIN Pin kodu
     * @param aCert Sertifika bilgisi
     * @return BaseSigner nesnesnini döndürür.
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert) throws SmartCardException, LoginException {

        if (mSigner == null) {
            bsc.login(aCardPIN);
            mSigner = bsc.getSigner(aCert.asX509Certificate(), Algorithms.SIGNATURE_RSA_SHA256);
        }
        return mSigner;
    }

    /**
     * İstenen sertifika için BaseSigner arayüzünü döndürür.
     * Kriptodan sonra oturumu kapatmayı unutmayın.
     *
      * @param aCardPIN Pin kodu
     * @param aCert Sertifika bilgisi
     * @return BaseSigner nesnesnini döndürür.
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {

        if (mSigner == null) {
            bsc.login(aCardPIN);
            mSigner = bsc.getSigner(aCert.asX509Certificate(), aSigningAlg, aParams);
        }
        return mSigner;
    }

    /**
     * Smart karttan çıkış yapar.
     *
     * @throws SmartCardException
     */
    public synchronized void logout() throws SmartCardException {
        mSigner = null;
        bsc.logout();
    }

    /**
     * İmza sertifikası için döner. İstenen kartta birden fazla sertifika varsa
     * öznitelikler, kullanıcının sertifikayı seçmesini istiyor. Önbelleği sıfırlamak için seçilen sertifikayı önbelleğe alır,
     * çağrı sıfırlama işlevi.
     *
     * @param checkIsQualified      Nitelikli sertifikaları yalnızca doğruysa seçer.
     * @param checkBeingNonQualified Nitelikli olmayan sertifikaları yalnızca doğruysa seçer.
      * 							 eğer iki parametre yanlışsa, tüm sertifikaları seçer.
      * 							 eğer iki parametre doğruysa, ESYAException değerini atar. Sertifika nitelikli ve nitelikli olamaz
      *                               Aynı zaman.
     * @return certificate
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized ECertificate getSignatureCertificate(ParameterModel searchModel,boolean checkIsQualified, boolean checkBeingNonQualified) throws SmartCardException, ESYAException {

        if (mSignatureCert == null) {
            List<byte[]> allCerts = bsc.getSignatureCertificates();
            mSignatureCert = selectCertificate(searchModel,checkIsQualified, checkBeingNonQualified, allCerts);
        }

        return mSignatureCert;
    }

    public synchronized ECertificate getSignatureCertificate(ParameterModel searchModel,boolean isQualified) throws ESYAException {
        return getSignatureCertificate(searchModel,isQualified, !isQualified);
    }

    /**
     * Şifreleme sertifikası için döner. İstenen kartta birden fazla sertifika varsa
      * öznitelikler, kullanıcının sertifikayı seçmesini istiyor. Önbelleği sıfırlamak için seçilen sertifikayı önbelleğe alır,
      * çağrı sıfırlama işlevi.
     *
     * @param checkIsQualified
     * @param checkBeingNonQualified
     * @return
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized ECertificate getEncryptionCertificate(ParameterModel searchModel,boolean checkIsQualified, boolean checkBeingNonQualified) throws SmartCardException, ESYAException {
        if (mEncryptionCert == null) {
            List<byte[]> allCerts = bsc.getEncryptionCertificates();
            mEncryptionCert = selectCertificate(searchModel,checkIsQualified, checkBeingNonQualified, allCerts);
        }

        return mEncryptionCert;
    }
/**
 * Sertifika seçimini gerçekleştirir
 * @param searchModel sertifka filtesi slotId'ye göre mi yoksa SerialNumber alanına göre mi yapılacağını tutar.
 * @param checkIsQualified
 * @param checkBeingNonQualified
 * @param aCerts
 * @return
 * @throws ESYAException
 */
    private ECertificate selectCertificate(ParameterModel searchModel,boolean checkIsQualified, boolean checkBeingNonQualified, List<byte[]> aCerts) throws ESYAException {
    	 
        if (aCerts != null && aCerts.size() == 0)
            throw new ESYAException("No certificate in smartcard");

        if (checkIsQualified && checkBeingNonQualified)
            throw new ESYAException("A certificate is either qualified or not, cannot be both");

        List<ECertificate> certs = new ArrayList<ECertificate>();

        for (byte[] bs : aCerts) {
            ECertificate cert = new ECertificate(bs);
            if (checkIsQualified) {
                if (cert.isQualifiedCertificate())
                    certs.add(cert);
            } else if (checkBeingNonQualified) {
                if (!cert.isQualifiedCertificate())
                    certs.add(cert);
            } else {
                certs.add(cert);
            }
        }

        ECertificate selectedCert = null;

        if (certs.size() == 0) {
            if (checkIsQualified)
                throw new ESYAException("No qualified certificate in smartcard");
            else if (checkBeingNonQualified)
                throw new ESYAException("No non-qualified certificate in smartcard");

            throw new ESYAException("No certificate in smartcard");
        } else if (certs.size() == 1) {
            selectedCert = certs.get(0);
        } else {
            for (int i = 0; i < certs.size(); i++) {
            	System.out.println(certs.get(i).toString());
            	 switch (searchModel.getSearchType()) {
            	 case BY_SLOT_ID:
            		 selectedCert = certs.get(Integer.parseInt(searchModel.getSearchValue()));
            	 	break;
            	 case BY_SERIAL_NUMBER:
            		 if(certs.get(i).getSerialNumber().equals(searchModel.getSearchValue())) {
            			 selectedCert = certs.get(i);
            		 } 
            	 	break;
            	 }
            }
        }
        return selectedCert;
    }

    private String getSelectedSerialNumber() {
        return mSerialNumber;
    }

    private int getSlotCount() {
        return mSlotCount;
    }

    public BaseSmartCard getBasicSmartCard() {
        return bsc;
    }

 
}
