package com.dunesart.android.bitquiz.Model;

public class User  {
    private String userName;
    private String password;
    private String email;
    private int score;

    public User() {
    }

    public User(String userName, String password, String email, int score) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore(){ return score; }

    public void setScore(int score) {this.score = score; }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                '}';
    }
}
