/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import my.exceptions.NonexistentEntityException;
import my.exceptions.PreexistingEntityException;

/**
 *
 * @author Sasha
 */
public class MessagesJpaController implements Serializable {

    public MessagesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Messages messages) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Questions idqestion = messages.getIdqestion();
            if (idqestion != null) {
                idqestion = em.getReference(idqestion.getClass(), idqestion.getId());
                messages.setIdqestion(idqestion);
            }
            em.persist(messages);
            if (idqestion != null) {
                idqestion.getMessagesCollection().add(messages);
                idqestion = em.merge(idqestion);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMessages(messages.getId()) != null) {
                throw new PreexistingEntityException("Messages " + messages + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Messages messages) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Messages persistentMessages = em.find(Messages.class, messages.getId());
            Questions idqestionOld = persistentMessages.getIdqestion();
            Questions idqestionNew = messages.getIdqestion();
            if (idqestionNew != null) {
                idqestionNew = em.getReference(idqestionNew.getClass(), idqestionNew.getId());
                messages.setIdqestion(idqestionNew);
            }
            messages = em.merge(messages);
            if (idqestionOld != null && !idqestionOld.equals(idqestionNew)) {
                idqestionOld.getMessagesCollection().remove(messages);
                idqestionOld = em.merge(idqestionOld);
            }
            if (idqestionNew != null && !idqestionNew.equals(idqestionOld)) {
                idqestionNew.getMessagesCollection().add(messages);
                idqestionNew = em.merge(idqestionNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = messages.getId();
                if (findMessages(id) == null) {
                    throw new NonexistentEntityException("The messages with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(BigDecimal id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Messages messages;
            try {
                messages = em.getReference(Messages.class, id);
                messages.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The messages with id " + id + " no longer exists.", enfe);
            }
            Questions idqestion = messages.getIdqestion();
            if (idqestion != null) {
                idqestion.getMessagesCollection().remove(messages);
                idqestion = em.merge(idqestion);
            }
            em.remove(messages);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Messages> findMessagesEntities() {
        return findMessagesEntities(true, -1, -1);
    }

    public List<Messages> findMessagesEntities(int maxResults, int firstResult) {
        return findMessagesEntities(false, maxResults, firstResult);
    }

    private List<Messages> findMessagesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Messages.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Messages findMessages(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Messages.class, id);
        } finally {
            em.close();
        }
    }

    public int getMessagesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Messages> rt = cq.from(Messages.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
