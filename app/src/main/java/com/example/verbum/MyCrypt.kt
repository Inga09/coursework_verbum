package com.example.verbum

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
//шифрование
class MyCrypt {

    val secretKey: String = "662ede816988e58fb6d057d9d85605e0"

    /*private lateinit var cipher: Cipher
    private lateinit var decipher: Cipher
    private var encryptionKey: ByteArray = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6)
    private lateinit var secretKeySpec: SecretKeySpec;
    private lateinit var stringMessage: String;*/


    /*constructor()
    {
        try
        {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        }
        catch (e: NoSuchAlgorithmException)
        {
            e.printStackTrace()
        }
        catch (e: NoSuchPaddingException)
        {
            e.printStackTrace()
        }

        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
    }

    public fun doEncrypt(message: String) : String
    {
        var stringByte: ByteArray = message.toByteArray(charset("UTF8"))
        var encrypteByte: ByteArray = byteArrayOf(stringByte.count().toByte())

        try
        {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            encrypteByte = cipher.doFinal(stringByte)
        }
        catch (e: InvalidKeyException)
        {
            e.printStackTrace()
        }
        catch (e: BadPaddingException)
        {
            e.printStackTrace()
        }
        catch (e: IllegalBlockSizeException)
        {
            e.printStackTrace()
        }
        var returnString = ""
        try
        {
            returnString = String(encrypteByte, charset("UTF8"))
        }
        catch (e: UnsupportedEncodingException)
        {
            e.printStackTrace();
        }
        return returnString;
    }

    public fun doDecrypt(message: String) : String
    {
        var EncryptedByte: ByteArray = message.toByteArray()
        var decryptedString = message

        var decryption = byteArrayOf()

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            decryption = decipher.doFinal(EncryptedByte)
            decryptedString = decryption.toString(charset("UTF8"))
        }
        catch (e: InvalidKeyException )
        {
            e.printStackTrace();
        }
        catch (e: BadPaddingException )
        {
            e.printStackTrace();
        }
        catch (e: IllegalBlockSizeException )
        {
            e.printStackTrace();
        }
        return decryptedString;
    }*/

    fun encrypt(strToEncrypt: String): String {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secretKey.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                    Base64.encode(cipherText)
                )
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return strToEncrypt
    }

    fun decrypt(strToDecrypt: String): String {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secretKey.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt.trim { it <= ' ' }.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return strToDecrypt
    }
}