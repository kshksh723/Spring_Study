<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%

String ctxPath = request.getContextPath();

%>
<style type="text/css">
    
    th {background-color: #ddd}
    
    .subjectStyle {font-weight: bold;
                   color: navy;
                   cursor: pointer; }
                   
    a {text-decoration: none !important;} /* 페이지바의 a 태그에 밑줄 없애기 */
    
</style>
<script type="text/javascript">

	$(document).ready(function(){
		
		$("span.subject").hover(function(e){
			
			$(e.target).addClass("subjectStyle");
			
		}, function(e){
			$(e.target).removeClass("subjectStyle");
			
		});
		
		
		
	}); // end of $(document).ready(function()
	
			
	//function Declaration
	function goView(seq){
		
		location.href=`<%=  ctxPath%>/view.action?seq=\${seq}`;
		
		
		
		<%-- location.href="<%=  ctxPath%>/view.action?seq="+seq; 둘 중에 아무거나 써도 된다 --%>
		
		
	} // end of function
	
</script>

<div style="display: flex;">
<div style="margin: auto; padding-left: 3%;">

   <h2 style="margin-bottom: 30px;">글목록</h2>
   
   <table style="width: 1024px" class="table table-bordered">
      <thead>
          <tr>
            <th style="width: 70px;  text-align: center;">글번호</th>
            <th style="width: 360px; text-align: center;">제목</th>
            <th style="width: 70px;  text-align: center;">성명</th>
            <th style="width: 150px; text-align: center;">날짜</th>
            <th style="width: 70px;  text-align: center;">조회수</th>
          </tr>
      </thead>
      
      <tbody>
     		<c:if test="${not empty requestScope.boardList}">
     			<c:forEach var="boardvo" items="${requestScope.boardList}">
     				<tr>
     					<td align="center">${boardvo.seq}</td>
     					<td>
		     					<span class="subject" onclick="goView('${boardvo.seq}')">
		     						${boardvo.subject}
		     					</span>
		     			</td>
     					<td align="center">${boardvo.name}</td>
     					<td align="center">${boardvo.regDate}</td>
     					<td align="center">${boardvo.readCount}</td>
     				</tr>
     			</c:forEach>
     		
     		
     		</c:if> 	
      
      		<c:if test="${empty requestScope.boardList}">
     			<tr>
     				<td colspan="5"> 데이터가 없습니다 </td>
     			
     			</tr>
     		
     		
     		</c:if> 	
      
      </tbody>
     
    </table>
</div>
</div>