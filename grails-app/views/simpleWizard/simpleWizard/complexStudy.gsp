<%@ page import="org.dbnp.gdt.GdtService" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="layout" content="main"/>
	<title>Simple study wizard</title>
	
	<g:render template="javascripts" />
	
</head>
<body>
	<div class="simpleWizard">
		<h1>
			Complex study
			<span class="stepNumber">(step 2 of 4)</span>
		</h1>
		
		<span class="info"> 
			<span class="title">Complex study</span> 
			The study you are editing has a complex structure.  
			<g:if test="${message}">
				${message.toString().encodeAsHTML()}
			</g:if>
			This wizard cannot handle that complexity. You can not edit the samples using this wizard.
			<br />
			You can edit the study properties using the <g:link controller="studyWizard" action="index" id="${study.id}" params="['jump': 'edit']">normal wizard</g:link>.
			<br />
			<b>N.B.</b> If you have edited the study information before, you should save your changed first, using the save button below.	
		</span>
		
		<g:form class="simpleWizard" name="existingSamples" action="simpleWizard">
			<input type="hidden" name="_eventId" value="refresh" />
		</g:form>
		
		<p class="options">
			<a href="#" onClick="submitForm( 'existingSamples', 'previous' ); return false;" class="previous">Previous</a>
			<a href="#" onClick="submitForm( 'existingSamples', 'save' ); return false;" class="save separator">Save</a>
		</p>
	</div>	
</body>
</html>
