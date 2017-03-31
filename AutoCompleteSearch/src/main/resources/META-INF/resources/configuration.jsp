<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ include file="init.jsp" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>


<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<%-- <liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" /> --%>

<br/>
<h1><liferay-ui:message key="select.values"/></h1><br>


<aui:form action="<%= configurationActionURL %>" method="post" name="configurationFm">

    <aui:input name="<%= Constants.CMD %>" type="hidden"
        value="<%= Constants.UPDATE %>" />
    <%-- <aui:input name="redirect" type="hidden"
        value="<%= configurationRenderURL %>" /> --%>

    <aui:fieldset>
    
    <aui:input label="label.announcement" name="announcement" type="checkbox" 
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("announcement","false"))==false)? false: true %>"  />
    	<div class="divAnnouncement" style="display: none;">
    		<label for="<portlet:namespace />announcementLabel"><liferay-ui:message key="label.announcement" /> <liferay-ui:icon-help message='announcement-url-msg' /></label>
		
		<aui:input name="announcementUrl" type="text" label="" value="<%=GetterUtil.getString(portletPreferences.getValue("announcementUrl", "")) %>" >
			 <aui:validator name="required">
                function() {
                        return AUI().one('#<portlet:namespace />announcement').get('checked');
                }
        </aui:validator>
		</aui:input>
    	
    </div>
    	<aui:input label="label.blog" name="blog" type="checkbox"
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("blog","false"))==false)? false: true %>"  />
    	<div class="divBlog" style="display: none;">
    	
    		<label for="<portlet:namespace />blogLabel"><liferay-ui:message key="label.blog" /> <liferay-ui:icon-help message='blog-url-msg' /></label>
		
		<aui:input name="blogUrl" type="text" label="" value="<%=GetterUtil.getString(portletPreferences.getValue("blogUrl", "")) %>" >
			 <aui:validator name="required">
                function() {
                        return AUI().one('#<portlet:namespace />blog').get('checked');
                }
        </aui:validator>
		</aui:input>
    </div>	
    		
    		
    	<aui:input label="label.layout" name="layout" type="checkbox" 
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("layout","false"))==false)? false: true %>"  />
    	<aui:input label="label.dlFileEntry" name="dlFileEntry" type="checkbox"
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("dlFileEntry","false"))==false)? false: true %>"  />
    	<aui:input label="label.webContent" name="webContent" type="checkbox" 
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("webContent","false"))==false)? false: true %>" />
    	<aui:input label="label.user" name="user" type="checkbox" 
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("user","false"))==false)? false: true %>" />
    	<aui:input label="label.messageBoard" name="messageBoard" type="checkbox" 
    		checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("messageBoard","false"))==false)? false: true %>" />	
		<div class="divMessageBoard" style="display: none;">
		<label for="<portlet:namespace />messageBoardLabel"><liferay-ui:message key="label.messageBoard" /> <liferay-ui:icon-help message='messageboard-url-msg' /></label>
		
		<aui:input name="messageBoardUrl" type="text" label="" value="<%=GetterUtil.getString(portletPreferences.getValue("messageBoardUrl", "")) %>" >
			 <aui:validator name="required">
                function() {
                        return AUI().one('#<portlet:namespace />messageBoard').get('checked');
                }
        </aui:validator>
		</aui:input>
</div>
		<aui:input label="label.wikiPage" name="wikiPage" type="checkbox" 
	      checked="<%=(GetterUtil.getBoolean(portletPreferences.getValue("wikiPage","false"))==false)? false: true %>" /> 
	  
	<div class="divWiki" style="display: none;">
		  <label for="<portlet:namespace />wikiPageLabel"><liferay-ui:message key="label.wikiPage" /> <liferay-ui:icon-help message='wiki-page-url-msg' /></label>
		  
		  <aui:input name="wikiPageUrl" type="text" label="" value="<%=GetterUtil.getString(portletPreferences.getValue("wikiPageUrl", "")) %>" >
		    <aui:validator name="required">
		                function() {
		                        return AUI().one('#<portlet:namespace />wikiPage').get('checked');
		                }
		        </aui:validator>
		  </aui:input>
	 </div>
	    </aui:fieldset>
    <aui:button-row>
        <aui:button type="submit"></aui:button>
    </aui:button-row>
</aui:form>
<aui:script>
  AUI().ready('aui-module', function(A){
	  
		  /* var mbstatus = A.one("#<portlet:namespace/>messageBoard").is(":checked"); */
		  var mbstatus = A.one("#<portlet:namespace/>messageBoard").get('checked');
		  var wikistatus = A.one("#<portlet:namespace/>wikiPage").get('checked');
		  var announcementstatus = A.one("#<portlet:namespace/>announcement").get('checked');
		  var blogstatus = A.one("#<portlet:namespace/>blog").get('checked');
			
		  if(mbstatus == true){
			  A.one(".divMessageBoard").setStyle("display","block");
		  }
		  if(wikistatus == true){
			  A.one(".divWiki").setStyle("display","block");
		  }
		  if(announcementstatus == true){
			  A.one(".divAnnouncement").setStyle("display","block");
		  } if(blogstatus == true){
			  A.one(".divBlog").setStyle("display","block");
		  }
	});
  AUI().use('aui-base',function(A) {
	  A.one("#<portlet:namespace/>announcement").on('change',function(){
			 if(this.get('checked') == true) {
				 A.one(".divAnnouncement").setStyle("display","block");
			 }else{
				 A.one(".divAnnouncement").setStyle("display","none");
			 }
		});
	  A.one("#<portlet:namespace/>wikiPage").on('change',function(){
			 if(this.get('checked') == true) {
				 A.one(".divWiki").setStyle("display","block");
			 }else{
				 A.one(".divWiki").setStyle("display","none");
			 }
		});
		
	  A.one("#<portlet:namespace/>messageBoard").on('change',function(){
			 if(this.get('checked') == true) {
				 A.one(".divMessageBoard").setStyle("display","block");
			 }else{
				 A.one(".divMessageBoard").setStyle("display","none");
			 }
		});
		
	  A.one("#<portlet:namespace/>blog").on('change',function(){
			 if(this.get('checked') == true) {
				 A.one(".divBlog").setStyle("display","block");
			 }else{
				 A.one(".divBlog").setStyle("display","none");
			 }
		});
  });
</aui:script>
