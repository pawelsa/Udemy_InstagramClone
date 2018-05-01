package com.example.pawel.udemy_instagramclone.myImageGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pawel.udemy_instagramclone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 22.02.2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.Holder> implements LoadPhotosFromMemory.OnPhotoLoadedListener {

    private List<Photo> albums;
    private Context context;

    OnItemSelectedListener mOnItemSelectedListener;


    public GalleryAdapter( Context context) {
        this.albums = new ArrayList<>();
        this.context = context;
    }

    public LoadPhotosFromMemory.OnPhotoLoadedListener getOnPhotoLoadListener(){
        return this;
    }

    public boolean isEmpty(){
        return albums.isEmpty();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, null);

        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        fillImageViewInMyGallery(holder, position);

        onImageClickLister(holder, position);

    }

    private void fillImageViewInMyGallery(Holder holder, int position) {

        holder.imageView.layout(0, 0, 0, 0);

        if (albums.get(position).uri != null) {

            Glide.with(context).load(albums.get(position).uri).apply(new RequestOptions().centerInside()).into(holder.imageView);
        } else if (albums.get(position).uri == null) {

            final int BORDER = 80;
            holder.imageView.setPadding(BORDER, BORDER, BORDER, BORDER);
            Glide.with(context).load(R.drawable.add).apply(new RequestOptions().centerInside()).into(holder.imageView);
        } else {

            Glide.tearDown();

            holder.imageView.setImageBitmap(null);
        }
    }

    private void onImageClickLister(Holder holder, final int position) {

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(albums.get(position));
                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        CustomImageView imageView;

        Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.custom);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {

        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {

        void onItemSelected(Photo mPhoto);
    }

    @Override
    public void photoLoaded(Photo photo) {
        albums.add(photo);
        notifyDataSetChanged();
    }
}
