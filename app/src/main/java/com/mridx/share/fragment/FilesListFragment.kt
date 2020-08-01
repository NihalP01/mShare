package com.mridx.share.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mridx.share.R

class FilesListFragment : Fragment() {

    companion object {
        private const val ARG_PATH: String = "FILE_PATH"
        fun build(block : Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var path: String = ""
        fun build(): FilesListFragment {
            val fragment = FilesListFragment()
            val args = Bundle()
            args.putString(ARG_PATH, path)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.files_list_fragment, container, false)
    }
}