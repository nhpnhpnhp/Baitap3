<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết Video</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<c:choose>
    <c:when test="${error != null}">
        <h3 class="alert alert-danger"><c:out value="${error}"/></h3>
    </c:when>
    <c:otherwise>
        <h2><c:out value="${video.title}"/></h2>
        <p>
            <strong>Người đăng:</strong> <c:out value="${video.user != null ? video.user.fullName : 'Hệ thống'}"/> |
            <strong>Danh mục:</strong> <c:out value="${video.category != null ? video.category.categoryname : 'Không có'}"/> |
            <strong>Lượt xem:</strong> ${video.views} |
            <strong>Ngày tạo:</strong> <c:out value="${video.createdDate}"/>
        </p>
        <c:if test="${not empty video.description}">
            <p><strong>Mô tả:</strong> <c:out value="${video.description}"/></p>
        </c:if>

        <div class="video-player">
            <c:choose>
                <c:when test="${not empty video.videoPath}">
                    <video width="640" controls>
                        <source src="${pageContext.request.contextPath}/video-file?fname=${video.videoPath}">
                        Trình duyệt của bạn không hỗ trợ phát video.
                    </video>
                </c:when>
                <c:otherwise>
                    <p>Video này chưa có file phát.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${video.user != null && video.user.id == sessionScope.account.id}">
            <p>
                <a href="${pageContext.request.contextPath}/video/edit?id=${video.videoId}">Sửa video</a> |
                <a href="${pageContext.request.contextPath}/video/delete?id=${video.videoId}"
                   onclick="return confirm('Bạn có chắc muốn xóa video này?');">Xóa video</a>
            </p>
        </c:if>
    </c:otherwise>
</c:choose>
<p><a href="${pageContext.request.contextPath}/home">Về trang chủ</a> | <a href="${pageContext.request.contextPath}/video">Video của tôi</a></p>
</body></html>
