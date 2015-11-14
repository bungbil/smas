/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package de.forsthaus.webui.logging.loginlog.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Separator;

import de.forsthaus.backend.model.CountryCode;
import de.forsthaus.backend.model.Ip2Country;
import de.forsthaus.backend.model.LoginStatus;
import de.forsthaus.backend.model.SecLoginlog;
import de.forsthaus.backend.service.IpToCountryService;
import de.forsthaus.backend.service.LoginLoggingService;
import de.forsthaus.backend.service.SecurityService;
import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * Item renderer for listitems in the listbox.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class SecLoginlogListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SecLoginlogListModelItemRenderer.class);

	private transient LoginLoggingService loginLoggingService;
	private transient SecurityService securityService;
	private transient IpToCountryService ipToCountryService;

	public SecurityService getSecurityService() {
		if (this.securityService == null) {
			this.securityService = (SecurityService) SpringUtil.getBean("securityService");
			setSecurityService(this.securityService);
		}
		return this.securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public LoginLoggingService getLoginLoggingService() {
		if (this.loginLoggingService == null) {
			this.loginLoggingService = (LoginLoggingService) SpringUtil.getBean("loginLoggingService");
			setLoginLoggingService(this.loginLoggingService);
		}
		return this.loginLoggingService;
	}

	public void setLoginLoggingService(LoginLoggingService loginLoggingService) {
		this.loginLoggingService = loginLoggingService;
	}

	public void setIpToCountryService(IpToCountryService ipToCountryService) {
		this.ipToCountryService = ipToCountryService;
	}

	public IpToCountryService getIpToCountryService() {
		if (this.ipToCountryService == null) {
			this.ipToCountryService = (IpToCountryService) SpringUtil.getBean("ipToCountryService");
			setIpToCountryService(this.ipToCountryService);
		}
		return this.ipToCountryService;
	}

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final SecLoginlog log = (SecLoginlog) data;

		Listcell lc;
		final LoginStatus loginStatus = getLoginLoggingService().getTypById(log.getLglStatusid());

		lc = new Listcell(getDateTime(log.getLglLogtime()));

		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(log.getLglLoginname());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		lc = new Listcell(loginStatus.getStpTypname());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		

		/* Session-ID */
		lc = new Listcell(log.getLglSessionid());
		if (log.getLglStatusid() == 0) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		// ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

	}

	private String truncateIPForPrivacy(String remoteIp) {
		return StringUtils.substringBeforeLast(remoteIp, ".") + ".xxx";
	}

	/**
	 * Format the date/time. <br>
	 * 
	 * @return String of date/time
	 */
	private String getDateTime(Date date) {
		if (date != null) {
			return ZksampleDateFormat.getDateTimeLongFormater().format(date);
		}
		return "";
	}

}
