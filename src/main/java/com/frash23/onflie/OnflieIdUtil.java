package com.frash23.onflie;

import java.util.UUID;

public class OnflieIdUtil {
    private OnflieIdUtil() {}

    final private static String babecafe = "10111010101111101100101011111110";
    final private static String nameChars = " abcdefghijklmnopqrstuvwxyz0123456789_";
    public static UUID onflieId(String name) {

        name = "             " + name.toLowerCase();
        name = name.substring( name.length() - 16 );

        String mostSigString = babecafe;
        String leastSigString = "";

        for(byte i=0; i<16; i++) {
            String toAdd = "00000" + Integer.toBinaryString( nameChars.indexOf( name.charAt(i) ) );
            toAdd = toAdd.substring( toAdd.length() - 6 );

            if( mostSigString.length() < 61 ) mostSigString += toAdd;
            else if( mostSigString.length() >= 64 ) leastSigString += toAdd;
            else if( mostSigString.length() == 62 ) {
                mostSigString += toAdd.substring(0, 2);
                leastSigString += toAdd.substring(2);
            }

        }

        return new UUID( binaryLong(mostSigString), binaryLong(leastSigString) );
    }

    private static long binaryLong(String in) {
        return in.substring(0, 1).equals("1")
                ? Long.parseLong(in.substring(1), 2) + Long.MIN_VALUE
                : Long.parseLong(in, 2);
    }
}
