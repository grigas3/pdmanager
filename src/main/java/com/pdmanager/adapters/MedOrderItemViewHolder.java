package com.pdmanager.adapters;

import android.view.View;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by George on 1/29/2016.
 */


class MedOrderItemViewHolder extends ListViewHolder {

    public TextView itemProduct;


    public TextView itemInstructions;
    public TextView itemStatus;
    public TextView itemDate;
    public TextView itemPrescriber;
    public TextView itemDoses;


    public MedOrderItemViewHolder(View itemView) {
        super(itemView);


        this.itemProduct = (TextView) itemView.findViewById(R.id.productView);

        this.itemStatus = (TextView) itemView.findViewById(R.id.statusView);
        this.itemInstructions = (TextView) itemView.findViewById(R.id.instructionsView);
        this.itemDate = (TextView) itemView.findViewById(R.id.prescrDateView);
        this.itemPrescriber = (TextView) itemView.findViewById(R.id.prescriberView);
        this.itemDoses = (TextView) itemView.findViewById(R.id.dosesView);


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

