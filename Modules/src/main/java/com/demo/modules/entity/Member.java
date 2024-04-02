package com.demo.modules.entity;

import com.demo.modules.dto.OAuth2UserInfo;
import com.demo.modules.entity.BaseEntity;
import com.demo.modules.enums.MemberStatusEnum;
import com.demo.modules.enums.ProviderType;
import com.demo.modules.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "member")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
//@Inheritance(strategy = InheritanceType.JOINED)
public class Member extends BaseEntity {

    private static final long serialVersionUID = 2820667885310603793L;

    public Member() {
    }

    @Builder
    public Member(String email, String name, String imageUrl,
                  ProviderType providerType, Set<Role> roles, String socialId,
                  MemberStatusEnum status) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.providerType = providerType;
        this.roles = roles;
        this.socialId = socialId;
        this.status = status;
    }

    public Member(String id) {
        this.id = id;
    }

    /* 식별자 */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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

    /* 간편 로그인을 한 경우 ID 지정 */
    @Column(name = "socialId")
    private String socialId;

    /* 프로바이더 타입 */
    @Column(name = "providerType")
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    // 컬렉션 필드를 매핑할 때, 해당 필드에 저장되는 요소의 타입을 지정
    // role 이 확정이 되지 않은 상태이므로 임시로 즉시 로딩으로 설정
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Column(name = "imageUrl")
    private String imageUrl;

}
