package com.example.eulerproject.Models;

public class RankModel {

    private String name;
    private int score;
    private int rank;

    public RankModel(int score, int rank, String name) {
        this.score = score;
        this.rank = rank;
        this.name = name;
    }

    public RankModel(String name, int total_score, int rank) {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
