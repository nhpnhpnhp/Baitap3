package vn.iotstar.service;

import vn.iotstar.model.User;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    User findByEmail(String email);
    void insert(User user);
    boolean register(String username, String password, String email, String fullname, String phone);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
    boolean sendRegisterOtp(String email);
    String verifyRegisterOtp(String email, String otp);
    String sendForgotPasswordOtp(String email);
    String verifyForgotPasswordOtp(String email, String otp);
    boolean resetPassword(String email, String newPassword);
}
