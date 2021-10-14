package ru.itmo.soa.app.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.itmo.soa.app.datasource.HibernateDatasource;
import ru.itmo.soa.app.entity.HumanBeing;
import ru.itmo.soa.app.entity.Team;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

public class TeamDao {

    public List<Team> getTeams() {
        Transaction transaction = null;
        List<Team> teams = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            teams = session.createQuery("SELECT a FROM Team a", Team.class).getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return teams;
    }

    public Optional<Team> getTeam(long id) {
        Transaction transaction = null;
        Team team = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            team = session.find(Team.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return Optional.ofNullable(team);
    }

    public Optional<HumanBeing> getHuman(long id) {
        Transaction transaction = null;
        HumanBeing humanBeing = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            humanBeing = session.find(HumanBeing.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return Optional.ofNullable(humanBeing);
    }

    public List<Team> findTeamsByHumanId(long id) {
        Transaction transaction = null;
        List<Team> teams = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Team> cr = cb.createQuery(Team.class);
            Root<Team> root = cr.from(Team.class);
            Join<Team, HumanBeing> join = root.join("humans", JoinType.LEFT);

            CriteriaQuery<Team> query = cr.select(root).where(cb.equal(join.get("id"), id));
            Query<Team> typedQuery = session.createQuery(query);

            teams = typedQuery.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return teams;
    }

    public Team createTeam(Team team) {
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(team);
            transaction.commit();
            return team;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void updateTeam(Team team) {
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(team);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public boolean deleteTeam(long id) {
        Transaction transaction = null;
        boolean successful = false;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Team team = session.find(Team.class, id);
            if (team != null) {
                session.delete(team);
                session.flush();
                successful = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return successful;
    }
}
