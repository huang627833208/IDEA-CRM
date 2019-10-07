<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>title</title>
</head>
<body>

        $.ajax({

            url : "",
            data : {


            },
            type : "",
            dataType : "json",
            success : function (data) {


            }

        })

        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();



        boolean flag = true;

        int count = clueActivityRelationDao.delete(id);

        if(count!=1){

        flag = false;

        }

        return flag;

</body>
</html>
