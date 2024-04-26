package com.themoviedb.app;

public class Cast {

    private int id;
    private String name;
    private String profilePath;
    private String character;

    public Cast(int id, String name, String profilePath, String character) {
        this.id = id;
        this.name = name;
        this.profilePath = profilePath;
        this.character = character;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return this.profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getCharacter() {
        return this.character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
