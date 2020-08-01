package com.mridx.share.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mridx.share.R;

public class FileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.files_fragment, container, false);

        if (savedInstanceState == null) {
            FilesListFragment filesListFragment = FilesListFragment.Companion.build(builder -> {
                builder.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());
                return null;
            });

            getChildFragmentManager().beginTransaction()
                    .add(R.id.filesListHolder, filesListFragment)
                    .addToBackStack(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .commit();
        }

        return view;
    }
}
