package com.korres;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.korres.entity.Admin;
import com.korres.service.AdminService;
import com.korres.service.CaptchaService;
import com.korres.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/*
 * 类名：AuthenticationRealm.java
 * 功能说明：令牌
 * 创建日期：2018-12-14 下午04:05:16
 * 作者：liuxicai
 * 版权：yanhaoIt
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public class AuthenticationRealm extends AuthorizingRealm {

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	protected AuthenticationInfo doGetAuthenticationInfo(
			org.apache.shiro.authc.AuthenticationToken token) {
		AuthenticationToken authenticationToken = (AuthenticationToken) token;
		String username = authenticationToken.getUsername();
		String password = new String(authenticationToken.getPassword());
		String captchaId = authenticationToken.getCaptchaId();
		String captcha = authenticationToken.getCaptcha();
		String host = authenticationToken.getHost();
		if (!this.captchaService.isValid(Setting.CaptchaType.adminLogin,
				captchaId, captcha))
			throw new UnsupportedTokenException();
		if ((username != null) && (password != null)) {
			Admin admin = this.adminService.findByUsername(username);
			if (admin == null){
				throw new UnknownAccountException();
			}
			
			if (!admin.getIsEnabled().booleanValue()){
				throw new DisabledAccountException();
			}
			
			Setting setting = SettingUtils.get();
			int i;
			if (admin.getIsLocked().booleanValue())
				if (ArrayUtils.contains(setting.getAccountLockTypes(),
						Setting.AccountLockType.admin)) {
					i = setting.getAccountLockTime().intValue();
					if (i == 0){
						throw new LockedAccountException();
					}
					
					Date lockedDate = admin.getLockedDate();
					Date localDate2 = DateUtils.addMinutes(lockedDate, i);
					if (new Date().after(localDate2)) {
						admin.setLoginFailureCount(Integer.valueOf(0));
						admin.setIsLocked(Boolean.valueOf(false));
						admin.setLockedDate(null);
						this.adminService.update(admin);
					} else {
						throw new LockedAccountException();
					}
				} else {
					admin.setLoginFailureCount(Integer.valueOf(0));
					admin.setIsLocked(Boolean.valueOf(false));
					admin.setLockedDate(null);
					this.adminService.update(admin);
				}
			if (!DigestUtils.md5Hex(password).equals(admin.getPassword())) {
				i = admin.getLoginFailureCount().intValue() + 1;
				if (i >= setting.getAccountLockCount().intValue()) {
					admin.setIsLocked(Boolean.valueOf(true));
					admin.setLockedDate(new Date());
				}
				admin.setLoginFailureCount(Integer.valueOf(i));
				this.adminService.update(admin);
				throw new IncorrectCredentialsException();
			}
			admin.setLoginIp(host);
			admin.setLoginDate(new Date());
			admin.setLoginFailureCount(Integer.valueOf(0));
			this.adminService.update(admin);
			return new SimpleAuthenticationInfo(new Principal(admin.getId(),
					username), password, getName());
		}
		throw new UnknownAccountException();
	}

	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		Principal principal = (Principal) principals.fromRealm(getName())
				.iterator().next();
		if (principal != null) {
			List<String> list = this.adminService.findAuthorities(principal.getId());
			if (list != null) {
				SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
				simpleAuthorizationInfo.addStringPermissions(list);
				return simpleAuthorizationInfo;
			}
		}
		return null;
	}
}