package org.learncode.aama.service;
import org.learncode.aama.Dao.*;
import org.learncode.aama.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class LoanService {
    @Autowired
    private LoanRepo loanRepo;
    @Autowired
    private LoanRequestRepo loanRequestRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NoticeRepo noticeRepo;

    public Notice createLoan(Long userId, LoanRequest loanRequest){
        Optional<Users> user = userRepo.findById(userId);
        Users users = user.get();
        loanRequest.setUsers(users);
        LoanRequest loanreq = loanRequestRepo.save(loanRequest);
        Notice notice= new Notice();
        notice.setType("Loan Request for Rs"+loanRequest.getAmount());
        notice.setPurpose(loanRequest.getPurpose()+"  Status : "+loanRequest.getStatus());
        notice.setNoticeCreator(users.getName());
        return notice;

    }

    public Notice approve(String name,Long adminId){
        Users admin = userRepo.getById(adminId);
        if(admin.getRole().equals("ADMIN")){
            Users user = userRepo.findUsersByName(name);
            LoanRequest loanrequest = loanRequestRepo.findByUsers_UserID(user.getUserID());
            Notice noticeByPurpose = noticeRepo.getNoticeByPurpose(loanrequest.getPurpose() + "  Status : " + loanrequest.getStatus());
            loanrequest.setStatus("Approved");
            loanRequestRepo.save(loanrequest);
            Loan loan= new Loan();
            loan.setPrincipal(loanrequest.getAmount());
            loan.setUsers(loanrequest.getUsers());
            loan.setStatus("ACTIVE");
            noticeByPurpose.setPurpose(loanrequest.getPurpose() + "  Status : " + loanrequest.getStatus());
            Notice save = noticeRepo.save(noticeByPurpose);
            return save;

        }
        else{
            System.out.println("Only admin can approve request");
            return null;
        }
    }

    public Notice reject(String name,Long adminId){
        Users admin = userRepo.getById(adminId);
        if(admin.getRole().equals("ADMIN")){
            Users user = userRepo.findUsersByName(name);
            LoanRequest loanrequest = loanRequestRepo.findByUsers_UserID(user.getUserID());
            Notice noticeByPurpose = noticeRepo.getNoticeByPurpose(loanrequest.getPurpose() + "  Status : " + loanrequest.getStatus());
            loanrequest.setStatus("Rejected");
            loanRequestRepo.save(loanrequest);
            noticeByPurpose.setPurpose(loanrequest.getPurpose() + "  Status : " + loanrequest.getStatus());
            Notice save = noticeRepo.save(noticeByPurpose);
            return save;
        }
        else{
            System.out.println("Only admin can approve request");
            return null;
        }



    }


}
