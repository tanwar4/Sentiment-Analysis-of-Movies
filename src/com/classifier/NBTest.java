package com.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;


public class NBTest {

	static HashMap<String, ArrayList<Integer>> vocab=new HashMap<String, ArrayList<Integer>>();
	static TreeMap<String , ArrayList<String>> nbScore = new TreeMap<String, ArrayList<String>>();
	static TreeMap<Double,String> posToNegVocab=new TreeMap<Double, String>(Collections.reverseOrder());
	static TreeMap<Double,String> negToPosVocab= new TreeMap<Double, String>(Collections.reverseOrder());

	static  double posPriori=0.0;
	static double negPriori=0.0;
	static int posTermCount=0;
	static int negTermCount=0;
	static int vocabCount=0;

//	static String vocabFile= "src/Model.txt";
	static String vocabFile=null;
//	static String devDoc = "C:/ReferenceDocs/IR/Assignment/Classifier/textcat/test";
	static String devDoc=null;
	
//	static String resultFilePath="C:/ReferenceDocs/IR/Assignment/Classifier";
	static String resultFilePath=null;
	
	static String sentimentType="";
	
	static int correctPos=0;
	static int correctNeg=0;

	public static void main(String args[]) throws Exception
	{
		vocabFile=args[0];
		devDoc=args[1];
		resultFilePath=vocabFile.substring(0,vocabFile.lastIndexOf("/"));
		System.out.println("res::"+resultFilePath);
		
		updateVocab();
		System.out.println(":::Vocab Updated:::");
		System.out.println("vocab size::"+vocab.size());
		predictSentiment();
		System.out.println(":::End of Classification:::");
		
	}

	private static void predictSentiment() throws Exception {
		
		getFiles(new File(devDoc));
		nbScoreToFile();
		ratioOfWeights();
		writeRatioToFile();
	}

	public static void getFiles(File filePath) throws IOException{

		for ( File fileEntry : filePath.listFiles()) {
			//Reading the file and creating the train data
			if(fileEntry.isDirectory())
			{
				sentimentType=fileEntry.getName();
				getFiles(fileEntry.getAbsoluteFile());
			}
			else
			{
				readFile(fileEntry);
			}

		}
	}
	private static void readFile(File fileEntry) throws IOException {
		
		BufferedReader br=null;
		
		try
		{
		br = new  BufferedReader(new FileReader(fileEntry));

		String str = null;
		double posScore=0.0;
		double negScore=0.0;
		
		while((str = br.readLine()) != null){
			String s[] = str.split(" ");
			for(String token:s){
				if(null!=vocab.get(token))
				{
					
				posScore = posScore + (Math.log((vocab.get(token).get(0)+1)/(float)(vocabCount+posTermCount)));
				
				negScore = negScore + (Math.log((vocab.get(token).get(1)+1)/(float)(vocabCount+negTermCount)));
				}
				else
				{
					posScore = posScore + (Math.log(1/(float)(vocabCount+posTermCount)));
					negScore = negScore + (Math.log(1/(float)(vocabCount+negTermCount)));
				}
				
				//System.out.println("posscore::"+posScore);
				//System.out.println("negscore::"+negScore);
			}		

		}
		posScore=posScore+Math.log(posPriori);
		negScore=negScore+Math.log(negPriori);
		
		ArrayList<String> al = new ArrayList<String>();
		al.add(posScore+"");
		al.add(negScore+"");
		if(posScore>= negScore){
			al.add("pos");
			if("pos".equalsIgnoreCase(sentimentType))
				correctPos++;
		}
		else{
			al.add("neg");
			if("neg".equalsIgnoreCase(sentimentType))
				correctNeg++;
		}
		al.add(sentimentType);
		

       nbScore.put(fileEntry.getName(), al);
		}
		finally
		{
			br.close();
		}

	}

