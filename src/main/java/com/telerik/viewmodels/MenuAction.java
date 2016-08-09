package com.telerik.viewmodels;

/**
 * Created by George on 1/30/2016.
 */
public class MenuAction {
    private String headerText = "";
    private String descriptionText = "";
    private String ActionInfo = "";
    private String fragmentName = "FallbackActions";
    private Boolean isNew = false, isHighlighted = false;
    private String imageResource = "drawable/control_logo";


    public MenuAction() {

    }


    public String getFragmentName() {
        return String.format("com.pdmanager.views.%s", fragmentName);
    }

    public void setFragmentName(String fragment) {
        this.fragmentName = fragment;
    }

    public String getShortFragmentName() {
        return this.fragmentName;
    }

    public String getActionInfo() {
        return this.ActionInfo;
    }

    public void setActionInfo(String info) {
        this.ActionInfo = info;
    }

    public String getHeaderText() {
        return this.headerText;
    }

    public void setHeaderText(String value) {
        this.headerText = value;
    }

    public Boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(Boolean value) {
        this.isNew = value;
    }

    public Boolean getIsHighlighted() {
        return this.isHighlighted;
    }

    public void setIsHighlighted(Boolean value) {
        this.isHighlighted = value;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getImage() {
        return this.imageResource;
    }

    public void setImage(String imageId) {
        this.imageResource = imageId;
    }
}
