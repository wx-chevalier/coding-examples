package nju.iip.kevin.ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

/**
 * 用户接口类，该类负责提供用户输入输出的接口
 */
public class AcDroid{
	/*调试参数，设定是否需要显示程序运行时间*/
	private boolean showtimeFlag; 
	/**
	 * @function 默认无参构造函数
	 */
	public AcDroid() {
		showtimeFlag = false;
	}
	/**
	 * @function 用于在文本中搜索单字符串
	 * @param 
	 * @param 
	 * @return 
	 */
	public Set findSingleWord(String keyword, String text){
		try{
			if(keyword.trim().length()==0){
				throw new Exception("Key word's content is empty.");
			}
		}catch(Exception e){
			System.out.println("Key word's content is empty.");
			e.printStackTrace();			
		}
		/*将输入的单字符串放入字符串数组中并调用findWordsInArray函数*/
		String[] keywords = {keyword.trim()};
		return findWordsInArray(keywords, text);
	}

	public Set findWordsInArray(String[] keywords, String text){
		/*去除字符串首尾的无意义空格*/
		text = text.trim();
		long startTime=0,endTime=0;
		try{
			if(keywords.length==0){
				throw new Exception("Array of keywords is empty.");
			}
		}catch(Exception e){
			System.out.println("Array of keywords is empty.");
			e.printStackTrace();
		}

		AhoCorasick tree = new AhoCorasick();
		
		if(showtimeFlag){
			startTime = System.currentTimeMillis();
		}
		int length = keywords.length;
		
		for (int i=0; i<length; i++) {
			if(keywords[i].trim().length()!=0){
				tree.add(keywords[i].trim().getBytes(), keywords[i].trim());
			}
		}
		tree.prepare();

		Set arr_output = new HashSet();
		Iterator iter = tree.search(text.getBytes());
		SearchResult result = null;
		while (iter.hasNext()) {
			result = (SearchResult) iter.next();
			arr_output.addAll(result.getOutputs());
		}
		if(showtimeFlag){
			endTime = System.currentTimeMillis();
			System.out.println("run time: " + (endTime-startTime)+"ms");
		}
		return arr_output;
	}

	/**
	 * @function 用于从文件中读取模串集合与匹配文本进行查找
	 * @param filename1
	 * @param filename2
	 * @return
	 */
	public Set findWordsInFile(String filename1, String filename2){
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		StringBuffer textbuffer = new StringBuffer();
		String keyword = null;
		String text = null;	

		AhoCorasick tree = new AhoCorasick();
		long startTime=0,endTime=0;
		try {			
			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename1)), "UTF-8"));
			while ((keyword=br1.readLine()) != null) {
				if(keyword.trim().length()!=0){
					tree.add(keyword.trim().getBytes(), keyword.trim());
				}
			}

			br2 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename2)), "UTF-8"));
			while ((text=br2.readLine()) != null) {
				if(text.trim().length()!=0){
					textbuffer.append(text.trim());
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can not find the file.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupport file encode type.");
			e.printStackTrace();
		}catch(IOException e){
			System.out.println("Read file is error.");
			e.printStackTrace();			
		}

		tree.prepare();

		Set arr_output = new HashSet();
		Iterator iter = tree.search(textbuffer.toString().getBytes());
		SearchResult result = null;
		while (iter.hasNext()) {
			result = (SearchResult) iter.next();
			arr_output.addAll(result.getOutputs());
		}
		if(showtimeFlag){
			endTime = System.currentTimeMillis();
			System.out.println("run time:" + (endTime-startTime)+"ms");
		}
		return arr_output;					
	}
}
