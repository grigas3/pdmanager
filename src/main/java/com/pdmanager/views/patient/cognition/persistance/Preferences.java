package com.pdmanager.views.patient.cognition.persistance;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;


public class Preferences {

    private final static String
            KEY = "lst.pdmanager",
            USERID = "userid",
            H0 = "h0",
            H1 = "h1",

    // Tests
    TESTS = "tests",
            VAS = "vas",

    // Days of Week
    MON = "mon",
            TUE = "tue",
            WED = "wed",
            THU = "thu",
            FRI = "fri",
            SAT = "sat",
            SUN = "sun";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public Preferences(Context ctx) {
        sp = ctx.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public String getUsername() {
        return sp.getString(USERID, null);
    }

    public void setUsername(String user) {
        editor = sp.edit();
        editor.putString(USERID, user);
        editor.commit();
    }

    public int getAlarmHour0() {
        return sp.getInt(H0, -1);
    }

    public void setAlarmHour0(int h) {
        editor = sp.edit();
        editor.putInt(H0, h);
        editor.commit();
    }

    public int getAlarmHour1() {
        return sp.getInt(H1, -1);
    }

    public void setAlarmHour1(int h) {
        editor = sp.edit();
        editor.putInt(H1, h);
        editor.commit();
    }

    public void saveDow(boolean bmon, boolean btue, boolean bwed, boolean bthu,
                        boolean bfri, boolean bsat, boolean bsun) {
        editor = sp.edit();
        editor.putBoolean(MON, bmon);
        editor.putBoolean(TUE, btue);
        editor.putBoolean(WED, bwed);
        editor.putBoolean(THU, bthu);
        editor.putBoolean(FRI, bfri);
        editor.putBoolean(SAT, bsat);
        editor.putBoolean(SUN, bsun);
        editor.commit();
    }

    public ArrayList<Boolean> getDoW() {
        ArrayList<Boolean> dows = new ArrayList<>();
        dows.add(sp.getBoolean(MON, false));
        dows.add(sp.getBoolean(TUE, false));
        dows.add(sp.getBoolean(WED, false));
        dows.add(sp.getBoolean(THU, false));
        dows.add(sp.getBoolean(FRI, false));
        dows.add(sp.getBoolean(SAT, false));
        dows.add(sp.getBoolean(SUN, false));
        return dows;
    }

    public String getVasScore() {
        return sp.getString(VAS, null);
    }

    public void setVasScore(String n) {
        editor = sp.edit();
        editor.putString(VAS, n);
        editor.commit();
    }

    public void clearVasScore() {
        setVasScore(null);
    }

    public void clearTests() {
        setTests(null);
    }

    public ArrayList<Integer> getTests() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        String aux = sp.getString(TESTS, null);
        if (aux != null && !aux.equals("")) {
            String[] tests = aux.split(",");
            for (String s : tests) list.add(Integer.parseInt(s));
        }
        return list;
    }

    public void setTests(ArrayList<Integer> tests) {
        String s = "";
        if (tests != null && tests.size() > 0) {
            for (Integer i : tests) s += i + ",";
        } else s = null;
        editor = sp.edit();
        editor.putString(TESTS, s);
        editor.commit();
    }

}
