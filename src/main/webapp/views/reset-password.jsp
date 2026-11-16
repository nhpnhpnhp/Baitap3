<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Đặt lại mật khẩu</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<%@ include file="/views/topbar.jsp" %>
<h2>Đặt lại mật khẩu</h2>
<p>Email: <strong><c:out value="${sessionScope.resetEmail}"/></strong></p>
<c:if test="${message != null}">
    <p class="alert alert-success"><c:out value="${message}"/></p>
</c:if>
<c:if test="${alert != null}">
    <h3 class="alert alert-danger"><c:out value="${alert}"/></h3>
</c:if>

<c:choose>
    <c:when test="${sessionScope.resetOtpVerified == true}">
        <form action="${pageContext.request.contextPath}/reset-password" method="post">
            <input type="hidden" name="action" value="resetPassword">
            <section>
                <label>Mật khẩu mới:</label><br>
                <input type="password" name="newPassword" placeholder="Mật khẩu mới" class="form-control" required>
            </section>
            <section>
                <label>Xác nhận mật khẩu mới:</label><br>
                <input type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu mới" class="form-control" required>
            </section>
            <button type="submit">Đổi mật khẩu</button>
        </form>
    </c:when>
    <c:otherwise>
        <form action="${pageContext.request.contextPath}/reset-password" method="post">
            <section>
                <label>Mã OTP xác nhận (6 chữ số):</label><br>
                <input type="text" name="otp" maxlength="6" placeholder="Nhập mã OTP" class="form-control">
            </section>
            <button type="submit" name="action" value="verifyOtp">Xác nhận OTP</button>
            <button type="submit" name="action" value="resend" formnovalidate>Gửi lại OTP</button>
        </form>
    </c:otherwise>
</c:choose>
<p><a href="${pageContext.request.contextPath}/forgot-password">Dùng email khác</a> | <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
</body></html>
