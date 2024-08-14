package exam.master.repository;

import exam.master.domain.History;
import exam.master.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryRepository {

  private final EntityManager em;

  public History save(History history){
    em.persist(history);
    return history;
  }

  public History findOne(UUID historyId){
    return em.find(History.class, historyId);
  }

  public List<History> findAllByMemberId(UUID memberId){
    return em.createQuery("select h from History h where h.member.id = :memberId", History.class)
        .setParameter("memberId", memberId)
        .getResultList();
  }

  public int deleteHistoryByHistoryId(UUID historyId){
    return em.createQuery("DELETE FROM History h WHERE h.historyId = :historyId")
        .setParameter("historyId", historyId)
        .executeUpdate(); // 데이터베이스에 변경 사항을 적용, 삭제된 행의 수를 반환
  }

}
