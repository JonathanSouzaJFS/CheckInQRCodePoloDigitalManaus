package br.orgipdec.checkinqrcodepolodigitalmanaus.model;


import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.orgipdec.checkinqrcodepolodigitalmanaus.model.Speaker;

public class Talk implements Serializable
{

    @SerializedName("talk-id")
    @Expose
    private String talkId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("hour")
    @Expose
    private String hour;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("speakers")
    @Expose
    private List<Speaker> speakers = null;
    private final static long serialVersionUID = 4679302575148196766L;

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

}