package com.hhgz.wage.dto;

/**
 * @description:
 * @author: JinLong Cai
 * @date: 2024/9/1 20:47
 */
public class PersonTimeDTO {

    private Integer day;

    private Integer minute;

    private Integer lunchDec;

    public PersonTimeDTO() {
        this.day = 0;
        this.minute = 0;
        this.lunchDec = 0;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getLunchDec() {
        return lunchDec;
    }

    public void setLunchDec(Integer lunchDec) {
        this.lunchDec = lunchDec;
    }
}