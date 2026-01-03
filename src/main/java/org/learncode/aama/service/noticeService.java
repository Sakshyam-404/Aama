package org.learncode.aama.service;

import org.learncode.aama.Dao.NoticeRepo;
import org.learncode.aama.Dao.NotificationRecpRepo;
import org.learncode.aama.Dao.UserRepo;
import org.learncode.aama.entites.Notice;
import org.learncode.aama.entites.NotificationReceipent;
import org.learncode.aama.entites.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class noticeService {
    @Autowired
    private NoticeRepo noticeRepo;
    @Autowired
    private NotificationRecpRepo notificationRecpRepo;
    @Autowired
    private UserRepo userRepo;


    public Notice createNotice(Notice notice, String name){
        Users user = userRepo.findUsersByName(name);
        notice.setNoticeCreator(name);
        Notice save = noticeRepo.save(notice);
        List<Users> allUsers = userRepo.findAll();
        List<NotificationReceipent> receipents= new ArrayList<>();
        for(Users u :allUsers){
            NotificationReceipent nr= new NotificationReceipent();
            nr.setNotice(notice);
            nr.setUser(u);
            nr.setStatus("Unread");
            receipents.add(nr);

        }
        notificationRecpRepo.saveAll(receipents);
        return notice;
    }

    public List<Notice> getNotice(Long userid){
        List<NotificationReceipent> unread = notificationRecpRepo.findNotificationReceipentByUser_UserIDAndStatus(userid, "Unread");
        List<Notice> notices= new ArrayList<>();
        for(NotificationReceipent n : unread){
            Notice notice= new Notice();
            notice.setNotice_id(n.getNotice().getNotice_id());
            notice.setType(n.getNotice().getType());
            notice.setPurpose(n.getNotice().getPurpose());
            notice.setNoticeCreator(n.getNotice().getNoticeCreator());
            notices.add(notice);
        }
        return notices;





    }
}
