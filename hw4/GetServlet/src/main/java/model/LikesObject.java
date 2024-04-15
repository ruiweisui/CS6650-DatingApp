package model;

public class LikesObject {

    private int numLikes;
    private int numDislikes;

    public LikesObject(int numLikes, int numDislikes) {
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumDislikes() {
        return numDislikes;
    }

    public void setNumDislikes(int numDislikes) {
        this.numDislikes = numDislikes;
    }
    public void incrementLikes(){
        this.numLikes += 1;
    }

    public void incrementDislikes(){
        this.numDislikes += 1;
    }
}
