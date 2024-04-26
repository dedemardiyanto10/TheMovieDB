package com.themoviedb.app;

public class MovieDetails {
    private String budget;
    private String genres;
    private String productionCompanies;
    private String runtime;
    private String status;
    private String revenue;
    private String tagline;
    private String logoPath;
    private String companyName;
    private String originCountry;

    public MovieDetails(
            String budget,
            String genres,
            String productionCompanies,
            String runtime,
            String status,
            String revenue,
            String tagline,
            String logoPath,
            String companyName,
            String originCountry) {
        this.budget = budget;
        this.genres = genres;
        this.productionCompanies = productionCompanies;
        this.runtime = runtime;
        this.status = status;
        this.revenue = revenue;
        this.tagline = tagline;
        this.logoPath = logoPath;
        this.companyName = companyName;
        this.originCountry = originCountry;
    }

    public MovieDetails() {}

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(String productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }
}
