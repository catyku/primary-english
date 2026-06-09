package com.primaryenglish.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vocabularies")
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String english;

    @Column(nullable = false, length = 100)
    private String chinese;

    @Column(length = 100)
    private String phonetic;

    @Column(name = "example_en", length = 500)
    private String exampleEn;

    @Column(name = "example_cn", length = 500)
    private String exampleCn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 100)
    private String image;

    public Vocabulary() {}

    public Vocabulary(Long id, String english, String chinese, String phonetic,
                      String exampleEn, String exampleCn, Category category, String image) {
        this.id = id;
        this.english = english;
        this.chinese = chinese;
        this.phonetic = phonetic;
        this.exampleEn = exampleEn;
        this.exampleCn = exampleCn;
        this.category = category;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getChinese() { return chinese; }
    public void setChinese(String chinese) { this.chinese = chinese; }

    public String getPhonetic() { return phonetic; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }

    public String getExampleEn() { return exampleEn; }
    public void setExampleEn(String exampleEn) { this.exampleEn = exampleEn; }

    public String getExampleCn() { return exampleCn; }
    public void setExampleCn(String exampleCn) { this.exampleCn = exampleCn; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
