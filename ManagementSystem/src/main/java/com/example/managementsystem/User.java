package com.example.managementsystem;

public class User {
    private static User user =null;
    private int userID;
    private String username;
    private String password;
    private String role;
    private boolean isDarkMode;

    private User() {

    }




    public static User getUser() {
        if (user == null) {
            user= new User();
            return user;
        } else {
            return user;
        }
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role=role;
    }
    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

}

