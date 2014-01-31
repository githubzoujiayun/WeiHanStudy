package com.fax.weihanstudy.utils;

import android.content.Context;
import android.graphics.Typeface;

public final class UyghurConvert {

    private static final class Struc {
        public char character;//未变形前
        public char endGlyph;//结尾，前连，右连
        public char iniGlyph;//开头，后连，左连
        public char midGlyph;//中间，前后连，左右连
        public char isoGlyph;//独立
        /**未变形前，右连，左连，左右连，独立*/
        public Struc(char Character, char EndGlyph, char IniGlyph,
                char MidGlyph, char IsoGlyph) {
            character = Character;
            endGlyph = EndGlyph;
            iniGlyph = IniGlyph;
            midGlyph = MidGlyph;
            isoGlyph = IsoGlyph;
        }
        private boolean contain(char c){
        	if(character==c||endGlyph==c||iniGlyph==c||midGlyph==c||isoGlyph==c){
        		return true;
        	}
        	return false;
        }
    }

    private static Struc[] arrStruc = {
            new Struc((char) 0x627, (char) 0xfe8e, (char) 0xfe8d,
                    (char) 0xfe8e, (char) 0xfe8d),
            new Struc((char) 0x6d5, (char) 0xfeea, (char) 0xfee9,
                    (char) 0xfeea, (char) 0xfee9),
            new Struc((char) 0x628, (char) 0xfe90, (char) 0xfe91,
                    (char) 0xfe92, (char) 0xfe8f),
            new Struc((char) 0x67e, (char) 0xfb57, (char) 0xfb58,
                    (char) 0xfb59, (char) 0xfb56),
            new Struc((char) 0x62a, (char) 0xfe96, (char) 0xfe97,
                    (char) 0xfe98, (char) 0xfe95),
            new Struc((char) 0x62c, (char) 0xfe9e, (char) 0xfe9f,
                    (char) 0xfea0, (char) 0xfe9d),
            new Struc((char) 0x686, (char) 0xfb7b, (char) 0xfb7c,
                    (char) 0xfb7d, (char) 0xfb7a),
            new Struc((char) 0x62e, (char) 0xfea6, (char) 0xfea7,
                    (char) 0xfea8, (char) 0xfea5),
            new Struc((char) 0x62f, (char) 0xfeaa, (char) 0xfea9,
                    (char) 0xfeaa, (char) 0xfea9),
            new Struc((char) 0x631, (char) 0xfeae, (char) 0xfead,
                    (char) 0xfeae, (char) 0xfead),
            new Struc((char) 0x632, (char) 0xfeb0, (char) 0xfeaf,
                    (char) 0xfeb0, (char) 0xfeaf),
            new Struc((char) 0x698, (char) 0xfb8b, (char) 0xfb8a,
                    (char) 0xfb8b, (char) 0xfb8a),
            new Struc((char) 0x633, (char) 0xfeb2, (char) 0xfeb3,
                    (char) 0xfeb4, (char) 0xfeb1),
            new Struc((char) 0x634, (char) 0xfeb6, (char) 0xfeb7,
                    (char) 0xfeb8, (char) 0xfeb5),
            new Struc((char) 0x63a, (char) 0xfece, (char) 0xfecf,
                    (char) 0xfed0, (char) 0xfecd),
            new Struc((char) 0x641, (char) 0xfed2, (char) 0xfed3,
                    (char) 0xfed4, (char) 0xfed1),
            new Struc((char) 0x642, (char) 0xfed6, (char) 0xfed7,
                    (char) 0xfed8, (char) 0xfed5),
            new Struc((char) 0x643, (char) 0xfeda, (char) 0xfedb,
                    (char) 0xfedc, (char) 0xfed9),
            new Struc((char) 0x6af, (char) 0xfb93, (char) 0xfb94,
                    (char) 0xfb95, (char) 0xfb92),
            new Struc((char) 0x6ad, (char) 0xfbd4, (char) 0xfbd5,
                    (char) 0xfbd6, (char) 0xfbd3),
            new Struc((char) 0x644, (char) 0xfede, (char) 0xfedf,
                    (char) 0xfee0, (char) 0xfedd),
            new Struc((char) 0x645, (char) 0xfee2, (char) 0xfee3,
                    (char) 0xfee4, (char) 0xfee1),
            new Struc((char) 0x646, (char) 0xfee6, (char) 0xfee7,
                    (char) 0xfee8, (char) 0xfee5),
            new Struc((char) 0x6be, (char) 0xfbab, (char) 0xfbac,
                    (char) 0xfbad, (char) 0xfbaa),
            new Struc((char) 0x648, (char) 0xfeee, (char) 0xfeed,
                    (char) 0xfeee, (char) 0xfeed),
            new Struc((char) 0x6c7, (char) 0xfbd8, (char) 0xfbd7,
                    (char) 0xfbd8, (char) 0xfbd7),
            new Struc((char) 0x648, (char) 0xfeee, (char) 0xfeed,
                    (char) 0xfeee, (char) 0xfeed),
            new Struc((char) 0x6c7, (char) 0xfbd8, (char) 0xfbd7,
                    (char) 0xfbd8, (char) 0xfbd7),
            new Struc((char) 0x6c6, (char) 0xfbda, (char) 0xfbd9,
                    (char) 0xfbda, (char) 0xfbd9),
            new Struc((char) 0x6c8, (char) 0xfbdc, (char) 0xfbdb,
                    (char) 0xfbdc, (char) 0xfbdb),
            new Struc((char) 0x6cb, (char) 0xfbdf, (char) 0xfbde,
                    (char) 0xfbdf, (char) 0xfbde),
            new Struc((char) 0x6d0, (char) 0xfbe5, (char) 0xfbe6,
                    (char) 0xfbe7, (char) 0xfbe4),
            new Struc((char) 0x649, (char) 0xfef0, (char) 0xfbe8,
                    (char) 0xfbe9, (char) 0xfeef),
            new Struc((char) 0x64a, (char) 0xfef2, (char) 0xfef3,
                    (char) 0xfef4, (char) 0xfef1),
            new Struc((char) 0x626, (char) 0xfe8a, (char) 0xfe8b,
                    (char) 0xfe8c, (char) 0xfe89),
    };

