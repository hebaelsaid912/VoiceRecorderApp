package com.example.android.voicerecorderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    private File[] allfiles;
    private  TimeAgo timeAgo;
    private onItemListClick onItemListClick;
    public AudioListAdapter(File[] allfiles , onItemListClick onItemListClick) {
        this.allfiles = allfiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioListAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListAdapter.AudioViewHolder holder, int position) {
        holder.fileName.setText(allfiles[position].getName());
        holder.fileDate.setText(timeAgo.getTimeAgo(allfiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allfiles.length;
    }

    class AudioViewHolder extends RecyclerView.ViewHolder{
        private ImageView playImage;
        private TextView fileName;
        private TextView fileDate;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
           playImage = itemView.findViewById(R.id.listimageview);
           fileName = itemView.findViewById(R.id.filename);
           fileDate = itemView.findViewById(R.id.filedate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListClick.onClickListener(allfiles[getAdapterPosition()], getAdapterPosition());
                }
            });
        }
    }
    public interface onItemListClick {
        void onClickListener(File file, int position);
    }
}
