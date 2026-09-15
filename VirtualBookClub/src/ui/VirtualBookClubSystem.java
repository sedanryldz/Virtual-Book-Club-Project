package ui;

import database.DatabaseManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
//import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VirtualBookClubSystem extends JFrame {

    private static Map<String, Reader> userDatabase = new HashMap<>();
    private static List<BookClub> globalClubs = new ArrayList<>();
    private static final Map<String, String> ALL_BADGES = new LinkedHashMap<>();

    static {
        ALL_BADGES.put("First Book Finished", "Complete your first book to unlock this badge.");
        ALL_BADGES.put("Novice Reader", "Read 100 pages to complete your first challenge.");
        ALL_BADGES.put("Bookworm", "Reach 500 pages read.");
        ALL_BADGES.put("Sci-Fi Explorer", "Finish 3 Science Fiction books.");
        ALL_BADGES.put("Level 5 Scholar", "Reach Level 5 by earning EXP.");
        ALL_BADGES.put("Dune Master", "Read all books in the Dune trilogy.");
        ALL_BADGES.put("Weekend Sprint", "Read 300 pages before the strict deadline.");
    }

    private Reader activeUser;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    private JLabel profileInfoLabel;
    private JLabel profileImageLabel;
    private JPanel challengesListPanel;
    private JPanel badgesContainerPanel;
    private JPanel clubVotesPanel;
    private JPanel discussionPanel;
    private JPanel activityFeedPanel;

    private JList<Book> readingListUI;
    private JList<Book> wantToReadListUI;
    private JList<Book> completedListUI;
    private DefaultListModel<Book> readingModel = new DefaultListModel<>();
    private DefaultListModel<Book> wantModel = new DefaultListModel<>();
    private DefaultListModel<Book> completedModel = new DefaultListModel<>();
    private DefaultComboBoxModel<Book> readingComboModel = new DefaultComboBoxModel<>();
    private JPanel analyticsChartPanel;
    private DefaultListModel<String> allUsersModel = new DefaultListModel<>();

    private final Color COLOR_BG = new Color(243, 244, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(99, 102, 241);
    private final Color COLOR_TEXT = new Color(31, 41, 55);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    static class FlatIcon implements Icon {

        enum Type {
            USER, BOOK, TROPHY, STAR, CHART, ROCKET, BOOKMARK, CHECK, LOCK_OPEN, LOCK_CLOSED, USERS, CHAT, INFO, GEAR
        }
        private Type type;
        private Color color;
        private int size;

        public FlatIcon(Type type, Color color, int size) {
            this.type = type;
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int s = size;
            if (type == Type.USER) {
                g2.fillOval(x + s / 4, y + s / 8, s / 2, s / 2);
                g2.fillArc(x + s / 8, y + s / 2, s * 3 / 4, s * 3 / 4, 0, 180);
            } else if (type == Type.USERS) {
                g2.fillOval(x + s / 8, y + s / 8, s / 3, s / 3);
                g2.fillArc(x, y + s / 2, s * 7 / 12, s / 2, 0, 180);
                g2.fillOval(x + s / 2, y + s / 8, s / 3, s / 3);
                g2.fillArc(x + s * 5 / 12, y + s / 2, s * 7 / 12, s / 2, 0, 180);
            } else if (type == Type.CHAT) {
                g2.fillRoundRect(x + s / 8, y + s / 8, s * 3 / 4, s * 5 / 8, 5, 5);
                int[] px = {x + s / 4, x + s / 4, x + s * 3 / 8};
                int[] py = {y + s * 3 / 4, y + s * 7 / 8, y + s * 3 / 4};
                g2.fillPolygon(px, py, 3);
            } else if (type == Type.BOOK) {
                g2.fillRoundRect(x + s / 8, y + s / 6, s / 3, s * 2 / 3, 2, 2);
                g2.fillRoundRect(x + s / 2, y + s / 6, s / 3, s * 2 / 3, 2, 2);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(x + s / 2, y + s / 8, x + s / 2, y + s * 5 / 6);
            } else if (type == Type.TROPHY) {
                g2.fillRect(x + s / 4, y + s / 8, s / 2, s / 2);
                g2.fillRect(x + s * 3 / 8, y + s * 5 / 8, s / 4, s / 4);
                g2.fillRect(x + s / 4, y + s * 7 / 8, s / 2, s / 8);
                g2.drawArc(x + s / 8, y + s / 8, s / 4, s / 3, 90, 180);
                g2.drawArc(x + s * 5 / 8, y + s / 8, s / 4, s / 3, -90, 180);
            } else if (type == Type.STAR) {
                int[] xPoints = {x + s / 2, x + s * 5 / 8, x + s * 9 / 10, x + s * 11 / 16, x + s * 13 / 16, x + s / 2, x + s * 3 / 16, x + s / 10, x + s * 5 / 16, x + s * 3 / 8};
                int[] yPoints = {y + s / 10, y + s * 3 / 8, y + s * 3 / 8, y + s * 9 / 16, y + s * 9 / 10, y + s * 3 / 4, y + s * 9 / 10, y + s * 9 / 16, y + s * 3 / 8, y + s * 3 / 8};
                g2.fillPolygon(xPoints, yPoints, 10);
            } else if (type == Type.CHART) {
                g2.fillArc(x + s / 8, y + s / 8, s * 3 / 4, s * 3 / 4, 0, 270);
                g2.setColor(new Color(245, 158, 11));
                g2.fillArc(x + s / 8 + 2, y + s / 8 - 2, s * 3 / 4, s * 3 / 4, 270, 90);
            } else if (type == Type.ROCKET) {
                int[] px = {x + s / 2, x + s * 3 / 4, x + s / 4};
                int[] py = {y + s / 8, y + s * 3 / 4, y + s * 3 / 4};
                g2.fillPolygon(px, py, 3);
                g2.fillRect(x + s * 3 / 8, y + s * 3 / 4, s / 4, s / 8);
            } else if (type == Type.BOOKMARK) {
                int[] px = {x + s / 3, x + s * 2 / 3, x + s * 2 / 3, x + s / 2, x + s / 3};
                int[] py = {y + s / 8, y + s / 8, y + s * 7 / 8, y + s * 5 / 8, y + s * 7 / 8};
                g2.fillPolygon(px, py, 5);
            } else if (type == Type.CHECK) {
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + s / 4, y + s / 2, x + s * 2 / 5, y + s * 3 / 4);
                g2.drawLine(x + s * 2 / 5, y + s * 3 / 4, x + s * 3 / 4, y + s / 4);
            } else if (type == Type.LOCK_CLOSED) {
                g2.fillRoundRect(x + s / 4, y + s * 3 / 8, s / 2, s / 2, 4, 4);
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(x + s / 3, y + s / 8, s / 3, s / 3, 0, 180);
            } else if (type == Type.LOCK_OPEN) {
                g2.fillRoundRect(x + s / 4, y + s * 3 / 8, s / 2, s / 2, 4, 4);
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(x + s / 3, y + s / 16, s / 3, s / 3, 90, 180);
            } else if (type == Type.INFO) {
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x + s / 8, y + s / 8, s * 3 / 4, s * 3 / 4);
                g2.fillOval(x + s / 2 - 2, y + s / 4 + 2, 4, 4);
                g2.fillRect(x + s / 2 - 2, y + s / 2 - 2, 4, s * 5 / 16);
            } else if (type == Type.GEAR) {
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(x + s / 4, y + s / 4, s / 2, s / 2);
                g2.drawLine(x + s / 2, y, x + s / 2, y + s / 4);
                g2.drawLine(x + s / 2, y + s * 3 / 4, x + s / 2, y + s);
                g2.drawLine(x, y + s / 2, x + s / 4, y + s / 2);
                g2.drawLine(x + s * 3 / 4, y + s / 2, x + s, y + s / 2);
            }
            g2.dispose();
        }
    }

    public VirtualBookClubSystem() {
        setTitle("Virtual Book Club & Social Challenge");
        setSize(1100, 750);
        // İkon dosyasını projeye yükleme ve pencereye uygulama
        try {
            // .ico veya .png dosyanızın adı neyse buraya tam adını yazın
            java.net.URL iconURL = getClass().getResource("/icon.png");
            if (iconURL != null) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                System.err.println("İkon dosyası kaynak klasöründe (resources) bulunamadı!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        mainContainer.add(createLoginPanel(), "Login");
        add(mainContainer);
        cardLayout.show(mainContainer, "Login");
    }

    private void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        if (isPrimary) {
            btn.setBackground(COLOR_PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(229, 231, 235));
            btn.setForeground(COLOR_TEXT);
        }
    }

    private void styleTextField(JComponent field) {
        field.setFont(FONT_NORMAL);
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(209, 213, 219), 1, true), new EmptyBorder(8, 10, 8, 10)));
    }

    private JPanel createCardPanel(String titleStr) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(229, 231, 235), 1, true), new EmptyBorder(20, 20, 20, 20)));
        JLabel title = new JLabel(titleStr);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(COLOR_PRIMARY);
        panel.add(title, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(COLOR_BG);
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(229, 231, 235), 1, true), new EmptyBorder(40, 40, 40, 40)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome", new FlatIcon(FlatIcon.Type.BOOK, COLOR_PRIMARY, 36), SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT);
        title.setIconTextGap(15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        card.add(new JLabel("Username:"), gbc);
        JTextField userField = new JTextField(15);
        styleTextField(userField);
        gbc.gridx = 1;
        card.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(new JLabel("Password:"), gbc);
        JPasswordField passField = new JPasswordField(15);
        styleTextField(passField);
        gbc.gridx = 1;
        card.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10);
        card.add(loginBtn, gbc);

        // --- YENİ EKLENEN: KAYIT OL (REGISTER) BUTONU VE İŞLEVİ ---
        JButton registerBtn = new JButton("Create New Account");
        styleButton(registerBtn, false); // İkincil buton stili
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 10, 10);
        card.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            // Kayıt için bir açılır pencere (Dialog) tasarlıyoruz
            JTextField regUserField = new JTextField(15);
            styleTextField(regUserField);
            JTextField regEmailField = new JTextField(15);
            styleTextField(regEmailField);
            JPasswordField regPassField = new JPasswordField(15);
            styleTextField(regPassField);
            JPasswordField regConfirmPassField = new JPasswordField(15);
            styleTextField(regConfirmPassField);

            JPanel regPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            regPanel.add(new JLabel("Username:"));
            regPanel.add(regUserField);
            regPanel.add(new JLabel("Email:"));
            regPanel.add(regEmailField);
            regPanel.add(new JLabel("Password:"));
            regPanel.add(regPassField);
            regPanel.add(new JLabel("Confirm Password:"));
            regPanel.add(regConfirmPassField);

            int result = JOptionPane.showConfirmDialog(this, regPanel, "Register New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newUser = regUserField.getText().trim();
                String newEmail = regEmailField.getText().trim();
                String newPass = new String(regPassField.getPassword());
                String confirmPass = new String(regConfirmPassField.getPassword());

                // Girdi kontrolleri
                if (newUser.isEmpty() || newEmail.isEmpty() || newPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Veritabanına kayıt işlemi
                boolean success = DatabaseManager.registerUser(newUser, newEmail, newPass);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    userField.setText(newUser); // Kolaylık olsun diye username'i login ekranına yaz
                    passField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed! Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            if (DatabaseManager.validateLogin(user, pass)) {
                try (java.sql.Connection conn = DatabaseManager.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

                    pstmt.setString(1, user);
                    java.sql.ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        String role = rs.getString("role");
                        String email = rs.getString("email");
                        int level = rs.getInt("level");
                        int exp = rs.getInt("exp");
                        int totalPagesRead = rs.getInt("totalPagesRead");

                        // YENİ: Veritabanından BLOB fotoğraf verisini çekiyoruz
                        byte[] photoBytes = rs.getBytes("profile_photo");

                        if (role.contains("Admin")) {
                            activeUser = new Administrator(user, email, pass);
                        } else {
                            activeUser = new Reader(user, email, pass);

                            ((Reader) activeUser).level = level;
                            ((Reader) activeUser).exp = exp;
                            ((Reader) activeUser).totalPagesRead = totalPagesRead;
                            ((Reader) activeUser).expToNextLevel = (int) (100 * java.lang.Math.pow(1.5, level - 1));

                            List<String> earnedBadges = DatabaseManager.getUserBadges(user);
                            ((Reader) activeUser).getBadges().addAll(earnedBadges);
                        }

                        // YENİ: Varsa profil fotoğrafını atıyoruz
                        if (photoBytes != null && activeUser instanceof Reader) {
                            ImageIcon profilePic = DatabaseManager.bytesToIcon(photoBytes);
                            activeUser.setProfileImage(profilePic);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (activeUser instanceof Reader) {
                    Reader readerUser = (Reader) activeUser;

                    List<String[]> readingBooks = DatabaseManager.getUserBooks(user, "Currently Reading");
                    for (String[] bData : readingBooks) {
                        Book b = new FictionBook(bData[0], bData[1], bData[3], Integer.parseInt(bData[2]));
                        b.addPagesRead(Integer.parseInt(bData[4]));
                        readerUser.startReading(b);
                    }

                    List<String[]> wantBooks = DatabaseManager.getUserBooks(user, "Want to Read");
                    for (String[] wData : wantBooks) {
                        Book b = new FictionBook(wData[0], wData[1], wData[3], Integer.parseInt(wData[2]));
                        readerUser.addToWantToRead(b);
                    }

                    List<String[]> completedBooks = DatabaseManager.getUserBooks(user, "Completed");
                    for (String[] cData : completedBooks) {
                        Book b = new FictionBook(cData[0], cData[1], cData[3], Integer.parseInt(cData[2]));
                        b.addPagesRead(b.getTotalPages());
                        // YENİ: Veritabanından gelen puanı ve notu kitaba aktar
                        b.rate(Integer.parseInt(cData[5]));
                        b.setPersonalNote(cData[6] != null ? cData[6] : "");
                        readerUser.getCompletedBooks().add(b);
                    }

                    readerUser.getPageHistory().putAll(DatabaseManager.getReadingHistory(user));
                    readerUser.getFollowing().addAll(DatabaseManager.getFollowing(user));

                    List<String> userClubNames = DatabaseManager.getUserClubs(user);
                    for (BookClub club : globalClubs) {
                        if (userClubNames.contains(club.getName())) {
                            club.getMembers().add(readerUser);
                            readerUser.getMyClubs().add(club);
                        }
                    }
                    // YENİ: Veritabanından kabul edilmiş dinamik/arkadaş görevlerini yükle
                    List<String[]> dynamicChals = DatabaseManager.getUserDynamicChallenges(user);
                    for (String[] chalData : dynamicChals) {
                        String cType = chalData[0];
                        String cTitle = chalData[1];
                        String cDesc = chalData[2];
                        int cTarget = Integer.parseInt(chalData[3]);
                        String cDeadline = chalData[4];

                        if (cType.equals("PageCount")) {
                            readerUser.getActiveChallenges().add(new PageCountChallenge(cTitle, cDesc, cTarget));
                        } else if (cType.equals("TimeSprint")) {
                            readerUser.getActiveChallenges().add(new TimeSprintChallenge(cTitle, cDesc, cTarget, java.time.LocalDate.parse(cDeadline)));
                        }
                    }

                    for (Challenge c : readerUser.getActiveChallenges()) {
                        c.checkProgress(readerUser);
                    }
                }

                mainContainer.add(createMainDashboard(), "Dashboard");
                cardLayout.show(mainContainer, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı Kullanıcı Adı veya Şifre!", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            }
        });

        wrapperPanel.add(card);
        return wrapperPanel;
    }

    private JTabbedPane createMainDashboard() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(COLOR_BG);
        tabs.setForeground(COLOR_TEXT);

        tabs.addTab("Profile", new FlatIcon(FlatIcon.Type.USER, COLOR_PRIMARY, 16), createProfilePanel());
        tabs.addTab("Library", new FlatIcon(FlatIcon.Type.BOOK, COLOR_PRIMARY, 16), createLibraryPanel());
        tabs.addTab("Analytics", new FlatIcon(FlatIcon.Type.CHART, new Color(5, 150, 105), 16), createAnalyticsPanel());
        tabs.addTab("Community", new FlatIcon(FlatIcon.Type.USERS, new Color(236, 72, 153), 16), createSocialPanel());
        tabs.addTab("Challenges", new FlatIcon(FlatIcon.Type.TROPHY, COLOR_PRIMARY, 16), createChallengesPanel());
        tabs.addTab("Badges", new FlatIcon(FlatIcon.Type.STAR, new Color(245, 158, 11), 16), createBadgesPanel());
        tabs.addTab("Guide", new FlatIcon(FlatIcon.Type.INFO, new Color(14, 165, 233), 16), createGuidePanel());

        if (activeUser instanceof Administrator) {
            tabs.addTab("Admin Panel", new FlatIcon(FlatIcon.Type.GEAR, new Color(239, 68, 68), 16), createAdminPanel());
        }

        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));
        return tabs;
    }

    private JPanel createAdminPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel card = createCardPanel("System Administrator Panel");

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(COLOR_CARD);
        contentPanel.add(new JLabel("All Registered Users:"), BorderLayout.NORTH);

        // YENİ: Listeyi RAM'den değil, canlı olarak Veritabanından çekiyoruz!
        allUsersModel.clear();
        List<String[]> dbUsers = DatabaseManager.getAllUsers();
        for (String[] u : dbUsers) {
            allUsersModel.addElement(u[0] + " - Role: " + u[1]);
        }

        JList<String> usersList = new JList<>(allUsersModel);
        usersList.setFont(FONT_NORMAL);
        contentPanel.add(new JScrollPane(usersList), BorderLayout.CENTER);

        JButton deleteUserBtn = new JButton("Delete Selected User");
        styleButton(deleteUserBtn, false);
        deleteUserBtn.setForeground(new Color(239, 68, 68));

        deleteUserBtn.addActionListener(e -> {
            String selected = usersList.getSelectedValue();
            if (selected != null) {
                String uName = selected.split(" -")[0]; // "username - Role: XYZ" stringinden username'i ayır

                if (uName.equals(activeUser.getUsername())) {
                    JOptionPane.showMessageDialog(this, "You cannot delete yourself!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to COMPLETELY delete user: " + uName + "?\nThis will erase all their reading history, badges, and club data.",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // YENİ: Kullanıcıyı Veritabanından Kalıcı Olarak Sil
                        boolean success = DatabaseManager.deleteUser(uName);

                        if (success) {
                            allUsersModel.removeElement(selected); // Arayüzden kaldır
                            JOptionPane.showMessageDialog(this, "User successfully deleted from the database.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete user due to a database error.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        contentPanel.add(deleteUserBtn, BorderLayout.SOUTH);
        card.add(contentPanel, BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createGuidePanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel card = createCardPanel("Adventurer's Handbook");

        String guideHtml = "<html><body style='font-family: Segoe UI; padding: 15px; width: 650px;'>"
                + "<h2 style='color: #4F46E5;'>Welcome, Bookworm!</h2>"
                + "<p>This platform is designed for you to track your reading habits, challenge your friends, and be part of a vibrant community.</p>"
                + "<hr style='border: 1px solid #E5E7EB;'>"
                + "<h3 style='color: #059669;'>🚀 Social Features & Following</h3>"
                + "<ul><li><b>Profile:</b> Go to your Profile section and click 'Find Users' to start following your friends.</li>"
                + "<li><b>Live Feed:</b> Share your thoughts, reading updates, or book recommendations in the 'Community' tab.</li></ul>"
                + "<h3 style='color: #EC4899;'>⚔️ Challenges & Invites</h3>"
                + "<ul><li><b>Sending Challenges:</b> Challenge your friends to reading goals via the 'Challenges' tab.</li>"
                + "<li><b>Inbox:</b> Check your 'Invitations & Inbox' to accept or reject new challenges.</li>"
                + "<li><b>Badges:</b> Complete specific challenges to earn exclusive badges for your profile!</li></ul>"
                + "<h3 style='color: #D97706;'>📚 Book Clubs & Voting</h3>"
                + "<ul><li>Create your own club or join existing ones to read together.</li>"
                + "<li>Club founders and Admins can add new book candidates to the voting list.</li></ul>"
                + "<h3 style='color: #8B5CF6;'>🛠️ Administration</h3>"
                + "<p>If you have Administrator privileges, you can manage the system and remove users via the 'Admin Panel'.</p>"
                + "</body></html>";

        JLabel guideLabel = new JLabel(guideHtml);
        guideLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(new JScrollPane(guideLabel), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel createSocialPanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        JTabbedPane socialTabs = new JTabbedPane();
        socialTabs.setFont(FONT_NORMAL);

        JPanel feedPanel = createCardPanel("Live Activity Feed");
        JPanel feedList = new JPanel();
        feedList.setLayout(new BoxLayout(feedList, BoxLayout.Y_AXIS));
        feedList.setBackground(COLOR_CARD);
        // --- GERÇEK CANLI AKIŞ (LIVE FEED) PANELİ BAŞLANGICI ---
        JPanel feedCard = createCardPanel("Live Activity Feed");
        JPanel feedContainer = new JPanel(new BorderLayout(0, 10));
        feedContainer.setBackground(COLOR_CARD);

        activityFeedPanel = new JPanel();
        activityFeedPanel.setLayout(new BoxLayout(activityFeedPanel, BoxLayout.Y_AXIS));
        activityFeedPanel.setBackground(COLOR_CARD);

        refreshActivityFeed(); // Veritabanından mesajları çeker

        feedContainer.add(new JScrollPane(activityFeedPanel), BorderLayout.CENTER);

        // Mesaj Gönderme Kutusu
        JPanel feedInputPanel = new JPanel(new BorderLayout(10, 10));
        feedInputPanel.setBackground(COLOR_CARD);
        JTextField feedField = new JTextField();
        styleTextField(feedField);
        JButton postFeedBtn = new JButton("Share Update");
        styleButton(postFeedBtn, true);

        postFeedBtn.addActionListener(e -> {
            String text = feedField.getText().trim();
            if (!text.isEmpty()) {
                DatabaseManager.addFeedMessage(activeUser.getUsername(), text);
                feedField.setText("");
                refreshActivityFeed(); // Gönderdikten sonra paneli yenile
            }
        });

        feedInputPanel.add(feedField, BorderLayout.CENTER);
        feedInputPanel.add(postFeedBtn, BorderLayout.EAST);
        feedContainer.add(feedInputPanel, BorderLayout.SOUTH);

        feedCard.add(feedContainer, BorderLayout.CENTER);
        socialTabs.addTab("Activity Feed", new FlatIcon(FlatIcon.Type.CHART, COLOR_PRIMARY, 16), feedCard);

        // CLUBS PANEL
        JPanel clubsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        clubsPanel.setBackground(COLOR_BG);
        JPanel leftClub = createCardPanel("Discover & Join");
        DefaultListModel<String> clubListModel = new DefaultListModel<>();
        for (BookClub bc : globalClubs) {
            clubListModel.addElement(bc.getName() + " (" + bc.getGenre() + ")");
        }
        JList<String> uiClubList = new JList<>(clubListModel);
        uiClubList.setFont(FONT_NORMAL);
        leftClub.add(new JScrollPane(uiClubList), BorderLayout.CENTER);
        // --- KULÜPLER VE BUTONLAR KISMI BAŞLANGICI ---
        JButton joinBtn = new JButton("Join Selected Club");
        styleButton(joinBtn, false);

        // YENİ: Kulüp Kurma Butonu
        JButton createClubBtn = new JButton("Create New Club");
        styleButton(createClubBtn, true); // Mavi ve dikkat çekici renk

        // İki butonu alt alta koymak için yeni bir panel
        JPanel clubButtonsPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        clubButtonsPanel.setBackground(COLOR_CARD);
        clubButtonsPanel.add(joinBtn);
        clubButtonsPanel.add(createClubBtn);

        leftClub.add(clubButtonsPanel, BorderLayout.SOUTH);

        JPanel rightClub = createCardPanel("Club Details & Voting");
        clubVotesPanel = new JPanel();
        clubVotesPanel.setLayout(new BoxLayout(clubVotesPanel, BoxLayout.Y_AXIS));
        clubVotesPanel.setBackground(COLOR_CARD);
        rightClub.add(new JScrollPane(clubVotesPanel), BorderLayout.CENTER);

        uiClubList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateClubDetailsPanel(uiClubList.getSelectedIndex());
            }
        });

        joinBtn.addActionListener(e -> {
            int idx = uiClubList.getSelectedIndex();
            if (idx != -1) {
                BookClub selectedClub = globalClubs.get(idx);
                selectedClub.joinClub((Reader) activeUser);
                ((Reader) activeUser).getMyClubs().add(selectedClub);
                DatabaseManager.joinClub(selectedClub.getName(), activeUser.getUsername());
                JOptionPane.showMessageDialog(this, "Successfully joined the club!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateClubDetailsPanel(idx);
            }
        });

        // YENİ: Kulüp Kurma İşlemleri
        createClubBtn.addActionListener(e -> {
            JTextField clubNameField = new JTextField(15);
            styleTextField(clubNameField);
            String[] genres = {"Sci-Fi", "Fantasy", "Classic", "Self-Help", "History", "Mystery", "Thriller", "Romance"};
            JComboBox<String> genreCombo = new JComboBox<>(genres);
            styleTextField(genreCombo);

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            inputPanel.add(new JLabel("Club Name:"));
            inputPanel.add(clubNameField);
            inputPanel.add(new JLabel("Club Genre:"));
            inputPanel.add(genreCombo);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Create a New Book Club", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String cName = clubNameField.getText().trim();
                String cGenre = (String) genreCombo.getSelectedItem();

                if (cName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Club name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kulüp isminin daha önce alınıp alınmadığını kontrol et
                boolean exists = globalClubs.stream().anyMatch(c -> c.getName().equalsIgnoreCase(cName));
                if (exists) {
                    JOptionPane.showMessageDialog(this, "A club with this name already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Veritabanına kaydet
                boolean success = DatabaseManager.createNewClub(cName, cGenre, activeUser.getUsername());
                if (success) {
                    DatabaseManager.addClubCandidate(cName, "To Be Decided");
                    List<String> cand = new ArrayList<>();
                    cand.add("To Be Decided");
                    BookClub newClub = new BookClub(cName, cGenre, activeUser.getUsername(), cand, new HashMap<>());
                    newClub.joinClub((Reader) activeUser);
                    ((Reader) activeUser).getMyClubs().add(newClub);
                    globalClubs.add(newClub);
                    clubListModel.addElement(newClub.getName() + " (" + newClub.getGenre() + ")");

                    JOptionPane.showMessageDialog(this, "Club '" + cName + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create club. Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clubsPanel.add(leftClub);
        clubsPanel.add(rightClub);
        socialTabs.addTab("Clubs & Voting", new FlatIcon(FlatIcon.Type.USERS, COLOR_PRIMARY, 16), clubsPanel);

        // FORUM PANEL
        JPanel forumPanel = createCardPanel("Book Discussions");
        discussionPanel = new JPanel();
        discussionPanel.setLayout(new BoxLayout(discussionPanel, BoxLayout.Y_AXIS));
        discussionPanel.setBackground(COLOR_CARD);
        refreshDiscussions();
        forumPanel.add(new JScrollPane(discussionPanel), BorderLayout.CENTER);

        JPanel newCommentPanel = new JPanel(new BorderLayout(10, 10));
        newCommentPanel.setBackground(COLOR_CARD);
        JTextField commentField = new JTextField();
        styleTextField(commentField);
        JCheckBox spoilerCheck = new JCheckBox("Contains Spoiler");
        spoilerCheck.setBackground(COLOR_CARD);
        JButton postBtn = new JButton("Post");
        styleButton(postBtn, true);

        postBtn.addActionListener(e -> {
            if (!commentField.getText().trim().isEmpty()) {
                DatabaseManager.addForumComment(activeUser.getUsername(), commentField.getText().trim(), spoilerCheck.isSelected());
                commentField.setText("");
                spoilerCheck.setSelected(false);
                refreshDiscussions();
            }
        });

        newCommentPanel.add(commentField, BorderLayout.CENTER);
        JPanel botControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botControl.setBackground(COLOR_CARD);
        botControl.add(spoilerCheck);
        botControl.add(postBtn);
        newCommentPanel.add(botControl, BorderLayout.EAST);
        forumPanel.add(newCommentPanel, BorderLayout.SOUTH);

        socialTabs.addTab("Discussion Forum", new FlatIcon(FlatIcon.Type.CHAT, COLOR_PRIMARY, 16), forumPanel);
        p.add(socialTabs, BorderLayout.CENTER);
        return p;
    }

    private void updateClubDetailsPanel(int index) {
        clubVotesPanel.removeAll();
        if (index >= 0 && index < globalClubs.size()) {
            BookClub selected = globalClubs.get(index);
            boolean isMember = selected.getMembers().contains(activeUser);

            // YETKİ KONTROLÜ: Admin mi yoksa bu kulübü kuran kişi mi?
            boolean isAdminOrFounder = (activeUser instanceof Administrator) || activeUser.getUsername().equals(selected.getFounder());

            clubVotesPanel.add(new JLabel("<html><h3>" + selected.getName() + "</h3>"
                    + "<p>Founder: <b>" + selected.getFounder() + "</b></p>"
                    + "<p>Member Count: " + selected.getMembers().size() + "</p><hr></html>"));

            if (isMember) {
                clubVotesPanel.add(new JLabel("<html><b>Vote for the next book:</b></html>"));

                for (Map.Entry<String, Integer> voteEntry : selected.getVotes().entrySet()) {
                    JPanel vPanel = new JPanel(new BorderLayout());
                    vPanel.setBackground(COLOR_CARD);
                    vPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
                    vPanel.add(new JLabel(voteEntry.getKey() + " - (" + voteEntry.getValue() + " Votes)"), BorderLayout.CENTER);

                    JButton vBtn = new JButton("Vote");
                    styleButton(vBtn, true);
                    vBtn.addActionListener(e -> {
                        DatabaseManager.castClubVote(selected.getName(), activeUser.getUsername(), voteEntry.getKey());

                        // Oyları veritabanından tazeleyip arayüzü güncelliyoruz
                        selected.getVotes().clear();
                        List<String> cand = DatabaseManager.getClubCandidates(selected.getName());
                        for (String c : cand) {
                            selected.getVotes().put(c, 0);
                        }
                        selected.getVotes().putAll(DatabaseManager.getClubVotes(selected.getName()));

                        updateClubDetailsPanel(index);
                    });
                    vPanel.add(vBtn, BorderLayout.EAST);
                    clubVotesPanel.add(vPanel);
                }

                // YENİ EKLENEN: SADECE ADMİN VE KURUCULAR YENİ KİTAP ADAYI EKLİYOR!
                if (isAdminOrFounder) {
                    JButton addCandidateBtn = new JButton("+ Add Book Candidate");
                    styleButton(addCandidateBtn, false);
                    addCandidateBtn.setBackground(new Color(16, 185, 129));
                    addCandidateBtn.setForeground(Color.WHITE);
                    addCandidateBtn.addActionListener(e -> {
                        String newBook = JOptionPane.showInputDialog(this, "Enter book title to add to voting:", "Add Candidate", JOptionPane.PLAIN_MESSAGE);
                        if (newBook != null && !newBook.trim().isEmpty()) {
                            DatabaseManager.addClubCandidate(selected.getName(), newBook.trim());
                            selected.addCandidate(newBook.trim());
                            updateClubDetailsPanel(index);
                        }
                    });

                    JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    botPanel.setBackground(COLOR_CARD);
                    botPanel.add(addCandidateBtn);
                    clubVotesPanel.add(botPanel);
                }

            } else {
                clubVotesPanel.add(new JLabel("You must join the club to see and participate in voting."));
            }
        }
        clubVotesPanel.revalidate();
        clubVotesPanel.repaint();
    }

    private void refreshDiscussions() {
        discussionPanel.removeAll();
        List<String[]> dbComments = DatabaseManager.getForumComments();

        for (String[] c : dbComments) {
            String author = c[0];
            String content = c[1];
            boolean hasSpoiler = Boolean.parseBoolean(c[2]);

            JPanel cPanel = new JPanel(new BorderLayout(5, 5));
            cPanel.setBackground(new Color(249, 250, 251));
            cPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(new Color(229, 231, 235), 1, true)));
            cPanel.add(new JLabel("<html><b>" + author + "</b> says:</html>"), BorderLayout.NORTH);

            if (hasSpoiler) {
                JButton revealBtn = new JButton("Hidden Content (Spoiler) - Click to Reveal");
                styleButton(revealBtn, false);
                revealBtn.setForeground(new Color(239, 68, 68));
                revealBtn.addActionListener(e -> {
                    cPanel.remove(revealBtn);
                    cPanel.add(new JLabel("<html><p style='width:600px; color:#EF4444;'>" + content + "</p></html>"), BorderLayout.CENTER);
                    cPanel.revalidate();
                    cPanel.repaint();
                });
                cPanel.add(revealBtn, BorderLayout.CENTER);
            } else {
                cPanel.add(new JLabel("<html><p style='width:600px;'>" + content + "</p></html>"), BorderLayout.CENTER);
            }
            discussionPanel.add(cPanel);
        }
        discussionPanel.revalidate();
        discussionPanel.repaint();
    }

    private void refreshActivityFeed() {
        if (activityFeedPanel == null) {
            return;
        }
        activityFeedPanel.removeAll();
        List<String[]> feeds = DatabaseManager.getRecentFeeds();

        if (feeds.isEmpty()) {
            activityFeedPanel.add(new JLabel("  No recent activity. Be the first to share something!"));
        } else {
            for (String[] f : feeds) {
                String user = f[0];
                String msg = f[1];
                String time = f[2];

                JPanel fPanel = new JPanel(new BorderLayout(10, 10));
                fPanel.setBackground(new Color(249, 250, 251));
                fPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(new Color(229, 231, 235), 1, true)));

                String content = "<html><div style='width: 750px;'><b><font color='#4F46E5'>" + user + "</font></b>: " + msg + "<br><i style='font-size:10px; color:#9CA3AF;'>" + time + "</i></div></html>";
                JLabel lbl = new JLabel(content);
                lbl.setIcon(new FlatIcon(FlatIcon.Type.USER, COLOR_PRIMARY, 24));

                fPanel.add(lbl, BorderLayout.CENTER);
                activityFeedPanel.add(fPanel);
            }
        }
        activityFeedPanel.revalidate();
        activityFeedPanel.repaint();
    }

    private JPanel createProfilePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel card = createCardPanel("Character Status");

        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setBackground(COLOR_CARD);
        JPanel photoPanel = new JPanel(new BorderLayout(5, 5));
        photoPanel.setBackground(COLOR_CARD);

        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(120, 120));
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setBorder(new LineBorder(new Color(209, 213, 219), 2, true));

        if (activeUser.getProfileImage() != null) {
            profileImageLabel.setIcon(activeUser.getProfileImage());
        } else {
            profileImageLabel.setIcon(new FlatIcon(FlatIcon.Type.USER, COLOR_PRIMARY, 80));
        }

        JButton btnChangePhoto = new JButton("Select Photo");
        styleButton(btnChangePhoto, false);
        btnChangePhoto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChangePhoto.setMargin(new Insets(5, 5, 5, 5));

        JButton btnDeletePhoto = new JButton("Delete");
        styleButton(btnDeletePhoto, false);
        btnDeletePhoto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDeletePhoto.setMargin(new Insets(5, 5, 5, 5));
        btnDeletePhoto.setForeground(new Color(239, 68, 68));

        btnChangePhoto.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files (JPG, PNG)", "jpg", "jpeg", "png");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    Image img = new ImageIcon(chooser.getSelectedFile().getAbsolutePath()).getImage();
                    ImageIcon scaledIcon = new ImageIcon(img.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                    activeUser.setProfileImage(scaledIcon);
                    profileImageLabel.setIcon(scaledIcon);
                    byte[] photoBytes = DatabaseManager.iconToBytes(scaledIcon);
                    DatabaseManager.updateProfilePhoto(activeUser.getUsername(), photoBytes);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDeletePhoto.addActionListener(e -> {
            activeUser.setProfileImage(null);
            profileImageLabel.setIcon(new FlatIcon(FlatIcon.Type.USER, COLOR_PRIMARY, 80));
            DatabaseManager.updateProfilePhoto(activeUser.getUsername(), null);
        });

        JPanel photoButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        photoButtonsPanel.setBackground(COLOR_CARD);
        photoButtonsPanel.add(btnChangePhoto);
        photoButtonsPanel.add(btnDeletePhoto);
        photoPanel.add(profileImageLabel, BorderLayout.CENTER);
        photoPanel.add(photoButtonsPanel, BorderLayout.SOUTH);
        headerPanel.add(photoPanel, BorderLayout.WEST);

        profileInfoLabel = new JLabel(getProfileInfoText());
        profileInfoLabel.setFont(FONT_NORMAL);
        headerPanel.add(profileInfoLabel, BorderLayout.CENTER);
        card.add(headerPanel, BorderLayout.NORTH);

        // --- YENİ: GEÇMİŞ VE TAKİP LİSTESİNİ YAN YANA KOYAN KISIM ---
        JPanel centerSplit = new JPanel(new GridLayout(1, 2, 15, 0));
        centerSplit.setBackground(COLOR_CARD);

        JLabel historyLabel = new JLabel(getReadingHistoryText());
        centerSplit.add(new JScrollPane(historyLabel)); // Sol taraf: Geçmiş

        JPanel followingPanel = new JPanel(new BorderLayout(5, 5));
        followingPanel.setBackground(COLOR_CARD);
        followingPanel.add(new JLabel("<html><h3 style='color:#374151; margin-bottom:0;'>Following:</h3></html>"), BorderLayout.NORTH);

        DefaultListModel<String> followingModel = new DefaultListModel<>();
        if (activeUser instanceof Reader) {
            for (String f : ((Reader) activeUser).getFollowing()) {
                followingModel.addElement(f);
            }
        }
        JList<String> followingListUI = new JList<>(followingModel);
        followingListUI.setFont(FONT_NORMAL);
        followingPanel.add(new JScrollPane(followingListUI), BorderLayout.CENTER);

        JButton findUsersBtn = new JButton("+ Find Users to Follow");
        styleButton(findUsersBtn, false);
        findUsersBtn.addActionListener(e -> {
            List<String[]> allUsers = DatabaseManager.getAllUsers();
            List<String> notFollowing = new ArrayList<>();
            for (String[] u : allUsers) {
                String uName = u[0];
                if (!uName.equals(activeUser.getUsername()) && !((Reader) activeUser).getFollowing().contains(uName)) {
                    notFollowing.add(uName);
                }
            }
            if (notFollowing.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No new users found to follow.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String selectedUser = (String) JOptionPane.showInputDialog(this, "Select a user to follow:", "Find Users", JOptionPane.PLAIN_MESSAGE, null, notFollowing.toArray(), notFollowing.get(0));
            if (selectedUser != null) {
                if (DatabaseManager.followUser(activeUser.getUsername(), selectedUser)) {
                    ((Reader) activeUser).getFollowing().add(selectedUser);
                    followingModel.addElement(selectedUser);
                }
            }
        });
        followingPanel.add(findUsersBtn, BorderLayout.SOUTH);
        centerSplit.add(followingPanel); // Sağ taraf: Takip

        card.add(centerSplit, BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Secure Logout");
        styleButton(logoutBtn, false);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.add(logoutBtn);
        logoutBtn.addActionListener(e -> {
            mainContainer.remove(mainContainer.getComponentCount() - 1);
            cardLayout.show(mainContainer, "Login");
        });
        p.add(bottomPanel, BorderLayout.SOUTH);
        return p;
    }

    private String getProfileInfoText() {
        return "<html><body style='font-family: Segoe UI; padding: 10px; width: 400px;'>"
                + "<h1 style='color: #4F46E5; margin-bottom: 0;'>Welcome, " + activeUser.getUsername() + "!</h1>"
                + "<p style='color: #6B7280; font-size: 13px; margin-top: 0;'><b>Role:</b> " + activeUser.getRole() + "</p>"
                + "<div style='background-color: #F3F4F6; padding: 10px; border-radius: 8px; margin-top: 15px;'>"
                + "<h2 style='color: #059669; margin: 0;'>Level " + activeUser.getLevel() + " Reader</h2>"
                + "<p style='margin: 5px 0;'><b>EXP:</b> " + activeUser.getExp() + " / " + activeUser.getExpToNextLevel() + "</p>"
                + "<p style='margin: 5px 0;'><b>Total Pages Read:</b> " + activeUser.getTotalPagesRead() + "</p>"
                + "<p style='margin: 5px 0; color: #D97706;'><b>Daily Pace:</b> Average " + activeUser.getDailyPace() + " Pgs/Day</p></div>"
                + "</body></html>";
    }

    private String getReadingHistoryText() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Segoe UI; padding: 10px; width: 500px;'>")
                .append("<h3 style='color: #374151; margin-top: 5px;'>Reading History:</h3><ul style='font-size: 13px;'>");
        if (activeUser instanceof Reader && ((Reader) activeUser).getPageHistory().isEmpty()) {
            sb.append("<li style='color: #6B7280;'>No records yet.</li>");
        } else if (activeUser instanceof Reader) {
            for (Map.Entry<String, Integer> entry : ((Reader) activeUser).getPageHistory().entrySet()) {
                sb.append("<li><b>").append(entry.getKey()).append(":</b> ").append(entry.getValue()).append(" pages</li>");
            }
        }
        sb.append("</ul></body></html>");
        return sb.toString();
    }

    private JPanel createLibraryPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 25, 0));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        readingModel.clear();
        wantModel.clear();
        completedModel.clear();
        readingComboModel.removeAllElements();
        if (activeUser instanceof Reader) {
            for (Book b : ((Reader) activeUser).getCurrentlyReading()) {
                readingModel.addElement(b);
                readingComboModel.addElement(b);
            }
            for (Book b : ((Reader) activeUser).getWantToRead()) {
                wantModel.addElement(b);
            }
            for (Book b : ((Reader) activeUser).getCompletedBooks()) {
                completedModel.addElement(b);
            }
        }

        JPanel leftCard = createCardPanel("My Digital Library");
        JTabbedPane libraryTabs = new JTabbedPane();
        readingListUI = new JList<>(readingModel);
        readingListUI.setFont(FONT_NORMAL);
        wantToReadListUI = new JList<>(wantModel);
        wantToReadListUI.setFont(FONT_NORMAL);
        completedListUI = new JList<>(completedModel);
        completedListUI.setFont(FONT_NORMAL);

        libraryTabs.addTab("Reading", new FlatIcon(FlatIcon.Type.BOOK, COLOR_PRIMARY, 16), new JScrollPane(readingListUI));
        libraryTabs.addTab("Want to Read", new FlatIcon(FlatIcon.Type.BOOKMARK, new Color(245, 158, 11), 16), new JScrollPane(wantToReadListUI));
        libraryTabs.addTab("Completed", new FlatIcon(FlatIcon.Type.CHECK, new Color(16, 185, 129), 16), new JScrollPane(completedListUI));
        completedListUI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Çift tıklama kontrolü
                    Book selected = completedListUI.getSelectedValue();
                    if (selected != null) {
                        String msg = "Your Rating: " + selected.getUserRating() + " / 5 Stars\n\n"
                                + "Personal Note & Quotes:\n"
                                + (selected.getPersonalNote() == null || selected.getPersonalNote().isEmpty() ? "No notes added." : selected.getPersonalNote());
                        JOptionPane.showMessageDialog(VirtualBookClubSystem.this, msg, "Review: " + selected.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        leftCard.add(libraryTabs, BorderLayout.CENTER);

        JButton addBtn = new JButton("Discover New Book");
        styleButton(addBtn, false);
        leftCard.add(addBtn, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            JTextField titleField = new JTextField(15);
            styleTextField(titleField);
            JTextField authorField = new JTextField(15);
            styleTextField(authorField);
            JTextField pagesField = new JTextField(5);
            styleTextField(pagesField);
            String[] genres = {"Sci-Fi", "Fantasy", "Classic", "Self-Help", "History", "Mystery"};
            JComboBox<String> genreCombo = new JComboBox<>(genres);
            styleTextField(genreCombo);
            String[] types = {"Fiction", "Non-Fiction", "Graphic Novel", "Audiobook"};
            JComboBox<String> typeCombo = new JComboBox<>(types);
            styleTextField(typeCombo);
            String[] statuses = {"Start Reading Now", "Add to Want to Read"};
            JComboBox<String> statusCombo = new JComboBox<>(statuses);
            styleTextField(statusCombo);

            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            inputPanel.add(new JLabel("Book Title:"));
            inputPanel.add(titleField);
            inputPanel.add(new JLabel("Author:"));
            inputPanel.add(authorField);
            inputPanel.add(new JLabel("Genre:"));
            inputPanel.add(genreCombo);
            inputPanel.add(new JLabel("Book Type:"));
            inputPanel.add(typeCombo);
            inputPanel.add(new JLabel("Pages (Mins):"));
            inputPanel.add(pagesField);
            inputPanel.add(new JLabel("Status:"));
            inputPanel.add(statusCombo);

            if (JOptionPane.showConfirmDialog(this, inputPanel, "Add New Book", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    String title = titleField.getText().trim();
                    String author = authorField.getText().trim();
                    String genre = (String) genreCombo.getSelectedItem();
                    int pages = Integer.parseInt(pagesField.getText().trim());
                    if (pages <= 0) {
                        throw new NumberFormatException();
                    }

                    Book b;
                    if (typeCombo.getSelectedIndex() == 0) {
                        b = new FictionBook(title, author, genre, pages);
                    } else if (typeCombo.getSelectedIndex() == 1) {
                        b = new NonFictionBook(title, author, genre, pages);
                    } else if (typeCombo.getSelectedIndex() == 2) {
                        b = new GraphicNovel(title, author, genre, pages);
                    } else {
                        b = new AudioBook(title, author, genre, pages);
                    }

                    if (statusCombo.getSelectedIndex() == 0) {
                        ((Reader) activeUser).startReading(b);
                        readingModel.addElement(b);
                        readingComboModel.addElement(b);
                        DatabaseManager.saveBookToDatabase(activeUser.getUsername(), title, author, pages, genre, "Currently Reading");
                    } else {
                        ((Reader) activeUser).addToWantToRead(b);
                        wantModel.addElement(b);
                        DatabaseManager.saveBookToDatabase(activeUser.getUsername(), title, author, pages, genre, "Want to Read");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel rightCard = createCardPanel("Daily Progress Log");
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("<html><b>Select Book:</b></html>"), gbc);
        gbc.gridy = 1;
        JComboBox<Book> bookComboBox = new JComboBox<>(readingComboModel);
        styleTextField(bookComboBox);
        formPanel.add(bookComboBox, gbc);
        gbc.gridy = 2;
        formPanel.add(new JLabel("<html><b>Pages read today?</b></html>"), gbc);
        gbc.gridy = 3;
        JTextField readPagesField = new JTextField(10);
        styleTextField(readPagesField);
        formPanel.add(readPagesField, gbc);
        gbc.gridy = 4;
        JButton progressBtn = new JButton("Save Progress");
        progressBtn.setIcon(new FlatIcon(FlatIcon.Type.ROCKET, Color.WHITE, 16));
        styleButton(progressBtn, true);
        formPanel.add(progressBtn, gbc);
        rightCard.add(formPanel, BorderLayout.NORTH);

        progressBtn.addActionListener(e -> {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            if (selectedBook.getRemainingPages() == 0) {
                showReviewDialog(selectedBook);
                ((Reader) activeUser).finishBook(selectedBook);
                readingModel.removeElement(selectedBook);
                readingComboModel.removeElement(selectedBook);
                completedModel.addElement(selectedBook);

                DatabaseManager.updateBookProgress(activeUser.getUsername(), selectedBook.getTitle(), "Completed", selectedBook.getTotalPages());
                DatabaseManager.saveBookReview(activeUser.getUsername(), selectedBook.getTitle(), selectedBook.getUserRating(), selectedBook.getPersonalNote());

                String successMessage = "You have successfully finished and reviewed the book!";
            }
            try {
                int readPages = Integer.parseInt(readPagesField.getText().trim());
                if (readPages <= 0 || readPages > selectedBook.getRemainingPages()) {
                    JOptionPane.showMessageDialog(this, "Invalid page number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ((Reader) activeUser).logPages(selectedBook, readPages);

                DatabaseManager.addReadingLog(activeUser.getUsername(), selectedBook.getTitle(), readPages);
                DatabaseManager.updateUserStats(activeUser.getUsername(), ((Reader) activeUser).getTotalPagesRead(), ((Reader) activeUser).getLevel(), ((Reader) activeUser).getExp());
                DatabaseManager.updateBookProgress(activeUser.getUsername(), selectedBook.getTitle(), "Currently Reading", selectedBook.getPagesRead());

                int daysLeft = ((Reader) activeUser).getDailyPace() > 0 ? (selectedBook.getRemainingPages() / ((Reader) activeUser).getDailyPace()) : 0;
                String successMessage = "Read " + readPages + " pages from " + selectedBook.getTitle() + "!\nEstimated finish: in " + daysLeft + " days.";

                if (selectedBook.getRemainingPages() == 0) {
                    showReviewDialog(selectedBook);
                    ((Reader) activeUser).finishBook(selectedBook);
                    readingModel.removeElement(selectedBook);
                    readingComboModel.removeElement(selectedBook);
                    completedModel.addElement(selectedBook);
                    successMessage = "You have successfully finished and reviewed the book!";
                    DatabaseManager.updateBookProgress(activeUser.getUsername(), selectedBook.getTitle(), "Completed", selectedBook.getTotalPages());
                    DatabaseManager.saveBookReview(activeUser.getUsername(), selectedBook.getTitle(), selectedBook.getUserRating(), selectedBook.getPersonalNote());
                }
                updateUIComponents();
                readPagesField.setText("");
                JOptionPane.showMessageDialog(this, successMessage, "Progress Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(leftCard);
        p.add(rightCard);
        return p;
    }

    private void showReviewDialog(Book book) {
        JPanel reviewPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        reviewPanel.add(new JLabel("Congratulations! You finished the book. Rating (1-5):"));
        JSlider ratingSlider = new JSlider(1, 5, 5);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        reviewPanel.add(ratingSlider);
        reviewPanel.add(new JLabel("Personal Note/Quote:"));
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setBorder(new LineBorder(Color.GRAY));
        reviewPanel.add(new JScrollPane(noteArea));
        JOptionPane.showMessageDialog(this, reviewPanel, "Book Review", JOptionPane.PLAIN_MESSAGE);
        book.rate(ratingSlider.getValue());
        book.setPersonalNote(noteArea.getText());
    }

    private JPanel createAnalyticsPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Analytics içine alt sekmeler ekliyoruz
        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.setFont(FONT_NORMAL);

        // --- 1. ALT SEKME: 3'LÜ DAİRE GRAFİĞİ (TÜR DAĞILIMI) ---
        JPanel pieChartsContainer = new JPanel(new GridLayout(1, 3, 15, 0));
        pieChartsContainer.setBackground(COLOR_BG);
        pieChartsContainer.add(createPieChartPanel("Completed Books", 1));
        pieChartsContainer.add(createPieChartPanel("Currently Reading", 2));
        pieChartsContainer.add(createPieChartPanel("Want to Read", 3));

        analyticsTabs.addTab("Genre Distributions (Pie)", new FlatIcon(FlatIcon.Type.CHART, COLOR_PRIMARY, 16), pieChartsContainer);

        // --- 2. ALT SEKME: SÜTUN GRAFİĞİ (GÜNLÜK OKUMA) ---
        JPanel barChartPanel = createCardPanel("Daily Reading Progress (Last 7 Days)");
        JPanel barChartCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!(activeUser instanceof Reader)) {
                    return;
                }

                // Veritabanından gün-gün sayfa verilerini çekiyoruz
                Map<String, Integer> dailyData = DatabaseManager.getDailyReadingStats(activeUser.getUsername());
                if (dailyData.isEmpty()) {
                    g2.setFont(FONT_NORMAL);
                    g2.drawString("No reading logs found for the last 7 days. Start reading to see your progress!", 50, 50);
                    return;
                }

                // Grafiğin tepe noktasını hesapla
                int maxPages = dailyData.values().stream().max(Integer::compareTo).orElse(1);
                maxPages = Math.max(maxPages, 10); // Grafiğin çok kısa görünmemesi için min sınır

                int width = getWidth();
                int height = getHeight();
                int padding = 40;

                int columnCount = Math.max(dailyData.size(), 7);
                int barWidth = (width - (2 * padding)) / columnCount;
                int x = padding;

                // X ve Y eksenlerini çiz
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(padding, padding, padding, height - padding); // Y Eksen
                g2.drawLine(padding, height - padding, width - padding, height - padding); // X Eksen

                // Dinamik Sütunları Çiz
                for (Map.Entry<String, Integer> entry : dailyData.entrySet()) {
                    int barHeight = (int) (((double) entry.getValue() / maxPages) * (height - 2 * padding));
                    int y = height - padding - barHeight;

                    // Sütun Rengi ve Şekli
                    g2.setColor(new Color(5, 150, 105)); // Şık bir yeşil
                    g2.fillRect(x + 10, y, barWidth - 20, barHeight);

                    // Sütun üstüne okunan sayfa sayısını yaz
                    g2.setColor(COLOR_TEXT);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2.drawString(entry.getValue() + " Pgs", x + (barWidth / 2) - 15, y - 5);

                    // X Eksenine Tarihi Yaz (YYYY-MM-DD formatını MM-DD yapar)
                    String dateStr = entry.getKey();
                    if (dateStr != null && dateStr.length() >= 10) {
                        dateStr = dateStr.substring(5);
                    }
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.drawString(dateStr, x + (barWidth / 2) - 15, height - padding + 15);

                    x += barWidth;
                }
            }
        };
        barChartCanvas.setBackground(Color.WHITE);
        barChartPanel.add(barChartCanvas, BorderLayout.CENTER);

        analyticsTabs.addTab("Daily Tracker (Bar)", new FlatIcon(FlatIcon.Type.ROCKET, new Color(245, 158, 11), 16), barChartPanel);

        p.add(analyticsTabs, BorderLayout.CENTER);
        return p;
    }

    // YENİ YARDIMCI METOT: 3'lü daire grafiği tasarımlarını otomatik üretir
    private JPanel createPieChartPanel(String title, int type) {
        JPanel wrapper = createCardPanel(title);
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!(activeUser instanceof Reader)) {
                    return;
                }
                Reader r = (Reader) activeUser;

                // Tipe göre doğru listeyi hesapla
                Map<String, Integer> dist;
                if (type == 1) {
                    dist = r.getGenreDistribution(r.getCompletedBooks());
                } else if (type == 2) {
                    dist = r.getGenreDistribution(r.getCurrentlyReading());
                } else {
                    dist = r.getGenreDistribution(r.getWantToRead());
                }

                if (dist.isEmpty()) {
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    g2.setColor(Color.GRAY);
                    g2.drawString("Empty List", 20, 50);
                    return;
                }

                int total = dist.values().stream().mapToInt(Integer::intValue).sum();
                int startAngle = 0;
                int x = 20, y = 10, size = 120;
                Color[] colors = {new Color(99, 102, 241), new Color(16, 185, 129), new Color(245, 158, 11),
                    new Color(239, 68, 68), new Color(139, 92, 246), new Color(14, 165, 233)};
                int colorIdx = 0;
                int legendY = size + 30;

                for (Map.Entry<String, Integer> entry : dist.entrySet()) {
                    int arcAngle = (int) Math.round((entry.getValue() / (double) total) * 360);
                    g2.setColor(colors[colorIdx % colors.length]);
                    g2.fillArc(x, y, size, size, startAngle, arcAngle);
                    startAngle += arcAngle;

                    g2.fillRect(10, legendY, 12, 12);
                    g2.setColor(COLOR_TEXT);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString(entry.getKey() + " (" + entry.getValue() + ")", 30, legendY + 10);
                    legendY += 20;
                    colorIdx++;
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        wrapper.add(canvas, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createChallengesPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTabbedPane challengeTabs = new JTabbedPane();
        challengeTabs.setFont(FONT_NORMAL);

        // --- 1. AKTİF GÖREVLERİM ---
        JPanel myChallengesCard = createCardPanel("Active Challenges");
        challengesListPanel = new JPanel();
        challengesListPanel.setLayout(new BoxLayout(challengesListPanel, BoxLayout.Y_AXIS));
        challengesListPanel.setBackground(COLOR_CARD);
        refreshChallengesList();
        myChallengesCard.add(new JScrollPane(challengesListPanel), BorderLayout.CENTER);
        challengeTabs.addTab("My Challenges", new FlatIcon(FlatIcon.Type.TROPHY, COLOR_PRIMARY, 16), myChallengesCard);

        // --- 2. GELEN DAVETLER VE GÖNDERME EKRANI ---
        JPanel invitesCard = createCardPanel("Challenge Invitations");
        JPanel invitesContent = new JPanel(new BorderLayout());
        invitesContent.setBackground(COLOR_CARD);

        // Yeni Davet Gönderme Butonu
        JButton sendInviteBtn = new JButton("+ Challenge a User");
        styleButton(sendInviteBtn, true);
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(COLOR_CARD);
        topBar.add(sendInviteBtn);
        invitesContent.add(topBar, BorderLayout.NORTH);

        // Gelen Davetler Listesi
        JPanel invitesListPanel = new JPanel();
        invitesListPanel.setLayout(new BoxLayout(invitesListPanel, BoxLayout.Y_AXIS));
        invitesListPanel.setBackground(COLOR_CARD);
        refreshInvitesList(invitesListPanel);

        invitesContent.add(new JScrollPane(invitesListPanel), BorderLayout.CENTER);
        invitesCard.add(invitesContent, BorderLayout.CENTER);
        challengeTabs.addTab("Invitations & Inbox", new FlatIcon(FlatIcon.Type.CHAT, new Color(236, 72, 153), 16), invitesCard);

        // Davet Gönderme Olayı (Event) - TAMAMEN DEĞİŞEN KISIM
        sendInviteBtn.addActionListener(e -> {
            // Elle girmek yerine sadece takip ettiklerini listele
            List<String> myFollowing = activeUser instanceof Reader ? ((Reader) activeUser).getFollowing() : new ArrayList<>();
            JComboBox<String> receiverCombo = new JComboBox<>(myFollowing.toArray(new String[0]));
            styleTextField(receiverCombo);

            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"PageCount", "TimeSprint"});
            styleTextField(typeCombo);
            JTextField titleField = new JTextField(15);
            styleTextField(titleField);
            JTextField targetField = new JTextField(5);
            styleTextField(targetField);

            JPanel dialogPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            dialogPanel.add(new JLabel("Select Friend:"));
            dialogPanel.add(receiverCombo);
            dialogPanel.add(new JLabel("Challenge Type:"));
            dialogPanel.add(typeCombo);
            dialogPanel.add(new JLabel("Challenge Title:"));
            dialogPanel.add(titleField);
            dialogPanel.add(new JLabel("Target Pages:"));
            dialogPanel.add(targetField);

            if (JOptionPane.showConfirmDialog(this, dialogPanel, "Send a Challenge", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    String receiver = (String) receiverCombo.getSelectedItem();
                    if (receiver == null) {
                        JOptionPane.showMessageDialog(this, "You need to follow someone first!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String type = (String) typeCombo.getSelectedItem();
                    String title = titleField.getText().trim();
                    int target = Integer.parseInt(targetField.getText().trim());
                    String desc = "A custom challenge from " + activeUser.getUsername();
                    String deadline = java.time.LocalDate.now().plusDays(7).toString();

                    DatabaseManager.sendChallengeInvite(activeUser.getUsername(), receiver, type, title, desc, target, deadline);
                    JOptionPane.showMessageDialog(this, "Challenge sent successfully to " + receiver + "!", "Success", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid target pages!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        p.add(challengeTabs, BorderLayout.CENTER);
        return p;
    }

    // Gelen Davetleri Ekrana Çizme Metodu
    private void refreshInvitesList(JPanel panel) {
        if (!(activeUser instanceof Reader)) {
            return;
        }
        panel.removeAll();
        List<String[]> invites = DatabaseManager.getPendingInvites(activeUser.getUsername());

        if (invites.isEmpty()) {
            panel.add(new JLabel("  You have no pending challenge invitations."));
        } else {
            for (String[] inv : invites) {
                int id = Integer.parseInt(inv[0]);
                String sender = inv[1];
                String type = inv[2];
                String title = inv[3];
                String desc = inv[4];
                int target = Integer.parseInt(inv[5]);
                String deadline = inv[6];

                JPanel invCard = new JPanel(new BorderLayout(10, 10));
                invCard.setBackground(new Color(249, 250, 251));
                invCard.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(Color.LIGHT_GRAY, 1, true)));

                String info = "<html><b style='color:#4F46E5;'>From: " + sender + "</b><br><b>Challenge:</b> " + title + " (" + type + ")<br><i>" + desc + "</i><br><b>Target:</b> " + target + " pages</html>";
                invCard.add(new JLabel(info), BorderLayout.CENTER);

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btnPanel.setBackground(invCard.getBackground());

                JButton acceptBtn = new JButton("Accept");
                acceptBtn.setBackground(new Color(16, 185, 129));
                acceptBtn.setForeground(Color.WHITE);
                JButton rejectBtn = new JButton("Reject");
                rejectBtn.setBackground(new Color(239, 68, 68));
                rejectBtn.setForeground(Color.WHITE);

                acceptBtn.addActionListener(e -> {
                    DatabaseManager.respondToInvite(id, true, activeUser.getUsername(), type, title, desc, target, deadline);
                    // Kabul edilen görevi anında UI'a ekle
                    if (type.equals("PageCount")) {
                        ((Reader) activeUser).getActiveChallenges().add(new PageCountChallenge(title, desc, target));
                    } else {
                        ((Reader) activeUser).getActiveChallenges().add(new TimeSprintChallenge(title, desc, target, java.time.LocalDate.parse(deadline)));
                    }
                    refreshChallengesList();
                    refreshInvitesList(panel);
                    JOptionPane.showMessageDialog(this, "Challenge Accepted! Check your Active Challenges tab.", "Success", JOptionPane.INFORMATION_MESSAGE);
                });

                rejectBtn.addActionListener(e -> {
                    DatabaseManager.respondToInvite(id, false, activeUser.getUsername(), type, title, desc, target, deadline);
                    refreshInvitesList(panel);
                });

                btnPanel.add(acceptBtn);
                btnPanel.add(rejectBtn);
                invCard.add(btnPanel, BorderLayout.EAST);
                panel.add(invCard);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void refreshChallengesList() {
        if (challengesListPanel == null || !(activeUser instanceof Reader)) {
            return;
        }
        challengesListPanel.removeAll();
        for (Challenge c : ((Reader) activeUser).getActiveChallenges()) {
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setBackground(c.isCompleted() ? new Color(236, 253, 245) : new Color(249, 250, 251));
            taskPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), BorderFactory.createCompoundBorder(new LineBorder(new Color(209, 213, 219), 1, true), new EmptyBorder(10, 10, 10, 10))));
            String statusHtml = "<html><b>" + c.getTitle() + "</b><br><i style='font-size:10px;'>" + c.getDescription() + "</i></html>";
            JLabel progressLabel = new JLabel(c.isCompleted() ? "COMPLETED" : c.getProgressText());
            if (c.isCompleted()) {
                progressLabel.setIcon(new FlatIcon(FlatIcon.Type.CHECK, new Color(5, 150, 105), 16));
                progressLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            }
            progressLabel.setFont(FONT_BOLD);
            progressLabel.setForeground(c.isCompleted() ? new Color(5, 150, 105) : COLOR_PRIMARY);
            taskPanel.add(new JLabel(statusHtml), BorderLayout.CENTER);
            taskPanel.add(progressLabel, BorderLayout.EAST);
            challengesListPanel.add(taskPanel);
        }
        challengesListPanel.revalidate();
        challengesListPanel.repaint();
    }

    private JPanel createBadgesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel card = createCardPanel("Collection: Earned and Locked Badges");
        badgesContainerPanel = new JPanel();
        badgesContainerPanel.setLayout(new BoxLayout(badgesContainerPanel, BoxLayout.Y_AXIS));
        badgesContainerPanel.setBackground(COLOR_CARD);
        refreshBadgesList();
        card.add(new JScrollPane(badgesContainerPanel), BorderLayout.CENTER);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void refreshBadgesList() {
        if (badgesContainerPanel == null || !(activeUser instanceof Reader)) {
            return;
        }
        badgesContainerPanel.removeAll();
        for (Map.Entry<String, String> entry : ALL_BADGES.entrySet()) {
            boolean isUnlocked = ((Reader) activeUser).getBadges().contains(entry.getKey());
            JPanel badgeCard = new JPanel(new BorderLayout());
            badgeCard.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 5, 5), BorderFactory.createCompoundBorder(new LineBorder(isUnlocked ? COLOR_PRIMARY : new Color(209, 213, 219), 1, true), new EmptyBorder(10, 10, 10, 10))));
            badgeCard.setBackground(isUnlocked ? new Color(238, 242, 255) : new Color(249, 250, 251));
            String titleColor = isUnlocked ? "#4F46E5" : "#9CA3AF";
            String descColor = isUnlocked ? "#374151" : "#9CA3AF";
            String statusHtml = "<html><b style='font-size:14px; color:" + titleColor + ";'>" + entry.getKey() + "</b><br><i style='font-size:11px; color:" + descColor + ";'>" + entry.getValue() + "</i></html>";
            JLabel statusLabel = new JLabel(isUnlocked ? "UNLOCKED" : "LOCKED");
            statusLabel.setIcon(new FlatIcon(isUnlocked ? FlatIcon.Type.LOCK_OPEN : FlatIcon.Type.LOCK_CLOSED, isUnlocked ? new Color(5, 150, 105) : new Color(156, 163, 175), 18));
            statusLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            statusLabel.setFont(FONT_BOLD);
            statusLabel.setForeground(isUnlocked ? new Color(5, 150, 105) : new Color(156, 163, 175));
            badgeCard.add(new JLabel(statusHtml), BorderLayout.CENTER);
            badgeCard.add(statusLabel, BorderLayout.EAST);
            badgesContainerPanel.add(badgeCard);
        }
        badgesContainerPanel.revalidate();
        badgesContainerPanel.repaint();
    }

    private void updateUIComponents() {
        if (profileInfoLabel != null) {
            profileInfoLabel.setText(getProfileInfoText());
        }
        refreshChallengesList();
        refreshBadgesList();
        if (analyticsChartPanel != null) {
            analyticsChartPanel.repaint();
        }
        if (readingListUI != null) {
            readingListUI.repaint();
        }
    }

    // Uygulamayı Başlatan Ana Metot
    public static void main(String[] args) {

        DatabaseManager.initDatabase();

        globalClubs.clear();
        List<String[]> dbClubs = DatabaseManager.getAllClubs();
        for (String[] cData : dbClubs) {
            String cName = cData[0], cGenre = cData[1], cFounder = cData[2];
            List<String> candidates = DatabaseManager.getClubCandidates(cName);
            Map<String, Integer> actualVotes = DatabaseManager.getClubVotes(cName);
            globalClubs.add(new BookClub(cName, cGenre, cFounder, candidates, actualVotes));
        }
        SwingUtilities.invokeLater(() -> new VirtualBookClubSystem().setVisible(true));
    }
}
