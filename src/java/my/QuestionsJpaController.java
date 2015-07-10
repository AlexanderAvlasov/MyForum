/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import my.exceptions.NonexistentEntityException;
import my.exceptions.PreexistingEntityException;

/**
 *
 * @author Sasha
 */
public class QuestionsJpaController implements Serializable {

    public QuestionsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Questions questions) throws PreexistingEntityException, Exception {
        if (questions.getMessagesCollection() == null) {
            questions.setMessagesCollection(new ArrayList<Messages>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Topics idtopic = questions.getIdtopic();
            if (idtopic != null) {
                idtopic = em.getReference(idtopic.getClass(), idtopic.getId());
                questions.setIdtopic(idtopic);
            }
            Collection<Messages> attachedMessagesCollection = new ArrayList<Messages>();
            for (Messages messagesCollectionMessagesToAttach : questions.getMessagesCollection()) {
                messagesCollectionMessagesToAttach = em.getReference(messagesCollectionMessagesToAttach.getClass(), messagesCollectionMessagesToAttach.getId());
                attachedMessagesCollection.add(messagesCollectionMessagesToAttach);
            }
            questions.setMessagesCollection(attachedMessagesCollection);
            em.persist(questions);
            if (idtopic != null) {
                idtopic.getQuestionsCollection().add(questions);
                idtopic = em.merge(idtopic);
            }
            for (Messages messagesCollectionMessages : questions.getMessagesCollection()) {
                Questions oldIdqestionOfMessagesCollectionMessages = messagesCollectionMessages.getIdqestion();
                messagesCollectionMessages.setIdqestion(questions);
                messagesCollectionMessages = em.merge(messagesCollectionMessages);
                if (oldIdqestionOfMessagesCollectionMessages != null) {
                    oldIdqestionOfMessagesCollectionMessages.getMessagesCollection().remove(messagesCollectionMessages);
                    oldIdqestionOfMessagesCollectionMessages = em.merge(oldIdqestionOfMessagesCollectionMessages);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findQuestions(questions.getId()) != null) {
                throw new PreexistingEntityException("Questions " + questions + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Questions questions) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Questions persistentQuestions = em.find(Questions.class, questions.getId());
            Topics idtopicOld = persistentQuestions.getIdtopic();
            Topics idtopicNew = questions.getIdtopic();
            Collection<Messages> messagesCollectionOld = persistentQuestions.getMessagesCollection();
            Collection<Messages> messagesCollectionNew = questions.getMessagesCollection();
            if (idtopicNew != null) {
                idtopicNew = em.getReference(idtopicNew.getClass(), idtopicNew.getId());
                questions.setIdtopic(idtopicNew);
            }
            Collection<Messages> attachedMessagesCollectionNew = new ArrayList<Messages>();
            for (Messages messagesCollectionNewMessagesToAttach : messagesCollectionNew) {
                messagesCollectionNewMessagesToAttach = em.getReference(messagesCollectionNewMessagesToAttach.getClass(), messagesCollectionNewMessagesToAttach.getId());
                attachedMessagesCollectionNew.add(messagesCollectionNewMessagesToAttach);
            }
            messagesCollectionNew = attachedMessagesCollectionNew;
            questions.setMessagesCollection(messagesCollectionNew);
            questions = em.merge(questions);
            if (idtopicOld != null && !idtopicOld.equals(idtopicNew)) {
                idtopicOld.getQuestionsCollection().remove(questions);
                idtopicOld = em.merge(idtopicOld);
            }
            if (idtopicNew != null && !idtopicNew.equals(idtopicOld)) {
                idtopicNew.getQuestionsCollection().add(questions);
                idtopicNew = em.merge(idtopicNew);
            }
            for (Messages messagesCollectionOldMessages : messagesCollectionOld) {
                if (!messagesCollectionNew.contains(messagesCollectionOldMessages)) {
                    messagesCollectionOldMessages.setIdqestion(null);
                    messagesCollectionOldMessages = em.merge(messagesCollectionOldMessages);
                }
            }
            for (Messages messagesCollectionNewMessages : messagesCollectionNew) {
                if (!messagesCollectionOld.contains(messagesCollectionNewMessages)) {
                    Questions oldIdqestionOfMessagesCollectionNewMessages = messagesCollectionNewMessages.getIdqestion();
                    messagesCollectionNewMessages.setIdqestion(questions);
                    messagesCollectionNewMessages = em.merge(messagesCollectionNewMessages);
                    if (oldIdqestionOfMessagesCollectionNewMessages != null && !oldIdqestionOfMessagesCollectionNewMessages.equals(questions)) {
                        oldIdqestionOfMessagesCollectionNewMessages.getMessagesCollection().remove(messagesCollectionNewMessages);
                        oldIdqestionOfMessagesCollectionNewMessages = em.merge(oldIdqestionOfMessagesCollectionNewMessages);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = questions.getId();
                if (findQuestions(id) == null) {
                    throw new NonexistentEntityException("The questions with id " + id + " no longer exists.");
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
            Questions questions;
            try {
                questions = em.getReference(Questions.class, id);
                questions.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The questions with id " + id + " no longer exists.", enfe);
            }
            Topics idtopic = questions.getIdtopic();
            if (idtopic != null) {
                idtopic.getQuestionsCollection().remove(questions);
                idtopic = em.merge(idtopic);
            }
            Collection<Messages> messagesCollection = questions.getMessagesCollection();
            for (Messages messagesCollectionMessages : messagesCollection) {
                messagesCollectionMessages.setIdqestion(null);
                messagesCollectionMessages = em.merge(messagesCollectionMessages);
            }
            em.remove(questions);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Questions> findQuestionsEntities() {
        return findQuestionsEntities(true, -1, -1);
    }

    public List<Questions> findQuestionsEntities(int maxResults, int firstResult) {
        return findQuestionsEntities(false, maxResults, firstResult);
    }

    private List<Questions> findQuestionsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Questions.class));
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

    public Questions findQuestions(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Questions.class, id);
        } finally {
            em.close();
        }
    }

    public int getQuestionsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Questions> rt = cq.from(Questions.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
