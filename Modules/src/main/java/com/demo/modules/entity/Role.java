package com.demo.modules.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@DynamicInsert
@DynamicUpdate
@DiscriminatorColumn
@Getter
@Setter
public class Role {
    @Id
    @Column(name = "id")
    private String roleId;

    @Column(name = "role_name")
    private String roleName;
}
