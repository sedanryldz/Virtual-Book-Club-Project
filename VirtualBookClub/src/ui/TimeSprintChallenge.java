
package ui;

import ui.Challenge;
import ui.Reader;

class TimeSprintChallenge extends Challenge {

    private int targetPages;
    private int currentPages;
    private java.time.LocalDate deadline;

    public TimeSprintChallenge(String title, String description, int targetPages, java.time.LocalDate deadline) {
        super(title, description);
        this.targetPages = targetPages;
        this.deadline = deadline;
    }

    @Override
    public void checkProgress(Reader reader) {
        if (isCompleted) {
            return;
        }

        // Eğer günümüz tarihi, son teslim tarihini (deadline) geçmişse görevi kontrol etmeyi bırakır
        if (java.time.LocalDate.now().isAfter(deadline)) {
            return;
        }

        this.currentPages = reader.getTotalPagesRead();
        if (this.currentPages >= targetPages) {
            this.isCompleted = true;
            reader.unlockBadge(title);
            reader.gainExp(600);
        }
    }

    @Override
    public String getProgressText() {
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), deadline);
        String timeStr = daysLeft >= 0 ? (" (" + daysLeft + " days left)") : " (Expired)";
        return (currentPages > targetPages ? targetPages : currentPages) + " / " + targetPages + " Pgs" + timeStr;
    }
}