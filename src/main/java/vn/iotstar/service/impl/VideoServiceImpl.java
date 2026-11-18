package vn.iotstar.service.impl;

import vn.iotstar.dao.IVideoDao;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;

import java.util.List;

public class VideoServiceImpl implements IVideoService {
    private final IVideoDao videoDao = new VideoDaoImpl();

    @Override
    public void insert(Video video) {
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) {
        videoDao.update(video);
    }

    @Override
    public void delete(String videoId) throws Exception {
        videoDao.delete(videoId);
    }

    @Override
    public Video findById(String videoId) {
        return videoDao.findById(videoId);
    }

    @Override
    public List<Video> findLatest(int limit) {
        return videoDao.findLatest(limit);
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        return videoDao.findByUserId(userId, page, pageSize);
    }

    @Override
    public int countByUserId(int userId) {
        return videoDao.countByUserId(userId);
    }
}
