import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.io.*;

public class RsaSign {

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Missing command line argument.");
            System.out.println("Usage: java RsaSign {v|s} {filename.rsa}");
            System.exit(1);
        }

        char flag = args[0].charAt(0);
        String filename = args[1];

        if (flag == 'v') {
            verify(filename);
        } else if (flag == 's') {
            sign(filename);
        } else {
            System.out.println("Invalid flag!");
            System.out.println("Flag must be v (verify) or s (sign)");
        }

    }

    private static void verify(String filename) {
        System.out.println("Verifying key...");
        try {
            // read in the file to hash
            Path path = Paths.get(filename);
            byte[] data = Files.readAllBytes(path);

            // create class instance to create SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // process the file
            md.update(data);
            // generate a has of the file
            byte[] digest = md.digest();

            // Check if the sig file exists
            String sig_file = filename.concat(".sig");
            File exists = new File(sig_file);
            if (!exists.exists()) {
                System.out.println("The signature key does not exist!");
                System.out.println("Unable to verify file!");
                System.exit(1);
            }

            // get signature from the file
            byte[] sig_bytes = new byte[(int) exists.length()];
            FileInputStream fis = new FileInputStream(sig_file);
            fis.read(sig_bytes);
            fis.close();

            // Check if the pub file exists
            exists = new File("pubkey.rsa");
            if (!exists.exists()) {
                System.out.println("The public key does not exist!");
                System.out.println("Unable to verify file!");
                System.exit(1);
            }
            // get e and n from the private key file.
            int e_length = (int) exists.length() - 129;
            byte[] e_bytes = new byte[e_length];
            byte[] n_bytes = new byte[129];
            fis = new FileInputStream("pubkey.rsa");
            fis.read(e_bytes, 0, e_length);
            fis.read(n_bytes, 0, 129);
            fis.close();

            // Encrypt the data
            LargeInteger decrypt = new LargeInteger(sig_bytes).modularExp(new LargeInteger(e_bytes), new LargeInteger(n_bytes));
            if (decrypt.equals(new LargeInteger(digest))) {
                System.out.println("Signature Verified!");
            } else {
                System.out.println("The signature is invalid. Unable to verify.");
            }


        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private static void sign(String filename) {
        System.out.println("Signing file...");
        System.out.println("This may take a few seconds...");
        // Hash stuff from HashEx.java
        try {
            // read in the file to hash
            Path path = Paths.get(filename);
            byte[] data = Files.readAllBytes(path);

            // create class instance to create SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // process the file
            md.update(data);
            // generate a has of the file
            byte[] digest = md.digest();

            // Check if the file exists
            File exists = new File("privkey.rsa");
            if (!exists.exists()) {
                System.out.println("The private key does not exist!");
                System.out.println("Unable to sign file!");
                System.exit(1);
            }
            // get d and n from the private key file.
            int d_length = (int) exists.length() - 129;
            byte[] d_bytes = new byte[d_length];
            byte[] n_bytes = new byte[129];
            FileInputStream fis = new FileInputStream("privkey.rsa");
            fis.read(d_bytes, 0, d_length);
            fis.read(n_bytes, 0, 129);
            fis.close();

            LargeInteger d = new LargeInteger(d_bytes);
            LargeInteger n = new LargeInteger(n_bytes);
            LargeInteger digestli = new LargeInteger(digest);


            // Decrypt the data
            //digest = new byte[] {99};
            LargeInteger decrypt = digestli.modularExp(d, n);
            FileOutputStream signature = new FileOutputStream(filename + ".sig");
            signature.write(decrypt.getVal());
            signature.close();

            System.out.println("File signed successfully!");

        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

}
