package exam.master.repository;

import exam.master.domain.History;
import exam.master.domain.Prompt;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PromptRepository {

  private final EntityManager em;

  public Prompt save(Prompt prompt){
    em.persist(prompt);
    return prompt;
  }

  public List<Prompt> findByHistoryId(UUID historyId) {
    return em.createQuery("select p from Prompt p where p.history.id = :historyId", Prompt.class)
        .setParameter("historyId", historyId)
        .getResultList();
  }

  public int deletePromptsByHistoryId(UUID historyId){
    return em.createQuery("DELETE FROM Prompt p WHERE p.history.id = :historyId")
        .setParameter("historyId", historyId)
        .executeUpdate(); // 데이터베이스에 변경 사항을 적용, 삭제된 행의 수를 반환
  }
}
