<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Đăng nhập</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<form action="${pageContext.request.contextPath}/login" method="post">
    <h2>Đăng nhập</h2>
    <c:if test="${message != null}">
        <p class="alert alert-success"><c:out value="${message}"/></p>
    </c:if>
    <c:if test="${alert != null}">
        <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
    </c:if>
    <c:if test="${needVerify}">
        <p><a href="${pageContext.request.contextPath}/verify-otp">Nhập mã OTP kích hoạt tài khoản</a></p>
    </c:if>
    <section>
        <label class="input login-input">
            <div class="input-group">
                <span class="input-group-addon"><i class="fa fa-user"></i></span>
                <input type="text" placeholder="Tài khoản" name="username" class="form-control" required>
            </div>
        </label>
    </section>
    <section>
        <label class="input login-input">
            <div class="input-group">
                <input type="password" placeholder="Mật khẩu" name="password" class="form-control" required>
            </div>
        </label>
    </section>
    <section><label><input type="checkbox" name="remember"> Ghi nhớ đăng nhập</label></section>
    <button type="submit">Đăng nhập</button>
</form>
<p>
    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a> |
    <a href="${pageContext.request.contextPath}/register">Đăng ký tài khoản</a>
</p>
</body></html>
