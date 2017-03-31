package com.knowarth.autocomplete.action;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

@Component(
	    configurationPolicy = ConfigurationPolicy.OPTIONAL,
	    immediate = true,
	    property = {
	        "javax.portlet.name=com_knowarth_autocomplete_search_AutocompletesearchPortlet"
	    },
	    service = ConfigurationAction.class
	)

public class AutoCompleteSearchConfiguration extends DefaultConfigurationAction {

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		PortletPreferences prefs = request.getPreferences();
		String blog = prefs.getValue("blog", "false");
		
		
		super.render(request, response);
	}
	
	@Override
	public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		Boolean announcement = ParamUtil.getBoolean(actionRequest, "announcement");
		String announcementUrl=ParamUtil.getString(actionRequest,"announcementUrl");
	    
		
		
		Boolean blog = ParamUtil.getBoolean(actionRequest, "blog");
		Boolean layout = ParamUtil.getBoolean(actionRequest, "layout");
		Boolean dlFileEntry = ParamUtil.getBoolean(actionRequest, "dlFileEntry");
		Boolean webContent = ParamUtil.getBoolean(actionRequest, "webContent");
		Boolean user = ParamUtil.getBoolean(actionRequest, "user");
		Boolean messageBoard = ParamUtil.getBoolean(actionRequest, "messageBoard");
		String messageBoardUrl = ParamUtil.getString(actionRequest, "messageBoardUrl");
		String wikiPageUrl=ParamUtil.getString(actionRequest,"wikiPageUrl");
	    Boolean wikiPage=ParamUtil.getBoolean(actionRequest,"wikiPage");
		
	    String blogUrl=ParamUtil.getString(actionRequest,"blogUrl");
	   
	    setPreference(actionRequest, "announcement", announcement.toString());
		
		setPreference(actionRequest, "blog", blog.toString());
		setPreference(actionRequest, "layout", layout.toString());
		setPreference(actionRequest, "dlFileEntry", dlFileEntry.toString());
		setPreference(actionRequest, "webContent", webContent.toString());
		setPreference(actionRequest, "user", user.toString());
		setPreference(actionRequest, "messageBoard", messageBoard.toString());
		setPreference(actionRequest, "messageBoardUrl", messageBoardUrl);
		setPreference(actionRequest,"wikiPage",wikiPage.toString());
		setPreference(actionRequest,"wikiPageUrl",wikiPageUrl.toString());
		setPreference(actionRequest,"blogUrl",blogUrl.toString());
		setPreference(actionRequest,"announcementUrl",announcementUrl.toString());
		
		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	
	private static Log _log = LogFactoryUtil.getLog(AutoCompleteSearchConfiguration.class);
	
}
