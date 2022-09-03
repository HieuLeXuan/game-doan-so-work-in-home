package cybersoft.javabackend.java18.gamedoanso.model;

public class Ranking {

    private String username;
    private int numPlay;
    private int timePlay;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumPlay() {
        return numPlay;
    }

    public void setNumPlay(int numPlay) {
        this.numPlay = numPlay;
    }

    public int getTimePlay() {
        return timePlay;
    }

    public void setTimePlay(int timePlay) {
        this.timePlay = timePlay;
    }

    // fluent style api
    public Ranking username(String username) {
        this.username = username;
        return this;
    }

    public Ranking numPlay(int numPlay) {
        this.numPlay = numPlay;
        return this;
    }

    public Ranking timePlay(int timePlay) {
        this.timePlay = timePlay;
        return this;
    }
}
