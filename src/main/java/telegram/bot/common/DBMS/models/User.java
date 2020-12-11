package telegram.bot.common.DBMS.models;

import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "chat_id")
    private Long id;

    @NotNull
    @Column(length = 50)
    private String first_name;

    @NotNull
    @Column(length = 50)
    private String last_name;

    @NotNull
    @Unique
    @Column(length = 70)
    private String user_name;

    @NotNull
    @Column(length = 80)
    private String city;

    @NotNull
    @Column(length = 20, name = "sub_time")
    private String subTime;

    @NotNull
    private Boolean starting;

    @NotNull
    private Boolean subscribe;

    public User() {}

    public User(
            Long id,
            String first_name,
            String last_name,
            String user_name,
            String subTime
    ) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_name = user_name;
        this.city = "Москва";
        this.subTime = subTime;
        this.starting = false;
        this.subscribe = false;
    }

    public Boolean getStarting() { return starting; }

    public Boolean getSubscribe() { return subscribe; }

    public Long getId() { return id; }

    public String getCity() { return city; }

    public String getFirst_name() { return first_name; }

    public String getLast_name() { return last_name; }

    public String getSubTime() { return subTime; }

    public String getUser_name() { return user_name; }

    public void setId(Long id) { this.id = id; }

    public void setCity(String city) { this.city = city; }

    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public void setLast_name(String last_name) { this.last_name = last_name; }

    public void setStarting(Boolean starting) { this.starting = starting; }

    public void setSubscribe(Boolean subscribe) { this.subscribe = subscribe; }

    public void setSubTime(String subTime) { this.subTime = subTime; }

    public void setUser_name(String user_name) { this.user_name = user_name; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", user_name='" + user_name + '\'' +
                ", city='" + city + '\'' +
                ", subTime='" + subTime + '\'' +
                ", starting=" + starting +
                ", subscribe=" + subscribe +
                '}';
    }
}
