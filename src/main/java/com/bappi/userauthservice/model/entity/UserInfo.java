package com.bappi.userauthservice.model.entity;

import com.bappi.userauthservice.model.enums.UserState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

import static com.bappi.userauthservice.config.UserInfoDBConstant.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = USER_INFO_TABLE)
public class UserInfo {

    @Id
    @Column(name = ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = CODE,unique=true)
    private String code;

    @Column(name = EMAIL,unique=true)
    private String email;

    @Column(name = USER_NAME,unique=true)
    private String userName;

    @Column(name = FULL_NAME)
    private String fullName;

    @Column(name = PASSWORD)
    private String password;

    @Column(name = ROLES)
    private String roles;

    @Column(name = USER_STATE)
    @Enumerated(EnumType.STRING)
    private UserState userState = UserState.PENDING;

    @Column(name = ACTIVE_STATUS)
    private Boolean activeStatus;

    @Column(name = CREATE_DATE)
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    @Column(name = CREATED_BY)
    private Long createBy;

    @Column(name = UPDATE_DATE)
    private Timestamp updateDate = new Timestamp(System.currentTimeMillis());

    @Column(name = UPDATED_BY)
    private Long updateBy;

}
