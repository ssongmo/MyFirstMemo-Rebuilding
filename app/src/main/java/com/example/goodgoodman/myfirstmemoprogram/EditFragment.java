package com.example.goodgoodman.myfirstmemoprogram;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.goodgoodman.myfirstmemoprogram.interfaces.EditInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class EditFragment extends Fragment implements View.OnClickListener{

    EditInterface editInterface = null;
    Context context = null;
    View view = null;

    Button btnSave;
    EditText editMemo;
    ImageButton btnCamera;

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit, container, false);
        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnCamera = (ImageButton)view.findViewById(R.id.btnCamera);
        editMemo = (EditText)view.findViewById(R.id.editMemo);

        btnSave.setOnClickListener(this);
        btnCamera.setOnClickListener(this);

        return view;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.editInterface = (EditInterface)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onClick(View v) {
        //준무테스트


        switch (v.getId()){
            case R.id.btnSave:
               try {
                    Memo memo = new Memo();
                    String saveText = editMemo.getText().toString();
                    editMemo.setText("");
                    memo.setMemo(saveText);
                    memo.setDate(new Date(System.currentTimeMillis()));
                    editInterface.saveToList(memo);
                    ((MainActivity)getActivity()).CreateMemo(memo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            break;

            case R.id.btnCamera:

                final String items[] = {"카메라", "앨범"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       switch (id){
                           case 0:
                               Toast.makeText(context, "Camera", Toast.LENGTH_SHORT).show();
                               //카메라 로직 호출
                               break;
                           case 1:
                               Toast.makeText(context, "Album", Toast.LENGTH_SHORT).show();
                               //앨범 로직 호출
                               break;

                       }

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

        }
    }

}
