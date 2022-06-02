package com.example.verbum

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.io.UnsupportedEncodingException
import java.security.AccessController.getContext
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.time.LocalDate
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

class Chat_AES: AppCompatActivity() {
    private lateinit var editText: EditText;
    private lateinit var listView: ListView;
    private lateinit var FER_DATABASE_ROOT: DatabaseReference;

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    private lateinit var stringMessage: String;
    private var encryptionKey: ByteArray =
        byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6)
    private lateinit var cipher: Cipher;
    private lateinit var decipher: Cipher;
    private lateinit var secretKeySpec: SecretKeySpec;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_single_chat);
        editText = findViewById(R.id.chat_input_message);
        listView = findViewById(R.id.chat_swipe_refresh);

        try {
            FER_DATABASE_ROOT = FirebaseDatabase.getInstance().getReference("message");

            try {
                cipher = Cipher.getInstance("AES");
                decipher = Cipher.getInstance("AES");
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace();
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace();
            }
            secretKeySpec = SecretKeySpec(encryptionKey, "AES")

            FER_DATABASE_ROOT.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    stringMessage = dataSnapshot.getValue().toString()
                    if (stringMessage != null) {
                        stringMessage = stringMessage.substring(1, stringMessage.length - 1);
                    }
                    assert(stringMessage != null)
                    //val strs = "name, 2012, 2017".split(",").toTypedArray()
                    //val stringMessageArray = arrayOf(", ".split(stringMessage))
                    val stringMessageArray = stringMessage.split(", ").toTypedArray()
                    Arrays.sort(stringMessageArray);
                    var stringFinalInt = arrayOf(stringMessageArray.count() * 2)
                    var stringFinal = stringFinalInt.map { it.toString() }.toTypedArray()

                    try {
                        for (i in stringMessageArray.indices) {
                            //var stringKeyValue = arrayOf(stringMessageArray[i].split("=", limit = 2))
                            var stringKeyValue =
                                stringMessageArray[i].split("=", limit = 2).toTypedArray()
                            stringFinal[2 * i] = android.text.format.DateFormat.format(
                                "dd-MM-yyyy hh:mm:ss",
                                stringKeyValue[0].toLong()
                            ).toString()
                            stringFinal[2 * i + 1] =
                                AESDecryptionMethod(stringKeyValue[1])
                        }


                        listView.setAdapter(
                             ArrayAdapter <String>(
                                 this@Chat_AES,
                                android.R.layout.simple_list_item_1,
                                stringFinal
                            )
                        );
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            });
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }


    public fun AESEncryptionMethod(string: String) :String {
        //var stringByte: ByteArray = string.getBytes();
        //private var encryptionKey: ByteArray =  byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6)
        var stringByte: ByteArray = string.toByteArray()
        //var encrypteByte: ByteArray = byte[stringByte.length];
        var encrypteByte: ByteArray = byteArrayOf(stringByte.count().toByte())

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            encrypteByte = cipher.doFinal(stringByte);
        } catch (e: InvalidKeyException) {
            e.printStackTrace();
        } catch (e: BadPaddingException) {
            e.printStackTrace();
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace();
        }
        var returnString = "";
        try {
            //returnString = String(encrypteByte, "ISO-8859-1")
            returnString = String(encrypteByte, charset("ISO-8859-1"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace();
        }
        return returnString;
    }

    public fun AESDecryptionMethod(string: String) :String
    {
        var EncryptedByte: ByteArray = string.toByteArray(charset("ISO-8859-1"))
        var decryptedString = string

        var decryption = byteArrayOf()

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            decryption = decipher.doFinal(EncryptedByte)
            decryptedString = decryption.toString()
        } catch (e: InvalidKeyException ) {
            e.printStackTrace();
        } catch (e: BadPaddingException ) {
            e.printStackTrace();
        } catch (e: IllegalBlockSizeException ) {
            e.printStackTrace();
        }
        return decryptedString;
    }


    public fun sendButton(view: View)
    {
        var date = Date()
        FER_DATABASE_ROOT.child((date.time).toString()).setValue(AESEncryptionMethod(editText.text.toString()));
        editText.setText("");
    }
}




