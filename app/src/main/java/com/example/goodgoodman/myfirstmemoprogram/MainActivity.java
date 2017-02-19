package com.example.goodgoodman.myfirstmemoprogram;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.goodgoodman.myfirstmemoprogram.Data.DBHelper;
import com.example.goodgoodman.myfirstmemoprogram.interfaces.EditInterface;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements EditInterface {
    public static MainActivity MAIN_ACTIVITY;
    // 탭 및 페이저 속성 정의
    final int TAB_COUNT = 2;
    // 현재 페이지
    private int page_position = 0;

    ViewPager viewPager;
    ListFragment list;
    EditFragment edit;

    //DBHelper
    DBHelper dbHelper;
    //

    List<Memo> datas = new ArrayList<>();
    Dao<Memo, Integer> memoDao;
    FragmentManager manager;


    // 페이지 이동경로를 저장하는 stack 변수
    Stack<Integer> pageStack = new Stack<>();
    boolean backPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MAIN_ACTIVITY = this;

        setContentView(R.layout.activity_main);

        // 프래그먼트
        list = new ListFragment();
        edit = new EditFragment();


        // - 탭 Layout 정의
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        // 탭 생성 및 타이틀 입력
        tabLayout.addTab( tabLayout.newTab().setText("New") );
        tabLayout.addTab( tabLayout.newTab().setText("List") );


        // - 프래그먼트 페이저 작성
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // 아답터 생성
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 1. 페이저 리스너 : 페이저가 변경되었을때 탭을 바꿔주는 리스너
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // 2. 페이지의 변경사항을 체크한다.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 뒤로가기를 누르지 않았을때만 stack 에 포지션을 더한다
                if(!backPress){
                    pageStack.push(page_position);
                    // 뒤로가기를 눌렀으면 false로 다시 세팅해준다.
                }else{
                    backPress = false;
                }
                page_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 3. 탭 리스너 : 탭이 변경되었을 때 페이지를 바꿔저는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    public void loadData() throws SQLException {
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        datas = memoDao.queryForAll();

    }
    // 목록 프래그먼트 FrameLayout 에 add

    @Override
    public void backToList() {

    }

    @Override
    public void saveToList(Memo memo) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        //memoDao.create(memo);
        loadData();
        list.setData(datas);
        list.refreshAdapter();
        viewPager.setCurrentItem(1);


//        manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.add( R.id.activity_main, list );
//        transaction.commit();

    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0: fragment = edit; break;
                case 1: fragment = list; break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }


    @Override
    public void onBackPressed() {
                goBackStack();
    }

    // stack 뒤로가기
    private void goBackStack(){
        // stack 의 사이즈가 0이면 앱을 종료
        if(pageStack.size() < 1){
            super.onBackPressed();
            // stack 에 position 값이 있으면
        }else {
            // View Pager 리스너에서 stack에 더해지는 것을 방지하기 위해 backpress 상태값을 미리 세팅
            backPress = true;
            // 페이지를 stack의 가장 마지막에 있는 위치값으로 이동
            viewPager.setCurrentItem(pageStack.pop());
        }

        /*준무테스트
        //
        getActivity().CreateMemo(memo);
        //

        getActivity().addDBListeners(new DBListener() {
            @Override
            public void onDBCreated(Memo createdMemo) {
                //갱신
                Toast.makeText(MainActivity.this, "DataCreated", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }


    public void CreateMemo(Memo memo) {
        try {
            memoDao.create(memo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (DBListener listener : dbListeners) {
            listener.onDBCreated(memo);
        }
    }

    private List<DBListener> dbListeners = new ArrayList<>();

    public void addDBListeners(DBListener dbListener) {
        dbListeners.add(dbListener);
    }


    //TODO: 테스트 코드입니다, 추후 수정예정
    public interface DBListener {
        void onDBCreated(Memo createdMemo);
    }
    //~테스트코드

}
