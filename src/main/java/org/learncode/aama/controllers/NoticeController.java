package org.learncode.aama.controllers;


import org.learncode.aama.entites.Notice;
import org.learncode.aama.service.noticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeController {
    @Autowired
    private noticeService service;

    @PostMapping("/create-notice")
    public List<Notice> postNotice(@RequestBody Notice notice,String name){
        Notice notice1 = service.createNotice(notice,name);
        return List.of(notice1);

    }

    @GetMapping("/notice/{userid}")
    public List<Notice> getNotice(@PathVariable("userid") Long userId){
        List<Notice> notice = service.getNotice(userId);
        return notice;
    }


}
