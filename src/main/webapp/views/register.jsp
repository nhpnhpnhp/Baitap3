<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Đăng ký</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<form action="${pageContext.request.contextPath}/register" method="post">
    <h2>Tạo tài khoản mới</h2>
    <c:if test="${alert != null}">
        <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
    </c:if>
    <section><label class="input login-input"><div class="input-group">
        <span class="input-group-addon"><i class="fa fa-user"></i></span>
        <input type="text" placeholder="Tài khoản" name="username" class="form-control" required>
    </div></label></section>
    <section><label class="input login-input"><div class="input-group">
        <input type="password" placeholder="Mật khẩu" name="password" class="form-control" required>
    </div></label></section>
    <section><label class="input login-input"><div class="input-group">
        <input type="email" placeholder="Email" name="email" class="form-control" required>
    </div></label></section>
    <section><label class="input login-input"><div class="input-group">
        <input type="text" placeholder="Họ tên" name="fullname" class="form-control" required>
    </div></label></section>
    <section><label class="input login-input"><div class="input-group">
        <input type="text" placeholder="Số điện thoại" name="phone" class="form-control" required>
    </div></label></section>
    <button type="submit">Đăng ký</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">Đã có tài khoản? Đăng nhập</a></p>
</body></html>
