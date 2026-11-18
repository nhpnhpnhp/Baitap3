package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;
import vn.iotstar.model.User;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = {
        "/video",
        "/video/add",
        "/video/insert",
        "/video/edit",
        "/video/update",
        "/video/delete",
        "/video/detail"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 50,
        maxRequestSize = 1024 * 1024 * 50 * 2
)
public class VideoController extends HttpServlet {
    private static final int PAGE_SIZE = 6;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".mp4", ".webm", ".ogg", ".mov");

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService categoryService = new CategoryServiceImpl();

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) {
            return Constant.DEFAULT_FILENAME;
        }
        for (String content : header.split(";")) {
            if (content.trim().startsWith("filename")) {
                String rawName = content.substring(content.indexOf("=") + 2, content.length() - 1);
                if (rawName.isBlank()) {
                    return "";
                }
                return Paths.get(rawName).getFileName().toString();
            }
        }
        return Constant.DEFAULT_FILENAME;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getAuthenticatedUser(req, resp);
        if (currentUser == null) {
            return;
        }

        String path = req.getServletPath();
        if ("/video".equals(path)) {
            handleList(req, resp, currentUser);
        } else if ("/video/add".equals(path)) {
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
        } else if ("/video/edit".equals(path)) {
            handleEditForm(req, resp, currentUser);
        } else if ("/video/delete".equals(path)) {
            handleDelete(req, resp, currentUser);
        } else if ("/video/detail".equals(path)) {
            handleDetail(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User currentUser = getAuthenticatedUser(req, resp);
        if (currentUser == null) {
            return;
        }

        String path = req.getServletPath();
        if ("/video/insert".equals(path)) {
            handleInsert(req, resp, currentUser);
        } else if ("/video/update".equals(path)) {
            handleUpdate(req, resp, currentUser);
        } else {
            resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isBlank()) {
            try {
                page = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        if (page < 1) {
            page = 1;
        }

        int totalVideos = videoService.countByUserId(currentUser.getId());
        int totalPages = (int) Math.ceil((double) totalVideos / PAGE_SIZE);
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<Video> videos = totalVideos > 0
                ? videoService.findByUserId(currentUser.getId(), page, PAGE_SIZE)
                : List.of();

        req.setAttribute("videos", videos);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalVideos", totalVideos);
        req.getRequestDispatcher("/views/video-list.jsp").forward(req, resp);
    }

    private void handleEditForm(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null || id.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }
        Video video = videoService.findById(id.trim());
        if (video == null) {
            resp.sendError(404);
            return;
        }
        if (!isOwner(video, currentUser)) {
            resp.sendError(403);
            return;
        }
        req.setAttribute("video", video);
        req.setAttribute("categories", categoryService.findAll());
        req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
    }

    private void handleInsert(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        int active = parseIntOrDefault(req.getParameter("active"), 1);
        Category category = resolveCategory(req.getParameter("categoryId"));

        Video draft = new Video();
        draft.setTitle(title);
        draft.setDescription(description);
        draft.setActive(active);
        draft.setCategory(category);

        if (title.isEmpty()) {
            req.setAttribute("alert", "Vui lòng nhập tiêu đề video.");
            req.setAttribute("video", draft);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
            return;
        }

        String savedFileName = saveUploadedVideo(req, true);
        if (savedFileName == null) {
            req.setAttribute("alert", "Vui lòng chọn file video hợp lệ (.mp4, .webm, .ogg, .mov).");
            req.setAttribute("video", draft);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
            return;
        }

        draft.setVideoId("v" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6));
        draft.setViews(0);
        draft.setVideoPath(savedFileName);
        draft.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        draft.setUser(currentUser);

        videoService.insert(draft);
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, User currentUser)
            throws ServletException, IOException {
        String videoId = trim(req.getParameter("videoId"));
        Video video = videoId.isEmpty() ? null : videoService.findById(videoId);
        if (video == null) {
            resp.sendError(404);
            return;
        }
        if (!isOwner(video, currentUser)) {
            resp.sendError(403);
            return;
        }

        String title = trim(req.getParameter("title"));
        String description = trim(req.getParameter("description"));
        int active = parseIntOrDefault(req.getParameter("active"), 1);
        Category category = resolveCategory(req.getParameter("categoryId"));

        if (title.isEmpty()) {
            req.setAttribute("alert", "Tiêu đề video không được để trống.");
            req.setAttribute("video", video);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
            return;
        }

        Part videoPart = req.getPart("videoFile");
        if (videoPart != null && videoPart.getSize() > 0) {
            String newFileName = saveUploadedVideo(req, false);
            if (newFileName == null) {
                req.setAttribute("alert", "Định dạng file video không hợp lệ (.mp4, .webm, .ogg, .mov).");
                req.setAttribute("video", video);
                req.setAttribute("categories", categoryService.findAll());
                req.getRequestDispatcher("/views/video-form.jsp").forward(req, resp);
                return;
            }
            deleteVideoFile(video.getVideoPath());
            video.setVideoPath(newFileName);
        }

        video.setTitle(title);
        video.setDescription(description);
        video.setActive(active);
        video.setCategory(category);

        videoService.update(video);
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws IOException {
        String id = req.getParameter("id");
        if (id != null && !id.isBlank()) {
            Video video = videoService.findById(id.trim());
            if (video != null) {
                if (!isOwner(video, currentUser)) {
                    resp.sendError(403);
                    return;
                }
                deleteVideoFile(video.getVideoPath());
                try {
                    videoService.delete(video.getVideoId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        resp.sendRedirect(req.getContextPath() + "/video");
    }

    private void handleDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null || id.isBlank()) {
            req.setAttribute("error", "ID video không hợp lệ.");
            req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
            return;
        }
        Video video = videoService.findById(id.trim());
        if (video == null) {
            req.setAttribute("error", "Video không tồn tại hoặc đã bị xóa.");
            req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
            return;
        }
        video.setViews(video.getViews() + 1);
        videoService.update(video);
        req.setAttribute("video", video);
        req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
    }

    private String saveUploadedVideo(HttpServletRequest req, boolean required) throws IOException, ServletException {
        String uploadPath = Constant.VIDEO_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            for (Part part : req.getParts()) {
                if (!"videoFile".equals(part.getName()) || part.getSize() == 0) {
                    continue;
                }
                String fileName = getFileName(part);
                if (fileName.isEmpty() || Constant.DEFAULT_FILENAME.equals(fileName)) {
                    return null;
                }
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex < 0) {
                    return null;
                }
                String ext = fileName.substring(dotIndex).toLowerCase();
                if (!ALLOWED_EXTENSIONS.contains(ext)) {
                    return null;
                }
                String safeBase = fileName.substring(0, dotIndex).replaceAll("[^a-zA-Z0-9._-]", "_");
                String savedFileName = System.currentTimeMillis() + "_" + safeBase + ext;
                part.write(uploadPath + File.separator + savedFileName);
                return savedFileName;
            }
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
            return null;
        }
        return required ? null : "";
    }

    private void deleteVideoFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            Path path = Paths.get(Constant.VIDEO_DIR, fileName);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User getAuthenticatedUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (User) session.getAttribute("account");
    }

    private boolean isOwner(Video video, User user) {
        return video.getUser() != null && video.getUser().getId() == user.getId();
    }

    private Category resolveCategory(String categoryIdParam) {
        if (categoryIdParam == null || categoryIdParam.isBlank()) {
            return null;
        }
        try {
            int categoryId = Integer.parseInt(categoryIdParam.trim());
            return categoryId > 0 ? categoryService.findById(categoryId) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseIntOrDefault(String value, int defaultVal) {
        try {
            return value == null ? defaultVal : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
