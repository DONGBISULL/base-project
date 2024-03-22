package com.demo.modules.entity;

import com.demo.modules.enums.MemberStatusEnum;
import com.demo.modules.enums.ProviderType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "member")
@DynamicInsert
@DynamicUpdate
@DiscriminatorColumn
@Getter
@Setter
public class Member extends BaseEntity {
    /* 식별자 */
    @Id
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    /* 패스워드 */
    @Column(name = "password")
    private String password;

    /* 이름 */
    @Column(name = "name")
    private String name;

    /* 전화번호 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /* 소속부서 */
    @Column(name = "department")
    private String department;

    /* 이메일(아이디) */
    @Column(name = "email")
    private String email;

    /* 상태 */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MemberStatusEnum status;

    /* 프로바이더 타입 */
    @Column(name = "providerType")
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles;
}
