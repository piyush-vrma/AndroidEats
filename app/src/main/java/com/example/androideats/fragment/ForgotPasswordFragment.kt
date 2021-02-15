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
import com.example.androideats.util.ConnectionManager
import org.json.JSONObject

class ForgotPasswordFragment : Fragment() {

    lateinit var mobileNumberField: EditText
    lateinit var emailField: EditText
    lateinit var nextButton: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    private var sharedPreferences: SharedPreferences? = null
    lateinit var logo:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        initializeFields(view)
        progressLayout.visibility = View.GONE
        progressBar.visibility = View.GONE

        nextButton.setOnClickListener {
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                progressLayout.visibility = View.VISIBLE
                resetPassword()
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

    private fun resetPassword() {

        val userMbNo = mobileNumberField.text.toString().trim()
        val userEmail = emailField.text.toString().trim()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", userMbNo)
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
                            val firstTry = data.getBoolean("first_try")

                            if (!firstTry) {
                                if (activity != null) {
                                    Toast.makeText(
                                        activity as Context,
                                        "Not the first try...Please use the Already mailed OTP.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                if (activity != null) {
                                    Toast.makeText(
                                        activity as Context,
                                        "OTP sent successfully on the registered email address",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            val mobileNumber =  mobileNumberField.text.toString().trim()
                            sharedPreferences?.edit()?.putString("mobile_number",mobileNumber)?.apply()
                            openResetPassword()

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

    private fun openResetPassword() {
        val fragment = ResetPasswordFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frame, fragment)?.addToBackStack(null)?.commit()
    }

    private fun validate(): Boolean {
        val userEmail = emailField.text.toString().trim()
        val userMbNo = mobileNumberField.text.toString().trim()

        var isValidate = true
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
        return isValidate
    }

    private fun initializeFields(view: View) {
        mobileNumberField = view.findViewById(R.id.etMobileNo)
        emailField = view.findViewById(R.id.email)
        nextButton = view.findViewById(R.id.btnNext)
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