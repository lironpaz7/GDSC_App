package com.gdsc_technion.gdsc_app

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*
import java.util.*
import kotlin.collections.HashMap


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


data class User(
    var name: String? = null,
    var age: String? = null,
    var interests: String? = null,
    var imagePath: String? = null,
    var degree: String? = null,
    var degreeYear: String? = null,
    var shareMyInfo: String? = null,
    var admin: String? = null

)

class ProfileFragment : Fragment() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var userEmail: String
    private lateinit var doc: User
    private lateinit var storageReference: StorageReference
    private lateinit var fireBaseStore: FirebaseStorage
    private lateinit var profilePictureRef: ImageView
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var docRef: DocumentReference
    private var uriRef: Uri? = null
    private var currentState: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fireBaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    profilePictureRef.setImageURI(uri)
                    uriRef = uri
                    Log.d("URI_LOCAL", uri.path.toString())
                }
            }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fAuth = FirebaseAuth.getInstance()

        profilePictureRef = view.findViewById(R.id.profile_picture)
        userEmail = fAuth.currentUser?.email.toString()


        // buttons and Textview
        val saveButton = view.findViewById<ImageButton>(R.id.profile_save_btn)
        saveButton.isEnabled = false

        val arrowButton = view.findViewById<ImageView>(R.id.profile_arrow_btn)
        arrowButton.isEnabled = false
        arrowButton.isVisible = false

        val shareMyInfoButton = view.findViewById<SwitchCompat>(R.id.profile_shareInfoButton)
        shareMyInfoButton.alpha = ALPHA_OFF

        val editButton = view.findViewById<ImageButton>(R.id.profile_edit_btn)
        val abortButton = view.findViewById<ImageButton>(R.id.profile_abort_btn)
        abortButton.isEnabled = false

        val editPictureButton = view.findViewById<ImageButton>(R.id.profile_editPicture_btn)
        editPictureButton.isEnabled = false

        val defaultProfileBtn = view.findViewById<ImageButton>(R.id.profile_default_picture_btn)
        defaultProfileBtn.isEnabled = false


        val textName = view.findViewById<EditText>(R.id.profile_username)
        val textAge = view.findViewById<EditText>(R.id.profile_age)
        textAge.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))

        val textInterests = view.findViewById<EditText>(R.id.profile_interests)
        val textDegree = view.findViewById<EditText>(R.id.profile_degree)
        val textDegreeYear = view.findViewById<EditText>(R.id.profile_degreeYear)
        textDegreeYear.filters = arrayOf<InputFilter>(MinMaxFilter(1, 7))


        // read Firestore data base to update on create view
        val docId = fAuth.currentUser?.email.toString()
        val userNameFromMail = docId.substring(0, docId.indexOf("@"))
        val db = FirebaseFirestore.getInstance()
        docRef = db.collection("users").document(docId)

        docRef.get().addOnSuccessListener { docSnap ->
            //get data class object for easy data assignment
            doc = docSnap.toObject(User::class.java)!!

            // update User data class with Firestore data
            if (doc.name == null) {
                doc.name = userNameFromMail
            }

            if (doc.shareMyInfo == null) {
                doc.shareMyInfo = "no"
            }

            if (doc.age == null) {
                doc.age = ""
            }
            if (doc.degree == null) {
                doc.degree = ""
            }
            if (doc.degreeYear == null) {
                doc.degreeYear = ""
            }

            if (doc.interests == null) {
                doc.interests = ""
            }

            // update text views with firebase info
            textName.setText(doc.name)
            textAge.setText(doc.age)
            textInterests.setText(doc.interests)
            textDegree.setText(doc.degree)
            textDegreeYear.setText(doc.degreeYear)
            shareMyInfoButton.isChecked = doc.shareMyInfo == "yes"

            // updates profile picture
            if (doc.imagePath != null && doc.imagePath != "null") {
                loadImageFromStorage(doc.imagePath.toString())
            } else {
                setDefaultProfilePicture()
            }

        }


        // shareMyInfo button

        shareMyInfoButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                doc.shareMyInfo = "yes"
            } else {
                doc.shareMyInfo = "no"
            }
        }

        // arrow button - degrees picker
        arrowButton.setOnClickListener {
            val popupMenu = PopupMenu(context, arrowButton, Gravity.CENTER)

            // menu options
            popupMenu.menu.add(Menu.NONE, 1, 1, "Data Science and Engineering")
            popupMenu.menu.add(Menu.NONE, 2, 2, "Computer Science")
            popupMenu.menu.add(Menu.NONE, 3, 3, "Electrical Engineering")
            popupMenu.menu.add(Menu.NONE, 4, 4, "Information Systems Engineering")
            popupMenu.menu.add(Menu.NONE, 5, 5, "Industrial Engineering")
            popupMenu.menu.add(Menu.NONE, 6, 6, "Biology")
            popupMenu.menu.add(Menu.NONE, 7, 7, "Mathematics")
            popupMenu.menu.add(Menu.NONE, 8, 8, "Physics")
            popupMenu.menu.add(Menu.NONE, 9, 9, "Medicine")
            popupMenu.menu.add(Menu.NONE, 10, 10, "Mechanical Engineering")
            popupMenu.menu.add(Menu.NONE, 11, 11, "Civil and Environmental Engineering")
            popupMenu.menu.add(Menu.NONE, 12, 12, "Chemistry")
            popupMenu.menu.add(Menu.NONE, 13, 13, "Biotechnology and Food Engineering")
            popupMenu.menu.add(Menu.NONE, 14, 14, "Biomedical Engineering")
            popupMenu.menu.add(Menu.NONE, 15, 15, "Aerospace Engineering")
            popupMenu.menu.add(Menu.NONE, 16, 16, "Education in Science and Technology")
            popupMenu.menu.add(Menu.NONE, 17, 17, "Other")

            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    1 -> {
                        doc.degree = popupMenu.menu.getItem(0).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(0).title.toString())
                    }
                    2 -> {
                        doc.degree = popupMenu.menu.getItem(1).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(1).title.toString())
                    }
                    3 -> {
                        doc.degree = popupMenu.menu.getItem(2).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(2).title.toString())
                    }
                    4 -> {
                        doc.degree = popupMenu.menu.getItem(3).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(3).title.toString())
                    }
                    5 -> {
                        doc.degree = popupMenu.menu.getItem(4).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(4).title.toString())
                    }
                    6 -> {
                        doc.degree = popupMenu.menu.getItem(5).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(5).title.toString())
                    }
                    7 -> {
                        doc.degree = popupMenu.menu.getItem(6).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(6).title.toString())
                    }
                    8 -> {
                        doc.degree = popupMenu.menu.getItem(7).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(7).title.toString())
                    }
                    9 -> {
                        doc.degree = popupMenu.menu.getItem(8).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(8).title.toString())
                    }
                    10 -> {
                        doc.degree = popupMenu.menu.getItem(9).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(9).title.toString())
                    }
                    11 -> {
                        doc.degree = popupMenu.menu.getItem(10).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(10).title.toString())
                    }
                    12 -> {
                        doc.degree = popupMenu.menu.getItem(11).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(11).title.toString())
                    }
                    13 -> {
                        doc.degree = popupMenu.menu.getItem(12).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(12).title.toString())
                    }
                    14 -> {
                        doc.degree = popupMenu.menu.getItem(13).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(13).title.toString())
                    }
                    15 -> {
                        doc.degree = popupMenu.menu.getItem(14).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(14).title.toString())
                    }
                    16 -> {
                        doc.degree = popupMenu.menu.getItem(15).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(15).title.toString())
                    }
                    17 -> {
                        doc.degree = popupMenu.menu.getItem(16).title.toString()
                        textDegree.setText(popupMenu.menu.getItem(16).title.toString())
                    }
                }
                true
            }

        }

        // edit button
        editButton.setOnClickListener {

            // save current data in hashmap
            currentState["name"] = doc.name.toString()
            currentState["age"] = doc.age.toString()
            currentState["interests"] = doc.interests.toString()
            currentState["imagePath"] = doc.imagePath.toString()
            currentState["degree"] = doc.degree.toString()
            currentState["degreeYear"] = doc.degreeYear.toString()
            currentState["shareMyInfo"] = doc.shareMyInfo.toString()

            // set enabled on
            textName.isEnabled = true
            textAge.isEnabled = true
            textInterests.isEnabled = true
            textDegreeYear.isEnabled = true


            // arrow btn enable
            arrowButton.isEnabled = true
            arrowButton.isVisible = true

            // edit picture btn enable
            editPictureButton.isEnabled = true
            editPictureButton.visibility = View.VISIBLE

            // abort btn enable
            abortButton.isEnabled = true
            abortButton.visibility = View.VISIBLE

            // shareInfo btn enable
            shareMyInfoButton.isClickable = true
            shareMyInfoButton.alpha = ALPHA_ON

            // default picture btn enable
            defaultProfileBtn.visibility = View.VISIBLE
            defaultProfileBtn.isEnabled = true

            // save button enable
            saveButton.visibility = View.VISIBLE
            saveButton.isEnabled = true

            // edit button disable
            editButton.alpha = ALPHA_OFF
            editButton.isEnabled = false
        }

        // edit picture button
        editPictureButton.setOnClickListener {
            getContent.launch("image/*")
        }

        // default picture button
        defaultProfileBtn.setOnClickListener {
            doc.imagePath = null
            Toast.makeText(context, "Default image restored", Toast.LENGTH_SHORT).show()
        }


        // abort button

        abortButton.setOnClickListener {
            // set enabled off
            textName.isEnabled = false
            textAge.isEnabled = false
            textInterests.isEnabled = false
            textDegreeYear.isEnabled = false

            // arrow button disable
            arrowButton.isEnabled = false
            arrowButton.isVisible = false

            // edit picture btn disable
            editPictureButton.isEnabled = false
            editPictureButton.visibility = View.INVISIBLE

            // abort btn disable
            abortButton.isEnabled = false
            abortButton.visibility = View.INVISIBLE

            // shareInfo btn disable
            shareMyInfoButton.isClickable = false
            shareMyInfoButton.alpha = ALPHA_OFF

            // default picture btn disable
            defaultProfileBtn.visibility = View.INVISIBLE
            defaultProfileBtn.isEnabled = false

            // save button disable
            saveButton.visibility = View.INVISIBLE
            saveButton.isEnabled = false

            // edit button enable
            editButton.alpha = ALPHA_ON
            editButton.isEnabled = true

            // retreive data from hashmap
            textName.setText(currentState["name"])
            textAge.setText(currentState["age"])
            textInterests.setText(currentState["interests"])
            textDegree.setText(currentState["degree"])
            textDegreeYear.setText(currentState["degreeYear"])
            shareMyInfoButton.isChecked = currentState["shareMyInfo"] == "yes"
            doc.shareMyInfo = currentState["shareMyInfo"]
            doc.imagePath = currentState["imagePath"]
        }


        // save button

        saveButton.setOnClickListener {
            // set enabled off
            textName.isEnabled = false
            textAge.isEnabled = false
            textInterests.isEnabled = false
            textDegreeYear.isEnabled = false

            // arrow button disable
            arrowButton.isEnabled = false
            arrowButton.isVisible = false

            // edit button enable
            editButton.alpha = ALPHA_ON
            editButton.isEnabled = true

            // shareInfo btn disable
            shareMyInfoButton.isClickable = false
            shareMyInfoButton.alpha = ALPHA_OFF

            // abort btn disable
            abortButton.isEnabled = false
            abortButton.visibility = View.INVISIBLE

            // edit picture button disable
            editPictureButton.isEnabled = false
            editPictureButton.visibility = View.INVISIBLE

            // default picture btn disable
            defaultProfileBtn.visibility = View.INVISIBLE
            defaultProfileBtn.isEnabled = false

            // save button disable
            saveButton.visibility = View.INVISIBLE
            saveButton.isEnabled = false

            // save data in User data class
            if (TextUtils.isEmpty(textName.text.toString())) {
                doc.name = userNameFromMail
                textName.setText(doc.name)
            } else {
                doc.name = textName.text.toString()
            }

            doc.age = textAge.text.toString()
            doc.interests = textInterests.text.toString()
            doc.degreeYear = textDegreeYear.text.toString()

            // update Firestore
            uriRef?.let {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                val path = saveToInternalStorage(bitmap)
                doc.imagePath = path
                addUploadRecord()
            }

            docRef.update(
                "name",
                doc.name,
                "age",
                doc.age,
                "interests",
                doc.interests,
                "imagePath", doc.imagePath,
                "degree", doc.degree,
                "degreeYear", doc.degreeYear,
                "shareMyInfo", doc.shareMyInfo
            )
            shareMyInfoButton.isChecked = doc.shareMyInfo == "yes"
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        }

        // back button
        view.findViewById<Button>(R.id.profileBackButton).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_indexFragment)
        }
    }

    private fun setDefaultProfilePicture() {
        doc.imagePath = null
        profilePictureRef.setImageResource(R.drawable.profile)
    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin = 0
        private var intMax = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }


    private fun addUploadRecord() {
        docRef.update("imagePath", doc.imagePath)

    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(requireContext())
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    private fun loadImageFromStorage(path: String) {
        try {
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            profilePictureRef.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}