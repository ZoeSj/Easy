package com.thhh.easy.dao.imp;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.thhh.easy.dao.IActDao;
import com.thhh.easy.dao.IParticiDao;
import com.thhh.easy.entity.Act;
import com.thhh.easy.entity.Partici;
import com.thhh.easy.entity.Users;

/**
 * A data access object (DAO) providing persistence and search support for
 * Partici entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.thhh.easy.entity.Partici
 * @author MyEclipse Persistence Tools
 */
public class ParticiDAO extends HibernateDaoSupport implements IParticiDao {
	private static final Logger log = LoggerFactory.getLogger(ParticiDAO.class);

	// property constants

	protected void initDao() {
		// do nothing
	}

	/**
	 * 参加活动
	 * 
	 * @return
	 */
	public void insertPartici(Act act, Users users) {

		Session session = this.getHibernateTemplate().getSessionFactory()
				.openSession();
		Partici partici = new Partici();

		partici.setUsers(users);
		partici.setAct(act);

		session.save(partici);
		session.beginTransaction().commit();

		// Transaction tx = session.beginTransaction();
		// session.save(act);
		// tx.commit();

		// session.close();

	}

	/**
	 * 查询该用户已经参加了的活动列表
	 */
	public List<Act> allPartici(int userid, int pageIndex, int rowCount) {

		// 查询该用户参与的活动id, 根据活动id得到活动实体
		Session session = getSession();
		Query query = session
				.createQuery("from Act where id in(select act_id from Partici where users_id = ?)");
		query.setInteger(0, userid);

		// 分页
		int startRow = (pageIndex - 1) * rowCount;
		query.setFirstResult(startRow);
		query.setMaxResults(rowCount);

		query.executeUpdate();

		List<Act> actList = query.list();

		session.close();
		return actList;
	}

	public static Logger getLog() {
		return log;
	}

	public static IActDao getFromApplicationContext(ApplicationContext ctx) {
		return (IActDao) ctx.getBean("ActDAO");
	}

}