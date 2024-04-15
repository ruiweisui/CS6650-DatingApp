import io.swagger.client.model.SwipeDetails;

import java.util.concurrent.ThreadLocalRandom;

public class RequestGenerator {

    String direction;
    SwipeDetails body;

    public RequestGenerator() {
        this.direction = generateSwipe();
        this.body = generateBody();
    }

    public String getDirection() {
        return direction;
    }

    public SwipeDetails getBody() {
        return body;
    }

    private String generateSwipe(){
        int oneOrZero = ThreadLocalRandom.current().nextInt(0,2);
        if (oneOrZero == 0){
            return "left";
        } else {
            return "right";
        }
    }

    private SwipeDetails generateBody(){
        SwipeDetails body = new SwipeDetails();

        int swiper = ThreadLocalRandom.current().nextInt(1,5001);
        int swipee = ThreadLocalRandom.current().nextInt(1,1000001);
        String comment = RandomStringGenerator(256);

        body.setSwiper(String.valueOf(swiper));
        body.setSwipee(String.valueOf(swipee));
        body.setComment(comment);

        return body;
    }


    private String RandomStringGenerator(int length){
        StringBuilder comment = new StringBuilder();
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        for (int i = 0; i < length; i++){
            int charIndex = ThreadLocalRandom.current().nextInt(leftLimit,rightLimit+1);
            comment.append((char)charIndex);
        }

        return comment.toString();
    }

}
