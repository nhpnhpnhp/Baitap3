<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Trang chủ</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>Trang chủ</h2>
<p>Xin chào, <c:out value="${sessionScope.account.fullName}"/> (<c:out value="${sessionScope.account.userName}"/>).</p>
<p><a href="${pageContext.request.contextPath}/video">Quản lý video của tôi</a> | <a href="${pageContext.request.contextPath}/video/add">+ Đăng video mới</a></p>

<h3>10 Video mới nhất</h3>
<c:choose>
    <c:when test="${empty videos}">
        <p>Chưa có video nào.</p>
    </c:when>
    <c:otherwise>
        <table class="data-table">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Tiêu đề</th>
                    <th>Người đăng</th>
                    <th>Danh mục</th>
                    <th>Lượt xem</th>
                    <th>Ngày tạo</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${videos}" var="v" varStatus="st">
                    <tr>
                        <td>${st.index + 1}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}">
                                <c:out value="${v.title}"/>
                            </a>
                        </td>
                        <td><c:out value="${v.user != null ? v.user.fullName : 'Hệ thống'}"/></td>
                        <td><c:out value="${v.category != null ? v.category.categoryname : ''}"/></td>
                        <td>${v.views}</td>
                        <td><c:out value="${v.createdDate}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body></html>
