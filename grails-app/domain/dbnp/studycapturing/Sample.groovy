package dbnp.studycapturing

import dbnp.data.Term

/**
 * The Sample class describes an actual sample which results from a SamplingEvent.
 */
class Sample extends TemplateEntity {
	static searchable = true

	Subject parentSubject
	SamplingEvent parentEvent

	String name      // should be unique with respect to the parent study (which can be inferred)
	Term material
	// a member that describes the quantity of the sample? --> should be in the templates

	Map giveDomainFields() {
		return ['name':TemplateFieldType.STRING,'material':TemplateFieldType.ONTOLOGYTERM]
	}

	static constraints = {
		parentSubject(nullable:true)
	}

	static getSamplesFor( event ) {
            return  Sample.findAll( 'from Sample s where s.parentEvent =:event', [event:event] )
	}

}
