package org.learncode.aama.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactFMemDto {
    private Long userID;
    private String name;
    private String phonenumber;
    private int nofactiveloans;
    private String role;
}
