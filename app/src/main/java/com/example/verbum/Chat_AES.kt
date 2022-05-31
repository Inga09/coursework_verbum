package com.example.verbum

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

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

            FER_DATABASE_ROOT.addValueEventListener(ValueEventListener() {
                @Override
                public fun onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    stringMessage = (String) dataSnapshot . getValue ();
                    if (stringMessage != null) {
                        stringMessage = stringMessage.substring(1, stringMessage.length() - 1);
                    }
                    assert stringMessage != null;
                    String[] stringMessageArray;
                    var stringMessageArray = ", ".split(stringMessage);
                    Arrays.sort(stringMessageArray);
                    String[] stringFinal = new String[stringMessageArray.length * 2];

                    try {
                        for (int i = 0; i < stringMessageArray.length; i++) {
                            String[] stringKeyValue = stringMessageArray [i].split("=", 2);
                            stringFinal[2 * i] =
                                (String) android . text . format . DateFormat . format ("dd-MM-yyyy hh:mm:ss", Long.parseLong(stringKeyValue[0]));
                            stringFinal[2 * i + 1] = AESDecryptionMethod(stringKeyValue[1]);
                        }


                        listView.setAdapter(
                            new ArrayAdapter < String >(
                                Chat_AES.this,
                                android.R.layout.simple_list_item_1,
                                stringFinal
                            )
                        );
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }

                }

                @Override
                fun onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }


        private fun  AESEncryptionMethod(string: String ){
            var    stringByte: ByteArray  = string.getBytes();
            var  encrypteByte:  ByteArray = byte[stringByte.length];

            try {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
                encrypteByte=cipher.doFinal(stringByte);
            } catch (e: InvalidKeyException) {
                e.printStackTrace();
            } catch (e: BadPaddingException) {
                e.printStackTrace();
            } catch (e: IllegalBlockSizeException) {
                e.printStackTrace();
            }
            String returnString = null;
            try {
                returnString = String(encrypteByte,"ISO-8859-1");
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace();
            }
            return returnString;
        }
        private fun AESDecryptionMethod(String string) throws UnsupportedEncodingException {
            byte[] EncryptedByte = string.getBytes("ISO-8859-1");
            String decryptedString = string;

            byte[] decryption;

            try {
                decipher.init(cipher.DECRYPT_MODE, secretKeySpec);
                decryption = decipher.doFinal(EncryptedByte);
                decryptedString = new String(decryption);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            return decryptedString;
        }

        public void sendButton(View view) {
            Date date=new Date();
            databaseReference.child(Long.toString(date.getTime())).setValue(AESEncryptionMethod(editText.getText().toString()));
            editText.setText("");
        }
    }
    }




