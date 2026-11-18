package vn.iotstar.service;

import vn.iotstar.entity.Video;

import java.util.List;

public interface IVideoService {
    void insert(Video video);

    void update(Video video);

    void delete(String videoId) throws Exception;

    Video findById(String videoId);

    List<Video> findLatest(int limit);

    List<Video> findByUserId(int userId, int page, int pageSize);

    int countByUserId(int userId);
}
