package com.example.goodgoodman.myfirstmemoprogram;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.goodgoodman.myfirstmemoprogram.Data.DBHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MemoPageActivity extends AppCompatActivity {

    Dao<Memo, Integer> memoDao;
    //List<Memo> datas;
    ImageButton btnSave;
    ImageButton btnDelete;
    EditText editTxtMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_page);

        btnSave = (ImageButton)findViewById(R.id.btnSaveIn);
        btnDelete = (ImageButton) findViewById(R.id.btnDeleteIn);
        editTxtMemo = (EditText) findViewById(R.id.callMemo);

        btnSave.setOnClickListener(listener);
        btnDelete.setOnClickListener(listener);

        DBHelper dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        try {
            memoDao = dbHelper.getMemoDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int memoID = getIntent().getExtras().getInt("id");

        Memo memo;
        try {
            memo = memoDao.queryForId(memoID);
        } catch (SQLException e) {
            e.printStackTrace();
            memo = new Memo();
            memo.memo = "액션빔(비었으니까..ㅎ)";
        }


        editTxtMemo.setText(memo.getMemo());


        MainActivity.MAIN_ACTIVITY.CreateMemo(memo);
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSaveIn:
                    Toast.makeText(MemoPageActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnDeleteIn:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MemoPageActivity.this);
                    builder.setTitle("삭제 하시겠습니까?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MemoPageActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MemoPageActivity.this, "삭제 취소", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    };
}
