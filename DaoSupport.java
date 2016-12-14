package com.newer.dao.impl;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import utils.Pagination;

import com.newer.dao.BaseDao;

public class DaoSupport<T> implements BaseDao<T> {
	private HibernateTemplate hibernateTemplate;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	public void findByHQLpage(final String hql, final Object[] params,
			final Pagination<T> pagination) {
		// TODO Auto-generated method stub
		/**
		 * 查询带条件分页total
		 */
		String sql = "select count(*) " + hql;
		int total = ((Long) this.hibernateTemplate.iterate(sql, params).next())
				.intValue();
		pagination.setTotal(total);
		
		/**
		 * 查询带条件分页list
		 */
		this.hibernateTemplate.executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				// TODO Auto-generated method stub
				Query query = session.createQuery(hql).setFirstResult(
						(pagination.getCurrentPage() - 1)
								* pagination.getPageSize()).setMaxResults(
						pagination.getPageSize());
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						query.setParameter(i, params[i]);
					}
				}
				pagination.setRows(query.list());
				return null;
			}
		});
	}
}
