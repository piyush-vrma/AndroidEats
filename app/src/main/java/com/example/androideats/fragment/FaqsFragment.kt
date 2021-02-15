package com.example.androideats.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.androideats.R

class FaqsFragment : Fragment() {
    lateinit var linkedIn:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)
        linkedIn =  view.findViewById(R.id.linkedIn)
        val content = SpannableString(linkedIn.text)
        content.setSpan(UnderlineSpan(), 0, linkedIn.text.length, 0)
        linkedIn.text = content
        linkedIn.setOnClickListener {
            gotoUrl("https://www.linkedin.com/in/piyush-verma-152022196")
        }
        return view
    }

    private fun gotoUrl(Link: String) {
             val uri = Uri.parse(Link)
             val intent = Intent(Intent.ACTION_VIEW,uri)
             startActivity(intent)
    }

}