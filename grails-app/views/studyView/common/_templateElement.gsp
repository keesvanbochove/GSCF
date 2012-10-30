<element identifier="${entity.giveUUID()}" type="${entityType}" class="vertical">
	<description>
		${field.name[0].toUpperCase()}${field.name.substring(1)}
	</description>

	<g:render template="types/${fieldType}" model="['entity':entity, 'field':field, 'value': entity.getFieldValue(field.name), 'css': 'vertical', 'canRead': canRead, 'canWrite': canWrite, 'horizontal': false]" />

	<debug>
		<g:if test="${field.required}"><b>*</b></g:if>
		${fieldType}
	</debug>
</element>
