package com.pdmanager.views.patient.cognition.cognitive.LondonTowers;

public class PS {
    public Integer top, middle, bottom;

    public PS(Integer... list) {
        top = null;
        middle = null;
        bottom = null;
        if (list.length > 0) bottom = list[0];
        if (list.length > 1) middle = list[1];
        if (list.length > 2) top = list[2];
    }

    public boolean push(int p) {
        if (bottom == null) bottom = p;
        else if (middle == null) middle = p;
        else if (top == null) top = p;
        else return false;
        return true;
    }

    public Integer pop() {
        Integer res = null;
        if (top != null) {
            res = top;
            top = null;
        } else if (middle != null) {
            res = middle;
            middle = null;
        } else if (bottom != null) {
            res = bottom;
            bottom = null;
        }
        return res;
    }

    public boolean isEqualTo(PS tps) {
        return (top == tps.top && middle == tps.middle && bottom == tps.bottom);
    }
}
