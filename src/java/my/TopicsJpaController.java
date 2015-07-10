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
public class TopicsJpaController implements Serializable {

    public TopicsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Topics topics) throws PreexistingEntityException, Exception {
        if (topics.getQuestionsCollection() == null) {
            topics.setQuestionsCollection(new ArrayList<Questions>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Questions> attachedQuestionsCollection = new ArrayList<Questions>();
            for (Questions questionsCollectionQuestionsToAttach : topics.getQuestionsCollection()) {
                questionsCollectionQuestionsToAttach = em.getReference(questionsCollectionQuestionsToAttach.getClass(), questionsCollectionQuestionsToAttach.getId());
                attachedQuestionsCollection.add(questionsCollectionQuestionsToAttach);
            }
            topics.setQuestionsCollection(attachedQuestionsCollection);
            em.persist(topics);
            for (Questions questionsCollectionQuestions : topics.getQuestionsCollection()) {
                Topics oldIdtopicOfQuestionsCollectionQuestions = questionsCollectionQuestions.getIdtopic();
                questionsCollectionQuestions.setIdtopic(topics);
                questionsCollectionQuestions = em.merge(questionsCollectionQuestions);
                if (oldIdtopicOfQuestionsCollectionQuestions != null) {
                    oldIdtopicOfQuestionsCollectionQuestions.getQuestionsCollection().remove(questionsCollectionQuestions);
                    oldIdtopicOfQuestionsCollectionQuestions = em.merge(oldIdtopicOfQuestionsCollectionQuestions);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTopics(topics.getId()) != null) {
                throw new PreexistingEntityException("Topics " + topics + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Topics topics) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Topics persistentTopics = em.find(Topics.class, topics.getId());
            Collection<Questions> questionsCollectionOld = persistentTopics.getQuestionsCollection();
            Collection<Questions> questionsCollectionNew = topics.getQuestionsCollection();
            Collection<Questions> attachedQuestionsCollectionNew = new ArrayList<Questions>();
            for (Questions questionsCollectionNewQuestionsToAttach : questionsCollectionNew) {
                questionsCollectionNewQuestionsToAttach = em.getReference(questionsCollectionNewQuestionsToAttach.getClass(), questionsCollectionNewQuestionsToAttach.getId());
                attachedQuestionsCollectionNew.add(questionsCollectionNewQuestionsToAttach);
            }
            questionsCollectionNew = attachedQuestionsCollectionNew;
            topics.setQuestionsCollection(questionsCollectionNew);
            topics = em.merge(topics);
            for (Questions questionsCollectionOldQuestions : questionsCollectionOld) {
                if (!questionsCollectionNew.contains(questionsCollectionOldQuestions)) {
                    questionsCollectionOldQuestions.setIdtopic(null);
                    questionsCollectionOldQuestions = em.merge(questionsCollectionOldQuestions);
                }
            }
            for (Questions questionsCollectionNewQuestions : questionsCollectionNew) {
                if (!questionsCollectionOld.contains(questionsCollectionNewQuestions)) {
                    Topics oldIdtopicOfQuestionsCollectionNewQuestions = questionsCollectionNewQuestions.getIdtopic();
                    questionsCollectionNewQuestions.setIdtopic(topics);
                    questionsCollectionNewQuestions = em.merge(questionsCollectionNewQuestions);
                    if (oldIdtopicOfQuestionsCollectionNewQuestions != null && !oldIdtopicOfQuestionsCollectionNewQuestions.equals(topics)) {
                        oldIdtopicOfQuestionsCollectionNewQuestions.getQuestionsCollection().remove(questionsCollectionNewQuestions);
                        oldIdtopicOfQuestionsCollectionNewQuestions = em.merge(oldIdtopicOfQuestionsCollectionNewQuestions);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                BigDecimal id = topics.getId();
                if (findTopics(id) == null) {
                    throw new NonexistentEntityException("The topics with id " + id + " no longer exists.");
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
            Topics topics;
            try {
                topics = em.getReference(Topics.class, id);
                topics.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The topics with id " + id + " no longer exists.", enfe);
            }
            Collection<Questions> questionsCollection = topics.getQuestionsCollection();
            for (Questions questionsCollectionQuestions : questionsCollection) {
                questionsCollectionQuestions.setIdtopic(null);
                questionsCollectionQuestions = em.merge(questionsCollectionQuestions);
            }
            em.remove(topics);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Topics> findTopicsEntities() {
        return findTopicsEntities(true, -1, -1);
    }

    public List<Topics> findTopicsEntities(int maxResults, int firstResult) {
        return findTopicsEntities(false, maxResults, firstResult);
    }

    private List<Topics> findTopicsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Topics.class));
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

    public Topics findTopics(BigDecimal id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Topics.class, id);
        } finally {
            em.close();
        }
    }

    public int getTopicsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Topics> rt = cq.from(Topics.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
