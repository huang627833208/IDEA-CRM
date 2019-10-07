<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script>

		$(function () {

			//如果当前窗口（登录页），不是顶层窗口，我们需要将顶层窗口设置为当前窗口
			if(window.top!=window){
				window.top.location=window.location;
			}


			//页面加载完毕后，让用户名自动获得焦点
			$("#loginAct").focus();

			//页面加载完毕后，将用户名文本框中的内容清空掉
			$("#loginAct").val("");

			//为登录按钮绑定事件，执行登录操作
			$("#submitBtn").click(function () {

				login();

			})

			//为当前窗口绑定敲键盘事件，如果敲回车则执行登录操作
			//我们可以通过event参数接收敲键盘的键位码值
			$(window).keydown(function (event) {

				//如果码值为13，说明敲的是回车键
				if(event.keyCode==13){

					login();

				}

			})


		})


		function login() {

			//取得账号密码
			//$.trim(内容):去掉左右空格
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());

			//验证账号密码不能为空
			if(loginAct=="" || loginPwd==""){

				$("#msg").html("账号密码不能为空");

				//一旦进入到了该if，说明账号或者密码为空，就没有必须继续向下验证了
				//我们需要及时终止掉当前方法
				return false;

			}

			//验证账号密码是否正确
			//ajax
			$.ajax({

				url : "settings/user/login.do",
				data : {

					"loginAct" : loginAct,
					"loginPwd" : loginPwd

				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false,"msg":?}

					 */

					//如果返回值为true，表示登录成功
					if(data.success){

						//如果登录成功,跳转到工作台的初始页（同时也是登录后的欢迎页）
						window.location.href = "workbench/index.jsp";


					//表示登录失败
					}else{

						//如果登录失败，应该在<span id="msg"></span>标签对中，为用户提供错误信息
						$("#msg").html(data.msg);


						/*

							关于错误消息，出现了乱码
							在web开发中，都需要处理哪些乱码？
							1.get请中文参数乱码
								我们现在使用的是tomcat9，所以get请求中文乱码不会出现
								如果我们在企业中使用的是较低版本的tomcat，那么get请求的乱码还是需要处理的
								tomcat/conf/server.xml 63/69（配置端口号的这一行） 加上URIEncoding="UTF-8"

							2.post请求中文参数乱码问题
								request.setCharacterEncoding("UTF-8");

							3.响应流响应中文出现乱码
								response.setContentType("text/html;charset=utf-8")

						 */



					}


				}

			})

		}

	</script>

</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg" style="color: red"></span>
						
					</div>
					<!--

						此处按钮类型，一定要改为button，否则会发出传统请求提交表单

					-->
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>