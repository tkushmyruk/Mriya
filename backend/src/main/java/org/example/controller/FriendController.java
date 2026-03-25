package org.example.controller;

import org.example.domain.sql.Friend;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendController {

    @GetMapping
    public Friend getFriend() {
        return new Friend("Best Friend");
    }
}
