package com.example.sporttracker;

public class User {


    private Integer id;
    private String firstName;
    private String lastName;
    private String password;
    private Integer height;
    private Integer weight;
    private Integer age;


    public User(String firstName, String lastName, String password, Integer height, Integer weight, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public User(Integer id, String firstName, String lastName, String password, Integer height, Integer weight, Integer age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.height = height;
        this.weight = weight;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
