<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${video != null && video.videoId != null}"/>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>${isEdit ? 'Cập nhật Video' : 'Thêm Video mới'}</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>${isEdit ? 'Cập nhật Video' : 'Thêm Video mới'}</h2>
<c:if test="${alert != null}">
    <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
</c:if>

<form method="post"
      action="${pageContext.request.contextPath}${isEdit ? '/video/update' : '/video/insert'}"
      enctype="multipart/form-data">
    <c:if test="${isEdit}">
        <input type="hidden" name="videoId" value="<c:out value='${video.videoId}'/>">
    </c:if>
    <section>
        <label>Tiêu đề video:</label><br>
        <input type="text" name="title" value="<c:out value='${video.title}'/>" class="form-control" required>
    </section>
    <section>
        <label>Mô tả:</label><br>
        <textarea name="description" rows="4" class="form-control"><c:out value="${video.description}"/></textarea>
    </section>
    <section>
        <label>Danh mục:</label><br>
        <select name="categoryId" class="form-control">
            <option value="">-- Không chọn --</option>
            <c:forEach items="${categories}" var="c">
                <option value="${c.categoryid}" ${video.category != null && video.category.categoryid == c.categoryid ? 'selected' : ''}>
                    <c:out value="${c.categoryname}"/>
                </option>
            </c:forEach>
        </select>
    </section>
    <section>
        <label>Trạng thái:</label><br>
        <label><input type="radio" name="active" value="1" ${video == null || video.active == 1 ? 'checked' : ''}> Hoạt động</label>
        <label><input type="radio" name="active" value="0" ${video != null && video.active == 0 ? 'checked' : ''}> Ẩn</label>
    </section>
    <section>
        <label>Chọn file video (.mp4, .webm, .ogg, .mov):</label><br>
        <c:if test="${isEdit && not empty video.videoPath}">
            <p>File hiện tại: <c:out value="${video.videoPath}"/> (bỏ trống nếu giữ nguyên)</p>
        </c:if>
        <input type="file" name="videoFile" accept="video/mp4,video/webm,video/ogg,video/quicktime" ${isEdit ? '' : 'required'}>
    </section>
    <button type="submit">${isEdit ? 'Cập nhật' : 'Upload & Lưu'}</button>
    <a href="${pageContext.request.contextPath}/video">Hủy</a>
</form>
</body></html>
