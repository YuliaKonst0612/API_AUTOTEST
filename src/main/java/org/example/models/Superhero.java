package org.example.models;

import java.util.Objects;

public class Superhero {
    private String birthDate;
    private String city;
    private String fullName;
    private String gender;
    private int id;
    private String mainSkill;
    private String phone;

    public Superhero() {
    }

    public Superhero(String birthDate, String city, String fullName, String gender, int id, String mainSkill, String phone) {
        this.birthDate = birthDate;
        this.city = city;
        this.fullName = fullName;
        this.gender = gender;
        this.id = id;
        this.mainSkill = mainSkill;
        this.phone = phone;
    }

    public Superhero(String birthDate, String city, String fullName, String gender, String mainSkill, String phone) {
        this.birthDate = birthDate;
        this.city = city;
        this.fullName = fullName;
        this.gender = gender;
        this.mainSkill = mainSkill;
        this.phone = phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Superhero{" +
                "birthDate='" + birthDate + '\'' +
                ", city='" + city + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", id=" + id +
                ", mainSkill='" + mainSkill + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Superhero superhero = (Superhero) o;
        return Objects.equals(getBirthDate(), superhero.getBirthDate()) && Objects.equals(getCity(), superhero.getCity()) && Objects.equals(getFullName(), superhero.getFullName()) && Objects.equals(getGender(), superhero.getGender()) && Objects.equals(getMainSkill(), superhero.getMainSkill()) && Objects.equals(getPhone(), superhero.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBirthDate(), getCity(), getFullName(), getGender(), getMainSkill(), getPhone());
    }
}