    private static final boolean canLinkRight(char ch) {
    	for(Struc c:arrStruc){
        	if(c.character==ch){
        		return true;
        	}
        }
        return false;
    }

    private static final boolean canLinkLeft(char ch) {
        for(Struc c:arrStruc){
        	if(c.character==ch){
        		return c.isoGlyph!=c.iniGlyph;
        	}
        }
        return false;
    }
    public static final boolean isNeedConvert(String in){
    	for(int i=0;i<in.length();i++){
    		char c=in.charAt(i);
    		for(Struc struc:arrStruc){
    			if(struc.character==c){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    public static String reverseConvert(String in){
		if (in == null) {
			return "";
		}
		in = in.replace(s1after,s1before);
		in = in.replace(s2after,s2before);
		char[] chIn = in.toCharArray();
		StringBuilder outSb=new StringBuilder();
    	for(char c:chIn){
    		for(Struc struc:arrStruc){
    			if(struc.contain(c)){
    				c=struc.character;
    			}
    		}
    		outSb.append(c);
    	}
    	return outSb.toString();
    }

    public static final String convert(String in) {
		if (in == null) {
			return "";
		}

		boolean linkRight, linkLeft,isUyghurChar;
		String Out = in;
		char[] chOut = new char[Out.length()];
		chOut = Out.toCharArray();
		char[] chIn = new char[in.length()];
		chIn = in.toCharArray();

		for (int i = 0; i < in.length(); i++) {
			isUyghurChar = false;
			int idx = 0;
			while (idx < arrStruc.length) {
				if (arrStruc[idx].character == chIn[i]){
					isUyghurChar = true;
					break;
				}
				++idx;
			}
			
			if (isUyghurChar) {
				if (i == in.length() - 1) linkLeft = false;
				else linkLeft = (canLinkRight(chIn[i + 1]));
				if (i == 0) linkRight = false;
				else linkRight = canLinkLeft(chIn[i - 1]);
				
				if (linkRight && linkLeft) chOut[i] = arrStruc[idx].midGlyph;
				else if (linkRight && !linkLeft) chOut[i] = arrStruc[idx].endGlyph;
				else if (!linkRight && linkLeft) chOut[i] = arrStruc[idx].iniGlyph;
				else if (!linkRight && !linkLeft) chOut[i] = arrStruc[idx].isoGlyph;
				else chOut[i] = arrStruc[idx].isoGlyph;
			} else {
				chOut[i] = chIn[i];
			}
		}
		Out = "";
		for (int j = 0; j < chOut.length; j++) Out += chOut[j];

	    //处理特殊变形
		Out = Out.replace(s1before, s1after);
		Out = Out.replace(s2before, s2after);

		// return reorderWords(Out);
		return (Out);

	}
//    Struc s1=new Struc((char) 0x644, (char) 0xfede, (char) 0xfedf,
//            (char) 0xfee0, (char) 0xfedd);
//    Struc s2=new Struc((char) 0x627, (char) 0xfe8e, (char) 0xfe8d,
//            (char) 0xfe8e, (char) 0xfe8d);
    static String s1before=Character.toString((char)0xfedf)+Character.toString((char)0xfe8e);
    static String s2before=Character.toString((char)0xfee0)+Character.toString((char)0xfe8e);
    static String s1after=Character.toString((char)0xfefb);
    static String s2after=Character.toString((char)0xfefc);
    
    
    
    private static Typeface typeface;
    public static final Typeface GetUyghurFont(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(),
                    "UKIJInichke.ttf");
        }
        return typeface;
    }
}