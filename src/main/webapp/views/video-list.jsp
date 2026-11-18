<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Video của tôi</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>Danh sách Video của tôi</h2>
<p><a href="${pageContext.request.contextPath}/video/add">+ Thêm video mới</a></p>

<c:choose>
    <c:when test="${empty videos}">
        <p>Bạn chưa có video nào.</p>
    </c:when>
    <c:otherwise>
        <table class="data-table">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Danh mục</th>
                    <th>Lượt xem</th>
                    <th>Ngày tạo</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${videos}" var="v" varStatus="st">
                    <tr>
                        <td>${(currentPage - 1) * 6 + st.index + 1}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}">
                                <c:out value="${v.title}"/>
                            </a>
                        </td>
                        <td><c:out value="${v.description}"/></td>
                        <td><c:out value="${v.category != null ? v.category.categoryname : ''}"/></td>
                        <td>${v.views}</td>
                        <td><c:out value="${v.createdDate}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}">Xem</a> |
                            <a href="${pageContext.request.contextPath}/video/edit?id=${v.videoId}">Sửa</a> |
                            <a href="${pageContext.request.contextPath}/video/delete?id=${v.videoId}"
                               onclick="return confirm('Bạn có chắc muốn xóa video này?');">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/video?page=${currentPage - 1}">Trước</a>
                </c:if>
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:choose>
                        <c:when test="${p == currentPage}">
                            <strong>[${p}]</strong>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/video?page=${p}">${p}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/video?page=${currentPage + 1}">Sau</a>
                </c:if>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>
</body></html>
