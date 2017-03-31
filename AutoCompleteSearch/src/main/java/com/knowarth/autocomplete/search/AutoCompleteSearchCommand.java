package com.knowarth.autocomplete.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalServiceUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.service.MBMessageLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

@Component(immediate = true, property = { "javax.portlet.name=com_knowarth_autocomplete_search_AutocompletesearchPortlet",
"mvc.command.name=searchContent" }, service = MVCResourceCommand.class)
public class AutoCompleteSearchCommand implements MVCResourceCommand{

	private static final String AUTO_COMPLETE = "autoComplete";
	private static final Log log = LogFactoryUtil.getLog(AutoCompleteSearchCommand.class);
	
	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String cmd = ParamUtil.getString(resourceRequest, "cmd");
		String searchKey = ParamUtil.getString(resourceRequest, "searchKey");

		Locale locale = resourceRequest.getLocale();
		HttpServletRequest request = PortalUtil.getHttpServletRequest(resourceRequest);
		long companyId = PortalUtil.getCompanyId(request);
		long groupId = 0L;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
		} catch (PortalException e) {
			e.printStackTrace();
		}

		PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();
		PortletPreferences prefs = resourceRequest.getPreferences();
		
		if (cmd.equalsIgnoreCase(AUTO_COMPLETE) && cmd.length() > 0) {

			// Asset Entries Searching
			DynamicQuery assetEntryQuery = DynamicQueryFactoryUtil.forClass(AssetEntry.class,
					PortalClassLoaderUtil.getClassLoader());
			Junction junction = RestrictionsFactoryUtil.conjunction();

			Property propertyG = PropertyFactoryUtil.forName("groupId");
			junction.add(propertyG.eq(groupId));

			Property propertyC = PropertyFactoryUtil.forName("companyId");
			junction.add(propertyC.eq(companyId));

			Junction junctionD = RestrictionsFactoryUtil.disjunction();
			
			Property propertyT = PropertyFactoryUtil.forName("title");
			junctionD.add(propertyT.like(StringPool.PERCENT.concat(searchKey.toLowerCase()).concat(StringPool.PERCENT)));
			junctionD.add(propertyT.like(StringPool.PERCENT.concat(searchKey.toUpperCase()).concat(StringPool.PERCENT)));
			junctionD.add(propertyT.like(StringPool.PERCENT.concat(searchKey).concat(StringPool.PERCENT)));
			long classNameIdLayout = ClassNameLocalServiceUtil.getClassNameId(Layout.class);
			long classNameIdDLFE = ClassNameLocalServiceUtil.getClassNameId(DLFileEntry.class);
			long classNameIdJA = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class);
			long classNameIdBE = ClassNameLocalServiceUtil.getClassNameId(BlogsEntry.class);
			long classNameIdMB = ClassNameLocalServiceUtil.getClassNameId(MBMessage.class);
			long classNameIdWK = ClassNameLocalServiceUtil.getClassNameId(WikiPage.class);

			Property property = PropertyFactoryUtil.forName("classNameId");
			if (prefs.getValue("layout", "false").equals("true")) {
				junctionD.add(property.eq(classNameIdLayout));
			}
			if(prefs.getValue("dlFileEntry", "false").equals("true")){
				junctionD.add(property.eq(classNameIdDLFE));
		   } 
		   
		   if(prefs.getValue("webContent", "false").equals("true")){
			   junctionD.add(property.eq(classNameIdJA));
		   } 
		   
		   if(prefs.getValue("blog", "false").equals("true")){
			   junctionD.add(property.eq(classNameIdBE));
		   }
		   if(prefs.getValue("messageBoard", "false").equals("true")){
			   junctionD.add(property.eq(classNameIdMB));
		   }
		   if(prefs.getValue("wikiPage", "false").equals("true")){
			   junctionD.add(property.eq(classNameIdWK));
		   }
			
			assetEntryQuery.add(junction);
			assetEntryQuery.add(junctionD);

			List<AssetEntry> assetEntries = AssetEntryLocalServiceUtil.dynamicQuery(assetEntryQuery);
			log.info("Assets = "+assetEntries);
			
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
			for (AssetEntry assetEntry : assetEntries) {

				long classPK = assetEntry.getClassPK();

				if (assetEntry.getClassName().equals(Layout.class.getName())) {

					if (hasPermission(groupId, permissionChecker, Layout.class.getName(), classPK)) {
						try {
							Layout layout = LayoutLocalServiceUtil.getLayout(classPK);
							JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
							jsonObject.put("title", layout.getName(locale));
							jsonObject.put("url", PortalUtil.getLayoutURL(layout, themeDisplay));
							jsonObject.put("type", "layout");
							jsonArray.put(jsonObject);
						} catch (PortalException e) {

						}
					} else {
						// System.out.println("No Perssion =" + classPK);
					}

				} else if (assetEntry.getClassName().equals(DLFileEntry.class.getName())) {
					if (hasPermission(groupId, permissionChecker, DLFileEntry.class.getName(), classPK)) {
						try {

							FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(classPK);
							FileVersion fileVersion = fileEntry.getFileVersion();
							JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
							jsonObject.put("title", fileEntry.getTitle());
							jsonObject.put("url",
									DLUtil.getPreviewURL(fileEntry, fileVersion, themeDisplay, "", true, true));
							jsonObject.put("type", "dlFileEntry");
							jsonArray.put(jsonObject);

						} catch (PortalException e) {

						}
					}
				} else if (assetEntry.getClassName().equals(JournalArticle.class.getName())) {

					if (hasPermission(groupId, permissionChecker, JournalArticle.class.getName(), classPK)) {
						try {

							JournalArticleResource journalArticleResource = JournalArticleResourceLocalServiceUtil
									.getJournalArticleResource(classPK);
							JournalArticle journalArticle = JournalArticleLocalServiceUtil
									.getLatestArticle(journalArticleResource.getResourcePrimKey());
							List<Long> hitLayoutIds = new ArrayList<>();
							List<Long> hitLayoutIdsPublic = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId,
									false, journalArticle.getArticleId());
							hitLayoutIds.addAll(hitLayoutIdsPublic);
							if (themeDisplay.isSignedIn()) {
								List<Long> hitLayoutIdsPrivate = JournalContentSearchLocalServiceUtil
										.getLayoutIds(groupId, true, journalArticle.getArticleId());
								hitLayoutIds.addAll(hitLayoutIdsPrivate);
							}
							if (!hitLayoutIds.isEmpty()) {
								Long hitLayoutId = hitLayoutIds.get(0);
								Layout hitLayout = LayoutLocalServiceUtil.getLayout(groupId, false,
										hitLayoutId.longValue());
								if (themeDisplay.isSignedIn() && hitLayoutIdsPublic.size() == 0) {
									hitLayout = LayoutLocalServiceUtil.getLayout(groupId, true,
											hitLayoutId.longValue());
								}
								String layoutUrl = PortalUtil.getLayoutURL(hitLayout, themeDisplay);
								JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
								jsonObject.put("title", journalArticle.getTitle(locale));
								jsonObject.put("url", layoutUrl);
								jsonObject.put("type", "journalArticle");
								jsonArray.put(jsonObject);
							}

						} catch (PortalException e) {

						}
					}
				} else if (assetEntry.getClassName().equals(BlogsEntry.class.getName())) {

					if (hasPermission(groupId, permissionChecker, BlogsEntry.class.getName(), classPK)) {
						try {
							BlogsEntry blogsEntry = BlogsEntryLocalServiceUtil.getBlogsEntry(classPK);
							JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
							jsonObject.put("title", blogsEntry.getTitle());
							jsonObject.put("url",
									prefs.getValue("blogUrl", "") + StringPool.SLASH.concat(StringPool.DASH)
											.concat(StringPool.SLASH).concat("blogs").concat(StringPool.SLASH)
											.concat(blogsEntry.getUrlTitle()));
							jsonObject.put("type", "blogEntry");
							jsonArray.put(jsonObject);
						} catch (PortalException e) {

						}
					}
				} else if (assetEntry.getClassName().equals(MBMessage.class.getName())) {

					if (hasPermission(groupId, permissionChecker, MBMessage.class.getName(), classPK)) {
						try {

							String portalUrl = PortalUtil.getPortalURL(request);
							MBMessage mbMessage = MBMessageLocalServiceUtil.getMBMessage(classPK);
							JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
							jsonObject.put("title", mbMessage.getSubject());
							jsonObject.put("url", portalUrl + prefs.getValue("messageBoardUrl", "")
									+ "/-/message_boards/message/" + mbMessage.getMessageId());
							jsonObject.put("type", "messageBoard");
							jsonArray.put(jsonObject);
						} catch (PortalException e) {

						}
					}
				}  else if (assetEntry.getClassName().equals(WikiPage.class.getName())) {

					if (hasPermission(groupId, permissionChecker, WikiPage.class.getName(), classPK)) {
						try {

							String portalUrl = PortalUtil.getPortalURL(request);
							WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(classPK);
							WikiNode node = wikiPage.getNode();
							JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
							jsonObject.put("title", wikiPage.getTitle());
							jsonObject.put("url", portalUrl + prefs.getValue("wikiPageUrl", "") + "/-/wiki/"
									+ wikiPage.getNode().getName() + StringPool.SLASH + wikiPage.getTitle());
							jsonObject.put("type", "wikiPage");
							jsonArray.put(jsonObject);
						} catch (PortalException e) {

						}
					}
				}

			}

			// User Searching
			if(prefs.getValue("user", "false").equals("true")){
					DynamicQuery _assetEntryQuery = DynamicQueryFactoryUtil.forClass(AssetEntry.class,
					PortalClassLoaderUtil.getClassLoader());
			
					Junction _junction = RestrictionsFactoryUtil.conjunction();
		
					Property _propertyC = PropertyFactoryUtil.forName("companyId");
					_junction.add(_propertyC.eq(companyId));
		
					Property _propertyT = PropertyFactoryUtil.forName("title");
					_junction.add(
							_propertyT.like(StringPool.PERCENT.concat(searchKey.toLowerCase()).concat(StringPool.PERCENT)));
		
					long classNameIdU = ClassNameLocalServiceUtil.getClassNameId(User.class);
					Property _property = PropertyFactoryUtil.forName("classNameId");
					_junction.add(_property.eq(classNameIdU));
		
					assetEntryQuery.add(junction);
		
					List<AssetEntry> _assetEntries = AssetEntryLocalServiceUtil.dynamicQuery(_assetEntryQuery);
					for (AssetEntry assetEntry : _assetEntries) {
						long classPK = assetEntry.getClassPK();
						if (assetEntry.getClassName().equals(User.class.getName())) {
							try {
		
								User user = UserLocalServiceUtil.getUser(classPK);
								JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
								jsonObject.put("title", user.getFullName());
								jsonObject.put("url",
										StringPool.SLASH.concat("web").concat(StringPool.SLASH).concat(user.getScreenName()));
								jsonObject.put("type", "user");
								jsonArray.put(jsonObject);
							} catch (PortalException e) {
		
							}
		
						}
					}
			}
			// Announcement Search
			if (prefs.getValue("announcement", "false").equals("true")) {
				DynamicQuery _announcementQuery = DynamicQueryFactoryUtil.forClass(AnnouncementsEntry.class,
					PortalClassLoaderUtil.getClassLoader());
			
				Junction _announcementJunction = RestrictionsFactoryUtil.conjunction();
	
				Property _announcementPropertyC = PropertyFactoryUtil.forName("companyId");
				_announcementJunction.add(_announcementPropertyC.eq(companyId));
	
				Property __announcementPropertyT = PropertyFactoryUtil.forName("title");
				_announcementJunction.add(__announcementPropertyT
						.like(StringPool.PERCENT.concat(searchKey.toLowerCase()).concat(StringPool.PERCENT)));
	
				_announcementQuery.add(_announcementJunction);
				List<AnnouncementsEntry> __announcementEntries = new ArrayList<AnnouncementsEntry>();
				__announcementEntries = AnnouncementsEntryLocalServiceUtil.dynamicQuery(_announcementQuery);
				for (AnnouncementsEntry announcementEntry : __announcementEntries) {
					log.info(prefs.getValue("announcement", "false").equals("true"));
					
						JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
						jsonObject.put("title", announcementEntry.getTitle());
						jsonObject.put("url", prefs.getValue("announcementUrl", ""));
						jsonObject.put("type", "announcement");
						jsonArray.put(jsonObject);
	
					
				}
			}

			try {
				resourceResponse.getWriter().write(jsonArray.toString());
			} catch (IOException e) {
				_log.error("IOException Resource not generated");
			}

		}

		return true;
	}
	
	private static Log _log = LogFactoryUtil.getLog(AutoCompleteSearchCommand.class);

	private boolean hasPermission(long groupId, PermissionChecker permissionChecker, String className,
			long primaryKey) {
		return permissionChecker.hasPermission(groupId, className, primaryKey, ActionKeys.VIEW);
	}

}
