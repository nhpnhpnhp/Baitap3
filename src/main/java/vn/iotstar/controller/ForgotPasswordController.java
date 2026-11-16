package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;

import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("resetOtpVerified");
        }
        req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        email = email == null ? "" : email.trim();
        req.setAttribute("email", email);

        if (email.isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập email đã đăng ký.");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        String result = userService.sendForgotPasswordOtp(email);
        if ("SENT".equals(result)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("resetEmail", email);
            session.removeAttribute("resetOtpVerified");
            session.setAttribute("resetMessage", "Mã OTP đặt lại mật khẩu (hiệu lực 5 phút) đã được gửi đến email của bạn.");
            resp.sendRedirect(req.getContextPath() + "/reset-password");
        } else if ("NOT_FOUND".equals(result)) {
            req.setAttribute("alert", "Email không tồn tại trong hệ thống.");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
        } else if ("NOT_ACTIVE".equals(result)) {
            req.setAttribute("alert", "Tài khoản chưa được kích hoạt. Vui lòng kích hoạt tài khoản trước.");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
        } else {
            req.setAttribute("alert", "Không thể gửi email OTP. Vui lòng kiểm tra cấu hình SMTP.");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
        }
    }
}
