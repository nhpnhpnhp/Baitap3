package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.util.Constant;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video-file")
public class VideoStreamController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendError(401);
            return;
        }

        String fileName = req.getParameter("fname");
        if (fileName == null || fileName.isBlank() || fileName.contains("/")
                || fileName.contains("\\") || fileName.contains("..")) {
            resp.sendError(404);
            return;
        }

        File file = new File(Constant.VIDEO_DIR, fileName);
        if (!file.isFile()) {
            resp.sendError(404);
            return;
        }

        String contentType = resolveContentType(file.getName());
        long fileLength = file.length();
        String rangeHeader = req.getHeader("Range");

        resp.setHeader("Accept-Ranges", "bytes");
        resp.setContentType(contentType);

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            long start = 0;
            long end = fileLength - 1;
            String[] parts = rangeHeader.substring(6).split("-", 2);
            try {
                if (!parts[0].isEmpty()) {
                    start = Long.parseLong(parts[0].trim());
                }
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    end = Long.parseLong(parts[1].trim());
                }
            } catch (NumberFormatException e) {
                resp.sendError(416);
                return;
            }
            if (start > end || end >= fileLength) {
                end = fileLength - 1;
            }
            long contentLength = end - start + 1;
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            resp.setHeader("Content-Length", String.valueOf(contentLength));
            streamRange(file, start, contentLength, resp.getOutputStream());
        } else {
            resp.setHeader("Content-Length", String.valueOf(fileLength));
            streamRange(file, 0, fileLength, resp.getOutputStream());
        }
    }

    private void streamRange(File file, long start, long length, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            long remaining = length;
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = raf.read(buffer, 0, toRead);
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                remaining -= read;
            }
        }
    }

    private String resolveContentType(String name) {
        String lower = name.toLowerCase();
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".ogg")) return "video/ogg";
        if (lower.endsWith(".mov")) return "video/quicktime";
        String mime = getServletContext().getMimeType(name);
        return mime == null ? "application/octet-stream" : mime;
    }
}
