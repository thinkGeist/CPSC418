/*
 * decryptFile.java
 *
 * Nathan Geist
 * 10152796
 * October 4th 2016
 *
 */
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;


public class decryptFile {
    private static KeyGenerator keyGen = null;
    private static SecretKey secKey = null;
    private static byte[] raw = null;
    private static SecretKeySpec secKeySpec = null;
    private static Cipher secCipher = null;

    public static void main(String[] args) {
        String fileName = args[0];
        byte[] seed = args[1].getBytes();
        byte[] plainText;

        try {
            int readBytes = 0;
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] cipherText = new byte[inputStream.available()];
            readBytes = inputStream.read(cipherText);
            inputStream.close();
            // Run algorithm to digest file to get MD5 checksum
            // Start AES encryption
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            sr.setSeed(seed);

            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, sr);
            secKey = keyGen.generateKey();

            raw = secKey.getEncoded();

            secKeySpec = new SecretKeySpec(raw, "AES");
            secCipher = Cipher.getInstance("AES");
            plainText = aesDecrypt(cipherText);

            // Split the old digest off of the decrypted file.
            byte[] fileDigest = new byte[16];
            byte[] msg = new byte[plainText.length - 16];
            System.arraycopy(plainText, 0, msg, 0, msg.length);
            System.arraycopy(plainText, msg.length, fileDigest, 0, fileDigest.length);



            // Calculate the actual digest of the now split off message
            byte[] digest = DigestMe.execute(msg);

            if(Arrays.equals(digest, fileDigest)) {
                System.out.println("Item verified, saving resulting file...");
                FileOutputStream outFile = new FileOutputStream(fileName);
                outFile.write(msg);

                System.out.println("Item decrypted and saved to local drive at: " + fileName);

            }
            else{
                throw new MessageAlteredException();
            }

        }
        catch (MessageAlteredException | BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Message was altered, not saving resulting file");
        }
        catch(Exception e){
            e.printStackTrace();
          }
    }

    public static byte[] aesDecrypt(byte[] cipherText) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException{
        byte[] plainText = null;

            secCipher.init(Cipher.DECRYPT_MODE, secKeySpec);
            plainText = secCipher.doFinal(cipherText);

        return plainText;

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
}
