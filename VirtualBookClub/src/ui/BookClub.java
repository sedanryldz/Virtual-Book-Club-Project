
package ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class BookClub {

    private String name;
    private String genre;
    private String founder; // YENİ
    private List<Reader> members = new ArrayList<>();
    private Map<String, Integer> votes = new LinkedHashMap<>();

    public BookClub(String name, String genre, String founder, List<String> candidates, Map<String, Integer> actualVotes) {
        this.name = name;
        this.genre = genre;
        this.founder = founder;
        for (String candidate : candidates) {
            votes.put(candidate, 0);
        }
        if (actualVotes != null) {
            for (Map.Entry<String, Integer> entry : actualVotes.entrySet()) {
                votes.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getFounder() {
        return founder;
    }

    public List<Reader> getMembers() {
        return members;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public void joinClub(Reader reader) {
        if (!members.contains(reader)) {
            members.add(reader);
        }
    }

    public void addCandidate(String book) {
        if (!votes.containsKey(book)) {
            votes.put(book, 0);
        }
    }

}
