package com.pdmanager.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by George on 1/29/2016.
 */


class EventItemViewHolder extends ListViewHolder {

    public ImageView itemImage;

    public TextView itemEvent;
    public TextView itemValue;
    public TextView itemDate;
    public TextView itemStatus;

    LinearLayout layout;

    public EventItemViewHolder(View itemView) {
        super(itemView);


        this.itemImage = (ImageView) itemView.findViewById(R.id.imageView);

        this.itemEvent = (TextView) itemView.findViewById(R.id.eventView);

        this.itemDate = (TextView) itemView.findViewById(R.id.dateView);

        this.itemStatus = (TextView) itemView.findViewById(R.id.statusView);
        this.itemValue = (TextView) itemView.findViewById(R.id.valueView);

    }



    /*public void bind(PhotoItemData entity) {
        this.entity = entity;

        this.itemAuthor.setText(entity.getPhoto().getOwner().getUsername());
        this.itemTitle.setText(entity.getPhoto().getTitle());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(FlickrHelper.getPhotoDownloadUrl(entity.getPhoto()))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(itemImage.getController())
                .build();
        itemImage.setController(controller);
    }*/
}

