import java.util.*;
import java.io.*;

public class RsaKeyGen {
    private static final byte[] ONE = {(byte) 1};
    static LargeInteger one = new LargeInteger(ONE);
    static Random r = new Random();
    static LargeInteger p, q, n, phi_n, e;

    public static void main(String args[]) {
        System.out.println("Generating RSA key pair...");
        generate_key_pair();
        System.out.println("Key pairs generated successfully!");
    }

    private static void generate_key_pair() {
        //Pick p and q to be random primes of an appropriate size to generate a 512-bit key
        // You multiply them, so in order to get a 512 bit key, you need a 256 bit p and q.
        System.out.println("Generating random Large Integer for p...");
        p = new LargeInteger(512, r);
        System.out.println("Generated p successfully!");

        System.out.println("Generating random Large Integer for q...");
        q = new LargeInteger(512, r);
        System.out.println("Generated q successfully!");

        //Calculate n as p*q
        System.out.println("Calculating n as p*q...");
        n = p.multiply(q);
        System.out.println("Calculated n successfully!");

        //Calculate φ(n) as (p-1)*(q-1)
        System.out.println("Calculating phi_n as (p-1)*(q-1)...");
        phi_n = (p.subtract(one)).multiply((q.subtract(one)));
        System.out.println("Calculated phi_n successfully!");

        //Choose an e such that 1 < e < φ(n) and gcd(e, φ(n)) = 1 (e must not share a factor with φ(n))
        System.out.println("Finding e...");
        e = new LargeInteger(512, r);
        while (!valid_e()) {
            e = new LargeInteger(512, r);
        }
        System.out.println("Found valid e successfully!");

        //Determine d such that d = e⁻¹ mod φ(n)
        System.out.println("Calculating d...");
        LargeInteger d = e.modularExp(one.negate(), phi_n);
        System.out.println("Calculated d successfully!");


        System.out.println("Writing public key...");
        write_pub_keys(e, n);
        System.out.println("Writing private key...");
        write_priv_keys(d, n);

        System.out.println("Rsa keys successfully generated!");
        System.out.println("Bye!");
    }

    private static boolean valid_e() {
        // e is greater than phi
        if (phi_n.subtract(e).isNegative()) {
            System.out.println("Randomly generated e is not less than phi_n...");
            return false;
        }
        LargeInteger[] xgcd = phi_n.XGCD(e);
        // e and phi share a factor
        if (xgcd[0].getVal()[xgcd[0].length() - 1] != 1) {
            return false;
        }
        return true;
    }

    private static void write_pub_keys(LargeInteger e, LargeInteger n) {
        try {
            FileOutputStream pub_key = new FileOutputStream("pubkey.rsa");
            pub_key.write(e.getVal());
            pub_key.write(n.getVal());
            pub_key.close();

        } catch (Exception ex) {
            System.out.println("Error Writing Public Key File");
        }
    }

    private static void write_priv_keys(LargeInteger d, LargeInteger n) {
        try {
            FileOutputStream priv_key = new FileOutputStream("privkey.rsa");
            priv_key.write(d.getVal());
            priv_key.write(n.getVal());
            priv_key.close();

        } catch (Exception ex) {
            System.out.println("Error Writing Private Key File");
        }
    }
}
