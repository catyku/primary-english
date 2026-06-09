package com.primaryenglish.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String nameEn;

    @Column(length = 50)
    private String icon;

    @Column(length = 100)
    private String image;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public Category() {}

    public Category(Long id, String name, String nameEn, String icon, String image, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.icon = icon;
        this.image = image;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
