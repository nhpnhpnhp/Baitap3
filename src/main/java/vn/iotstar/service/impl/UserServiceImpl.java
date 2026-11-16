package vn.iotstar.service.impl;

import org.mindrot.jbcrypt.BCrypt;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.EmailUtil;

import java.security.SecureRandom;
import java.sql.Timestamp;

public class UserServiceImpl implements UserService {
    private static final long OTP_VALID_DURATION_MS = 5 * 60 * 1000L;
    private static final String OTP_TYPE_REGISTER = "REGISTER";
    private static final String OTP_TYPE_RESET = "RESET_PASSWORD";

    UserDao userDao = new UserDaoImpl();
    private final SecureRandom random = new SecureRandom();

    @Override
    public User login(String username, String password) {
        User user = this.get(username);
        if (user == null || password == null || user.getPassWord() == null || !user.getPassWord().startsWith("$2")) {
            return null;
        }
        if (BCrypt.checkpw(password, user.getPassWord())) {
            return user;
        }
        return null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username) || userDao.checkExistEmail(email)) {
            return false;
        }
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, username, fullname, hashedPassword, null, 5, phone, date);
        user.setActive(false);
        try {
            userDao.insert(user);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public boolean sendRegisterOtp(String email) {
        User user = userDao.findByEmail(email);
        if (user == null || user.isActive()) {
            return false;
        }
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(new Timestamp(System.currentTimeMillis() + OTP_VALID_DURATION_MS));
        user.setOtpType(OTP_TYPE_REGISTER);
        userDao.update(user);

        String subject = "Mã OTP kích hoạt tài khoản";
        String body = "Xin chào " + user.getFullName() + ",\n\n"
                + "Mã OTP kích hoạt tài khoản của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 5 phút và chỉ sử dụng một lần.";
        return EmailUtil.sendOtpEmail(user.getEmail(), subject, body);
    }

    @Override
    public String verifyRegisterOtp(String email, String otp) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return "NOT_FOUND";
        }
        if (user.isActive()) {
            return "ALREADY_ACTIVE";
        }
        String check = validateOtp(user, otp, OTP_TYPE_REGISTER);
        if (!"OK".equals(check)) {
            return check;
        }
        user.setActive(true);
        clearOtp(user);
        userDao.update(user);
        return "OK";
    }

    @Override
    public String sendForgotPasswordOtp(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return "NOT_FOUND";
        }
        if (!user.isActive()) {
            return "NOT_ACTIVE";
        }
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(new Timestamp(System.currentTimeMillis() + OTP_VALID_DURATION_MS));
        user.setOtpType(OTP_TYPE_RESET);
        userDao.update(user);

        String subject = "Mã OTP đặt lại mật khẩu";
        String body = "Xin chào " + user.getFullName() + ",\n\n"
                + "Mã OTP đặt lại mật khẩu của bạn là: " + otp + "\n"
                + "Mã có hiệu lực trong 5 phút và chỉ sử dụng một lần.";
        boolean sent = EmailUtil.sendOtpEmail(user.getEmail(), subject, body);
        return sent ? "SENT" : "MAIL_ERROR";
    }

    @Override
    public String verifyForgotPasswordOtp(String email, String otp) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return "NOT_FOUND";
        }
        if (!user.isActive()) {
            return "NOT_ACTIVE";
        }
        String check = validateOtp(user, otp, OTP_TYPE_RESET);
        if (!"OK".equals(check)) {
            return check;
        }
        clearOtp(user);
        userDao.update(user);
        return "OK";
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        User user = userDao.findByEmail(email);
        if (user == null || !user.isActive() || newPassword == null || newPassword.isBlank()) {
            return false;
        }
        user.setPassWord(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        clearOtp(user);
        userDao.update(user);
        return true;
    }

    private String generateOtp() {
        int number = random.nextInt(1000000);
        return String.format("%06d", number);
    }

    private String validateOtp(User user, String otp, String expectedType) {
        if (otp == null || otp.trim().isEmpty()) {
            return "INVALID";
        }
        if (user.getOtpCode() == null || !expectedType.equals(user.getOtpType())) {
            return "INVALID";
        }
        if (user.getOtpExpiry() == null || !user.getOtpExpiry().after(new Timestamp(System.currentTimeMillis()))) {
            return "EXPIRED";
        }
        if (!user.getOtpCode().equals(otp.trim())) {
            return "INVALID";
        }
        return "OK";
    }

    private void clearOtp(User user) {
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setOtpType(null);
    }
}
