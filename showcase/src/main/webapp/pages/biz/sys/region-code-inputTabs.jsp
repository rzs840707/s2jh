<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<s2:tabbedpanel disableItemsExcludeFirst="%{isBlank(#parameters.id)}">
	<ul>
		<li><a
			href="${base}/biz/sys/region-code!<s:property value="%{isBlank(#parameters.id)?'create':'update'}"/>?id=<s:property value='#parameters.id'/>">
				<span>基本信息</span>
		</a></li>
	</ul>
</s2:tabbedpanel>
