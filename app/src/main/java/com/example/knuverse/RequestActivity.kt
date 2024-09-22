package com.example.knuverse

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.knuverse.databinding.ActivityRequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private lateinit var uri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RequestActivity", "onCreate 시작") // 에러 확인용 로그

        binding = DataBindingUtil.setContentView(this, R.layout.activity_request)
        Log.d("RequestActivity", "setContentView 완료")



        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        setupDatePickers()
        Log.d("RequestActivity", "setupDatePickers 호출 완료")
        setupContentPreviewUpdater()
        Log.d("RequestActivity", "setupContentPreviewUpdater 호출 완료")

        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        Log.d("RequestActivity", "Firebase 인증 초기화 완료")

        val user = firebaseAuth.currentUser
        Log.d("RequestActivity", "현재 사용자 가져오기 시도")

        val userName = if (user != null && !user.displayName.isNullOrEmpty()) {
            "${user.displayName}님"
        } else {
            "사용자"
        }

        binding.userName = userName
        Log.d("RequestActivity", "사용자 이름 설정 완료: $userName")

        // 제목을 textview에 반영
        binding.titleInputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.previewText.text = s.toString()  // 입력된 텍스트를 미리보기 TextView에 반영
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // 로그아웃 버튼
        binding.btnlogout.setOnClickListener {
            firebaseAuth.signOut()  // Firebase 로그아웃 처리
            Toast.makeText(this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show()
            binding.txtUserName.text = "사용자님"
        }

        // 버튼을 눌렀을 경우 앨범을 호출
        binding.btnPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
        }

        // 등록하기 버튼을 눌렀을 경우
        binding.btnRegister.setOnClickListener {
            imageUpload(uri)
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        // 시작 날짜 선택 리스너
        val startDateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            startDate = formatDate(year, month, dayOfMonth)
            updateDatePreview()  // 미리보기 업데이트
        }

        // 종료 날짜 선택 리스너
        val endDateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            endDate = formatDate(year, month, dayOfMonth)
            updateDatePreview()  // 미리보기 업데이트
        }

        // 시작 날짜 클릭 시 DatePickerDialog 표시
        binding.txtStartDate.setOnClickListener {
            DatePickerDialog(
                this, startDateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 종료 날짜 클릭 시 DatePickerDialog 표시
        binding.txtEndDate.setOnClickListener {
            DatePickerDialog(
                this, endDateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // 날짜 포맷팅 함수
    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year.${month + 1}.$day"
    }

    // 날짜 미리보기 업데이트 함수
    private fun updateDatePreview() {
        binding.datePreviewText.text = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            "$startDate ~ $endDate"
        } else {
            "날짜를 선택해주세요."
        }
    }

    private fun setupContentPreviewUpdater() {
        binding.contentInputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전에 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
                binding.contentPreviewText.text = s.toString()  // 입력된 내용을 미리보기 텍스트뷰에 설정
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후에 호출
            }
        })
    }

    companion object {
        private const val START_DATE = 1
        private const val END_DATE = 2
        private var currentPicker = START_DATE

        fun formatDate(year: Int, month: Int, day: Int): String {
            return "$year-${month + 1}-$day"
        }
    }

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                uri = result.data?.data!!
                // ImageView에 표시
                binding.imageArea.setImageURI(uri)
                binding.txtFileStatus.text = "파일 선택 완료"
            }
        }


    // 파일을 Firebase Storage에 업로드
    private fun imageUpload(uri: Uri) {
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("image")
        // storage에 저장할 파일명 선언
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainsRef = storageRef.child("${fileName}.png")

        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // 파일 업로드 성공
            Toast.makeText(this, "업로드되었습니다", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // 파일 업로드 실패
            Toast.makeText(this, "업로드되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // Firebase Storage에서 이미지를 다운로드하여 Glide로 ImageView에 표시
    private fun imageDownload() {
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("image")
        // storage에서 가져올 파일명 선언
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainsRef = storageRef.child("${fileName}.png")

        mountainsRef.downloadUrl.addOnSuccessListener { uri ->
            // 파일 다운로드 성공
        }.addOnFailureListener {
            // 파일 다운로드 실패
            Toast.makeText(this, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imageDelete() { // 이미지 삭제
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("image")
        // storage에서 삭제 할 파일명
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainsRef = storageRef.child("${fileName}.png")

        val deleteTask = mountainsRef.delete()
        deleteTask.addOnCompleteListener {
            // 파일 삭제 성공
        }.addOnFailureListener {
            // 파일 삭제 실패
        }
    }

    private fun uploadData(inputText: String, uri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.png")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val data = hashMapOf(
                    "text" to inputText,
                    "imageUrl" to downloadUri.toString(),
                    "timestamp" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                )

                db.collection("uploads")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "모든 데이터가 성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Firestore에 데이터 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
}