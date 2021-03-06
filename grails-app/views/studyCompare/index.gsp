<%
/**
 * Wizard index page
 *
 * @author Jeroen Wesbeek
 * @since  20120123
 *
 * Revision information:
 * $Rev:  66849 $
 * $Author:  duh $
 * $Date:  2010-12-08 15:12:54 +0100 (Wed, 08 Dec 2010) $
 */
%>
<html>
<head>
	<meta name="layout" content="main"/>
	<g:if env="development">
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'ajaxflow.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'studyCompare.css')}"/>
	</g:if><g:else>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'ajaxflow.min.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'studyCompare.min.css')}"/>
	</g:else>
</head>
<body>
	<g:render template="common/ajaxflow"/>
</body>
</html>
