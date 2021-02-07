package com.ltt.Utils;

import org.tartarus.snowball.ext.PorterStemmer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static String getKeywordsStem(String keywords) {
		String[] words = keywords.split(" ");
		StringBuilder sb = new StringBuilder();
		PorterStemmer stemmer = new PorterStemmer();
		for(String word : words) {
			stemmer.setCurrent(word);
			stemmer.stem();
			sb.append(stemmer.getCurrent()+" ");
		}
		return sb.substring(0, sb.length()-1);
	}

	public static String processCamelCase(String label){
		Pattern pattern = Pattern.compile("[a-z]+|[A-Z]+[a-z]*");
		Matcher matcher = pattern.matcher(label);
		List<String> list = new ArrayList<>();
		while(matcher.find())
			list.add(matcher.group());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++)
			sb.append(list.get(i)+" ");
		return sb.substring(0, sb.length()).trim();
	}

	public static String processKeyword(String keyword) {/**keyword用这个取词干*/
		keyword = keyword.replaceAll("\"|#", "");
		keyword = keyword.replaceAll("_|-|\\\\|/|\\(|\\)|\\.\\.\\.|:|,|;|\\?|!|\\+|~|&|\\$|%|\\^|@|\\*|=|<|>|\\[|\\]|\\{|\\}|\\|", " ");
		keyword = keyword.replaceAll("u00..", " ").trim();
		String[] labelSplit = keyword.split("\\s+");
		if (labelSplit.length == 0)
			return "";
		Set<String> set = new HashSet<>();
		List<String> list = new ArrayList<>();
		for (String ls : labelSplit) {
			if(Pattern.matches("[a-z]*[[A-Z]+[a-z]*]+", ls)) { // AaBbCc or aaBbCc
				Pattern pattern = Pattern.compile("[a-z]+|[A-Z]+[a-z]*");
				Matcher matcher = pattern.matcher(ls);
				while(matcher.find())
					list.add(matcher.group());
			} else
				list.add(ls);
		}
		for(String ls : list) {
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(ls.toLowerCase());
			stemmer.stem();
			set.add(stemmer.getCurrent()); //delete duplicate
		}
		StringBuilder sb = new StringBuilder();
		for(String s : set)
			sb.append(s + " ");
		keyword = sb.toString().trim();
		return keyword;
	}

	public static String processLabel(String label){/**建索引之前用，stemAnalyzer*/
		// replace what lucene cannot process
		String keyword = label.replaceAll("\"|#", "");
		keyword = keyword.replaceAll("_|-|\\\\|/|\\(|\\)|\\.\\.\\.|:|,|;|\\?|!|\\+|~|&|\\$|%|\\^|@|\\*|=|<|>|\\[|\\]|\\{|\\}|\\|", " ");
		keyword = keyword.replaceAll("u00..", " ").trim();
//		System.out.println(keyword);
		String[] labelSplit = keyword.split("\\s+");
		if(labelSplit.length == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<>();
		for (String ls : labelSplit) {
			if(Pattern.matches("[a-z]*[[A-Z]+[a-z]*]+", ls)) { // AaBbCc or aaBbCc
				Pattern pattern = Pattern.compile("[a-z]+|[A-Z]+[a-z]*");
				Matcher matcher = pattern.matcher(ls);
				while(matcher.find())
					list.add(matcher.group());
			} else
				list.add(ls);
		}
		for(String ls : list) {
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(ls.toLowerCase());
			stemmer.stem();
			sb.append(stemmer.getCurrent()+" ");
		}
		keyword = sb.toString().trim();
		return keyword;
	}

	public static String splitLabel(String label){/**旧的，仅用于KSD generation*/
		String keyword = label.replaceAll("\"|#", "");
		keyword = keyword.replaceAll("\\.|'|_|-|\\\\|/|\\(|\\)|\\.\\.\\.|:|,|;|\\?|!|\\+|~|&|\\$|%|\\^|@|\\*|=|<|>|\\[|\\]|\\{|\\}|\\|", " ");
		keyword = keyword.replaceAll("u00..", " ").trim();
		String[] labelSplit = keyword.split("\\s+");
		if(labelSplit.length == 0)
			return "";
		else if(labelSplit.length == 1){
			labelSplit = keyword.split("(?<!^)(?=[A-Z])");
		}
		StringBuilder sb = new StringBuilder();
		for(String ls:labelSplit){
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(ls.toLowerCase());
			stemmer.stem();
			sb.append(stemmer.getCurrent()+" ");
		}
		keyword = sb.toString().trim();
		return keyword;
	}

	public static String strBreakForSPO(String str){
		StringBuffer ts = new StringBuffer(str);
		for(int i =19;i<ts.length();i+=20){
			ts.insert(i,'\n');
		}
		return ts.toString();
	}
}
