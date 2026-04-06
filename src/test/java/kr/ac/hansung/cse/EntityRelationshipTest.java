package kr.ac.hansung.cse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.ac.hansung.cse.config.DbConfig;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DbConfig.class)
public class EntityRelationshipTest {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private CategoryRepository categoryRepo;

    // ───────────────────────────────────────────────────────────────────
    // 실습 1-A: @ManyToOne 단방향
    // ───────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("실습1-A: @ManyToOne 단방향 - Product가 Category를 참조")
    public void test_ManyToOne_Unidirectional() {
        // [1] Category 저장 (먼저 저장해야 FK 참조 가능)
        Category electronics = new Category("전자제품");

        categoryRepo.save(electronics);
        em.flush();

        // [2] Product에 Category 설정 (FK 설정)
        Product laptop = new Product("테스트 노트북", electronics,
                new BigDecimal("1500000"), "테스트용 노트북");
        laptop.setCategory(electronics);          // Owning Side: FK 설정
        em.persist(laptop);
        em.flush(); em.clear();                   // 1차 캐시 초기화

        // [3] 저장된 Product 조회 → Category 확인
        Product found = em.find(Product.class, laptop.getId());
        assertNotNull(found.getCategory());
        assertEquals("전자제품", found.getCategory().getName());
        System.out.println("Category: " + found.getCategory().getName());
    }

    @Test
    @DisplayName("실습1-B: @OneToMany 양방향 - Category에서 Products 접근")
    public void test_OneToMany_Bidirectional() {
        Category electronics = new Category("전자제품");

        Product p1 = new Product("노트북", electronics, new BigDecimal("1500000"), "테스트");
        Product p2 = new Product("마우스", electronics, new BigDecimal("30000"), "테스트");

        // addProduct() 편의 메서드 사용 → 양쪽 참조 동시 설정
        electronics.addProduct(p1);
        electronics.addProduct(p2);

        // CascadeType.ALL → Category 저장 시 Products도 함께 저장
        categoryRepo.save(electronics);
        em.flush(); em.clear();

        // JOIN FETCH로 Category + Products 한 번에 로드 (N+1 방지)
        Category found = categoryRepo.findByIdWithProducts(electronics.getId())
                .orElseThrow();
        assertEquals(2, found.getProducts().size());

        System.out.println("Products in '전자제품':");
        found.getProducts().forEach(p -> System.out.println("  - " + p.getName()));
    }

}