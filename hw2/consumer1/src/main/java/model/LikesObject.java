package model;

public class LikesObject {

    private String swiper;
    private int numOfLikes;
    private int numOfDislikes;

    public String getSwiper() {
        return swiper;
    }

    public void setSwiper(String swiper) {
        this.swiper = swiper;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public void setNumOfLikes(int numOfLikes) {
        this.numOfLikes = numOfLikes;
    }

    public int getNumOfDislikes() {
        return numOfDislikes;
    }

    public void setNumOfDislikes(int numOfDislikes) {
        this.numOfDislikes = numOfDislikes;
    }
    public void incrementLikes(){
        this.numOfLikes += 1;
    }

    public void incrementDislikes(){
        this.numOfDislikes += 1;
    }
}
