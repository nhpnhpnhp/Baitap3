package vn.iotstar.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Video;
import vn.iotstar.model.User;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/home", "/admin/home", "/manager/home", "/member/myaccount"})
public class HomeController extends HttpServlet {
    private final IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("account");
        String path = req.getServletPath();
        if (("/admin/home".equals(path) && user.getRoleid() != 1)
                || ("/manager/home".equals(path) && user.getRoleid() != 2)) {
            resp.sendError(403);
            return;
        }
        if ("/home".equals(path) || "/member/myaccount".equals(path)) {
            List<Video> videos = videoService.findLatest(10);
            req.setAttribute("videos", videos);
        }
        String view = "/admin/home".equals(path) ? "/views/admin/home.jsp"
                : "/manager/home".equals(path) ? "/views/manager/home.jsp" : "/views/home.jsp";
        req.getRequestDispatcher(view).forward(req, resp);
    }
}
