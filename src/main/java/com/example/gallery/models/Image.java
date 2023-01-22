package com.example.gallery.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="image_id")
    private Integer id;

    @Column(nullable=false)
    private String title;

    @Lob
    @Column(name="image_file")
    private byte[] file;

    @Column(nullable=false)
    private boolean hidden = true;

    @Column(nullable=false, name="who_added")
    private String whoAdded;

    @Column(nullable=false)
    private Date date;

    @Column(nullable=false)
    private Integer likes = 0;

    private Boolean likedByCurrentUser = false;
}
