package vn.tienbm.pojo;

import java.util.List;

/**
 * Created by tienbm on 27/04/2015.
 */
public class Webpage {
    String content;
    Long published;
    List<Image> images;
    List<Video> videos;
    String langugages;
    List<String> probalitiyLanguages;

    public Webpage(String content, Long published, List<Image> images, List<Video> videos, String langugages, List<String> probalitiyLanguages) {
        this.content = content;
        this.published = published;
        this.images = images;
        this.videos = videos;
        this.langugages = langugages;
        this.probalitiyLanguages = probalitiyLanguages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPublished() {
        return published;
    }

    public void setPublished(Long published) {
        this.published = published;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public String getLangugages() {
        return langugages;
    }

    public void setLangugages(String langugages) {
        this.langugages = langugages;
    }

    public List<String> getProbalitiyLanguages() {
        return probalitiyLanguages;
    }

    public void setProbalitiyLanguages(List<String> probalitiyLanguages) {
        this.probalitiyLanguages = probalitiyLanguages;
    }
}
