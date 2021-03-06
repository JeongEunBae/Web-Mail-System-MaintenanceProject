<%-- 
    Document   : register
    Created on : 2021. 5. 15., 오후 1:30:38
    Author     : 김기목
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page errorPage="error_page.jsp"%>

<%@page import = "java.sql.*"%>
<%@page import="cse.maven_webmail.control.CommandType"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />
       
        <div>
            <center>
                <strong><div>회원가입 정보 </div></strong> <br>
             </center>
             <form name="AddUser" action="UserAddhandler.do?menu=<%= CommandType.ADD_USER_COMMAND%>" method="POST">
                <center>
                아이디 :  <input type="text" name="register_id" size="20" required> <br /><br />
                비밀번호:  <input type="password" name="register_pw" id="register_pw" onchange="check_pw()" size="20" required><br /><br />
                비밀번호 확인 :  <input type="password" name="register_pw_check" id="register_pw_check" onchange="check_pw()" size="20" required> <br />
                <span id="check"></span> <br/><br/>
                이름: <input type="text" name="register_name" size="10" required> <br /><br />
                전화번호 :  <input type="tel" name="register_number" size="10" required> <br /><br />
                 <div>
              <input type="submit" value="확인" name ="register_ok" onClick ="return getConfirmResult()">
              <input type="reset" value="다시 입력" name="reset_1">
              </center>
                  </form>
        </div>
            
        </div>
     
    
                 <!-- 비밀번호 중복 여부 체크  -->             
    <script>
        function check_pw(){

            var pw = document.getElementById('register_pw').value;
            var SC = ["!","@","#","$","%"];
            var check_SC = 0;
 
            if(pw.length < 6 || pw.length>16){
                window.alert('비밀번호는 6글자 이상, 16글자 이하만 이용 가능합니다.');
                document.getElementById('register_pw').value='';
            }
            for(var i=0;i<SC.length;i++){
                if(pw.indexOf(SC[i]) != -1){
                    check_SC = 1;
                }
            }
            if(check_SC == 0){
                window.alert('!,@,#,$,% 의 특수문자가 들어가 있지 않습니다.')
                document.getElementById('register_pw').value='';
            }
            if(document.getElementById('register_pw').value !='' && document.getElementById('register_pw_check').value!=''){
                if(document.getElementById('register_pw').value==document.getElementById('register_pw_check').value){
                    document.getElementById('check').innerHTML='비밀번호가 일치합니다.'
                    document.getElementById('check').style.color='blue';
                }
                else{
                    document.getElementById('check').innerHTML='비밀번호가 일치하지 않습니다. 다시 입력해주세요.';
                    document.getElementById('check').style.color='red';
                    document.getElementById('register_pw_check').value='';
                }
            }
        }
    </script>
         <jsp:include page="footer.jsp" />

    </body>
</html>
