package com.zhao.myreader.ui.home.bookstore;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhao.myreader.R;

import com.zhao.myreader.callback.ResultCallback;


import com.zhao.myreader.databinding.ListviewBookStoreBookItemBinding;
import com.zhao.myreader.greendao.entity.Book;
import com.zhao.myreader.util.StringHelper;
import com.zhao.myreader.util.TextHelper;
import com.zhao.myreader.webapi.BookStoreApi;


import java.util.List;



public class BookStoreBookAdapter extends RecyclerView.Adapter<BookStoreBookAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Book> mDatas;

    private Context mContext;
    private RecyclerView rvContent;


    private OnItemClickListener onItemClickListener;

    private boolean isScrolling;


    private Handler mHandle = new Handler(message -> {

        switch (message.what){
            case  1:
                ViewHolder holder = (ViewHolder) message.obj;
                int pos = message.arg1;
                initImgAndDec(pos,holder);
                break;
        }

        return false;

    });


   BookStoreBookAdapter(Context context,  List<Book> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;

        mContext = context;



    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ListviewBookStoreBookItemBinding binding;

        ViewHolder(ListviewBookStoreBookItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (rvContent == null) rvContent = (RecyclerView) parent;
        ListviewBookStoreBookItemBinding binding = ListviewBookStoreBookItemBinding.inflate(mInflater,parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initView(position, holder);
        if (onItemClickListener != null){

            holder.itemView.setOnClickListener(view -> {

                onItemClickListener.onClick(position,view);

            });

        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private void initView(final int position, final ViewHolder holder) {
       Book book = mDatas.get(position);
       holder.binding.tvBookName.setText(book.getName());
       holder.binding.tvBookAuthor.setText(book.getAuthor());
       holder.binding.tvBookDesc.setText("");
       holder.binding.tvBookName.setTag(position);//????????????????????????????????????
       if (StringHelper.isEmpty(book.getImgUrl())){
          getBookInfo(position,holder,book);
       }else{
           initImgAndDec(position,holder);
       }



    }

    /**
     * ??????????????????
     * @param position
     * @param holder
     * @param book
     */
    private void getBookInfo(final int position, final ViewHolder holder,final Book book){

        Glide.with(mContext).clear(holder.binding.ivBookImg);
        //??????????????????
        BookStoreApi.getBookInfo(book, new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                mDatas.set(position,(Book) o);
                //??????????????????????????????????????????????????????????????????
                if (holder.binding.tvBookName.getTag() == null || (int)holder.binding.tvBookName.getTag() == position) {
                    mHandle.sendMessage(mHandle.obtainMessage(1,position,0,holder));
                }

            }

            @Override
            public void onError(Exception e) {
                //??????????????????????????????????????????????????????
                getBookInfo(position,holder,book);


            }
        });
    }

    private void initImgAndDec(final int position, final ViewHolder holder){
        Book book = mDatas.get(position);

        if (holder.binding.ivBookImg.getTag() != null && (int)holder.binding.ivBookImg.getTag() != position){

            Glide.with(mContext).clear(holder.binding.ivBookImg);

        }

        //??????
        Glide.with(mContext)
                .load(book.getImgUrl())
                .error(R.mipmap.no_image)
                .placeholder(R.mipmap.no_image)
                .into(holder.binding.ivBookImg);


        holder.binding.ivBookImg.setTag(position);


        //??????
        holder.binding.tvBookDesc.setText(book.getDesc());

        //??????
        holder.binding.tvBookAuthor.setText(book.getAuthor());




    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{

        void onClick(int pos,View view);

    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }
}
