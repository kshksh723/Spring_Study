<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
   String ctxPath = request.getContextPath();
%>

<style type="text/css">
   
   span.move  {cursor: pointer; color: navy;}
   .moveColor {color: #660029; font-weight: bold; background-color: #ffffe6;}

    a {text-decoration: none !important;}
</style>

<script type="text/javascript">
	$(document).ready(function(){
		
	
		
	}); // end of $(document).ready(function(){});
	

</script>

<div style="display: flex;">
	<div style="margin: auto; padding-left: 3%;">

   		<h2 style="margin-bottom: 30px;">글내용보기</h2>
   		
   		<c:if test="${not empty requestScope.boardvo}">
   			<table class="table table-bordered table-dark" style="width: 1024px; word-wrap: break-word; table-layout: fixed; ">
	   			<tr>
		              <th style="width: 15%">글번호</th>
		               <td>${requestScope.boardvo.seq}</td>
	           </tr>   
	         
	           <tr>
		              <th>성명</th>
		               <td>${requestScope.boardvo.name}</td>
	           </tr> 
	         
	           <tr>
		              <th>제목</th>
		               <td>${requestScope.boardvo.subject}</td>
	           </tr>
   			
   			   <tr>
		              <th>내용</th>
		              <td>  <p style="word-break: break-all;">${requestScope.boardvo.content}
		               <%-- 
                style="word-break: break-all; 은 공백없는 긴영문일 경우 width 크기를 뚫고 나오는 것을 막는 것임. 
                    그런데 style="word-break: break-all; 나 style="word-wrap: break-word; 은
                    테이블태그의 <td>태그에는 안되고 <p> 나 <div> 태그안에서 적용되어지므로 <td>태그에서 적용하려면
                <table>태그속에 style="word-wrap: break-word; table-layout: fixed;" 을 주면 된다.
            --%></p>
            </td>
	           </tr>
   			  <tr>
		              <th>조회수</th>
		               <td>${requestScope.boardvo.readCount}</td>
	           </tr>
	            <tr>
		              <th>날짜</th>
		               <td>${requestScope.boardvo.regDate}</td>
	           </tr>
   			
   			</table>
   		</c:if>
   
   		<c:if test="${empty requestScope.boardvo}">
   			<div style="padding: 20px 0; font-size: 16pt; color: red;" >존재하지 않습니다</div> 
   		</c:if>
   	
		   	<div class="mt-5">
		   	<%-- 글 조회수 1증가를 위해서 view.action 대신에 view_2.action --%>	
		  		<div style="margin-bottom: 1%;">이전글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.previousseq}'">${requestScope.boardvo.previoussubject}</span></div>
		  		<div style="margin-bottom: 1%;">다음글제목&nbsp;&nbsp;<span class="move" onclick="javascript:location.href='view_2.action?seq=${requestScope.boardvo.nextseq}'">${requestScope.boardvo.nextsubject}</span></div>
		  		<br>
		  		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/list.action'">전체목록보기</button>
		  		<c:if test="${not empty sessionScope.loginuser && sessionScope.loginuser.userid == requestScope.boardvo.fk_userid}">
		  		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/edit.action?seq=${requestScope.boardvo.seq}'">글 수정하기</button>
		  		
		  		<button type="button" class="btn btn-secondary btn-sm mr-3" onclick="javascript:location.href='<%= ctxPath%>/del.action?seq=${requestScope.boardvo.seq}'">글 삭제하기</button>
		  	</c:if>
		  	</div>                                                                                                                                                                                   
	</div>
</div>