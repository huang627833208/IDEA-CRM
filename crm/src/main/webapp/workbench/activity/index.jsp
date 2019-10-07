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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//为创建按钮绑定事件，打开添加操作的模态窗口
		$("#addBtn").click(function () {

			/*

				操作模态窗口：

					目标模态窗口（div）的jquery对象，调用modal方法，为方法传递参数
																	show：打开模态窗口
																	hide：关闭模态窗口


			 */

			/*alert(123);
			$("#createActivityModal").modal("show");*/

			//发出请求取得用户信息列表，为所有者的下拉框铺值，铺完值后，打开模态窗口
			$.ajax({

				url : "workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function (data) {

					/*

						data
							List<User> uList
							[{用户1},{2},{3}]

					 */

					var html = "<option></option>";

					//每一个n，就是每一个用户的json对象
					$.each(data,function (i,n) {

						html += "<option value='"+n.id+"'>"+n.name+"</option>";

					})

					//找到所有者的select标签对，往标签对中填写option
					$("#create-owner").html(html);

					//上述所有者下拉框铺值之后，打开添加操作的模态窗口
					$("#createActivityModal").modal("show");

					//使当前登录的用户，作为所有者下拉框的默认选项
					//取得当前登录用户的id
					//js中使用el表达式，el表达式必须要套用在字符串的引号中
					var id = "${user.id}";
					$("#create-owner").val(id);


				}

			})



		})


		//为保存按钮绑定事件，执行市场活动的添加操作
		$("#saveBtn").click(function () {

			$.ajax({

				url : "workbench/activity/save.do",
				data : {

					"owner" : $.trim($("#create-owner").val()),
					"name" : $.trim($("#create-name").val()),
					"startDate" : $.trim($("#create-startDate").val()),
					"endDate" : $.trim($("#create-endDate").val()),
					"cost" : $.trim($("#create-cost").val()),
					"description" : $.trim($("#create-description").val())


				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false}

					 */

					if(data.success){

						//添加成功

						//将添加操作模态窗口中的信息清除掉
						//找到表单，重置表单
						//jquery对象为我们提供了submit方法用来做提交表单的操作
						//$("#activitySaveForm").submit();
						//jquery对象虽然为我们提供了submit方法，但是并没有为我们提供reset方法
						//虽然jquery没有为我们提供reset方法，原生js为我们提供了这个方法
						/*

							jquery对象转换为dom对象
								jquery[0]


							dom对象转换为jquery对象
								$(dom)


						 */
						//$("#activitySaveForm")[0].reset();

						//刷新列表

						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


						//关闭模态窗口
						$("#createActivityModal").modal("hide");


					}else{

						alert("添加市场活动失败");

					}


				}

			})

		})


		//页面加载完毕后，刷新市场活动列表
		pageList(1,2);

		//为查询按钮绑定事件，执行市场活动的条件查询操作
		$("#searchBtn").click(function () {

			//将搜索框中的值保存到隐藏域中
			$("#hidden-name").val($("#search-name").val());
			$("#hidden-owner").val($("#search-owner").val());
			$("#hidden-startDate").val($("#search-startDate").val());
			$("#hidden-endDate").val($("#search-endDate").val());

			pageList(1,2);

		})

		//为全选复选框绑定事件，触发全选操作
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked",this.checked);


		})

		/*$("input[name=xz]").click(function () {

			alert(123);

		})*/

		/*

			以上操作alert(123)弹不了
			是因为我们所有name等于xz的input都是通过我们的js代码手动拼接生成的
			动态拼接的元素是不能够以传统形式来绑定事件的
			我们这次需要使用on方法来实现事件的绑定

			公式：
				$(找到需要绑定的元素的有效的父级元素).on(绑定事件的方式，$(需要绑定地元素),回调函数)

		 */

		$("#activityBody").on("click",$("input[name=xz]"),function () {

			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);

		})

		//为删除按钮绑定事件，执行删除市场活动操作（可批量删除）
		/*

			关于删除：
				将来在实际项目开发中的删除分成两种

				一种叫做物理删（执行delete sql 执行删除），删除之后数据就从表中彻底移除了

				一种叫做逻辑删（执行update sql 执行删除），删除之后数据还在表中


		*/
		$("#deleteBtn").click(function () {

			var $xz = $("input[name=xz]:checked");

			//没选
			if($xz.length==0){

				alert("请选择需要删除的记录");

			//选了 有可能选中了多条
			}else{

				//workbench/activity/delete.do?id=xxx&id=xxx&id=xxx
				/*

					url:workbench/activity/delete.do
					data : "id=xxx&id=xxx&id=xxx"
					后台接收参数的方式：String[] ids = request.getParameterValues("id");


				 */

				if(confirm("删不删？！")){

					var param = "";

					for(var i=0;i<$xz.length;i++){

						param += "id="+$($xz[i]).val();

						//如果不是最后一条记录
						if(i<$xz.length-1){

							param += "&";

						}

					}

					//alert(param);

					//以上参数拼接完毕之后，发出ajax请求，执行市场活动的删除操作
					$.ajax({

						url : "workbench/activity/delete.do",
						data : param,
						type : "post",
						dataType : "json",
						success : function (data) {

							/*

                                data
                                    {"success":true/false}

                             */

							if(data.success){

								//删除成功

								//刷新页面
								//pageList(1,2);

								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}else{

								alert("删除市场活动失败");

							}

						}

					})

				}


			}

		})

		//为修改按钮绑定事件，打开修改操作的模态窗口
		$("#editBtn").click(function () {

			var $xz = $("input[name=xz]:checked");

			if($xz.length==0){

				alert("请选择需要修改的记录");

			}else if($xz.length>1){

				alert("只能选择一条记录执行修改操作");

			//肯定选了，而且肯定只选了一条
			}else{

				//取得选择的复选框的value值(就是取得了需要修改记录的id值)
				//虽然我们现在操作的是复选框，但是我们现在在eles中，是能够保证复选框只选中了一条记录
				//我们就可以直接调用val方法，取得唯一选中的复选框的值

				var id = $xz.val();

				//alert("需要修改记录的id："+id);

				$.ajax({

					url : "workbench/activity/getUserListAndActivity.do",
					data : {

						"id" : id

					},
					type : "get",
					dataType : "json",
					success : function (data) {

						/*

							data
								List<User> uList...
								Activity a
								{"uList":[{用户1},{2},{3}],"a":{市场活动}}

						 */

						var html = "<option></option>";

						$.each(data.uList,function (i,n) {

							html += "<option value='"+n.id+"'>"+n.name+"</option>";

						})

						$("#edit-owner").html(html);

						//以上是为所有者的下拉框铺值

						//以下是为表单铺该条记录原有的数据
						//记得在表单中创建一个隐藏域，用来保存id
						//我们有id，才知道改的是哪一条记录

						$("#edit-id").val(data.a.id);
						$("#edit-name").val(data.a.name);
						$("#edit-owner").val(data.a.owner);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-description").val(data.a.description);

						//当表单中的值都处理完毕后，需要打开修改操作的模态窗口
						$("#editActivityModal").modal("show");

					}

				})

			}


		})

		//为更新按钮绑定事件，执行市场活动的修改操作
		$("#updateBtn").click(function () {

			$.ajax({

				url : "workbench/activity/update.do",
				data : {

					"id" : $.trim($("#edit-id").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"name" : $.trim($("#edit-name").val()),
					"startDate" : $.trim($("#edit-startDate").val()),
					"endDate" : $.trim($("#edit-endDate").val()),
					"cost" : $.trim($("#edit-cost").val()),
					"description" : $.trim($("#edit-description").val())


				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false}

					 */

					if(data.success){

						//修改成功

						//刷新市场活动列表
						//pageList(1,2);

						/*

							$("#activityPage").bs_pagination('getOption', 'currentPage')
								在执行操作后，停留在当前页上

							$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
								在执行操作后，仍然维持每页展现的记录数
						 */

						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


						//关闭模态窗口
						$("#editActivityModal").modal("hide");


					}else{

						alert("修改市场活动失败");

					}


				}

			})

		})


	});

	/*

		关于方法：
		该方法的作用是，（局部）刷新市场活动列表
		该方法都有哪些入口？（都在什么情况下，需要（局部）刷新市场活动列表）
		1.点击左侧菜单"市场活动"，跳转到该页面，在该页面加载完毕后，执行该方法，（局部）刷新市场活动列表
		2.点击查询按钮的时候，执行该方法，（局部）刷新市场活动列表
		3.点击分页组件的时候，执行该方法，（局部）刷新市场活动列表
		4.添加操作后，执行该方法，（局部）刷新市场活动列表
		5.修改操作后，执行该方法，（局部）刷新市场活动列表
		6.删除操作后，执行该方法，（局部）刷新市场活动列表


		关于参数：

		方法中为我们提供了两个参数，我们先分析方法中的两个参数
		pageNo:（当前页的）页码
		pageSize:每页需要展现的记录数
		无论我们使用什么关系型数据库，作为前端的分页操作，就肯定会使用到pageNo和pageSize，其他的组件我们可以通过这两个参数计算得到
		也就是说，这两个参数就是前端分页操作的基础建设，必须存在

		我们应该为ajax请求到后台提供的参数

		分页查询相关参数：
		pageNo
		pageSize

		条件查询相关参数：
		name
		owner
		startDate
		endDate


	 */
	function pageList(pageNo,pageSize) {

		//每次刷新列表，需要将全选复选框的√灭掉
		$("#qx").prop("checked",false);

		//alert("（局部）刷新市场活动列表");

		//从隐藏域中取值，赋值给搜索框
		$("#search-name").val($("#hidden-name").val());
		$("#search-owner").val($("#hidden-owner").val());
		$("#search-startDate").val($("#hidden-startDate").val());
		$("#search-endDate").val($("#hidden-endDate").val());

		$.ajax({

			url : "workbench/activity/pageList.do",
			data : {

				"name" : $.trim($("#search-name").val()),
				"owner" : $.trim($("#search-owner").val()),
				"startDate" : $.trim($("#search-startDate").val()),
				"endDate" : $.trim($("#search-endDate").val()),
				"pageNo" : pageNo,
				"pageSize" : pageSize

			},
			type : "get",
			dataType : "json",
			success : function (data) {

				/*

					data
						List<Activity> dataList ...
						int total ...
						{"total":100,"dataList":[{市场活动1},{2},{3}]}

				 */

				var html = "";

				//每一个n就是每一个市场活动json对象
				$.each(data.dataList,function (i,n) {

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';

				})

				//将tr往tbody里存
				$("#activityBody").html(html);

				//计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//以上列表展现完毕后，使用分页插件展现分页信息以及相关分页功能
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该函数是在点击分页组件相关按钮的时候触发，执行查询列表操作
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});



			}

		})

	}




