package com.spring.app.common;

import javax.servlet.http.HttpServletRequest; //ctrl+shift+o

public class MyUtil {


	// *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성 *** //
	public static String getCurrentURL(HttpServletRequest request) {
		
		String currentURL = request.getRequestURL().toString();
		// System.out.println("currentURL => " + currentURL);
		// currentURL => http://localhost:9090/MyMVC/member/memberList.up

		
	String queryString = request.getQueryString();
	
//	 System.out.println("queryString => " + queryString);
	// /memberList.up?searchType=name&searchWord=유&sizePerPage=5&currentShowPageNo=15	
	 // queryString => searchType=name&searchWord=%EC%9C%A0&sizePerPage=5&currentShowPageNo=15
// queryString ==> null (post 방식일 경우)
	
	if(queryString != null ) { // GET 방식일 경우
		currentURL += "?"+queryString;
		// currentURL => http://localhost:9090/MyMVC/member/memberList.up
		
	}
	
	String ctxPath = request.getContextPath();
	// mymvc
	
	int beginIndex = currentURL.indexOf(ctxPath) + ctxPath.length();
	// 27	=		 21						+		6
	
	
	currentURL = currentURL.substring(beginIndex);
	// System.out.println("currentURL");
	return currentURL;
	
	} // end of public static String getCurrentURL(HttpServletRequest request)
}

