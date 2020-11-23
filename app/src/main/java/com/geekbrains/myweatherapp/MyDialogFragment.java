package com.geekbrains.myweatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInstance(String info) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INFO, info);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String info = getArguments().getString(Constants.INFO);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        TextView textViewInfo;

        view = inflater.inflate(R.layout.layout_for_mydialogfragment, null);
        textViewInfo = view.findViewById(R.id.tv_info);
        textViewInfo.setText(info);
        builder.setView(view);

        return builder.create();
    }
}
