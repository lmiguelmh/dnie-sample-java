package test.lmiguelmh.dnie;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import org.jmrtd.BACKey;
import org.jmrtd.PACEKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author lmiguelmh
 */
public class SimpleICAOMain {

    static {
        // load BC cryptographic provider into JVM this is needed for DNI v1 (ISO9797Alg3Mac)
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static void main(String[] args) throws CardException, CardServiceException, IOException, InterruptedException {
        final CardTerminal ct = TerminalFactory.getDefault().terminals().list().get(0);
        final CardService cs = CardService.getInstance(ct);
        final PassportService ps = new PassportService(cs, 256, 256, true, true);
        ps.open();

        // give some time for OS and other services to access the card
        Thread.sleep(3000);

        // Basic Authentication
        final String documentNumber = "12345678";
        final String dateOfBirth = "000102";
        final String dateOfExpiry = "210102";
        doBAC(ps, documentNumber, dateOfBirth, dateOfExpiry);  // works for DNIe v1 and DNIe v2
        // PACE Authentication
        // final String nuCan = "123456";
        // doPACE(ps, nuCan);  // works only for DNIe v2

        // DG1
        final DG1File dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, ps.getInputStream(PassportService.EF_DG1, 256));
        System.out.println("DG1: " + dg1);
        MRZInfo info = dg1.getMRZInfo();
        System.out.println("DG1 mrzInfo.documentType: " + info.getDocumentType());
        System.out.println("DG1 mrzInfo.dateOfBirth: " + info.getDateOfBirth());
        System.out.println("DG1 mrzInfo.dateOfExpiry: " + info.getDateOfExpiry());
        System.out.println("DG1 mrzInfo.documentNumber: " + info.getDocumentNumber());
        System.out.println("DG1 mrzInfo.documentType: " + info.getDocumentType());
        System.out.println("DG1 mrzInfo.documentCode: " + info.getDocumentCode());
        System.out.println("DG1 mrzInfo.issuingState: " + info.getIssuingState());
        System.out.println("DG1 mrzInfo.primaryIdentifier: " + info.getPrimaryIdentifier());
        System.out.println("DG1 mrzInfo.secondaryIdentifier: " + info.getSecondaryIdentifier());
        System.out.println("DG1 mrzInfo.secondaryIdentifierComponents: " + Arrays.toString(info.getSecondaryIdentifierComponents()));
        System.out.println("DG1 mrzInfo.nationality: " + info.getNationality());
        System.out.println("DG1 mrzInfo.personalNumber: " + info.getPersonalNumber());
        System.out.println("DG1 mrzInfo.optionalData1: " + info.getOptionalData1());
        System.out.println("DG1 mrzInfo.optionalData2: " + info.getOptionalData2());
        System.out.println("DG1 mrzInfo.gender: " + info.getGender());
        System.out.println();

        // DG2
        final DG2File dg2 = (DG2File) LDSFileUtil.getLDSFile(PassportService.EF_DG2, ps.getInputStream(PassportService.EF_DG2, 256));
        // System.out.println("DG2 faceInfos size: " + dg2.getFaceInfos().size());
        final FaceInfo faceInfo = dg2.getFaceInfos().get(0);
        // System.out.println("DG2 faceInfo[0].faceImageInfos size: " + faceInfo.getFaceImageInfos().size());
        final FaceImageInfo faceImageInfo = faceInfo.getFaceImageInfos().get(0);
        System.out.println("DG2 faceInfo[0].faceImageInfos[0].imageLength: " + faceImageInfo.getImageLength());
        byte[] faceImage = new byte[faceImageInfo.getImageLength()];
        new DataInputStream(faceImageInfo.getImageInputStream()).readFully(faceImage);
        switch (faceImageInfo.getImageDataType()) {
            case FaceImageInfo.IMAGE_DATA_TYPE_JPEG:
                Files.write(Paths.get("face.jpg"), faceImage);
                break;
            case FaceImageInfo.IMAGE_DATA_TYPE_JPEG2000:
                Files.write(Paths.get("face.jp2"), faceImage);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static void doBAC(PassportService ps, String documentNumber, String dateOfBirth, String dateOfExpiry) throws CardServiceException {
        final BACKey bacKey = new BACKey(documentNumber, dateOfBirth, dateOfExpiry);
        ps.sendSelectApplet(false);
        ps.doBAC(bacKey);
    }

    public static void doPACE(PassportService ps, String can) throws CardServiceException, IOException {
        final CardAccessFile cardAccessFile = new CardAccessFile(ps.getInputStream(PassportService.EF_CARD_ACCESS, 256));
        final Collection<SecurityInfo> securityInfos = cardAccessFile.getSecurityInfos();
        final PACEInfo paceInfo = (PACEInfo) securityInfos.stream().filter(e -> e instanceof PACEInfo).findFirst().get();
        final PACEKeySpec paceKey = PACEKeySpec.createCANKey(can);
        ps.doPACE(paceKey, paceInfo.getObjectIdentifier(), PACEInfo.toParameterSpec(paceInfo.getParameterId()), null);
        ps.sendSelectApplet(true);
    }
}
