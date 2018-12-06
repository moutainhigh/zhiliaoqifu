<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/views/common/init.jsp"%>
    <%@ include file="/WEB-INF/views/common/head.jsp"%>
    <script src="${ctx}/static/oms/js/specialAccount/company/listCompany.js"></script>
</head>
<body>
	   <%@ include file="/WEB-INF/views/common/navbar.jsp"%>
            <div id="contentwrapper">
                <div class="main_content">
                	<nav>
			            <div id="jCrumbs" class="breadCrumb module">
			                <ul>
			                    <li><a href="#"><i class="icon-home"></i></a></li>
			                    <li>专用账户管理</li>
			                    <li><a href="${ctx }/specialAccount/company/listCompany.do">企业管理</a></li>
			                    <li>企业列表</li>
			                </ul>
			            </div>
			        </nav>
					<form id="searchForm" action="${ctx }/specialAccount/company/listCompany.do" class="form-inline" method="post">
						<input type="hidden" id="operStatus"  value="${operStatus }"/>
						<h3 class="heading">企业列表</h3>
						<div class="row-fluid" id="h_search">
							 <div class="span10">
		                       	<div class="input-prepend">
		           			   	   	<span class="add-on">企业名称</span><input id="name" name="name" type="text" class="input-medium" value="${companyInf.name }" />
		                       	</div>
		                       	<%-- <div class="input-prepend">
		           			   	   	<span class="add-on">企业类型</span><input id="type" name="type" type="text" class="input-medium" value="${companyInf.type }" />
		                       	</div> --%>
		                       	<div class="input-prepend">
		           			   	   	<span class="add-on">交易开关状态</span>
		           			   	   	<select name="flag" id="flag" class="input-medium">
                                    <option value="">--请选择--</option>
                                    <option <c:if test="${companyInf.flag == '0' }">selected="selected"</c:if> value="0">开</option>
                                    <option <c:if test="${companyInf.flag == '1' }">selected="selected"</c:if> value="1">关</option>
                                    </select>
		                       	</div>
		                       	<div class="input-prepend">
		           			   	   	<span class="add-on">联系人</span><input id="contacts" name="contacts" type="text" class="input-medium" value="${companyInf.contacts }" />
		                       	</div>
							</div>
							<div class="pull-right">
								<button type="submit" class="btn btn-search">查 询</button>
								<button type="reset" class="btn btn-inverse btn-reset">重 置</button>
								<sec:authorize access="hasRole('ROLE_COMPANY_INFO_INTOADD')">
								<button type="button" class="btn btn-primary btn-add">新增企业</button>
								</sec:authorize>
							</div>
						</div>
						
				         </br >       
				         <table class="table table-striped table-bordered dTableR table-hover" id="dt_gal">
				             <thead>
				             <tr>
				               <th>企业名称</th>
				               <!-- <th>企业类型</th> -->
				               <th>企业代码</th>
				               <th>统一社会信用代码</th>
				               <th>地址</th>
				               <th>联系人</th>
				               <th>联系电话</th>
				               <th>交易开关状态</th>
				               <th>备注</th>
				               <th>操作</th>
				             </tr>
				             </thead>
				             <tbody>
				             <c:forEach var="company" items="${pageInfo.list}" varStatus="st">
				                 <tr>
				                 	<td>${company.name}</td>
									<%-- <td>${company.type}</td> --%>
				                    <td>${company.comCode}</td>
				                    <td>${company.lawCode}</td>
				                    <td>${company.address}</td>
				                    <td>${company.contacts}</td>
				                    <td>${company.phoneNO}</td>
				                    <td>
				                    	<c:if test="${company.flag=='1'}">关</c:if>
				                    	<c:if test="${company.flag=='0'}">开</c:if>
				                    </td>				                 
				                    <td>${company.remarks}</td>
				                    <td>
				                            <sec:authorize access="hasRole('ROLE_COMPANY_INFO_INTOEDIT')">
											<a cId="${company.cId}" title="编辑" class="btn-mini btn-edit" href="#"><i class="icon-edit"></i></a>
											</sec:authorize>
											<sec:authorize access="hasRole('ROLE_COMPANY_INFO_DELETE')">
											<a cId="${company.cId}" title="删除" class="btn-mini btn-delete" href="#"><i class="icon-remove"></i></a>
											</sec:authorize>
				                    </td>
				                 </tr>
				             </c:forEach>
				             </tbody>
				         </table>
				         <%@ include file="/WEB-INF/views/common/pagination.jsp"%>
				      </form>
			   </div>
	    </div>
      <div id="msg" style="display: none;">${msg }</div>
<script>
$(function(){
	var msg = $('#msg').html();
	if (msg != "" && msg != null) {
		setTimeout(function() {
			Helper.alert(msg);	
		}, 1000);
	
	}
});
</script>
</body>
</html>
