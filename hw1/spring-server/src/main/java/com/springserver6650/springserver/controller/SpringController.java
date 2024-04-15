package com.springserver6650.springserver.controller;

import com.google.gson.Gson;
import com.springserver6650.springserver.model.SwipeBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

@ComponentScan(basePackageClasses = SpringController.class)
@RestController
@RequestMapping("/swipe")
public class SpringController {
    private Gson gson = new Gson();
    @PostMapping( value = "/{leftOrRight}/", produces = "application/json")
    public ResponseEntity<String> doPost(@RequestBody String body ,@PathVariable("leftOrRight")String leftOrRight) throws Exception {

        if (leftOrRight == null || leftOrRight.isEmpty()){
            return new ResponseEntity<>("missing parameters", HttpStatus.NOT_FOUND);
        }

        SwipeBody swipeBody = gson.fromJson(body, SwipeBody.class);
        if (!isUrlValid(leftOrRight)) {
            return new ResponseEntity<>("Invalid url", HttpStatus.BAD_REQUEST);
        } else if (!isRequestBodyValid(swipeBody)){
            return new ResponseEntity<>("Bad request data", HttpStatus.BAD_REQUEST);
        } else {
            String jsonSwipe = gson.toJson(swipeBody);
            return new ResponseEntity<>(jsonSwipe, HttpStatus.OK);
        }
    }

    @GetMapping("test")
    public ResponseEntity<String> doGet(){
        return new ResponseEntity<>("hi there", HttpStatus.OK);
    }

    private boolean isUrlValid(String leftOrRight) {
        return leftOrRight.equals("left") || leftOrRight.equals("right");
    }

    private boolean isRequestBodyValid(SwipeBody body){
        if (Integer.parseInt(body.getSwiper()) >= 1 &&
                Integer.parseInt(body.getSwiper()) <= 5000 &&
                Integer.parseInt(body.getSwipee()) >= 1 &&
                Integer.parseInt(body.getSwipee()) <= 1000000 &&
                body.getComment().length() == 256
        ){
            return true;
        }
        return false;
    }

}
