package User;

import java.time.LocalDateTime;

public class Data_user {

    private int id;
    private String username;
    private String email;
    private String role;
 
    private String registeredDate;

    public Data_user() {
    }

    public Data_user(int id, String username, String email, String role, String status, String registeredDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    
        this.registeredDate = registeredDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    @Override
    public String toString() {
        return "Datauser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                
                ", registeredDate='" + registeredDate + '\'' +
                '}';
    }

	 
}
