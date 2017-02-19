package com.example.goodgoodman.myfirstmemoprogram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.goodgoodman.myfirstmemoprogram.Data.DBHelper;
import com.example.goodgoodman.myfirstmemoprogram.interfaces.ListInterface;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<Memo> datas = new ArrayList<>();

    Context context = null;
    //ListInterface listInterface = null;
    View view = null;

    RecyclerView recyclerView;
    ListAdapter listAdapter;
    DBHelper dbHelper;
    Dao<Memo, Integer> memoDao;


    public ListFragment() {
    }


    public static ListFragment newInstance(int columnCount)  {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        dbHelper = OpenHelperManager.getHelper(getActivity(), DBHelper.class);
        try {
            memoDao = dbHelper.getMemoDao();
            datas = memoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //((MainActivity)getActivity()).loadData(datas);
        setData(datas);



        ((MainActivity)getActivity()).addDBListeners(new MainActivity.DBListener() {
            @Override
            public void onDBCreated(Memo createdMemo) {
                //갱신
                Toast.makeText(getActivity(), "DataCreated", Toast.LENGTH_SHORT).show();
                datas.add(createdMemo);
                setData(datas);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view != null)
            return view;

        view = inflater.inflate(R.layout.layout_list, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.list);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
//        this.listInterface = (ListInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setData(List<Memo> datas){
        this.datas = datas;
    }

    public void refreshAdapter() {
        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {

    }
}
