package com.pdmanager.core.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdmanager.core.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by George on 1/29/2016.
 */


class PedMedItemViewHolder extends ListViewHolder {

    public TextView itemMedication;


    public TextView itemInstructions;
    public TextView itemStatus;
    public TextView itemHour;

    public LinearLayout itemBackground;


    public PedMedItemViewHolder(View itemView) {
        super(itemView);


        this.itemMedication = (TextView) itemView.findViewById(R.id.productView);

        this.itemStatus = (TextView) itemView.findViewById(R.id.statusView);
        this.itemInstructions = (TextView) itemView.findViewById(R.id.instructionsView);
        this.itemHour = (TextView) itemView.findViewById(R.id.prescrDateView);
        this.itemBackground = (LinearLayout) itemView.findViewById(R.id.layoutView);


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

