package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IVideoDao;
import vn.iotstar.entity.Video;

import java.util.List;

public class VideoDaoImpl implements IVideoDao {

    @Override
    public void insert(Video video) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.persist(video);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void update(Video video) {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            enma.merge(video);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public void delete(String videoId) throws Exception {
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        try {
            trans.begin();
            Video video = enma.find(Video.class, videoId);
            if (video != null) {
                enma.remove(video);
            } else {
                throw new Exception("Không tìm thấy video");
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            enma.close();
        }
    }

    @Override
    public Video findById(String videoId) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            return enma.find(Video.class, videoId);
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findLatest(int limit) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v ORDER BY v.createdDate DESC, v.videoId DESC";
            TypedQuery<Video> query = enma.createQuery(jpql, Video.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT v FROM Video v WHERE v.user.id = :userId ORDER BY v.createdDate DESC, v.videoId DESC";
            TypedQuery<Video> query = enma.createQuery(jpql, Video.class);
            query.setParameter("userId", userId);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            enma.close();
        }
    }

    @Override
    public int countByUserId(int userId) {
        EntityManager enma = JPAConfig.getEntityManager();
        try {
            String jpql = "SELECT count(v) FROM Video v WHERE v.user.id = :userId";
            TypedQuery<Long> query = enma.createQuery(jpql, Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult().intValue();
        } finally {
            enma.close();
        }
    }
}
