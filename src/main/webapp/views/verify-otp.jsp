<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Xác nhận OTP</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>Kích hoạt tài khoản bằng OTP</h2>
<c:if test="${message != null}">
    <p class="alert alert-success"><c:out value="${message}"/></p>
</c:if>
<c:if test="${alert != null}">
    <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
</c:if>
<form action="${pageContext.request.contextPath}/verify-otp" method="post">
    <section>
        <label>Email đăng ký:</label><br>
        <input type="email" name="email" value="<c:out value='${email != null ? email : param.email}'/>" placeholder="Email" class="form-control" required>
    </section>
    <section>
        <label>Mã OTP (6 chữ số):</label><br>
        <input type="text" name="otp" maxlength="6" placeholder="Nhập mã OTP" class="form-control">
    </section>
    <button type="submit" name="action" value="verify">Xác nhận kích hoạt</button>
    <button type="submit" name="action" value="resend" formnovalidate>Gửi lại OTP</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a></p>
</body></html>
