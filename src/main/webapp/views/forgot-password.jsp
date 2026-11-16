<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Quên mật khẩu</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>Quên mật khẩu</h2>
<c:if test="${alert != null}">
    <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
</c:if>
<form action="${pageContext.request.contextPath}/forgot-password" method="post">
    <section>
        <label>Nhập email đã đăng ký:</label><br>
        <input type="email" name="email" value="<c:out value='${email}'/>" placeholder="Email" class="form-control" required>
    </section>
    <button type="submit">Gửi mã OTP</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
</body></html>
