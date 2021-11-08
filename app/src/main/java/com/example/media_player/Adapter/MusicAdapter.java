package com.example.media_player.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.media_player.MusicList;
import com.example.media_player.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private final List<MusicList> list;
    private final Context context;

    public MusicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MyViewHolder holder, int position) {
        MusicList list2 = list.get(position);

        if(list2.isPlaying()){
            holder.rootLayout.setBackgroundResource(R.drawable.round_back_blue_10);
        } else  {
            holder.rootLayout.setBackgroundResource(R.drawable.round_back_10);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        /**
         * Register all the UI components in the layout file.
         * **/
        private final RelativeLayout rootLayout;
        private final TextView title;
        private final TextView artist;



        public MyViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout);
            title = itemView.findViewById(R.id.music_title);
            artist = itemView.findViewById(R.id.music_artist);
        }
    }
}
