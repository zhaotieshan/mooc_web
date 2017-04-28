package org.mooc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.mooc.utility.MongoConn;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * @author: wuke 
 * @date  : 2016年11月24日 下午5:29:46
 * Title  : FrequentRec
 * Description : Servlet. Respond to user's request, which include userId, page and pageSize, 
 * an example, 
 * http://localhost:8080/mooc/fpRec?userId=40f3d4bf-4631-4181-95d4-7714576db407&page=1&pageSize=5
 */
public class FrequentRec extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// Get userId and n from HttpServletRequest request
		String userId = null;
		userId = request.getParameter("userId");
		
		// Query from MongoDB mooc.frequentRec
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", "frequentRec");
		
		Bson filter = Filters.eq("userId", userId);
		
		BasicDBObject sort = new BasicDBObject();
	    sort.put("count",-1); // 1, ascending; －1, descending
		
		int page = 0;
		int pageSize = 0;
	    List<Document> docList = null;
		if(request.getParameter("page") != null && request.getParameter("pageSize") !=null) { // Split pages
			page = Integer.parseInt(request.getParameter("page"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));			
		    
			docList = collection.find(filter).
					sort(sort).skip((page -1) * pageSize).limit(pageSize).into(new ArrayList<Document>());
		} else { // Show all the recommendations
			docList = collection.find(filter).sort(sort).into(new ArrayList<Document>());
		}
		
		// Transform docList into String by method of Gson
		String json=null;
		Gson gson = new Gson();
		json = gson.toJson(docList);
		
		// Respond to the request
		response.setContentType("application/json;charset=utf-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().write(json);
        
        // Print some information to the Console
		System.out.println("client IP adderss:" + request.getRemoteAddr());
        System.out.println(json);
	}

}
