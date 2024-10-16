package jm.task.core.jdbc.model;

//import javax.persistence.*;


//@Table(name = "users") //  таблица в базе данных, к которой привязан этот класс, имеет имя "users"
public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // значение первичного ключа будет генерироваться базой данных с использованием механизма автоинкремента
//    @Column(name = "user_id", updatable = false, nullable = false) //  updatable = false: это поле нельзя изменять после того, как оно было установлено при первоначальном сохранении сущности в базу данных.
    private Long id;

//    @Column(name = "user_name", length = 50, nullable = false)
    private String name;

//    @Column(name = "user_lastName", length = 70, nullable = false)
    private String lastName;

//    @Column(name = "user_age", nullable = false)
    private Byte age;

    public User() {
        // NOP
    }

    public User(String name, String lastName, Byte age) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}
