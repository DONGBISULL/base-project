package com.demo.modules.entity;

import com.demo.modules.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

//@Entity
//@Table(name = "member_roles")
//@DynamicInsert
//@DynamicUpdate
@Getter
@Setter
public class MemberRole {

//    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name="uuid2", strategy = "uuid2")
//    @Column(name = "id")
//    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "member_id", referencedColumnName = "id")
//    private Member member;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "role")
//    private Role role;
}
