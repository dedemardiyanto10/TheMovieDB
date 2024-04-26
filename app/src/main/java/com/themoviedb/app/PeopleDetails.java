package com.themoviedb.app;

public class PeopleDetails {
    private String knownAs;
    private double popularity;
    private String department;
    private String gender;
    private String birthday;
    private String place;
    private String deathday;
    private String biography;

    public PeopleDetails(
            String knownAs,
            double popularity,
            String department,
            String gender,
            String birthday,
            String place,
            String deathday,
            String biography) {
        this.knownAs = knownAs;
        this.popularity = popularity;
        this.department = department;
        this.gender = gender;
        this.birthday = birthday;
        this.place = place;
        this.deathday = deathday;
        this.biography = biography;
    }

    public PeopleDetails() {
        // Konstruktor kosong
    }

    public String getKnownAs() {
        return this.knownAs;
    }

    public void setKnownAs(String knownAs) {
        this.knownAs = knownAs;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
    
   public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDeathday() {
        return this.deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public String getBiography() {
        return this.biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
