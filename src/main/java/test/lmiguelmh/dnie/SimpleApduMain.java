package test.lmiguelmh.dnie;

import test.lmiguelmh.dnie.util.DnieUtil;

import javax.smartcardio.*;
import java.util.List;

/**
 * @author lmiguel
 */
public class SimpleApduMain {
    public static void main(String[] args) throws CardException {

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

        int cla, ins, p1, p2;
        ResponseAPDU r;

        // send ISO 7816's SELECT apdu
        // https://cardwerk.com/smart-card-standard-iso7816-4-section-6-basic-interindustry-commands/
        cla = 0x00;
        ins = 0xA4;
        p1 = 0x00;
        p2 = 0x00;
        r = channel.transmit(new CommandAPDU(cla, ins, p1, p2));
        System.out.println("response: " + DnieUtil.toString(r.getBytes()));

        // disconnect
        card.disconnect(false);
    }
}
