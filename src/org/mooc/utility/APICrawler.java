package org.mooc.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
* @author : wuke
* @date   : 2017年3月31日下午7:06:09
* Title   : APICrawler
* Description : get all the content in a URL and store them into a String
*/
public class APICrawler {
	/**
	 * get all the content in the URL and store them into a String
	 * @param url
	 * @param param
	 * @return
	 */
	public static String getApiContent(String url,String param) {
		BufferedReader br = null;
		InputStreamReader isr = null;
		String str = "";
		
		try {
			URL aUrl = new URL(url);
			
			isr = new InputStreamReader(aUrl.openStream(), param);
			br = new BufferedReader(isr);

			str = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;
	}
}
