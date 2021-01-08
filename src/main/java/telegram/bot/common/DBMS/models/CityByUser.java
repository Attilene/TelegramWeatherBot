package telegram.bot.common.DBMS.models;

import javax.persistence.*;

@Entity
@Table(name = "cities_by_user")
public class CityByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public CityByUser() {}

    public Integer getId() { return id; }

    public User getUser() { return user; }

    public City getCity() { return city; }

    public void setUser(User user) { this.user = user; }

    public void setCity(City city) { this.city = city; }

    @Override
    public String toString() {
        return "CityByUser{" +
                "id=" + id +
                ", city=" + city +
                ", user=" + user +
                '}';
    }
}
