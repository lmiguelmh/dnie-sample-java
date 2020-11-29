package test.lmiguelmh.dnie;

import sun.security.pkcs11.SunPKCS11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * @author lmiguelmh
 */
public class SimplePKCS11ProviderMain {

    private static byte[] calcSignature(byte[] bytes, PrivateKey key, Provider provider)
            throws GeneralSecurityException {
        // do a "raw" signature, same as using the mechanism RSA_PKCS
        final Signature signatureAlgorithm = Signature.getInstance("NONEwithRSA", provider);
        signatureAlgorithm.initSign(key);
        signatureAlgorithm.update(bytes);
        return signatureAlgorithm.sign();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        // Java PKCS#11 guide
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/p11guide.html
        final String slot = "1";
        final String pkcs11config = "" +
                "name = SmartCard\n" +
                "description = OpenSC PKCS#11 Module\n" +
                "slot = " + slot + "\n" +
                // choose the same architecture as the JVM
                "library = \"C:/Program Files (x86)/OpenSC Project/OpenSC/pkcs11/opensc-pkcs11.dll\"";
        final Provider pkcs11Provider = new SunPKCS11(new ByteArrayInputStream(pkcs11config.getBytes()));
        final char[] pin = {'1', '1', '2', '2', '3', '3'};
        final KeyStore smartCardKeyStore = KeyStore.getInstance("PKCS11", pkcs11Provider);
        smartCardKeyStore.load(null, pin);

        final Enumeration<String> aliasesEnum = smartCardKeyStore.aliases();
        while (aliasesEnum.hasMoreElements()) {
            final String alias = aliasesEnum.nextElement();
            System.out.println(alias);

            // read the certificate
            final X509Certificate certificate = (X509Certificate) smartCardKeyStore.getCertificate(alias);
            Files.write(Paths.get(slot + ".cer"), certificate.getEncoded());

            // get a handle to the private key
            final PrivateKey privateKey = (PrivateKey) smartCardKeyStore.getKey(alias, null);
            if (privateKey != null) {
                Files.write(Paths.get("d:\\sistemas-unap.java.sig"), calcSignature(Files.readAllBytes(Paths.get("d:\\sistemas-unap.txt")), privateKey, pkcs11Provider));
            }
        }
        Security.removeProvider(pkcs11Provider.getName());
    }

    // SHA-2 PKCS wrapping
    // public static byte[] wrap (byte[] hash) {
    //     ByteBuffer byteBuffer = ByteBuffer.allocate(51);
    //     byteBuffer.put(new byte[]{48, 49, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 1, 5, 0, 4, 32});
    //     byteBuffer.put(hash);
    //     return byteBuffer.array();
    // }

}
