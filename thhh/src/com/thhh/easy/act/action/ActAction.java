package com.thhh.easy.act.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.thhh.easy.act.service.IActService;
import com.thhh.easy.entity.Act;
import com.thhh.easy.entity.Partici;
import com.thhh.easy.entity.Report;
import com.thhh.easy.entity.Users;
import com.thhh.easy.user.service.IUserService;
import com.thhh.easy.util.Constant;
import com.thhh.easy.util.MyUtil;

public class ActAction {

	private IUserService userService;
	private IActService actService;
	private int pageIndex; // 当前页数
	private int rowCount; // 每页显示条数

	private Act act; // 活动对象
	private Users users; // 用户对象
	private Partici partici; // 参与表对象
	private Report report; // 举报表对象

	/**
	 * [查看]该用户未参加的正在发起的活动,按照发起活动的时间降序排序
	 */
	public void findAct() {

		if (pageIndex == 0 || rowCount == 0) {
			setPageIndex(Constant.DEFAULT_PAGE);
			setRowCount(Constant.DEFAULT_PAGE_SIZE);
		}

		List<Act> listAct = actService.findAct(users.getId(), getPageIndex(),
				getRowCount());

		// List<Map<String, Object>> listMap = new ArrayList<Map<String,
		// Object>>();
		//
		// for (Act act : listAct) {
		//
		// Map<String, Object> map = new HashMap<String, Object>();
		// // map.put("id", act.getId());
		// // map.put("user.id", act.getUsers().getId());
		// map.put("user.name", act.getUsers().getName());
		// map.put("user_img", act.getUsers().getImage().getUrls());
		// // map.put("create.date",
		// // MyUtil.formatDate(act.getCreateDate(), "yyyy-MM-dd"));
		// map.put("start_date",
		// MyUtil.formatDate(act.getStartDate(), "yyyy-MM-dd"));
		// map.put("days", act.getDays());
		// map.put("theme", act.getTheme());
		// // map.put("state", act.getStates());
		// listMap.add(map);
		// }
		MyUtil.sendString(listAct);
	}

	/**
	 * 查看单个活动详情
	 * 
	 * @return
	 */
	public void findActDetail() {

		// 由前台传送的活动id查询该活动信息
		Act act2 = actService.findActDetail(act.getId());
		// 参加该活动的人数
		int count = actService.countPartici(act.getId());
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("id", act2.getId());
		// map.put("user.id", act2.getUsers().getId());
		// map.put("user.name", act2.getUsers().getName());
		// map.put("create.date",
		// MyUtil.formatDate(act2.getCreateDate(), "yyyy-MM-dd"));
		// map.put("theme", act2.getTheme());
		// map.put("account", act2.getAccount());
		// map.put("days", act2.getDays());
		// map.put("pay", act2.getPay());
		// map.put("state", act2.getStates());
		// map.put("contents", act2.getContents());
		// map.put("start.date",
		// MyUtil.formatDate(act2.getStartDate(), "yyyy-MM--dd"));

		// 将查询得到的数据传送到前台
		MyUtil.sendString(act2 + "；");

		MyUtil.sendString(count);

	}

	/**
	 * 参加活动
	 * 
	 * @return
	 */
	public void addAct() {

		// 当前是否有用户登录
		if (users.getId() == 0) {
			String msg1 = new String("not login；");
			MyUtil.sendString(msg1);
			// 未登录
			MyUtil.sendString(Constant.STRING_0);
		} else {

			// 判断用户是否参加过该活动
			String flag = actService
					.findParticiById(users.getId(), act.getId());

			// "1"表示在数据库中查询到记录，"0"表示没有在数据库中查到记录
			if ("1".equals(flag)) {
				String msg2 = new String("already add；");
				MyUtil.sendString(msg2);
				MyUtil.sendString(Constant.STRING_0);

			} else if ("0".equals(flag)) {

				// 向参与表插入一行数据
				actService.addAct(users.getId(), act.getId());

				// 向前台发送成功信息
				MyUtil.sendString(Constant.STRING_1);
			}

		}
	}

