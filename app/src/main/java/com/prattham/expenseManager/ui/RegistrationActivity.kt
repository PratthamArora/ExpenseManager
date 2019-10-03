package com.prattham.expenseManager.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.prattham.expenseManager.R
import kotlinx.android.synthetic.main.activity_registration.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class RegistrationActivity : AppCompatActivity() {


    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val mProgressBar = ProgressDialog(this)


        signin_text.setOnClickListener {
            startActivity<MainActivity>()
        }

        btn_signup.setOnClickListener {
            if (TextUtils.isEmpty(email_signup.text.toString().trim())) {
                email_signup.error = "Required Field!"
                return@setOnClickListener    // check this
            }
            if (TextUtils.isEmpty(password_signup.text.toString().trim())) {
                password_signup.error = "Required Field!"
                return@setOnClickListener
            }

            mProgressBar.setMessage("Processing...")
            mProgressBar.show()

            mAuth.createUserWithEmailAndPassword(
                email_signup.text.toString(),
                password_signup.text.toString()
            )
                .addOnSuccessListener {
                    toast("Registration Completed Successfully")
                    mProgressBar.dismiss()
                    startActivity<Default>()
                    finish()
                }
                .addOnCompleteListener {
                    btn_signup.isEnabled = false
                    mProgressBar.dismiss()
                }
                .addOnFailureListener {
                    toast("Registration Failed")
                    mProgressBar.dismiss()
                }

        }


    }
}
