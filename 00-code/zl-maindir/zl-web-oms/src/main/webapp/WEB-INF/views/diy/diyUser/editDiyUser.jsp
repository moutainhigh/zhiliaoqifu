<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/views/common/init.jsp"%>
    <%@ include file="/WEB-INF/views/common/head.jsp"%>
        <script src="${ctx}/static/oms/js/diy/diyUser/editDiyUser.js"></script>
</head>
<body>
	   <%@ include file="/WEB-INF/views/common/navbar.jsp"%>
          <!-- main content -->
            <div id="contentwrapper">
                <div class="main_content">
                	<nav>
			            <div id="jCrumbs" class="breadCrumb module">
			                <ul>
			                    <li><a href="#"><i class="icon-home"></i></a></li>
			                    <li>商户自助平台管理</li>
					            <li><a href="${ctx }/diy/diyUser/listDiyUser.do">商户用户管理</a></li>
			                    <li>编辑用户</li>
			                </ul>
			            </div>
			        </nav>
					 <div class="row-fluid">
					 	<div class="span12">
							<form id="mainForm" class="form-horizontal form_validation_tip" method="post">
								<input type="hidden" id="userId" name="userId" value="${diyUser.id }">
								<input type="hidden" id="roleIdFinance" name="roleIdFinance" value="${roleIdFinance}">
								<input type="hidden" id="roleIdBoss" name="roleIdBoss" value="${roleIdBoss}">
								 <h3 class="heading">编辑用户</h3>
						         <div class="control-group">
						             <label class="control-label">用户名<span style="color:red">*</span></label>
						             <div class="controls">
						                 <input type="text" class="span6" id="userName" name="userName" value="${diyUser.userName }" maxlength="32" onkeyup="this.value=this.value.replace(/\s+/g,'')"/>
						                 <span class="help-block"></span>
						             </div>
						         </div>
						         <div class="control-group">
						             <label class="control-label">手机号码<span style="color:red">*</span></label>
						             <div class="controls">
						                 <input type="text" class="span6" id="phoneNo" name="phoneNo" value="${diyUser.phoneNo }" maxlength="11" onkeyup="this.value=this.value.replace(/\s+/g,'')"/>
						                 <span class="help-block"></span>
						             </div>
						         </div>
						         
						        <%--  <div class="control-group">
						             <label class="control-label">所属分销商</label>
						             <div class="controls">
										<select class="form-control span6" id="supplierId" name="supplierId">
											<option value="0" selected="selected">请选择</option>
											 <c:forEach var="sup" items="${supplierList}" varStatus="st">
											 		<option value="${sup.supplierId}" <c:if test="${sup.supplierId == diyUser.supplierId}"> selected="selected"</c:if> >${sup.supplierName}</option>
											 </c:forEach>
										 </select>
										 <span class="help-block"></span>
						             </div>
						         </div> --%>
						         
						         <div class="control-group">
						             <label class="control-label">角色<span style="color:red">*</span></label> 
						             <div class="controls">
						                  <select class="form-control span6" id="roleIds" name="roleIds">
						                  	<option value="0" selected="selected">请选择</option>
											 <c:forEach var="diyRole" items="${diyRoleList}" varStatus="st">
											 		<option value="${diyRole.id}"
											 		 	<c:forEach var="usRoles" items="${diyUserRoleList}" varStatus="us">
											 		 		<c:if test="${diyRole.id == usRoles.id}">selected="selected"</c:if>
											 		 	</c:forEach>
											 		 >
											 		 ${diyRole.roleName }
											 		 </option>
											 </c:forEach>
											</select>
						                 <span class="help-block"></span>
						             </div>
						         </div>
						         
						         <div class="control-group ">
				                            <div class="controls">
				                            	<sec:authorize access="hasRole('ROLE_DIY_USER_EDITCOMMIT')">
				                                	<button class="btn btn-primary btn-submit" type="submit">提 交</button>
				                                </sec:authorize>
				                                <button class="btn btn-inverse btn-reset" type="reset">重 置</button>
				                            </div>
				                  </div>
						     </form>
					     </div>
				     </div>
				</div>
		</div>
</body>
</html>