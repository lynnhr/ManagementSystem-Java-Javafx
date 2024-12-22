package com.example.managementsystem;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserCRUD {
    private static UserCRUD userCRUD = null;
    private PreparedStatement createUser,loginUser;
    private  PreparedStatement getUsers;

    public static UserCRUD getUserCRUD(){
        if( userCRUD==null){
            return new UserCRUD();
        }else {
            return  userCRUD;
        }
    }
    private  UserCRUD(){
        try {
            createUser =Database.getInstance().getConnection().prepareStatement("Insert Into users (username,password) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            loginUser=Database.getInstance().getConnection().prepareStatement("SELECT user_id, username, password,role FROM users WHERE username = ? AND password = ? ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean LoggedInUser(String userName,String password) throws SQLException {
        loginUser.setString(1,userName);
        loginUser.setString(2,password);
        ResultSet rs = loginUser.executeQuery();
        int userID;String role;
        if (rs.next()) {
             userID = rs.getInt("user_id");
             role = rs.getString("role");
            System.out.println("Logged in successfully!");

            User user = User.getUser();
            user.setUserID(userID);
            user.setUsername(userName);
            user.setPassword(password);
            user.setRole(role);
            System.out.println("User:\n ID: " + user.getUserID() + "\n role: " + user.getRole());
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }


    }
    public void createUserFn(String userName,String password) throws SQLException {
        createUser.setString(1,userName);
        createUser.setString(2,password);
        createUser.executeUpdate();
        int newUserID=-1;
        ResultSet rs = createUser.getGeneratedKeys();
        if (rs.next()) {
            newUserID = rs.getInt(1);
        }
        System.out.println( " records inserted with ID:"+newUserID);
        User user=User.getUser();
        user.setUserID(newUserID);
        user.setUsername(userName);
        user.setPassword(password);
        System.out.println("user:\n id: "+user.getUserID()+"\n name:"+user.getUsername()+"has been created");

    }
    public boolean isUsernameTaken(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = Database.getInstance().getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }



}
