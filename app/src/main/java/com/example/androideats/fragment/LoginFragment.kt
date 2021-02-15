package com.example.androideats.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.activity.MainActivity
import com.example.androideats.util.ConnectionManager
import org.json.JSONObject


class LoginFragment : Fragment() {

    lateinit var mobileNumberField: EditText
    lateinit var passwordField: EditText
    lateinit var loginButton: Button
    lateinit var forgotPassword: TextView
    lateinit var signUp: TextView
    private var sharedPreferences: SharedPreferences? = null
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var logo:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        initializeFields(view)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        forgotPassword.setOnClickListener {
            openForgotPasswordFragment()
        }

        signUp.setOnClickListener {
            openRegistrationFragment()
        }

        loginButton.setOnClickListener {
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                progressLayout.visibility = View.VISIBLE
                signIn()
            } else {
                if(activity != null){
                    Toast.makeText(
                        activity as Context,
                        "Please check the Input Fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return view
    }

    private fun signIn() {
        val userMbNo = mobileNumberField.text.toString().trim()
        val userPass = passwordField.text.toString().trim()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/login/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", userMbNo)
        jsonParams.put("password", userPass)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val userData = data.getJSONObject("data")
                            val user_id = userData.getString("user_id")
                            val name = userData.getString("name")
                            val email = userData.getString("email")
                            val mobile_number = userData.getString("mobile_number")
                            val address = userData.getString("address")
                            savePreference(user_id, name, email, mobile_number, address)
                            openHome()

                        } else {
                            val errorMessage = data.getString("errorMessage")
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } catch (e: Exception) {
                        progressLayout.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Some Error $e occurred!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }, Response.ErrorListener {
                    progressLayout.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }
            queue.add(jsonRequest)

        } else {
            if (activity != null) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    // Do Nothing
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    activity?.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    // Do Nothing
                    ActivityCompat.finishAffinity(activity as Activity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    private fun openRegistrationFragment() {
        val fragment = RegistrationFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frame, fragment)?.addToBackStack(null)?.commit()
    }

    private fun openForgotPasswordFragment() {
        val fragment = ForgotPasswordFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frame, fragment)?.addToBackStack(null)?.commit()
    }

    fun openHome() {
        if (activity != null) {
            val intent = Intent(activity!!.application, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    fun savePreference(
        user_id: String,
        name: String,
        email: String,
        mobile_number: String,
        address: String
    ) {
        sharedPreferences?.edit()?.putBoolean("isLoggedIn", true)?.apply()
        sharedPreferences?.edit()?.putString("user_id", user_id)?.apply()
        sharedPreferences?.edit()?.putString("name", name)?.apply()
        sharedPreferences?.edit()?.putString("email", email)?.apply()
        sharedPreferences?.edit()?.putString("mobile_number", mobile_number)?.apply()
        sharedPreferences?.edit()?.putString("address", address)?.apply()
    }

    private fun validate(): Boolean {
        val userMbNo = mobileNumberField.text.toString().trim()
        val userPass = passwordField.text.toString().trim()
        var isValidate = true

        if (userMbNo.isEmpty()) {
            mobileNumberField.error = "Please enter Mobile Number"
            isValidate = false
        }
        if (userPass.isEmpty()) {
            passwordField.error = "Please enter Password"
            isValidate = false
        } else if (userPass.length < 4) {
            passwordField.error = "Password must have at-least 4 characters"
            isValidate = false
        }
        return isValidate
    }

    private fun initializeFields(view: View) {
        mobileNumberField = view.findViewById(R.id.etMobileNo)
        passwordField = view.findViewById(R.id.etPassword)
        loginButton = view.findViewById(R.id.btnLogin)
        forgotPassword = view.findViewById(R.id.txtForgotPassword)
        signUp = view.findViewById(R.id.txtDontHaveAcc)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        logo = view.findViewById(R.id.imgLogo)

        logo.animate().apply {
            duration = 1000
            rotationYBy(360f)
        }.withEndAction{
            logo.animate().apply {
                duration = 1000
                rotationYBy(3600f)
            }.start()
        }

        val content = SpannableString(forgotPassword.text)
        content.setSpan(UnderlineSpan(), 0, forgotPassword.text.length, 0)
        forgotPassword.text = content

        val content1 = SpannableString(signUp.text)
        content1.setSpan(UnderlineSpan(), 0, signUp.text.length, 0)
        signUp.text = content1
    }

}