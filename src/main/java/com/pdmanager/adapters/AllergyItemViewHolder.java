package com.pdmanager.adapters;

import android.view.View;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by George on 1/29/2016.
 */


class AllergyItemViewHolder extends ListViewHolder {


    public TextView itemValue;

    public AllergyItemViewHolder(View itemView) {
        super(itemView);


        this.itemValue = (TextView) itemView.findViewById(R.id.text1);


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

