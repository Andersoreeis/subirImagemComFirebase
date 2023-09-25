package br.senai.sp.jandira.upload_ds3t

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import br.senai.sp.jandira.upload_ds3t.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// ATRIBUTOS

// REPRESENTAÇÃO DA CLASSE DE MANIPULAÇÃO DE OBJETOS DE VIEWS DA TELAS
private lateinit var binding: ActivityMainBinding

// REPESENTAÇÃO DA CLASSE DE MANIOULAÇÃO DE ENDEREÇO (LOCAL) DE ARQUIVOS
private var imageUri: Uri? = null

// RREFERENCIA PARA ACESSO E MANIPULAÇÃO DO CLOUD STORAGE
private lateinit var storageRef: StorageReference

// RREFERENCIA PARA ACESSO E MANIPULAÇÃO DO FIREBASE
private lateinit var firebaseFirestore: FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVars()
        registrerClickEvents()


    }

    // INICILIAZAÇÃO DOS ATRIBUTOS DO FIREBASE
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        firebaseFirestore = FirebaseFirestore.getInstance()

    }
    private val resultLaucher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        binding.imageView.setImageURI(it)
    }

    private fun registrerClickEvents() {
        binding.imageView.setOnClickListener {
            resultLaucher.launch("image/*")
        }
        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }
    }
    // UPLOAD DE IMAGENS NO FIREBASE
    private fun uploadImage() {

        binding.progressBar.visibility = View.VISIBLE

        storageRef = storageRef.child(System.currentTimeMillis().toString())


        //  /** UPLOADv1 Inicio  **/
//        imageUri?.let {
//            storageRef.putFile(it).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Upload realizado com sucesso", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Houve um erro ao tentar realziar o upload ",
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                }
//                binding.progressBar.visibility = View.GONE
//            }
//
//        }
//    }
//
//
//    //lançador de recursos extérnos da aplicação
//
//
//
//    // tratamento de eventos de click

//}
///** UPLOADv1 Fim  **/

///// PROCESSO DE UPLOAD - V2 /////
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()

                        firebaseFirestore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->

                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Uploaded realizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Erro ao tentar realizar o upload",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                binding.progressBar.visibility = View.GONE
                                binding.imageView.setImageResource(R.drawable.upload)

                            }
                    }

                } else {

                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

                }

                //BARRA DE PROGRESSO DO UPLOAD
                binding.progressBar.visibility = View.GONE

                //TROCA A IMAGEM PARA A IMAGEM PADRÃO
                binding.imageView.setImageResource(R.drawable.upload)

            }
        }
    }
}
///// PROCESSO DE UPLOAD - V2 /////