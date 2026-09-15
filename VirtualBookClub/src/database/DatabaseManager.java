
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/bookclub.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // SQLite sürücüsünü yüklüyoruz
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC Sürücüsü bulunamadı!");
            }
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void initDatabase() {
        
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Hazır harici SQLite veritabanı şemasına başarıyla bağlanıldı!");
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı başlatma/bağlantı hatası: " + e.getMessage());
        }
    }

    public static boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sisteme yeni bir kullanıcı kaydeder (Varsayılan rol: Standard Reader, Level: 1)
    public static boolean registerUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password, role, level, exp, totalPagesRead, profile_photo) VALUES (?, ?, ?, 'Standard Reader', 1, 0, 0, NULL);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            return true; // Kayıt başarılı
        } catch (Exception e) {
            // Eğer username zaten veritabanında varsa (PRIMARY KEY kısıtlaması) buraya düşer
            System.err.println("Kayıt hatası (Kullanıcı adı alınmış olabilir): " + e.getMessage());
            return false;
        }
    }

    public static void updateUserStats(String username, int totalPagesRead, int level, int exp) {
        String sql = "UPDATE users SET totalPagesRead = ?, level = ?, exp = ? WHERE username = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, totalPagesRead);
            pstmt.setInt(2, level);
            pstmt.setInt(3, exp);
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // YENİ: Profil fotoğrafını veritabanına kaydeder
    public static void updateProfilePhoto(String username, byte[] photoData) {
        String sql = "UPDATE users SET profile_photo = ? WHERE username = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, photoData);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            System.out.println(username + " profil fotoğrafı veritabanına kaydedildi.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBookToDatabase(String username, String title, String author, int pages, String genre, String status) {
        String insertBook = "INSERT OR IGNORE INTO books (title, author, pages, genre) VALUES (?, ?, ?, ?);";
        String insertList = "INSERT OR REPLACE INTO reading_lists (username, book_title, status) VALUES (?, ?, ?);";

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertBook)) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setInt(3, pages);
                pstmt.setString(4, genre);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(insertList)) {
                pstmt.setString(1, username);
                pstmt.setString(2, title);
                pstmt.setString(3, status);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBookProgress(String username, String bookTitle, String status, int pagesRead) {
        String sql = "UPDATE reading_lists SET status = ?, pages_read = ? WHERE username = ? AND book_title = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, pagesRead);
            pstmt.setString(3, username);
            pstmt.setString(4, bookTitle);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getUserBooks(String username, String status) {
        List<String[]> bookList = new ArrayList<>();
        // YENİ: rating ve personal_note sütunları da SELECT sorgusuna eklendi
        String query = "SELECT b.title, b.author, b.pages, b.genre, rl.pages_read, rl.rating, rl.personal_note FROM reading_lists rl "
                     + "JOIN books b ON rl.book_title = b.title WHERE rl.username = ? AND rl.status = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username); pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookList.add(new String[]{
                    rs.getString("title"), 
                    rs.getString("author"), 
                    String.valueOf(rs.getInt("pages")), 
                    rs.getString("genre"),
                    String.valueOf(rs.getInt("pages_read")),
                    String.valueOf(rs.getInt("rating")),    // Index 5: Puan
                    rs.getString("personal_note")           // Index 6: Kişisel Not
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return bookList;
    }

    public static void unlockBadge(String username, String badgeName) {
        String sql = "INSERT OR IGNORE INTO user_badges (username, badge_name) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, badgeName);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getUserBadges(String username) {
        List<String> badges = new ArrayList<>();
        String sql = "SELECT badge_name FROM user_badges WHERE username = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                badges.add(rs.getString("badge_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return badges;
    }

    public static void addForumComment(String author, String content, boolean hasSpoiler) {
        String sql = "INSERT INTO forum_comments (author, content, has_spoiler) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author);
            pstmt.setString(2, content);
            pstmt.setInt(3, hasSpoiler ? 1 : 0);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getForumComments() {
        List<String[]> comments = new ArrayList<>();
        String sql = "SELECT author, content, has_spoiler FROM forum_comments ORDER BY id ASC;";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comments.add(new String[]{
                    rs.getString("author"), rs.getString("content"), String.valueOf(rs.getInt("has_spoiler") == 1)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static void castClubVote(String clubName, String username, String candidateBook) {
        String sql = "INSERT OR REPLACE INTO club_votes (club_name, username, book_candidate) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clubName);
            pstmt.setString(2, username);
            pstmt.setString(3, candidateBook);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addReadingLog(String username, String bookTitle, int pagesRead) {
        String sql = "INSERT INTO reading_logs (username, book_title, pages_read) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, bookTitle);
            pstmt.setInt(3, pagesRead);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.Map<String, Integer> getReadingHistory(String username) {
        java.util.Map<String, Integer> history = new java.util.LinkedHashMap<>();
        String sql = "SELECT book_title, SUM(pages_read) as total FROM reading_logs WHERE username = ? GROUP BY book_title;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.put(rs.getString("book_title"), rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }

    public static void joinClub(String clubName, String username) {
        String sql = "INSERT OR IGNORE INTO club_members (club_name, username) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clubName);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<String> getUserClubs(String username) {
        java.util.List<String> clubs = new java.util.ArrayList<>();
        String sql = "SELECT club_name FROM club_members WHERE username = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clubs.add(rs.getString("club_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clubs;
    }

    public static byte[] iconToBytes(ImageIcon icon) {
        if (icon == null) {
            return null;
        }
        try {
            BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
    public static java.util.Map<String, Integer> getDailyReadingStats(String username) {
        java.util.Map<String, Integer> dailyStats = new java.util.LinkedHashMap<>();
        // date('now', '-6 days') ile bugünü ve önceki 6 günü (toplam 7 gün) kapsarız
        String sql = "SELECT DATE(log_date) as read_date, SUM(pages_read) as total " +
                     "FROM reading_logs WHERE username = ? " +
                     "AND log_date >= date('now', '-6 days') " +
                     "GROUP BY DATE(log_date) ORDER BY read_date ASC;";
                     
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                dailyStats.put(rs.getString("read_date"), rs.getInt("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dailyStats;
    }
    
    public static void saveBookReview(String username, String bookTitle, int rating, String personalNote) {
        String sql = "UPDATE reading_lists SET rating = ?, personal_note = ? WHERE username = ? AND book_title = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rating);
            pstmt.setString(2, personalNote);
            pstmt.setString(3, username);
            pstmt.setString(4, bookTitle);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public static boolean createNewClub(String clubName, String genre, String creatorUsername) {
        String insertClubSQL = "INSERT INTO clubs (name, genre, founder) VALUES (?, ?, ?);";
        String insertMemberSQL = "INSERT INTO club_members (club_name, username) VALUES (?, ?);";
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertClubSQL)) {
                pstmt1.setString(1, clubName); pstmt1.setString(2, genre); pstmt1.setString(3, creatorUsername);
                pstmt1.executeUpdate();
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertMemberSQL)) {
                pstmt2.setString(1, clubName); pstmt2.setString(2, creatorUsername);
                pstmt2.executeUpdate();
            }
            return true;
        } catch (Exception e) { return false; }
    }

    public static List<String[]> getAllClubs() {
        List<String[]> clubs = new ArrayList<>();
        String sql = "SELECT name, genre, founder FROM clubs;";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { clubs.add(new String[]{rs.getString("name"), rs.getString("genre"), rs.getString("founder")}); }
        } catch (Exception e) { e.printStackTrace(); }
        return clubs;
    }

    public static void addClubCandidate(String clubName, String candidateName) {
        String sql = "INSERT OR IGNORE INTO club_candidates (club_name, candidate_name) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clubName); pstmt.setString(2, candidateName);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Kulübün oylama adaylarını getirir
    public static List<String> getClubCandidates(String clubName) {
        List<String> candidates = new ArrayList<>();
        String sql = "SELECT candidate_name FROM club_candidates WHERE club_name = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clubName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { candidates.add(rs.getString("candidate_name")); }
        } catch (Exception e) { e.printStackTrace(); }
        return candidates;
    }

    public static java.util.Map<String, Integer> getClubVotes(String clubName) {
        java.util.Map<String, Integer> votes = new java.util.LinkedHashMap<>();
        String sql = "SELECT book_candidate, COUNT(*) as vote_count FROM club_votes WHERE club_name = ? GROUP BY book_candidate;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clubName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { votes.put(rs.getString("book_candidate"), rs.getInt("vote_count")); }
        } catch (Exception e) { e.printStackTrace(); }
        return votes;
    }
    
  
    
  
    public static List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        String sql = "SELECT username, role FROM users;";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                users.add(new String[]{rs.getString("username"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return users;
    }

   
    public static boolean deleteUser(String username) {
        
        String[] queries = {
            "DELETE FROM users WHERE username = ?;",
            "DELETE FROM reading_lists WHERE username = ?;",
            "DELETE FROM reading_logs WHERE username = ?;",
            "DELETE FROM user_badges WHERE username = ?;",
            "DELETE FROM club_members WHERE username = ?;",
            "DELETE FROM club_votes WHERE username = ?;"
        };
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Transaction (İşlem Bütünlüğü) başlatıyoruz
            
            for (String query : queries) {
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.executeUpdate();
                }
            }
            
            conn.commit(); // Hiçbir hata çıkmadıysa tüm silme işlemlerini kalıcı olarak onayla
            System.out.println(username + " adlı kullanıcı ve tüm verileri sistemden silindi.");
            return true;
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false;
        }
    }
   
   
    public static void sendChallengeInvite(String sender, String receiver, String type, String title, String desc, int target, String deadline) {
        String sql = "INSERT INTO challenge_invites (sender, receiver, type, title, description, target_value, deadline) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender); pstmt.setString(2, receiver); pstmt.setString(3, type);
            pstmt.setString(4, title); pstmt.setString(5, desc); pstmt.setInt(6, target); pstmt.setString(7, deadline);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

   
    public static List<String[]> getPendingInvites(String username) {
        List<String[]> invites = new ArrayList<>();
        String sql = "SELECT id, sender, type, title, description, target_value, deadline FROM challenge_invites WHERE receiver = ? AND status = 'PENDING';";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                invites.add(new String[]{
                    String.valueOf(rs.getInt("id")), rs.getString("sender"), rs.getString("type"),
                    rs.getString("title"), rs.getString("description"),
                    String.valueOf(rs.getInt("target_value")), rs.getString("deadline")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return invites;
    }

    
    public static void respondToInvite(int inviteId, boolean accepted, String receiver, String type, String title, String desc, int target, String deadline) {
        String updateSql = "UPDATE challenge_invites SET status = ? WHERE id = ?;";
        String insertSql = "INSERT INTO user_challenges (username, type, title, description, target_value, deadline) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, accepted ? "ACCEPTED" : "REJECTED");
                pstmt.setInt(2, inviteId);
                pstmt.executeUpdate();
            }
            if (accepted) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, receiver); pstmt.setString(2, type); pstmt.setString(3, title);
                    pstmt.setString(4, desc); pstmt.setInt(5, target); pstmt.setString(6, deadline);
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Giriş yapıldığında kullanıcının kabul ettiği dinamik görevleri getirir
    public static List<String[]> getUserDynamicChallenges(String username) {
        List<String[]> challenges = new ArrayList<>();
        String sql = "SELECT type, title, description, target_value, deadline FROM user_challenges WHERE username = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                challenges.add(new String[]{rs.getString("type"), rs.getString("title"), rs.getString("description"), String.valueOf(rs.getInt("target_value")), rs.getString("deadline")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return challenges;
    }
    
    public static boolean followUser(String follower, String followed) {
        String sql = "INSERT OR IGNORE INTO user_follows (follower, followed) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, follower); pstmt.setString(2, followed);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public static boolean unfollowUser(String follower, String followed) {
        String sql = "DELETE FROM user_follows WHERE follower = ? AND followed = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, follower); pstmt.setString(2, followed);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public static List<String> getFollowing(String username) {
        List<String> following = new ArrayList<>();
        String sql = "SELECT followed FROM user_follows WHERE follower = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { following.add(rs.getString("followed")); }
        } catch (Exception e) { e.printStackTrace(); }
        return following;
    }
    
    public static void addFeedMessage(String username, String message) {
        String sql = "INSERT INTO live_feed (username, message) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static List<String[]> getRecentFeeds() {
        List<String[]> feeds = new ArrayList<>();
        // En son atılan 50 mesajı, tarih formatını yerel saate çevirerek getirir
        String sql = "SELECT username, message, datetime(timestamp, 'localtime') as ts FROM live_feed ORDER BY id DESC LIMIT 50;";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                feeds.add(new String[]{rs.getString("username"), rs.getString("message"), rs.getString("ts")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return feeds;
    }

    
    public static ImageIcon bytesToIcon(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        return new ImageIcon(imageData);
    }
}