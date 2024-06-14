<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String ctxPath = request.getContextPath();
    //     /board
%>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>form 연습3</title>

<script type="text/javascript" src="<%= ctxPath%>/resources/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		
		$("form[name='testFrm']").submit(function(){
			
			const no_val = $("input:text[name='no']").val();
			const name_val = $("input:text[name='name']").val();
			
			if( no_val.trim() == "" || name_val.trim() == "" ) {
				alert("번호와 성명 모두 입력하세요!!");
			 	return false; // submit(전송)을 하지말라는 뜻이다. 
			}
			
		});
		
	});
</script>

</head>
<body>

	<div>/test/test_form3.action 페이지</div>
	<br>
	<form name="testFrm" action="<%= ctxPath%>/test/test_form2.action" method="post"> 
		번호 : <input type="text" name="no" /><br>
		성명 : <input type="text" name="name" /><br>
	    <input type="submit" value="확인" />
	    <input type="reset"  value="취소" /> 
	</form>

</body>
</html>




