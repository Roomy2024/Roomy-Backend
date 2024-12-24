package com.example.Roomy.image.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file_group")
public class FileGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileGroupId;

    @OneToMany(mappedBy = "fileGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageEntity> images = new ArrayList<>();
}
