package com.example.photoalbum;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyImagesAdapter extends RecyclerView.Adapter<MyImagesAdapter.MyImagesHolder> {
    List<MyImages> imagesList=new ArrayList<>();
    private OnImageClickListener listener;
    public void setListener(OnImageClickListener listener) {
        this.listener = listener;
    }
    public void setImagesList(List<MyImages> imagesList) {
        this.imagesList = imagesList;
        notifyDataSetChanged();
    }
    public  interface OnImageClickListener{
        void onImageClick(MyImages myImages);
    }
    public MyImages getPosition(int position){
        return imagesList.get(position);
    }
    @NonNull
    @Override
    public MyImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card,parent,false);
        return new MyImagesHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyImagesHolder holder, int position) {
        MyImages myImages=imagesList.get(position);
        holder.textViewTitle.setText(myImages.getImageTitle());
        holder.textViewDescription.setText(myImages.getImageDescription());
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(myImages.getImage(),0,myImages.getImage().length));
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class MyImagesHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewTitle,textViewDescription;

        public MyImagesHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textViewTitle=itemView.findViewById(R.id.textViewTitle);
            textViewDescription=itemView.findViewById(R.id.textViewDescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(listener !=null && position !=RecyclerView.NO_POSITION){
                        listener.onImageClick(imagesList.get(position));
                    }
                }
            });
        }
    }
}
