<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
    <c:when test="${sessionScope.account == null}">
        <div class="col-sm-6">
            <ul class="list-inline right-topbar pull-right">
                <li><a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    | <a href="${pageContext.request.contextPath}/register">Đăng ký</a></li>
            </ul>
        </div>
    </c:when>
    <c:otherwise>
        <div class="col-sm-6">
            <ul class="list-inline right-topbar pull-right">
                <li>
                    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
                    | <a href="${pageContext.request.contextPath}/video">Video của tôi</a>
                    <c:if test="${sessionScope.account.roleid == 1}">
                        | <a href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục</a>
                    </c:if>
                    | <a href="${pageContext.request.contextPath}/member/myaccount"><c:out value="${sessionScope.account.fullName}"/></a>
                    | <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
                </li>
            </ul>
        </div>
    </c:otherwise>
</c:choose>
