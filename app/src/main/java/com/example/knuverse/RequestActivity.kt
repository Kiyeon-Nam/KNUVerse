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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

        binding.btnStartDate.setOnClickListener {
            showDatePickerDialog(true)
        }

        binding.btnEndDate.setOnClickListener {
            showDatePickerDialog(false)
        }

        binding.btnRegister.setOnClickListener {
            val title = binding.titleInputField.text.toString()
            val content = binding.contentInputField.text.toString()
            val isPartnership = binding.radioButtonSpring.isChecked // Ensure this switch exists in your layout

            if (title.isNotEmpty() && content.isNotEmpty() && ::uri.isInitialized) {
                CoroutineScope(Dispatchers.IO).launch {
                    uploadData(title, content, isPartnership, uri)
                }
            } else {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            }
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

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        this.let { context ->
            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear.${selectedMonth + 1}.$selectedDay"
                if (isStartDate) {
                    startDate = selectedDate
                    binding.txtStartDate.text = selectedDate // 시작일 텍스트 설정
                    binding.datePreviewText.text = "$startDate ~ $endDate" // 프리뷰 텍스트 업데이트

                    // 종료일이 설정되어 있을 경우 검증
                    if (endDate.isNotEmpty() && !isValidDateRange(startDate, endDate)) {
                        Toast.makeText(this, "시작일이 종료일보다 늦습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    endDate = selectedDate
                    binding.txtEndDate.text = selectedDate // 종료일 텍스트 설정
                    binding.datePreviewText.text = "$startDate ~ $endDate" // 프리뷰 텍스트 업데이트

                    // 시작일이 설정되어 있을 경우 검증
                    if (startDate.isNotEmpty() && !isValidDateRange(startDate, endDate)) {
                        Toast.makeText(this, "시작일이 종료일보다 늦습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }, year, month, day).show() // DatePickerDialog를 바로 보여줌
        }
    }

    private suspend fun uploadData(title: String, content: String, isPartnership: Boolean, uri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.png")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.await() // Upload task가 완료될 때까지 대기
        val downloadUri = storageRef.downloadUrl.await() // 다운로드 URL을 대기

        val translator = PapagoTranslator()
        var translatedTitle: String
        var translatedContent: String

        try {
            translatedTitle = translator.translateText("auto", "en", title) ?: title
        } catch (e: Exception) {
            Log.e("TranslationError", "Title translation failed", e)
            translatedTitle = title
        }

        try {
            translatedContent = translator.translateText("auto", "en", content) ?: content
        } catch (e: Exception) {
            Log.e("TranslationError", "Content translation failed", e)
            translatedContent = content
        }

        // 날짜 검증
        if (!isValidDateRange(startDate, endDate)) {
            Toast.makeText(this, "시작일이 종료일보다 늦거나 같을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val temp = UUID.randomUUID().toString()

        // 한국어 데이터 저장
        val koreanData = DocumentData(
            documentId = temp,
            title = title,
            content = content,
            startDate = startDate,
            endDate = endDate,
            isPartnership = isPartnership,
            imageUrls = listOf(downloadUri.toString())
        )

        db.collection("Partnerships")
            .add(koreanData.toMap())
            .addOnSuccessListener {
                Toast.makeText(this@RequestActivity, "한국어 데이터가 성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RequestActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 Activity 종료
            }

        // 영어 데이터 저장
        val englishData = DocumentData(
            documentId = temp,
            title = translatedTitle,
            content = translatedContent,
            startDate = startDate,
            endDate = endDate,
            isPartnership = isPartnership,
            imageUrls = listOf(downloadUri.toString())
        )

        db.collection("Partnerships_en")
            .add(englishData.toMap())
            .addOnSuccessListener {
                Toast.makeText(this@RequestActivity, "영어 데이터가 성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RequestActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 Activity 종료
            }
    }

    private fun isValidDateRange(startDate: String, endDate: String): Boolean {
        val startParts = startDate.split(".").map { it.toInt() }
        val endParts = endDate.split(".").map { it.toInt() }

        // 날짜 비교
        return if (startParts.size == 3 && endParts.size == 3) {
            val startCal = Calendar.getInstance().apply {
                set(startParts[0], startParts[1] - 1, startParts[2])
            }
            val endCal = Calendar.getInstance().apply {
                set(endParts[0], endParts[1] - 1, endParts[2])
            }
            startCal <= endCal
        } else {
            false
        }
    }

    private fun DocumentData.toMap(): Map<String, Any> {
        return mapOf(
            "documentId" to documentId,
            "title" to title,
            "content" to content,
            "startDate" to startDate,
            "endDate" to endDate,
            "isPartnership" to isPartnership,
            "imageUrls" to imageUrls
        )
    }
}