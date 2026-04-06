package kr.ac.hansung.cse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // ── ② 양방향 선언 시 추가 (처음에는 없어도 됨) ───────────────────
    @OneToMany(mappedBy = "category",         // Product.java의 category 필드명
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    // 편의 메서드: 양쪽 참조를 한 번에 설정
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);            // Owning Side(FK) 설정!
    }

    public Category(String name) { this.name = name; }
}
