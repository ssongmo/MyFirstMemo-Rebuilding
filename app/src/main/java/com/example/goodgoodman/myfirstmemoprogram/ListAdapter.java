package com.example.goodgoodman.myfirstmemoprogram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.util.Date;
import java.util.List;
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    Context context;
    List<Memo> datas;
    Intent intent =null;


    int count;
    public ListAdapter(Context context, List<Memo> datas) {
        this.context = context;
        this.datas = datas;
        this.intent = new Intent(context, MemoPageActivity.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Memo memo = datas.get(position);//포지션에 있는 아이디와 데이터베이스에 있는 아이디를 비교해서 메모랑 시간을 가져와야한다
        holder.textView.setText(memo.getMemo()); //홀더에서 받아온 텍스트뷰에 메모 넣기.
        holder.dateView.setText(memo.getDate().toGMTString().toString());
        holder.memoID = memo.getId();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }



    /////////////////////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        TextView dateView ;
        ImageButton btnDelete;
        int memoID;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            dateView = (TextView) itemView.findViewById(R.id.dateView);
            btnDelete = (ImageButton)itemView.findViewById(R.id.btnDelete);
            cardView.setOnClickListener(listener);
            btnDelete.setOnClickListener(listener);
        }

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.cardView:
                        intent = new Intent(context, MemoPageActivity.class);
                        intent.putExtra("id", memoID);

                        context.startActivity(intent);

                        break;

                    case R.id.btnDelete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("삭제 하시겠습니까?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "삭제완료!", Toast.LENGTH_SHORT).show();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "삭제취소!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    break;
                }

            }
        };

    }
}
