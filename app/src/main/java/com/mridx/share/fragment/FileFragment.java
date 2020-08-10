package com.mridx.share.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mridx.share.R;
import com.mridx.share.data.FileData;
import com.mridx.share.data.StorageData;
import com.mridx.share.ui.MainUI;
import com.mridx.share.utils.FileType;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FileFragment extends Fragment implements MainUI.OnItemClickedListener, MainUI.OnBackPressed, Function1<StorageData, Unit> {

    private MainUI mainUI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.files_fragment, container, false);

        if (savedInstanceState == null) {

            FileRootFragment fileRootFragment = new FileRootFragment();
            fileRootFragment.setOnRootItemClicked(this);

            /*FilesListFragment filesListFragment = FilesListFragment.Companion.build(builder -> {
                builder.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());
                return null;
            });*/

            getChildFragmentManager().beginTransaction()
                    .add(R.id.filesListHolder, fileRootFragment)
                    //.addToBackStack(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .commit();
        }

        return view;
    }

    private void addFileFragment(FileData fileData) {
        FilesListFragment filesListFragment = FilesListFragment.Companion.build(builder -> {
            builder.setPath(fileData.getPath());
            return null;
        });
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.filesListHolder, filesListFragment);
        fragmentTransaction.addToBackStack(fileData.getPath());
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mainUI = ((MainUI) context);
            mainUI.setOnItemClickedListener(this);
            mainUI.setOnBackPressed(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClicked(FileData fileData) {
        if (fileData.getFileType() == FileType.FOLDER) addFileFragment(fileData);
    }

    @Override
    public void onPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStackImmediate();
            return;
        }
        getActivity().finish();
    }

    @Override
    public Unit invoke(StorageData storageData) {
        addFileFragment(new FileData(storageData.getPath(), storageData.getName(), null, null, storageData.getTotalSize(), 0, false));
        return null;
    }
}