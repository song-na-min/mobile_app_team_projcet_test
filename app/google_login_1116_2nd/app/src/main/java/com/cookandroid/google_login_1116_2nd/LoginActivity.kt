package com.cookandroid.google_login_1116_2nd

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.google_login_1116_2nd.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
//import com.google.firebase.quickstart.auth.R
import com.cookandroid.google_login_1116_2nd.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    // private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView (binding.root)
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("input_your_token?")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        moveMainPage(firebaseAuth?.currentUser)//자동로그인
    }
    private fun signInWithGoogle() {
        googleSignInClient?.signInIntent?.run {
            startActivityForResult(this, REQ_CODE_SIGN_IN) }
    }

    companion object { private const val REQ_CODE_SIGN_IN = 1000 }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SIGN_IN) {
            val signInTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { val account = signInTask.getResult(ApiException::class.java)
                onGoogleSignInAccount(account) }
            catch (e: ApiException) {
                e.printStackTrace() } }
    }
    private fun onGoogleSignInAccount(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener {
                onFirebaseAuthTask(it) } }
    }private fun onFirebaseAuthTask(task: Task<AuthResult>) {
        if (task.isSuccessful) { // Google로 로그인 성공
            Log.d(this::class.java.simpleName, "User Email :: ${task.result?.user?.email}")
            moveMainPage(task.result?.user)
        }
        else { // Google로 로그인 실패
            Log.d(this::class.java.simpleName, "Firebase Login Failure.")
        }
    }
    fun moveMainPage(user:FirebaseUser?){
        if(user!=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }




}