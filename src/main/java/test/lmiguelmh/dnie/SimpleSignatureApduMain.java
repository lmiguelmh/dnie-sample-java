package test.lmiguelmh.dnie;

import test.lmiguelmh.dnie.util.DnieUtil;

import javax.smartcardio.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author lmiguelmh
 */
public class SimpleSignatureApduMain {
    public static void main(String[] args) throws CardException, NoSuchAlgorithmException, IOException {

        // list available terminals
        final TerminalFactory factory = TerminalFactory.getDefault();
        final List<CardTerminal> terminals = factory.terminals().list();
        System.out.println("Terminals: " + terminals);

        // use the first terminal (!)
        final CardTerminal terminal = terminals.get(0);

        // connect to the card and let Java decide transmission protocol (T=0, T=1...)
        final Card card = terminal.connect("*");
        System.out.println("card: " + card);

        // open a logical channel
        final CardChannel channel = card.getBasicChannel();

        ResponseAPDU r;

        // select MF
        r = channel.transmit(new CommandAPDU(DnieUtil.toBytes("00A40000023F00")));
        System.out.println("select MF: " + DnieUtil.toString(r.getBytes()));

        // select PKI
        r = channel.transmit(new CommandAPDU(DnieUtil.toBytes("00A404000EE828BD080FD25047656E65726963")));
        System.out.println("select PKI: " + DnieUtil.toString(r.getBytes()));

        // verify PIN
        r = channel.transmit(new CommandAPDU(DnieUtil.toBytes("0020008106313132323333")));
        System.out.println("verify PIN: " + DnieUtil.toString(r.getBytes()));

        // manage SE
        r = channel.transmit(new CommandAPDU(DnieUtil.toBytes("002241B60680018A840181")));
        System.out.println("manage SE: " + DnieUtil.toString(r.getBytes()));

        // perform SO
        byte[] hash = getHashFromFile("face.jp2");
        byte[] toSign = toPKCS1(hash);
        r = channel.transmit(new CommandAPDU(DnieUtil.toBytes("002A9E9A30" + DnieUtil.toString(toSign))));
        System.out.println("perform SO: " + DnieUtil.toString(r.getBytes()));

        // disconnect
        card.disconnect(false);
    }

    public static byte[] getHashFromFile(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)))) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
        }
        return digest.digest();
    }

    public static byte[] toPKCS1(byte[] hash) {
        byte[] header = new byte[]{48, 46, 48, 10, 6, 8, 42, -122, 72, -122, -9, 13, 2, 9, 4, 32};
        ByteBuffer byteBuffer = ByteBuffer.allocate(header.length + hash.length);
        byteBuffer.put(header);
        byteBuffer.put(hash);
        return byteBuffer.array();
    }
}
