package com.mridx.share.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mridx.share.R
import com.mridx.share.adapter.FilesListAdapter
import com.mridx.share.utils.FileUtils
import kotlinx.android.synthetic.main.files_list_fragment.*

class FilesListFragment : Fragment() {

    private lateinit var filesListAdapter: FilesListAdapter
    private lateinit var PATH: String
    private lateinit var callback: FilesListAdapter.OnAdapterItemClicked


    companion object {
        private const val ARG_PATH: String = "FILE_PATH"
        fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filePath = arguments?.getString(ARG_PATH)
        if (filePath == null) {
            Toast.makeText(context, "Path should not be null", Toast.LENGTH_LONG).show()
            return
        }
        PATH = filePath
        initViews()
    }

    private fun initViews() {
        filesListAdapter = FilesListAdapter()
        filesListAdapter.setOnAdapterItemClicked(callback)
        filesHolder.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filesListAdapter
        }
        getData()
    }

    private fun getData() {
        filesListAdapter.setFileList(FileUtils.getFileModelsFromFiles(PATH))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as FilesListAdapter.OnAdapterItemClicked
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}