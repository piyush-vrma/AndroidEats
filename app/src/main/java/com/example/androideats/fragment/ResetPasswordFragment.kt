package com.example.androideats.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androideats.R
import com.example.androideats.activity.MainActivity
import com.example.androideats.util.ConnectionManager
import org.json.JSONObject

class ResetPasswordFragment : Fragment() {

    lateinit var otpField: EditText
    lateinit var passwordField: EditText
    lateinit var confirmPasswordField: EditText
    lateinit var submitButton: Button
    private var sharedPreferences: SharedPreferences? = null
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var logo:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        initializeFields(view)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        submitButton.setOnClickListener {
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                progressLayout.visibility = View.VISIBLE
                changePassword()
            } else {
                if (activity != null) {
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

    private fun changePassword() {
        val userMbNo = sharedPreferences?.getString("mobile_number", "9999999999")
        val userPass = passwordField.text.toString().trim()
        val otp = otpField.text.toString().trim()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", userMbNo)
        jsonParams.put("password", userPass)
        jsonParams.put("otp", otp)

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            val successMessage = data.getString("successMessage")
                            if (activity != null) {
                                Toast.makeText(
                                    activity as Context,
                                    successMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            sharedPreferences?.edit()?.clear()?.apply()
                            openLogin()

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

    private fun openLogin() {
        if (activity != null) {
            val fragment = LoginFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frame, fragment)?.commit()
        }
    }

    private fun validate(): Boolean {

        val userOtp = otpField.text.toString().trim()
        val userPass = passwordField.text.toString().trim()
        val confPass = confirmPasswordField.text.toString().trim()

        var isValidate = true
        if (userOtp.isEmpty()) {
            otpField.error = "Please enter OTP"
            isValidate = false
        } else if (userOtp.length < 4) {
            otpField.error = "Please enter valid 4 character OTP"
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
        otpField = view.findViewById(R.id.etOtp)
        passwordField = view.findViewById(R.id.etPassword)
        confirmPasswordField = view.findViewById(R.id.etConfirmPassword)
        submitButton = view.findViewById(R.id.btnSubmit)
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