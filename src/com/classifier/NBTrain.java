package com.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class NBTrain {
	//static final File folder = new File("C:/ReferenceDocs/IR/Assignment/Classifier/textcat/train");
	//static final File folder = new File("C:/ReferenceDocs/IR/Assignment/Classifier/testData");
	static File folder=null;
	static HashMap<String, ArrayList<Integer>> vocab = new HashMap<String, ArrayList<Integer>>(); 
	
	static  int posFilesCount=0;
	static int negFileCount=0;
	static int posTermCount=0;
	static int negTermCount=0;
	
	static String sentimentType=null;

	public static void main(String[] args) throws Exception {
		
		folder=new File(args[0]);
		getFiles(folder);
		vocabFilter();
		//writetoFile("src/Model.txt");
		writetoFile(args[1]);
	}
	public static void getFiles(File filePath){
		
		for ( File fileEntry : filePath.listFiles()) {
			//Reading the file and creating the train data
			if(fileEntry.isDirectory())
			{
				sentimentType=fileEntry.getName();
				System.out.println("sentiment type: "+sentimentType);
				if("pos".equalsIgnoreCase(sentimentType))
					posFilesCount=fileEntry.listFiles().length;
				else
					negFileCount=fileEntry.listFiles().length;
				getFiles(fileEntry.getAbsoluteFile());
			}
			else
			{
				readFile(fileEntry, sentimentType);
			}

		}
	}
	private static void readFile(File fileEntry, String sentimentType) {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileEntry));
			String line=null;
			while((line= br.readLine()) != null){
				parseLine(line,sentimentType);
			}
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void parseLine(String line, String sentimentType) {
		// TODO Auto-generated method stub
		String tokens[] = line.split(" ");
		ArrayList<Integer> data = null;
		for(String str:tokens){
			//if(!str.trim().equalsIgnoreCase(""))
			//{
			if(vocab.containsKey(str)){
				data = vocab.get(str);
				if(sentimentType.equals("pos")){
					data.set(0, data.get(0)+1);
				}
				else{
					data.set(1, data.get(1)+1); 
				}
				vocab.put(str, data);


			}
			else{
				data = new ArrayList<>();
				if(sentimentType.equals("pos")){
					data.add(1);
					data.add(0);
				}
				else{
					data.add(0);
					data.add(1);
					
				}
				vocab.put(str, data);
			}
		//}
		}

	}
	
	private static void vocabFilter()
	{
		ArrayList<String> lowKeys=new ArrayList<String>();
		for(Entry<String,ArrayList<Integer>> entry:vocab.entrySet())
		{
			if((entry.getValue().get(0)+entry.getValue().get(1))<5)
			{
				lowKeys.add(entry.getKey());
			}
			else
			{
				posTermCount=posTermCount+entry.getValue().get(0);
				negTermCount=negTermCount+entry.getValue().get(1);
			}
			
		}
		
		for(String key:lowKeys)
		{
			vocab.remove(key);
		}
	}
	
	private static void writetoFile(String file) throws Exception
	{
		FileWriter fw=null;
		StringBuffer buff=new StringBuffer();
		
		buff.append("PosPriori "+((float)posFilesCount/(posFilesCount+negFileCount)));
		buff.append(" NegPriori "+((float)negFileCount/(posFilesCount+negFileCount)));
		buff.append(" vocabCount "+vocab.size());
		buff.append(" posTermCount "+posTermCount);
		buff.append(" negTermCount "+negTermCount);
		
		buff.append(System.getProperty("line.separator"));
		
		for(Entry<String,ArrayList<Integer>> entry:vocab.entrySet())
		{
			buff.append(entry.getKey()+ " "+entry.getValue().get(0)+" "+entry.getValue().get(1));
			buff.append(System.getProperty("line.separator"));
		}
		
		try
		{
			fw=new FileWriter(new File(file));
			fw.write(buff.toString());
		}
		finally
		{
			if(null!=fw)
				fw.close();
		}
	}
}