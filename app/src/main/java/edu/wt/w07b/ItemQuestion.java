package edu.wt.w07b;

import com.google.gson.annotations.SerializedName;

public class ItemQuestion {
    public String title;
    public String link;

    @SerializedName("question_id")
    public long id;

    @Override
    public String toString() {
        return(title);
    }

    public ItemQuestion() {
    }

    public ItemQuestion(String title, String link, long id) {
        this.setTitle(title);
        this.setLink(link);
        this.setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
