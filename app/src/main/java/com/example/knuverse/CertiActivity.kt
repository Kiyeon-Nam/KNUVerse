package com.example.knuverse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

class CertiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertiBinding
    private lateinit var uri: Uri
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_certi)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        if (user != null) {
            binding.txtUserName.text = getString(R.string.welcome_user, user.displayName)
        } else {
            binding.txtUserName.text = getString(R.string.welcome_default)
        }
        
        // 로그아웃 버튼
        binding.btnlogout.setOnClickListener {
            firebaseAuth.signOut()  // Firebase 로그아웃 처리
            Toast.makeText(this, "로그아웃되었습니다", Toast.LENGTH_SHORT).show()
            binding.txtUserName.text = "사용자님"
        }

        // 이미지뷰를 눌렀을 경우 앨범을 호출
        binding.btnPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            registerForActivityResult.launch(intent)
        }

        // 등록하기 버튼을 눌렀을 경우
        binding.btnRegister.setOnClickListener {
            imageUpload(uri)
        }
    }

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uri = result.data?.data!!
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
            Toast.makeText(this, "인증 요청을 보냈습니다", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // 파일 업로드 실패
            Toast.makeText(this, "인증 요청을 보냈습니다", Toast.LENGTH_SHORT).show()
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
}
