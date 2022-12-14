package com.platform.mvc.user;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.platform.annotation.Controller;
import com.platform.dto.ZtreeNode;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.dept.Department;
import com.platform.mvc.station.Station;
import com.platform.mvc.upload.Upload;
import com.platform.mvc.upload.UploadService;

/**
 * 用户管理
 */
@Controller("/platform/user")
public class UserController extends BaseController {

	@SuppressWarnings("unused")
	private static final Log log = Log.getLog(UserController.class);
	
	private UserService userService;
	private UploadService uploadService;
	
	/**
	 * 默认列表
	 */
	public void index() {
		paging(splitPage, User.sqlId_splitPageSelect, User.sqlId_splitPageFrom);
		render("/platform/user/list.html");
	}

	/**
	 * 验证账号是否可用
	 */
	public void valiUserName(){
		String userIds = getPara("userIds");
		String userName = getPara("userName");
		boolean bool = userService.valiUserName(userIds, userName);
		renderText(String.valueOf(bool));
	}
	
	/**
	 * 验证邮箱是否可用
	 */
	public void valiMailBox(){
		String userInfoIds = getPara("userInfoIds");
		String mailBox = getPara("mailBox");
		boolean bool = userService.valiMailBox(userInfoIds, mailBox);
		renderText(String.valueOf(bool));
	}

	/**
	 * 验证身份证是否可用
	 */
	public void valiIdcard(){
		String userInfoIds = getPara("userInfoIds");
		String idcard = getPara("idcard");
		boolean bool = userService.valiIdcard(userInfoIds, idcard);
		renderText(String.valueOf(bool));
	}

	/**
	 * 验证手机号是否可用
	 */
	public void valiMobile(){
		String userInfoIds = getPara("userInfoIds");
		String mobile = getPara("mobile");
		boolean bool = userService.valiMobile(userInfoIds, mobile);
		renderText(String.valueOf(bool));
	}

	/**
	 * 保存新增用户
	 */
	@Before(UserValidator.class)
	public void save() {
		String ids = getAttr("ids");
		String password = getPara("password");
		String privateKey = getCUser().getRsaprivate(); // 当前登录用户私钥
		
		User user = getModel(User.class);
		UserInfo userInfo = getModel(UserInfo.class);
		
		userService.save(ids, user, password, userInfo, privateKey);

		forwardAction("/platform/user/backOff");
	}
	
	/**
	 * 准备更新
	 */
	public void edit() {
		User user = User.dao.findById(getPara());
		setAttr("user", user);
		setAttr("userInfo", UserInfo.dao.findById(user.getPKValue()));
		setAttr("station", Station.dao.findById(user.getStationids()));
		setAttr("dept", Department.dao.findById(user.getDepartmentids()));
		setAttr("upload", Upload.dao.findById(user.getPKValue()));
		render("/platform/user/update.html");
	}
	
	/**
	 * 更新
	 */
	@Before(UserValidator.class)
	public void update() {
		String password = getPara("password");
		String privateKey = getCUser().getRsaprivate(); // 当前登录用户私钥
		User user = getModel(User.class);
		UserInfo userInfo = getModel(UserInfo.class);
		
		UploadFile file = getAttr("file");
		if(file != null){
			// 删除旧LOGO
			Upload.dao.deleteById(user.getPKValue());
			
			// 存入新LOGO
			uploadService.upload("webRoot", file, user.getPKValue());
		}
		
		userService.update(user, password, userInfo, privateKey);
		forwardAction("/platform/user/backOff");
	}

	/**
	 * 查看
	 */
	public void view() {
		User user = User.dao.findById(getPara());
		setAttr("user", user);
		setAttr("userInfo", user.getUserInfo());
		setAttr("station", Station.dao.findById(user.getStationids()));
		setAttr("dept", Department.dao.findById(user.getDepartmentids()));
		setAttr("upload", Upload.dao.findById(user.getPKValue()));
		render("/platform/user/view.html");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		userService.delete(getPara() == null ? ids : getPara());
		forwardAction("/platform/user/backOff");
	}

	/**
	 * 用户树ztree节点数据
	 */
	public void treeData() {
		List<ZtreeNode> list = userService.childNodeData(getCxt(), getPara("deptIds"));
		renderJson(list);
	}
	
	/**
	 * 验证旧密码是否正确
	 */
	public void valiPassWord(){
		User user = getCUser();
		String passWord = getPara("passWord");
		boolean bool = userService.valiPassWord(user, passWord);
		renderText(String.valueOf(bool));
	}
	
	/**
	 * 密码变更
	 */
	public void passChange(){
		User user = getCUser();
		String passOld = getPara("passOld");
		String passNew = getPara("passNew");
		boolean bool = userService.passChange(user, passOld, passNew);
		renderText(String.valueOf(bool));
	}
	
}


