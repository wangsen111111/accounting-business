<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/jsp/common/head.jsp"%>
<div class="right">
     <div class="location">
         <strong>你现在所在的位置是:</strong>
         <span>排砂采气作业记录页面 >> 信息查看</span>
     </div>
     <div class="providerView">
         <p><strong>作业编号：</strong><span>${bill.billCode }</span></p>
         <p><strong>作业内容：</strong><span>${bill.productName }</span></p>
         <p><strong>作业方式：</strong><span>${bill.productUnit }</span></p>
         <p><strong>采气量：</strong><span>${bill.productCount }</span></p>
         <p><strong>排砂量：</strong><span>${bill.totalPrice }</span></p>
         <p><strong>供应商：</strong><span>${bill.providerName }</span></p>
         <p><strong>作业状态：</strong>
         	<span>
         		<c:if test="${bill.isPayment == 1}">未完成</c:if>
				<c:if test="${bill.isPayment == 2}">已完成</c:if>
			</span>
		</p>
		<div class="providerAddBtn">
         	<input type="button" id="back" name="back" value="返回" >
        </div>
     </div>
 </div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/billview.js"></script>