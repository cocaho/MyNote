package com.example.hau.mynote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Hau on 2017/5/6.
 */

public class NoteAdapter extends RecyclerViewCursorAdapter<NoteAdapter.ViewHolder> {

    Context mContext;
    Cursor cursor;

    RecyclerViewOnItemClickListener mOnItemClickListener;
    RecyclerViewOnItemLongClickListener mOnItemLongClickListener;

    private boolean isshowBox = false;//是否显示单选框
    private Map<Integer,Boolean> map = new HashMap<>();//存储勾选框状态的map集合


    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView1,textView2;
        CardView cardView;
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView)itemView.findViewById(R.id.row_text);
            textView2 = (TextView)itemView.findViewById(R.id.tv_note_time );
            imageView = (ImageView)itemView.findViewById(R.id.image);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }


    public NoteAdapter(Context context,Cursor cursor,int flags){
      super(context,cursor,flags);
        this.mContext = context;
        this.cursor = cursor;
    }

    //初始化map集合,默认为不选中
    private void initMap() {
        for (int i = 0; i < cursor.getCount(); i++) {
            map.put(i, false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    protected void onContentChanged() {

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
         final  int position = holder.getAdapterPosition();
         holder.textView1.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_CONTENT)));
         holder.textView2.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_DATETIME)));

        if(cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT))==1){
            holder.imageView.setImageResource(R.mipmap.star_light);
        }

        if (isshowBox) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.cardView.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //用map集合保存
                map.put(position, isChecked);
            }
        });
        // 设置CheckBox的状态
        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.checkBox.setChecked(map.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(view, position);
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    initMap();
                    mOnItemLongClickListener.onItemLongClickListener(view, position);

                }
                return false;
            }
        });

    }

//实质上cardview的点击事件，这个方法是让其暴露出来。点击事件最重要的是view和position
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClickListener(View view, int position);
    }
    public interface RecyclerViewOnItemLongClickListener {
        void onItemLongClickListener(View view, int position);
    }
    public void setRecyclerViewOnItemLongClickListener(RecyclerViewOnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isshowBox = !isshowBox;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (map.get(position)) {
            map.put(position, false);
        } else {
            map.put(position, true);
        }
        notifyItemChanged(position);
    }

    //返回集合给MainActivity
    public Map<Integer, Boolean> getMap()
    {
        return map;
    }
}

