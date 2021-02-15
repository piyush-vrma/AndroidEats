package com.example.androideats.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.Html
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

class RegistrationFragment : Fragment() {

    lateinit var nameField: EditText
    lateinit var emailField: EditText
    lateinit var mobileNumberField: EditText
    lateinit var deliveryAddressField: EditText
    lateinit var passwordField: EditText
    lateinit var confirmPasswordField: EditText
    lateinit var registerButton: Button
    private var sharedPreferences: SharedPreferences? = null
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var logo:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        initializeFields(view)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        registerButton.setOnClickListener {

            if (validate()) {
                progressBar.visibility = View.VISIBLE
                progressLayout.visibility = View.VISIBLE
                signUp()
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

    private fun signUp() {

        val userName = nameField.text.toString().trim()
        val userEmail = emailField.text.toString().trim()
        val userMbNo = mobileNumberField.text.toString().trim()
        val userAddress = deliveryAddressField.text.toString().trim()
        val userPass = passwordField.text.toString().trim()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("name", userName)
        jsonParams.put("mobile_number", userMbNo)
        jsonParams.put("password", userPass)
        jsonParams.put("address", userAddress)
        jsonParams.put("email", userEmail)

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
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val errorMessage = data.getString("errorMessage")
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

    fun openHome() {
        // In this function we are going to a activity from a fragment that is why we are using intent in fragment
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

        val userName = nameField.text.toString().trim()
        val userEmail = emailField.text.toString().trim()
        val userMbNo = mobileNumberField.text.toString().trim()
        val userAddress = deliveryAddressField.text.toString().trim()
        val userPass = passwordField.text.toString().trim()
        val confPass = confirmPasswordField.text.toString().trim()

        var isValidate = true
        if (userName.isEmpty()) {
            nameField.error = "Please enter Name"
            isValidate = false
        }
        if (userEmail.isEmpty()) {
            emailField.error = "Please enter Email Address"
            isValidate = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            emailField.error = "Please enter a valid Email Address"
            isValidate = false
        }
        if (userMbNo.isEmpty()) {
            mobileNumberField.error = "Please enter Mobile Number"
            isValidate = false
        }
        if (userAddress.isEmpty()) {
            deliveryAddressField.error = "Please enter Delivery Address"
            isValidate = false
        }
        if (userPass.isEmpty()) {
            passwordField.error = "Please enter Password"
            isValidate = false
        } else if (userPass.length < 4) {
            passwordField.error = "Password must have at-least 4 characters"
            isValidate = false
        }

        if (confPass.isEmpty()) {
            confirmPasswordField.error = "Please enter Password"
            isValidate = false
        } else if (confPass.length < 4) {
            confirmPasswordField.error = "Password must have at-least 4 characters"
            isValidate = false
        }
        if (userPass != confPass) {
            passwordField.error = "Password not matching"
            confirmPasswordField.error = "Password not matching"
            isValidate = false
        }

        return isValidate
    }

    private fun initializeFields(view: View) {
        nameField = view.findViewById(R.id.name)
        emailField = view.findViewById(R.id.email)
        mobileNumberField = view.findViewById(R.id.etMobileNo)
        deliveryAddressField = view.findViewById(R.id.etDeliveryAddress)
        passwordField = view.findViewById(R.id.etPassword)
        confirmPasswordField = view.findViewById(R.id.etConfirmPassword)
        registerButton = view.findViewById(R.id.btnRegister)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        logo = view.findViewById(R.id.imgLogo)
        logo.animate().apply {
            duration = 1000
            alpha(.5f)
            scaleXBy(0.5f)
            scaleYBy(0.5f)
            rotationYBy(360f)
            translationYBy(200f)
        }.withEndAction{
            logo.animate().apply {
                duration = 1000
                alpha(1f)
                scaleXBy(-0.5f)
                scaleYBy(-0.5f)
                rotationXBy(360f)
                translationYBy(-200f)
            }
        }.start()
    }

}