	/**
	 * 举报活动
	 * 
	 * @return
	 */
	public void reportAct() {

		// 查询该用户是否已经举报过该活动
		String flag = actService.findReportById(users.getId(), act.getId());

		// "1"表示在数据库中查询到记录，"0"表示没有在数据库中查到记录

		if ("1".equals(flag)) {

			String msg = new String(
					"The user has already reported the activity !!!");
			MyUtil.sendString(Constant.STRING_0);

		} else if ("0".equals(flag)) {

			// 向举报表插入一行数据
			actService.reportAct(users.getId(), act.getId(), users.getRp());

			// 向前台发送成功信息
			MyUtil.sendString(Constant.STRING_1);
		}

	}

	/**
	 * 发起活动
	 * 
	 * @return
	 */
	public void initAct() {

		final Act act2 = new Act();

		// if (act2 == null || act2.getUsers() == null
		// || act2.getUsers().getId() == null) {
		// MyUtil.sendString(Constant.STRING_0);
		// act2 = null;
		// return;
		// }
		act2.setUsers(actService.findUserById(6));
		// act2.setUsers(actService.findUserById(users.getId()));
		act2.setTheme("uffbbbbbbbbbbugg");
		act2.setAccount(14);
		act2.setPay(25f);
		act2.setDays(2);
		act2.setContents("endixuwei522");

		act2.setCreateDate(new Date());

		act2.setStartDate(new Date());

		act2.setStates("1");

		// 活动表插入数据
		actService.saveAct(act2);
		// 同时向参与表加入一行数据，类似级联操作
		actService.addAct(users.getId(), act2.getId());

		MyUtil.sendString(Constant.STRING_1);

		// 定时器。。。
		// 状态转为活动正在进行
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// 状态设置为2
				actService.underway(act2.getId());
			}
		}, (act2.getStartDate().getTime() - act2.getCreateDate().getTime())
				* 24 * 60 * 60 * 1000);
		// 状态转为活动完成
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// 状态设置为3
				actService.over(act2.getId());
			}
		}, act2.getDays() * 24 * 60 * 60 * 1000);

	}

	/**
	 * 取消活动
	 * 
	 * @return
	 */
	public void cancelAct() {
		// 判断是不是发起人在取消活动
		String flag = actService.checkCancel(users.getId(), act.getId());
		System.out.println(flag);
		if ("0".equals(flag)) {
			// 没查到表记录
			String msg = new String("No authority,can not cancel；");
			MyUtil.sendString(Constant.STRING_0);

		} else if ("1".equals(flag)) {
			// 修改字段，改变活动状态
			actService.cancelAct(act.getId());
			// 向前台发送成功信息
			MyUtil.sendString(Constant.STRING_1);
		}

	}

	/**
	 * 查询该用户发起的活动，和参与的活动，按照活动发起时间排序
	 * 
	 * @return
	 */
	public void findAllAct() {

		// if (act == null || act.getUsers() == null
		// || act.getUsers().getId() == null) {
		// MyUtil.sendString(Constant.STRING_0);
		// act = null;
		// return;
		// }

		if (pageIndex == 0 || rowCount == 0) {
			setPageIndex(Constant.DEFAULT_PAGE);
			setRowCount(Constant.DEFAULT_PAGE_SIZE);
		}

		List<Act> listAct = actService.allPartici(users.getId(), pageIndex,
				rowCount);

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

		for (Act act : listAct) {

			Map<String, Object> map = new HashMap<String, Object>();
			// map.put("id", act.getId());
			// map.put("user.id", act.getUsers().getId());
			map.put("user.name", act.getUsers().getName());
			map.put("create.date",
					MyUtil.formatDate(act.getCreateDate(), "yyyy-MM-dd"));
			map.put("theme", act.getTheme());
			map.put("state", act.getStates());
			listMap.add(map);
		}
		System.out.println(listMap);
		MyUtil.sendString(listMap.toArray());
	}

	public IActService getActService() {
		return actService;
	}

	public void setActService(IActService actService) {
		this.actService = actService;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public Act getAct() {
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Partici getPartici() {
		return partici;
	}

	public void setPartici(Partici partici) {
		this.partici = partici;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

}