</script>
</head>
<body>

	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>


	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">

					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id"/>

						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">



								</select>
							</div>
							<label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-name">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--

									关于textarea
										textarea与其他的表单元素（input、select）不同
										其他的表单元素（input、select）操作的都是value属性值
										textarea的值是写在标签对之间

									注意：
										textarea比较特殊，它虽然操作的是标签对中的内容，但是它也是属于表单元素范畴
											只要是表单元素，我们一概使用val()放法来操作值



								-->
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>

					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activitySaveForm" class="form-horizontal" role="form">

						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">



								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">

					<!--

						data-dismiss="modal"
							关闭模态窗口

					-->
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	

	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表123</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate"/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">

					<!--

						data-toggle="modal"
							触发该按钮，将要打开一个模态窗口（模态框）

						data-target="#createActivityModal"
							模态窗口的目标是谁，以#id的形式来进行查找

						临时需求：
							我现在想要在打开模态窗口前，做一个alert(123)的弹框

							不能够触发alert（如果能触发也会非常困难），是因为我们是将
							以下这两对属性和属性值在button元素中写死了
							只要写上，就会触发，如果我们想要处理其他的事，什么时候触发，
							仅仅只是使用元素中的属性和属性值是控制不了
							data-toggle="modal" data-target="#createActivityModal"

							所以，在未来的实际项目开发中，按钮都能够做什么，模态窗口要不要打开，什么时候打开，什么时候关闭，
														在打开前或者打开后，都需要处理什么额外的信息
														是需要方法做支撑的

							我们应该去除操作模态窗口的属性和属性值，绑定该按钮，为该按钮触发事件，由方法决定按钮的行为


					-->

				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称123</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>