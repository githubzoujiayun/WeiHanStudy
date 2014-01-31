package com.fax.weihanstudy;


//һ������
public class Word {
	private String word;
	private String wordContent;
	public Word(String word,String wordContent){
		this.word=word;
		this.wordContent=wordContent;
	}
	public Word(){
	}
	public String getWord(){
		return word;
	}
	public String getContent(){
		return wordContent;
	}
	/**
	 * �õ����������ؼ��ֵ���س̶�
	 * @param s �ؼ��֣�����Կո�ֿ�
	 * @return ��س̶ȣ���0-10��ԽС��ض�Խ��
	 */
	public int relateWith(String s){
		while(s.startsWith(" ")) s=s.substring(1);
		while(s.endsWith(" ")) s=s.substring(0,s.length()-1);
		if(getWord().equals(s)) return 0;
		if(getWord().startsWith(s)) return 1;
		if(getWord().contains(s)) return 2;
		if(getContent().contains(s)) return 3;
		
		while(s.contains("  ")) s=s.replace("  ", " ");
		String[] ss=s.split(" ");
		if(ss.length==1) return 10;
		int value=0;
		for(int i=0;i<ss.length;i++){
			if(ss[i].length()==0) continue;
			value+=relateWith(ss[i]);//��÷ָ���ַ�����س̶�
		}
		value/=ss.length;
		return value;
	}
}
