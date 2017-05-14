package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bson.Document;
import org.mooc.utility.MongoConn;

import com.mongodb.client.MongoCollection;

/**
 * @author: wuke 
 * @date  : 20161220 4:29:08
 * Title  : MyApriori
 * Description : 
 */
public class MyApriori {

	private static double MIN_SUPPORT = 0.01; // min support
	
	static {
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", "userCourses");
		MIN_SUPPORT *= collection.count();
	}
	
	/**
	 * Tesing.
	 */
	private static void test() {
		ArrayList<String> dataList = GenAprioriDataset.generateRecords();
		
		// test MyApriori.findFrequentOneItemset()
		Map<String, Integer> frequentOneItemsetMap = MyApriori.findFrequentOneItemset(dataList);
		
		System.out.println(frequentOneItemsetMap.size());
		for(Map.Entry<String, Integer> entry : frequentOneItemsetMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
		// test MyApriori.genCandidateTwoItemset()
		Set<String> candidateTwoItemset = genCandidateTwoItemset(frequentOneItemsetMap);
		
		System.out.println(candidateTwoItemset.size());
		for(String str : candidateTwoItemset) {
			System.out.println(str);
		}
		
		// test MyApriori.findFrequentTwoItemset()
		Map<String, Integer> frequentTwoItemsetMap = MyApriori.findFrequentTwoItemset(dataList, frequentOneItemsetMap);
		
		System.out.println(frequentTwoItemsetMap.size());
		for(Map.Entry<String, Integer> entry : frequentTwoItemsetMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	/**
	 * Find the frequent one itemset.
	 * @param dataList ArrayList<String>
	 * @return frequentOneItemset Map<String, Integer>
	 */
	static Map<String, Integer> findFrequentOneItemset(ArrayList<String> dataList) {
		Map<String, Integer> candidateOneItemsetMap = new TreeMap<String, Integer>(); // orderly
		
		// Iterate through the dataList, count every item's appearance times.
		String[] strArr = null;
		for(String str : dataList) { // iterate the dataList
			strArr = str.split(",");
			
			for(String temp : strArr) { 
				if(candidateOneItemsetMap.containsKey(temp)) {
					candidateOneItemsetMap.put(temp, candidateOneItemsetMap.get(temp)+1);
				} else {
					candidateOneItemsetMap.put(temp, 1);
				}
			}
		}
		
		// Return the items in candidate set which satisfy the MIN_SUPPORT
		return MyApriori.minSupport(candidateOneItemsetMap);
	}
	
	/**
	 * Find the frequent two itemset.
	 * @param dataList
	 * @param frequentOneItemset
	 * @return 
	 */
	static Map<String, Integer> findFrequentTwoItemset(ArrayList<String> dataList, 
			Map<String, Integer> frequentOneItemset) {
		Set<String> candidateTwoItemset = genCandidateTwoItemset(frequentOneItemset);
		
		Map<String, Integer> candidateTwoItemsetMap = new TreeMap<String, Integer>();
		
		// Count the appearance times of the two itemset in the dataList.
		for(String candidate : candidateTwoItemset) { 
			for(String data : dataList) {
				boolean flag = true;
				
				String[] items = candidate.split(",");
				for(String str : items) {
					if(data.indexOf(str) == (-1)) { // one of the two items is not in the record "data"
						flag = false;
						break;
					}
				}
				
				if(flag) {
					if(candidateTwoItemsetMap.containsKey(candidate))
						candidateTwoItemsetMap.put(candidate, candidateTwoItemsetMap.get(candidate)+1);
					else
						candidateTwoItemsetMap.put(candidate, 1);
				}					
			}
		}
		
		// Return the items in candidate set which satisfy the MIN_SUPPORT
		return MyApriori.minSupport(candidateTwoItemsetMap);
	}
	
	/**
	 * Using the frequent one itemset to generate candidate two itemset.
	 * @param frequentOneItemset
	 * @return candidateTwoItemset
	 */
	private static Set<String> genCandidateTwoItemset(Map<String, Integer> frequentOneItemset) {
		Set<String> candidateTwoItemSet = new TreeSet<String>();
		
		for(Map.Entry<String, Integer> entry1 : frequentOneItemset.entrySet()) {
			String str1 = entry1.getKey();
			
			for(Map.Entry<String, Integer> entry2 : frequentOneItemset.entrySet()) {
				String str2 = entry2.getKey();
				
				StringBuilder temp = new StringBuilder();
				if(str1.compareTo(str2) < 0) {
					temp.append(str1).append(",").append(str2);
					candidateTwoItemSet.add(temp.toString()); // ���Ӳ�
				}
			}
		}
		
		return candidateTwoItemSet;
	}
	
	/**
	 * Judge if the items in candidate itemset satisfy the MIN_SUPPORT. If don't, remove it.
	 * @param subject
	 * @return
	 */
	private static Map<String, Integer> minSupport(Map<String, Integer> subject) {
		Iterator<Map.Entry<String, Integer>> iter = subject.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if(entry.getValue() < MIN_SUPPORT) { // smaller, delete the item
				iter.remove();
			}
		}
		
		return subject;
	}
}
