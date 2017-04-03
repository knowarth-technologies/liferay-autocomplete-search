<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="javax.portlet.PortletMode"%>
<%@page import="javax.portlet.PortletURL"%>
<%@ include file="init.jsp" %>

<%
PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("mvcPath", "/search.jsp");
portletURL.setParameter("redirect", currentURL);
portletURL.setPortletMode(PortletMode.VIEW);
portletURL.setWindowState(WindowState.MAXIMIZED);

pageContext.setAttribute("portletURL", portletURL);
%>

<aui:form action="<%= portletURL %>" method="get" name="fm" onSubmit='<%= renderResponse.getNamespace() + "search(); event.preventDefault();" %>'>

	<aui:fieldset>
		<aui:input cssClass="search-input" inlineField="<%= true %>" label='<%=StringPool.BLANK %>' id="keywords" name="keywords" placeholder="search" size="30" title="search" type="text"  />
		<aui:input name="urlToBeSaved" type="hidden" />
		<aui:field-wrapper inlineField="<%= true %>">
			<liferay-ui:icon cssClass="icon-monospaced search-image" icon="search" markupView="lexicon" onClick='<%= renderResponse.getNamespace() + "search();" %>' url="javascript:;" />
		</aui:field-wrapper>
		
	</aui:fieldset>
	
	<portlet:resourceURL id="searchContent" var="searchResourceURL">
	</portlet:resourceURL>

	<aui:script>
	AUI().use('array-extras','autocomplete-list','aui-base','aui-io-request','autocomplete-filters','autocomplete-highlighters',function (A) {
		var autoCompleteData;
		var autoComplete = new A.AutoCompleteList({
								inputNode: '#<portlet:namespace/>keywords',
								render: 'true',
								scrollIntoView: true,
								queryDelay: 500, 
								
								on: {
									select: function(item) {
										
										var result = item.result.raw;
										console.log(item);
										if(result.type !== 'header'){
											A.one('#<portlet:namespace/>keywords').val(result.title);
											window.location.href = result.url;
										}else{
											item.preventDefault();
											item.stopPropagation();
										}
									},
									visibleChange: function(item){
										 
										A.all(".result-header").each(function(node){
											node.ancestor('li').setStyle('color','#fff');
											node.ancestor('li').setStyle('background','#365d9e'); 
										}); 
										/* A.all(".result-header").ancestor('li').setStyle('color','#fff');
										A.all(".result-header").ancestor('li').setStyle('background','#365d9e'); */
									}
								},
								
								source:function(){
									
									var inputValue=A.one('#<portlet:namespace/>keywords').val();
									var myAjaxRequest=A.io.request('<%=searchResourceURL%>',{
														dataType: 'json',
														sync:true,
														method:'POST',
														data:{
															<portlet:namespace/>searchKey:inputValue,
															<portlet:namespace/>cmd:"autoComplete"
														},
														autoLoad:false,
														on: {
															success:function(){
																var data=this.get('responseData');
																autoCompleteData=data;
														}}
												});
									myAjaxRequest.start();
									return autoCompleteData;
								},
								resultHighlighter: function(query, results){
									
								var finalResults = [];
									return A.Array.map(results, function (result) {
										
									    return A.Highlight.all(result.text, query);
									});
								},
								resultFilters: function(query, results){
									
									query = query.toLowerCase();
									var results = A.Array.filter(results, function (result) {
										return result.text.toLowerCase().indexOf(query) !== -1;	
									});
									
									var finalResult = [];
									
									var resultsUser = [];
									var resultsDLFileEntry = [];
									var resultsLayout = [];
									var resultsBlogEntry = [];
									var resultsJournalArticle = [];
									var resultsMessageBoard=[];
									var resultsWikiPage= [];
									var resultsAnnouncementEntry= [];
									
									
									resultsUser.push({display : 'Users', highlighted : 'Users', raw:{title : 'Users', type: 'header',url: ''}, text: 'Users'});
									resultsDLFileEntry.push({display : 'Files', highlighted : 'Files', raw:{title : 'Files',type: 'header',url: ''}, text: 'Files'});
									resultsLayout.push({display : 'Pages', highlighted : 'Pages', raw:{title : 'Pages', type: 'header',url: ''}, text: 'Pages'});
									resultsBlogEntry.push({display : 'Blogs', highlighted : 'Blogs', raw:{title : 'Blogs', type: 'header',url: ''}, text: 'Blogs'});
									resultsJournalArticle.push({display : 'Articles', highlighted : 'Articles', raw:{title : 'Articles', type: 'header',url: ''}, text: 'Articles'});
									resultsMessageBoard.push({display : 'Message', highlighted : 'Message', raw:{title : 'Message', type: 'header',url: ''}, text: 'Message'});
									 resultsWikiPage.push({display : 'WikiPage',  raw:{title : 'WikiPage', type: 'header'}, text: 'WikiPage',url: ''});
									 resultsAnnouncementEntry.push({display : 'AnnouncementEntry',  raw:{title : 'AnnouncementEntry', type: 'header',url: ''}, text: 'AnnouncementEntry'});
									 
									for(var i = 0; i < results.length; i++){
										var raw = results[i].raw;
										if(raw.type == "user"){
					                		resultsUser.push(results[i]);
										}else if(raw.type == "dlFileEntry"){
											resultsDLFileEntry.push(results[i]);
										} else if(raw.type == "layout"){
											resultsLayout.push(results[i]);
										} else if(raw.type == "blogEntry"){
											resultsBlogEntry.push(results[i]);
										} else if(raw.type == "journalArticle"){
											resultsJournalArticle.push(results[i]);
										} else if(raw.type == "messageBoard"){
									           resultsMessageBoard.push(results[i]);
								        }else if(raw.type == "wikiPage"){
	                                         resultsWikiPage.push(results[i]);
                                          }else if(raw.type == "announcement"){
                                        	resultsAnnouncementEntry.push(results[i]);
                      
								      }  else{
											
										}	
									}
									
									if(resultsUser.length > 1){
										finalResult.push.apply( finalResult, resultsUser );
									}
									if(resultsDLFileEntry.length > 1){
										finalResult.push.apply( finalResult, resultsDLFileEntry );
									}
									if(resultsLayout.length > 1){
										finalResult.push.apply( finalResult, resultsLayout );
									}
									if(resultsBlogEntry.length > 1){
										finalResult.push.apply( finalResult, resultsBlogEntry );
									}
									if(resultsJournalArticle.length > 1){
										finalResult.push.apply( finalResult, resultsJournalArticle );
									}
									if(resultsMessageBoard.length > 1){
								          finalResult.push.apply( finalResult, resultsMessageBoard );
								    }
									if(resultsWikiPage.length > 1){
                                        finalResult.push.apply( finalResult, resultsWikiPage );
                                  	}
									if(resultsAnnouncementEntry.length > 1){
                                        finalResult.push.apply( finalResult, resultsAnnouncementEntry );
                                  	}
									return finalResult;
								},
								resultFormatter: function (query, results) {
								
									return A.Array.map(results, function (result) {
										var icon = "icon-info";
										var header = "result-title";
											var headerIcon ="";
										if(result.raw.type == "header"){
											header = "result-header"
										}
										if(result.raw.type == "header" && result.raw.title == "Users"){
											icon = "icon-user";
										}else if(result.raw.type == "header" && result.raw.title == "Files"){
											icon = "icon-file";
										}else if(result.raw.type == "header" && result.raw.title == "Pages"){
											icon = "icon-compass";
										}else if(result.raw.type == "header" && result.raw.title == "Blogs"){
											icon = "icon-comment";
										}else if(result.raw.type == "header" && result.raw.title == "Articles"){
											icon = "icon-book";
										}else if(result.raw.type == "header" && result.raw.title == "Message"){
											icon = "icon-folder-close";
										}else if(result.raw.type == "header" && result.raw.title == "WikiPage"){
											icon = "icon-tags";
										}else if(result.raw.type == "header" && result.raw.title == "AnnouncementEntry"){
											icon = "icon-star";
										}
								
										/* if(result.raw.type == "user"){
											
											icon = "icon-user";
										}else if(result.raw.type == "dlFileEntry"){
											icon = "icon-file";
										} else if(result.raw.type == "layout"){
											icon = "icon-compass";
										} else if(result.raw.type == "blogEntry"){
											icon = "icon-comment";
										} else if(result.raw.type == "journalArticle"){
											icon = "icon-book";
										} else {
											icon = "icon-eye-open";
										}
										 */
									/* 	 A.one(".result-header").ancestor('li').setStyle('color','#fff');
										 A.one(".result-header").ancestor('li').setStyle('background','#365d9e'); */
										 
										if(result.raw.type == "header"){
											return '<div class="'+header+'">'+result.highlighted+'<span class="result-icon" style="float:right;"><i class="'+icon+'"></i></span></div>';
										}else{
											return '<div class="'+header+'">'+result.highlighted+'</div>';
										}
										 
										 
										/* return '<span class="result-icon '+header+'">'+result.highlighted+' '+icon+'</span>'; */	
									/* return '<span class="result-icon '+header+'">'+result.highlighted+' '+headerIcon+'</span>'; */
									});
									
									/* A.one(".result-header").ancestor('li').setStyle('color','#fff');
									 A.one(".result-header").ancestor('li').setStyle('background','#365d9e'); */
								},
								resultTextLocator: function (result) {
									
									return result['title'];
									
								},
								
							});
	
		});
	
	
		function <portlet:namespace />search() {
			var keywords = document.<portlet:namespace />fm.<portlet:namespace />keywords.value;

			keywords = keywords.replace(/^\s+|\s+$/, '');

			if (keywords != '') {
				submitForm(document.<portlet:namespace />fm);
			}
			
			
		}
		
		AUI().use('aui-base',function(A) {
			
		});
	</aui:script>
	

</aui:form>