	private static void updateVocab() throws Exception
	{
		String line=null;
		FileReader fr=new FileReader(new File(vocabFile));
		BufferedReader br=null;
		
		try
		{
			br=new BufferedReader(fr);

			updateCounts(br.readLine());

		while(null!=(line=br.readLine()))
		{
			String[] vocabVals=line.split(" ");
			ArrayList<Integer> vals=new ArrayList<Integer>();
			vals.add(Integer.parseInt(vocabVals[1]));
			vals.add(Integer.parseInt(vocabVals[2]));

			vocab.put(vocabVals[0], vals);
		}
		}
		finally
		{
			if(null!=br)
				br.close();
		}
	}

	private static void updateCounts(String line)
	{
		String[] vals=line.split(" ");

		posPriori=Double.parseDouble(vals[1]);
		negPriori=Double.parseDouble(vals[3]);
		vocabCount=Integer.parseInt(vals[5]);
		posTermCount = Integer.parseInt(vals[7]);
		negTermCount = Integer.parseInt(vals[9]);		

	}
	
	private static void ratioOfWeights()
	{
		double posRatio=0.0;
		double negRatio=0.0;
		for(Entry<String,ArrayList<Integer>> entry:vocab.entrySet())
		{
			posRatio=(Math.log((vocab.get(entry.getKey()).get(0)+1)/(float)(vocabCount+posTermCount)))
					- (Math.log((vocab.get(entry.getKey()).get(1)+1)/(float)(vocabCount+negTermCount)));
			
			posToNegVocab.put(posRatio, entry.getKey());
			
			negRatio=(Math.log((vocab.get(entry.getKey()).get(1)+1)/(float)(vocabCount+negTermCount)))
					- (Math.log((vocab.get(entry.getKey()).get(0)+1)/(float)(vocabCount+posTermCount)));
			
			negToPosVocab.put(negRatio, entry.getKey());
		}
	}
	private static void writeRatioToFile() throws Exception
	{
		StringBuffer posBuff=new StringBuffer();
		StringBuffer negBuff=new StringBuffer();
		
		for(Entry<Double, String> entry:posToNegVocab.entrySet())
		{
			List<Integer> scores=vocab.get(entry.getValue());
			posBuff.append(entry.getValue()+" "+entry.getKey()+" "+scores.get(0)+" "+scores.get(1));
			posBuff.append(System.getProperty("line.separator"));
		}
		
		writeToFile(posBuff.toString(), "PosToNegTerms");
		
		for(Entry<Double, String> entry:negToPosVocab.entrySet())
		{
			List<Integer> scores=vocab.get(entry.getValue());
			negBuff.append(entry.getValue()+" "+entry.getKey()+" "+scores.get(0)+" "+scores.get(1));
			negBuff.append(System.getProperty("line.separator"));
		}
		
		writeToFile(negBuff.toString(), "NegToPosTerms");
			
	}
	
	private static void nbScoreToFile() throws Exception
	{
		StringBuffer buff=new StringBuffer();
		int posCount=0;
		int negCount=0;
		
		for(Entry<String, ArrayList<String>> entry:nbScore.entrySet())
		{
			buff.append(entry.getKey()+" "+entry.getValue().get(0)+":"+ entry.getValue().get(1)+" "+entry.getValue().get(2)+" "+entry.getValue().get(3));
			buff.append(System.getProperty("line.separator"));
			if(entry.getValue().get(2).equalsIgnoreCase("pos"))
				posCount++;
			else
				negCount++;
				
		}
		buff.append("Positives :: "+posCount+" Negatives :: "+negCount);
		buff.append(System.getProperty("line.separator"));
		buff.append("CorrectPositives(for dev) :: "+correctPos+" CorrectNegatives(for dev) :: "+correctNeg);
		System.out.println("buff::"+buff.toString());
		
		writeToFile(buff.toString(),"Classifier");
	}
	
	private static void writeToFile(String buff,String filePrefix) throws Exception
	{
		
		
		FileWriter fw=null;
		
		try
		{
			fw=new FileWriter(new File(resultFilePath+"/"+filePrefix+"_"+new SimpleDateFormat("MM-dd-yy_HH-mm-ss").format(new Date())));
			fw.write(buff.toString());
		}
		finally
		{
			fw.close();
		}
	}
}
