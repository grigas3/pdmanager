package com.pdmanager.common.data;


import com.pdmanager.common.DataTypes;
import com.pdmanager.core.posturedetector.Core.PostureTypes;

/**
 * Created by admin on 16/5/2015.
 */
public class PostureData extends SensorData<Integer> {


    @Override
    public int getDataType() {
        return DataTypes.ACTIVITY;
    }


    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue);
    }

    @Override
    public String getDisplay() {

        if (this.mValue == PostureTypes.SITTING_LYING) {

            return "Sitting or Lying";

        } else if (this.mValue == PostureTypes.STANDING_WALKING) {

            return "Standing";

        } else if (this.mValue == PostureTypes.EATING) {

            return "Eating";

        } else if (this.mValue == PostureTypes.DRINKING) {

            return "Drinking";

        } else if (this.mValue == PostureTypes.HANDPOSTURE) {

            return "Hand Posture";

        } else if (this.mValue == PostureTypes.WALKING) {

            return "Walking";

        } else
            return "Other";


    }
}
