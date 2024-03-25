package com.demo.modules.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu")
@DynamicInsert
@DynamicUpdate
@DiscriminatorColumn
@Getter
@Setter
public class Menu extends BaseEntity{
    @Id
    @Column(name = "id", length = 36)
    private String menuId;

    @Column(name = "menu_name", length = 100)
    private String menuName;

    @Column(name = "menu_url", length = 255)
    private String menuUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_menu_id")
    private Menu parentMenu;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "menu")
    private List<MenuPermission> permissions;
}
