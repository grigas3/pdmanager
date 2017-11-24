package com.pdmanager.adapters;

import android.view.View;
import android.widget.TextView;

import com.pdmanager.R;
import com.telerik.widget.list.ListViewHolder;

/**
 * Created by IP on 11/21/2017.
 */


class DssInfoItemViewHolder extends ListViewHolder {

    public TextView itemCode;


    public TextView itemText;
    public TextView itemSubscriber;
    // public TextView itemPriority;
    public TextView itemDate;


    public DssInfoItemViewHolder(View itemView) {
        super(itemView);


        this.itemText = (TextView) itemView.findViewById(R.id.textView);
        this.itemCode = (TextView) itemView.findViewById(R.id.codeView);
        this.itemDate = (TextView) itemView.findViewById(R.id.prescrDateView);
        this.itemSubscriber = (TextView) itemView.findViewById(R.id.prescriberView);
        //   this.itemPriority = (TextView) itemView.findViewById(R.id.statusView);


    }


}

