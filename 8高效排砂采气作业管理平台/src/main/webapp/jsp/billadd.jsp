<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/jsp/common/head.jsp"%>

<div class="right">
     <div class="location">
         <strong>你现在所在的位置是:</strong>
         <span>排砂采气作业页面 >> 排砂采气作业添加页面</span>
     </div>
     <div class="providerAdd">
         <form id="billForm" name="billForm" method="post" action="${pageContext.request.contextPath }/jsp/bill.do">
             <!--div的class 为error是验证错误，ok是验证成功-->
             <input type="hidden" name="method" value="add">
             <div class="">
                 <label for="billCode">部门编码：</label>
                 <input type="text" name="billCode" class="text" id="billCode" value=""> 
				 <!-- 放置提示信息 -->
				 <font color="red"></font>
             </div>
             <div>
                 <label for="productName">作业内容：</label>
                 <input type="text" name="productName" id="productName" value=""> 
				 <font color="red"></font>
             </div>
             <div>
                 <label for="productUnit">单位：</label>
                 <input type="text" name="productUnit" id="productUnit" value=""> 
				 <font color="red"></font>
             </div>
             <div>
                 <label for="productCount">数量：</label>
                 <input type="text" name="productCount" id="productCount" value=""> 
				 <font color="red"></font>
             </div>
             <div>
                 <label for="totalPrice">总数量：</label>
                 <input type="text" name="totalPrice" id="totalPrice" value=""> 
				 <font color="red"></font>
             </div>
             <div>
                 <label >作业部门：</label>
                 <select name="providerId" id="providerId">
		         </select>
				 <font color="red"></font>
             </div>
             <div>
                 <label >作业状态：</label>
                 <input type="radio" name="isPayment" value="1" checked="checked">未完成
				 <input type="radio" name="isPayment" value="2" >已完成
             </div>
             <div class="providerAddBtn">
                  <input type="button" name="add" id="add" value="保存">
				  <input type="button" id="back" name="back" value="返回" >
             </div>
         </form>
     </div>
 </div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/billadd.js"></script>