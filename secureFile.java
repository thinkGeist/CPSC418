/*
 * secureFile.java
 *
 * Nathan Geist
 * 10152796
 * October 4th 2016
 *
 */


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;

public class secureFile {
    private static KeyGenerator keyGen = null;
    private static SecretKey secKey = null;
    private static byte[] raw = null;
    private static SecretKeySpec secKeySpec = null;
    private static Cipher secCipher = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String fileName = args[0];
        byte[] seed = args[1].getBytes();
        byte[] aesCiphertext = null;
        int readBytes = 0;
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] msg = new byte[inputStream.available()];


            // Run algorithm to digest file to get MD5 checksum


            readBytes = inputStream.read(msg);
            inputStream.close();
            byte[] digest = DigestMe.execute(msg);


            // Start AES encryption
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            sr.setSeed(seed);

            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, sr);
            secKey = keyGen.generateKey();


            raw = secKey.getEncoded();

            secKeySpec = new SecretKeySpec(raw, "AES");
            secCipher = Cipher.getInstance("AES");
            // Append digest to message and encrypt
            byte[] toEncrypt = concatenate(msg, digest);
            aesCiphertext = aesEncrypt(toEncrypt);

            FileOutputStream outFile = new FileOutputStream(fileName);
            outFile.write(aesCiphertext);

            outFile.close();
            System.out.println("Item successfully encrypted and saved at: " + fileName);
        }
        catch(FileNotFoundException e){
            System.out.println("File could not be found!");
        }
        catch(IOException e){
            System.out.println("Error while attempting to access file");
        }
        catch(Exception e){
            System.out.println("Unexpected error occurred while attempting to encrypt file");
        }


    }

    public static String toHexString(byte[] block){
        StringBuffer buffer = new StringBuffer();
        int length = block.length;

        for(int i=0; i< length; i++){
            byteToHex(block[i], buffer);
            if (i < length-1)
                buffer.append(":");
        }
        return buffer.toString();
    }

    private static void byteToHex(byte b, StringBuffer buffer){
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buffer.append(hexChars[high]);
        buffer.append(hexChars[low]);



    }

    private static byte[] aesEncrypt(byte[] msg){
        byte[] outBytes = null;
        try{
            secCipher.init(Cipher.ENCRYPT_MODE, secKeySpec);
            outBytes = secCipher.doFinal(msg);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return outBytes;
    }

    /**
     * Concatenate array function.
     * Credit: http://stackoverflow.com/a/80503
     * @param first First array
     * @param second Second array
     * @return Concatenation of the two arrays.
     */
    public static byte[] concatenate(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
