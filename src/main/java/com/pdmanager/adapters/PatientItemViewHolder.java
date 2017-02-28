package com.pdmanager.adapters;

import android.view.View;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by George on 1/29/2016.
 */


class PatientItemViewHolder extends ListViewHolder {

    public TextView itemName;

    public TextView itemPhone;
    public TextView itemDOB;
    public TextView itemStatus;
    public TextView itemLastVisit;
    public TextView itemMRN;
    public TextView itemYWP;

    public PatientItemViewHolder(View itemView) {
        super(itemView);


        this.itemName = (TextView) itemView.findViewById(R.id.nameView);

        this.itemStatus = (TextView) itemView.findViewById(R.id.statusView);
        this.itemLastVisit = (TextView) itemView.findViewById(R.id.lastVisitView);
        this.itemDOB = (TextView) itemView.findViewById(R.id.dobView);
        this.itemYWP = (TextView) itemView.findViewById(R.id.ywpView);
        this.itemMRN = (TextView) itemView.findViewById(R.id.mrnView);
        this.itemPhone = (TextView) itemView.findViewById(R.id.phoneView);


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

