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
<title>Ajax 연습1</title>
<style type="text/css">
   table, th, td {
      border: solid 1px gray;
      border-collapse: collapse;
   }
</style>

<script type="text/javascript" src="<%= ctxPath%>/resources/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
   $(document).ready(function(){
      
      func_ajax_select();
      
      $("button#btnOK").click(function(){
         const no_val = $("input:text[name='no']").val();
            const name_val = $("input:text[name='name']").val();
            
            if( no_val.trim() == "" || name_val.trim() == "" ) {
               alert("번호와 성명 모두 입력하세요!!");
                return; // 종료 
            }
            
            $.ajax({
               url : "<%= ctxPath%>/test/ajax_insert.action",
               type : "post",
               data : {"no":no_val,
                     "name":name_val},
               dataType : "json",
               success : function(json){
                  // console.log(JSON.stringify(json));
                  // 기대치 "{"n":1}"
                  
                  if(json.n==1){
                     func_ajax_select();
                     
                     $("input:text[name='no']").val("");
                     $("input:text[name='name']").val("");
                  }
                  
                  
               },
               error: function(request, status, error){
                   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
                  }
               
            });
      });
      
   });// end of $(document).ready(function()--------
   
   // Function Declaration
   function func_ajax_select(){
      
      $.ajax({
         url : "<%= ctxPath%>/test/ajax_select.action",
         dataType : "json",
         success : function(json){
            
            // console.log(JSON.stringify(json));   // 한글이 깨져 나올 경우가 있음 지금은 버전이 업그레이드 되서 인지 안깨짐
            let v_html = `<table>
                          <tr>
                             <th>번호</th>
                             <th>입력번호</th>
                             <th>성명</th>
                             <th>작성일자</th>
                          </tr>`;
            $.each(json, function(index, item){
               v_html += `<tr>
                                <td>\${index+1}</td>
                             <td>\${item.no}</td>
                             <td>\${item.name}</td>
                             <td>\${item.writeday}</td>
                          </tr>`;
            });// end of $.each(json, function(index, item){
            
            v_html += `</table>`;
            
            $("div#view").html(v_html);
            
            
         },
         error: function(request, status, error){
               alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
              }
         
      });
      
   }// end of function func_ajax_select()----
   
</script>
</head>
<body>
<h2>Ajax 연습1</h2>
    <p>
             안녕하세요?<br>
             여기는  /board/test/test_form5.action 페이지 입니다.
    </p>
    
      번호 : <input type="text" name="no" /><br>
      성명 : <input type="text" name="name" /><br>
       <button type="button" id="btnOK">확인</button>
       <button type="reset" id="btnCancel">취소</button>
       <br><br> 
   
   <div id="view"></div>

</body>
</html>

