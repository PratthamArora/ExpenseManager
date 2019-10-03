package com.prattham.expenseManager.ui


import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.prattham.expenseManager.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mProgress = ProgressDialog(this)

        if (mAuth.currentUser != null) {
            startActivity<Default>()
            finish()
        }
        //forgot password
        
        forgot_pass.setOnClickListener {
            val emailReset = email_login.text.toString()
            if (emailReset.isNotEmpty()) {
                mAuth.sendPasswordResetEmail(emailReset).addOnCompleteListener {
                    if (it.isSuccessful) {
                        longToast("Please check your email to reset password")
                    } else {
                        toast("error")
                    }
                }
            } else {
                toast("Please enter valid email")
            }

        }

        btn_login.setOnClickListener {
            if (TextUtils.isEmpty(email_login.text.toString().trim())) {
                email_login.error = "Required Field!"
                return@setOnClickListener    // check this
            }
            if (TextUtils.isEmpty(password_login.text.toString().trim())) {
                password_login.error = "Required Field!"
                return@setOnClickListener
            }
            mProgress.setMessage("Processing")
            mProgress.show()

            mAuth.signInWithEmailAndPassword(
                email_login.text.toString(),
                password_login.text.toString()
            )
                .addOnCompleteListener {
                    mProgress.dismiss()
                }
                .addOnSuccessListener {
                    startActivity<Default>()
                    finish()
                    toast("Log In Complete")     //USING ANKO LIB
                    mProgress.dismiss()
                }
                .addOnFailureListener {
                    toast("Log In Failed")
                    mProgress.dismiss()
                }

        }
        signup_text.setOnClickListener {
            startActivity<RegistrationActivity>()  //USING ANKO LIB
            finish()

        }
    }

}
