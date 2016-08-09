package com.pdmanager.common;


import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by george on 3/6/2016.
 */
public class ReadTestFiles {


    private static void readLine(String line, String[] lineData, int n) throws Exception {
        int ii = 0;
        int jj = 0;

        for (int i = 0; i < n - 1; i++) {

            jj = line.indexOf('\t', ii);
            lineData[i] = line.substring(ii, (ii) + (jj - ii)).trim();
            ii = jj + 1;
        }
        jj = line.length();
        lineData[n - 1] = line.substring(ii, (ii) + (jj - ii)).trim();
    }

    private static double getDoubleFromString(String data) throws Exception {
        return (!data.trim().equals("null")) ? Double.valueOf(data.trim()) : Double.NaN;
    }


    public static SignalCollection ReadMoticon(int n, int c, String filename) throws Exception {


        SignalCollection signal = new SignalCollection(c, n);


        FileReader fs = new FileReader(filename);
        BufferedReader str = new BufferedReader(fs);

        String[] vals = new String[c + 1];

        String line;
        int i = 0;
        //Skip Lines
        str.readLine();
        str.readLine();
        str.readLine();
        str.readLine();
        str.readLine();


        while ((line = str.readLine()) != null && i < n) {


            readLine(line, vals, c + 1);

            for (int j = 1; j < c + 1; j++)
                signal.get___idx(j - 1).set___idx(i, getDoubleFromString(vals[j]));


            i++;


        }


        return signal;


    }

    public static SignalCollection ReadMoticon(int n, int c, int o, String filename) throws Exception {


        SignalCollection signal = new SignalCollection(c, n);


        FileReader fs = new FileReader(filename);
        BufferedReader str = new BufferedReader(fs);

        String[] vals = new String[c + 1];

        String line;
        int i = 0;
        //Skip Lines
        str.readLine();
        str.readLine();
        str.readLine();
        str.readLine();
        str.readLine();


        int k = 0;
        while ((line = str.readLine()) != null && i < n) {


            readLine(line, vals, c + 1);

            if (k >= o) {
                for (int j = 1; j < c + 1; j++)
                    signal.get___idx(j - 1).set___idx(i, getDoubleFromString(vals[j]));


                i++;

            }
            k++;


        }


        return signal;


    }

    public static SignalCollection Read(int n, int c, String filename) throws Exception {


        SignalCollection signal = new SignalCollection(c, n);


        FileReader fs = new FileReader(filename);
        BufferedReader str = new BufferedReader(fs);

        String[] vals = new String[3];

        String line;
        int i = 0;
        while ((line = str.readLine()) != null && i < n) {


            readLine(line, vals, c);

            for (int j = 0; j < c; j++)
                signal.get___idx(j).set___idx(i, getDoubleFromString(vals[j]));


            i++;


        }


        return signal;


    }


}



